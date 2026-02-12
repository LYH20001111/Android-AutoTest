package com.newland.sdkdemo.fragment;

import android.content.Context;
import android.newland.os.NdkApi;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.newland.ndk.NdkApiManager;
import com.newland.sdk.me.module.emvl3.impl.EmvL3Constant;
import com.newland.sdk.me.module.emvl3.impl.MENEmvL3Decorator;
import com.newland.sdk.module.cardreader.CardReaderExtParams;
import com.newland.sdk.module.cardreader.SearchCardRule;
import com.newland.sdk.module.emv.CAPK;
import com.newland.sdk.module.emv.CardInterface;
import com.newland.sdk.module.emv.EMVTransController;
import com.newland.sdk.module.emv.EMVUtils;
import com.newland.sdk.module.emv.EmvExtParams;
import com.newland.sdk.module.emv.TransactionExtParams;
import com.newland.sdk.module.emv.TransactionType;
import com.newland.sdk.module.externalPin.ExtPinpadModule;
import com.newland.sdk.module.externalPin.PinpadInitExtParams;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.module.serialport.PortType;
import com.newland.sdk.module.swiper.MSDAlgorithmType;
import com.newland.sdk.module.swiper.MagStripeCardModule;
import com.newland.sdk.module.swiper.SwipExtParams;
import com.newland.sdk.module.swiper.SwipResult;
import com.newland.sdk.module.swiper.SwipResultCode;
import com.newland.sdk.module.swiper.SwiperReadModel;
import com.newland.sdk.module.cardreader.CardReaderListener;
import com.newland.sdk.module.cardreader.CardReaderModule;
import com.newland.sdk.module.cardreader.CardType;
import com.newland.sdk.module.cardreader.RFCardInfo;
import com.newland.sdk.module.emv.AID;
import com.newland.sdk.module.emv.AIDEntity;
import com.newland.sdk.module.emv.ECTransLog;
import com.newland.sdk.module.emv.ECTransLogListener;
import com.newland.sdk.module.emv.EMVCardInfo;
import com.newland.sdk.module.emv.EMVModule;
import com.newland.sdk.module.emv.EMVTransLog;
import com.newland.sdk.module.emv.EMVTransLogListener;
import com.newland.sdk.module.rfcard.RFCardType;
import com.newland.sdk.mtype.util.Dump;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdk.utils.TLVPackage;
import com.newland.sdkdemo.AppConfig;
import com.newland.sdkdemo.MainActivity;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;

import com.newland.sdkdemo.event.EMVListener;
import com.newland.sdkdemo.utils.DialogUtils;
import com.newland.sdkdemo.utils.MessageTag;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Author by bxy, Date on 2019/5/11 0011.
 */
public class EMVFragment extends BaseFragment {

    private EMVModule emvModule;
    private CardReaderModule cardReader;
    private EMVTransController controller;
    private  int transType;
    private RadioGroup transTypeGroup;
    private MagStripeCardModule magStripeCardModule;
    private TransactionExtParams transactionExtParams;
    private EMVListener simpleTransferListener;
    private PortType portType = null;
    private ExtPinpadModule externalPinInput;

