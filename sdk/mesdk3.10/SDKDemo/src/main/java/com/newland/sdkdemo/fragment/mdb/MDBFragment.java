package com.newland.sdkdemo.fragment.mdb;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.newland.emv.jni.type.EmvConst;
import com.newland.nsdk.mdb.MDBException;
import com.newland.nsdk.mdb.MDBModule;
import com.newland.nsdk.mdb.MDBModuleImpl;
import com.newland.nsdk.mdb.callback.ConfigData;
import com.newland.nsdk.mdb.callback.FeatureBitmap;
import com.newland.nsdk.mdb.callback.MDBConfiguration;
import com.newland.nsdk.mdb.callback.MDBDeviceInfoType;
import com.newland.nsdk.mdb.callback.MDBFunctionCallBack;
import com.newland.nsdk.mdb.callback.PriceLimitParams;
import com.newland.nsdk.mdb.callback.ReaderParameters;
import com.newland.nsdk.mdb.callback.RequestIDParameters;
import com.newland.nsdk.mdb.callback.RevalueAmount;
import com.newland.nsdk.mdb.callback.VendParameters;
import com.newland.sdk.ModuleManage;
import com.newland.sdk.module.cardreader.CardReaderExtParams;
import com.newland.sdk.module.cardreader.CardReaderListener;
import com.newland.sdk.module.cardreader.CardReaderModule;
import com.newland.sdk.module.cardreader.CardType;
import com.newland.sdk.module.cardreader.RFCardInfo;
import com.newland.sdk.module.cardreader.SearchCardRule;
import com.newland.sdk.module.emv.AIDEntity;
import com.newland.sdk.module.emv.AccountType;
import com.newland.sdk.module.emv.EMVControllerListener;
import com.newland.sdk.module.emv.EMVModule;
import com.newland.sdk.module.emv.EMVTransController;
import com.newland.sdk.module.emv.IDCardType;
import com.newland.sdk.module.emv.OnlineTransactionData;
import com.newland.sdk.module.emv.PINEntity;
import com.newland.sdk.module.emv.TransactionType;
import com.newland.sdk.module.rfcard.RFCardType;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdk.utils.TLVPackage;
import com.newland.sdkdemo.AppConfig;
import com.newland.sdkdemo.MainActivity;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;
import com.newland.sdkdemo.event.PinEntryListener;
import com.newland.sdkdemo.fragment.BaseFragment;
import com.newland.sdkdemo.fragment.mdb.ReturnValue.*;
import com.newland.sdkdemo.fragment.mdb2.MDBActivity;
import com.newland.sdkdemo.utils.DialogUtils;
import com.newland.sdkdemo.utils.MessageTag;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author by bxy, Date on 2019/12/19.
 */
public class MDBFragment extends BaseFragment {

    private static final String TAG = "MDBFragment";
    private MDBConfiguration mdbConfiguration;
    private MDBModule mdbModule;

    private CardBean cardBean;
    private TransBean transBean;

    public ConfigData  configData;
    public PriceLimitParams priceLimitParams;
    private volatile boolean performMDBIng = false;

