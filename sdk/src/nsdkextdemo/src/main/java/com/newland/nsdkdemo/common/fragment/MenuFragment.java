package com.newland.nsdkdemo.common.fragment;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import com.newland.nsdkdemo.common.MainActivity;
import com.newland.nsdkdemo.common.AppConfig;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.adapter.ListViewAdapter;
import com.newland.nsdkdemo.common.adapter.ListViewItem;
import com.newland.nsdkdemo.common.event.ModuleClickListener;

import java.util.ArrayList;

public class MenuFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private ModuleClickListener listener;

    public MenuFragment(Context context, ModuleClickListener listener) {
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
        for (int i = AppConfig.INDEX_FRAGMENT_START; i < MainActivity.moduleFragments.size(); i++) {
            ModuleInfo moduleInfo = MainActivity.moduleFragments.get(i);
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
        if (listener != null)
            listener.onModuleClickListener(position + AppConfig.INDEX_FRAGMENT_START);
    }
}
