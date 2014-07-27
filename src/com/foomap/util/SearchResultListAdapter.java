package com.foomap.util;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.foodmap.R;
import com.foomap.model.ShopData;
import com.foomap.service.ShophttpService;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class SearchResultListAdapter extends BaseAdapter {

	public ArrayList<ShopData> mShopList;
	private Context mContext;
	public int mPagerNum, mSearchOrderType, mSearchOrderBy;
	public String mKeyword;
	public int mDataType, mSearchType;
	public boolean isPopupwindowUpdate;

	public SearchResultListAdapter(Context context) {
		this.mContext = context;
		isPopupwindowUpdate = false;
		mShopList = new ArrayList<ShopData>();
		mPagerNum = 1;
		mSearchOrderType = ShophttpService.ASCENDING;
		mSearchOrderBy = ShophttpService.ORDERBY_PRICE;
	}

	@Override
	public int getCount() {
		return mShopList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final Holder holder;

		if (null == convertView) {
			holder = new Holder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.sr_ll_main_item, null);
			holder.title = (TextView) convertView
					.findViewById(R.id.tvSrMainItemTitle);
			holder.price = (TextView) convertView
					.findViewById(R.id.tvSrMainItemPrice);
			holder.locat = (TextView) convertView
					.findViewById(R.id.tvSrMainItemLocation);
			holder.foodPic = (ImageView) convertView
					.findViewById(R.id.ivSrMainItemImage);
			holder.score = (RatingBar) convertView
					.findViewById(R.id.rbSrMainItemScore);
			holder.feature = (TextView) convertView
					.findViewById(R.id.tvSrMainItemFeature);
			holder.picDownload = (ProgressBar) convertView
					.findViewById(R.id.pbSrMainItemImage);

			holder.foodPic.setTag(mShopList.get(position).iconUrl);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.title.setText(mShopList.get(position).name);
		holder.price.setText("¥" + mShopList.get(position).cost_avg);
		holder.locat.setText(mShopList.get(position).address);
		holder.score.setRating((float) mShopList.get(position).grade_avg);
		holder.feature.setText(mShopList.get(position).feature);

		ImageManager.Load(mShopList.get(position).iconUrl, holder.foodPic,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						// TODO Auto-generated method stub
						// String message = null;
						// switch (failReason.getType()) {
						// case IO_ERROR:
						// message = "Input/Output error";
						// break;
						// case DECODING_ERROR:
						// message = "Image can't be decoded";
						// break;
						// case NETWORK_DENIED:
						// message = "Downloads are denied";
						// break;
						// case OUT_OF_MEMORY:
						// message = "Out Of Memory error";
						// break;
						// case UNKNOWN:
						// message = "Unknown error";
						// break;
						// }
						// Toast.makeText(mContext, message, Toast.LENGTH_SHORT)
						// .show();
						holder.picDownload.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap arg2) {
						holder.picDownload.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						// TODO Auto-generated method stub
						holder.picDownload.setVisibility(View.GONE);
					}
				});

		return convertView;
	}

	private class Holder {
		public TextView title;
		public TextView locat;
		public TextView price;
		public TextView feature;
		public RatingBar score;
		public ImageView foodPic;
		public ProgressBar picDownload;
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		int inSampleSize = 1;
		if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
			final int heightRatio = Math.round((float) options.outHeight
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) options.outWidth
					/ (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		options.inSampleSize = inSampleSize;
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}
}