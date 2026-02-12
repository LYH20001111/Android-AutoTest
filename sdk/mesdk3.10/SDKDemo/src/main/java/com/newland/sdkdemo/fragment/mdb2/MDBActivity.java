package com.newland.sdkdemo.fragment.mdb2;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import com.newland.nsdk.unattended.mdb.BeginSessionParameters;
import com.newland.nsdk.unattended.mdb.CashSaleParameters;
import com.newland.nsdk.unattended.mdb.CashlessState;
import com.newland.nsdk.unattended.mdb.ConfigData;
import com.newland.nsdk.unattended.mdb.FTLParameters;
import com.newland.nsdk.unattended.mdb.MDBCallback;
import com.newland.nsdk.unattended.mdb.MDBModule;
import com.newland.nsdk.unattended.mdb.MDBModuleImpl;
import com.newland.nsdk.unattended.mdb.NSDKMDBException;
import com.newland.nsdk.unattended.mdb.NegativeVendResponse;
import com.newland.nsdk.unattended.mdb.RevalueLimitResponse;
import com.newland.nsdk.unattended.mdb.RevalueState;
import com.newland.nsdk.unattended.mdb.SelectionParameters;
import com.newland.nsdk.unattended.mdb.VMCTimeParameters;
import com.newland.nsdk.unattended.mdb.VendParameters;
import com.newland.nsdk.unattended.mdb.VendState;
import com.newland.nsdk.unattended.mdb.VendSuccessParameters;
import com.newland.nsdk.unattended.mdb.VmcDataBean;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.databinding.ActivityMdbBinding;
import com.newland.sdkdemo.showutil.ShowInfoUtil;
import com.newland.sdkdemo.utils.MessageTag;

