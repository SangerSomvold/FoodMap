package com.foomap.util;


import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class CompressImg {
	private static String TAG = "CompressImg";
	// bitmap选项
	
	private CompressImgConfig config;
	private ArrayList<Bitmap> bitmapList;
	
	public CompressImg()
	{	
		bitmapList=new ArrayList<Bitmap>();
	}
	
	/*
	 *  压缩文件 存储缩略图 返回 File
	 */
	public File getFile() {
		if (isFileCached(config.filePathFrom, config.dirPathTo)) {
			// 已缓存
			File tmpFile = new File(config.filePathFrom);
			String fileName = tmpFile.getName().trim();
			Log.i(TAG, "已缓存");
			return new File(config.dirPathTo, fileName);
		} else {
			//未缓存
			Bitmap small=getBitmap(false);
			String fileCachePath=loadBitmap(small);
			return new File(fileCachePath);
		}

	}

	/*
	 * 压缩文件 返回 Bitmap 大小只有大概范围 isCheckCache 是否检测缓存 config 压缩设置
	 */
	public Bitmap getBitmap(boolean isCheckCache) {
		if (isCheckCache && isFileCached(config.filePathFrom, config.dirPathTo)) {
			// 已在sd卡上缓存过文件
			Log.i(TAG, "bit 已缓存");
			File tmpFile = new File(config.filePathFrom);
			String fileName = tmpFile.getName().trim();
			return BitmapFactory.decodeFile(config.dirPathTo +"/"+ fileName);

		}
		//-------压缩
		 BitmapFactory.Options option = new BitmapFactory.Options();
		 //每个像素占用 2byte 内存
		 option.inPreferredConfig=Bitmap.Config.RGB_565;
		Bitmap bitmap_small = null;
		int inSampleSize = 1;
		// 图片实际宽高
		int imgWide, imgHeight;
		// 只返回文件信息
		option.inJustDecodeBounds = true;
		String filePath = config.filePathFrom;
		BitmapFactory.decodeFile(filePath, option);
		imgHeight = option.outHeight;
		imgWide = option.outWidth;
		Log.i(TAG, "imgWide "+imgWide+"----------- imgHeight "+imgHeight);
		// 计算压缩比
		if (config.getRequestWide() < imgWide
				|| config.getRequestHeight() < imgHeight) {
			int heightRatio = Math.round(((float) imgHeight)
					/ ((float) config.getRequestHeight()));
			int wideRatio = Math.round(((float) imgWide)
					/ ((float) config.getRequestWide()));
			inSampleSize = heightRatio > wideRatio ? heightRatio : wideRatio;
		}
		if(inSampleSize>1)
		{
			option.inSampleSize = inSampleSize;
			option.inJustDecodeBounds = false;
			bitmap_small = BitmapFactory.decodeFile(filePath, option);
		}
		else
		{
			// 返回实际文件的Bitmap
			bitmap_small=BitmapFactory.decodeFile(filePath);
		}
		Log.i(TAG, "缩放比---> " + inSampleSize);
		
		return bitmap_small;
	}

	/*
	 *  存储bitmap
	 */
	public String loadBitmap(Bitmap bitmap) {
		String path = null;
		// 建立图片缓存文件夹
		//File sd = Environment.getExternalStorageDirectory();
		File dir = new File(config.getDirPathTo());
		String fileName=new File(config.getFilePathFrom()).getName().trim();
		if (!dir.exists()) {
			dir.mkdirs();
		}
		// 保存
		File outFile = new File(dir.getPath(), fileName);
	
		try {
			FileOutputStream outStream = new FileOutputStream(outFile);
			// 再次质量压缩(100-CompressRatio)/100
			bitmap.compress(Bitmap.CompressFormat.PNG, config.getCompressRatio(), outStream);
			outStream.flush();
			// 回收
			if(config.getAutoRecycle())
			{
				bitmap.recycle();
			}
			else
			{
				bitmapList.add(bitmap);
			}
			
			outStream.close();
			
			path = dir.getPath() + "/" + fileName;
			Log.i(TAG, "保存成功");
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(TAG, "存储文件异常!");
		}
		return path;
	}
	
	
	//回收bitmap
	public void recycleBitmap()
	{
		for(Bitmap map:bitmapList)
		{
			map.recycle();
		}
		bitmapList.clear();
	}

	/*
	 *  判断某文件是否被缓存过
	 */
	public boolean isFileCached(String filePathFrom, String dirTo) {
		File from = new File(filePathFrom);
		String fileName = from.getName().trim();
		File fileCache = new File(dirTo, fileName);
		if (fileCache.exists()) {
			// 已缓存
			return true;
		}
		// 未被缓存过
		return false;
	}
	public void setConfig(CompressImgConfig config)
	{
	   this.config=config;	
	}

	/*
	 *  压缩选项
	 */
	public  class CompressImgConfig {
		public final String DEFAULT_PATH_DIR_TO=Environment.getExternalStorageState()+"/foodmap/cache/";
		private int requestWide = -1, requestHeight = -1;
		private String dirPathTo, filePathFrom;
		// 压缩比例compressRatio/100
		private int compressRatio = -1;
		private boolean isAutoRecycle=true;
		
		public boolean getAutoRecycle() {
			return isAutoRecycle;
		}
		public void setAutoRecycle(boolean isAutoRecycle) {
			this.isAutoRecycle = isAutoRecycle;
		}
		public int getRequestWide() {
			return requestWide;
		}
		public void setRequestWide(int requestWide) {
			this.requestWide = requestWide;
		}
		public int getRequestHeight() {
			return requestHeight;
		}
		public void setRequestHeight(int requestHeight) {
			this.requestHeight = requestHeight;
		}
		public String getDirPathTo() {
			return dirPathTo;
		}
		public void setDirPathTo(String dirPathTo) {
			this.dirPathTo = dirPathTo;
		}
		public String getFilePathFrom() {
			return filePathFrom;
		}
		public void setFilePathFrom(String filePathFrom) {
			this.filePathFrom = filePathFrom;
		}
		public int getCompressRatio() {
			return compressRatio;
		}
		public void setCompressRatio(int compressRatio) {
			this.compressRatio = compressRatio;
		}

	}
	
}






