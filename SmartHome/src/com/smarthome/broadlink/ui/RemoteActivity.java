package com.smarthome.broadlink.ui;

import java.sql.SQLException;
import java.util.List;
import com.app.smarthome.R;
import com.j256.ormlite.dao.Dao;
import com.smarthome.adapter.RemoteGridAdapter;
import com.smarthome.database.ControlInfo;
import com.smarthome.database.DBManager;
import com.smarthome.database.RemoteInfo;
import com.smarthome.database.SmartHomeDBHelper;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author Yann-LJY
 * 
 */
public class RemoteActivity extends AppCompatActivity implements
		OnClickListener, OnItemClickListener, OnItemLongClickListener {
	private GridView controlGridView;
	private List<RemoteInfo> remoteList = null;
	private RemoteGridAdapter adapter;
	private AlertDialog remoteAlertDialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remote);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		findView();

		SmartHomeDBHelper smartHomeDBHelper = DBManager.getDBhelper();
		try {
			Dao<RemoteInfo, Integer> controlDao = smartHomeDBHelper
					.getDao(RemoteInfo.class);
			remoteList = controlDao.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		controlGridView = (GridView) findViewById(R.id.controlgridview);
		adapter = new RemoteGridAdapter(this, remoteList);
		controlGridView.setAdapter(adapter);

		controlGridView.setOnItemClickListener(this);
		controlGridView.setOnItemLongClickListener(this);

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void findView() {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.remote_air:
			remoteAlertDialog.dismiss();
			addRemote(RemoteInfo.air, null);
			break;

		case R.id.remote_diy:
			remoteAlertDialog.dismiss();
			addRemote(RemoteInfo.diy, null);
			break;
		case R.id.remote_tv:
			remoteAlertDialog.dismiss();
			addRemote(RemoteInfo.tv, null);
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_remote, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.menu_device_add_camera:
			intent = new Intent(this, AddCameraActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_device_add_infrared:
			intent = new Intent(this, AddInfraredActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_device_list:
			intent = new Intent(RemoteActivity.this, DeviceListActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_control_add:
			remoteAlertDialog = new AlertDialog.Builder(RemoteActivity.this)
					.create();
			View remoteDialogView = LayoutInflater.from(RemoteActivity.this)
					.inflate(R.layout.dialog_add_remote, null);
			TextView tv_dialog,
			diy_dialog,
			air_dialog;
			tv_dialog = (TextView) remoteDialogView
					.findViewById(R.id.remote_tv);
			tv_dialog.setOnClickListener(RemoteActivity.this);
			air_dialog = (TextView) remoteDialogView
					.findViewById(R.id.remote_air);
			air_dialog.setOnClickListener(RemoteActivity.this);
			diy_dialog = (TextView) remoteDialogView
					.findViewById(R.id.remote_diy);
			diy_dialog.setOnClickListener(this);
			remoteAlertDialog.setView(remoteDialogView);
			remoteAlertDialog.setTitle("添加遥控器");
			remoteAlertDialog.show();
			break;
		}
		return true;
	}

	private void addRemote(final int type, final RemoteInfo remoteInfo) {
		final AppCompatEditText editText = new AppCompatEditText(this);
		editText.setHint("遥控器名称");
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setPositiveButton("确定", new Dialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (remoteInfo == null) {
							RemoteInfo info = new RemoteInfo();
							info.name = editText.getText().toString();
							info.type = type;
							try {
								Dao<RemoteInfo, Integer> remoteDao = DBManager
										.getDBhelper().getDao(RemoteInfo.class);
								info = remoteDao.createIfNotExists(info);
							} catch (SQLException e) {
								e.printStackTrace();
							}
							remoteList.add(info);
							adapter.notifyDataSetChanged();
						} else {

							remoteInfo.name = editText.getText().toString();

							try {
								Dao<RemoteInfo, Integer> remoteDao = DBManager
										.getDBhelper().getDao(RemoteInfo.class);
								remoteDao.update(remoteInfo);
							} catch (SQLException e) {
								e.printStackTrace();
							}
							adapter.notifyDataSetChanged();
						}

						dialog.cancel();

					}
				}).setNegativeButton("取消", new Dialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).create();
		dialog.setView(editText);
		if (remoteInfo == null) {
			dialog.setMessage("输入遥控器名称");
			dialog.setTitle("添加遥控器");
		} else {
			dialog.setMessage("输入遥控器名称");
			dialog.setTitle("修改遥控器");
		}

		dialog.show();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			final int position, long id) {
		final RemoteInfo remoteInfo = remoteList.get(position);
		final AlertDialog operateAlertDialog = new AlertDialog.Builder(
				RemoteActivity.this).create();
		View operateView = LayoutInflater.from(RemoteActivity.this).inflate(
				R.layout.dialog_operate, null);
		TextView update_dialog, delete_dialog;
		update_dialog = (TextView) operateView
				.findViewById(R.id.operate_update);
		update_dialog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				operateAlertDialog.cancel();
				addRemote(remoteInfo.type, remoteInfo);
			}
		});

		delete_dialog = (TextView) operateView
				.findViewById(R.id.operate_delete);
		delete_dialog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				operateAlertDialog.cancel();
				remoteList.remove(position);
				adapter.notifyDataSetChanged();
				try {
					Dao<ControlInfo, Integer> cDao = DBManager.getDBhelper()
							.getDao(ControlInfo.class);
					cDao.delete(cDao.queryBuilder().where()
							.eq("remote_id", remoteInfo).query());
					Dao<RemoteInfo, Integer> remoteDao = DBManager
							.getDBhelper().getDao(RemoteInfo.class);
					remoteDao.delete(remoteInfo);
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		});

		operateAlertDialog.setView(operateView);
		operateAlertDialog.setTitle("操作");
		operateAlertDialog.show();
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		int type = remoteList.get(position).type;
		Intent intent;
		switch (type) {
		case RemoteInfo.tv:
			intent = new Intent(RemoteActivity.this, TVControlActivity.class);
			intent.putExtra("remoteId", remoteList.get(position));
			startActivity(intent);
			break;
		case RemoteInfo.diy:
			intent = new Intent(RemoteActivity.this, SpControlActivity.class);
			intent.putExtra("remoteId", remoteList.get(position));
			startActivity(intent);
			break;
		case RemoteInfo.air:
			intent = new Intent(RemoteActivity.this, AirControlActivity.class);
			intent.putExtra("remoteId", remoteList.get(position));
			startActivity(intent);
			break;
		}

	}
}
