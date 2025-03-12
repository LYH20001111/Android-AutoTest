package com.hudou.autotest.constant;

import com.hudou.autotest.annotation.TestItem;
import com.hudou.autotest.base.item.BaseTestCase;
import com.hudou.autotest.util.ReflectionUtils;

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
