package com.hudou.autotest.fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hudou.autotest.adapter.MyRecycleAdapter;
import com.hudou.autotest.annotation.TestItemClass;
import com.hudou.autotest.base.fragment.BaseFragment;
import com.hudou.autotest.base.item.BaseTestItem;
import com.hudou.autotest.constant.Item;
import com.hudou.autotest.databinding.AutoTestTestListFragmentBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class TestListFragment extends BaseFragment<AutoTestTestListFragmentBinding> {

    @Override
    public void initData() {
        ArrayList<Item> items = new ArrayList<>();
        Class<?> fragmentClass = this.getClass();
        if (fragmentClass.isAnnotationPresent(TestItemClass.class)) {
            TestItemClass annotation = fragmentClass.getAnnotation(TestItemClass.class);
            Class<? extends BaseTestItem>[] testItemClasses = annotation.clz();

            //去除重复的class
            Set<Class<? extends BaseTestItem>> testItemSet = new LinkedHashSet<>(Arrays.asList(testItemClasses));
            Class<? extends BaseTestItem>[] testItems = testItemSet.toArray(new Class[0]);

            for (Class<? extends BaseTestItem> testItemClass : testItems) {
                items.add(new Item(testItemClass));
            }
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        viewBinding.rvType.setLayoutManager(linearLayoutManager);
        viewBinding.rvType.setAdapter(new MyRecycleAdapter(getActivity(), items));
    }



}
