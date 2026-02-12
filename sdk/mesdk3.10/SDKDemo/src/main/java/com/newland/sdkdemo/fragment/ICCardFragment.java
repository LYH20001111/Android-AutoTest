package com.newland.sdkdemo.fragment;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.newland.industryic.CPUCard;
import com.newland.industryic.IndustryICCardImpl;
import com.newland.sdk.module.iccard.ICCardModule;
import com.newland.sdk.module.iccard.ICCardSlot;
import com.newland.sdk.module.iccard.ICCardSlotState;
import com.newland.sdk.module.iccard.ICCardType;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;
import com.newland.sdkdemo.utils.DialogUtils;
import com.newland.sdkdemo.utils.MessageTag;
import com.newland.sdkdemo.utils.MyRadioGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * Author by bxy, Date on 2019/5/11 0011.
 */
public class ICCardFragment extends BaseFragment {
    private ICCardModule iCCardModule;
    private ICCardSlot icCardSlot = ICCardSlot.IC1;
    private ICCardType icCardType = ICCardType.CPUCARD;
    private byte poweronresult[] = null;
    private IndustryICCardImpl icCardImpl = null;

    private static final int INDEX_ICCARD_POWERON = 1;
    private static final int INDEX_ICCARD_COMMUNICATION = 2;
    private static final int INDEX_ICCARD_SLOT_STATE = 3;

    private static final int INDEX_ICCARD_POWEROFF = 4;
    private static final int INDEX_ICCARD_FILL0 = 5;
    private static final int INDEX_ICCARD_FILL1 = 6;

    private static final int INDEX_ICCARD_FILL2 = 7;
    private static final int INDEX_ICCARD_FILL3 = 8;
    private static final int INDEX_ICCARD_FILL4 = 9;

    private static final int INDEX_MEMORYCARD_OPEN = 10;
    private static final int INDEX_MEMORYCARD_VERIFY = 11;
    private static final int INDEX_MEMORYCARD_READ = 12;
    private static final int INDEX_MEMORYCARD_WRITE = 13;
    private static final int INDEX_MEMORYCARD_CHANGE_PASSWORD = 14;
    private static final int INDEX_MEMORYCARD_CLOSE = 15;

