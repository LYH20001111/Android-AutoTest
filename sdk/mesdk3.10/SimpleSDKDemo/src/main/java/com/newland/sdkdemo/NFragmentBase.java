package com.newland.sdkdemo;

import com.newland.basetest.base3.BaseFragment3;

public abstract class NFragmentBase extends BaseFragment3 {
    public MainActivity getMainActivity(){
        return (MainActivity)getActivity();
    }

}
