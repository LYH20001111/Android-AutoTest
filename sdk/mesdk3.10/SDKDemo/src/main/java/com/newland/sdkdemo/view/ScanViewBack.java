/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.newland.sdkdemo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.newland.sdkdemo.R;

import java.util.Collection;
import java.util.HashSet;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 *
 */
public final class ScanViewBack extends View {
	/**
	 * Time to refresh the activity
	 */
	private static final long ANIMATION_DELAY = 10L;
	private static final int OPAQUE = 0xFF;
//	private static final int MIN_FRAME_HEIGHT = 500;
//	private static final int MAX_FRAME_WIDTH = 540;

	private static final int MIN_FRAME_WIDTH = 240;
	private static final int MIN_FRAME_HEIGHT = 240;
	private static final int MAX_FRAME_WIDTH = 480;
	private static final int MAX_FRAME_HEIGHT = 360;
	/**
	 * The lengths of the four corners
	 */
	private int ScreenRate;

	/**
	 * The widths of the four corners
	 */
	private static final int CORNER_WIDTH = 3;
	/**
	 *  The width of the center line in the scan box
	 */
	private static final int MIDDLE_LINE_WIDTH = 5;

	/**
	 * The gap between the median line in the scanning frame and the left and right sides of the scanning frame
	 */
	private static int MIDDLE_LINE_PADDING ;//

	/**
	 * The distance that the middle line moves per refresh
	 */
	private static final int SPEEN_DISTANCE = 5;

	/**
	 * The screen density
	 */
	private static float density;
	/**
	 * Text size
	 */
	private static final int TEXT_SIZE = 16;
	/**
	 * The distance between the font and the scan box
	 */
	private static final int TEXT_PADDING_TOP = 50;

	/**
	 * Paint object
	 */
	private Paint paint;

	/**
	 * The top position of the middle slide line
	 */
	private int slideTop;

	/**
	 * The bottom position of the middle slide line
	 */
	private int slideBottom;

	private Bitmap resultBitmap;
	private final int maskColor;
	private final int resultColor;
	private final int resultPointColor;
	private Collection<ResultPoint> possibleResultPoints;
	private Collection<ResultPoint> lastPossibleResultPoints;
	boolean isFirst;
	private Context context;

	public ScanViewBack(Context context){
		super(context);
		this.context=context;
		density = context.getResources().getDisplayMetrics().density;
		//Convert pixels to dp
		ScreenRate = (int)(20 * density);
		paint = new Paint();
		maskColor = Color.parseColor("#60000000");//resources.getColor(R.color.viewfinder_mask);
		resultColor = Color.parseColor("#b0000000");//resources.getColor(R.color.result_view);

		resultPointColor = Color.parseColor("#c0ffff00");//resources.getColor(R.color.possible_result_points);
		possibleResultPoints = new HashSet<ResultPoint>(5);
	}

	public ScanViewBack(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		density = context.getResources().getDisplayMetrics().density;
		//Convert pixels to dp
		ScreenRate = (int)(20 * density);
		paint = new Paint();
		maskColor = Color.parseColor("#60000000");//resources.getColor(R.color.viewfinder_mask);
		resultColor = Color.parseColor("#b0000000");//resources.getColor(R.color.result_view);

		resultPointColor = Color.parseColor("#c0ffff00");//resources.getColor(R.color.possible_result_points);
		possibleResultPoints = new HashSet<ResultPoint>(5);
	}
	/**
	 * Gets the width and height of the screen
	 *
	 * @return
	 */
	@SuppressLint("NewApi")
	public static Point getScreenResolution(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		Point theScreenResolution = new Point();
		display.getSize(theScreenResolution);
		return new Point(theScreenResolution.x, theScreenResolution.y);
	}

	private Camera camera;
	private Rect framingRect;
	public Rect getFramingRect() {

		Point screenResolution = getScreenResolution(context);
//		int width = screenResolution.x *3/4;
//		System.out.println("getScreenResolutionwidth"+width);//540
//
//				if (width > MAX_FRAME_WIDTH) {
//				width = MAX_FRAME_WIDTH;
//			}
//
//		int height = screenResolution.y *9/20;  //
//			System.out.println("getScreenResolutionheight"+height);//880
//		if (height < MIN_FRAME_HEIGHT) {
//				height = MIN_FRAME_HEIGHT;
//			}
//		System.out.println("getScreenResolutionheight1"+height);//880
//
//
//		//扫码框位置
//		int leftOffset = (screenResolution.x - width) / 2;
//		int topOffset = (screenResolution.y - height) / 2;
//		framingRect = new Rect(leftOffset, topOffset, leftOffset + width,
//				topOffset + height);
//		return framingRect;
		int width = screenResolution.x * 3 / 4;
		if (width < MIN_FRAME_WIDTH) {
			width = MIN_FRAME_WIDTH;
		} else if (width > MAX_FRAME_WIDTH) {
			width = MAX_FRAME_WIDTH;
		}
		int height = screenResolution.y * 3 / 4;
		if (height < MIN_FRAME_HEIGHT) {
			height = MIN_FRAME_HEIGHT;
		} else if (height > MAX_FRAME_HEIGHT) {
			height = MAX_FRAME_HEIGHT;
		}
		int leftOffset = (screenResolution.x - width) / 2;
		int topOffset = (screenResolution.y - height) / 2;
		framingRect = new Rect(leftOffset, topOffset, leftOffset + width,
				topOffset + height);
		return framingRect;
	}
	@Override
	public void onDraw(Canvas canvas) {
		// in the middle of the scan box. If you need to change the size of the scan box, go to CameraManager.
		Rect frame = getFramingRect();
		if (frame == null) {
			return;
		}

		//Initialize the top and bottom of the midline slide
		if(!isFirst){
			isFirst = true;
			slideTop = frame.top;
			slideBottom = frame.bottom;
		}

		MIDDLE_LINE_PADDING = new Double((frame.right - frame.left)/6).intValue();

		//Get the width and height of the screen
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		paint.setColor(resultBitmap != null ? resultColor : maskColor);

		//Draw the shadow outside the scan box. There are four parts, the top of the scan box to the top of the screen, the bottom of the scan box to the bottom of the screen
		// the left of the scan box to the left of the screen, the right of the scan box to the right of the screen
		canvas.drawRect(0, 0, width, frame.top, paint);
		canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
		canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1,
				paint);
		canvas.drawRect(0, frame.bottom + 1, width, height, paint);



