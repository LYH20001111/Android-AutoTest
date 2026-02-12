package com.newland.nsdk.core.api.common.emv;
/**
 * Constant definition for the ErrorCode class.
 * */
public class EMVErrorCode {

    /** EMV benchmark error code */
    public final static int EMV_ERR_BASE = 0;
    /** Failed to read aid configuration*/
    public final static int EMV_ERR_READCONFIG = -1;
	/** Failed to read aid list*/
    public final static int EMV_ERR_READAIDLIST = -2;
	/** IC card unable to power on*/
    public final static int EMV_ERR_POWERUP = -3;
	/** IC card not support instruction*/
    public final static int EMV_ERR_NOTSUPPORT = -4;
	/** Application lock*/
    public final static int EMV_ERR_APPBLOCK = -5;
	/** Can't find supported applications*/
    public final static int EMV_ERR_FINDAPP = -6;
	/** Quit the transaction*/
    public final static int EMV_ERR_CANCEL = -7;
	/** Application selection failed*/
    public final static int EMV_ERR_SELECTAPP = -8;
	/** Application initialization failed*/
    public final static int EMV_ERR_APPINIT	 = -9;
	/** Failed to read application data*/
    public final static int EMV_ERR_READAPPDATA	 = -10;
	/** Offline data authentication failed*/
    public final static int EMV_ERR_OFFAUTH	 = -11;
	/** Process limit failed*/
    public final static int EMV_ERR_PROCESSLIMIT = -12;
	/** Cardholder authentication failed*/
    public final static int EMV_ERR_CARDVERIFY	 = -13;
	/** Terminal risk management failed*/
    public final static int EMV_ERR_TERMRISKMANAGE	 = -14;
	/** Terminal behavior analysis failed*/
    public final static int EMV_ERR_TERMACTANALYZE	 = -15;
	/** Unsupported service*/
    public final static int EMV_ERR_NOTSUPPORTSERVICE = -16;
	/** No random number*/
    public final static int EMV_ERR_NORANDNUM = -17;
	/** Card lock*/
    public final static int EMV_ERR_CARDBLOCK = -18;
	/** GEN AC execution failed*/
    public final static int EMV_ERR_COMPLETION = -19;
	/** Save key configuration failed*/
    public final static int EMV_ERR_SAVECONFIG = -20;
	/** RF card preprocessing failed*/
    public final static int EMV_ERR_RF_PREPROCESS = -30;
	/** Operation timeout*/
    public final static int EMV_ERR_TIMEOUT	 = -31;
	/** Inserted IC card detected during RF card finder*/
    public final static int EMV_ERR_RF_GETICC = -32;
	/** Too many AIDs*/
    public final static int EMV_ERR_AID_COUNT_EXCEED = -33;
	/** Empty aid*/
    public final static int EMV_ERR_AID_EMPTY	 = -34;


	/** File Process*/
	
	/** AID configuration file benchmark error code*/
    public final static int FILEERR_BASE	 = -900;
	/** AID configuration file open failed*/
    public final static int FILE_OPEN_FILE	 = -901;
	/** AID configuration file read failed*/
    public final static int FILE_READ_FILE	 = -902;
	/** AID configuration file write failed*/
    public final static int FILE_WRITE_FILE	 = -903;
	/** AID configuration file version error*/
    public final static int FILE_AID_VERSION = -904;
	/** AID configuration file Can't get the required AID*/
    public final static int FILE_GETTLVDATA_NOEXIST	 = -905;
	/** AID configuration file Tlv data parsing failed*/
    public final static int FILE_AIDERR_PARSE = -906;
	/** Use wrong API interface*/
    public final static int FILE_AIDERR_API	 = -907;
	

	/** ICC Apdu*/
	
	/** ICC Apdu benchmark error code*/
	public final static int ICCERR_BASE  = -1000;
	/** apdu interactive data is empty*/
	public final static int APDU_DATA_NULL  = -1001;
	/** Kernel basic operation benchmark error code*/
	public final static int COREERR_BASE  = -1100;
	/** Get POS time error*/
	public final static int COREERR_GETTIME  = -1101;
	/** READ FINAL PARAM error*/
	public final static int COREERR_READFINALPARAM  = -1102;
	/** Get random number error*/
	public final static int COREERR_GETUNPNUM  = -1103;
	/** Data cache benchmark error code*/
	public final static int BUFERR_BASE  = -1200;
	/** Data buffer not enough storage*/
	public final static int BUFERR_BUFOVER  = -1201;
	/** The label is unique and cannot be overwritten if the length is greater than zero*/
	public final static int BUFERR_OBJDUP  = -1202;
	/** Dynamic memory allocation failed*/
	public final static int BUFERR_MALLOCFAIL  = -1203;

	/** Application Select*/
	
	/** Application select benchmark error code*/
	public final static int SELERR_BASE  = -1300;
	/** application select AID list failed*/
	public final static int SEL_USE_AIDLIST  = -1301;
	/** Application select cancle the transaction*/
	public final static int SEL_QUIT  = -1302;
	/** FCI data format error*/
	public final static int SEL_FCIFMTERR  = -1303;
	/** FCI data without 6F*/
	public final static int SEL_FCINO6F  = -1304;
	/** FCI data without 84*/
	public final static int SEL_FCINO84  = -1305;
	/** FCI data without A5*/
	public final static int SEL_FCINOA5  = -1306;
	/**FCI data error 9F38*/
	public final static int SEL_POSERR9F38  = -1307;
	/** FCI data tagBF0C repeat*/
	public final static int SEL_BF0CDUP  = -1308;
	/** Application selection return transaction*/
	public final static int SEL_FALLBACK  = -1309;
	/** FCI data tag50 repeat*/
	public final static int SEL_FCI50DUP  = -1310;
	/** FCI data repeat*/
	public final static int SEL_FCIDUP  = -1311;
	/**FCI data TLV parsing failed*/
	public final static int SEL_TLV_ERR  = -1312;
	/** FCI data without tag 6F*/
	public final static int SEL_NO6F  = -1313;
	/** FCI data without tag 84*/
	public final static int SEL_NO84  = -1314;
	/** FCI data without tag A5*/
	public final static int SEL_NOA5  = -1315;
	/** The tag sequence is wrong. For example"tag '84' should be placed before tag 'A5'*/
	public final static int SEL_TAG_SEQERR  = -1316;
	/** FCI data without tag BF0C*/
	public final static int SEL_NOBF0C  = -1317;
	/** FCI data tag 8F0C parsing failed*/
	public final static int SEL_BFOC_DATAERR  = -1318;
	/** final selection, return DF Name  not the same as the AID of command*/
	public final static int SEL_AID_DIFF_DFNAME  = -1319;
	/** Wrong transaction type*/
	public final static int SEL_ERR_TRANSTYPE  = -1320;
	/** PPSE command returned failure*/
	public final static int SEL_PPSE_ERROR  = -1321;
	/** Select the next AID*/
	public final static int SEL_NEXT_AID  = -1322;
	/** Discover zip aid special treatment for ppse*/
	public final static int SEL_DISCOVER_ZIP_AID  = -1323;
	/** FCI data without the value of tag 84*/
	public final static int SEL_NO84VALUE  = -1324;
	/** DPAS ppse only returns dpas aid does not return zip aid*/
	public final static int SEL_DPASNOHAVEZIPAID  = -1325;
	/** contactless transaction amount  exceeds limit*/
	public final static int SEL_AMT_OVER_CLSS  = -1327;
	/** when Amount zero, Set the Contactless Application Not Allowed*/
	public final static int SEL_AMTZERO_CLSS_NOT_ALLOW  = -1328;
	/**when Amount zero, shall set go online, but terminal offline only*/
	public final static int SEL_AMTZERO_CLSS_OFFLINE_OLY  = -1329;
	/**Bancomat Legacy aid special treatment for ppse*/
	public final static int SEL_BANCOMATLEGACY_AID  = -1330;

