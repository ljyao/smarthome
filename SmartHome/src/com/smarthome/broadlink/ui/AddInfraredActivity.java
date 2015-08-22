package com.smarthome.broadlink.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.com.broadlink.networkapi.NetworkAPI;
import com.app.smarthome.SmartHomeApplication;
import com.app.smarthome.R;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smarthome.broadlink.util.NetworkUtil;

public class AddInfraredActivity extends AppCompatActivity {

	private NetworkAPI mBlNetwork;
	private Button mBtnEasyConfigV2;
	private EditText mEtWifiSSIDEditText, mEtWifiPasswordEditText;
	private final String CODE = "code";
	private String MSG = "msg";
	private Context context = AddInfraredActivity.this;
	private String gatewayipaddr;
	private ProgressDialog easyConfigDialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_infrareddevice);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mBlNetwork = SmartHomeApplication.mBlNetwork;
		easyConfigDialog = new ProgressDialog(this);
		easyConfigDialog.setTitle("提示");
		easyConfigDialog.setMessage("请稍等...");
		findView();

		setListener();

	}

	@Override
	public void onResume() {
		super.onResume();
		initView();
	}

	public void findView() {

		mEtWifiSSIDEditText = (EditText) findViewById(R.id.et_wifi_ssid);
		mEtWifiPasswordEditText = (EditText) findViewById(R.id.et_wifi_password);
		mBtnEasyConfigV2 = (Button) findViewById(R.id.btn_smartConfig_v2);
	}

	/**
	 * 网络配置
	 * 
	 * @param ssid
	 * @param password
	 * @param easyConfigVersion
	 */
	public void easyConfig(String ssid, String password, int easyConfigVersion) {
		JsonObject in = new JsonObject();
		JsonObject out = new JsonObject();
		String outString;
		in.addProperty("ssid", ssid);
		// 密码不为空时，传入password参数
		if (!TextUtils.isEmpty(password.trim()))
			in.addProperty("password", password);
		// 设备配置的超时时间默认15
		in.addProperty("timeout", 15);
		in.addProperty("gatewayaddr", gatewayipaddr);

		String easyconfigstring = in.toString();
		outString = mBlNetwork.deviceEasyConfig(easyconfigstring);
		out = new JsonParser().parse(outString).getAsJsonObject();
		System.out.println(outString);
		final int code = out.get(CODE).getAsInt();
		String msg = out.get(MSG).getAsString();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				easyConfigDialog.cancel();
				switch (code) {
				case 0:
					Toast.makeText(context,
							R.string.toast_probe_to_show_new_device,
							Toast.LENGTH_SHORT).show();
					break;
				default:
					Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		});

	}

	public void initView() {
		NetworkUtil networkUtil = new NetworkUtil(
				AddInfraredActivity.this);
		networkUtil.startScan();
		gatewayipaddr = networkUtil.getGatewayaddr();
		String ssid = networkUtil.getWiFiSSID();
		mEtWifiSSIDEditText.setText(ssid);

	}

	public void setListener() {
		mBtnEasyConfigV2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				easyConfigDialog.show();
				new Thread(new Runnable() {

					@Override
					public void run() {
						String ssid = mEtWifiSSIDEditText.getText().toString();
						String password = mEtWifiPasswordEditText.getText()
								.toString();
						easyConfig(ssid, password, 1);
					}
				}).start();
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
