package com.newland.ndk.pin;

import android.graphics.Point;
import android.util.DisplayMetrics;

import com.newland.ndk.NdkApiManager;

/**
 * Author by bxy, Date on 2018/11/15 0015.
 */
public class KeyBoardHelper {
    private static final String TAG = "KeyBoardHelper";
    private static final byte NUM_RANDOM = 0x00;
    private static final byte NUM_ASSIGN = 0x01;
    private static final byte KEYBOARD_RANDOM = 0x02;
    private byte[] coordinate;
    private byte randomLayout;
    private byte[] keySeq;
    public KeyBoardHelper(byte[] coordinate) {
        this.coordinate=coordinate;
        this.randomLayout=NUM_RANDOM;
    }
    public KeyBoardHelper(byte[] coordinate,String keySeq) {
        this.coordinate=coordinate;
        this.randomLayout=NUM_ASSIGN;
        this.keySeq=keySeq.getBytes();
    }
    public KeyBoardHelper(byte[] coordinate,byte[] keySeq) {
        this.coordinate=coordinate;
        this.randomLayout=KEYBOARD_RANDOM;
        this.keySeq=keySeq;
    }
    public byte[] getCoordinate() {
        return coordinate;
    }
    public byte getRandomLayout() {
        return randomLayout;
    }
    public byte[] getKeySeq() {
        return keySeq;
    }
    public void setCoordinate(byte[] coordinate) {
        this.coordinate = coordinate;
    }

    private final byte[] DEFAULT_LAYOUT = { (byte) 0x00, (byte) 0x88, (byte) 0x01, (byte) 0x89, (byte) 0x00, (byte) 0x91, (byte) 0x01, (byte) 0x0B,
            (byte) 0x01, (byte) 0x06, (byte) 0x01, (byte) 0x89, (byte) 0x01, (byte) 0x0F, (byte) 0x01, (byte) 0x0B, (byte) 0x01, (byte) 0x84, (byte) 0x01,
            (byte) 0x88, (byte) 0x01, (byte) 0x8C, (byte) 0x01, (byte) 0x0B, (byte) 0x02, (byte) 0x01, (byte) 0x01, (byte) 0x88, (byte) 0x00, (byte) 0x13,
            (byte) 0x01, (byte) 0x80, (byte) 0x00, (byte) 0x88, (byte) 0x01, (byte) 0xFE, (byte) 0x00, (byte) 0x91, (byte) 0x01, (byte) 0x80, (byte) 0x01,
            (byte) 0x06, (byte) 0x01, (byte) 0xFE, (byte) 0x01, (byte) 0x0F, (byte) 0x01, (byte) 0x80, (byte) 0x01, (byte) 0x84, (byte) 0x01, (byte) 0xFD,
            (byte) 0x01, (byte) 0x8C, (byte) 0x01, (byte) 0x80, (byte) 0x02, (byte) 0x01, (byte) 0x01, (byte) 0xFD, (byte) 0x00, (byte) 0x13, (byte) 0x01,
            (byte) 0xF5, (byte) 0x00, (byte) 0x88, (byte) 0x02, (byte) 0x78, (byte) 0x00, (byte) 0x99, (byte) 0x01, (byte) 0xF5, (byte) 0x01, (byte) 0x0E,
            (byte) 0x02, (byte) 0x78, (byte) 0x01, (byte) 0x1F, (byte) 0x01, (byte) 0xF5, (byte) 0x01, (byte) 0x94, (byte) 0x02, (byte) 0x78, (byte) 0x00,
            (byte) 0x19, (byte) 0x02, (byte) 0x6A, (byte) 0x00, (byte) 0x88, (byte) 0x02, (byte) 0XF0, (byte) 0x00, (byte) 0x99, (byte) 0x02, (byte) 0x6A,
            (byte) 0x01, (byte) 0x0E, (byte) 0x02, (byte) 0XF0, (byte) 0x01, (byte) 0x1F, (byte) 0x02, (byte) 0x6A, (byte) 0x01, (byte) 0x94, (byte) 0x02,
            (byte) 0XF0, (byte) 0x01, (byte) 0XA5, (byte) 0x01, (byte) 0xF5, (byte) 0x02, (byte) 0x8F, (byte) 0x02, (byte) 0x5A };

