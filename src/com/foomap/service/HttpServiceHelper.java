package com.foomap.service;



import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import com.foomap.util.CheckNetworkConnected;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public abstract class HttpServiceHelper {
	private static String TAG = "HttpServiceHelper";

	private IOnHttpRequeseListener requeseListener = null;
	// 编码格式
	protected String encoding = null;
	// 请求类型
	protected final int METHOD_POSET = 0;
	protected final int METHOD_GET = 1;
	protected int METHOD_TYPE = -1;
	protected Handler handler;
	//异步结果
	private final int ASYN_FINISH=0x10;
	private String AsynResult=null;
	//get 方法参数
	private String url_get;
	private HashMap<String, String> map;
	private Context context;

	public HttpServiceHelper(Context context) {
		this.context=context;
		encoding = "utf-8";
	
		handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what==ASYN_FINISH)
				{
					if(requeseListener!=null)
					{
						requeseListener.finished(AsynResult);
					}
				
				}
			}
			
		};
	}
	
	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setRequestListener(IOnHttpRequeseListener arg1) {
		this.requeseListener = arg1;
	}
	
	
	
	public void doAsyn()
	{
		
		if(!CheckNetworkConnected.isNetworkConnected(context))
		{
			Toast.makeText(context, "网络连接已关闭，请打开连接", Toast.LENGTH_SHORT).show();
			if(requeseListener!=null)
			{
				requeseListener.finished(null);
			}
			return;
		}
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (METHOD_TYPE == METHOD_GET) {	
					AsynResult = getMethod();
				} else if (METHOD_TYPE == METHOD_POSET) {
					AsynResult = postMethod();
				}
				Message msg=new Message();
				msg.what=ASYN_FINISH;
				handler.sendMessage(msg);
			}
			
		}).start();
	
	}
	
	// post 异步入口
	public void post_Asyn() {
		METHOD_TYPE = METHOD_POSET;
	   doAsyn();

	}

	// get异步入口
	public boolean get_Asyn(String url, HashMap<String, String> map) {
		if (requeseListener != null) {
			METHOD_TYPE = METHOD_GET;
			this.url_get=url;
			this.map=map;	
			doAsyn();
			return true;
		}
		return false;
	}

	// get 参数组装
	protected ArrayList<NameValuePair> getNameValuePairs(
			HashMap<String, String> map) {	
		ArrayList<NameValuePair> paramsList = null;
		if (map != null) {
			paramsList = new ArrayList<NameValuePair>();
			Iterator iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				NameValuePair nameValuePair = new BasicNameValuePair(
						(String) entry.getKey(), (String) entry.getValue());
				paramsList.add(nameValuePair);
			}
		}
		return paramsList;

	}



	// post 实现
	protected  abstract String postMethod();

	// get实现
	protected String getMethod() {
		String res = null;

		try {
			HttpClient httpClient = new DefaultHttpClient();
			if(map!=null)
			{
				
			}
			ArrayList<NameValuePair> nvp = getNameValuePairs(map);
			// 参数
			String params="";
			if (nvp != null) {
				params = URLEncodedUtils.format(nvp, encoding);
			} else {
				params = "";
			}
			HttpGet get = new HttpGet(url_get + "?" + params);
			Log.i(TAG, url_get + "?" + params.toString());
			HttpResponse response = httpClient.execute(get);
			res = EntityUtils.toString(response.getEntity(), encoding);
			// 关闭连接
			httpClient.getConnectionManager().shutdown();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "UnsupportedEncodingException");

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "ClientProtocolException");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "IOException");
		}

		return res;
	}
	// 异步回调接口
	public interface IOnHttpRequeseListener {
		public void finished(String jsonRes);

	}

}
