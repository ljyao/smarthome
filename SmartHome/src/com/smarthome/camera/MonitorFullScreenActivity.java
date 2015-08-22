package com.smarthome.camera;

import com.app.smarthome.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MonitorFullScreenActivity extends Activity implements
		OnTouchListener {

	private TextView tv_Loading;
	private RelativeLayout rl_MonitorCtrl;
	private SurfaceView sf_VideoMonitor;

	private Button btn_UpLeft, btn_Up, btn_UpRight;
	private Button btn_Left, btn_Auto, btn_Right;
	private Button btn_DownLeft, btn_Down, btn_DownRight;
	private Button btn_ZoomIn, btn_ZoomOut;
	private Button btn_FocusNear, btn_FocusFar;

	private final StartRenderingReceiver receiver = new StartRenderingReceiver();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor_fullscreen);
		IntentFilter filter = new IntentFilter();
		filter.addAction(HC_DVRManager.ACTION_START_RENDERING);
		filter.addAction(HC_DVRManager.ACTION_DVR_OUTLINE);
		registerReceiver(receiver, filter);
		init();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
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

	@Override
	public boolean onTouch(final View v, final MotionEvent event) {
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
	private void init() {
		rl_MonitorCtrl = (RelativeLayout) findViewById(R.id.in_MonitorCtrl);
		tv_Loading = (TextView) findViewById(R.id.tv_Loading);
		sf_VideoMonitor = (SurfaceView) findViewById(R.id.sf_VideoMonitor_FullScreen);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		LayoutParams lp = sf_VideoMonitor.getLayoutParams();
		lp.width = dm.widthPixels - 200;
		lp.height = lp.width / 16 * 9;
		sf_VideoMonitor.setLayoutParams(lp);
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
				new Thread() {
					@Override
					public void run() {
						HC_DVRManager.getInstance().setSurfaceHolder(
								sf_VideoMonitor.getHolder());
						HC_DVRManager.getInstance().realPlay();
					}
				}.start();
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
			private boolean isMoving;

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

	private class StartRenderingReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (HC_DVRManager.ACTION_START_RENDERING.equals(intent.getAction())) {
				tv_Loading.setVisibility(View.GONE);
				rl_MonitorCtrl.setVisibility(View.VISIBLE);
			}
			if (HC_DVRManager.ACTION_DVR_OUTLINE.equals(intent.getAction())) {
				tv_Loading.setText(getString(R.string.tv_connect_cam_error));
			}
		}

	}
}
