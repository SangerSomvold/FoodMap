package com.foomap.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class JsonUtils {
	
	//�����û���¼���
	//0��½�ɹ� 1����ʧ�� 2�û�������� 3�û������� 
	public static int UsrLogin(String jsonString)
	{
		
		int res=1;
		try {
			JSONObject jsonObject=new JSONObject(jsonString);
			if(jsonObject.getBoolean("success")==true)
			{
				String usrRes=jsonObject.getString("result");
				if(usrRes.equals("������Ϊ��"))
				{
					res=0;
				}
				else
				{
					res=2;
				}
			}
			else
			{
				res=1;
			}
			
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return res;
	}

}
