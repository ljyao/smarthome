package com.smarthome.broadlink.ui;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;
import com.smarthome.adapter.CameraListAdapter;
import com.smarthome.adapter.DeviceListAdapter;
import com.smarthome.broadlink.util.API;
import com.smarthome.camera.MonitorActivity;
import com.smarthome.database.CameraDeviceInfo;
import com.smarthome.database.DBManager;
import com.smarthome.database.DeviceInfo;
import com.smarthome.database.SmartHomeDBHelper;

/**
 * @author Yann-LJY
 * 
 */
public class DeviceListActivity extends AppCompatActivity implements
		OnItemLongClickListener, OnItemClickListener, OnClickListener {

	private NetworkAPI mBlNetwork;
	private Context mcontent = this;
	private final String CODE = "code";
	private String MSG = "msg";
	private List<DeviceInfo> deviceArrayList = new ArrayList<DeviceInfo>();
	private List<CameraDeviceInfo> cameraList = new ArrayList<CameraDeviceInfo>();
	private ListView deviceListView, cameraListView;
	private DeviceListAdapter mDeviceAdapter;
	private CameraListAdapter mCameraAdapter;
	private SwipeRefreshLayout refreshLayout;
	private HashMap<String, Boolean> deviceStatusMap = new HashMap<String, Boolean>();
	private AlertDialog operateAlertDialog;
	private API api;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_list);
		mBlNetwork = SmartHomeApplication.mBlNetwork;
		api = new API(mBlNetwork, null, this);
		findView();

		setListener();

		initView();
	}

	public void findView() {
		cameraListView = (ListView) findViewById(R.id.cameralistView);
		deviceListView = (ListView) findViewById(R.id.ProbeDevicelistView);
		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe);
	}

	/*
	 * 该函数的作用是去 在deviceArrayList设备列表查找对应的 设备是否已经配对成功了 根据key是否为空来判断
	 * 未配对成功，key为null，id为0；若已经配对成功了，则返回该设备在设备列表中的下标。并且该设备不需要重复配对
	 * 重复配对次数超过40，再次配对会报错-5
	 */
	public int whehtheralreadypiared(DeviceInfo device) {
		for (DeviceInfo temp : deviceArrayList) {
			if ((temp.getMac().equals(device.getMac()))
					&& (temp.getKey() != null))
				return deviceArrayList.indexOf(temp);
		}
		return -1;

	}

	private void refreshBLList() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				probeList();
			}
		}).start();
	}

	// Probe List,查找设备
	public void probeList() {
		// TODO Auto-generated method stub
		JsonObject in = new JsonObject();
		JsonObject out = new JsonObject();
		JsonArray listJsonArray = new JsonArray();

		in.addProperty("scantime", 3000);
		in.addProperty("interval", 1000);
		String probeJsonstring = in.toString();
		String probeOut = mBlNetwork.deviceProbe(probeJsonstring);

		out = new JsonParser().parse(probeOut).getAsJsonObject();
		System.out.println(out);
		int code = out.get(CODE).getAsInt();
		String msg = out.get(MSG).getAsString();
		listJsonArray = out.get("list").getAsJsonArray();

		Gson gson = new Gson();
		Type listType = new TypeToken<ArrayList<DeviceInfo>>() {
		}.getType();

		final List<DeviceInfo> tempdeviceArrayList = (ArrayList<DeviceInfo>) gson
				.fromJson(listJsonArray, listType);

		Iterator<DeviceInfo> sListIterator = tempdeviceArrayList.iterator();

		deviceStatusMap.clear();

		while (sListIterator.hasNext()) {
			DeviceInfo temp = sListIterator.next();
			if (temp.getType() != 10002) {
				sListIterator.remove();
			} else {
				temp.status = true;
				deviceStatusMap.put(temp.getMac(), true);
				int i = whehtheralreadypiared(temp);
				// i>0代表该设备之前已经配对成功过，将key和id复制过来，后面就不需要重新配对了
				if (i >= 0) {
					temp.setId(deviceArrayList.get(i).getId());
					temp.setKey(deviceArrayList.get(i).getKey());
				}
			}

		}
		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				refreshLayout.setRefreshing(false);

				deviceArrayList.clear();
				deviceArrayList.addAll(tempdeviceArrayList);
				if (deviceArrayList.size() > 0) {
					for (int i = 0; i < deviceArrayList.size(); i++) {
						DeviceInfo device = deviceArrayList.get(i);
						// key为null,表示未配对成功，则进行配对
						if (device.getKey() == null) {
							api.devicePair(device);
						}
					}
				} else {
					Toast.makeText(DeviceListActivity.this,
							R.string.toast_probe_no_device, Toast.LENGTH_SHORT)
							.show();
				}
				// 界面更新
				SmartHomeDBHelper smartHomeDBHelper = DBManager.getDBhelper();
				try {
					Dao<DeviceInfo, Integer> deviceDao = smartHomeDBHelper
							.getDao(DeviceInfo.class);
					List<DeviceInfo> deviceInfos = deviceDao.queryForAll();
					deviceArrayList.clear();
					deviceArrayList.addAll(deviceInfos);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				for (DeviceInfo deviceInfo : deviceArrayList) {
					Boolean statue = deviceStatusMap.get(deviceInfo.mac);
					if (statue != null && statue.booleanValue()) {
						deviceInfo.status = true;
					} else {
						deviceInfo.status = false;
					}
				}
				mDeviceAdapter.notifyDataSetChanged();
				refreshLayout.setRefreshing(false);
			}
		});

	}

	@Override
	public void onResume() {
		updateView();
		super.onResume();

	}

	private void updateView() {

		refreshLayout.setRefreshing(true);
		refreshBLList();
		// 摄像头
		SmartHomeDBHelper smartHomeDBHelper = DBManager.getDBhelper();
		try {
			Dao<CameraDeviceInfo, String> cameraDao = smartHomeDBHelper
					.getDao(CameraDeviceInfo.class);
			List<CameraDeviceInfo> cameraInfos = cameraDao.queryForAll();
			cameraList.clear();
			cameraList.addAll(cameraInfos);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		mCameraAdapter.notifyDataSetChanged();
	}

	public void initView() {

		// 红外列表初始化
		SmartHomeDBHelper smartHomeDBHelper = DBManager.getDBhelper();
		try {
			Dao<DeviceInfo, String> deviceDao = smartHomeDBHelper
					.getDao(DeviceInfo.class);
			List<DeviceInfo> deviceInfos = deviceDao.queryForAll();
			deviceArrayList.clear();
			deviceArrayList.addAll(deviceInfos);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		mDeviceAdapter = new DeviceListAdapter(deviceArrayList,
				DeviceListActivity.this, deviceListView);

		deviceListView.setAdapter(mDeviceAdapter);
		// 摄像头
		try {
			Dao<CameraDeviceInfo, Integer> cameraDao = smartHomeDBHelper
					.getDao(CameraDeviceInfo.class);
			List<CameraDeviceInfo> cameraInfos = cameraDao.queryForAll();
			cameraList.clear();
			cameraList.addAll(cameraInfos);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		mCameraAdapter = new CameraListAdapter(cameraList,
				DeviceListActivity.this, cameraListView);

		cameraListView.setAdapter(mCameraAdapter);
	}

	public void setListener() {
		refreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				refreshBLList();
			}
		});
		cameraListView.setOnItemClickListener(this);
		cameraListView.setOnItemLongClickListener(this);
		deviceListView.setOnItemClickListener(this);
		deviceListView.setOnItemLongClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_devicelist, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.menu_device_add_camera:
			intent = new Intent(this, AddCameraActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_device_add_infrared:
			intent = new Intent(this, AddInfraredActivity.class);
			startActivity(intent);
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			final int position, long id) {
		TextView btn_update, btn_delete;
		View operateView;
		switch (parent.getId()) {
		case R.id.cameralistView:

			operateAlertDialog = new AlertDialog.Builder(
					DeviceListActivity.this).create();
			operateView = LayoutInflater.from(DeviceListActivity.this).inflate(
					R.layout.dialog_operate, null);
			btn_update = (TextView) operateView
					.findViewById(R.id.operate_update);
			btn_update.setOnClickListener(DeviceListActivity.this);
			btn_update.setTag(Integer.valueOf((int) id));
			btn_delete = (TextView) operateView
					.findViewById(R.id.operate_delete);
			btn_delete.setOnClickListener(DeviceListActivity.this);
			btn_delete.setTag(Integer.valueOf((int) id));
			operateAlertDialog.setView(operateView);
			operateAlertDialog.setTitle("操作");
			operateAlertDialog.show();
			break;
		case R.id.ProbeDevicelistView:
			operateAlertDialog = new AlertDialog.Builder(
					DeviceListActivity.this).create();
			operateView = LayoutInflater.from(DeviceListActivity.this).inflate(
					R.layout.dialog_operate, null);

			btn_update = (TextView) operateView
					.findViewById(R.id.operate_update);
			btn_update.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					final AppCompatEditText editText = new AppCompatEditText(
							DeviceListActivity.this);
					editText.setHint("设备名称");
					AlertDialog dialog = new AlertDialog.Builder(
							DeviceListActivity.this)
							.setPositiveButton("确定",
									new Dialog.OnClickListener() {
										@Override
										public void onClick(
												final DialogInterface dialog,
												int which) {
											String name = editText.getText()
													.toString();
											DeviceInfo deviceInfo = deviceArrayList
													.get(position);
											deviceInfo.name = name;
											try {
												Dao<DeviceInfo, String> deviceDao = DBManager
														.getDBhelper()
														.getDao(DeviceInfo.class);
												deviceDao.update(deviceInfo);
											} catch (SQLException e) {
												e.printStackTrace();
											}
											mDeviceAdapter
													.notifyDataSetChanged();
											operateAlertDialog.cancel();
											dialog.cancel();

										}
									})
							.setNegativeButton("取消",
									new Dialog.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.cancel();
										}
									}).create();
					dialog.setView(editText);
					dialog.setMessage("修改设备名称");
					dialog.setTitle("重命名");
					dialog.show();

				}
			});
			btn_delete = (TextView) operateView
					.findViewById(R.id.operate_delete);
			btn_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					DeviceInfo deviceInfo = deviceArrayList.remove(position);
					try {
						Dao<DeviceInfo, String> deviceDao = DBManager
								.getDBhelper().getDao(DeviceInfo.class);
						deviceDao.delete(deviceInfo);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					mDeviceAdapter.notifyDataSetChanged();
					operateAlertDialog.cancel();
				}
			});
			operateAlertDialog.setView(operateView);
			operateAlertDialog.setTitle("操作");
			operateAlertDialog.show();
			break;
		}

		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (parent.getId()) {

		case R.id.cameralistView:
			Intent intent = new Intent(DeviceListActivity.this,
					MonitorActivity.class);
			CameraDeviceInfo cameraDeviceInfo = cameraList.get((int) id);
			intent.putExtra("cameraInfo", cameraDeviceInfo);
			startActivity(intent);

			break;
		case R.id.ProbeDevicelistView:
			SmartHomeApplication.mdeviceinfo = deviceArrayList.get((int) id);
			intent = new Intent(mcontent, RemoteActivity.class);
			mcontent.startActivity(intent);
			break;
		}
	}

	@Override
	public void onClick(View v) {
		int postion = (Integer) v.getTag();
		final CameraDeviceInfo cameraDeviceInfo;
		switch (v.getId()) {
		case R.id.operate_update:
			postion = (Integer) v.getTag();
			cameraDeviceInfo = cameraList.get(postion);
			final AppCompatEditText cameraNamEditText;
			final AppCompatEditText cUserNameEditText;
			final AppCompatEditText cPassWordEditText;
			final AppCompatEditText cIpaddrEditText;
			View layout = LayoutInflater.from(DeviceListActivity.this).inflate(
					R.layout.add_camera, null);
			cameraNamEditText = (AppCompatEditText) layout
					.findViewById(R.id.et_camera_name);
			cUserNameEditText = (AppCompatEditText) layout
					.findViewById(R.id.et_camera_username);
			cPassWordEditText = (AppCompatEditText) layout
					.findViewById(R.id.et_camera_password);
			cIpaddrEditText = (AppCompatEditText) layout
					.findViewById(R.id.et_camera_ipaddr);
			if (cameraDeviceInfo.name == null) {
				cameraDeviceInfo.name = "";
			}
			cameraNamEditText.setText(cameraDeviceInfo.name);
			if (cameraDeviceInfo.username == null) {
				cameraDeviceInfo.username = "";
			}
			cUserNameEditText.setText(cameraDeviceInfo.username);
			if (cameraDeviceInfo.userpwd == null) {
				cameraDeviceInfo.userpwd = "";
			}
			cPassWordEditText.setText(cameraDeviceInfo.userpwd);

			cIpaddrEditText.setText(cameraDeviceInfo.serverip + ":"
					+ cameraDeviceInfo.serverport);

			AlertDialog dialog = new AlertDialog.Builder(
					DeviceListActivity.this)
					.setPositiveButton("确定", new Dialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							cameraDeviceInfo.name = cameraNamEditText.getText()
									.toString();
							cameraDeviceInfo.username = cUserNameEditText
									.getText().toString();
							cameraDeviceInfo.userpwd = cPassWordEditText
									.getText().toString();
							String ipaddr = cIpaddrEditText.getText()
									.toString();
							String str[] = ipaddr.split(":");
							if (str.length == 2) {
								cameraDeviceInfo.serverip = str[0];
								cameraDeviceInfo.serverport = Integer
										.parseInt(str[1]);
								Toast.makeText(DeviceListActivity.this, "成功！",
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(DeviceListActivity.this,
										"ip地址格式不正确！", Toast.LENGTH_LONG).show();
								return;
							}
							try {
								Dao<CameraDeviceInfo, Integer> cameraDeviceDao = DBManager
										.getDBhelper().getDao(
												CameraDeviceInfo.class);
								cameraDeviceDao.update(cameraDeviceInfo);
							} catch (SQLException e) {
								e.printStackTrace();
							}
							dialog.cancel();
							operateAlertDialog.cancel();
							mCameraAdapter.notifyDataSetChanged();
						}
					}).setNegativeButton("取消", new Dialog.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							operateAlertDialog.cancel();
						}
					}).create();
			dialog.setView(layout);
			dialog.setMessage("修改摄像头");
			dialog.setTitle("摄像头");
			dialog.show();

			break;
		case R.id.operate_delete:
			postion = (Integer) v.getTag();
			cameraDeviceInfo = cameraList.remove(postion);
			try {
				Dao<CameraDeviceInfo, Integer> cameraDeviceDao = DBManager
						.getDBhelper().getDao(CameraDeviceInfo.class);
				cameraDeviceDao.delete(cameraDeviceInfo);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			mCameraAdapter.notifyDataSetChanged();
			operateAlertDialog.cancel();
			break;
		default:
			break;
		}
	}
}
