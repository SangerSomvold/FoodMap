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
import com.foomap.service.ShophttpService;
import com.foomap.util.SearchResultListAdapter;

public class SearchResultPopSortWindow extends PopupWindow {
	private View popupView;
	private String[] sort;

	public SearchResultPopSortWindow(Context parent,
			final SearchResultListAdapter myAdapter) {
		super(parent);

		LayoutInflater inflater = (LayoutInflater) parent
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		popupView = inflater.inflate(R.layout.sr_ll_sort, null);

		ListView lvSort = (ListView) popupView.findViewById(R.id.lvSrSortList);

		sort = new String[] { "价格最低", "价格最高", "评分最高" };

		ArrayAdapter<String> ListAdapter = new ArrayAdapter<String>(parent,
				R.layout.sr_tv_simple_item, sort);

		lvSort.setAdapter(ListAdapter);

		lvSort.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case 0:
					myAdapter.isPopupwindowUpdate = true;
					myAdapter.mSearchOrderType = ShophttpService.ASCENDING;
					myAdapter.mSearchOrderBy = ShophttpService.ORDERBY_PRICE;
					break;
				case 1:
					myAdapter.isPopupwindowUpdate = true;
					myAdapter.mSearchOrderType = ShophttpService.DESCENDING;
					myAdapter.mSearchOrderBy = ShophttpService.ORDERBY_PRICE;
					break;
				case 2:
					myAdapter.isPopupwindowUpdate = true;
					myAdapter.mSearchOrderType = ShophttpService.DESCENDING;
					myAdapter.mSearchOrderBy = ShophttpService.ORDERBY_GRADE;
					break;
				}
				dismiss();
			}
		});

		LinearLayout llSrSort = (LinearLayout) popupView
				.findViewById(R.id.llSrSort);
		llSrSort.setOnClickListener(new OnClickListener() {

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
		lvSort.setAnimation(AnimationUtils.loadAnimation(parent,
				R.anim.sr_anim_show_window));
		this.setAnimationStyle(R.style.popbg_anim_style);
	}
}