package com.smarthome.broadlink.ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.broadlink.networkapi.NetworkAPI;
import com.app.smarthome.SmartHomeApplication;
import com.app.smarthome.R;
import com.google.gson.JsonObject;
import com.j256.ormlite.dao.Dao;
import com.smarthome.adapter.TVGridAdapter;
import com.smarthome.broadlink.util.API;
import com.smarthome.broadlink.util.API.RefreshInfo;
import com.smarthome.broadlink.util.API.SendCallBack;
import com.smarthome.database.ControlInfo;
import com.smarthome.database.DBManager;
import com.smarthome.database.RemoteInfo;

public class TVControlActivity extends AppCompatActivity implements
		OnClickListener, OnItemClickListener, OnItemLongClickListener,
		OnLongClickListener {

	private NetworkAPI mBlNetwork;
	private API api;
	private TVGridAdapter tvGridAdapter;
	private GridView tvGridView;
	private List<ControlInfo> tvControlInfos = new ArrayList<ControlInfo>(),
			tvBaseData;
	private ImageView btn_mute, btn_power, btn_up, btn_down, btn_left,
			btn_right, btn_menu;
	private RemoteInfo remoteInfo;
	private AlertDialog operateAlertDialog;
	private TextView btn_update, btn_delete;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tv_control);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mBlNetwork = SmartHomeApplication.mBlNetwork;
		initView();
		api = new API(mBlNetwork, devicejsonstring(), this);

		remoteInfo = (RemoteInfo) getIntent().getSerializableExtra("remoteId");
		setTitle(remoteInfo.name);

		initData();
		tvGridAdapter = new TVGridAdapter(this, tvControlInfos);
		tvGridView.setAdapter(tvGridAdapter);
	}

	private void initView() {
		btn_mute = (ImageView) findViewById(R.id.tv_mute);
		btn_power = (ImageView) findViewById(R.id.tv_power);
		btn_up = (ImageView) findViewById(R.id.tv_up);
		btn_down = (ImageView) findViewById(R.id.tv_down);
		btn_left = (ImageView) findViewById(R.id.tv_left);
		btn_right = (ImageView) findViewById(R.id.tv_right);
		btn_menu = (ImageView) findViewById(R.id.tv_menu);

		btn_mute.setOnClickListener(this);
		btn_power.setOnClickListener(this);
		btn_up.setOnClickListener(this);
		btn_down.setOnClickListener(this);
		btn_left.setOnClickListener(this);
		btn_right.setOnClickListener(this);
		btn_menu.setOnClickListener(this);

		btn_mute.setOnLongClickListener(this);
		btn_power.setOnLongClickListener(this);
		btn_up.setOnLongClickListener(this);
		btn_down.setOnLongClickListener(this);
		btn_left.setOnLongClickListener(this);
		btn_right.setOnLongClickListener(this);
		btn_menu.setOnLongClickListener(this);

		tvGridView = (GridView) findViewById(R.id.tv_gridview);
		tvGridView.setOnItemClickListener(this);
		tvGridView.setOnItemLongClickListener(this);

		// 操作对话框
		operateAlertDialog = new AlertDialog.Builder(TVControlActivity.this)
				.create();
		View operateView = LayoutInflater.from(TVControlActivity.this).inflate(
				R.layout.dialog_operate, null);
		btn_update = (TextView) operateView.findViewById(R.id.operate_update);
		btn_update.setOnClickListener(TVControlActivity.this);
		btn_delete = (TextView) operateView.findViewById(R.id.operate_delete);
		btn_delete.setOnClickListener(TVControlActivity.this);
		operateAlertDialog.setView(operateView);
		operateAlertDialog.setTitle("操作");
	}

	private void initData() {
		try {
			Dao<ControlInfo, Integer> controlDao = DBManager.getDBhelper()
					.getDao(ControlInfo.class);
			tvBaseData = controlDao.queryBuilder().where()
					.eq("remote_id", remoteInfo).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (tvBaseData.size() == 0) {
			String name[] = { "静音", "电源", "节目+", "音量+", "menu", "音量-", "节目-" };
			for (int i = 0; i < 7; i++) {
				ControlInfo info = new ControlInfo();
				info.remote = remoteInfo;
				info.name = name[i];
				tvBaseData.add(info);
			}
			for (int i = 1; i <= 9; i++) {
				ControlInfo info = new ControlInfo();
				info.remote = remoteInfo;
				info.name = i + "";
				tvBaseData.add(info);
			}
			for (int i = 0; i < 3; i++) {
				ControlInfo info = new ControlInfo();
				info.remote = remoteInfo;
				switch (i) {
				case 0:
					info.name = "*";
					tvBaseData.add(info);
					break;
				case 1:
					info.name = "0";
					tvBaseData.add(info);
					break;
				case 2:
					info.name = "#";
					tvBaseData.add(info);
					break;
				}
			}
			try {
				Dao<ControlInfo, Integer> controlDao = DBManager.getDBhelper()
						.getDao(ControlInfo.class);
				for (ControlInfo Info : tvBaseData) {
					controlDao.createIfNotExists(Info);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		for (int j = 0; j < tvBaseData.size(); j++) {
			if (j >= 7) {
				tvControlInfos.add(tvBaseData.get(j));
			}
		}
	}

	/* 该函数用于生成设备对应的json描述信息 */
	protected String devicejsonstring() {

		JsonObject deviceinfoin = new JsonObject();
		deviceinfoin.addProperty("mac",
				SmartHomeApplication.mdeviceinfo.getMac());
		deviceinfoin.addProperty("type",
				SmartHomeApplication.mdeviceinfo.getType());
		deviceinfoin.addProperty("key",
				SmartHomeApplication.mdeviceinfo.getKey());
		deviceinfoin
				.addProperty("id", SmartHomeApplication.mdeviceinfo.getId());
		deviceinfoin.addProperty("password",
				SmartHomeApplication.mdeviceinfo.getPassword());
		deviceinfoin.addProperty("lanaddr",
				SmartHomeApplication.mdeviceinfo.getLanaddr());
		deviceinfoin.addProperty("subdevice",
				SmartHomeApplication.mdeviceinfo.getSubdevice());
		String deviceinfo = deviceinfoin.toString();
		return deviceinfo;

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void findView() {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_control, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.menu_remote_list:
			intent = new Intent(TVControlActivity.this,
					RemoteActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_device_list:
			intent = new Intent(TVControlActivity.this,
					DeviceListActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_control_add:
			addByStudy(null, true);
			break;
		}

		return true;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {

		if (position < 12) {
			ControlInfo controlInfo = tvControlInfos.get(position);
			addByStudy(controlInfo, false);
		} else {
			btn_update.setTag(Integer.valueOf((int) id));
			btn_delete.setTag(Integer.valueOf((int) id));
			operateAlertDialog.show();
		}

		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ControlInfo controlInfo = tvControlInfos.get(position);
		sendCode(controlInfo);

	}

	private void sendCode(final ControlInfo controlInfo) {
		if (controlInfo.code == null || controlInfo.code.equals("")) {
			addByStudy(controlInfo, false);
		} else {
			api.dnaControl_send(controlInfo.code, new SendCallBack() {
				@Override
				public void result(int status, Object reObject) {
				}
			});
		}
	}

	@Override
	public boolean onLongClick(View v) {
		ControlInfo controlInfo = null;
		switch (v.getId()) {
		case R.id.tv_mute:
			controlInfo = tvBaseData.get(0);
			break;
		case R.id.tv_power:
			controlInfo = tvBaseData.get(1);
			break;
		case R.id.tv_up:
			controlInfo = tvBaseData.get(2);
			break;
		case R.id.tv_left:
			controlInfo = tvBaseData.get(3);
			break;
		case R.id.tv_menu:
			controlInfo = tvBaseData.get(4);
			break;
		case R.id.tv_right:
			controlInfo = tvBaseData.get(5);
			break;
		case R.id.tv_down:
			controlInfo = tvBaseData.get(6);
			break;

		}
		addByStudy(controlInfo, false);
		return true;
	}

	@Override
	public void onClick(View v) {
		ControlInfo controlInfo = null;
		Integer postion;
		switch (v.getId()) {
		case R.id.operate_update:
			postion = (Integer) v.getTag();
			controlInfo = tvControlInfos.get(postion);
			addByStudy(controlInfo, true);
			break;
		case R.id.operate_delete:
			postion = (Integer) v.getTag();
			controlInfo = tvControlInfos.get(postion);

			try {
				Dao<ControlInfo, Integer> ControlInfoDao = DBManager
						.getDBhelper().getDao(ControlInfo.class);
				ControlInfoDao.delete(controlInfo);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			tvControlInfos.remove(controlInfo);
			tvGridAdapter.notifyDataSetChanged();
			operateAlertDialog.cancel();
			break;
		case R.id.tv_mute:
			controlInfo = tvBaseData.get(0);
			sendCode(controlInfo);
			break;
		case R.id.tv_power:
			controlInfo = tvBaseData.get(1);
			sendCode(controlInfo);
			break;
		case R.id.tv_up:
			controlInfo = tvBaseData.get(2);
			sendCode(controlInfo);
			break;
		case R.id.tv_left:
			controlInfo = tvBaseData.get(3);
			sendCode(controlInfo);
			break;
		case R.id.tv_menu:
			controlInfo = tvBaseData.get(4);
			sendCode(controlInfo);
			break;
		case R.id.tv_right:
			controlInfo = tvBaseData.get(5);
			sendCode(controlInfo);
			break;
		case R.id.tv_down:
			controlInfo = tvBaseData.get(6);
			sendCode(controlInfo);
			break;
		}

	}

	private void addByStudy(final ControlInfo addInfo, boolean isReName) {
		api.dnaControl_study(new SendCallBack() {
			@Override
			public void result(int status, Object reObject) {
			}
		});
		final AppCompatEditText editText = new AppCompatEditText(this);
		editText.setHint("按键名称");
		if (addInfo != null) {
			editText.setText(addInfo.name);
		}
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setPositiveButton("确定", new Dialog.OnClickListener() {

					@Override
					public void onClick(final DialogInterface dialog, int which) {
						api.dnaControl_code(new SendCallBack() {
							@Override
							public void result(int status, Object reObject) {
								String studyCode = null;
								if (status == 0) {
									studyCode = (String) reObject;
									Toast.makeText(TVControlActivity.this,
											"学习成功!", Toast.LENGTH_LONG).show();
								} else {
									Toast.makeText(TVControlActivity.this,
											"没有学习到信息!", Toast.LENGTH_LONG)
											.show();
								}

								if (addInfo == null) {
									ControlInfo info = new ControlInfo();
									info.code = studyCode;
									info.remote = remoteInfo;
									info.name = editText.getText().toString();

									try {
										Dao<ControlInfo, Integer> controlDao = DBManager
												.getDBhelper().getDao(
														ControlInfo.class);
										info = controlDao
												.createIfNotExists(info);
									} catch (SQLException e) {
										e.printStackTrace();
									}
									tvControlInfos.add(info);
								} else {
									addInfo.name = editText.getText()
											.toString();
									if (studyCode != null) {
										addInfo.code = studyCode;
									}

									try {
										Dao<ControlInfo, Integer> controlDao = DBManager
												.getDBhelper().getDao(
														ControlInfo.class);
										controlDao.update(addInfo);
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
								operateAlertDialog.cancel();
								tvGridAdapter.notifyDataSetChanged();
								dialog.cancel();

							}
						});

					}
				}).setNegativeButton("取消", new Dialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).create();
		if (isReName) {
			dialog.setView(editText);
			dialog.setMessage("请对准设备，按下遥控器按键");
		} else {
			dialog.setMessage("请对准设备，按下遥控器\"" + addInfo.name + "\"按键");
		}
		dialog.setTitle("学习");
		dialog.show();
	}
}
