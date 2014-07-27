package com.foomap.util;

import org.json.JSONException;
import org.json.JSONObject;

public class UserJsonUtils {

	public static boolean isSuccess(String jsonString) {
		boolean res = false;
		if (jsonString != null) {
			try {
				JSONObject jsonObject = new JSONObject(jsonString);
				if (jsonObject.getBoolean("success")) {
					res = true;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return res;
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
