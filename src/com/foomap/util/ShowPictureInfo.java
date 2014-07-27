package com.foomap.util;

import java.io.Serializable;

import android.graphics.Bitmap;

public class ShowPictureInfo implements Serializable{

	private Bitmap bMap;
	private String mDrr;
	private Boolean checked = false;
	
	public Bitmap getBitmap()
	{
		return bMap;
	}
	
	public void setBitmap(Bitmap bm)
	{
		bMap = bm;
	}
	
	public String getDrr()
	{
		return mDrr;
	}
	
	public void setDrr(String str)
	{
		mDrr = str;
	}
	
	public Boolean getStatus()
	{
		return checked;
	}
	
	public void setStatus(Boolean st)
	{
		checked = st;
	}
}
