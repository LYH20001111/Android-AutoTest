package com.hudou.autotest.constant;

public class TableItem {
    private String caseItem;
    private String caseTotalCount;
    private String failCount;
    private String totalCount;

    public TableItem(String caseItem, String caseTotalCount, String failCount, String totalCount) {
        this.caseItem = caseItem;
        this.caseTotalCount = caseTotalCount;
        this.failCount = failCount;
        this.totalCount = totalCount;
    }

    public String getCaseItem() {
        return caseItem;
    }

    public String getCaseTotalCount() {
        return caseTotalCount;
    }

    public String getFailCount() {
        return failCount;
    }

    public String getTotalCount() {
        return totalCount;
    }
}