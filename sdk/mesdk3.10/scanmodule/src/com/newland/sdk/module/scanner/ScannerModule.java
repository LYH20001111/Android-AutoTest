package com.newland.sdk.module.scanner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.SurfaceView;


/** 
 * Barcode scanner device<p>
 * 
 * @author youjf
 * @since 3.10.01
 */
public interface ScannerModule {
	/**
	 * Whether support front-facing/rear-facing camera or not
	 * @param scannerType {@link ScannerType}
	 * @since 3.10.01
	 * @return
	 */
	public boolean isSupScanCode(ScannerType scannerType);

	/**
	 * Initiate a scanning operation <p>
	 * From scanning initiation to ending, as long as there is data scanning, the listener will return events till the following event occurs.  <p>
	 * <ol>
	 * <li> scanning timeout </li>
	 * <li>active call stopScan</li>
	 * </ol>
	 * @param context context
	 * @param scannerType {@link ScannerType}
	 * @param surfaceView the object of preview
	 * @param timeout (second)
	 * @param scannerExtParams  <p> they are parameters that scanner needed.</p>
	 * @param scannerListener <p>Scanning result event lisener</p>
	 * @since 3.10.01
	 */
	public void startScan(Context context, ScannerType scannerType, SurfaceView surfaceView,int timeout, @NonNull ScannerListener scannerListener, ScannerExtParams scannerExtParams);

	/**
	 * Initiate a scanning operation <p>
	 * From scanning initiation to ending, as long as there is data scanning, the listener will return events till the following event occurs.  <p>
	 * <ol>
	 * <li> scanning timeout </li>
	 * <li>active call stopScan</li>
	 * </ol>
	 * @param context context
	 * @param scannerType {@link ScannerType}
	 * @param surfaceView the object of preview
	 * @param timeout (second)
	 * @param scannerExtParams  <p> they are parameters that scanner needed.</p>
	 * @param scanListener <p>Scanning result event lisener</p>
	 */
	public void startScanForOversea(Context context, ScannerType scannerType, SurfaceView surfaceView,int timeout, @NonNull ScanListener scanListener, ScannerExtParams scannerExtParams);


	/**
	 * Interrupt scanning.<p>
	 * @since 3.10.01
	 */
	public void stopScan();

	/**
	 * Control ledLight、redledLicght open or close
	 * @param type light type{@link ScanLightType}
	 * @param lightOperType The light type to be operated.{@link LightOperType}
	 * @return true:suceessful. false:failed
	 * @since 3.10.01
	 */
	public boolean operateLight(ScanLightType type, LightOperType lightOperType);

	/**init decode,it should be called before startYUVDecode.
	 * @param context
	 * @return
	 */
	public boolean initDecode(Context context);

	/**
	 * Decode yuv420 data。
	 * @param yuv yuv420 data
	 * @param width the image width of yuv420 data
	 * @param height the image height of yuv420 data
	 * @param decodeListener {@link DecodeListener}
	 * @since 3.10.06
	 */
	public void startYUVDecode(@NonNull byte[] yuv, int width, int height,DecodeListener decodeListener);

	/**
	 * Stop decode
	 */
	public void stopDecode();

	/**
	 * Some special support
	 * Set whether the N550 device illuminate light is on
	 * @param illuminate {@code true} turn on the scan light
	 *                   {@code false} turn off the scan light
	 * @return
	 * @since 3.10.01
	 */
	boolean setIlluminateLight(boolean illuminate);

}
