package com.foomap.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.foodmap.R;
import com.foomap.service.HttpServiceHelper.IOnHttpRequeseListener;
import com.foomap.service.UserHttpService;
import com.foomap.util.UserJsonUtils;

public class RegisterActivity extends Activity implements OnClickListener {

	private Button register_Button;
	private Button login_Button;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);
		initView();
	}

	private void initView() {
		register_Button = (Button) findViewById(R.id.register_button);
		register_Button.setOnClickListener(this);

		login_Button = (Button) findViewById(R.id.register_login_button);
		login_Button.setOnClickListener(this);

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_button:
			String userPassword = ((EditText) this
					.findViewById(R.id.register_password_edit)).getText()
					.toString().replace(" ", "");
			String userPasswordCheck = ((EditText) this
					.findViewById(R.id.register_check_edit)).getText()
					.toString().replace(" ", "");
			if (((EditText) this.findViewById(R.id.register_username_edit))
					.getText().toString().replace(" ", "").equals("")) {
				Toast.makeText(RegisterActivity.this, "用户名不能为空",
						Toast.LENGTH_SHORT).show();
			} else if (userPassword.equals("")) {
				Toast.makeText(RegisterActivity.this, "密码不能为空",
						Toast.LENGTH_SHORT).show();
			} else if (userPasswordCheck.equals(userPassword)) {
				UserHttpService uhs = new UserHttpService(this);
				if (null != uhs) {
					uhs.register(((EditText) RegisterActivity.this
							.findViewById(R.id.register_username_edit))
							.getText().toString(), userPassword,
							new IOnHttpRequeseListener() {

								@Override
								public void finished(String jsonRes) {
									if (UserJsonUtils.isSuccess(jsonRes)) {
										Toast.makeText(RegisterActivity.this,
												"注册成功", Toast.LENGTH_SHORT)
												.show();
										finish();
									} else {
										Toast.makeText(RegisterActivity.this,
												"用户已存在", Toast.LENGTH_SHORT)
												.show();
									}
								}
							});
				}
			} else {
				Toast.makeText(this, "两次输入的密码不同", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.register_login_button:
			finish();
		default:
			break;
		}
	}
}
