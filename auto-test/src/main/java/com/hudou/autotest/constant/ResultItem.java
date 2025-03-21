package com.hudou.autotest.constant;

import com.hudou.autotest.base.item.AutoTestTestItem;
import com.hudou.autotest.base.item.BaseTestCase;

import java.util.List;

public class ResultItem {
    private Class<? extends BaseTestCase> clz;
    private List<ResultData> resultDataList;

    public ResultItem(Class<? extends BaseTestCase> clz, List<ResultData> resultDataList) {
        this.clz = clz;
        this.resultDataList = resultDataList;
    }

    public Class<? extends BaseTestCase> getClz() {
        return clz;
    }

    public void setClz(Class<? extends BaseTestCase> clz) {
        this.clz = clz;
    }

    public List<ResultData> getResultDataList() {
        return resultDataList;
    }

    public void setResultDataList(List<ResultData> resultDataList) {
        this.resultDataList = resultDataList;
    }

    @Override
    public String toString() {
        return "ResultItem{" +
                "clz=" + clz +
                ", resultDataList=" + resultDataList +
                '}';
    }
}
