package com.hudou.autotest.fragment;

import com.hudou.autotest.annotation.Navigation;
import com.hudou.autotest.base.fragment.BaseFragment;
import com.hudou.autotest.databinding.AutoTestHomeFragmentBinding;

@Navigation(name = HomeFragment.NAV_NAME)
public final class HomeFragment extends BaseFragment<AutoTestHomeFragmentBinding> {
    static final String NAV_NAME = "首页";
}
