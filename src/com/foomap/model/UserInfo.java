package com.foomap.model;

import com.baidu.mapapi.utils.d;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserInfo {
	private static String TAG="UserInfo";


	// 登录用户操作

	public static class UserData {
		public String userId, password;


		// 打印信息

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return userId + "  " + password;
		}
	}


	// 添加登录用户信息

	public static void insert(UserData data, Context appContext) {
		boolean res = false;

		// 用户存在

		if (search(data.userId, appContext) != null) {
			return ;

		}


		// 用户不存在 添加

		SQLiteDatabase db = appContext.openOrCreateDatabase("userInfo.db",
				Context.MODE_PRIVATE, null);


		// 用户表是否存在

		if (!isTableExist("UserInfo", db, appContext)) {
			createTable(db);
		}
	


		// 添加

		String insertRule = "INSERT INTO UserInfo VALUES (?)";
		db.execSQL(insertRule, new String[] { data.userId });
		db.close();

	}


	// 根据Id搜索

	public static UserData search(String id, Context appContext) {
		UserData data = null;

		

	


		SQLiteDatabase db = appContext.openOrCreateDatabase("userInfo.db",
				Context.MODE_PRIVATE, null);

		//用户表是否存在

		if (!isTableExist("UserInfo", db, appContext)) {
			db.close();
			return null;
		}

		Cursor cursor = db.rawQuery("SELECT * FROM UserInfo WHERE _id = ?",
				new String[] { id });
		if (cursor.getCount() > 0) {
			data = new UserData();
			while (cursor.moveToNext()) {
				data.userId = cursor.getString(0);
			}
		}
		db.close();
		return data;
	}



	// 删除用户

	public static void delete(Context appContext) {

		SQLiteDatabase db = appContext.openOrCreateDatabase("userInfo.db",
				Context.MODE_PRIVATE, null);

	   //表存在

		if (!isTableExist("UserInfo", db, appContext)) {
			db.close();
			return;
		}
		clean("UserInfo",db);
		//db.delete("UserInfo", "_id = ", new String[] { userId });
		db.close();
	}


	// 表是否存在判断

	private static boolean isTableExist(String tableName, SQLiteDatabase db,
			Context appContext) {




		String rule = "SELECT * FROM sqlite_master where name= " + "'"
				+ tableName + "'";
		Cursor c = db.rawQuery(rule, null);
		if (c.getCount() != 0) {
			return true;
		} else {
			return false;
		}
	}


	// 创建表

	private static void createTable(SQLiteDatabase db) {
		String createRule = "CREATE TABLE  UserInfo (_id VARCHAR)";
		db.execSQL(createRule);
	}

	//获得登录用户

	public static UserData getUserData(Context appContext) {
		UserData data = null;



		SQLiteDatabase db = appContext.openOrCreateDatabase("userInfo.db",
				Context.MODE_PRIVATE, null);


		if (!isTableExist("UserInfo", db, appContext)) {
			db.close();
			return null;
		}

		Cursor cursor = db.rawQuery("SELECT * FROM UserInfo ",
				null);
		//存在用户
		if (cursor.getCount() > 0) {
			data = new UserData();
			while (cursor.moveToNext()) {
				data.userId = cursor.getString(0);
				Log.i(TAG, data.userId );
				
			}
		}
		db.close();
		return data;
	}

	//清楚用户表数据  注销

	private static void clean(String tableName,SQLiteDatabase db)
	{
		db.delete(tableName, null, null);
	}
}
