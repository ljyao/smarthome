package com.smarthome.broadlink.ui;

import java.sql.SQLException;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.app.smarthome.R;
import com.j256.ormlite.dao.Dao;
import com.smarthome.database.CameraDeviceInfo;
import com.smarthome.database.DBManager;

public class AddCameraActivity extends AppCompatActivity {

	private Button mCameraConfig;
	private EditText cameraNamEditText, cUserNameEditText, cPassWordEditText,
			cIpaddrEditText;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_camera);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		findView();

		setListener();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void findView() {

		mCameraConfig = (Button) findViewById(R.id.btn_camera_config);
		cameraNamEditText = (EditText) findViewById(R.id.et_camera_name);
		cUserNameEditText = (EditText) findViewById(R.id.et_camera_username);
		cPassWordEditText = (EditText) findViewById(R.id.et_camera_password);
		cIpaddrEditText = (EditText) findViewById(R.id.et_camera_ipaddr);
		cIpaddrEditText.setText(":8000");
	}

	public void setListener() {
		mCameraConfig.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CameraDeviceInfo cameraDeviceInfo = new CameraDeviceInfo();
				cameraDeviceInfo.name = cameraNamEditText.getText().toString();
				cameraDeviceInfo.username = cUserNameEditText.getText()
						.toString();
				cameraDeviceInfo.userpwd = cPassWordEditText.getText()
						.toString();
				String ipaddr = cIpaddrEditText.getText().toString();
				String str[] = ipaddr.split(":");
				if (str.length == 2) {
					cameraDeviceInfo.serverip = str[0];
					cameraDeviceInfo.serverport = Integer.parseInt(str[1]);
					Toast.makeText(AddCameraActivity.this, "成功！",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(AddCameraActivity.this, "ip地址格式不正确！",
							Toast.LENGTH_LONG).show();
					return;
				}
				try {
					Dao<CameraDeviceInfo, Integer> cameraDeviceDao = DBManager
							.getDBhelper().getDao(CameraDeviceInfo.class);
					cameraDeviceDao.createIfNotExists(cameraDeviceInfo);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
