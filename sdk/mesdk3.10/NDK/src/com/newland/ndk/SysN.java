package com.newland.ndk;

import com.newland.event.EventCallBack;
import com.newland.ndk.h.EM_SYS_CONFIG;
import com.newland.ndk.h.EM_SYS_HWINFO;
import com.newland.ndk.h.PosTime;

public class SysN {
	protected SysN(){
		super();
	}

	/**
	 * Get hardware info.
	 * @param emFlag Hardware type
	 * @param punLen Length of buffer returned (16-100)
	 * @param psBuf Buffer to save data returned
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public int NDK_SysGetPosInfo(EM_SYS_HWINFO emFlag,int[] punLen,byte[] psBuf){
		return NDK_SysGetPosInfo_m(emFlag.ordinal(),punLen,psBuf);
	}

	/**
	 * Get system configuration.
	 * @param emConfig Configuration type index
	 * @param pnValue Value of configuration
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public int NDK_SysGetConfigInfo(EM_SYS_CONFIG emConfig, int[] pnValue){
		return NDK_SysGetConfigInfo_m(emConfig.ordinal(),pnValue);
	}

	/**
	 * Set hardware info.
	 * @param emFlag Hardware type
	 * @param psBuf  Buffer to save data returned
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public int NDK_SysSetPosInfo(EM_SYS_HWINFO emFlag, String psBuf){
		return NDK_SysSetPosInfo_m(emFlag.ordinal(),psBuf);
	}

	/**
	 * Set time.
	 * @param time Time
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public int NDK_SysSetPosTime(PosTime time){
		return NDK_SysSetPosTime_m(time.tm_year-1900,time.tm_mon,time.tm_mday,time.tm_hour,time.tm_min,time.tm_sec);
	}

	/**
	 * Get time.
	 * @param time time
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public int NDK_SysGetPosTime(PosTime time){
		int tm_year[] = new int[1];
		int tm_mon[] = new int[1];
		int tm_mday[] = new int[1];
		int tm_hour[] = new int[1];
		int tm_min[] = new int[1];
		int tm_sec[] = new int[1];
		int ret = NDK_SysGetPosTime_m(tm_year,tm_mon,tm_mday,tm_hour,tm_min,tm_sec);
		time.tm_year = tm_year[0]+1900;
		time.tm_mon = tm_mon[0];
		time.tm_mday = tm_mday[0];
		time.tm_hour = tm_hour[0];
		time.tm_min = tm_min[0];
		time.tm_sec = tm_min[0];
		return ret;
	}

	/**
	 * Beep once.
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SysBeep();

	/**
	 * 	Get API version.
	 * @param version Version string (No less than 16 bytes) .
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_Getlibver(byte[] version);

	/**
	 * Beep.
	 * @param unFrequency Frequency in Hz (0-4000)
	 * @param unSeconds Duration in ms
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SysTimeBeep(int unFrequency,int unSeconds);
	private native int NDK_SysSetPosTime_m(int tm_year,int tm_mon,int tm_mday,int tm_hour,int tm_min,int tm_sec);
	private native int NDK_SysGetPosTime_m(int[] tm_year,int[] tm_mon,int[] tm_mday,int[] tm_hour,int[] tm_min,int[] tm_sec);
	/**
	 *Start watch.
	 *Usually for calculating time elapsed, work with NDK_SysStopWatch. (Accuracy within 1 millisecond.)
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SysStartWatch();

	/**
	 * Stop watch.
	 * @param punTime Time count value
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SysStopWatch(int[] punTime);

	/**
	 * Delay.
	 * @param unDelayTime Delay time (Unit: 0.1s)
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SysDelay(int unDelayTime);

	/**
	 * Delay (Unit: 1ms)
	 * @param unDelayTime Delay time
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SysMsDelay(int unDelayTime);

	/**
	 * Exit system.
	 * @param nErrCode Error code returned when exit. (0: Normal exit; Non-zero: Abnormal exit)
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SysExit(int nErrCode);

	/**
	 * Restart terminal.
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SysReboot();

	/**
	 * Shutdown terminal.
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SysShutDown();

	/**
	 * Set beep volume.
	 * @param unVolNum Volume value (0-5, Default:5)
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SysSetBeepVol(int unVolNum);

	/**
	 * Get beep volume.
	 * @param punVolNum Volume value
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SysGetBeepVol(int[] punVolNum);

	/**
	 * Enable/Disable auto-sleep.
	 * @param unFlag 0: Disable; 1: Enable
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SysSetSuspend(int  unFlag);

	/**
	 * Enter sleep mode immediately.
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SysGoSuspend();

	/**
	 * Get battery level.
	 * @param punVol 0: External adapter; Non-zero: Battery level (Ex. 8310 means 8.31V)
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SysGetPowerVol(int[] punVol);

	/**
	 * Turn on/off LED.
	 * @param emStatus Use '|' to control more than one LEDs. e.g. NDK_LedStatus(LED_RFID_RED_ON|LED_RFID_YELLOW_FLICK) means turning red LED on and flicker yellow LED, other LEDs status remain unchanged.
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_LedStatus(int emStatus);

	public native int NDK_LedSetFlickParam(int emStatus, long unFlickOn,long unFlickOff);

	public native int NDK_LedLt1118Status(long emStatus);

	public int setLedLt1118Status(boolean isOn){
		if(isOn){
			return NDK_LedLt1118Status(0x80000000001L|0x80000000040L|0x80000001000L|0x80000040000L|0x80001000000L);
		}else {
			return NDK_LedLt1118Status(0x80000000002L|0x80000000080L|0x80000002000L|0x80000080000L|0x80002000000L);
		}
	}
	/**
	 * Read Watch
	 * @param punTime
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SysReadWatch(int[] punTime);

	private native int NDK_SysGetPosInfo_m(int emFlag,int[] punLen,byte[] psBuf);
	private native int NDK_SysGetConfigInfo_m(int emConfig,int[] pnValue);

	/**
	 * Initialize statistics data.
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SysInitStatisticsData();

	/**
	 * Get statistics data.
	 * @param emDevId Device ID (EM_SS_DEV_ID)
	 * @param pulValue Statistics data
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SysGetStatisticsData(int emDevId,long[] pulValue);

	/**
	 * Get firmware type.
	 * @param emFWinfo Firmware type (EM_SYS_FWINFO)
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SysGetFirmwareInfo(int[] emFWinfo);

	/**
	 * Get time from 1970-01-01 00:00:00 in seconds.
	 * @param ulTime Time in seconds
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SysTime(int[] ulTime);

	/**
	 * Set auto-wakeup.
	 * @param unSec Wakeup time in seconds (No less than 60 seconds)
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SysSetSuspendDuration(int unSec);
	//public native int NDK_SysGetPowerVolRange(unsigned int *punMax,unsigned int *punMin);
	public native int NDK_SysKeyVolSet(int sel);
	//public native int NDK_SysPeerOper(EM_SYS_PEEROPER oper);

	public native int NDK_SysEnterBoot();
	private native int NDK_SysSetPosInfo_m(int emFlag, String psBuf);

	/**
	 * Get K21 Version
	 * @param version Version string
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDk_SysGetK21Version(byte[] version);
	//public native int NDK_TSKbdGetXY(uint unTime, uint *x, uint *y);

	/**
	 * WakeUp
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SysWakeUp();

	/**
	 * Enter sleep mode immediately.
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SysGoSuspend_Extern();
	//public native int NDK_TSKbd_Ctrl(uint ctrl);
	//public native int NDK_SysGetEvent(uint *event, int *len, uint8_t *out_data);

	/**
	 * registe sys event
	 * @param event sys event
	 * @param timeOutMs time
	 * @param callBack  event callback
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SYS_RegisterEvent(int event,int timeOutMs, EventCallBack callBack);

	/**
	 * unregiste sys event
	 * @param event sys event
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SYS_UnRegisterEvent(int event);
	public native int NDK_SysDevBacklightCtrl(int mdevice, int value);

	public native int NDK_SysTimeBeep_Ex(int unFrequency,int unSeconds,int unVolumn);
	public native int NDK_SysSetBeepVol_Extern(int type,int unVolumn);

	public native int NDK_SysGetBeepVol_Extern(int type,int[] unVolumn);

}
