package com.newland.sdk.module.externalScan;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.newland.sdk.module.serialport.Baudrate;
import com.newland.sdk.module.serialport.SerialExtParams;

import java.util.Map;

/**
 * @description External Scan Box
 * @author Suyuming
 * @create 2019/7/31
 */
public interface ExtScanBoxModule {

    /**
     * Set serial communication parameters of the device
     * @param params   {@link ScanBoxInitExtParams}
     * @return
     * @since 3.10.01
     */
    boolean init(@Nullable ScanBoxInitExtParams params);

    /**
     * Set serial communication parameters of the device
     * @param baudrate Baud Rate {@link Baudrate}
     * @param params   {@link SerialExtParams}
     * @return true,Success;false,fail;
     * TODO 外接键盘等均不实现，暂不实现此功能
     */
//    boolean setConfiguration(Baudrate baudrate, SerialExtParams params);

    /**
     * Setting Machine Parameters
     * @param param  Device parameters, parameters can be set SN,PN,CSN.
     * @param value  Parameter values, using GBK encoding.
     * @return true,Success;  false,fail;
     * @since 3.10.01
     */
    boolean setParams(@NonNull ScanBoxDevParams param, @NonNull String value);

    /**
     * Obtain equipment parameters
     * @param param Device parameters
     * @return parameters
     * @since 3.10.01
     */
    Map<String,String> getParams(@NonNull ScanBoxDevParams[] param);

    /**
     * Set external scan parameters
     * @param params
     * @return
     * @since 3.10.01
     */
    boolean setScanParams(@NonNull ScanBoxParams params);

    /**
     * start scan code
     * @param amount       Pass in null to close the amount displayed, up to 6 digit Numbers plus a decimal point.
     * @param timeOut      timeOut (unit:millisecond)
     * @param listener     listener
     * @param params       {@link StartScanExtParams}
     * @return
     * @since 3.10.01
     */
    boolean startScan(String amount, int timeOut, ResultListener listener, @Nullable StartScanExtParams params);

    /**
     * Stop scan code
     * @return
     * @since 3.10.01
     */
    boolean stopScan();
}
