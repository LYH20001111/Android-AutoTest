/*
***********************************************************************************************
版权说明：
版本号：
生成日期：
作者：
内容：
功能：
与其它文件的关系：
修改日志：
***********************************************************************************************
*/
#ifndef _MPOS_CMDAPI_H_
#define _MPOS_CMDAPI_H_
#include "NDK.h"
#include "mpos_api_frame.h"

//#define WEAK_DEFAULT __attribute__ ((weak, alias("ME_DefaultCmd")))
/************************************************************************************************
* KB
************************************************************************************************/
extern int CommandParse_KbHit(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_KbGetCode(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_KbGetString(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
/************************************************************************************************
* 文件系统
************************************************************************************************/
extern int CommandParse_OpenRecords(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam) ;
extern int CommandParse_GetRecordNum(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam) ;
extern int CommandParse_WriteRecord(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam) ;
extern int CommandParse_GetRecord(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam) ;
extern int CommandParse_ModifyRecord(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam) ;
extern int CommandParse_WriteFile(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_ReadFile(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_DeleteFile(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
/************************************************************************************************
* 磁卡
************************************************************************************************/
extern int CommandParse_OpenCard(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_CloseCard(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_GetMagCardAuthen(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_GetMagCard_ENC(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_GetTrack_ENC(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
/************************************************************************************************
* IC
************************************************************************************************/
extern int CommandParse_IccSetType(unsigned char *pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam) ;
extern int CommandParse_IccDetect(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_IccPowerUp(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, pSysParam_t pTParam);
extern int CommandParse_IccPowerDown(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_Iccrw(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
/************************************************************************************************
* 非接
************************************************************************************************/
extern int CommandParse_RFIDPowerup(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_RFIDPowerDown(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_RFIDAPDU(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_M1StoreKey(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_M1LoadKey(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_M1Auth(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_M1AuthKey(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_M1ReadBlock(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_M1WriteBlock(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_M1Increment(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_M1Decrement(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_RFIDDetect(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);//寻卡
extern int CommandParse_RFIDAnti(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);//防冲突
extern int CommandParse_RFIDSelect (unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);//选卡
extern int CommandParse_RFIDActivate (unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);//激活
extern int CommandParse_RridDetect(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);

/************************************************************************************************
* 安全认证
************************************************************************************************/
extern int CommandParse_ReadDeviceInformation(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam) ;
extern int CommandParse_GetRandomNumber(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam) ;
extern int CommandParse_WriteEquipmentInformation(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam) ;
extern int CommandParse_DeviceAuth(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_GetSpecLENRandomNumber(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam) ;

/************************************************************************************************
* 密码键盘
************************************************************************************************/
extern int CommandParse_RandomKeyboard(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_PasswordInput(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_LoadMKey(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_DataEncryptionOrDecryption(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_DataMac(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_LoadWKey(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_PinpadInit(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_extPasswordInput(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_extDataEncryptionOrDecryption(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_extDataMac(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_extLoadMKey(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_PinpadPassthrough(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_extLoadWKey(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_LoadDukpt(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_PinpadPassthrough(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_RmKey(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_NoKeyPasswordInput(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_SetUmsKeyIndex(unsigned char *pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_IncreaseUmsKsn(unsigned char *pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_CheckKey(unsigned char *pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);

/************************************************************************************************
* 打印
************************************************************************************************/
extern int CommandParse_PrnInit(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_GetPrnStatus(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_PaperSkip(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_SetFontType(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_SetLineSpace(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_SetGreyScale(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_SetFont(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_Setting(unsigned char * pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_PrintNormal(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_PrintTup(puchar pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_SetYuzhi(puchar pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
/************************************************************************************************
* pboc
************************************************************************************************/
extern int CommandParse_PbocCapk_Set(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_PbocAid_Set(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_TranPro_Set(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_Pboc_GetData(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_Pboc_Standard(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_Pboc_SencAuth(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_Pboc_End(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_QPboc_Pboc(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_Pboc_GetLastWaterinfo(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_Pboc_OfflineAuth(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
/************************************************************************************************
* 终端管理类
************************************************************************************************/
extern int CommandParse_Buzzer(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_BlinkingLights(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_ScanStringInstruction(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_SetDateTime(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_GetDateTime(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_SetTerminalParam(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_GetTerminalParam(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_CancelReset(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_LoadApp(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_CommTest(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_ShutDown(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_Confirmation(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_SetKeyVol(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_LEDStatus(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);

/************************************************************************************************
* 生产
************************************************************************************************/
extern int CommandParse_ReadDeviceInfoPro(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_SetSN(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_FirmwareUpgrades(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_SetProPara(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_GetProPara(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);


/************************************************************************************************
* 数据透传
************************************************************************************************/
extern int CommandParse_DataPassthrough(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_CheckData(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_ReadCmdVersion(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
extern int CommandParse_LoadKEK(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);

#endif
