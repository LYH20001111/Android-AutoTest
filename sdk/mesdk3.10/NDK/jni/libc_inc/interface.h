#ifndef __INTERFACE__
#define __INTERFACE__

#define   MAX_DDOL_LEN                        (252)     /**< 动态数据对象列表(DDOL)最大长度 */
#define   MAX_TDOL_LEN                        (252)     /**< 交易证书对象列表(TDOL)最大长度 */

/*
 * emvparam._status 
 * 标识本次入网测试项目的类别,
 * 执行交易前，必须赋值标识测试交易类型
 * 中国境内使用默认置PBOC2_ENB, 无特殊要求置0 
 */
#define   BCTC_TEST_ENB                       (0x01)    /**< 中国银行卡检测中心 */
#define   PBOC2_ENB                           (0x02)    /**< PBOC入网 */
#define   VISA_ENB                            (0x04)    /**< visa入网 */
#define   MASTERCARD_ENB                      (0x08)    /**< master card入网 */
#define   JCB_ENB                             (0x10)    /**< jcb入网 */
#define   AMEX_ENB                            (0x20)    /**< amex入网 */  

/*
 * @struct STEMVPARAM EMV终端参数
 * support = 1, not support = 0;no used set 0
 */
typedef struct {
    unsigned char _tac_default[5];                /**< 缺省终端行为代码default Terminal Action Code */                             
    unsigned char _tac_denial[5];                 /**< 拒绝终端行为代码denial Terminal Action Code */                              
    unsigned char _tac_online[5];                 /**< 联机终端行为代码online Terminal Action Code */                              
    unsigned char _target_percent;                /**< 目标百分数target percent */                                                 
    unsigned char _max_target_percent;            /**< 最大目标百分数max target percent */                                         
    unsigned char _threshold_value[4];            /**< 阀值thresold value */                                                       
    unsigned char _trans_ref_conv[4];             /**< 交易参考货币兑换 transaction reference currency convert, default 0 */       
    unsigned char _script_dev_limit;              /**< script length limit ,default 0 */                                           
    unsigned char _ics[7];                        /**< ICS (Implementation Comformance Statement)每个bit表示一个设置               
                                                       具体见以下[emvparam._ics   ICS相关位]宏定义                                 
                                                       e.g.:设置支持持卡人认证                                                     
                                                           ics_opt_set( AS_Support_CardHolder_Confirm, emvparm._ics)               
                                                           不支持持卡人认证                                                        
                                                           ics_opt_unset( AS_Support_CardHolder_Confirm, emvparm._ics) */          
    unsigned char _status;                        /**< Test type indicator*/                                                       
    unsigned char _ec_indicator;                  /**< 电子现金标识 _EMV_TAG_9F7A_TM_SUPPEC */                                     
    unsigned char _type;                          /**< 终端类型 _EMV_TAG_9F35_TM_TERMTYPE */                                       
    unsigned char _cap[3];                        /**< 终端能力 _EMV_TAG_9F33_TM_CAP */                                            
    unsigned char _add_cap[5];                    /**< 额外终端能力, _EMV_TAG_9F40_TM_CAP_AD */                                    
    unsigned char _aid[16];                       /**< 应用标识, IC卡和终端不一样4F(ICC), 9F06(Terminal), b, 5-16 bytes */         
    unsigned char _aid_len;                       /**< 应用标识长度, length of AID*/                                               
    unsigned char _app_ver[2];                    /**< 终端应用版本, _EMV_TAG_9F09_TM_APPVERNO */                                  
    unsigned char _pos_entry;                     /**< POS接入模式, 参见_EMV_TAG_9F39_TM_POSENTMODE */                             
    unsigned char _floorlimit[4];                 /**< 终端交易限额, 参见_EMV_TAG_9F1B_TM_FLOORLMT */                              
    unsigned char _acq_id[6];                     /**< 收单行标识, 参见_EMV_TAG_9F01_TM_ACQID */                                   
    unsigned char _mer_category_code[2];          /**< 终端类型码, 参见_EMV_TAG_9F15_TM_MCHCATCODE */                              
    unsigned char _merchant_id[15];               /**< 终端商户ID, 参见_EMV_TAG_9F16_TM_MCHID */                                   
    unsigned char _trans_curr_code[2];            /**< 交易货币代码, 参见_EMV_TAG_5F2A_TM_CURCODE */                               
    unsigned char _trans_curr_exp;                /**< 交易货币指数, 参见_EMV_TAG_5F36_TM_CUREXP */                                
    unsigned char _trans_ref_curr_code[2];        /**< 交易参考货币代码, 参见_EMV_TAG_9F3C_TM_REFCURCODE */                        
    unsigned char _trans_ref_curr_exp;            /**< 交易参考货币指数, 参见_EMV_TAG_9F3D_TM_REFCUREXP */                         
    unsigned char _term_country_code[2];          /**< 终端国家码, 参见_EMV_TAG_9F1A_TM_CNTRYCODE */                               
    unsigned char _ifd_serial_num[8];             /**< 接口设备序列号, _EMV_TAG_9F1E_TM_IFDSN */                                   
    unsigned char _terminal_id[8];                /**< 终端标识符, _EMV_TAG_9F1C_TM_TERMID*/                                       
    unsigned char _default_ddol_len;              /**< the length of the default ddol following the merchant name and location */  
    unsigned char _default_tdol_len;              /**< the length of the default todl following the default ddol */                
    unsigned char _default_ddol[MAX_DDOL_LEN];    /**< default ddol */
/**< linld要求商户名称20字节空间，考虑到实际上TDOL应不会达到极限252字节情况，预留20字节出来*/                                                             
    unsigned char _default_tdol[MAX_TDOL_LEN - 20];    /**< default tdol */
    unsigned char _merchant_name[20];		  /**< 商户名称20字节*/                                                         
    unsigned char _app_sel_indicator;             /**< 是否支持部分应用选择符匹配 application select indicator                     
                                                       when ics support and this byte is 0x01(!0)                                  
                                                       application select support partial match */                                 
    unsigned char _fallback_posentry;             /**< 回退pos入口 fallback pos entry */                                           
    unsigned char _limit_exist;                   /**< limist exist?(判断以下限额是否存在的标识)                                   
                                                       bit 1    =1    EC limint exsit                                              
                                                       bit 2    =1    contactless limit exsit  非接                                
                                                       bit 3    =1    contactless offline limit exsit 非接脱机                     
                                                       bit 4    =1    cvm limit  exsit CVM限额 */                                  
    unsigned char _ec_limit[6];                   /**< 电子现金终端限额 _EMV_TAG_9F7B_TM_EC_LMT_IN */                              
    unsigned char _cl_limit[6];                   /**< 非接触终端交易限额 n12  6bytes */                                           
    unsigned char _cl_offline_limit[6];           /**< 非接触终端脱机最低限额n12  6bytes */                                        
    unsigned char _cvm_limit[6];                  /**< 终端执行CVM限额    n12  6bytes */                                           
    unsigned char _trans_prop[4];                 /**< 终端交易属性 _EMV_TAG_9F66_TM_TRANSPTY_IN */                                
    unsigned char _status_check;                  /**< 非接触状态检查默认为0*/                                                     
    unsigned char _appid;                         /**< 应用标识表示该应用支持哪些交易                                              
                                                       应用终端配置表示终端支持                                                    
                                                       (为保持兼容,全0表示支持所有) */                                             
    unsigned char _resv[2];                       /**< 补齐4字节*/  	
}emvparam;

/**
* @定义emvparam._appid  bit 相应的支持应用位
*/
#define   EMV_APPID_PBOC               (0x20)     /**< AID支持PBOC */
#define   EMV_APPID_UPCARD             (0x08)
#define   EMV_APPID_QPBOC              (0x04)
#define   EMV_APPID_MSD                (0x02)
#define   EMV_APPID_EC                 (0x01)

/**	
* x为:emvparam._limit_exist
* 用于判断终端参数是否定义相应的限额。
* 1      表示存在
* 0      不存在
*/
#define   EC_LIMIT_EXIST(x)            ((x) & 0x01)     /**< 电子现金限额是否存在 */
#define   CL_LIMIT_EXIST(x)            ((x) & 0x02)     /**< 非接触终端交易限额是否存在 */
#define   CLOFFLINE_LIMIT_EXIST(x)     ((x) & 0x04)     /**< 非接触终端脱机最低限额是否存 */
#define   CVM_LIMIT_EXIST(x)           ((x) & 0x08)     /**< 终端执行CVM限额是否存在 */