	/** Application selection tag61 is invalid*/
	public final static int SEL_TAG61_INVALID  = -1335;
	/** Application selection tag4F is invalid*/
	public final static int SEL_TAG4F_INVALID  = -1336;
	/**Application selection tag4F partial matching incomplete*/
	public final static int SEL_TAG4F_PARTIAL  = -1337;

	/** JCB FCI format error*/
	public final static int SEL_FCI_FMTERR  = -1338;
	/** JCB FCI 84 error*/
	public final static int SEL_FCIERR84  = -1339;
	/** FCI without 50*/
	public final static int SEL_FCINO50  = -1340;
	/** JCB FCI without 9F38*/
	public final static int SEL_FCINO9F38  = -1341;
	/** JCB FCI 9F28 is empty*/
	public final static int SEL_FCIEMPTY9F28  = -1342;
	/** JCB FCI failed to find proprietary data*/
	public final static int SEL_FCI_ILLEGALTAG  = -1343;
	/** JCB Torn transaction is Legacy Mode*/
	public final static int SEL_FCI_REV_LEGACY  = -1344;
	/** MCCS enforces data loss*/
	public final static int SEL_FCI_MANDATAMISS  = -1345;
	/** FCI without tag 87*/
	public final static int SEL_FCINO87  = -1346;
	/** FCI data 9F38 length wrong*/
	public final static int SEL_FCI9F38LENWRONG  = -1347;
	/** FCI format error*/
	public final static int SEL_FCIPARAMERROR  = -1348;
	/** Application selection returns 6300,State of non-volatile memory changed; authentication failed*/
	public final static int SEL_FINALSEL_6300  = -1350;
	/** Application selection returns 63C1*/
	public final static int SEL_FINALSEL_63C1  = -1351;
	/** Application selection returns 6983,Command not allowed; authentication method blocked*/
	public final static int SEL_FINALSEL_6983  = -1352;
	/** Application selection returns 6984,Command not allowed; reference data not usable*/
	public final static int SEL_FINALSEL_6984  = -1353;
	/** Application selection returns 6985,Command not allowed; conditions of use not satisfied*/
	public final static int SEL_FINALSEL_6985  = -1354;
	/** Application selection returns 6A82,Wrong parameters P1-P2; file or application not found*/
	public final static int SEL_FINALSEL_6A82  = -1355;
	/** Application selection returns 6A83,Wrong parameters P1-P2; record not found*/
	public final static int SEL_FINALSEL_6A83  = -1356;
	/** Application selection returns 6A88,Reference data (data objects) not found*/
	public final static int SEL_FINALSEL_6A88  = -1357;
	/** Application selection returns 6400*/
	public final static int SEL_FINALSEL_6400  = -1358;
	/** Application selection returns 6500*/
	public final static int SEL_FINALSEL_6500  = -1359;
	/** Application selection returns 9001*/
	public final static int SEL_FINALSEL_9001  = -1360;
	/** Application selection apdu communication failed*/
	public final static int SEL_L1_FAIL  = -1361;
	/** Active card failed*/
	public final static int SEL_L1_ACTIVE = -1362;
	/** FCI PDOL 9F02 Lenth Wrong*/
	public final static int SEL_FCIPDOL9F02WRONG = -1363;
	/** FCI PDOL 9A Lenth Wrong*/
	public final static int SEL_FCIPDOL9AWRONG = -1364;
	/** FCI PDOL 9C Lenth Wrong*/
	public final static int SEL_FCIPDOL9CWRONG = -1365;
	/** FCI PDOL 9F37 Lenth Wrong*/
	public final static int SEL_FCIPDOL9F37WRONG = -1366;
	/** FCI PDOL 9F35 Lenth Wrong*/
	public final static int SEL_FCIPDOL9F35WRONG = -1367;
	/** FCI PDOL 9F66 Lenth Wrong*/
	public final static int SEL_FCIPDOL9F66WRONG = -1368;

	/**Select FCI Response Analysis Error Code*/
	
	/** JCB benchmark error code*/
	public final static int KA_ERR_BASE  = -1370;
	/** JCB configured to be empty*/
	public final static int KA_CONFIG_EMPTY  = -1371;
	/** JCB Legacy mode is not supported*/
	public final static int KA_LEGACY_NOTSUPPORTED  = -1372;



	/** Application Initialization*/
	
