package com.foomap.util;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class CreatePoiItemActivityDataList {

	public static List<CreatePoiItemActivityListViewItem> getData(Context context)
	{
		List<CreatePoiItemActivityListViewItem> dataList = new ArrayList<CreatePoiItemActivityListViewItem>();
		CreatePoiItemActivityListViewItem item = new CreatePoiItemActivityListViewItem();
		item.name_input = "first";
		dataList.add(item);
		item = new CreatePoiItemActivityListViewItem();
		item.name_input = "second";
		dataList.add(item);
		item = new CreatePoiItemActivityListViewItem();
		item.name_input = "first";
		item = new CreatePoiItemActivityListViewItem();
		item.name_input = "second";
		dataList.add(item);
		item = new CreatePoiItemActivityListViewItem();
		item.name_input = "first";
		dataList.add(item);
		item = new CreatePoiItemActivityListViewItem();
		item.name_input = "second";
		dataList.add(item);
		item = new CreatePoiItemActivityListViewItem();
		item.name_input = "first";
		dataList.add(item);
		item = new CreatePoiItemActivityListViewItem();
		item.name_input = "second";
		dataList.add(item);
		item = new CreatePoiItemActivityListViewItem();
		item.name_input = "first";
		dataList.add(item);
		item = new CreatePoiItemActivityListViewItem();
		item.name_input = "second";
		dataList.add(item);
		item = new CreatePoiItemActivityListViewItem();
		item.name_input = "first";
		dataList.add(item);
		item = new CreatePoiItemActivityListViewItem();
		item.name_input = "second";
		dataList.add(item);
		return dataList;
	}
}