/*	AID侯选列表结构体,进行侯选列表选择时用到*/
/**
* @struct AID侯选列表结构体
* @detail 进行侯选列表选择时用到
*/
typedef struct {
    unsigned char _aid[16];                  /**< AID应用标识符 */                                                        
    unsigned char _aid_len;                  /**< length of AID */                                                        
    unsigned char _lable[20];                /**< 应用标签                                                                
                                                  50(ICC), ans, 1-16 bytes, we prepare 20 for some PBOC2 errors */        
    unsigned char _lable_len;                /**< length of lable*/                                                       
    unsigned char _preferred_name[20];       /**< 应用优先名9F12(ICC), ans, 1-16 bytes */                                 
    unsigned char _preferred_name_len;       /**< length of preferred name */                                             
    unsigned char _priority;                 /**< 应用优先权标识符87(ICC), b, 1 bytes */                                  
    unsigned char  _enable;                  /**< indicate whether the candidate is enabled                               
                                                  0    去除                                                               
                                                  1    可用 */                                                            
    unsigned char _flag;                     /**< 非接触限额置位标识*/                                                    
    unsigned char _resv[2];                  /**< reserve bytes */                                                        
    int           _file_offset;              /**< the offset of this AID in the parameters file */                         
}candidate;

/**
 * Self-defined tags for internal usage.
 * These tag names are just defined according to
 * current hash value spread. So if the hash
 * function changes in emvbuf.c leading to
 * a different spread, these tag names may
 * be changed too.
 * Always use these macros instead of
 * any real integer.
 * (DF31 后台工具表示脚本处理结果)
 * PBOC Tag:	DF4X	(except DF41 后台工具表示强制接受)
 * EC Tag:	DF5X	(except DF51 后台工具表示联机PIN)
 * QPBOC Tag:	DF6X	()
 * Paypass Tag:DF7X	()
 */
#define   EMV_APPID_TAG                (0xDF42)         /**< AID._appid  */
#define   CURR_DATE_TAG                (0xDF43)         /**< 当前交易时间4 bytes:YYYYMMDD */
#define   DEF_DDOL_TAG                 (0xDF44)         /**< 默认动态数据对象列表(DDOL)标签 */
#define   DEF_TDOL_TAG                 (0xDF45)         /**< 交易证书数据对象列表(TDOL)标签 */
#define   PDOL_BUF_TAG                 (0xDF46)         /**< 默认处理选项数据对象列表(PDOL)标签 */
#define   CDOL1_BUF_TAG                (0xDF47)         /**< 卡风险管理数据对象列表1(CDOL1)标签 */
#define   CDOL2_BUF_TAG                (0xDF48)         /**< 卡风险管理数据对象列表2(CDOL2)标签 */
#define   AUTH_DATA                    (0xDF49)         /**< 静态认证数据标签 */
#define   ISSUER_PK_TAG                (0xDF4A)         /**< 发卡行公钥标签 */
#define   ICC_PK_TAG                   (0xDF4B)         /**< IC卡公钥标签 */
#define   SCRIPT_REST_TAG              (0xDF4C)         /**< 发卡行脚本执行结果标签 */
#define   ONLINE_PIN_TAG               (0xDF4D)         /**< 联机PIN标签 */
#define   SCRIPT_REST_TAG2             (0xDF31)         /**< 应用部要求同时保存脚本结果到DF31方便应用 */
//#define   TRACK2_DATA                (0xDF4E)         /**< 二磁道数据标签 */
#define   CARD_EXPIRE_DATE             (0xDF4F)         /**< 卡片过期时间标签(二磁道数据里的) */
#define   EC_SELECT_RESULT             (0xDF53)         /**< 借贷记+ EC的卡片选择是否进行EC结果 */
#define   EC_ONLINE_PIN_TAG            (0xDF54)         /**< 电子现金联机PIN标签(低于阈值时) */
#define   CTTA_TAG                     (0xDF55)         /**< 累计交易总金额标签 */
#define   CTTAL_TAG                    (0xDF56)         /**< 累计交易总金额限制标签 */
#define   ONLINE_PIN_REQUEST           (0xDF57)         /**< 卡片请求联机PIN时返回的终端性能中联机PIN功能值 标签 */
//#define QPBOC_NOTRYIC                (0xDF62)         /**< QPBOC不进行转向IC交易(BCTC test) */

/**********************************emvparam._ics   ICS相关位 ************************************************/
/*
 * AS   : Application Selection
 * Macro:
   AS_Support_PSE                 : Support PSE selection method
   AS_Support_CardHolder_Confirm  : Support Cardholder confirmation
   AS_Support_Prefferd_Order      : Have a preferred order of displaying applications
   AS_Support_Partial_AID         : Does the terminal perform partial AID selection
   AS_Support_Multi_Language      : Does the terminal have multi language support
   AS_Support_Common_Charset      : Does the terminal support Common Character Set as
                                    defined in "Annex B table 20 Book 4"

 * EMV 4.1 ICS Version 3.9 Level2
 */
#define AS_Support_PSE                     (0x0080)
#define AS_Support_CardHolder_Confirm      (0x0040)
#define AS_Support_Preferred_Order         (0x0020)
#define AS_Support_Partial_AID             (0x0010)
#define AS_Support_Multi_Language          (0x0008)
#define AS_Support_Common_Charset          (0x0004)

/*
 * DA   : Data Authentication
 * IPKC : Issuer Public Key Certificate
 * CAPK : Certification Authority Public Key
 * Macro:
 	 DA_Support_IPKC_Revoc_Check      : During DA, does the terminal check the revocation of IPKC
 	 DA_Support_Default_DDOL          : Does the terminal contain a default DDOL
 	 DA_Support_CAPKLoad_Fail_Action  : Is operation action required when loading CAPK fails
 	 DA_Support_CAPK_Checksum         : Is CAPK verified with CAPK checksum

 * EMV 4.1 ICS Version 3.9 Level2
 */
#define DA_Support_IPKC_Revoc_Check        (0x0180)
#define DA_Support_Default_DDOL            (0x0140)
#define DA_Support_CAPKLoad_Fail_Action    (0x0120)
#define DA_Support_CAPK_Checksum           (0x0110)

/*
 * CV   : Cardholder Verification
 * CVM   : Cardholder Verification Methods
 * Macro:
 	 CV_Support_Bypass_PIN          : Terminal supports bypass PIN entry
 	 CV_Support_PIN_Try_Counter     : Terminal supports Get Data for PIN Try Counter
 	 CV_Support_Fail_CVM            : Terminal supports Fail CVM
 	 CV_Support_Amounts_before_CVM  : Are amounts known before CVM processing

 * EMV 4.1 ICS Version 3.9 Level2
 */
#define CV_Support_Bypass_PIN              (0x0280)
#define CV_Support_PIN_Try_Counter         (0x0240)
#define CV_Support_Fail_CVM                (0x0220)
#define CV_Support_Amounts_before_CVM      (0x0210)
#define CV_Support_Bypass_ALL_PIN          (0x0208)

/*
 * TRM  : Terminal Risk Management
 * Macro:
   TRM_Support_FloorLimit     : Floor Limit Checking,
                                Mandatory for terminal with offline capability
   TRM_Support_RandomSelect   : Random Transaction Selections,
                                Mandatory for offline terminal with online capability,
                                except when cardholder controlled
   TRM_Support_VelocityCheck  : Velocity checking,
                                Mandatory for for terminal with offline capability
   TRM_Support_TransLog       : Support transaction log
   TRM_Support_ExceptionFile  : Support exception file
   TRM_Support_AIPBased       : Performance of TRM based on AIP setting
   TRM_Use_EMV_LogPolicy      : EMV has a different log policy with PBOC2, marked here

 * EMV 4.1 ICS Version 3.9 Level2
 */
#define TRM_Support_FloorLimit            (0x0380)
#define TRM_Support_RandomSelect          (0x0340)
#define TRM_Support_VelocityCheck         (0x0320)
#define TRM_Support_TransLog              (0x0310)
#define TRM_Support_ExceptionFile         (0x0308)
#define TRM_Support_AIPBased              (0x0304)
#define TRM_Use_EMV_LogPolicy             (0x0302)

