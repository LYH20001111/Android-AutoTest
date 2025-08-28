package com.hudou.autotest.constant;

public class ResultData {
    private String Id;
    private String testCaseName;
    private String result;
    private String chineseDescription;
    private String englishDescription;
    private String detail = "";

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public String getTestCaseName() {
        return testCaseName;
    }

    public void setTestCaseName(String testCaseName) {
        this.testCaseName = testCaseName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getChineseDescription() {
        return chineseDescription;
    }

    public void setChineseDescription(String chineseDescription) {
        this.chineseDescription = chineseDescription;
    }

    public String getEnglishDescription() {
        return englishDescription;
    }

    public void setEnglishDescription(String englishDescription) {
        this.englishDescription = englishDescription;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void appendDetail(String detail) {
        this.detail = this.detail.concat(detail);
    }

    @Override
    public String toString() {
        return "ResultData{" +
                "Id='" + Id + '\'' +
                ", testCaseName='" + testCaseName + '\'' +
                ", result='" + result + '\'' +
                ", chineseDescription='" + chineseDescription + '\'' +
                ", englishDescription='" + englishDescription + '\'' +
                ", detail='" + detail + '\'' +
                '}';
    }
}
