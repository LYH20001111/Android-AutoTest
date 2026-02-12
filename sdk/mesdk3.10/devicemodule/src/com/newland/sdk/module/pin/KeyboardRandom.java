package com.newland.sdk.module.pin;

import android.view.View;

public class KeyboardRandom {
    /**
     * Digit random layout
     */
    private static final byte NUM_RANDOM = 0x00;
    /**
     * Digit appointed layout
     */
    private static final byte NUM_ASSIGN = 0x01;
    /**
     * Full keyboard self-defined layout
     */
    private static final byte KEYBOARD_RANDOM = 0x02;
    /**
     * Key coordinate
     */
    private byte[] coordinate;

    /**
     * Layout type
     */
    private byte randomLayout;

    /**
     * Key value
     */
    private byte[] keySeq;

    public enum PinKeySeq {
        /**
         * random number<p>
         *
         */
        NUM_RANDOM,
        /**
         * normol model<p>
         *
         */
        NORMAL,
        /**
         * random all<p>
         *
         */
        RANDOM_ALL;
    }
    /**
     * Random layout (only digits are random and the function keys are fixed)
     *
     * @param coordinate Digit key coordinates (a key position includes the upper left and lower right coordinates and each coordinate has four bytes)
     */
    public KeyboardRandom(byte[] coordinate) {
        this.coordinate = coordinate;
        this.randomLayout = NUM_RANDOM;
    }
    /**
     *
     * Set the keyboard layout
     * @param btns
     *            keyboard button object（The object of the incoming order  digit keys（0x30~0x39）, cancel key（0x1B）,backspace key（0x0A），confirm key（0x0D），# key（0x1C）star key（0x2E）<p>
     *  @param pinKeySeq  keyboard layout type selection
     */