	/** Application initialization benchmark error code*/
	public final static int INITERR_BASE  = -1400;
	/** Application initialization PDOL packaging failed*/
	public final static int INITERR_DOLPACKET  = -1401;
	/** Application initialization return value is not equal to 9000*/
	public final static int INITERR_RETURNSEL  = -1402;
	/** Application initialization returns 6984*/
	public final static int INITERR_GPOCMD  = -1403;
	/** Application initialization returns TLV parsing error*/
	public final static int INITERR_TLVDECODE  = -1404;
	/** Application initialization returns 80 template length error*/
	public final static int INITERR_80VALUELEN  = -1405;
	/** Application initialization returns 77 template without AIP*/
	public final static int INITERR_77NOAIP  = -1406;
	/** Application initialization returns AIP length error*/
	public final static int INITERR_AIPLEN  = -1407;
	/** Application initialization returns 77 template without AFL*/
	public final static int INITERR_77NOAFL  = -1408;
	/** Application initialization returns AFL length error*/
	public final static int INITERR_AFLLEN  = -1409;
	/** Application initialization returns invalid tag*/
	public final static int INITERR_UNEXPECTTAG  = -1410;
	/** Application initialization without PDOL*/
	public final static int INITERR_NOPDOL  = -1411;
	/** Application initialize pboc return 80 template*/
	public final static int INITERR_RETURNDATA  = -1412;
	/** Application initialization card is not supported*/
	public final static int INITERR_CARDNOSUPPORT  = -1413;
	/**Cancel electronic cash transactions*/
	public final static int INITERR_ECSELECT_QUIT  = -1414;
	/** Pure electronic cash card, but does not support e-cash*/
	public final static int INITERR_ECONLY_DENIAL  = -1415;
	/** Application initialization returns 6984< 20120911 zhengel 6984 special treatment*/
	public final static int INITERR_GPO_RETURN_6984  = -1416;
	/** Application initialization returns 6985 <20160330 fangjt 6985 directly terminate the transaction*/
	public final static int INITERR_GPO_RETURN_6985  = -1417;
	/** Application initialization returns 6283*/
	public final static int INITERR_GPO_RETURN_6283  = -1418;
	/** Application initialization returns 6300*/
	public final static int INITERR_GPO_RETURN_6300  = -1419;
	/** Application initialization returns 63C1*/
	public final static int INITERR_GPO_RETURN_63C1  = -1420;
	/**Application initialization returns 6983*/
	public final static int INITERR_GPO_RETURN_6983  = -1421;
	/** Application initialization returns 6986*/
	public final static int INITERR_GPO_RETURN_6986  = -1422;
	/** Application initialization returns 9001*/
	public final static int INITERR_GPO_RETURN_9001  = -1423;
	/** Application initialization returns 6A81*/
	public final static int INITERR_GPO_RETURN_6A81  = -1424;
	/** Application initialization returns 6A82*/
	public final static int INITERR_GPO_RETURN_6A82  = -1425;
	/** Application initialization returns 6A83*/
	public final static int INITERR_GPO_RETURN_6A83  = -1426;
	/** Application initialization returns 6A88*/
	public final static int INITERR_GPO_RETURN_6A88  = -1427;
	/**Application initialization returns 6500*/
	public final static int INITERR_GPO_RETURN_6500  = -1428;
	/** Application initialization returns 6400*/
	public final static int INITERR_GPO_RETURN_6400  = -1429;
	/** Application initialization returns 9408*/
	public final static int INITERR_GPO_RETURN_9408  = -1430;
	/** Application initialization returns ATC error*/
	public final static int INITERR_RF_ATC  = -1431;
	/** Application initialization returns AC error*/
	public final static int INITERR_RF_AC  = -1432;
	/** Application initialization returns 9F10 error*/
	public final static int INITERR_RF_9F10  = -1433;
	/** Application initialization returns 57 error*/
	public final static int INITERR_RF_57  = -1434;
	/** Application initialization returns AFL error*/
	public final static int INITERR_RF_AFL  = -1435;
	/** Application initialization returns 5F20 error*/
	public final static int INITERR_RF_5F20  = -1436;
	/** Application initialization without 9F66*/
	public final static int INITERR_RF_NO9F66  = -1437;
	/**The insert card is detected during the contactless application initialization process*/
	public final static int INITERR_RF_INSERTICC  = -1438;
	/** Application initialization 9F27 error*/
	public final static int INITERR_RF_9F27  = -1439;
	/** Application initialization APP does not support*/
	public final static int INITERR_RF_APPNOSUPPORT  = -1440;
	/** Pure electronic cash card but requires online*/
	public final static int INITERR_RF_ECONLY_ONLINE  = -1441;
	/** Pure electronic cash card but requires CVM*/
	public final static int INITERR_RF_ECONLY_CVM  = -1442;
	/** Magnetic stripe card is detected during the contactless application initialization process*/
	public final static int INITERR_RF_STRIPE  = -1443;
	/** Application initialization returns ATC error*/
	public final static int INITERR_SELECT_KERNEL_ERR  = -1444;
	/** Application initialization failed to save data*/
	public final static int INITERR_GPO_SAVEDATAERR  = -1445;
	/** Application initialization returns 77 template AFL data duplication*/
	public final static int INITERR_77DUB_AFL  = -1446;
	/** Application initialization returns 80 template AFL data duplication*/
	public final static int INITERR_80DUB_AFL  = -1447;
	/** Application initialization gets 81*/
	public final static int INITERR_81_GET  = -1448;
	/** Application initialization returns 77 template AIP data duplication*/
	public final static int INITERR_77DUB_AIP  = -1449;
	/** Application initialization returns 80 template AIP data duplication*/
	public final static int INITERR_80DUB_AIP  = -1450;
	/** Application initialization returns 9F01 error*/
	public final static int INITERR_9F01_GET  = -1451;
	/** Application initialization returns 9F69 error*/
	public final static int INITERR_9F69_GET  = -1452;
	/** Application initialization failed to save data*/
	public final static int INITERR_SAVE_FAIL  = -1453;
	/** Application initialization tlv data parsing error*/
	public final static int INITERR_PARSE_ERR  = -1454;
	/** Application initialization card data lose*/
	public final static int INITERR_CARDDATA_MISSING  = -1455;
	/** Application initialization AFL format error*/
	public final static int INITERR_AFLHEADERR  = -1456;
	/** Application initialization tag repeat*/
	public final static int INITERR_DUPLICATED  = -1457;
	/** JCB application initialization without return AIP*/
	public final static int INITERR_AIP_MISSING  = -1458;
	/** JCB application initialization without return SFI*/
	public final static int INITERR_SFI_MISSING  = -1459;
	/** JCB application initialization is not 77 or 80 template*/
	public final static int INITERR_ILLEGALTAG  = -1460;
	/** JCB application initializes SFI error*/
	public final static int INITERR_INVALID_SFI  = -1461;
	/** Application initialization cancel transaction*/
	public final static int INITERR_QUIT  = -1462;
	/** Interac FFI does not support contactless*/
	public final static int INITERR_FFI_NOSUPP_CONTAACTLESS  = -1463;
	/** Interac FFI does not support mobile phones*/
	public final static int INITERR_FFI_NOSUPP_MOBILE  = -1464;
	/**Interac FFI value error*/
	public final static int INITERR_FFI_WRONG  = -1465;
	/** interac no CTI*/
	public final static int INITERR_NO_CTI  = -1466;
	/** interac CTI lenth wrong*/
	public final static int INITERR_CTI_LENTHWRONG  = -1467;
	/**Interac CTI requires a transfer interface*/
	public final static int INITERR_CTI_TRYOTHERINTERFACE  = -1468;
	/** Interac CTI requires a transfer interface, but this machine does not support, viewing other machines*/
	public final static int INITERR_CTI_OTHERTERMINAL  = -1469;
	/** Interac CTI requires a transfer interface, which is not supported by this machine and other machines.*/
	public final static int INITERR_CTI_NOOTHERTERMINAL  = -1470;
	/** Interac exceeds the maximum number of try again*/
	public final static int INITERR_OVER_RETRYLIMIT  = -1471;
	/** GPO APDU response exception*/
	public final static int INITERR_GPO_RESPONSE_ERR  = -1472;
	/** Rupay second Remove the card and put it back later is mismatch*/
	public final static int INITERR_SECONDTAPWRONG  = -1473;
	/** Application initialization returns 6D00*/
	public final static int INITERR_GPO_RETURN_6D00  = -1474;
	/** Application initialization returns 6588*/
	public final static int INITERR_GPO_RETURN_6588  = -1475;
	/** Application initialization returns AIP NO Support CDA*/
	public final static int INITERR_AIP_NOSUPPCDA  = -1476;
	/** CPACE Amount, Authorized is missing or Amount, Authorized is empty (Length = 0) */
	public final static int INITERR_9F02_WRONG  = -1477;
	/** CPACE Transaction Currency Code is missing or Transaction Currency Code is empty (Length = 0) */
	public final static int INITERR_5F2A_WRONG  = -1478;
	/** CPACE Amount, Authorized > Contactless Transaction Limit  */
	public final static int INITERR_AIP_CDCVMLIMIT  = -1479;
	/** Application initialization returns AIP NO Support CDA*/
	public final static int INITERR_AIP_NOSUPPEMV  = -1480;
	/** 5F34 Repeat*/
	public final static int INITERR_5F34DUP  = -1481;
	/** 9F26 Repeat*/
	public final static int INITERR_9F26DUP  = -1482;

	/** Read Application Data*/

