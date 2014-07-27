package com.foomap.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.foodmap.R;
import com.foomap.model.UserInfo;
import com.foomap.model.UserInfo.UserData;
import com.foomap.service.HttpServiceHelper.IOnHttpRequeseListener;
import com.foomap.service.UserHttpService;
import com.foomap.util.UserJsonUtils;

public class LoginActivity extends Activity implements OnClickListener {
	private static String TAG = "LoginActivity";
	private Button login_Button, login_register_Button;
	private EditText userId, password;
	private UserHttpService uHttpService;
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_activity);
		context = this;
		initView();
	}

	private void initView() {
		login_Button = (Button) findViewById(R.id.login_signin_button);
		login_register_Button = (Button) findViewById(R.id.login_register_button);
		uHttpService = new UserHttpService(context);
		userId = (EditText) findViewById(R.id.username_logIn);
		password = (EditText) findViewById(R.id.userPassword_logIn);
		bindEvnt();

	}

	protected void bindEvnt() {
		login_Button.setOnClickListener(this);
		login_register_Button.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_signin_button:
		    
			String uid=userId.getText().toString();
			String pd=password.getText().toString();
			if(uid==null||pd==null||uid.isEmpty()||pd.isEmpty())
			{
				Toast.makeText(context, "请输入数据", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(context, "正在登陆中", Toast.LENGTH_SHORT).show();
				login();
			}

			break;
		case R.id.login_register_button:
            goToActivity(RegisterActivity.class);
			break;
		default:
			break;
		}
	}

	// 登陆
	protected boolean login() {
		boolean isSucceed = false;
		String uId = userId.getText().toString();
		String uPassword = password.getText().toString();
		if (uId != null && uPassword != null && !uId.isEmpty()
				&& !uPassword.isEmpty()) {
			uHttpService.setRequestListener(new IOnHttpRequeseListener() {

				@Override
				public void finished(String jsonRes) {
					// TODO Auto-generated method stub
					if(jsonRes==null)
					{
						return ;
					}
					
					Log.i(TAG, jsonRes);
					
					// 登陆成功
					if (UserJsonUtils.isOpSucceed(jsonRes)) {
						UserData data = new UserData();
						data.userId = userId.getText().toString();
						UserInfo.insert(data, context.getApplicationContext());
						goToActivity(MainActivity.class);
				        
					}
					// 登录失败
					else {
						Toast.makeText(context,
								"账号/密码错误",
								Toast.LENGTH_LONG).show();
					}

				}
			});
			uHttpService.logIn(uId, uPassword);
		}
		return isSucceed;
	}
	//跳转
	protected void goToActivity(Class<?> cls)
	{
		Intent intent=new Intent();
		intent.setClass(context, cls);
		startActivity(intent);
	}

}
