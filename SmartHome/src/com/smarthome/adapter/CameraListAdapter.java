package com.smarthome.adapter;

import java.util.List;

import com.app.smarthome.R;
import com.smarthome.database.CameraDeviceInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CameraListAdapter extends BaseAdapter {

	private List<CameraDeviceInfo> mDeviceList;
	private LayoutInflater mInflater;
	private ListView listView;

	public CameraListAdapter(List<CameraDeviceInfo> deviceList,
			Context context, ListView view) {
		mDeviceList = deviceList;
		mInflater = LayoutInflater.from(context);
		listView = view;
	}

	@Override
	public int getCount() {
		if (mDeviceList.size() == 0) {
			listView.setBackgroundResource(R.drawable.ic_no_device);
		} else {
			listView.setBackgroundResource(0);
		}
		return mDeviceList.size();
	}

	@Override
	public Object getItem(int position) {

		return mDeviceList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.camera_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.cv_name);
			viewHolder.ip = (TextView) convertView.findViewById(R.id.cv_ip);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.name.setText(mDeviceList.get(position).name);
		viewHolder.ip.setText(mDeviceList.get(position).serverip + ":"
				+ mDeviceList.get(position).serverport);

		return convertView;
	}

	private static class ViewHolder {
		TextView name;
		TextView ip;
	}
}
