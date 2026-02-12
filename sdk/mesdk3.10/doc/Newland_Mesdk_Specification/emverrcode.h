/**************************************************************************
* Copyright (C) 2019 Newland Payment Technology Co., Ltd
* All Rights Reserved
* @file		EMVERRCODE.H
* @brief	EMV Ct and Clss Error Code.
*
* @version  0.1
* @date		19.02.20
* @brief	create
*
* @version
* @date
* @brief	
**************************************************************************/

#ifndef __EMV_ERRCODE_H__
#define __EMV_ERRCODE_H__


//BASE
#define EMV_ERR_BASE							(0)						/*EMV基准错误码(EMV benchmark error code)*/
#define EMV_ERR_READCONFIG						(EMV_ERR_BASE - 1)   	/*读终端配置失败(Failed to read aid configuration)*/
#define EMV_ERR_READAIDLIST						(EMV_ERR_BASE - 2)    	/*读终端列表失败(Failed to read aid list)*/
#define EMV_ERR_POWERUP							(EMV_ERR_BASE - 3)    	/*IC卡无法上电(IC card unable to power on)*/
#define EMV_ERR_NOTSUPPORT						(EMV_ERR_BASE - 4)    	/*IC卡不支持的指令(IC card not support instruction)*/
#define EMV_ERR_APPBLOCK						(EMV_ERR_BASE - 5)   	/*应用锁定(Application lock)*/
#define EMV_ERR_FINDAPP							(EMV_ERR_BASE - 6)    	/*找不到支持的应用(Can't find supported applications)*/
#define EMV_ERR_Cancel							(EMV_ERR_BASE - 7)     	/*放弃交易(Quit the transaction)*/
#define EMV_ERR_SELECTAPP						(EMV_ERR_BASE - 8)     	/*应用选择失败(Application selection failed)*/
#define EMV_ERR_APPINIT							(EMV_ERR_BASE - 9)    	/*应用初始化失败(Application initialization failed)*/
#define EMV_ERR_READAPPDATA						(EMV_ERR_BASE - 10)  	/*读应用数据失败(Failed to read application data)*/
#define EMV_ERR_OFFAUTH							(EMV_ERR_BASE - 11)   	/*脱机数据认证失败(Offline data authentication failed)*/
#define EMV_ERR_PROCESSLIMIT					(EMV_ERR_BASE - 12)   	/*处理限制失败(Process limit failed)*/
#define EMV_ERR_CARDVERIFY						(EMV_ERR_BASE - 13)    	/*持卡人认证失败(Cardholder authentication failed)*/
#define EMV_ERR_TERMRISKMANAGE					(EMV_ERR_BASE - 14)   	/*终端风险管理失败(Terminal risk management failed)*/
#define EMV_ERR_TERMACTANALYZE					(EMV_ERR_BASE - 15)     /*终端行为分析失败(Terminal behavior analysis failed)*/
#define EMV_ERR_NOTSUPPORTSERVICE				(EMV_ERR_BASE - 16)     /*不支持的服务(Unsupported service)*/
#define EMV_ERR_NORANDNUM						(EMV_ERR_BASE - 17)     /*无随机数(No random number)*/
#define EMV_ERR_CARDBLOCK						(EMV_ERR_BASE - 18)     /*卡片锁定(Card lock)*/
#define EMV_ERR_COMPLETION						(EMV_ERR_BASE - 19)     /*GEN AC执行失败(GEN AC execution failed*/
#define EMV_ERR_SAVECONFIG						(EMV_ERR_BASE - 20)   	/*存重点配置失败(Save key configuration failed)*/
#define EMV_ERR_RF_PREPROCESS					(EMV_ERR_BASE - 30)   	/*射频卡预处理失败(RF card preprocessing failed)*/
#define EMV_ERR_TIMEOUT							(EMV_ERR_BASE - 31) 	/*操作超时(Operation timeout)*/
#define EMV_ERR_RF_GETICC						(EMV_ERR_BASE - 32)		/*射频卡寻卡时检测到插入IC卡(Inserted IC card detected during RF card finder)*/
#define EMV_ERR_AID_COUNT_EXCEED				(EMV_ERR_BASE - 33)		/*AID条数太多(Too many AIDs)*/

//File Process
#define FILEERR_BASE							(EMV_ERR_BASE - 900)	/*aid配置文件基准错误码(AID configuration file benchmark error code)*/
#define FILE_OPEN_FILE							(FILEERR_BASE - 1)		/*aid配置文件打开失败(AID configuration file open failed)*/
#define FILE_READ_FILE							(FILEERR_BASE - 2)		/*aid配置文件读取失败(AID configuration file read failed)*/
#define FILE_WRITE_FILE							(FILEERR_BASE - 3)		/*aid配置文件写失败(AID configuration file write failed)*/
#define FILE_AID_VERSION						(FILEERR_BASE - 4)		/*aid配置文件版本错(AID configuration file version error)*/
#define FILE_GETTLVDATA_NOEXIST					(FILEERR_BASE - 5)		/*aid配置文件获取不到所需aid(AID configuration file Can't get the required AID)*/
#define FILE_AIDERR_PARSE						(FILEERR_BASE - 6)		/*aid配置文件tlv数据解析失败(AID configuration file Tlv data parsing failed)*/
#define FILE_AIDERR_API							(FILEERR_BASE - 7)		/*使用了错误的API接口(Use wrong API interface)*/

//ICC Apdu
#define ICCERR_BASE								(EMV_ERR_BASE - 1000)	/*ICC Apdu基准错误码(ICC Apdu benchmark error code)*/
#define APDU_DATA_NULL							(ICCERR_BASE - 1)		/*apdu交互数据为空(apdu interactive data is empty*/

#define COREERR_BASE							(EMV_ERR_BASE - 1100)	/*内核基本操作基准错误码(Kernel basic operation benchmark error code*/
#define COREERR_GETTIME							(COREERR_BASE - 1)      /*取POS时间错误(Get POS time error) */
#define COREERR_READFINALPARAM					(COREERR_BASE - 2)      /*READ FINAL PARAM错误 (READ FINAL PARAM error)*/
#define COREERR_GETUNPNUM						(COREERR_BASE - 3)      /*取随机数错误 (Get random number error)*/

#define BUFERR_BASE								(EMV_ERR_BASE - 1200)	/*数据缓存基准错误码(Data cache benchmark error code)*/
#define BUFERR_BUFOVER          				(BUFERR_BASE - 1)       /*数据缓存区内存不足(Data buffer not enough storage)*/
#define BUFERR_OBJDUP           				(BUFERR_BASE - 2)       /*标签是unique且长度大于零时不能覆盖(The label is unique and cannot be overwritten if the length is greater than zero)*/
#define BUFERR_MALLOCFAIL           			(BUFERR_BASE - 3)       /*动态内存分配失败(Dynamic memory allocation failed)*/

//Application Select
#define SELERR_BASE								(EMV_ERR_BASE - 1300)	/*应用选择基准错误码(Application select benchmark error code)*/
#define SEL_USE_AIDLIST							(SELERR_BASE - 1)		/*应用选择aid 列表错误(application select AID list failed)*/
#define SEL_QUIT								(SELERR_BASE - 2)		/*应用选择取消交易(Application select cancle the transaction)*/
#define SEL_FCIFMTERR							(SELERR_BASE - 3)		/*FCI数据格式错(FCI data format error)*/
#define SEL_FCINO6F								(SELERR_BASE - 4)		/*FCI数据没有6F(FCI data without 6F)*/
#define SEL_FCINO84								(SELERR_BASE - 5)		/*FCI数据没有84(FCI data without 84)*/
#define SEL_FCINOA5								(SELERR_BASE - 6)		/*FCI数据没有A5(FCI data without A5)*/
#define SEL_POSERR9F38							(SELERR_BASE - 7)		/*FCI数据错误的9F38(FCI data error 9F38)*/
#define SEL_BF0CDUP								(SELERR_BASE - 8)		/*FCI数据tagBF0C重复(FCI data tagBF0C repeat)*/
#define SEL_FALLBACK							(SELERR_BASE - 9)		/*应用选择回退交易(Application selection return transaction)*/
#define SEL_FCI50DUP							(SELERR_BASE - 10)		/*FCI数据tag50重复(FCI data tag50 repeat)*/
#define SEL_FCIDUP								(SELERR_BASE - 11)		/*FCI数据重复(FCI data repeat)*/
#define SEL_TLV_ERR								(SELERR_BASE - 12)		/*FCI数据TLV解析错误(FCI data TLV parsing failed)*/
#define SEL_NO6F								(SELERR_BASE - 13)		/*FCI数据没有tag 6F(FCI data without tag 6F)*/
#define SEL_NO84								(SELERR_BASE - 14)		/*FCI数据没有tag 84(FCI data without tag 84)*/
#define SEL_NOA5								(SELERR_BASE - 15)		/*FCI数据没有tag A5(FCI data without tag A5)*/
#define SEL_TAG_SEQERR							(SELERR_BASE - 16)      /*tag顺序错误. 比如tag '84' should be placed before tag 'A5' (The tag sequence is wrong. For example"tag '84' should be placed before tag 'A5'"*/
#define SEL_NOBF0C								(SELERR_BASE - 17)		/*FCI数据没有tag BF0C(FCI data without tag BF0C)*/
#define SEL_BFOC_DATAERR						(SELERR_BASE - 18)		/*FCI数据tagBF0C解析错误(FCI data tag 8F0C parsing failed)*/
#define SEL_AID_DIFF_DFNAME						(SELERR_BASE - 19)      /*最终选择，返回df名称与辅助命令不同(final selection, return DF Name  not the same as the AID of command)*/
#define SEL_ERR_TRANSTYPE						(SELERR_BASE - 20)      /*错误的交易类型(Wrong transaction type)*/
#define SEL_PPSE_ERROR							(SELERR_BASE - 21)      /*PPSE命令返回失败(PPSE command returned failure)*/
#define SEL_NEXT_AID							(SELERR_BASE - 22)      /*选择下一个AID(Select the next AID)*/
#define SEL_DISCOVER_ZIP_AID					(SELERR_BASE - 23)		/*discover zip aid对ppse的特殊处理(Discover zip aid special treatment for ppse)*/
#define SEL_NO84VALUE							(SELERR_BASE - 24)		/*FCI数据没有tag 84的值(FCI data without the value of tag 84)*/
#define SEL_DPASNOHAVEZIPAID					(SELERR_BASE - 25)		/*DPAS ppse只返回dpas aid没有返回zip aid(DPAS ppse only returns dpas aid does not return zip aid)*/
#define SEL_AMT_OVER_CLSS						(SELERR_BASE - 27)		/*非接触交易金额超限额(contactless transaction amount  exceeds limit)*/
#define SEL_AMTZERO_CLSS_NOT_ALLOW				(SELERR_BASE - 28)		/*当金额为零时，不允许设置非接触式应用程序(when Amount zero, Set the Contactless Application Not Allowed)*/
#define SEL_AMTZERO_CLSS_OFFLINE_OLY 			(SELERR_BASE - 29)		/*当金额为零时，应设置为联机，但仅终端脱机（when Amount zero, shall set go online, but terminal offline only）*/
#define SEL_BANCOMATLEGACY_AID					(SELERR_BASE - 30)		/*Bancomat Legacy aid对ppse的特殊处理(Bancomat Legacy aid special treatment for ppse)*/

