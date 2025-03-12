package com.newland.autotest.fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.newland.autotest.adapter.RecycleAdapter;
import com.newland.autotest.annotation.TestItemClass;
import com.newland.autotest.base.fragment.BaseFragment;
import com.newland.autotest.base.item.BaseTestItem;
import com.newland.autotest.constant.Item;
import com.newland.autotest.databinding.AutoTestTestListFragmentBinding;

import java.util.ArrayList;

public class TestListFragment extends BaseFragment<AutoTestTestListFragmentBinding> {

    @Override
    protected void initData() {
        ArrayList<Item> items = new ArrayList<>();
        Class<?> fragmentClass = this.getClass();
        if (fragmentClass.isAnnotationPresent(TestItemClass.class)) {
            TestItemClass annotation = fragmentClass.getAnnotation(TestItemClass.class);
            Class<? extends BaseTestItem>[] testItemClasses = annotation.clz();
            if (testItemClasses != null) {
                for (Class<? extends BaseTestItem> testItemClass : testItemClasses) {
                    items.add(new Item(testItemClass));
                }
            }
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        viewBinding.rvType.setLayoutManager(linearLayoutManager);
        viewBinding.rvType.setAdapter(new RecycleAdapter(getActivity(), items));
    }

    @Override
    protected void initActionAfterInitData() {

    }

}
