package com.newland.sdkdemo.fragment;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;

import com.newland.sdkdemo.AppConfig;
import com.newland.sdkdemo.MainActivity;
import com.newland.sdkdemo.adapter.DividerGridItemDecoration;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.adapter.RecyclerViewAdapter;
import com.newland.sdkdemo.annotation.MethodGridBean;
import com.newland.sdkdemo.event.ModuleClickListener;

import java.util.ArrayList;

/**
 * Author by bxy, Date on 2018/11/13 0013.
 */
public class ModulesFragment extends BaseFragment{

    private ModuleClickListener listener;
    public ModulesFragment(Context context, ModuleClickListener listener){
        super(context, LayoutMode.GRID);
        this.listener = listener;
    }

    @Override
    public String title() {
        return "SDKDemo";
    }

    @Override
    public void initData() {
        ArrayList<MethodGridBean> objects = new ArrayList<MethodGridBean>();
        for(int i = AppConfig.INDEX_FRAGMENT_START; i < MainActivity.modulesFragment.size(); i++){
            MainActivity.ModuleInfo moduleInfo = MainActivity.modulesFragment.get(i);
            objects.add(new MethodGridBean(moduleInfo.nameId,moduleInfo.picId));
        }
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(objects, RecyclerViewAdapter.RecyclerViewType.ROW3_BIG);
        GridLayoutManager layoutManager = new GridLayoutManager(context,3);
        recyclerViewAdapter.setOnItemClickLitener(new RecyclerViewAdapter.OnItemClickLitener(){
            @Override
            public void onItemClick(int position) {
                if(listener!=null){
                    listener.onModuleClickListener(position+ AppConfig.INDEX_FRAGMENT_START);
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