/*
 * TAA  : Terminal Action Analysis
 * (x)  : the var of struct STEMVCONFIG
 * TAC  : Terminal Action Codes
 * DAC  : Default Action Codes
 * Macro:
   TAA_Support_TAC                  : Does the terminal support Terminal Action Codes
   TAA_Support_DAC_before_1GenAC    : Does the terminal process DAC prior to first GenAC
   TAA_Support_DAC_after_1GenAC     : Does the terminal process DAC after first GenAC
   TAA_Support_Skip_DAC_OnlineFail  : Does the terminal skip DAC processing and automatically
                                      request an AAC when unable to go online
   TAA_Support_DAC_OnlineFail       : Does the terminal process DAC as normal
                                      when unable to go online
   TAA_Support_CDAFail_Detected     : Device capable of detecting CDA Failure before TAA
   TAA_Support_CDA_Always_in_ARQC   : CDA always requested in a first Gen AC, ARQC request
   TAA_Support_CDA_Never_in_ARQC    : CDA never requested in a first Gen AC, ARQC request
   TAA_Support_CDA_Alawys_in_2TC    : CDA always requested in a second Gen AC when successful
                                      host response is received, with TC request
   TAA_Support_CDA_Never_in_2TC     : CDA never requested in a second Gen AC when successful
                                      host response is received, with TC request
 * EMV 4.1 ICS Version 3.9 Level2
 */
#define TAA_Support_TAC                    (0x0480)
#define TAA_Support_DAC_before_1GenAC      (0x0440)
#define TAA_Support_DAC_after_1GenAC       (0x0420)
#define TAA_Support_Skip_DAC_OnlineFail    (0x0410)
#define TAA_Support_DAC_OnlineFail         (0x0408)
#define TAA_Support_CDAFail_Detected       (0x0404)
#define TAA_Support_CDA_Always_in_ARQC     (0x0402)
#define TAA_Support_CDA_Alawys_in_2TC      (0x0401)

/*
 * CP  : Completion Process
 * (x)  : the var of struct STEMVCONFIG
 * Macro:
   CP_Support_Force_Online         : Transaction forced Online capability
   CP_Support_Force_Accept         : Transaction forced Acceptance capability
   CP_Support_Advices              : Does the terminal support advices
   CP_Support_Issuer_VoiceRef      : Does the terminal support Issuer Initiated Voice Referrals
   CP_Support_Batch_Data_Capture   : Does the terminal support Batch Data Capture
   CP_Support_Online_Data_capture  : Does the terminal support Online Data Capture
   CP_Support_Default_TDOL         : Does the terminal support a default TDOL

 * EMV 4.1 ICS Version 3.9 Level2
 */
#define CP_Support_Force_Online            (0x0580)
#define CP_Support_Force_Accept            (0x0540)
#define CP_Support_Advices                 (0x0520)
#define CP_Support_Issuer_VoiceRef         (0x0510)
#define CP_Support_Batch_Data_Capture      (0x0508)
#define CP_Support_Online_Data_capture     (0x0504)
#define CP_Support_Default_TDOL            (0x0502)

/*
 * MISC : Miscellaneous
 * (x)  : the var of struct emvconfig
 * Macro:
   MISC_Support_Account_Select         : Does the terminal support account type selection
   MISC_Support_ISDL_Greater_than_128  : Is Issuer Script Device Limit greater than 128 bytes
   MISC_Support_Internal_Date_Mana     : Does the terminal support internal date management

 * EMV 4.1 ICS Version 3.9 Level2
 */
#define MISC_Support_Account_Select        (0x0680)
#define MISC_Support_ISDL_Greater_than_128 (0x0640)
#define MISC_Support_Internal_Date_Mana    (0x0620)

/*	
* 用于判断ICS支持的功能,	
* 1	   支持
* 0	   不支持
* nr   指以上ICS支持相关功能的宏定义:XXX_Support_XXX
* _ics 是传入的ICS指针
*/
#define ics_opt_get(nr, _ics) \
        ((_ics)[((unsigned int)(nr)) >> 8] & (((unsigned int)(nr)) & 0x00FF))
        
 /**< 设置ICS支持相关功能 */
#define ics_opt_set(nr, _ics) \
        ((_ics)[((unsigned int)(nr)) >> 8] |= (((unsigned int)(nr)) & 0x00FF))
        
 /**< 取消ICS支持相关功能 */
#define ics_opt_unset(nr, _ics) \
        ((_ics)[((unsigned int)(nr)) >> 8] &= ~(((unsigned int)(nr)) & 0x00FF))

/*
* RESV_Terminal_Present_DefaultTAC
* RESV_Terminal_Present_DenialTAC
* RESV_Terminal_Present_OnlineTAC: to indicate whether the terminal provide these TACs
* 供外部参数工具保留使用TAC的相关赋值
*/
#define   RESV_Terminal_Present_DefaultTAC    (0x0008)
#define   RESV_Terminal_Present_DenialTAC     (0x0004)
#define   RESV_Terminal_Present_OnlineTAC     (0x0002)

#define   _tac_status_get(nr, _status)        ics_opt_get(nr, _status)
#define   _tac_status_set(nr, _status)        ics_opt_set(nr, _status)
#define   _tac_status_unset(nr, _status)      ics_opt_unset(nr, _status)

/**
 * TT : Terminal Type(emvparam._type)
 */
#define   TT_Unattended(x)                    ((*(x) & 0x0F) > 3)        /**< 终端无人值守,如:ATM */
#define   TT_Attended(x)                      ((*(x) & 0x0F) < 4)        /**< 终端有人值守 */

/**
 * TC : Terminal Capabilities(emvparam._cap) 定义终端性能
 */
#define   TC_Manual_Key_Entry                 (0x0080)          /**< 手工键盘输入 */                                                
#define   TC_Magnetic_Stripe                  (0x0040)          /**< 磁条卡 */                                                      
#define   TC_IC_With_Contacts                 (0x0020)          /**< 接触式IC卡 */                                                  
#define   TC_Plaintext_PIN                    (0x0180)          /**< 明文PIN验证 */                                                 
#define   TC_Enciphered_PIN_Online            (0x0140)          /**< 联机密文PIN验证 */                                             
#define   TC_Signature_Paper                  (0x0120)          /**< 签名(纸质) */                                                  
#define   TC_Enciphered_PIN_Offline           (0x0110)          /**< 脱机密文PIN验证 */                                             
#define   TC_No_CVM_Required                  (0x0108)          /**< 无需CVM */                                                     
#define   TC_Cardholder_Cert                  (0x0101)          /**< 持卡人证件出示 */                                              
#define   TC_SDA                              (0x0280)          /**< 静态数据认证SDA */                                             
#define   TC_DDA                              (0x0240)          /**< 动态数据认证DDA */                                             
#define   TC_Card_Capture                     (0x0220)          /**< 吞卡 */                                                        
#define   TC_CDA                              (0x0208)          /**< 复合动态数据认证/应用密文生成CDA */ 

/**
 * 判断终端性能所支持的功能
 *  1     支持
 *  0     不支持
 * nr:    以上定义的终端各种功能
 * cap:   传入的终端性能指针
 */
#define   terminal_cap(nr, cap)               ics_opt_get(nr, cap)                                              
 /**< 设置终端性能支持相关功能 */              
#define   terminal_cap_set(nr, cap)           ics_opt_set(nr, cap)                                              
 /**< 设置终端性能不支持相关功能 */             
#define   terminal_cap_unset(nr, cap)         ics_opt_unset(nr, cap)                                              
/**< 终端是否支持离线PIN(明文+密文) */        
#define   terminal_offline_pin(cap)           (*(cap + 1) & 0x90)

/*
 * ATC : Additional Terminal Capabilities(emvparam._add_cap)		终端附加性能
 */
