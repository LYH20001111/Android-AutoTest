package com.newland.nsdk.core.api.external.keyboard;

import java.math.BigDecimal;

public class AmountParameters {
    String title;
    String text;
    int maxDigits;
    BigDecimal totalAmount;
    float[] tipPercentages;
    BigDecimal[] tipSuggestions;
    BigDecimal tipCalculationAmount;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getMaxDigits() {
        return maxDigits;
    }

    public void setMaxDigits(int maxDigits) {
        this.maxDigits = maxDigits;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public float[] getTipPercentages() {
        return tipPercentages;
    }

    public void setTipPercentages(float[] tipPercentages) {
        this.tipPercentages = tipPercentages;
    }

    public BigDecimal[] getTipSuggestions() {
        return tipSuggestions;
    }

    public void setTipSuggestions(BigDecimal[] tipSuggestions) {
        this.tipSuggestions = tipSuggestions;
    }

    public BigDecimal getTipCalculationAmount() {
        return tipCalculationAmount;
    }

    public void setTipCalculationAmount(BigDecimal tipCalculationAmount) {
        this.tipCalculationAmount = tipCalculationAmount;
    }
}
