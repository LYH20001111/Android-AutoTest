package com.newland.sdkdemo.event;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.SystemClock;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.newland.emv.jni.type.EmvConst;
import com.newland.sdk.ModuleManage;
import com.newland.sdk.module.cardreader.CardType;
import com.newland.sdk.module.emv.AIDEntity;
import com.newland.sdk.module.emv.AccountType;
import com.newland.sdk.module.emv.EMVControllerListener;
import com.newland.sdk.module.emv.EMVModule;
import com.newland.sdk.module.emv.EMVTransController;
import com.newland.sdk.module.emv.IDCardType;
import com.newland.sdk.module.emv.TransactionType;
import com.newland.sdk.module.emv.OnlineTransactionData;
import com.newland.sdk.module.emv.PINEntity;
import com.newland.sdk.module.emvl3.common.EmvL3Const;
import com.newland.sdk.module.light.IndicatorLightModule;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.module.swiper.MSDAlgorithmType;
import com.newland.sdk.module.swiper.MagStripeCardModule;
import com.newland.sdk.module.swiper.SwipExtParams;
import com.newland.sdk.module.swiper.SwipResult;
import com.newland.sdk.mtype.util.Dump;
import com.newland.sdk.utils.TLVPackage;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdkdemo.AppConfig;
import com.newland.sdkdemo.MainActivity;
import com.newland.sdkdemo.R;

