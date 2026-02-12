package com.newland.nsdkdemo.common.fragment;

public class ModuleInfo {
    public BaseFragment fragment;
    public int nameId;
    public int picId;

    public ModuleInfo(BaseFragment fragment, int nameId, int picId) {
        this.fragment = fragment;
        this.nameId = nameId;
        this.picId = picId;
    }
}
