package com.foomap.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.foodmap.R;

public class SearchResultPopLocationWindow extends PopupWindow {
	private View popupView;
	private String[] title;
	private String[] location;
	private int iLastChoose = 0;

	public SearchResultPopLocationWindow(Context parent) {
		super(parent);

		LayoutInflater inflater = (LayoutInflater) parent
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		popupView = inflater.inflate(R.layout.sr_ll_location, null);

		ListView lvLocationLeft = (ListView) popupView
				.findViewById(R.id.lvSrLocationListLeft);
		ListView lvLocationRight = (ListView) popupView
				.findViewById(R.id.lvSrLocationListRight);

		title = new String[] { "武侯区", "锦江区", "青羊区", "金牛区", "武侯区", "成华区",
				"龙泉驿区", "青白江区", "新都区", "温江区" };
		location = new String[] { "望江", "江安", "华西", "望江", "江安", "华西", "望江",
				"江安", "华西", "望江", "江安", "华西", "望江", "江安", "华西" };

		final MyOrArrrayAdapter leftListAdapter = new MyOrArrrayAdapter(parent);

		ArrayAdapter<String> rightListAdapter = new ArrayAdapter<String>(
				parent, R.layout.sr_tv_simple_item, location);
		lvLocationLeft.setAdapter(leftListAdapter);
		lvLocationRight.setAdapter(rightListAdapter);
		lvLocationLeft.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lvLocationLeft.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				iLastChoose = arg2;
				leftListAdapter.notifyDataSetChanged();
			}
		});

		LinearLayout llSrLocation = (LinearLayout) popupView
				.findViewById(R.id.llSrLocation);
		llSrLocation.setOnClickListener(new OnClickListener() {

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
		lvLocationLeft.setAnimation(AnimationUtils.loadAnimation(parent,
				R.anim.sr_anim_show_window));
		lvLocationRight.setAnimation(AnimationUtils.loadAnimation(parent,
				R.anim.sr_anim_show_window));
		this.setAnimationStyle(R.style.popbg_anim_style);

	}

	class MyOrArrrayAdapter extends BaseAdapter {

		private Context context;

		public MyOrArrrayAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return title.length;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			Holder holder;
			if (null == convertView) {
				holder = new Holder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.sr_tv_simple_item, null);
				holder.title = (TextView) convertView
						.findViewById(R.id.tvSrSimpleItem);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			holder.title.setText(title[position]);

			if (iLastChoose == position) {
				convertView.setBackgroundColor(0xFFDDDDDD);
			} else {
				convertView.setBackgroundColor(Color.WHITE);
			}
			return convertView;
		}

		class Holder {
			public TextView title;
		}
	}
}
