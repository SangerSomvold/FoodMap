package com.foomap.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.foodmap.R;
import com.foomap.model.UserInfo;
import com.foomap.model.UserInfo.UserData;
import com.foomap.service.HttpServiceHelper.IOnHttpRequeseListener;
import com.foomap.service.UserHttpService;
import com.foomap.util.UserJsonUtils;

public class PasswrodChangeActivity<KeyEvent> extends Activity {
	public Button pswverifybtn, cancelbtn;

	String oldpsw, newpsw, pswverify;
	static String error = "error: 修改密码失败: 两次输入密码不同";
	static String error3 = "error: 修改密码失败";
	static String error2 = "error: 修改密码失败 旧密码不对";
	static String successed = "修改成功";

	private UserData usr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.passwordchange);
		usr = UserInfo.getUserData(getApplicationContext());
//		EditText oldpswET = (EditText) findViewById(R.id.oldpswET);
//		EditText newpswET = (EditText) findViewById(R.id.newpswET);
//		EditText pswverifyET = (EditText) findViewById(R.id.pswverifyET);
		pswverifybtn = (Button) findViewById(R.id.pswverifybtn);
		pswverifybtn.setOnClickListener(listener);
		cancelbtn = (Button) findViewById(R.id.pswcancelbtn);
		cancelbtn.setOnClickListener(listener);

	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Button btn = (Button) v;
			switch (btn.getId()) {
			case R.id.pswverifybtn: {

				EditText oldpswET = (EditText) findViewById(R.id.oldpswET);
				EditText newpswET = (EditText) findViewById(R.id.newpswET);
				EditText pswverifyET = (EditText) findViewById(R.id.pswverifyET);
				// Toast.makeText(PasswrodChangeActivity.this,
				// oldpswET.getText().toString(), Toast.LENGTH_SHORT).show();
				oldpsw = oldpswET.getText().toString().trim();
				newpsw = newpswET.getText().toString().trim();
				pswverify = pswverifyET.getText().toString().trim();

				if (oldpsw.equals("") || newpsw.equals("")
						|| pswverify.equals("")) {
					Toast.makeText(PasswrodChangeActivity.this, "输入信息不全",
							Toast.LENGTH_SHORT).show();
					break;
				}

				if (!newpsw.equals(pswverify)) {
					Toast.makeText(PasswrodChangeActivity.this, error,
							Toast.LENGTH_SHORT).show();

					break;
				}
				if (newpsw.equals(pswverify)) {

					UserHttpService userHttpService = new UserHttpService(
							PasswrodChangeActivity.this.getApplicationContext());
					userHttpService.resetPassWord(usr.userId, oldpsw,
							pswverify, new IOnHttpRequeseListener() {

								@Override
								public void finished(String jsonRes) {
									if (jsonRes.equals("")) {
										Toast.makeText(
												PasswrodChangeActivity.this,
												"网络不佳", Toast.LENGTH_SHORT)
												.show();
										return;

									} else {
										boolean b = UserJsonUtils
												.isOpSucceed(jsonRes);
										if (b) {
											Toast.makeText(
													PasswrodChangeActivity.this,
													successed,
													Toast.LENGTH_SHORT).show();
											finish();
										} else {
											Toast.makeText(
													PasswrodChangeActivity.this,
													error3, Toast.LENGTH_SHORT)
													.show();
										}
									}
								}
							});

				} else
					Toast.makeText(PasswrodChangeActivity.this, error3,
							Toast.LENGTH_SHORT).show();

			}

				break;
			case R.id.pswcancelbtn:
				Intent intent = new Intent();
				intent.setClass(PasswrodChangeActivity.this,
						UsrInfoActivity.class);
				startActivity(intent);
				finish();
				break;

			default:
				break;
			}
		}

	};

}
