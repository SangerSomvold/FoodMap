package com.foomap.model;

import android.app.Application;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class FoodMapApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext()).denyCacheImageMultipleSizesInMemory()
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheSize(50 * 1024 * 1024).threadPoolSize(2)
				.threadPriority(Thread.MIN_PRIORITY + 3)
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
	}
}
