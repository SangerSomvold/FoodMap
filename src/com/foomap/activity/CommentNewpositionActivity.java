package com.foomap.activity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RatingBar.OnRatingBarChangeListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.foodmap.R;
import com.foomap.model.UserInfo;
import com.foomap.model.UserInfo.UserData;
import com.foomap.service.LocationSever;
import com.foomap.service.ShophttpService;
import com.foomap.service.ShophttpService.IShopDataAdapter;
import com.foomap.util.CommentNewpositionActivityGridAdapter;
import com.foomap.util.DraftsDbManager;
import com.foomap.util.PictureFileUtils;
import com.foomap.util.ShowPictureActivityGridAdapter;
import com.foomap.util.ShowPictureInfo;

public class CommentNewpositionActivity extends Activity implements OnItemClickListener{
	private static String TAG="CommentNewpositionActivity";

	private static final int LOCATION_OK = 0;
	private static final int TAKE_PICTURE = 0;
	private static final int RESULT_LOAD_IMAGE = 1;
	private Context context;
	private Button mButton;
	private GridView mGridView;
	private EditText mPoiName;
	private EditText mPoiFeature;
	private EditText mPoicost;
	private EditText mPoiAddress;
	private float mRating=-1;
	private CommentNewpositionActivityGridAdapter mAdapter;
	private String mNameStr;
	private String mFeatureStr;
	private String mCostStr;
	private String mAddressStr;
	private float mCostFloat=-1;
	private List<ShowPictureInfo> mInfo = new ArrayList<ShowPictureInfo>();
	private List<String> mDrr = new ArrayList<String>();
	private List<String> last_mDrr = new ArrayList<String>();
	private HorizontalScrollView selectimg_horizontalScrollView;
	private RatingBar rating; 
	private DraftsDbManager dbManager;
	private Button draftsButton;
	private String mUserIdStr;
	private int mId;
	private String picture;
	private UserData userData;
	private LocationSever mLocationServer;
	private BDLocationListener mBDListener;
	private double longitude=-1;
	private double latitude=-1;
	private int contentFlag = 0;
	private String mTime;
	private Spinner mSpinner;
	private String[] mSpinnerItems = {"火锅","干锅","清真","西餐","串串","快餐","冒菜","炒菜","豆花","其他"};
	private ArrayAdapter mSpinnerAdapter;
	private int mTypeId = 1;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOCATION_OK:
				Log.i("setAdrress",mAddressStr);
				mPoiAddress.setText(mAddressStr);
				mLocationServer.stop();
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(com.foodmap.R.layout.comment_activity_newposition);
		this.context=this;
		mTime = (String) new DateFormat().format("yyyy-MM-dd-hh-mm",Calendar.getInstance(Locale.CHINA));
		dbManager = new DraftsDbManager(this);
		mLocationServer = new LocationSever(this);
		mBDListener=new BDLocationListener() {
			
			@Override
			public void onReceiveLocation(BDLocation arg0) {
				// TODO Auto-generated method stub
				mAddressStr = arg0.getAddrStr();
				longitude = arg0.getLongitude();
				latitude = arg0.getLatitude();
				if(!mAddressStr.isEmpty())
				{
					Log.i("location information",mAddressStr);
					mHandler.sendEmptyMessage(LOCATION_OK);
				}
				else mLocationServer.stop();
			}
		};
		mLocationServer.requestLocation(mBDListener);
		mGridView = (GridView) findViewById(com.foodmap.R.id.commentActivityNewpositionGridView);  
		Bundle extras = getIntent().getExtras();
		mId = extras.getInt("id");
		if(mId == 0)
		{	
			/*
			 * find the user data from user database
			 */
			userData = UserInfo.getUserData(getApplicationContext());
			if(userData == null)
			{
				Intent mIntent = new Intent(CommentNewpositionActivity.this,LoginActivity.class);
				startActivity(mIntent);
				CommentNewpositionActivity.this.finish();
			}
			else
			{
				mUserIdStr = userData.userId;
				contentFlag++;
				Log.i("Drafts or Create?","Create");
			}
		}
		else if(mId == 1)
		{
			mUserIdStr = extras.getString("userId");
			mNameStr = extras.getString("name");
			mRating = extras.getFloat("rating");
			mCostFloat = extras.getFloat("cost");
			mCostStr = ""+mCostFloat;
			mFeatureStr = extras.getString("feature");
			mAddressStr = extras.getString("address");
			picture = extras.getString("picture");
			mDrr = convertList(picture);
			last_mDrr = convertList(picture);
			Log.i("Drafts or Create?","Drafts");
			Log.i("mDrr's length",""+mDrr.size());
		}
		Init();
	}
	
	public void Init(){
		mButton = (Button)findViewById(R.id.commentActivityNewpositionCreatButton);
		draftsButton = (Button)findViewById(R.id.commentActivityNewpositionDraftsButton);
		selectimg_horizontalScrollView = (HorizontalScrollView) findViewById(R.id.commentActivityNewpositionHorizontalScrollView);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		convertPicture();
		gridViewInit();
		mPoiName = (EditText)findViewById(R.id.commentActivityNewposition_name);
		mPoiName.setFocusable(true);
		mPoiName.setFocusableInTouchMode(true);
		if(mId == 1) mPoiName.setText(mNameStr);
		mPoiName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				mNameStr = s.toString();
				if(!mNameStr.isEmpty()) contentFlag++;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
		mPoiFeature = (EditText)findViewById(R.id.commentActivityNewpositionEditText_feature);
		mPoiFeature.setFocusable(true);
		mPoiFeature.setFocusableInTouchMode(true);
		if(mId == 1) mPoiFeature.setText(mFeatureStr);
		mPoiFeature.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				mFeatureStr = s.toString();
				if(!mFeatureStr.isEmpty()) contentFlag++;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
		mPoicost = (EditText)findViewById(R.id.commentActivityNewpositionEditText_cost);
		mPoicost.setFocusable(true);
		mPoicost.setFocusableInTouchMode(true);
		if(mId == 1) mPoicost.setText(mCostStr);
		mPoicost.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				mCostStr = s.toString();
				if(!mCostStr.isEmpty()) contentFlag++;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
		mPoiAddress = (EditText)findViewById(R.id.commentActivityNewposition_location);
		mPoiAddress.setFocusable(true);
		mPoiAddress.setFocusableInTouchMode(true);
		mPoiAddress.setText(mAddressStr);
		mPoiAddress.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				mAddressStr = s.toString();
				if(!mAddressStr.isEmpty()) contentFlag++;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
		rating = (RatingBar)findViewById(R.id.commentActivityNewposition_rating);
		if(mId == 1) rating.setRating(mRating);
		rating.setOnRatingBarChangeListener(new OnRatingBarChangeListener(){

			@Override
			public void onRatingChanged(RatingBar arg0, float arg1, boolean arg2) {
				// TODO Auto-generated method stub
				mRating = arg1;
				contentFlag++;
			}
		});
		mSpinner = (Spinner)findViewById(R.id.commentActivityNewposition_spinner);
		mSpinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,mSpinnerItems);
		mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinner.setAdapter(mSpinnerAdapter);
		
		mSpinner.setVisibility(View.VISIBLE);
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				for(int i=0;i<mSpinnerItems.length;i++)
				{
					if(mSpinnerItems[arg2] == mSpinnerItems[i])
					{
						mTypeId = i+1;
						break;
					}
				}
				Log.i("mTypeId:",""+mTypeId);
				Log.i("arg2 of spinner:",""+arg2);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		draftsButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					if(judgeImg())
					{
						mCostFloat = Float.parseFloat(mCostStr);
						String str = convertJson(last_mDrr);
						Log.i("mUserIdStr",mUserIdStr);
						Log.i("mNameStr",mNameStr);
						Log.i("mRating",""+mRating);
						Log.i("mCostFloat",""+mCostFloat);
						Log.i("mFeatureStr",mFeatureStr);
						Log.i("mAddressStr",mAddressStr);
						Log.i("convertJson",str);
						dbManager.addInfo(mUserIdStr,mNameStr,mRating,mCostFloat,mFeatureStr,mAddressStr,str);
						Toast.makeText(getApplicationContext(), "店铺保存中...",
								Toast.LENGTH_SHORT).show();
						CommentNewpositionActivity.this.finish();
					}
					else
					{
						Toast.makeText(getApplicationContext(), "信息不完整，请填完保存...",
								Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		mButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				if(judgeImg()){
					mCostFloat = Float.parseFloat(mCostStr);
					ShophttpService mService = new ShophttpService(context);
					mService.addShop(new IShopDataAdapter() {
						
						@Override
						public int getTypeId() {
							// TODO Auto-generated method stub
							return mTypeId;
						}
						
						@Override
						public List<String> getPicsPath() {
							// TODO Auto-generated method stub

						//	mDrr.add("/storage/sdcard0/LoveWallpaper/save/113778-102.jpg");

							for(String d:mDrr)
							{
								Log.i(TAG,d);
							}

							return last_mDrr;
						}
						
						@Override
						public String getName() {
							// TODO Auto-generated method stub
							return mNameStr;
						}
						
						@Override
						public double getLongitude() {
							// TODO Auto-generated method stub
							return longitude;
						}
						
						@Override
						public double getLatitude() {
							// TODO Auto-generated method stub
							return latitude;
						}
						
						
						@Override
						public String getIconPath() {
							// TODO Auto-generated method stub
							if(mDrr.size()>0)
								return mDrr.get(0);
							else {
								return null;
							}
						}
						
						@Override
						public double getGrade_avg() {
							// TODO Auto-generated method stub
							return mRating;
						}
						
						@Override
						public String getFeature() {
							// TODO Auto-generated method stub
							return mFeatureStr;
						}
						
						@Override
						public String getCreateUserId() {
							// TODO Auto-generated method stub
							return mUserIdStr;
						}
						
						@Override
						public double getCost_avg() {
							// TODO Auto-generated method stub
							return mCostFloat;
						}
						
						@Override
						public String getAddress() {
							// TODO Auto-generated method stub
							return mAddressStr;
						}

						@Override
						public String getCreateDate() {
							// TODO Auto-generated method stub
							return mTime;
						}

					} );
					Toast.makeText(getApplicationContext(), "店铺新建中...",
							Toast.LENGTH_SHORT).show();
					CommentNewpositionActivity.this.finish();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "信息不完整，请填完上传...",
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	public void gridViewInit(){	
		int size = 0;
		if (mInfo.size() < 6) {
			size = mInfo.size() + 1;
		} else {
			size = mInfo.size();
		}
		mAdapter = new CommentNewpositionActivityGridAdapter(this,mInfo);
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
		.addOnPreDrawListener(// 缁樺埗瀹屾瘯
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
				.hideSoftInputFromWindow(CommentNewpositionActivity.this
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
		if (arg2 == mInfo.size()) {
			String sdcardState = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
				Intent intent = new Intent(CommentNewpositionActivity.this,ShowPictureActivity.class);
				startActivityForResult(intent,RESULT_LOAD_IMAGE);
			} else {
				Toast.makeText(getApplicationContext(), "sdcard已拔出，不能选择照片",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Intent intent = new Intent(CommentNewpositionActivity.this,ShowPictureActivity.class);
			startActivityForResult(intent,RESULT_LOAD_IMAGE);
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {	
		case RESULT_LOAD_IMAGE:
			if (mInfo.size() < 6 && resultCode == RESULT_OK && null != data) {// 鐩稿唽杩斿洖
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
	
	private String convertJson(List<String> drr) throws JSONException
	{
		String str = "";
		JSONObject object = new JSONObject();
		JSONArray jsonarray = new JSONArray();
		for(int i=0;i<drr.size();i++)
		{
			JSONObject obj = new JSONObject();
			obj.put(""+i, drr.get(i));
			jsonarray.put(obj);
		}
		object.put("picture", jsonarray);
		str = object.toString();
		contentFlag++;
		return str;
	}
	private List<String> convertList(String str)
	{
		List<String> drr = new ArrayList<String>();
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(str);
			JSONArray jsonArray=jsonObject.getJSONArray("picture");
			for(int i=0;i<jsonArray.length();i++)
			{
				JSONObject obj = jsonArray.getJSONObject(i);
				String path = obj.getString(""+i);
				drr.add(path);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return drr;
	}
	
	private void convertPicture()
	{
		for (int i = 0; i < mDrr.size(); i++) {
			 ShowPictureInfo info = new ShowPictureInfo();
			 BitmapFactory.Options options = new BitmapFactory.Options();
			 options.inJustDecodeBounds = true;
			 Bitmap bitmap =BitmapFactory.decodeFile(mDrr.get(i),options);
			 options.inJustDecodeBounds = false;
			 int be = options.outHeight/40;
			 if (be <= 0) {
				 be = 10;
			 }
			 options.inSampleSize = be;
			 bitmap = BitmapFactory.decodeFile(mDrr.get(i),options);
			 Log.i("URL of picture",mDrr.get(i));
			 info.setBitmap(bitmap);
			 info.setDrr(mDrr.get(i));
			 mInfo.add(info);
		}
	}
	
	
	protected boolean judgeImg()
	{
	
		
		if(last_mDrr==null||mNameStr==null||mCostStr==null||mUserIdStr==null||mFeatureStr==null||
				mAddressStr==null||mNameStr.isEmpty()||last_mDrr.size()<=0||mUserIdStr.isEmpty()||mAddressStr.isEmpty()||mCostStr.isEmpty()||mFeatureStr.isEmpty())
		{
			return false;
		}
		else
		{
			Log.i("mDrr_again",""+mDrr.size());
			Log.i("mNameStr_again",mNameStr);
			Log.i("longitude_again",""+longitude);
			Log.i("latitude_again",""+latitude);
			Log.i("mRating_again",""+mRating);
			Log.i("mFeatureStr_again",mFeatureStr);
			Log.i("mUserIdStr_again",mUserIdStr);
			Log.i("mCostFloat_again",""+mCostFloat);
			Log.i("mAddressStr_again",mAddressStr);
			return true;
		}
	}
	
	
	protected void onDestroy() {

		PictureFileUtils.deleteDir(PictureFileUtils.SDPATH);
		// 娓呯悊鍥剧墖缂撳瓨
		for (int i = 0; i < mInfo.size(); i++) {
			if(mInfo.get(i).getBitmap()!=null)
				mInfo.get(i).getBitmap().recycle();
		}
		//mDrr.clear();
		mInfo.clear();
		super.onDestroy();
	}
}
