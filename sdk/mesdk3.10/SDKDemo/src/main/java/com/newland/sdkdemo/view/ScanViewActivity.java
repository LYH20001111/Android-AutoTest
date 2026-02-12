package com.newland.sdkdemo.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.newland.SettingsManager;
import android.newland.content.NlContext;
import android.newland.os.NlBuild;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newland.sdk.module.scanner.ScanListener;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdkdemo.AppConfig;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.fragment.ScannerFragment;
import com.newland.sdkdemo.utils.SoundPoolImpl;
import com.newland.sdk.module.scanner.LightOperType;
import com.newland.sdk.module.scanner.ScanLightType;
import com.newland.sdk.module.scanner.ScannerExtParams;
import com.newland.sdk.module.scanner.ScannerListener;
import com.newland.sdk.module.scanner.ScannerType;

public class ScanViewActivity extends Activity {
	
	private SurfaceView surfaceView;
	private Context context;
	private ScannerType scanType;
	private static DeviceLogger logger = DeviceLoggerFactory.getLogger(ScanViewActivity.class);
	private ImageView scanIV;
	private SoundPoolImpl spi;
	private RelativeLayout frontLL;
	private LinearLayout switch_fr;
	private LinearLayout switch_bc;
	private boolean isFinish = false;
	private boolean isTimeout = true;
	private AnimationDrawable scanAnim;
	private FrameLayout backFL;
	private static final int Code_PERMISSION=100;
	private TextView picTv,posTv;
	private SettingsManager settingManager;

	private com.newland.sdkdemo.view.ScanViewBack scanViewBack;
	private LinearLayout lyHardScan;


