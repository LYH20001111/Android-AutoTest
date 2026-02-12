package com.newland.sdk.me.module.emv;

import java.util.Locale;

public class EMVDialogTips {
    private String dialogMesdkRequestAmountTitle = "please input trade amount:";
    private String dialogMesdkConfirm = "confirm";
    private String dialogMesdkCancel = "cancel";
    private String dailogMesdkCardnumberConfirmTitle = "confirm the card number";
    private String dialogMesdkCardnunberCancel = "No";
    private String dialogMesdkCardnumberConfirm = "Yes" ;
    private String dialogMesdkAidselectTitle = "select the aids";
    private String dialogMesdkCertConfirmTitle = "please show your certificate";
    private String dialogMesdkCertConfirm = "Yse";
    private String dialogMesdkCertCancel = "No" ;
    private String dialogMesdkCertType = "certificate type：";
    private String dialogMesdkCertNumber = "certificate number：";
    private String dialogMesdkEcChoiceTitle = "electronic cash selection";
    private String dialogMesdkEcChoiceContext = "The card support electronic cash,whether use electronic cash or not?";
    private String dialogMesdkEcChoiceOnline = "No";
    private String dialogMesdkEcChiceEc = "Yes";
    private String dialogMesdkEcChoiceCancel = "cancel the transaction";
    private String[] certType ={"identity card","military ID","passport","entry card","temporary card","others"};
    private String language = "LANGUAGE";



    public EMVDialogTips(){
        boolean isEnglish = isEnglishLanguage();
        if(isEnglish){
            setEnglishTips();
        }else{
            setChineseTips();
        }
    }

    /**
     * 设置英文对话框提示语
     */
    public void setEnglishTips(){
        setDailogMesdkCardnumberConfirmTitle("confirm the card number");
        setDialogMesdkAidselectTitle("select the aids");
        setDialogMesdkCancel("cancel");
        setDialogMesdkCardnumberConfirm("Yes");
        setDialogMesdkCardnunberCancel("No");
        setDialogMesdkCertCancel("No");
        setDialogMesdkCertConfirm("Yes");
        setDialogMesdkCertConfirmTitle("please show your certificate");
        setDialogMesdkCertNumber("certificate number:");
        setDialogMesdkCertType("certificate type:");
        setDialogMesdkConfirm("Confirm");
        setDialogMesdkEcChiceEc("Yes");
        setDialogMesdkEcChoiceCancel("cancel the transaction");
        setDialogMesdkEcChoiceContext("The card support electronic cash,whether use electronic cash or not?");
        setDialogMesdkEcChoiceOnline("No");
        setDialogMesdkEcChoiceTitle("electronic cash selection");
        setDialogMesdkRequestAmountTitle("please input trade amount:");
        setCertType(new String[]{"identity card","military ID","passport","entry card","temporary card","others"});
        setLanguage("LANGUAGE");
    }

    /**
     * 设置中文对话框提示语
     */
    public void setChineseTips(){
        setDailogMesdkCardnumberConfirmTitle("卡号确认");
        setDialogMesdkAidselectTitle("应用选择");
        setDialogMesdkCancel("取消");
        setDialogMesdkCardnumberConfirm("确认");
        setDialogMesdkCardnunberCancel("取消");
        setDialogMesdkCertCancel("错误(取消)");
        setDialogMesdkCertConfirm("正确(确认)");
        setDialogMesdkCertConfirmTitle("请出示证件");
        setDialogMesdkCertNumber("证件号:");
        setDialogMesdkCertType("证件类型:");
        setDialogMesdkConfirm("确定");
        setDialogMesdkEcChiceEc("是(确认)");
        setDialogMesdkEcChoiceCancel("终止当前交易");
        setDialogMesdkEcChoiceContext("卡片支持电子现金交易,是否进行电子现金交易?");
        setDialogMesdkEcChoiceOnline("否(联机)");
        setDialogMesdkEcChoiceTitle("电子现金交易方式选择");
        setDialogMesdkRequestAmountTitle("请输入金额，单位:元");
        setCertType(new String[]{ "身份证", "军官证", "护照", "入境证", "临时身份证", "其他" });
        setLanguage("语言");
    }

    /**
     * 设备是否是英文语言
     *
     */
    private boolean isEnglishLanguage() {
        try{
            // 获取系统语言
            String locale = Locale.getDefault().getLanguage();
            System.out.println("=============locale=" + locale);
            if (locale.equals("zh")) {
                return false;
            } else {
                return true;
            }
        }catch (Exception e){

        }
        return true;
    }

    public String getDialogMesdkRequestAmountTitle() {
        return dialogMesdkRequestAmountTitle;
    }

    public void setDialogMesdkRequestAmountTitle(String dialogMesdkRequestAmountTitle) {
        this.dialogMesdkRequestAmountTitle = dialogMesdkRequestAmountTitle;
    }

