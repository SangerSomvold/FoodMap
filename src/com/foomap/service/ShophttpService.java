package com.foomap.service;

/*
 * 必须实现     IOnHttpRequeseListener接口 接受返回值  否则不执行异步请求
 * 
 * addComment 方法必须实现 IShopDataAdapter接口  提供数据 否则不执行异步请求
 */

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.foomap.util.CompressImg;
import com.foomap.util.CompressImg.CompressImgConfig;

public class ShophttpService extends HttpServiceHelper {
	private static String TAG = "ShophttpService";
	// URL
	private static final String BASE_URI = "http://10.252.252.31:8080/foodmap/shop/";
	// 分页获得数据
	private static final String DATA_ALL = BASE_URI + "data_all";
	// id搜索
	private static final String DATA_BYID = BASE_URI + "data_byId";
	// 类型搜索
	private static final String DATA_BYTYPE = BASE_URI + "data_byType";
	// 名称搜索
	private static final String DATA_BYKEY = BASE_URI + "data_byKey";
	// 类型
	private static final String DATA_TYPE = "http://10.252.252.31:8080/foodmap/type/type_get";
	// 添加大图片
	private static final String DATA_UP_IMG = BASE_URI + "upImg";

	// 升序 降序
	public static int ASCENDING = 0, DESCENDING = 1;
	// 排序根据
	public final static int ORDERBY_GRADE = 0, ORDERBY_PRICE = 1;

	// 添加
	private static final String DATA_ADD = BASE_URI + "data_add";
	//
	private IShopDataAdapter shopDataAdapter = null;
	private CompressImg.CompressImgConfig compressConfig;
	private CompressImg comImg;

	public ShophttpService(Context context) {
		super(context);
		comImg = new CompressImg();
		compressConfig = comImg.new CompressImgConfig();
		compressConfig.setCompressRatio(10);
		compressConfig.setDirPathTo(Environment.getExternalStorageDirectory()
				+ "/foodmap/cache");
		compressConfig.setRequestHeight(320);
		compressConfig.setRequestWide(400);
		comImg.setConfig(compressConfig);

	}