    private int[] recover(byte[] initCoordinate) {
        int[] orgCoordinate = new int[initCoordinate.length / 2];
        for (int i = 0; i < orgCoordinate.length; i++) {
            orgCoordinate[i] = initCoordinate[i * 2];
            orgCoordinate[i] = (orgCoordinate[i] << 8) | 0x00ff & initCoordinate[i * 2 + 1];
        }
        return orgCoordinate;
    }
    public byte[] loadRandomKeyboard(String nlBuildModel, String nlBuildTouchscreenResolution, DisplayMetrics displayMetrics){
        try {
            Point point = new Point();
            if (nlBuildModel.equals("N900")) {
                point = new Point(540, 864);
            } else if (nlBuildModel.equals("N850")) {
                point = new Point(600, 952);
            } else if(nlBuildModel.equals("N920")){
                point = new Point(720, 1184);
            }else {
                try {
                    String TOUCHSCREEN_RESOLUTION = nlBuildTouchscreenResolution;
                    int height = Integer.valueOf(TOUCHSCREEN_RESOLUTION.split("x")[0]);
                    int width = Integer.valueOf(TOUCHSCREEN_RESOLUTION.split("x")[1]);
                    point = new Point(width, height);
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
            int[] coordinateInt = recover(this.getCoordinate());
            for (int i = 0; i < coordinateInt.length; i++) {
                if (i % 2 == 0) {
                    coordinateInt[i] = coordinateInt[i] * point.x / displayMetrics.widthPixels;
                } else {
                    coordinateInt[i] = coordinateInt[i] * point.y / displayMetrics.heightPixels;
                }
            }
            byte[] initCoordinate = new byte[coordinateInt.length * 2];
            for (int i = 0, j = 0; i < coordinateInt.length; i++, j++) {
                initCoordinate[j] = (byte) ((coordinateInt[i] >> 8) & 0xff);
                j++;
                initCoordinate[j] = (byte) (coordinateInt[i] & 0xff);
            }
            this.setCoordinate(initCoordinate);

            byte[] fiveKey = new byte[]{0x1b,0x0a,0x0d,0x2e,0x1c};
            int offset = 0;
            int funOffset = 0,n2 = 0,n1 = 0,numBtnOffset = 0;
            byte[] x1 = new byte[2],y1 = new byte[2],x2 = new byte[2],y2 = new byte[2],funKey = new byte[36],numBtn = new byte[80];
            byte[] outSeq = new byte[10],randombuf = new byte[15],pOutSeq;
            int funcRamFlag = 0,numRamFlag = 0;
            if(randomLayout == NUM_RANDOM || randomLayout == NUM_ASSIGN) {
                for (int i = 0; i < 15; i++) {
                    System.arraycopy(initCoordinate, offset, x1, 0, 2);
                    x1 = endianSwab16(x1);
                    offset += 2;
                    System.arraycopy(initCoordinate, offset, y1, 0, 2);
                    y1 = endianSwab16(y1);
                    offset += 2;
                    System.arraycopy(initCoordinate, offset, x2, 0, 2);
                    x2 = endianSwab16(x2);
                    offset += 2;
                    System.arraycopy(initCoordinate, offset, y2, 0, 2);
                    y2 = endianSwab16(y2);
                    offset += 2;
                    if (i == 11 || i == 13) {
                        continue;
                    } else if (i == 3 || i == 7 || i == 14) {
                        byte[] temp = new byte[4];
                        System.arraycopy(fiveKey, funOffset / 12, temp, 0, 1);
                        System.arraycopy(temp, 0, funKey, funOffset, 4);
                        funOffset += 4;
                        System.arraycopy(x1, 0, funKey, funOffset, 2);
                        funOffset += 2;
                        System.arraycopy(y1, 0, funKey, funOffset, 2);
                        funOffset += 2;
                        System.arraycopy(x2, 0, funKey, funOffset, 2);
                        funOffset += 2;
                        System.arraycopy(y2, 0, funKey, funOffset, 2);
                        funOffset += 2;
                    } else {
                        if (randomLayout == NUM_ASSIGN)
                            numBtnOffset = (keySeq[n2++] - '0') * 8;
                        System.arraycopy(x1, 0, numBtn, numBtnOffset, 2);
                        numBtnOffset += 2;
                        System.arraycopy(y1, 0, numBtn, numBtnOffset, 2);
                        numBtnOffset += 2;
                        System.arraycopy(x2, 0, numBtn, numBtnOffset, 2);
                        numBtnOffset += 2;
                        System.arraycopy(y2, 0, numBtn, numBtnOffset, 2);
                        numBtnOffset += 2;
                    }
                }

                if (randomLayout == NUM_RANDOM)
                    pOutSeq = outSeq;
                else
                    pOutSeq = null;
//                Log.d(TAG,"loadRandomKeyboard randomLayout="+randomLayout);
//                if(numBtn!=null)
//                    Log.d(TAG, "loadRandomKeyboard: numBtn="+ISOUtils.hexString(numBtn));
//                if(funKey!=null)
//                    Log.d(TAG, "loadRandomKeyboard: funKey="+ISOUtils.hexString(funKey));
//                if(pOutSeq!=null)
//                    Log.d(TAG, "loadRandomKeyboard: pOutSeq="+ISOUtils.hexString(pOutSeq));
                int ret = NdkApiManager.getNdkApiManager().getSecN().NDK_SecVppTpInit(numBtn, funKey, pOutSeq);
                if (ret != 0) {
                    return null;
                }
                n2 = 0;
                for (int i = 0; i < 15; i++) {
                    if (i == 11 || i == 13) {
                        if (i == 11) {
                            randombuf[i] = 0x2e;
                        }
                        if (i == 13) {
                            randombuf[i] = 0x1c;
                        }
                    } else if (i == 3 || i == 7 || i == 14) {
                        randombuf[i] = (byte) fiveKey[n1++];
                    } else {
                        if (randomLayout == NUM_RANDOM)
                            randombuf[i] = pOutSeq[n2++];
                        if (randomLayout == NUM_ASSIGN)
                            randombuf[i] = keySeq[n2++];
                    }
                }
            }

            if(randomLayout == KEYBOARD_RANDOM) {
                byte[] mRandom = new byte[1];
                if (initCoordinate.length != keySeq.length * 8) {
                    return null;
                }
    //          ifesc = 0;
                for (int i = 0; i < keySeq.length; i++) {
                    if (keySeq[i] == 0x7E)
                        numRamFlag++;
                    if (keySeq[i] == 0x7F)
                        funcRamFlag++;
                    if (keySeq[i] == 0x9c) ;
    //                  ifesc = 1;
                }
//                Log.d(TAG, "loadRandomKeyboard: numRamFlag="+numRamFlag+" funcRamFlag="+funcRamFlag);
                if (numRamFlag >= 0 && numRamFlag < 10) {
                    for (int i = 0; i < numRamFlag; i++) {
                        while (true) {
                            int ret = NdkApiManager.getNdkApiManager().getSecN().NDK_SecGetRandom(1, mRandom);
                            if (ret != 0) {
                                return null;
                            }
                            mRandom[0] = (byte) (Math.abs(mRandom[0])% 10);
                            mRandom[0] += '0';
                            int j = 0;
                            for (j = 0; j < keySeq.length; j++) {
                                if (mRandom[0] == keySeq[j])
                                    break;
                            }
                            if (j == keySeq.length) {
                                for (n2 = 0; n2 < keySeq.length; n2++) {
                                    if (keySeq[n2] == 0x7E) {
                                        keySeq[n2] = (byte) mRandom[0];
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
                if (funcRamFlag>0) {
                    for (int i = 0; i < funcRamFlag; i++) {
                        while (true) {
                            int ret = NdkApiManager.getNdkApiManager().getSecN().NDK_SecGetRandom(1, mRandom);
                            if (ret != 0) {
                                return null;
                            }
                            mRandom[0] = (byte) (Math.abs(mRandom[0])%(keySeq.length - 10));
                            mRandom[0] = fiveKey[mRandom[0]];
                            int j = 0;
                            for (j = 0; j < keySeq.length; j++) {
                                if (mRandom[0] == keySeq[j])
                                    break;
                            }
                            if (j == keySeq.length) {
                                for (n2 = 0; n2 < keySeq.length; n2++) {
                                    if (keySeq[n2] == 0x7F) {
                                        keySeq[n2] = (byte) mRandom[0];
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
                n2 = 0;
                offset = 0;
                for (int i = 0; i < keySeq.length; i++) {
                    System.arraycopy(initCoordinate, offset, x1, 0, 2);
                    x1 = endianSwab16(x1);
                    offset += 2;
                    System.arraycopy(initCoordinate, offset, y1, 0, 2);
                    y1 = endianSwab16(y1);
                    offset += 2;
                    System.arraycopy(initCoordinate, offset, x2, 0, 2);
                    x2 = endianSwab16(x2);
                    offset += 2;
                    System.arraycopy(initCoordinate, offset, y2, 0, 2);
                    y2 = endianSwab16(y2);
                    offset += 2;
                    if (numRamFlag >= 0 && numRamFlag < 10) {
                        if (keySeq[n2] >= '0' && keySeq[n2] <= '9') {
                            numBtnOffset = (keySeq[n2] - '0') * 8;
                            System.arraycopy(x1, 0, numBtn, numBtnOffset, 2);
                            numBtnOffset += 2;
                            System.arraycopy(y1, 0, numBtn, numBtnOffset, 2);
                            numBtnOffset += 2;
                            System.arraycopy(x2, 0, numBtn, numBtnOffset, 2);
                            numBtnOffset += 2;
                            System.arraycopy(y2, 0, numBtn, numBtnOffset, 2);
                            numBtnOffset += 2;
                        }
                    } else if (numRamFlag == 10) {
                        if (keySeq[n2] == 0x7E) {
                            System.arraycopy(x1, 0, numBtn, numBtnOffset, 2);
                            numBtnOffset += 2;
                            System.arraycopy(y1, 0, numBtn, numBtnOffset, 2);
                            numBtnOffset += 2;
                            System.arraycopy(x2, 0, numBtn, numBtnOffset, 2);
                            numBtnOffset += 2;
                            System.arraycopy(y2, 0, numBtn, numBtnOffset, 2);
                            numBtnOffset += 2;
                        }
                    }

                    if (keySeq[n2] == 0x1B || keySeq[n2] == 0x0A || keySeq[n2] == 0x0D || keySeq[n2] == 0x9b || keySeq[n2] == 0x9c) {
                        byte[] temp = new byte[4];
                        System.arraycopy(keySeq, n2, temp, 0, 1);
                        System.arraycopy(temp, 0, funKey, funOffset, 4);
                        funOffset += 4;
                        System.arraycopy(x1, 0, funKey, funOffset, 2);
                        funOffset += 2;
                        System.arraycopy(y1, 0, funKey, funOffset, 2);
                        funOffset += 2;
                        System.arraycopy(x2, 0, funKey, funOffset, 2);
                        funOffset += 2;
                        System.arraycopy(y2, 0, funKey, funOffset, 2);
                        funOffset += 2;
                    }
                    n2++;
                }
                if (numRamFlag == 10)
                    pOutSeq = outSeq;
                else
                    pOutSeq = null;
//                Log.d(TAG,"loadRandomKeyboard randomLayout=2");
//                if(numBtn!=null)
//                    Log.d(TAG, "loadRandomKeyboard: numBtn="+ISOUtils.hexString(numBtn));
//                if(funKey!=null)
//                    Log.d(TAG, "loadRandomKeyboard: funKey="+ISOUtils.hexString(funKey));
//                if(pOutSeq!=null)
//                    Log.d(TAG, "loadRandomKeyboard: pOutSeq="+ISOUtils.hexString(pOutSeq));
                int ret = NdkApiManager.getNdkApiManager().getSecN().NDK_SecVppTpInit(numBtn, funKey, pOutSeq);
                if (ret != 0) {
                    return null;
                }
                n2 = 0;
                for (int i = 0; i < keySeq.length; i++) {
                    if (keySeq[i] >= '0' && keySeq[i] <= '9'){
                        randombuf[i] = keySeq[i];
                    }
                    if (keySeq[i] == 0x7E)
                        randombuf[i] = pOutSeq[n2++];
                    if (keySeq[i] == 0x1B || keySeq[i] == 0x0A || keySeq[i] == 0x0D || keySeq[i] == 0x2E || keySeq[i] == 0x1C) {
                        randombuf[i] = keySeq[i];
                    }
                }
            }
//            if(randombuf != null){
//                Log.d(TAG, "loadRandomKeyboard: randombuf="+ISOUtils.hexString(randombuf));
//            }
            return randombuf;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] endianSwab16(byte[] data){
        try {
            byte[] temp = new byte[2];
            temp[0] = data[1];
            temp[1] = data[0];
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
