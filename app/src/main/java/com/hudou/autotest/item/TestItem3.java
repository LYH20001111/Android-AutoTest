package com.hudou.autotest.item;


import com.hudou.autotest.annotation.TestCase;
import com.hudou.autotest.annotation.TestItem;
import com.hudou.autotest.base.item.AutoTestTestItem;

import java.lang.reflect.Method;


@TestItem(name = "Test1", description = "测试项目1")
public class TestItem3 extends AutoTestTestItem {

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


    @TestCase(name = "TestItem1 test1_000")
    private void test1_000(){
        recordMessage("wo hen hao");
    }

    @TestCase(name = "TestItem1 test1_001")
    private void test1_001(){
        for (int i = 0; i < 100; i++) {
            recordMessage("Hello World" + i);
        }
    }

    @TestCase(name = "TestItem1 test1_002")
    private void test1_002(){
        recordMessage("Ni Hao Shi Jie");
    }

    @TestCase(name = "TestItem1 test1_003")
    private void test1_003(){
        for (int i = 0; i < 1000; i++) {
            recordMessage("Hello World" + i);
        }
    }

    @TestCase(name = "TestItem1 test1_004", unsupportedDevice = {"P70", "N950", "Newland N950S"})
    private void test1_004(){
        recordMessage("Ni Hao Shi Jie");
    }

    @TestCase(name = "TestItem1 test1_005", abandon = true)
    private void test1_005(){
        recordMessage("Ni Hao Shi Jie");
    }
}
