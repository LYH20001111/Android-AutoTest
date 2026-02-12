package com.newland.nsdkdemo.common.fragment;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.MainActivity;
import com.newland.nsdkdemo.common.SDKExecutors;
import com.newland.nsdkdemo.common.adapter.DividerGridItemDecoration;
import com.newland.nsdkdemo.common.adapter.ExpandableListViewAdapter;
import com.newland.nsdkdemo.common.adapter.ExpandableListViewParent;
import com.newland.nsdkdemo.common.adapter.ExpandableListViewSon;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.adapter.ListViewAdapter;
import com.newland.nsdkdemo.common.adapter.ListViewItem;
import com.newland.nsdkdemo.common.adapter.RecyclerViewAdapter;
import com.newland.nsdkdemo.common.annotation.MethodBean;
import com.newland.nsdkdemo.common.annotation.MethodEntity;
import com.newland.nsdkdemo.common.annotation.MethodGridBean;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.annotation.MethodGroupBean;
import com.newland.nsdkdemo.common.annotation.MethodGroupEntity;
import com.newland.nsdkdemo.common.utils.MessageTag;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseFragment implements AdapterView.OnItemClickListener {

    private View rootView = null;
    private boolean isInitData = false;

    public abstract String title();

    public abstract void initData();

    public abstract Object getModule();

    protected Context context;

    public View getRootView() {
        return rootView;
    }

    public void setInitData(boolean initData) {
        isInitData = initData;
    }

    public boolean isInitData() {
        return isInitData;
    }

    protected void showMessage(final int messid) {
        showMessage(context.getString(messid));
    }

    protected void showMessage(final String mess) {
        ((MainActivity) context).showMessage(mess, MessageTag.NORMAL);
    }

    protected void showPicMessage(final String hexString) {
        ((MainActivity) context).showPicMessage(hexString);
    }

    protected void showMessage(final int messid, final int messageType) {
        showMessage(context.getString(messid), messageType);
    }

    protected void showMessage(final String mess, final int messageType) {
        ((MainActivity) context).showMessage(mess, messageType);
    }

    protected void updateFirstLineMessage(final String mess) {
        ((MainActivity) context).updateFirstLineMessage(mess, MessageTag.NORMAL);
    }

    protected void showErrorMessage(Exception e, String operation) {
        if(e instanceof NSDKException) {
            showMessage(String.format("Failed to %s(%d). %s", operation, ((NSDKException)e).getCode(), e.getMessage()), MessageTag.ERROR);
        }else {
            showMessage(String.format("Failed to %s(%d). %s", operation, -1, e.getMessage()), MessageTag.ERROR);
        }
    }

    protected void clearMessage() {
        ((MainActivity) context).clearMessage();
    }

    protected long getTimeDistance(Date startTime) {
        try {
            Date end = new Date();
            return end.getTime() - startTime.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public BaseFragment(Context context, LayoutMode layoutMode) {
        this.context = context;

        if (layoutMode == LayoutMode.LINE) {
            rootView = initListView();
            invokeMethodsLine();
        } else if (layoutMode == LayoutMode.GROUP) {
            rootView = initExListView();
            invokeMethodsGroup();
        } else if (layoutMode == LayoutMode.GRID) {
            rootView = initRecycleView();
            invokeMethodsGrid();
        }
    }

    /***********************************************************/
    protected ListView listView;

    private View initListView() {
        View view = View.inflate(context, R.layout.fragment_listview, null);
        listView = view.findViewById(R.id.id_listview);
        return view;
    }

    private void invokeMethodsLine() {
        Object moule = getModule();
        if (moule == null) {
            return;
        }
        Map<Integer, MethodBean> testExampleMap = getModuleMethodsLine(moule);
        if (testExampleMap == null) {
            return;
        }
        ArrayList<ListViewItem> objects = new ArrayList<ListViewItem>();
        for (Map.Entry<Integer, MethodBean> entry : testExampleMap.entrySet()) {
            Integer key = entry.getKey();
            MethodBean methodBean = entry.getValue();
            objects.add(new ListViewItem(methodBean.name, methodBean.desc, methodBean.index));
        }
        Collections.sort(objects, new SortByIndex());
        ListViewAdapter adapter = new ListViewAdapter(this.context, objects);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    private Map<Object, Map<Integer, MethodBean>> modulesMap = new HashMap<>();

    protected Map<Integer, MethodBean> getModuleMethodsLine(Object module) {
        try {
            if (modulesMap.get(module) != null) {
                return modulesMap.get(module);
            }
            Map<Integer, MethodBean> exampleMap = new HashMap<>();
            Class clazz = module.getClass();
            Method[] mothods = clazz.getDeclaredMethods();
            for (Method method : mothods) {
                MethodEntity testAnno = method.getAnnotation(MethodEntity.class);
                if (testAnno == null) {
                    continue;
                }
                MethodBean bean = new MethodBean(testAnno.finddesc(), testAnno.name(), testAnno.id(), method, module);
                exampleMap.put(testAnno.id(), bean);
            }
            modulesMap.put(module, exampleMap);
            return exampleMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Map<Integer, MethodBean> testExampleMap = getModuleMethodsLine(getModule());
        if (testExampleMap == null) {
            return;
        }
        MethodBean bean = testExampleMap.get(position);
        if (bean == null) {
            return;
        }
        try {
            bean.method.setAccessible(true);
            bean.method.invoke(bean.module);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SortByIndex implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            ListViewItem s1 = (ListViewItem) o1;
            ListViewItem s2 = (ListViewItem) o2;
            if (s1.index > s2.index) {
                return 1;
            }
            return -1;
        }
    }

    /***********************************************************/
    protected ExpandableListView exlistView;
    private ExpandableListViewAdapter expandableListViewAdapter;
    private List<ExpandableListViewParent> mParentList = new ArrayList<ExpandableListViewParent>();
    private Map<ExpandableListViewParent, List<ExpandableListViewSon>> mSonList = new ConcurrentHashMap<ExpandableListViewParent, List<ExpandableListViewSon>>();
    private Map<Object, ModuleMethods> modulesExMap = new HashMap<>();

    private class ModuleMethods {
        public Map<Integer, MethodGroupBean> groupMap = new HashMap<>();
        public Map<Integer, ArrayList<MethodGroupBean>> childMap = new HashMap<>();
    }

    private View initExListView() {
        View view = View.inflate(context, R.layout.fragment_exlistview, null);
        exlistView = view.findViewById(R.id.id_exlistview);
        return view;
    }

    private void invokeMethodsGroup() {
        Object moule = getModule();
        if (moule == null) {
            return;
        }
        ModuleMethods moduleMethods = getModuleMethodsGroup(moule);
        if (moduleMethods == null) {
            return;
        }
        for (Map.Entry<Integer, MethodGroupBean> groupentry : moduleMethods.groupMap.entrySet()) {
            Integer groupKey = groupentry.getKey();
            MethodGroupBean groupean = groupentry.getValue();
            ExpandableListViewParent parentItem = new ExpandableListViewParent(groupean);
            mParentList.add(parentItem);
            Collections.sort(mParentList, new SortByGroup());
            ArrayList<MethodGroupBean> childlist = moduleMethods.childMap.get(groupKey);
            if (childlist == null) {
                continue;
            }
            Collections.sort(childlist, new SortByChild());
            List<ExpandableListViewSon> listSon = new ArrayList<>();
            for (int i = 0; i < childlist.size(); i++) {
                ExpandableListViewSon sonItem = new ExpandableListViewSon(childlist.get(i));
                listSon.add(sonItem);
            }
            mSonList.put(parentItem, listSon);
        }
        expandableListViewAdapter = new ExpandableListViewAdapter(context, mSonList, mParentList);
        exlistView.setAdapter(expandableListViewAdapter);
        exlistView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int parentPos, int childPos, long l) {
                try {
                    ExpandableListViewSon mSonItem = mSonList.get(mParentList.get(parentPos)).get(childPos);
                    MethodGroupBean bean = mSonItem.bean;
                    bean.method.setAccessible(true);
                    bean.method.invoke(bean.module);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        exlistView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View view, int groupPosition, long id) {
                ExpandableListViewParent mParentItem = mParentList.get(groupPosition);
                return false;
            }
        });
    }

    protected ModuleMethods getModuleMethodsGroup(Object module) {
        try {
            if (modulesExMap.get(module) != null) {
                return modulesExMap.get(module);
            }
            ModuleMethods moduleMethods = new ModuleMethods();
            Class clazz = module.getClass();
            Method[] mothods = clazz.getDeclaredMethods();
            for (Method method : mothods) {
                MethodGroupEntity testAnno = method.getAnnotation(MethodGroupEntity.class);
                if (testAnno == null) {
                    continue;
                }
                MethodGroupBean bean = new MethodGroupBean(testAnno.finddesc(), testAnno.groupname(), testAnno.groupid(), testAnno.childname(), testAnno.childid(), method, module);
                if (moduleMethods.groupMap.containsKey(testAnno.groupid())) {
                    continue;
                }
                moduleMethods.groupMap.put(testAnno.groupid(), bean);
                ArrayList<MethodGroupBean> childlist = new ArrayList<MethodGroupBean>();
                for (Method method1 : mothods) {
                    MethodGroupEntity testAnno1 = method1.getAnnotation(MethodGroupEntity.class);
                    if (testAnno1 == null) {
                        continue;
                    }
                    if (testAnno.groupid() == testAnno1.groupid()) {
                        MethodGroupBean bean1 = new MethodGroupBean(testAnno1.finddesc(), testAnno1.groupname(), testAnno1.groupid(), testAnno1.childname(), testAnno1.childid(), method1, module);
                        childlist.add(bean1);
                    }
                }
                moduleMethods.childMap.put(testAnno.groupid(), childlist);
            }
            modulesExMap.put(module, moduleMethods);
            return moduleMethods;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private class SortByGroup implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            ExpandableListViewParent s1 = (ExpandableListViewParent) o1;
            ExpandableListViewParent s2 = (ExpandableListViewParent) o2;
            if (s1.bean.groupid > s2.bean.groupid) {
                return 1;
            }
            return -1;
        }
    }

    private class SortByChild implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            MethodGroupBean s1 = (MethodGroupBean) o1;
            MethodGroupBean s2 = (MethodGroupBean) o2;
            if (s1.childid > s2.childid) {
                return 1;
            }
            return -1;
        }
    }

    /***********************************************************/
    private static boolean isFunRunning = false;

    public static void setFunRunning(boolean running) {
        isFunRunning = running;
    }

    protected RecyclerView recycleview;

    public int getSpanCount() {
        return 3;
    }

    private View initRecycleView() {
        View view = View.inflate(context, R.layout.fragment_recyclerview, null);
        recycleview = view.findViewById(R.id.id_recycleview);
        return view;
    }

    private Map<Object, ArrayList<MethodGridBean>> modulesMapGrid = new HashMap<>();

    protected ArrayList<MethodGridBean> getModuleMethodsGrid(Object module) {
        try {
            if (modulesMapGrid.get(module) != null) {
                return modulesMapGrid.get(module);
            }
            ArrayList<MethodGridBean> gridList = new ArrayList<MethodGridBean>();
            Class clazz = module.getClass();
            Method[] mothods = clazz.getDeclaredMethods();
            for (Method method : mothods) {
                MethodGridEntity gridAnno = method.getAnnotation(MethodGridEntity.class);
                if (gridAnno == null) {
                    continue;
                }
                MethodGridBean bean = new MethodGridBean(gridAnno.btnname(), gridAnno.btnnameid(), gridAnno.functionid(), gridAnno.btnimageid(), gridAnno.issync(), gridAnno.divtipid(), method, module);
                gridList.add(bean);
            }
            Collections.sort(gridList, new SortGridItem());
            modulesMapGrid.put(module, gridList);
            return gridList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void invokeMethodsGrid() {
        Object moule = getModule();
        if (moule == null) {
            return;
        }
        ArrayList<MethodGridBean> moduleMethodsGrid = getModuleMethodsGrid(moule);
        if (moduleMethodsGrid == null) {
            return;
        }
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(moduleMethodsGrid, RecyclerViewAdapter.RecyclerViewType.ROW3_SMALL);
        GridLayoutManager layoutManager = new GridLayoutManager(context, getSpanCount());
        recyclerViewAdapter.setOnItemClickLitener(new RecyclerViewAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(int position) {
                ArrayList<MethodGridBean> moduleMethodsGrid = getModuleMethodsGrid(getModule());
                final MethodGridBean bean = moduleMethodsGrid.get(position);
                SDKExecutors.getThreadPoolInstance().submit(new Runnable() {
                    @Override
                    public void run() {
                        if (SDKExecutors.isThreadPoolRunning()) {
                            if (isFunRunning) {
                                showMessage(R.string.msg_check_first, MessageTag.ERROR);
                                return;
                            }
                            if (bean.issync) {
                                isFunRunning = true;
                            }
                            try {
                                bean.method.setAccessible(true);
                                bean.method.invoke(bean.module);
                            } catch (Exception e) {
                                e.printStackTrace();
                                showMessage(context.getString(R.string.msg_error) + e, MessageTag.ERROR);
                            }
                            isFunRunning = false;
                        }
                    }
                });
            }
        });
        recycleview.addItemDecoration(new DividerGridItemDecoration(context));
        recycleview.setLayoutManager(layoutManager);
        recycleview.setAdapter(recyclerViewAdapter);
    }
//    public class SortGridItem implements Comparator<Object> {
//        @Override
//        public int compare(Object arg0, Object arg1) {
//            MethodGridBean bean0 =(MethodGridBean)arg0;
//            MethodGridBean bean1 =(MethodGridBean)arg1;
//            if((bean0.x-bean1.x)==0){
//                return (bean0.y - bean1.y);
//            }else{
//                return (bean0.x-bean1.x);
//            }
//        }
//    }

    private class SortGridItem implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            try {
                MethodGridBean bean1 = (MethodGridBean) o1;
                MethodGridBean bean2 = (MethodGridBean) o2;
                int id1 = Integer.valueOf(bean1.functionid);
                int id2 = Integer.valueOf(bean2.functionid);
                return (id1 - id2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }
    }
}