    public String getDialogMesdkConfirm() {
        return dialogMesdkConfirm;
    }

    public void setDialogMesdkConfirm(String dialogMesdkConfirm) {
        this.dialogMesdkConfirm = dialogMesdkConfirm;
    }

    public String getDialogMesdkCancel() {
        return dialogMesdkCancel;
    }

    public void setDialogMesdkCancel(String dialogMesdkCancel) {
        this.dialogMesdkCancel = dialogMesdkCancel;
    }

    public String getDailogMesdkCardnumberConfirmTitle() {
        return dailogMesdkCardnumberConfirmTitle;
    }

    public void setDailogMesdkCardnumberConfirmTitle(String dailogMesdkCardnumberConfirmTitle) {
        this.dailogMesdkCardnumberConfirmTitle = dailogMesdkCardnumberConfirmTitle;
    }

    public String getDialogMesdkCardnunberCancel() {
        return dialogMesdkCardnunberCancel;
    }

    public void setDialogMesdkCardnunberCancel(String dialogMesdkCardnunberCancel) {
        this.dialogMesdkCardnunberCancel = dialogMesdkCardnunberCancel;
    }

    public String getDialogMesdkCardnumberConfirm() {
        return dialogMesdkCardnumberConfirm;
    }

    public void setDialogMesdkCardnumberConfirm(String dialogMesdkCardnumberConfirm) {
        this.dialogMesdkCardnumberConfirm = dialogMesdkCardnumberConfirm;
    }

    public String getDialogMesdkAidselectTitle() {
        return dialogMesdkAidselectTitle;
    }

    public void setDialogMesdkAidselectTitle(String dialogMesdkAidselectTitle) {
        this.dialogMesdkAidselectTitle = dialogMesdkAidselectTitle;
    }

    public String getDialogMesdkCertConfirmTitle() {
        return dialogMesdkCertConfirmTitle;
    }

    public void setDialogMesdkCertConfirmTitle(String dialogMesdkCertConfirmTitle) {
        this.dialogMesdkCertConfirmTitle = dialogMesdkCertConfirmTitle;
    }

    public String getDialogMesdkCertConfirm() {
        return dialogMesdkCertConfirm;
    }

    public void setDialogMesdkCertConfirm(String dialogMesdkCertConfirm) {
        this.dialogMesdkCertConfirm = dialogMesdkCertConfirm;
    }

    public String getDialogMesdkCertCancel() {
        return dialogMesdkCertCancel;
    }

    public void setDialogMesdkCertCancel(String dialogMesdkCertCancel) {
        this.dialogMesdkCertCancel = dialogMesdkCertCancel;
    }

    public String getDialogMesdkCertType() {
        return dialogMesdkCertType;
    }

    public void setDialogMesdkCertType(String dialogMesdkCertType) {
        this.dialogMesdkCertType = dialogMesdkCertType;
    }

    public String getDialogMesdkCertNumber() {
        return dialogMesdkCertNumber;
    }

    public void setDialogMesdkCertNumber(String dialogMesdkCertNumber) {
        this.dialogMesdkCertNumber = dialogMesdkCertNumber;
    }

    public String getDialogMesdkEcChoiceTitle() {
        return dialogMesdkEcChoiceTitle;
    }

    public void setDialogMesdkEcChoiceTitle(String dialogMesdkEcChoiceTitle) {
        this.dialogMesdkEcChoiceTitle = dialogMesdkEcChoiceTitle;
    }

    public String getDialogMesdkEcChoiceContext() {
        return dialogMesdkEcChoiceContext;
    }

    public void setDialogMesdkEcChoiceContext(String dialogMesdkEcChoiceContext) {
        this.dialogMesdkEcChoiceContext = dialogMesdkEcChoiceContext;
    }

    public String getDialogMesdkEcChoiceOnline() {
        return dialogMesdkEcChoiceOnline;
    }

    public void setDialogMesdkEcChoiceOnline(String dialogMesdkEcChoiceOnline) {
        this.dialogMesdkEcChoiceOnline = dialogMesdkEcChoiceOnline;
    }

    public String getDialogMesdkEcChiceEc() {
        return dialogMesdkEcChiceEc;
    }

    public void setDialogMesdkEcChiceEc(String dialogMesdkEcChiceEc) {
        this.dialogMesdkEcChiceEc = dialogMesdkEcChiceEc;
    }

    public String getDialogMesdkEcChoiceCancel() {
        return dialogMesdkEcChoiceCancel;
    }

    public void setDialogMesdkEcChoiceCancel(String dialogMesdkEcChoiceCancel) {
        this.dialogMesdkEcChoiceCancel = dialogMesdkEcChoiceCancel;
    }

    public String[] getCertType() {
        return certType;
    }

    public void setCertType(String[] certType) {
        this.certType = certType;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