	/** Read record benchmark error code*/
	public final static int READRECERR_BASE  = -1500;
	/** Read record SFI error*/
	public final static int READREC_SFIERR  = -1501;
	/** Read record first record error*/
	public final static int READREC_FIRSTBE0  = -1502;
	/** Read record SFI record range error*/
	public final static int READREC_RECRANGEERR  = -1503;
	/** Read record return code error*/
	public final static int READREC_CMDERR  = -1504;
	/** Read record TLV parsing error*/
	public final static int READREC_TLVERR  = -1505;
	/** Read record without return tag 5A*/
	public final static int READREC_NO5A  = -1506;
	/** Read record without return tag 8C*/
	public final static int READREC_NO8C  = -1507;
	/** Read record without return tag 8D*/
	public final static int READREC_NO8D  = -1508;
	/** Read record tag5F24 error*/
	public final static int READREC_5F24ERR  = -1509;
	/** Read record tag5F25 error*/
	public final static int READREC_5F25ERR  = -1510;
	/** Failed to read record storage data*/
	public final static int READREC_SAVEDATA  = -1511;
	/** readrecord return Terminal Data,so terminate*/
	public final static int READREC_RET_TEMDATA  = -1512;
	/** The last readrecord cmd failed (this macro value is immutable)*/
	public final static int READREC_LASTCMDERR  = -1513;
	/** Read record without return tag 57*/
	public final static int READREC_NO57  = -1514;
	/** Read record without return tag 5F20*/
	public final static int READREC_NO5F20  = -1515;
	/** Read record without return tag 9F74*/
	public final static int READREC_NO9F74  = -1521;
	/** Read record without return tag 9F79*/
	public final static int READREC_NO9F79  = -1522;
	/** Read record without return tag 9F36*/
	public final static int READREC_NO9F36  = -1523;
	/** Read record returns tag 9F36 error*/
	public final static int READREC_ERR_ATC  = -1524;
	/** Transaction date expired*/
	public final static int READREC_ERR_DATEEXPIRE  = -1531;
	/** The date of transaction is not valid*/
	public final static int READREC_ERR_DATENOEFFECT  = -1532;
	/** Read record returns to 6283*/
	public final static int READREC_RETURN_6283  = -1550;
	/** Read record returns to 6300*/
	public final static int READREC_RETURN_6300  = -1551;
	/** Read record returns to 63C1*/
	public final static int READREC_RETURN_63C1  = -1552;
	/** Read record returns to 6983*/
	public final static int READREC_RETURN_6983  = -1553;
	/** Read record returns to 6984*/
	public final static int READREC_RETURN_6984  = -1554;
	/** Read record returns to 6985*/
	public final static int READREC_RETURN_6985  = -1555;
	/** Read record returns to 6A81*/
	public final static int READREC_RETURN_6A81  = -1556;
	/** Read record returns to 6A82*/
	public final static int READREC_RETURN_6A82  = -1557;
	/** Read record returns to 6A83*/
	public final static int READREC_RETURN_6A83  = -1558;
	/** Read record returns to 6A88*/
	public final static int READREC_RETURN_6A88  = -1559;
	/** Read record returns to 6400*/
	public final static int READREC_RETURN_6400  = -1560;
	/** Read record returns to 6500*/
	public final static int READREC_RETURN_6500  = -1561;
	/** Read record returns to 9001*/
	public final static int READREC_RETURN_9001  = -1562;
	/** Read record returns 5A repeat*/
	public final static int READREC_RETURN_5ADUP  = -1563;
	/** Read record returns 5F24 repeat*/
	public final static int READREC_RETURN_5F24DUP  = -1564;
	/** Read record returns 57 repeat*/
	public final static int READREC_RETURN_57DUP  = -1565;
	/** Read record cancel transaction*/
	public final static int READREC_QUIT  = -1566;
	/** The date of the card returned by the read record is incorrect*/
	public final static int READREC_DATEWRONG  = -1567;
	/** 5A and 57 returned by the read record do not match*/
	public final static int READREC_NOSAME5A57  = -1568;
	/**Read record without return tag 9F08*/
	public final static int READREC_NO9F08  = -1569;
	/** Read record without return tag 9F02*/
	public final static int READREC_NO9F02  = -1570;
	/** Read records exceed the maximum limit*/
	public final static int READREC_MAX_LIMIT_EXCEEDED  = -1571;
	/**Read record returns tag 9F4A error*/
	public final static int READREC_ERR_PP_ERR_9F4A  = -1572;
	/** Read record without return tag 9F4A*/
	public final static int READREC_ERR_PP_NO9F4A  = -1573;
	/** Read record without return tag 8F*/
	public final static int READREC_ERR_PP_NO8F  = -1574;
	/** Read record without return tag 90*/
	public final static int READREC_ERR_PP_NO90  = -1575;
	/** Read record without return tag 9F32*/
	public final static int READREC_ERR_PP_NO9F32  = -1576;
	/** Read record without return tag 93*/
	public final static int READREC_ERR_PP_NO93  = -1577;
	/** Read record without return tag 9F46*/
	public final static int READREC_ERR_PP_NO9F46  = -1578;
	/** Read record without return tag 9F47*/
	public final static int READREC_ERR_PP_NO9F47  = -1579;
	/** Capk does not support*/
	public final static int READREC_ERR_PP_CAPKNOSURPT  = -1580;
	/** ICC return CardReader Data,so terminate*/
	public final static int READREC_ERR_PP_CARDDATA  = -1581;
	/** Read record without return tag 57*/
	public final static int READREC_ERR_PW_NO57  = -1582;
	/** Read record return data format error*/
	public final static int READREC_FMT_ERROR  = -1583;
	/** Read record without return tag 5F24*/
	public final static int READREC_NO5F24  = -1584;
	/** Read record TAG 9F42 INVALID*/
	public final static int READREC_TAG9F42_INVALID  = -1585;
	/** Read record TAG 5F25 INVALID*/
	public final static int READREC_TAG5F25_INVALID  = -1586;
	/** Read record TAG 5A INVALID*/
	public final static int READREC_TAG5A_INVALID  = -1587;
	/**Read record TAG 9F07 INVALID*/
	public final static int READREC_TAG9F07_INVALID  = -1588;
	/** Read record TAG 5F20 INVALID*/
	public final static int READREC_TAG5F20_INVALID  = -1589;
	/** Read record TAG 9F0D INVALID*/
	public final static int READREC_TAG9F0D_INVALID  = -1590;
	/** Read record TAG 9F0E INVALID*/
	public final static int READREC_TAG9F0E_INVALID  = -1591;
	/** Read record TAG 9F0F INVALID*/
	public final static int READREC_TAG9F0F_INVALID  = -1592;
	/** Read record TAG 5F34 INVALID*/
	public final static int READREC_TAG5F34_INVALID  = -1593;
	/** Read record TAG 9F11 INVALID*/
	public final static int READREC_TAG9F11_INVALID  = -1594;
	/** Read record TAG 5F28 INVALID*/
	public final static int READREC_TAG5F28_INVALID  = -1595;
	/** Read record TAG 8F INVALID*/
	public final static int READREC_TAG8F_INVALID  = -1596;
	/** Read record without return tag 9F6D*/
	public final static int READREC_NO9F6D  = -1597;
	/** Read record without return tag 5F28*/
	public final static int READREC_NO5F28  = -1598;
	/** Read record without return tag 9F07*/
	public final static int READREC_NO9F07  = -1599;


	/** Data Authentication*/
	