#define SEL_TAG61_INVALID						(SELERR_BASE - 35)		/*应用选择tag61无效（Application selection tag61 is invalid）*/
#define SEL_TAG4F_INVALID						(SELERR_BASE - 36)		/*应用选择tag4F无效（Application selection tag4F is invalid)*/
#define SEL_TAG4F_PARTIAL						(SELERR_BASE - 37)		/*应用选择tag4F部分匹配不全(Application selection tag4F partial matching incomplete)*/

#define SEL_FCI_FMTERR							(SELERR_BASE - 38)		/*JCB FCI格式错误(JCB FCI format error)*/
#define SEL_FCIERR84							(SELERR_BASE - 39)		/*JCB FCI 84错误(JCB FCI 84 error)*/
#define SEL_FCINO50								(SELERR_BASE - 40)		/*FCI 没有50,(FCI without 50,)*/
#define SEL_FCINO9F38							(SELERR_BASE - 41)		/*JCB FCI 没有9F38(JCB FCI without 9F38)*/
#define SEL_FCIEMPTY9F28						(SELERR_BASE - 42)		/*JCB FCI 9F28为空(JCB FCI 9F28 is empty)*/
#define SEL_FCI_ILLEGALTAG						(SELERR_BASE - 43)		/*JCB FCI 查找专有数据失败(JCB FCI failed to find proprietary data)*/
#define SEL_FCI_REV_LEGACY						(SELERR_BASE - 44)		/*JCB Torn交易为Legacy Mode(JCB Torn transaction is Legacy Mode)*/
#define SEL_FCI_MANDATAMISS						(SELERR_BASE - 45)		/*MCCS强制数据丢失(MCCS enforces data loss)*/
#define SEL_FCINO87								(SELERR_BASE - 46)		/*FCI 没有tag 87,(FCI without tag 87,)*/
#define SEL_FCI9F38LENWRONG						(SELERR_BASE - 47)		/*FCI 9F38长度错(FCI data 9F38 length wrong)*/

#define SEL_FINALSEL_6300						(SELERR_BASE - 50)		/*应用选择返回6300,State of non-volatile memory changed; authentication failed(Application selection returns 6300,State of non-volatile memory changed; authentication failed)*/
#define SEL_FINALSEL_63C1						(SELERR_BASE - 51)		/*应用选择返回63C1,(Application selection returns 63C1)*/
#define SEL_FINALSEL_6983						(SELERR_BASE - 52)		/*应用选择返回6983,Command not allowed; authentication method blocked(Application selection returns 6983,Command not allowed; authentication method blocked)*/
#define SEL_FINALSEL_6984						(SELERR_BASE - 53)		/*应用选择返回6984,Command not allowed; reference data not usable(Application selection returns 6984,Command not allowed; reference data not usable)*/
#define SEL_FINALSEL_6985						(SELERR_BASE - 54)		/*应用选择返回6985,Command not allowed; conditions of use not satisfied(Application selection returns 6985,Command not allowed; conditions of use not satisfied)*/
#define SEL_FINALSEL_6A82						(SELERR_BASE - 55)		/*应用选择返回6A82,Wrong parameters P1-P2; file or application not found(Application selection returns 6A82,Wrong parameters P1-P2; file or application not found)*/
#define SEL_FINALSEL_6A83						(SELERR_BASE - 56)		/*应用选择返回6A83,Wrong parameters P1-P2; record not found(Application selection returns 6A83,Wrong parameters P1-P2; record not found)*/
#define SEL_FINALSEL_6A88						(SELERR_BASE - 57)		/*应用选择返回6A88,Reference data (data objects) not found(Application selection returns 6A88,Reference data (data objects) not found)*/
#define SEL_FINALSEL_6400						(SELERR_BASE - 58)		/*应用选择返回6400,(Application selection returns 6400)*/
#define SEL_FINALSEL_6500						(SELERR_BASE - 59)		/*应用选择返回6500,(Application selection returns 6500)*/
#define SEL_FINALSEL_9001						(SELERR_BASE - 60)		/*应用选择返回9001,(Application selection returns 9001)*/
#define SEL_L1_FAIL								(SELERR_BASE - 61)		/*应用选择apdu通讯失败,(Application selection apdu communication failed)*/
#define SEL_L1_ACTIVE							(SELERR_BASE - 62)		/*perforem激活失败,(perform transaction active card  failed)*/
#define SEL_FCIPDOL9F02WRONG					(SELERR_BASE - 63)		/*FCI PDOL中的9F02长度错,(FCI PDOL 9F02 Lenth Wrong)*/
#define SEL_FCIPDOL9AWRONG						(SELERR_BASE - 64)		/*FCI PDOL中的9A长度错,(FCI PDOL 9A Lenth Wrong)*/
#define SEL_FCIPDOL9CWRONG						(SELERR_BASE - 65)		/*FCI PDOL中的9C长度错,(FCI PDOL 9C Lenth Wrong)*/
#define SEL_FCIPDOL9F37WRONG					(SELERR_BASE - 66)		/*FCI PDOL中的9F37长度错,(FCI PDOL 9F37 Lenth Wrong)*/
#define SEL_FCIPDOL9F35WRONG					(SELERR_BASE - 67)		/*FCI PDOL中的9F35长度错,(FCI PDOL 9F35 Lenth Wrong)*/
#define SEL_FCIPDOL9F66WRONG					(SELERR_BASE - 68)		/*FCI PDOL中的9F66长度错,(FCI PDOL 9F66 Lenth Wrong)*/

/*Select FCI Response Analysis Error Code*/
#define KA_ERR_BASE								(SELERR_BASE - 70)		/*JCB基准错误码(JCB benchmark error code)*/
#define KA_CONFIG_EMPTY							(KA_ERR_BASE - 1)		/*JCB 配置为空(JCB configured to be empty)*/
#define KA_LEGACY_NOTSUPPORTED					(KA_ERR_BASE - 2)		/*JCB Legacy模式不支持(JCB Legacy mode is not supported)*/

