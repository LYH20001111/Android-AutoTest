package com.newland.sdk.scanner;

/**
 * @author youjf
 * @description
 * @date 2020/5/29
 * @since V3.10.01
 */
public class ResultPoint {
    private final float x;
    private final float y;

    public ResultPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public final float getX() {
        return this.x;
    }

    public final float getY() {
        return this.y;
    }

}