	/** Data authentication benchmark error code*/
	public final static int SECERR_BASE  = -1600;
	/**Ic card data loss*/
	public final static int SECERR_ICCDATAMISSING  = -1601;
	/** Data Authentication Certificate Length Error*/
	public final static int SECERR_CERTLENGTH  = -1602;
	/** Data authentication RSA failed to recover public key*/
	public final static int SECERR_RECOVERKEY  = -1603;
	/** Data authentication recovery data header error*/
	public final static int SECERR_DATAHEADER  = -1604;
	/** The second byte of the data authentication certificate is wrong*/
	public final static int SECERR_FORMATWRONG  = -1605;
	/** Second to last byte of the data authentication certificate is wrong*/
	public final static int SECERR_DATATAILER  = -1606;
	/** Data authentication hash value check does not match*/
	public final static int SECERR_RECOVERHASH  = -1607;
	/** Data authentication algorithm identification error*/
	public final static int SECERR_ALGORITHM  = -1608;
	/** Data Authentication Certificate Module Length error*/
	public final static int SECERR_MODULUSLENGTH  = -1609;
	/** Data Authentication Certificate expires*/
	public final static int SECERR_CERTEXPIRED  = -1610;
	/** Data authentication and certification center public key blacklist*/
	public final static int SECERR_CERTREVOC  = -1611;
	/** Data authentication SSAD length error*/
	public final static int SECERR_SSADLEN  = -1612;
	/** Data authentication recovery SSAD error*/
	public final static int SECERR_RECOVERSSAD  = -1613;
	/** Data authentication 9F4A error*/
	public final static int SECERR_9F4AERR  = -1614;
	/** Data authentication without default DDOL*/
	public final static int SECERR_NONEDDOL  = -1615;
	/** Data authentication without tag 9F37*/
	public final static int SECERR_NO9F37  = -1616;
	/** Data authentication DDOL package error*/
	public final static int SECERR_DDOLPROCESS  = -1617;
	/** Data authentication SSAD length error*/
	public final static int SECERR_SDADLEN  = -1618;
	/** Data authentication without issuing bank public key*/
	public final static int SECERR_NOISSUERPK  = -1619;
	/** Data authentication without IC card public key*/
	public final static int SECERR_NOICCPK  = -1620;
	/** Data Authentication Acquisition Random Number error*/
	public final static int SECERR_GETCHALLENGE  = -1621;
	/** Data Authentication Recovery RSA Data error*/
	public final static int SECERR_RECOVERENCPIN  = -1622;
	/** Data authentication without tag 9F4B*/
	public final static int SECERR_NO9F4B  = -1623;
	/** Data Authentication Recovery SDAD Error*/
	public final static int SECERR_RECOVERSDAD  = -1624;
	/** Data Authentication SCDAD Length Error*/
	public final static int SECERR_SCDADLEN  = -1625;
	/** Data Authentication Recovery SCDAD Error*/
	public final static int SECERR_RECOVERSCDAD  = -1626;
	/** Data Authentication CID Value Mismatch*/
	public final static int SECERR_CIDNOTMATCHED  = -1627;
	/** Data authentication hash check 1 error*/
	public final static int SECERR_CDAHASH1  = -1628;
	/** Data authentication hash check 2 error*/
	public final static int SECERR_CDAHASH2  = -1629;
	/** Data Authentication Reading Record Error*/
	public final static int SECERR_FAILINREADREC  = -1630;
	/** Data Authentication Card Number Mismatch*/
	public final static int SECERR_PANNOTMATCH  = -1631;
	/** Data Authentication and Authentication Center Public Key Mismatch*/
	public final static int SECERR_CAPKNOTFOUND  = -1632;
	/** Data authentication without tag 9F36*/
	public final static int SECERR_NO9F36  = -1633;
	/** Data Authentication FDDA Version does not support*/
	public final static int SECERR_FDDAVERNOTSUP  = -1634;
	/** Data Authentication FDDA 9F69 Length Error*/
	public final static int SECERR_FDDA9F69LENERR  = -1635;
	/** Data Authentication national secret Elliptic Parameter Identification Error*/
	public final static int SECERR_ALGORITHMPARAM  = -1636;
	/** Data authentication SM2 authentication signature error*/
	public final static int SECERR_SM2VERIFY  = -1640;
	/** Data authentication 9F69 error*/
	public final static int SECERR_9F69  = -1641;
	/** Data authentication FDDA version error*/
	public final static int SECERR_FDDA_VER  = -1642;
	/** Data authentication ATC error*/
	public final static int SECERR_ATC  = -1643;
	/** Data authentication forces data errors*/
	public final static int SECERR_INALCMD_ERRDATA  = -1644;
	/** Data authentication without tag 5A*/
	public final static int SECERR_NOPAN  = -1645;
	/** the length of ICC Dynamic Data is less*/
	public final static int SECERR_ICCDDLEN  = -1646;
	/**paypass CDA RRP not match*/
	public final static int SECERR_RRP  = -1647;
	/** Hash indication of data authentication error*/
	public final static int SECERR_ERRHASH_INDICATOR  = -1648;
	/** Data authentication without hash algorithm*/
	public final static int SECERR_NOHASH_ALGORITHM  = -1649;
	/** Data authentication without return tag9F4B or tag92*/
	public final static int SECERR_NO9F4BOR92  = -1650;
	/** Data authentication DDA failed*/
	public final static int SECERR_FAILDDA  = -1651;
	/** Without 92 or 9F48*/
	public final static int SECERR_NOREMAINDER  = -1652;
	/** DPAS Without Card ID*/
	public final static int SECERR_NOCARDID  = -1653;
	/** DPAS no match Card ID*/
	public final static int SECERR_NOMATCHCARDID  = -1654;
	/** DPAS Data authentication hash check 3 error*/
	public final static int SECERR_CDAHASH3  = -1655;
	public final static int SECERR_9F4CERROR  = -1656;
	/** ECC cert format wrong*/
	public final static int SECERR_ECC_FORMAT  = -1660;
	/** ECC cert Encoding wrong*/
	public final static int SECERR_ECC_ENCODING  = -1661;
	/** ECC cert RID wrong*/
	public final static int SECERR_ECC_RID  = -1662;
	/** ECC cert Index wrong*/
	public final static int SECERR_ECC_INDEX  = -1663;
	/** Data authentication ECC authentication signature error*/
	public final static int SECERR_ECC_VERIFY  = -1664;
	/** Data authentication ECC Hash error*/
	public final static int SECERR_ECC_HASHALG  = -1665;
	/** Data authentication ICC ECC Hash check error*/
	public final static int SECERR_ECC_ICCHASHWRONG  = -1666;


	/** Cardholder Verification*/
	
	/** Cardholder Certification benchmark Error Code*/
	public final static int CVERR_BASE  = -1700;
	/** tag8E length error*/
	public final static int CVERR_8ELENWRONG  = -1701;
	/** Input offline pin failed*/
	public final static int CVERR_OFFLINEPIN  = -1702;
	/** Input online pin failed*/
	public final static int CVERR_ONLINEPIN  = -1703;
	/** Card AIP does not support CVM*/
	public final static int CVERR_AIPCVM_NOSUPP  = -1704;
	/** Without CVM list*/
	public final static int CVERR_NO8E  = -1705;
	/** Without CVM rules*/
	public final static int CVERR_NOCVMRULES  = -1706;
	/** DPAS CDCVM Not Enrolled*/
	public final static int CVERR_DPASCDCVMNOENROLLED  = -1707;
	/** DPAS CDCVM Not Performed*/
	public final static int CVERR_DPASCDCVMNOTPERFORMED  = -1708;





