package com.foomap.util;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class CreatePoiItemActivityListAdapter extends BaseAdapter{

	List<CreatePoiItemActivityListViewItem> list;
	private Context context;
	public CreatePoiItemActivityListAdapter(Context context,List<CreatePoiItemActivityListViewItem> list)
	{
		this.context = context;
		this.list = list;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView==null){
			convertView=LayoutInflater.from(context).inflate(com.foodmap.R.layout.create_poi_item_activity_list_item_1, null);
		}
		CreatePoiItemActivityListViewItem item = list.get(position);
		ImageView image = (ImageView)convertView.findViewById(com.foodmap.R.id.createPoiItemActivityListItem_image);
		TextView name = (TextView)convertView.findViewById(com.foodmap.R.id.createPoiItemActivityListItem_name);
		//TextView comment = (TextView)convertView.findViewById(com.foodmap.R.id.createPoiItemActivityListItem_comment);
		//RatingBar rating = (RatingBar)convertView.findViewById(com.foodmap.R.id.createPoiItemActivityListItem_rating);
		//rating.setRating(item.rating);
		//TextView time = (TextView)convertView.findViewById(com.foodmap.R.id.createPoiItemActivityListItem_time);
		//TextView price_input= (TextView)convertView.findViewById(com.foodmap.R.id.createPoiItemActivityListItem_price_input);
		//price_input.setText(item.price_input);
		TextView name_input = (TextView)convertView.findViewById(com.foodmap.R.id.createPoiItemActivityListItem_name_input);
		name_input.setText(item.name_input);
		//TextView time_input = (TextView)convertView.findViewById(com.foodmap.R.id.createPoiItemActivityListItem_time_input);
		//time_input.setText(item.time_input);
		return convertView;
	}
}
