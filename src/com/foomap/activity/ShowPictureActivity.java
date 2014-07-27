package com.foomap.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.foodmap.R;
import com.foomap.util.CompressImg;
import com.foomap.util.ShowPictureActivityGridAdapter;
import com.foomap.util.ShowPictureInfo;
import com.foomap.util.CompressImg.CompressImgConfig;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ShowPictureActivity extends Activity{

	private final static int SCAN_OK = 1;
	private final static int SCAN_ING = 2;
	private ProgressDialog mProgressDialog;
    private GridView mGridView;
    private ShowPictureActivityGridAdapter mGridAdapter;
    private static ArrayList<String> paths = new ArrayList<String>();
    private static String PICPATH = Environment.getExternalStorageDirectory()+ "/DCIM/Camera/";
	private List<ShowPictureInfo> mInfo = new ArrayList<ShowPictureInfo>();
	private List<String> mDrr = new ArrayList<String>();
    private Button mPbutton;
    private Button mNbutton;
    private String path = "";
    private String photoUrl = "";
    private static final int TAKE_PICTURE = 0;
    private boolean mKilled = false;
    private CompressImg comImg;
    private CompressImg.CompressImgConfig compressConfig;
	
    private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SCAN_OK:
				mProgressDialog.dismiss();
		        mGridAdapter.notifyDataSetChanged();
				break;
			case SCAN_ING:
				mProgressDialog.dismiss();
				mGridAdapter.notifyDataSetChanged();
				break;
			}
		}
	};
    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_picture_activity_main);
        /*try {
			buildThum();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        //getImages();
        mPbutton = (Button)findViewById(R.id.showPictureActivity_positiveButton);
        mNbutton = (Button)findViewById(R.id.showPictureActivity_negativeButton);
        mPbutton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				convert();
				Intent intent = new Intent();
				intent.putExtra("pictureUrl", (Serializable)mDrr);
				setResult(Activity.RESULT_OK,intent);
				mKilled = true;
				ShowPictureActivity.this.finish();
			}
		});
		mNbutton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						mKilled = true;
						ShowPictureActivity.this.finish();
					}
				});
        mGridView = (GridView) findViewById(R.id.showPictureActivityGridView);
        mGridAdapter = new ShowPictureActivityGridAdapter(this,mInfo);
        mGridView.setAdapter(mGridAdapter);
        mGridView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				// TODO Auto-generated method stub
				ShowPictureInfo info = (ShowPictureInfo) mGridAdapter.getItem(position);
				Log.i("info","clicked");
				if(position == 0)
				{
					photo();
				}
				else if(info.getStatus())
				{
					mInfo.get(position).setStatus(false);
				}
				else mInfo.get(position).setStatus(true);
				mGridAdapter.notifyDataSetChanged();
			}
        });
        getImages();
    }

    public void photo() {
    	
    	Intent openCameraIntent = new Intent(
				MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}
    
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
    	if (resultCode == Activity.RESULT_OK) { 
    		String name = "IMG_"+new DateFormat().format("yyyyMMdd_hhmmss",Calendar.getInstance(Locale.CHINA)) + ".jpg";
    		Bundle bundle = data.getExtras();  
    		Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式  
    		FileOutputStream b = null;
    		File file = new File(PICPATH);
    		String fileName = PICPATH+name;
    		try {
				b = new FileOutputStream(fileName);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件 
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
    		try {
				b.flush();
				b.close();  
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
    		photoUrl = fileName;
    		mDrr.add(photoUrl);
    		Intent intent = new Intent();
			intent.putExtra("pictureUrl", (Serializable)mDrr);
			setResult(Activity.RESULT_OK,intent);
			mKilled = true;
			ShowPictureActivity.this.finish();
    	}
    }

    
    private void convert()
    {
    	for(int i=0;i<mInfo.size();i++)
    	{
    		if(mInfo.get(i).getStatus())
    		{
    			String str = mInfo.get(i).getDrr();
        		mDrr.add(str);
    		}
    	}
    }
    
	
	private void getImages() {
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			return;
		}
		
		mProgressDialog = ProgressDialog.show(this, null, "加载中...");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = ShowPictureActivity.this.getContentResolver();

				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);
				
				while (mCursor.moveToNext() && mKilled == false) {
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					Log.i("image_path",path);
					ShowPictureInfo info = new ShowPictureInfo();
					 /*BitmapFactory.Options options = new BitmapFactory.Options();
					 options.inJustDecodeBounds = true;
					 Bitmap bitmap =BitmapFactory.decodeFile(path,options);
					 options.inJustDecodeBounds = false;
					 int be = options.outHeight/40;
					 if (be <= 0) {
						 be = 10;
					 }
					 options.inSampleSize = be;
					 bitmap = BitmapFactory.decodeFile(path,options);*/
					 comImg = new CompressImg();
					 compressConfig = comImg.new CompressImgConfig();
					 compressConfig.setCompressRatio(20);
					 compressConfig.setDirPathTo(Environment.getExternalStorageDirectory()
							+ "/foodmap/cache");
					 compressConfig.setRequestHeight(320);
					 compressConfig.setRequestWide(400);
					 compressConfig.setFilePathFrom(path);
					 compressConfig.setAutoRecycle(false);
					 comImg.setConfig(compressConfig);
					 Bitmap bitmap = comImg.getBitmap(true);
					 comImg.loadBitmap(bitmap);
					 info.setBitmap(bitmap);
					 info.setDrr(path);
					 mInfo.add(info);
					 Log.i("URL",path);
					 String parentName = new File(path).getParentFile().getName();
					 Log.i("parentURL",parentName);
					 mHandler.sendEmptyMessage(SCAN_ING);
					
				}
				mCursor.close();
				mHandler.sendEmptyMessage(SCAN_OK);
			}
		}).start();
	}
	
	protected void onDestroy() {

		for (int i = 0; i < mInfo.size(); i++) {
			if(mInfo.get(i).getBitmap()!=null)
				mInfo.get(i).getBitmap().recycle();
		}
		comImg.recycleBitmap();
		mDrr.clear();
		mInfo.clear();
		mKilled = true;
		super.onDestroy();
	}
}