//Application Initialization
#define INITERR_BASE							(EMV_ERR_BASE - 1400)	/*应用初始化基准错误码(Application initialization benchmark error code)*/
#define INITERR_DOLPACKET						(INITERR_BASE - 1)		/*应用初始化PDOL打包失败(Application initialization PDOL packaging failed)*/
#define INITERR_RETURNSEL						(INITERR_BASE - 2)		/*应用初始化返回值不等于9000(Application initialization return value is not equal to 9000)*/
#define INITERR_GPOCMD							(INITERR_BASE - 3)		/*应用初始化返回其他错误码(Application initialization returns status word)*/
#define INITERR_TLVDECODE						(INITERR_BASE - 4)		/*应用初始化返回TLV解析错(Application initialization returns TLV parsing error)*/
#define INITERR_80VALUELEN						(INITERR_BASE - 5)		/*应用初始化返回80模版长度错(Application initialization returns 80 template length error)*/
#define INITERR_77NOAIP							(INITERR_BASE - 6)		/*应用初始化返回77模版没有AIP(Application initialization returns 77 template without AIP)*/
#define INITERR_AIPLEN							(INITERR_BASE - 7)		/*应用初始化返回AIP长度错(Application initialization returns AIP length error)*/
#define INITERR_77NOAFL							(INITERR_BASE - 8)		/*应用初始化返回77模版没有AFL(Application initialization returns 77 template without AFL)*/
#define INITERR_AFLLEN							(INITERR_BASE - 9)		/*应用初始化返回AFL长度错(Application initialization returns AFL length error)*/
#define INITERR_UNEXPECTTAG						(INITERR_BASE - 10)		/*应用初始化返回无效tag(Application initialization returns invalid tag)*/
#define INITERR_NOPDOL							(INITERR_BASE - 11)		/*应用初始化没有PDOL(Application initialization without PDOL)*/
#define INITERR_RETURNDATA						(INITERR_BASE - 12)		/*应用初始化qpboc返回80模版(Application initialize pboc return 80 template)*/
#define INITERR_CARDNOSUPPORT					(INITERR_BASE - 13)		/*应用初始化卡片不支持(Application initialization card is not supported)*/
#define INITERR_ECSELECT_QUIT					(INITERR_BASE - 14)		/*取消电子现金交易(Cancel electronic cash transactions)*/
#define INITERR_ECONLY_DENIAL					(INITERR_BASE - 15)		/*纯电子现金卡,但不支持电子现金(Pure electronic cash card, but does not support e-cash)*/
#define INITERR_GPO_RETURN_6984					(INITERR_BASE - 16) 	/*应用初始化返回6984< 20120911 zhengel 6984特殊处理(Application initialization returns 6984< 20120911 zhengel 6984 special treatment)*/
#define INITERR_GPO_RETURN_6985					(INITERR_BASE - 17) 	/*应用初始化返回6985 <20160330 fangjt 6985直接终止交易(Application initialization returns 6985 <20160330 fangjt 6985 directly terminate the transaction)*/
#define INITERR_GPO_RETURN_6283					(INITERR_BASE - 18)		/*应用初始化返回6283(Application initialization returns 6283)*/
#define INITERR_GPO_RETURN_6300					(INITERR_BASE - 19)		/*应用初始化返回6300(Application initialization returns 6300)*/
#define INITERR_GPO_RETURN_63C1					(INITERR_BASE - 20)		/*应用初始化返回63C1(Application initialization returns 63C1)*/
#define INITERR_GPO_RETURN_6983					(INITERR_BASE - 21)		/*应用初始化返回6983(Application initialization returns 6983)*/
#define INITERR_GPO_RETURN_6986					(INITERR_BASE - 22)		/*应用初始化返回6986(Application initialization returns 6986)*/
#define INITERR_GPO_RETURN_9001					(INITERR_BASE - 23)		/*应用初始化返回9001(Application initialization returns 9001)*/
#define INITERR_GPO_RETURN_6A81					(INITERR_BASE - 24)		/*应用初始化返回6A81(Application initialization returns 6A81)*/
#define INITERR_GPO_RETURN_6A82					(INITERR_BASE - 25)		/*应用初始化返回6A82(Application initialization returns 6A82)*/
#define INITERR_GPO_RETURN_6A83					(INITERR_BASE - 26)		/*应用初始化返回6A83(Application initialization returns 6A83)*/
#define INITERR_GPO_RETURN_6A88					(INITERR_BASE - 27)		/*应用初始化返回6A88(Application initialization returns 6A88)*/
#define INITERR_GPO_RETURN_6500					(INITERR_BASE - 28)		/*应用初始化返回6500(Application initialization returns 6500)*/
#define INITERR_GPO_RETURN_6400					(INITERR_BASE - 29)		/*应用初始化返回6400(Application initialization returns 6400)*/
#define INITERR_GPO_RETURN_9408					(INITERR_BASE - 30)		/*应用初始化返回9408(Application initialization returns 9408)*/
#define INITERR_RF_ATC							(INITERR_BASE - 31)		/*应用初始化返回ATC错误(Application initialization returns ATC error)*/
#define INITERR_RF_AC							(INITERR_BASE - 32)		/*应用初始化返回AC错误(Application initialization returns AC error)*/
#define INITERR_RF_9F10							(INITERR_BASE - 33)		/*应用初始化返回9F10错误(Application initialization returns 9F10 error)*/
#define INITERR_RF_57							(INITERR_BASE - 34)		/*应用初始化返回57错误(Application initialization returns 57 error)*/
#define INITERR_RF_AFL							(INITERR_BASE - 35)		/*应用初始化返回AFL错误(Application initialization returns AFL error)*/
#define INITERR_RF_5F20							(INITERR_BASE - 36)		/*应用初始化返回5F20错误(Application initialization returns 5F20 error)*/
#define INITERR_RF_NO9F66						(INITERR_BASE - 37)		/*应用初始化没有9F66(Application initialization without 9F66)*/
#define INITERR_RF_INSERTICC					(INITERR_BASE - 38)		/*非接应用初始化过程检测到插卡(The insert card is detected during the contactless application initialization process)*/
#define INITERR_RF_9F27							(INITERR_BASE - 39)		/*应用初始化9F27错误(Application initialization 9F27 error)*/
#define INITERR_RF_APPNOSUPPORT					(INITERR_BASE - 40)		/*应用初始化APP不支持(Application initialization APP does not support)*/
#define INITERR_RF_ECONLY_ONLINE				(INITERR_BASE - 41)		/*纯电子现金卡但要求联机(Pure electronic cash card but requires online)*/
#define INITERR_RF_ECONLY_CVM					(INITERR_BASE - 42)		/*纯电子现金卡但要求CVM(Pure electronic cash card but requires CVM)*/
#define INITERR_RF_STRIPE						(INITERR_BASE - 43)		/*非接应用初始化过程检测到磁条卡(Magnetic stripe card is detected during the contactless application initialization process.)*/
#define INITERR_SELECT_KERNEL_ERR				(INITERR_BASE - 44)		/*应用初始化返回ATC错误(Application initialization returns ATC error)*/
#define INITERR_GPO_SAVEDATAERR					(INITERR_BASE - 45)		/*应用初始化保存数据失败(Application initialization failed to save data)*/
#define INITERR_77DUB_AFL						(INITERR_BASE - 46)		/*应用初始化返回77模版AFL数据重复(Application initialization returns 77 template AFL data duplication)*/
#define INITERR_80DUB_AFL						(INITERR_BASE - 47)		/*应用初始化返回80模版AFL数据重复(Application initialization returns 80 template AFL data duplication)*/
#define INITERR_81_GET							(INITERR_BASE - 48)		/*应用初始化获取到81(Application initialization gets 81)*/
#define INITERR_77DUB_AIP						(INITERR_BASE - 49)		/*应用初始化返回77模版AIP数据重复(Application initialization returns 77 template AIP data duplication)*/
#define INITERR_80DUB_AIP						(INITERR_BASE - 50)		/*应用初始化返回80模版AIP数据重复(Application initialization returns 80 template AIP data duplication)*/
#define INITERR_9F01_GET						(INITERR_BASE - 51)		/*应用初始化返回9F01错误(Application initialization returns 9F01 error)*/
#define INITERR_9F69_GET						(INITERR_BASE - 52)		/*应用初始化返回9F69错误(Application initialization returns 9F69 error)*/
#define INITERR_SAVE_FAIL						(INITERR_BASE - 53)		/*应用初始化保存数据失败(Application initialization failed to save data)*/
#define INITERR_PARSE_ERR						(INITERR_BASE - 54)		/*应用初始化tlv数据解析错(Application initialization tlv data parsing error)*/
#define INITERR_CARDDATA_MISSING				(INITERR_BASE - 55)		/*应用初始化卡数据丢失(Application initialization card data lose)*/
#define INITERR_AFLHEADERR						(INITERR_BASE - 56)		/*应用初始化AFL格式错(Application initialization AFL format error)*/
#define INITERR_DUPLICATED						(INITERR_BASE - 57)		/*应用初始化tag重复(Application initialization tag repeat)*/
#define INITERR_AIP_MISSING						(INITERR_BASE - 58)		/*JCB应用初始化没有返回AIP(JCB application initialization without return AIP)*/
#define INITERR_SFI_MISSING						(INITERR_BASE - 59)		/*JCB应用初始化没有返回SFI(JCB application initialization without return SFI)*/
#define INITERR_ILLEGALTAG						(INITERR_BASE - 60)		/*JCB应用初始化不是77或者80模版(JCB application initialization is not 77 or 80 template)*/
#define INITERR_INVALID_SFI						(INITERR_BASE - 61)		/*JCB应用初始化SFI错误(JCB application initializes SFI error)*/
#define INITERR_QUIT							(INITERR_BASE - 62)		/*应用初始化取消交易(Application initialization cancel transaction)*/
#define INITERR_FFI_NOSUPP_CONTAACTLESS			(INITERR_BASE - 63)		/*interac FFI不支持非接(Interac FFI does not support contactless)*/
#define INITERR_FFI_NOSUPP_MOBILE				(INITERR_BASE - 64)		/*interac FFI不支持手机(Interac FFI does not support mobile phones)*/
#define INITERR_FFI_WRONG						(INITERR_BASE - 65)		/*interac FFI值错误(Interac FFI value error)*/
#define INITERR_NO_CTI							(INITERR_BASE - 66)		/*interac没有CTI（interac no CTI）*/
#define INITERR_CTI_LENTHWRONG					(INITERR_BASE - 67)		/*interac CTI长度错误（interac CTI lenth wrong）*/
#define INITERR_CTI_TRYOTHERINTERFACE			(INITERR_BASE - 68)		/*interac CTI 要求转接口（Interac CTI requires a transfer interface）*/
#define INITERR_CTI_OTHERTERMINAL				(INITERR_BASE - 69)		/*interac CTI 要求转接口,但是本机不支持，查看其他机器（Interac CTI requires a transfer interface, but this machine does not support ，viewing other machines）*/
#define INITERR_CTI_NOOTHERTERMINAL				(INITERR_BASE - 70)		/*interac CTI 要求转接口,本机和其他机器都不支持（Interac CTI requires a transfer interface, which is not supported by this machine and other machines.）*/
#define INITERR_OVER_RETRYLIMIT					(INITERR_BASE - 71)		/*interac 超过最大的try again次数（Interac exceeds the maximum number of try again）*/
#define INITERR_GPO_RESPONSE_ERR				(INITERR_BASE - 72)		/*GPO APDU应答异常（GPO APDU response exception）*/
#define INITERR_SECONDTAPWRONG					(INITERR_BASE - 73)		/*rupay第二次挥卡不匹配（Rupay second Remove the card and put it back later is mismatch）*/
#define INITERR_GPO_RETURN_6D00					(INITERR_BASE - 74)		/*应用初始化返回6D00(Application initialization returns 6D00)*/
#define INITERR_GPO_RETURN_6588					(INITERR_BASE - 75)		/*应用初始化返回6588(Application initialization returns 6588)*/
#define INITERR_AIP_NOSUPPCDA					(INITERR_BASE - 76)		/*应用初始化返回AIP不支持CDA(Application initialization returns AIP NO Support CDA)*/

