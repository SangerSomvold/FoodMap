package com.foomap.util;

import java.util.ArrayList;
import java.util.List;
import com.foodmap.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ShowPictureActivityGridAdapter extends BaseAdapter {

    private Context mContext;
	private List<ShowPictureInfo> mInfo = new ArrayList<ShowPictureInfo>();

    public ShowPictureActivityGridAdapter(Context ctx,List<ShowPictureInfo> mInfo) {
        mContext = ctx;
        this.mInfo = mInfo;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
    	if(mInfo.size()<0) return 0;
    	else return mInfo.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
    	ShowPictureInfo info = mInfo.get(position);
        if (convertView == null) {
        	convertView = LayoutInflater.from(mContext).inflate(
					R.layout.show_picture_activity_grid_item, null);
        }
        ImageView image = (ImageView)convertView.findViewById(R.id.showPictureActivityGridItem_image);
        ImageView select = (ImageView)convertView.findViewById(R.id.showPictureActivityGridItem_select);
        image.setImageBitmap(info.getBitmap());
        if(position == 0)
        {
        	image.setImageResource(R.drawable.show_picture_activity_photo);
        	select.setVisibility(View.GONE);
        }
        else if(info.getStatus())
        {	
        	select.setImageDrawable(mContext.getResources().getDrawable(R.drawable.show_picture_activity_choised));
        }
        else
        {
        	select.setImageDrawable(mContext.getResources().getDrawable(R.drawable.show_picture_activity_unchoised));
        }
        return convertView;
    }
}


