package com.newland.sdkdemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.widget.TextView;

import com.newland.sdk.utils.ISOUtils;
import com.newland.sdkdemo.AppConfig;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.event.EMVListener;
import com.newland.sdkdemo.event.PinEntryListener;
import com.newland.sdkdemo.utils.PinKeyType;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * offline password keyboard
 */
public class PlainKeyBoardNumberActivity extends Activity {
	
	private static final String TAG = "KeyBoardNumber";
	private TextView txtPassword;
	private StringBuffer buffer;
	private int inputLen = 0;
	private PlainPinKeyBoard pkb;
	private String accNo;
	private StringBuffer pin=new StringBuffer();;
	private InTimer intimer;
	int timeOut=60;
	private PinEntryListener pinEntryListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.input_pin_fragment_offline);
		intimer = new InTimer();
		pinEntryListener = AppConfig.getPinEntryListener();
		init();
	}
	private final class InTimer {

		private final ScheduledExecutorService inTimer = Executors
				.newSingleThreadScheduledExecutor(new DaemonThreadFactory());
		private ScheduledFuture<?> inFuture = null;

		public void onShow(long time) {
			Log.i("keyboard","Timer time: " + time);
			cancel();
			inFuture = inTimer.schedule(new Runnable() {
				@Override
				public void run() {
					finish();
					Log.i(TAG, getString(R.string.log_enter_pwd_timeout));
					if(pinEntryListener!=null){
						pinEntryListener.onFinish(null);
					}
					finish();
					
				}
			}, time, TimeUnit.SECONDS);
		}

		private void cancel() {
			if (inFuture != null) {
				inFuture.cancel(true);
				inFuture = null;
			}
		}

		public void shutdown() {
			cancel();
			inTimer.shutdown();
		}
	}
	
	private void init() {
		txtPassword = (TextView) findViewById(R.id.txt_password);
		pkb = (PlainPinKeyBoard) findViewById(R.id.n900pinkeyboard);
		accNo = getIntent().getStringExtra("accNo");
		intimer.onShow(timeOut);
		pkb.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {

			private boolean first;// To prevent it from entering the onPreDraw() all the time.

			@Override
			public boolean onPreDraw() {
				if (!first) {
					first = true;
					pkb.setKeyboardClick(new PlainPinKeyBoard.KeyBoardClick() {
						@Override
						public void getKeyValue(PinKeyType pinKeyType, String presskey) {
							Log.i("keyboard","------presskey:---------"+ presskey);
							if(pinKeyType==PinKeyType.DELETE){
								inputLen = (inputLen <= 0 ? 0: inputLen - 1);
								if(pin.length()>0){
									pin.delete(pin.length()-1, pin.length());
								}
								Log.i(TAG, "input pin:" + pin.toString());
								Message msg = mHandler.obtainMessage(2);
								msg.obj = inputLen;
								msg.sendToTarget();
								
							}else if(pinKeyType== PinKeyType.CANCEL){
								finish();
								if(pinEntryListener!=null){
									pinEntryListener.onFinish(null);
								}
							}else if(pinKeyType==PinKeyType.NUMBER){
								inputLen = inputLen + 1;
								pin.append(presskey);
								Message msg = mHandler.obtainMessage(2);
								msg.obj = inputLen;
								msg.sendToTarget();
								
							}else if(pinKeyType==PinKeyType.CONFIRM){
								if(inputLen==0){
									Intent i = new Intent();
									i.putExtra("pin", new byte[] {});
									setResult(RESULT_OK, i);
									if(pinEntryListener!=null){
										pinEntryListener.onFinish(new byte[]{});
									}
								}else{
									Log.i(TAG, "input success:" + pin.toString());
									byte[] pinBlock=ISOUtils.hex2byte(pin.toString());
									Intent i = new Intent();
									i.putExtra("pin", pinBlock);
									setResult(RESULT_OK, i);
									if(pinEntryListener!=null){
										pinEntryListener.onFinish(pinBlock);
									}
								}
								finish();

							}
							
						}
					});
					
				}
				return first;
			}
		});

	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 2: // input password from cipher keyBoard
				int len = (Integer) msg.obj;
				buffer = new StringBuffer();
				for (int i = 0; i < len; i++) {
					buffer.append(" * ");
				}
				txtPassword.setText(buffer.toString());
				break;
			default:
				break;
			}
		}
	};
	private static final class DaemonThreadFactory implements ThreadFactory {
		public Thread newThread(Runnable runnable) {
			Thread thread = new Thread(runnable);
			thread.setDaemon(true);
			return thread;
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != intimer)
			intimer.shutdown();
	}
}