//Read Application Data
#define READRECERR_BASE							(EMV_ERR_BASE - 1500)	/*读记录基准错误码（Read record benchmark error code）*/
#define READREC_SFIERR							(READRECERR_BASE - 1)   /*读记录SFI错误（Read record SFI error）*/
#define READREC_FIRSTBE0						(READRECERR_BASE - 2)   /*读记录第一条记录错误（Read record first record error）*/
#define READREC_RECRANGEERR						(READRECERR_BASE - 3)  	/*读记录SFI记录范围错误（Read record SFI record range error）*/
#define READREC_CMDERR							(READRECERR_BASE - 4)  	/*读记录返回码错误（Read record return code error）*/
#define READREC_TLVERR							(READRECERR_BASE - 5) 	/*读记录TLV解析错误（Read record TLV parsing error）*/
#define READREC_NO5A							(READRECERR_BASE - 6)  	/*读记录没有返回tag 5A（Read record without return tag 5A）*/
#define READREC_NO8C							(READRECERR_BASE - 7)  	/*读记录没有返回tag 8C (Read record without return tag 8C)*/
#define READREC_NO8D							(READRECERR_BASE - 8)   /*读记录没有返回tag 8D(Read record without return tag 8D)*/
#define READREC_5F24ERR							(READRECERR_BASE - 9)   /*读记录tag5F24错误(Read record tag5F24 error)*/
#define READREC_5F25ERR							(READRECERR_BASE - 10)  /*读记录tag5F25错误(Read record tag5F25 error)*/
#define READREC_SAVEDATA						(READRECERR_BASE - 11) 	/*读记录存储数据失败(Failed to read record storage data)*/
#define READREC_RET_TEMDATA						(READRECERR_BASE - 12)	/*读记录返回终端数据，因此终止（readrecord return Terminal Data,so terminate）*/
#define READREC_LASTCMDERR						(READRECERR_BASE - 13)	/*最后一条readrecord cmd 失败(此宏值不可变)(The last readrecord cmd failed (this macro value is immutable))*/
#define READREC_NO57							(READRECERR_BASE - 14) 	/*读记录没有返回tag 57(Read record without return tag 57)*/
#define READREC_NO5F20							(READRECERR_BASE - 15) 	/*读记录没有返回tag 5F20(Read record without return tag 5F20)*/
#define READREC_NO9F74							(READRECERR_BASE - 21) 	/*读记录没有返回tag 9F74(Read record without return tag 9F74)*/
#define READREC_NO9F79							(READRECERR_BASE - 22) 	/*读记录没有返回tag 9F79(Read record without return tag 9F79)*/
#define READREC_NO9F36							(READRECERR_BASE - 23)  /*读记录没有返回tag 9F36(Read record without return tag 9F36)*/
#define READREC_ERR_ATC							(READRECERR_BASE - 24)  /*读记录返回tag 9F36错误(Read record returns tag 9F36 error)*/
#define READREC_ERR_DATEEXPIRE					(READRECERR_BASE - 31)  /*交易日期过期(Transaction date expired)*/
#define READREC_ERR_DATENOEFFECT				(READRECERR_BASE - 32)	/*交易日期未生效(The date of transaction is not valid)*/
#define READREC_RETURN_6283						(READRECERR_BASE - 50) 	/*读记录返回6283(Read record returns to 6283)*/
#define READREC_RETURN_6300						(READRECERR_BASE - 51) 	/*读记录返回6300(Read record returns to 6300)*/
#define READREC_RETURN_63C1						(READRECERR_BASE - 52) 	/*读记录返回63C1(Read record returns to 63C1)*/
#define READREC_RETURN_6983						(READRECERR_BASE - 53) 	/*读记录返回6983(Read record returns to 6983)*/
#define READREC_RETURN_6984						(READRECERR_BASE - 54) 	/*读记录返回6984(Read record returns to 6984)*/
#define READREC_RETURN_6985						(READRECERR_BASE - 55) 	/*读记录返回6985(Read record returns to 6985)*/
#define READREC_RETURN_6A81						(READRECERR_BASE - 56) 	/*读记录返回6A81(Read record returns to 6A81)*/
#define READREC_RETURN_6A82						(READRECERR_BASE - 57) 	/*读记录返回6A82(Read record returns to 6A82)*/
#define READREC_RETURN_6A83						(READRECERR_BASE - 58) 	/*读记录返回6A83(Read record returns to 6A83)*/
#define READREC_RETURN_6A88						(READRECERR_BASE - 59) 	/*读记录返回6A88(Read record returns to 6A88)*/
#define READREC_RETURN_6400						(READRECERR_BASE - 60) 	/*读记录返回6400(Read record returns to 6400)*/
#define READREC_RETURN_6500						(READRECERR_BASE - 61) 	/*读记录返回6500(Read record returns to 6500)*/
#define READREC_RETURN_9001						(READRECERR_BASE - 62) 	/*读记录返回9001(Read record returns to 9001)*/
#define READREC_RETURN_5ADUP					(READRECERR_BASE - 63)	/*读记录返回5A重复(Read record returns 5A repeat)*/
#define READREC_RETURN_5F24DUP					(READRECERR_BASE - 64)	/*读记录返回5F24重复(Read record returns 5F24 repeat)*/
#define READREC_RETURN_57DUP					(READRECERR_BASE - 65)	/*读记录返回57重复(Read record returns 57 repeat)*/
#define READREC_QUIT							(READRECERR_BASE - 66)	/*读记录取消交易(Read record cancel transaction)*/
#define READREC_DATEWRONG						(READRECERR_BASE - 67)	/*读记录返回的卡片日期不对(The date of the card returned by the read record is incorrect)*/
#define READREC_NOSAME5A57						(READRECERR_BASE - 68)	/*读记录返回的5A跟57不匹配(5A and 57 returned by the read record do not match)*/

#define READREC_NO9F08							(READRECERR_BASE - 69)  /*读记录没有返回tag 9F08(Read record without return tag 9F08)*/
#define READREC_NO9F02							(READRECERR_BASE - 70)  /*读记录没有返回tag 9F02(Read record without return tag 9F02)*/
#define READREC_MAX_LIMIT_EXCEEDED				(READRECERR_BASE - 71) 	/*读记录超过最大限额(Read records exceed the maximum limit)*/
#define READREC_ERR_PP_ERR_9F4A					(READRECERR_BASE - 72)	/*读记录返回tag 9F4A错误(Read record returns tag 9F4A error)*/
#define READREC_ERR_PP_NO9F4A					(READRECERR_BASE - 73) 	/*读记录没有返回tag 9F4A(Read record without return tag 9F4A)*/
#define READREC_ERR_PP_NO8F						(READRECERR_BASE - 74) 	/*读记录没有返回tag 8F(Read record without return tag 8F)*/
#define READREC_ERR_PP_NO90						(READRECERR_BASE - 75)  /*读记录没有返回tag 90(Read record without return tag 90)*/
#define READREC_ERR_PP_NO9F32					(READRECERR_BASE - 76)  /*读记录没有返回tag 9F32(Read record without return tag 9F32)*/
#define READREC_ERR_PP_NO93						(READRECERR_BASE - 77)  /*读记录没有返回tag 93(Read record without return tag 93)*/
#define READREC_ERR_PP_NO9F46					(READRECERR_BASE - 78)  /*读记录没有返回tag 9F46(Read record without return tag 9F46)*/
#define READREC_ERR_PP_NO9F47					(READRECERR_BASE - 79)  /*读记录没有返回tag 9F47(Read record without return tag 9F47)*/
#define READREC_ERR_PP_CAPKNOSURPT				(READRECERR_BASE - 80)  /*capk不支持(Capk does not support)*/
#define READREC_ERR_PP_CARDDATA					(READRECERR_BASE - 81)  /*ICC返回读卡器数据，因此终止（ICC return CardReader Data,so terminate）*/
#define READREC_ERR_PW_NO57						(READRECERR_BASE - 82) 	/*读记录没有返回tag57(Read record without return tag 57)*/
#define READREC_FMT_ERROR						(READRECERR_BASE - 83) 	/*读记录返回的数据格式错误（Read record return data format error）*/
#define READREC_NO5F24							(READRECERR_BASE - 84) 	/*读记录没有返回5F24(Read record without return tag 5F24)*/
#define READREC_TAG9F42_INVALID					(READRECERR_BASE - 85)	/*读记录TAG 9F42无效（Read record TAG 9F42 INVALID*/
#define READREC_TAG5F25_INVALID					(READRECERR_BASE - 86)	/*读记录TAG 5F25无效（Read record TAG 5F25 INVALID*/
#define READREC_TAG5A_INVALID					(READRECERR_BASE - 87)	/*读记录TAG 5A无效（Read record TAG 5A INVALID*/
#define READREC_TAG9F07_INVALID					(READRECERR_BASE - 88)	/*读记录TAG 9F07无效（Read record TAG 9F07 INVALID*/
#define READREC_TAG5F20_INVALID					(READRECERR_BASE - 89)	/*读记录TAG 5F20无效（Read record TAG 5F20 INVALID*/
#define READREC_TAG9F0D_INVALID					(READRECERR_BASE - 90)	/*读记录TAG 9F0D无效（Read record TAG 9F0D INVALID*/
#define READREC_TAG9F0E_INVALID					(READRECERR_BASE - 91)	/*读记录TAG 9F0E无效（Read record TAG 9F0E INVALID*/
#define READREC_TAG9F0F_INVALID					(READRECERR_BASE - 92)	/*读记录TAG 9F0F无效（Read record TAG 9F0F INVALID*/
#define READREC_TAG5F34_INVALID					(READRECERR_BASE - 93)	/*读记录TAG 5F34无效（Read record TAG 5F34 INVALID*/
#define READREC_TAG9F11_INVALID					(READRECERR_BASE - 94)	/*读记录TAG 9F11无效（Read record TAG 9F11 INVALID*/
#define READREC_TAG5F28_INVALID					(READRECERR_BASE - 95)	/*读记录TAG 5F28无效（Read record TAG 5F28 INVALID*/
#define READREC_TAG8F_INVALID					(READRECERR_BASE - 96)	/*读记录TAG 8F无效（Read record TAG 8F INVALID*/
#define READREC_NO9F6D							(READRECERR_BASE - 97) 	/*读记录没有返回tag 9F6D(Read record without return tag 9F6D)*/
#define READREC_NO5F28							(READRECERR_BASE - 98) 	/*读记录没有返回tag 5F28(Read record without return tag 5F28)*/
#define READREC_NO9F07							(READRECERR_BASE - 99) 	/*读记录没有返回tag 9F07(Read record without return tag 9F07)*/

