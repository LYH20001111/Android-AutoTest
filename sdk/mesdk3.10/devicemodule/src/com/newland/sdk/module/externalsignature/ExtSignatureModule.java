package com.newland.sdk.module.externalsignature;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.newland.sdk.module.externalPin.PinpadInitExtParams;

/**
 * @description: External Signature
 * @author Suyuming
 * @create 2018/7/28
 */
public interface ExtSignatureModule {

    /**
     * PinPad port initialization
     * @param params  Pinpad initialization extension parameters. If it`s null,the default use pinpad port to transmit.
     * @return
     * @since 3.10.01
     */
    boolean init(@Nullable PinpadInitExtParams params);

    /**
     * Set External Signature parameters
     * @param params {@link SignatureExtParams}
     * @return
     * @since 3.10.01
     */
    boolean setSignatureParams(SignatureExtParams params);

    /**
     * Do the signature operation.
     * @param definedInput the text shows in the background.
     * @return the signature data. The actual data is offset by 3 bytes.
     * @since 3.10.01
     */
    byte[] doSign(@Nullable String definedInput);

    /**
     * Do the signature operation.
     * ME51 not supported
     * P180 supported
     * SP130 supported since V07.00.38
     * @param definedInput the text shows in the background.
     * @param listener Sign listener {@link DoSignListener}
     * @param extParams {@link DoSignExtParams}
     */
    void doSign(@Nullable String definedInput, @NonNull DoSignListener listener, DoSignExtParams extParams);
}
