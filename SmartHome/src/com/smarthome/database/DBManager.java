package com.smarthome.database;

import com.app.smarthome.SmartHomeApplication;

import android.content.Context;

/**
 * @author wcy
 * 
 */
public class DBManager {
	private static SmartHomeDBHelper smartHomeDBHelper;

	public static SmartHomeDBHelper getDBhelper() {
	if (smartHomeDBHelper==null) {
		smartHomeDBHelper= new SmartHomeDBHelper(SmartHomeApplication.getInstance());
	}
	return smartHomeDBHelper;
}
}