//Data Authentication
#define SECERR_BASE								(EMV_ERR_BASE - 1600)	/*数据认证基准错误码（Data authentication benchmark error code）*/
#define SECERR_ICCDATAMISSING					(SECERR_BASE - 1)		/*ic卡数据丢失（Ic card data loss）*/
#define SECERR_CERTLENGTH						(SECERR_BASE - 2)		/*数据认证证书长度错误（Data Authentication Certificate Length Error）*/
#define SECERR_RECOVERKEY						(SECERR_BASE - 3)		/*数据认证RSA恢复公钥失败（Data authentication RSA failed to recover public key）*/
#define SECERR_DATAHEADER						(SECERR_BASE - 4)		/*数据认证恢复的数据头错误（Data authentication recovery data header error）*/
#define SECERR_FORMATWRONG						(SECERR_BASE - 5)		/*数据认证证书开头第二字节错（The second byte of the data authentication certificate is wrong）*/
#define SECERR_DATATAILER						(SECERR_BASE - 6)		/*数据认证证书倒数第二字节错（Second to last byte of the data authentication certificate is wrong）*/
#define SECERR_RECOVERHASH						(SECERR_BASE - 7)		/*数据认证哈希值校验不匹配（Data authentication hash value check does not match）*/
#define SECERR_ALGORITHM						(SECERR_BASE - 8)		/*数据认证算法标识错（Data authentication algorithm identification error）*/
#define SECERR_MODULUSLENGTH					(SECERR_BASE - 9)		/*数据认证证书模长错（Data Authentication Certificate Module Length error）*/
#define SECERR_CERTEXPIRED						(SECERR_BASE - 10)		/*数据认证证书过期（Data Authentication Certificate expires）*/
#define SECERR_CERTREVOC						(SECERR_BASE - 11)		/*数据认证认证中心公钥黑名单（Data authentication and certification center public key blacklist）*/
#define SECERR_SSADLEN							(SECERR_BASE - 12)		/*数据认证SSAD长度错（Data authentication SSAD length error）*/
#define SECERR_RECOVERSSAD						(SECERR_BASE - 13)		/*数据认证恢复SSAD错（Data authentication recovery SSAD error）*/
#define SECERR_9F4AERR							(SECERR_BASE - 14)		/*数据认证9F4A错（Data authentication 9F4A error）*/
#define SECERR_NONEDDOL							(SECERR_BASE - 15)		/*数据认证没有默认DDOL（Data authentication without default DDOL）*/
#define SECERR_NO9F37							(SECERR_BASE - 16)		/*数据认证没有tag 9F37(Data authentication without tag 9F37)*/
#define SECERR_DDOLPROCESS						(SECERR_BASE - 17)		/*数据认证DDOL打包错(Data authentication DDOL package error)*/
#define SECERR_SDADLEN							(SECERR_BASE - 18)		/*数据认证SSAD长度错(Data authentication SSAD length error)*/
#define SECERR_NOISSUERPK						(SECERR_BASE - 19)		/*数据认证没有发卡行公钥(Data authentication without issuing bank public key)*/
#define SECERR_NOICCPK							(SECERR_BASE - 20)		/*数据认证没有ic卡公钥(Data authentication without IC card public key)*/
#define SECERR_GETCHALLENGE						(SECERR_BASE - 21)		/*数据认证获取随机数错误(Data Authentication Acquisition Random Number error)*/
#define SECERR_RECOVERENCPIN					(SECERR_BASE - 22)		/*数据认证恢复RSA数据错误(Data Authentication Recovery RSA Data error)*/
#define SECERR_NO9F4B							(SECERR_BASE - 23)		/*数据认证没有tag 9F4B(Data authentication without tag 9F4B)*/
#define SECERR_RECOVERSDAD						(SECERR_BASE - 24)		/*数据认证恢复SDAD错(Data Authentication Recovery SDAD Error)*/
#define SECERR_SCDADLEN							(SECERR_BASE - 25)		/*数据认证SCDAD长度错(Data Authentication SCDAD Length Error)*/
#define SECERR_RECOVERSCDAD						(SECERR_BASE - 26)		/*数据认证恢复SCDAD错(Data Authentication Recovery SCDAD Error)*/
#define SECERR_CIDNOTMATCHED					(SECERR_BASE - 27)		/*数据认证CID值不匹配(Data Authentication CID Value Mismatch)*/
#define SECERR_CDAHASH1							(SECERR_BASE - 28)		/*数据认证哈希校验1错误(Data authentication hash check 1 error)*/
#define SECERR_CDAHASH2							(SECERR_BASE - 29)		/*数据认证哈希校验2错误(Data authentication hash check 2 error)*/
#define SECERR_FAILINREADREC					(SECERR_BASE - 30)		/*数据认证读记录错误(Data Authentication Reading Record Error)*/
#define SECERR_PANNOTMATCH						(SECERR_BASE - 31)		/*数据认证卡号不匹配(Data Authentication Card Number Mismatch)*/
#define SECERR_CAPKNOTFOUND						(SECERR_BASE - 32)		/*数据认证认证中心公钥不匹配(Data Authentication and Authentication Center Public Key Mismatch)*/
#define SECERR_NO9F36							(SECERR_BASE - 33)		/*数据认证没有tag 9F36(Data authentication without tag 9F36)*/
#define SECERR_FDDAVERNOTSUP					(SECERR_BASE - 34)		/*数据认证FDDA版本不支持(Data Authentication FDDA Version does not support)*/
#define SECERR_FDDA9F69LENERR					(SECERR_BASE - 35)		/*数据认证FDDA 9F69长度错(Data Authentication FDDA 9F69 Length Error)*/
#define SECERR_ALGORITHMPARAM					(SECERR_BASE - 36)		/*数据认证国密椭圆参数标识错误(Data Authentication national secret Elliptic Parameter Identification Error)*/
#define SECERR_SM2VERIFY						(SECERR_BASE - 40)		/*数据认证SM2认证签名错误（Data authentication SM2 authentication signature error）*/
#define SECERR_9F69								(SECERR_BASE - 41)		/*数据认证9F69错误（Data authentication 9F69 error）*/
#define SECERR_FDDA_VER							(SECERR_BASE - 42)		/*数据认证FDDA版本错误（Data authentication FDDA version error）*/
#define SECERR_ATC								(SECERR_BASE - 43)		/*数据认证ATC错误（Data authentication ATC error）*/
#define SECERR_INALCMD_ERRDATA					(SECERR_BASE - 44)		/*数据认证强制数据错误（Data authentication forces data errors）*/
#define SECERR_NOPAN							(SECERR_BASE - 45)		/*数据认证没有tag 5A（Data authentication without tag 5A）*/
#define SECERR_ICCDDLEN							(SECERR_BASE - 46)		/*ICC动态数据的长度较小（the length of ICC Dynamic Data is less）*/
#define SECERR_RRP								(SECERR_BASE - 47)		/*PayPass CDA RRP不匹配(paypass CDA RRP not match)*/
#define SECERR_ERRHASH_INDICATOR 				(SECERR_BASE - 48)		/*数据认证错误的hash指示(Hash indication of data authentication error)*/
#define SECERR_NOHASH_ALGORITHM					(SECERR_BASE - 49)		/*数据认证没有hash算法(Data authentication without hash algorithm)*/
#define SECERR_NO9F4BOR92						(SECERR_BASE - 50) 		/*数据认证没有返回tag9F4B或者tag92(Data authentication without return tag9F4B or tag92)*/
#define SECERR_FAILDDA							(SECERR_BASE - 51) 		/*数据认证DDA失败(Data authentication DDA failed)*/
#define SECERR_NOREMAINDER						(SECERR_BASE - 52) 		/*没有92或9F48(Without 92 or 9F48)*/

//Cardholder Verification
#define CVERR_BASE								(EMV_ERR_BASE - 1700)	/*持卡人认证基准错误码(Cardholder Certification benchmark Error Code)*/
#define CVERR_8ELENWRONG						(CVERR_BASE - 1) 		/*tag8E长度错误(tag8E length error)*/
#define CVERR_OFFLINEPIN						(CVERR_BASE - 2)		/*输入脱机pin失败(Input offline pin failed)*/
#define CVERR_ONLINEPIN							(CVERR_BASE - 3)		/*输入联机pin失败(Input online pin failed)*/
#define CVERR_AIPCVM_NOSUPP						(CVERR_BASE - 4)		/*卡片AIP不支持CVM(Card AIP does not support CVM)*/
#define CVERR_NO8E								(CVERR_BASE - 5)		/*没有CVM list(Without CVM list)*/
#define CVERR_NOCVMRULES						(CVERR_BASE - 6)		/*没有CVM rules(Without CVM rules)*/

