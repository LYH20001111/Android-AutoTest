package com.newland.sdkdemo.test;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.newland.basetest.annotation.FragmentAno;
import com.newland.basetest.annotation.MethodAno;
import com.newland.basetest.pinc.MessageTag;
import com.newland.basetest.pinc.MethodBean;
import com.newland.mesdk.simple.demo.R;
import com.newland.sdk.module.cardreader.CardReaderExtParams;
import com.newland.sdk.module.cardreader.CardReaderListener;
import com.newland.sdk.module.cardreader.CardType;
import com.newland.sdk.module.cardreader.RFCardInfo;
import com.newland.sdk.module.rfcard.FelicaParams;
import com.newland.sdk.module.rfcard.RFCardModule;
import com.newland.sdk.module.rfcard.RFCardPowerOnExtParams;
import com.newland.sdk.module.rfcard.RFCardType;
import com.newland.sdk.module.rfcard.RFKeyMode;
import com.newland.sdk.module.rfcard.RFResult;
import com.newland.sdk.module.swiper.SwipResult;
import com.newland.sdk.module.swiper.SwiperReadModel;
import com.newland.sdk.mtype.util.Dump;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdkdemo.FragmentBase;
import com.newland.sdkdemo.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;


@FragmentAno(name = "非接卡", numId = 4)
public class RFCardOption extends FragmentBase {
    private RFCardModule rfCardModule;
    private String snr;
    private int timeout = 60;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rfCardModule = getModuleManage().getRFCardModule();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @MethodAno(name = "寻卡上电", numId = 0)
    public void startScan(MethodBean bean) {
        DialogUtils dialogUtils = DialogUtils.getInstance();
        dialogUtils.createCustomDialog(getContext(), R.string.dialog_rf_power_on, null, R.layout.dialog_rfcard_power_on, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                CheckBox checkBoxAcard = view.findViewById(R.id.checkbox_rf_A_card);
                CheckBox checkBoxBcard = view.findViewById(R.id.checkbox_rf_B_card);
                CheckBox checkBoxM1card = view.findViewById(R.id.checkbox_rf_M1_card);
                CheckBox checkBoxM0card = view.findViewById(R.id.checkbox_rf_M0_card);
                CheckBox checkBoxFelicaCard = view.findViewById(R.id.checkbox_rf_felic_card);
                checkBoxAcard.setChecked(true);
                checkBoxBcard.setChecked(true);
                checkBoxM1card.setChecked(true);
                checkBoxM0card.setChecked(true);
                checkBoxFelicaCard.setChecked(false);
            }

            @Override
            public void onResult(int id, View view) {
                try {
                    showMessage("开始非接寻卡上电",MessageTag.NORMAL);
                    List<RFCardType> cardTypeList = new ArrayList<RFCardType>();
                    RFCardPowerOnExtParams rfCardPowerOnExtParams = null;

                    CheckBox checkBoxAcard = view.findViewById(R.id.checkbox_rf_A_card);
                    CheckBox checkBoxBcard = view.findViewById(R.id.checkbox_rf_B_card);
                    CheckBox checkBoxM1card = view.findViewById(R.id.checkbox_rf_M1_card);
                    CheckBox checkBoxM0card = view.findViewById(R.id.checkbox_rf_M0_card);
                    CheckBox checkBoxFelicaCard = view.findViewById(R.id.checkbox_rf_felic_card);

                    if (checkBoxAcard.isChecked()) {
                        cardTypeList.add(RFCardType.ACARD);
                    }
                    if (checkBoxBcard.isChecked()) {
                        cardTypeList.add(RFCardType.BCARD);
                    }
                    if (checkBoxM1card.isChecked()) {
                        cardTypeList.add(RFCardType.M1CARD);
                    }
                    if (checkBoxM0card.isChecked()) {
                        cardTypeList.add(RFCardType.M0CARD);
                    }
                    if (checkBoxFelicaCard.isChecked()) {
                        cardTypeList.add(RFCardType.FELICA_CARD);
                    }

                    EditText editTimeout = view.findViewById(R.id.edit_rf_power_on_timeout);
                    if (editTimeout.getText().toString() != null && editTimeout.getText().toString().length() > 0) {
                        timeout = Integer.valueOf(editTimeout.getText().toString());
                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //同步接口，不要在主线程调用
                                RFResult rfResult = rfCardModule.powerOn(cardTypeList.toArray(new RFCardType[cardTypeList.size()]), timeout, rfCardPowerOnExtParams);
                                if(rfResult==null){
                                    showMessage("寻卡结果空" , MessageTag.DATA);
                                    return;
                                }
                                if (rfResult != null && rfResult.getRfcardType() != null) {
                                    showMessage("寻卡上电成功，卡类型：" + rfResult.getRfcardType(), MessageTag.DATA);
                                } else {
                                    showMessage("寻卡上电失败", MessageTag.ERROR);
                                    return;
                                }

                                if (rfResult.getSNR() == null) {
                                    showMessage( "卡序列号空", MessageTag.DATA);
                                } else {
                                    snr = ISOUtils.hexString(rfResult.getSNR());
                                    showMessage("卡序列号：" + ISOUtils.hexString(rfResult.getSNR()) + "\r\n", MessageTag.DATA);
                                }

                                if (rfResult.getATQA() == null) {
                                    showMessage("ATQA:", MessageTag.DATA);
                                } else {
                                    showMessage("ATQA:" + Dump.getHexDump(rfResult.getATQA()) + "\r\n", MessageTag.DATA);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                if((""+e).contains("ErrCode:-10005")){//寻卡取消
                                    showMessage("寻卡取消完成",MessageTag.NORMAL);
                                }else{
                                    showMessage("寻卡失败，"+e,MessageTag.ERROR);
                                }
                            }

                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage("寻卡异常："+e,MessageTag.ERROR);
                }
            }
        });
    }


    @MethodAno(name = "取消寻卡", numId = 1)
    public void cancelCardReader(MethodBean bean) {
        rfCardModule.powerOff();
    }

    @MethodAno(name = "卡片是否在位", numId = 2)
    public void rfcardIsInducted() {
        try {
            boolean isExit = rfCardModule.isCardExist();
            if (isExit) {
                showMessage( "卡在位", MessageTag.DATA);
            } else {
                showMessage("卡不在位", MessageTag.DATA);
            }
        } catch (Exception e) {
            showMessage( "卡在检测异常："+e, MessageTag.ERROR);
        }
    }

    @MethodAno(name = "A卡APDU通讯", numId = 3)
    private void rfcardCommunication() {
        DialogUtils dialogUtils = DialogUtils.getInstance();
        dialogUtils.createCustomDialog(getContext(), "A卡APDU通讯", null, R.layout.dialog_iccard_communication, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if(id == -1){//cancel
                        return;
                    }
                    EditText edtText = dialogView.findViewById(R.id.edit_ICCardSend);
                    String str = edtText.getText().toString();//Get communication data
                    byte req[] = ISOUtils.hex2byte(str);
                    byte result[] = rfCardModule.transmit(req, 60);
                    showMessage("APDU请求命令：" + (req==null?null:ISOUtils.hexString(req)), MessageTag.DATA);
                    showMessage("响应数据：" + (result==null?null:ISOUtils.hexString(result)), MessageTag.DATA);
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage( "APDU异常："+e, MessageTag.ERROR);
                }
            }
        });
    }
    @MethodAno(name = "M1卡认证", numId = 4)
    private void m1Athenticate() {
        DialogUtils dialogUtils = DialogUtils.getInstance();
        String[] items = new String[]{"KEYA_0x60", "KEYA_0x00", "KEYB_0x61", "KEYB_0x01"};
        dialogUtils.createCustomDialog(getContext(), "M1卡认证", items, R.layout.dialog_m1_external_auth, new DialogUtils.CustomDialogCallback() {

            @Override
            public void onResult(int id, View dialogView) {
                RFKeyMode qpKeyMode = RFKeyMode.KEYA_0X60;
                if (id >= 0) {
                    if (id == 0) {
                        qpKeyMode = RFKeyMode.KEYA_0X60;
                        showMessage("KEYA_0X60", MessageTag.DATA);
                    } else if (id == 1) {
                        showMessage("KEYA_0X00", MessageTag.DATA);
                        qpKeyMode = RFKeyMode.KEYA_0X00;
                    } else if (id == 2) {
                        showMessage("KEYB_0X61", MessageTag.DATA);
                        qpKeyMode = RFKeyMode.KEYB_0X61;
                    } else {
                        showMessage("KEYB_0X01", MessageTag.DATA);
                        qpKeyMode = RFKeyMode.KEYB_0X01;
                    }

                    EditText edtBlockNum = dialogView.findViewById(R.id.edit_qccard_block);
                    EditText edtKey = dialogView.findViewById(R.id.edit_qccard_key);

                    int block = Integer.valueOf(edtBlockNum.getText().toString());
                    byte sn[] = null;

                    if (snr != null) {
                        sn = ISOUtils.hex2byte(snr);
                    } else {
                        showMessage("卡序列号为空，请先寻卡上电", MessageTag.ERROR);
                        return;
                    }
                    byte key[] = ISOUtils.hex2byte(edtKey.getText().toString());
                    if (block >= 0 && block <= 255 && key.length == 6 && sn != null && sn.length == 4) {
                        try {
                            boolean isSucess = rfCardModule.m1Authenticate(qpKeyMode, sn, block, key);
                            showMessage("M1卡密钥认证结果：" + isSucess, MessageTag.NORMAL);
                            showMessage("M1卡密钥认证密钥类型：" + qpKeyMode + "\r\n", MessageTag.DATA);
                            showMessage("M1卡卡序列号："  + (snr == null ? "null" : ISOUtils.hexString(sn)) + "\r\n", MessageTag.DATA);
                            showMessage("M1卡认证的块号"  + block + "\r\n", MessageTag.DATA);
                            showMessage("M1卡密钥认证密钥：" + (key == null ? "null" : ISOUtils.hexString(key)) + "\r\n", MessageTag.DATA);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showMessage( "M1卡密钥认证异常："+e, MessageTag.ERROR);
                        }
                    } else {
                        showMessage("参数错", MessageTag.ERROR);
                    }
                } else {
                    showMessage("取消操作", MessageTag.DATA);
                }

            }
        });
    }

    @MethodAno(name = "M1卡写操作", numId = 5)
    public void m1CardWrite() {
        DialogUtils dialogUtils = DialogUtils.getInstance();
        dialogUtils.createCustomDialog(getContext(), "M1卡写操作", null, R.layout.dialog_m1_write, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if(id == -1){//cancel
                        return;
                    }
                    EditText edit_qccard_block = (EditText) dialogView.findViewById(R.id.edit_qccard_block);
                    EditText edit_qccard_data = (EditText) dialogView.findViewById(R.id.edit_qccard_data);
                    int block = Integer.valueOf(edit_qccard_block.getText().toString());
                    byte input[] = ISOUtils.hex2byte(edit_qccard_data.getText().toString());
                    if (block >= 0 && block <= 255 && input.length == 16) {
                        boolean result = rfCardModule.m1WriteBlockData(block, input);
                        showMessage("M1卡写结果：" +result, MessageTag.NORMAL);
                        showMessage("M1卡写入的块号：" + block + "\r\n", MessageTag.DATA);
                        showMessage("M1卡写入的数据：" + (input == null ? "null" : ISOUtils.hexString(input)) + "\r\n", MessageTag.DATA);

                    } else {
                        showMessage("参数错", MessageTag.ERROR);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage( "写异常："+e, MessageTag.ERROR);
                }
            }
        });
    }

    @MethodAno(name = "M1卡读操作", numId = 6)
    private void m1CardRead() {
        TextView tip = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edittext, null).findViewById(R.id.textview_tip);
        tip.setText("块号");
        DialogUtils dialogUtils = DialogUtils.getInstance();
        dialogUtils.createCustomDialog(getContext(), "M1卡读操作", null, R.layout.dialog_edittext, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if(id == -1){//cancel
                        return;
                    }
                    EditText editTextData = dialogView.findViewById(R.id.edit_data);
                    int block = Integer.valueOf(editTextData.getText().toString());
                    if (block >= 0 && block <= 255) {
                        byte output[] = rfCardModule.m1ReadBlockData(block);

                        showMessage("读取的块号：" + block + "\r\n", MessageTag.DATA);
                        showMessage("读取的数据：" + (output == null ? "null" : ISOUtils.hexString(output)) + "\r\n", MessageTag.DATA);
                    } else {
                        showMessage( "参数错", MessageTag.ERROR);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage( "读取异常："+e, MessageTag.ERROR);
                }

            }
        });
    }

    @MethodAno(name = "M1卡增量操作", numId = 7)
    private void m1CardIncrease() {
        DialogUtils dialogUtils = DialogUtils.getInstance();
        dialogUtils.createCustomDialog(getContext(), "增量操作", null, R.layout.dialog_m1_operate, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if(id == -1){//cancel
                        return;
                    }
                    EditText edit_qccard_block = (EditText) dialogView.findViewById(R.id.edit_m1_block);
                    EditText edit_qccard_data = (EditText) dialogView.findViewById(R.id.edit_m1_data);
                    int block = Integer.valueOf(edit_qccard_block.getText().toString());
                    byte input[] = ISOUtils.hex2byte(edit_qccard_data.getText().toString());
                    if (block >= 0 && block <= 255 && input.length == 4) {
                        boolean result = rfCardModule.m1Increment(block, input);
                        showMessage("增量操作结果："+ result, MessageTag.NORMAL);
                        showMessage("增量操作块号" + block + "\r\n", MessageTag.DATA);
                        showMessage("增量操作数据" + input == null ? "null" : ISOUtils.hexString(input) + "\r\n", MessageTag.DATA);
                    } else {
                        showMessage( "参数错", MessageTag.ERROR);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage( "增量操作异常："+e, MessageTag.ERROR);
                }
            }
        });
    }

    @MethodAno(name = "M1卡减量操作", numId = 8)
    private void m1CardDecrease() {
        DialogUtils dialogUtils = DialogUtils.getInstance();
        dialogUtils.createCustomDialog(getContext(), "M1卡减量操作", null, R.layout.dialog_m1_operate, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if(id == -1){//cancel
                        return;
                    }
                    EditText edit_qccard_block = (EditText) dialogView.findViewById(R.id.edit_m1_block);
                    EditText edit_qccard_data = (EditText) dialogView.findViewById(R.id.edit_m1_data);
                    int block = Integer.valueOf(edit_qccard_block.getText().toString());
                    byte input[] = ISOUtils.hex2byte(edit_qccard_data.getText().toString());
                    if (block >= 0 && block <= 255 && input.length == 4) {
                        boolean result = rfCardModule.m1Decrement(block, input);
                        showMessage("减量操作结果：" +result, MessageTag.NORMAL);
                        showMessage("减量操作块号：" + block + "\r\n", MessageTag.DATA);
                        showMessage("减量操作数据"+(input == null ? "null" : ISOUtils.hexString(input) ), MessageTag.DATA);
                    } else {
                        showMessage("参数错", MessageTag.ERROR);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage("减量操作异常："+e, MessageTag.ERROR);

                }
            }
        });
    }



}
