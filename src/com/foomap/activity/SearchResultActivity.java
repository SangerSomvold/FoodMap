package com.foomap.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.foodmap.R;
import com.foomap.model.ShopData;
import com.foomap.model.TypeData;
import com.foomap.pulltorefresh.library.PullToRefreshBase;
import com.foomap.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.foomap.pulltorefresh.library.PullToRefreshListView;
import com.foomap.service.HttpServiceHelper.IOnHttpRequeseListener;
import com.foomap.service.LocationSever;
import com.foomap.service.ShophttpService;
import com.foomap.service.ShophttpService.ISearchOption;
import com.foomap.util.CheckNetworkConnected;
import com.foomap.util.SearchResultListAdapter;
import com.foomap.util.ShopJsonUtils;
import com.foomap.view.SearchResultPopClassifyWindow;
import com.foomap.view.SearchResultPopSortWindow;

public class SearchResultActivity extends Activity implements OnClickListener {

	private TextView mTvMyPlace, mTvSearchKeyword;
	private ImageButton mIbMap, mIbClassify, mIbRank, mIbBack;
	private SearchResultPopClassifyWindow mPwClassify;
	private SearchResultPopSortWindow mPwSort;
	private PullToRefreshListView mLvMain;
	private SearchResultListAdapter myAdapter;
	private String[] classify;
	private boolean isFirstConnected;
	private LocationSever locationServer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sr_rl_main);

		isFirstConnected = true;
		mLvMain = (PullToRefreshListView) findViewById(R.id.lvSrList);
		mLvMain.setMode(com.foomap.pulltorefresh.library.PullToRefreshBase.Mode.PULL_FROM_END);
		mTvMyPlace = (TextView) findViewById(R.id.tvSrMyPlace);
		mTvSearchKeyword = (TextView) findViewById(R.id.tvSrKeyword);
		mIbMap = (ImageButton) findViewById(R.id.ibSrMap);
		mIbMap.setClickable(false);
		mIbClassify = (ImageButton) findViewById(R.id.ibSrClassify);
		mIbClassify.setClickable(false);
		mIbRank = (ImageButton) findViewById(R.id.ibSrRank);
		mIbRank.setClickable(false);
		mIbBack = (ImageButton) findViewById(R.id.ibSrBack);

		mTvMyPlace.setText("正在获取我的位置...");
		myAdapter = new SearchResultListAdapter(SearchResultActivity.this);

		myAdapter.mKeyword = getIntent().getExtras().getString("text");
		myAdapter.mDataType = getIntent().getExtras().getInt("type");
		myAdapter.mSearchType = getIntent().getExtras().getInt("typeId");

		initListenner();

		if (update()) {
			mLvMain.setAdapter(myAdapter);
		}
	}

	private void initListenner() {
		mIbMap.setOnClickListener(this);
		mIbClassify.setOnClickListener(this);
		mIbRank.setOnClickListener(this);
		mIbBack.setOnClickListener(this);

		class GetDataTask extends AsyncTask<Void, Void, String[]> {
			@Override
			protected void onPostExecute(String[] result) {
				super.onPostExecute(result);
				update();
			}

			@Override
			protected String[] doInBackground(Void... arg0) {
				return null;
			}
		}
		mLvMain.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				new GetDataTask().execute();
			}
		});
		mLvMain.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent();

				Bundle bundle = new Bundle();
				bundle.putInt("shopId", myAdapter.mShopList.get(arg2 - 1).id);

				intent.putExtras(bundle);
				intent.setClass(SearchResultActivity.this,
						PoiDetailActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibSrMap:
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("shopData", myAdapter.mShopList);
			intent.putExtras(bundle);
			startActivity(intent.setClass(this, MapViewActivity.class));
			break;
		case R.id.tvSrMyPlace:
			break;
		case R.id.ibSrClassify:
			mPwClassify.showAtLocation(v, Gravity.TOP, 0, 0);
			break;
		case R.id.ibSrRank:
			mPwSort.showAtLocation(v, Gravity.TOP, 0, 0);
			break;
		case R.id.ibSrBack:
			finish();
			break;
		}
	}

	private void initPopupwindow() {
		mPwClassify = new SearchResultPopClassifyWindow(
				SearchResultActivity.this, myAdapter, classify);
		mPwSort = new SearchResultPopSortWindow(SearchResultActivity.this,
				myAdapter);

		mPwClassify.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				if (myAdapter.isPopupwindowUpdate) {
					findViewById(R.id.pbSrRefresh).setVisibility(View.VISIBLE);
					mLvMain.setMode(com.foomap.pulltorefresh.library.PullToRefreshBase.Mode.PULL_FROM_END);
					myAdapter.mShopList.clear();
					myAdapter.mDataType = 1;
					myAdapter.mPagerNum = 1;
					update();
					myAdapter.isPopupwindowUpdate = false;
				}
			}
		});
		mPwSort.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				if (myAdapter.isPopupwindowUpdate) {
					mLvMain.setMode(com.foomap.pulltorefresh.library.PullToRefreshBase.Mode.PULL_FROM_END);
					findViewById(R.id.pbSrRefresh).setVisibility(View.VISIBLE);
					myAdapter.mShopList.clear();
					myAdapter.mPagerNum = 1;
					update();
					myAdapter.isPopupwindowUpdate = false;
				}
			}
		});
	}

	private void firstConUpdate() {
		ShophttpService shs = new ShophttpService(this);
		shs.getTypeList(new IOnHttpRequeseListener() {

			@Override
			public void finished(String jsonRes) {
				ArrayList<TypeData> alTd = ShopJsonUtils.getTypeList(jsonRes);
				classify = new String[alTd.size()];
				for (int i = 0; i < classify.length; i++) {
					classify[i] = alTd.get(i).typeName;
				}

				initPopupwindow();

				switch (myAdapter.mDataType) {
				case 0:
					if (myAdapter.mKeyword.equals("")) {
						mTvSearchKeyword.setText("搜索全部");
					} else {
						mTvSearchKeyword.setText("搜索\"" + myAdapter.mKeyword
								+ "\"");
					}
					break;
				case 1:
					mTvSearchKeyword.setText("搜索\""
							+ classify[myAdapter.mSearchType - 1] + "\"");
					break;
				}

				mIbClassify.setClickable(true);
				mIbRank.setClickable(true);
				mIbMap.setClickable(true);
			}
		});

		locationServer = new LocationSever(getApplicationContext());
		locationServer.requestLocation(new BDLocationListener() {

			@Override
			public void onReceiveLocation(BDLocation arg0) {
				mTvMyPlace.setText(arg0.getAddrStr());
			}
		});
	}

	private boolean update() {
		if (CheckNetworkConnected.isNetworkConnected(this)) {
			if (isFirstConnected) {
				firstConUpdate();
			}

			ShophttpService shs2 = new ShophttpService(this);
			ISearchOption isOption = new ISearchOption() {

				@Override
				public int getPageNumber() {
					return myAdapter.mPagerNum;
				}

				@Override
				public int getOrderType() {
					return myAdapter.mSearchOrderType;
				}

				@Override
				public int getOrderBy() {
					return myAdapter.mSearchOrderBy;
				}
			};
			IOnHttpRequeseListener iorListener = new IOnHttpRequeseListener() {

				@Override
				public void finished(String jsonRes) {
					ArrayList<ShopData> srShopList = ShopJsonUtils
							.getDataList(jsonRes);
					myAdapter.mShopList.addAll(srShopList);

					mLvMain.onRefreshComplete();

					if (srShopList.size() == 0) {
						mLvMain.setMode(com.foomap.pulltorefresh.library.PullToRefreshBase.Mode.DISABLED);
						Toast.makeText(SearchResultActivity.this, "没有更多了噢亲",
								Toast.LENGTH_SHORT).show();
					}

					myAdapter.notifyDataSetChanged();
					if (1 == myAdapter.mPagerNum) {
						findViewById(R.id.pbSrRefresh).setVisibility(View.GONE);
					}
					myAdapter.mPagerNum++;
				}
			};

			switch (myAdapter.mDataType) {
			case 0:
				if (myAdapter.mKeyword.equals(""))
					shs2.getAllData(isOption, iorListener);
				else
					shs2.searchByName(myAdapter.mKeyword, isOption, iorListener);
				mTvSearchKeyword.setText(myAdapter.mKeyword);
				break;
			case 1:
				shs2.searchByType(myAdapter.mSearchType, isOption, iorListener);
				break;
			}
			return true;
		} else {
			Toast.makeText(SearchResultActivity.this, "请连接网络",
					Toast.LENGTH_SHORT).show();
			mLvMain.onRefreshComplete();
			return false;
		}
	}

}
