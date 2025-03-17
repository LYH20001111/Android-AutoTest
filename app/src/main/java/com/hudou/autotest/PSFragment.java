package com.hudou.autotest;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hudou.autotest.annotation.Navigation;
import com.hudou.autotest.annotation.TestItemClass;
import com.hudou.autotest.fragment.TestListFragment;


@Navigation(name = "PS")
@TestItemClass(clz = {TestItem1.class, TestItem2.class})
public class PSFragment extends TestListFragment {
}
