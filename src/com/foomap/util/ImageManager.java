package com.foomap.util;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.foodmap.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ImageManager {

	public static DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(null).showImageForEmptyUri(R.drawable.dicon)
			.showImageOnFail(R.drawable.image_miss).cacheInMemory(true)
			.cacheOnDisk(true).considerExifParams(true)
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
			.bitmapConfig(Bitmap.Config.RGB_565).resetViewBeforeLoading(true)
			.displayer(new FadeInBitmapDisplayer(100))
			.displayer(new RoundedBitmapDisplayer(20)).build();

	public static void Load(String imgUrl, ImageView imageView,
			ImageLoadingListener ill) {
		ImageLoader.getInstance().displayImage(imgUrl, imageView, options, ill);
	}

	public static void Load(String imgUrl, ImageView imageView,
			DisplayImageOptions options) {
		ImageLoader.getInstance().displayImage(imgUrl, imageView, options);
	}
}
