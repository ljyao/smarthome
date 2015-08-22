package com.app.smarthome;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import cn.com.broadlink.networkapi.NetworkAPI;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smarthome.camera.CameraSDK;
import com.smarthome.camera.HC_DVRManager;
import com.smarthome.database.DeviceInfo;

public class SmartHomeApplication extends Application {

	public static NetworkAPI mBlNetwork;
	public static SmartHomeApplication context;
	public Context mcontent = this;
	public static String api_id = "api_id";
	public static String command = "command";
	public static String CODE = "code";
	public static String userlicense = "KYlRY2ipkl7D4J9bbmw5c+Jv+0T2z6SIhDMmj1gVGZOcueP69NQZGh4k4TYn5dPMqeY2dQWuZklzYvHWWwOFgG08wAV8tCp2g0phBWj1UFETSeAsb+c=";
	public static String typelicense = "74/tBaGgMZNpogcvqCNJXcNxYmrGhicfaFd3BWCuyhGbxT6+5/4viywBtXEVeh6Q";
	public static String filepath;
	// 缓存的最新设备信息，不同的activity之间共享
	public static DeviceInfo mdeviceinfo;

	@Override
	public void onCreate() {
		super.onCreate();

		init();

		context = this;
	}

	/*
	 * 1.获取NetworkApi的实例 2.建立app对应的文件夹，其中有filepath用于存放从云端下载下来的文件
	 */
	private void init() {
		// TODO Auto-generated method stub
		mBlNetwork = new NetworkAPI(mcontent);
		
		filepath = getFilesDir().getPath();

		new Thread(new Runnable() {
			@Override
			public void run() {

				File filebl = new File(filepath, "10002.bl");
				File filepat = new File(filepath, "10002.pat");
				if (!filebl.exists() || !filepat.exists()) {
					try {
						InputStream isBl = getResources().openRawResource(
								R.raw.bl);// 通过raw得到数据资源
						FileOutputStream fsBl = new FileOutputStream(filebl);
						byte[] bufferBl = new byte[1024];
						int countBl = 0;// 循环写出
						while ((countBl = isBl.read(bufferBl)) > 0) {
							fsBl.write(bufferBl, 0, countBl);
						}
						fsBl.close();// 关闭流
						isBl.close();

						InputStream isPat = getResources().openRawResource(
								R.raw.pat);// 通过raw得到数据资源
						FileOutputStream fsPat = new FileOutputStream(filepat);
						byte[] bufferPat = new byte[1024];
						int countPat = 0;// 循环写出
						while ((countPat = isPat.read(bufferPat)) > 0) {
							fsPat.write(bufferPat, 0, countPat);
						}
						fsPat.close();// 关闭流
						isPat.close();
					} catch (Exception e) {
					}
				}

				Sdkinit();
			}
		}).start();

	}

	/*
	 * Init Network Lib sdk初始化
	 */
	private void Sdkinit() {

		JsonObject initJsonObjectIn = new JsonObject();
		JsonObject initJsonObjectOut = new JsonObject();
		String initOut;

		initJsonObjectIn.addProperty("typelicense", typelicense);
		initJsonObjectIn.addProperty("userlicense", userlicense);
		initJsonObjectIn.addProperty("filepath", filepath);
		String string = initJsonObjectIn.toString();

		initOut = mBlNetwork.SDKInit(string);
		initJsonObjectOut = new JsonParser().parse(initOut).getAsJsonObject();

		if (initJsonObjectOut.get("code").getAsInt() != 0) {
			Log.i("Sdkinit failed", initJsonObjectOut.get("msg").getAsString());
		}

		// 海康摄像头初始化
		HC_DVRManager.getInstance().initSDK();
	}

	public static SmartHomeApplication getInstance() {
		return context;
	}
}
