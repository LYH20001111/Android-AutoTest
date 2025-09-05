package com.hudou.autotest.constant;

import android.graphics.Color;

import androidx.annotation.IntegerRes;

public class ChildModel {
    private int childIcon;
    private String childName;

    private int color;

    public ChildModel(@IntegerRes int childIcon, String childName) {
        this.childIcon = childIcon;
        this.childName = childName;
        this.color = Color.BLUE;
    }

    public ChildModel(@IntegerRes int childIcon, String childName, int color) {
        this.childIcon = childIcon;
        this.childName = childName;
        this.color = color;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public int getChildIcon() {
        return childIcon;
    }

    public String getChildName() {
        return childName;
    }

    public int getColor() {
        return color;
    }
}