package com.foomap.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.foodmap.R;
import com.foomap.model.UserInfo;
import com.foomap.model.UserInfo.UserData;
import com.foomap.service.CommentHttpService;
import com.foomap.service.CommentHttpService.ICommentAdapter;
import com.foomap.service.HttpServiceHelper.IOnHttpRequeseListener;
import com.foomap.service.LocationSever;
import com.foomap.service.ShophttpService;
import com.foomap.service.ShophttpService.IShopDataAdapter;
import com.foomap.util.CommentActivityGridAdapter;
import com.foomap.util.PictureFileUtils;
import com.foomap.util.ShowPictureInfo;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RatingBar.OnRatingBarChangeListener;

public class CommentActivity extends Activity implements OnItemClickListener{
	private static String TAG="CommentActivity";
	
	private static final int LOCATION_OK = 0;
	private static final int TAKE_PICTURE = 0;
	private static final int RESULT_LOAD_IMAGE = 1;
	private Context context;
	private GridView mGridView;
	private EditText mCommentContent;
	private CommentActivityGridAdapter mAdapter;
	private String mCommentStr;
	private List<ShowPictureInfo> mInfo = new ArrayList<ShowPictureInfo>();
	private List<String> mDrr = new ArrayList<String>();
	private List<String> last_mDrr = new ArrayList<String>();
	private HorizontalScrollView selectimg_horizontalScrollView;
	private Button mButton;
	private Button mButton2;
	private String mUserIdStr;
	private String mNameStr;
	private String mAddressStr;
	private UserData userData;
	private double longitude = -1;
	private double latitude = -1;
	private LocationSever mLocationServer;
	private BDLocationListener mBDListener;
	private TextView mLocationView; 
	private int mShopId;
	private CommentHttpService commentHttpService;
	private String mTime;
	private RatingBar mRatingBar;
	private EditText mCostEdit;
	private float mRating;
	private String mCostStr;
	private float mCost = -1;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOCATION_OK:
				Log.i("setAdrress",mAddressStr);
				mLocationView.setText(mAddressStr);
				mLocationServer.stop();
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(com.foodmap.R.layout.comment_activity_main);
		this.context = this;
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
		userData = UserInfo.getUserData(getApplicationContext());
		mUserIdStr = userData.userId;
		Log.i("userId",mUserIdStr);
		mLocationServer.requestLocation(mBDListener);
		mGridView = (GridView) findViewById(com.foodmap.R.id.commentActivityGridView);  
		TextView mTextView = (TextView)findViewById(com.foodmap.R.id.commentActivity_name);
		mLocationView = (TextView)findViewById(com.foodmap.R.id.commentActivity_location);
		Bundle extras = getIntent().getExtras();
		mNameStr = extras.getString("shopName");
		mShopId = extras.getInt("shopId");
		mTime = (String) new DateFormat().format("yyyy-MM-dd-hh-mm",Calendar.getInstance(Locale.CHINA));
		Log.i("Time:",mTime);
		mTextView.setText(mNameStr);
		Init();
	}
	
	public void Init(){
		mButton = (Button)findViewById(R.id.commentActivityCreatButton);
		mButton2 = (Button)findViewById(R.id.commentActivityCancelButton);
		selectimg_horizontalScrollView = (HorizontalScrollView) findViewById(R.id.commentActivityHorizontalScrollView);
		mCommentContent = (EditText)findViewById(R.id.commentActivityEditText);
		mCommentContent.setFocusable(true);
		mCommentContent.setFocusableInTouchMode(true);
		mGridView = (GridView) findViewById(com.foodmap.R.id.commentActivityGridView);  
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		convertPicture();
		gridViewInit();
		mRatingBar = (RatingBar)findViewById(R.id.commentActivity_rating);
		mRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener(){

			@Override
			public void onRatingChanged(RatingBar arg0, float arg1, boolean arg2) {
				// TODO Auto-generated method stub
				mRating = arg1;
			}
		});
		mCostEdit = (EditText)findViewById(R.id.commentActivity_cost);
		mCostEdit.setFocusable(true);
		mCostEdit.setFocusableInTouchMode(true);
		mCostEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				mCostStr= s.toString();
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
		mCommentContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				mCommentStr = s.toString();
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
		mButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(judgeImg())
				{
					if(commentHttpService==null)
					{
						commentHttpService=new CommentHttpService(context);
					}
					Log.i("mUserIdStr",mUserIdStr);
					Log.i("mTime",mTime);
					Log.i("mShopId",""+mShopId);
					Log.i("mDrr",""+last_mDrr.size());
					Log.i("mCommentStr",mCommentStr);
				   commentHttpService.addComment(new ICommentAdapter() {
					
					@Override
					public String getUserId() {
						// TODO Auto-generated method stub
						return mUserIdStr;
					}
					@Override
					public String getTime() {
						// TODO Auto-generated method stub
						return mTime;
					}
					
					@Override
					public int getShopId() {
						// TODO Auto-generated method stub
						return mShopId;
					}
					
					@Override
					public List<String> getPicPaths() {
						// TODO Auto-generated method stub
			
						return last_mDrr;

					}
					
					@Override
					public double getGradeAvg() {
						// TODO Auto-generated method stub
						return mRating;
					}
					
					@Override
					public Integer getCostAvg() {
						// TODO Auto-generated method stub
						return Integer.parseInt(mCostStr);
					}
					
					@Override
					public String getComment() {
						// TODO Auto-generated method stub
						return mCommentStr;
					}
				});
				   Toast.makeText(getApplicationContext(), "数据上传...",
							Toast.LENGTH_SHORT).show();
				   Log.i("mUserIdStr_again",mUserIdStr);
					Log.i("mTime_again",mTime);
					Log.i("mShopId_again",""+mShopId);
					Log.i("mDrr_again",""+mDrr.size());
					Log.i("mCommentStr_again",mCommentStr);
					CommentActivity.this.finish();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "数据不完整，请填完上传...",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		mButton2.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CommentActivity.this.finish();
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
		mAdapter = new CommentActivityGridAdapter(this,mInfo);
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
				.hideSoftInputFromWindow(CommentActivity.this
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
		if (arg2 == mInfo.size()) {
			String sdcardState = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
				Intent intent = new Intent(CommentActivity.this,ShowPictureActivity.class);
				intent.putExtra("ID", arg2);
				startActivityForResult(intent,RESULT_LOAD_IMAGE);
			} else {
				Toast.makeText(getApplicationContext(), "sdcard已拔出，不能选择照片",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Intent intent = new Intent(CommentActivity.this,ShowPictureActivity.class);
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
	
		
		if(last_mDrr==null||mNameStr==null||mCostStr==null||mUserIdStr==null||
			mAddressStr==null||mNameStr.isEmpty()||last_mDrr.size()<=0||mUserIdStr.isEmpty()||mAddressStr.isEmpty()||mCostStr.isEmpty())
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
			Log.i("mUserIdStr_again",mUserIdStr);
			Log.i("mCostFloat_again",mCostStr);
			Log.i("mAddressStr_again",mAddressStr);
			return true;
		}
	}
	
	protected void onDestroy() {

		PictureFileUtils.deleteDir(PictureFileUtils.SDPATH);
		// 清理图片缓存
		for (int i = 0; i < mInfo.size(); i++) {
			if(mInfo.get(i).getBitmap()!=null)
				mInfo.get(i).getBitmap().recycle();
		}
		mInfo.clear();
		super.onDestroy();
	}
}
