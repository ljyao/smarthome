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
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class RemoteGridAdapter extends BaseAdapter {
	private List<RemoteInfo> infos;
	private Context mContext;

	public RemoteGridAdapter(Context context, List<RemoteInfo> Infos) {
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
					R.layout.retome_item, null);
		}
		TextView textView = (TextView) convertView
				.findViewById(R.id.remotename);
		textView.setText(infos.get(position).name);
		ImageView imageView = (ImageView) convertView
				.findViewById(R.id.re_item_image);
		switch (infos.get(position).type) {
		case RemoteInfo.air:
			imageView.setImageResource(R.drawable.icon_ac);
			break;
		case RemoteInfo.diy:
			imageView.setImageResource(R.drawable.icon_diy);
			break;
		case RemoteInfo.tv:
			imageView.setImageResource(R.drawable.icon_tv);
			break;
		}

		return convertView;
	}

}
