package com.foomap.model;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/*
 *  历史输入信息
 * 
 */
public class InPutHistory {
	private static String TAG = "InPutHistory";
	Context context;
	SharedPreferences historySharedPreferences;
	Editor editor;

	public InPutHistory(Context context) {
		this.context = context;
		historySharedPreferences = context.getSharedPreferences(
				"usr_input_history", Context.MODE_APPEND);
		editor = historySharedPreferences.edit();
	}

	public ArrayList<String> getData() {
		ArrayList<String> res = new ArrayList<String>();

		String history = historySharedPreferences.getString("history", "无数据,");
		String data[]=history.split(",");

		for(String s:data)
		{
			res.add(s);
		}

		Log.i(TAG, "------>" + "res[0]" + res.get(0) + "    ||" + history);
		return res;
	}

	public void putdata(String data) {

	     Log.i(TAG, "============>"+data);
		if (data == null || data.isEmpty()) {
			return;
		}
		String newdata = null;
		ArrayList<String>  saveList= getData();
		// 取数据
		if (!saveList.get(0).equals("无数据")) {
			// 是否已存在?
			for (String s : saveList) {
				if (data.equals(s)) {
					return;
				} else {
					if(newdata==null)
					{
						newdata=s;
					}
					else
					{
						newdata = s +","+ newdata;
					}
				
				}
			}
			newdata = data + "," + newdata;
		} else {
			newdata = data;
		}

		editor.putString("history", newdata);
		editor.commit();
		
		// Log.i(TAG, save);
	}

}