#define   ATC_Cash                            (0x0080)          /**< 现金 */
#define   ATC_Goods                           (0x0040)          /**< 货物 */
#define   ATC_Services                        (0x0020)          /**< 服务 */
#define   ATC_Cashback                        (0x0010)          /**< 返现 */
#define   ATC_Inquiry                         (0x0008)          /**< 查询 */
#define   ATC_Transfer                        (0x0004)          /**< 转账 */
#define   ATC_Payment                         (0x0002)          /**< 支付 */
#define   ATC_Administrative                  (0x0001)          /**< 管理 */
#define   ATC_Cash_Deposit                    (0x0180)          /**< 存款 */                                           
#define   ATC_Numeric_Keys                    (0x0280)          /**< 数字键 */
#define   ATC_Alphabetic_Special_Keys         (0x0240)          /**< 字母和特殊字符键 */
#define   ATC_Command_Keys                    (0x0220)          /**< 命令键 */
#define   ATC_Function_Keys                   (0x0210)          /**< 功能键 */                                           
#define   ATC_Print_Attendant                 (0x0380)          /**< 打印给服务员 */
#define   ATC_Print_Cardholder                (0x0340)          /**< 打印给持卡人 */
#define   ATC_Display_Attendant               (0x0320)          /**< 显示给服务员 */
#define   ATC_Display_Cardholder              (0x0310)          /**< 显示给持卡人 */                                           
#define   ATC_Code_Table_10                   (0x0302)          /**< 编码表10 */
#define   ATC_Code_Table_9                    (0x0301)          /**< 编码表9 */
#define   ATC_Code_Table_8                    (0x0480)          /**< 编码表8 */
#define   ATC_Code_Table_7                    (0x0440)          /**< 编码表7 */
#define   ATC_Code_Table_6                    (0x0420)          /**< 编码表6 */
#define   ATC_Code_Table_5                    (0x0410)          /**< 编码表5 */
#define   ATC_Code_Table_4                    (0x0408)          /**< 编码表4 */
#define   ATC_Code_Table_3                    (0x0404)          /**< 编码表3 */
#define   ATC_Code_Table_2                    (0x0402)          /**< 编码表2 */
#define   ATC_Code_Table_1                    (0x0401)          /**< 编码表1 */ 

/**
 * 判断终端附加性能
 *  1        支持
 *  0        不支持
 * nr:       以上终端附加性能位宏定义
 * addcap:   保存终端附加性能指针
 */
#define   add_terminal_cap(nr, addcap)        ics_opt_get(nr, addcap)
                                              
/**< 设置终端附加性能支持相关功能 */             
#define   add_terminal_cap_set(nr, addcap)    ics_opt_set(nr, addcap)
                                              
/**< 设置终端附加性能不支持相关功能 */           
#define   add_terminal_cap_unset(nr, addcap)  ics_opt_unset(nr, addcap)

/*    
  *终端交易属性9F66 (emvparam._trans_prop)
  */
#define   EMV_PROP_MSD                        (0x0080)          /**< 1:支持非接触磁条 (MSD) */
#define   EMV_PROP_PBOCCLSS                   (0x0040)          /**< 1:支持非接触PBOC */
#define   EMV_PROP_QPBOC                      (0x0020)          /**< 1:支持非接触qPBOC */
#define   EMV_PROP_PBOC                       (0x0010)          /**< 1:支持接触PBOC */
#define   EMV_PROP_OFFLINE_ONLY               (0x0008)          /**< 1:读写器仅支持脱机
                                                                     0:读写器具有联机能力 */
#define   EMV_PROP_ONLINEPIN                  (0x0004)          /**< 1:支持联机PIN */
#define   EMV_PROP_SIGNATURE                  (0x0002)          /**< 1:支持签名 */
/**< byte1 bit1 reserve */                                     
                                                               
/**< 以下宏及保留位默认设置为0 */                              
#define   EMV_PROP_ONLINEAC                   (0x0180)          /**< 要求联机密文 */
#define   EMV_PROP_CVM                        (0x0140)          /**< 要求CVM */
#define   EMV_PROP_01VERSUPPORT               (0x0380)          
/*other bits reserve */

/**
 * 获取终端交易属性
 *  1         支持
 *  0         不支持
 * nr:        以上终端交易属性位宏定义
 * transprop: 保存终端交易属性的指针
 */
#define   trans_prop(nr, transprop)           ics_opt_get(nr, transprop)
/**< 设置终端交易属性支持相关功能 */          
#define   trans_prop_set(nr, transprop)       ics_opt_set(nr, transprop)
/**< 设置终端交易属性不支持相关功能 */        
#define   trans_prop_unset(nr, transprop)     ics_opt_unset(nr, transprop)


/*=========================emv_opt 结构体及相关域=========================================*/
/*	emv_opt._seq_to (emv交易流程序列号)，表示要求emv执行到哪里结束，
	一般赋值EMV_PROC_CONTINUE，表示正确执行完emv流程*/
typedef enum {
    EMV_PROC_TO_APPSEL_INIT,                              /**< 应用选择初始化 */
    EMV_PROC_TO_READAPPDATA,                              /**< 读应用数据 */
    EMV_PROC_TO_OFFLINEAUTH,                              /**< 离线数据认证 */
    EMV_PROC_TO_RESTRITCT,                                /**< 处理限制 */
    EMV_PROC_TO_CV,                                       /**< 持卡人验证 */
    EMV_PROC_TO_RISKMANA,                                 /**< 终端风险管理 */
    EMV_PROC_TO_1GENAC,                                   /**< 第一次密文生成 */
    EMV_PROC_TO_2GENAC,                                   /**< 第二次密文生成 */
    EMV_PROC_CONTINUE                                     /**< PBOC交易继续 */
}emv_seq;

/**< emv_opt._trans_ret && emv_opt._online_result(交易结果返回值) */
/**< 非接触激活卡片失败错误值 */
#define   EMV_TRANS_CANCEL                 (-13)          /**< 交易取消 */
#define   EMV_TRANS_NOCARD                 (-12)          /**< 未出示卡片 */
#define   EMV_TRANS_MORECARD               (-11)          /**< 多张卡 */
#define   EMV_TRANS_FALLBACK                (-2)          /**< fallback */
#define   EMV_TRANS_TERMINATE               (-1)          /**< 交易中止 */
#define   EMV_TRANS_ACCEPT                   (1)          /**< 交易授受 */
#define   EMV_TRANS_DENIAL                   (2)          /**< 交易拒绝 */
#define   EMV_TRANS_GOONLINE                 (3)          /**< 联机 */
#define   EMV_TRANS_2GAC_AAC                 (4)          /**< 第二个Generate AC返回AAC */
#define   EMV_TRANS_ONLINEFAIL               (5)          /**< emv_opt._online_result联机失败 */
#define   EMV_TRANS_ONLINESUCC_ACCEPT        (6)          /**< emv_opt._online_result联机成功并授受交易 */
#define   EMV_TRANS_ONLINESUCC_DENIAL        (7)          /**< emv_opt._online_result联机成功并拒绝参考 */
#define   EMV_TRANS_ONLINESUCC_ISSREF        (8)          /**< emv_opt._online_result联机成功并返回参考 */
#define   EMV_TRANS_GOON_PBOC2LOG            (9)          /**< 成功获取PBOC2日志 */
#define   EMV_TRANS_GOON_ECLOADLOG          (10)          /**< 成功获取圈存日志 */     
//#define EMV_TRANS_EC_GOON_LOG             (11)          /**< 返回电子现金日志和PBOC2日志相同 */
#define   EMV_TRANS_EC_GOON_AMOUNT          (12)          /**< 成功获取EC余额 */
#define   EMV_TRANS_QPBOC_ACCEPT            (13)          /**< 非接触QPBOC交易接受 */
#define   EMV_TRANS_QPBOC_DENIAL            (14)          /**< 非接触QPBOC交易拒绝 */
#define   EMV_TRANS_QPBOC_GOONLINE          (15)          /**< 非接触QPBOC交易联机 */
#define   EMV_TRANS_MSD_GOONLINE            (16)          /**< 非接触MSD交易联机 */
#define   EMV_TRANS_RF_GOON_AMOUNT          (17)          /**< 成功获取QPBOC余额 */
#define   EMV_TRANS_RF_ACTIVECARD           (18)          /**< 请激活射频卡片 */    
#define   EMV_TRANS_SLECT_NEXTAID           (19)          /**< 请求下一个AID*/    
                                           
/**< emv_opt._trans_type(请求交易的类型) */
#define   EMV_TRANS_GOODS                 (0x01)          /**< 货物 */
#define   EMV_TRANS_SERVICES              (0x02)          /**< 服务 */
#define   EMV_TRANS_CASH                  (0x03)          /**< 现金 */
#define   EMV_TRANS_CASHBACK              (0x04)          /**< 返现 */
#define   EMV_TRANS_INQUIRY               (0x05)          /**< 查询 */
#define   EMV_TRANS_TRANFER               (0x06)          /**< 转账 */
#define   EMV_TRANS_ADMIN                 (0x07)          /**< 管理 */
#define   EMV_TRANS_CASHDEPOSIT           (0x08)          /**< 存款 */
#define   EMV_TRANS_PAYMENT               (0x09)          /**< 支付 */
#define   EMV_TRANS_PBOCLOG               (0x0A)          /**< 获取PBOC 或电子现金日志
                                                               (当EMV_Start()函数的返回值为
                                                       　      EMV_TRANS_GOON_PBOC2LOG时，才表示获
                                                       　      取日志成功，否则获取日志失败) */