	/** Terminal Action Analysis and Card Action Analysis*/
	
	/**Terminal and card behavior analysis benchmark error code*/
	public final static int GACERR_BASE  = -1800;
	/**GAC response error*/
	public final static int GACERR_WRONGREQ  = -1801;
	/**GAC CDOL data pack error*/
	public final static int GACERR_CDOLPACKET  = -1802;
	/**GAC return code error*/
	public final static int GACERR_GACCMD  = -1803;
	/**GAC returns data parsing error*/
	public final static int GACERR_TLVDEOCDE  = -1804;
	/**Tag 80 templet cda requested and AAC not returned*/
	public final static int GACERR_CDAREQUIRE77  = -1805;
	/**GAC Returns Tag Length Error*/
	public final static int GACERR_TAG80VALLEN  = -1806;
	/**GAC returns 77 templates without returning 9F27*/
	public final static int GACERR_77NO9F27  = -1807;
	/**GAC returns 77 templates without returning 9F36*/
	public final static int GACERR_77NO9F36  = -1808;
	/**GAC returns 77 templates without returning 9F4B*/
	public final static int GACERR_77NO9F4B  = -1809;
	/**GAC returns not 77 or 80 templates*/
	public final static int GACERR_NOT77OR80  = -1810;
	/**CID request AAR returned by GAC*/
	public final static int GACERR_AARRET  = -1811;
	/**GAC returns the wrong CID*/
	public final static int GACERR_WRONGCID  = -1812;
	/**GAC returns 77 templates without returning 9F26*/
	public final static int GACERR_77NO9F26  = -1813;
	/**GAC without return 9F10*/
	public final static int GACERR_NO9F10  = -1814;
	/**GAC returns zero data*/
	public final static int GACERR_SPECIAL_PAD0  = -1815;
	/**DRDOL Data Pack Failed*/
	public final static int GACERR_DRDOLPACKET  = -1816;
	/**RAC response code is not 9000*/
	public final static int GACERR_RAC_SW12_NO9000  = -1817;
	/**GAC returns 80 templates with 9F36*/
	public final static int GACERR_80_9F36_EXIST  = -1818;
	/**GAC Error in Electronic Cash*/
	public final static int GACERR_ECMAC  = -1820;
	/**Failed to obtain EC balance*/
	public final static int GACERR_GETECBALANCE  = -1821;
	/**EC Balance not enough*/
	public final static int GACERR_EC_BALANCELACK  = -1822;
	/**Pure electronic cash card requires EC online but refuse*/
	public final static int GACERR_ECONLY_GOONLINE  = -1823;
	/**EC balance < transaction amount + threshold, resulting in online*/
	public final static int GACERR_EC_THRESHOLD  = -1824;
	/**GAC returns 77 templates without returning 81*/
	public final static int GACERR_RETURN_81  = -1825;
	/**GAC returns 77 templates without returning 9F01*/
	public final static int GACERR_RETURN_9F01  = -1826;
	/**GAC returns CID length error*/
	public final static int GACERR_RETURN_ERROR  = -1827;
	/**GAC returns 9F10 format error*/
	public final static int GACERR_9F10ERROR  = -1828;
	/**GAC returns 77 templates without returning tag C5*/
	public final static int GACERR_77_GACNOC5  = -1829;
	/**Neither MCCS GPO nor GAC returned to tag C5*/
	public final static int GACERR_77_ALLNOC5  = -1830;
	/**MCCS GPO and GAC return tage C5 CVM different ways*/
	public final static int GACERR_DISTINCTCVMINFOR  = -1831;
	/**MCCS GAC apdu without return, need to enter torn transaction*/
	public final static int GACERR_ECHO  = -1832;
	/**MCCS GAC without 9F4B but has 9F26*/
	public final static int GACERR_77NO9F4BHAVE9F26  = -1833;
	/**First GAC CDOL package failed*/
	public final static int GACERR_CDOL1_PACK  = -1834;
	/**The 77 template format returned by JCB GAC is wrong*/
	public final static int GACERR_FMT_ERROR  = -1835;
	/**JCB failed to store the label returned by GAC*/
	public final static int GACERR_TAG_DUP  = -1836;
	/**JCB storage GAC without return CID*/
	public final static int GACERR_CID_MISSING  = -1837;
	/**JCB GAC without return ATC*/
	public final static int GACERR_ATC_MISSING  = -1838;
	/**JCB GAC without return 9F4B*/
	public final static int GACERR_9F4B_MISSING  = -1839;
	/**JCB GAC without return AC*/
	public final static int GACERR_AC_MISSING  = -1840;
	/**JCB GAC without return 9F50*/
	public final static int GACERR_9F50_MISSING  = -1841;
	/**JCB GAC return AC type wrong*/
	public final static int GACERR_ACTYPE_ERR  = -1842;
	/**JCB GAC return AAC*/
	public final static int GACERR_AAC  = -1843;
	/**JCB GAC returns 9F5F format error*/
	public final static int GACERR_9F5F_INVALID  = -1844;
	/**JCB GAC returns 9F60 format error*/
	public final static int GACERR_9F60_INVALID  = -1845;
	/**JCB does not support CVM authentication*/
	public final static int CVM_NOT_SUPPORT  = -1846;
	/**JCB LEGACY mode GAC returns not 80 template*/
	public final static int GACERR_LEGACY_FMT  = -1847;
	/**JCB LEGACY mode GAC returned CID is not ARQC*/
	public final static int GACERR_LEGACY_DENIAL  = -1848;
	/**GAC cancels the transaction*/
	public final static int GACERR_QUIT  = -1849;
	/**9F27 length error*/
	public final static int GACERR_9F27LENWRONG  = -1850;
	/**9F36 length error*/
	public final static int GACERR_9F36LENWRONG  = -1851;
	/**9F26 length error*/
	public final static int GACERR_9F26LENWRONG  = -1852;
	/**JCB GMD command benchmark Error Code*/
	public final static int GMDERR_BASE  = -1860;
	/**JCB GMD failed to package MDOL*/
	public final static int GMDERR_MDOL_PACK  = -1861;
	/**JCB GMD command returns data error*/
	public final static int GMDERR_FMT_ERROR  = -1862;
	/**JCB GMD command without return tag57*/
	public final static int GMDERR_TK2ED_MISSING  = -1863;
	/**JCB MS mode GMD return code is 6300*/
	public final static int GMDERR_MS_DENIAL  = -1864;

	/**echo command*/
	
	/**echo command benchmark Error Code*/
	public final static int EHCO_BASE  = -1870;
	/**JCB echo command returns data format error*/
	public final static int EHCO_FMT_ERROR  = -1871;
	/**JCB echo returns data with duplicate tag*/
	public final static int EHCO_TAG_DUP  = -1872;
	/**JCB echo without return CID*/
	public final static int EHCO_CID_MISSING  = -1873;
	/**JCB echo without return ATC*/
	public final static int EHCO_ATC_MISSING  = -1874;
	/**JCB echo without return 9F4B*/
	public final static int EHCO_9F4B_MISSING  = -1875;
	/**JCB echo without return AC*/
	public final static int EHCO_AC_MISSING  = -1876;
	/**JCB echo without return 9F50*/
	public final static int EHCO_9F50_MISSING  = -1877;
	/**Echo command cancel transaction*/
	public final static int EHCO_QUIT  = -1878;


