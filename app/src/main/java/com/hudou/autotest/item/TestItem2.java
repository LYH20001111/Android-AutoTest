package com.hudou.autotest.item;

import com.hudou.autotest.annotation.TestCase;
import com.hudou.autotest.annotation.TestItem;
import com.hudou.autotest.base.item.AutoTestTestItem;


import java.lang.reflect.Method;

@TestItem(name = "Test2", description = "测试项目2")
public class TestItem2 extends AutoTestTestItem {
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

    @TestCase(name = "TestItem2 test2_000")
    private void test2_000(){
        for (int i = 0; i < 100; i++) {
            recordMessage("Hello World" + i);
        }
    }

    @TestCase(name = "TestItem2 test2_001")
    private void test2_001(){
        recordMessage("Ni Hao Shi Jie");
    }

    @TestCase(name = "TestItem2 test2_002")
    private void test2_002(){
        for (int i = 0; i < 1000; i++) {
            recordMessage("Hello World" + i);
        }
    }
}