#define   EMV_TRANS_SALE                  (0x0B)          /**< 消费 */
#define   EMV_TRANS_PREAUTH               (0x0C)          /**< 预授权 */
#define   EMV_TRANS_BALANCE               (0x0D)          /**< 余额 */
#define   EMV_TRANS_ECLOADLOG             (0x0E)          /**< 电子现金圈存日志 */           

/**< +++ 电子现金交易的类型+++ */
#define   EMV_TRANS_EC_GOODS              (EMV_TRANS_GOODS)         /**< 电子现金货物 */
#define   EMV_TRANS_EC_SERVICES           (EMV_TRANS_SERVICES)      /**< 电子现金服务 */
#define   EMV_TRANS_EC_SALE               (EMV_TRANS_SALE)          /**< 电子现金消费 */
#define   EMV_TRANS_EC_BINDLOAD           (0x21)                    /**< 电子现金指定账户圈存 */
#define   EMV_TRANS_EC_NOBINDLOAD         (0x22)                    /**< 电子现金非指定账户圈存 */
#define   EMV_TRANS_EC_CASHLOAD           (0x23)                    /**< 电子现金现金圈存 */
#define   EMV_TRANS_EC_UPLOAD             (0x24)                    /**< 电子现金圈提(暂未实现) */
#define   EMV_TRANS_EC_INQUIRE_LOG        (EMV_TRANS_PBOCLOG)       /**< 电子现金日志(和PBOC日志一样) */
#define   EMV_TRANS_EC_INQUIRE_AMOUNT     (0x25)                    /**< 电子现金余额查询
                                                       　                (当EMV_start( )函数返回为:
                                                       　                TRANS_EC_GOON_AMOUNT, 才表示获
                                                       　                取余额成功，否则失败) */
#define   EMV_TRANS_EC_CASHLOAD_VOID      (0x26)                    /**< 电子现金现金圈存撤销 */

/**< +++QPBOC /MSD 交易的类型+++ */
#define   EMV_TRANS_RF_START              (0x30)                    /**< *<只做标识不做交易类型 */
#define   EMV_TRANS_RF_GOODS              (EMV_TRANS_GOODS)         /**< QPBOC/MSD货物 */
#define   EMV_TRANS_RF_SERVICES           (EMV_TRANS_SERVICES)      /**< QPBOC/MSD服务 */
#define   EMV_TRANS_RF_SALE               (EMV_TRANS_SALE)          /**< QPBOC/MSD消费 */
#define   EMV_TRANS_RF_BINDLOAD           (0x31)                    /**< 非接指定账户圈存 */
#define   EMV_TRANS_RF_NOBINDLOAD         (0x32)                    /**< 非接现金非指定账户圈存 */
#define   EMV_TRANS_RF_CASHLOAD           (0x33)                    /**< 非接现金现金圈存 */
#define   EMV_TRANS_RF_INQUIRE_AMOUNT     (0x34)                    /**< QPBOC余额查询
                                                       　                (当EMV_rf_start( )函数返回为:
                                                       　                TRANS_RF_GOON_AMOUNT, 才表示获
                                                       　                取余额成功(并可以通过EMV_getdata获取
                                                                         9F77电子现金余额上限)，否则失败) */
#define   EMV_TRANS_RF_UPLOAD             (0x35)                    /**< 非接现金圈提(暂未实现) */
#define   EMV_TRANS_RF_CASHLOAD_VOID      (0x36)                    /**< 非接现金现金圈存撤销 */
#define   EMV_TRANS_RF_PBOCLOG            (0x37)                    /**< 非接取PBOC明细 */
#define   EMV_TRANS_RF_UPTCARDINFO        (0x38)                    /**< 卡片信息写入 */
#define   EMV_TRANS_RF_PBOC_SALE          (0x39)         
#define   EMV_TRANS_RF_ECLOADLOG          (0x40)                    /**< 非接取圈存明细 */
/**< 定义STEMVOPTION.nRequestAmount 宏:何时输入金额 */
#define   EMV_TRANS_REQAMT_NO             (0)                       /**< 不输入金额 */
#define   EMV_TRANS_REQAMT_APS            (1)                       /**< 应用选择时输入 */
#define   EMV_TRANS_REQAMT_DDA            (2)                       /**< 数据认证时输入 */
#define   EMV_TRANS_REQAMT_RFPRECESS      (3)                       /**< 射频卡:预处理时输入 */

/*	EMV交易选项结构体*/
typedef struct {
    unsigned char  _trans_type;              /**< in, transaction type, see above */                                      
    emv_seq        _seq_to;                  /**< in, when to terminate the session */                         
    int            _request_amt;             /**< in, whether to request the the amount, before PAN            
                                                  具体见_request_amt 宏定义 */  
    /**< if the terminal ICS support the below 3 options */   
    int            _force_online_enable;     /**< in, whether the force online option opened */                
    int            _account_type_enable;     /**< in, whether the account type selection opened */             
    unsigned char* _online_pin;              /**< out, string with '\0' if online pin is entered */            
    unsigned char* _iss_script_res;          /**< out, if issuer script result exists */                       
    int            _iss_sres_len;                                                                              
    int            _advice_req;              /**< out, if advice is required (must be supported by ics) */     
    int            _force_accept_supported;  /**< out, if ICS support it */                                    
    int            _signature_req;           /**< out, if the CVM finally request a signature */ 
    unsigned char* _auth_resp_code;          /**< in, 8A from the host */                             
    unsigned char* _field55;                 /**< in, field55 or tlv decoded data from the host */    
    int            _field55_len;                                                                      
    int            _online_result;           /**< in, the online result */                            
    int            _trans_ret;               /**< transaction return */                               
}emv_opt;

/*
 * EMV Files name structure
 */
#define   EMV_PATH_NAME_MAX             (100)          /**< EMV最长路径名 */
#define   EMV_FILE_NAME_MAX             (20)          /**< EMV最长文件名 */

/**
* @struct  emv_file Emv文件路径和各文件名(EMV Files name structure)
*/
typedef struct {
    char  _file_path[EMV_PATH_NAME_MAX];                   /**< 文件存放路径 */
    char  _conf_name[EMV_FILE_NAME_MAX];                   /**< 终端配置参数文件名 */
    char  _capk_name[EMV_FILE_NAME_MAX];                   /**< 公钥文件名 */
    char  _card_blk[EMV_FILE_NAME_MAX];                    /**< 卡黑名单文件名 */
    char  _cert_blk[EMV_FILE_NAME_MAX];                    /**< 证书黑名单文件名 */
    char  _emv_log[EMV_FILE_NAME_MAX];                     /**< 保存EMV 交易金额LOG文件名 */
}emv_file;


/**< 定义IC卡读写时cardno的宏 */
#define   EMV_CARD_IC                    (0x00)            /**< 接触式IC卡 */
#define   EMV_CARD_IC2                   (0x01)            /**< 接触式IC卡2 */
#define   EMV_CARD_QIC                   (0xa1)            /**< 非接触IC卡 */

/**< emv传入的操作函数指针(具体见emv传入操作函数说明.doc) */
typedef struct {
    int          (*emv_get_transamt)(unsigned char transtype, unsigned long long* cash, unsigned long long* cashback);
                 /**< -1    输入失败    故障
                      -2    未输入      BYPASS
                      -3    BCTC Test: Timeout, Unipay:中止交易
                      >0    输入密码长度*/
    int          (*emv_get_pinentry)(int type, char* pinentry);
    int          (*iss_ref)(unsigned char* pan, int panlen);
    int          (*acctype_sel)(void);
    int          (*inc_tsc)(void);
    int          (*cert_confirm)(unsigned char type, unsigned char* pcon, int len);
    int          (*lcd_msg)(char* title, unsigned char* msg, int len,  int yesno, int waittime);
    int          (*candidate_sel)(candidate* pcan, int amt, int times);
    int          (*emv_asc_2_bcd)(unsigned char* ascstr, int asclen, unsigned char* bcdstr, int align);
    int          (*emv_bcd_2_asc)(unsigned char* bcd, int asclen, unsigned char* asc, int align);
    unsigned int (*emv_c4_2_uint)(unsigned char* c4);
    void         (*emv_uint_2_c4)(unsigned int num, unsigned char* c4);
    unsigned long long  (*bcd_2_uint64)(unsigned char*bcd,  int bcd_len);
    int          (*emv_icc_rw)(int cardno, unsigned char* inbuf, int inlen, unsigned char* outbuf, int olen);
    /**< EMV 射频卡的去激活 */
    int          (*emv_rf_powerdown)(int cardno);
                 /**< Func:    当卡和终端支持电子现金交易时,是否进行电子现金交易
                      Return:   1        继续电子现金
                                0        不进行电子现金
                               -1        用户中止
                               -3        超时 */
    int          (*emv_ec_switch)(void);
    /**< Func:    IC卡上电函数 */
    int          (*emv_icc_powerup)(int* cardno);  
    int          (*emv_get_bcdamt)(unsigned char ucTransType, unsigned char *pusBCDCash, unsigned char *pusBCDCashBack);
}emv_oper;

