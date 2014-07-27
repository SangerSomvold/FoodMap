package com.foomap.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.ParcelFileDescriptor.OnCloseListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.foodmap.R;
import com.foomap.model.*;
import com.foomap.model.UserInfo.UserData;
import com.foomap.service.CollectionHttpService;
import com.foomap.service.HttpServiceHelper.IOnHttpRequeseListener;
import com.foomap.util.CollectionJsonUtils;
import com.foomap.util.DraftlvAdapter;
import com.foomap.util.DraftsDbManager;
import com.foomap.util.PersonalpageSwipeAdapter;
import com.foomap.util.ShowPictureInfo;
import com.foomap.view.BaseSwipeListViewListener;
import com.foomap.view.SwipeListView;

public class UsrInfoActivity extends Activity {

	static String TAG = "UsrInfoActivity";

	private SwipeListView mSwipeListView;
	private static final int RESULT_LOAD_IMAGE = 1;
	private TextView Username;
	String Usernamesting = "default";
	private PersonalpageSwipeAdapter mAdapter;
	public static int deviceWidth;
	private List<String> testData;
	public int[] shopId;
	public ImageButton backbtn, draftbtn, passwordchangebtn;
	private List<String> mDrr = new ArrayList<String>();
	private Bitmap mBitmap;
	private ImageButton mImage;

	private Button logoff;

	private UserData usr;

