package com.newland.sdkdemo.fragment.dock;



import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.newland.sdk.DeviceController;
import com.newland.sdk.constant.Code;
import com.newland.sdk.inter.externalCardreader.CardReaderExtParams;
import com.newland.sdk.inter.externalCardreader.CardReaderListener;
import com.newland.sdk.inter.externalCardreader.CardType;
import com.newland.sdk.inter.externalCardreader.ExtCardReaderModule;
import com.newland.sdk.inter.externalCardreader.RFCardInfo;
import com.newland.sdk.inter.externalmagic.ExtMagicCardModule;
import com.newland.sdk.inter.externalmagic.SwipResult;
import com.newland.sdk.inter.externalmagic.SwiperReadModel;
import com.newland.sdk.inter.externalpin.AccessType;
import com.newland.sdk.inter.externalpin.ExtParams;
import com.newland.sdk.inter.externalpin.ExtPinpadModule;
import com.newland.sdk.inter.externalrfcard.RFCardType;
import com.newland.sdk.module.emvl3.CAPK;
import com.newland.sdk.module.emvl3.CardContactMode;
import com.newland.sdk.module.emvl3.EMVL3Module;
import com.newland.sdk.module.emvl3.EmvInitParams;
import com.newland.sdk.pinpad.utils.ISOUtils;
import com.newland.sdk.pinpad.utils.LoggerUtil;
import com.newland.sdkdemo.AppConfig;
import com.newland.sdkdemo.MainActivity;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.SDKExecutors;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;
import com.newland.sdkdemo.fragment.BaseFragment;
import com.newland.sdkdemo.fragment.dock.communication.ChannelType;
import com.newland.sdkdemo.fragment.dock.communication.CommunicationTool;
import com.newland.sdkdemo.fragment.dock.emv.EmvL3Controller;
import com.newland.sdkdemo.fragment.dock.emv.EmvL3Listener;
import com.newland.sdkdemo.fragment.dock.emv.EmvL3TransConstant;
import com.newland.sdkdemo.fragment.dock.emv.TransParam;
import com.newland.sdkdemo.fragment.dock.emv.TransResultListener;
import com.newland.sdkdemo.utils.DialogUtils;
import com.newland.sdkdemo.utils.MessageTag;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Author by wuhh, Date on 2020/3/17.
 */
public class DockEmvL3Fragment extends BaseFragment {

    private static final String TAG = "EmvL3Fragment";
    private EMVL3Module mEmvL3Module;
    private ExtParams extParams;
    private EmvL3Listener emvL3Listener;
    private Context context;
    private ExtCardReaderModule extCardReaderModule;
    private ExtMagicCardModule extMagicCardModule;

    public DockEmvL3Fragment(Context context) {
        super(context, LayoutMode.GRID);
        this.context = context;
    }

    @Override
    public String title() {
        return null;
    }

    @Override
    public void initData() {
        mEmvL3Module = DeviceController.getInstance().getEMVL3Module();
        extCardReaderModule = DeviceController.getInstance().getExtCardReaderModule();
        extMagicCardModule = DeviceController.getInstance().getExtMagicCardModule();
    }

    @Override
    public Object getModule() {
        return DockEmvL3Fragment.this;
    }

    private static final int INDEX_MPOS_INIT = 1;
    private static final int INDEX_LOAD_PARAMS = 2;
    private static final int INDEX_LOADTERMINALCONFIG = 3;
    private static final int INDEX_OPER_AID = 4;
    private static final int INDEX_OPER_CAPK = 5;
    private static final int INDEX_TRANSCATION = 6;