/**
* @brief Emv初始化函数
* @detail 传入EMV文件路径、EMV相关文件名、EMV相关操作函数指针,
* @param in  pfile  --- EMV文件名
* @param in  poper  --- EMV相关操作函数指针
* @return
* @li 0		    成功
* @li <0		失败
*/
extern int EMV_Initialize(emv_file* pfile, emv_oper* poper);


/***********************************IC卡交易函数*************************/
/**
* @brief 开始执行EMV IC卡交易
* @param in out popt  --- EMV交易选项
* @return
* @li    交易成功
* @li    卡片激活成功，则再次调用本函数继续交易
* @li    卡片激活失败，则结束交易
*/
extern int EMV_Start(emv_opt * popt);


/**
* @brief EMV IC卡交易结束执行函数
* @detail 执行IC卡下电和交易金额log保存
* @param in nTransRes --- 最终交易结果
* @return
* @li        0        成功
* @li       -1        失败
*/
extern int EMV_Suspend(int nTransRes);


/******************************其他操作函数 **************************/
/**
* @brief 返回EMV版本字符串
* @return
* @li Version    版本号
*/
extern const char* EMV_getVersion(void);

/**
* @brief 返回EMV错误码
* @return
* @li ErrorCode        错误码
*/
extern int EMV_ErrorCode(void);

/**
* @brief 获取lunTagName[]里的一系列TLV数据,返回的数据格式为tag + len + value
* @param in out   punTagName  保存要获取TLV数据的标签数组首指针
*        in       nTagCnt     要获取的TLV数据个数
*        out      pusOutBuf   保存获取的TLV数据指针
*        in       nMaxOutLen  pusOutBuf数组的最大保存空间 
* @return 
* @li    -2        参数为空
* @li    <0        失败
* @li   n(n>0)     获取的数据总长度
*/    
extern int EMV_FetchData(unsigned int* punTagName, int nTagCnt, unsigned char* pusOutBuf, int nMaxOutLen);

/**
* @brief 获取TagName的数据值
* @param in  unTagName                ---    待读取的Tag名称
* @param out pusData                  ---    Value
* @param in  nMaxOutLen               ---    Value最大长度限制
* @return
* @li 0        标签值不存在
* @li >0       取到数据的长度
* @li -1       数据值长超出data长度限制
*/
extern int EMV_getdata(unsigned int unTagName, unsigned char *pusData, int nMaxOutLen);

/**
* @brief 设置unTagName的数据值
* @param in       unTagName  要查找的标签名
*        in       pusData    存放查找的数据值
*        in       nMaxLen    pusData存放的长度
* @return 
* @li     0        设置成功
* @li    <0        设置失败
* @li    -2        无设置该标签权限
*/ 
extern int EMV_setdata(unsigned int unTagName, unsigned char* pusData, int nMaxLen);

/*=========================================================================*/
#if 0
typedef struct tlv {
    /**< internal state */
    unsigned char* _tagptr;           /**< pointer of  'tag' field in the TLV String */                                
    unsigned char* _lenptr;           /**< pointer of  'len' field in the TLV String */                                
    unsigned int   _len;              /**< length from first pointer  to itself in its parent's string */              
                                                                                                                  
    /**< parsed information */                                                                                    
    int            parent;            /**< recored the parent position in the tlv_t structure */                       
    int            childnum;          /**< the num of its children (no children = -1) */                               
                                                                                                                  
    /**< tag len value */                                                                                         
    unsigned int   tagname;           /**< record tagname */                                                           
    unsigned int   valuelen;          /**< record the length of value */                                               
    unsigned char* pvalue;            /**< pointer of value in the TLV String */                                       
}tlv_t;


/**< nDefFlag defined */
#define   SINGLE_TLVOBJ     (0x01)    /**< it is a single constructed object */
#define   STRING_TLVOBJ     (0x02)    /**< it is a tlv object string not coded in a constructed object */
#define   DECODE_LEVEL1     (0x10)    /**< just decode the object in level one */

#endif 
/**
* @brief  解析TLV字符串到tlv_t  *pstTlvList结构体.
* @detail (pstTlvList[0]记录TLV的相关信息不存放解析出的TLV对象,
          实际存放从pstTlvList[1]开始)
          pstTlvList[0].pvalue        保存TLV字符串指针
          pstTlvList[0].valuelen      保存TLV字符串长度
          pstTlvList[0].childnum      保存此次共解析多少个TLV结点
* @param  in  pusTlvBuf       ---     传入TLV数据的首指针
* @param  in  nTlvlen         ---     Tlv数据的长度
* @param  out pstTlvList      ---     存放解析好的Tag链表
* @param  in  nTlvArrayNum    ---     Tag数组的大小
* @param  in  nFlag           ---     SINGLE_TLVOBJ        TLV字符串为单个结构数据对象
*                                     STRING_TLVOBJ        TLV对象字符串集(tag+len+value+tag+len+value...)
*                                     DECODE_LEVEL1        只解析出子结点TLV字串
*                                                          (如果子结点为结构数据对象将不继续解析其子结点)
* @return
* @li 0       解析成功
* @li <0      解析失败
*/
extern int EMV_parse_tlv(unsigned char* pusTlvBuf, int nTlvlen, tlv_t* pstTlvList, int nTlvArrayNum, int nDefFlag);

/**< 参数level类别 */
//#define   SEARCH_ALL_DESC               (0x01)            /**< search all the descendants */
//#define   SEARCH_ONLY_SON               (0x02)            /**< search only its sons  */


/**
* @brief  在pstTlvList  结构体中获取unTagName的TLV数据的索引号
* @detail 配合int EMV_parse_tlv()函数使用
* @param  in nParent       ---    从这个父结点开始搜索
* @param  in unTagName     ---    Tag标签
* @param  in pstTlvList    ---    待搜索的Tag链表
* @param  in nLevel        ---    搜索的层度
*                                 SEARCH_ONLY_SON   只在自己子结点查找
*                                 SEARCH_ALL_DESC   在所有后续结点查找
* @return 
* @li     查找成功         返回子结点序号
* @li     查找失败         0
*/
extern int EMV_fetch_tlv(int nParent, unsigned int unTagName, tlv_t* pstTlvList, int nLevel);

/**
* detail PBOC Transaction Logs Reading
*/
typedef enum {
    PBOCLOG_RECNUM = -2,
    PBOCLOG_SFI,
    PBOCLOG_FMT
}EM_PBOCLOGTAG;
/**
* @brief 获取EMV交易日志。
* @param in  nRec        ---当>0时，为要读出的记录数
*                           =PBOCLOG_SFI     函数返回:包含日志数据的文件SFI
*                           =PBOCLOG_RECNUM  函数返回:日志记录数
*                           =PBOCLOG_FMT     函数返回obuf日志格式
* @param out pusOut      ---保存函数传出的日志格式或者日志数据
* @param in  nOutMaxLen  ---传出数据的最大长度限制
* @return
* @li     0              没有获取到日志数据
* @li    >0              返回pusOut的数据长度
* @li    <0              失败
*/
extern int EMV_GetPBOCLog(int nRec, unsigned char* pusOut, int nOutMaxLen);

/*
 * PBOC unload Logs Reading
 */
/**
* @brief 获取EMV交易日志。     
* @param in    nRecNo      当>0时，为要读出的记录数
*                          =PBOCLOG_SFI       函数返回:包含日志数据的文件SFI
*                          =PBOCLOG_RECNUM    函数返回:日志记录数
*                          =PBOCLOG_FMT       函数返回obuf日志格式
*        out   pusOutBuf   保存函数传出的日志格式或者日志数据
*        in    nMaxOutLen  传出数据的最大长度限制
*
* @return 
* @li   < 0            函数错误
*       > 0            返回obuf的数据长度
*       = 0            没有获取到日志数据
*/  
extern int EMV_GetecloadLog(int nRecNo, unsigned char* pusOutBuf, int nMaxOutLen); 

