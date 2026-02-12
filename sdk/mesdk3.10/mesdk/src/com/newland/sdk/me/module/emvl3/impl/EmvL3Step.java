package com.newland.sdk.me.module.emvl3.impl;
import android.util.Log;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
/**
 * @Description
 * @Author wuhh
 * @Date 2020/9/8
 */
public class EmvL3Step {
    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger("EmvL3Step");
    private static final String TAG = "EmvL3Step";
    private Object emvStepSync = new Object();
//    private Lock mEmvStepLock = new ReentrantLock();
    private EmvL3ListenerStep mEmvL3ListenerStep;

    //MEEmvL3Listener
    public enum EmvL3PauseStep {
        uiEvent("uiEvent"),
        getPIN("getPIN"),
        selectCandidateList("selectCandidateList"),
        selectAccount("selectAccount"),
        selectLanguage("selectLanguage"),
        checkCredentials("checkCredentials"),
        voiceReferrals("voiceReferrals"),
        dek_det("dek_det"),
        onFinalSelect("onFinalSelect"),
        onConfirmCardInfo("onConfirmCardInfo"),
        getManualData("getManualData"),
        fallback("fallback"),

        finished("finished"),
        onlineProcess("onlineProcess"),
        cancel("cancel");

        private String mStep;
        EmvL3PauseStep(String step){
            this.mStep = step;
        }
        public String toString(){
            return mStep;
        }
    }

    //EMVControllerListener
    public enum EmvL3ListenerStep{
        onRequestSelectApplication("onRequestSelectApplication"),
        onRequestConfirmCardInfo("onRequestConfirmCardInfo"),
        onRequestInputAmount("onRequestInputAmount"),
        onRequestInputPIN("onRequestInputPIN"),
        onRequestOnlineProcess("onRequestOnlineProcess"),
        onEmvFinished("onEmvFinished"),
        onFallback("onFallback"),
        onError("onError"),
        onRequestSelectAccountType("onRequestSelectAccountType"),
        onRequestConfirmID("onRequestConfirmID"),
        onRequestConfirmEC("onRequestConfirmEC"),
        onRequestShowMessage("onRequestShowMessage"),
        onRequestSelectLanguage("onRequestSelectLanguage"),
        onRequestConfirmFinalAppSelection("onRequestConfirmFinalAppSelection");

        private String mStep;
        EmvL3ListenerStep(String step){
            this.mStep = step;
        }
        public String toString(){
            return mStep;
        }
    }

    //EMVTransController
    public static final String completeEMVProcess = "completeEMVProcess";


    public void pauseStep(Runnable runnable,EmvL3PauseStep pauseStep,EmvL3ListenerStep emvStep){
        synchronized (emvStepSync){
            try {
                mEmvL3ListenerStep = emvStep;
                Log.e(TAG, "EmvL3Step pauseStep "+pauseStep+"->"+emvStep);
                //线程池怎么获取指定线程是否已经运行?
                //METhreadExecutors.startThread(runnable);
                Thread thread = new Thread(runnable);
                thread.start();
                while(!thread.isAlive()){}//Busy waiting
                if(pauseStep == EmvL3PauseStep.selectCandidateList||
                        pauseStep == EmvL3PauseStep.onFinalSelect||
                        pauseStep == EmvL3PauseStep.onConfirmCardInfo||
                        pauseStep == EmvL3PauseStep.getPIN||
                        pauseStep == EmvL3PauseStep.checkCredentials){
                    EmvL3Global.setIsInterruptTime(true);
                    deviceLogger.error("[pauseStep] pauseStep="+pauseStep+" isInterruptTime=true");
                }
                int timeOutMs = EmvL3Global.getEmvStepTimeOutMs()+2000;
                deviceLogger.debug("[pauseStep] EmvStepTimeOutMs="+timeOutMs);
                emvStepSync.wait(timeOutMs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public EmvL3ListenerStep getEmvStep(){
        synchronized (emvStepSync){
            return mEmvL3ListenerStep;
        }
    }
    public void resumeStep(EmvL3ListenerStep emvStep,String goStep){
        synchronized (emvStepSync){
            emvStepSync.notify();
            if(goStep.equals(EmvL3PauseStep.selectCandidateList.toString())||
                    goStep.equals(EmvL3PauseStep.onFinalSelect.toString())||
                    goStep.equals(EmvL3PauseStep.onConfirmCardInfo.toString())||
                    goStep.equals(EmvL3PauseStep.getPIN.toString())||
                    goStep.equals(EmvL3PauseStep.checkCredentials.toString())){
                EmvL3Global.setIsInterruptTime(false);
                deviceLogger.error("[resumeStep] goStep="+goStep+" isInterruptTime=false");
            }
            Log.e(TAG, "EmvL3Step resumeStep "+emvStep+"->"+goStep);
        }
    }
    public void resumeStep(EmvL3ListenerStep emvStep,EmvL3PauseStep goStep){
        this.resumeStep(emvStep,goStep.toString()+"");
    }

    public void interruptStep(EmvL3PauseStep goStep){
        synchronized (emvStepSync){
            emvStepSync.notifyAll();
            Log.e(TAG, "EmvL3Step interruptStep "+mEmvL3ListenerStep+"->"+goStep);
        }
    }
}