    public KeyboardRandom(View[] btns, PinKeySeq pinKeySeq){
        switch (pinKeySeq){
            case NUM_RANDOM:
                this.randomLayout=NUM_RANDOM;
                break;
            case NORMAL:
                this.randomLayout=KEYBOARD_RANDOM;
                this.keySeq=new byte[] { 0x31, 0x32, 0x33, 0x1B, 0x34, 0x35, 0x36,
                        0x0A, 0x37, 0x38, 0x39, 0x2E, 0x30, 0x1C, 0x0D };
                break;
            case RANDOM_ALL:
                this.randomLayout=KEYBOARD_RANDOM;
                this.keySeq=new byte[] { 0x7E, 0x7E, 0x7E, 0x7F, 0x7E, 0x7E, 0x7E, 0x7F,
                        0x7E, 0x7E, 0x7E, 0x2E, 0x7E, 0x1C, 0x7F };
                break;
        }

        View iv0 = btns[0],iv1 = btns[1],iv2=btns[2],iv3=btns[3],iv4=btns[4],iv5 = btns[5],iv6 = btns[6],iv7=btns[7],iv8=btns[8],iv9=btns[9];
        View ivCancel=btns[10],ivDelete=btns[11],ivConfirm=btns[12];

        int x0 = getX(iv1), x01=x0+getW(iv1), x1 = getX(iv2),x11=x1+getW(iv2),x2 = getX(iv3),x21=x2+getW(iv3), x3 = getX(iv4),x31=x3+getW(iv4), x4 = getX(iv5),x41=x4+getW(iv5);
        int y0 = getY(iv1), y01=y0+getH(iv1), y1 = getY(iv2),y11=y1+getH(iv2),y2 = getY(iv3),y21=y2+getH(iv3), y3 = getY(iv4),y31=y3+getH(iv4), y4 = getY(iv5),y41=y4+getH(iv5);
        int x5 = getX(iv6), x51=x5+getW(iv6), x6 = getX(iv7),x61=x6+getW(iv7),x7 = getX(iv8),x71=x7+getW(iv8), x8 = getX(iv9),x81=x8+getW(iv9);
        int y5 = getY(iv6), y51=y5+getH(iv6), y6 = getY(iv7),y61=y6+getH(iv7),y7 = getY(iv8),y71=y7+getH(iv8), y8 = getY(iv9),y81=y8+getH(iv9);

        int xc=getX(ivCancel),xc1=xc+getW(ivCancel),yc=getY(ivCancel),yc1=yc+getH(ivCancel);
        int xe=getX(ivConfirm),xe1=xe+getW(ivConfirm),ye=getY(ivConfirm),ye1=ye+getH(ivConfirm);
        int xd=getX(ivDelete),xd1=xd+getW(ivDelete),yd=getY(ivDelete),yd1=yd+getH(ivDelete);

        int xz=getX(iv0),xz1=xz+getW(iv0),yz=getY(iv0),yz1=yz+getH(iv0);

        int xj=0,xj1=0,yj=0,yj1=0;
        int xx=0,xx1=0,yx=0,yx1=0;
        if(btns.length>13){
            View ivj=btns[13],ivx=btns[14];
            xj=getX(ivj);xj1=xj+getW(ivj);yj=getY(ivj);yj1=yj+getH(ivj);
            xx=getX(ivx);xx1=xx+getW(ivx);yx=getY(ivx);yx1=yx+getH(ivx);
        }
        int[] coordinateInt = new int[]{
                //1
                x0, y0, x01, y01,
                //2
                x1, y1, x11, y11,
                //3
                x2, y2, x21, y21,
                //cancel
                xc, yc, xc1, yc1,

                //4
                x3, y3, x31, y31,
                //5
                x4, y4, x41, y41,
                //6
                x5, y5, x51, y51,
                //delete
                xd, yd, xd1, yd1,

                //7
                x6, y6, x61, y61,
                //8
                x7, y7, x71, y71,
                //9
                x8, y8, x81, y81,


                //# key
                xj, yj,xj1, yj1,
                //0
                xz, yz, xz1, yz1,
                //* key
                xx, yx, xx1, yx1,

                //confirm
                xe, ye, xe1, ye1,};
        byte[] initCoordinate = new byte[coordinateInt.length * 2];
        for (int i = 0, j = 0; i < coordinateInt.length; i++, j++) {
            initCoordinate[j] = (byte) ((coordinateInt[i] >> 8) & 0xff);
            j++;
            initCoordinate[j] = (byte) (coordinateInt[i] & 0xff);
        }
        this.coordinate=initCoordinate;


    }
    /**
     * Digit appointed layout（(only digits are random and the function keys are fixed)
     *
     * @param coordinate Digit keyboard coordinates (a key position includes the upper left and lower right coordinates and each coordinate has four bytes)
     * @param keySeq     Key value series（e.g.："1234567890"，from left to right and from up to down on the keyboard）
     */
    public KeyboardRandom(byte[] coordinate, String keySeq) {
        this.coordinate = coordinate;
        this.randomLayout = NUM_ASSIGN;
        this.keySeq = keySeq.getBytes();
    }

    /**
     * Full keyboard appointed layout (both digit and function keys are random)
     *
     * @param coordinate Full keyboard coordinates include function keys a key position includes the upper left and lower right coordinates and each coordinate has four bytes)
     * @param keySeq     Full keyboard key value series include function and digit keys（0x30~0x39）, cancel key（0x1B）,backspace key（0x0A），confirm key（0x0D），# key（0x1C）star key（0x2E）
     *                   If digits are random, the corresponding positions are replaced by 0x7E and if function keys are random, the corresponding positions are replaced by0x7F.
     */
    public KeyboardRandom(byte[] coordinate, byte[] keySeq) {
        this.coordinate = coordinate;
        this.randomLayout = KEYBOARD_RANDOM;
        this.keySeq = keySeq;
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
    /**
     * Access controls the x coordinate
     *
     * @param view button control
     */
    private int getX(View view) {
        int[] local = new int[2];
        view.getLocationOnScreen(local);
        return local[0];
    }

    /**
     * Access controls the y coordinate
     *
     * @param view button control
     */
    private int getY(View view) {
        int[] local = new int[2];
        view.getLocationOnScreen(local);
        return local[1];
    }

    /**
     * Access controls the width
     *
     * @param view button control
     */
    private int getW(View view) {
        int width = view.getMeasuredWidth();
        return width;
    }

    /**
     * Access controls the hight
     *
     * @param view button control
     */
    private int getH(View view) {

        int height = view.getMeasuredHeight();
        return height;
    }
}
