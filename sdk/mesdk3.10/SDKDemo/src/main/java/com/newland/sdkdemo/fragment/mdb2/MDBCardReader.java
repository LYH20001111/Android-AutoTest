package com.newland.sdkdemo.fragment.mdb2;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.newland.nsdk.unattended.mdb.BeginSessionParameters;
import com.newland.nsdk.unattended.mdb.CashlessState;
import com.newland.nsdk.unattended.mdb.MDBModuleImpl;
import com.newland.nsdk.unattended.mdb.VmcDataBean;
import com.newland.sdk.ModuleManage;
import com.newland.sdk.module.cardreader.CardReaderExtParams;
import com.newland.sdk.module.cardreader.CardReaderListener;
import com.newland.sdk.module.cardreader.CardReaderModule;
import com.newland.sdk.module.cardreader.CardType;
import com.newland.sdk.module.cardreader.RFCardInfo;
import com.newland.sdk.module.cardreader.SearchCardRule;
import com.newland.sdk.module.rfcard.RFCardType;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.utils.MessageTag;

public class MDBCardReader {
    private static final String TAG = "RfidCard";
    private Context context;
    private CardReaderModule cardReaderModule;
    private String status;
    private Handler mhandler;
    private VmcDataBean vmcDataBean = MDBModuleImpl.getInstance().getVmcDataBean();

    public MDBCardReader(Context context, Handler handler) {
        this.context = context;
        this.mhandler = handler;
    }

    public void showMessage(String msg,int type) {
        Message mess = new Message();
        mess.what = 100;
        mess.obj = msg;
        mess.arg1 = type;
        mhandler.sendMessage(mess);
    }

    private CardReaderListener cardReaderListener = new CardReaderListener() {
        @Override
        public void onTimeout() {
            showMessage(context.getString(R.string.msg_timeout) + "\r\n", MessageTag.NORMAL);
        }

        @Override
        public void onCancel() {
            showMessage(context.getString(R.string.msg_cancel_open_reader) + "\r\n", MessageTag.NORMAL);
        }

        @Override
        public void onError(int errorCode, String message) {
            showMessage(context.getString(R.string.msg_reader_open_exception) + message + "\r\n", MessageTag.ERROR);
        }

        @Override
        public void onFindMagCard(boolean isSuccessful) {
            showMessage(context.getString(R.string.msg_cardreader_swiper), MessageTag.DATA);
            showMessage(context.getString(R.string.msg_swiper_result) + isSuccessful, MessageTag.DATA);
        }

        @Override
        public void onFindICCard() {
            showMessage(context.getString(R.string.msg_cradreader_insert), MessageTag.DATA);
        }

        @Override
        public void onFindRFCard(@Nullable RFCardType rfCardType, @Nullable RFCardInfo rfCardInfo) {
            showMessage(context.getString(R.string.msg_cardreader_rfcard), MessageTag.DATA);
            Log.d(TAG, "card detected");
            Log.d(TAG, " Current thread ID is " + Thread.currentThread().getId());
            MDBModuleImpl cashlessDevice = MDBModuleImpl.getInstance();

            if (status.equals("Enable")) {
                showMessage("Begin session",MessageTag.TIP);
                try {
                    char fundsAvailable = 0xffff;
                    int paymentMediaID = 0xFFFFFFFF;
                    byte paymentType = 0x00;
                    char paymentData = 0X0000;
                    char userLanguage = 0x1840;
                    char userCurrencyCode = 0x1840;
                    byte cardOptions = 0x07;
                    if (cashlessDevice.getState() != CashlessState.CASHLESS_STATE_ENABLE) return;
                    BeginSessionParameters beginSessionParameters = new BeginSessionParameters();
                    if (vmcDataBean.vmcFeatureLevel == 0x01) {
                        beginSessionParameters.setFundsAvailable((byte) fundsAvailable);
                        cashlessDevice.responseBeginSession(beginSessionParameters);
                    } else if (vmcDataBean.vmcFeatureLevel == 0x03 && vmcDataBean.isExpandCurrencyMode) {
                        int funds = 0xffffffff;
                        beginSessionParameters.setFundsAvailable((byte) funds);
                        beginSessionParameters.setPaymentMediaID(paymentMediaID);
                        beginSessionParameters.setPaymentType(paymentType);
                        beginSessionParameters.setPaymentData((byte) paymentData);
                        beginSessionParameters.setCardOptions(cardOptions);
                        beginSessionParameters.setUserLanguage(userLanguage);
                        beginSessionParameters.setUserCurrencyCode(userCurrencyCode);
                        cashlessDevice.responseBeginSession(beginSessionParameters);
                    } else {
                        beginSessionParameters.setFundsAvailable((byte) fundsAvailable);
                        beginSessionParameters.setPaymentMediaID(paymentMediaID);
                        beginSessionParameters.setPaymentType(paymentType);
                        beginSessionParameters.setPaymentData((byte) paymentData);
                        cashlessDevice.responseBeginSession(beginSessionParameters);
                    }

                } catch (Exception e) {
                    Log.d(TAG, "err responseBeginSession" + e.getMessage());
                    showMessage( e.getMessage(),MessageTag.ERROR);
                    return;
                }
                deactive();
            } else if (status.equals("Vend")) {
                status = "VendFinish";
                showMessage("Vend Approved",MessageTag.TIP);
                deactive();
            } else if (status.equals("Refound")) {
                status = "VendFinish";
                Log.d(TAG, "Refound: success");
                showMessage("Refound success",MessageTag.TIP);
                deactive();
            }
        }
    };

    public void active(String statusvalue) {
        CardType[] cardTypes = new CardType[]{CardType.MSGCARD, CardType.ICCARD, CardType.RFCARD};
        CardReaderExtParams cardReaderExtParams = new CardReaderExtParams();
        cardReaderExtParams.setSearchCardRule(SearchCardRule.RFCARD_QUICKLY);
        ModuleManage.getInstance().getCardReaderModule().openCardReader(cardTypes, 60, cardReaderListener, cardReaderExtParams);
        status = statusvalue;
    }

    public boolean isVendFinish() {
        return status.equals("VendFinish") || status.equals("VendDenied");
    }

    public void setVendResult(String result) {
        status = result;
    }

    public String getVendResult() {
        return status;
    }

    public void deactive() {
        ModuleManage.getInstance().getCardReaderModule().cancelCardReader();
    }

}
