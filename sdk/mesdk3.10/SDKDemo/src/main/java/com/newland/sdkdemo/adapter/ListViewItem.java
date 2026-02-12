package com.newland.sdkdemo.adapter;
/**
 * Author by bxy, Date on 2018/11/14 0014.
 */
public class ListViewItem {
    public String title;
    public String desc;
    public int index;

    public ListViewItem(String title,String desc,int index) {
        this.title = title;
        this.desc = desc;
        this.index = index;
    }

    public ListViewItem(String title,String desc) {
        this.title = title;
        this.desc = desc;
    }

    public ListViewItem(String title){
        this.title = title;
    }
}
