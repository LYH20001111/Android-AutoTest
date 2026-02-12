/**
 * Author by wuhh, Date on 2019/3/12 0022.
 */
#ifndef __PIN_H_
#define __PIN_H_
#include "ndk.h"
#include "list.h"
#define PIN_ACK_OK						"00"
#define PIN_ACK_FAIL  					"02"
#define PIN_ACK_CHECKVALUE_ERR			"41"
#define PIN_ACK_BADDATALEN				"45"
extern void Pin_PwdInput(puchar pbuf, int buf_len, unsigned char *pOut, int *outLen);
extern int Pin_Cancel();
typedef struct{
	int loadMode;
	int algMode;
	int mkeyIndex;
	int mkeyDataLen;
	uchar *mkeyData;
	int mkeyIndexDes;
	int kcvLen;
	uchar *kcvData;
	int cbcLen;
	uchar *cbcData;
}StPinLoadMKeyParam,*pStPinLoadMKeyParam;

typedef struct{
    int loadMode;
    int algMode;
    int keyType;
    int MKeyIndex;
    int WKeyIndex;
    int WKeyDataLen;
    uchar *WKeyData;
    int kcvDataLen;
    uchar *kcvData;
    int cbcDataLen;
    uchar *cbcData;
}StPinLoadWKeyParam,*pStPinLoadWKeyParam;

typedef enum{
	MKEY_TR31_BLOCK_DES = 0x01,
	MKEY_ENCRYPT_TMK_DES = 0x02,
	MKEY_PRIVATE_KEY_DES = 0x03,
	MKEY_MAIN_KEY_DES = 0x04,
	MKEY_TRANSPORT_KEY_DES = 0x05,
	MKEY_ICCARD_DES = 0x06,
	MKEY_PLAIN_KEY_DES = 0x07,
	MKEY_TR31_BLOCK_SM4 = 0x11,
	MKEY_ENCRYPT_TMK_SM4 = 0x12,
	MKEY_PRIVATE_KEY_SM4 = 0x13,
	MKEY_MAIN_KEY_SM4 = 0x14,
	MKEY_TRANSPORT_KEY_SM4 = 0x15,
	MKEY_ICCARD_SM4 = 0x16,
	MKEY_PLAIN_KEY_SM4 = 0x17,
	MKEY_TRANSPORT_KEY_AES = 0x20,
	MKEY_MAIN_KEY_AES = 0x21,
	MKEY_PLAIN_KEY_AES = 0x22,
}KekUsingType;


typedef enum{
	WKEY_TDK_DES = 0x01,
	WKEY_TPK_DES = 0x02,
	WKEY_TAK_DES = 0x03,
	WKEY_TDK_SM4 = 0x11,
	WKEY_TPK_SM4 = 0x12,
	WKEY_TAK_SM4 = 0x13,
	WKEY_TDK_AES = 0x21,
	WKEY_TPK_AES = 0x22,
	WKEY_TAK_AES = 0x23,
}WorkingKeyType;

typedef enum{
	WKEY_MODE_ENCRYPT  = 0x01,
	WKEY_MODE_PLAIN    = 0x02,
	WKEY_MODE_CALCKCV  = 0x03,
}KeyWorkingMode;

typedef struct{
	StPinLoadMKeyParam *parm;
	StPinLoadWKeyParam *wkeyparm;
	ST_SEC_KEY_INFO stKeyInfo;
	ST_SEC_KCV_INFO stKcvInfoIn;
	ST_EXTEND_KEYBLOCK stExtendKey;
	uchar ackCode[2];
	uchar kcvLen;
	uchar kcvCode[16];
}LoadKeyFunParam,*pLoadKeyAESFunParam;

typedef struct{
	int keySys;
	int algMode;
	int endeMode;
	int keyIndex;//wkey or mkey index
	int endeDataLen;
	uchar *endeData;
	int keyDataLen;
	char *keyData;
	int cbcDataLen;
	uchar*cbcData;
}StPinEnDeParam,*pStPinEnDeParam;

typedef enum {
	ENORDE_MKSK_DES   = 0x00,
	ENORDE_HANYIN          = 0x01,
	ENORDE_DOUBLE_DISPERSE = 0x02,
	ENORDE_UMS             = 0x03,
	ENORDE_MKSK_SM4        = 0x04,
	ENORDE_DUKPT_DES       = 0x05,
	ENORDE_MKSK_AES        = 0x06,
}EmEnorDeKeySysAlg;

