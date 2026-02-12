package com.newland.sdk.module.pin;

import android.support.annotation.Nullable;

/**
 * pininput module
 *
 * @author youjf
 * @since ver3.10.01
 */
public interface NPinpadModule extends PinpadModule {
    /**
     * Not support
     * Calculate signature data
     *
     * @param macType   Specific mac algorithm. {@link MacType}
     * @param keyIndex  the key index.(1-200).
     * @param inputData calculate data.
     * @param calMacExtParams  <p>the external params used to calculate mac.</p>{@link NCalMacExtParams}
     * @return true if success,false if error.
     */
    public MacResult calculateMac(MacType macType, int keyIndex, byte[] inputData, @Nullable NCalMacExtParams calMacExtParams);

    /**
     *
     * @param loadKeyMode
     * @param srcKeyAlg
     * @param srcKeyIndex
     * @param srcKeyType
     * @param destKeyAlg
     * @param destKeyIndex
     * @param destKeyType
     * @param keyData
     * @param params
     * @return
     */
     public boolean injectKey(LoadKeyMode loadKeyMode,
                                AlgorithmMode srcKeyAlg, int srcKeyIndex, InjectKeyType srcKeyType,
                                AlgorithmMode destKeyAlg, int destKeyIndex, InjectKeyType destKeyType,
                                byte[] keyData,NInjectKeyParams params);
}
