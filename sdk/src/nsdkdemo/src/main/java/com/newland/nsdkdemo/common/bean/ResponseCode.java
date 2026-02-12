package com.newland.nsdkdemo.common.bean;

import com.newland.nsdkdemo.R;

public enum ResponseCode {

    SERVER_OK("00", R.string.server_response_code0),
    SERVER_ERR_01("01",R.string.server_response_code1),
    SERVER_ERR_02("02",R.string.server_response_code2),
    SERVER_ERR_03("03",R.string.server_response_code3),
    SERVER_ERR_04("04",R.string.server_response_code4),
    SERVER_ERR_05("05",R.string.server_response_code5),
    SERVER_ERR_06("06",R.string.server_response_code6),
    SERVER_ERR_07("07",R.string.server_response_code7),
    SERVER_ERR_10("10",R.string.server_response_code10),
    SERVER_ERR_11("11",R.string.server_response_code11),
    SERVER_ERR_12("12",R.string.server_response_code12),
    SERVER_ERR_13("13",R.string.server_response_code13),
    SERVER_ERR_14("14",R.string.server_response_code14),
    SERVER_ERR_15("15",R.string.server_response_code15),
    SERVER_ERR_16("16",R.string.server_response_code16),
    SERVER_ERR_17("17",R.string.server_response_code17),
    SERVER_ERR_18("18",R.string.server_response_code18),
    SERVER_ERR_19("19",R.string.server_response_code19),
    SERVER_ERR_20("20",R.string.server_response_code20),
    SERVER_ERR_30("30",R.string.server_response_code30),
    SERVER_ERR_40("40",R.string.server_response_code40),
    SERVER_ERR_41("41",R.string.server_response_code41),
    SERVER_ERR_99("99",R.string.server_response_code99);



    private String value;
    private int resId;

    ResponseCode(String value, int resId) {
        this.value = value;
        this.resId = resId;
    }

    public String getValue() {
        return value;
    }
    public int getResId() {
        return resId;
    }

    public static ResponseCode getValue(String value){
        for (ResponseCode response:values()) {
            if (response.getValue().equals(value) ){
                return response;
            }
        }
        return SERVER_ERR_99;
    }
}