	/**Script Processing and Completion*/
	
	/**Basic and EMV complete benchmark error code*/
	public final static int COMERR_BASE  = -1900;
	/**Script exceeds limit*/
	public final static int COMERR_BASE_SCRIPT  = -1901;
	/**EC script empty*/
	public final static int COMERR_BASE_SCRIPTEMPTY  = -1902;
	/**EC deposit amount exceeds the limit*/
	public final static int COMERR_BASE_ECLOADAMOUNT  = -1903;
	/**Script execution error*/
	public final static int COMERR_BASE_SCRIPTRET  = -1904;
	/**Script error*/
	public final static int COMERR_BASE_SCRIPTERROR  = -1905;


	/**Flash Card*/
	
	/**Qpboc flash card benchmark error code*/
	public final static int FLASHCARD_ERR_BASE  = -2000;
	/**Flash card number does not match*/
	public final static int FLASHCARD_ERR_PAN_NO_EQUAL  = -2001;
	/**Flash card application transaction counter does not match*/
	public final static int FLASHCARD_ERR_ATC_NO_EQUAL  = -2002;
	/**Flash card currency code does not match*/
	public final static int FLASHCARD_ERR_CURCODE_NO_EQUAL  = -2003;
	/**Flash card electronic cash balance does not match*/
	public final static int FLASHCARD_ERR_BALANCE_NO_EQUAL  = -2004;
	/**Flash card without occor transaction*/
	public final static int FLASHCARD_ERR_NO_PURCHASE  = -2005;
	/**Flash card GPO error*/
	public final static int FLASHCARD_ERR_PURCHASE_GPO  = -2006;
	/**The last read record of the flash card is not responding*/
	public final static int FLASHCARD_ERR_LAST_RECORD_NO_RESPONSE  = -2007;
	/**Flash card can not get the card number*/
	public final static int FLASHCARD_ERR_NO_GET_PAN  = -2008;
	/**Flash card number is different*/
	public final static int FLASHCARD_ERR_PAN_NOT_SAME  = -2009;
	/**Flash card aid is not the same*/
	public final static int FLASHCARD_ERR_AID_NOT_SAME  = -2010;
	/**The last record of the flash card is not responding*/
	public final static int FLASHCARD_ERR_PAN_NO_RESPONSE  = -2011;


	/**Preprocess & qPboc errorcode*/

	/**Preprocessing benchmark error code*/
	public final static int RFERR_BASE  = -2100;
	/**Preprocessing parameter file error*/
	public final static int RFERR_PREPROCESS_PARAFILE  = -2101;
	/**Preprocessing input amount user exits*/
	public final static int RFERR_PREPROCESS_AMTQUIT  = -2102;
	/**Preprocessing input amount timeout*/
	public final static int RFERR_PREPROCESS_AMTTIMEOUT  = -2103;
	/**Preprocessing input amount failed*/
	public final static int RFERR_PREPROCESS_AMTFAIL  = -2104;
	/**Preprocessed input amount exceeds the limit*/
	public final static int RFERR_PREPROCESS_AMTLIMITOVER  = -2105;
	/**Preprocessing requires online, terminal cannot be online*/
	public final static int RFERR_PREPROCESS_REQONLINE  = -2106;
	/**AID is 0*/
	public final static int RFERR_PREPROCESS_NOAID  = -2107;
	/**RF card removal failed*/
	public final static int RFERR_ICCDEACTIVE  = -2111;
	/**Card returned error*/
	public final static int RFERR_ICCRETURNERROR  = -2112;
	/**Failed to read application data*/
	public final static int RFERR_READAPPDATA  = -2113;
	/**Card blacklist*/
	public final static int RFERR_BLKCARD  = -2114;
	/**The card is not valid*/
	public final static int RFERR_ICCNOEFFECT  = -2115;
	/**Card has invalid*/
	public final static int RFERR_ICCEXPIRE  = -2116;
	/**Card data authentication failed*/
	public final static int RFERR_DATAAUTH  = -2117;
	/**Card second magnetic equivalent data failed*/
	public final static int RFERR_TRACK2EDATA  = -2118;
	/**Frequency check exceeds limit*/
	public final static int RFERR_ICCFCHECK  = -2119;
	/**Pure electronic cash card can not be online*/
	public final static int RFERR_ECPURE_CANNOT_ONLINE  = -2120;
	/**Card rejection*/
	public final static int RFERR_CARD_DENIAL  = -2121;
	/**Card AIP has no data authentication*/
	public final static int RFERR_NOODA  = -2122;
	/**Card 9F10 return transaction result error*/
	public final static int RFERR_9F10CID  = -2123;
	/**Card data authentication without card number information*/
	public final static int RFERR_DATAAUTHNOPAN  = -2124;
	/**FDDA failed, card and terminal support contact PBOC*/
	public final static int RFERR_FDDAFAIL_SUPPBOC  = -2125;
	/**ODA failed, terminal refused to trade*/
	public final static int RFERR_ODAFAIL_DENIAL  = -2126;

	/**Paypass*/
	
	/**Paypass benchmark error code*/
	public final static int PPERR_BASE  = -2200;
	/**transaction usAmount over all Terminal Contactless Transaction Limit*/
	public final static int PPERR_TRANS_LIMITOVER  = -2201;
	/**paypass error track data*/
	public final static int PPERR_TRACK  = -2202;
	/**paypass PCVC3(paypass pcvc3*/
	public final static int PPERR_TRACK_PCVC3  = -2203;
	/**paypass punatc*/
	public final static int PPERR_TRACK_PUNATC  = -2204;
	/**paypass natc*/
	public final static int PPERR_TRACK_NATC  = -2205;
	/**k_track < t_track*/
	public final static int PPERR_TRACK_KLTT  = -2206;
	/**track data wrong unpredictable number*/
	public final static int PPERR_TRACK_NUN  = -2207;
	/**not the same as track2*/
	public final static int PPERR_TRACK1_PAN  = -2208;
	/**not the same as track2*/
	public final static int PPERR_TRACK1_EXPIREDATE  = -2209;
	/**compute cryptographic checksum error*/
	public final static int PPERR_CCC_CMD  = -2210;
	/**compute cryptographic checksum response error*/
	public final static int PPERR_CCC_RESPONSE  = -2211;
	/**UDOL NO 9F6A*/
	public final static int PPERR_CCC_UDOL_NO9F6A  = -2212;
	/**CCC command return track2 cvc3 error*/
	public final static int PPERR_CCC_TRACK1CVC3  = -2213;
	/**CCC command return track1 cvc3 error*/
	public final static int PPERR_CCC_TRACK2CVC3  = -2214;
	/**CCC command return atc error*/
	public final static int PPERR_CCC_ATC  = -2215;
	/**NO default UDOL*/
	public final static int PPERR_CCC_NODEF_UDOL  = -2216;
	/**UDOL pack error*/
	public final static int PPERR_CCC_DOLPACKET  = -2217;
	/**input pin error*/
	public final static int PPERR_PP_ENTERPIN  = -2218;
	/**read app  error*/
	public final static int PPERR_READAPPDATA  = -2219;
	/**Preprocessing input amount user exits*/
	public final static int PPERR_PREPROCESS_AMTQUIT  = -2220;
	/**Preprocessing input amount timeout*/
	public final static int PPERR_PREPROCESS_AMTTIMEOUT  = -2221;
	/**Preprocessing input amount failed*/
	public final static int PPERR_PREPROCESS_AMTFAIL  = -2222;
	/**SDA failed transaction terminate*/
	public final static int PPERR_SDAFAIL  = -2223;
	/**Track1 length error*/
	public final static int PPERR_TRACK1_LEN  = -2224;
	/**Track2 length error*/
	public final static int PPERR_TRACK2_LEN  = -2225;
	/**save data error*/
	public final static int PPERR_SAVEDATA  = -2226;
	/**read param config error*/
	public final static int PPERR_READPARAM  = -2227;
	/**CCC command return PCII error*/
	public final static int PPERR_CCC_PCII  = -2228;
	/**Used to specifically indicate data loss errors*/
	public final static int PPERR_CCC_TRACK_DATA_MISSING  = -2229;


