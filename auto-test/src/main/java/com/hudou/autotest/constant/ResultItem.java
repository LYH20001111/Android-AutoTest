package com.hudou.autotest.constant;

import com.hudou.autotest.base.item.BaseTestCase;

import java.util.concurrent.CopyOnWriteArrayList;

public class ResultItem {
    private Class<? extends BaseTestCase> clz;
    private CopyOnWriteArrayList<ResultData> resultDataList;
    private String startTime;
    private String endTime;
    private boolean isStartTimeSet = false; // 标志变量，记录是否已经设置了 startTime

    public ResultItem(Class<? extends BaseTestCase> clz, CopyOnWriteArrayList<ResultData> resultDataList) {
        this.clz = clz;
        this.resultDataList = resultDataList;
    }

    public Class<? extends BaseTestCase> getClz() {
        return clz;
    }

    public void setClz(Class<? extends BaseTestCase> clz) {
        this.clz = clz;
    }

    public CopyOnWriteArrayList<ResultData> getResultDataList() {
        return resultDataList;
    }

    public void setResultDataList(CopyOnWriteArrayList<ResultData> resultDataList) {
        this.resultDataList = resultDataList;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        if (!isStartTimeSet) { // 如果还没有设置过 startTime
            this.startTime = startTime;
            isStartTimeSet = true; // 标记为已设置
        }
        if (this.startTime == null){
            this.startTime = startTime;
            isStartTimeSet = true;
        }
        // 如果已经设置过 startTime，则忽略后续调用
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "ResultItem{" +
                "clz=" + clz +
                ", resultDataList=" + resultDataList +
                '}';
    }

    public boolean isStartTimeSet() {
        return isStartTimeSet;
    }
}