public class MDBActivity extends AppCompatActivity {
    private ActivityMdbBinding binding;
    private static final int MESSAGE_SHOW = 100;
    private static final int MESSAGE_CLEAR = 101;
    private MDBModule mdbModule;
    private int optionFeature;
    private CashlessState cashlessState = MDBModuleImpl.getInstance().getState();
    private VmcDataBean vmcDataBean = MDBModuleImpl.getInstance().getVmcDataBean();
    private MDBCardReader rfidCard;
    private ShowInfoUtil messageUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMdbBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initViews();
    }

    private void initViews() {
        messageUtils = new ShowInfoUtil(this);
        messageUtils.setShowMessageView(binding.tvBase);
        mdbModule = MDBModuleImpl.getInstance();
        rfidCard = new MDBCardReader(this,mHandler);
        binding.btnClearMessage.setOnClickListener(l-> {
            Message m = new Message();
            m.what = MESSAGE_CLEAR;
            mHandler.sendMessage(m);
        });
        binding.btnInitModule.setOnClickListener(l-> initModule());
        binding.btnBeginSession.setOnClickListener(l-> beginSession());
        binding.btnDataEntry.setOnClickListener(l-> dataEntry());
        binding.btnCancelSession.setOnClickListener(l-> cancelSession());
        binding.btnFtlCommands.setOnClickListener(l-> ftlCommands());
        binding.btnJustReset.setOnClickListener(l-> justReset());
        binding.btnReportCouponInfo.setOnClickListener(l-> reportCouponInfo());
        binding.btnRequestSelection.setOnClickListener(l-> requestSelection());
        binding.btnVendDeny.setOnClickListener(l-> vendDeny());
    }

    private void initModule() {
        try {
            mdbModule.init();
            showNormalMessage("Init module success");
            mdbModule.setMDBCallback(callback);
            showNormalMessage("Set callback success");
        } catch (NSDKMDBException e) {
            showErrorMessage(e.getMessage());
        }
    }

    private void justReset() {
        try {
            mdbModule.responseJustReset();
            showNormalMessage("Response just-reset to VMC for the first time handshake process.");
        } catch (NSDKMDBException e) {
            showErrorMessage(e.getMessage());
        }
    }



    private void beginSession() {
        new Thread(()-> {
            if (cashlessState != CashlessState.CASHLESS_STATE_ENABLE) {
                showNormalMessage("Current cashless device state is not enable.");
                return;
            }
            char fundsAvailable=0xffff;
            int paymentMediaID=0xFFFFFFFF;
            byte paymentType=0x00;
            char paymentData=0X0000;
            char userLanguage=0x1840;
            char userCurrencyCode=0x1840;
            byte cardOptions=0x07;
            BeginSessionParameters beginSessionParameters = new BeginSessionParameters();
            if(vmcDataBean.vmcFeatureLevel==0x01){
                beginSessionParameters.setFundsAvailable((byte) fundsAvailable);
            }else if(vmcDataBean.vmcFeatureLevel==0x03&& vmcDataBean.isExpandCurrencyMode){
                int funds=0xffffffff;
                beginSessionParameters.setFundsAvailable((byte) funds);
                beginSessionParameters.setPaymentMediaID(paymentMediaID);
                beginSessionParameters.setPaymentType(paymentType);
                beginSessionParameters.setPaymentData((byte) paymentData);
                beginSessionParameters.setCardOptions(cardOptions);
                beginSessionParameters.setUserLanguage(userLanguage);
                beginSessionParameters.setUserCurrencyCode(userCurrencyCode);
            }else{
                beginSessionParameters.setFundsAvailable((byte) fundsAvailable);
                beginSessionParameters.setPaymentMediaID(paymentMediaID);
                beginSessionParameters.setPaymentType(paymentType);
                beginSessionParameters.setPaymentData((byte) paymentData);
            }

            try {
                mdbModule.responseBeginSession(beginSessionParameters);
                showNormalMessage("Response begin session to VMC.");
            } catch (NSDKMDBException e) {
                showErrorMessage(e.getMessage());
            }
        }).start();

    }

    private void cancelSession() {
        new Thread(()-> {
            try {
                mdbModule.requestSessionCancel();
                showNormalMessage("Request session cancel.");
            } catch (NSDKMDBException e) {
                showErrorMessage(e.getMessage());
            }
        }).start();
    }

    private void vendDeny() {
        new Thread(()-> {
            rfidCard.setVendResult("VendDenied");
        }).start();
    }

    private void requestSelection() {
        new Thread(()-> {
            char foundsAvailable16=0x001E;
            int foundsAvailable32=0x0000001E;
            int paymentMediaID=0xffffffff;
            byte paymentType=0x00;
            char paymentData=0x0000;
            char itemNumber=0x0001;
            int itemOption=0x00000001;
            char userLanguage=0x1840;
            char userCurrencyCode=0x1840;
            byte cardOption=0x00;
            SelectionParameters selectionParameters = new SelectionParameters();
            selectionParameters.setCardOption(cardOption);
            selectionParameters.setItemNumber(itemNumber);
            selectionParameters.setItemOptions(itemOption);
            selectionParameters.setFundsAvailable(foundsAvailable32);
            selectionParameters.setPaymentData(paymentData);
            selectionParameters.setPaymentType(paymentType);
            selectionParameters.setPaymentMediaID(paymentMediaID);
            selectionParameters.setUserLanguage(userLanguage);
            selectionParameters.setUserCurrencyCode(userCurrencyCode);
            try {
                mdbModule.requestSelection(selectionParameters);
                showNormalMessage("Request selection.");
            } catch (NSDKMDBException e) {
                showErrorMessage(e.getMessage());
            }
        }).start();
    }

    private void dataEntry() {
        new Thread(()-> {
            try {
                mdbModule.requestDataEntry(false, 0x06);
                mdbModule.requestDisplayVMC(0x1E, "password");
            } catch (NSDKMDBException e) {
                showErrorMessage(e.getMessage());
            }
        }).start();
    }

    private void reportCouponInfo() {
        new Thread(()-> {
            try {
                mdbModule.reportCouponInfo((char) 0x0001, (byte) 0x01, 0xFA0, 0x01);
            } catch (NSDKMDBException e) {
                showErrorMessage(e.getMessage());
            }
        }).start();
    }

    private void ftlCommands() {
            AlertDialog.Builder ftlDialogBuilder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_ftl_operations, null);
            ftlDialogBuilder.setTitle("FTL Commands")
                    .setView(view)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Confirm", (dialogInterface, i) -> {
                    new Thread(()-> {
                        FTLParameters ftlParameters = new FTLParameters();
                        ftlParameters.setDestAddress((byte) 0x00);
                        ftlParameters.setSrcAddress((byte) 0x10);
                        ftlParameters.setFileID((byte) 0x01);
                        ftlParameters.setMaxLength((byte) 0x01);
                        ftlParameters.setControl((byte) 0x01);
                        RadioButton rbRequestReceiveFTL = view.findViewById(R.id.rb_requestReceiveFtl);
                        RadioButton rbRequestSendFTL = view.findViewById(R.id.rb_requestSendFtl);
                        RadioButton rbResponseOKToSend = view.findViewById(R.id.rb_responseOkToSend);
                        RadioButton rbResponseFTLRetryDeny = view.findViewById(R.id.rb_responseFtlRetryDeny);
                        RadioButton rbSendFTLBlockData = view.findViewById(R.id.rb_sendFtlBlockData);
                        if (rbRequestReceiveFTL.isChecked()) {
                            try {
                                mdbModule.responseRequestReceiveFTL(ftlParameters);
                                showTipMessage("Request Receive FTL success.");
                            } catch (NSDKMDBException e) {
                                showErrorMessage(e.getMessage());
                            }
                        } else if (rbRequestSendFTL.isChecked()) {
                            try {
                                mdbModule.responseRequestSendFTL(ftlParameters);
                                showTipMessage("Request send FTL success.");
                            } catch (NSDKMDBException e) {
                                showErrorMessage(e.getMessage());
                            }
                        } else if (rbResponseOKToSend.isChecked()) {
                            try {
                                mdbModule.responseFTLOKToSend((byte) 0x00, (byte) 0x10);
                                showTipMessage("Response FTL OK to Send.");
                            } catch (NSDKMDBException e) {
                                showErrorMessage(e.getMessage());
                            }
                        } else if (rbResponseFTLRetryDeny.isChecked()) {
                            try {
                                mdbModule.responseFTLRetryOrDeny((byte) 0x00, (byte) 0x10, (byte) 0x00);
                                showTipMessage("Response FTL retry deny");
                            } catch (NSDKMDBException e) {
                                showErrorMessage(e.getMessage());
                            }
                        } else if (rbSendFTLBlockData.isChecked()) {
                            try {
                                mdbModule.sendFTLBlockData((byte) 0x00, (byte) 0x10, ISOUtils.hex2byte("000102030405060708090A0B0C0D0E0F"));
                                showTipMessage("Send FTL Block Data success.");
                            } catch (NSDKMDBException e) {
                                showErrorMessage(e.getMessage());
                            }
                        }
        }).start();
                }).create().show();
    }

    private MDBCallback callback = new MDBCallback() {
        @Override
        public void onReset() throws NSDKMDBException {
            showNormalMessage("onReset");
        }

        @Override
        public void onSetUpConfigData(ConfigData configData) throws NSDKMDBException {
            showNormalMessage("VMC feature level: " + configData.getVmcFeatureLevel());
            showNormalMessage("Display columns: " + configData.getDisplayColumns());
            showNormalMessage("Display rows: " + configData.getDisplayRows());
            showNormalMessage("Display info: " + configData.getDisplayInfo());
        }

        @Override
        public void onSetUpMaxMinPrice(int maxPrice, int minPrice, char currencyCode) throws NSDKMDBException {
            showNormalMessage("Max price: " + maxPrice);
            showNormalMessage("Min price: " + minPrice);
            showNormalMessage("CurrencyCode: " + currencyCode);
        }

        @Override
        public void onReaderEnabled() {
            showNormalMessage("OnReaderEnabled.");
            rfidCard.active("Enable");
        }

        @Override
        public void onReaderDisabled() {
            showNormalMessage("OnReaderDisabled.");
        }

        @Override
        public void onReaderCancel() {
            showNormalMessage("OnReaderCancel.");
        }

        @Override
        public void onDataEntryResponse(byte[] dataEntry) {
            try {
                byte data=dataEntry[0];
                if(new String(dataEntry).equals("1")){//for data entry example#2
                    mdbModule.cancelDataEntry();
                }else{
                    mdbModule.requestDisplayVMC((byte) 0x1E, "*");
                }
            } catch (NSDKMDBException e) {
                showErrorMessage(e.getMessage());
            }
        }

        @Override
        public RevalueState onRevalueRequest(int amount) {
            showNormalMessage("OnRevalueRequest.");
            RevalueState revalueState = RevalueState.UNKNOWN;
            showNormalMessage("Amount: " + amount);
            if (amount == 0x0a) {
                revalueState = RevalueState.APPROVED;
            } else {
                revalueState = RevalueState.DENIED;
            }
            return revalueState;
        }

        @Override
        public RevalueLimitResponse onRevalueLimitRequest() {
            showNormalMessage("OnRevalueLimitRequest");
            RevalueLimitResponse revalueLimitResponse = new RevalueLimitResponse();
            if ((optionFeature & 0x06) != 0) {
                revalueLimitResponse.setRevalueState(RevalueState.LIMIT_AMOUNT);
                revalueLimitResponse.setLimitAmount(ISOUtils.hex2byte("000AE56"));
            } else {
                revalueLimitResponse.setRevalueState(RevalueState.DENIED);
            }
            return revalueLimitResponse;
        }

        @Override
        public VendState onVendRequest(VendParameters vendParameters) {
            showNormalMessage("OnVendRequest");
            VendState vendState = VendState.UNKNOWN;
            char itemPrice = (char) vendParameters.getItemPrice();
            if (itemPrice == 20) {
                byte[] sendData = new byte[]{
                        (byte)((0Xffffffff >> 8)&0XFF),
                        (byte) (0Xffffffff & 0XFF),
                        (byte) ((0Xffffffff >> 24)&0XFF),
                        (byte) ((0Xffffffff >> 16)&0XFF),
                        (byte) ((0Xffffffff >> 8)&0XFF),
                        (byte) (0Xffffffff & 0xFF),
                        0x00,
                        (byte) (0x0000 >> 8),
                        (byte) (0x0000 & 0xFF),
                };
                vendParameters.setData(sendData);
                return VendState.BEGIN_SESSION;
            }
            rfidCard.active("Vend");
            int count=30;
            while(rfidCard.isVendFinish()==false&&count!=0){
                try {
                    rfidCard.showMessage(String.format("Level03 CurrencyMode:Enabled\nitemPrice:$%.2f\n%d s ", (float) itemPrice/ 20.0,count), MessageTag.ERROR);
                    Thread.sleep(1000);
                    count-=1;
                } catch (InterruptedException e) { // on vendrequest maybe will cancel by VMC on VEND CANCEL,this kind of case maybe need refound
                    return VendState.DENIED;
                }

            }

            if(rfidCard.getVendResult().equals("VendDenied")||count==0){
                vendState = VendState.DENIED;
            }else {
                vendState = VendState.APPROVED;
                boolean isExpandedCurrencyMode = vmcDataBean.isExpandCurrencyMode;
                boolean isAllowBasketMode = vmcDataBean.isAllowBasketMode;
                showNormalMessage("isExpandedCurrencyMode: " + isExpandedCurrencyMode + ", isAllowBasketMode: " + isAllowBasketMode);
                if (!isAllowBasketMode && !isExpandedCurrencyMode) {
                    vendParameters.setData(new byte[]{(byte) (itemPrice >> 8), (byte) (itemPrice & 0XFF)});
                } else if (isAllowBasketMode && !isExpandedCurrencyMode) {
                    vendParameters.setData(new byte[]{(byte) (itemPrice >> 8), (byte) (itemPrice & 0XFF), 0x00, 0x00});
                } else if (!isAllowBasketMode && isExpandedCurrencyMode) {
                    vendParameters.setData(new byte[]{(byte) (itemPrice >> 24), (byte) ((itemPrice >> 16) & 0XFF), (byte) ((itemPrice >> 8) & 0XFF), (byte) (itemPrice & 0XFF)});
                } else {
                    vendParameters.setData(new byte[]{(byte) (itemPrice >> 24), (byte) ((itemPrice >> 16) & 0XFF), (byte) ((itemPrice >> 8) & 0XFF), (byte) (itemPrice & 0XFF), 0x00, 0x00, 0x00, 0x00});
                }
            }
            showNormalMessage("vendState: " + vendState.name());
            return vendState;
        }

        @Override
        public void onVendCancel() {
            showNormalMessage("onVendCancel");
        }

        @Override
        public void onVendSuccess(VendSuccessParameters vendSuccessParameters) {
            showNormalMessage("onVendSuccess");
            showNormalMessage("Item number: " + vendSuccessParameters.getItemNumber());
            if (vmcDataBean.isAllowEnhancedItem) {
                showNormalMessage("Selected item number: " + vendSuccessParameters.getItemNumberSelected());
                showNormalMessage("Dispensed item number: " + vendSuccessParameters.getItemNumberDispensed());
                showNormalMessage("Selected PA101 item: " + new String(vendSuccessParameters.getPa101ItemSelected()));
                showNormalMessage("Dispensed PA101 item: " + new String(vendSuccessParameters.getPa101ItemDispensed()));
            }
            if (vmcDataBean.isAllowBasketMode) {
                showNormalMessage("Vend amount: " + vendSuccessParameters.getVendAmount());
                showNormalMessage("Item count: " + vendSuccessParameters.getItemCount());
                showNormalMessage("Option amount: " + vendSuccessParameters.getOptionsAmount());
            }
        }

        @Override
        public byte onVendFailure(char itemNumber, int vendAmount, byte itemCount, int optionAmount, byte reason) {
            showNormalMessage("onVendFailure");
            showNormalMessage("Reason: " + reason);
            return 0x0A;
        }

        @Override
        public NegativeVendResponse onNegativeVendRequest(int itemValue, char itemNumber) {
            showNormalMessage("onNegativeVendRequest");
            rfidCard.showMessage(String.format("Level03 Currency Mode:Disabled\nNegative itemValue:$%.2f ", (float)  itemValue/20.0),MessageTag.ERROR);
            rfidCard.active("Refound");
            NegativeVendResponse negativeVendResponse = new NegativeVendResponse();
            while(rfidCard.isVendFinish()==false){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) { // on vendrequest maybe will cancel by VMC on VEND CANCEL,this kind of case maybe need refound
                    showNormalMessage("onNegativeVendRequest: InterruptedException");
                    return negativeVendResponse;
                }
            }
            rfidCard.deactive();
            //TODO get vend result timeout set vend denied
            if(rfidCard.getVendResult().equals("VendDenied")){
                negativeVendResponse.setVendState(VendState.DENIED);
            }else{
                negativeVendResponse.setVendState(VendState.APPROVED);
                if (vmcDataBean.isExpandCurrencyMode) {
                    negativeVendResponse.setData(new byte[]{(byte) (itemValue >> 24), (byte) ((itemValue >> 16) & 0XFF), (byte) ((itemValue >> 8) & 0XFF), (byte) (itemValue & 0XFF)});
                } else {
                    negativeVendResponse.setData(new byte[]{(byte) ((itemValue >> 8) & 0XFF), (byte) (itemValue & 0XFF)});
                }
            }
            return negativeVendResponse;
        }

        @Override
        public void onCashSale(CashSaleParameters cashSaleParameters) {
            showNormalMessage("onCashSale");
        }

        @Override
        public void onSessionCompleted() {
            showNormalMessage("onSessionCompleted");
            rfidCard.active("Enable");//restart for next
        }

        @Override
        public void onSelectionDenied(char itemNumber, byte reason) {
            showNormalMessage("onSelectionDenied");
            showNormalMessage("Reason: " + reason);
        }

        @Override
        public void onCouponReply(byte usage, char itemNumber, int usedValue) {
            showNormalMessage("onCouponReply");
            showNormalMessage("Usage: " + usage);
            showNormalMessage("itemNumber: " + itemNumber);
            showNormalMessage("usedValue: " + usedValue);
        }

        @Override
        public void onFTLRequestToRcv(byte srcAddress, byte fileID, int maxLength, byte control) {
            showNormalMessage("onFRLRequestToRcv");
            showNormalMessage(String.format("sourceAddr:0x%x ", (int)srcAddress));
            showNormalMessage(String.format("fileID:0x%x ", (int) fileID));
            showNormalMessage(String.format("MaxLength:0x%x ",   (int)maxLength));
            showNormalMessage(String.format("control:0x%x ",   (int)control));
            byte[] writeUserData1= new byte[]{0x4e,0x65,0x6C,0x61,0x6E,0x64,0x20,0x55,0x6E,0x61,0x74,0x65,0x6E,0x65,0x64,0x20,0x50,0x61,0x79,0x6D,0x65,0x6E,0x74,0x20};
            try {
                mdbModule.sendFTLBlockData((byte) 0x00, (byte) 0x01, writeUserData1);
            } catch (NSDKMDBException e) {
                showErrorMessage(e.getMessage());
            }
        }

        @Override
        public void onFTLRequestToSend(byte srcAddress, byte fileID, int maxLength, byte control) {
            showNormalMessage("onFTLRequestToSend");
            showNormalMessage(String.format("sourceAddr:0x%x ", (int)srcAddress));
            showNormalMessage(String.format("fileID:0x%x ", (int) fileID));
            showNormalMessage(String.format("MaxLength:0x%x ",   (int)maxLength));
            showNormalMessage(String.format("control:0x%x ",   (int)control));

        }

        @Override
        public void onFTLRetryOrDeny(byte srcAddress, byte retryOrDeny) {
            showNormalMessage("onFTLRetryOrDeny");
            showNormalMessage(String.format("sourceAddr:0x%x ", (int)srcAddress));
            showNormalMessage(String.format("retryDelay:0x%x ", (int) retryOrDeny));
        }

        @Override
        public void onFTLOkToSend(byte srcAddress) {
            showNormalMessage("onFTLOkToSend");
            showNormalMessage("srcAddress: " + srcAddress);
            try {
                mdbModule.responseFTLOKToSend((byte) 0x00, srcAddress);
            } catch (NSDKMDBException e) {
                showErrorMessage(e.getMessage());
            }
        }

        @Override
        public void onFTLSendBlock(byte blockNo, byte[] blockData) {
            showNormalMessage("onFTLSendBlock");
            showNormalMessage(String.format("blockNo:0x%x ", (int)blockNo));
        }

        @Override
        public void onExpansionRequestID(byte[] manufaturerCode, byte[] serialNumber, byte[] modelNumber, byte softwareVersion) {
            showNormalMessage("onExpansionRequestID");
            showNormalMessage("Manufacturer Code: " + new String(manufaturerCode));
            showNormalMessage("serialNumber: " + new String(serialNumber));
            showNormalMessage("modelNumber: " + new String(modelNumber));
            showNormalMessage("softwareVersion: " + softwareVersion);
        }

        @Override
        public byte[] onExpansionReadUserFile(byte userFileNumber) {
            if(userFileNumber == 0x01){
                return new byte[]{0x4E,0x65,0x77,0x6C,0x61,0x6E,0x64,0x20};
            }else{
                return new byte[]{0x55,0x6E,0x61,0x74,0x74,0x65,0x6E,0x65,0X64,0X20};
            }
        }

        @Override
        public void onExpansionWriteUserFile(byte userFileNumber, int userFileLength, byte[] userData) {
            showNormalMessage("onExpansionWriteUserFile");
            showNormalMessage(String.format("numberOfUserFile:0x%02x ",  (int)userFileNumber));
            showNormalMessage(String.format("lengthOfUserFile:0x%02x ",  (int)userFileLength));
            showNormalMessage(String.format("userData hex: %s", new String(userData)));

        }

        @Override
        public void onExpansionWriteTimeDate(VMCTimeParameters vmcTimeParameters) {
            showNormalMessage("onExpansionWriteTimeDate");
            showNormalMessage("Years: " + vmcTimeParameters.getYears());
            showNormalMessage("Months: " + vmcTimeParameters.getMonths());
            showNormalMessage("Days: " + vmcTimeParameters.getDays());
            showNormalMessage("Hours: " + vmcTimeParameters.getHours());
            showNormalMessage("Minutes: " + vmcTimeParameters.getMinutes());
            showNormalMessage("Seconds: " + vmcTimeParameters.getSeconds());
            showNormalMessage("DayOfWeeks: " + vmcTimeParameters.getDayOfWeeks());
            showNormalMessage("WeekNumber: " + vmcTimeParameters.getWeekNumber());
            showNormalMessage("isSummerTime: " + vmcTimeParameters.isSummerTime());
            showNormalMessage("isHoliday: " + vmcTimeParameters.isHoliday());
        }

        @Override
        public void onExpansionEnableOptions(int optionFeatureEnabled) {
            showNormalMessage("onExpansionEnableOptions");
            optionFeature = optionFeatureEnabled;
        }

        @Override
        public byte[] onExpansionDiagnostics(byte[] userData) {
            showNormalMessage("onExpansionDiagnostics");
            return userData;
        }
    };
    
    
    
    
    
    
    
    
    
    
    
    

    private void showMessage(String message, int type) {
        Message m = new Message();
        m.what = MESSAGE_SHOW;
        m.obj = message;
        m.arg1 = type;
        mHandler.sendMessage(m);
    }

    private void showNormalMessage(String message) {
        showMessage(message, MessageTag.NORMAL);
    }

    private void showTipMessage(String message) {
        showMessage(message, MessageTag.TIP);
    }

    private void showErrorMessage(String message) {
        showMessage(message, MessageTag.ERROR);
    }

    private Handler mHandler = new Handler(Looper.getMainLooper(), message -> {
        switch (message.what) {
            case MESSAGE_SHOW:
                com.newland.sdkdemo.showutil.MessageTag messageTag = com.newland.sdkdemo.showutil.MessageTag.NORMAL;
                switch (message.arg1) {
                    case MessageTag.NORMAL:
                        messageTag = com.newland.sdkdemo.showutil.MessageTag.NORMAL;
                        break;
                    case MessageTag.ERROR:
                        messageTag = com.newland.sdkdemo.showutil.MessageTag.ERROR;
                        break;
                    case MessageTag.TIP:
                        messageTag = com.newland.sdkdemo.showutil.MessageTag.TIP;
                        break;
                    case MessageTag.DATA:
                        messageTag = com.newland.sdkdemo.showutil.MessageTag.DATA;
                        break;
                }
                messageUtils.showMessage(message.obj.toString(),messageTag);
                break;
            case MESSAGE_CLEAR:
                messageUtils.cleanMessage();
                break;
        }
        return true;
    });

}