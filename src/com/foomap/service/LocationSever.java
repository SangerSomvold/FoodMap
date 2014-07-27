package com.foomap.service;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

public class LocationSever {
	private static String TAG = "LocationSever";
	private Context context;
	private LocationClient locationClient;
	private BDLocation loction = null;
	private LocationClientOption option;
	// 定位模式
	public final int Battery_Saving = 1;
	public final int Hight_Accuracy = 2;
	public final int Device_Sensors = 3;

	//

	public LocationSever(Context context) {
		this.context = context;
		init();
	}

	protected void init() {
		option = new LocationClientOption();
		locationClient = new LocationClient(context);
	}
	


	// 获得当前坐标
	public void requestLocation(BDLocationListener listener) {
		if(listener!=null)
		{
			locationClient.registerLocationListener(listener);
			//option.setCoorType("bd0911");
			option.setScanSpan(10000);
			option.setIsNeedAddress(true);
			option.setLocationMode(LocationMode.Battery_Saving);
			locationClient.setLocOption(option);
	        start();
	        Log.i(TAG, "------------>" + locationClient.requestLocation());
		}
		
		
		
		
	}

	// 设置精度要求
	public void setPrecision(int setPrecision) {
		switch (setPrecision) {

		case Hight_Accuracy:
			option.setLocationMode(LocationMode.Hight_Accuracy);
			break;
		case Device_Sensors:
			option.setLocationMode(LocationMode.Device_Sensors);
			break;
		case Battery_Saving:
		default:
			option.setLocationMode(LocationMode.Battery_Saving);
			break;
		}
	}


	public void start()
	{
		if(!locationClient.isStarted())
		{
			locationClient.start();
		}
	}
	public void stop()
	{
		if(locationClient.isStarted())
		{
			locationClient.stop();
		}
		
	}
	

}