		if (resultBitmap != null) {
			// Draw the opaque result bitmap over the scanning rectangle
			paint.setAlpha(OPAQUE);
			canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
		} else {

			//Draw the corners on the sides of the scan box, 8 parts in total
			paint.setColor(Color.RED);
			canvas.drawRect(frame.left, frame.top, frame.left + ScreenRate,
					frame.top + CORNER_WIDTH, paint);
			canvas.drawRect(frame.left, frame.top, frame.left + CORNER_WIDTH, frame.top
					+ ScreenRate, paint);
			canvas.drawRect(frame.right - ScreenRate, frame.top, frame.right,
					frame.top + CORNER_WIDTH, paint);
			canvas.drawRect(frame.right - CORNER_WIDTH, frame.top, frame.right, frame.top
					+ ScreenRate, paint);
			canvas.drawRect(frame.left, frame.bottom - CORNER_WIDTH, frame.left
					+ ScreenRate, frame.bottom, paint);
			canvas.drawRect(frame.left, frame.bottom - ScreenRate,
					frame.left + CORNER_WIDTH, frame.bottom, paint);
			canvas.drawRect(frame.right - ScreenRate, frame.bottom - CORNER_WIDTH,
					frame.right, frame.bottom, paint);
			canvas.drawRect(frame.right - CORNER_WIDTH, frame.bottom - ScreenRate,
					frame.right, frame.bottom, paint);

			paint.setColor(Color.WHITE);
			canvas.drawRect(frame.left + ScreenRate, frame.top, frame.right - ScreenRate,
					frame.top + CORNER_WIDTH, paint);// the white line above the scan box
			canvas.drawRect(frame.left + ScreenRate, frame.bottom - CORNER_WIDTH, frame.right - ScreenRate,
					frame.bottom, paint);// the white line below the scan box
			canvas.drawRect(frame.left, frame.top + ScreenRate, frame.left + CORNER_WIDTH,
					frame.bottom - ScreenRate, paint);//the white line to the left of the scan box
			canvas.drawRect(frame.right - CORNER_WIDTH, frame.top + ScreenRate, frame.right,
					frame.bottom - ScreenRate, paint);//the white line to the right of the scan box

			//Draw the middle line. Each time you refresh the screen, the middle line moves down SPEEN_DISTANCE
			slideTop += SPEEN_DISTANCE;
			if(slideTop >= frame.bottom){
				slideTop = frame.top;
			}
			paint.setColor(Color.RED);
			canvas.drawRect(frame.left + MIDDLE_LINE_PADDING, slideTop - MIDDLE_LINE_WIDTH/2, frame.right - MIDDLE_LINE_PADDING,slideTop + MIDDLE_LINE_WIDTH/2, paint);


			//Draw the words under the scan box
			paint.setColor(Color.WHITE);
			paint.setTextSize(TEXT_SIZE * density);
//			paint.setAlpha(0x40);
//			paint.setTypeface(Typeface.create("System", Typeface.BOLD));

			Rect bounds = new Rect();
			String text=getResources().getString(R.string.view_put_qrcode);
			paint.getTextBounds(text, 0, text.length(), bounds);

			canvas.drawText(text, (width - bounds.width()) / 2/*frame.left*/, (float) (frame.bottom + (float)TEXT_PADDING_TOP *density), paint);



			Collection<ResultPoint> currentPossible = possibleResultPoints;
			Collection<ResultPoint> currentLast = lastPossibleResultPoints;
			if (currentPossible.isEmpty()) {
				lastPossibleResultPoints = null;
			} else {
				possibleResultPoints = new HashSet<ResultPoint>(5);
				lastPossibleResultPoints = currentPossible;
				paint.setAlpha(OPAQUE);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentPossible) {
					canvas.drawCircle(frame.left + point.getX(), frame.top
							+ point.getY(), 6.0f, paint);
				}
			}
			if (currentLast != null) {
				paint.setAlpha(OPAQUE / 2);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentLast) {
					canvas.drawCircle(frame.left + point.getX(), frame.top
							+ point.getY(), 3.0f, paint);
				}
			}


			//Refresh only the contents of the scan box, not the rest
			postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
					frame.right, frame.bottom);

		}
	}

	public void drawViewfinder() {
		resultBitmap = null;
		invalidate();
	}

	/**
	 * Draw a bitmap with the result points highlighted instead of the live
	 * scanning display.
	 *
	 * @param barcode
	 *            An image of the decoded barcode.
	 */
	public void drawResultBitmap(Bitmap barcode) {
		resultBitmap = barcode;
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point) {
		possibleResultPoints.add(point);
	}

}
