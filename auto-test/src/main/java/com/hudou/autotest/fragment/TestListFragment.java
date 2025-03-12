package com.hudou.autotest.fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hudou.autotest.adapter.RecycleAdapter;
import com.hudou.autotest.annotation.TestItemClass;
import com.hudou.autotest.base.fragment.BaseFragment;
import com.hudou.autotest.base.item.BaseTestItem;
import com.hudou.autotest.constant.Item;
import com.hudou.autotest.databinding.AutoTestTestListFragmentBinding;

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
