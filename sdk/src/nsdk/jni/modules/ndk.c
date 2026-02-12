#include <stdio.h>
#include <android/log.h>
#include "jni.h"
#include "ndk.h"
#include <dlfcn.h>
#include "log.h"
#include <string.h>
#include "crypto.h"
#include "printer.h"
#define TAG "libnsdk"

void *functionLib;         /*  Handle to shared lib file   */
char *dlError;        /*  Pointer to error string     */
static int rc = 0;   
int g_rfMultiLevel = 1;
int g_aCardAtq = 1;

//kb
int (*NDK_KbFlush)(void);
int (*NDK_KbGetCode)(unsigned int unTime, int *pnCode);
int (*NDK_KbHit)(int *pnCode);
int (*NDK_SysBeep)(void);
int (*NDK_KbGetInput)(char *pszBuf,unsigned int unMinLen,unsigned int unMaxLen,unsigned int *punLen,EM_INPUTDISP emMode,unsigned int unWaitTime,EM_INPUT_CONTRL emControl);
int (*NDK_SysSetKeyLongPress)(EM_SYS_KEY key, int status);

//mag
int (*NDK_MagOpen)(void);
int (*NDK_MagClose)(void);
int (*NDK_MagReset)(void);
int (*NDK_MagSwiped)(unsigned char * psSwiped);
int (*NDK_MagReadNormal)(char *pszTk1, char *pszTk2, char *pszTk3, int *pnErrorCode);
int (*NDK_MagReadRaw)(uchar *pszTk1, ushort* pusTk1Len, uchar *pszTk2, ushort* pusTk2Len,uchar *pszTk3, ushort* pusTk3Len );
int (*NDK_MagReadRawData)(ENUM_MAG_DATA_TYPE type, ENUM_MAG_TRACK track, uint off, uint unLen, uchar *tkdata, uint *pnReadlen);
int (*NDK_MagReadCards)(mag_doublecard_t *card);

//print
int (*NDK_PrnInit)(uint unPrnDirSwitch);
int (*NDK_PrnStr)(const char *pszBuf);
int (*NDK_PrnStart)(void);
int (*NDK_PrnImage)(uint unXsize,uint unYsize,uint unXpos,const char *psImgBuf);
int (*NDK_PrnGetVersion)(char *pszVer);
int (*NDK_PrnSetFont)(EM_PRN_HZ_FONT emHZFont,EM_PRN_ZM_FONT emZMFont);
int (*NDK_PrnGetStatus)(EM_PRN_STATUS *pemStatus);
int (*NDK_PrnSetMode)(EM_PRN_MODE emMode,uint unSigOrDou);
int (*NDK_PrnSetGreyScale)(uint unGrey);
int (*NDK_PrnSetForm)(uint unBorder,uint unColumn, uint unRow);
int (*NDK_PrnFeedByPixel)(uint unPixel);
int (*NDK_PrnFeedPaper)(void);
int (*NDK_PrnFeedPaper_Extern)(EM_NAPI_PRN_FEEDPAPER operation);
int (*NDK_PrnSetUnderLine)(EM_PRN_UNDERLINE_STATUS emStatus);
int (*NDK_PrnSetParam)(EM_PRN_SET_PARAM_TYPE type, int value);
int (*NDK_Script_Print)(char* prndata,int indata_len);
int (*Png_Pint)(char * file,uint pos,int au);
int (*setyu)(int a);
void (*PrnInit)(void);
int (*NDK_PrnModuleInit)();
int (*NDK_PrnCutterPerformance)();
/**
 *@brief        获取adc值
 *@details      该接口只允许非打印状态下调用
 *@param        type  获取adc值类型，具体参照EM_PRN_GET_ERR_STATUS_VALUE枚举
 *@retval       alg_value 返回硬件真实值(比如打印机真实温度值，机器当前电压值)
 *@retval       dgt_value adc值
 *@return
 *@li   NDK_OK              操作成功
 *@li   \ref NDK_ERR_PARA "NDK_ERR_PARA"        参数错误
 *@li   \ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"        打印设备打开失败
 *@li   \ref NDK_ERR_NOT_SUPPORT "NDK_ERR_NOT_SUPPORT"        不支持该功能
*/
int (*NDK_PrnGetStatusValue)(EM_PRN_GET_ERR_STATUS_VALUE type_value, int *alg_value, int *dgt_value);

//TTF
int (*TTF_PrnExit)();
int (*TTF_PrnSetPaperSize)(EM_PRN_PAPER_SIZE size);
int (*TTF_ScriptPrint)(const char* pszBuf);
int (*TTF_PrnApiLoad)();
int (*TTF_GetVersion)(char *version);

//file
int (*NDK_FsOpen)(const char *pszName,const char *pszMode);
int (*NDK_FsClose)(int nHandle);
int (*NDK_FsRead)(int nHandle, char *psBuffer, uint unLength );
int (*NDK_FsWrite)(int nHandle, const char *psBuffer, uint unLength );
int (*NDK_FsSeek)(int nHandle, ulong ulDistance, uint unPosition );
int (*NDK_FsDel)(const char *pszName);
int (*NDK_FsFileSize)(const char *pszName,uint *punSize);
int (*NDK_FsExist)(const char *pszName);
int (*NDK_FsTruncate)(const char *pszPath ,uint unLen );
int (*NDK_FsTell)(int nHandle,ulong *pulRet);
int (*NDK_FsRename)(const char *pszSrcName, const char *pszDstName );
int (*NDK_FsFormat)(void);
int (*NDK_CopyFileToSecMod)(const unsigned char* sourcefile, const unsigned char* destfile);

//tool
int (*NDK_AddDigitStr)(const uchar *pszDigStr1, const uchar *pszDigStr2, uchar* pszResult, int *pnResultLen );
int (*NDK_IncNum )(uchar * pszStrNum );
int (*NDK_FmtAmtStr )(const uchar* pszSource, uchar* pszTarget, int* pnTargetLen );
int (*NDK_AscToHex )(const uchar* pszAsciiBuf, int nLen, uchar ucType, uchar* psBcdBuf);
int (*NDK_HexToAsc )(const uchar* psBcdBuf, int nLen, uchar ucType, uchar* pszAsciiBuf);
int (*NDK_IntToC4 )(uchar* psBuf, uint unNum );
int (*NDK_IntToC2 )(uchar* psBuf, uint unNum );
int (*NDK_C4ToInt)(uint* unNum, uchar* psBuf );
int (*NDK_C2ToInt)(uint* unNum, uchar* psBuf );
int (*NDK_ByteToBcd)(int nNum, uchar *psCh);
int (*NDK_BcdToByte)(uchar ucCh, int *pnNum);
int (*NDK_IntToBcd)(uchar *psBcd, int *pnBcdLen, int nNum);
int (*NDK_BcdToInt)(const uchar * psBcd, int *nNum);
int (*NDK_CalcLRC)(const uchar *psBuf, int nLen, uchar *ucLRC);
int (*NDK_LeftTrim)(uchar *pszBuf);
int (*NDK_RightTrim)(uchar *pszBuf);
int (*NDK_AllTrim)(uchar *pszBuf);
int (*NDK_AddSymbolToStr)(uchar *pszString, int nLen, uchar ucCh, int nOption);
int (*NDK_SubStr)(const uchar *pszSouStr, int nStartPos, int nNum, uchar *pszObjStr, int *pnObjStrLen);
int (*NDK_IsDigitChar)(uchar ucCh);
int (*NDK_IsDigitStr)(const uchar *pszString);
int (*NDK_IsLeapYear)(int nYear);
int (*NDK_MonthDays)(int nYear, int nMon, int *pnDays);
int (*NDK_IsValidDate)(const uchar *pszDate);

//app
int (*NDK_AppRun)(const char *pszAppName);
int (*NDK_AppLoad)(const char *pszFileName, int nRebootFlag);
int (*NDK_AppDel)(const char *pszAppName);

//ic
int (*NDK_IccGetVersion)(char *version);
int (*NDK_IccPowerUp )(EM_ICTYPE emIctype, unsigned char *psAtrbuf,int *pnAtrlen);
int (*NDK_IccPowerDown)(EM_ICTYPE emIctype);
int (*NDK_IccDetect)(int *pnSta);
int (*NDK_Iccrw)(EM_ICTYPE emIcType, int nSendLen,  unsigned char *psSendBuf, int *pnRecvLen,  unsigned char *psRecvBuf);
int (*NDK_IccSetConfig)(EM_ICTYPE emIctype, EM_CFGTYPE emCfgtype, unsigned int cfgValue);
int (*NDK_IccSetPowerUpMode)(int pnMode, int pnVoltage);
int (*NDK_IccGetProtocol)(EM_ICTYPE emIctype, int *pnProtocol);
int (*NDK_IccGetWorkStatus)(uint8_t *psta);

