package com.newland.autotest.constant;

import android.graphics.Color;

public class ShowMessage {
    private int color = Color.BLACK;
    private String message;

    public ShowMessage(String message) {
        this.message = message;
    }

    public ShowMessage(int color, String message) {
        this.color = color;
        this.message = message;
    }

    public int getColor() {
        return color;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ShowMessage{" +
                "color=" + color +
                ", message='" + message + '\'' +
                '}';
    }
}