    private Object readerLock = new Object();
    private Object vendLock = new Object();
    private CardReaderModule cardReader;
    private EMVModule emvModule;
    private EMVTransController emvTransController0;
    public MDBFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_mdb_f);
    }

    @Override
    public void initData() {
        cardBean = new CardBean();
        transBean = new TransBean();
        configData = new ConfigData();
        priceLimitParams = new PriceLimitParams();
        mdbConfiguration = new MDBConfiguration();
        mdbModule = MDBModuleImpl.getInstance();
        cardReader = ModuleManage.getInstance().getCardReaderModule();
        emvModule = ModuleManage.getInstance().getEMVModule();
        emvModule.init(context, null);
    }

    @Override
    public Object getModule() {
        return MDBFragment.this;
    }

    @Override
    public int getSpanCount() {
        return 2;
    }

    private static final int INDEX_START = 1;
    private static final int INDEX_STOP = 2;
    private static final int INDEX_START_MDB2 = 3;

    @MethodGridEntity(btnnameid = R.string.perform_mdb, functionid = INDEX_START)
    private void performMDB() {
        if(performMDBIng){
            showMessage("MDB is running......");
            return;
        }
        performMDBIng = true;
        //If you want to view MDB logs
        mdbConfiguration.setDebug(1);
        //If you want to run at level 3
        mdbConfiguration.setFeatureLevel((byte) 0x03);
        try {
            //init MDB
            mdbModule.init();

            //Set MDB config
            Log.d(TAG,"MDBConfig: Level:"+mdbConfiguration.getFeatureLevel()+"  Address:"+mdbConfiguration.getCashlessAddress()+"  Always idle"+mdbConfiguration.getAlwaysIdleAllow());
            mdbModule.setMDBConfig(mdbConfiguration);

            //Set the callback function
            mdbModule.setMDBCallback(callBack);
        } catch (MDBException e) {
            showMessage(e.getMessage());
        }

        clearMessage();
        showMessage("WELCOME!");


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mdbModule.perform();
                } catch (MDBException e) {
                    showMessage(e.getMessage());
                }
            }
        }).start();
    }

    @MethodGridEntity(btnnameid = R.string.stop_mdb, functionid = INDEX_STOP)
    private void stopMDB() {
        if(cardBean.isReadCard())
            cardReader.cancelCardReader();
        try {
            mdbModule.stop();
            mdbModule.cleanUp();
        } catch (MDBException e) {
            showMessage(e.getMessage());
        }
        performMDBIng = false;
    }

    @MethodGridEntity(btnnameid = R.string.start_mdb, functionid = INDEX_START_MDB2)
    private void startMDB2() {
        Intent intent = new Intent(context, MDBActivity.class);
        context.startActivity(intent);
    }

    private MDBFunctionCallBack callBack = new MDBFunctionCallBack() {

        @Override
        public void reset() {
            Log.d(TAG,"reset");
            clearMessage();
            showMessage("resetting......");
            if (cardBean.isReadCard()) {
                cardBean.setResult(ReturnValue.CallbackResult.CALLBACK_ERROR);
                cardReader.cancelCardReader();
            }
            if(transBean.isVending()) {

            }
            clearMessage();
            showMessage("welcome");
        }

        @Override
        public void setUpConfigData(ConfigData configData) {
            Log.d(TAG,"into callback setUpConfigData======>");
            clearMessage();
            showMessage("CMD:setUpConfigData");
            MDBFragment.this.configData = configData;
            //TODO
        }

        @Override
        public void setUpPriceLimit(PriceLimitParams priceLimit) {
            Log.d(TAG,"into callback setUpPriceLimit======>");
            String maxPrice;
            String minPrice;
            MDBFragment.this.priceLimitParams = priceLimit;
            clearMessage();
            try {
                maxPrice = new String(priceLimit.getMaxPrice(), "ascii");
                minPrice = new String(priceLimit.getMinPrice(), "ascii");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            showMessage("Max Price:" + maxPrice);
            showMessage("Min Price:" + minPrice);
            //TODO
        }

        @Override
        public int enableReader(ReaderParameters readerParameters) {
            Log.d(TAG,"into callback enableReader======>");
            byte[] alwaysIdleFlag = new byte[2];

            clearMessage();

            try {
                alwaysIdleFlag = mdbModule.getMDBDeviceInfo(MDBDeviceInfoType.ALWAYS_IDLE);
            } catch (MDBException e) {
                showMessage(e.getMessage(), MessageTag.ERROR);
            }
            if(alwaysIdleFlag[0] != 0)
            {
                showMessage("Please Select Product");
                return ReturnValue.CallbackResult.CALLBACK_OK;
            }
            else
            {
                SystemClock.sleep(1000);
                //if “always idle” disabled,vend request cannot be processed until a valid payment media is read
                showMessage("Please Tap Card");
                showMessage("Sign In");

                cardBean.setResult(ReturnValue.CallbackResult.CALLBACK_ERROR);

                CardType[] cardTypes = new CardType[]{CardType.MSGCARD, CardType.ICCARD, CardType.RFCARD};
                CardReaderExtParams cardReaderExtParams = new CardReaderExtParams();
                cardReaderExtParams.setSearchCardRule(SearchCardRule.RFCARD_QUICKLY);
                CardReaderListener cardReaderListener = new CardReaderListener(){
                    @Override
                    public void onTimeout() {
                        // Handle timeout
                        clearMessage();
                        showMessage("Time out!");
                        showMessage("Card Auth Fail");
                        cardBean.setResult(CallbackResult.CALLBACK_TIMEOUT);
                        cardBean.setReadCard(false);
                        synchronized (readerLock){
                            readerLock.notify();
                        }
                    }

                    @Override
                    public void onCancel() {
                        // Handle cancel
                        clearMessage();
                        if(cardBean.getResult() == CallbackResult.CALLBACK_DISABLE)
                            showMessage("Reader Disable");

                        cardBean.setReadCard(false);
                        synchronized (readerLock){
                            readerLock.notify();
                        }
                    }

                    @Override
                    public void onError(int code, String message) {
                        Log.d(TAG,"card read error,error code"+code+"  Tip:"+message);
                        // Handle error
                        clearMessage();
                        showMessage("Card Auth Fail");
                        cardBean.setResult(CallbackResult.CALLBACK_ERROR);
                        cardBean.setReadCard(false);
                        synchronized (readerLock){
                            readerLock.notify();
                        }
                    }

                    @Override
                    public void onFindMagCard(boolean b) {
                        clearMessage();
                        showMessage("Card Auth OK");
                        showMessage("Please Select Product");
                        cardBean.setResult(CallbackResult.CALLBACK_OK);
                        cardBean.setReadCard(false);
                        synchronized (readerLock){
                            readerLock.notify();
                        }
                    }

                    @Override
                    public void onFindICCard() {
                        clearMessage();
                        showMessage("Card Auth OK");
                        showMessage("Please Select Product");
                        cardBean.setResult(CallbackResult.CALLBACK_OK);
                        cardBean.setReadCard(false);
                        synchronized (readerLock){
                            readerLock.notify();
                        }
                    }

                    @Override
                    public void onFindRFCard(@Nullable RFCardType rfCardType, @Nullable RFCardInfo rfCardInfo) {
                        clearMessage();
                        showMessage("Card Auth OK");
                        showMessage("Please Select Product");
                        cardBean.setResult(CallbackResult.CALLBACK_OK);
                        cardBean.setReadCard(false);
                        synchronized (readerLock){
                            readerLock.notify();
                        }
                    }
                };
                cardBean.setReadCard(true);
                cardReader.openCardReader(cardTypes, 0, cardReaderListener, cardReaderExtParams);
            }

            synchronized (readerLock){
                try {
                    readerLock.wait();
                    return cardBean.getResult();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }

        @Override
        public void cancelReader() {
            Log.d(TAG,"into callback cancelReader======>");
            showMessage("Cancel Reader");
            //abort payment media reader activities
            //TODO
        }

        @Override
        public void disableReader() {
            Log.d(TAG,"into callback disableReader======>");
            clearMessage();
            showMessage("Disable Reader");
            cardBean.setResult(ReturnValue.CallbackResult.CALLBACK_DISABLE);
            if(cardBean.isReadCard())
                cardReader.cancelCardReader();
        }

        //完成交易过程
        @Override
        public int requestVend(VendParameters vendParameters) {
            Log.d(TAG,"into callback requestVend======>");
            String price;

            byte[] discountedAmount = vendParameters.getItemPrice();//In practice, you might need to multiply by a coefficient
            //if the card is VIP, may get a discount
            vendParameters.setAmount(discountedAmount);

            clearMessage();
            transBean.setResult(ReturnValue.CallbackResult.CALLBACK_ERROR);

            try {
                price = new String(vendParameters.getItemPrice(),"ascii");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            int itemPrice = (int) (Double.parseDouble(price)*Math.pow(10,mdbConfiguration.getDecimalPlaces()));
            String itemIndex = String.valueOf(vendParameters.getItemIndex());

            transBean.setVending(true);
            emvTransController0 = emvModule.getEmvTransController(new EMVControllerListener() {
                @Override
                public void onRequestSelectApplication(EMVTransController emvTransController, List<AIDEntity> aidEntityList, int times) {
                    showMessage("MDB onRequestSelectApplication");
                    showMessage(context.getString(R.string.msg_select_app_hint) + times, MessageTag.DATA);
                    final List<Integer> indexList = new ArrayList<Integer>();
                    List<byte[]> aidList = new ArrayList<byte[]>();

                    for (AIDEntity entry : aidEntityList) {
                        indexList.add(entry.getIndex());
                        aidList.add(entry.getAid());
                        showMessage(context.getString(R.string.msg_aid_name) + entry.getName(), MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_aid) + ISOUtils.hexString(entry.getAid()), MessageTag.DATA);
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

                @Override
                public void onRequestConfirmCardInfo(EMVTransController emvTransController) {
                    showMessage("MDB onRequestConfirmCardInfo");
                    emvTransController.confirmInformation(true);
                }

                @Override
                public void onRequestInputAmount(EMVTransController emvTransController) {
                    showMessage("MDB onRequestInputAmount");
                    emvTransController.setTransactionAmount(new BigDecimal(itemPrice));
                }

                @Override
                public void onRequestInputPIN(EMVTransController emvTransController, boolean requireOnline, PINEntity pinEntity) {
                    showMessage("MDB onRequestInputPIN");
                    TLVPackage tlvPackage = emvTransController.getEmvData(new int[]{0x5a});
                    String cardNumber = tlvPackage.getString(0x5a);
                    if (null != cardNumber && cardNumber.endsWith("F")) {
                        cardNumber = cardNumber.substring(0, cardNumber.length() - 1);
                    }
                    PinEntryListener pinEntryListener = new PinEntryListener() {
                        @Override
                        public void onFinish(byte[] pinblock) {
                            if (pinblock != null) {
                                emvTransController.setPIN(pinblock);
                            } else {
                                emvTransController.cancelEMVProcess();
                            }
                            AppConfig.setPinEntryListener(null);
                        }
                    };
                    AppConfig.setPinEntryListener(pinEntryListener);
                    doPinInput(requireOnline, cardNumber, pinEntity);
                }

                @Override
                public void onRequestOnlineProcess(EMVTransController emvTransController) {
                    showMessage("MDB onRequestOnlineProcess");
                    int[] emvTags = new int[5];
                    emvTags[0] = 0x5a;
                    emvTags[1] = 0x5F34;
                    emvTags[2] = 0x5f24;
                    emvTags[3] = 0x57;
                    emvTags[4] = 0x9f06;
                    TLVPackage tlv = emvTransController.getEmvData(emvTags);
                    String cardNo = tlv.getString(0x5a);
                    String track2 = tlv.getString(0x57); // Two track data == context.getTrack_2_eqv_data()
                    if (null == cardNo && track2 != null) {
                        cardNo = track2.substring(0, track2.indexOf('D'));
                    }
                    //Since the array is BCD encoded, the last digit of the card number needs to be removed if it is 'F'.
                    if (null != cardNo && cardNo.endsWith("F"))
                        cardNo = cardNo.substring(0, cardNo.length() - 1);

                    if (emvTransController.getEMVTransInfo().getOpenCardType() == CardType.RFCARD) {
                        byte[] data_9F51 = emvModule.getEmvData(0x9F51);
                        byte[] data_DF71 = emvModule.getEmvData(0xDF71);

                        if (Arrays.equals(data_9F51, new byte[]{0x01, 0x56}) || Arrays.equals(data_DF71, new byte[]{0x01, 0x56})) {//unionpay
                            //todo pin input
                        } else {
                            // * NO CVM:0x00; OBTAIN SIGNATURE:0x10; ONLINE PIN:0x20;CONFIRMATION CODE VERIFIED:0x30;
                            if (emvTransController.getEMVTransInfo().getCvm() == EmvConst.OP_ONLINE_PIN) {
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
                        emvTransController.completeEMVProcess(onlineTransactionData);// set Online result to end of emv process.
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
                        emvTransController.completeEMVProcess(onlineTransactionData);
                    }
                }

                @Override
                public void onEmvFinished(boolean b, EMVTransController emvTransController) {
                    int df75Code = emvTransController.getEMVTransInfo().getExecuteRslt();
                    int emvrsltCode = emvTransController.getEMVTransInfo().getEmvrsltCode();
                    int errorCode = emvTransController.getEMVTransInfo().getErrorcode();
                    showMessage("MDB onEmvFinished df75Code="+df75Code+" emvrsltCode="+emvrsltCode+" errorCode="+errorCode);
                    if(df75Code == 0 || df75Code == 1){
                        transBean.setResult(CallbackResult.CALLBACK_OK);
                    }else {
                        transBean.setResult(CallbackResult.CALLBACK_DENIED);
                    }
                    transBean.setVending(false);
                    synchronized (vendLock){
                        vendLock.notify();
                    }
                }

                @Override
                public void onFallback(EMVTransController emvTransController) {
                    showMessage("MDB onFallback");
                    transBean.setResult(CallbackResult.CALLBACK_DENIED);
                    transBean.setVending(false);
                    synchronized (vendLock){
                        vendLock.notify();
                    }
                }

                @Override
                public void onError(EMVTransController emvTransController, Exception e) {
                    showMessage("MDB onError");
                    transBean.setResult(CallbackResult.CALLBACK_DENIED);
                    transBean.setVending(false);
                    synchronized (vendLock){
                        vendLock.notify();
                    }
                }

                @Override
                public void onRequestSelectAccountType(EMVTransController emvTransController, AccountType[] accountTypes) {
                    showMessage("MDB onRequestSelectAccountType");
                    emvTransController.setSelectedAccountType(AccountType.DEFAULT);
                }

                @Override
                public void onRequestConfirmID(EMVTransController emvTransController, IDCardType idCardType, String s) {
                    showMessage("MDB onRequestConfirmID");
                    emvTransController.confirmID(true);
                }

                @Override
                public void onRequestConfirmEC(EMVTransController emvTransController) {
                    showMessage("MDB onRequestConfirmEC");
                    emvTransController.confirmEC(false);
                }

                @Override
                public void onRequestShowMessage(EMVTransController emvTransController, String title, String msg, boolean yesnoShowed, int waittingTime) {
                    showMessage("MDB onRequestShowMessage");
                    showMessage("onRequestShowMessage", MessageTag.TIP);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(title+msg);
                    builder.setPositiveButton(context.getString(R.string.common_yes), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            emvTransController.confirmMessage(true);
                        }
                    });
                    if(yesnoShowed){
                        builder.setNegativeButton(context.getString(R.string.common_no), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                emvTransController.confirmMessage(false);
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
                public void onRequestSelectLanguage(EMVTransController emvTransController, String[] language) {
                    showMessage("MDB onRequestSelectLanguage");
                    if (language != null && language.length > 0) {
                        emvTransController.setSelectedLanguage(language[0]);
                    } else {
                        emvTransController.cancelEMVProcess();
                    }
                }

                @Override
                public void onRequestConfirmFinalAppSelection(EMVTransController emvTransController) {
                    showMessage("MDB onRequestConfirmFinalAppSelection");
                    emvTransController.confirmInformation(true);
                }
            });
            emvTransController0.startEMV(TransactionType.STANDARD,new BigDecimal(itemPrice),false,null);
            synchronized (vendLock){
                try {
                    vendLock.wait();
                    return transBean.getResult();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public void cancelVend() {
            Log.d(TAG,"into callback cancelVend======>");

            clearMessage();
            showMessage("cancelVend");
            if(transBean.isVending() && emvTransController0 != null){
                emvTransController0.cancelEMVProcess();
            }
            transBean.setResult(ReturnValue.CallbackResult.CALLBACK_CANCEL);
        }

        @Override
        public void onVendSuccess() {
            Log.d(TAG,"into callback onVendSuccess======>");
            clearMessage();
            showMessage("Vend Success");
        }

        @Override
        public int onVendFailure() {
            Log.d(TAG,"into callback onVendFailure======>");
            //The product was not dispensed. Funds should be refunded to user’s account.
            clearMessage();
            showMessage("Vend Failure");
            //TODO
            return 0;
        }

        @Override
        public void onVendTimeout() {
            //Not In Use
        }

        @Override
        public int onSessionCompleted() {
            Log.d(TAG,"into callback onSessionCompleted======>");
            showMessage("CMD:Session Complete");
            //TODO
            return 0;
        }

        @Override
        public int cashSale(VendParameters vendParameters) {
            Log.d(TAG,"into callback cashSale======>");
            //cash sale is not implemented yet and this callback function will not be used
            //Passing information between different payment devices, which is used in the cases of mixed payment
            return 0;
        }


        @Override
        public int requestRevalue(RevalueAmount amount) {
            Log.d(TAG,"into callback requestRevalue======>");
            String revalueAmount;
            try {
                revalueAmount = new String(amount.getAmount(),"ascii");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            clearMessage();
            showMessage("revalue amount:" + revalueAmount);
            //VMC tries to transfer the balance to the payment media
            //TODO
            return 0;
        }


        @Override
        public int limitRevalue(RevalueAmount vendParameters) {
            Log.d(TAG,"into callback limitRevalue======>");
            //Negotiate the maximum revalue amount.
            vendParameters.setAmount("0".getBytes());
            return 0;
        }

        @Override
        public void requestID(RequestIDParameters parameters) {
            Log.d(TAG,"into callback requestID======>");
            String MFC, MN, SN;
            try {
                MFC = new String(parameters.getManufactureCode(), "ascii");
                MN = new String(parameters.getModelNum(), "ascii");
                SN = new String(parameters.getSerialNum(), "ascii");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            clearMessage();
            showMessage("VMC ManufactureCode:" + MFC);
            showMessage("VMC ModelNum:" + MN);
            showMessage("VMC SerialNum:" + SN);
            showMessage("VMC AppVersion:" + ISOUtils.hexString(parameters.getVmcAppVersion()));
            //TODO
        }

        @Override
        public void enableOptionalFeature(FeatureBitmap featureBitmap) {
            Log.d(TAG,"into callback enableOptionalFeature======>");
            showMessage("CMD:Enable Options");
            //TODO

        }
    };
    public void doPinInput(boolean isOnline, String cardNum, PINEntity pinEntity) {
        showMessage("doPinInput isOnline:" + isOnline + ";cardNum:" + cardNum, MessageTag.DATA);
        if (isOnline) {
            ((MainActivity) context).startOnlinePinInput(cardNum, AppConfig.isExternalEmv,false);
        } else {
            ((MainActivity) context).startOfflinePinInput(pinEntity.getModulus(), pinEntity.getExponent(), AppConfig.isExternalEmv);
        }
    }
}