//Terminal Action Analysis and Card Action Analysis
#define GACERR_BASE								(EMV_ERR_BASE - 1800)	/*终端以及卡片行为分析基准错误码(Terminal and card behavior analysis benchmark error code)*/
#define GACERR_WRONGREQ							(GACERR_BASE - 1)  	 	/*GAC应答错误(GAC response error)*/
#define GACERR_CDOLPACKET						(GACERR_BASE - 2)		/*GAC CDOL数据打包错误(GAC CDOL data pack error)*/
#define GACERR_GACCMD							(GACERR_BASE - 3)		/*GAC 返回码错误(GAC return code error)*/
#define GACERR_TLVDEOCDE						(GACERR_BASE - 4)		/*GAC 返回数据解析错误(GAC returns data parsing error)*/
#define GACERR_CDAREQUIRE77						(GACERR_BASE - 5)		/*Tag 80 templet cda requested and AAC not returned*/
#define GACERR_TAG80VALLEN						(GACERR_BASE - 6)		/*GAC返回Tag 长度错误(GAC Returns Tag Length Error)*/
#define GACERR_77NO9F27							(GACERR_BASE - 7)		/*GAC返回77模版没有返回9F27（GAC returns 77 templates without returning 9F27）*/
#define GACERR_77NO9F36							(GACERR_BASE - 8)		/*GAC返回77模版没有返回9F36（GAC returns 77 templates without returning 9F36）*/
#define GACERR_77NO9F4B							(GACERR_BASE - 9)		/*GAC返回77模版没有返回9F4B（GAC returns 77 templates without returning 9F4B）*/
#define GACERR_NOT77OR80						(GACERR_BASE - 10)		/*GAC返回不是77或者80模版（GAC returns not 77 or 80 templates）*/
#define GACERR_AARRET							(GACERR_BASE - 11)		/*GAC返回的CID请求AAR(CID request AAR returned by GAC)*/
#define GACERR_WRONGCID							(GACERR_BASE - 12)		/*GAC返回错误的CID(GAC returns the wrong CID)*/
#define GACERR_77NO9F26							(GACERR_BASE - 13)		/*GAC返回77模版没有返回9F26（GAC returns 77 templates without returning 9F26）*/
#define GACERR_NO9F10							(GACERR_BASE - 14)		/*GAC没有返回9F10(GAC without return 9F10)*/
#define GACERR_SPECIAL_PAD0						(GACERR_BASE - 15)		/*GAC返回数据为全零(GAC returns zero data)*/
#define GACERR_DRDOLPACKET						(GACERR_BASE - 16)		/*DRDOL数据打包失败(DRDOL Data Pack Failed)*/
#define GACERR_RAC_SW12_NO9000					(GACERR_BASE - 17)		/*RAC应答码不是9000(RAC response code is not 9000)*/
#define GACERR_80_9F36_EXIST					(GACERR_BASE - 18)		/*GAC返回80模版存在9F36(GAC returns 80 templates with 9F36)*/
#define GACERR_ECMAC							(GACERR_BASE - 20)		/*电子现金GAC错误(GAC Error in Electronic Cash)*/
#define GACERR_GETECBALANCE						(GACERR_BASE - 21)		/*获取EC余额失败(Failed to obtain EC balance)*/
#define GACERR_EC_BALANCELACK					(GACERR_BASE - 22)      /*EC余额不足(EC Balance not enough)*/
#define GACERR_ECONLY_GOONLINE					(GACERR_BASE - 23)      /*纯电子现金卡要求EC联机则拒绝(Pure electronic cash card requires EC online but refuse)*/
#define GACERR_EC_THRESHOLD						(GACERR_BASE - 24)      /*EC 余额 <  交易金额 + 阈值,导致联机(EC balance < transaction amount + threshold, resulting in online)*/
#define GACERR_RETURN_81						(GACERR_BASE - 25)		/*GAC返回77模版没有返回81（GAC returns 77 templates without returning 81）*/
#define GACERR_RETURN_9F01						(GACERR_BASE - 26)		/*GAC返回77模版没有返回9F01（GAC returns 77 templates without returning 9F01）*/
#define GACERR_RETURN_ERROR						(GACERR_BASE - 27)		/*GAC返回CID长度错误(GAC returns CID length error)*/
#define GACERR_9F10ERROR						(GACERR_BASE - 28)      /*GAC返回9F10格式错(GAC returns 9F10 format error)*/
#define GACERR_77_GACNOC5						(GACERR_BASE - 29)		/*MCCS GAC返回77模版没有返回tag C5（GAC returns 77 templates without returning tag C5）*/
#define GACERR_77_ALLNOC5						(GACERR_BASE - 30)		/*MCCS GPO跟GAC都没有返回tag C5(Neither MCCS GPO nor GAC returned to tag C5)*/
#define GACERR_DISTINCTCVMINFOR					(GACERR_BASE - 31)		/*MCCS GPO跟GAC返回的tage C5 CVM方式不同(MCCS GPO and GAC return tage C5 CVM different ways)*/
#define GACERR_ECHO								(GACERR_BASE - 32)		/*MCCS GAC apdu没有返回，需要进入torn交易(MCCS GAC apdu without return, need to enter torn transaction)*/
#define GACERR_77NO9F4BHAVE9F26					(GACERR_BASE - 33)		/*MCCS GAC没有9F4B但是有9F26(MCCS GAC without 9F4B but has 9F26)*/

#define GACERR_CDOL1_PACK						(GACERR_BASE -34)		/*第一次GAC CDOL打包失败(First GAC CDOL package failed)*/
#define GACERR_FMT_ERROR						(GACERR_BASE -35)		/*JCB GAC返回的77模版格式错(The 77 template format returned by JCB GAC is wrong.)*/
#define GACERR_TAG_DUP							(GACERR_BASE -36)		/*JCB 存储GAC返回的标签失败(JCB failed to store the label returned by GAC)*/
#define GACERR_CID_MISSING						(GACERR_BASE -37)		/*JCB 存储GAC没有返回CID(JCB storage GAC without return CID)*/
#define GACERR_ATC_MISSING						(GACERR_BASE -38)		/*JCB GAC没有返回ATC(JCB GAC without return ATC)*/
#define GACERR_9F4B_MISSING						(GACERR_BASE -39)		/*JCB GAC没有返回9F4B(JCB GAC without return 9F4B)*/
#define GACERR_AC_MISSING						(GACERR_BASE -40)		/*JCB GAC没有返回AC(JCB GAC without return AC)*/
#define GACERR_9F50_MISSING						(GACERR_BASE -41)		/*JCB GAC没有返回9F50(JCB GAC without return 9F50)*/
#define GACERR_ACTYPE_ERR						(GACERR_BASE -42)		/*JCB GAC返回的AC类型错(JCB GAC返回的AC类型错)*/
#define GACERR_AAC								(GACERR_BASE -43)		/*JCB GAC返回AAC(JCB GAC return AAC)*/
#define GACERR_9F5F_INVALID						(GACERR_BASE -44)		/*JCB GAC返回9F5F格式错(JCB GAC returns 9F5F format error)*/
#define GACERR_9F60_INVALID						(GACERR_BASE -45)		/*JCB GAC返回9F60格式错(JCB GAC returns 9F60 format error)*/

#define CVM_NOT_SUPPORT							(GACERR_BASE -46)		/*JCB 不支持CVM认证(JCB does not support CVM authentication)*/
#define GACERR_LEGACY_FMT						(GACERR_BASE -47)		/*JCB LEGACY 模式GAC返回不为80模版(JCB LEGACY mode GAC returns not 80 template)*/
#define GACERR_LEGACY_DENIAL					(GACERR_BASE -48)		/*JCB LEGACY 模式GAC 返回的CID不为ARQC(JCB LEGACY mode GAC returned CID is not ARQC)*/
#define GACERR_QUIT								(GACERR_BASE -49)		/*GAC取消交易(GAC cancels the transaction)*/
#define GACERR_9F27LENWRONG						(GACERR_BASE -50)		/*9F27长度错(9F27 length error)*/
#define GACERR_9F36LENWRONG						(GACERR_BASE -51)		/*9F36长度错(9F36 length error)*/
#define GACERR_9F26LENWRONG						(GACERR_BASE -52)		/*9F26长度错(9F26 length error)*/

#define GMDERR_BASE								(GACERR_BASE - 60)		/*JCB GMD命令基准错误码(JCB GMD command benchmark Error Code)*/
#define GMDERR_MDOL_PACK						(GMDERR_BASE - 1)		/*JCB GMD打包MDOL失败(JCB GMD failed to package MDOL)*/
#define GMDERR_FMT_ERROR						(GMDERR_BASE - 2)		/*JCB GMD命令返回数据错(JCB GMD command returns data error)*/
#define GMDERR_TK2ED_MISSING					(GMDERR_BASE - 3)		/*JCB GMD命令没有返回tag57(JCB GMD command without return tag57)*/
#define GMDERR_MS_DENIAL						(GMDERR_BASE - 4)		/*JCB MS 模式GMD返回码为6300(JCB MS mode GMD return code is 6300)*/

//echo command
#define EHCO_BASE								(GACERR_BASE - 70)		/*echo 命令基准错误码(echo command benchmark Error Code)*/
#define EHCO_FMT_ERROR							(EHCO_BASE - 1)			/*JCB echo 命令返回数据格式错误(JCB echo command returns data format error)*/
#define EHCO_TAG_DUP							(EHCO_BASE - 2)			/*JCB echo 返回数据有重复tag(JCB echo returns data with duplicate tag)*/
#define EHCO_CID_MISSING						(EHCO_BASE - 3)			/*JCB echo 没有返回CID(JCB echo without return CID)*/
#define EHCO_ATC_MISSING						(EHCO_BASE - 4)			/*JCB echo 没有返回ATC(JCB echo without return ATC)*/
#define EHCO_9F4B_MISSING						(EHCO_BASE - 5)			/*JCB echo 没有返回9F4B(JCB echo without return 9F4B)*/
#define EHCO_AC_MISSING							(EHCO_BASE - 6)			/*JCB echo 没有返回AC(JCB echo without return AC)*/
#define EHCO_9F50_MISSING						(EHCO_BASE - 7)			/*JCB echo 没有返回9F50(JCB echo without return 9F50)*/
#define EHCO_QUIT								(EHCO_BASE - 8)			/*echo command取消交易(Echo command cancel transaction*/

