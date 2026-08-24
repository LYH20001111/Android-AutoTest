package com.hudou.autotest.base.activity;

import androidx.fragment.app.Fragment;

import java.util.List;

public interface AutoTestInterface {
    void addNavigationFragment(List<Fragment> list);
    boolean isPhysicalKeyboard();
}
