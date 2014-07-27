package com.foomap.util;



import java.util.ArrayList;
import java.util.List;

import com.foodmap.R;
import com.foomap.activity.ShowPictureActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

public class CommentActivityGridAdapter extends BaseAdapter {
	private int selectedPosition = -1;
	private boolean shape;
	Context context;
	private List<ShowPictureInfo> mInfo = new ArrayList<ShowPictureInfo>();
	
	public boolean isShape() {
		return shape;
	}

	public void setShape(boolean shape) {
		this.shape = shape;
	}

	public class ViewHolder {
		public ImageView image;
		public Button bt;
	}

	public CommentActivityGridAdapter(Context context,List<ShowPictureInfo> mInfo) {
		this.context = context;
		this.mInfo = mInfo;
	}

	public int getCount() {
		if (mInfo.size() < 6) {
			return mInfo.size() + 1;
		} else {
			return mInfo.size();
		}
	}

	public Object getItem(int arg0) {

		return mInfo.get(arg0);
	}

	public long getItemId(int arg0) {

		return 0;
	}

	public void setSelectedPosition(int position) {
		selectedPosition = position;
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}

	/**
	 * ListView Item设置
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		final int sign = position;
		// 自定义视图
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			// 获取list_item布局文件的视图

			convertView = LayoutInflater.from(context).inflate(
					R.layout.create_poi_item_activity_grid_item_1, null);

			// 获取控件对象
			holder.image = (ImageView) convertView
					.findViewById(R.id.createPoiItemActicityGridItem1_image);
			holder.bt = (Button) convertView
					.findViewById(R.id.createPoiItemActicityGridItem1_button);
			// 设置控件集到convertView
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position == mInfo.size()) {
			holder.image.setImageBitmap(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.create_poi_item_activity_addphoto));
			holder.bt.setVisibility(View.GONE);
		} 
		else {
			holder.image.setImageBitmap(mInfo.get(position).getBitmap());
			holder.bt.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					//ShowPictureActivity.bitmap.remove(sign);
					mInfo.get(sign).getBitmap().recycle();
					mInfo.remove(sign);
					notifyDataSetChanged();
				}
			});
		}
		return convertView;
	}
}