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

#ifndef _MPOS_CMD_PBOC_H_
#define _MPOS_CMD_PBOC_H_

#include "interface.h"

#ifndef EXT
#define EXT		extern
#endif

typedef enum{
	PBOC_CAPK, /*公钥*/
	PBOC_AID,
	PBOC_TRAN_PRO,
	PBOC_GETDATA,
	PBOC_STANDARD,
	PBOC_SENC_AUTH,
	PBOC_END,
	QPBOC_PBOC	
}pboc_id_e;

#if 0
typedef struct {
	int (*EMV_Initialize)(void);
	int (*EMV_Start)(void);
	int (*EMV_Suspend)(void);
	const char * (*EMV_getVersion)(void);
	int (*EMV_ErrorCode)(void);
	int (*EMV_FetchData)(void);
	int (*EMV_getdata) (void);
	int (*EMV_setdata)(void);
	int (*EMV_parse_tlv)(void);
	int (*EMV_fetch_tlv)(void);
	int (*EMV_GetPBOCLog)(void);
	int (*EMV_GetecloadLog)(void);
	int (*EMV_OperCAPK)(void);
	int (*EMV_removeCAPKByRID)(void);
	int (*EMV_EnumCAPK)(void);
	int (*EMV_OperAID)(void);
	int (*EMV_EnumAID)(void);
	int (*EMV_buildAidList)(void);
	int (*EMV_oper_certblk)(void);
	int (*EMV_oper_cardblk)(void);
	int (*EMV_rf_start)(void);
	int (*EMV_rf_suspend)(void);
	int (*EMV_get_clvip_cardNo)(void);
	int (*EMV_ICC_getdata)(void);
	int (*PBOC_func)(pboc_id_e id, unsigned char *datain, int inlen, unsigned char *dataout, int *outlen);
} emv_api_t;
#endif

int Mpos_Rfid_SearchCard_single( unsigned char *getCardCode,  int maxCount );
int Mpos_Rfid_SearchCard( unsigned char *getCardCode ,  int waitTime );
int Validate_AuthResp(uchar *authresp);
int Getnumstr(char *buf, int maxlen, int mode, int WaitTime);
void PubLeftTrimChar(uchar *pszSrc, uchar ucRemoveChar);
int PubAddSymbolToStr(char *pszString, int nLen, char ch, int nOption);
//void SetRadixpoint(char *pszNum, int nPoint);
void PubConvAmount(uchar *pszPrefix, uchar *pszIn, uchar ucRadPt, uchar *pszOut);
void ShowAscAmount(char *psAscAmt, const char *psPrompt);
void Pack_TransLog(char *szbuff, int recLen);
int parse_pboc_log(char fmtstr[][40], uchar fmtlen[], int fmtnum, int fmt_len, int rec);
int show_one_log(char fmtstr[][40], uchar fmtlen[], int fmtnum, int fmt_len, int recnum);
int show_all_log(char fmtstr[][40], uchar fmtlen[], int fmtnum, int fmt_len, int recnum);
int Start_PBOC_Log(void);
int parse_ecload_log(char fmtstr[][40], uchar fmtlen[], int fmtnum, int fmt_len, int rec);
int show_all_log_ecload(char fmtstr[][40], uchar fmtlen[], int fmtnum, int fmt_len, int recnum);
int show_one_log_ecload(char fmtstr[][40], uchar fmtlen[], int fmtnum, int fmt_len, int recnum);
int Start_ec_load_Log(void);
int fun_emv_asc_2_bcd(uchar *ascstr, int asclen, uchar *bcdstr, int align);
int  fun_emv_bcd_2_asc(uchar *bcd, int asclen, uchar *asc, int align);
uint fun_emv_c4_2_uint(uchar *c4);
void fun_emv_uint_2_c4(unsigned int num, unsigned char *c4);
uint64_t fun_emv_bcd_2_uint64(uchar *bcd,  int bcd_len);
//void ReverseStr(unsigned char *pusSrc);
int TranslateUllTo12Asc(unsigned long long ullTransAmt,  char *pszAscAmt);
int _emv_get_bcdamt(uchar transtype, unsigned char   *cash, unsigned char   *cashback);
int _iss_ref(uchar * pan, int panlen);
int _cert_confirm(uchar type, uchar * pcon, int len);
int _acctype_sel(void);
int _inc_tsc(void);
int _lcd_msg(char * title, uchar * msg, int len,  int yesno, int waittime);
int _candidate_sel(candidate *pcan, int amt, int times);
int _emv_ec_switch( void );
int ICC_RF_PowerUp(int *pnCardNo);
int ICC_RF_PowerDown(int cardno);
int ICC_RF_Rw(int cardno, unsigned char *inbuf, int inlen, unsigned char *outbuf, int olen);
int Pack_Pboc_Dataout(uint tagList[],  int nTagNum, int result, int nTransType, char *dataout, int nFlag);
int InitEMVParam(void);

int PbocCapk_Set_ums(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
int PbocAid_Set_ums(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
int TranPro_Set_ums(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
int Pboc_GetData_ums(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
int Pboc_Standard_ums(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
int Pboc_SencAuth_ums(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
int Pboc_End_ums(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
int Pboc_GetLastWaterinfo_ums(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
int QPboc_Pboc_ums(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
int PbocCapk_Set_lakala(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
int PbocAid_Set_lakala(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
int TranPro_Set_lakala(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
int Pboc_GetData_lakala(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
int Pboc_Standard_lakala(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
int Pboc_SencAuth_lakala(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
int Pboc_End_lakala(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
int Pboc_GetLastWaterinfo_lakala(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
int QPboc_Pboc_lakala(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
int Pboc_OfflineAuth_lakala(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);



EXT int CommandParse_PbocCapk_Set(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
EXT int CommandParse_PbocAid_Set(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
EXT int CommandParse_TranPro_Set(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
EXT int CommandParse_Pboc_GetData(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
EXT int CommandParse_Pboc_Standard(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
EXT int CommandParse_Pboc_SencAuth(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
EXT int CommandParse_Pboc_End(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
EXT int CommandParse_QPboc_Pboc(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
EXT int CommandParse_Pboc_GetLastWaterinfo(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);

//lakala
EXT int CommandParse_Pboc_OfflineAuth(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen, const pSysParam_t pTParam);
#endif