	@SuppressLint({"WrongAppConfigant", "WrongConstant"})
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		context=this;
		View view = View.inflate(this, R.layout.scan_view, null);
		setContentView(view);
		
		
		spi = SoundPoolImpl.getInstance();
		spi.initLoad(this);
		init();
		try {
			settingManager = (SettingsManager) getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
			settingManager.setAppSwitchKeyEnabled(false);
			settingManager.setHomeKeyEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private Handler scanHandler = new Handler(Looper.getMainLooper()) {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 1: {
					startScan();
					break;
				}
				default:
					break;
			}
		}

	};

	private void init() {
		int type =getIntent().getIntExtra("scanType", 0x01);//Front default
		if(type == 0x01){
			scanType = ScannerType.FRONT;
		}else{
			scanType = ScannerType.BACK;
		}
		surfaceView=(SurfaceView) findViewById(R.id.surfaceView);
		frontLL=(RelativeLayout) findViewById(R.id.ll_front);
		switch_fr = (LinearLayout) findViewById(R.id.ll_switch_front);
		switch_bc=(LinearLayout)findViewById(R.id.ll_switch_back);
		backFL=(FrameLayout) findViewById(R.id.fl_back);
		scanIV=(ImageView) findViewById(R.id.iv_scan);

		picTv=(TextView) findViewById(R.id.text_pic);
		posTv=(TextView) findViewById(R.id.text_pos);

		lyHardScan = (LinearLayout) findViewById(R.id.ly_back_hard_scan);
		scanViewBack = (com.newland.sdkdemo.view.ScanViewBack)findViewById(R.id.scanviewback);

		//默认使用900的扫码前置预览界面
		if(NlBuild.VERSION.MODEL.equals("CPOS X5")|| android.os.Build.MODEL.equals("STAR A-6300")){
			scanIV.setImageResource(R.drawable.scan_x5_list);
			picTv.setGravity(Gravity.LEFT);
			posTv.setGravity(Gravity.LEFT);
			picTv.setPadding(200,0,0,0);
			posTv.setPadding(200,0,0,0);
		}else if(NlBuild.VERSION.MODEL.equals("CPOS X3")){
			scanIV.setImageResource(R.drawable.scan_x3_list);
			picTv.setGravity(Gravity.LEFT);
			posTv.setGravity(Gravity.LEFT);
			picTv.setPadding(200,0,0,0);
			posTv.setPadding(200,0,0,0);
		}else if(NlBuild.VERSION.MODEL.equals("N910")){
			scanIV.setImageResource(R.drawable.scan_910_list);
		}else if(NlBuild.VERSION.MODEL.equals("N550")){
			picTv.setTextSize(25);
			posTv.setTextSize(25);
			scanIV.setImageResource(R.drawable.scan_550_list);
		}else if(NlBuild.VERSION.MODEL.equals("N850")){
			scanIV.setImageResource(R.drawable.scan_850_list);
		}else if(NlBuild.VERSION.MODEL.equals("N700")){
			scanIV.setImageResource(R.drawable.scan_700_list);
		}else {
			frontLL.setBackgroundColor(Color.WHITE);
			scanIV.setPadding(100, 0, 100, 0);
			scanIV.setScaleType(ImageView.ScaleType.FIT_CENTER);
			scanIV.setImageResource(R.drawable.scan_default_list);
		}

		switch_fr.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("---------------切换前置---------");
				switch_bc.setEnabled(true);
				switch_fr.setEnabled(false);
				if(Build.MODEL.equals("N700")){
					ScannerFragment.scanner.operateLight(ScanLightType.LED_LIGHT,LightOperType.CLOSE);
					ScannerFragment.scanner.operateLight(ScanLightType.RED_LIGHT,LightOperType.CLOSE);
				}

				ScannerFragment.scanner.stopScan();
				isFinish = true;
				scanType = ScannerType.FRONT;
				startScan();

			}
		});

		switch_bc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				logger.debug("---------------切换后置---------");
				switch_bc.setEnabled(false);
				switch_fr.setEnabled(true);
				if(Build.MODEL.equals("N700")){
					ScannerFragment.scanner.operateLight(ScanLightType.LED_LIGHT,LightOperType.CLOSE);
					ScannerFragment.scanner.operateLight(ScanLightType.RED_LIGHT,LightOperType.CLOSE);
				}
				ScannerFragment.scanner.stopScan();
				isFinish = true;
				scanType = ScannerType.BACK;
				startScan();
			}
		});

		if (Build.VERSION.SDK_INT>22){
			if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
				//先判断有没有权限 ，没有就在这里进行权限的申请
				ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},Code_PERMISSION);
			}else {
				startScan();
			}
		} else {
			startScan();
		}


	}

	private void startScan(){
		try{
			isFinish = false;
			if(scanType==ScannerType.BACK){//后置的
				surfaceView=(SurfaceView) findViewById(R.id.surfaceView);
				surfaceView.setVisibility(View.VISIBLE);
				frontLL.setVisibility(View.GONE);
				backFL.setVisibility(View.VISIBLE);
				boolean resutl = ScannerFragment.scanner.isSupScanCode(ScannerType.FRONT);
				switch_fr.setVisibility(View.GONE);
				if(resutl){
					switch_fr.setVisibility(View.VISIBLE);
				}
				String config = NlBuild.VERSION.NL_HARDWARE_CONFIG;
				if (Build.MODEL.equals("N950") && config != null && NlBuild.VERSION.NL_HARDWARE_CONFIG.length() >= 10 && config.substring(8, 10).equals("15")) {
					lyHardScan.setVisibility(View.VISIBLE);
					scanViewBack.setVisibility(View.GONE);
				}
			}else if(scanType==ScannerType.FRONT){
				surfaceView.setVisibility(View.GONE);
				surfaceView = null;
				backFL.setVisibility(View.GONE);
				frontLL.setVisibility(View.VISIBLE);
				scanAnim = (AnimationDrawable)scanIV.getDrawable();
				if (scanAnim != null && !scanAnim.isRunning()) {
					scanAnim.start();
				}
				boolean resutl = ScannerFragment.scanner.isSupScanCode(ScannerType.BACK);
				switch_bc.setVisibility(View.GONE);
				if(resutl){
					switch_bc.setVisibility(View.VISIBLE);
				}
				if(Build.MODEL.equals("N700")){
					ScannerFragment.scanner.operateLight(ScanLightType.LED_LIGHT,LightOperType.OPEN);
					ScannerFragment.scanner.operateLight(ScanLightType.RED_LIGHT,LightOperType.OPEN);
				}
			}else{
				finish();
				Message scanMsg = new Message();
				scanMsg.what = AppConfig.ScanResult.SCAN_ERROR;
				Bundle scanBundle = new Bundle();
				scanBundle.putInt("errorCode", 0x00);
				scanBundle.putString("errormessage",context.getString(R.string.content_sacnview_error_message));
				scanMsg.setData(scanBundle);
				ScannerFragment.getScanEventHandler().sendMessage(scanMsg);
			}

			ScannerExtParams scannerExtParams = new ScannerExtParams();
			scannerExtParams.setOnce(true);
			ScannerFragment.scanner.startScanForOversea(this,scanType,surfaceView,30, new ScanListener() {

				@Override
				public void onTimeout() {
					isFinish = true;
					finish();
					Message scanMsg = new Message();
					scanMsg.what = AppConfig.ScanResult.SCAN_TIMEOUT;
					ScannerFragment.getScanEventHandler().sendMessage(scanMsg);
				}

				@Override
				public void onResponse(byte[][] barcodes) {
					logger.debug("---------------onResponse---------"+barcodes[0]);
					spi.play();
					if(barcodes!=null){

						for (int i = 0; i < barcodes.length; i++) {
							for (int j = 0; j < barcodes[i].length; j++) {
								System.out.print(barcodes[i][j]);
							}
							System.out.println();
						}
					}
					Message scanMsg = new Message();
					scanMsg.what = AppConfig.ScanResult.SCAN_RESPONSE;
					Bundle scanBundle = new Bundle();

					scanBundle.putStringArray("barcodes", new String[]{});
					scanMsg.setData(scanBundle);
					ScannerFragment.getScanEventHandler().sendMessage(scanMsg);

				}

				@Override
				public void onFinish() {
					logger.debug("---------------onFinish---------"+isFinish);
					isFinish = true;
					Message scanMsg = new Message();
					scanMsg.what = AppConfig.ScanResult.SCAN_FINISH;
					ScannerFragment.getScanEventHandler().sendMessage(scanMsg);
					finish();
				}

				@Override
				public void onError(int i, String s) {
					logger.debug("-----onError--errorCode:"+i+";message:"+s);
					isFinish = true;
					Message scanMsg = new Message();
					scanMsg.what = AppConfig.ScanResult.SCAN_ERROR;
					Bundle scanBundle = new Bundle();
					scanBundle.putInt("errorCode", i);
					scanBundle.putString("errormessage", s);
					scanMsg.setData(scanBundle);
					ScannerFragment.getScanEventHandler().sendMessage(scanMsg);
					finish();
				}

				@Override
				public void onCancel() {
					isFinish = true;
					Message scanMsg = new Message();
					scanMsg.what = AppConfig.ScanResult.SCAN_CANCEL;
					ScannerFragment.getScanEventHandler().sendMessage(scanMsg);
				}
			},scannerExtParams);

		}catch(Exception e){
			e.printStackTrace();
			isFinish = true;
			logger.debug("---------------Exception---------"+e.getMessage());
			finish();
			e.getStackTrace();
			Message scanMsg = new Message();
			scanMsg.what = AppConfig.ScanResult.SCAN_ERROR;
			Bundle scanBundle = new Bundle();
			scanBundle.putInt("errorCode", 0);
			scanBundle.putString("errormessage", e.getMessage());
			scanMsg.setData(scanBundle);
			ScannerFragment.getScanEventHandler().sendMessage(scanMsg);

		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		logger.debug("------onPause--------" + isTimeout + "isFinish：" + isFinish);
		if(!isFinish){
			ScannerFragment.scanner.stopScan();
		}
		if (scanAnim != null && scanAnim.isRunning()) {
			scanAnim.stop();
		}
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		logger.debug("---------------keyCode---------"+keyCode);//700S设备 左边是24 右边是25
		logger.debug("---------------event---------"+event);
//		if((keyCode==KeyEvent.KEYCODE_VOLUME_UP&& event.getRepeatCount() == 0)){
//			logger.debug("发起700扫码");
//			startScan();
//		}else if((keyCode==KeyEvent.KEYCODE_VOLUME_DOWN&& event.getRepeatCount() == 0)){
//			logger.debug("发起700扫码");
//			startScan();
//		}

		if(keyCode==KeyEvent.KEYCODE_BACK){
			logger.debug("回退键");
			isFinish=false;
			finish();


		}

		return super.onKeyDown(keyCode, event);

	}

	@Override
	protected void onDestroy() {
		logger.debug("---------------onDestroy---------");
		try {
			spi.release();
			settingManager.setAppSwitchKeyEnabled(true);
			settingManager.setHomeKeyEnabled(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onDestroy();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		try{
			if (requestCode == Code_PERMISSION) {
				if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					//权限被用户同意,做相应的事情
					startScan();
				} else {
					//权限被用户拒绝，做相应的事情
					Message scanMsg = new Message();
					scanMsg.what = AppConfig.ScanResult.SCAN_ERROR;
					Bundle scanBundle = new Bundle();
					scanBundle.putInt("errorCode", 0);
					scanBundle.putString("errormessage","摄像头动态授权失败");
					scanMsg.setData(scanBundle);
					ScannerFragment.getScanEventHandler().sendMessage(scanMsg);
					finish();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			finish();

		}

		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

	}
}
