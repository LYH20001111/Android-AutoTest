package com.hudou.autotest.fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hudou.autotest.adapter.MyRecycleAdapter;
import com.hudou.autotest.annotation.TestItemClass;
import com.hudou.autotest.base.fragment.BaseFragment;
import com.hudou.autotest.base.item.AutoTestTestItem;
import com.hudou.autotest.constant.Item;
import com.hudou.autotest.databinding.AutoTestTestListFragmentBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AutoTestTestListFragment extends BaseFragment<AutoTestTestListFragmentBinding> {
    private static float titleSize = 30;

    @Override
    public void onInitData() {
        ArrayList<Item> items = new ArrayList<>();
        Class<?> fragmentClass = this.getClass();
        if (fragmentClass.isAnnotationPresent(TestItemClass.class)) {
            TestItemClass annotation = fragmentClass.getAnnotation(TestItemClass.class);
            Class<? extends AutoTestTestItem>[] testItemClasses = annotation.clz();

            //去除重复的class
            Set<Class<? extends AutoTestTestItem>> testItemSet = new LinkedHashSet<>(Arrays.asList(testItemClasses));
            Class<? extends AutoTestTestItem>[] testItems = testItemSet.toArray(new Class[0]);

            for (Class<? extends AutoTestTestItem> testItemClass : testItems) {
                items.add(new Item(testItemClass));
            }
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        viewBinding.rvType.setLayoutManager(linearLayoutManager);
        // //初始化分隔线、添加分隔线
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//        viewBinding.rvType.addItemDecoration(dividerItemDecoration);

        viewBinding.rvType.setAdapter(new MyRecycleAdapter(getActivity(), items));
        String applicationName = onNameTitle();
        viewBinding.tvAppName.setTextSize(titleSize);
        if (applicationName != null && !"".equals(applicationName)) {
            viewBinding.tvAppName.setText(applicationName);
        }
    }

    public static void setTitleSize(float size){
        titleSize = size;
    }

    public abstract String onNameTitle();



}