import com.newland.sdkdemo.utils.DialogUtils;
import com.newland.sdkdemo.utils.EmvErrorCode;
import com.newland.sdkdemo.utils.MessageTag;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class EMVListener implements EMVControllerListener {

    private Context context;
    private static int[] L_55TAGS = new int[26];
    private static int[] L_SCRIPTTAGS = new int[21];
    private static int[] L_REVTAGS = new int[5];
    private EMVModule emvModule;
    private int transType;
    private int trackKeyIndex;
    private MSDAlgorithmType msdAlgorithmType;
    private KeyManagement keyManagement;
    private ModuleManage moduleManage;

    static {
        L_55TAGS[0] = 0x9f26;
        L_55TAGS[1] = 0x9F10;
        L_55TAGS[2] = 0x9F27;
        L_55TAGS[3] = 0x9F37;
        L_55TAGS[4] = 0x9F36;
        L_55TAGS[5] = 0x95;
        L_55TAGS[6] = 0x9A;
        L_55TAGS[7] = 0x9C;
        L_55TAGS[8] = 0x9F02;
        L_55TAGS[9] = 0x5F2A;
        L_55TAGS[10] = 0x82;
        L_55TAGS[11] = 0x9F1A;
        L_55TAGS[12] = 0x9F03;
        L_55TAGS[13] = 0x9F33;
        L_55TAGS[14] = 0x9F34;
        L_55TAGS[15] = 0x9F35;
        L_55TAGS[16] = 0x9F1E;
        L_55TAGS[17] = 0x84;
        L_55TAGS[18] = 0x9F09;
        L_55TAGS[19] = 0x9F41;
        L_55TAGS[20] = 0x8a;
        L_55TAGS[21] = 0x9f63;
        L_55TAGS[22] = 0x50;
        L_55TAGS[23] = 0x4f;
        L_55TAGS[24] = 0x9f12;
        L_55TAGS[25] = 0x9B;

        L_SCRIPTTAGS[0] = 0x9F33;
        L_SCRIPTTAGS[1] = 0x9F34;
        L_SCRIPTTAGS[2] = 0x9F35;
        L_SCRIPTTAGS[3] = 0x95;
        L_SCRIPTTAGS[4] = 0x9F37;
        L_SCRIPTTAGS[5] = 0x9F1E;
        L_SCRIPTTAGS[6] = 0x9F10;
        L_SCRIPTTAGS[7] = 0x9F26;
        L_SCRIPTTAGS[8] = 0x9F27;
        L_SCRIPTTAGS[9] = 0x9F36;
        L_SCRIPTTAGS[10] = 0x82;
        L_SCRIPTTAGS[11] = 0xDF31;
        L_SCRIPTTAGS[12] = 0x9F1A;
        L_SCRIPTTAGS[13] = 0x9A;
        L_SCRIPTTAGS[14] = 0x9C;
        L_SCRIPTTAGS[15] = 0x9F02;
        L_SCRIPTTAGS[16] = 0x5F2A;
        L_SCRIPTTAGS[17] = 0x84;
        L_SCRIPTTAGS[18] = 0x9F09;
        L_SCRIPTTAGS[19] = 0x9F41;
        L_SCRIPTTAGS[20] = 0x9F63;

        L_REVTAGS[0] = 0x95;
        L_REVTAGS[1] = 0x9F1E;
        L_REVTAGS[2] = 0x9F10;
        L_REVTAGS[3] = 0x9F36;
        L_REVTAGS[4] = 0xDF31;
    }

    public EMVListener(Context context, EMVModule emvModule, int transType) {
        this.context = context;
        this.emvModule = emvModule;
        this.transType = transType;
        moduleManage = ModuleManage.getInstance();
        if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
            keyManagement = KeyManagement.MKSK;
            msdAlgorithmType = MSDAlgorithmType.UNIONPAY_MODEL;
            trackKeyIndex = AppConfig.Pin.MKSK_DES_INDEX_WK_TRACK;
        } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
            keyManagement = KeyManagement.MKSK;
            msdAlgorithmType = MSDAlgorithmType.SM4_MODEL;
            trackKeyIndex = AppConfig.Pin.MKSK_SM4_INDEX_WK_TRACK;
        } else if (AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
            keyManagement = KeyManagement.MKSK;
            msdAlgorithmType = MSDAlgorithmType.UNIONPAY_MODEL;
            trackKeyIndex = AppConfig.Pin.MKSK_AES_INDEX_WK_TRACK;
        } else if (AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)) {
            keyManagement = KeyManagement.DUKPT;
            trackKeyIndex = AppConfig.Pin.DUKPT_DES_INDEX;
            msdAlgorithmType = MSDAlgorithmType.UNIONPAY_MODEL;
        }
    }

    @Override
    public void onEmvFinished(boolean isSuccess, EMVTransController controller) {
        showMessage("onEmvFinished", MessageTag.TIP);
//        if (!Locale.getDefault().getLanguage().equalsIgnoreCase("zh")) {
//            operateLED();
//        }
        // TTransaction code
        int executeRslt = controller.getEMVTransInfo().getExecuteRslt();
        String resultMsg = null;
        /**
         * ExecuteRslt:
         * 0x00/0x01:Transaction approved
         * 0x02:Transaction GAC1 decline
         * 0x04:Transaction GAC2 decline
         * other:Execute failed
         */
        switch (executeRslt) {
            case 0:
            case 1:
                resultMsg = context.getString(R.string.msg_trans_tc);
                break;
            case 2:
                resultMsg = context.getString(R.string.msg_trans_gac1_aac);
                break;
            case 4:
                resultMsg = context.getString(R.string.msg_trans_gac2_aac);
                break;
            default:
                resultMsg = context.getString(R.string.msg_trans_failed) + executeRslt;
                break;
        }
        showMessage(context.getString(R.string.msg_trans_result) + resultMsg + "\r\n", MessageTag.DATA);
        //Get the errorCode from the source code "controller.getEMVTransInfo().getErrorcode()" and the fail detail description from the source code "EmvErrorCode.getErrorDescribe(errorCode))"
        //The specific reason for the error code
        int errorCode = controller.getEMVTransInfo().getErrorcode();
        String description = "success";
        if (errorCode != 0) {
            description = EmvErrorCode.getErrorDescribe(errorCode);
            showMessage(context.getString(R.string.msg_trans_error_details) + description + "\r\n", MessageTag.DATA);
        }
        Log.d("EMVListener", "transaction resultCode:" + executeRslt + ",ErrorCode:" + errorCode + ",description:" + description);

        if(posEntryMode!=null){
            int cardInterface=posEntryMode[0]&0xff;
            if(AppConfig.isExternalEmv&(cardInterface==EmvL3Const.EntryMode.MSR||cardInterface==EmvL3Const.EntryMode.CT_FALLBACK)) {
                return;
            }

        }
            int[] emvTags = new int[3];
            emvTags[0] = 0x5a;
            emvTags[1] = 0x5F34;
            emvTags[2] = 0x57;
            TLVPackage tlv = controller.getEmvData(emvTags);
            String track2 = tlv.getString(0x57); // Two track  data
            String cardNo = tlv.getString(0x5a);
            if (null == cardNo && track2 != null) {
                cardNo = track2.substring(0, track2.indexOf('D'));
            }
            String cardSN = tlv.getString(0x5F34);// PAN
            String expiredDate = null;

            if (track2 != null) {
                expiredDate = track2.substring(track2.indexOf('D') + 1, track2.indexOf('D') + 5);
            }
            if (cardSN == null) {
                cardSN = "000";
            } else {
                cardSN = ISOUtils.padleft(cardSN, 3, '0');
            }
            String serviceCode = "";
            if (null != track2) {
                serviceCode = track2.substring(track2.indexOf('D') + 5, track2.indexOf('D') + 8);
            }
            //Since the array is BCD encoded, the last digit of the card number needs to be removed if it is 'F'.
            if (null != cardNo && cardNo.endsWith("F"))
                cardNo = cardNo.substring(0, cardNo.length() - 1);
            showMessage(context.getString(R.string.msg_term_cardNo) + cardNo + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_term_cardSN) + cardSN + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_term_expiredDate) + expiredDate + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_term_serviceCode) + serviceCode + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_term_track2) + track2 + "\r\n", MessageTag.DATA);

            TLVPackage tlvPackage = controller.getEmvData(L_55TAGS);
            byte[] data55 = tlvPackage.pack();