    public ICCardFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_iccard_f);
    }

    @Override
    public void initData() {
        iCCardModule = moduleManage.getICCardModule();
        icCardImpl = new IndustryICCardImpl();
    }

    @Override
    public Object getModule() {
        return ICCardFragment.this;
    }


    @MethodGridEntity(btnnameid = R.string.tv_iccard_poweron, functionid = INDEX_ICCARD_POWERON)
    private void icCardPowerOn() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_iccard_poweron), null, R.layout.dialog_iccard_poweron, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if(id == -1){//cancel
                        return;
                    }
                    icCardSlot = getICCardSlot(dialogView);
                    icCardType = getICCardType(dialogView);
                    showMessage(context.getString(R.string.common_slot) + icCardSlot + "；" + context.getString(R.string.common_card_type) + icCardType, MessageTag.DATA);
                    poweronresult = iCCardModule.powerOn(icCardSlot, icCardType);
                    showMessage(context.getString(R.string.msg_poweron_result) + (poweronresult == null ? null : ISOUtils.hexString(poweronresult)) + "\r\n", MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_poweron_end) + "\r\n", MessageTag.DATA);
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(context.getString(R.string.msg_slot_poweron_ex) + e , MessageTag.ERROR);
                    showMessage(context.getString(R.string.msg_pl_check_inserted) + "\r\n", MessageTag.ERROR);
                }

            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_iccard_transation, functionid = INDEX_ICCARD_COMMUNICATION)
    private void icCardCommunication() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_iccard_transation), null, R.layout.dialog_iccard_communication, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if(id == -1){//cancel
                        return;
                    }
                    EditText edtText = dialogView.findViewById(R.id.edit_ICCardSend);
                    String str = edtText.getText().toString();//Get communication data
                    byte req[] = ISOUtils.hex2byte(str);
                    showMessage(context.getString(R.string.common_slot) + icCardSlot + "；" + context.getString(R.string.common_card_type) + icCardType, MessageTag.DATA);
                    byte back[] = iCCardModule.transmit(icCardSlot, icCardType, req, 30);
                    showMessage(context.getString(R.string.msg_iccard_type) + icCardType, MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_send_data) + str, MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_receive_data) + (back==null?null:ISOUtils.hexString(back)), MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_iccard_comm_succ) + "\r\n", MessageTag.DATA);
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(context.getString(R.string.msg_iccard_comm_ex) + e, MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_iccard_state, functionid = INDEX_ICCARD_SLOT_STATE)
    private void icCardSlotState() {
        try {
            Map<ICCardSlot, ICCardSlotState> map = new HashMap<ICCardSlot, ICCardSlotState>();
            map = iCCardModule.checkSlotsState();
            for (Map.Entry<ICCardSlot, ICCardSlotState> entry : map.entrySet()) {
                if (entry.getKey() != null)
                    if (!entry.getValue().toString().equals("NO_CARD")) {
                        showMessage(context.getString(R.string.common_slot) + entry.getKey() + "--->" + context.getString(R.string.common_card) + entry.getValue() + "\r\n", MessageTag.DATA);
                    } else {
                        showMessage(context.getString(R.string.common_slot) + entry.getKey() + "--->" + context.getString(R.string.common_card) + entry.getValue() + "\r\n", MessageTag.DATA);
                    }
            }
            showMessage(context.getString(R.string.msg_detect_end) + "\r\n", MessageTag.NORMAL);

        } catch (Exception e) {
            showMessage(context.getString(R.string.msg_detect_ex) + e , MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_iccard_powerof, functionid = INDEX_ICCARD_POWEROFF)
    private void icCardPowerOff() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_iccard_powerof), null, R.layout.dialog_iccard_poweron, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if(id == -1){//cancel
                        return;
                    }
                    icCardSlot = getICCardSlot(dialogView);
                    icCardType = getICCardType(dialogView);
                    showMessage(context.getString(R.string.common_slot) + icCardSlot + "；" + context.getString(R.string.common_card_type) + icCardType, MessageTag.DATA);
                    iCCardModule.powerOff(icCardSlot, icCardType);
                    showMessage(icCardSlot.toString() + context.getString(R.string.msg_poweroff_end) + "\r\n", MessageTag.DATA);

                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(context.getString(R.string.msg_slot_poweroff_ex) + e, MessageTag.ERROR);
                    showMessage(context.getString(R.string.msg_pl_check_inserted) + "\r\n", MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(functionid = INDEX_ICCARD_FILL0)
    private void fill0() {
    }

    @MethodGridEntity(functionid = INDEX_ICCARD_FILL1)
    private void fill1() {
    }


    @MethodGridEntity(divtipid = 0, functionid = INDEX_ICCARD_FILL2)
    private void fill2() {

    }

    @MethodGridEntity(divtipid = R.string.tv_iccard_memory_card, functionid = INDEX_ICCARD_FILL3)
    private void fill3() {

    }

    @MethodGridEntity(divtipid = 0, functionid = INDEX_ICCARD_FILL4)
    private void fill4() {

    }

    @MethodGridEntity(btnnameid = R.string.tv_iccard_open, functionid = INDEX_MEMORYCARD_OPEN,btnimageid = 1)
    private void memoryCardOpen() {
        String[] items = new String[]{"AT88SC102", "SLE4442", "AT24CXX"};
        DialogUtils.createSingleChoiceDialog(context, context.getString(R.string.tv_iccard_open), items, new DialogUtils.SingleChoiceDialogCallback() {
            @Override
            public void onResult(int id) {
                try {
                    switch (id) {
                        case 0: {
                            int rslt = icCardImpl.open(CPUCard.CPUCardType.AT88SC102);
                            if (rslt == 0) {
                                showMessage(context.getString(R.string.msg_open_at88_succ), MessageTag.DATA);
                            } else if (rslt == 1) {
                                showMessage(context.getString(R.string.msg_not_support_open_the_card), MessageTag.ERROR);
                            } else {
                                showMessage(context.getString(R.string.msg_open_at88_failed), MessageTag.ERROR);
                            }
                            break;
                        }
                        case 1: {
                            int rslt = icCardImpl.open(CPUCard.CPUCardType.SLE4442);
                            if (rslt == 0) {
                                showMessage(context.getString(R.string.msg_open_sle44_succ), MessageTag.DATA);
                            } else if (rslt == 1) {
                                showMessage(context.getString(R.string.msg_not_support_open_the_card), MessageTag.ERROR);
                            } else {
                                showMessage(context.getString(R.string.msg_open_sle44_failed), MessageTag.ERROR);
                            }
                            break;
                        }
                        case 2: {
                            int rslt = icCardImpl.open(CPUCard.CPUCardType.AT24CXX);
                            if (rslt == 0) {
                                showMessage(context.getString(R.string.msg_open_at24_succ), MessageTag.DATA);
                            } else if (rslt == 1) {
                                showMessage(context.getString(R.string.msg_not_support_open_the_card), MessageTag.ERROR);
                            } else {
                                showMessage(context.getString(R.string.msg_open_at24_failed), MessageTag.ERROR);
                            }
                            break;
                        }
                        default:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(context.getString(R.string.msg_open_industry_card_failed) + e, MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_iccard_open_atr, functionid = INDEX_MEMORYCARD_VERIFY,btnimageid = 1)
    private void memoryCardVerify() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_iccard_open_atr), null, R.layout.dialog_iccard_open, new DialogUtils.CustomDialogCallback() {

            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if(id == -1){//cancel
                        return;
                    }
                    EditText editText = dialogView.findViewById(R.id.edit_atr);
                    RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(R.id.radioGroup_iccard_industry);
                    int checkId = radioGroup.getCheckedRadioButtonId();
                    int rslt = 0;
                    byte atr[] = ISOUtils.hex2byte(editText.getText().toString());
                    int industryicCardType = CPUCard.CPUCardType.AT88SC102;
                    switch (checkId) {
                        case R.id.radio_102:
                            industryicCardType = CPUCard.CPUCardType.AT88SC102;
                            showMessage(context.getString(R.string.msg_open_at88_succ), MessageTag.NORMAL);
                            rslt = icCardImpl.openWithATRVerification(industryicCardType, atr);
                            if (rslt == 0) {
                                showMessage(context.getString(R.string.msg_open_at88_succ), MessageTag.DATA);
                            } else if (rslt == 1) {
                                showMessage(context.getString(R.string.msg_not_support_open_the_card), MessageTag.ERROR);
                            } else {
                                showMessage(context.getString(R.string.msg_open_at88_failed), MessageTag.ERROR);
                            }

                            break;
                        case R.id.radio_SLE44X2:
                            industryicCardType = CPUCard.CPUCardType.SLE4442;
                            showMessage("card type:" + industryicCardType, MessageTag.NORMAL);
                            rslt = icCardImpl.openWithATRVerification(industryicCardType, atr);
                            if (rslt == 0) {
                                showMessage(context.getString(R.string.msg_open_sle44_succ), MessageTag.DATA);
                            } else if (rslt == 1) {
                                showMessage(context.getString(R.string.msg_not_support_open_the_card), MessageTag.ERROR);
                            } else {
                                showMessage(context.getString(R.string.msg_open_sle44_failed), MessageTag.ERROR);
                            }
                            break;

                        case R.id.radio_AT24cxx:
                            industryicCardType = CPUCard.CPUCardType.AT24CXX;
                            rslt = icCardImpl.openWithATRVerification(industryicCardType, atr);
                            if (rslt == 0) {
                                showMessage(context.getString(R.string.msg_open_at24_succ), MessageTag.DATA);
                            } else if (rslt == 1) {
                                showMessage(context.getString(R.string.msg_not_support_open_the_card), MessageTag.ERROR);
                            } else {
                                showMessage(context.getString(R.string.msg_open_at24_failed), MessageTag.ERROR);
                            }
                            break;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_iccard_read, functionid = INDEX_MEMORYCARD_READ,btnimageid = 2)
    private void memoryCardRead() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_iccard_read), null, R.layout.dialog_verifypassword, new DialogUtils.CustomDialogCallback() {

            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if(id == -1){//cancel
                        return;
                    }
                    EditText password = (EditText) dialogView.findViewById(R.id.edt_psw);
                    EditText edtadress = (EditText) dialogView.findViewById(R.id.edt_adress);
                    EditText edtDataLen = (EditText) dialogView.findViewById(R.id.edt_datalen);

                    String psw = password.getText().toString();
                    String adress = edtadress.getText().toString();
                    String dataLen = edtDataLen.getText().toString();
                    showMessage(context.getString(R.string.msg_initial_address) + adress, MessageTag.NORMAL);
                    showMessage(context.getString(R.string.msg_read_data_length) + dataLen, MessageTag.NORMAL);
                    if ("".equals(adress) || "".equals(dataLen)) {
                        showMessage(context.getString(R.string.msg_cannot_null), MessageTag.ERROR);
                    } else {
                        if ("".equals(psw)) {
                            showMessage(context.getString(R.string.msg_nocheck_read), MessageTag.NORMAL);
                            byte[] readData = icCardImpl.read(null, Integer.parseInt(adress), Integer.parseInt(dataLen));
                            showMessage(context.getString(R.string.msg_read_cpu_result) + (readData == null ? null : ISOUtils.hexString(readData)), MessageTag.DATA);
                        } else {
                            showMessage(context.getString(R.string.msg_psw_check_enter) + psw, MessageTag.NORMAL);
                            byte[] readData = icCardImpl.read(ISOUtils.hex2byte(psw), Integer.parseInt(adress), Integer.parseInt(dataLen));
                            showMessage(context.getString(R.string.msg_read_cpu_result) + (readData == null ? null : ISOUtils.hexString(readData)), MessageTag.DATA);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(context.getString(R.string.msg_read_industry_card_failed) + e, MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_iccard_write, functionid = INDEX_MEMORYCARD_WRITE,btnimageid = 3)
    private void memoryCardWrite() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_iccard_write), null, R.layout.dialog_write_cpucard, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if(id == -1){//cancel
                        return;
                    }
                    EditText password = dialogView.findViewById(R.id.edt_write_psw);
                    EditText edtAdress = dialogView.findViewById(R.id.edt_write_adress);
                    EditText estData = dialogView.findViewById(R.id.edt_write_data);
                    String psw = password.getText().toString();
                    String adress = edtAdress.getText().toString();
                    String data = estData.getText().toString();
                    showMessage(context.getString(R.string.msg_initial_address) + adress, MessageTag.NORMAL);
                    showMessage(context.getString(R.string.msg_write_data_length) + data, MessageTag.NORMAL);
                    showMessage(context.getString(R.string.msg_psw_check_enter) + psw, MessageTag.NORMAL);
                    if ("".equals(adress) || "".equals(data)) {
                        showMessage(context.getString(R.string.msg_cannot_null_write), MessageTag.ERROR);
                    } else {
                        if ("".equals(psw)) {
                            boolean writeRslt = icCardImpl.write(null, Integer.parseInt(adress), ISOUtils.hex2byte(data));
                            if (writeRslt) {
                                showMessage(context.getString(R.string.msg_write_cpu_succ_result) + data, MessageTag.DATA);
                            } else {
                                showMessage(context.getString(R.string.msg_write_cpu_failed), MessageTag.ERROR);
                            }
                        } else {
                            boolean writeRslt = icCardImpl.write(ISOUtils.hex2byte(psw), Integer.parseInt(adress), ISOUtils.hex2byte(data));
                            if (writeRslt) {
                                showMessage(context.getString(R.string.msg_write_cpu_succ_result) + data, MessageTag.DATA);
                            } else {
                                showMessage(context.getString(R.string.msg_write_cpu_failed), MessageTag.ERROR);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(context.getString(R.string.msg_write_industry_card_failed) + e, MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_iccard_update_pwd, functionid = INDEX_MEMORYCARD_CHANGE_PASSWORD,btnimageid = 4)
    private void memoryCardChangePassword() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_iccard_update_pwd), null, R.layout.dialog_changepassword, new DialogUtils.CustomDialogCallback() {

            @Override
            public void onResult(int id, View dialogView) {
                if(id == -1){//cancel
                    return;
                }
                EditText oldPsw = (EditText) dialogView.findViewById(R.id.edt_oldpsw);
                EditText newPsw = (EditText) dialogView.findViewById(R.id.edt_newpsw);
                String oldPassWord = oldPsw.getText().toString();
                String newPassWord = newPsw.getText().toString();
                if ("".equals(oldPassWord) || "".equals(newPassWord)) {
                    showMessage(context.getString(R.string.msg_pwd_null), MessageTag.ERROR);
                } else {
                    boolean rslt = icCardImpl.changePassword(ISOUtils.hex2byte(oldPassWord), ISOUtils.hex2byte(newPassWord));
                    if (rslt) {
                        showMessage(context.getString(R.string.msg_change_cpu_pwd_succ), MessageTag.DATA);
                    } else {
                        showMessage(context.getString(R.string.msg_change_cpu_pwd_failed), MessageTag.ERROR);
                    }
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_iccard_close, functionid = INDEX_MEMORYCARD_CLOSE,btnimageid = 5)
    private void memoryCardClose() {
        try {
            showMessage(context.getString(R.string.msg_start_close_industry_card), MessageTag.NORMAL);
            icCardImpl.close();
            showMessage(context.getString(R.string.msg_close_industry_card_end), MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_close_industry_card_ex) + e, MessageTag.ERROR);
        }

    }

    private ICCardSlot getICCardSlot(View dialogView) {
        try {
            MyRadioGroup iccardSlot = (MyRadioGroup) dialogView.findViewById(R.id.radioGroup_iccardslot);
            int slotCheckedId = iccardSlot.getCheckedRadioButtonId();

            switch (slotCheckedId) {
                case R.id.radio_IC1:
                    icCardSlot = ICCardSlot.IC1;
                    break;
                case R.id.radio_IC2:
                    icCardSlot = ICCardSlot.IC2;
                    break;
                case R.id.radio_IC3:
                    icCardSlot = ICCardSlot.IC3;
                    break;
                case R.id.radio_SAM1:
                    icCardSlot = ICCardSlot.SAM1;
                    break;
                case R.id.radio_SAM2:
                    icCardSlot = ICCardSlot.SAM2;
                    break;
                case R.id.radio_SAM3:
                    icCardSlot = ICCardSlot.SAM3;
                    break;
            }
            return icCardSlot;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ICCardSlot.IC1;
    }

    private ICCardType getICCardType(View dialogView) {
        try {
            MyRadioGroup iccardType = (MyRadioGroup) dialogView.findViewById(R.id.radioGroup_iccard_type);
            int tyepCheckedId = iccardType.getCheckedRadioButtonId();


            switch (tyepCheckedId) {
                case R.id.radio_CPUCARD:
                    icCardType = ICCardType.CPUCARD;
                    break;
                case R.id.radio_SLE44X2:
                    icCardType = ICCardType.SLE44X2;
                    break;
                case R.id.radio_SLE44X8:
                    icCardType = ICCardType.SLE44X8;
                    break;
                case R.id.radio_AT88SC102:
                    icCardType = ICCardType.AT88SC102;
                    break;
                case R.id.radio_AT88SC1604:
                    icCardType = ICCardType.AT88SC1604;
                    break;
                case R.id.radio_AT88SC1608:
                    icCardType = ICCardType.AT88SC1608;
                    break;
                case R.id.radio_ISO7816:
                    icCardType = ICCardType.ISO7816;
                    break;
                case R.id.radio_AT88SC153:
                    icCardType = ICCardType.AT88SC153;
                    break;
                case R.id.radio_AT24C01:
                    icCardType = ICCardType.AT24C01;
                    break;
                case R.id.radio_AT24C02:
                    icCardType = ICCardType.AT24C02;
                    break;
                case R.id.radio_AT24C04:
                    icCardType = ICCardType.AT24C04;
                    break;
                case R.id.radio_AT24C08:
                    icCardType = ICCardType.AT24C08;
                    break;
                case R.id.radio_AT24C16:
                    icCardType = ICCardType.AT24C16;
                    break;
                case R.id.radio_AT24C33:
                    icCardType = ICCardType.AT24C32;
                    break;
                case R.id.radio_AT24C64:
                    icCardType = ICCardType.AT24C64;
                    break;
                case R.id.radio_AT24C128:
                    icCardType = ICCardType.AT24C128;
                    break;
                case R.id.radio_AT24C256:
                    icCardType = ICCardType.AT24C256;
                    break;
            }
            return icCardType;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ICCardType.CPUCARD;
    }
}
