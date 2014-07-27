package com.foomap.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DraftsSqlite extends SQLiteOpenHelper{

	public DraftsSqlite(Context context, String name, CursorFactory factory,
			int version) {
		super(context, "Drafts", factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS Drafts " +  
                "(userId VARCHAR,name VARCHAR,rating FLOAT,cost FLOAT,feature VARCHAR,address VARCHAR,picture VARCHAR)");  
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

}
