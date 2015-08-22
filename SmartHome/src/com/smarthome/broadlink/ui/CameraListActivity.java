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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.app.smarthome.R;
import com.j256.ormlite.dao.Dao;
import com.smarthome.adapter.CameraListAdapter;
import com.smarthome.camera.MonitorActivity;
import com.smarthome.database.CameraDeviceInfo;
import com.smarthome.database.DBManager;
import com.smarthome.database.SmartHomeDBHelper;

/**
 * @author Yann-LJY
 * 
 */
public class CameraListActivity extends AppCompatActivity implements
		OnItemLongClickListener, OnItemClickListener, OnClickListener {

	private List<CameraDeviceInfo> cameraList = new ArrayList<CameraDeviceInfo>();
	private ListView cameraListView;
	private CameraListAdapter mCameraAdapter;
	private AlertDialog operateAlertDialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_list);
		findView();

		setListener();

		initView();
	}

	public void findView() {
		cameraListView = (ListView) findViewById(R.id.cameralistView);
	}

	@Override
	public void onResume() {
		updateView();
		super.onResume();

	}

	private void updateView() {

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

		// 摄像头
		try {
			Dao<CameraDeviceInfo, Integer> cameraDao = DBManager.getDBhelper()
					.getDao(CameraDeviceInfo.class);
			List<CameraDeviceInfo> cameraInfos = cameraDao.queryForAll();
			cameraList.clear();
			cameraList.addAll(cameraInfos);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		mCameraAdapter = new CameraListAdapter(cameraList,
				CameraListActivity.this, cameraListView);

		cameraListView.setAdapter(mCameraAdapter);
	}

	public void setListener() {
		cameraListView.setOnItemClickListener(this);
		cameraListView.setOnItemLongClickListener(this);

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
					CameraListActivity.this).create();
			operateView = LayoutInflater.from(CameraListActivity.this).inflate(
					R.layout.dialog_operate, null);
			btn_update = (TextView) operateView
					.findViewById(R.id.operate_update);
			btn_update.setOnClickListener(CameraListActivity.this);
			btn_update.setTag(Integer.valueOf((int) id));
			btn_delete = (TextView) operateView
					.findViewById(R.id.operate_delete);
			btn_delete.setOnClickListener(CameraListActivity.this);
			btn_delete.setTag(Integer.valueOf((int) id));
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
			Intent intent = new Intent(CameraListActivity.this,
					MonitorActivity.class);
			CameraDeviceInfo cameraDeviceInfo = cameraList.get((int) id);
			intent.putExtra("cameraInfo", cameraDeviceInfo);
			startActivity(intent);

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
			View layout = LayoutInflater.from(CameraListActivity.this).inflate(
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
					CameraListActivity.this)
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
								Toast.makeText(CameraListActivity.this, "成功！",
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(CameraListActivity.this,
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
