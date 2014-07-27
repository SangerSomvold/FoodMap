package com.foomap.service;

/*
 * 必须实现     IOnHttpRequeseListener接口 接受返回值  否则不执行异步请求
 * 
 * addComment 方法必须实现 ICommentAdapter接口  提供数据 否则不执行异步请求
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

import com.foomap.util.CompressImg;
import com.foomap.util.CompressImg.CompressImgConfig;

import android.content.Context;
import android.os.Environment;
import android.util.Log;


public class CommentHttpService extends HttpServiceHelper {
	private static String TAG = "CommentHttpService";

	protected static final String BaseURL = "http://10.252.252.31:8080/foodmap/comment/";
	protected static final String ACTION_ADD = BaseURL + "comment_add";
	protected static final String ACTION_GET_BY_ID = BaseURL + "comment_byId";
	protected static final String ACTION_UPIMG = BaseURL + "comment_up";

	// 上传的评论数据
	private ICommentAdapter adapter = null;
	//图片设置
	private CompressImg.CompressImgConfig compressConfig;
	private CompressImg comImg;
	
	
	public CommentHttpService(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		comImg=new CompressImg();
		compressConfig=comImg.new CompressImgConfig();

		compressConfig.setCompressRatio(10);
		compressConfig.setDirPathTo(Environment.getExternalStorageDirectory()
				+ "/foodmap/cache");
		compressConfig.setRequestHeight(320);
		compressConfig.setRequestWide(400);
		comImg.setConfig(compressConfig);
	
	}

	// 获得评论数据
	public void getComments(int shopId, int pageNumber,
			IOnHttpRequeseListener listener) {
		setRequestListener(listener);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("shopId", shopId + "");
		map.put("pageNumber", pageNumber + "");
		get_Asyn(ACTION_GET_BY_ID, map);

	}

	// 添加评论
	public void addComment(ICommentAdapter adapter) {
		this.adapter = adapter;
		if (adapter != null && adapter.getUserId() != null
				&& adapter.getShopId() != 0) {
			post_Asyn();

		}
	}

	// ---------post实现

	@Override
	protected String postMethod() {
		// TODO Auto-generated method stub
		String res = null;

		try {
			MultipartEntity postEntity = getPostEntity();
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(ACTION_ADD);
			post.setEntity(postEntity);
			HttpResponse response = client.execute(post);
			res = EntityUtils.toString(response.getEntity(), encoding);
			Log.i(TAG,   " -------> res    "+res);
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
		return res;
	}
	
	
	//发送图片吗
	private void  upImgFile()
	{
		String resString=null;
			List<String> pathList=adapter.getPicPaths();
			if(pathList==null)
			{
				return;
			}
			try {
			for(int i=0;i<pathList.size();i++)
			{
				String path=pathList.get(i);
				//上传图片路径 压缩
				
                compressConfig.setFilePathFrom(path);
				MultipartEntity imgEntity=new MultipartEntity();
				HttpClient client=new DefaultHttpClient();
				HttpPost postRequest=new HttpPost(ACTION_UPIMG);
				
				imgEntity.addPart("datetime", new StringBody(adapter.getTime(),
							Charset.forName(encoding)));
				
				imgEntity.addPart("shopId", new StringBody(adapter.getShopId() + "",
						Charset.forName(encoding)));
				imgEntity.addPart("userId", new StringBody(adapter.getUserId() + "",
						Charset.forName(encoding)));
				imgEntity.addPart("imgData",new FileBody(comImg.getFile()));
				postRequest.setEntity(imgEntity);
				HttpResponse response=client.execute(postRequest);
				resString=EntityUtils.toString(response.getEntity(),encoding);
				//关闭资源
				client.getConnectionManager().shutdown();
				
	       	Log.i(TAG, resString);
			}
				
			}catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i(TAG, "UnsupportedEncodingException");
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i(TAG, "ClientProtocolException");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i(TAG, "IOException");
			}
		

	}

	// post文字信息组装
	private MultipartEntity getPostEntity() {
		MultipartEntity entity = new MultipartEntity();

		try {

			entity.addPart("comment", new StringBody(adapter.getComment(),
					Charset.forName(encoding)));
			entity.addPart("shopId", new StringBody(adapter.getShopId() + "",
					Charset.forName(encoding)));
			entity.addPart("grade", new StringBody(adapter.getGradeAvg() + "",
					Charset.forName(encoding)));
			entity.addPart("datetime", new StringBody(adapter.getTime(),
					Charset.forName(encoding)));
			entity.addPart("userId", new StringBody(adapter.getUserId() + "",
					Charset.forName(encoding)));
			entity.addPart("cost", new StringBody(adapter.getCostAvg() + "",
					Charset.forName(encoding)));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i(TAG, "UnsupportedEncodingException");
		}

		return entity;
	}

	// 提供数据数据
	public interface ICommentAdapter {
		public String getUserId();

		public int getShopId();

		public String getTime();

		public String getComment();

		public Integer getCostAvg();

		public double getGradeAvg();

		public List<String> getPicPaths();
	}

}
