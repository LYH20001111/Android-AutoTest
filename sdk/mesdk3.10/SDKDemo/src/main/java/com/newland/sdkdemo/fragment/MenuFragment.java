package com.newland.sdkdemo.fragment;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import com.newland.sdkdemo.AppConfig;
import com.newland.sdkdemo.MainActivity;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.adapter.ListViewAdapter;
import com.newland.sdkdemo.adapter.ListViewItem;
import com.newland.sdkdemo.event.ModuleClickListener;

import java.util.ArrayList;

/**
 * Author by bxy, Date on 2019/05/11 0011.
 */
public class MenuFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private ModuleClickListener listener;
    public MenuFragment(Context context, ModuleClickListener listener){
        super(context, LayoutMode.LINE);
        this.listener = listener;
    }

    @Override
    public String title() {
        return "MenuFragment";
    }

    @Override
    public void initData() {
        ArrayList<ListViewItem> objects = new ArrayList<ListViewItem>();
        for(int i = AppConfig.INDEX_FRAGMENT_START; i < MainActivity.modulesFragment.size(); i++){
            MainActivity.ModuleInfo moduleInfo = MainActivity.modulesFragment.get(i);
            objects.add(new ListViewItem(context.getString(moduleInfo.nameId)));
        }
        ListViewAdapter adapter = new ListViewAdapter(this.context, objects);
        adapter.setType(ListViewAdapter.TYPE1);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public Object getModule() {
        return null;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(listener!=null)
            listener.onModuleClickListener(position + AppConfig.INDEX_FRAGMENT_START);
    }
}