//Script Processing and Completion
#define COMERR_BASE								(EMV_ERR_BASE - 1900)	/*基本以及EMV完成基准错误码(Basic and EMV complete benchmark error code)*/
#define COMERR_BASE_SCRIPT						(COMERR_BASE - 1)		/*脚本超限(Script exceeds limit)*/
#define COMERR_BASE_SCRIPTEMPTY					(COMERR_BASE - 2)       /*EC脚本空(EC script empty)*/
#define COMERR_BASE_ECLOADAMOUNT				(COMERR_BASE - 3)    	/*EC圈存金额超出限额(EC deposit amount exceeds the limit)*/
#define COMERR_BASE_SCRIPTRET					(COMERR_BASE - 4)      	/*脚本执行错误(Script execution error)*/
#define COMERR_BASE_SCRIPTERROR					(COMERR_BASE - 5)     	/*脚本错误(Script error)*/

//Flash Card
#define FLASHCARD_ERR_BASE						(EMV_ERR_BASE - 2000)		/*Qpboc闪卡基准错误码(Qpboc flash card benchmark error code)*/
#define FLASHCARD_ERR_PAN_NO_EQUAL				(FLASHCARD_ERR_BASE - 1)	/*闪卡卡号不匹配(Flash card number does not match)*/
#define FLASHCARD_ERR_ATC_NO_EQUAL				(FLASHCARD_ERR_BASE - 2)	/*闪卡应用交易计数器不匹配(Flash card application transaction counter does not match)*/
#define FLASHCARD_ERR_CURCODE_NO_EQUAL			(FLASHCARD_ERR_BASE - 3)	/*闪卡货币代码不匹配(Flash card currency code does not match)*/
#define FLASHCARD_ERR_BALANCE_NO_EQUAL			(FLASHCARD_ERR_BASE - 4)	/*闪卡电子现金余额不匹配(Flash card electronic cash balance does not match)*/
#define FLASHCARD_ERR_NO_PURCHASE				(FLASHCARD_ERR_BASE - 5)	/*闪卡未发生交易(Flash card without occor transaction)*/
#define FLASHCARD_ERR_PURCHASE_GPO				(FLASHCARD_ERR_BASE - 6)	/*闪卡GPO错误(Flash card GPO error)*/
#define FLASHCARD_ERR_LAST_RECORD_NO_RESPONSE	(FLASHCARD_ERR_BASE - 7)	/*闪卡最后一条读记录没有响应(The last read record of the flash card is not responding)*/
#define FLASHCARD_ERR_NO_GET_PAN				(FLASHCARD_ERR_BASE - 8)	/*闪卡获取不到卡号(Flash card can not get the card number)*/
#define FLASHCARD_ERR_PAN_NOT_SAME				(FLASHCARD_ERR_BASE - 9)	/*闪卡卡号不相同(Flash card number is different)*/
#define FLASHCARD_ERR_AID_NOT_SAME				(FLASHCARD_ERR_BASE - 10)	/*闪卡aid不相同(Flash card aid is not the same)*/
#define FLASHCARD_ERR_PAN_NO_RESPONSE			(FLASHCARD_ERR_BASE - 11)	/*闪卡读最后一条记录取卡号未响应(The last record of the flash card is not responding.)*/


//Preprocess & qPboc errorcode
#define RFERR_BASE								(EMV_ERR_BASE - 2100)	/*预处理基准错误码(Preprocessing benchmark error code)*/
#define RFERR_PREPROCESS_PARAFILE				(RFERR_BASE - 1)        /**< 预处理参数文件错误(Preprocessing parameter file error) */
#define RFERR_PREPROCESS_AMTQUIT				(RFERR_BASE - 2)        /**< 预处理输入金额用户退出(Preprocessing input amount user exits) */
#define RFERR_PREPROCESS_AMTTIMEOUT				(RFERR_BASE - 3)        /**< 预处理输入金额 超时(Preprocessing input amount timeout) */
#define RFERR_PREPROCESS_AMTFAIL				(RFERR_BASE - 4)        /**< 预处理输入金额 失败(Preprocessing input amount failed) */
#define RFERR_PREPROCESS_AMTLIMITOVER			(RFERR_BASE - 5)        /**< 预处理输入金额 超出限额(Preprocessed input amount exceeds the limit) */
#define RFERR_PREPROCESS_REQONLINE				(RFERR_BASE - 6)        /**< 预处理要求联机,终端不能联机(Preprocessing requires online, terminal cannot be online) */
#define RFERR_PREPROCESS_NOAID					(RFERR_BASE - 7)        /**< AID为0(AID is 0) */
#define RFERR_ICCDEACTIVE						(RFERR_BASE - 11)       /**< 射频卡去卡失败(RF card removal failed) */
#define RFERR_ICCRETURNERROR					(RFERR_BASE - 12)       /**< 卡片返回错误(Card returned error) */
#define RFERR_READAPPDATA						(RFERR_BASE - 13)       /**< 读应用数据失败(Failed to read application data) */
#define RFERR_BLKCARD							(RFERR_BASE - 14)       /**< 卡片黑名单(Card blacklist) */
#define RFERR_ICCNOEFFECT						(RFERR_BASE - 15)       /**< 卡片未生效(The card is not valid) */
#define RFERR_ICCEXPIRE							(RFERR_BASE - 16)       /**< 卡片已失效(Card has invalid) */
#define RFERR_DATAAUTH							(RFERR_BASE - 17)       /**< 卡片数据认证失败(Card data authentication failed) */
#define RFERR_TRACK2EDATA						(RFERR_BASE - 18)       /**< 卡片二磁等价数据失败(Card second magnetic equivalent data failed) */
#define RFERR_ICCFCHECK							(RFERR_BASE - 19)       /**< 频度检查超限(Frequency check exceeds limit) */
#define RFERR_ECPURE_CANNOT_ONLINE				(RFERR_BASE - 20)       /**< 纯电子现金卡不能联机（Pure electronic cash card can not be online） */
#define RFERR_CARD_DENIAL						(RFERR_BASE - 21)       /**< 卡片拒绝（Card rejection） */
#define RFERR_NOODA								(RFERR_BASE - 22)       /**< 卡片AIP没有数据认证（Card AIP has no data authentication） */
#define RFERR_9F10CID							(RFERR_BASE - 23)       /**< 卡片9F10中返回交易结果错误 (Card 9F10 return transaction result error)*/
#define RFERR_DATAAUTHNOPAN						(RFERR_BASE - 24)       /**< 卡片数据认证无卡号信息(Card data authentication without card number information)*/
#define RFERR_FDDAFAIL_SUPPBOC					(RFERR_BASE - 25)       /**< FDDA失败，卡片和终端支持接触PBOC(FDDA failed, card and terminal support contact PBOC)*/
#define RFERR_ODAFAIL_DENIAL					(RFERR_BASE - 26)       /**< ODA失败，终端拒绝交易(ODA failed, terminal refused to trade)*/

//Paypass
#define PPERR_BASE								(EMV_ERR_BASE - 2200)	/*paypass基准错误码(Paypass benchmark error code)*/
#define PPERR_TRANS_LIMITOVER					(PPERR_BASE - 1)		/*交易金额超过所有终端非接交易限额（transaction usAmount over all Terminal Contactless Transaction Limit） */
#define PPERR_TRACK								(PPERR_BASE - 2)		/*Paypass错误跟踪数据（paypass error track data）*/
#define PPERR_TRACK_PCVC3						(PPERR_BASE - 3)  	 	/*paypass PCVC3(paypass pcvc3)*/
#define PPERR_TRACK_PUNATC						(PPERR_BASE - 4)		/*paypass punatc*/
#define PPERR_TRACK_NATC						(PPERR_BASE - 5)		/*paypass natc*/
#define PPERR_TRACK_KLTT						(PPERR_BASE - 6) 		/*k_track < t_track*/
#define PPERR_TRACK_NUN							(PPERR_BASE - 7)		/*磁道数据错误数量未知（track data wrong unpredictable number）*/
#define PPERR_TRACK1_PAN						(PPERR_BASE - 8) 		/*没有相同的第二磁道（not the same as track2）*/
#define PPERR_TRACK1_EXPIREDATE					(PPERR_BASE - 9)   		/*没有相同的第二磁道（not the same as track2）*/
#define PPERR_CCC_CMD							(PPERR_BASE - 10) 		/*计算加密checksum错误（compute cryptographic checksum error） */
#define PPERR_CCC_RESPONSE						(PPERR_BASE - 11) 		/*计算加密checksum应答错误（compute cryptographic checksum response error） */
#define PPERR_CCC_UDOL_NO9F6A					(PPERR_BASE - 12)   	/*UDOL没有9F6A（UDOL NO 9F6A） */
#define PPERR_CCC_TRACK1CVC3					(PPERR_BASE - 13)     	/*CCC命令返回磁道2 cvc3错误（CCC command return track2 cvc3 error） */
#define PPERR_CCC_TRACK2CVC3					(PPERR_BASE - 14)   	/*CCC命令返回磁道1 cvc3错误（CCC command return track1 cvc3 error） */
#define PPERR_CCC_ATC							(PPERR_BASE - 15)   	/*CCC命令返回atc错误（CCC command return atc error） */
#define PPERR_CCC_NODEF_UDOL					(PPERR_BASE - 16)   	/*没有默认UDOL（NO default UDOL ）*/
#define PPERR_CCC_DOLPACKET						(PPERR_BASE - 17)	 	/*UDOL打包错误（UDOL pack error） */
#define PPERR_PP_ENTERPIN						(PPERR_BASE - 18)    	/*输入pin错误（input pin error）  */
#define PPERR_READAPPDATA						(PPERR_BASE - 19)    	/*读取应用错误（read app  error）  */
#define PPERR_PREPROCESS_AMTQUIT				(PPERR_BASE - 20)    	/*预处理输入金额用户退出（Preprocessing input amount user exits）*/
#define PPERR_PREPROCESS_AMTTIMEOUT				(PPERR_BASE - 21)    	/*预处理输入金额 超时（Preprocessing input amount timeout）*/
#define PPERR_PREPROCESS_AMTFAIL				(PPERR_BASE - 22)    	/*预处理输入金额 失败（Preprocessing input amount failed）*/
#define PPERR_SDAFAIL							(PPERR_BASE - 23)    	/*SDA失败交易中止（SDA failed transaction terminate）*/
#define PPERR_TRACK1_LEN						(PPERR_BASE - 24)    	/*track1 长度错误（Track1 length error）*/
#define PPERR_TRACK2_LEN						(PPERR_BASE - 25)    	/*track2 长度错误（Track2 length error）*/
#define PPERR_SAVEDATA							(PPERR_BASE - 26)    	/*保存数据错误（save data error）*/
#define PPERR_READPARAM							(PPERR_BASE - 27)    	/*读取参数配置错误（read param config error） */
#define PPERR_CCC_PCII							(PPERR_BASE - 28)    	/*CCC命令返回PCII错误（CCC command return PCII error） */
#define PPERR_CCC_TRACK_DATA_MISSING			(PPERR_BASE - 29)    	/*用于专门表示数据丢失错误（Used to specifically indicate data loss errors）*/

