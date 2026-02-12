package com.newland.sdkdemo.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Author by bxy, Date on 2018/11/13 0013.
 */
@SuppressLint("ValidFragment")
public class BridgeFragment extends Fragment {

    private BaseFragment currPager;

    @SuppressLint("ValidFragment")
    public BridgeFragment(BaseFragment pager) {
        this.currPager=pager;
    }

    public BridgeFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(currPager != null){
            return currPager.getRootView();
        }
        return null;
    }
}
