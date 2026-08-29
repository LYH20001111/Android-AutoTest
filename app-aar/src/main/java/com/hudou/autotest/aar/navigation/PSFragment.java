package com.hudou.autotest.aar.navigation;


import android.os.Bundle;

import androidx.annotation.Nullable;

import com.hudou.autotest.annotation.Navigation;
import com.hudou.autotest.annotation.TestItemClass;
import com.hudou.autotest.fragment.AutoTestTestListFragment;
import com.hudou.autotest.aar.item.TestItem1;
import com.hudou.autotest.aar.item.TestItem2;
import com.hudou.autotest.aar.item.TestItem3;
import com.hudou.autotest.aar.item.TestItem4;


@Navigation(name = "AutoTest")
@TestItemClass(clz = {TestItem1.class, TestItem2.class, TestItem3.class, TestItem4.class})
public class PSFragment extends AutoTestTestListFragment {

    @Override
    public void onCreate(@Nullable Bundle bundle) {
//        setTitleSize(25);
        super.onCreate(bundle);
    }

    @Override
    public String onNameTitle() {
        return "AutoTest AAR 测试项";
    }
}
