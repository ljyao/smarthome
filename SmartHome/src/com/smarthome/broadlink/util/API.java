package com.smarthome.broadlink.util;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import android.R.interpolator;
import android.content.Context;
import android.os.Handler;
import cn.com.broadlink.networkapi.NetworkAPI;

import com.app.smarthome.SmartHomeApplication;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.smarthome.broadlink.ui.DeviceListActivity;
import com.smarthome.database.DeviceInfo;
import com.smarthome.database.SmartHomeDBHelper;

/**
 * @author Yann-LJY 所有请求开线程处理，最后post回主线程
 */
public class API {
	/**
	 * 局域网超时时间,单位：ms， <可选字段>
	 */
	static final int ltimeout = 1000;
	/**
	 * 远程超时时间,单位：ms， <可选字段>
	 */
	static final int rtimeout = 3000;
	/**
	 * 发送次数
	 */
	static final int sendcount = 1;
	private NetworkAPI mBlNetwork;
	private String deviceinfo;
	private Context mContext;
	private Handler mHandler;

	public API(NetworkAPI mBlNetwork, String deviceinfo, Context context) {
		this.mBlNetwork = mBlNetwork;
		this.deviceinfo = deviceinfo;
		this.mContext = context;
		mHandler = new Handler();
	}

	/**
	 * @param tasks
	 *            将要添加的任务
	 * @param taskdata
	 *            控制码信息
	 * @return 所有的任务
	 */
	public void dnaControl_taskadd(TaskInfo taskInfo,
			ArrayList<TaskCodeData> taskdata, final SendCallBack sendCallBack) {
		JsonObject in = getBaseJson();
		JsonObject rm_res_t = new JsonObject();
		String data = "";
		for (TaskCodeData task : taskdata) {
			data += task.taskdata;
		}
		// 定时发送的控制码，最多支持5个控制码拼成一个字符串，且字符串总长度不得大于2560字节
		rm_res_t.addProperty("taskdata", data);
		rm_res_t.addProperty("count", taskdata.size());

		JsonObject rm_task_baseinfo_t = new JsonObject();
		JsonObject baseinfo = new JsonObject();
		rm_task_baseinfo_t.addProperty("name", taskInfo.name);
		rm_task_baseinfo_t.addProperty("enable", taskInfo.enable);
		rm_task_baseinfo_t.addProperty("hour", taskInfo.hour);
		rm_task_baseinfo_t.addProperty("index", taskInfo.index);
		rm_task_baseinfo_t.addProperty("minute", taskInfo.minute);
		rm_task_baseinfo_t.addProperty("weeks", taskInfo.weeks);
		baseinfo.add("rm_task_baseinfo_t", rm_task_baseinfo_t);
		rm_res_t.add("baseinfo", baseinfo);

		JsonArray frameJsonArray = new JsonArray();
		for (TaskCodeData task : taskdata) {
			JsonObject jsonObject = new JsonObject();

			JsonObject rm_task_frame_t = new JsonObject();
			rm_task_frame_t.addProperty("delay", task.delay);
			rm_task_frame_t.addProperty("length", task.length);
			jsonObject.add("rm_task_frame_t", rm_task_frame_t);
			frameJsonArray.add(jsonObject);
		}
		rm_res_t.add("frame", frameJsonArray);

		in.add("rm_addtask_req_t", rm_res_t);
		in.addProperty("command", "taskadd");

		sendDnaControl(in.toString(), new APICallBack() {
			@Override
			public void success(final int codeStatus, final String out) {
				final ArrayList<TaskInfo> result = deal_taskResult(out);
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						sendCallBack.result(codeStatus, result);
					}
				});
			}

