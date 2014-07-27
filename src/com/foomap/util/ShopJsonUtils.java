package com.foomap.util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.foomap.model.ShopData;
import com.foomap.model.TypeData;

public class ShopJsonUtils {

	private static String TAG="ShopJsonUtils";
	public ShopJsonUtils()
	{
	
	}
	
	public static ArrayList<ShopData> getDataList(String jsonString)
	{
		if(jsonString==null) return null;
		 ArrayList<ShopData> list=new ArrayList<ShopData>();
		 try {
			JSONObject jsonObject=new JSONObject(jsonString);
			boolean isExist=jsonObject.getBoolean("isExist");
			if(isExist)
			{
				JSONArray jsonArray=jsonObject.getJSONArray("shopList");
				for(int i=0;i<jsonArray.length();i++)
				{
					JSONObject item=jsonArray.getJSONObject(i);
					list.add(getShopData(item));
					
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 
		 return list;
	}
	//
	public static ShopData getShopData(JSONObject item)
	{
		ShopData shop=new ShopData();
		try {
			
			shop.address=item.getString("address");
			shop.name=item.getString("name");
			shop.id=item.getInt("id");
			shop.iconUrl=item.getString("iconUri");
			shop.cost_avg=item.getDouble("costAvg");
			shop.grade_avg=item.getDouble("gradeAvg");
			shop.createUserId=item.getString("createUserId");
			
			shop.feature=item.getString("feature");
			
			//经纬度坐标
			JSONObject latlngoJsonObject=item.getJSONObject("latlong");
			shop.latitude=latlngoJsonObject.getDouble("latitude");
			shop.longitude=latlngoJsonObject.getDouble("longitude");
			//图片
			JSONArray picUriJsonArray=item.getJSONArray("picUri");
			ArrayList<String> listUri=new ArrayList<String>();
			for(int indexOfPic=0;indexOfPic<picUriJsonArray.length();indexOfPic++)
			{
				listUri.add(picUriJsonArray.getString(indexOfPic));
			}
			shop.picUrls=listUri;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return shop;
	}
	//获得类型列表
	public static ArrayList<TypeData> getTypeList(String jsonRes)
	{
		ArrayList<TypeData> dataList=new ArrayList<TypeData>();
		
		try {
			JSONObject jsonObject=new JSONObject(jsonRes);
			if(jsonObject.getBoolean("isExist"))
			{
				JSONArray jsonArray=jsonObject.getJSONArray("typeList");
				for(int i=0;i<jsonArray.length();i++)
				{
					JSONObject item=jsonArray.getJSONObject(i);
					TypeData data=new TypeData();
					data.typeId=item.getInt("type_id");
					data.typeName=item.getString("type_name");
					dataList.add(data);
				}
			}	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		return dataList;
	}



}
