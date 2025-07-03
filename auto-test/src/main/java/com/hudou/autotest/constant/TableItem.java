package com.hudou.autotest.constant;

public class TableItem {
    private String caseItem;
    private int failCount;
    private int totalCount;

    public TableItem(String caseItem, int passCount, int totalCount) {
        this.caseItem = caseItem;
        this.failCount = passCount;
        this.totalCount = totalCount;
    }

    public String getCaseItem() {
        return caseItem;
    }

    public int getFailCount() {
        return failCount;
    }

    public int getTotalCount() {
        return totalCount;
    }
}