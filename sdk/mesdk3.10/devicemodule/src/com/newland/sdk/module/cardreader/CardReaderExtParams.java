package com.newland.sdk.module.cardreader;

import com.newland.sdk.module.rfcard.FelicaParams;
import com.newland.sdk.module.rfcard.RFCardType;
import com.newland.sdk.module.swiper.SwiperReadModel;

/**
 * Created by youjf on 2019/7/23 09:50
 */
public class CardReaderExtParams {
    /**
     * Search card rule,it can set whether find rf card first
     */
    private SearchCardRule searchCardRule;
    /**
     * it can set which magnetic track to check
     */
    private SwiperReadModel[] checkReadModel;

    /**
     * the rf card type that expected to be found
     */
    private RFCardType[] expectedRfCardTypes;

    /**
     * felica card Param
     */
    private FelicaParams[] felicaParams;

    private boolean checkUnionCard = true;

    private boolean enablePreParam = false;

    /**
     * vas card Param
     */
    private boolean vasEnable = false;
    private byte[] vasParams;


    /**
     * get the rule of searching card
     *
     * @return card searching rule{@link SearchCardRule}
     */
    public SearchCardRule getSearchCardRule() {
        return searchCardRule;
    }

    /*only for ME51*/
    private String firstLineMessage;
    private String secondLineMessage;
    private String thirdLineMessage;
    private String fourthLineMessage;

    /**
     * message encode
     */
    private String messageEncode = "GB2312";

    public String getFirstLineMessage() {
        return firstLineMessage;
    }

    public void setFirstLineMessage(String firstLineMessage) {
        this.firstLineMessage = firstLineMessage;
    }

    public String getSecondLineMessage() {
        return secondLineMessage;
    }

    public void setSecondLineMessage(String secondLineMessage) {
        this.secondLineMessage = secondLineMessage;
    }

    public String getThirdLineMessage() {
        return thirdLineMessage;
    }

    public void setThirdLineMessage(String thirdLineMessage) {
        this.thirdLineMessage = thirdLineMessage;
    }

    public String getFourthLineMessage() {
        return fourthLineMessage;
    }

    public void setFourthLineMessage(String fourthLineMessage) {
        this.fourthLineMessage = fourthLineMessage;
    }

    /**
     * set the rule of searching card,it can set whether find rf card first
     *
     * @param searchCardRule card searching rule{@link SearchCardRule}
     */
    public void setSearchCardRule(SearchCardRule searchCardRule) {
        this.searchCardRule = searchCardRule;
    }

    /**
     * get the  magnetic tracks to check
     *
     * @return magnetic tracks {@link SwiperReadModel}
     */
    public SwiperReadModel[] getCheckReadModel() {
        return checkReadModel;
    }

    /**
     * set the  magnetic tracks to check
     *
     * @param checkReadModel magnetic tracks {@link SwiperReadModel}
     */
    public void setCheckReadModel(SwiperReadModel[] checkReadModel) {
        this.checkReadModel = checkReadModel;
    }

    /**
     * get the rf card type that expected to be found
     *
     * @return
     */
    public RFCardType[] getExpectedRfCardTypes() {
        return expectedRfCardTypes;
    }

    /**
     * set the rf card type that expected to be found
     *
     * @param expectedRfCardTypes
     */
    public void setExpectedRfCardTypes(RFCardType[] expectedRfCardTypes) {
        this.expectedRfCardTypes = expectedRfCardTypes;
    }

    /**
     * set felica params
     *
     * @param felicaParams
     */
    public void setFelicaParams(FelicaParams[] felicaParams) {
        this.felicaParams = felicaParams;
    }

    /**
     * get felica params
     *
     * @return
     */
    public FelicaParams[] getFelicaParams() {
        return felicaParams;
    }

    public boolean isCheckUnionCard() {
        return checkUnionCard;
    }

    public void setCheckUnionCard(boolean checkUnionCard) {
        this.checkUnionCard = checkUnionCard;
    }

    public String getMessageEncode() {
        return messageEncode;
    }

    public void setMessageEncode(String messageEncode) {
        this.messageEncode = messageEncode;
    }

    public boolean isEnablePreParam() {
        return enablePreParam;
    }

    public void setEnablePreParam(boolean enablePreParam) {
        this.enablePreParam = enablePreParam;
    }

    public byte[] getVasParams() {
        return vasParams;
    }

    public void setVasParams(boolean vasEnable,byte[] vasParams) {
        this.vasEnable = vasEnable;
        this.vasParams = vasParams;
    }

    public boolean isVasEnable() {
        return vasEnable;
    }
}