	/**Paywave*/

	/**Paywave benchmark error code*/
	public final static int PWERR_BASE  = -2300;
	/**error or no 9F26 in read record command*/
	public final static int PWERR_9F26  = -2305;
	/**error or no 9F36 in read record command*/
	public final static int PWERR_9F36  = -2306;
	/**error or no 9F10 in read record command*/
	public final static int PWERR_9F10  = -2307;
	/**error or no 57 in read record command*/
	public final static int PWERR_57  = -2308;
	/**error or no 9F27 in read record command*/
	public final static int PWERR_9F27  = -2309;
	/**international transaction*/
	public final static int PWERR_INTER_TRANS  = -2310;
	/**Did not return 9F74*/
	public final static int PWERR_NO9F74  = -2311;
	/**define request online error*/
	public final static int PWERR_PW_ENTERPIN  = -2318;
	/**aip no support fdda*/
	public final static int PWERR_AIP_NOSUPPORTFDDA  = -2319;

	/**Ruoay*/

	/**Rupay benchmark error code*/
	public final static int RUERR_BASE  = -2400;
	/**Rupay KCV error*/
	public final static int RUERR_SERVICE_KCV_WRONG  = -2401;


	/**MIR*/

	/**MIR benchmark error code*/
	public final static int MIR_BASE  = -2500;
	/**MIR protocol value error*/
	public final static int MIR_PROTOCOLWRONG  = -2501;
	/**MIR aip does not support EMV mode*/
	public final static int MIR_AIP_NOSUPPORT_EMV  = -2502;
	/**MIR Protocol 2 does not have DF6F ODOL*/
	public final static int MIR_NOHAVE_DF6F  = -2503;
	/**MIR Protocol 2 DF6F ODOL error*/
	public final static int MIR_DF6F_WRONG  = -2504;
	/**MIR protocol 2 service not allowed*/
	public final static int MIR_ESRVICE_NOTALLOW  = -2505;
	/**MIR without SDAD*/
	public final static int MIR_SDADMISSING  = -2506;
	/**MIR executes transaction commands without returning 9000*/
	public final static int MIR_TRANSPERFORM_BADSW  = -2507;
	/**MIR executes transaction commands without returning 77 templates*/
	public final static int MIR_TRANSPERFORM_NOT77  = -2508;
	/**MIR executes transaction commands without returning 9F27*/
	public final static int MIR_TRANSPERFORM_77NOT9F27  = -2509;
	/**MIR executes transaction commands without returning 9F36*/
	public final static int MIR_TRANSPERFORM_77NOT9F36  = -2510;
	/**MIR executes transaction commands without returning 9F71*/
	public final static int MIR_TRANSPERFORM_77NOT9F71  = -2511;
	/**MIR executes transaction commands without returning 9000*/
	public final static int MIR_TRANSCPMPLETE_BADSW  = -2512;
	/**MIR executes transaction commands without returning 9F26*/
	public final static int MIR_TRANSCPMPLETE_NO9F26  = -2513;
	/**MIR does not support recovery when executing transaction commands*/
	public final static int MIR_TRANSPERFORM_NORECOVRY  = -2514;
	/**MIR Executes Transaction Command Restoration Over Restriction*/
	public final static int MIR_TRANSPERFORM_LIMIT  = -2515;
	/**MIR Executes Transaction Completion Command does not support recovery*/
	public final static int MIR_TRANSCOMPLETE_NORECOVRY  = -2516;
	/**MIR Executes Transaction Completion Command Restore Over Restriction*/
	public final static int MIR_TRANSCOMPLETE_LIMIT  = -2517;
	/**MIR Read Record Command does not support recovery*/
	public final static int MIR_TRANSREADRECORD_NORECOVRY  = -2518;
	/**MIR Read Record Command Restore Over Restriction*/
	public final static int MIR_TRANSREADRECORD_LIMIT  = -2519;


	/**CAPK Oper Errorcode*/

	/**Public key file operation benchmark error code*/
	public final static int CAPKERR_BASE  = -4000;
	/**File open error*/
	public final static int CAPKERR_FILEOPEN  = -4001;
	/**write file error*/
	public final static int CAPKERR_FILEWRITE  = -4002;
	/**Read file error*/
	public final static int CAPKERR_FILEREAD  = -4003;
	/**Public key checksum error*/
	public final static int CAPKERR_CHKSUM  = -4004;
	/**This public key was not found*/
	public final static int CAPKERR_LOST  = -4005;
	/**Parameter error*/
	public final static int CAPKERR_PARAM  = -4006;
	/**File length error*/
	public final static int CAPKERR_FILELEN  = -4007;


	/** Revocation/Exception list Oper Errorcode*/

	/**Public key collection list and card blacklist file operation benchmark error code*/
	public final static int LIST_BASE  = -4100;
	/**File open error*/
	public final static int LIST_FILEOPEN  = -4101;
	/**write file error*/
	public final static int LIST_FILEWRITE  = -4102;
	/**Read file error*/
	public final static int LIST_FILEREAD  = -4103;
	/**Data length is exceeds limit*/
	public final static int LIST_LEN_EXCEED  = -4104;
	/**Without find the corresponding record*/
	public final static int LIST_RECORD_NOFOUND  = -4105;
	/**Parameter error*/
	public final static int LIST_PARAM  = -4106;
	/**file length error*/
	public final static int LIST_FILELEN  = -4107;

	/**AID Oper Errorcode*/

	/**AID parameter file operation benchmark error code*/
	public final static int AIDERR_BASE  = -5000;
	/**File open error*/
	public final static int AIDERR_FILEOPEN  = -5001;
	/**write file error*/
	public final static int AIDERR_FILEWRITE  = -5002;
	/**Read file error*/
	public final static int AIDERR_FILEREAD  = -5003;
	/**Public key checksum error*/
	public final static int AIDERR_CHKSUM  = -5004;
	/**This AID was not found*/
	public final static int AIDERR_LOST  = -5005;
	/**Parameter error*/
	public final static int AIDERR_PARAM  = -5006;
	/**File length error*/
	public final static int AIDERR_FILELEN  = -5007;
	/**Synchronous update of AID corresponding data fails when updating terminal configuration parameters*/
	public final static int AIDERR_UPTAID  = -5008;
	/**AID parsing failed*/
	public final static int AIDERR_PARSE  = -5009;
	/**AID is not available*/
	public final static int AIDERR_DISABLE  = -5010;
	
}






