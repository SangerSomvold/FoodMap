package com.foomap.activity;


import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.foodmap.R;
import com.foomap.model.CollectionData;
import com.foomap.model.ShopData;
import com.foomap.service.CollectionHttpService;
import com.foomap.service.CommentHttpService;
import com.foomap.service.CommentHttpService.ICommentAdapter;
import com.foomap.service.HttpServiceHelper.IOnHttpRequeseListener;
import com.foomap.service.LocationSever;
import com.foomap.service.UserHttpService;
import com.foomap.util.CollectionJsonUtils;

public class MapViewActivity extends Activity {
	
	private static String TAG="MapViewActivity";
	private MapView mapView;
	private BaiduMap map;
	private Context context;
	private LocationSever lc;
	private LatLng myLocation;
	//商家信息
	ArrayList<ShopData> shopDatas;
	

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 百度SDK初始化
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.mapview_activity);
		this.context=this;
		init();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mapView.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mapView.onResume();
		
		
		// 遮挡物
		map.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		lc.requestLocation(new BDLocationListener() {
			
			@Override
			public void onReceiveLocation(BDLocation loction) {
				// TODO Auto-generated method stub
				if(loction!=null)
				{
					myLocation=new LatLng(loction.getLatitude(), loction.getLongitude());
					
					if(myLocation!=null)
					{
						lc.stop();
						setMapViewCenter(myLocation);
						addOverLay(shopDatas);
					}
				
				}
				
		
			}
		});   
		
	
		
		
	}

	// ===============================================================
	protected void init() {
		mapView = (MapView) findViewById(R.id.bmapView_mapView);
		shopDatas=(ArrayList<ShopData>) getIntent().getExtras().getSerializable("shopData");
		if(shopDatas==null)
		{
			Log.i(TAG, "shopDatas==null");
		}
		else
		{
			Log.i(TAG, "shopDatas!=null");			
		}
		
		map = mapView.getMap();
		lc=new LocationSever(getApplicationContext());	
	}
	public void setMapViewCenter(LatLng latLng)
	{
	   map.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(latLng, 15));
	
	   BitmapDescriptor bitmap=BitmapDescriptorFactory.fromResource(R.drawable.mv_my_place);
		OverlayOptions option=new MarkerOptions().position(latLng).icon(bitmap);
		map.addOverlay(option);
	   
	}
	


	public void addOverLay(ArrayList<ShopData> list) {
		BitmapDescriptor bitmap=BitmapDescriptorFactory.fromResource(R.drawable.mv_location);
		for (ShopData item : list) {
			LatLng itemLatLng=new LatLng(item.getLatitude(), item.getLongitude());
			Log.i(TAG, item.getLatitude()+"lat");
			Log.i(TAG, item.getLongitude()+"lng");
			OverlayOptions option=new MarkerOptions().position(itemLatLng).icon(bitmap);
			map.addOverlay(option);
              
		}
	}



	

}