/*
 * CAPK Interface
 */
#define   MAX_MODULUS_LEN               (248)             /**< 公钥最大模长 */

/**
* @struct publickey (RSA Key)
*/
typedef struct {
    unsigned char pk_modulus[MAX_MODULUS_LEN];        /**< 公钥模 */     
    unsigned char pk_mod_len;                         /**< 公钥模长 */   
    unsigned char pk_exponent[3];                     /**< 公钥指数 */   
}publickey;

/**
* @struct capk (Certification Authority Public Key structure)
*/
typedef struct {
    publickey     _key;                               /**< RSA公钥结构体 */             
    unsigned char _hashvalue[20];                     /**< 公钥HASH校验值 */            
    unsigned char _expired_date[4];                   /**< 公钥过期时间 */              
    unsigned char _rid[5];                            /**< 注册应用提供商标识RID */     
    unsigned char _index;                             /**< 公钥索引 */                  
    unsigned char _pk_algorithm;                      /**< 公钥算法标识 */              
    unsigned char _hash_algorithm;                    /**< HASH算法标识 */              
    unsigned char _disable;                           /**< =1公钥失效 */                
    unsigned char _resv[3];                           /**< 保留位 */                                        
}capk;

/**< 公钥操作错误值定义 */
#define   CAPKERR_BASE              (-4000)
#define   CAPKERR_FILEOPEN          (CAPKERR_BASE - 1) /**< 文件打开错误 */
#define   CAPKERR_FILEWRITE         (CAPKERR_BASE - 2) /**< 写文件错误 */
#define   CAPKERR_FILEREAD          (CAPKERR_BASE - 3) /**< 读文件错误 */
#define   CAPKERR_CHKSUM            (CAPKERR_BASE - 4) /**< 公钥checksum错误 */
#define   CAPKERR_LOST              (CAPKERR_BASE - 5) /**< 未找到此公钥 */
#define   CAPKERR_PARAM             (CAPKERR_BASE - 6) /**< 参数错误 */
#define   CAPKERR_FILELEN           (CAPKERR_BASE - 7) /**< 文件长度错误 */

                                                       
/**< 公钥操作mode  */                                  
#define   CAPK_RMV                  (0x01)             /**< 公钥删除 */
#define   CAPK_UPT                  (0x02)             /**< 公钥更新若不存在则新增一个 */
#define   CAPK_DIS                  (0x04)             /**< 公钥去激活 */
#define   CAPK_ENB                  (0x08)             /**< 公钥激活 */
#define   CAPK_GET                  (0x10)             /**< 公钥获取 */
#define   CAPK_CLR                  (0x20)             /**< 公钥清空 */

/**
* @brief    操作公钥
* @param in out pstCAPK   ---公钥参数结构体
* @param in nMode         ---公钥操作Mode
* @return
* @li       0             成功
* @li       -1            失败
*/
extern int EMV_OperCAPK(capk* pstCAPK, int nMode);
/**
* @brief    删除某个RID所有的CAPK    
* @param in sRID          ---要删除的RID字符串
* @return
* @li       0             成功
* @li       -1            失败
*/
extern int EMV_removeCAPKByRID(char sRID[5]);

/**
* @brief     获取从start到end的公钥RID 及RID的index
* @param in  nStart        ---第几个公钥开始
* @param in  nEnd          ---到第几个公钥结束
* @param out lsCAPK       ---公钥数组
                              每个数组元素的前5个字符保存RID,第6个字符保存RID索引
* @return
* @li        >0            公钥个数
* @li        <= 0          失败
*/   
extern int EMV_EnumCAPK(int nStart, int nEnd, char lsCAPK[][6]);

/*=========================================================================*/
/**< AID操作错误值定义*/
#define   AIDERR_BASE                   (-5000)
#define   AIDERR_FILEOPEN               (AIDERR_BASE - 1) /**< 文件打开错误 */
#define   AIDERR_FILEWRITE              (AIDERR_BASE - 2) /**< 写文件错误 */
#define   AIDERR_FILEREAD               (AIDERR_BASE - 3) /**< 读文件错误 */
#define   AIDERR_CHKSUM                 (AIDERR_BASE - 4) /**< 公钥checksum错误 */
#define   AIDERR_LOST                   (AIDERR_BASE - 5) /**< 未找到此AID */
#define   AIDERR_PARAM                  (AIDERR_BASE - 6) /**< 参数错误 */
#define   AIDERR_FILELEN                (AIDERR_BASE - 7) /**< 文件长度错误 */
#define   AIDERR_UPTAID                 (AIDERR_BASE - 8) /**< 更新终端配置参数时同步更新AID相应数据失败 */

/**< AID操作mode */
#define   AID_RMV                       (0x01)            /**< 删除一个AID */
#define   AID_UPT                       (0x02)            /**< 更新一个AID若不存在则新增一个*/
#define   AID_GET                       (0x10)            /**< 获取一个AID */
#define   AID_CONFIG_R                  (0x20)            /**< 读取终端配置参数 */
#define   AID_CONFIG_W                  (0x40)            /**< 写入终端配置参数 */
#define   AID_CLR                       (0x80)            /**< 清空全部AID参数(不影响终端配置参数) */

/**
* @brief    操作终端配置或者AID
* @detail   (注意:终端配置参数应写在文件中的第一个位置,
            AID从第2个参数位置开始写入)
* @param in out pstEmvParam  ---EMV参数结构体
* @param in     nMode        ---AID操作Mode
* @return
* @li           0            成功
* @li           -1           失败
*/
extern int EMV_OperAID(emvparam* pstEmvParam, int nMode);

/**
* @brief  获取所有AID
* @param out lsAid    ---获取的AID数组列表值
                         数组的第一个字符(即lsAid[X][0]) 保存AID长度;
                         从第二个字符(即lsAid[X][1])开始保存AID值
* @return
* @li        >0           AID个数
* @li        <= 0         失败
*/  
extern int EMV_EnumAID(char lsAid[][17]);

/**
* @brief  重新读取AID文件，建立AID List
* @return
* @li     0              成功
* @li     -1             失败
*/
extern int EMV_buildAidList(void);
/**
 *@detail 文件操作统一方式 
 */
#define   STRUCT_DEL                    (0x01)            /**< 删除一个结构 */
#define   STRUCT_UPT                    (0x02)            /**< 更新一个结构若不存在则新增 */
#define   STRUCT_GET                    (0x04)            /**< 获取一个结构 */
#define   STRUCT_CLR                    (0x08)            /**< 清空全部结构 */


/*========================证书黑名单=============================*/
/**
* @struct  Certificate black 证书黑名单
*/
typedef struct {
    unsigned char  _rid[5];
    unsigned char  _index;
    unsigned char  _sn[3];
    unsigned char  _disable;                              /**< 屏蔽位(0x01屏蔽) */
    unsigned char  _rsv[2];
}certblk;

/**
* @brief    操作证书黑名单函数
* @param in out pstCertBlk    ---保存要操作的黑名单结构体
* @param in     nMode         ---STRUCT_DEL     删除一个结构
*                                STRUCT_UPT     更新一个结构若不存在则新增
*                                STRUCT_GET     获取一个结构    
*                                STRUCT_CLR     清空全部结构
* @return
* @li           0          成功
* @li           -1         失败
*/
extern int EMV_oper_certblk(certblk* pstCertBlk, int nMode);

/**
* @struct  cardblk 卡片黑名单(PAN+PAN Seq)
*/
typedef struct {
    unsigned char _card_no[10];                /**< 要屏蔽的PAN */                                    
    unsigned char _len;                        /**< PAN 长度(0x5A) */                                 
    unsigned char _index;                      /**< PAN序列号(0x5F34) */                              
    unsigned char _disable;                    /**< 0x01时, 屏蔽整个卡片黑名单 */                     
    unsigned char _partial_match;              /**< 0x01时, 允许PAN向前部分匹配 */                    
    unsigned char _disable_index;              /**< 0x01时, 屏蔽序列号(黑名单为:仅PAN不带PAN序列号)   
                                                    0x00时, (黑名单为: PAN + PAN序列号) */            
    unsigned char _rsv[1];
}cardblk;

/**
* @brief  操作卡片黑名单函数
* @param in out pstEmvCardBlack    ---保存要操作的黑名单结构体
* @param in nMode                  ---STRUCT_DEL      删除一个结构
*                                     STRUCT_UPT      更新一个结构若不存在则新增
*                                     STRUCT_GET     获取一个结构    
*                                     STRUCT_CLR     清空全部结构
* @return
* @li       0             成功
* @li       -1            失败
*/
extern int EMV_oper_cardblk(cardblk *pstEmvCardBlack, int nMode);

