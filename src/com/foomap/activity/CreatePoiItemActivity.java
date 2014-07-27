package com.foomap.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.foodmap.R;
import com.foomap.model.UserInfo;
import com.foomap.model.UserInfo.UserData;
import com.foomap.util.CreatePoiItemActivityDataList;
import com.foomap.util.CreatePoiItemActivityGridAdapter;
import com.foomap.util.CreatePoiItemActivityListAdapter;
import com.foomap.util.CreatePoiItemActivityListViewItem;
import com.foomap.util.DraftsDbManager;
import com.foomap.util.PictureFileUtils;
import com.foomap.util.ShowPictureInfo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class CreatePoiItemActivity extends Activity implements OnItemClickListener{

	private static final int TAKE_PICTURE = 0;
	private static final int RESULT_LOAD_IMAGE = 1;
	private static final int CUT_PHOTO_REQUEST_CODE = 2;
	private String path = ""; 
	private String photoUrl = "";
	private GridView mGridView;
	private CreatePoiItemActivityGridAdapter mAdapter;
	private String mTemp;
	private List<ShowPictureInfo> mInfo = new ArrayList<ShowPictureInfo>();
	private List<String> last_mDrr = new ArrayList<String>();
	private List<String> mDrr = new ArrayList<String>();
	private HorizontalScrollView selectimg_horizontalScrollView;
	private Button mButton;
	private UserData userData;
	private String mAddressStr;
	private DraftsDbManager mDbManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(com.foodmap.R.layout.create_poi_item_activity_main);
		/*
		 * initialize the drafts database
		 */
		mDbManager = new DraftsDbManager(this);
		ListView mListView = (ListView) findViewById(com.foodmap.R.id.createPoiItemActivityListView);
		/*
		 * find the user data from user database
		 */
		userData = UserInfo.getUserData(getApplicationContext());
        List<CreatePoiItemActivityListViewItem> list=CreatePoiItemActivityDataList.getData(getApplicationContext());
        CreatePoiItemActivityListAdapter adapter=new CreatePoiItemActivityListAdapter(this,list);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(userData == null)
				{
					Intent mListIntent = new Intent(CreatePoiItemActivity.this,LoginActivity.class);
					startActivity(mListIntent);
				}
				else{
					Log.i("userId",userData.userId);
					Intent mListIntent = new Intent(CreatePoiItemActivity.this,CommentActivity.class);
					CreatePoiItemActivityListViewItem mItem = (CreatePoiItemActivityListViewItem)arg0.getItemAtPosition(arg2);
					String mName = mItem.name_input;
					mListIntent.putExtra("userId", userData.userId);
					mListIntent.putExtra("name", mName);
					mListIntent.putExtra("address", mAddressStr);
					mListIntent.putExtra("pictureUrl", (Serializable)last_mDrr);
					startActivity(mListIntent);
				}
				/*Intent mListIntent = new Intent(CreatePoiItemActivity.this,CommentNewpositionActivity.class);
				ContentValues cv = new ContentValues();
				cv = mDbManager.queryInfo("fancy","shop");
				mListIntent.putExtra("id",1);
				mListIntent.putExtra("userId", cv.getAsString("userId"));
				mListIntent.putExtra("name", cv.getAsString("name"));
				mListIntent.putExtra("rating", cv.getAsFloat("rating"));
				mListIntent.putExtra("cost", cv.getAsFloat("cost"));
				mListIntent.putExtra("feature", cv.getAsString("feature"));
				mListIntent.putExtra("address", cv.getAsString("address"));
				mListIntent.putExtra("comment", cv.getAsString("comment"));
				mListIntent.putExtra("picture", cv.getAsString("picture"));*/
			}
		});
		Init();
	} 
	public void Init(){
		mGridView = (GridView) findViewById(com.foodmap.R.id.createPoiItemActivityGridView);  
		selectimg_horizontalScrollView = (HorizontalScrollView) findViewById(R.id.createPoiItemActivityHorizontalScrollView);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mButton = (Button)findViewById(R.id.createPoiItemActivityCreatButton);
		mButton.setOnClickListener(new  OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(userData == null)
				{
					Intent mButtonIntent = new Intent(CreatePoiItemActivity.this,LoginActivity.class);
					startActivity(mButtonIntent);
				}
				else{
					Log.i("userId",userData.userId);
					Intent mButtonIntent = new Intent(CreatePoiItemActivity.this,CommentNewpositionActivity.class);
					mButtonIntent.putExtra("id", 0);
					mButtonIntent.putExtra("pictureUrl", (Serializable)last_mDrr);
					mButtonIntent.putExtra("userId",userData.userId);
					//mButtonIntent.putExtra("userId","fancy");
					startActivity(mButtonIntent);
				}
			}
		});
		gridViewInit();
	}
	
	public void gridViewInit(){	
		int size = 0;
		if (mInfo.size() < 3) {
			size = mInfo.size() + 1;
		} else {
			size = mInfo.size();
		}
		mAdapter = new CreatePoiItemActivityGridAdapter(this,mInfo);
		mAdapter.setSelectedPosition(0);
		LayoutParams params = mGridView.getLayoutParams();
		final int width = size * (int) (15 * 9.4f);
		params.width = width;
		mGridView.setLayoutParams(params);
		mGridView.setColumnWidth((int) (15 * 9.4f));
		mGridView.setStretchMode(GridView.NO_STRETCH);
		mGridView.setNumColumns(size);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(this);
		selectimg_horizontalScrollView.getViewTreeObserver()
		.addOnPreDrawListener(// 绘制完毕
				new OnPreDrawListener() {
					public boolean onPreDraw() {
						selectimg_horizontalScrollView.scrollTo(width,
								0);
						selectimg_horizontalScrollView
								.getViewTreeObserver()
								.removeOnPreDrawListener(this);
						return false;
					}
				});
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(CreatePoiItemActivity.this
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
		if (arg2 == mInfo.size()) {
			String sdcardState = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
				Intent intent = new Intent(CreatePoiItemActivity.this,ShowPictureActivity.class);
				intent.putExtra("ID", arg2);
				startActivityForResult(intent,RESULT_LOAD_IMAGE);
			} else {
				Toast.makeText(getApplicationContext(), "sdcard已拔出，不能选择照片",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Intent intent = new Intent(CreatePoiItemActivity.this,ShowPictureActivity.class);
			intent.putExtra("ID", arg2);
			startActivityForResult(intent,RESULT_LOAD_IMAGE);
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {	
		case RESULT_LOAD_IMAGE:
			if (mInfo.size() < 6 && resultCode == RESULT_OK && null != data) {// 相册返回
				mDrr = (List<String>)data.getSerializableExtra("pictureUrl");
				for(int i=0;i<mDrr.size();i++)
				{
					last_mDrr.add(mDrr.get(i));
				}
				convertPicture();
				gridViewInit();
			}
			break;
		}
	}
	
	private void convertPicture()
	{
		for (int i = 0; i < mDrr.size(); i++) {
			 ShowPictureInfo info = new ShowPictureInfo();
			 BitmapFactory.Options options = new BitmapFactory.Options();
			 options.inJustDecodeBounds = true;
			 Bitmap bitmap =BitmapFactory.decodeFile(mDrr.get(i),options);
			 options.inJustDecodeBounds = false;
			 int be = options.outHeight/20;
			 if (be <= 0) {
				 be = 20;
			 }
			 options.inSampleSize = be;
			 bitmap = BitmapFactory.decodeFile(mDrr.get(i),options);
			 Log.i("URL of picture",mDrr.get(i));
			 info.setBitmap(bitmap);
			 info.setDrr(mDrr.get(i));
			 mInfo.add(info);
		}
	}
	protected void onDestroy() {

		PictureFileUtils.deleteDir(PictureFileUtils.SDPATH);
		// 清理图片缓存
		for (int i = 0; i < mInfo.size(); i++) {
			if(mInfo.get(i).getBitmap()!=null)
				mInfo.get(i).getBitmap().recycle();
		}
		mDrr.clear();
		mInfo.clear();
		super.onDestroy();
	}
}
