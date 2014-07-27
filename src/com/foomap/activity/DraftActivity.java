package com.foomap.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;














import com.foodmap.R;



import com.foomap.model.UserInfo;
import com.foomap.model.UserInfo.UserData;
import com.foomap.util.DraftlvAdapter;
import com.foomap.util.DraftsDbManager;
import com.foomap.util.DraftsInfo;
import com.foomap.view.BaseSwipeListViewListener;
import com.foomap.view.SwipeListView;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.os.Build;

public class DraftActivity extends Activity {
	private SwipeListView mSwipeListView;
	private DraftlvAdapter mAdapter;
	public static int deviceWidth;
	private List<String> testData = new ArrayList<String>();
	public Button backbtn;
	public Button dv;
	private UserData usr;
	private List<DraftsInfo> mDraftsInfo = new ArrayList<DraftsInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_draft);
		usr = UserInfo.getUserData(getApplicationContext());
	//	 getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title);
		mSwipeListView = (SwipeListView) findViewById(R.id.draft_lv_list);
		getTestData();
		mAdapter = new DraftlvAdapter(this, R.layout.draftlv_row, testData,
				mSwipeListView);
		deviceWidth = getDeviceWidth();
		if(testData != null)
		{
			mSwipeListView.setAdapter(mAdapter);
			mSwipeListView.setSwipeListViewListener(new TestBaseSwipeListViewListener());
			reload();
		}
	//	dv=(Button)findViewById(R.id.example_row_b_action_1);
	//	dv.setOnClickListener(listener);
		backbtn = (Button) findViewById(R.id.draftback);
		backbtn.setOnClickListener(listener);

	}

	private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Button btn = (Button) v;
			switch (btn.getId()) {
			case R.id.draftback:
			 {
					Intent intent3 = new Intent();
					intent3.setClass(DraftActivity.this, UsrInfoActivity.class);

					startActivity(intent3);
					finish();
					break;
				}
default:
	break;
			
			
			
		}
	}
	};
		
		

	private void getTestData() {
				DraftsDbManager ddm= new DraftsDbManager(getBaseContext());
				Cursor cursor;
				cursor=ddm.queryTheCursor();
				while (cursor.moveToNext()) { 
				    String muserId = cursor.getString(0); //获取第一列的值,第一列的索引从0开始 
				    if(muserId.equals(getUsername()));
				    {
				    	DraftsInfo mInfo = new DraftsInfo();
				    	String mname = cursor.getString(cursor.getColumnIndex("name"));
				    	testData.add(mname);
				    	mInfo.userId = cursor.getString(cursor.getColumnIndex("userId"));
				    	mInfo.name = cursor.getString(cursor.getColumnIndex("name"));
				    	mInfo.rating = cursor.getFloat(cursor.getColumnIndex("rating"));
				    	mInfo.cost = cursor.getFloat(cursor.getColumnIndex("cost"));
				    	mInfo.feature = cursor.getString(cursor.getColumnIndex("feature"));
				    	mInfo.address = cursor.getString(cursor.getColumnIndex("address"));
				    	mInfo.picture = cursor.getString(cursor.getColumnIndex("picture"));
				    	mDraftsInfo.add(mInfo);
				    }
				} 
	}

	private int getDeviceWidth() {
		return getResources().getDisplayMetrics().widthPixels;
	}

	private void reload() {
		mSwipeListView.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
		mSwipeListView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);
		// mSwipeListView.setSwipeActionRight(settings.getSwipeActionRight());
		mSwipeListView.setOffsetLeft(deviceWidth * 1 / 2);
		// mSwipeListView.setOffsetRight(convertDpToPixel(settings.getSwipeOffsetRight()));
		mSwipeListView.setAnimationTime(0);
		mSwipeListView.setSwipeOpenOnLongPress(false);
	}
	private String getUsername()
	{
		String userID;
		UserData usr= new UserData();
		userID=usr.userId;
	  
		return userID;
		
		
	}
	class TestBaseSwipeListViewListener extends BaseSwipeListViewListener {

		@Override
		public void onClickFrontView(int position) {
			super.onClickFrontView(position);
			Intent mListIntent = new Intent(DraftActivity.this,CommentNewpositionActivity.class);
			mListIntent.putExtra("id",1);
			mListIntent.putExtra("userId", mDraftsInfo.get(position).userId);
			mListIntent.putExtra("name", mDraftsInfo.get(position).name);
			mListIntent.putExtra("rating", mDraftsInfo.get(position).rating);
			mListIntent.putExtra("cost", mDraftsInfo.get(position).cost);
			mListIntent.putExtra("feature", mDraftsInfo.get(position).feature);
			mListIntent.putExtra("address", mDraftsInfo.get(position).address);
			mListIntent.putExtra("picture", mDraftsInfo.get(position).picture);
			Toast.makeText(getApplicationContext(), testData.get(position),
					Toast.LENGTH_SHORT).show();
			startActivity(mListIntent);
			DraftActivity.this.finish();
		}

		@Override
		public void onDismiss(int[] reverseSortedPositions) {
			for (int position : reverseSortedPositions) {
				testData.remove(position);
			}
			mAdapter.notifyDataSetChanged();
		}
	}	
}



