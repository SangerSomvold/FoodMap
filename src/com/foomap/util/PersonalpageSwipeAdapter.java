package com.foomap.util;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.baidu.mapapi.utils.d;
import com.foodmap.R;
import com.foomap.activity.PoiDetailActivity;
import com.foomap.model.CollectionData;
import com.foomap.model.ShopData;
import com.foomap.model.UserInfo;
import com.foomap.model.UserInfo.UserData;
import com.foomap.service.CollectionHttpService;
import com.foomap.service.HttpServiceHelper.IOnHttpRequeseListener;
import com.foomap.view.SwipeListView;

public class PersonalpageSwipeAdapter extends BaseAdapter {
	private Context context;
	private List<CollectionData> dataList;
	private SwipeListView mSwipeListView;
	private UserData usr;
	CollectionHttpService cHttpService;
	private int position_Item;

	public PersonalpageSwipeAdapter(Context context, SwipeListView view,
			List<CollectionData> dataList) {
		this.context = context;
		this.dataList = dataList;
		this.mSwipeListView = view;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (dataList == null) {
			return 0;
		}
		return dataList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	
	@Override
	public View getView(final int position, View container, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		usr = UserInfo.getUserData(context.getApplicationContext());
		position_Item = position;
		if (container == null) {
			holder = new ViewHolder();
			LayoutInflater lf = LayoutInflater.from(context);
			container = lf.inflate(R.layout.personalpagerow, null);
			holder.tv = (TextView) container
					.findViewById(R.id.example_row_tv_title);
			
			holder.del = (Button) container
					.findViewById(R.id.example_row_b_action_del);
			container.setTag(holder);
		} else {
			holder = (ViewHolder) container.getTag();
		}
		holder.tv.setText(dataList.get(position).shopName);
		
		holder.del.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				cHttpService=new CollectionHttpService(context.getApplicationContext());
				cHttpService.del(usr.userId, dataList.get(position).shopId, new IOnHttpRequeseListener() {
					
					@Override
					public void finished(String jsonRes) {
						// TODO Auto-generated method stub
						if(null==jsonRes)
					return;
					}
					
				});
				mSwipeListView.closeAnimate(position);
				mSwipeListView.dismiss(position);
			//	dataList.remove(position_Item);
				
				notifyDataSetChanged();
			}
		});

		return container;
	}

	private class ViewHolder {
		private TextView tv;
		private Button del;
	//	private int position;

	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*private LayoutInflater mInflater ;
	private List<String> objects ;
	private SwipeListView mSwipeListView ;
	private Context context;
	public PersonalpageSwipeAdapter(Context context, int textViewResourceId,List<String> objects,SwipeListView mSwipeListView) {
		super();
		this.context=context;
		this.objects = objects ;
		this.mSwipeListView = mSwipeListView ;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null ;
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.personalpagerow, parent, false);
			holder = new ViewHolder();
			holder.mFrontText = (TextView) convertView.findViewById(R.id.example_row_tv_title);
			holder.mBackEdit = (ImageButton) convertView.findViewById(R.id.example_row_b_action_2);
			//holder.mfront = (Button) convertView.findViewById(R.id.example_row_b_action_1);
			holder.mBackDelete = (ImageButton) convertView.findViewById(R.id.example_row_b_action_3);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mBackDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				mSwipeListView.closeAnimate(position);
				mSwipeListView.dismiss(position);
				
			}
			
		});
		holder.mBackEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSwipeListView.setSelection(position);
			    
			}
		});
//		holder.mfront.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				  //int str = objects.get(position);
//				    int srt= shopId[position];
//				  Intent intent=new Intent();
//				  Bundle bundle=new Bundle();
//				  bundle.putInt("shopId", str);
//				  intent.putExtras(bundle);
//				  intent.setClass(context, PoiDetailActivity.class);
//			    
//			}
//		});
		String item = getItem(position);
		holder.mFrontText.setText(item);
		   notifyDataSetChanged();
		return convertView;
	}
	class ViewHolder{
		TextView mFrontText ;
		ImageButton mBackEdit,mBackDelete;
		//Button mfront;
	}*/
}