//sys
int (*NDK_SysBeep)(void);
int (*NDK_Getlibver)(char *version);
int (*NDK_SysTimeBeep)(unsigned int unFrequency,unsigned int unSeconds);
int (*NDK_SysSetPosTime)(struct tm stTime);
int (*NDK_SysGetPosTime)(struct tm *pstTime);
int (*NDK_SysStartWatch)(void);
int (*NDK_SysStopWatch)(unsigned int *punTime);
int (*NDK_SysDelay)(unsigned int unDelayTime);
int (*NDK_SysMsDelay)(unsigned int unDelayTime);
int (*NDK_SysExit)(int nErrCode);
int (*NDK_SysReboot)(void);
int (*NDK_SysShutDown)(void);
int (*NDK_SysSetBeepVol)(unsigned int unVolNum);
int (*NDK_SysGetBatteryProperty)(EM_BATTERY_PROPERTY type, int bufLen, char *value);
int (*NDK_SysGetBeepVol)(unsigned int *punVolNum);
int (*NDK_SysSetSuspend)(unsigned int  unFlag);
int (*NDK_SysGoSuspend)(void);
int (*NDK_SysGetPowerVol)(unsigned int *punVol);
int (*NDK_LedStatus)(EM_LED emStatus);
int (*NDK_LedSetFlickParam)(EM_LED emStatus, ST_NDK_LED_FLICK flickParam);
int (*NDK_LedLt1118Status)(EM_LED_LT1118 emStatus);
int (*NDK_SysReadWatch)(unsigned int *punTime);
int (*NDK_SysGetPosInfo)(EM_SYS_HWINFO emFlag,unsigned int *punLen,char *psBuf);
int (*NDK_SysGetConfigInfo)(EM_SYS_CONFIG emConfig,int *pnValue);
int (*NDK_SysInitStatisticsData)(void);
int (*NDK_SysGetStatisticsData)(EM_SS_DEV_ID emDevId,unsigned long *pulValue);
int (*NDK_SysGetFirmwareInfo)(EM_SYS_FWINFO *emFWinfo);
int (*NDK_SysTime)(unsigned long *ulTime);
int (*NDK_SysSetSuspendDuration)(unsigned int unSec);
int (*NDK_SysGetPowerVolRange)(unsigned int *punMax,unsigned int *punMin);
int (*NDK_SysKeyVolSet)(uint sel);
int (*NDK_SysSetBeepVol_Extern)(BEEP_TYPE type, uint unVolumn);
int (*NDK_LedFuncModeSet)(EM_LED_FUNC_TYPE emType,uint16_t interval_ms);
int (*NDK_SysPeerOper)(EM_SYS_PEEROPER oper);
int (*NDK_SysEnterBoot)(void);
int (*NDK_SysSetPosInfo)(EM_SYS_HWINFO emFlag, const char *psBuf);
int (*NDk_SysGetK21Version)(char *version);
int (*NDK_SysWakeUp)(void);
int (*NDK_SP_SysSetPosInfo)(EM_SYS_HWINFO emFlag, const char *psBuf);
int (*NDK_SP_SysGetPosInfo)(EM_SYS_HWINFO emFlag, unsigned int *punLen, char *psBuf);
int (*NDK_ScrDrawBitmapV)(uint unX,uint unY,uint unWidth,uint unHeight, const uchar *psBuf);
int (*NDK_ScrDispString)(uint unX,uint unY,const char *pszS,uint unMode);
int (*NDK_ScrBackLight)(EM_BACKLIGHT emBL);
int (*NDK_ScrClrs)(void);
int (*NDK_CEisSupport)(uchar *isSupport);

//rf
int (*NDK_RfidLogoDisplay)(int onoff);
int (*NDK_RfidFunisSupport)(functionType Type , uchar* isSupport);
int (*NDK_RfidVersion)(unsigned char *pszVersion);
int (*NDK_RfidInit)(uchar *psStatus);
int (*NDK_RfidOpenRf)(void);
int (*NDK_RfidCloseRf)(void);
int (*NDK_RfidPiccState)(void);
int (*NDK_RfidSuspend)(void);
int (*NDK_RfidResume)(void);
int (*NDK_RfidPiccType)(uchar ucPicctype);
int (*NDK_RfidPiccDetect)(uchar *psPicctype);
int (*NDK_RfidPiccDetect_Atq)(uchar *psPiccType,int *pnAtqlen,uchar* psAtqbuf);
int (*NDK_RfidSetDetectType)(uint32_t ucPicctype);
int (*NDK_RfidDetectWithCardType)(uint32_t *psPicctype, int* outputBufLen, uchar* outputBuf);
int (*NDK_RfidSetPiccParam)(uchar ucPiccparamtype, int pnParamlen, uchar *psParambuf);
int (*NDK_RfidGetPiccInfo)(uchar ucPiccinfotype, int *pnInfolen, uchar *psInfobuf);
int (*NDK_RfidPiccActivate)(uchar *psPicctype, int *pnDatalen,  uchar *psDatabuf);
int (*NDK_RfidPiccActivateWithInfo)(uchar *psPicctype, int *pnDatalen, uchar *psDatabuf, int *pnInfolen, uchar *psInfobuf);
int (*NDK_MifareActiveWithInfo)(uchar ucReqCode,uchar *psUID, uchar *pucUIDLen, uchar *psSak, int *pnInfolen, uchar *psInfobuf);
int (*NDK_RfidPiccDeactivate)(uchar ucDelayms);
int (*NDK_RfidPiccApdu)(int nSendlen, uchar *psSendbuf, int *pnRecvlen,  uchar *psRecebuf);
int (*NDK_RfidLpcdStartDetect)(void);
int (*NDK_RfidLpcdGetState)(uchar *lpcdState);
int (*NDK_RfidLpcdStopDetect)(void);
int (*NDK_M1Request)(uchar ucReqcode, int *pnDatalen, uchar *psDatabuf);
int (*NDK_M1Anti)(int *pnDatalen, uchar *psDatabuf);
int (*NDK_M1Anti_SEL)(uchar ucSelCode, int *pnDataLen, uchar *psDataBuf);
int (*NDK_M1Select)(int nUidlen, uchar *pnUidbuf, uchar *psSakbuf);
int (*NDK_M1Select_SEL)(uchar ucSelCode, int nUidLen, uchar *psUidBuf, uchar *psSakBuf);

int (*NDK_M1KeyStore)(uchar ucKeytype,  uchar ucKeynum, uchar *psKeydata);
int (*NDK_M1KeyLoad)(uchar ucKeytype,  uchar ucKeynum);
int (*NDK_M1InternalAuthen)(int nUidlen, uchar *psUidbuf, uchar ucKeytype, uchar ucBlocknum);
int (*NDK_M1ExternalAuthen)(int nUidlen, uchar *psUidbuf, uchar ucKeytype, uchar *psKeydata, uchar ucBlocknum);
int (*NDK_M1Read)(uchar ucBlocknum, int *pnDatalen, uchar *psBlockdata);
int (*NDK_M1Write)(uchar ucBlocknum,  int *pnDataLen, uchar *psBlockdata);
int (*NDK_M1Increment)(uchar ucBlocknum, int nDatalen, uchar *psDatabuf);
int (*NDK_M1Decrement)(uchar ucBlocknum, int nDanalen, uchar *psDatabuf);
int (*NDK_M1Transfer)(uchar ucBlocknum);
int (*NDK_M1Restore)(uchar ucBlocknum);
int (*NDK_PiccQuickRequest)(int nModecode);
int (*NDK_SetIgnoreProtocol)(int nModecode);
int (*NDK_GetIgnoreProtocol)(int *pnModecode);
int (*NDK_GetRfidType)(int *pnRfidtype);
int (*NDK_RfidTypeARats)(uchar cid,int *pnDatalen, uchar *psDatabuf);
int (*NDK_RfidFelicaPoll)(uchar *psRecebuf,int *pnRecvlen);
int (*NDK_FelicaPoll)(felica_param_t fex, uchar *psRecebuf, uint *pnRecvlen);
int (*NDK_FelicaSetTimeout)(int timeout);
int (*NDK_RfidFelicaApdu)(int nSendlen, uchar *psSendbuf, int *pnRecvlen,  uchar *psRecebuf);
int (*NDK_MifareActive)(uchar ucReqCode,uchar *psUID, uchar *pnUIDLen, uchar *psSak);
int (*NDK_M0Read)(uchar ucPageNum, int *pnDataLen, uchar *psPageData);
int (*NDK_M0Write)(uchar ucPageNum, int pnDataLen, uchar *psPageData);
int (*NDK_M0Authen)(uchar *psKey);


//alg
int (*NDK_AlgTDes)(uchar *psDataIn, uchar *psDataOut, uchar *psKey, int nKeyLen, int nMode);
int (*NDK_AlgSHA1)(uchar *psDataIn, int nInlen, uchar *psDataOut);
int (*NDK_AlgSHA256)(uchar *psDataIn, int nInlen, uchar *psDataOut);
//int (*NDK_AlgSHA512)(uchar *psDataIn, int nInlen, uchar *psDataOut);
//int (*NDK_AlgRSAKeyPairGen)( int nProtoKeyBit, int nPubEType, ST_RSA_PUBLIC_KEY *pstPublicKeyOut, ST_RSA_PRIVATE_KEY *pstPrivateKeyOut);
//int (*NDK_AlgRSARecover)(uchar *psModule, int nModuleLen, uchar *psExp, uchar *psDataIn, uchar *psDataOut);
//int (*NDK_AlgRSAKeyPairVerify)(ST_RSA_PUBLIC_KEY *pstPublicKey, ST_RSA_PRIVATE_KEY *pstPrivateKey);

//port
int (*NDK_PortOpen)(EM_PORT_NUM emPort, const char *pszAttr);
int (*NDK_PortClose)(EM_PORT_NUM emPort);
int (*NDK_PortRead)(EM_PORT_NUM emPort, unsigned int unLen, char *pszOutbuf,int nTimeoutMs, int *pnReadlen);
int (*NDK_PortWrite)(EM_PORT_NUM emPort, unsigned int  unLen,const char *pszInbuf);
int (*NDK_PortTxSendOver)(EM_PORT_NUM emPort);
int (*NDK_PortClrBuf)(EM_PORT_NUM emPort);
int (*NDK_PortReadLen)(EM_PORT_NUM emPort,int *pnReadLen);

