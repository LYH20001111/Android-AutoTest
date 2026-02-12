package com.newland.ndk;

import android.util.Log;

import com.newland.ndk.napi.SecNapi;

public class NdkApiManager {
	private static final String tag = "NdkApiManager";
	private AlgN alg = null;
	private FileN file = null;
	private IcCard ic = null;
	private MagCard mag = null;
	private Print print = null;
	private RfCard rf = null;
	private SecN sec = null;
	private SysN sys = null;
	private SerialPort serialPort = null;
	private SecNapi mSecNapi;
	static NdkApiManager nm = null;
	static{
		try {
			Log.d(tag, "load ndkapi in nakapimanager!!!");
			System.loadLibrary("ndkapi");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	} 
	private NdkApiManager(){
		super();
		int ret = NDK_Init();
		Log.d(tag," NdkApiManager NDK_Init ret="+ret);
	}

	/**
	 * NDK API Manager
	 * @return NdkApiManager
	 */
	public static NdkApiManager getNdkApiManager(){
		if(nm == null){
			synchronized (NdkApiManager.class){
				if(nm == null){
					nm = new NdkApiManager();
				}
			}
		}
		return nm; 
	}

	/**
	 * get algorithm mode
	 * @return AlgN
	 */
	public AlgN getAlgN(){ 
		if(alg == null)
			alg = new AlgN(); 
		return alg;
	}

	/**
	 * get file mode
	 * @return FileN
	 */
	public FileN getFileN(){
		if(file == null)
			file = new FileN();
		return file;
	}

	/**
	 * get IcCard mode
	 * @return IcCard
	 */
	public IcCard getIcCard(){
		if(ic == null)
			ic = new IcCard();
		return ic;
	}

	/**
	 * get MagCard mode
	 * @return MagCard
	 */
	public MagCard getMagCard(){
		if(mag == null)
			mag = new MagCard();
		return mag;
	}

	/**
	 * get print mode
	 * @return Print
	 */
	public Print getPrint(){
		if(print == null)
			print = new Print();
		return print;
	}

	/**
	 * get RfCard mode
	 * @return RfCard
	 */
	public RfCard getRfCard(){
		if(rf == null)
			rf = new RfCard();
		return rf;
	}

	/**
	 * get security mode
	 * @return SecN
	 */
	public SecN getSecN(){
		if(sec == null)
			sec = new SecN();
		return sec;
	}

	/**
	 * get sys mode
	 * @return SysN
	 */
	public SysN getSysN(){
		if(sys == null)
			sys = new SysN();
		return sys;
	}

	/**
	 * get SerialPort mode
	 * @return SerialPort
	 */
	public SerialPort getSerialPort(){
		if(serialPort == null)
			serialPort = new SerialPort();
		return serialPort;	
	}

	public SecNapi getSecNapi(){
		if(mSecNapi == null){
			synchronized (SecNapi.class){
				if(mSecNapi == null){
					mSecNapi = new SecNapi();
				}
			}
		}
		return mSecNapi;
	}

	private native int NDK_Init();


}
