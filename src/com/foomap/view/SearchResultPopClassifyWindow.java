package com.foomap.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.foodmap.R;
import com.foomap.util.SearchResultListAdapter;

public class SearchResultPopClassifyWindow extends PopupWindow {
	private View popupView;

	public SearchResultPopClassifyWindow(Context parent,
			final SearchResultListAdapter myAdapter, String[] classify) {
		super(parent);

		LayoutInflater inflater = (LayoutInflater) parent
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		popupView = inflater.inflate(R.layout.sr_ll_classify, null);

		ListView lvClassify = (ListView) popupView
				.findViewById(R.id.lvSrClassifyList);

		ArrayAdapter<String> ListAdapter = new ArrayAdapter<String>(parent,
				R.layout.sr_tv_simple_item, classify);

		lvClassify.setAdapter(ListAdapter);
		lvClassify.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				myAdapter.isPopupwindowUpdate = true;
				myAdapter.mDataType = 1;
				myAdapter.mSearchType = arg2 + 1;
				dismiss();
			}
		});

		LinearLayout llSrClassify = (LinearLayout) popupView
				.findViewById(R.id.llSrClassify);
		llSrClassify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dismiss();
			}
		});

		this.setContentView(popupView);
		this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
		this.setFocusable(true);
		this.setTouchable(true);
		this.setOutsideTouchable(true);
		this.setBackgroundDrawable(new ColorDrawable(0x00FFFFFF));
		lvClassify.setAnimation(AnimationUtils.loadAnimation(parent,
				R.anim.sr_anim_show_window));
		this.setAnimationStyle(R.style.popbg_anim_style);
	}
}