typedef struct{	
	StPinEnDeParam *endeparam;
	int   elementLen;
	int   calcLen;
	uchar *calcData;
	uchar secKeyType;
	uchar secMode;
	uchar *descLog;
	//ack
	uchar mkeyIndex;
	uchar ackCode[2];
	uint   endeResultLen;
	uchar enderesult[4000];
	uchar ksn[10];
}StEnOrDeFunParam,*pStEnOrDeFunParam;

typedef enum{
	ENCRYPTION_ECB = 0x01,
	ENCRYPTION_CBC = 0x02,
	DECRYPTION_ECB = 0x03,
	DECRYPTION_CBC = 0x04,
}KeyMode;

typedef struct{
    uint keySys;
    uint macAlgMode;
	uint keyIndex;//mkey or wkey
//	uint keyManageType;
//	uint macAlgorithm;
	uint blockFlag;
	uint macDataLen;
	uchar *macData;
	uint  keyDataLen;
	uchar *keyData;
	uint  randomDataLen;
	uint  randomData;
}StPinMacParam,*pStPinMacParam;

typedef struct{	
	//in
	StPinMacParam *macparam;
	uchar macMode;
	//out
	uchar *resultMac[32];
	//ack
	uchar keyIndex;
	uchar ackCode[2];
	uchar mac[8];
	uchar ksn[10];
	uchar macsm4[16];//sm4;aes
}StMacFunParam,*pStMacFunParam;

#define SELF_SEC_MAC_CBC  100

typedef enum{
	MAC_ALG_X99 = 0x00,
	MAC_ALG_X919,
	MAC_ALG_ECB,
	MAC_ALG_9606,
	MAC_ALG_CBC,
	MAC_ALG_SM4 = 0x05,
	MAC_ALG_SM4_UNIONPAY = 0x06,
	MAC_ALG_AES = 0x07,
    MAC_ALG_SM4_9606 = 0x0A,
	MAC_ALG_DISPERSE_MAC_X99 = 0x10,
	MAC_ALG_DISPERSE_MAC_X919,
	MAC_ALG_DISPERSE_MAC_ECB,
	MAC_ALG_DISPERSE_MAC_9606,
	MAC_ALG_DISPERSE_DOUBLE_MAC_X99 = 0x20,
	MAC_ALG_DISPERSE_DOUBLE_MAC_X919,
	MAC_ALG_DISPERSE_DOUBLE_MAC_ECB,
	MAC_ALG_DISPERSE_DOUBLE_MAC_9606,
}MAC_Algorithm;

typedef struct{	
	int keyType;
	int keyIndex;
	int kcvDataLen;
	uchar kcvData[16];
}StGetKcvParam;


//typedef struct{
//	int keyIndex;
//	int KeyManageType;
//	int acctInputType;
//	uchar account[40];
//	int wkeyDataLen;
//	uchar *wkeyData;
//	int inputMaxLen;
//	uchar encryptExtData[10];
//	int pinFunKeyType;//回车\确认\取消
//	int timeOuts;
//	int displayContentLen;
//	uchar *displayContent;
//	int numKeySound;//0~9
//	int starKeySound;//*
//	int poundKeySound;//#
//	int cancelKeySound;
//	int backspaceKeySound;
//	int enterKeySound;
//	int externalListenMode;
//	int pwdInputRangeLen;
//	uchar pwdInputRange[20];
//	int pinblockMode;
//	int modulusLen;
//	uchar *modulus;
//	int exponentLen;
//	uchar *exponent;
//}StPinPwdInputParam;

typedef struct{
    int keySys;
    int algMode;
    int keyIndex;
//    int KeyManageType;
    int acctInputType;
    uchar account[40];
    int wkeyDataLen;
    uchar *wkeyData;
    int inputMaxLen;
//    uchar encryptExtData[10];
    int pinFunKeyType;//回车\确认\取消
    int timeOuts;
//    int displayContentLen;
//    uchar *displayContent;
//    int numKeySound;//0~9
//    int starKeySound;//*
//    int poundKeySound;//#
//    int cancelKeySound;
//    int backspaceKeySound;
//    int enterKeySound;
//    int externalListenMode;
    int pwdInputRangeLen;
    uchar pwdInputRange[20];
    int pinblockMode;
    int modulusLen;
    uchar *modulus;
    int exponentLen;
    uchar *exponent;
    char pinblockAlgMode;//ISO9564 0-4;SM1;SM2;
    int pinEventMode;//1:enable;0:disable.
	int dukptDerivateUsage;
	int derivateKeyLen;
	int isRNIB;
}StPinPwdInputParam;

