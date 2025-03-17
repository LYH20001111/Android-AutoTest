package com.hudou.autotest.navigation;


import com.hudou.autotest.item.TestItem1;
import com.hudou.autotest.item.TestItem2;
import com.hudou.autotest.annotation.Navigation;
import com.hudou.autotest.annotation.TestItemClass;
import com.hudou.autotest.fragment.TestListFragment;


@Navigation(name = "PS")
@TestItemClass(clz = {TestItem1.class, TestItem2.class})
public class PSFragment extends TestListFragment {
}
