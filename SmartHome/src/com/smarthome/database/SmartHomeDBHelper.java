package com.smarthome.database;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class SmartHomeDBHelper extends OrmLiteSqliteOpenHelper {
	private static final String DATABASE_NAME = "smarthome";
	public static final int DATABASE_VERSION = 1;

	public SmartHomeDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		try {
			TableUtils.createTableIfNotExists(arg1, DeviceInfo.class);
			TableUtils.createTableIfNotExists(arg1, RemoteInfo.class);
			TableUtils.createTableIfNotExists(arg1, ControlInfo.class);
			TableUtils.createTableIfNotExists(arg1, CameraDeviceInfo.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1, int arg2,
			int arg3) {
		onCreate(arg0, arg1);

	}

}