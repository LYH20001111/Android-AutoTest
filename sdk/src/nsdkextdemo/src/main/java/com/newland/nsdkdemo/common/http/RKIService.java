package com.newland.nsdkdemo.common.http;

import com.newland.nsdkdemo.common.bean.InitiationRequest;
import com.newland.nsdkdemo.common.bean.InitiationResponse;
import com.newland.nsdkdemo.common.bean.KeyRequest;
import com.newland.nsdkdemo.common.bean.KeyResponse;
import com.newland.nsdkdemo.common.bean.VerificationRequest;
import com.newland.nsdkdemo.common.bean.VerificationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RKIService {
    @POST("/kas/v1/rki/initation")
    Call<InitiationResponse> initRKI(@Body InitiationRequest initiationRequest);

    @POST("kas/v1/rki/requestkey")
    Call<KeyResponse> requestKey(@Body KeyRequest keyRequest);

    @POST("kas/v1/rki/verification")
    Call<VerificationResponse> verifyResult(@Body VerificationRequest verificationRequest);
}
