package com.smarthome.adapter;

import java.util.List;

import com.app.smarthome.R;
import com.smarthome.database.DeviceInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class DeviceListAdapter extends BaseAdapter {

	private List<DeviceInfo> mDeviceList;
	private LayoutInflater mInflater;
	private ListView listView;

	public DeviceListAdapter(List<DeviceInfo> deviceList, Context context,
			ListView view) {
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
			convertView = mInflater.inflate(R.layout.device_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
			viewHolder.mac = (TextView) convertView.findViewById(R.id.tv_mac);
			viewHolder.status = (ImageView) convertView
					.findViewById(R.id.tv_status);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.name.setText(mDeviceList.get(position).getName());
		viewHolder.mac.setText(mDeviceList.get(position).getMac());
		if (mDeviceList.get(position).status) {
			viewHolder.status.setImageResource(R.drawable.s_local);
		} else {
			viewHolder.status.setImageResource(R.drawable.s_off_line);
		}

		return convertView;
	}

	private static class ViewHolder {
		TextView name;
		TextView mac;
		ImageView status;
	}

}