//Paywave
#define PWERR_BASE								(EMV_ERR_BASE - 2300)	/*paywave基准错误码(Paywave benchmark error code)*/
#define PWERR_9F26								(PWERR_BASE - 5)		/*读取记录命令中有错误或没有9F26(error or no 9F26 in read record command)*/
#define PWERR_9F36								(PWERR_BASE - 6)		/*读取记录命令中有错误或没有9F36(error or no 9F36 in read record command)*/
#define PWERR_9F10								(PWERR_BASE - 7)		/*读取记录命令中有错误或没有9F10(error or no 9F10 in read record command*/
#define PWERR_57								(PWERR_BASE - 8)		/*读取记录命令中有错误或没有57(error or no 57 in read record command)*/
#define PWERR_9F27								(PWERR_BASE - 9)		/*读取记录命令中有错误或没有9F27(error or no 9F27 in read record command)*/
#define PWERR_INTER_TRANS						(PWERR_BASE - 10)       /*国际交易(international transaction)*/
#define PWERR_NO9F74							(PWERR_BASE - 11)       /*没有返回9F74(Did not return 9F74) */
#define PWERR_PW_ENTERPIN						(PWERR_BASE - 18)       /*定义请求联机错误(define request online error)*/
#define PWERR_AIP_NOSUPPORTFDDA					(PWERR_BASE - 19)       /*AIP不支持FDDA(aip no support fdda)*/

//Ruoay
#define RUERR_BASE								(EMV_ERR_BASE - 2400)	/*Rupay基准错误码(Rupay benchmark error code)*/
#define RUERR_SERVICE_KCV_WRONG					(RUERR_BASE - 1)		/*Rupay kcv错误(Rupay KCV error)*/

//MIR
#define MIR_BASE								(EMV_ERR_BASE - 2500)	/*MIR基准错误码(MIR benchmark error code)*/
#define MIR_PROTOCOLWRONG						(MIR_BASE - 1)			/*MIR协议值错误(MIR protocol value error)*/
#define MIR_AIP_NOSUPPORT_EMV					(MIR_BASE - 2)			/*MIR aip不支持EMV模式(MIR aip does not support EMV mode)*/
#define MIR_NOHAVE_DF6F							(MIR_BASE - 3)			/*MIR 协议2没有DF6F ODOL(MIR Protocol 2 does not have DF6F ODOL)*/
#define MIR_DF6F_WRONG							(MIR_BASE - 4)			/*MIR 协议2 DF6F ODOL错误(MIR Protocol 2 DF6F ODOL error)*/
#define MIR_SERVICE_NOTALLOW					(MIR_BASE - 5)			/*MIR 协议2服务不支持（MIR protocol 2 service not allowed）*/
#define MIR_SDADMISSING							(MIR_BASE - 6)			/*MIR 没有SDAD（MIR without SDAD）*/
#define MIR_TRANSPERFORM_BADSW					(MIR_BASE - 7)			/*MIR 执行交易命令不返回9000（MIR executes transaction commands without returning 9000）*/
#define MIR_TRANSPERFORM_NOT77					(MIR_BASE - 8)			/*MIR 执行交易命令不返回77模版（MIR executes transaction commands without returning 77 templates）*/
#define MIR_TRANSPERFORM_77NOT9F27				(MIR_BASE - 9)			/*MIR 执行交易命令不返回9F27（MIR executes transaction commands without returning 9F27)*/
#define MIR_TRANSPERFORM_77NOT9F36				(MIR_BASE - 10)			/*MIR 执行交易命令不返回9F36（MIR executes transaction commands without returning 9F36)*/
#define MIR_TRANSPERFORM_77NOT9F71				(MIR_BASE - 11)			/*MIR 执行交易命令不返回9F71（MIR executes transaction commands without returning 9F71)*/
#define MIR_TRANSCPMPLETE_BADSW					(MIR_BASE - 12)			/*MIR 执行交易完成命令不返回9000（MIR executes transaction commands without returning 9000)*/
#define MIR_TRANSCPMPLETE_NO9F26				(MIR_BASE - 13)			/*MIR 执行交易完成命令不返回9F26（MIR executes transaction commands without returning 9F26)*/
#define MIR_TRANSPERFORM_NORECOVRY				(MIR_BASE - 14)			/*MIR 执行交易命令不支持恢复(MIR does not support recovery when executing transaction commands)*/
#define MIR_TRANSPERFORM_LIMIT					(MIR_BASE - 15)			/*MIR 执行交易命令恢复超过限制(MIR Executes Transaction Command Restoration Over Restriction)*/
#define MIR_TRANSCOMPLETE_NORECOVRY				(MIR_BASE - 16)			/*MIR 执行交易完成命令不支持恢复(MIR Executes Transaction Completion Command does not support recovery)*/
#define MIR_TRANSCOMPLETE_LIMIT					(MIR_BASE - 17)			/*MIR 执行交易完成命令恢复超过限制(MIR Executes Transaction Completion Command Restore Over Restriction)*/
#define MIR_TRANSREADRECORD_NORECOVRY			(MIR_BASE - 18)			/*MIR 读记录命令不支持恢复（MIR Read Record Command does not support recovery）*/
#define MIR_TRANSREADRECORD_LIMIT				(MIR_BASE - 19)			/*MIR 读记录命令恢复超过限制（MIR Read Record Command Restore Over Restriction）*/

//CAPK Oper Errorcode
#define   CAPKERR_BASE							(-4000)					/*公钥文件操作基准错误码(Public key file operation benchmark error code)*/
#define   CAPKERR_FILEOPEN						(CAPKERR_BASE - 1) 		/*文件打开错误 (File open error)*/
#define   CAPKERR_FILEWRITE						(CAPKERR_BASE - 2) 		/*写文件错误 (write file error)*/
#define   CAPKERR_FILEREAD						(CAPKERR_BASE - 3) 		/*读文件错误 (Read file error)*/
#define   CAPKERR_CHKSUM						(CAPKERR_BASE - 4) 		/*公钥checksum错误 (Public key checksum error)*/
#define   CAPKERR_LOST							(CAPKERR_BASE - 5) 		/*未找到此公钥(This public key was not found) */
#define   CAPKERR_PARAM							(CAPKERR_BASE - 6) 		/*参数错误 (Parameter error)*/
#define   CAPKERR_FILELEN						(CAPKERR_BASE - 7) 		/*文件长度错误(File length error) */

//Revocation/Exception list Oper Errorcode
#define   LIST_BASE								(-4100)					/*公钥回收列表以及卡片黑名单文件操作基准错误码(Public key collection list and card blacklist file operation benchmark error code)*/
#define   LIST_FILEOPEN							(LIST_BASE - 1) 		/*文件打开错误 (File open error)*/
#define   LIST_FILEWRITE						(LIST_BASE - 2) 		/*写文件错误 (write file error)*/
#define   LIST_FILEREAD							(LIST_BASE - 3) 		/*读文件错误 (Read file error)*/
#define   LIST_LEN_EXCEED						(LIST_BASE - 4) 		/*数据长度超限(Data length is exceeds limit) */
#define   LIST_RECORD_NOFOUND					(LIST_BASE - 5) 		/*未找到对应的记录(Without find the corresponding record) */
#define   LIST_PARAM							(LIST_BASE - 6) 		/*参数错误(Parameter error) */

//AID Oper Errorcode
#define	AIDERR_BASE								(EMV_ERR_BASE - 5000)	/*AID参数文件操作基准错误码(AID parameter file operation benchmark error code)*/
#define	AIDERR_FILEOPEN							(AIDERR_BASE - 1) 		/*文件打开错误(File open error) */
#define	AIDERR_FILEWRITE						(AIDERR_BASE - 2) 		/*写文件错误 (write file error)*/
#define	AIDERR_FILEREAD							(AIDERR_BASE - 3) 		/*读文件错误 (Read file error)*/
#define	AIDERR_CHKSUM							(AIDERR_BASE - 4) 		/*公钥checksum错误(Public key checksum error) */
#define	AIDERR_LOST								(AIDERR_BASE - 5) 		/*未找到此AID(This AID was not found) */
#define	AIDERR_PARAM							(AIDERR_BASE - 6) 		/*参数错误(Parameter error) */
#define	AIDERR_FILELEN							(AIDERR_BASE - 7) 		/*文件长度错误(File length error) */
#define	AIDERR_UPTAID							(AIDERR_BASE - 8) 		/*更新终端配置参数时同步更新AID相应数据失败(Synchronous update of AID corresponding data fails when updating terminal configuration parameters) */
#define	AIDERR_PARSE							(AIDERR_BASE - 9) 		/*AID解析失败(AID parsing failed)*/
#define AIDERR_DISABLE							(AIDERR_BASE - 10)  	/*AID不可用(AID is not available)*/

#endif

