package com.newland.nsdkdemo.internal.pin;

import android.newland.os.NlBuild;
import android.os.Build;

/**
 * @author linweikun
 * @date 2019/8/8
 * 机器型号
 */
public class PosModel {
    public static final String MODEL = NlBuild.VERSION.MODEL;

    public static final String N510 = "N510";
    public static final String N550 = "N550";
    public static final String N700 = "N700";
    public static final String N850 = "N850";
    public static final String N900 = "N900";
    public static final String N910 = "N910";
    public static final String N920 = "N920";
    public static final String N950 = "N950";
    public static final String N950Pro = "N950 Pro";
    public static final String N910Pro = "N910 Pro";

    public static final String IM81 = "IM81";

    public static final String CPOS_X1 = "CPOS X1";
    public static final String CPOS_X3 = "CPOS X3";
    public static final String CPOS_X5 = "CPOS X5";
    public static final String COPS_POYNT = "CPOS-X5-STD";
    public static final String POYNTA7 = "poynt";
    public static final String X5_TEST = "x5_test";
    //public static final String COPS_POYNT = "x5_test";

    public static final String F7 = "FPOS F7";
    public static final String F10 = "FPOS F10";
    public static final String S10 = "FPOS S10";


    public static boolean isN510(){
        return MODEL.equals(N510);
    }

    public static boolean isN550(){
        return MODEL.equals(N550);
    }

    public static boolean isN700(){
        return MODEL.equals(N700);
    }

    public static boolean isN850(){
        return MODEL.equals(N850);
    }


    public static boolean isN900(){
        return MODEL.equals(N900);
    }


    public static boolean isN910(){
        return MODEL.equals(N910);
    }

    public static boolean isS10(){
        return MODEL.equals(S10);
    }

    public static boolean isN920(){
        return MODEL.equals(N920);
    }

    public static boolean isN950(){
        return MODEL.equals(N950);
    }

    public static boolean isN950Pro(){
        return MODEL.equals(N950Pro);
    }

    public static boolean isN910Pro(){
        return MODEL.equals(N910Pro);
    }

    public static boolean isIM81(){
        return MODEL.equals(IM81);
    }


    public static boolean isCpos(){
        if (MODEL==null){
            return false;
        }
        return MODEL.equals(CPOS_X5) || MODEL.equals(CPOS_X3) || MODEL.equals(CPOS_X1)|| MODEL.equals(X5_TEST) || MODEL.equals(COPS_POYNT);
    }

    public static boolean isCposX1(){
        return MODEL.equals(CPOS_X1);
    }

    public static boolean isCposX3(){
        return MODEL.equals(CPOS_X3);
    }

    public static boolean isCposX5(){
        return MODEL.equals(CPOS_X5);
    }

    public static boolean isPoynt(){
        return MODEL.equals(COPS_POYNT);
    }


    public static boolean isA10(){

        return Build.VERSION.SDK_INT == 29;
    }

    public static boolean isF7(){
        return MODEL.equals(F7);
    }

    public static boolean isF10(){
        return MODEL.equals(F10) || MODEL.equals(S10);
    }


    public static boolean isOverseas(){
        String customerId = NlBuild.CUSTOMER_ID;
        return "overseas".equals(customerId)||"Brasil".equals(customerId)|"poynt".equals(customerId);
    }

    public static boolean isX800(){
        return "X800".equals(NlBuild.VERSION.MODEL);
    }
}