//        showMessage(context.getString(R.string.msg_term_55tag) + (data55 == null ? null : ISOUtils.hexString(data55)) + "\r\n", MessageTag.DATA);
            AppConfig.EMV.ic55Data = data55;// 55 filed data
            tlvPackage = controller.getEmvData(L_SCRIPTTAGS);
            byte[] dataScript = tlvPackage.pack();
//        showMessage(context.getString(R.string.msg_term_script) + (dataScript == null ? null : ISOUtils.hexString(dataScript)) + "\r\n", MessageTag.DATA);
            tlvPackage = controller.getEmvData(L_REVTAGS);
            byte[] revData = tlvPackage.pack();
//        showMessage(context.getString(R.string.msg_term_flushes_data) + (revData == null ? null : ISOUtils.hexString(revData)) + "\r\n", MessageTag.DATA);

    }

    @Override
    public void onError(EMVTransController arg0, Exception arg1) {
        showMessage("onError", MessageTag.ERROR);
        showMessage(context.getString(R.string.msg_emv_trans_failed) + arg1.getMessage() + "\r\n", MessageTag.ERROR);
        arg1.printStackTrace();
    }

    @Override
    public void onFallback(EMVTransController EMVTransController) {
        showMessage("onFallback", MessageTag.ERROR);
        showMessage(context.getString(R.string.msg_ic_env_notmeet_fallback) + "\r\n", MessageTag.ERROR);
    }

    @Override
    public void onRequestOnlineProcess(EMVTransController controller) {
        showMessage("onRequestOnlineProcess", MessageTag.TIP);
        int emvResult = controller.getEMVTransInfo().getEmvrsltCode();
        String resultMsg = null;
        switch (emvResult) {
            case 3:
                resultMsg = context.getString(R.string.msg_pboc_online);
                break;
            case 15:
                resultMsg = context.getString(R.string.msg_rfqpboc_online);
                break;
        }
        showMessage(context.getString(R.string.msg_request_online_result) + resultMsg + "\r\n", MessageTag.DATA);

        if(posEntryMode!=null){
            int cardInterface=posEntryMode[0]&0xff;
            if(AppConfig.isExternalEmv&(cardInterface==EmvL3Const.EntryMode.MSR||cardInterface==EmvL3Const.EntryMode.CT_FALLBACK)) {
                byte[] firstTrack = controller.getEmvData(EmvL3Const.L3_DATA.TRACK1);
                byte[] secondTrack = controller.getEmvData(EmvL3Const.L3_DATA.TRACK2);
                byte[] thirdTrack = controller.getEmvData(EmvL3Const.L3_DATA.TRACK3);
                byte[] pan = controller.getEmvData(EmvL3Const.L3_DATA.PAN);
                byte[] servicecode = controller.getEmvData(EmvL3Const.L3_DATA.SERVICE_CODE);
                byte[] expireddate = controller.getEmvData(EmvL3Const.L3_DATA.EXPIRE_DATE);
                showMessage(context.getString(R.string.common_first_track) + (firstTrack == null ? "null" : Dump.getHexDump(firstTrack)) + "\r\n", MessageTag.DATA);
                showMessage(context.getString(R.string.common_second_track) + (secondTrack == null ? "null" : Dump.getHexDump(secondTrack)) + "\r\n", MessageTag.DATA);
                showMessage(context.getString(R.string.common_third_track) + (thirdTrack == null ? "null" : Dump.getHexDump(thirdTrack)) + "\r\n", MessageTag.DATA);
                showMessage(context.getString(R.string.msg_card_no) + (pan == null ? "null" : Dump.getHexDump(pan)), MessageTag.DATA);
                showMessage(context.getString(R.string.msg_card_vaild_date) + (expireddate == null ? "null" : Dump.getHexDump(expireddate)), MessageTag.DATA);
                showMessage(context.getString(R.string.msg_service_code) + (servicecode == null ? "null" : Dump.getHexDump(servicecode)), MessageTag.DATA);
                //  Online transaction result , true if  get online transaction response,   false if online request exception or host no response,ect.
                boolean onlineResuestResult = true;
                OnlineTransactionData onlineTransactionData = new OnlineTransactionData();

                if (onlineResuestResult) {
                    //0x8a Transaction reply code: Get from host response DE 39.
                    // TODO pls filled  with the actual value of host response .
                    onlineTransactionData.setAuthorisationResponseCode("00");
                    //  TODO 0x89 Authorization code
                    // onlineTransactionData.setAuthorisationCode("504343");
                    // TODO  filled  with host response data of 8583 message  DE 55
                    //onlineTransactionData.setTlvData(ISOUtils.hex2byte("910A0B8B433AFD5C54F53030"));
                } else {
                    //if online request exception or host no response,ect.
                    onlineTransactionData.setAuthorisationResponseCode("01");
                }
                // [step2].Get Online transaction result and call completeEMVProcess method to end of emv process，then  onEmvfinished method triggered after calling secondIssuance..
                controller.completeEMVProcess(onlineTransactionData);
                return;
            }

        }
            int[] emvTags = new int[5];
            emvTags[0] = 0x5a;
            emvTags[1] = 0x5F34;
            emvTags[2] = 0x5f24;
            emvTags[3] = 0x57;
            emvTags[4] = 0x9f06;
            TLVPackage tlv = controller.getEmvData(emvTags);
            showMessage("AID:" + hexString(tlv.getValue(0x9f06)) + "\r\n", MessageTag.DATA);
            String cardNo = tlv.getString(0x5a);
            String cardSN = tlv.getString(0x5F34);// Card serial number == context.getCardSequenceNumber()
            String track2 = tlv.getString(0x57); // Two track data == context.getTrack_2_eqv_data()
            if (null == cardNo && track2 != null) {
                cardNo = track2.substring(0, track2.indexOf('D'));
            }
            String expiredDate = null;
            if (track2 != null) {
                expiredDate = track2.substring(track2.indexOf('D') + 1, track2.indexOf('D') + 5);
            }
            if (cardSN == null) {
                cardSN = "000";
            } else {
                cardSN = ISOUtils.padleft(cardSN, 3, '0');
            }
            String serviceCode = "";
            if (null != track2) {
                serviceCode = track2.substring(track2.indexOf('D') + 5, track2.indexOf('D') + 8);
            }
            //Since the array is BCD encoded, the last digit of the card number needs to be removed if it is 'F'.
            if (null != cardNo && cardNo.endsWith("F"))
                cardNo = cardNo.substring(0, cardNo.length() - 1);
            showMessage(context.getString(R.string.msg_term_cardNo) + cardNo + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_term_cardSN) + cardSN + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_term_expiredDate) + expiredDate + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_term_serviceCode) + serviceCode + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_term_track2) + track2 + "\r\n", MessageTag.DATA);

            TLVPackage tlvPackage = emvModule.getEmvData(L_55TAGS);
            //TLVPackage tlvPackage = controller.getEmvData(L_55TAGS);
            byte[] data55 = tlvPackage.pack();
