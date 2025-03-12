package com.newland.autotest.constant;

import com.newland.autotest.annotation.TestItem;
import com.newland.autotest.base.item.BaseTestCase;
import com.newland.autotest.util.ReflectionUtils;

public class Item {
    private Class<? extends BaseTestCase> clz;
    private String name;
    private String description;

    public Item(Class<? extends BaseTestCase> clz){
        this.clz = clz;
        this.name = ReflectionUtils.getAnnotationValue(clz, TestItem.class, "name");
        this.description = ReflectionUtils.getAnnotationValue(clz, TestItem.class, "description");
    }

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public Class<? extends BaseTestCase> getClz() {
        return clz;
    }

}
