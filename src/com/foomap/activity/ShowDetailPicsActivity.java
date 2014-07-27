package com.foomap.activity;

import java.util.ArrayList;
import java.util.List;

import com.foomap.model.ShopData;
import com.foomap.util.DensityUtil;
import com.foomap.util.ImageManager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ShowDetailPicsActivity extends Activity{
	private ShopData shopData;
	private ViewPager viewPager;
	private Context context;
	private PicViewAdapter adapter;
	private static String TAG="ShowDetailPicsActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context=this;
		init();
		
		
	}
	public void init()
	{
		shopData=(ShopData) getIntent().getSerializableExtra("shopData");
		adapter=new PicViewAdapter();
		
		if(shopData==null)
		{
			Log.i(TAG, "NULL");
		}
		else
		{
			Log.i(TAG, "VAL");
		}
		viewPager=new ViewPager(context);
		viewPager.setAdapter(adapter);
		setContentView(viewPager);
	}
	
	private class PicViewAdapter extends PagerAdapter
	{
		List<ImageView> imageViews;
		public PicViewAdapter(){
			imageViews=new ArrayList<ImageView>();
			for(String url:shopData.picUrls)
			{
				ImageView iView=new ImageView(context);
     				LayoutParams lp=new LayoutParams();
     				lp.width=LayoutParams.MATCH_PARENT;
     				lp.height=LayoutParams.WRAP_CONTENT;
     				iView.setLayoutParams(lp);
				imageViews.add(new ImageView(context));
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imageViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0==arg1;
		}

	

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			container.removeView(imageViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			container.addView(imageViews.get(position));
			
			ImageManager.Load(shopData.picUrls.get(position), imageViews.get(position), ImageManager.options);
			return imageViews.get(position);
		}
	
	}
}