//        showMessage(context.getString(R.string.msg_term_55tag) + (data55 == null ? null : ISOUtils.hexString(data55)) + "\r\n", MessageTag.DATA);
            AppConfig.EMV.ic55Data = data55;// Trans info
            tlvPackage = controller.getEmvData(L_SCRIPTTAGS);
            byte[] dataScript = tlvPackage.pack();
//        showMessage(context.getString(R.string.msg_term_script) + (dataScript == null ? null : ISOUtils.hexString(dataScript)) + "\r\n", MessageTag.DATA);
            tlvPackage = controller.getEmvData(L_REVTAGS);
            byte[] revData = tlvPackage.pack();
//        showMessage(context.getString(R.string.msg_term_flushes_data) + (revData == null ? null : ISOUtils.hexString(revData)) + "\r\n", MessageTag.DATA);

            //TODO contactless transaction pin entry handle process
            if (controller.getEMVTransInfo().getOpenCardType() == CardType.RFCARD && transType != TransactionType.EC_APPOINTED_LOAD_CTLS && transType != TransactionType.EC_NOT_APPOINTED_LOAD_CTLS && transType != TransactionType.EC_CASH_LOAD_CTLS) {
                byte[] data_9F51 = emvModule.getEmvData(0x9F51);
                byte[] data_DF71 = emvModule.getEmvData(0xDF71);

                if (Arrays.equals(data_9F51, new byte[]{0x01, 0x56}) || Arrays.equals(data_DF71, new byte[]{0x01, 0x56})) {//unionpay
                    //todo pin input
                } else {
                    // * NO CVM:0x00; OBTAIN SIGNATURE:0x10; ONLINE PIN:0x20;CONFIRMATION CODE VERIFIED:0x30;
                    if (controller.getEMVTransInfo().getCvm() == EmvConst.OP_ONLINE_PIN) {
                        // todo pin input
                    }
                }

                // [step1]：get ic card data from controller.getEMVTransInfo(),and pack ISO8583 mesaage then send to host
                // TODO Rquest host Online contactless transaction ....
                // [step2].Get Online transaction result and call completeEMVProcess method to end of emv process，then  onEmvfinished method triggered.

                //  Online transaction result , true if  get online transaction response,   false if online request exception or host no response,ect.
                boolean onlineResuestResult = true;
                OnlineTransactionData onlineTransactionData = new OnlineTransactionData();

                if (onlineResuestResult) {
                    //0x8a Transaction reply code: Get from host response DE 39.
                    // pls filled  with the actual value from host response .
                    onlineTransactionData.setAuthorisationResponseCode("00");
                } else {
                    //if online request exception or host no response,ect.
                    onlineTransactionData.setAuthorisationResponseCode("01");
                }
                controller.completeEMVProcess(onlineTransactionData);// set Online result to end of emv process.
            } else {
                //TODO  contact transaction handle process

                // [step1]：get ic card data from controller.getEMVTransInfo() then send to host
                // TODO Rquest host Online contact transaction ....

                //  Online transaction result , true if  get online transaction response,   false if online request exception or host no response,ect.
                boolean onlineResuestResult = true;
                OnlineTransactionData onlineTransactionData = new OnlineTransactionData();

                if (onlineResuestResult) {
                    //0x8a Transaction reply code: Get from host response DE 39.
                    // TODO pls filled  with the actual value of host response .
                    onlineTransactionData.setAuthorisationResponseCode("00");
                    //  TODO 0x89 Authorization code
                    // onlineTransactionData.setAuthorisationCode("504343");
                    // TODO  filled  with host response data of 8583 message  DE 55
                    //onlineTransactionData.setTlvData(ISOUtils.hex2byte("910A0B8B433AFD5C54F53030"));
                } else {
                    //if online request exception or host no response,ect.
                    onlineTransactionData.setAuthorisationResponseCode("01");
                }
                // [step2].Get Online transaction result and call completeEMVProcess method to end of emv process，then  onEmvfinished method triggered after calling secondIssuance..
                controller.completeEMVProcess(onlineTransactionData);
            }

    }

    /**
     * this method will be triggered if useing Multi-application card
     * pls prompt AID list in order to select application in your App.
     *
     * @param emvTransController
     * @param aidEntityList      AID  item list
     * @param times
     */
    @Override
    public void onRequestSelectApplication(final EMVTransController emvTransController, List<AIDEntity> aidEntityList, int times) {
        showMessage("onRequestSelectApplication", MessageTag.TIP);
        showMessage(context.getString(R.string.msg_select_app_hint) + times, MessageTag.DATA);
        final List<Integer> indexList = new ArrayList<Integer>();
        List<byte[]> aidList = new ArrayList<byte[]>();

        for (AIDEntity entry : aidEntityList) {
            indexList.add(entry.getIndex());
            aidList.add(entry.getAid());
            showMessage(context.getString(R.string.msg_aid_name) + entry.getName(), MessageTag.DATA);
            showMessage(context.getString(R.string.msg_aid) + hexString(entry.getAid()), MessageTag.DATA);
        }
        String items[] = new String[aidList.size()];
        for (int i = 0; i < aidList.size(); i++) {
            items[i] = ISOUtils.hexString(aidList.get(i));
        }
        DialogUtils.createSingleChoiceDialog(context, context.getString(R.string.msg_select_app_hint), items, new DialogUtils.SingleChoiceDialogCallback() {
            @Override
            public void onResult(int id) {
                try {
                    if (id < 0) {
                        showMessage("Cancel Select aid", MessageTag.ERROR);
                        emvTransController.cancelEMVProcess();
                        return;
                    }
                    showMessage("Selected id:" + id, MessageTag.DATA);
                    emvTransController.setSelectedApplication(indexList.get(id));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    byte[] posEntryMode;
    @Override
    public void onRequestConfirmCardInfo(EMVTransController controller) {
        showMessage("onRequestConfirmCardInfo", MessageTag.TIP);
        showMessage(context.getString(R.string.msg_trans_confirm_finish) + "\r\n", MessageTag.DATA);
        if(AppConfig.isExternalEmv){
           posEntryMode=controller.getEmvData(EmvL3Const.L3_DATA.POS_ENTRY_MODE);
            int cardInterface=posEntryMode[0]&0xff;
            showMessage("cardInterface："+cardInterface, MessageTag.TIP);
            switch (cardInterface){
                case EmvL3Const.EntryMode.MSR:
                    showMessage("Find MAGSTRIPE", MessageTag.TIP);
                    break;
                case EmvL3Const.EntryMode.ICC:
                    showMessage("Find CONTACT", MessageTag.TIP);
                    break;
                case EmvL3Const.EntryMode.CLSS:
                    showMessage("Find CONTACTLESS", MessageTag.TIP);
                    break;
                case EmvL3Const.EntryMode.MANUAL:
                    showMessage("MANUAL", MessageTag.TIP);
                    break;
                case EmvL3Const.EntryMode.CT_FALLBACK:
                    showMessage("CT_FALLBACK", MessageTag.TIP);
                    break;
            }
        }
        controller.confirmInformation(true);
    }

    /**
     * this method will be triggered when  performt contact transaction and pin entry require。
     *
     * @param controller
     * @param requireOnline
     * @param pinEntity
     */
    @Override
    public void onRequestInputPIN(final EMVTransController controller, boolean requireOnline, PINEntity pinEntity) {
        showMessage("onRequestInputPIN", MessageTag.TIP);
        TLVPackage tlvPackage = controller.getEmvData(new int[]{0x5a});
        String cardNumber = tlvPackage.getString(0x5a);
        if (null != cardNumber && cardNumber.endsWith("F")) {
            cardNumber = cardNumber.substring(0, cardNumber.length() - 1);
        }
        PinEntryListener pinEntryListener = new PinEntryListener() {
            @Override
            public void onFinish(byte[] pinblock) {
                if (pinblock != null) {
                    controller.setPIN(pinblock);
                } else {
                    controller.cancelEMVProcess();
                }
                AppConfig.setPinEntryListener(null);
            }
        };
        AppConfig.setPinEntryListener(pinEntryListener);
        doPinInput(requireOnline, cardNumber, pinEntity);
    }

    /**
     * input password
     *
     * @param isOnline is it online pin?
     * @param cardNum  card number
     * @throws Exception
     */
    public void doPinInput(boolean isOnline, String cardNum, PINEntity pinEntity) {
        showMessage("doPinInput isOnline:" + isOnline + ";cardNum:" + cardNum+";isExternalEmv"+AppConfig.isExternalEmv, MessageTag.DATA);
        if (isOnline) {
            ((MainActivity) context).startOnlinePinInput(cardNum, AppConfig.isExternalEmv,false);
        } else {
            ((MainActivity) context).startOfflinePinInput(pinEntity.getModulus(), pinEntity.getExponent(), AppConfig.isExternalEmv);
        }
    }

    /**
     * account type selection
     * <p>
     * return to int range
     * <p>
     * <ol>
     * <li>default</li>
     * <li>savings</li>
     * <li>Cheque/debit</li>
     * <li>Credit</li>
     * </ol>
     *
     * @return 1-4:selection range， －1：failed
     */
    @Override
    public void onRequestSelectAccountType(EMVTransController controller, AccountType[] types) {
        showMessage("onRequestSelectAccountType", MessageTag.TIP);
        controller.setSelectedAccountType(AccountType.DEFAULT);
    }

    /**
     * cardHolder certificated confirmation
     * <p>
     *
     * @return true:confirmation succeed， false:confirmation failed
     */
    @Override
    public void onRequestConfirmID(EMVTransController controller, IDCardType certType, String certno) {
        showMessage("onRequestConfirmID", MessageTag.TIP);
        controller.confirmID(true);
    }

    /**
     * Ecash /emv selection
     * <p>
     * transaction return：
     * <p>
     * <ul>
     * <li>1:continue electronic cash transactions</li>
     * <li>0:do not carry out electronic cash transactions</li>
     * <li>－1:user termination</li>
     * <li>－3:time out</li>
     * </ul>
     */
    @Override
    public void onRequestConfirmEC(final EMVTransController controller) {
        try {
            showMessage("onRequestConfirmEC", MessageTag.TIP);
            final Builder builder = new Builder(context);
            builder.setMessage(context.getString(R.string.msg_is_use_ecash));
            builder.setPositiveButton(context.getString(R.string.common_yes), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    controller.confirmEC(true);
                }
            });
            builder.setNegativeButton(context.getString(R.string.common_no), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    controller.confirmEC(false);
                }
            });
            ((MainActivity) context).runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    builder.setCancelable(false);
                    builder.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            controller.cancelEMVProcess();
        }

    }


    /**
     * display info
     *
     * @param title        title
     * @param msg          message
     * @param yesnoShowed  whether show yes no
     * @param waittingTime waiting time
     * @return if yesnoShow is equal to true, return 1 means confirmation.
     * return 0 means cancel.
     * if yesnoShow is equal to false,return value has no meaning.
     */
    @Override
    public void onRequestShowMessage(EMVTransController controller, String title, String msg, boolean yesnoShowed, int waittingTime) {
        showMessage("onRequestShowMessage", MessageTag.TIP);
        final Builder builder = new Builder(context);
        builder.setMessage(title+msg);
        builder.setPositiveButton(context.getString(R.string.common_yes), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                controller.confirmMessage(true);
            }
        });
        if(yesnoShowed){
            builder.setNegativeButton(context.getString(R.string.common_no), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    controller.confirmMessage(false);
                }
            });
        }
        ((MainActivity) context).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                builder.setCancelable(false);
                builder.show();
            }
        });

    }


    @Override
    public void onRequestSelectLanguage(EMVTransController controller, String[] language) {
        showMessage("onRequestSelectLanguage", MessageTag.TIP);
        if (language != null && language.length > 0) {
            controller.setSelectedLanguage(language[0]);
        } else {
            controller.cancelEMVProcess();
        }
    }


    @Override
    public void onRequestInputAmount(final EMVTransController controller) {
        showMessage("onRequestInputAmount", MessageTag.TIP);
        showMessage(context.getString(R.string.msg_money_callback_request) + "\r\n", MessageTag.NORMAL);
        DialogUtils.createCustomDialog(context, context.getString(R.string.msg_enter_preauto_money), null, R.layout.dialog_amtinput, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                if (id == 0) {//sure
                    Editable editable = ((EditText) dialogView.findViewById(R.id.edit_amt_input)).getText();
                    if (editable.toString().equals("") || editable.toString() == null) {
                        showMessage(context.getString(R.string.msg_preauth_money_null) + "\r\n", MessageTag.NORMAL);
                        controller.cancelEMVProcess();
                    } else {
                        DecimalFormat df = new DecimalFormat("#.00");
                        BigDecimal amt = new BigDecimal(editable.toString());
                        AppConfig.EMV.amt = amt;
                        showMessage(context.getString(R.string.msg_preauth_money) + df.format(amt) + "\r\n", MessageTag.NORMAL);
                        controller.setTransactionAmount(amt);
                    }
                } else if (id == -1) {//cancel
                    showMessage(context.getString(R.string.msg_trans_cancel) + "\r\n", MessageTag.NORMAL);
                    controller.cancelEMVProcess();    //When the amount of pre-authorization is empty ,it means to cancel the transaction.
                }
            }
        });
    }

    /**
     * this method will be triggered after  applaction selected .
     * emv kernel config able to change runtime in this step.
     * NOTE: the  emv kernel data only set to runtime memory,  don't have set to kernel file.
     *
     * @param emvTransController
     */
    @Override
    public void onRequestConfirmFinalAppSelection(EMVTransController emvTransController) {
        showMessage("onRequestConfirmFinalAppSelection", MessageTag.TIP);
        // change emv kernel data by tag  and value
        //emvModule.setEmvData(0x9F02, ISOUtils.hex2byte("000000000800"));

//        if(AppConfig.isExternalEmv&&emvTransController.getEmvData(EmvL3Const.L3_DATA.POS_ENTRY_MODE)!=null) {
//            int cardInterface = emvTransController.getEmvData(EmvL3Const.L3_DATA.POS_ENTRY_MODE)[0]&0xff;
//            switch (cardInterface) {
//                case EmvL3Const.EntryMode.MSR:
//                    showMessage("Find MAGSTRIPE", MessageTag.TIP);
//                    break;
//                case EmvL3Const.EntryMode.ICC:
//                    showMessage("Find CONTACT", MessageTag.TIP);
//                    break;
//                case EmvL3Const.EntryMode.CLSS:
//                    showMessage("Find CONTACTLESS", MessageTag.TIP);
//                    break;
//                case EmvL3Const.EntryMode.MANUAL:
//                    showMessage("MANUAL", MessageTag.TIP);
//                    break;
//                case EmvL3Const.EntryMode.CT_FALLBACK:
//                    showMessage("CT_FALLBACK", MessageTag.TIP);
//                    break;
//            }
//        }
        emvTransController.confirmInformation(true);
    }

    private void showMessage(String msg, int messageType) {
        ((MainActivity) context).showMessage(msg, messageType);
    }

    public void operateLED() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SystemClock.sleep(750);
                    MainActivity.getLedOperationRunnable().enterStandByMode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private String hexString(byte[] data) {
        return data == null ? "null" : ISOUtils.hexString(data);
    }

}
