package com.hudou.autotest.navigation;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hudou.autotest.MainActivity;
import com.hudou.autotest.customUI.dialog.DialogUtils;
import com.hudou.autotest.customUI.dialog.listener.NotifyDialogListener;
import com.hudou.autotest.fragment.AutoTestTestListFragment;
import com.hudou.autotest.item.TestItem1;
import com.hudou.autotest.item.TestItem2;
import com.hudou.autotest.annotation.Navigation;
import com.hudou.autotest.annotation.TestItemClass;


@Navigation(name = "PS")
@TestItemClass(clz = {TestItem1.class, TestItem2.class})
public class PSFragment extends AutoTestTestListFragment {
    @Override
    public String onTestItemName() {
        return null;
    }
}
