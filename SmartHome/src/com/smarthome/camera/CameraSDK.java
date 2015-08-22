package com.smarthome.camera;

import org.MediaPlayer.PlayM4.Player;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_CLIENTINFO;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.RealPlayCallBack;
import com.smarthome.database.CameraDeviceInfo;

import android.view.SurfaceHolder;
import android.widget.VideoView;

public class CameraSDK {
	// sdk定义
	private Player myPlayer = null;
	public HCNetSDK videoCtr;
	private int playPort;
	public int playFlag;
	private SurfaceHolder videoHolder;
	private int userid;
	private NET_DVR_CLIENTINFO clientInfo;

	public CameraSDK() {
		init();
	}

	public void init() {

		// ******************************************************************
		// sdk初始化
		videoCtr = new HCNetSDK(); // 实例化网络库SDK
		videoCtr.NET_DVR_Init(); // 初始化网络库SDK
		// 播放库
		myPlayer = Player.getInstance(); // 实例话播放库对象
		playPort = myPlayer.getPort(); // 获取播放端口
		// 播放参数
		clientInfo = new NET_DVR_CLIENTINFO();
		clientInfo.lChannel = 1; // 需要打开的通道（可以参考通道开始序号和通道个数，一般从1开始）
		clientInfo.lLinkMode = 0x80000000; // 子码流（保证图像连续性），tcp连接方式，如果要保证图像清晰度，可选用主码流
		clientInfo.sMultiCastIP = null;
	}

	public NET_DVR_DEVICEINFO_V30 login(CameraDeviceInfo cameraInfo) {
		// ******************************************************************
		// 连接服务器，登录

		// 登录服务器
		NET_DVR_DEVICEINFO_V30 cameraDeviceInfo = new NET_DVR_DEVICEINFO_V30();
		userid = videoCtr.NET_DVR_Login_V30(cameraInfo.serverip,
				cameraInfo.serverport, cameraInfo.username, cameraInfo.userpwd,
				cameraDeviceInfo);
		System.out.println("下面是设备信息************************");
		System.out.println("通道开始=" + cameraDeviceInfo.byStartChan);
		System.out.println("通道个数=" + cameraDeviceInfo.byChanNum);
		System.out.println("设备类型=" + cameraDeviceInfo.byDVRType);
		System.out.println("ip通道个数=" + cameraDeviceInfo.byIPChanNum);

		byte[] snByte = cameraDeviceInfo.sSerialNumber;
		String sNo = "";
		for (int i = 0; i < snByte.length; i++) {
			sNo += String.valueOf(snByte[i]);
		}
		System.out.println("设备序列号=" + sNo);
		return cameraDeviceInfo;
	}

	public void play(VideoView videoView) {

		// /关于播放库SDK的使用海康威视官网有详细说明
		videoHolder = videoView.getHolder(); // 获取视频显示窗口（SurfaceView对象）的Holder
		playFlag = videoCtr.NET_DVR_RealPlay_V30(userid, clientInfo,
				mRealDataCallback, false); // mRealDataCallback即为数据回传回掉函数
	}

	public void PTZControl() {
		// videoCtr.NET_DVR_PTZControl(playFlag, arg2)
	}

	private RealPlayCallBack mRealDataCallback = new RealPlayCallBack() {
		@Override
		public void fRealDataCallBack(int arg0, int dataType,
				byte[] paramArrayOfByte, int byteLen) {
			// TODO Auto-generated method stub
			// 回掉函数

			switch (dataType) {
			case 1: // 头数据

				if (myPlayer.openStream(playPort, paramArrayOfByte, byteLen,
						1024 * 1024)) {
					if (myPlayer.setStreamOpenMode(playPort, 1)) {
						if (myPlayer.play(playPort, videoHolder)) {
							playFlag = 1;
						} else {
							playError(3);
						}
					} else {
						playError(2);
					}
				} else {
					playError(1);
				}

				break;
			case 2:
			case 3:

				if (playFlag == 1
						&& myPlayer.inputData(playPort, paramArrayOfByte,
								byteLen)) {
					playFlag = 1;
				} else {
					playError(4);
					playFlag = 0;
				}

			}
		}
	};

	public void playError(int errorCode) {
	}

}
