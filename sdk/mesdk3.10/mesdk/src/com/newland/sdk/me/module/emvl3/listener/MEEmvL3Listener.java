package com.newland.sdk.me.module.emvl3.listener;

import com.newland.sdk.me.module.emvl3.external.EmvL3Listener;

/**
 * @Description EMVL3监听,继承内置EMVL3监听实现兼容.
 * @Author wuhh
 * @Date 2020/9/8
 */
public interface MEEmvL3Listener extends EmvL3Listener {
    //回调需要保持跟内置EMVL3一致,这样可以直接兼容内置.
    /*
    int uiEvent(int uiEventID, byte[] uiEventData);

    int selectCandidateList(ArrayList<Candidate> candidateList, int[] select);

    int onFinalSelect(int cardInterface, byte[] aid, int aidLen);

    int getPIN(int pinType, int pinTryCnt, publickey pinPK, byte[] sw1sw2);

    int checkCredentials();

    int selectAccount(int[] select);

    int selectLanguage();

    int voiceReferrals();

    int dek_det(int type, byte[] data, int[] dataLen);

    int getManualData();
    */

    //智能内置未定义,暂和EMVL3指令名称一致.
    int cardnumConfirm();
}
