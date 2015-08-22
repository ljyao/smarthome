package com.smarthome.broadlink.ui;

import com.app.smarthome.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class DeviceTypeActivity extends AppCompatActivity implements
		OnClickListener {
	private CardView infrared, camera;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_type);
		infrared = (CardView) findViewById(R.id.id_type_infraced);
		infrared.setOnClickListener(this);
		camera = (CardView) findViewById(R.id.id_type_camera);
		camera.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.id_type_infraced:
			intent = new Intent(this, InfracedListActivity.class);
			startActivity(intent);
			break;

		case R.id.id_type_camera:
			intent = new Intent(this, CameraListActivity.class);
			startActivity(intent);
			break;
		}
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
}
