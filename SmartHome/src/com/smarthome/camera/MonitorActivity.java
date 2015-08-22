package com.smarthome.camera;

import com.app.smarthome.R;
import com.smarthome.broadlink.util.SmortHomeUtils;
import com.smarthome.database.CameraDeviceInfo;

import android.R.integer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Yann-LJY
 * 
 */
public class MonitorActivity extends AppCompatActivity implements
		OnTouchListener, OnClickListener {

	private TextView tv_Loading;
	private RelativeLayout rl_MonitorCtrl;
	private SurfaceView sf_VideoMonitor;

	private Button btn_UpLeft, btn_Up, btn_UpRight;
	private Button btn_Left, btn_Auto, btn_Right;
	private Button btn_DownLeft, btn_Down, btn_DownRight;
	private Button btn_ZoomIn, btn_ZoomOut;
	private Button btn_FocusNear, btn_FocusFar;
	private ImageView btn_screenShort, btn_video;
	private final StartRenderingReceiver receiver = new StartRenderingReceiver();
	/**
	 * 返回标记
	 */
	private boolean backflag;
	/**
	 * 正在移动标记
	 */
	private boolean isMoving;
	private boolean video_save = false;
	private SeekBar mSeekBar;
	private int seekStatus;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle("摄像头");
		// 设置用于发广播的上下文
		HC_DVRManager.getInstance().setContext(getApplicationContext());
		initView();

		mSeekBar = (SeekBar) findViewById(R.id.seekBar);
		mSeekBar.setProgress(HC_DVRManager.dwSpeed * 14);
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				seekStatus = seekBar.getProgress();
				if (seekStatus >= 0 && seekStatus < 14) {
					HC_DVRManager.dwSpeed = 1;
				} else if (seekStatus >= 14 && seekStatus < 28) {
					HC_DVRManager.dwSpeed = 2;
				} else if (seekStatus >= 28 && seekStatus < 42) {
					HC_DVRManager.dwSpeed = 3;
				} else if (seekStatus >= 42 && seekStatus < 56) {
					HC_DVRManager.dwSpeed = 4;
				} else if (seekStatus >= 56 && seekStatus < 60) {
					HC_DVRManager.dwSpeed = 5;
				} else if (seekStatus >= 60 && seekStatus < 74) {
					HC_DVRManager.dwSpeed = 6;
				} else if (seekStatus >= 74 && seekStatus < 100) {
					HC_DVRManager.dwSpeed = 7;
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
			}
		});
	}

	private DeviceBean getDeviceBean() {
		Intent intent = getIntent();
		CameraDeviceInfo cameraDeviceInfo = (CameraDeviceInfo) intent
				.getSerializableExtra("cameraInfo");
		DeviceBean bean = new DeviceBean();
		bean.setIP(cameraDeviceInfo.serverip);
		bean.setPort(cameraDeviceInfo.serverport + "");
		bean.setUserName(cameraDeviceInfo.username);
		bean.setPassWord(cameraDeviceInfo.userpwd);
		bean.setChannel("0");
		return bean;
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction(HC_DVRManager.ACTION_START_RENDERING);
		filter.addAction(HC_DVRManager.ACTION_DVR_OUTLINE);
		registerReceiver(receiver, filter);

		tv_Loading.setVisibility(View.VISIBLE);
		tv_Loading.setText(getString(R.string.tv_connect_cam));
		rl_MonitorCtrl.setVisibility(View.INVISIBLE);
		if (backflag) {
			backflag = false;
			new Thread() {
				@Override
				public void run() {
					HC_DVRManager.getInstance().setSurfaceHolder(
							sf_VideoMonitor.getHolder());
					HC_DVRManager.getInstance().realPlay();
				}
			}.start();
		} else {
			new Thread() {
				@Override
				public void run() {
					HC_DVRManager.getInstance().setDeviceBean(getDeviceBean());
					HC_DVRManager.getInstance().setSurfaceHolder(
							sf_VideoMonitor.getHolder());

					HC_DVRManager.getInstance().loginDevice();
					HC_DVRManager.getInstance().realPlay();
				}
			}.start();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
		HC_DVRManager.getInstance().stopPlay();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		new Thread() {
			@Override
			public void run() {
				HC_DVRManager.getInstance().logoutDevice();
				// HC_DVRManager.getInstance().freeSDK();
			}
		}.start();
	}

	@Override
	public boolean onTouch(final View v, final MotionEvent event) {
		if (isMoving) {
			return true;
		}
		new Thread() {
			@Override
			public void run() {
				switch (v.getId()) {
				case R.id.btn_UpLeft:
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						HC_DVRManager.getInstance().startMove(7);
					}
					if (event.getAction() == MotionEvent.ACTION_UP) {
						HC_DVRManager.getInstance().stopMove(7);
					}
					break;
				case R.id.btn_Up:
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						HC_DVRManager.getInstance().startMove(8);
					}
					if (event.getAction() == MotionEvent.ACTION_UP) {
						HC_DVRManager.getInstance().stopMove(8);
					}
					break;
				case R.id.btn_UpRight:
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						HC_DVRManager.getInstance().startMove(9);
					}
					if (event.getAction() == MotionEvent.ACTION_UP) {
						HC_DVRManager.getInstance().stopMove(9);
					}
					break;
				case R.id.btn_Left:
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						HC_DVRManager.getInstance().startMove(4);
					}
					if (event.getAction() == MotionEvent.ACTION_UP) {
						HC_DVRManager.getInstance().stopMove(4);
					}
					break;
				case R.id.btn_Right:
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						HC_DVRManager.getInstance().startMove(6);
					}
					if (event.getAction() == MotionEvent.ACTION_UP) {
						HC_DVRManager.getInstance().stopMove(6);
					}
					break;
				case R.id.btn_DownLeft:
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						HC_DVRManager.getInstance().startMove(1);
					}
					if (event.getAction() == MotionEvent.ACTION_UP) {
						HC_DVRManager.getInstance().stopMove(1);
					}
					break;
				case R.id.btn_Down:
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						HC_DVRManager.getInstance().startMove(2);
					}
					if (event.getAction() == MotionEvent.ACTION_UP) {
						HC_DVRManager.getInstance().stopMove(2);
					}
					break;
				case R.id.btn_DownRight:
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						HC_DVRManager.getInstance().startMove(3);
					}
					if (event.getAction() == MotionEvent.ACTION_UP) {
						HC_DVRManager.getInstance().stopMove(3);
					}
					break;
				case R.id.btn_ZoomIn:
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						HC_DVRManager.getInstance().startZoom(1);
					}
					if (event.getAction() == MotionEvent.ACTION_UP) {
						HC_DVRManager.getInstance().stopZoom(1);
					}
					break;
				case R.id.btn_ZoomOut:
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						HC_DVRManager.getInstance().startZoom(-1);
					}
					if (event.getAction() == MotionEvent.ACTION_UP) {
						HC_DVRManager.getInstance().stopZoom(-1);
					}
					break;
				case R.id.btn_FocusNear:
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						HC_DVRManager.getInstance().startFocus(-1);
					}
					if (event.getAction() == MotionEvent.ACTION_UP) {
						HC_DVRManager.getInstance().stopFocus(-1);
					}
					break;
				case R.id.btn_FocusFar:
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						HC_DVRManager.getInstance().startFocus(1);
					}
					if (event.getAction() == MotionEvent.ACTION_UP) {
						HC_DVRManager.getInstance().stopFocus(1);
					}
					break;
				default:
					break;
				}
			}
		}.start();
		return false;
	}

	/**
	 * 初始化
	 */
	private void initView() {
		btn_screenShort = (ImageView) findViewById(R.id.id_screeshort);
		btn_screenShort.setOnClickListener(this);
		btn_video = (ImageView) findViewById(R.id.id_video);
		btn_video.setOnClickListener(this);

		rl_MonitorCtrl = (RelativeLayout) findViewById(R.id.in_MonitorCtrl);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		tv_Loading = (TextView) findViewById(R.id.tv_Loading);
		sf_VideoMonitor = (SurfaceView) findViewById(R.id.sf_VideoMonitor);
		LayoutParams lp = sf_VideoMonitor.getLayoutParams();
		lp.width = dm.widthPixels - 30;
		lp.height = lp.width / 16 * 9;
		sf_VideoMonitor.setLayoutParams(lp);
		tv_Loading.setLayoutParams(lp);
		Log.d("DEBUG", "视频窗口尺寸：" + lp.width + "x" + lp.height);

		sf_VideoMonitor.getHolder().addCallback(new Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				Log.d("DEBUG", getLocalClassName() + " surfaceDestroyed");
				sf_VideoMonitor.destroyDrawingCache();
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				Log.d("DEBUG", getLocalClassName() + " surfaceCreated");
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				Log.d("DEBUG", getLocalClassName() + " surfaceChanged");
			}
		});

		btn_UpLeft = (Button) findViewById(R.id.btn_UpLeft);
		btn_UpLeft.setOnTouchListener(this);
		btn_Up = (Button) findViewById(R.id.btn_Up);
		btn_Up.setOnTouchListener(this);
		btn_UpRight = (Button) findViewById(R.id.btn_UpRight);
		btn_UpRight.setOnTouchListener(this);
		btn_Left = (Button) findViewById(R.id.btn_Left);
		btn_Left.setOnTouchListener(this);
		btn_Auto = (Button) findViewById(R.id.btn_Auto);
		btn_Auto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (isMoving) {
					v.setBackgroundResource(R.drawable.btn_auto_normal);
				} else {
					v.setBackgroundResource(R.drawable.btn_auto_pressed);
				}
				new Thread() {
					@Override
					public void run() {
						if (isMoving) {
							HC_DVRManager.getInstance().stopMove(5);
							isMoving = false;
						} else {
							HC_DVRManager.getInstance().startMove(5);
							isMoving = true;
						}
					}
				}.start();
			}
		});
		btn_Right = (Button) findViewById(R.id.btn_Right);
		btn_Right.setOnTouchListener(this);
		btn_DownLeft = (Button) findViewById(R.id.btn_DownLeft);
		btn_DownLeft.setOnTouchListener(this);
		btn_Down = (Button) findViewById(R.id.btn_Down);
		btn_Down.setOnTouchListener(this);
		btn_DownRight = (Button) findViewById(R.id.btn_DownRight);
		btn_DownRight.setOnTouchListener(this);
		btn_ZoomIn = (Button) findViewById(R.id.btn_ZoomIn);
		btn_ZoomIn.setOnTouchListener(this);
		btn_ZoomOut = (Button) findViewById(R.id.btn_ZoomOut);
		btn_ZoomOut.setOnTouchListener(this);
		btn_FocusNear = (Button) findViewById(R.id.btn_FocusNear);
		btn_FocusNear.setOnTouchListener(this);
		btn_FocusFar = (Button) findViewById(R.id.btn_FocusFar);
		btn_FocusFar.setOnTouchListener(this);

	}

	private void setSurfaceViewOnClick() {
		sf_VideoMonitor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread() {
					@Override
					public void run() {
						HC_DVRManager.getInstance().stopPlay();
					}
				}.start();
				startActivity(new Intent(MonitorActivity.this,
						MonitorFullScreenActivity.class));
				backflag = true;
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new Thread() {
				@Override
				public void run() {
					HC_DVRManager.getInstance().stopPlay();
				}
			}.start();
		}
		return super.onKeyDown(keyCode, event);
	}

	private class StartRenderingReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (HC_DVRManager.ACTION_START_RENDERING.equals(intent.getAction())) {
				tv_Loading.setVisibility(View.GONE);
				rl_MonitorCtrl.setVisibility(View.VISIBLE);
				setSurfaceViewOnClick();
			}
			if (HC_DVRManager.ACTION_DVR_OUTLINE.equals(intent.getAction())) {
				tv_Loading.setText(getString(R.string.tv_connect_cam_error));
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		String name = SmortHomeUtils.CreatName();
		switch (v.getId()) {
		case R.id.id_screeshort:
			if (HC_DVRManager.getInstance().sreenShort(name + ".jpg")) {
				Toast.makeText(this,
						"截图成功！已保存到" + SmortHomeUtils.getPictrueDir(),
						Toast.LENGTH_LONG).show();
			}
			break;

		case R.id.id_video:
			if (video_save) {
				if (HC_DVRManager.getInstance().stopSaveVideo()) {
					Toast.makeText(this,
							"录像成功！已保存到" + SmortHomeUtils.getVideoDir(),
							Toast.LENGTH_LONG).show();
				}
				video_save = false;
				btn_video.setImageResource(R.drawable.icon_camera);
			} else {
				video_save = true;
				btn_video.setImageResource(R.drawable.ic_video_stop);
				if (HC_DVRManager.getInstance().saveVideo(name + ".mp4")) {
					Toast.makeText(this, "开始录像", Toast.LENGTH_LONG).show();
				}
			}
			break;
		}

	}
}
