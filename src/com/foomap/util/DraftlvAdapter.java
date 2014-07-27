package com.foomap.util;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.foodmap.R;
import com.foomap.activity.CommentNewpositionActivity;
import com.foomap.activity.DraftActivity;
import com.foomap.model.UserInfo;
import com.foomap.model.UserInfo.UserData;
import com.foomap.view.SwipeListView;

public class DraftlvAdapter extends ArrayAdapter<String> {
	private LayoutInflater mInflater ;
	private List<String> objects ;
	private SwipeListView mSwipeListView ;
	private DraftsDbManager dbManager;
	private UserData userData;
	public DraftlvAdapter(Context context, int textViewResourceId,List<String> objects, SwipeListView mSwipeListView) {
		super(context, textViewResourceId, objects);
		this.objects = objects ;
		this.mSwipeListView = mSwipeListView ;
		dbManager = new DraftsDbManager(context);
		userData = UserInfo.getUserData(context);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null ;
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.draftlv_row, parent, false);
			holder = new ViewHolder();
			holder.mFrontText = (TextView) convertView.findViewById(R.id.example_row_tv_title);
		    holder.miteam=(Button) convertView.findViewById(R.id.example_row_b_action_1);
			holder.mBackDelete = (Button) convertView.findViewById(R.id.deletaction);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.mBackDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSwipeListView.closeAnimate(position);
				mSwipeListView.dismiss(position);
				dbManager.deleteInfo(userData.userId, objects.get(position));
			}
		});
		/*holder.miteam.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mListIntent =new Intent(DraftActivity.this,CommentNewpositionActivity.class);
			}
		});*/
		String item = getItem(position);
		holder.mFrontText.setText(item);
		return convertView;
	}
	class ViewHolder{
		TextView mFrontText ;
		Button mBackDelete,miteam;
	}
}