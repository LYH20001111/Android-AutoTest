package com.hudou.autotest.item;


import com.hudou.autotest.annotation.TestCase;
import com.hudou.autotest.annotation.TestItem;
import com.hudou.autotest.base.item.AutoTestTestItem;

import java.lang.reflect.Method;


@TestItem(name = "Test1", description = "测试项目1")
public class TestItem1 extends AutoTestTestItem {

    @Override
    public void onCaseStart(Method method) {
        super.onCaseStart(method);
        recordMessage("=============" + method.getName() + "=============");
    }

    @Override
    public void onCaseFinish(Method method) {
        super.onCaseFinish(method);
        recordPass();
    }


//    @TestCase(name = "TestItem2 test2_001")
//    private void test1_000(){
//        recordMessage("wo hen hao");
//    }
}
