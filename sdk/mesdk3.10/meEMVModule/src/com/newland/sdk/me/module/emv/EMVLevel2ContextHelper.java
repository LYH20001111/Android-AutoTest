package com.newland.sdk.me.module.emv;

import android.content.Context;
import android.os.Handler;

import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.module.emv.EMVInterceptListener;
import com.newland.sdk.module.emv.EMVTransController;
import com.newland.sdk.module.iccard.ICCardSlot;
import com.newland.sdk.module.iccard.ICCardType;
import com.newland.sdk.module.rfcard.RFResult;

/**
 * Emv leve2上下文服务类<p>
 * 用于设置标准的emv参数数据<p>
 *  
 * no confusion 
 *
 * 
 *
 */
public class EMVLevel2ContextHelper {
	
	private Context context;
	private Handler mainHandler;
	private TransferSequenceGenerator seqGen;
	
	private ModuleType defaultModuleType;
	
	private volatile ICCardSlot useICCardSlot =  ICCardSlot.IC1;
	
	private volatile ICCardType useICCardType = ICCardType.CPUCARD;
	
	private volatile RFResult rfrslt;
	
	volatile EMVCoreOperator.PinEntryRunnable pinEntryRunnable = null;
	volatile EMVCoreOperator.AIDSelectRunnable aidselectRunnable = null;
	volatile EMVCoreOperator.AmtEntryRunnable amtEntryRunnable = null;
	volatile EMVCoreOperator.ECChoiceRunnable ecChoiceRunnable= null;
	volatile EMVCoreOperator.AccountTypeSelectRunnable accountTypeSelectedRunnable = null;
	volatile EMVCoreOperator.LanguageChoiceRunnable languageChoiceRunnable = null;
	volatile EMVCoreOperator.MessageConfirmRunnable messageConfirmRunnable = null;
	volatile EMVCoreOperator.CertIDConfirmRunnable certIDConfirmRunnable = null;
	volatile EMVCoreOperator.FinalSelectRunnable finalSelectRunnable = null;


	public ICCardSlot getUseICCardSlot() {
		return useICCardSlot;
	}

	public void setUseICCardSlot(ICCardSlot useICCardSlot) {
		this.useICCardSlot = useICCardSlot;
	}
	
	public ICCardType getUseICCardType() {
		return useICCardType;
	}

	public void setUseICCardType(ICCardType useICCardType) {
		this.useICCardType = useICCardType;
	}

	/**
	 * 获得一个上下文实例
	 * @return
	 */
	public static EMVLevel2ContextHelper getContextHelper(EMVTransController controller){
		if(!(controller instanceof EMVLevel2TransferController)){
			return null; //非对应实例
		}
		return ((EMVLevel2TransferController)controller).contextHelper;
	}
	
	public void init(EMVTransController controller, Context context){
		if(context == null)
			throw new EMVTransferException("must run:EMVModule.initEmvModule(android.content.Context) before getEmvController!");
		this.context = context;
		this.mainHandler = new Handler(context.getMainLooper());
		if(controller instanceof EMVLevel2TransferController){
			EMVLevel2TransferController l2Controller = (EMVLevel2TransferController)controller;
			
			EMVInterceptListener listener = null;
			if(l2Controller.getListener() instanceof EMVInterceptListener){
				listener = (EMVInterceptListener) l2Controller.getListener();
			}
			if(listener == null || !listener.activateTransactionCountInterceptor()){
				this.seqGen = TransferSequenceGenerator.getInstance(context);
			}
				
		}
	}

	Context getContext() {
		return context;
	}

	public Handler getMainHandler() {
		return mainHandler;
	}

	public TransferSequenceGenerator getSeqGen() {
		return seqGen;
	}

	public ModuleType getDefaultModuleType() {
		return defaultModuleType;
	}

	public void setDefaultModuleType(ModuleType defaultModuleType) {
		this.defaultModuleType = defaultModuleType;
	}

	public RFResult getRfrslt() {
		return rfrslt;
	}

	public void setRfrslt(RFResult rfrslt) {
		this.rfrslt = rfrslt;
	}

}