//sec
int (*NDK_SecGetVer)(uchar * pszVerInfoOut);
int (*NDK_SecGetRandom)(int nRandLen , void *pvRandom);
int (*NDK_SecSetCfg)(unsigned int unCfgInfo);
int (*NDK_SecGetCfg)(unsigned int *punCfgInfo);
int (*NDK_SecGetKcv)(uchar ucKeyType, uchar ucKeyIdx, ST_SEC_KCV_INFO *pstKcvInfoOut);
int (*NDK_SecKeyErase)(void);
int (*NDK_SecLoadKey)(ST_SEC_KEY_INFO * pstKeyInfoIn, ST_SEC_KCV_INFO * pstKcvInfoIn);
int (*NDK_SecSetIntervaltime)(unsigned int unTPKIntervalTimeMs, unsigned int unTAKIntervalTimeMs);
int (*NDK_SecSetFunctionKey)(uchar ucType);
int (*NDK_SecGetMac)(uchar ucKeyIdx, uchar *psDataIn, int nDataInLen, uchar *psMacOut, uchar ucMod);
int (*NDK_SecGetPin)(uchar ucKeyIdx, uchar *pszExpPinLenIn,const uchar * pszDataIn, uchar *psPinBlockOut, uchar ucMode, unsigned int nTimeOutMs);
int (*NDK_SecCalcDes)(uchar ucKeyType, uchar ucKeyIdx, uchar * psDataIn, int nDataInLen, uchar *psDataOut, uchar ucMode);
int (*NDK_SecVerifyPlainPin)(uchar ucIccSlot, uchar *pszExpPinLenIn, uchar *psIccRespOut, uchar ucMode,  unsigned int unTimeoutMs);
int (*NDK_SecVerifyCipherPin)(uchar ucIccSlot, uchar *pszExpPinLenIn, ST_SEC_RSA_KEY *pstRsaPinKeyIn, uchar *psIccRespOut, uchar ucMode, unsigned int unTimeoutMs);
int (*NDK_SecLoadTIK)(uchar ucGroupIdx, uchar ucSrcKeyIdx, uchar ucKeyLen, uchar * psKeyValueIn, uchar * psKsnIn, ST_SEC_KCV_INFO * pstKcvInfoIn);
int (*NDK_SecGetDukptKsn)(uchar ucGroupIdx, uchar * psKsnOut);
int (*NDK_SecIncreaseDukptKsn)(uchar ucGroupIdx);
int (*NDK_SecGetPinDukpt)(uchar ucGroupIdx, uchar *pszExpPinLenIn, uchar * psDataIn, uchar* psKsnOut, uchar *psPinBlockOut, uchar ucMode, unsigned int unTimeoutMs);
int (*NDK_SecGetMacDukpt)(uchar ucGroupIdx, uchar *psDataIn, int nDataInLen, uchar *psMacOut, uchar *psKsnOut, uchar ucMode);
int (*NDK_SecCalcDesDukpt)(uchar ucGroupIdx, uchar ucKeyVarType, uchar *psIV, unsigned short usDataInLen, uchar *psDataIn,uchar *psDataOut,uchar *psKsnOut ,uchar ucMode);
int (*NDK_SecLoadRsaKey)(uchar ucRsaKeyIndex, ST_SEC_RSA_KEY *pstRsaKeyIn);
int (*NDK_SecRecover)(uchar ucRsaKeyIndex, const uchar *psDataIn, int nDataLen, uchar *psDataOut);
int (*NDK_SecGetPinResult)(uchar *psPinBlock, int *nStatus);
int (*NDK_SecSetKeyOwner)(char *pszName);
int (*NDK_SecGetTamperStatus)(int *pnStatus);
int (*NDK_SecGetPinResultDukpt)(uchar *psPinBlock, uchar *psKsn, int *nStatus);
int (*NDK_GetTamperStatus)();
int (*NDK_SecKeyDelete)(uchar ucKeyIdx,uchar ucKeyType);
int (*NDK_SysGoSuspend_Extern)(void);
int (*NDK_SecGetDrySR)(int *pnVal);
int (*NDK_SecClear)(void);
int (*NDK_SecVppTpInit)(uchar *num_btn, uchar *func_key, uchar *out_seq);
int (*NDK_SecUserKeyDelete)(void);
int (*NDK_SecLoadDukptKey)(uchar ucGroupIdx, uchar ucSrcKeyType, uchar ucSrcKeyIdx, uchar ucKeyLen, uchar * psKeyValueIn, uchar * psKsnIn, ST_SEC_KCV_INFO * pstKcvInfoIn);

int (*NDK_RpcTransRW)(unsigned char* DataIn,int LenIn,unsigned char *DataOut,int *LenOut,int maxlen,int utimeout);
int (*NDK_initSdtp)();
int (*NDK_InitCom )( int (*send)(unsigned char * data, int len, int timeout),int (*recv)(unsigned char * buf, int buflen, int timeout), char * info );

int (*Ndk_beginTransactions)(int iTimeoutSec);
int (*Ndk_endTransactions)();
int (*Ndk_getStatus)();
int (*Ndk_getVKeybPin)(char* pinlen, char index, char mode, int timeout, char* account, char* KSN, char* pinblock);
int (*NDK_SYS_RegisterEvent)(EM_SYS_EVENT eventNum, int timeOutMs, int (* notifyEvent )( EM_SYS_EVENT eventNum, int msgLen, char * msg));
int (*NDK_SYS_UnRegisterEvent)(EM_SYS_EVENT eventNum);
int (*NDK_SYS_ResumeEvent)(EM_SYS_EVENT eventNum);
int (*NDK_SysOpenDebug)(int ndklev,int sdtplev);
int (*NDK_SysGetCapability)(int nSizeOfCap, char* szCaps);

//NAPI
int (*NAPI_SecKeyErase)();

int (*NAPI_SysGetInfo)(SYS_INFO_ID InfoID, char *OutBuf, int *OutBufLen);

int(*NAPI_SecTR34GenerateRandom)(uint32_t randomSize, uchar *randomData);

int(*NAPI_SecTR34ProcessKeyBlock)(ST_SEC_TR34_BLOCK_PARAMS *tr34BlockParams, ST_SEC_TR34_KEY_INFO *keyInfo,  uint8_t *adData, uint32_t adDataLen);

int (*NAPI_SecGeneratePubkeyCert)(ST_SEC_ASYM_KEY_INFO *pstKeyinfo, ST_SEC_ASYM_KEY_INFO *pstCAinfo);

int (*NAPI_SecKeyExport)(EM_SEC_KEYEXPORT_MODE mode, ST_SEC_KEYINFO *kek, ST_SEC_KEYINFO *key, uint8_t *outdata, uint32_t *outlen, void *pad, uint32_t adSize);

int (*NAPI_SecGetSymmKeyNum)(int *pnTotalKeyNum, ST_SEC_KEYNUM_INFO *pstKeyNumInfoArray, int* pnArrayCount);

int (*NAPI_SecGetSymmKeyInfoByID)(uchar ucKeyID, ST_SEC_SYMM_KEYID_INFO *pstKeyInfoArray, int* pnArrayCount);

int (*NAPI_SecVPPAAInit)(vpp_key *keyInfo, uint32_t keyNum, vppAAConfig_st *config, vpp_button *tsArea, vpp_button *keypadArea, void *pad, uint32_t adSize);

int (*NAPI_SecVPPAASetMap)(vppEventActionMap_st *mapList, uint32_t count, uint8_t mode);

int (*NAPI_SecVPPAAGetPin)(uint32_t *vppkey, uint32_t *vppEvent, uint32_t *state, uint8_t *pinblock, uint32_t *outPinLen, uint8_t *ksn, uint32_t *ksnLen);


/**
 *@brief 		Beep
 *@param[in]   unFrequency  Frequency in Hz (0-4000]
 *@param[in]   unMsSeconds Duration in ms
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
int (*NAPI_SysBeepIt)(uint unFrequency,uint unMsSeconds);

/**
 *@brief         Encrypt Data using the algorithm and Key specified.
 *@param[in] pstDataIn       Pointer to the data for encryption (\ref ST_SEC_ENCRYPTION_DATA "ST_SEC_ENCRYPTION_DATA")
 *@param[out] psDataOut      Pointer to output data
 *@param[out] pnOutLen       Pointer to size of output data
 *@param[out] psKsnOut       Pointer to output KSN if it is a DUKPT encryption
 *@param[out] pnOutKsnLen    Pointer to the size of output KSN if it is a DUKPT encryption.
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref EM_NAPI_ERR "EM_NAPI_ERR".
*/
int (*NAPI_SecEncryption)(ST_SEC_ENCRYPTION_DATA *pstDataIn, uchar *psDataOut, int *pnOutLen, uchar *psKsnOut, int *pnOutKsnLen);

/**
 *@brief         Decrypt Data using the algorithm and Key specified.
 *@param[in] pstDataIn       Pointer to the data for decryption (\ref ST_SEC_ENCRYPTION_DATA "ST_SEC_ENCRYPTION_DATA")
 *@param[out] psDataOut      Pointer to output data
 *@param[out] pnOutLen       Pointer to size of output data
 *@param[out] psKsnOut       Pointer to output KSN if it is a DUKPT decryption.
 *@param[out] pnOutKsnLen    Pointer to the size of output KSN if it is a DUKPT decryption.
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref EM_NAPI_ERR "EM_NAPI_ERR".
*/
int (*NAPI_SecDecryption)(ST_SEC_ENCRYPTION_DATA *pstDataIn, uchar *psDataOut, int *pnOutLen, uchar *psKsnOut, int *pnOutKsnLen);