	CollectionHttpService cHttpService;
	private Context context;
	// 收藏数据
	private ArrayList<CollectionData> dataList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.personalpage_lv);

		this.context = this;
		Username = (TextView) findViewById(R.id.ID);
		usr = UserInfo.getUserData(getApplicationContext());
		if (usr == null) {
			Usernamesting = "default";

		} else {

			Usernamesting = usr.userId;
		}
		// if(!getUsername().equals(null))

		Username.setText(Usernamesting);
		mSwipeListView = (SwipeListView) findViewById(R.id.restauran_lv_list);
		testData = new ArrayList<String>();

		refreshUi(usr);
		dataList = new ArrayList<CollectionData>();
		mAdapter = new PersonalpageSwipeAdapter(context, mSwipeListView,
				dataList);
		/*
		 * new PersonalpageSwipeAdapter(this, R.layout.personalpagerow,
		 * testData,mSwipeListView);
		 */
		deviceWidth = getDeviceWidth();
		mSwipeListView.setAdapter(mAdapter);
		mSwipeListView
				.setSwipeListViewListener(new TestBaseSwipeListViewListener());
		reload();
		mImage = (ImageButton) findViewById(R.id.Userpic);

		mImage.setOnClickListener(listener);
		logoff = (Button) findViewById(R.id.logoff);

		logoff.setOnClickListener(btnlistener);
		draftbtn = (ImageButton) findViewById(R.id.draft);
		draftbtn.setOnClickListener(listener);
		backbtn = (ImageButton) findViewById(R.id.personalpageback);
		backbtn.setOnClickListener(listener);
		passwordchangebtn = (ImageButton) findViewById(R.id.passwordchange);
		passwordchangebtn.setOnClickListener(listener);
		// refreshUi(usr);
		mSwipeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Bundle bundle = new Bundle();

				bundle.putInt("shopId", dataList.get(position).shopId);
				startActivity(intent);
				finish();

			}
		});

	}

	protected void refreshUi(UserData usr) {
		cHttpService = new CollectionHttpService(context.getApplicationContext());
		cHttpService.getCollection(usr.userId, new IOnHttpRequeseListener() {

			@Override
			public void finished(String jsonRes) {
				if(null==jsonRes)
				{return;}
				else
				{
				// TODO Auto-generated method stub
				Log.i(TAG, jsonRes);
				int count = 0;

				ArrayList<CollectionData> list = CollectionJsonUtils
						.getCollectionList(jsonRes);
				dataList.addAll(list);
				Log.i(TAG, jsonRes);

				ArrayList<CollectionData> dataList = CollectionJsonUtils
						.getCollectionList(jsonRes);
				for (CollectionData d : dataList) {
					Log.i(TAG, "----->" + d.shopName);
					Log.i(TAG, d.shopId + "");
				}

				/*
				 * for(CollectionData data:dataList) {
				 * testData.add(data.shopName); shopId[count]=data.shopId; }
				 */
				mAdapter.notifyDataSetChanged();

			}}
		});
	}

	private int getDeviceWidth() {
		return getResources().getDisplayMetrics().widthPixels;
	}

	private void reload() {
		mSwipeListView.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
		mSwipeListView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);

		mSwipeListView.setOffsetLeft(deviceWidth * 2 / 3);

		mSwipeListView.setAnimationTime(0);
		mSwipeListView.setSwipeOpenOnLongPress(false);
	}

	class TestBaseSwipeListViewListener extends BaseSwipeListViewListener {

		@Override
		public void onClickFrontView(int position) {
			super.onClickFrontView(position);

			int srt;
			srt = dataList.get(position).shopId;
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putInt("shopId", srt);
			intent.putExtras(bundle);
			/*
			 * Toast.makeText(getApplicationContext(), testData.get(position),
			 * Toast.LENGTH_SHORT).show();
			 */
			intent.setClass(getBaseContext(), PoiDetailActivity.class);
			startActivity(intent);

		}

		@Override
		public void onDismiss(int[] reverseSortedPositions) {
			for (int position : reverseSortedPositions) {
				dataList.remove(position);
			}
			mAdapter.notifyDataSetChanged();
		}

	}

	public void UserpicChange() {

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case RESULT_LOAD_IMAGE:
			if (resultCode == RESULT_OK && null != data) {
				mDrr = (List<String>) data.getSerializableExtra("pictureUrl");
				convertPicture();
				break;
			}
		}
	}

	private void convertPicture() {
		for (int i = 0; i < mDrr.size(); i++) {
			ShowPictureInfo info = new ShowPictureInfo();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			options.inJustDecodeBounds = false;
			int be = options.outHeight / 20;
			if (be <= 0) {
				be = 20;
			}
			options.inSampleSize = be;
			mBitmap = BitmapFactory.decodeFile(mDrr.get(i), options);
			Bitmap img = null;
			img = Bitmap.createScaledBitmap(mBitmap, 89, 89, true);
			mImage.setImageBitmap(img);

		}
	}

	private OnClickListener btnlistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			Button btn = (Button) v;
			switch (btn.getId()) {

			case R.id.logoff: {
				UserInfo.delete(context.getApplicationContext());
				finish();
				Toast.makeText(context, "注销成功", Toast.LENGTH_LONG).show();
				break;
			}
			default:
				break;

			}
		}
	};
	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ImageButton btn = (ImageButton) v;
			switch (btn.getId()) {
			case R.id.Userpic: {
				// Intent intent = new Intent(UsrInfoActivity.this,
				// 1ShowPictureActivity.class);
				// startActivityForResult(intent, RESULT_LOAD_IMAGE);
				break;
			}
			case R.id.passwordchange: {
				Intent intent2 = new Intent();
				intent2.setClass(UsrInfoActivity.this,
						PasswrodChangeActivity.class);

				startActivity(intent2);

				break;
			}
			case R.id.personalpageback: {
				// Intent intent3 = new Intent();
				// intent3.setClass(UsrInfoActivity.this, MainActivity.class);
				//
				// startActivity(intent3);
				finish();

				break;
			}

			case R.id.draft: {
				Intent intent2 = new Intent();
				intent2.setClass(UsrInfoActivity.this, DraftActivity.class);
				startActivity(intent2);
				onPause();
				break;
			}
			// case R.id.logoff:
			// {
			// //usr.delete(usr.userId,context);
			// UserInfo.delete(context.getApplicationContext());
			// finish();
			// break;
			// }
			default:
				break;
			}
		}

	};

}
