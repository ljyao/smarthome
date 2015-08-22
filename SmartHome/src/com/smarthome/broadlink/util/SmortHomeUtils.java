package com.smarthome.broadlink.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;
import android.view.View.MeasureSpec;

public class SmortHomeUtils {
	public static int[] weeksTobit(int week) {
		int weeks[] = new int[7];
		for (int i = 0; i < 7; i++) {
			int bit = week & (week << i);
			weeks[i] = bit;
		}
		return weeks;
	}

	public static int bitToweeks(int weeks[]) {
		String data = "";
		for (int i : weeks) {
			data += i + "";
		}
		int week = Integer.valueOf(data, 2);
		return week;
	}

	public static Bitmap viewToBitmap(Context context, View view) {

		view.setDrawingCacheEnabled(true);
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		return view.getDrawingCache();
	}

	/**
	 * 获取截图文件夹
	 * 
	 * @return /sdcard/smorthome/Screenshots/
	 */
	public static String getPictrueDir() {
		String fileDir;
		if (hasSDCard()) {
			fileDir = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/smorthome/Screenshots/";
			File file = new File(fileDir);
			if (!file.exists()) {
				file.mkdirs();
			}
		} else {
			fileDir = "";
		}
		return fileDir;
	}

	/**
	 * 获取视频文件夹
	 * 
	 * @return /sdcard/smorthome/video/
	 */
	public static String getVideoDir() {
		String fileDir;
		if (hasSDCard()) {
			fileDir = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/smorthome/video/";
			File file = new File(fileDir);
			if (!file.exists()) {
				file.mkdirs();
			}
		} else {
			fileDir = "";
		}
		return fileDir;
	}

	/**
	 * 检测SD卡是否可用
	 * 
	 * @return
	 */
	public static boolean hasSDCard() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		} else {
			return false;
		}
	}
	public static String CreatName() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		str += (int) (Math.random() * 1000) + "";
		return str;
	}
}