/**
*@brief Key Injection
 *@details Generic key injection for all key types, with different algorithm...
 *@param[in] Method             Key generation method,ref EM_SEC_KEYIN_METHOD "EM_SEC_KEYIN_METHOD"
 *@param[in] pstKGData          All needed data for key injection, ref ST_SEC_KEYIN_DATA "ST_SEC_KEYIN_DATA"
 *@param[in] pstKcvData         Key check value (KCV) for the injected key, Optional
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref EM_NAPI_ERR "EM_NAPI_ERR".
*/
int (*NAPI_SecGenerateKey)( EM_SEC_KEYIN_METHOD Method, ST_SEC_KEYIN_DATA *pstKGData, ST_SEC_KCV_DATA *pstKcvData);

/**
*@brief Symmetric Key Injection  with the Asymmetric key
 *@details Inject symmetric key with the asymmetric key for all key types, with different algorithm...
 *@param[in] Method             Key generation method,ref EM_SEC_KEYIN_METHOD "EM_SEC_KEYIN_METHOD"
 *@param[in] pstKGData          All needed data for key injection, ref ST_SEC_ASYM_KEYIN_DATA "ST_SEC_ASYM_KEYIN_DATA"
 *@param[in] pstKcvData         Key check value (KCV) for the injected key, Optional
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref EM_NAPI_ERR "EM_NAPI_ERR".
*/
int (*NAPI_SecAsymGenerateKey)( EM_SEC_KEYIN_METHOD Method, ST_SEC_ASYM_KEYIN_DATA *pstKGData, ST_SEC_KCV_DATA *pstKcvData);

/**
 *@brief        Generate Message Authentication Code for a block of data.
 *@param[in] CipherType      Full cipher identifier (e.g. SEC_CIPHER_AES_128_CBC)
 *@param[in] ucKeyID         Key index
 *@param[in] psIV            Initial Vector
 *@param[in] unIVSize        IV size, 8 bytes for TDES, 16 bytes for AES
 *@param[in] psDataIn        Input data
 *@param[in] nDataInLen      Input data length
 *@param[in] pAD             Additional data, Pointer to a ST_SEC_SESSION_KEY structure when a session key is used to encrypt data.
                             This means that the key indicated by KeyID is a KEK
 *@param[in] unADSize        Size of additional data, could be the size of ST_SEC_SESSION_KEY
 *@param[out] psMacOut       Pointer to output MAC value
 *@param[out] pnOutLen       Pointer to size of output data
 *@param[out] psKsnOut       Pointer to output KSN if the encryption key is DUKPT key
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref EM_NAPI_ERR "EM_NAPI_ERR".
*/
int (*NAPI_SecGenerateMAC)(EM_SEC_MAC_TYPE MacType, uchar ucKeyID, uchar *psIV, int unIVSize, uchar *psDataIn, int nDataInLen, uchar *pAD, int unADSize,
						   uchar *psMacOut, int *pnOutLen, uchar *psKsnOut, int *nOutKsnLen);

/**
*@brief 	   Returns key information such as KCV, length, etc.
*@param[in] ucKeyID 		Key index, 1~250
*@param[in] KeyType 		Key Type
*@param[in] KeyUsage		Key Usage
*@param[in] pAD 			Additional data for key information.
*@param[in] unADSize		Size of Additional data.

*@param[out] psOutInfo		 Pointer to the output buffer
*@param[out] pnOutInfoLen	 Pointer to the output length
*@return
 On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref EM_NAPI_ERR "EM_NAPI_ERR".
*/
int (*NAPI_SecGetKeyInfo)(EM_SEC_KEY_INFO_ID InfoID, uchar ucKeyID, EM_SEC_CRYPTO_KEY_TYPE KeyType, EM_SEC_KEY_USAGE KeyUsage,
						  uchar *pAD, uint unADSize, uchar *psOutInfo, int *pnOutInfoLen);


/**
 *@brief		Delete key.
 *@param[in] ucKeyID		 Key index, 1~250
 *@param[in] KeyType		 Key Type
 *@param[in] KeyUsage		 Key Usage

 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref EM_NAPI_ERR "EM_NAPI_ERR".
*/
int(*NAPI_SecDeleteKey)(uchar ucKeyID, EM_SEC_CRYPTO_KEY_TYPE KeyType, EM_SEC_KEY_USAGE KeyUsage);

int(*NAPI_SecSymmKeyErase)();

/**
 *@brief		  Initialises the Virtual (internal) PIN pad. Start the PIN entry mode.
 *@param[in] SessionType	  For SessionType "SEC_VPP_MASTER_SESSION", pAD will be an encrypted session key, see ST_SEC_SESSION_KEY.
 *@param[in] CipherID 	  PIN Key Algorithm: TDES or AES
 *@param[in] ucKeyIdx 	  PIN Key index, 1~250.
 *@param[in] pPAN 		  Primary Account Number, NULL terminated character string.
 *@param[in] PINBlockFmt	  PIN BLOCK per ISO9564, format 0~4.
 *@param[in] unTimeOut	  Timeout value (seconds), 5-200.
 *@param[in] pRSAKey		  RSA public key for the offline ciphertext PIN encryption.
 *@param[in] pAD			  Additional data, for Master Session this is packed encrypted session key, given by the structure ST_SEC_SESSION_KEY.
 *@param[in] unADSize 	  Size of Additional Data.
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref EM_NAPI_ERR "EM_NAPI_ERR".
*/
int (*NAPI_SecVPPInit)( EM_SEC_VPP_SESSION_TYPE SessionType,
						EM_SEC_CRYPTO_KEY_TYPE KeyType,
						uchar ucKeyIdx,
						char *pPAN,
						uint PINBlockFmt,
						uint unTimeOut,
						ST_NAPI_RSA_KEY *pRSAKey,
						void *pAD,
						uint unADSize );

int (*NAPI_SecVppRNIBTpInit)(vpp_key *keyInfo, uint32_t keyNum, vpp_button *tsArea, vpp_button *keypadArea);

int (*NAPI_SecVPPSetButtonFunc)(int button, EM_SEC_VPP_BUTTON_FUNC func);

/**
 *@brief		Process and get PIN entry event
 *@param[out]	nEvent		  PIN entry event, see EM_SEC_VPP_KEY
 *@param[out]	psPinBlock	  Ciphertext pinblock if the user finish PIN entry and press Enter key.
							  During the PIN entry, the first byte of psPinBlock[0] indictaes length of current PIN digits.
 *@param[out]	pnOutPinLen   Pointer to size of output pinblock.
 *@param[out]	psKsn		  Pointer to the output KSN for current PIN encryption if the "SessionType" is DUKPT.
 *@param[out]	pnOutKsnLen   Pointer to size of output KSN if the "SessionType" is DUKPT.
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref EM_NAPI_ERR "EM_NAPI_ERR".
*/
int (*NAPI_SecVPPGetEvent)(int *nEvent, uchar *psPinBlock, int *pnOutPinLen, uchar *psKsn, int *pnOutKsnLen);

/**
 @brief Simulated key code to externally influence PIN entry procedure.
 *@param[in] key    The simulated key may be set externally during PIN entry:
                    KEY_CANCEL - simulates pressing CANCEL key
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref EM_NAPI_ERR "EM_NAPI_ERR".
*/
int (*NAPI_SecVPPSetEvent)(uint key);

int (*NAPI_SecGetKeyOwner)(int nLenOfOwnerBuffer,char *pszOwner);

int (*NAPI_SecSetKeyOwner)(char *pszName);

