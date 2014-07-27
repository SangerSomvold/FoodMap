package com.foomap.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DraftsDbManager {

	SQLiteDatabase db;
	DraftsSqlite mSqlite;
	
	public DraftsDbManager(Context context)
	{
		mSqlite = new DraftsSqlite(context, "Drafts", null, 1);
		db = mSqlite.getWritableDatabase();
	}
	
	public void addInfo(String userId,String name,float rating,float cost,String feature,String address,String mDrr)
	{
		db.beginTransaction();
		try {
			ContentValues cv = new ContentValues();
			cv.put("userId", userId);
			cv.put("name", name);
			cv.put("rating", rating);
			cv.put("cost", cost);
			cv.put("feature", feature);
			cv.put("address", address);
			cv.put("picture", mDrr);
			db.insert("Drafts", null, cv);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	public void deleteInfo(String userId,String name) {
		db.beginTransaction();
		String sql = "DELETE FROM Drafts WHERE userId like '" +userId +"'AND name like '"+ name + "'";
		db.execSQL(sql);
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	public Cursor queryTheCursor() {
		db.beginTransaction();
		Cursor c = db.rawQuery("SELECT * FROM Drafts", null);
		db.setTransactionSuccessful();
		db.endTransaction();
		return c;
	}
	
	public ContentValues queryInfo(String userId,String name)
	{
		db.beginTransaction();
		ContentValues cv = new ContentValues();
		Cursor c = db.rawQuery("SELECT * FROM Drafts WHERE userId like '" +userId +"'AND name like '"+ name + "'" , null);
		if (c.moveToFirst()) {
			String mUserId = c.getString(c.getColumnIndex("userId"));
			String mName = c.getString(c.getColumnIndex("name"));
			float mRating = c.getFloat(c.getColumnIndex("rating"));
			float mCost = c.getFloat(c.getColumnIndex("cost"));
			String mFeature = c.getString(c.getColumnIndex("feature"));
			String mAddress = c.getString(c.getColumnIndex("address"));
			String mDrr = c.getString(c.getColumnIndex("picture"));
			cv.put("userId", mUserId);
			cv.put("name", mName);
			cv.put("rating", mRating);
			cv.put("cost", mCost);
			cv.put("feature", mFeature);
			cv.put("address", mAddress);
			cv.put("picture", mDrr);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		return cv;
	}
	
	public void closeDB() {
		db.close();
	}
}

