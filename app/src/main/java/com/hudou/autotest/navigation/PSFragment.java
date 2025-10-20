package com.hudou.autotest.navigation;


import android.os.Bundle;
;
import androidx.annotation.Nullable;

import com.hudou.autotest.fragment.AutoTestTestListFragment;
import com.hudou.autotest.item.TestItem1;
import com.hudou.autotest.item.TestItem2;
import com.hudou.autotest.annotation.Navigation;
import com.hudou.autotest.annotation.TestItemClass;


@Navigation(name = "AutoTest")
@TestItemClass(clz = {TestItem1.class, TestItem2.class})
public class PSFragment extends AutoTestTestListFragment {

    @Override
    public void onCreate(@Nullable Bundle bundle) {
//        setTitleSize(25);
        super.onCreate(bundle);
    }

    @Override
    public String onNameTitle() {
        return "AutoTest 测试项";
    }
}
