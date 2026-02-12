package com.newland.sdkdemo.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.newland.sdkdemo.R;
import com.newland.sdkdemo.utils.PinKeyType;

/**
 *	PlainPinKey Layout View 
 * @author LinDan
 *
 */
public class PlainPinKeyBoard extends View {
	
	private float width, height;
	private Paint paint;
	private int[] nums;
	private int contentsize;
	private Path path;

	private DisplayMetrics dm;
	// Record the click area
	private RectF press;

	public PlainPinKeyBoard(Context context) {
		super(context);
		getScreenResolution(context);
		init();
	}

	public PlainPinKeyBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
		getScreenResolution(context);
		init();
	}

	private void getScreenResolution(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		Log.i("N900PinKeyBoard", "height=" + dm.heightPixels + ";width"
				+ dm.widthPixels);
	}

	private void init() {
		press = new RectF();
		path = new Path();
		paint = new Paint();
		paint.setAntiAlias(true);
		nums = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawColor(0xfff5f5f9);

		paint.setAlpha(255);
		paint.setColor(0xfff24c4d);
		canvas.drawRect(0, height / 4 * 3 + 1, width / 4 - 1, height, paint);

		paint.setColor(0xfff3e250);
		canvas.drawRect(width / 4 * 3 + 1, 0, width, height / 2, paint);

		paint.setColor(0xff70d145);
		canvas.drawRect(width / 4 * 3 + 1, height / 2, width, height, paint);

		paint.setColor(0xffe1e1e1);
		paint.setStrokeWidth(1f);
		canvas.drawLine(0, height / 4, width / 4 * 3, height / 4, paint);
		canvas.drawLine(0, height / 2, width / 4 * 3, height / 2, paint);
		canvas.drawLine(0, height / 4 * 3, width / 4 * 3, height / 4 * 3, paint);
		canvas.drawLine(width / 4, 0, width / 4, height, paint);
		canvas.drawLine(width / 2, 0, width / 2, height / 4 * 3, paint);
		canvas.drawLine(width / 4 * 3, 0, width / 4 * 3, height, paint);

//		paint.setColor(0x40000000);
//		canvas.drawRect(press, paint);

		paint.setColor(Color.WHITE);
		paint.setTextSize(contentsize);
		drawStringCenter(canvas, width / 8, height / 8 * 7, "cancel");
		drawStringCenter(canvas, width / 8 * 7, height / 4 * 3, "confirm");
		drawdelete(canvas, width / 8 * 7, height / 4, height / 12);

		paint.setColor(Color.BLACK);
		paint.setTextSize(contentsize + 10);
		drawStringCenter(canvas, width / 8, height / 8, nums[0] + "");
		drawStringCenter(canvas, width / 8 * 3, height / 8, nums[1] + "");
		drawStringCenter(canvas, width / 8 * 5, height / 8, nums[2] + "");

		drawStringCenter(canvas, width / 8, height / 8 * 3, nums[3] + "");
		drawStringCenter(canvas, width / 8 * 3, height / 8 * 3, nums[4] + "");
		drawStringCenter(canvas, width / 8 * 5, height / 8 * 3, nums[5] + "");

		drawStringCenter(canvas, width / 8, height / 8 * 5, nums[6] + "");
		drawStringCenter(canvas, width / 8 * 3, height / 8 * 5, nums[7] + "");
		drawStringCenter(canvas, width / 8 * 5, height / 8 * 5, nums[8] + "");
		drawStringCenter(canvas, width / 2, height / 8 * 7, nums[9] + "");
	}

	private void drawStringCenter(Canvas canvas, float centerpointX,
			float centerpointY, String s) {
		paint.setStyle(Style.FILL);
		paint.setStrokeWidth(4);
		FontMetrics fmtemp = paint.getFontMetrics();
		int ctwidth = (int) paint.measureText(s);
		int ctheight = (int) Math.ceil(fmtemp.descent - fmtemp.ascent);
		int ctdescent = (int) fmtemp.descent;
		canvas.drawText(s, centerpointX - ctwidth / 2, centerpointY - ctdescent
				+ ctheight / 2, paint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = width / 5 * 4;
		for (int size = 15; size < 100; size++) {
			paint.setTextSize(size);
			FontMetrics fm = paint.getFontMetrics();
			float cs = fm.descent - fm.ascent;
			float tempw = paint.measureText(getResources().getString(R.string.dialog_ok));
			if (cs > height / 8 || tempw > width / 8) {
				contentsize = size;
				break;
			}
		}
		this.setMeasuredDimension((int) width, (int) height);
	}

	private void drawdelete(Canvas canvas, float centerX, float centerY,
			float sizeheight) {
		float left = centerX - sizeheight;
		float top = centerY - sizeheight / 2;
		path.reset();
		path.moveTo(left, top + sizeheight / 2);
		path.lineTo(left + sizeheight / 2, top);
		path.lineTo(left + 2 * sizeheight, top);
		path.lineTo(left + 2 * sizeheight, top + sizeheight);
		path.lineTo(left + sizeheight / 2, top + sizeheight);
		path.close();

		int gap = 8;
		path.moveTo(left + 2 * sizeheight / 8 * 3 + 5, top + gap);
		path.lineTo(left + 2 * sizeheight / 8 * 7 - 5, top + sizeheight - gap);
		path.moveTo(left + 2 * sizeheight / 8 * 7 - 5, top + gap);
		path.lineTo(left + 2 * sizeheight / 8 * 3 + 5, top + sizeheight - gap);

		// paint.setColor(keyboardcolors[2]);
		paint.setStyle(Style.STROKE);
		canvas.drawPath(path, paint);
	}
	
	private String pressKey;
	private long curtime;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x, y;
		x = event.getX();
		y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			curtime = System.currentTimeMillis();
			this.pressKey = getKey(x, y);
			invalidate();
			return true;
		case MotionEvent.ACTION_UP:
			 if(System.currentTimeMillis()-curtime<200&&this.pressKey.equals(getKey(x,y))){
				 Log.i("keyboard", pressKey+" is click!");
				 keyboardClick.getKeyValue(pinKeyType,pressKey);
//				 Toast.makeText(getContext(), pressKey+" is click!", Toast.LENGTH_SHORT).show();
			 }
			 press.setEmpty();
			 invalidate();
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}
	PinKeyType pinKeyType;
	private String getKey(float x,float y){
		String pressKey = null;
		press.setEmpty();
		if (x < width /4*3 && y < height / 4*3) {
			a: for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (x > i * width / 4 && x <= (i + 1) * width / 4&&y > j * height / 4 && y <= (j + 1) * height / 4) {
						press.left = i * width / 4;
						press.right = (i + 1) * width / 4;
						press.top = j * height / 4;
						press.bottom = (j + 1) * height / 4;
						break a;
					}
				}
			}
			pressKey = ""
					+ nums[(int) (y * 4 / height) * 3
							+ (int) (x * 4 / width)];
			pinKeyType=PinKeyType.NUMBER;
		} else if (x > width / 4 * 3 && x <= width && y > 0
				&& y <= height / 2) {
			press.set(width / 4 * 3, 0, width, height / 2);
			pressKey = "delete";
			pinKeyType=PinKeyType.DELETE;
		} else if (x > 0 && x <= width / 4 && y > height / 4 * 3
				&& y <= height) {
			press.set(0, height / 4 * 3, width / 4, height);
			pressKey = "cancel";
			pinKeyType=PinKeyType.CANCEL;
		} else if (x > width / 4 && x <= width / 4 * 3
				&& y > height / 4 * 3 && y <= height) {
			press.set(width / 4, height / 4 * 3, width / 4 * 3, height);
			pressKey = "" + nums[9];
			pinKeyType=PinKeyType.NUMBER;
		} else if (x > width / 4 * 3 && x <= width && y > height / 2
				&& y <= height) {
			press.set(width / 4 * 3, height / 2, width, height);
			pressKey = "ok";
			pinKeyType=PinKeyType.CONFIRM;
		}
		return pressKey;
	}
	
	
	private KeyBoardClick keyboardClick=null;
	
	public interface KeyBoardClick{
		public void getKeyValue(PinKeyType pinKeyType, String presskey);
	}

	public KeyBoardClick getKeyboardClick() {
		return keyboardClick;
	}

	public void setKeyboardClick(KeyBoardClick keyboardClick) {
		this.keyboardClick = keyboardClick;
	}
	
	

}