			@Override
			public void fail(final int codeStatus, String out) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						sendCallBack.result(codeStatus, null);
					}
				});

			}
		});

	}

	public void dnaControl_taskdel(int value, final SendCallBack sendCallBack) {
		JsonObject in = getBaseJson();
		JsonObject rm_res_t = new JsonObject();
		rm_res_t.addProperty("index", value);
		in.add("rm_deltask_req_t", rm_res_t);
		in.addProperty("command", "taskdel");

		sendDnaControl(in.toString(), new APICallBack() {
			@Override
			public void success(final int codeStatus, String out) {
				final ArrayList<TaskInfo> result = deal_taskResult(out);
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						sendCallBack.result(codeStatus, result);
					}
				});
			}

			@Override
			public void fail(final int codeStatus, String out) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						sendCallBack.result(codeStatus, null);
					}
				});

			}
		});

	}

	public void dnaControl_send(String data, final SendCallBack sendCallBack) {
		JsonObject in = getBaseJson();
		JsonObject rm_res_t = new JsonObject();
		rm_res_t.addProperty("data", data);
		in.add("rm_send_req_t", rm_res_t);
		in.addProperty("command", "send");

		sendDnaControl(in.toString(), new APICallBack() {
			@Override
			public void success(final int codeStatus, String out) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						sendCallBack.result(codeStatus, null);
					}
				});

			}

			@Override
			public void fail(final int codeStatus, String out) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						sendCallBack.result(codeStatus, null);
					}
				});

			}
		});
	}

	public void dnaControl_featurelist(final SendCallBack sendCallBack) {
		JsonObject in = getBaseJson();
		in.addProperty("command", "featurelist");

		sendDnaControl(in.toString(), new APICallBack() {
			@Override
			public void success(final int codeStatus, String out) {
				JsonArray sendOut;
				sendOut = new JsonParser().parse(out).getAsJsonObject()
						.get("featurelist").getAsJsonObject().get("list")
						.getAsJsonArray();
				final ArrayList<String> featureList = new ArrayList<String>();
				for (JsonElement jsonElement : sendOut) {
					String dna = jsonElement.getAsString();
					featureList.add(dna);
				}
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						sendCallBack.result(codeStatus, featureList);
					}
				});

			}

			@Override
			public void fail(final int codeStatus, String out) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						sendCallBack.result(codeStatus, null);
					}
				});

			}
		});
	}

	public void dnaControl_code(final SendCallBack sendCallBack) {
		JsonObject in = getBaseJson();
		JsonObject rm_res_t = new JsonObject();
		in.add("rm_code_req_t", rm_res_t);
		in.addProperty("command", "code");

		sendDnaControl(in.toString(), new APICallBack() {
			@Override
			public void success(final int codeStatus, String out) {
				JsonObject sendOut;
				sendOut = new JsonParser().parse(out).getAsJsonObject()
						.getAsJsonObject().get("response").getAsJsonObject()
						.get("rm_code_res_t").getAsJsonObject();
				final String data = sendOut.get("data").getAsString();
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						sendCallBack.result(codeStatus, data);
					}
				});

			}

			@Override
			public void fail(final int codeStatus, String out) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						sendCallBack.result(codeStatus, null);
					}
				});

			}
		});
	}

	public void dnaControl_tasklist(final SendCallBack sendCallBack) {
		JsonObject in = getBaseJson();
		JsonObject rm_res_t = new JsonObject();
		in.add("rm_tasklist_req_t", rm_res_t);
		in.addProperty("command", "tasklist");

		sendDnaControl(in.toString(), new APICallBack() {
			@Override
			public void success(final int codeStatus, String out) {
				final ArrayList<TaskInfo> result = deal_taskResult(out);
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						sendCallBack.result(codeStatus, result);
					}
				});
			}

			@Override
			public void fail(final int codeStatus, String out) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						sendCallBack.result(codeStatus, null);
					}
				});

			}
		});

	}

	public void dnaControl_description(final SendCallBack sendCallBack) {
		JsonObject in = getBaseJson();
		in.addProperty("command", "description");

		sendDnaControl(in.toString(), new APICallBack() {
			@Override
			public void success(final int codeStatus, String out) {
				JsonObject sendOut;
				sendOut = new JsonParser().parse(out).getAsJsonObject()
						.get("response").getAsJsonObject().get("description")
						.getAsJsonObject();
				final DescriptionInfo descriptionInfo = new DescriptionInfo();
				descriptionInfo.category = sendOut.get("category")
						.getAsString();
				descriptionInfo.company = sendOut.get("company").getAsString();
				descriptionInfo.model = sendOut.get("model").getAsString();
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						sendCallBack.result(codeStatus, descriptionInfo);
					}
				});

			}

			@Override
			public void fail(final int codeStatus, String out) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						sendCallBack.result(codeStatus, null);
					}
				});

			}
		});

	}

	public void dnaControl_refresh(final SendCallBack sendCallBack) {
		JsonObject in = getBaseJson();
		JsonObject rm_res_t = new JsonObject();
		in.add("rm_refresh_req_t", rm_res_t);
		in.addProperty("command", "refresh");

		sendDnaControl(in.toString(), new APICallBack() {
			@Override
			public void success(final int codeStatus, String out) {
				JsonObject sendOut, info, status;
				sendOut = new JsonParser().parse(out).getAsJsonObject()
						.get("response").getAsJsonObject()
						.get("rm_refresh_res_t").getAsJsonObject();

				final RefreshInfo refreshInfo = new RefreshInfo();
				info = sendOut.get("info").getAsJsonObject().get("rm_info_t")
						.getAsJsonObject();
				refreshInfo.lock = info.get("lock").getAsInt();
				refreshInfo.name = info.get("name").getAsString();
				status = sendOut.get("status").getAsJsonObject()
						.get("rm_status_t").getAsJsonObject();
				refreshInfo.temp_decimal = status.get("temp_decimal")
						.getAsInt();
				refreshInfo.temp_integer = status.get("temp_integer")
						.getAsInt();
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						sendCallBack.result(codeStatus, refreshInfo);
					}
				});

			}

			@Override
			public void fail(final int codeStatus, String out) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						sendCallBack.result(codeStatus, null);
					}
				});

			}
		});

	}

	public void dnaControl_study(final SendCallBack sendCallBack) {
		JsonObject in = getBaseJson();
		JsonObject rm_res_t = new JsonObject();
		in.add("rm_study_req_t", rm_res_t);
		in.addProperty("command", "study");
		sendDnaControl(in.toString(), new APICallBack() {
			@Override
			public void success(final int status, String out) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						sendCallBack.result(status, null);
					}
				});

			}

			@Override
			public void fail(final int status, String out) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						sendCallBack.result(status, null);
					}
				});

			}
		});

	}

	public void sendDnaControl(final String dnacontrolstring,
			final APICallBack apiCallBack) {
		new Thread(new Runnable() {
			public void run() {
				String out = mBlNetwork
						.dnaControl(deviceinfo, dnacontrolstring);
				System.out.println(out);
				int code = new JsonParser().parse(out).getAsJsonObject()
						.get("code").getAsInt();
				if (code == -7) {
					devicePair(SmartHomeApplication.mdeviceinfo);
				}
				if (code == 0) {
					apiCallBack.success(code, out);
				} else {
					apiCallBack.fail(code, out);
				}
			}
		}).start();
	}

	private ArrayList<TaskInfo> deal_taskResult(String result) {
		JsonObject out;
		out = new JsonParser().parse(result).getAsJsonObject().get("response")
				.getAsJsonObject().get("rm_tasklist_res_t").getAsJsonObject()
				.get("tasklist").getAsJsonObject().get("rm_task_list_t")
				.getAsJsonObject();
		JsonArray baseinfo = out.get("baseinfo").getAsJsonArray();
		int count = out.get("count").getAsInt();
		ArrayList<TaskInfo> tasks = new ArrayList<API.TaskInfo>();
		for (JsonElement jsonElement : baseinfo) {
			JsonObject jsonObject = jsonElement.getAsJsonObject()
					.get("rm_task_baseinfo_t").getAsJsonObject();
			TaskInfo task = new TaskInfo();
			task.enable = jsonObject.get("enable").getAsInt();
			task.hour = jsonObject.get("hour").getAsInt();
			task.index = jsonObject.get("index").getAsInt();
			task.minute = jsonObject.get("minute").getAsInt();
			task.name = jsonObject.get("name").getAsString();
			task.weeks = jsonObject.get("weeks").getAsInt();
			tasks.add(task);
		}
		return tasks;
	}

	public JsonObject getBaseJson() {
		JsonObject in = new JsonObject();
		in.addProperty("ltimeout", ltimeout);
		in.addProperty("rtimeout", rtimeout);
		in.addProperty("sendcount", sendcount);
		in.addProperty("parsefile", SmartHomeApplication.filepath
				+ File.separator + SmartHomeApplication.mdeviceinfo.getType()
				+ ".bl");
		in.addProperty("patternfile", SmartHomeApplication.filepath
				+ File.separator + SmartHomeApplication.mdeviceinfo.getType()
				+ ".pat");
		return in;
	}

	public static class RefreshInfo {
		/**
		 * 设备锁定状态, 1锁定，0非锁定
		 */
		public int lock;
		/**
		 * 设备名称
		 */
		public String name;
		/**
		 * 温度的小数部分
		 */
		public int temp_decimal;
		/**
		 * 温度的整数部分
		 */
		public int temp_integer;
	}

	public static class DescriptionInfo {
		/**
		 * 设备型号
		 */
		public String model;
		/**
		 * 设备类别
		 */
		public String category;
		/**
		 * 设备隶属公司
		 */
		public String company;
	}

	public static class TaskInfo {
		/**
		 * 任务名称
		 */
		public String name;
		/**
		 * 定时任务的执行周期，若为0则表示仅执行一次，位操作：bit0:每周日 bit1:每周一 bit2:每周二 … bit6:每周六
		 */
		public int weeks;
		/**
		 * 任务时间小时值
		 */
		public int hour;
		/**
		 * 任务使能
		 */
		public int enable;
		/**
		 * 任务时间分钟值
		 */
		public int minute;
		/**
		 * 任务序号
		 */
		public int index;

	}

	public static class TaskCodeData {
		/**
		 * 控制码
		 */
		public String taskdata;
		/**
		 * 控制码延时发送时间,单位:ms
		 */
		public int delay;
		/**
		 * 对应的控制码的长度
		 */
		public int length;
	}

	private interface APICallBack {
		public void success(int status, String out);

		public void fail(int status, String out);
	}

	public interface SendCallBack {
		public void result(int status, Object reObject);
	}

	public void devicePair(DeviceInfo device) {

		JsonObject devicepairin = new JsonObject();
		devicepairin.addProperty("mac", device.getMac());
		devicepairin.addProperty("type", device.getType());
		devicepairin.addProperty("subdevice", device.getSubdevice());
		devicepairin.addProperty("password", device.getPassword());
		devicepairin.addProperty("lanaddr", device.getLanaddr());

		JsonObject devicepairout = new JsonObject();
		String devicepairJsonstring = devicepairin.toString();
		/*
		 * deviceprobe返回后进行设备配对 使用 devicepair方法进行设备配对
		 */
		String pairOut = mBlNetwork.devicePair(devicepairJsonstring, 1);
		devicepairout = new JsonParser().parse(pairOut).getAsJsonObject();
		if (devicepairout.get("code").getAsInt() == 0) {
			/* 配对成功后，置id和key，后面控制设备的时候会用到key和id */
			device.setId(devicepairout.get("id").getAsInt());
			device.setKey(devicepairout.get("key").getAsString());
			// save
			SmartHomeDBHelper smartHomeDBHelper = new SmartHomeDBHelper(
					mContext);
			try {
				Dao<DeviceInfo, String> devicerDao = smartHomeDBHelper
						.getDao(DeviceInfo.class);
				DeviceInfo preDevicerinfo = devicerDao.queryForId(device.mac);
				if (preDevicerinfo != null) {
					device.name = preDevicerinfo.name;
				}
				devicerDao.createOrUpdate(device);

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
}
