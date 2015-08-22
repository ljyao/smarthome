package com.smarthome.adapter;

import java.util.List;

import com.app.smarthome.R;
import com.smarthome.database.ControlInfo;
import com.smarthome.database.RemoteInfo;

import android.content.Context;
import android.database.DataSetObserver;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

public class TVGridAdapter extends BaseAdapter {
	private List<ControlInfo> infos;
	private Context mContext;

	public TVGridAdapter(Context context, List<ControlInfo> Infos) {
		this.infos = Infos;
		mContext = context;
	}

	@Override
	public int getCount() {
		return infos.size();
	}

	@Override
	public Object getItem(int position) {
		return infos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.tv_gridview_item, null);
		}
		TextView textView = (TextView) convertView
				.findViewById(R.id.tv_button);
		textView.setText(infos.get(position).name);
		return convertView;
	}

}
