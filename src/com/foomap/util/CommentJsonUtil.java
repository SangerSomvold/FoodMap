package com.foomap.util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.foomap.model.CommentData;

public class CommentJsonUtil {
	public static CommentData getComent(JSONObject jsonObject) {
		CommentData data = null;
		if (jsonObject != null) {
			try {
				data = new CommentData();

				data.id = jsonObject.getInt("id");
				data.shopId=jsonObject.getInt("shopId");
				data.grade=jsonObject.getDouble("grade");
				data.userId=jsonObject.getString("userId");
				data.comment=jsonObject.getString("comment");
				data.commentTime=jsonObject.getString("datetime");
			    data.cost=jsonObject.getInt("cost");
				//评论图片
				ArrayList<String> picUrlList=new ArrayList<String>();
				JSONArray array=jsonObject.getJSONArray("picUri");
			for(int i=0;i<array.length();i++)
			{
				
				picUrlList.add(array.getString(i));
			}
			data.picUrl=picUrlList;
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return data;
	}
	public static ArrayList<CommentData> getComentList(String jsonRes)
	{
		ArrayList<CommentData> commentList=new ArrayList<CommentData>();
		try {
			JSONObject jsonObject=new JSONObject(jsonRes);
			if(jsonObject.getBoolean("isExist"))
			{
				JSONArray jsonArray=jsonObject.getJSONArray("commentList");
				for(int i=0;i<jsonArray.length();i++)
				{
					JSONObject item=jsonArray.getJSONObject(i);
					CommentData data=getComent(item);
					commentList.add(data);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return commentList;
	}

}
