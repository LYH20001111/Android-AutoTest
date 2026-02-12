package com.newland.sdk.pininput;

import android.content.Context;
import android.util.Log;

import com.newland.buildtask.R;
import com.newland.sdk.module.pin.DefaultLayout;

/**
 * Author by bxy, Date on 2019/12/2.
 */
public class KeyBoardLayoutConfig {
    private static final String TAG = "KeyBoardLayoutConfig";
    private Context context;
    private DefaultLayout lyParam;

    public KeyBoardLayoutConfig(Context context){
        this.context = context;
    }

    public void updateConfig(DefaultLayout param){
        this.lyParam = param;
        if(param.getDividerSize() < 0){
            param.setDividerSize(4);
        }

        if(param.getRoundSize() < 0){
            param.setRoundSize(10);
        }

        if(param.getBgColor() == 0){
            param.setBgColor(0xffd3d7d9);
        }

        if(param.getKeyRondomType()==null){
            param.setKeyRondomType(DefaultLayout.KeyRondomType.RANDOM_NUM);
        }

        if(param.getLayoutStyle()==null){
            param.setLayoutStyle(DefaultLayout.Style.STYLE_1);
        }

        if(param.getCancelKeyAttr() == null){
            param.setCancelKeyAttr(new DefaultLayout.KeyAttribute(DefaultLayout.Key.CANCEL,0xfff24c4d,
                    context.getResources().getString(R.string.keyboard_cancel),-1,0xffffffff,null));
        }

        if(param.getBackSpaceKeyAttr() == null){
            param.setBackSpaceKeyAttr(new DefaultLayout.KeyAttribute(DefaultLayout.Key.BACKSPACE,0xfff3e250,
                    null,-1,0xffffffff,null));
        }

        if(param.getConfirmAttr() == null){
            param.setConfirmAttr(new DefaultLayout.KeyAttribute(DefaultLayout.Key.CONFIRM,0xff70d145,
                    context.getResources().getString(R.string.keyboard_confirm),-1,0xffffffff,null));
        }

        if(param.getNumKeyAttr() == null){
            param.setNumKeyAttr(new DefaultLayout.KeyAttribute(DefaultLayout.Key.NUM,0xfff5f5f9,
                    null,-1,0xff000000,null));
        }
    }

    public DefaultLayout getDefaultLayoutParam() {
        return lyParam;
    }

    public int[][] getLayoutStyle(int[][] buttons,int angle){
        if(lyParam.getLayoutStyle() == DefaultLayout.Style.STYLE_1){
            return layoutStyle1(buttons,angle);
        }else if(lyParam.getLayoutStyle() == DefaultLayout.Style.STYLE_2){
            return layoutStyle2(buttons,angle);
        }else if(lyParam.getLayoutStyle() == DefaultLayout.Style.STYLE_3){
            return layoutStyle3(buttons,angle);
        }else{
            return layoutStyle1(buttons,angle);
        }
    }
    private int[][] layoutStyle1(int[][] buttons,int angle){
        int[][] pinButtons = new int[15][];
        int[] invalidBtn = new int[]{-1,-1,-1,-1};
        for(int i=0;i<pinButtons.length;i++) {
            if (i == 11 || i == 13) {
                pinButtons[i] = invalidBtn;
            } else if (i == 3) {
                pinButtons[3] = buttons[12];
            } else if (i == 7) {
                if(angle == 180 || angle == 90){
                    System.arraycopy(buttons[7], 0, buttons[3], 0, 2);
                    pinButtons[7] = buttons[3];
                }else {
                    System.arraycopy(buttons[7], 2, buttons[3], 2, 2);
                    pinButtons[7] = buttons[3];
                }
            } else if (i == 12) {
                if(angle == 180 || angle == 270){
                    System.arraycopy(buttons[14], 0, buttons[13], 0, 2);
                    pinButtons[12] = buttons[13];
                }else {
                    System.arraycopy(buttons[14], 2, buttons[13], 2, 2);
                    pinButtons[12] = buttons[13];
                }
            } else if (i == 14) {
                if(angle == 180 || angle == 90){
                    System.arraycopy(buttons[15], 0, buttons[11], 0, 2);
                    pinButtons[14] = buttons[11];
                }else {
                    System.arraycopy(buttons[15], 2, buttons[11], 2, 2);
                    pinButtons[14] = buttons[11];
                }
            } else {
                pinButtons[i] = buttons[i];
            }
        }
        return pinButtons;
    }

    private int[][] layoutStyle2(int[][] buttons,int angle){
        int[][] pinButtons = new int[15][];
        int[] invalidBtn = new int[]{-1,-1,-1,-1};
        for(int i=0;i<pinButtons.length;i++){
            if(i==11||i==13){
                pinButtons[i] = invalidBtn;
            }else if(i==12){
                pinButtons[i] = buttons[13];
            }else if(i==14){
                if(angle == 180 || angle == 90){
                    System.arraycopy(buttons[15],0,buttons[11],0,2);
                    pinButtons[i] = buttons[11];
                }else {
                    System.arraycopy(buttons[15],2,buttons[11],2,2);
                    pinButtons[i] = buttons[11];
                }
            }else{
                pinButtons[i] = buttons[i];
            }
        }
        return pinButtons;
    }

    private int[][] layoutStyle3(int[][] buttons,int angle){
        int[][] pinButtons = new int[15][];
        int[] invalidBtn = new int[]{-1,-1,-1,-1};
        for(int i=0;i<pinButtons.length;i++){
            if(i==11||i==13){
                pinButtons[i] = invalidBtn;
            }else if(i==3){
                pinButtons[i] = buttons[3];
                if(angle == 180 || angle == 90){
                    System.arraycopy(buttons[7],0,buttons[3],0,2);
                }else {
                    System.arraycopy(buttons[7],2,buttons[3],2,2);
                }
            }else if(i==7){
                if(angle == 180 || angle == 90){
                    System.arraycopy(buttons[15],0,buttons[11],0,2);
                    pinButtons[i] = buttons[11];
                }else {
                    System.arraycopy(buttons[15],2,buttons[11],2,2);
                    pinButtons[i] = buttons[11];
                }
            }else if(i==14){
                if(angle == 180 || angle == 270){
                    System.arraycopy(buttons[14],0,buttons[13],0,2);
                    pinButtons[i] = buttons[13];
                }else {
                    System.arraycopy(buttons[14],2,buttons[13],2,2);
                    pinButtons[i] = buttons[13];
                }
            }else{
                pinButtons[i] = buttons[i];
            }
        }
        return pinButtons;
    }
}
