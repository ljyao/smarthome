package com.smarthome.broadlink.ui;

import java.sql.SQLException;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.broadlink.networkapi.NetworkAPI;
import com.app.smarthome.SmartHomeApplication;
import com.app.smarthome.R;
import com.google.gson.JsonObject;
import com.j256.ormlite.dao.Dao;
import com.smarthome.adapter.ControlListAdapter;
import com.smarthome.broadlink.util.API;
import com.smarthome.broadlink.util.API.RefreshInfo;
import com.smarthome.broadlink.util.API.SendCallBack;
import com.smarthome.database.ControlInfo;
import com.smarthome.database.DBManager;
import com.smarthome.database.RemoteInfo;
import com.smarthome.database.SmartHomeDBHelper;

public class SpControlActivity extends AppCompatActivity implements
		OnClickListener, OnItemClickListener, OnItemLongClickListener {

	private NetworkAPI mBlNetwork;
	private ListView controlListView;
	private API api;
	private List<ControlInfo> controlInfos = null;
	private ControlListAdapter adapter;
	private TextView tempTextView;
	private RemoteInfo remoteInfo;
	private AlertDialog operateAlertDialog;
	private TextView btn_update, btn_delete;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_control);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mBlNetwork = SmartHomeApplication.mBlNetwork;

		api = new API(mBlNetwork, devicejsonstring(), this);
		findView();
		remoteInfo = (RemoteInfo) getIntent().getSerializableExtra("remoteId");
		setTitle(remoteInfo.name);
		SmartHomeDBHelper smartHomeDBHelper = DBManager.getDBhelper();
		try {
			Dao<ControlInfo, Integer> controlDao = smartHomeDBHelper
					.getDao(ControlInfo.class);
			controlInfos = controlDao.queryBuilder().where()
					.eq("remote_id", remoteInfo).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		controlListView = (ListView) findViewById(R.id.controllistview);
		adapter = new ControlListAdapter(this, controlInfos);
		controlListView.setAdapter(adapter);
		controlListView.setOnItemClickListener(this);
		controlListView.setOnItemLongClickListener(this);

	}

	private void querystatus() {
		try {
			api.dnaControl_refresh(new SendCallBack() {
				@Override
				public void result(int status, Object reObject) {
					if (status != 0) {
						return;
					}
					RefreshInfo refreshInfo = (RefreshInfo) reObject;
					tempTextView.setText("室温：" + refreshInfo.temp_integer + "."
							+ refreshInfo.temp_decimal + "℃");
				}
			});
		} catch (Exception e) {
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
		querystatus();
		super.onResume();
	}

	public void findView() {
		tempTextView = (TextView) findViewById(R.id.id_temp);
		// 操作对话框
		operateAlertDialog = new AlertDialog.Builder(this).create();
		View operateView = LayoutInflater.from(this).inflate(
				R.layout.dialog_operate, null);
		btn_update = (TextView) operateView.findViewById(R.id.operate_update);
		btn_update.setOnClickListener(this);
		btn_delete = (TextView) operateView.findViewById(R.id.operate_delete);
		btn_delete.setOnClickListener(this);
		operateAlertDialog.setView(operateView);
		operateAlertDialog.setTitle("操作");
	}

	@Override
	public void onClick(View v) {
		ControlInfo controlInfo = null;
		Integer postion;
		switch (v.getId()) {
		case R.id.operate_update:
			postion = (Integer) v.getTag();
			controlInfo = controlInfos.get(postion);
			addByStudy(controlInfo, true);
			break;
		case R.id.operate_delete:
			postion = (Integer) v.getTag();
			controlInfo = controlInfos.get(postion);

			try {
				Dao<ControlInfo, Integer> ControlInfoDao = DBManager
						.getDBhelper().getDao(ControlInfo.class);
				ControlInfoDao.delete(controlInfo);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			controlInfos.remove(controlInfo);
			adapter.notifyDataSetChanged();
			operateAlertDialog.cancel();
			break;
		}
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
			intent = new Intent(SpControlActivity.this,
					RemoteActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_device_list:
			intent = new Intent(SpControlActivity.this,
					DeviceListActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_control_add:
			addByStudy(null, true);
			break;
		}
		return true;
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
									Toast.makeText(SpControlActivity.this,
											"学习成功!", Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(SpControlActivity.this,
											"没有学习到信息!", Toast.LENGTH_SHORT)
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
									controlInfos.add(info);
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
								adapter.notifyDataSetChanged();
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

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		btn_update.setTag(Integer.valueOf((int) id));
		btn_delete.setTag(Integer.valueOf((int) id));
		operateAlertDialog.show();
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		sendCode(controlInfos.get(position));
	}

}