    @MethodGridEntity(btnnameid = R.string.tv_emv_init, functionid = INDEX_MPOS_INIT)
    public void Init() {
        extParams = new ExtParams();
        CommunicationTool.getInstance().init(context);
        String[] accessTypes = new String[]{"DEFAULT-USB","DOCK-UART","DOCK_USB1"};
        DialogUtils.createSingleChoiceDialog(context, context.getString(R.string.msg_select_access_type), accessTypes, new DialogUtils.SingleChoiceDialogCallback() {
            @Override
            public void onResult(int id) {
                if (id == 0) {
                    extParams.setAccessType(AccessType.DEFAULT_USB);
                    CommunicationTool.getInstance().setChannelType(ChannelType.DEFAULT_USB);
                    showMessage(R.string.msg_choose_default_usb, MessageTag.NORMAL);
                } else if (id == 1) {
                    extParams.setAccessType(AccessType.OTHERS);
                    CommunicationTool.getInstance().setChannelType(ChannelType.DOCK_UART);
                    showMessage(R.string.msg_choose_serial, MessageTag.NORMAL);
                } else if (id == 2) {
                    extParams.setAccessType(AccessType.OTHERS);
                    CommunicationTool.getInstance().setChannelType(ChannelType.DOCK_USB1);
                    showMessage(R.string.msg_choose_usb, MessageTag.NORMAL);
                }
                SDKExecutors.getFixedThreadPoolInstance().submit(()->{
                    EmvInitParams emvInitParams = new EmvInitParams();
                    emvInitParams.setSupportEC(true);
                    emvInitParams.setSupportSM(true);
                    emvL3Listener = new EmvL3Listener(context);
                    extParams.setEmvl3Listener(emvL3Listener);
                    boolean isSucc = mEmvL3Module.init(emvInitParams, CommunicationTool.getInstance().getCommunicationListener(), extParams);
                    showMessage(context.getString(R.string.tv_emv_init) + " " + isSucc);
                });
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
            showMessage("configurate params loading......", MessageTag.TIP);
            boolean result = mEmvL3Module.loadConfiguration("Newland_L3_configuration.xml");
            showMessage("load configurate result:" + result);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("loadConfigParams exception:" + e, MessageTag.ERROR);
        }

    }

    @MethodGridEntity(btnnameid = R.string.tv_oper_terminalconfig, functionid = INDEX_LOADTERMINALCONFIG)
    private void loadTerminalConfig() {
        String items[] = new String[]{context.getString(R.string.tv_update_terminalconfig), context.getString(R.string.tv_get_terminalconfig)};
        DialogUtils.createSingleChoiceDialog(context, context.getString(R.string.tv_oper_terminalconfig), items, new DialogUtils.SingleChoiceDialogCallback() {
            @Override
            public void onResult(int id) {
                try {
                    switch (id) {
                        case 0:
                            updateTerminalconfig();
                            break;
                        case 1:
                            getTerminalconfig();
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateTerminalconfig() {
        byte[] contactConfig = ISOUtils.hex2byte("9F1A0201569F01060123456789009F150212345F2A0201565F3601009F3C0201569F3D0100DF220531303030009F1C0831323334353637389F160F3132333435363738393031323334359F350100");

//        byte[] contactConfig = ISOUtils.hex2byte("9F061000000000000000000000000000000000DF6407F4C0F0E8EF0E605F2A020840DF2204000000009F7A01019F350122DF0101019F3303E0F8C89F4005FF80F0A0019F01061234567890009F150212349F160F3132333435363738393031323334359F3C0208405F3601029F3D01009F1A0208409F1E0830303030303030319F1C0831323334353637389F7B06000000050000DF160100DF170100DF1504000001F49F1B0400000000DF44039F3704DF450F9F02065F2A029A039C0195059F37049F09020002");
        boolean issucc = mEmvL3Module.updateTerminalConfig(CardContactMode.CONTACT, contactConfig);
        showMessage(context.getString(R.string.tv_update_terminalconfig) + "  CONTACT " + issucc, MessageTag.DATA);
        byte[] contactlessConfig = ISOUtils.hex2byte("9F1A0201569F01060123456789009F150212345F2A0201565F3601009F3C0201569F3D0100DF220531303030009F1C0831323334353637389F160F3132333435363738393031323334359F350100");

//        byte[] contactlessConfig = ISOUtils.hex2byte("9F061000000000000000000000000000000000DF6407F4C0F0F8EF0E629F3501229F3303E0F8C89F4005FF80F0A0019F01060001234567899F150212349F160F3132333435363738393031323334355F3601029F3C0208409F3D01029F1A0208409F1E083835313049434300DF27011FDF2006000099999999DF1906000000020000DF2106000000050000DF3A0101DF390100DF1504000013889F09020002DF440B9F37049F47018F019F3201DF45039F0802DF0101015F2A0208409F1B04000000001F81020100");
        issucc = mEmvL3Module.updateTerminalConfig(CardContactMode.CONTACTLESS, contactlessConfig);
        showMessage(context.getString(R.string.tv_update_terminalconfig) + "  CONTACTLESS " + issucc, MessageTag.DATA);
    }

    private void getTerminalconfig() {
        byte[] config = mEmvL3Module.getTerminalConfig(CardContactMode.CONTACT);
        showMessage(context.getString(R.string.tv_get_terminalconfig) + "  CONTACT " + (config == null ? "null" : ISOUtils.hexString(config)), MessageTag.DATA);
        config = mEmvL3Module.getTerminalConfig(CardContactMode.CONTACTLESS);
        showMessage(context.getString(R.string.tv_get_terminalconfig) + "  CONTACTLESS " + (config == null ? "null" : ISOUtils.hexString(config)), MessageTag.DATA);
    }

    @MethodGridEntity(btnnameid = R.string.tv_oper_aid, functionid = INDEX_OPER_AID)
    public void operAID() {
        String items[] = new String[]{context.getString(R.string.tv_add_aid), context.getString(R.string.tv_get_one_aid), context.getString(R.string.tv_del_a_aid), context.getString(R.string.tv_clean_all_aid)};
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
                            delAid();
                            break;
                        case 3:
                            cleanAllAid();
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addAid() {
        try {
            boolean addResult = false;
            String aid1 = "9F0608A000000333010101DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000100000DF1906000000100000DF2006000000100000DF2106000000100000";
            addResult = mEmvL3Module.addAID(CardContactMode.CONTACT, ISOUtils.hex2byte(aid1));
            showMessage("Add AID[A000000333010101] CONTACT:" + (addResult == true ? context.getString(R.string.msg_common_succ) : context.getString(R.string.msg_common_failed)), MessageTag.DATA);
            addResult = mEmvL3Module.addAID(CardContactMode.CONTACTLESS, ISOUtils.hex2byte(aid1));
            showMessage("Add AID[A000000333010101] CONTACTLESS:" + (addResult == true ? context.getString(R.string.msg_common_succ) : context.getString(R.string.msg_common_failed)), MessageTag.DATA);

            String aid2 = "9F0608A000000333010102DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000100000DF1906000000100000DF2006000000100000DF2106000000100000";
            addResult = mEmvL3Module.addAID(CardContactMode.CONTACT, ISOUtils.hex2byte(aid2));
            showMessage("Add AID[A000000333010102]  CONTACT:" + (addResult == true ? context.getString(R.string.msg_common_succ) : context.getString(R.string.msg_common_failed)), MessageTag.DATA);
            addResult = mEmvL3Module.addAID(CardContactMode.CONTACTLESS, ISOUtils.hex2byte(aid2));
            showMessage("Add AID[A000000333010102] CONTACTLESS:" + (addResult == true ? context.getString(R.string.msg_common_succ) : context.getString(R.string.msg_common_failed)), MessageTag.DATA);

            String aid3 = "9F0608A000000333010103DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000100000DF1906000000100000DF2006000000100000DF2106000000100000";
            addResult = mEmvL3Module.addAID(CardContactMode.CONTACT, ISOUtils.hex2byte(aid3));
            showMessage("Add AID[A000000333010103] CONTACT:" + (addResult == true ? context.getString(R.string.msg_common_succ) : context.getString(R.string.msg_common_failed)), MessageTag.DATA);
            addResult = mEmvL3Module.addAID(CardContactMode.CONTACTLESS, ISOUtils.hex2byte(aid3));
            showMessage("Add AID[A000000333010103] CONTACTLESS:" + (addResult == true ? context.getString(R.string.msg_common_succ) : context.getString(R.string.msg_common_failed)), MessageTag.DATA);

            String aid4 = "9F0608A000000333010106DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000100000DF1906000000100000DF2006000000100000DF2106000000100000";
            addResult = mEmvL3Module.addAID(CardContactMode.CONTACT, ISOUtils.hex2byte(aid4));
            showMessage("Add AID[A000000333010106] CONTACT:" + (addResult == true ? context.getString(R.string.msg_common_succ) : context.getString(R.string.msg_common_failed)), MessageTag.DATA);
            addResult = mEmvL3Module.addAID(CardContactMode.CONTACTLESS, ISOUtils.hex2byte(aid4));
            showMessage("Add AID[A000000333010106] CONTACTLESS:" + (addResult == true ? context.getString(R.string.msg_common_succ) : context.getString(R.string.msg_common_failed)), MessageTag.DATA);

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
            delAID = mEmvL3Module.deleteAID(CardContactMode.CONTACT, aid);
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
            byte[] aidConfig = mEmvL3Module.getAID(CardContactMode.CONTACT, aidData);
            if (aidConfig != null) {
                showMessage("aid:" + (aidConfig == null ? "null" : ISOUtils.hexString(aidConfig)), MessageTag.ERROR);
            } else {
                showMessage(context.getString(R.string.msg_get_one_aid_failed), MessageTag.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_get_all_aid_list_exception) + e.getMessage(), MessageTag.ERROR);
        }

    }


    @MethodGridEntity(btnnameid = R.string.tv_oper_capk, functionid = INDEX_OPER_CAPK)
    public void operCAPK() {
        String items[] = new String[]{context.getString(R.string.tv_add_pk), context.getString(R.string.tv_get_one_pk), context.getString(R.string.tv_del_a_pk), context.getString(R.string.tv_clean_all_pk)};
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
                            delCapk();
                            break;
                        case 3:
                            cleanAllCapk();
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void addCapk() {
        try {
            boolean addResult = false;
            String capk1 = "9F0605A0000003339F220180DF05083230333031323331DF060101DF070101DF028180CCDBA686E2EFB84CE2EA01209EEB53BEF21AB6D353274FF8391D7035D76E2156CAEDD07510E07DAFCACABB7CCB0950BA2F0A3CEC313C52EE6CD09EF00401A3D6CC5F68CA5FCD0AC6132141FAFD1CFA36A2692D02DDC27EDA4CD5BEA6FF21913B513CE78BF33E6877AA5B605BC69A534F3777CBED6376BA649C72516A7E16AF85DF0403010001DF0314A5E44BB0E1FA4F96A11709186670D0835057D35E";
            String capk = "9F0605A0000003339F220101DF05083230303931323331DF060101DF070101DF028180BBE9066D2517511D239C7BFA77884144AE20C7372F515147E8CE6537C54C0A6A4D45F8CA4D290870CDA59F1344EF71D17D3F35D92F3F06778D0D511EC2A7DC4FFEADF4FB1253CE37A7B2B5A3741227BEF72524DA7A2B7B1CB426BEE27BC513B0CB11AB99BC1BC61DF5AC6CC4D831D0848788CD74F6D543AD37C5A2B4C5D5A93BDF040103DF0314E881E390675D44C2DD81234DCE29C3F5AB2297A0";
            addResult = mEmvL3Module.addCAPublicKey(ISOUtils.hex2byte(capk1));
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
            CAPK capk = mEmvL3Module.getCAPublicKey(specifiedRid, specifiedIndex);
            if (capk != null) {
                int index = capk.getIndex();
                showMessage(context.getString(R.string.msg_get_pk_index) + index, MessageTag.DATA);
                showMessage(context.getString(R.string.msg_get_pk_rid) + ISOUtils.hexString(specifiedRid), MessageTag.DATA);
                showMessage("hashAlgorithmIndicator:" + capk.getHashAlgorithmIndicator(), MessageTag.DATA);
                showMessage("publicKeyAlgorithmIndicator:" + capk.getPublicKeyAlgorithmIndicator(), MessageTag.DATA);
                showMessage("modulus:" + (capk.getModulus() == null ? null : ISOUtils.hexString(capk.getModulus())), MessageTag.DATA);
                showMessage("exponent:" + (capk.getExponent() == null ? null : ISOUtils.hexString(capk.getExponent())), MessageTag.DATA);
                showMessage("ExpirationDate:" + (capk.getExpirationDate() == null ? null : capk.getExpirationDate()), MessageTag.DATA);
                showMessage("Sha1CheckSum:" + (capk.getSha1CheckSum() == null ? null : ISOUtils.hexString(capk.getSha1CheckSum())), MessageTag.DATA);

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
            delPKByRid = mEmvL3Module.deleteCAPublicKey(rid, keyIndex);
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

    /**
     * clear all capk
     */
    private void cleanAllCapk() {
        try {
            showMessage(context.getString(R.string.msg_clean_all_pk_start), MessageTag.TIP);
            boolean clearAllCAPublicKey = false;
            clearAllCAPublicKey = mEmvL3Module.deleteAllCAPublicKey();
            showMessage(context.getString(R.string.msg_clean_all_pk_result) + clearAllCAPublicKey, MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_clean_all_pk) + context.getString(R.string.common_exception) + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_consume, functionid = INDEX_TRANSCATION)
    private void trans() {
        DialogUtils.createCustomDialog(context,R.string.tv_consume, null, R.layout.dialog_trans_l3, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View dialogView) {
                boolean hasMagneticModule = hasMagneticModule(extParams);
                if (hasMagneticModule) {
                    LinearLayout llCheckUnionCard = dialogView.findViewById(R.id.ll_l3_mag_checkunioncard);
                    llCheckUnionCard.setVisibility(View.VISIBLE);
                } else {
                    LinearLayout llCheckUnionCard = dialogView.findViewById(R.id.ll_l3_mag_checkunioncard);
                    llCheckUnionCard.setVisibility(View.GONE);
                }
            }

            @Override
            public void onResult(int id, View dialogView) {
                if (id == -1) {//cancel
                    return;
                }
                AppConfig.isSimpleFlow = false;
                RadioGroup transType = dialogView.findViewById(R.id.radiogroup_transtype);
                EditText amoutEdit = dialogView.findViewById(R.id.edit_amt_trans);
                DecimalFormat df = new DecimalFormat("#.00");
                if (amoutEdit.getText().toString().equals("") || amoutEdit.getText().toString() == null) {
                    AppConfig.EMV.amt = null;
                    showMessage(context.getString(R.string.msg_trans_money_empty) + "\r\n", MessageTag.NORMAL);
                } else {
                    AppConfig.EMV.amt = new BigDecimal(amoutEdit.getText().toString());
                    showMessage(context.getString(R.string.msg_trans_money_is) + df.format(AppConfig.EMV.amt) + "\r\n", MessageTag.NORMAL);
                }
                CardReaderExtParams cardReaderExtParams = null;
                boolean hasMagneticModule = hasMagneticModule(extParams);
                if (hasMagneticModule) {
                    /**
                     * 行业卡或者存折时，需要设置setCheckUnionCard为false，cardReaderExtParams为null或者setCheckUnionCard不设置，则校验二磁道信息。
                     * When using an industry card or passbook, it is necessary to set setCheckUnionCard to false, cardReaderExtParams to null,
                     * or setCheckUnionCard is not set to verify the two track information.
                     */
                    RadioGroup checkUnionGroup = dialogView.findViewById(R.id.radiogroup_checkunion);
                    int checkId = checkUnionGroup.getCheckedRadioButtonId();
                    boolean isCheck = true;
                    if (checkId == R.id.radio_mag_checkunioncard_true) {
                        isCheck = true;
                    } else if (checkId == R.id.radio_mag_checkunioncard_false) {
                        isCheck = false;
                    }
                    cardReaderExtParams = new CardReaderExtParams();
                    cardReaderExtParams.setCheckUnionCard(isCheck);
                    showMessage(context.getString(R.string.msg_mag_check_union_card) + isCheck + "\r\n", MessageTag.NORMAL);
                } else {
                    cardReaderExtParams = null;
                }

                TransParam transParam = new TransParam();
                try {
                    switch (transType.getCheckedRadioButtonId()) {
                        case R.id.radio_sale:
                            showMessage("start sale transaction...");
                            transParam.setTransType(EmvL3TransConstant.TransType.SALE);
                            break;
                        case R.id.radio_cashback:
                            showMessage("start cashback transaction...");
                            transParam.setTransType(EmvL3TransConstant.TransType.CASHBACK);
                            break;
                        case R.id.radio_refund:
                            showMessage("start refund transaction...");
                            transParam.setTransType(EmvL3TransConstant.TransType.REFUND);
                            break;
                        case R.id.radio_simple_flow:
                            showMessage("start refund transaction...");
                            AppConfig.isSimpleFlow = true;
                            break;
                        default:
                            return;
                    }
                    //CardType.MSGCARD, CardType.ICCARD,
                    extCardReaderModule.openCardReader(new CardType[]{CardType.MSGCARD, CardType.ICCARD,CardType.RFCARD}, 60, new CardReaderListener() {
                        @Override
                        public void onTimeout() {
                            showMessage("openCardReader [onTimeout]", MessageTag.ERROR);

                        }

                        @Override
                        public void onCancel() {
                            showMessage("openCardReader [onCancel]", MessageTag.ERROR);

                        }

                        @Override
                        public void onError(int errorCode, String message) {
                            showMessage("openCardReader [onError]", MessageTag.ERROR);

                        }

                        @Override
                        public void onFindMagCard(boolean isSuccessful) {
                            showMessage("openCardReader [onFindMagCard]", MessageTag.TIP);
                            SwipResult swipResult = extMagicCardModule.readPlainResult(new SwiperReadModel[]{SwiperReadModel.FIRST_TRACK, SwiperReadModel.SECOND_TRACK, SwiperReadModel.THIRD_TRACK});
                            if (swipResult != null) {
                                byte[] firstData = swipResult.getFirstTrackData();
                                byte[] secondData = swipResult.getSecondTrackData();
                                byte[] thirdData = swipResult.getThirdTrackData();
                                showMessage("One track data：" + (firstData == null ? null : new String(firstData)), MessageTag.DATA);
                                showMessage("Two track data：" + (secondData == null ? null : new String(secondData)), MessageTag.DATA);
                                showMessage("Three track data：" + (thirdData == null ? null : new String(thirdData)), MessageTag.DATA);

                            } else {
                                showMessage("openCardReader swipResult == null", MessageTag.ERROR);

                            }
                        }

                        @Override
                        public void onFindICCard() {
                            showMessage("openCardReader [onFindICCard]", MessageTag.TIP);
                            transParam.setCurrentCardInterfaces(Code.CardInterfaces.CONTACT);//若先调用了openCardReader，需要设置当前识别到的卡介质类型
                            startTrans(0x07, transParam);
                        }

                        @Override
                        public void onFindRFCard(@Nullable RFCardType rfCardType, @Nullable RFCardInfo rfCardInfo) {
                            showMessage("openCardReader [onFindRFCard]", MessageTag.TIP);
                            transParam.setCurrentCardInterfaces(Code.CardInterfaces.CONTACTLESS);//若先调用了openCardReader，需要设置当前识别到的卡介质类型
                            startTrans(0x07, transParam);
                        }
                    }, cardReaderExtParams);
////                    int cardInputMode = 0x01 | 0x02 | 0x04; // sp100暂不支持
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage("exception:" + e, MessageTag.ERROR);
                }
            }
        });
    }

    private void startTrans(int cardInputMode, TransParam transParam) {
        try {
            //  int cardInputMode = 0x04;
            transParam.setCardInputMode(cardInputMode);
            EmvL3Controller emvL3Controller = new EmvL3Controller(context, mEmvL3Module, transParam);
            emvL3Controller.startTransaction(new TransResultListener() {
                @Override
                public void onSuccess() {
                    LoggerUtil.debug(TAG, "Transaction Success");
                    showMessage("Transaction Success");
                }

                @Override
                public void onFail(String message) {
                    LoggerUtil.debug(TAG, message);
                    showMessage(message);
                }
            });
            emvL3Listener.setEmvL3Controller(emvL3Controller);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cleanAllAid() {
//        try {
//            showMessage(context.getString(R.string.msg_clean_all_aid), MessageTag.TIP);
//            boolean clearAllAID = false;
//            clearAllAID = mEmvL3Module.deleteAllAID(CardContactMode.CONTACT);
//            showMessage(context.getString(R.string.msg_clean_aid_result) + clearAllAID, MessageTag.DATA);
//
//            clearAllAID = mEmvL3Module.deleteAllAID(CardContactMode.CONTACTLESS);
//            showMessage(context.getString(R.string.msg_clean_aid_result) + clearAllAID, MessageTag.DATA);
//        } catch (Exception e) {
//            showMessage(context.getString(R.string.msg_clean_aid) + context.getString(R.string.common_exception) + e.getMessage(), MessageTag.ERROR);
//        }
    }

    public boolean hasMagneticModule(ExtParams extParams) {
        String version = getVersion(extParams);
        if (version != null) {
            if (version.startsWith("V05")) {
                // ME51机型才有磁条卡
                return true;
            }
        }
        return false;
    }

    private String getVersion(ExtParams extParams) {
        ExtPinpadModule externalPinInput = DeviceController.getInstance().getExtPinpadModule();
        externalPinInput.init(CommunicationTool.getInstance().getCommunicationListener(),extParams);
        String version = externalPinInput.getInfo("VERSION");
        LoggerUtil.debug(TAG, "[getVersion] " + version);
        return version;
    }
}
