package com.hudou.autotest.item;

import com.hudou.autotest.annotation.TestCase;
import com.hudou.autotest.annotation.TestItem;
import com.hudou.autotest.base.item.BaseTestItem;


import java.lang.reflect.Method;

@TestItem(name = "Test2", description = "测试项目2")
public class TestItem2 extends BaseTestItem {
    @Override
    public void onCaseStart(Method method) {
        super.onCaseStart(method);
    }

    @Override
    public void onCaseFinish(Method method) {
        super.onCaseFinish(method);
    }

    @TestCase(name = "TestItem2 test2_000")
    private void test2_000(){
        recordNormal("Hello World");
    }

    @TestCase(name = "TestItem2 test2_001")
    private void test2_001(){
        recordNormal("Ni Hao Shi Jie");
    }

    
}
