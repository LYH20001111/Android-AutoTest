package com.newland.nsdkdemo.common.fragment;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;

import com.newland.nsdkdemo.common.MainActivity;
import com.newland.nsdkdemo.common.AppConfig;
import com.newland.nsdkdemo.common.adapter.DividerGridItemDecoration;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.adapter.RecyclerViewAdapter;
import com.newland.nsdkdemo.common.annotation.MethodGridBean;
import com.newland.nsdkdemo.common.event.ModuleClickListener;

import java.util.ArrayList;

public class ModulesFragment extends BaseFragment {

    private ModuleClickListener listener;

    public ModulesFragment(Context context, ModuleClickListener listener) {
        super(context, LayoutMode.GRID);
        this.listener = listener;
    }

    @Override
    public String title() {
        return "NSDKDemo";
    }

    @Override
    public void initData() {
        ArrayList<MethodGridBean> objects = new ArrayList<MethodGridBean>();
        for (int i = AppConfig.INDEX_FRAGMENT_START; i < MainActivity.moduleFragments.size(); i++) {
            ModuleInfo moduleInfo = MainActivity.moduleFragments.get(i);
            objects.add(new MethodGridBean(moduleInfo.nameId, moduleInfo.picId));
        }
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(objects, RecyclerViewAdapter.RecyclerViewType.ROW3_BIG);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        recyclerViewAdapter.setOnItemClickLitener(new RecyclerViewAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(int position) {
                if (listener != null) {
                    listener.onModuleClickListener(position + AppConfig.INDEX_FRAGMENT_START);
                }
            }
        });
        recycleview.addItemDecoration(new DividerGridItemDecoration(context));
        recycleview.setLayoutManager(layoutManager);
        recycleview.setAdapter(recyclerViewAdapter);
    }

    @Override
    public Object getModule() {
        return null;
    }

}
