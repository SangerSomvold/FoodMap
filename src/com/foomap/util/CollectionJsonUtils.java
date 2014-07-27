package com.foomap.util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.foomap.model.CollectionData;

public class CollectionJsonUtils {
	//返回列表
	public static ArrayList<CollectionData> getCollectionList(String jsonString)
	{
		ArrayList<CollectionData> dataList=new ArrayList<CollectionData>();
		try {
			JSONObject jsonObject=new JSONObject(jsonString);
			if(jsonObject.getBoolean("isExist"))
			{
				JSONArray jsonArray=jsonObject.getJSONArray("typeList");
				for(int i=0;i<jsonArray.length();i++)
				{
					JSONObject jsonItem=jsonArray.getJSONObject(i);
					dataList.add(getCollection(jsonItem));
				}
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataList;
	}
	//返回单例收藏
	protected static CollectionData getCollection(JSONObject jsonObject)
	{
		CollectionData data=new CollectionData();
		try {
		
			data.shopId=jsonObject.getInt("shop_id");
			data.shopName=jsonObject.getString("shop_name");
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
		return data;
	}
	public static boolean isOpSucceed(String jsonString)
	{
		boolean res=false;
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(jsonString);
			if(jsonObject.getBoolean("success")&&jsonObject.getBoolean("result"))
			{
				res=true;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

}

