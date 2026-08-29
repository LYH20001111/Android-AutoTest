package com.hudou.autotest.aar.navigation;


import android.widget.Toast;

import com.hudou.autotest.aar.MainActivity;
import com.hudou.autotest.annotation.Function;
import com.hudou.autotest.constant.FunctionType;
import com.hudou.autotest.fragment.AutoTestSettingFragment;

import java.util.ArrayList;
import java.util.List;


//@Navigation(name = "Setting")
//@TestItemClass(clz = {TestItem2.class})
public class SettingFragment extends AutoTestSettingFragment {


    @Override
    public void onFragmentVisibility() {
        super.onFragmentVisibility();
        Toast.makeText(MainActivity.mContext, "Setting Selected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddActions() {
        super.onAddActions();
    }

    @Override
    public String onSetReportPath() {
        //changeEditPathCap(EditCap.ON);
//        setIsEnglishReport(true);
        return super.onSetReportPath();
    }

    @Override
    public String onSetTestFilesPath() {
        return super.onSetTestFilesPath();
    }

    @Override
    public List<String> addAssetsDirs() {
        return new ArrayList<String>(){{
            add("document");
            add("test");
        }};
    }

    @Override
    public String onAddReportNamePrefix() {
        return "AutoTest-";
    }

    @Function(title = "额外功能 1 ")
    private void function1() {

    }

    @Function(title = "额外功能 2 开关", type = FunctionType.SWITCH, isChecked = false)
    private boolean function2() {
        return false;
    }
}