/*	****************************	射频卡函数	*****************************	*/
/**
* @brief  EMV 非接触交易执行函数。
* @detail 先调用此函数,进行非接触交易预处理
          调用该函数后，最后一定要调用EMV_RF_Stop
* @param in out pstEmvOption    --- EMV交易选项
* @param in     transAmount     --- 交易金额6字节BCD,EMV_TRANS_REQAMT_NO类型有效,其它无效
* @return
* @li    EMV_TRANS_RF_ACTIVECARD    可以激活卡片
* @li    <0                         失败,交易终止
* @li                               卡片激活成功，则再次调用本函数继续交易
* @li                               卡片激活失败，则结束交易
*/
extern int EMV_rf_start(emv_opt* pstEmvOption, unsigned long long transAmount);
/**
* @brief  EMV 射频卡交易结束处理函数。
* @param in nFinalFlag    ---最终交易结果(交易接受,交易拒绝...)
* @return
* @li       0             成功
* @li       -1            失败
*/
extern int EMV_rf_suspend(int nFinalFlag);

/**
* @brief 获取长隆非接触VIP卡号(只针对长隆卡才能使用).
* @param out pusCardNo     ---数据输出缓冲
* @param out pnCardNoLen   ---输出数据长度
* @return
* @li        0             成功
* @li        -1            取卡号目录失败
* @li        -2            取卡号目录文件失败
* @li        -3            获取数据长度不符合要求
*/
extern int EMV_get_clvip_cardNo(unsigned char* pusCardNo, int* pnCardNoLen);

typedef enum {
    ATC,
    LASTONLINE_ATC,
    PWD_RETRY,
    LOG_FMT,
    EC_BALANCE,
    EC_RESET_THRESHOLD,
    RF_BALANCE,
    EC_BALANCE_LIMIT,
    EC_SINGLE_AMOUNT_LIMIT,
    EC_LOAD_LOG_FMT,
    EC_BALANCE_SEC,
    EC_APPCURR_CODE,
    EC_SECCURR_CODE,
    GETDATA_TOTAL
}GetData;
/**
* @brief 对IC卡发送Get Data指令，获取EM_EMV_GetData 类型的值
* @param in  emGetData  ---Getdata数据类型
* @param out pusOut     ---数据输出缓冲
* @param out pnOutLen   ---输出数据长度
* @return
* @li         0            成功
* @li        -1            失败
*/
extern int EMV_ICC_getdata(GetData emGetData, unsigned char* pusOut, int* pnOutLen);

/**
* @brief     执行EMV并根据设置执行到相应位置
* @param in  nVal          =0 正常执行, =1 设置执行到最终应用选择并返回
* @return
* @li         0            总是成功
*/
extern int EMV_run_to_finalsel(int value);

/**
* @brief 对IC卡发送Get Data指令
* @param in  unTagName  ---Tag名称 两个字节
* @param out pusOut     ---数据输出缓冲
* @param out pnOutLen   ---输出数据长度
* @return
* @li         0            成功
* @li        -1            失败
* @该函数原为内部使用，因银联U加构开放
*/
extern int EMV_ICC_GetDataByTagName(unsigned int unTagName, unsigned char * pusOut, int * pnOutLen);

/**
* @brief 获取交易的分支emvconfig的transbranch
* @param out pucTransBranch ---交易分支存储
* @return
* @li         0            成功
* @li        -1            失败
* @
*/
extern int EMVL2_GetTransBranch(unsigned char *pucTransBranch);

/**
* @brief 设置是否检测黑名单，
* @param in ucNotChkBlkCard ---0表示检测，1表不检测
* @return
* @li         
* @
*/
extern void  EMVL2_SetNotChkBlkCard(const unsigned char ucNotChkBlkCard);

/**
* @brief 获取是否检测黑名单，
* @param in ucNotChkBlkCard ---0表示检测，1表不检测
* @return
* @li         
* @
*/
extern unsigned char EMVL2_GetNotChkBlkCard(void);

/**
* @brief 功能与EMV_Initialize相同，不同的是输入金额为bcd码，以后将全部采用该函数
*        EMV_Initialize函数将淘汰
* @detail 传入EMV文件路径、EMV相关文件名、EMV相关操作函数指针,
* @param in  pfile  --- EMV文件名
* @param in  poper  --- EMV相关操作函数指针
* @return
* @li 0		    成功
* @li <0		失败
*/
extern int EMVL2_Initialize(emv_file * pfile, emv_oper * poper);


extern void EMVL2_Set9CTransType(const unsigned char ucTransType);
extern unsigned char EMVL2_Get9CTransType(void);
extern void EMVL2_Set9CFlag(const unsigned char ucFlag);
extern unsigned char EMVL2_Get9CFlag(void);
extern void EMVL2_SetTransProperty(const unsigned char *pusTransProperty);
extern void EMVL2_GetTransProperty(unsigned char *pusTransProperty);

extern void EMVL2_JTCard_Operation(void);


//需要指定AID操作的时候操作这个函数，参数pucAid传对应的AID，nAidLen对应长度
//不需要操作的时候需要设置参数为NULL，并且长度等于0
extern int EMVL2_SetBindAid(unsigned char  *pucAid, int nAidLen);

extern int EMVL2_GetBindAid(unsigned char  *pucAid, int *pnAidLen);

//双币电子现金标记开关
extern int EMVL2_SetDoulCurrFlag(int nDoubCurrFlag);
extern int EMVL2_GetDoulCurrFlag(int *pnDoubCurrFlag);
/**************************ME系列调用接口函数***************************************/

typedef struct {
	int (*EMV_Initialize)(emv_file * pfile, emv_oper * poper);
	int (*EMV_Start)(emv_opt * popt );
	int (*EMV_Suspend)(int trans_final);
	const char * (*EMV_getVersion)(void);
	int (*EMV_ErrorCode)(void);
	int (*EMV_FetchData)(unsigned int * tagname, int count, unsigned char * obuf, int olen);
	int (*EMV_getdata)( unsigned int tagname, unsigned char *data, int dataLimit );
	int (*EMV_setdata)( unsigned int tagname, unsigned char *data, int dataLen );
	int (*EMV_parse_tlv)(unsigned char * ptlvstr, int tlvlen, tlv_t * pobj, int objspace, int deflag);
	int (*EMV_fetch_tlv)(int parent,  unsigned int tagname, tlv_t * pobj, int level);
	int (*EMV_GetPBOCLog)(int rec, unsigned char * obuf, int obuflen);
	int (*EMV_GetecloadLog)(int rec, unsigned char * obuf, int obuflen);
	int (*EMV_OperCAPK)(capk * pk, int mode);
	int (*EMV_removeCAPKByRID)(char RID[5]);
	int (*EMV_EnumCAPK)(int start, int end, char strCAPK[][6]);
	int (*EMV_OperAID)(emvparam * par, int mode);
	int (*EMV_EnumAID)(char strAID[][17]);
	int (*EMV_buildAidList)(void);
	int (*EMV_oper_certblk)( certblk *blk, int mode );
	int (*EMV_oper_cardblk)( cardblk *blk, int mode );
	int (*EMV_rf_start)( emv_opt * popt , unsigned long long  transAmount);
	int (*EMV_rf_suspend)( int trans_final );
	int (*EMV_get_clvip_cardNo)( unsigned char *obuf, int *len );
	int (*EMV_ICC_getdata)(GetData getdatatype,  unsigned char * pout, int * outlen);
    int (*EMVL2_Initialize)(emv_file * pfile, emv_oper * poper);
    int (*EMVL2_GetTransBranch)(unsigned char *pucTransBranch);
    int (*EMVL2_JTCard_Operation)(void);
    char sRev[8];
} emv_api_t;

typedef struct
{
	int (*pNL_open)( const char *filename, int filemode );
	int (*pNL_close)(int hd);
	int (*pNL_read)(int hd, char *buffer, int size );
	int (*pNL_write)(int hd, char *buffer, int size );
	int (*pNL_seek)(int hd, int offset, int where );
	/* we should allways use this one */
	int (*pNL_truncate)(const char *filename, int size);
	int (*pNL_delete)(const char *filename);
	int (*pNL_rename)( const char *srcname, const char *dstname );
}emv_file_oper;

extern void EMVL2_SetEmvFileOper(const emv_file_oper stEmvFileOper);
#endif

