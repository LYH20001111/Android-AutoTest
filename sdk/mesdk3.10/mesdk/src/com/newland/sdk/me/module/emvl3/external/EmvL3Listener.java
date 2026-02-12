package com.newland.sdk.me.module.emvl3.external;

import java.util.ArrayList;

/**
 * @Description
 * @Author wuhh
 * @Date 2021/3/30
 */
public interface EmvL3Listener {
    int uiEvent(int var1, byte[] var2);

    int getPIN(int var1, int var2, publickey var3, byte[] var4);

    int selectCandidateList(ArrayList<Candidate> var1, int[] var2);

    int selectAccount(int[] var1);

    int selectLanguage();

    int checkCredentials();

    int voiceReferrals();

    int dek_det(int var1, byte[] var2, int[] var3);

    int onFinalSelect(int var1, byte[] var2, int var3);

    int getManualData();
}
