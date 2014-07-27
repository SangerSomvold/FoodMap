package com.foomap.view;

import com.foodmap.R;
import com.foomap.model.ShopData;
import com.foomap.model.UserInfo;
import com.foomap.model.UserInfo.UserData;
import com.foomap.service.CollectionHttpService;
import com.foomap.service.HttpServiceHelper.IOnHttpRequeseListener;
import com.foomap.util.CollectionJsonUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class PoiDesCriptionView extends LinearLayout {
	private static String TAG = "PoiDesCriptionView";
	private Context context;

	private TextView shopName_tx, address_tx, costAvg_tx;
	private RatingBar gradeAvg_rb;
	private TextView feature_tx;
	private ImageView collectTag;
	private CheckListener checkListener;
	private CollectionHttpService coHttpService;
	private boolean isCollect = false;
	private  boolean isFirst=true;  

	public PoiDesCriptionView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		inflate(context, R.layout.poidescription, this);
		init();

	}

	//

	public void init()

	{
		feature_tx = (TextView) findViewById(R.id.feature_poidetail);
		gradeAvg_rb = (RatingBar) findViewById(R.id.gradeAvg_poidetail);
		address_tx = (TextView) findViewById(R.id.address_poidetail);
		costAvg_tx = (TextView) findViewById(R.id.costAvg_poidetail);
		shopName_tx = (TextView) findViewById(R.id.shopName_poidetail);
		collectTag = (ImageView) findViewById(R.id.collectTag_poidetail);
		coHttpService = new CollectionHttpService(context);
		checkListener = new CheckListener();

	}

	// 刷新
	public void refresh(ShopData shopData) {
		feature_tx.setText(shopData.feature);
		address_tx.setText(shopData.address);
		costAvg_tx.setText("¥" + shopData.cost_avg + "");
		shopName_tx.setText(shopData.name);
		gradeAvg_rb.setRating((float) shopData.grade_avg);
		UserData uData = UserInfo.getUserData(context.getApplicationContext());
		Log.i(TAG, "refresh");

		if (uData != null) {
			coHttpService
					.isCollection(uData.userId, shopData.id, checkListener);
		} else {
			Log.i(TAG, "用户未登录");
		}

	}

	private class CheckListener implements IOnHttpRequeseListener {

		@Override
		public void finished(String jsonRes) {
			// TODO Auto-generated method stub
			Log.i(TAG, jsonRes);
			if (jsonRes != null && CollectionJsonUtils.isOpSucceed(jsonRes)) {		
				changeCollectTag();
			}

		}

	}



	public void changeCollectTag() {
		if (isCollect) {
			isCollect = false;
			collectTag.setImageDrawable(getResources().getDrawable(
					R.drawable.collection_bt_poidetail_icon));
			if(!isFirst)
			{
				
				Toast.makeText(context, "取消收藏",Toast.LENGTH_SHORT).show();
			}
			else
			{
				isFirst=false;
			}
			

		} else {
			isCollect = true;
			collectTag.setImageDrawable(getResources().getDrawable(
					R.drawable.collection_bt_poidetail_icon_d));
			if(!isFirst)
			{
				Toast.makeText(context, "收藏成功",Toast.LENGTH_SHORT).show();
			}
			else
			{
				isFirst=false;
			}
		
		}

	}

}