    public EMVFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_emv_f);
    }

    @Override
    public void initData() {
        emvModule = moduleManage.getEMVModule();
        emvModule.init(context, null);
        try {
            cardReader = moduleManage.getCardReaderModule();
            magStripeCardModule = moduleManage.getMagStripeCardModule();
            externalPinInput = moduleManage.getExtPinpadModule();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Object getModule() {
        return EMVFragment.this;
    }

    private static final int INDEX_TRANS_CHANNEL = 1;
    private static final int INDEX_LOAD_PARAMS = 2;
    private static final int INDEX_OPER_AID = 3;
    private static final int INDEX_OPER_CAPK = 4;
    private static final int INDEX_TRANSCATION = 5;

    @MethodGridEntity(btnnameid = R.string.tv_trans_channel, functionid = INDEX_TRANS_CHANNEL)
    public void transChannel() {
        String[] items = new String[]{"Inner EMV","External Pinpad","BleBase USB1 Port","BleBase USB2 Port","USB"};
        DialogUtils.createSingleChoiceDialog(context, context.getString(R.string.tv_trans_channel), items,new DialogUtils.SingleChoiceDialogCallback(){

            @Override
            public void onResult(int id) {
                if (id == -1) {//cancel
                    return;
                }
                if(id==0){
                    AppConfig.isExternalEmv = false;
                    showMessage("Inner EMV");
                    emvModule = moduleManage.getEMVModule();
                    emvModule.init(context, null);
                }else if(id==1){
                    AppConfig.isExternalEmv = true;
                    showMessage("External EMV");
                    portType = null;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            emvModule = moduleManage.getEMVModule();
                            EmvExtParams emvExtParams = new EmvExtParams(true);
                            emvModule.init(context, emvExtParams);
                        }
                    }).start();
                }else if(id==2){
                    AppConfig.isExternalEmv = true;
                    emvModule = moduleManage.getEMVL3Module();
                    showMessage("External BluetoothBase USB1 port EMV");
                    portType = PortType.BLEBASE_USB1;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            EmvExtParams emvExtParams = new EmvExtParams(true);
                            emvExtParams.setPortType(PortType.BLEBASE_USB1);
                            boolean result = emvModule.init(context, emvExtParams);
                            showMessage("External BluetoothBase USB1 port EMV result="+result);
                            if (result) {
                                ((MENEmvL3Decorator) emvModule).setDebugMode(0); //0 close log, 3 open all log
                            }
                        }
                    }).start();
                }else if(id==3){
                    AppConfig.isExternalEmv = true;
                    emvModule = moduleManage.getEMVL3Module();
                    showMessage("External BluetoothBase USB2 port EMV");
                    portType = PortType.BLEBASE_USB2;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            EmvExtParams emvExtParams = new EmvExtParams(true);
                            emvExtParams.setPortType(PortType.BLEBASE_USB2);
                            boolean result = emvModule.init(context, emvExtParams);
                            showMessage("External BluetoothBase USB2 port EMV result="+result);
                        }
                    }).start();
                }else if(id==4){
                    AppConfig.isExternalEmv = true;
                    emvModule = moduleManage.getEMVL3Module();
                    showMessage("External usb EMV");
                    portType = PortType.USB;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            EmvExtParams emvExtParams = new EmvExtParams(true);
                            emvExtParams.setPortType(PortType.USB);
                            boolean result = emvModule.init(context, emvExtParams);

                            showMessage("External usb EMV result=" + result);
                            if (result) {
                                ((MENEmvL3Decorator) emvModule).setDebugMode(0); //0 close log, 3 open all log
                            }
                        }
                    }).start();
                }
            }
        });

    }

    /**
     * load emv kernel configurate from xml file
     */
    @MethodGridEntity(btnnameid = R.string.tv_load_config, functionid = INDEX_LOAD_PARAMS)
    public void loadConfigParams() {
        try {
            showMessage("load configurate params from: assets/Newland_L3_configuration.xml", MessageTag.TIP);
            showMessage("configurate params loading", MessageTag.TIP);
            boolean result = emvModule.loadConfigurationFromXML("Newland_L3_configuration.xml");
            showMessage("load configurate result:" + result);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("loadConfigParams exception:" + e, MessageTag.ERROR);
        }

    }

    @MethodGridEntity(btnnameid = R.string.tv_oper_aid, functionid = INDEX_OPER_AID)
    public void operAID() {
        clearMessage();
        String items[] = new String[]{context.getString(R.string.tv_add_aid), context.getString(R.string.tv_get_one_aid), context.getString(R.string.tv_get_all_aid), context.getString(R.string.tv_del_a_aid), context.getString(R.string.tv_clean_all_aid)};
        DialogUtils.createSingleChoiceDialog(context, context.getString(R.string.tv_oper_aid), items, new DialogUtils.SingleChoiceDialogCallback() {
            @Override
            public void onResult(int id) {
                try {
                    switch (id) {
                        case 0:
                            addAid();
                            break;
                        case 1:
                            getOneAid();
                            break;
                        case 2:
                            getAllAid();
                            break;
                        case 3:
                            delAid();
                            break;
                        case 4:
                            cleanAllAid();
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_oper_capk, functionid = INDEX_OPER_CAPK)
    public void operCAPK() {
        clearMessage();
        String items[] = new String[]{context.getString(R.string.tv_add_pk), context.getString(R.string.tv_get_one_pk), context.getString(R.string.tv_get_all_pk), context.getString(R.string.tv_del_a_pk), context.getString(R.string.tv_clean_all_pk)};
        DialogUtils.createSingleChoiceDialog(context, context.getString(R.string.tv_oper_capk), items, new DialogUtils.SingleChoiceDialogCallback() {
            @Override
            public void onResult(int id) {
                try {
                    switch (id) {
                        case 0:
                            addCapk();
                            break;
                        case 1:
                            getSpecifiedCapk();
                            break;
                        case 2:
                            getAllCapk();
                            break;
                        case 3:
                            delCapk();
                            break;
                        case 4:
                            cleanAllCapk();
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * add aid
     */
    public void addAid() {
        try {
            boolean addResult = false;
            String aid1 = "9F0608A000000333010101DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000100000DF1906000000100000DF2006000000100000DF2106000000100000DF7D01011F81010101";
            addResult = emvModule.addAID(ISOUtils.hex2byte(aid1), CardInterface.CONTACT);
            showMessage("Add AID[A000000333010101] CONTACT:" + (addResult == true ? context.getString(R.string.msg_common_succ) : context.getString(R.string.msg_common_failed)), MessageTag.DATA);
            addResult = emvModule.addAID(ISOUtils.hex2byte(aid1), CardInterface.CONTACTLESS);
            showMessage("Add AID[A000000333010101] CONTACTLESS:" + (addResult == true ? context.getString(R.string.msg_common_succ) : context.getString(R.string.msg_common_failed)), MessageTag.DATA);


            String aid2 = "9F0608A000000333010102DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000100000DF1906000000100000DF2006000000100000DF2106000000100000DF7D01011F81010101";
            addResult = emvModule.addAID(ISOUtils.hex2byte(aid2), CardInterface.CONTACT);
            showMessage("Add AID[A000000333010102]  CONTACT:" + (addResult == true ? context.getString(R.string.msg_common_succ) : context.getString(R.string.msg_common_failed)), MessageTag.DATA);
            addResult = emvModule.addAID(ISOUtils.hex2byte(aid2), CardInterface.CONTACTLESS);
            showMessage("Add AID[A000000333010102] CONTACTLESS:" + (addResult == true ? context.getString(R.string.msg_common_succ) : context.getString(R.string.msg_common_failed)), MessageTag.DATA);


            String aid3 = "9F0608A000000333010103DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000100000DF1906000000100000DF2006000000100000DF2106000000100000";
            addResult = emvModule.addAID(ISOUtils.hex2byte(aid3), CardInterface.CONTACT);
            showMessage("Add AID[A000000333010103] CONTACT:" + (addResult == true ? context.getString(R.string.msg_common_succ) : context.getString(R.string.msg_common_failed)), MessageTag.DATA);
            addResult = emvModule.addAID(ISOUtils.hex2byte(aid3), CardInterface.CONTACTLESS);
            showMessage("Add AID[A000000333010103] CONTACTLESS:" + (addResult == true ? context.getString(R.string.msg_common_succ) : context.getString(R.string.msg_common_failed)), MessageTag.DATA);

            String aid4 = "9F0608A000000333010106DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000100000DF1906000000100000DF2006000000100000DF2106000000100000";
            addResult = emvModule.addAID(ISOUtils.hex2byte(aid4), CardInterface.CONTACT);
            showMessage("Add AID[A000000333010106] CONTACT:" + (addResult == true ? context.getString(R.string.msg_common_succ) : context.getString(R.string.msg_common_failed)), MessageTag.DATA);
            addResult = emvModule.addAID(ISOUtils.hex2byte(aid4), CardInterface.CONTACTLESS);
            showMessage("Add AID[A000000333010106] CONTACTLESS:" + (addResult == true ? context.getString(R.string.msg_common_succ) : context.getString(R.string.msg_common_failed)), MessageTag.DATA);

            String aid5 = "9F0607A0000000031010DF0101019F0902008CDF11050000000000DF12050000000000DF130500000000009F1B0400002710DF150400001388DF160100DF170100DF140B9F37049F47018F019F3201DF1801009F7B06000000000000DF1906000000000000DF2006000000000000DF250102DF3F8201200531026826200000000000000000000000AC9999999999990000000000000000000050010831026826120000030000000000000000AC9999999999990000000000000000000050010531026826120000000000000000000000AC9999999999990000000000000000000050010531026826000000000000000000000000AC999999999999000000000000000000005001160000000000000000000000000000000000000000000000000000000000000000000000160000000000000000000000000000000000000000000000000000000000000000000000160000000000000000000000000000000000000000000000000000000000000000000000160000000000000000000000000000000000000000000000000000000000000000000000";
            addResult = emvModule.addAID(ISOUtils.hex2byte(aid5), CardInterface.CONTACT);
            showMessage("Add AID[A0000000031010] CONTACT:" + (addResult == true ? context.getString(R.string.msg_common_succ) : context.getString(R.string.msg_common_failed)), MessageTag.DATA);
            addResult = emvModule.addAID(ISOUtils.hex2byte(aid5), CardInterface.CONTACTLESS);
            showMessage("Add AID[A0000000031010] CONTACTLESS:" + (addResult == true ? context.getString(R.string.msg_common_succ) : context.getString(R.string.msg_common_failed)), MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_add_aid_e) + e, MessageTag.ERROR);
        }
    }

    /**
     * del one aid
     */
    private void delAid() {
        try {
            showMessage(context.getString(R.string.msg_start_del_aid) + "A000000333010101", MessageTag.TIP);
            boolean delAID = false;
            byte[] aid = new byte[]{(byte) 0xA0, 0x00, 0x00, 0x03, 0x33, 0x01, 0x01, 0x01};
            delAID = emvModule.deleteAID(aid, CardInterface.CONTACT);
            if (delAID) {
                showMessage(context.getString(R.string.msg_del_aid) + context.getString(R.string.msg_common_succ), MessageTag.DATA);
            } else {
                showMessage(context.getString(R.string.msg_del_aid) + context.getString(R.string.msg_common_failed), MessageTag.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_del_aid_ex) + e.getMessage(), MessageTag.ERROR);
        }
    }

    /**
     * get one aid
     */
    private void getOneAid() {
        try {
            byte[] aidData = ISOUtils.hex2byte("A000000333010101");
            List<AID> aidConfigList = emvModule.getAID(aidData, CardInterface.CONTACT);

            if (aidConfigList != null && aidConfigList.size() > 0) {
                AID aidConfig = aidConfigList.get(0);
                byte[] aid = aidConfig.getAid();
                aidConfig.getTransactionLimit();
                showMessage(context.getString(R.string.msg_get_aid) + hexString(aid), MessageTag.DATA);
                showMessage(context.getString(R.string.msg_get_aid1) + hexString(aidConfig.getEcTransactionLimit()), MessageTag.DATA);
                showMessage(context.getString(R.string.msg_get_aid2) + hexString(aidConfig.getOfflineFloorLimit()), MessageTag.DATA);
                showMessage(context.getString(R.string.msg_get_aid3) + hexString(aidConfig.getCvmLimit()), MessageTag.DATA);
            } else {
                showMessage(context.getString(R.string.msg_get_one_aid_failed), MessageTag.ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_get_all_aid_list_exception) + e.getMessage(), MessageTag.ERROR);
        }

    }

    /**
     * get all aids
     */
    private void getAllAid() {
        try {
            showMessage(context.getString(R.string.msg_get_all_aid_list), MessageTag.TIP);
            List<AID> listAIDConfig = emvModule.getAID(null, CardInterface.CONTACT);

            if (listAIDConfig != null) {
                StringBuilder sb = new StringBuilder();
                for (Iterator i = listAIDConfig.iterator(); i.hasNext(); ) {
                    AID oneAidConfig = (AID) i.next();
                    byte[] aid = oneAidConfig.getAid();
                    oneAidConfig.getTransactionLimit();
                    sb.append(context.getString(R.string.msg_get_aid)).append(hexString(aid)).append("<br>");
                    sb.append(context.getString(R.string.msg_get_aid1)).append(hexString(oneAidConfig.getEcTransactionLimit())).append("<br>");
                    sb.append(context.getString(R.string.msg_get_aid2)).append(hexString(oneAidConfig.getOfflineFloorLimit())).append("<br>");
                    sb.append(context.getString(R.string.msg_get_aid3)).append(hexString(oneAidConfig.getCvmLimit())).append("<br>");
                }
                if (listAIDConfig.size() == 0) {
                    showMessage(context.getString(R.string.msg_no_aid), MessageTag.TIP);
                } else {
                    showMessage(sb.toString(), MessageTag.DATA);
                }
            } else {
                showMessage(context.getString(R.string.msg_get_all_aid_list_failed), MessageTag.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_get_all_aid_list_exception) + e.getMessage(), MessageTag.ERROR);
        }

    }

    /**
     * clear all aid
     */
    private void cleanAllAid() {
        try {
            showMessage(context.getString(R.string.msg_clean_all_aid), MessageTag.TIP);
            boolean clearAllAID = false;
            clearAllAID = emvModule.deleteAID(null, CardInterface.CONTACT);
            showMessage(context.getString(R.string.msg_clean_aid_result) + clearAllAID, MessageTag.DATA);
        } catch (Exception e) {
            showMessage(context.getString(R.string.msg_clean_aid) + context.getString(R.string.common_exception) + e.getMessage(), MessageTag.ERROR);
        }
    }

    public void addCapk() {
        try {
            boolean addResult = false;
            String capk = "9F0605A0000003339F220101DF05083230303931323331DF060101DF070101DF028180BBE9066D2517511D239C7BFA77884144AE20C7372F515147E8CE6537C54C0A6A4D45F8CA4D290870CDA59F1344EF71D17D3F35D92F3F06778D0D511EC2A7DC4FFEADF4FB1253CE37A7B2B5A3741227BEF72524DA7A2B7B1CB426BEE27BC513B0CB11AB99BC1BC61DF5AC6CC4D831D0848788CD74F6D543AD37C5A2B4C5D5A93BDF040103DF0314E881E390675D44C2DD81234DCE29C3F5AB2297A0";
            addResult = emvModule.addCAPublicKey(ISOUtils.hex2byte(capk));
            showMessage("Add public key,[rid:A000000333; index:01]:" + (addResult == true ? context.getString(R.string.msg_common_succ) : context.getString(R.string.msg_common_failed)), MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_add_pk_result) + context.getString(R.string.common_exception) + e.getMessage(), MessageTag.ERROR);
        }
    }

    /**
     * get a specified Capk
     */
    private void getSpecifiedCapk() {
        try {
            byte[] specifiedRid = new byte[]{(byte) 0xA0, 0x00, 0x00, 0x03, 0x33};
            int specifiedIndex = 01;
            CAPK capk = emvModule.getSpecifiedCAPublicKey(specifiedRid, specifiedIndex);
            if (capk != null) {
                int index = capk.getIndex();
                showMessage(context.getString(R.string.msg_get_pk_index) + index, MessageTag.DATA);
                showMessage(context.getString(R.string.msg_get_pk_rid) + hexString(specifiedRid), MessageTag.DATA);
                showMessage("hashAlgorithmIndicator:" + capk.getHashAlgorithmIndicator(), MessageTag.DATA);
                showMessage("publicKeyAlgorithmIndicator:" + capk.getPublicKeyAlgorithmIndicator(), MessageTag.DATA);
                showMessage("modulus:" + (capk.getModulus() == null ? null : hexString(capk.getModulus())), MessageTag.DATA);
                showMessage("exponent:" + (capk.getExponent() == null ? null : hexString(capk.getExponent())), MessageTag.DATA);

            } else {
                showMessage(context.getString(R.string.tv_get_one_pk_failed), MessageTag.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("error:" + e, MessageTag.ERROR);
        }
    }

    private void delCapk() {
        try {
            showMessage(context.getString(R.string.msg_start_del_rids_pk), MessageTag.TIP);
            boolean delPKByRid = false;
            byte[] rid = new byte[]{(byte) 0xA0, 0x00, 0x00, 0x03, 0x33};
            int keyIndex = 01;
            delPKByRid = emvModule.deleteCAPublicKey(rid, keyIndex);
            if (delPKByRid) {
                showMessage(context.getString(R.string.msg_del_rids_pk) + context.getString(R.string.msg_common_succ), MessageTag.DATA);
            } else {
                showMessage(context.getString(R.string.msg_del_rids_pk) + context.getString(R.string.msg_common_failed), MessageTag.ERROR);

            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_del_rids_pk) + context.getString(R.string.common_exception) + e, MessageTag.ERROR);
        }
    }

    private void getAllCapk() {
        try {
            showMessage(context.getString(R.string.msg_get_pk_list), MessageTag.TIP);
            List<CAPK> listCAPublicKey;
            listCAPublicKey = emvModule.getAllCAPublicKey();
            showMessage(context.getString(R.string.msg_get_pk_list) + "length:" + listCAPublicKey.size(), MessageTag.TIP);
            if (listCAPublicKey != null) {
                StringBuilder sb = new StringBuilder();
                for (Iterator i = listCAPublicKey.iterator(); i.hasNext(); ) {
                    CAPK caPublicKey = (CAPK) i.next();
                    int index = caPublicKey.getIndex();
                    byte[] rid = caPublicKey.getRid();
                    String rid1 = Dump.getHexDump(rid);
                    sb.append(context.getString(R.string.msg_get_pk_index)).append(index).append("<br>");
                    sb.append(context.getString(R.string.msg_get_pk_rid)).append(rid1).append("<br>");
                }
                if (listCAPublicKey.size() == 0) {
                    showMessage(context.getString(R.string.msg_no_pk), MessageTag.TIP);
                } else {
                    showMessage(sb.toString(), MessageTag.DATA);
                }
            } else {
                showMessage(context.getString(R.string.msg_get_pk_list_failed), MessageTag.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_get_pk_list_exception) + e.getMessage(), MessageTag.ERROR);
        }
    }

    /**
     * clear all capk
     */
    private void cleanAllCapk() {
        try {
            showMessage(context.getString(R.string.msg_clean_all_pk_start), MessageTag.TIP);
            boolean clearAllCAPublicKey = false;
            clearAllCAPublicKey = emvModule.deleteAllCAPublicKey();
            showMessage(context.getString(R.string.msg_clean_all_pk_result) + clearAllCAPublicKey, MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_clean_all_pk) + context.getString(R.string.common_exception) + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_consume, functionid = INDEX_TRANSCATION)
    private void trans() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_consume), null, R.layout.dialog_trans, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                if (id == -1) {//cancel
                    return;
                }
                transType = TransactionType.STANDARD;
                simpleTransferListener = new EMVListener(context, emvModule, transType);

                transTypeGroup = dialogView.findViewById(R.id.radiogroup_transtype);
                EditText amoutEdit = dialogView.findViewById(R.id.edit_amt_trans);
                DecimalFormat df = new DecimalFormat("#.00");
                if (amoutEdit.getText().toString() == null || amoutEdit.getText().toString().equals("")) {
                    AppConfig.EMV.amt = null;
                    showMessage(context.getString(R.string.msg_trans_money_empty) + "\r\n", MessageTag.NORMAL);
                } else {
                    BigDecimal amt = new BigDecimal(amoutEdit.getText().toString());
                    AppConfig.EMV.amt = amt;
                    showMessage(context.getString(R.string.msg_trans_money_is) + df.format(amt) + "\r\n", MessageTag.NORMAL);
                }

                if (AppConfig.isExternalEmv) {//use for external pinpad.
                    EmvExtParams emvExtParams = null;
                    if(portType!=null && portType== PortType.BLEBASE_USB1){
                        emvExtParams = new EmvExtParams(true);
                        emvExtParams.setPortType(PortType.BLEBASE_USB1);
                    }else if(portType!=null && portType== PortType.BLEBASE_USB2){
                        emvExtParams = new EmvExtParams(true);
                        emvExtParams.setPortType(PortType.BLEBASE_USB2);
                    }else if(portType!=null && portType== PortType.USB){
                        emvExtParams = new EmvExtParams(true);
                        emvExtParams.setPortType(PortType.USB);
                    } else{
                        emvExtParams = new EmvExtParams(true);
                    }
                   // emvExtParams.setEnableEMVDebug(true);
                    Log.i("-----------EMVFragment","----------开启交易");
//                    emvModule.init(context, emvExtParams);
                    transactionExtParams = new TransactionExtParams();
//                    transactionExtParams.setCardInterfaces(0x04);//MSR = 0x01;CONTACT = 0x02;CONTACTLESS = 0x04;
                    if(AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)||AppConfig.DUKPT_AES.equals(AppConfig.KEY_SYS_ALG)){
                        PinpadInitExtParams pinpadInitExtParams = new PinpadInitExtParams(portType,null,null,null);
                        externalPinInput.init(pinpadInitExtParams);
                        try {
                            boolean rs = externalPinInput.ksnIncrease(AppConfig.Pin.DUKPT_DES_INDEX);
                            showMessage("Increase KSN:" + rs);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                    startTrans(transTypeGroup);
                } else {//use for pos device.
                    if (!moduleManage.getDeviceBasicModule().hasSecurityModule()) {
                        showMessage("Unsupport inner EMV. This device hasn't sec module!", MessageTag.ERROR);
                        return;
                    }
//                    if (!Locale.getDefault().getLanguage().equalsIgnoreCase("zh")) {
//                        //emvModule.setIndicatorsAndBeep(true);
//                        MainActivity.getLedOperationRunnable().outStandByMode();
//                    }
                    emvModule.init(context, null);
                    CardReaderExtParams cardReaderExtParams = null;
                    try {
                        showMessage(context.getString(R.string.msg_pl_insert_or_rf) + "\r\n", MessageTag.NORMAL);
                        CardType[] cardTypes = new CardType[]{CardType.MSGCARD, CardType.ICCARD, CardType.RFCARD};
                        cardReader.openCardReader(cardTypes, 30, new CardReaderListener() {
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
                                showMessage(context.getString(R.string.msg_reader_open_failed) + message, MessageTag.NORMAL);
                            }

                            @Override
                            public void onFindMagCard(boolean isSuccessful) {
                                showMessage(context.getString(R.string.msg_cardreader_swiper), MessageTag.TIP);
                                if (isSuccessful) {
                                    SwipResult swipRslt = null;
                                    SwiperReadModel[] readModels = new SwiperReadModel[]{SwiperReadModel.SECOND_TRACK, SwiperReadModel.THIRD_TRACK};
                                    byte[] acctMask = new byte[]{0x000, 0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x00, 0x00, 0x00};
                                    SwipExtParams swipExtParams = new SwipExtParams();
                                    swipExtParams.setAcctMask(acctMask);
                                    swipExtParams.setMSDAlgorithmType(MSDAlgorithmType.UNIONPAY_MODEL);
                                    if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
                                        swipRslt = magStripeCardModule.readEncryptResult(KeyManagement.MKSK, AppConfig.Pin.MKSK_DES_INDEX_WK_TRACK, swipExtParams);

                                    } else if (AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)) {
                                        swipRslt = magStripeCardModule.readEncryptResult(KeyManagement.MKSK, AppConfig.Pin.DUKPT_DES_INDEX, swipExtParams);
                                    }
                                    if (null != swipRslt && swipRslt.getRsltCode() == SwipResultCode.SUCCESS) {
                                        AppConfig.EMV.swipResult = swipRslt;
                                        byte[] secondTrack = swipRslt.getSecondTrackData();
                                        byte[] thirdTrack = swipRslt.getThirdTrackData();
                                        showMessage(context.getString(R.string.common_second_track) + (secondTrack == null ? "null" : Dump.getHexDump(secondTrack)) + "\r\n", MessageTag.DATA);
                                        showMessage(context.getString(R.string.common_third_track) + (thirdTrack == null ? "null" : Dump.getHexDump(thirdTrack)) + "\r\n", MessageTag.DATA);
                                        showMessage(context.getString(R.string.msg_siwper_succ) + "\r\n", MessageTag.TIP);
                                        showMessage(context.getString(R.string.msg_pl_enter_pwd) + "\r\n", MessageTag.TIP);
                                        boolean isExternalPinpad = false;
                                        if ("CPOS X5".equals(Build.MODEL)) {
                                            isExternalPinpad = true;
                                        }
                                        ((MainActivity) context).startOnlinePinInput(swipRslt.getAccount().getAcctNo(), isExternalPinpad,false);
                                    } else {
                                        showMessage(context.getString(R.string.msg_swiper_null_reswiper) + "\r\n", MessageTag.TIP);
                                    }
                                }
                            }

                            @Override
                            public void onFindICCard() {
                                showMessage(context.getString(R.string.msg_cradreader_insert), MessageTag.TIP);
                                startTrans(transTypeGroup);
                            }

                            @Override
                            public void onFindRFCard(@Nullable RFCardType rfCardType, @Nullable RFCardInfo rfCardInfo) {
                                showMessage(context.getString(R.string.msg_cardreader_rfcard), MessageTag.TIP);
                                startTrans(transTypeGroup);
                            }
                        }, cardReaderExtParams);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showMessage(context.getString(R.string.msg_get_pboc_log_ex) + e + context.getString(R.string.msg_check_insert_rf), MessageTag.ERROR);
                    }
                }

            }
        });
    }

    /**
     * start transcation
     *
     * @param transTypeGroup
     */
    private void startTrans(RadioGroup transTypeGroup) {
        try {
            controller = emvModule.getEmvTransController(simpleTransferListener);
            switch (transTypeGroup.getCheckedRadioButtonId()) {
                case R.id.radio_consume:
                    transType = TransactionType.STANDARD;
                    showMessage("start standard EMV flow....");
                    controller.startEMV(transType, AppConfig.EMV.amt, false, transactionExtParams);
                    break;
                case R.id.radio_simple_flow:
                    showMessage("start simple EMV flow....");
                    showMessage("start EMV:" + System.currentTimeMillis());
                    transType = TransactionType.SIMPLE;
                    controller.startEMV(transType, AppConfig.EMV.amt, false, transactionExtParams);
                    break;
                case R.id.radio_acc_info:
                    showMessage(context.getString(R.string.msg_start_get_pboc_log), MessageTag.TIP);
                    getcardInfo();
                    break;
                case R.id.radio_pboclog:
                    showMessage("start fetching PBOC logs....", MessageTag.TIP);
                    fetchPbocLog();
                    break;
                case R.id.radio_eclog:
                    showMessage("start fetching EC logs....", MessageTag.TIP);
                    fetchEcLog();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("exception:" + e, MessageTag.ERROR);
        }

    }


    /**
     * get Account Info
     */
    private void getcardInfo() {
        EMVCardInfo emvCardInfo = emvModule.getCardInformation();
        if (emvCardInfo != null) {
            String cardMo = emvCardInfo.getCardNo();
            String cardSecuenceNumber = emvCardInfo.getCard_sequence_number();
            String cardExpirationData = emvCardInfo.getCardExpirationDate();
            String balance = emvCardInfo.getCardBalance();
            showMessage(context.getString(R.string.msg_get_account_succ), MessageTag.NORMAL);
            showMessage(context.getString(R.string.msg_ecash_balance) + balance, MessageTag.DATA);
            showMessage(context.getString(R.string.msg_card_no) + cardMo, MessageTag.DATA);
            showMessage(context.getString(R.string.msg_card_sn) + cardSecuenceNumber, MessageTag.DATA);
            showMessage(context.getString(R.string.msg_card_vaild_date) + cardExpirationData, MessageTag.DATA);
            showMessage(context.getString(R.string.msg_service_code) + emvCardInfo.getServiceCode(), MessageTag.DATA);
            showMessage(context.getString(R.string.msg_equivalent_track2) + (null == emvCardInfo.getTrack2() ? null : emvCardInfo.getTrack2()), MessageTag.DATA);
        } else {
            showMessage(context.getString(R.string.msg_get_account_null), MessageTag.DATA);
        }

    }


    /**
     * fetch PBOC logs
     */
    private void fetchPbocLog() {
        emvModule.getEMVTransLogs(new EMVTransLogListener() {
            @Override
            public int onRequestSelectApplication(List<AIDEntity> aidEntityList) {

                for (AIDEntity entry : aidEntityList) {
                    showMessage(context.getString(R.string.msg_aid_name) + entry.getName(), MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_aid) + entry.getAid(), MessageTag.DATA);
                }
                // Select the first application by default
                AIDEntity aidEntity = aidEntityList.get(1);
                System.out.println("aid:" + hexString(aidEntity.getAid()) + ",aidName:" + aidEntity.getName());
                return aidEntity.getIndex();
            }

            @Override
            public void onResult(List<EMVTransLog> transLogs) {
                if (transLogs != null && transLogs.size() > 0) {
                    showMessage(context.getString(R.string.msg_get_all_pboc_log) + context.getString(R.string.msg_common_succ), MessageTag.TIP);
                    for (EMVTransLog pbocLog : transLogs) {
                        byte[] otherAmout = pbocLog.getOtherAmount();
                        byte[] merchantName = pbocLog.getMerchantName();
                        byte[] tradeAmount = pbocLog.getTradeAmount();
                        byte[] tradeData = pbocLog.getTradeDate();
                        byte[] tradeTime = pbocLog.getTradeTime();
                        byte[] contryCode = pbocLog.getCountryCode();
                        byte[] currencyCode = pbocLog.getCurrencyCode();
                        byte[] tradeType = pbocLog.getTradeType();
                        byte[] tradeCount = pbocLog.getTransCount();

                        String otherAmountString = Dump.getHexDump(otherAmout);
                        String merchantName1 = Dump.getHexDump(merchantName);
                        String tradeAmount1 = Dump.getHexDump(tradeAmount);
                        String tradeData1 = Dump.getHexDump(tradeData);
                        String tradeTimeString = Dump.getHexDump(tradeTime);
                        String contryCodeString = Dump.getHexDump(contryCode);
                        String currencyCodeString = Dump.getHexDump(currencyCode);
                        String tradeTypeString = Dump.getHexDump(tradeType);
                        String tradeCountString = Dump.getHexDump(tradeCount);
                        showMessage(context.getString(R.string.msg_get_pboc_date) + tradeData1, MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_get_pboc_tradetime) + tradeTimeString, MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_get_pboc_trans_money) + tradeAmount1, MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_get_pboc_otheramount) + otherAmountString, MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_get_pboc_contrycode) + contryCodeString, MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_get_pboc_currencycode) + currencyCodeString, MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_get_pboc_store_name) + merchantName1, MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_get_pboc_tradetype) + tradeTypeString, MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_get_pboc_tradecont) + tradeCountString, MessageTag.DATA);
                    }

                } else {
                    showMessage(context.getString(R.string.msg_get_all_pboc_log) + null, MessageTag.ERROR);
                }
            }
        });
    }


    /**
     * fetch EC logs
     */
    private void fetchEcLog() {
        emvModule.getECTransLogs(new ECTransLogListener() {

            @Override
            public int onRequestSelectApplication(List<AIDEntity> aidEntityList) {
                List<Integer> indexList = new ArrayList<Integer>();
                List<byte[]> aidList = new ArrayList<byte[]>();

                for (AIDEntity entry : aidEntityList) {
                    indexList.add(entry.getIndex());
                    aidList.add(entry.getAid());
                    showMessage(context.getString(R.string.msg_aid_name) + entry.getName(), MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_aid) + entry.getAid(), MessageTag.DATA);
                }
                // Select the first application by default
                return indexList.get(0);
            }

            @Override
            public void onResult(List<ECTransLog> transLogs) {
                if (transLogs != null && transLogs.size() > 0) {
                    showMessage(context.getString(R.string.msg_get_all_load_log) + context.getString(R.string.msg_common_succ), MessageTag.TIP);
                    for (ECTransLog ecLog : transLogs) {
                        byte[] blanceOld = ecLog.getBlanceOld();
                        byte[] merchantName = ecLog.getMerchantName();
                        byte[] blanceNew = ecLog.getBlanceNew();
                        byte[] tradeData = ecLog.getTradeDate();
                        byte[] tradeTime = ecLog.getTradeTime();
                        byte[] contryCode = ecLog.getCountryCode();
                        byte[] tradeCount = ecLog.getTransCount();

                        String blanceOldString = Dump.getHexDump(blanceOld);
                        String merchantName1 = Dump.getHexDump(merchantName);
                        String blanceNew1 = Dump.getHexDump(blanceNew);
                        String tradeData1 = Dump.getHexDump(tradeData);
                        String tradeTimeString = Dump.getHexDump(tradeTime);
                        String contryCodeString = Dump.getHexDump(contryCode);
                        String tradeCountString = Dump.getHexDump(tradeCount);

                        showMessage(context.getString(R.string.msg_get_load_trans_date) + tradeData1, MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_get_load_trans_tradetime) + tradeTimeString, MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_get_load_trans_balance_before) + blanceOldString, MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_get_load_trans_trade_balance_after) + blanceNew1, MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_get_load_trans_contrycode) + contryCodeString, MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_get_load_trans_store_name) + merchantName1, MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_get_load_trans_tradecount) + tradeCountString, MessageTag.DATA);
                    }
                } else {
                    showMessage(context.getString(R.string.msg_get_all_load_trans_log) + null, MessageTag.ERROR);
                }
            }
        });
    }

    private String hexString(byte[] data) {
        return data == null ? "null" : ISOUtils.hexString(data);
    }

}
