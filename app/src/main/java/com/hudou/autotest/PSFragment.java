package com.hudou.autotest;


import android.util.Log;

import com.hudou.autotest.annotation.Navigation;
import com.hudou.autotest.annotation.TestItemClass;
import com.hudou.autotest.fragment.TestListFragment;


@Navigation(name = "PS")
@TestItemClass(clz = {TestItem1.class, TestItem2.class})
public class PSFragment extends TestListFragment {
}