/**
 *@brief	 csr process init(Not Supported on Android)
 *@param[in] handle 	  the handle of csr process
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
int (*NAPI_SecCSRInit)(CSR_HANDLE* handle);
/**
 *@brief	 set the SubjectName of csr process(Not Supported on Android)
 *@param[in] handle 	        the handle of csr process
 *@param[in] psSubjectName		Pointer to SubjectName
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
int (*NAPI_SecCSRSetSubjectName)(CSR_HANDLE handle,const unsigned char* psSubjectName);
/**
 *@brief	 set the key of csr process(Not Supported on Android)
 *@param[in] handle 	        the handle of csr process
 *@param[in] pstKeyinfo		    Pointer to keyinfo of the key
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
int (*NAPI_SecCSRSetKey)(CSR_HANDLE handle, ST_SEC_ASYM_KEY_INFO *pstKeyinfo);
/**
 *@brief	 set the alg of csr process(Not Supported on Android)
 *@param[in] handle 	        the handle of csr process
 *@param[in] MdAlg		        Alg
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
int (*NAPI_SecCSRSetMdAlg)(CSR_HANDLE handle, EM_SEC_MD_TYPE MdAlg);
/**
 *@brief	 set the KeyUsag of csr process(Not Supported on Android)
 *@param[in] handle 	        the handle of csr process
 *@param[in] ucKeyUsage		    KeyUsag
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
int (*NAPI_SecCSRSetKeyUsage)(CSR_HANDLE handle, unsigned short ucKeyUsage);
/**
*@brief	 set the CertType of csr process(Not Supported on Android)
*@param[in] handle 	        the handle of csr process
*@param[in] ucCertType		    CertType
*@return
 On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
int (*NAPI_SecCSRSetNSCertType)(CSR_HANDLE handle, unsigned char ucCertType);
/**
*@brief	 set the CA value of csr process(Not Supported on Android)
*@param[in] handle 	        the handle of csr process
*@param[in] ifCA		        the value of CA
*@return
 On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
int (*NAPI_SecCSRSetIsCA)(CSR_HANDLE handle, int is_ca);
/**
*@brief	 set the Extension infomation of csr process(Not Supported on Android)
*@param[in] handle 	        the handle of csr process
*@param[in] oid		        Pointer to oid
*@param[in] oidLen		        oidLen
*@param[in] val		        Pointer to val
*@param[in] valLen		        valLen
*@return
 On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
int (*NAPI_SecCSRSetExtension)(CSR_HANDLE handle, const char *oid, int oidLen, unsigned char* val, int valLen);
/**
*@brief	 generation the pem of csr(Not Supported on Android)
*@param[in] handle 	        the handle of csr process
*@param[in] pnOlen		        Pointer to len of outbuf
*@param[in] psOutBuf		    Pointer to the outbuf
*@return
 On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
int (*NAPI_SecCSRGenPem)(CSR_HANDLE handle, int *pnOlen, unsigned char* psOutBuf);
/**
*@brief	 generation the der of csr(Not Supported on Android)
*@param[in] handle 	        the handle of csr process
*@param[in] pnOlen		        Pointer to len of outbuf
*@param[in] psOutBuf		    Pointer to the outbuf
*@return
 On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
int (*NAPI_SecCSRGenDer)(CSR_HANDLE handle, int *pnOlen, unsigned char* psOutBuf);
/**
*@brief	 release the handle of csr(Not Supported on Android)
*@param[in] handle 	        the handle of csr process
*@param[in] pnOlen		        Pointer to len of outbuf
*@param[in] psOutBuf		    Pointer to the outbuf
*@return
 On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
int (*NAPI_SecCSRRelease)(CSR_HANDLE handle);

/**
 @brief Set the Lenth of passward during PIN entry.
 *@param[in] key    the Lenth of passward:
                    like:0,4,6
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
int (*NAPI_SecVPPSetExpPinLenIn)(char *pszExpPinLenIn);
int (*NAPI_SecVppTpInit)(uchar *num_btn, uchar *func_key, uchar *out_seq);
int (*NAPI_SecGetRandom)(int nRandLen, void *pvRandom);
int (*NAPI_SecLoadTrustedCert)(char isCA, char * cert, int certlen, char * pubkey, int * pubkeylen);
int (*NAPI_SecResetCertStatus)(void);
int (*NAPI_SecInitAtomic)(void);
int (*NAPI_SecCommitAtomic)(char status);
int (*NAPI_SecAsymEncryption)(ST_SEC_ASYM_KEY_INFO *pstKeyinfo, EM_SEC_MD_TYPE MdAlg, EM_SEC_ASYM_ENCODING_MODE PaddingMode, EM_SEC_ASYM_CRYPTO_MODE CryptoMode, int nDataInLen, const unsigned char *psDataIn,  int* pnDataOutLen, unsigned char *psDataOut);
int (*NAPI_SecAsymDecryption)(ST_SEC_ASYM_KEY_INFO *pstKeyinfo, EM_SEC_MD_TYPE MdAlg, EM_SEC_ASYM_ENCODING_MODE PaddingMode, EM_SEC_ASYM_CRYPTO_MODE CryptoMode,int nDataInLen, const unsigned char *psDataIn,  int* pnDataOutLen, unsigned char *psDataOut);
int (*NAPI_SecAsymSign)(ST_SEC_ASYM_KEY_INFO *pstKeyinfo, EM_SEC_MD_TYPE MdAlg, EM_SEC_ASYM_ENCODING_MODE PaddingMode, int nHashLen, const unsigned char *psHash,  int* nSigLen, const unsigned char *psSig);
int (*NAPI_SecAsymVerify)(ST_SEC_ASYM_KEY_INFO *pstKeyinfo,  EM_SEC_MD_TYPE MdAlg, EM_SEC_ASYM_ENCODING_MODE PaddingMode, int nHashLen, const unsigned char *psHash,  int nSigLen, const unsigned char *psSig);\
int (*NAPI_SecGenerateAsymKey)(NAPI_HANDLE* handle, ST_SEC_ASYM_KEY_INFO *pstKeyinfo, int nADSize, uchar *pAD);
int (*NAPI_SecGenerateAsymKeyState)(NAPI_HANDLE handle);
int (*NAPI_SecCancelGenerateAsymKey)(NAPI_HANDLE handle);
int (*NAPI_SecECDHEInit)(ECDHE_HANDLE* handle);
int (*NAPI_SecECDHERelease)(ECDHE_HANDLE handle);
int (*NAPI_SecECDHEGenKeyPair)(ECDHE_HANDLE handle, ECC_TYPE CurveType, int *pnPubKeyLen, uchar *psPubKey);
int (*NAPI_SecECDHEGenSK)(ECDHE_HANDLE handle, ST_SEC_ECDHE_KEY_INFO *pstSessionKeyInfo, ST_SEC_ECDHE_KDF_INFO *pstKDFInfo, int nServPubKeyLen, uchar *pServPubKey);
int (*NAPI_PrnOpenDev)();
int (*NAPI_PrnCloseDev)();
int (*NAPI_PrnGetStatus)(PRN_STATUS *PrnStatus);
int (*NAPI_SecGetDeviceStatus)(uint32_t *status);
int (*NAPI_SecSetDeviceStatus)(uint32_t status);
/**
*@brief
*@param[in] pstOriPINInfo Pointer to the origin PIN info (\ref ST_SEC_PINBLOCK_INFO "ST_SEC_PINBLOCK_INFO")
*@param[in] pstDstPINInfo Pointer to the destination PIN info (\ref ST_SEC_PINBLOCK_INFO "ST_SEC_PINBLOCK_INFO")
*@param[in] nOriPINBlockLen the length of origin PINBlock.
*@param[in] psOriPINBlock Pointer to the origin PINBlock.
*@param[out] pnDstPINBlockLen Pointer to the length of destination PINBlock.
*@param[out] psDstPINBlock Pointer to the destination PINBlock.
*@return
On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
int (*NAPI_SecPINBlockConvert)(ST_SEC_PINBLOCK_INFO *pstOriPINInfo, int oriPinBlockLen, uchar* oriPinBlock, ST_SEC_PINBLOCK_INFO *pstDstPINInfo, int* outPinBlockLen, uchar* outPinBlock);
int (*NAPI_SecCreateCryptogram)(ST_SEC_ASYM_KEY_INFO *cryptoKey, ST_SEC_CRYPTOINFO * cryptoInfo, ST_SEC_KEYINFO *sessionKey, ST_SEC_KEYINFO *componentSecret, uint8_t *outdata, uint32_t *outlen, uint8_t *pad, uint32_t adSize);
int (*NAPI_SecInjectPubKey)(ST_SEC_VERIFYKEY_INFO *keyVerifyInfo, ST_SEC_INJECTKEY_INFO *keyInfolist, uint32_t keyListCount, uint8_t *pad, uint32_t adSize);
int (*NAPI_AlgRSAVerifyCert)(ALG_VERIFY_CA_TYPE CaType, uint8_t * ca, uint32_t calen, uint8_t * cert, uint32_t certlen, uint8_t *pubkey, uint32_t *publen);
int (*NDK_KmlRkiGetPediRequest)(char *resp_data, int *resp_data_len, char** msg);
int (*NDK_KmlRkiSetPediResponse)(char *cmd_data, int cmd_data_len, int *resp_data_len, char** msg);
int (*NDK_KmlRkiGetPedkInitialRequest)(char *resp_data, int *resp_data_len, char** msg);
int (*NDK_KmlRkiSetPedkResponse)(char *cmd_data, int cmd_data_len, int *resp_data_len, char** msg);
int (*NDK_KmlRkiGetPedvRequest)(char *resp_data, int *resp_data_len, char** msg);
int (*NDK_KmlRkiSetPedvResponse)(char *cmd_data, int cmd_data_len, int *resp_data_len, char** msg);
int (*NDK_KmlRkiGetInstallKeyNum)(int *num);
int (*NDK_KmlRkiGetInstalledKeyInfo)(int bufLen, ST_RKL_KEY_INFO *keyInfoList);
int (*NDK_KmlRkiSetDeviceSignCertIndex)(char index);
int (*NDK_KmlRkiSetDeviceGroup)(char *name);
int (*NDK_KmlRkiSetWorkDirectory)(char *name);
int (*NDK_SysTimeBeep_Ex)(uint uFrequency, uint unSeconds, uint nVolume);

static int g_hasSecModule = 1;
static int NDK_Null()
{
    __android_log_print(ANDROID_LOG_INFO, TAG, "into NDK_Null %x", NDK_Null);
	return NDK_ERR_UNSUPPORT;
}
static void __supportFlagInit()
{
	g_rfMultiLevel = 1;
	g_aCardAtq = 1;
}

static void __setNonsupportFun(char *foo)
{
	__android_log_print(ANDROID_LOG_INFO, TAG, ">>>Nonsupport[%s]",foo);
	if(!strcmp(foo,"NDK_M1Anti_SEL")||!strcmp(foo,"NDK_M1Select_SEL"))
	{
		g_rfMultiLevel = 0;
		__android_log_print(ANDROID_LOG_INFO, TAG, ">>>Nonsupport[%s] g_rfMultiLevel[%d]",foo,g_rfMultiLevel);
	}
	if(!strcmp(foo,"NDK_RfidPiccDetect_Atq"))
	{
		g_aCardAtq = 0;
		__android_log_print(ANDROID_LOG_INFO, TAG, ">>>Nonsupport[%s] g_aCardAtq[%d]",foo,g_aCardAtq);
	}
}
static void Check_HasSecModule()
{
	char propBuf[128];
	memset(propBuf,0,sizeof(propBuf));
	int ret = property_get("persist.sys.HasSecModule",propBuf,"yes");
	if(strcmp(propBuf,"no")==0 && ret > 0){
		g_hasSecModule = 0;
	}else{
		g_hasSecModule = 1;
	}
	__android_log_print(ANDROID_LOG_INFO, TAG, ">>>persist.sys.HasSecModule[%s] g_hasSecModule[%d] ret[%d]",propBuf,g_hasSecModule,ret); 
}

#define DLSYM(lib, foo) {	                                                                                                       \	
	foo =dlsym( lib , #foo);		                                                                                               \
	dlError = (char *)dlerror();					                                                                               \
	if(lib == NULL || NULL == foo){		                                                                               \
		foo = NDK_Null;	                                                                                                           \
		rc -= 1;	                                                                                                               \
		__setNonsupportFun(#foo);                                                                                                  \
	  	__android_log_print(ANDROID_LOG_INFO, TAG, "dlsym fail:  %s "#foo"=%x ,ret will be %x\n", dlError,(int)foo,NDK_Null());    \
	}                                                                                                                              \
	dlError = NULL;                                                                                                                \
};                                                                                                                                 \

#define DLSYM2(lib, foo, flag) {	                                                                                               \
	foo =dlsym( lib , #foo);		                                                                                               \
	dlError = (char *)dlerror();					                                                                               \
	if(lib == NULL || NULL == foo){	                                                                                   \
		*flag = 0;	                                                                                                               \
		foo = NDK_Null;	                                                                                                           \
		rc -= 1;	                                                                                                               \
		__setNonsupportFun(#foo);                                                                                                  \
	  	__android_log_print(ANDROID_LOG_INFO, TAG, "dlsym fail:  %s "#foo"=%x ,ret will be %x\n", dlError,(int)foo,NDK_Null());    \
	}                                                                                                                              \
	dlError = NULL;                                                                                                                \
};

int Ndk_Dlload(){

    __supportFlagInit();
	Check_HasSecModule();

	functionLib = dlopen("libnlposapi.so",RTLD_LAZY);
	dlError = (char *)dlerror();
	__android_log_print(ANDROID_LOG_INFO, TAG, "dlopen libnlposapi.so dlError[%s] functionLib[0x%x]\n", dlError,functionLib);

	if(functionLib == NULL){
		functionLib = dlopen("libnlposapi.npt.so",RTLD_LAZY);
		dlError = (char *)dlerror();
		__android_log_print(ANDROID_LOG_INFO, TAG, "dlopen libnlposapi.npt.so dlError[%s] functionLib[0x%x]\n", dlError,functionLib);
	}

	rc = 0;
    // 不用判断是否有安全模块，都可以 dlsym 的，NDK 库里面也有判断，没有安全模块的时候接口会报错
//	if(g_hasSecModule==0){
//		functionLib = NULL;
//	}
	//dug
	DLSYM(functionLib,NDK_SysOpenDebug);
	//kb
	DLSYM(functionLib,NDK_KbFlush);
	DLSYM(functionLib,NDK_KbGetCode);
	DLSYM(functionLib,NDK_KbHit);
	DLSYM(functionLib,NDK_KbGetInput);
    DLSYM(functionLib,NDK_SysSetKeyLongPress);

	//mag
	DLSYM(functionLib,NDK_MagOpen);
	DLSYM(functionLib,NDK_MagClose);
	DLSYM(functionLib,NDK_MagReset);
	DLSYM(functionLib,NDK_MagSwiped);
	DLSYM(functionLib,NDK_MagReadNormal);
	DLSYM(functionLib,NDK_MagReadRaw);
	DLSYM(functionLib,NDK_MagReadRawData);
	DLSYM(functionLib,NDK_MagReadCards);

	//print
	DLSYM(functionLib,NDK_PrnInit);
	DLSYM(functionLib,NDK_PrnStr);
	DLSYM(functionLib,NDK_PrnStart);
	DLSYM(functionLib,NDK_PrnImage);
	DLSYM(functionLib,NDK_PrnGetVersion);
	DLSYM(functionLib,NDK_PrnSetFont);
	DLSYM(functionLib,NDK_PrnGetStatus);
	DLSYM(functionLib,NDK_PrnSetMode);
	DLSYM(functionLib,NDK_PrnSetGreyScale);
	DLSYM(functionLib,NDK_PrnSetForm);
	DLSYM(functionLib,NDK_PrnFeedByPixel);
	DLSYM(functionLib,NDK_PrnSetUnderLine);
	DLSYM(functionLib,NDK_PrnSetParam);
	DLSYM(functionLib,NDK_PrnFeedPaper);
	DLSYM(functionLib,NDK_PrnFeedPaper_Extern);
	DLSYM(functionLib,NDK_Script_Print);
	DLSYM(functionLib,Png_Pint);
	DLSYM(functionLib,setyu);
	DLSYM(functionLib,PrnInit);
	DLSYM(functionLib,NDK_PrnGetStatusValue);

	//file
	DLSYM(functionLib,NDK_FsOpen);
	DLSYM(functionLib,NDK_FsClose);
	DLSYM(functionLib,NDK_FsRead);
	DLSYM(functionLib,NDK_FsWrite);
	DLSYM(functionLib,NDK_FsSeek);
	DLSYM(functionLib,NDK_FsDel);
	DLSYM(functionLib,NDK_FsFileSize);
	DLSYM(functionLib,NDK_FsExist);
	DLSYM(functionLib,NDK_FsTruncate);
	DLSYM(functionLib,NDK_FsTell);
	DLSYM(functionLib,NDK_FsRename);
	DLSYM(functionLib,NDK_FsFormat);
	DLSYM(functionLib,NDK_CopyFileToSecMod);

	//tool
	DLSYM(functionLib,NDK_AddDigitStr);
	DLSYM(functionLib,NDK_IncNum );
	DLSYM(functionLib,NDK_FmtAmtStr );
	DLSYM(functionLib,NDK_AscToHex );
	DLSYM(functionLib,NDK_HexToAsc );
	DLSYM(functionLib,NDK_IntToC4 );
	DLSYM(functionLib,NDK_IntToC2 );
	DLSYM(functionLib,NDK_C4ToInt);
	DLSYM(functionLib,NDK_C2ToInt);
	DLSYM(functionLib,NDK_ByteToBcd);
	DLSYM(functionLib,NDK_BcdToByte);
	DLSYM(functionLib,NDK_IntToBcd);
	DLSYM(functionLib,NDK_BcdToInt);
	DLSYM(functionLib,NDK_CalcLRC);
	DLSYM(functionLib,NDK_LeftTrim);
	DLSYM(functionLib,NDK_RightTrim);
	DLSYM(functionLib,NDK_AllTrim);
	DLSYM(functionLib,NDK_AddSymbolToStr);
	DLSYM(functionLib,NDK_SubStr);
	DLSYM(functionLib,NDK_IsDigitChar);
	DLSYM(functionLib,NDK_IsDigitStr);
	DLSYM(functionLib,NDK_IsLeapYear);
	DLSYM(functionLib,NDK_MonthDays);
	DLSYM(functionLib,NDK_IsValidDate);

	//app
	DLSYM(functionLib,NDK_AppRun);
	DLSYM(functionLib,NDK_AppLoad);
	DLSYM(functionLib,NDK_AppDel);

	//ic
	DLSYM(functionLib,NDK_IccGetVersion);
	DLSYM(functionLib,NDK_IccPowerUp );
	DLSYM(functionLib,NDK_IccPowerDown);
	DLSYM(functionLib,NDK_IccDetect);
	DLSYM(functionLib,NDK_Iccrw);
	DLSYM(functionLib,NDK_IccSetConfig);
	DLSYM(functionLib,NDK_IccSetPowerUpMode);
	DLSYM(functionLib,NDK_IccGetProtocol);
	DLSYM(functionLib,NDK_IccGetWorkStatus);

	//sys
	DLSYM(functionLib,NDK_SysBeep);
	DLSYM(functionLib,NDK_Getlibver);
	DLSYM(functionLib,NDK_SysTimeBeep);
	DLSYM(functionLib,NDK_SysSetPosTime);
	DLSYM(functionLib,NDK_SysGetPosTime);
	DLSYM(functionLib,NDK_SysStartWatch);
	DLSYM(functionLib,NDK_SysStopWatch);
	DLSYM(functionLib,NDK_SysDelay);
	DLSYM(functionLib,NDK_SysMsDelay);
	DLSYM(functionLib,NDK_SysExit);
	DLSYM(functionLib,NDK_SysReboot);
	DLSYM(functionLib,NDK_SysShutDown);
	DLSYM(functionLib,NDK_SysSetBeepVol);
	DLSYM(functionLib,NDK_SysGetBatteryProperty);
	DLSYM(functionLib,NDK_SysGetBeepVol);
	DLSYM(functionLib,NDK_SysSetSuspend);
	DLSYM(functionLib,NDK_SysGoSuspend);
	DLSYM(functionLib,NDK_SysGetPowerVol);
	DLSYM(functionLib,NDK_LedStatus);
	DLSYM(functionLib,NDK_LedLt1118Status);
	DLSYM(functionLib,NDK_LedSetFlickParam);
	DLSYM(functionLib,NDK_SysReadWatch);
	DLSYM(functionLib,NDK_SysGetPosInfo);
	DLSYM(functionLib,NDK_SysGetConfigInfo);
	DLSYM(functionLib,NDK_SysInitStatisticsData);
	DLSYM(functionLib,NDK_SysGetStatisticsData);
	DLSYM(functionLib,NDK_SysGetFirmwareInfo);
	DLSYM(functionLib,NDK_SysTime);
	DLSYM(functionLib,NDK_SysSetSuspendDuration);
	DLSYM(functionLib,NDK_SysGetPowerVolRange);
	DLSYM(functionLib,NDK_SysKeyVolSet);
	DLSYM(functionLib,NDK_SysSetBeepVol_Extern);
	DLSYM(functionLib,NDK_LedFuncModeSet);
	DLSYM(functionLib,NDK_SysPeerOper);
	DLSYM(functionLib,NDK_SysEnterBoot);
	DLSYM(functionLib,NDK_SysSetPosInfo);
	DLSYM(functionLib,NDk_SysGetK21Version);
	DLSYM(functionLib,NDK_SysWakeUp);
	DLSYM(functionLib,NDK_SP_SysSetPosInfo);
	DLSYM(functionLib,NDK_SP_SysGetPosInfo);
	DLSYM(functionLib,NDK_SysGetCapability);
	DLSYM(functionLib,NDK_ScrBackLight);
	DLSYM(functionLib,NDK_ScrDispString);
	DLSYM(functionLib,NDK_ScrDrawBitmapV);
	DLSYM(functionLib,NDK_ScrClrs);
	DLSYM(functionLib,NAPI_SecGetDeviceStatus);
	DLSYM(functionLib,NAPI_SecSetDeviceStatus);
	DLSYM(functionLib,NDK_SysTimeBeep_Ex);
	DLSYM(functionLib,NDK_CEisSupport);
	//rf
	DLSYM(functionLib,NDK_RfidLogoDisplay);
	DLSYM(functionLib,NDK_RfidFunisSupport);
	DLSYM(functionLib,NDK_RfidVersion);
	DLSYM(functionLib,NDK_RfidInit);
	DLSYM(functionLib,NDK_RfidOpenRf);
	DLSYM(functionLib,NDK_RfidCloseRf);
	DLSYM(functionLib,NDK_RfidPiccState);
	DLSYM(functionLib,NDK_RfidSuspend);
	DLSYM(functionLib,NDK_RfidResume);
	DLSYM(functionLib,NDK_RfidPiccType);
	DLSYM(functionLib,NDK_RfidPiccDetect);
	DLSYM(functionLib,NDK_RfidSetDetectType);
	DLSYM(functionLib,NDK_RfidDetectWithCardType);
	DLSYM(functionLib,NDK_RfidGetPiccInfo);
	DLSYM(functionLib,NDK_RfidSetPiccParam);
	DLSYM(functionLib,NDK_RfidPiccDetect_Atq);
	DLSYM(functionLib,NDK_RfidPiccActivate);
	DLSYM(functionLib,NDK_RfidPiccActivateWithInfo);
	DLSYM(functionLib,NDK_MifareActiveWithInfo);

	DLSYM(functionLib,NDK_RfidPiccDeactivate);
	DLSYM(functionLib,NDK_RfidPiccApdu);
	DLSYM(functionLib,NDK_RfidLpcdStartDetect);
	DLSYM(functionLib,NDK_RfidLpcdGetState);
	DLSYM(functionLib,NDK_RfidLpcdStopDetect);
	DLSYM(functionLib,NDK_M1Request);
	DLSYM(functionLib,NDK_M1Anti);
	DLSYM(functionLib,NDK_M1Anti_SEL);
	DLSYM(functionLib,NDK_M1Select);	   
	DLSYM(functionLib,NDK_M1Select_SEL);	 
	DLSYM(functionLib,NDK_M1KeyStore);
	DLSYM(functionLib,NDK_M1KeyLoad);
	DLSYM(functionLib,NDK_M1InternalAuthen);
	DLSYM(functionLib,NDK_M1ExternalAuthen);
	DLSYM(functionLib,NDK_M1Read);
	DLSYM(functionLib,NDK_M1Write);
	DLSYM(functionLib,NDK_M1Increment);
	DLSYM(functionLib,NDK_M1Decrement);
	DLSYM(functionLib,NDK_M1Transfer);
	DLSYM(functionLib,NDK_M1Restore);
	DLSYM(functionLib,NDK_PiccQuickRequest);
	DLSYM(functionLib,NDK_SetIgnoreProtocol);
	DLSYM(functionLib,NDK_GetIgnoreProtocol);
	DLSYM(functionLib,NDK_GetRfidType);
	DLSYM(functionLib,NDK_RfidTypeARats);
	DLSYM(functionLib,NDK_RfidFelicaPoll);	
	DLSYM(functionLib,NDK_FelicaPoll);
	DLSYM(functionLib,NDK_FelicaSetTimeout);
	DLSYM(functionLib,NDK_RfidFelicaApdu);
	DLSYM(functionLib,NDK_MifareActive);
	DLSYM(functionLib,NDK_M0Authen);
	DLSYM(functionLib,NDK_M0Read);
	DLSYM(functionLib,NDK_M0Write);


	//alg
	DLSYM(functionLib,NDK_AlgTDes);
	DLSYM(functionLib,NDK_AlgSHA1);
	DLSYM(functionLib,NDK_AlgSHA256);

	//port
	DLSYM(functionLib,NDK_PortOpen);
	DLSYM(functionLib,NDK_PortClose);
	DLSYM(functionLib,NDK_PortRead);
	DLSYM(functionLib,NDK_PortWrite);
	DLSYM(functionLib,NDK_PortTxSendOver);
	DLSYM(functionLib,NDK_PortClrBuf);
	DLSYM(functionLib,NDK_PortReadLen);

	//sec
	DLSYM(functionLib,NDK_SecGetVer);
	DLSYM(functionLib,NDK_SecGetRandom);
	DLSYM(functionLib,NDK_SecSetCfg);
	DLSYM(functionLib,NDK_SecGetCfg);
	DLSYM(functionLib,NDK_SecGetKcv);
	DLSYM(functionLib,NDK_SecKeyErase);
	DLSYM(functionLib,NDK_SecLoadKey);
	DLSYM(functionLib,NDK_SecSetIntervaltime);
	DLSYM(functionLib,NDK_SecSetFunctionKey);
	DLSYM(functionLib,NDK_SecGetMac);
	DLSYM(functionLib,NDK_SecGetPin);
	DLSYM(functionLib,NDK_SecCalcDes);
	DLSYM(functionLib,NDK_SecVerifyPlainPin);
	DLSYM(functionLib,NDK_SecVerifyCipherPin);
	DLSYM(functionLib,NDK_SecLoadTIK);
	DLSYM(functionLib,NDK_SecGetDukptKsn);
	DLSYM(functionLib,NDK_SecIncreaseDukptKsn);
	DLSYM(functionLib,NDK_SecGetPinDukpt);
	DLSYM(functionLib,NDK_SecGetMacDukpt);
	DLSYM(functionLib,NDK_SecCalcDesDukpt);
	DLSYM(functionLib,NDK_SecLoadRsaKey);
	DLSYM(functionLib,NDK_SecRecover);
	DLSYM(functionLib,NDK_SecGetPinResult);
	DLSYM(functionLib,NDK_SecSetKeyOwner);
	DLSYM(functionLib,NDK_SecGetTamperStatus);
	DLSYM(functionLib,NDK_SecGetPinResultDukpt);
	DLSYM(functionLib,NDK_GetTamperStatus);
	DLSYM(functionLib,NDK_SecKeyDelete);
	DLSYM(functionLib,NDK_SysGoSuspend_Extern);
	DLSYM(functionLib,NDK_SecGetDrySR);
	DLSYM(functionLib,NDK_SecClear);
	DLSYM(functionLib,NDK_SecVppTpInit);
	DLSYM(functionLib,NDK_SecUserKeyDelete);
	DLSYM(functionLib,NDK_SecLoadDukptKey);
	int eventFlag = 1;
	DLSYM2(functionLib,NDK_SYS_RegisterEvent,&eventFlag);
	DLSYM2(functionLib,NDK_SYS_UnRegisterEvent,&eventFlag);

	//NAPI
	DLSYM(functionLib,NAPI_SecKeyErase);
	DLSYM(functionLib,NAPI_SecEncryption);
	DLSYM(functionLib,NAPI_SecDecryption);
	DLSYM(functionLib,NAPI_SecGenerateKey);
	DLSYM(functionLib,NAPI_SecGenerateMAC);
	DLSYM(functionLib,NAPI_SecGetKeyInfo);
    DLSYM(functionLib,NAPI_SecDeleteKey);
	DLSYM(functionLib,NAPI_SecGetKeyOwner);
	DLSYM(functionLib,NAPI_SecSetKeyOwner);
	DLSYM(functionLib,NAPI_SecSymmKeyErase);
	DLSYM(functionLib,NAPI_SecGetSymmKeyNum);
	DLSYM(functionLib,NAPI_SecGetSymmKeyInfoByID);
	DLSYM(functionLib,NAPI_SecTR34GenerateRandom);
	DLSYM(functionLib,NAPI_SecTR34ProcessKeyBlock);
    DLSYM(functionLib,NAPI_SecGeneratePubkeyCert);
	DLSYM(functionLib,NAPI_SecKeyExport);
	DLSYM(functionLib,NAPI_SecCSRInit);
	DLSYM(functionLib,NAPI_SecCSRSetSubjectName);
	DLSYM(functionLib,NAPI_SecCSRSetKey);
	DLSYM(functionLib,NAPI_SecCSRSetKeyUsage);
	DLSYM(functionLib,NAPI_SecCSRSetMdAlg);
	DLSYM(functionLib,NAPI_SecCSRSetNSCertType);
	DLSYM(functionLib,NAPI_SecCSRSetIsCA);
	DLSYM(functionLib,NAPI_SecCSRSetExtension);
	DLSYM(functionLib,NAPI_SecCSRGenPem);
	DLSYM(functionLib,NAPI_SecCSRGenDer);
	DLSYM(functionLib,NAPI_SecCSRRelease);

    DLSYM(functionLib,NAPI_SecLoadTrustedCert);
    DLSYM(functionLib,NAPI_SecResetCertStatus);
    DLSYM(functionLib,NAPI_SecInitAtomic);
    DLSYM(functionLib,NAPI_SecCommitAtomic);
    DLSYM(functionLib,NAPI_SecAsymGenerateKey);
    DLSYM(functionLib,NAPI_SecAsymEncryption);
    DLSYM(functionLib,NAPI_SecAsymDecryption);
    DLSYM(functionLib,NAPI_SecAsymSign);
    DLSYM(functionLib,NAPI_SecAsymVerify);

	DLSYM(functionLib,NAPI_SecVPPInit);
	DLSYM(functionLib,NAPI_SecVppTpInit);
	DLSYM(functionLib,NAPI_SecVppRNIBTpInit);
	DLSYM(functionLib,NAPI_SecVPPGetEvent);
	DLSYM(functionLib,NAPI_SecVPPSetEvent);
    DLSYM(functionLib,NAPI_SecVPPSetExpPinLenIn);
    DLSYM(functionLib,NAPI_SecGetRandom);
    DLSYM(functionLib,NDK_SecVerifyPIN);
	DLSYM(functionLib,NAPI_SecVPPSetButtonFunc);
	DLSYM(functionLib,NAPI_SecPINBlockConvert);
    DLSYM(functionLib,NAPI_SecCreateCryptogram);
	DLSYM(functionLib,NAPI_SecInjectPubKey);
	DLSYM(functionLib,NAPI_AlgRSAVerifyCert);

    DLSYM(functionLib, NAPI_PrnGetStatus);
    DLSYM(functionLib,NAPI_PrnOpenDev);
    DLSYM(functionLib,NAPI_PrnCloseDev);

    DLSYM(functionLib,NAPI_SysGetInfo);
    DLSYM(functionLib,NAPI_SysBeepIt);
    DLSYM(functionLib,NAPI_SecVPPAAInit);
    DLSYM(functionLib,NAPI_SecVPPAASetMap);
    DLSYM(functionLib,NAPI_SecVPPAAGetPin);

    // futurex
	DLSYM(functionLib,NDK_KmlRkiGetPediRequest);
	DLSYM(functionLib,NDK_KmlRkiSetPediResponse);
	DLSYM(functionLib,NDK_KmlRkiGetPedkInitialRequest);
	DLSYM(functionLib,NDK_KmlRkiSetPedkResponse);
	DLSYM(functionLib,NDK_KmlRkiGetPedvRequest);
	DLSYM(functionLib,NDK_KmlRkiSetPedvResponse);
	DLSYM(functionLib,NDK_KmlRkiGetInstallKeyNum);
	DLSYM(functionLib,NDK_KmlRkiGetInstalledKeyInfo);
	DLSYM(functionLib,NDK_KmlRkiSetDeviceSignCertIndex);
	DLSYM(functionLib,NDK_KmlRkiSetDeviceGroup);
	DLSYM(functionLib,NDK_KmlRkiSetWorkDirectory);

	// ECDHE
	DLSYM(functionLib, NAPI_SecECDHEInit);
	DLSYM(functionLib, NAPI_SecECDHERelease);
	DLSYM(functionLib, NAPI_SecECDHEGenKeyPair);
	DLSYM(functionLib, NAPI_SecECDHEGenSK);

	DLSYM(functionLib, NAPI_SecGenerateAsymKey);
	DLSYM(functionLib, NAPI_SecGenerateAsymKeyState);
	DLSYM(functionLib, NAPI_SecCancelGenerateAsymKey);

	functionLib = dlopen("libnl_ndk.so",RTLD_LAZY);
	dlError = (char *)dlerror();
	__android_log_print(ANDROID_LOG_INFO, TAG, "dlopen libnl_ndk.so dlError[%s] functionLib[0x%x]\n", dlError,functionLib);

	if(functionLib == NULL){
		functionLib = dlopen("libnl_ndk.npt.so",RTLD_LAZY);
		dlError = (char *)dlerror();
		__android_log_print(ANDROID_LOG_INFO, TAG, "dlopen libnl_ndk.npt.so dlError[%s] functionLib[0x%x]\n", dlError,functionLib);
	}

	// 不用判断是否有安全模块，都可以 dlsym 的，NDK 库里面也有判断，没有安全模块的时候接口会报错
//	if(g_hasSecModule==0){
//		functionLib = NULL;
//	}
	DLSYM(functionLib,Ndk_beginTransactions);
	DLSYM(functionLib,Ndk_endTransactions);
	DLSYM(functionLib,Ndk_getStatus);
	DLSYM(functionLib,Ndk_getVKeybPin);
    __android_log_print(ANDROID_LOG_INFO, TAG, ">>>eventFlag[%d]", eventFlag);
    if(!eventFlag){
        DLSYM(functionLib,NDK_SYS_RegisterEvent);
		DLSYM(functionLib,NDK_SYS_UnRegisterEvent);
	}
	DLSYM(functionLib,NDK_SYS_ResumeEvent);


	functionLib = dlopen("libnlprnapi.so", RTLD_LAZY);
	dlError = (char *)dlerror();
	__android_log_print(ANDROID_LOG_INFO, TAG, "dlopen libnlprnapi.so dlError[%s] functionLib[0x%x]\n", dlError,functionLib);

	if(functionLib != NULL && dlError == NULL)
	{
		DLSYM(functionLib, NDK_PrnGetStatus);
		DLSYM(functionLib, NDK_PrnSetGreyScale);
	}
	DLSYM(functionLib, NDK_PrnModuleInit); 
	DLSYM(functionLib, NDK_PrnCutterPerformance);

	functionLib = dlopen("libnlprintex.so",RTLD_LAZY);
	dlError = (char *)dlerror();
	__android_log_print(ANDROID_LOG_INFO, TAG, "dlopen libnlprintex.so dlError[%s] functionLib[0x%x]\n", dlError,functionLib);

	DLSYM(functionLib, TTF_PrnApiLoad);  
	DLSYM(functionLib, TTF_PrnSetPaperSize);
	DLSYM(functionLib, TTF_ScriptPrint);
	DLSYM(functionLib, TTF_PrnExit);
	DLSYM(functionLib, TTF_GetVersion);

	__android_log_print(ANDROID_LOG_INFO, TAG, "ndk_dlload = %d",rc);
	return rc;

}
static int ndkevents[]={
		SYS_EVENT_MAGCARD,
		SYS_EVENT_ICCARD,
		SYS_EVENT_RFID,
		SYS_EVENT_PIN,
		SYS_EVENT_PRNTER,
};
void NDK_UnRegisterEvent(int events){
	int idx = 0;
	for(;idx <sizeof(ndkevents) / sizeof(int); idx++){
		if(!(events&ndkevents[idx]))
			continue;
		if(events&SYS_EVENT_MAGCARD){
			LOGD_FMT(">>>UnRegister SYS_EVENT_MAGCARD.");
		}
		if(events&SYS_EVENT_ICCARD){
			LOGD_FMT(">>>UnRegister SYS_EVENT_ICCARD.");
		}
		if(events&SYS_EVENT_RFID){
			LOGD_FMT(">>>UnRegister SYS_EVENT_RFID.");
		}
		if(events&SYS_EVENT_PIN){
			LOGD_FMT(">>>UnRegister SYS_EVENT_PIN.");
		}
		if(events&SYS_EVENT_PRNTER){
			LOGD_FMT(">>>UnRegister SYS_EVENT_PRNTER.");
		}
		EXEC_NDK("#NDK_SYS_UnRegisterEvent",NDK_SYS_UnRegisterEvent(ndkevents[idx]),NDK_OK);
		if(events&SYS_EVENT_PIN){
			uchar sPinBlock[32],szPinKsn[16];
			int nStatus = 0x8000 | K_ESC;
			NDK_SecGetPinResult(sPinBlock, &nStatus);
//			EXEC_NDK("#NDK_SecGetPinResult",NDK_SecGetPinResult(sPinBlock, &nStatus),NDK_OK,COMMAND_NONE);
			NDK_SecGetPinResultDukpt(sPinBlock,szPinKsn,&nStatus);
//			EXEC_NDK("#NDK_SecGetPinResultDukpt",NDK_SecGetPinResultDukpt(sPinBlock,szPinKsn,&nStatus),NDK_OK,COMMAND_NONE);
		}
	}
}