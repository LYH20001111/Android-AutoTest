package com.newland.nsdk.core.api.internal.bootprovider;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;

public interface BootProvider extends Module {

    /**
     * Set the custom boot animation.
     * @param bootAnimation <b>[Required]</b> The absolute path of animation file, which should be signed in .zip format.
     * @throws NSDKException
     */
    void setCustomBootAnimation(String bootAnimation)throws NSDKException;

    /**
     * Remove the custom boot animation.
     * @throws NSDKException
     */
    void removeCustomBootAnimation() throws NSDKException;

    /**
     * Set the custom boot logo.
     * @param bootLogo <b>[Required]</b> The absolute path of logo file, which shall be signed in .img format.
     * @throws NSDKException
     */
    void setCustomBootLogo(String bootLogo) throws NSDKException;

    /**
     * Remove the custom boot logo.
     * @throws NSDKException
     */
    void removeCustomBootLogo() throws NSDKException;
}