typedef struct{
	//in
	StPinPwdInputParam *pwdparam;
	int   pwdLenRangeCount;
	uchar pwdLenRange[20];
	uchar apipwdLenRangIn[64];
	ST_SEC_RSA_KEY rsaKey;
	EM_SEC_PIN secPinMode;
    uchar apiPinKsn[16];
	//out
	uchar pinBlock[32];
	int   resultPwdLen;
	int   keyValue;
	//ack
	uchar ackCodeHead[2+1];

}StPwdFunParam,*pStPwdFunParam;


typedef enum {
	USE_ACCOUNT = 0x00,
	USE_ACCT_HASH = 0x01,
	UNUSE_ACCOUNT = 0x02,
	THREE_DIMENSIONS = 0x03,
	YINJIA =0x04,
	PINBLOCK = 0x05,
}EmPwdInputAcctInputType;

typedef enum {
    PINBLOCKMODE_PLAIN = 1,
    PINBLOCKMODE_OFFLINE = 2,
    PINBLOCKMODE_ENCRYPTION = 0xFF,
}EmPwdInputPinBlockMode;


typedef enum{
	PINFUNKEYTYPE_DISABLE_ENTER = 0x00,
	PINFUNKEYTYPE_ENABLE_ENTER = 0x01,
	PINFUNKEYTYPE_ENABLE_COMMAND = 0x02,
	PINFUNKEYTYPE_ENABLE_ENTER_COMMANG = 0x03,
}EmPwdInputPinFunKeyType;

typedef enum {
	PININPUTSTATUS_SYS_EVENT_PIN = 0x01,
	PININPUTSTATUS_TIMEOUT = 0x02,
	PININPUTSTATUS_CANCEL = 0x03,
	PININPUTSTATUS_SYS_EVENT_CARD = 0x04,
}EmPwdInputPinInputStatus;

typedef enum{
	PIN_KEY_CANCEL =0x06,
	PIN_KEY_BACKSPACE = 0x0A,
	PIN_KEY_SWIPCARD = 0x0B,
	PIN_KEY_ICCARD = 0x0C,
	PIN_KEY_ENTER = 0x0D,

}EmPwdInputPinKeyValue;

//OTHER

#define Response_Code_Good                            "00"
#define Response_Code_General_error                   "02"
#define Response_Code_CheckValue_error                "41"
#define Response_Code_Bad_key_tag                     "42"
#define Response_Code_Bad_master_key_index            "43"
#define Response_Code_Bad_data_length                 "45"
#define Response_Code_Invalid_TR31_block              "46"
#define Response_Code_Err_Get_Key                     "47"

enum LOAD_KEY_TYPE {
	SEC_TMK_KEY = 0,
	DATA_ENC_KEY = 1,
	PIN_WORK_KEY = 2,
	MAC_ENC_KEY = 3,
};

//3.10
typedef enum{
	MKSK  = 0x00,
	DUKPT = 0x01,
}KeySys;

typedef enum {
	DEFAULT_TRANSFER_TYPE = 0x01,
	MAINKEY_TYPE = 0x02,
	PLAIN_KEY = 0x03,
	TR31_KEY = 0x04,
}LoadMKMode;

typedef enum {
    DES = 0x01,
    SM4 = 0x02,
    AES = 0x03,
}AlgorithmMode;

typedef enum {
    WK_TRACK = 0x01,
    WK_PIN = 0x02,
    WK_MAC = 0x03,
}WKType;

typedef enum {
	KCV_TLK = 0,
	KCV_TMK = 1,
	KCV_TPK = 2,
	KCV_TAK = 3,
	KCV_TDK = 4,
}KCVKeyType;

typedef enum {
	RMKEY_TLK = 0,
	RMKEY_TMK = 1,
	RMKEY_TPK = 2,
	RMKEY_TAK = 3,
	RMKEY_TDK = 4,
	RMKEY_ALL = 5,
	RMKEY_USER= 6,
}RmKeyType;

typedef enum{
	FIRST_BLOCK = 0x00,
	NEXT_BLOCK = 0x01,
	LAST_BLOCK = 0x02,
	ONLY_BLOCK = 0x03,
}MacBlockFlag;

typedef struct
{
    uint status;
	struct list_head list;
}StPinEvent,*pStPinEvent;

extern int Pinpad_Encrypt(uint keySys,uint alg,uint cipherMode,uint keyIndex,
					 uchar *inputData,uint inputDataLen,uchar *iv,uint ivLen,
					 uchar *outputData,uint* outputDataLen,uchar *ksn,uint* ksnLen);
#endif

