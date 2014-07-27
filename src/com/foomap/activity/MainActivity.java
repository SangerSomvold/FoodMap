package com.foomap.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.foodmap.R;
import com.foomap.model.InPutHistory;
import com.foomap.model.TypeData;
import com.foomap.model.UserInfo;
import com.foomap.service.HttpServiceHelper.IOnHttpRequeseListener;
import com.foomap.service.ShophttpService;
import com.foomap.util.ShopJsonUtils;
import com.foomap.view.KeywordsFlow;

public class MainActivity extends Activity implements OnFocusChangeListener,
		OnTouchListener, OnClickListener {
	private static String TAG = "MainActivity";

	// -----------------------控件-----------------------
	private Context context;
	private ImageButton usr, search, createPoi; // topPanel控件
	private AutoCompleteTextView autoText;
	private RelativeLayout searchPanel;
	private Button search_ok;
	// 类别
	private KeywordsFlow keywordsFlow;

	private InPutHistory inPutHistory;
	// AutoCompleteTextView
	private ArrayList<String> autoTextData;
	private ArrayAdapter<String> arrayAdapter;
	// 输入法
	private InputMethodManager inputManager;

	//
	private ArrayList<HashMap<String, Object>> searchResult = null;
	private float preX = 0, distance = 0;
	// topPanel״̬
	private final int state_searing = 0;
	private final int state_main = 1;
	private int state_topPanel = state_main;
	// keyWordView״̬
	private final int state_scroll = 0;
	private final int state_clickItem = 1;
	private final int state_doNothing = 2;
	private int state_keywordView = state_doNothing;
	// 透明度
	private float KeyWordalpha = -1f;
	// 搜索类型
	private final int SEARCH_CONTENT = 0;
	private final int SEARCH_TYPE = 1;
	//类型数据
	private ShophttpService shophttpService;
	private ArrayList<TypeData> dataList;
	private ArrayList<TypeData> showDataList;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		context = this;
		init();

	}

	protected void init() {
		usr = (ImageButton) findViewById(R.id.usr_img_main);
		search = (ImageButton) findViewById(R.id.search_img_main);
		createPoi = (ImageButton) findViewById(R.id.camera_img_main);

		autoText = (AutoCompleteTextView) findViewById(R.id.autoText_main);
		searchPanel = (RelativeLayout) findViewById(R.id.searchPanel_main);
		search_ok = (Button) findViewById(R.id.search_ok_main);
		inPutHistory = new InPutHistory(context);
		keywordsFlow = (KeywordsFlow) findViewById(R.id.KeywordsFlow_main);
		shophttpService=new ShophttpService(context);
		
		shophttpService.getTypeList(new IOnHttpRequeseListener() {
			
			@Override
			public void finished(String jsonRes) {
				// TODO Auto-generated method stub
				if(jsonRes==null)
				{
					return;
				}
				Log.i(TAG, jsonRes);
				dataList=ShopJsonUtils.getTypeList(jsonRes);
				bindKeyWordData(dataList, KeywordsFlow.ANIMATION_IN);
			}
		});

		bindEvent();
		bindAutoTextData();
	}

	protected void bindEvent() {
		usr.setOnClickListener(this);
		search.setOnClickListener(this);
		createPoi.setOnClickListener(this);

		search_ok.setOnTouchListener(this);
		keywordsFlow.setOnItemClickListener(this);
		keywordsFlow.setOnTouchListener(this);

		// autoTextView状态监听
		autoText.setOnFocusChangeListener(this);
	}

	protected void bindKeyWordData(ArrayList<TypeData> typeList,int animMethod) {
		
		if(showDataList==null)
		{
			showDataList=new ArrayList<TypeData>();
		}
		showDataList.clear();
			Random random = new Random();
			for (int i = 0; i < KeywordsFlow.MAX; i++) {
				int ran = random.nextInt(typeList.size());
				TypeData tmp = typeList.get(i);
				
				keywordsFlow.feedKeyword(tmp.typeName);
				
				showDataList.add(tmp);
			}
		keywordsFlow.go2Show(animMethod);
	}

	// AutoCompleteTextViews数据绑定
	protected void bindAutoTextData() {

		autoTextData = inPutHistory.getData();
		if (arrayAdapter == null) {
			arrayAdapter = new ArrayAdapter<String>(context,
					android.R.layout.simple_dropdown_item_1line, autoTextData);
			autoText.setAdapter(arrayAdapter);
		}
	}

	// autoText状态

	@Override
	public void onFocusChange(View arg0, boolean arg1) {
		// TODO Auto-generated method stub
		//
		if (arg1) {
			// 更该背景色

			if (KeyWordalpha == -1f) {
				KeyWordalpha = keywordsFlow.getAlpha();
			}
			keywordsFlow.setAlpha(0.3f);

			if (inputManager == null) {
				inputManager = (InputMethodManager) context
						.getSystemService(Context.INPUT_METHOD_SERVICE);
			}
			inputManager.showSoftInput(autoText, 0);
			autoText.showDropDown();
			state_topPanel = state_searing;
		}
		// 弹出输入法
		else {
			state_topPanel = state_main;
			keywordsFlow.setAlpha(KeyWordalpha);
			if (inputManager != null) {
				inputManager.hideSoftInputFromWindow(autoText.getWindowToken(),
						0);
			}
		}

	}

	// 跳转界面
	public void jumpToSearchResult(int type, String text,int TypeId) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("text", text);
		bundle.putInt("type", type);
		bundle.putInt("typeId", TypeId);
		intent.putExtras(bundle);
		intent.setClass(context, SearchResultActivity.class);
		startActivity(intent);

	}

	// touch事件分发
	@Override
	public boolean onTouch(View v, MotionEvent event) {

		// TODO Auto-generated method stub

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 在操作AutoTextView
			if (state_topPanel == state_searing) {
				state_keywordView = state_doNothing;
				setSearchPanelVisable(false);
				// 更新AutoTextView数据
				updateAtuoTextData(autoText.getText().toString());
				if (v == search_ok) {
					jumpToSearchResult(SEARCH_CONTENT, autoText.getText()
							.toString(),-1);
				}
				return true;
			}
			// kewwordItem事件处理
			if (v instanceof TextView) {
				state_keywordView = state_clickItem;
				// Item点击事件
				TextView tmp = (TextView) v;
				String text=tmp.getText().toString();
				//类别id
				int typeId=-1;
				for(TypeData data:showDataList)
				{
					if(text.equals(data.typeName))
					{
						typeId=data.typeId;
					}
				}
				Log.i(TAG, typeId+"");

				jumpToSearchResult(SEARCH_TYPE, text,typeId);
				return true;
			}
			state_keywordView = state_scroll;

			break;
		case MotionEvent.ACTION_MOVE:
			if (state_keywordView == state_scroll) {
				float currentX = event.getX();
				distance = currentX - preX;
				if(dataList!=null)
				{
					if (distance > 50) {

						bindKeyWordData(dataList,keywordsFlow.ANIMATION_IN);
						return true;
					} else if (distance < -20) {
						bindKeyWordData(dataList,keywordsFlow.ANIMATION_OUT);
						return true;
					}
				}
			
			}

			break;
		case MotionEvent.ACTION_UP:
			distance = 0;
			break;
		}

		return true;
	}

	// 搜索栏是否可见
	protected void setSearchPanelVisable(boolean visable) {
		if (visable == false) {
			searchPanel.setVisibility(View.INVISIBLE);
			state_topPanel = state_main;
		} else {
			searchPanel.setVisibility(View.VISIBLE);
			state_topPanel = state_searing;
		}

		searchPanel.setClickable(visable);
	}

	// 更新autotextView数据
	protected void updateAtuoTextData(String data) {
		if (data != null && !data.isEmpty()) {
			inPutHistory.putdata(data);

			arrayAdapter.add(data);
		}

	}

	// 跳转界面
	protected void startToActovity(Context context, Class<?> cls) {
		Intent intent = new Intent();
		intent.setClass(context, cls);
		context.startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.usr_img_main:
			if (UserInfo.getUserData(context.getApplicationContext()) != null) {
				startToActovity(context, UsrInfoActivity.class);
			} else {
				startToActovity(context, LoginActivity.class);
			}
			break;
		case R.id.search_img_main:
			setSearchPanelVisable(true);
			break;
		case R.id.camera_img_main:
			Intent commentIntent = new Intent(MainActivity.this,CommentNewpositionActivity.class);
			commentIntent.putExtra("id", 0);
			startActivity(commentIntent);
			break;
		}
	}
	
	

}
