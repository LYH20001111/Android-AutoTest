package com.newland.nsdkdemo.internal.pin;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;

public class SecondDisplayPresentation extends Presentation{
    public final View contentView;



    public SecondDisplayPresentation(Context context,int layoutId,Display display){
        super(context,display);
        this.contentView = View.inflate(context,layoutId,null);
        initViews(this.contentView);
    }

    public SecondDisplayPresentation(View contentView, Display display) {
        super(contentView.getContext(), display);
        this.contentView = contentView;
        initViews(this.contentView);
    }


    protected void initViews(View contentView){

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (contentView.getParent() != null) {
            ((ViewGroup) contentView.getParent()).removeView(contentView);
        }
        setContentView(contentView);
    }


}
