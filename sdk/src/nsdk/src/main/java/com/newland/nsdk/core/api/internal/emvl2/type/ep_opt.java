package com.newland.nsdk.core.api.internal.emvl2.type;

public class ep_opt {
    public byte ucTransType;
    public int emSeqTo;
    public int emSeqStart;
    public int nRequestAmt;
    public byte ucCardNo;
    public byte ucRestart;
    public int nForceOnlineEnable;
    public int nAccountTypeEnable;
    public byte[] pusOnlinePin = new byte[12];
    public byte[] pusIssScriptRes = new byte[100];
    public int nIssSresLen;
    public int nAdviceReq;
    public int nForceAcceptSupported;
    public int nSignatureReq;
    public byte[] pusAuthRespCode = new byte[2];
    public byte[] pusField55 = new byte[1500];
    public int nField55Len;
    public int nOnlineResult;
    public int nTransRet;

    /*********************Entry Point Data***************************/
    /*****User Interface Request Data --DF8116 Len=23*****/
    public byte _UI_message_id;
    public byte _UI_status;
    public byte[] _UI_hold_time = new byte[3];
    public byte _UI_language_len;
    public byte[] _UI_language_preference = new byte[8];
    public byte _UI_value_qualifier;
    public byte[] _UI_value = new byte[6];
    public byte[] _UI_currency_code = new byte[2];

    /*****Outcome Parameter Set --DF8129 maybe can use op_set[8]*****/
    public byte _OP_status;
    public byte _OP_start;
    public byte _OP_online_response_data;
    public byte _OP_cvm;
    public byte _OP_ui_request_on_outcome_present;
    public byte _OP_ui_request_on_restart_present;
    public byte _OP_data_record_present;
    public byte _OP_discretionary_data_present;
    public byte _OP_receipt;
    public byte _OP_alternate_interface_preference;
    public byte _OP_field_off_request;
    public byte _OP_removal_timeout;

    /*****'FF8106'  10  Discretionary Data 'DF8115'  6  Error Indication  *****/
    public byte _ER_L1_indication;
    public byte _ER_L2_indication;
    public byte _ER_L3_indication;
    public byte _ER_SW1;
    public byte _ER_SW2;
    public byte _ER_MSG_ON_ERROR;

    /**
     * paywave for refund transaction request AAC. TC-0X40 ARQC-0X80 AC-OTHER
     */
    public byte _refund_request_aac;
    /**
     * control bits. 0x01 = CB implementation of PSE with selection of kernel based on DF61 if 9F2A is missing
     */
    public byte ucCtrl;
    /**
     * rupay used, whether the force online option opened
     */
    public byte ucForceBypssPinEnable;
    public byte[] _rfu = new byte[6];


}

