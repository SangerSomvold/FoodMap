package com.foomap.service;

import java.util.HashMap;

import android.content.Context;
import android.util.Log;

public class UserHttpService extends HttpServiceHelper {
	protected final static String BASE_URI = "http://10.252.252.31:8080/foodmap/user/";
	private static String TAG = "UserHttpService";
	protected final static String REGISTER = BASE_URI + "user_register";
	protected final static String LOGIN = BASE_URI + "user_login";
	protected final static String RESET = BASE_URI + "user_reset";

	public UserHttpService(Context context) {
		super(context);

	}

	// 登陆
	public void logIn(String id, String password) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("userId", id);
		map.put("password", password);
		get_Asyn(LOGIN, map);

	}

	// 注册
	public void register(String id, String password,
			IOnHttpRequeseListener listener) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("userId", id);
		map.put("password", password);
		setRequestListener(listener);
		get_Asyn(REGISTER, map);

	}
	//修改密码
	public void resetPassWord(String userId,String passsword,String newPassword,IOnHttpRequeseListener listener)
	{
		HashMap<String, String> map=new HashMap<String, String>();
		map.put("userId", userId);
		map.put("password", passsword);
		map.put("newPassword", newPassword);
		setRequestListener(listener);
		get_Asyn(RESET, map);
	}

	public boolean getInfo(String id) {
		return false;
	}

	@Override
	protected String postMethod() {
		// TODO Auto-generated method stub
		return null;
	}

}