	// 关键字搜索
	public void searchByName(String key, ISearchOption option,
			IOnHttpRequeseListener listener) {
		if (listener != null) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("shopName", key);
			map.put("pageNumber", option.getPageNumber() + "");
			map.put("orderBy", option.getOrderBy() + "");
			map.put("orderType", option.getOrderType() + "");
			setRequestListener(listener);
			get_Asyn(DATA_BYKEY, map);

		}
	}

	// 类型搜索
	public void searchByType(int typeId, ISearchOption option,
			IOnHttpRequeseListener listener) {
		if (listener != null) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("typeId", typeId + "");
			map.put("pageNumber", option.getPageNumber() + "");
			map.put("orderBy", option.getOrderBy() + "");
			map.put("orderType", option.getOrderType() + "");
			setRequestListener(listener);
			get_Asyn(DATA_BYTYPE, map);

		}
	}

	// 分页获取所有数据
	// 类型搜索
	public void getAllData(ISearchOption option, IOnHttpRequeseListener listener) {
		if (listener != null) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("pageNumber", option.getPageNumber() + "");
			map.put("orderBy", option.getOrderBy() + "");
			map.put("orderType", option.getOrderType() + "");
			setRequestListener(listener);
			get_Asyn(DATA_ALL, map);

		}
	}

	// 根据id搜索
	public void searchById(int id, IOnHttpRequeseListener listener) {
		if (listener != null) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("shopId", id + "");
			setRequestListener(listener);
			get_Asyn(DATA_BYID, map);

		}
	}

	// 商户数据
	public void addShop(IShopDataAdapter adapter) {
		if (adapter != null) {
			shopDataAdapter = adapter;
			post_Asyn();
		}

	}

	/*
	 * //上传图片 public void upImage(IUpLoadAdapter adapter) { if (adapter != null)
	 * { post_Asyn(DATA_ADD, adapter); } }
	 */
	@Override
	protected String postMethod() {
		// TODO Auto-generated method stub
		String res = "参数出错";
		MultipartEntity postEntity = getEntity();
		if (postEntity != null) {
			try {
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(DATA_ADD);
				post.setEntity(postEntity);
				HttpResponse response = client.execute(post);
				res = EntityUtils.toString(response.getEntity(), encoding);
				Log.i(TAG, res);

				// 关闭资源
				client.getConnectionManager().shutdown();
				//上传图片
				upImgFile();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return res;

	}

	// 组装post数据
	protected MultipartEntity getEntity() {
		MultipartEntity postEntity = new MultipartEntity();

		try {
			postEntity.addPart(
					"address",
					new StringBody(shopDataAdapter.getAddress(), Charset
							.forName(encoding)));
			postEntity.addPart(
					"costAvg",
					new StringBody(shopDataAdapter.getCost_avg() + "", Charset
							.forName(encoding)));
			postEntity.addPart(
					"feature",
					new StringBody(shopDataAdapter.getFeature(), Charset
							.forName(encoding)));
			postEntity.addPart(
					"gradeAvg",
					new StringBody(shopDataAdapter.getGrade_avg() + "", Charset
							.forName(encoding)));
			postEntity.addPart(
					"lat",
					new StringBody(shopDataAdapter.getLatitude() + "", Charset
							.forName(encoding)));

			postEntity.addPart(
					"lng",
					new StringBody(shopDataAdapter.getLongitude() + "", Charset
							.forName(encoding)));
		
			postEntity.addPart(
					"shopName",
					new StringBody(shopDataAdapter.getName(), Charset
							.forName(encoding)));
			postEntity.addPart("userId",
					new StringBody(shopDataAdapter.getCreateUserId() + "",
							Charset.forName(encoding)));
			postEntity.addPart("typeId",
					new StringBody(shopDataAdapter.getTypeId() + "",
							Charset.forName(encoding)));

			// 图片资源
			// icon

			if (shopDataAdapter.getIconPath() != null) {
				
				compressConfig.setFilePathFrom(shopDataAdapter.getIconPath());
				FileBody f = new FileBody(comImg.getFile());
				postEntity.addPart("iconData", f);
			}


		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return postEntity;
	}

	// 上传图片文件
	private void upImgFile() {
		List<String> imgPathList = shopDataAdapter.getPicsPath();
		if (imgPathList != null) {

			for (int i=0;i<imgPathList.size();i++) {
				

				try {
					String path=imgPathList.get(i);
					compressConfig.setFilePathFrom(path);
					HttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost(DATA_UP_IMG);
					MultipartEntity entity = new MultipartEntity();
					// 设置信息
					entity.addPart(
							"shopName",
							new StringBody(shopDataAdapter.getName(), Charset
									.forName(encoding)));
					entity.addPart("createDate",
							new StringBody(shopDataAdapter.getCreateDate(),
									Charset.forName(encoding)));
					entity.addPart("userId",
							new StringBody(shopDataAdapter.getCreateUserId(),
									Charset.forName(encoding)));
			
					entity.addPart("imgData", new FileBody(comImg.getFile()));
					post.setEntity(entity);

				   HttpResponse response= client.execute(post);
				   String res=EntityUtils.toString(response.getEntity(),encoding);
				   //关闭资源
				   client.getConnectionManager().shutdown();
				   Log.i(TAG, "upFile---->"+res);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.i(TAG, "UnsupportedEncodingException");
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					Log.i(TAG, "ClientProtocolException");
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.i(TAG, "IOException");
					e.printStackTrace();
				} 
			}
		}
	}

	// 获得所有商铺类型
	public void getTypeList(IOnHttpRequeseListener listener) {
		setRequestListener(listener);
		get_Asyn(DATA_TYPE, null);

	}

	// 搜索条件限制接口
	public interface ISearchOption {
		public int getPageNumber();

		public int getOrderBy();

		public int getOrderType();
	}

	// 商户接口
	public interface IShopDataAdapter {
		public String getCreateUserId();

		public String getName();

		public String getIconPath();

		public double getLatitude();

		public double getLongitude();

		public String getAddress();

		public String getFeature();

		public double getGrade_avg();

		public double getCost_avg();

		public int getTypeId();

		public String getCreateDate();

		public List<String> getPicsPath();

	}

}
