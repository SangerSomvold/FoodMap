
package com.foomap.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.foodmap.R;
import com.foomap.model.ShopData;
import com.foomap.model.UserInfo;
import com.foomap.model.UserInfo.UserData;
import com.foomap.service.CollectionHttpService;
import com.foomap.service.HttpServiceHelper.IOnHttpRequeseListener;
import com.foomap.service.ShophttpService;
import com.foomap.util.ImageManager;
import com.foomap.util.ShopJsonUtils;
import com.foomap.view.PoiCommentView;
import com.foomap.view.PoiDesCriptionView;

public class PoiDetailActivity extends Activity implements OnClickListener{ 
	private static String TAG="PoiDetailActivity";
	
	
	private Context context;
	private ViewPager viewPager;
	
	private int shopId;
	private ShophttpService shophttpService;
	private ImageView poiPicMain;
	//
	private ShopData shopData;
	//viewPager内容
	private ArrayList<View> viewList;
	private PoiCommentView commentView;
	private PoiDesCriptionView desCriptionView;
	private List<String> pageTitle;
	//评论收藏
	private Button comment,collection;
	private CollectionHttpService coHttpService;
	private boolean isCollected=false;
	CollectionHttpListener listener;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poidetail);
		context=this;
		
		init();
	}                    
	protected void init()
	{
		listener=new CollectionHttpListener();
		coHttpService=new CollectionHttpService(context);
		shophttpService=new ShophttpService(context);
		collection=(Button)findViewById(R.id.collection_bt_poiDetail);
		comment=(Button)findViewById(R.id.comment_bt_poiDetail);
		viewPager=(ViewPager)findViewById(R.id.viewpager_poidetail);
		desCriptionView=new PoiDesCriptionView(context);
		
		//viewPage内容填充
		pageTitle=new ArrayList<String>();
		pageTitle.add(getResources().getString(R.string.info));
		pageTitle.add(getResources().getString(R.string.comment));
		commentView=new PoiCommentView(context);
		viewList=new ArrayList<View>();
		viewList.add(desCriptionView);
		viewList.add(commentView);
		viewPager.setAdapter(new MPagerAdapter());
		poiPicMain=(ImageView)findViewById(R.id.poiPicMain_poiDetail);
		bindEvent();
		//填充商户信息
		
		shopId=getIntent().getExtras().getInt("shopId");
		shophttpService.searchById(shopId, new IOnHttpRequeseListener() {
			
			@Override
			public void finished(String jsonRes) {
				// TODO Auto-generated method stub
				if(jsonRes==null)
				{
					return;
				}
				Log.i(TAG, jsonRes);
				
				try {
					JSONObject shopJsonObject = new JSONObject(jsonRes).getJSONObject("info");
					shopData=ShopJsonUtils.getShopData(shopJsonObject);
					ImageManager.Load(shopData.iconUrl, poiPicMain,ImageManager.options);
					desCriptionView.refresh(shopData);
					commentView.refreash(shopId);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
	}
	public void bindEvent()
	{
		comment.setOnClickListener(this);
		collection.setOnClickListener(this);
		poiPicMain.setOnClickListener(this);
	}


	protected class MPagerAdapter extends PagerAdapter
	{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return viewList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0==arg1;
		}

	

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			container.removeView(viewList.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			container.addView(viewList.get(position));
			return viewList.get(position);
		}
		//标题

		@Override
		public CharSequence getPageTitle(int position) {
			// TODO Auto-generated method stub
			return pageTitle.get(position);
		}
		
		
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==collection)
		{
		    UserData udata=isUserLogin();
		    if(udata!=null)
		    {
		    	if(isCollected)
				{
					//取消收藏
					coHttpService.del(udata.userId, shopId,listener );
					isCollected=false;
					desCriptionView.changeCollectTag();	
				}
				else
				{
					
				  //添加收藏
					coHttpService.add(udata.userId, shopId, listener);
					isCollected=true;
					desCriptionView.changeCollectTag();
				}
		    }
		}
		else if(v==comment)
		{
		     if(isUserLogin()!=null)
		     {
		    		Intent intent=new Intent();
					intent.putExtra("shopId", shopData.id);
					intent.putExtra("shopName", shopData.name);
					intent.setClass(context, CommentActivity.class);
					startActivity(intent);
					
		     }
		}
		else if(v==poiPicMain)
		{
			if(shopData.picUrls!=null)
			for(String s:shopData.picUrls)
			{
				Log.i(TAG, s);
			}
			if(shopData.picUrls==null||shopData.picUrls.size()<=1)
			{
				Toast.makeText(context, "没有更多图片了", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Intent intent=new Intent();
				Bundle bundle=new Bundle();
				bundle.putSerializable("shopData", shopData);
				intent.putExtras(bundle);
				intent.setClass(context, ShowDetailPicsActivity.class);
				startActivity(intent);
			}
			
		}
	}
	
	//用户登陆判定
	protected UserData isUserLogin()
	{
		
		UserData udata=UserInfo.getUserData(context.getApplicationContext());
		if(udata==null)
		{
			Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
			Intent intent=new Intent();
			intent.setClass(context, LoginActivity.class);
			context.startActivity(intent);
			
			
			
		}
		return udata;
	}
	
	
	//收藏回调
	private class CollectionHttpListener implements IOnHttpRequeseListener
	{

		@Override
		public void finished(String jsonRes) {
			// TODO Auto-generated method stub
			Log.i(TAG, jsonRes);
			
		}
		
	}
	
}
