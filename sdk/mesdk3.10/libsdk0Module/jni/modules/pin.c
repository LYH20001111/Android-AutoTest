/**
 * Author by wuhh, Date on 2019/3/12 0022.
 */
#include "threadtool.h"
#include "threadcond.h"
#include <jni.h>
#include <pin.h>
#include <ndk.h>
#include <threadcond.h>
#include "crypto.h"
#include "pin.h"
#include "ndk.h"
#include "log.h"
#include "api.h"
#include "desc.h"
#include "list.h"
extern JavaVM *gJavaVM;
extern jobject g_cmdRspLisObj;
extern jmethodID g_cmdRspLisMid;
static int g_secVppTpInit = 0;
static int g_keyClear;
extern char g_szPanCode[32];
LIST_HEAD(g_pinEventList);
static volatile int pinEventMode;
static volatile int pinCancelFlag;
int getHasKeyClear(){
    return g_keyClear;
}
static char* getTrackPan(){
    return g_szPanCode;
}

int dukptAESFlag = 0;

#define MKEY

int Pin_GetLoadMKeyParam(StPinLoadMKeyParam* parm,unsigned char*pInput,int iLen)
{	
	if(parm == NULL){
		return ACK_ERR;
	}
	int offset=0;
	parm->loadMode = nlMpos_Command.mpos_getvar(pInput+offset, _VAR_BIT8);offset++;
	parm->algMode = nlMpos_Command.mpos_getvar(pInput+offset, _VAR_BIT8);offset++;
	parm->mkeyIndex= nlMpos_Command.mpos_getvar(pInput+offset, _VAR_BIT8);offset++;
	parm->mkeyDataLen = nlMpos_Command.mpos_readlen(pInput+offset, _VAR_BIT16);offset += 2; 
	parm->mkeyData = pInput+offset;offset += parm->mkeyDataLen;
	parm->mkeyIndexDes = pInput[offset++];
	parm->kcvLen = nlMpos_Command.mpos_readlen(pInput+offset, _VAR_BIT16);offset += 2;
	parm->kcvData = pInput+offset;offset+=parm->kcvLen;
	parm->cbcLen = nlMpos_Command.mpos_readlen(pInput+offset, _VAR_BIT16);offset += 2;
	parm->cbcData = pInput+offset;offset+=parm->cbcLen;
	LOGD_FMT(">>>loadMode[0x%x] algMode[%d] mkeyIndex[%d] mkeyDataLen[%d] mkeyIndexDes[%d] kcvLen[%d] cbcLen[%d]",\
	parm->loadMode,parm->algMode,parm->mkeyIndex,parm->mkeyDataLen,parm->mkeyIndexDes,parm->kcvLen,parm->cbcLen);
	return ACK_OK;
}

static int __loadTLKDESSM4(int keyLen,uchar dstKeyType){
    uchar *kcv = NULL;
    if(dstKeyType == (SEC_KEY_TYPE_TLK|SEC_KEY_DES)){
        LOGD_FMT("__loadTLKDES.");
        kcv = "\x40\x82\x6A\x58";
    }else if(dstKeyType == (SEC_KEY_TYPE_TLK|SEC_KEY_SM4)){
        LOGD_FMT("__loadTLKSM4.");
        kcv = "\x39\x4E\xDD\xC9";
    }else{
        LOGD_FMT("__loadTLK Error");
        return ACK_ERR;
    }
	ST_SEC_KEY_INFO stKeyInfo;
	ST_SEC_KCV_INFO stKcvInfoIn;
	stKeyInfo.ucScrKeyIdx = 0;
	stKeyInfo.ucScrKeyType = 0;
	stKeyInfo.ucDstKeyIdx = 1;
	stKeyInfo.ucDstKeyType = dstKeyType;
	stKeyInfo.nDstKeyLen= keyLen;
	memset(stKeyInfo.sDstKeyValue, 0x31, stKeyInfo.nDstKeyLen);
	stKcvInfoIn.nCheckMode=SEC_KCV_ZERO;
	stKcvInfoIn.nLen=4;
	memcpy(stKcvInfoIn.sCheckBuf,kcv,stKcvInfoIn.nLen);
	if(!EXEC_NDK("NDK_SecLoadKey DES TLK",NDK_SecLoadKey(&stKeyInfo, &stKcvInfoIn), NDK_OK,PINPAD_LOADMKEY)){
		//return ACK_ERR;
	}
	return ACK_OK;
}
static void __loadMKeyDESSM4Base(LoadKeyFunParam *funParam)
{
	char *secKey = NULL;
	if(funParam->stKeyInfo.ucDstKeyType == (SEC_KEY_TYPE_TMK|SEC_KEY_DES)){
		secKey = "DES";
	}else if(funParam->stKeyInfo.ucDstKeyType == (SEC_KEY_TYPE_TMK|SEC_KEY_SM4)){
		secKey = "SM4";
	}else{
		LOGD_FMT(">>> __loadMKeyDESBase dstKeyType err.");
		return;
	}
	LOGD_FMT(">>>loadMKey%s",secKey);
	funParam->stKeyInfo.ucDstKeyIdx = funParam->parm->mkeyIndex;

	if(funParam->parm->loadMode == TR31_KEY){
		LOGD_FMT("TR31_KEY");
		ST_EXTEND_KEYBLOCK stExtendKey;
		memset(&stExtendKey, 0, sizeof(ST_EXTEND_KEYBLOCK));
        stExtendKey.format = SEC_KEYBLOCK_FMT_TR31;
		stExtendKey.len = funParam->parm->mkeyDataLen;
		stExtendKey.pblock = (char *)funParam->parm->mkeyData;
		funParam->stKeyInfo.nDstKeyLen = sizeof(ST_EXTEND_KEYBLOCK);
		memcpy(funParam->stKeyInfo.sDstKeyValue, &stExtendKey, sizeof(ST_EXTEND_KEYBLOCK));
	} else{
		funParam->stKeyInfo.nDstKeyLen = funParam->parm->mkeyDataLen;
		memcpy(funParam->stKeyInfo.sDstKeyValue,funParam->parm->mkeyData,funParam->parm->mkeyDataLen);
	}
	if(funParam->parm->kcvLen != 0){
		funParam->stKcvInfoIn.nCheckMode = SEC_KCV_ZERO;
		funParam->stKcvInfoIn.nLen= 4;
		memcpy(funParam->stKcvInfoIn.sCheckBuf,funParam->parm->kcvData,funParam->stKcvInfoIn.nLen);
	}else{
		funParam->stKcvInfoIn.nCheckMode=SEC_KCV_NONE;
		funParam->stKcvInfoIn.nLen=0;
	}
	int nRet = 0;
	if(EXEC_NDK("NDK_SecLoadKey",nRet = NDK_SecLoadKey(&funParam->stKeyInfo,&funParam->stKcvInfoIn), NDK_OK,PINPAD_LOADMKEY)){
		LOGD_FMT(">>>NDK_SecLoadKey %s SUCC",secKey);
		if(funParam->parm->kcvLen == 0){
			ST_SEC_KCV_INFO stKcvInfoInTmp;
			stKcvInfoInTmp.nCheckMode = SEC_KCV_ZERO;
			if(EXEC_NDK("NDK_SecGetKcv", NDK_SecGetKcv(funParam->stKeyInfo.ucDstKeyType,funParam->parm->mkeyIndex, &stKcvInfoInTmp), NDK_OK,PINPAD_LOADMKEY)){
				char temp[128];
				memset(temp,0,sizeof(temp));
				sprintf(temp, "NDK_SecGetKcv %s SUCC",secKey);
				LOGD_STR(temp,stKcvInfoInTmp.sCheckBuf,stKcvInfoInTmp.nLen);
				memcpy(funParam->ackCode,PIN_ACK_OK,2);
				funParam->kcvLen = stKcvInfoInTmp.nLen;
				LOGD_FMT(">>>funParam->kcvLen[%d]",funParam->kcvLen);
				memcpy(funParam->kcvCode,stKcvInfoInTmp.sCheckBuf,stKcvInfoInTmp.nLen);
			}
		}else{
			memcpy(funParam->ackCode,PIN_ACK_OK,2);
		}
	}else if(NDK_ERR_SECP_KCV_CHK == nRet){
		memcpy(funParam->ackCode,PIN_ACK_CHECKVALUE_ERR,2);
	}

}
static void __loadMKeyDESTransport(LoadKeyFunParam *funParam)
{
	LOGD_FMT("");
	if(__loadTLKDESSM4(funParam->parm->mkeyDataLen,SEC_KEY_TYPE_TLK|SEC_KEY_DES)==ACK_ERR){
		return;
	}
	funParam->stKeyInfo.ucScrKeyIdx=1;
	funParam->stKeyInfo.ucScrKeyType = SEC_KEY_TYPE_TLK|SEC_KEY_DES;
	funParam->stKeyInfo.ucDstKeyType = SEC_KEY_TYPE_TMK|SEC_KEY_DES;
	__loadMKeyDESSM4Base(funParam);
}
static void __loadMKeyDESMain(LoadKeyFunParam *funParam)
{
	LOGD_FMT("");
	funParam->stKeyInfo.ucScrKeyIdx=funParam->parm->mkeyIndexDes;
	funParam->stKeyInfo.ucScrKeyType = SEC_KEY_TYPE_TMK|SEC_KEY_DES;
	funParam->stKeyInfo.ucDstKeyType = SEC_KEY_TYPE_TMK|SEC_KEY_DES;
	__loadMKeyDESSM4Base(funParam);
}
static void __loadMKeyDESPlain(LoadKeyFunParam *funParam)
{
	LOGD_FMT("");
	funParam->stKeyInfo.ucScrKeyIdx=0;
	funParam->stKeyInfo.ucScrKeyType=0;
	funParam->stKeyInfo.ucDstKeyType = SEC_KEY_TYPE_TMK|SEC_KEY_DES;
	__loadMKeyDESSM4Base(funParam);
}
static void __loadMKeyDESTR31(LoadKeyFunParam *funParam)
{
	LOGD_FMT("");
	funParam->stKeyInfo.ucScrKeyIdx=funParam->parm->mkeyIndexDes;
	funParam->stKeyInfo.ucScrKeyType = SEC_KEY_TYPE_TMK|SEC_KEY_DES;
	funParam->stKeyInfo.ucDstKeyType = SEC_KEY_TYPE_TMK|SEC_KEY_DES;
	__loadMKeyDESSM4Base(funParam);
}
static void __loadMKeySM4Transport(LoadKeyFunParam *funParam)
{
	LOGD_FMT("");
	if(__loadTLKDESSM4(funParam->parm->mkeyDataLen,SEC_KEY_TYPE_TLK|SEC_KEY_SM4)==ACK_ERR){
		return;
	}
	funParam->stKeyInfo.ucScrKeyIdx=1;
	funParam->stKeyInfo.ucScrKeyType = SEC_KEY_TYPE_TLK|SEC_KEY_SM4;
	funParam->stKeyInfo.ucDstKeyType = SEC_KEY_TYPE_TMK|SEC_KEY_SM4;
	__loadMKeyDESSM4Base(funParam);
}
static void __loadMKeySM4Main(LoadKeyFunParam *funParam)
{
	LOGD_FMT("");
	funParam->stKeyInfo.ucScrKeyIdx=funParam->parm->mkeyIndexDes;
	funParam->stKeyInfo.ucScrKeyType = SEC_KEY_TYPE_TMK|SEC_KEY_SM4;
	funParam->stKeyInfo.ucDstKeyType = SEC_KEY_TYPE_TMK|SEC_KEY_SM4;
	__loadMKeyDESSM4Base(funParam);
}
static void __loadMKeySM4Plain(LoadKeyFunParam *funParam)
{
	LOGD_FMT("");
	funParam->stKeyInfo.ucScrKeyIdx=0;
	funParam->stKeyInfo.ucScrKeyType=0;
	funParam->stKeyInfo.ucDstKeyType = SEC_KEY_TYPE_TMK|SEC_KEY_SM4;
	__loadMKeyDESSM4Base(funParam);
}
static void __loadMKeySM4TR31(LoadKeyFunParam *funParam)
{
	LOGD_FMT("");
	funParam->stKeyInfo.ucScrKeyIdx=funParam->parm->mkeyIndexDes;
	funParam->stKeyInfo.ucScrKeyType=SEC_KEY_TYPE_TMK|SEC_KEY_SM4;
	funParam->stKeyInfo.ucDstKeyType = SEC_KEY_TYPE_TMK|SEC_KEY_SM4;
	__loadMKeyDESSM4Base(funParam);
}
static int __loadTLKAES(int keyLen)
{
	LOGD_FMT(".");
	ST_SEC_KEY_INFO stKeyInfo;
	ST_SEC_KCV_INFO stKcvInfoIn;
	ST_EXTEND_KEYBLOCK stExtendKey;
	memset(&stKeyInfo,0,sizeof(ST_SEC_KEY_INFO));
	memset(&stKcvInfoIn,0,sizeof(ST_SEC_KCV_INFO));
	memset(&stExtendKey, 0, sizeof(ST_EXTEND_KEYBLOCK));
	stKeyInfo.ucScrKeyIdx=0;
	stKeyInfo.ucScrKeyType=0;
	stKeyInfo.ucDstKeyIdx=1;
	stKeyInfo.ucDstKeyType=SEC_KEY_TYPE_TLK|SEC_KEY_AES;
	stKeyInfo.nDstKeyLen = sizeof(ST_EXTEND_KEYBLOCK);
	stExtendKey.format = SEC_KEYBLOCK_FMT_AES;
	stExtendKey.len = keyLen;
	uchar tlkKey[keyLen];
	memset(tlkKey,0x31,sizeof(tlkKey));
	stExtendKey.pblock = tlkKey;
	memcpy(stKeyInfo.sDstKeyValue, &stExtendKey, sizeof(ST_EXTEND_KEYBLOCK));

	stKcvInfoIn.nCheckMode=SEC_KCV_ZERO;
	stKcvInfoIn.nLen=4;
	memcpy(stKcvInfoIn.sCheckBuf,"\xbd\xba\x7e\x06",stKcvInfoIn.nLen);
	if(!EXEC_NDK("NDK_SecLoadKey AES TLK",NDK_SecLoadKey(&stKeyInfo, &stKcvInfoIn), NDK_OK,PINPAD_LOADMKEY)){
		//return ACK_ERR;
	}
	return ACK_OK;

}
static void __loadMKeyAESBase(LoadKeyFunParam *funParam)
{
	funParam->stKeyInfo.ucDstKeyIdx=funParam->parm->mkeyIndex;
	funParam->stKeyInfo.ucDstKeyType = SEC_KEY_TYPE_TMK|SEC_KEY_AES;
	funParam->stKeyInfo.nDstKeyLen = sizeof(ST_EXTEND_KEYBLOCK);
	funParam->stExtendKey.format = SEC_KEYBLOCK_FMT_AES;
	funParam->stExtendKey.len = funParam->parm->mkeyDataLen;
	funParam->stExtendKey.pblock = (char*)funParam->parm->mkeyData;
	memcpy(funParam->stKeyInfo.sDstKeyValue, &funParam->stExtendKey, sizeof(ST_EXTEND_KEYBLOCK));
	if(funParam->parm->kcvLen != 0){
		funParam->stKcvInfoIn.nCheckMode = SEC_KCV_ZERO;
		funParam->stKcvInfoIn.nLen= 4;
		memcpy(funParam->stKcvInfoIn.sCheckBuf,funParam->parm->kcvData,funParam->stKcvInfoIn.nLen);
	}else{
		funParam->stKcvInfoIn.nCheckMode=SEC_KCV_NONE;
		funParam->stKcvInfoIn.nLen=0;
	}
	int nRet = 0;
	if(EXEC_NDK("NDK_SecLoadKey AES",nRet = NDK_SecLoadKey(&funParam->stKeyInfo,&funParam->stKcvInfoIn), NDK_OK,PINPAD_LOADMKEY)){
		LOGD_FMT(">>>NDK_SecLoadKey AES SUCC");
		if(funParam->parm->kcvLen == 0){
			ST_SEC_KCV_INFO stKcvInfoInTmp;
			stKcvInfoInTmp.nCheckMode = SEC_KCV_ZERO;
			if(EXEC_NDK("NDK_SecGetKcv", NDK_SecGetKcv(SEC_KEY_TYPE_TMK|SEC_KEY_AES,funParam->parm->mkeyIndex, &stKcvInfoInTmp), NDK_OK,PINPAD_LOADMKEY)){
				LOGD_STR("NDK_SecGetKcv AES SUCC",stKcvInfoInTmp.sCheckBuf,stKcvInfoInTmp.nLen);
				memcpy(funParam->ackCode,PIN_ACK_OK,2);
				funParam->kcvLen = stKcvInfoInTmp.nLen;
				memcpy(funParam->kcvCode,stKcvInfoInTmp.sCheckBuf,stKcvInfoInTmp.nLen);
			}
		}else{
			memcpy(funParam->ackCode,PIN_ACK_OK,2);
		}
	}else if(NDK_ERR_SECP_KCV_CHK == nRet){
		memcpy(funParam->ackCode,PIN_ACK_CHECKVALUE_ERR,2);
	}
}
static void __loadMKeyAESTransport(LoadKeyFunParam *funParam)
{
	LOGD_FMT("");
	if(__loadTLKAES(funParam->parm->mkeyDataLen)==ACK_ERR){
		return;
	}
	funParam->stKeyInfo.ucScrKeyIdx=1;
	funParam->stKeyInfo.ucScrKeyType=SEC_KEY_TYPE_TLK|SEC_KEY_AES;
	__loadMKeyAESBase(funParam);
}
static void __loadMKeyAESMain(LoadKeyFunParam *funParam)
{
	LOGD_FMT("");
	funParam->stKeyInfo.ucScrKeyIdx=funParam->parm->mkeyIndexDes;
	funParam->stKeyInfo.ucScrKeyType=SEC_KEY_TYPE_TMK|SEC_KEY_AES;
	__loadMKeyAESBase(funParam);
}
static void __loadMKeyAESPlain(LoadKeyFunParam *funParam)
{
	LOGD_FMT("");
	funParam->stKeyInfo.ucScrKeyIdx=0;
	funParam->stKeyInfo.ucScrKeyType=0;
	__loadMKeyAESBase(funParam);
}

int Pinpad_LoadMKey(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen)
{
	StPinLoadMKeyParam parm;
	memset(&parm,0,sizeof(StPinLoadMKeyParam));
	Pin_GetLoadMKeyParam(&parm,pbuf,buf_len);
	int mkeyDataLen = parm.mkeyDataLen;
	
	LoadKeyFunParam funParam;
	memset(&funParam,0,sizeof(LoadKeyFunParam));
	funParam.parm = &parm;

	memcpy(funParam.ackCode,PIN_ACK_FAIL,2);

	if (parm.kcvLen != 0 && !memcmp(parm.kcvData, "\x00\x00\x00\x00\x00\x00\x00\x00",8))
		parm.kcvLen = 0;

	//0x*1或者0x*3长度不需要判断,未处理
	if(parm.loadMode != TR31_KEY && mkeyDataLen != 8 && mkeyDataLen != 16 && mkeyDataLen != 24 && mkeyDataLen != 32){
		LOGE_FMT(">>>mkeyDataLen err.");
		ERRMSG(SDK_ERR_PARAM,PINPAD_LOADMKEY);
		goto ON_ERR;
	}
	if(parm.algMode < DES || parm.algMode > AES){
        LOGE_FMT(">>>algMode err.");
		ERRMSG(SDK_ERR_PARAM,PINPAD_LOADMKEY);
        goto ON_ERR;
	}
	if(parm.loadMode < DEFAULT_TRANSFER_TYPE || parm.loadMode > TR31_KEY){
        LOGE_FMT(">>>loadMode err.");
		ERRMSG(SDK_ERR_PARAM,PINPAD_LOADMKEY);
		goto ON_ERR;
	}

	if(parm.algMode == DES){
		if(parm.loadMode == DEFAULT_TRANSFER_TYPE){
			__loadMKeyDESTransport(&funParam);
		}else if(parm.loadMode == MAINKEY_TYPE){
			__loadMKeyDESMain(&funParam);
		}else if(parm.loadMode == PLAIN_KEY){
			__loadMKeyDESPlain(&funParam);
		}else if(parm.loadMode == TR31_KEY){
			__loadMKeyDESTR31(&funParam);
		}
	}else if(parm.algMode == SM4){
		if(parm.loadMode == DEFAULT_TRANSFER_TYPE){
			__loadMKeySM4Transport(&funParam);
		}else if(parm.loadMode == MAINKEY_TYPE){
			__loadMKeySM4Main(&funParam);
		}else if(parm.loadMode == PLAIN_KEY){
			__loadMKeySM4Plain(&funParam);
		}else if(parm.loadMode == TR31_KEY){
			__loadMKeySM4TR31(&funParam);
		}
	}else if(parm.algMode == AES){
		if(parm.loadMode == DEFAULT_TRANSFER_TYPE){
			__loadMKeyAESTransport(&funParam);
		}else if(parm.loadMode == MAINKEY_TYPE){
			__loadMKeyAESMain(&funParam);
		}else if(parm.loadMode == PLAIN_KEY){
			__loadMKeyAESPlain(&funParam);
		}else if(parm.loadMode == TR31_KEY){

		}
	}
	int offset = 2;
	memcpy(pOut+offset,funParam.ackCode, 2);offset += 2;
	nlMpos_Command.mpos_writelen(pOut+offset,funParam.kcvLen, _VAR_BIT16);offset += 2;
	memcpy(pOut+offset,funParam.kcvCode,funParam.kcvLen);offset += funParam.kcvLen;
	responseCmd(pOut,offset-2,outLen,CMD_OK);
	return 0;

	ON_ERR:
	responseCmd(pOut,0,outLen,CMD_ERR_OTHER);
	return 0;
}

#define WKEY

int Pin_GetLoadWKeyParam(StPinLoadWKeyParam* parm,unsigned char*pInput,int iLen)
{
	if(parm == NULL){
		return ACK_ERR;
	}
	int offset = 0;
	parm->loadMode = nlMpos_Command.mpos_getvar(pInput+offset, _VAR_BIT8);offset++;
	parm->algMode = nlMpos_Command.mpos_getvar(pInput+offset, _VAR_BIT8);offset++;
	parm->keyType = nlMpos_Command.mpos_getvar(pInput+offset, _VAR_BIT8);offset++;
	parm->MKeyIndex = nlMpos_Command.mpos_getvar(pInput+offset, _VAR_BIT8);offset++;
	parm->WKeyIndex = nlMpos_Command.mpos_getvar(pInput+offset, _VAR_BIT8);offset++;
	parm->WKeyDataLen = nlMpos_Command.mpos_readlen(pInput+offset, _VAR_BIT16);offset+=2;
	parm->WKeyData = pInput+offset;offset += parm->WKeyDataLen;
	parm->kcvDataLen = nlMpos_Command.mpos_readlen(pInput+offset, _VAR_BIT16);offset+=2;
	parm->kcvData = pInput+offset;offset += parm->kcvDataLen;
	parm->cbcDataLen = nlMpos_Command.mpos_readlen(pInput+offset, _VAR_BIT16);offset+=2;
	parm->cbcData = pInput+offset;offset+=parm->cbcDataLen;
	LOGD_FMT(">>>loadMode[%d] algMode[%d] keyType[0x%x] MKeyIndex[%d] WKeyIndex[%d] WKeyDataLen[%d] kcvDataLen[%d] cbcDataLen[%d]",\
			 parm->loadMode,parm->algMode,parm->keyType,parm->MKeyIndex,parm->WKeyIndex,parm->WKeyDataLen,parm->kcvDataLen,parm->cbcDataLen);
	return ACK_OK;
}
static void __loadWKeyDESSM4Base(LoadKeyFunParam *funParam)
{
	LOGD_FMT("funParam->stKeyInfo.ucDstKeyType[%04x]",funParam->stKeyInfo.ucDstKeyType);
	char *secKey = NULL;
	 if((funParam->stKeyInfo.ucDstKeyType & SEC_KEY_SM4) ==SEC_KEY_SM4){
		secKey = "SM4";
	}else if((funParam->stKeyInfo.ucDstKeyType & SEC_KEY_DES) == SEC_KEY_DES){
		secKey = "DES";
	}else{
		LOGD_FMT(">>> __loadWKeyDESSM4Base dstKeyType err.");
		return;
	}
    funParam->stKeyInfo.ucDstKeyIdx=funParam->wkeyparm->WKeyIndex;


    ST_EXTEND_KEYBLOCK stKeyBlock;
    ST_KEYBLOCK_CBC cbckb;

    cbckb.ivlen = funParam->wkeyparm->cbcDataLen;
    char ivData[cbckb.ivlen];

    cbckb.len = funParam->wkeyparm->WKeyDataLen;
    char inputData[cbckb.len];

    LOGD_FMT("ivlen[%d] cbckb.len[%d]",cbckb.ivlen,cbckb.len);
	if(funParam->wkeyparm->cbcDataLen != 0){
		LOGD_FMT(">>>loadWKey%s CBC..",secKey);

		memcpy(ivData,funParam->wkeyparm->cbcData,cbckb.ivlen);//cbckb.iv = (char*)funParam->wkeyparm->cbcData;
		cbckb.iv = ivData;
		LOGD_FMT(">>>cbckb.ivlen[%d]",cbckb.ivlen);
		LOGD_STR("cbckb.ivData",cbckb.iv,cbckb.ivlen);

		memcpy(inputData,funParam->wkeyparm->WKeyData,cbckb.len);//cbckb.input = (char*)funParam->wkeyparm->WKeyData;
		cbckb.input = inputData;
		LOGD_FMT(">>>cbckb.len[%d]",cbckb.len);
		LOGD_STR("cbckb.input",cbckb.input,cbckb.len);

		stKeyBlock.format = SEC_KEYBLOCK_FMT_CBC;
		stKeyBlock.len = sizeof(ST_KEYBLOCK_CBC);
		stKeyBlock.pblock = (char*)&cbckb;
		funParam->stKeyInfo.nDstKeyLen = sizeof(ST_EXTEND_KEYBLOCK);
        memcpy(funParam->stKeyInfo.sDstKeyValue, &stKeyBlock, sizeof(ST_EXTEND_KEYBLOCK));
	}else{
		LOGD_FMT(">>>loadWKey%s ECB",secKey);
		funParam->stKeyInfo.nDstKeyLen = funParam->wkeyparm->WKeyDataLen;
		memcpy(funParam->stKeyInfo.sDstKeyValue,funParam->wkeyparm->WKeyData,funParam->wkeyparm->WKeyDataLen);
	}
    if(funParam->wkeyparm->kcvDataLen != 0){
        funParam->stKcvInfoIn.nCheckMode = SEC_KCV_ZERO;
        funParam->stKcvInfoIn.nLen= 4;
        memcpy(funParam->stKcvInfoIn.sCheckBuf,funParam->wkeyparm->kcvData,funParam->stKcvInfoIn.nLen);
    }else{
        funParam->stKcvInfoIn.nCheckMode=SEC_KCV_NONE;
        funParam->stKcvInfoIn.nLen=0;
    }
    int nRet = 0;
    if(EXEC_NDK("NDK_SecLoadKey",nRet = NDK_SecLoadKey(&funParam->stKeyInfo, &funParam->stKcvInfoIn), NDK_OK,PINPAD_LOADWKEY)){
		LOGD_FMT(">>>NDK_SecLoadKey %s SUCC",secKey);
        if(funParam->wkeyparm->kcvDataLen == 0){
            ST_SEC_KCV_INFO stKcvInfoInTmp;
            stKcvInfoInTmp.nCheckMode = SEC_KCV_ZERO;
            if(EXEC_NDK("NDK_SecGetKcv", NDK_SecGetKcv(funParam->stKeyInfo.ucDstKeyType,funParam->wkeyparm->WKeyIndex, &stKcvInfoInTmp), NDK_OK,PINPAD_LOADWKEY)){
				char temp[128];
				memset(temp,0,sizeof(temp));
				sprintf(temp, "NDK_SecGetKcv %s SUCC",secKey);
                LOGD_STR(temp,stKcvInfoInTmp.sCheckBuf,stKcvInfoInTmp.nLen);
                memcpy(funParam->ackCode,PIN_ACK_OK,2);
				funParam->kcvLen = stKcvInfoInTmp.nLen;
                memcpy(funParam->kcvCode,stKcvInfoInTmp.sCheckBuf,stKcvInfoInTmp.nLen);
            }
        }else{
            memcpy(funParam->ackCode,PIN_ACK_OK,2);
        }
    }else if(NDK_ERR_SECP_KCV_CHK == nRet) {
        memcpy(funParam->ackCode, PIN_ACK_CHECKVALUE_ERR, 2);
    }
}


static void __loadWKeyDESTDK(LoadKeyFunParam *funParam)
{
    LOGD_FMT("");
	if(funParam->wkeyparm->loadMode == WKEY_MODE_ENCRYPT){
        funParam->stKeyInfo.ucScrKeyType = SEC_KEY_TYPE_TMK|SEC_KEY_DES;
		funParam->stKeyInfo.ucScrKeyIdx = funParam->wkeyparm->MKeyIndex;
    }else if(funParam->wkeyparm->loadMode == WKEY_MODE_PLAIN){
		funParam->stKeyInfo.ucScrKeyType = 0;
		funParam->stKeyInfo.ucScrKeyIdx  = 0;
    }
	funParam->stKeyInfo.ucDstKeyType = SEC_KEY_TYPE_TDK|SEC_KEY_DES;
	__loadWKeyDESSM4Base(funParam);
}
static void __loadWKeyDESTPK(LoadKeyFunParam *funParam)
{
    LOGD_FMT("");
    if(funParam->wkeyparm->loadMode == WKEY_MODE_ENCRYPT){
        funParam->stKeyInfo.ucScrKeyType = SEC_KEY_TYPE_TMK|SEC_KEY_DES;
		funParam->stKeyInfo.ucScrKeyIdx = funParam->wkeyparm->MKeyIndex;
    }else if(funParam->wkeyparm->loadMode == WKEY_MODE_PLAIN){
		funParam->stKeyInfo.ucScrKeyType = 0;
		funParam->stKeyInfo.ucScrKeyIdx  = 0;
    }
	funParam->stKeyInfo.ucDstKeyType = SEC_KEY_TYPE_TPK|SEC_KEY_DES;
    __loadWKeyDESSM4Base(funParam);
}
static void __loadWKeyDESTAK(LoadKeyFunParam *funParam)
{
    LOGD_FMT("");
    if(funParam->wkeyparm->loadMode == WKEY_MODE_ENCRYPT){
        funParam->stKeyInfo.ucScrKeyType = SEC_KEY_TYPE_TMK|SEC_KEY_DES;
		funParam->stKeyInfo.ucScrKeyIdx = funParam->wkeyparm->MKeyIndex;
    }else if(funParam->wkeyparm->loadMode == WKEY_MODE_PLAIN){
		funParam->stKeyInfo.ucScrKeyType = 0;
		funParam->stKeyInfo.ucScrKeyIdx  = 0;
    }
	funParam->stKeyInfo.ucDstKeyType = SEC_KEY_TYPE_TAK|SEC_KEY_DES;
	__loadWKeyDESSM4Base(funParam);
}
static void __loadWKeySM4TDK(LoadKeyFunParam *funParam)
{
	LOGD_FMT("");
    if(funParam->wkeyparm->loadMode == WKEY_MODE_ENCRYPT){
        funParam->stKeyInfo.ucScrKeyType = SEC_KEY_TYPE_TMK|SEC_KEY_SM4;
		funParam->stKeyInfo.ucScrKeyIdx = funParam->wkeyparm->MKeyIndex;
    }else if(funParam->wkeyparm->loadMode == WKEY_MODE_PLAIN){
		funParam->stKeyInfo.ucScrKeyType = 0;
		funParam->stKeyInfo.ucScrKeyIdx  = 0;
    }
	funParam->stKeyInfo.ucDstKeyType = SEC_KEY_TYPE_TDK|SEC_KEY_SM4;
	__loadWKeyDESSM4Base(funParam);
}
static void __loadWKeySM4TPK(LoadKeyFunParam *funParam)
{
	LOGD_FMT("");
    if(funParam->wkeyparm->loadMode == WKEY_MODE_ENCRYPT){
        funParam->stKeyInfo.ucScrKeyType = SEC_KEY_TYPE_TMK|SEC_KEY_SM4;
		funParam->stKeyInfo.ucScrKeyIdx = funParam->wkeyparm->MKeyIndex;
    }else if(funParam->wkeyparm->loadMode == WKEY_MODE_PLAIN){
		funParam->stKeyInfo.ucScrKeyType = 0;
		funParam->stKeyInfo.ucScrKeyIdx  = 0;
    }
	funParam->stKeyInfo.ucDstKeyType = SEC_KEY_TYPE_TPK|SEC_KEY_SM4;
	__loadWKeyDESSM4Base(funParam);
}
static void __loadWKeySM4TAK(LoadKeyFunParam *funParam)
{
	LOGD_FMT("");
    if(funParam->wkeyparm->loadMode == WKEY_MODE_ENCRYPT){
        funParam->stKeyInfo.ucScrKeyType = SEC_KEY_TYPE_TMK|SEC_KEY_SM4;
		funParam->stKeyInfo.ucScrKeyIdx = funParam->wkeyparm->MKeyIndex;
    }else if(funParam->wkeyparm->loadMode == WKEY_MODE_PLAIN){
		funParam->stKeyInfo.ucScrKeyType = 0;
		funParam->stKeyInfo.ucScrKeyIdx  = 0;
    }
	funParam->stKeyInfo.ucDstKeyType = SEC_KEY_TYPE_TAK|SEC_KEY_SM4;
	__loadWKeyDESSM4Base(funParam);
}
static void __loadWKeyAESBase(LoadKeyFunParam *funParam,EM_SEC_KEY_TYPE wkeyType)
{
	funParam->stKeyInfo.ucDstKeyIdx=funParam->wkeyparm->WKeyIndex;
	funParam->stKeyInfo.ucDstKeyType = wkeyType|SEC_KEY_AES;
	funParam->stKeyInfo.nDstKeyLen = sizeof(ST_EXTEND_KEYBLOCK);
	funParam->stExtendKey.format = SEC_KEYBLOCK_FMT_AES;
	funParam->stExtendKey.len = funParam->wkeyparm->WKeyDataLen;
	funParam->stExtendKey.pblock = (char*)funParam->wkeyparm->WKeyData;
	memcpy(funParam->stKeyInfo.sDstKeyValue, &funParam->stExtendKey, sizeof(ST_EXTEND_KEYBLOCK));
	if(funParam->wkeyparm->kcvDataLen != 0){
		funParam->stKcvInfoIn.nCheckMode = SEC_KCV_ZERO;
		funParam->stKcvInfoIn.nLen= 4;
		memcpy(funParam->stKcvInfoIn.sCheckBuf,funParam->wkeyparm->kcvData,funParam->stKcvInfoIn.nLen);
	}else{
		funParam->stKcvInfoIn.nCheckMode=SEC_KCV_NONE;
		funParam->stKcvInfoIn.nLen=0;
	}
	int nRet = 0;
	if(EXEC_NDK("NDK_SecLoadKey AES",nRet = NDK_SecLoadKey(&funParam->stKeyInfo, &funParam->stKcvInfoIn), NDK_OK,PINPAD_LOADWKEY)){
		LOGD_FMT(">>>NDK_SecLoadKey AES SUCC");
		if(funParam->wkeyparm->kcvDataLen == 0){
			ST_SEC_KCV_INFO stKcvInfoInTmp;
			stKcvInfoInTmp.nCheckMode = SEC_KCV_ZERO;
			if(EXEC_NDK("NDK_SecGetKcv", NDK_SecGetKcv(funParam->stKeyInfo.ucDstKeyType ,funParam->wkeyparm->WKeyIndex, &stKcvInfoInTmp), NDK_OK,PINPAD_LOADWKEY)){
				LOGD_STR("NDK_SecGetKcv AES SUCC",stKcvInfoInTmp.sCheckBuf,stKcvInfoInTmp.nLen);
				memcpy(funParam->ackCode,PIN_ACK_OK,2);
				funParam->kcvLen = stKcvInfoInTmp.nLen;
				memcpy(funParam->kcvCode,stKcvInfoInTmp.sCheckBuf,stKcvInfoInTmp.nLen);
			}
		}else{
			memcpy(funParam->ackCode,PIN_ACK_OK,2);
		}
	}else if(NDK_ERR_SECP_KCV_CHK == nRet){
		memcpy(funParam->ackCode,PIN_ACK_CHECKVALUE_ERR,2);
	}
}
static void __loadWKeyAESTDK(LoadKeyFunParam *funParam)
{
	LOGD_FMT("");
    if(funParam->wkeyparm->loadMode == WKEY_MODE_ENCRYPT){
        funParam->stKeyInfo.ucScrKeyIdx=funParam->wkeyparm->MKeyIndex;
        funParam->stKeyInfo.ucScrKeyType=SEC_KEY_TYPE_TMK|SEC_KEY_AES;
    }else if(funParam->wkeyparm->loadMode == WKEY_MODE_PLAIN){
		funParam->stKeyInfo.ucScrKeyIdx = 0;
		funParam->stKeyInfo.ucScrKeyType = 0;
    }
	__loadWKeyAESBase(funParam,SEC_KEY_TYPE_TDK);
}
static void __loadWKeyAESTPK(LoadKeyFunParam *funParam)
{
	LOGD_FMT("");
    if(funParam->wkeyparm->loadMode == WKEY_MODE_ENCRYPT){
        funParam->stKeyInfo.ucScrKeyIdx=funParam->wkeyparm->MKeyIndex;
        funParam->stKeyInfo.ucScrKeyType=SEC_KEY_TYPE_TMK|SEC_KEY_AES;
    }else if(funParam->wkeyparm->loadMode == WKEY_MODE_PLAIN){
		funParam->stKeyInfo.ucScrKeyIdx = 0;
		funParam->stKeyInfo.ucScrKeyType = 0;
    }
	__loadWKeyAESBase(funParam,SEC_KEY_TYPE_TPK);
}
static void __loadWKeyAESTAK(LoadKeyFunParam *funParam)
{
	LOGD_FMT("");
    if(funParam->wkeyparm->loadMode == WKEY_MODE_ENCRYPT){
        funParam->stKeyInfo.ucScrKeyIdx=funParam->wkeyparm->MKeyIndex;
        funParam->stKeyInfo.ucScrKeyType=SEC_KEY_TYPE_TMK|SEC_KEY_AES;
    }else if(funParam->wkeyparm->loadMode == WKEY_MODE_PLAIN){
		funParam->stKeyInfo.ucScrKeyIdx = 0;
		funParam->stKeyInfo.ucScrKeyType = 0;
    }
	__loadWKeyAESBase(funParam,SEC_KEY_TYPE_TAK);
}
//TODO所有密钥体系的所有算法KCV都返回4字节或者4字节全零
int Pinpad_LoadWKey(puchar pbuf, int buf_len, unsigned char *pOut, int *outLen)
{
	StPinLoadWKeyParam wkeyparm;
	memset(&wkeyparm,0,sizeof(StPinLoadWKeyParam));
	Pin_GetLoadWKeyParam(&wkeyparm,pbuf,buf_len);
	int wkeyDataLen = wkeyparm.WKeyDataLen;

	LoadKeyFunParam funParam;
	memset(&funParam,0,sizeof(LoadKeyFunParam));
	funParam.wkeyparm = &wkeyparm;

	memcpy(funParam.ackCode,PIN_ACK_FAIL,2);

	if (wkeyparm.kcvDataLen != 0 && !memcmp(wkeyparm.kcvData, "\x00\x00\x00\x00\x00\x00\x00\x00",8))
		wkeyparm.kcvDataLen = 0;

	if( wkeyDataLen != 8 && wkeyDataLen != 16 && wkeyDataLen != 24 && wkeyDataLen != 32){
		LOGE_FMT(">>>wkeyDataLen err.");
		ERRMSG(SDK_ERR_PARAM,PINPAD_LOADWKEY);
		goto ON_ERR;
	}
	if((wkeyparm.loadMode != WKEY_MODE_ENCRYPT) && (wkeyparm.loadMode != WKEY_MODE_PLAIN)){
		LOGE_FMT(">>>WKeyLoadMode err.");
		ERRMSG(SDK_ERR_PARAM,PINPAD_LOADWKEY);
		goto ON_ERR;
	}
	//DES CBC模式,未处理
	if(wkeyparm.algMode < DES || wkeyparm.algMode > AES){
        LOGE_FMT(">>>algMode err.");
		ERRMSG(SDK_ERR_PARAM,PINPAD_LOADWKEY);
		goto ON_ERR;
	}

	if(wkeyparm.algMode == DES){
		if(wkeyparm.keyType == WK_TRACK){
			__loadWKeyDESTDK(&funParam);
		}else if(wkeyparm.keyType == WK_PIN){
			__loadWKeyDESTPK(&funParam);
		}else if(wkeyparm.keyType == WK_MAC){
			__loadWKeyDESTAK(&funParam);
		}
	}else if(wkeyparm.algMode == SM4){
		if(wkeyparm.keyType == WK_TRACK){
			__loadWKeySM4TDK(&funParam);
		}else if(wkeyparm.keyType == WK_PIN){
			__loadWKeySM4TPK(&funParam);
		}else if(wkeyparm.keyType == WK_MAC){
			__loadWKeySM4TAK(&funParam);
		}
	}else if(wkeyparm.algMode == AES){
		if(wkeyparm.keyType == WK_TRACK){
			__loadWKeyAESTDK(&funParam);
		}else if(wkeyparm.keyType == WK_PIN){
			__loadWKeyAESTPK(&funParam);
		}else if(wkeyparm.keyType == WK_MAC){
			__loadWKeyAESTAK(&funParam);
		}
	}
	int offset = 2;
	memcpy(pOut+offset,funParam.ackCode, 2);offset += 2;
	nlMpos_Command.mpos_writelen(pOut+offset,funParam.kcvLen, _VAR_BIT16);offset += 2;
	memcpy(pOut+offset,funParam.kcvCode,funParam.kcvLen);offset += funParam.kcvLen;
	responseCmd(pOut,offset-2,outLen,CMD_OK);
	return 0;
	ON_ERR:
	responseCmd(pOut,0,outLen,CMD_ERR_OTHER);
	return 0;
}

#define ENCRYPTION_DECRYPTION

int Pin_GetEnOrDeParam(StPinEnDeParam* parm,unsigned char*pInput,int iLen)
{
	if(parm == NULL){
		return ACK_ERR;
	}
	int offset = 0;
	parm->keySys = nlMpos_Command.mpos_getvar(pInput+offset, _VAR_BIT8);offset++;
	parm->algMode = nlMpos_Command.mpos_getvar(pInput+offset, _VAR_BIT8);offset++;
	parm->endeMode = nlMpos_Command.mpos_getvar(pInput+offset, _VAR_BIT8);offset++;
	parm->keyIndex = nlMpos_Command.mpos_getvar(pInput+offset, _VAR_BIT8);offset++;
	parm->endeDataLen = nlMpos_Command.mpos_readlen(pInput+offset, _VAR_BIT16);offset += 2;
	parm->endeData = pInput+offset;offset += parm->endeDataLen;
	parm->keyDataLen = nlMpos_Command.mpos_readlen(pInput+offset, _VAR_BIT16); offset += 2; 
	parm->keyData =  pInput+offset;offset += parm->keyDataLen;
	parm->cbcDataLen = nlMpos_Command.mpos_readlen(pInput+offset, _VAR_BIT16);offset += 2; 
	parm->cbcData = pInput+offset;offset += parm->cbcDataLen;
	LOGD_FMT(">>>keySys[%d] algMode[%d] endeMode[%d] keyIndex[%d] endeDataLen[%d] keyDataLen[%d] cbcDataLen[%d]",\
	parm->keySys,parm->algMode,parm->endeMode,parm->keyIndex,parm->endeDataLen,parm->keyDataLen,parm->cbcDataLen);
	return ACK_OK;
}
static int __DoSecCalcDes(StEnOrDeFunParam *funParam,uchar *calcData,int calcDataLen,uchar *resultData)
{
	LOGD_FMT("secKeyType[%d] keyIndex[%d] calcDataLen[%d] secMode[0x%0x]",
             funParam->secKeyType,funParam->endeparam->keyIndex,calcDataLen,funParam->secMode);
	if(!EXEC_NDK("NDK_SecCalcDes",NDK_SecCalcDes(
			funParam->secKeyType,
			funParam->endeparam->keyIndex,
            calcData,calcDataLen,resultData,
			funParam->secMode), NDK_OK,PINPAD_ENCORDEC)){
		return ACK_ERR;
	}
	return ACK_OK;
}
static int __DoSecCalcDesDukpt(StEnOrDeFunParam *funParam,uchar *calcData,int calcDataLen,uchar *resultData)
{
	if(!EXEC_NDK("NDK_SecCalcDesDukpt",
				   NDK_SecCalcDesDukpt(
						   funParam->endeparam->keyIndex,
						   funParam->secKeyType, NULL,
                           calcDataLen,calcData,resultData,
						   funParam->ksn, funParam->secMode),NDK_OK,PINPAD_ENCORDEC)){
		return ACK_ERR;
	}
	return ACK_OK;
}
static int __DecryptionCBCBase(StEnOrDeFunParam *funParam)
{
	LOGD_FMT("");
    memcpy(funParam->ackCode, PIN_ACK_FAIL, 2);
    if(funParam->endeparam->endeDataLen % funParam->elementLen != 0){
		LOGD_FMT(">>>DataLen Error.");
		return ACK_ERR;
	}
	int i=0,j=0,elementLen = funParam->elementLen;
	uchar ivxor2[elementLen];
	memcpy(ivxor2,funParam->endeparam->cbcData,funParam->endeparam->cbcDataLen);
	uchar xor1[elementLen];
	for(i=0;i+elementLen <= funParam->calcLen;i+=elementLen){
        if(funParam->endeparam->keySys == MKSK && __DoSecCalcDes(funParam,funParam->calcData+i,elementLen,xor1)!=ACK_OK){
            return ACK_ERR;
        }
        if(funParam->endeparam->keySys == DUKPT && __DoSecCalcDesDukpt(funParam,funParam->calcData+i,elementLen,xor1)!=ACK_OK){
            return ACK_ERR;
        }
		for(j=0;j<elementLen;j++){
			 funParam->enderesult[i+j] = xor1[j]^ivxor2[j];				   
		}
		memcpy(ivxor2, funParam->calcData+i, elementLen);
	}
	memcpy(funParam->ackCode, PIN_ACK_OK, 2);
    LOGD_FMT(">>>%s succ",funParam->descLog);
    return ACK_OK;
}

static int __DecryptionECBBase(StEnOrDeFunParam *funParam)
{
	LOGD_FMT("");
    memcpy(funParam->ackCode, PIN_ACK_FAIL, 2);
    if(funParam->endeparam->endeDataLen % funParam->elementLen != 0){
		LOGD_FMT(">>>DataLen Error.");
		return ACK_ERR;
	}
    if(funParam->endeparam->keySys == MKSK && __DoSecCalcDes(funParam,funParam->calcData,funParam->calcLen,funParam->enderesult)!=ACK_OK) {
        return ACK_ERR;
    }
    if(funParam->endeparam->keySys == DUKPT && __DoSecCalcDesDukpt(funParam,funParam->calcData,funParam->calcLen,funParam->enderesult)!=ACK_OK){
        return ACK_ERR;
    }
	memcpy(funParam->ackCode, PIN_ACK_OK, 2);
    LOGD_FMT(">>>%s succ",funParam->descLog);
    return ACK_OK;
}

static int __EncryptionCBCBase(StEnOrDeFunParam *funParam)
{
	LOGD_FMT("");
    memcpy(funParam->ackCode, PIN_ACK_FAIL, 2);
	int i=0,j=0,elementLen = funParam->elementLen;
	uchar ivxor2[elementLen];
	memcpy(ivxor2,funParam->endeparam->cbcData,funParam->endeparam->cbcDataLen);
	uchar xor1[elementLen];
	for(i=0;i+elementLen <= funParam->calcLen;i+=elementLen){
		for(j=0;j<elementLen;j++){
			 xor1[j] = funParam->calcData[i+j]^ivxor2[j];				   
		}
        if(funParam->endeparam->keySys == MKSK && __DoSecCalcDes(funParam,xor1,elementLen,funParam->enderesult+i)!=ACK_OK){
            return ACK_ERR;
        }
		if(funParam->endeparam->keySys == DUKPT && __DoSecCalcDesDukpt(funParam,xor1,elementLen,funParam->enderesult+i)!=ACK_OK){
            return ACK_ERR;
        }
		memcpy(ivxor2, funParam->enderesult+i, elementLen);
	}
	memcpy(funParam->ackCode, PIN_ACK_OK, 2);
    LOGD_FMT(">>>%s succ",funParam->descLog);
    return ACK_OK;
}
							
static int __EncryptionECBBase(StEnOrDeFunParam *funParam)
{
	LOGD_FMT("");
    memcpy(funParam->ackCode, PIN_ACK_FAIL, 2);
    if(funParam->endeparam->keySys == MKSK && __DoSecCalcDes(funParam,funParam->calcData,funParam->calcLen,funParam->enderesult)){
        return ACK_ERR;
    }
	if(funParam->endeparam->keySys == DUKPT && __DoSecCalcDesDukpt(funParam,funParam->calcData,funParam->calcLen,funParam->enderesult)){
        return ACK_ERR;
    }
	memcpy(funParam->ackCode, PIN_ACK_OK, 2);
    LOGD_FMT(">>>%s succ",funParam->descLog);
    return ACK_OK;
}

static int __EnOrDePre(StEnOrDeFunParam *funParam)
{
    int elementLen = funParam->elementLen;
    funParam->calcLen = funParam->endeResultLen = (funParam->endeparam->endeDataLen + elementLen - 1) / elementLen * elementLen;
    uchar *calcData = malloc(funParam->calcLen);
    if(calcData == NULL) {
        LOGD_FMT("malloc err.");
		ERRMSG(SDK_ERR_MALLOC_FAILED,PINPAD_ENCORDEC);
		return ACK_ERR;
    }
    memset(calcData,0,funParam->calcLen);
    funParam->calcData = calcData;
    memcpy(calcData,funParam->endeparam->endeData,funParam->endeparam->endeDataLen);

    int KeyMode = funParam->endeparam->endeMode;
    LOGD_FMT(">>>KeyMode[%d] endeResultLen[%d]",KeyMode,funParam->endeResultLen);
    if(funParam->endeparam->cbcDataLen != elementLen && (KeyMode == ENCRYPTION_CBC || KeyMode == DECRYPTION_CBC)){
        LOGD_FMT(">>>cbcDataLen error. KeyMode[%d]",KeyMode);
		ERRMSG(SDK_ERR_PARAM,PINPAD_ENCORDEC);
        return ACK_ERR;
    }
    return ACK_OK;
}
static int __EnOrDeEnd(StEnOrDeFunParam *funParam)
{
    if(funParam->calcData != NULL){
        LOGD_FMT("");
        free(funParam->calcData);
    }
}
static int __EnOrDeMKSKDES(StEnOrDeFunParam *funParam)
{
    funParam->elementLen = 8;
    funParam->secKeyType = SEC_KEY_TYPE_TDK|SEC_KEY_DES;
    if(__EnOrDePre(funParam)!=ACK_OK){
        return ACK_ERR;
    }
    int KeyMode = funParam->endeparam->endeMode;
    int ret =  ACK_ERR;
    switch (KeyMode)
    {
        case ENCRYPTION_CBC:
            funParam->secMode = SEC_DES_KEYLEN_DEFAULT|SEC_DES_ENCRYPT;
            funParam->descLog = "MKSK/DES/ENC/CBC";
            ret = __EncryptionCBCBase(funParam);
            break;
        case ENCRYPTION_ECB:
            funParam->secMode = SEC_DES_KEYLEN_DEFAULT|SEC_DES_ENCRYPT;
            funParam->descLog = "MKSK/DES/ENC/ECB";
            ret = __EncryptionECBBase(funParam);
            break;
        case DECRYPTION_CBC:
            funParam->secMode = SEC_DES_KEYLEN_DEFAULT|SEC_DES_DECRYPT;
            funParam->descLog = "MKSK/DES/DEC/CBC";
            ret = __DecryptionCBCBase(funParam);
            break;
        case DECRYPTION_ECB:
            funParam->secMode = SEC_DES_KEYLEN_DEFAULT|SEC_DES_DECRYPT;
            funParam->descLog = "MKSK/DES/DEC/ECB";
            ret = __DecryptionECBBase(funParam);
            break;
    }
    if(ret != ACK_OK){
        return ACK_ERR;
    }
    return ACK_OK;
}

static int __EnOrDeMKSKSM4(StEnOrDeFunParam *funParam)
{
    funParam->elementLen = 16;
    funParam->secKeyType = SEC_KEY_TYPE_TDK|SEC_KEY_SM4;
    if(__EnOrDePre(funParam)!=ACK_OK){
        return ACK_ERR;
    }
    int KeyMode = funParam->endeparam->endeMode;
    int ret = ACK_ERR;
    switch (KeyMode)
    {
        case ENCRYPTION_CBC:
            funParam->secMode = SEC_DES_KEYLEN_DEFAULT|SEC_SM4_ENCRYPT;
            funParam->descLog = "MKSK/SM4/ENC/CBC";
            ret = __EncryptionCBCBase(funParam);
            break;
        case ENCRYPTION_ECB:
            funParam->secMode = SEC_DES_KEYLEN_DEFAULT|SEC_SM4_ENCRYPT;
            funParam->descLog = "MKSK/SM4/ENC/ECB";
            ret = __EncryptionECBBase(funParam);
            break;
        case DECRYPTION_CBC:
            funParam->secMode = SEC_DES_KEYLEN_DEFAULT|SEC_SM4_DECRYPT;
            funParam->descLog = "MKSK/SM4/DEC/CBC";
            ret = __DecryptionCBCBase(funParam);
            break;
        case DECRYPTION_ECB:
            funParam->secMode = SEC_DES_KEYLEN_DEFAULT|SEC_SM4_DECRYPT;
            funParam->descLog = "MKSK/SM4/DEC/ECB";
            ret = __DecryptionECBBase(funParam);
            break;
    }
    if(ret != ACK_OK){
        return ACK_ERR;
    }
    return ACK_OK;
}
static int __EnOrDeDUKPTDES(StEnOrDeFunParam *funParam)
{
    funParam->elementLen = 8;
    funParam->secKeyType = SEC_KEY_TYPE_TDK|SEC_KEY_DES;
    if(__EnOrDePre(funParam)!=ACK_OK){
        return ACK_ERR;
    }
    int KeyMode = funParam->endeparam->endeMode;
    int ret =  ACK_ERR;
    switch (KeyMode)
    {
        case ENCRYPTION_CBC:
            funParam->secMode = SEC_DES_KEYLEN_DEFAULT|SEC_DES_ENCRYPT;
            funParam->descLog = "DUKPT/DES/ENC/CBC";
            ret = __EncryptionCBCBase(funParam);
            break;
        case ENCRYPTION_ECB:
            funParam->secMode = SEC_DES_KEYLEN_DEFAULT|SEC_DES_ENCRYPT;
            funParam->descLog = "DUKPT/DES/ENC/ECB";
            ret = __EncryptionECBBase(funParam);
            break;
        case DECRYPTION_CBC:
            funParam->secMode = SEC_DES_KEYLEN_DEFAULT|SEC_DES_DECRYPT;
            funParam->descLog = "DUKPT/DES/DEC/CBC";
            ret = __DecryptionCBCBase(funParam);
            break;
        case DECRYPTION_ECB:
            funParam->secMode = SEC_DES_KEYLEN_DEFAULT|SEC_DES_DECRYPT;
            funParam->descLog = "DUKPT/DES/DEC/ECB";
            ret = __DecryptionECBBase(funParam);
            break;
    }
    if(ret != ACK_OK){
        return ACK_ERR;
    }
    return ACK_OK;
}
static int __EnOrDeMKSKAES(StEnOrDeFunParam *funParam)
{
    funParam->elementLen = 16;
    funParam->secKeyType = SEC_KEY_TYPE_TDK|SEC_KEY_AES;
    if(__EnOrDePre(funParam)!=ACK_OK){
        return ACK_ERR;
    }
    int KeyMode = funParam->endeparam->endeMode;
    int ret = ACK_ERR;
    switch (KeyMode)
    {
        case ENCRYPTION_CBC:
            funParam->secMode = SEC_DES_KEYLEN_DEFAULT|SEC_AES_ENCRYPT;
            funParam->descLog = "MKSK/AES/ENC/CBC";
            ret = __EncryptionCBCBase(funParam);
            break;
        case ENCRYPTION_ECB:
            funParam->secMode = SEC_DES_KEYLEN_DEFAULT|SEC_AES_ENCRYPT;
            funParam->descLog = "MKSK/AES/ENC/ECB";
            ret = __EncryptionECBBase(funParam);
            break;
        case DECRYPTION_CBC:
            funParam->secMode = SEC_DES_KEYLEN_DEFAULT|SEC_AES_DECRYPT;
            funParam->descLog = "MKSK/AES/DEC/CBC";
            ret = __DecryptionCBCBase(funParam);
            break;
        case DECRYPTION_ECB:
            funParam->secMode = SEC_DES_KEYLEN_DEFAULT|SEC_AES_DECRYPT;
            funParam->descLog = "MKSK/AES/DEC/ECB";
            ret = __DecryptionECBBase(funParam);
            break;
    }
    if(ret != ACK_OK){
        return ACK_ERR;
    }
    return ACK_OK;
}

static int __EnOrDeLoadOutWk(StPinEnDeParam *param){
	LOGD_FMT("");
	if(param->keySys == DUKPT){
		ERRMSG(SDK_ERR_PARAM,PINPAD_ENCORDEC);
		return ACK_ERR;
	}
	int tmkType,tdkType;
	char *algMode = NULL;
	if(param->algMode == DES){
		tmkType = SEC_KEY_TYPE_TMK|SEC_KEY_DES;
		tdkType = SEC_KEY_TYPE_TDK|SEC_KEY_DES;
		algMode = "DES";
	}else if(param->algMode == SM4){
		tmkType = SEC_KEY_TYPE_TMK|SEC_KEY_SM4;
		tdkType = SEC_KEY_TYPE_TDK|SEC_KEY_SM4;
		algMode = "SM4";
	}else if(param->algMode == AES){
		tmkType = SEC_KEY_TYPE_TMK|SEC_KEY_AES;
		tdkType = SEC_KEY_TYPE_TDK|SEC_KEY_AES;
		algMode = "AES";
	} else{
		LOGE_FMT("");
		ERRMSG(SDK_ERR_PARAM,PINPAD_ENCORDEC);
		return ACK_ERR;
	}
	ST_SEC_KEY_INFO stKeyInfoIn;
	ST_SEC_KCV_INFO stKcvInfoIn;
    memset(&stKeyInfoIn, 0, sizeof(stKeyInfoIn));
    memset(&stKcvInfoIn, 0, sizeof(stKcvInfoIn));
	stKeyInfoIn.ucScrKeyIdx = param->keyIndex;
	stKeyInfoIn.ucScrKeyType = tmkType;
	stKeyInfoIn.ucDstKeyIdx = SESSION_KEY_INDEX;
	stKeyInfoIn.ucDstKeyType = tdkType;
	if(param->algMode == AES){
		ST_EXTEND_KEYBLOCK stExtendKey;
		memset(&stExtendKey, 0, sizeof(ST_EXTEND_KEYBLOCK));
		stKeyInfoIn.nDstKeyLen = sizeof(ST_EXTEND_KEYBLOCK);
		stExtendKey.format = SEC_KEYBLOCK_FMT_AES;
		stExtendKey.len = param->keyDataLen;
		stExtendKey.pblock = param->keyData;
		memcpy(stKeyInfoIn.sDstKeyValue, &stExtendKey, sizeof(ST_EXTEND_KEYBLOCK));
	}else{
		stKeyInfoIn.nDstKeyLen = param->keyDataLen;
        memcpy(stKeyInfoIn.sDstKeyValue,param->keyData, stKeyInfoIn.nDstKeyLen);
    }
	stKcvInfoIn.nCheckMode = SEC_KCV_NONE;
    stKcvInfoIn.nLen = 0;

	char tag[128];
	memset(tag,0, sizeof(tag));
	sprintf(tag,"NDK_SecLoadKey %s",algMode);
	if(!EXEC_NDK(tag,NDK_SecLoadKey(&stKeyInfoIn, &stKcvInfoIn), NDK_OK,PINPAD_ENCORDEC)){
        return ACK_ERR;
	}
	param->keyIndex = stKeyInfoIn.ucDstKeyIdx;
	LOGD_FMT(">>>NDK_SecLoadKey %s succ wkIndex[%d]",algMode,param->keyIndex);
    return ACK_OK;
}


#define CALCULATE_MAX_1920 1920
#define CALCULATE_MAX_1024 1024


int Pinpad_Encrypt(uint keySys,uint alg,uint cipherMode,uint keyIndex, uchar *inputData,uint inputDataLen,uchar *iv,uint ivLen, uchar *outputData,uint* outputDataLen,uchar *ksn,uint* ksnLen){
    StPinEnDeParam endeparam;
    memset(&endeparam,0,sizeof(endeparam));
    endeparam.keySys = keySys;
    endeparam.algMode = alg;
    if(cipherMode == 0){
        endeparam.endeMode = ENCRYPTION_ECB;
    } else if(cipherMode == 1){
        endeparam.endeMode = ENCRYPTION_CBC;//not support
    }
    endeparam.keyIndex = keyIndex;
    endeparam.endeDataLen = inputDataLen;
    endeparam.endeData = inputData;
    endeparam.keyDataLen = 0;
    endeparam.keyData =  NULL;
    endeparam.cbcDataLen = ivLen;
    endeparam.cbcData = iv;
    LOGD_FMT(">>>keySys[%d] algMode[%d] endeMode[%d] keyIndex[%d] endeDataLen[%d] keyDataLen[%d] cbcDataLen[%d]",\
	endeparam.keySys,endeparam.algMode,endeparam.endeMode,endeparam.keyIndex,endeparam.endeDataLen,endeparam.keyDataLen,endeparam.cbcDataLen);

	char version[16];
	memset(version,0, sizeof(version));
	if(!EXEC_NDK("NDK_Getlibver",NDK_Getlibver(version),NDK_OK,PINPAD_ENCORDEC)){
		goto ON_ERR;
	}
	int maxLen = CALCULATE_MAX_1024;
	if(strcmp(version,"NDK_V3.0.50") >= 0){
		maxLen = CALCULATE_MAX_1920;
	}
	LOGD_FMT(">>>maxLen[%d]",maxLen);
    int elementLen = 16;
    if(endeparam.algMode == DES){
        elementLen = 8;
    }
    int destLen = (endeparam.endeDataLen + elementLen - 1) / elementLen * elementLen;
    int count = destLen / maxLen,remainder = destLen % maxLen;
    if((count == 1 && remainder != 0) || (count > 1)){
        uchar srcData[destLen];//byte[] srcData = new byte[destLen];
        memset(srcData,0,sizeof(srcData));//Arrays.fill(srcData, (byte) 0x00);
        memcpy(srcData,inputData,endeparam.endeDataLen);//System.arraycopy(inputData,0,srcData,0,inputData.length);
        uchar destData[destLen];//byte[] destData = new byte[destLen];
        uchar srcItem[maxLen];//byte[] srcItem = new byte[maxLen];
        uchar outKsn[maxLen];int outKsnLen = 0;
        memset(outKsn,0, sizeof(outKsn));
        for (int i = 0; i < count; i++) {
            memcpy(srcItem,srcData+i*maxLen,maxLen);//System.arraycopy(srcData,i*maxLen,srcItem,0,maxLen);
            uchar outData[maxLen];int outDataLen = 0;
            memset(outData,0, sizeof(outData));
            if(Pinpad_Encrypt(keySys,alg,cipherMode,keyIndex,srcItem,maxLen,iv,ivLen,outData,&outDataLen,outKsn,&outKsnLen)!=NDK_OK){
                goto ON_ERR;
            }//CipherResult result = encrypt(keyManagement,algorithmMode,cipherMode,keyIndex,srcItem,params);
            memcpy(destData+i*maxLen,outData,maxLen);//System.arraycopy(result.getData(),0,destData,i*maxLen,maxLen);
        }
        if(remainder!=0){
            uchar srcRemData[remainder];//byte[] srcRemData = new byte[remainder];
            memcpy(srcRemData,srcData+count*maxLen,remainder);//System.arraycopy(srcData,count*maxLen,srcRemData,0,remainder);
            uchar outData[remainder];int outDataLen = 0;
            memset(outData,0, sizeof(outData));
            if(Pinpad_Encrypt(keySys,alg,cipherMode,keyIndex,srcRemData,remainder,iv,ivLen,outData,&outDataLen,outKsn,&outKsnLen)!=NDK_OK){
                goto ON_ERR;
            }//CipherResult result = encrypt(keyManagement,algorithmMode,cipherMode,keyIndex,srcRemData,params);
            memcpy(destData+count*maxLen,outData,remainder);//System.arraycopy(result.getData(),0,destData,count*maxLen,remainder);
        }
        *outputDataLen = destLen;
        memcpy(outputData,destData,destLen);
        *ksnLen = 10;
        memcpy(ksn,outKsn,10);
        return NDK_OK;
    }

    StEnOrDeFunParam funParam;
    memset(&funParam,0,sizeof(funParam));
    funParam.endeparam = &endeparam;

    if(endeparam.endeDataLen == 0 || endeparam.endeData == NULL){
        LOGD_FMT(">>>endeDataLen[%d] endeData[%d] error",endeparam.endeDataLen,endeparam.endeData);
        ERRMSG(SDK_ERR_PARAM,PINPAD_ENCORDEC);
        goto ON_ERR;
    }

    if(0 != endeparam.keyDataLen && 8 != endeparam.keyDataLen && 16 != endeparam.keyDataLen && 24 != endeparam.keyDataLen && 32 != endeparam.keyDataLen) {
        LOGD_FMT(">>>keyDataLen[%d] error",endeparam.keyDataLen);
        ERRMSG(SDK_ERR_PARAM,PINPAD_ENCORDEC);
        goto ON_ERR;
    }
    if(endeparam.keySys < MKSK || endeparam.keySys > DUKPT){
        LOGD_FMT(">>>keySys[%d] error",endeparam.keySys);
        ERRMSG(SDK_ERR_PARAM,PINPAD_ENCORDEC);
        goto ON_ERR;
    }

    if(endeparam.keyDataLen != 0 && (__EnOrDeLoadOutWk(&endeparam) != ACK_OK)){
        goto ON_ERR;
    }
    int ret = ACK_ERR;
    memcpy(funParam.ackCode, PIN_ACK_FAIL, 2);
    if(endeparam.keySys == MKSK){
        if(endeparam.algMode == DES){
            ret =  __EnOrDeMKSKDES(&funParam);
        }else if(endeparam.algMode == SM4){
            ret = __EnOrDeMKSKSM4(&funParam);
        }else if(endeparam.algMode == AES){
            ret = __EnOrDeMKSKAES(&funParam);
        }
    }else if(endeparam.keySys == DUKPT){
        if(endeparam.algMode == DES){
            ret = __EnOrDeDUKPTDES(&funParam);
        }else if(endeparam.algMode == SM4){
            LOGD_FMT(">>>DUKPT SM4 nonsupport");
            goto ON_ERR;
        }else if(endeparam.algMode == AES){
            LOGD_FMT("DUKPT AES nonsupport");
            goto ON_ERR;
        }
    }
    __EnOrDeEnd(&funParam);
    if(ret != ACK_OK){
        LOGD_FMT("EnDe Fail.");
        goto ON_ERR;
    }
    *outputDataLen = funParam.endeResultLen;
    memcpy(outputData,funParam.enderesult,funParam.endeResultLen);
    *ksnLen = 10;
    memcpy(ksn,funParam.ksn,10);
    return NDK_OK;
    ON_ERR:
    return NDK_ERR;

}

int Pinpad_EncOrDec(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen)
{
	StPinEnDeParam endeparam;
	memset(&endeparam,0,sizeof(endeparam));
	Pin_GetEnOrDeParam(&endeparam,pbuf,buf_len);
	StEnOrDeFunParam funParam;
	memset(&funParam,0,sizeof(funParam));
	funParam.endeparam = &endeparam;

	if(endeparam.endeDataLen == 0 || endeparam.endeData == NULL){
		LOGD_FMT(">>>endeDataLen[%d] endeData[%d] error",endeparam.endeDataLen,endeparam.endeData);
		ERRMSG(SDK_ERR_PARAM,PINPAD_ENCORDEC);
		goto ON_ERR;
	}

	if(0 != endeparam.keyDataLen && 8 != endeparam.keyDataLen && 16 != endeparam.keyDataLen && 24 != endeparam.keyDataLen && 32 != endeparam.keyDataLen) {
		LOGD_FMT(">>>keyDataLen[%d] error",endeparam.keyDataLen);
		ERRMSG(SDK_ERR_PARAM,PINPAD_ENCORDEC);
		goto ON_ERR;
	}
    if(endeparam.keySys < MKSK || endeparam.keySys > DUKPT){
        LOGD_FMT(">>>keySys[%d] error",endeparam.keySys);
		ERRMSG(SDK_ERR_PARAM,PINPAD_ENCORDEC);
        goto ON_ERR;
    }

	if(endeparam.keyDataLen != 0 && (__EnOrDeLoadOutWk(&endeparam) != ACK_OK)){
		goto ON_ERR;
	}
	int ret = ACK_ERR;
	memcpy(funParam.ackCode, PIN_ACK_FAIL, 2);
    if(endeparam.keySys == MKSK){
        if(endeparam.algMode == DES){
            ret =  __EnOrDeMKSKDES(&funParam);
        }else if(endeparam.algMode == SM4){
            ret = __EnOrDeMKSKSM4(&funParam);
        }else if(endeparam.algMode == AES){
            ret = __EnOrDeMKSKAES(&funParam);
        }
    }else if(endeparam.keySys == DUKPT){
        if(endeparam.algMode == DES){
            ret = __EnOrDeDUKPTDES(&funParam);
        }else if(endeparam.algMode == SM4){
            LOGD_FMT(">>>DUKPT SM4 nonsupport");
            goto ON_ERR;
        }else if(endeparam.algMode == AES){
            LOGD_FMT("DUKPT AES nonsupport");
            goto ON_ERR;
        }
    }
    __EnOrDeEnd(&funParam);
    if(ret != ACK_OK){
        LOGD_FMT("EnDe Fail.");
        goto ON_ERR;
    }
	int offset = 2;
	memcpy(pOut+offset,&funParam.mkeyIndex,1);offset += 1;
	memcpy(pOut+offset,funParam.ackCode,2);offset += 2;
	nlMpos_Command.mpos_writelen(pOut+offset,funParam.endeResultLen, _VAR_BIT16);offset += 2;
	memcpy(pOut+offset,funParam.enderesult,funParam.endeResultLen);offset += funParam.endeResultLen;
	memcpy(pOut+offset,funParam.ksn,10);offset += 10;
	responseCmd(pOut,offset-2,outLen,CMD_OK);
    return 0;
ON_ERR:
	responseCmd(pOut,0,outLen,CMD_ERR_OTHER);
    return 0;
}

#define MAC

static int Pin_GetDataMacParam(StPinMacParam* parm,unsigned char*pbuf,int iLen)
{
	if(parm == NULL){
		return ACK_ERR;
	}
	int offset = 0;
    parm->keySys = nlMpos_Command.mpos_getvar(pbuf+offset, _VAR_BIT8);offset++;
    parm->macAlgMode = nlMpos_Command.mpos_getvar(pbuf+offset, _VAR_BIT8);offset++;
	parm->keyIndex = nlMpos_Command.mpos_getvar(pbuf+offset, _VAR_BIT8);offset++;
	parm->blockFlag = nlMpos_Command.mpos_getvar(pbuf+offset, _VAR_BIT8); offset++;
	parm->macDataLen = nlMpos_Command.mpos_readlen(pbuf+offset, _VAR_BIT16); offset += 2; 
	parm->macData = pbuf+offset;offset += parm->macDataLen;
	parm->keyDataLen = nlMpos_Command.mpos_readlen(pbuf+offset, _VAR_BIT16); offset += 2; 
	parm->keyData = pbuf+offset;offset += parm->keyDataLen;
    parm->randomDataLen = nlMpos_Command.mpos_readlen(pbuf+offset, _VAR_BIT16);offset += 2;
    if(parm->randomDataLen == 2){
        parm->randomData = nlMpos_Command.mpos_readlen(pbuf+offset, _VAR_BIT16);offset += parm->randomDataLen;
    }
	LOGD_FMT(">>>keySys[%d] macAlgMode[%d] keyIndex[%d] blockFlag[%d] macDataLen[%d] keyDataLen[%d] randomDataLen[%d]",\
	parm->keySys,parm->macAlgMode,parm->keyIndex,parm->blockFlag,parm->macDataLen,parm->keyDataLen,parm->randomDataLen);
	return ACK_OK;
}

static int __getAlgorithmType(int macAlgorithm)
{
	LOGD_FMT(">>>macAlgorithm[%d]",macAlgorithm);
	int macMode = ACK_ERR;
	switch (macAlgorithm)
	{
		case MAC_ALG_X99:
			macMode = SEC_MAC_X99;
			break;
		case MAC_ALG_X919:
			macMode = SEC_MAC_X919;
			break;
		case MAC_ALG_ECB:
			macMode = SEC_MAC_ECB;
			break;
		case MAC_ALG_9606:
			macMode = SEC_MAC_9606;
			break;
		case MAC_ALG_CBC:
			macMode = SELF_SEC_MAC_CBC;
			break;
		case MAC_ALG_SM4:
			macMode = SEC_MAC_SM4;
			break;
		case MAC_ALG_SM4_UNIONPAY:
			macMode = SEC_MAC_SM4_UNIONPAY;
			break;
		case MAC_ALG_AES:
			macMode = SEC_MAC_AES;
			break;
		case MAC_ALG_SM4_9606:
			macMode = SEC_MAC_SM4_9606;
			break;
	}
	return macMode;
}
static int __MacMKSKBase(pStMacFunParam funParam)
{
	if(!EXEC_NDK("NDK_SecGetMac", NDK_SecGetMac(funParam->macparam->keyIndex,
		funParam->macparam->macData,funParam->macparam->macDataLen, funParam->resultMac, 
		funParam->macMode), NDK_OK,PINPAD_DATAMAC)){
		return ACK_ERR;
	}
	LOGD_FMT(">>>___MACBase SUCC");
	LOGD_STR("ResultMac",funParam->resultMac, 16);
	return ACK_OK;
}
static int __MacCBCBase(pStMacFunParam funParam){
    int keyManageType = funParam->macparam->keySys;
	LOGD_FMT("keyManageType[%d]",keyManageType);
	int i=0,j=0,elementLen = 8;
	int calcLen = (funParam->macparam->macDataLen + elementLen - 1) / elementLen * elementLen;
	uchar calcData[calcLen];
	memset(calcData,0,sizeof(calcData));
	memcpy(calcData,funParam->macparam->macData,funParam->macparam->macDataLen);

	uchar ivxor2[elementLen];
	memset(ivxor2,0, sizeof(ivxor2));
	uchar xor1[elementLen];

	uchar resultData[elementLen];
	memset(resultData,0, sizeof(resultData));

	for(i=0;i+elementLen <= calcLen;i+=elementLen){
		for(j=0;j<elementLen;j++){
			xor1[j] = calcData[i+j]^ivxor2[j];
		}
        if(keyManageType == MKSK){
            if(!EXEC_NDK("NDK_SecCalcDes", NDK_SecCalcDes(
                    SEC_KEY_TYPE_TAK|SEC_KEY_DES,
                    funParam->macparam->keyIndex,xor1,elementLen,resultData,
                    SEC_DES_KEYLEN_DEFAULT|SEC_DES_ENCRYPT|ALG_TDS_MODE_ENC), NDK_OK,PINPAD_DATAMAC)){
                return ACK_ERR;
            }
        }else if(keyManageType == DUKPT){
            if(!EXEC_NDK("NDK_SecCalcDesDukpt",
                           NDK_SecCalcDesDukpt(
                                   funParam->macparam->keyIndex,
                                   SEC_KEY_TYPE_TAK|SEC_KEY_DES, NULL,
                                   elementLen,xor1,resultData,
                                   funParam->ksn,
                                   SEC_DES_KEYLEN_DEFAULT|SEC_DES_ENCRYPT|ALG_TDS_MODE_ENC),NDK_OK,PINPAD_DATAMAC)){
                return ACK_ERR;
            }
        }else{
            return ACK_ERR;
        }
		memcpy(ivxor2, resultData, elementLen);
	}
	LOGD_FMT(">>>__MACCBC SUCC. keyManageType[%d]",keyManageType);
	int macLen = sizeof(funParam->resultMac);
	if(sizeof(resultData) < macLen){
		macLen = sizeof(resultData);
	}
	memcpy(funParam->resultMac,resultData,macLen);
	return ACK_OK;
}
static int __MacMKSKLoadSessionKey(pStMacFunParam funParam)
{
    int secKeyType;char *algMode=NULL;
    int macMode = __getAlgorithmType(funParam->macparam->macAlgMode);
	if(macMode == SEC_MAC_SM4 || macMode == SEC_MAC_SM4_UNIONPAY || macMode == SEC_MAC_SM4_9606){
		secKeyType = SEC_KEY_SM4;
        algMode = "SM4";
	}else if(macMode == SEC_MAC_AES){
		secKeyType = SEC_KEY_AES;
        algMode = "AES";
	}else{
		secKeyType = SEC_KEY_DES;
        algMode = "DES";
	}
	ST_SEC_KEY_INFO stKeyInfo;
	ST_SEC_KCV_INFO stKcvInfoIn;
    memset(&stKeyInfo,0, sizeof(ST_SEC_KEY_INFO));
    memset(&stKcvInfoIn,0,sizeof(ST_SEC_KCV_INFO));
	stKeyInfo.ucScrKeyIdx = funParam->macparam->keyIndex;
	stKeyInfo.ucScrKeyType = SEC_KEY_TYPE_TMK|secKeyType;
	stKeyInfo.ucDstKeyIdx =  SESSION_KEY_INDEX;
	stKeyInfo.ucDstKeyType = SEC_KEY_TYPE_TAK|secKeyType;
    if(macMode == SEC_MAC_AES){
        ST_EXTEND_KEYBLOCK stExtendKey;
        memset(&stExtendKey, 0, sizeof(ST_EXTEND_KEYBLOCK));
        stKeyInfo.nDstKeyLen = sizeof(ST_EXTEND_KEYBLOCK);
        stExtendKey.format = SEC_KEYBLOCK_FMT_AES;
        stExtendKey.len = funParam->macparam->keyDataLen;
        stExtendKey.pblock = funParam->macparam->keyData;
        memcpy(stKeyInfo.sDstKeyValue, &stExtendKey, sizeof(ST_EXTEND_KEYBLOCK));
    }else{
        stKeyInfo.nDstKeyLen = funParam->macparam->keyDataLen;
        memcpy(stKeyInfo.sDstKeyValue, funParam->macparam->keyData,stKeyInfo.nDstKeyLen);
    }
	stKcvInfoIn.nCheckMode=SEC_KCV_NONE;
	stKcvInfoIn.nLen= 0;

    char tag[128];
    memset(tag,0, sizeof(tag));
    sprintf(tag,"NDK_SecLoadKey %s",algMode);
	if(!EXEC_NDK(tag,NDK_SecLoadKey(&stKeyInfo, &stKcvInfoIn), NDK_OK,PINPAD_DATAMAC)){
		return ACK_ERR;
	}
	funParam->macparam->keyIndex = stKeyInfo.ucDstKeyIdx;
	LOGD_FMT(">>>__macMKSKLoadSessionKey %s succ wkIndex[%d]",algMode,funParam->macparam->keyIndex);
	return ACK_OK;
}
static int __MacMKSK(pStMacFunParam funParam)
{
	if(funParam->macparam->keyDataLen != 0 && __MacMKSKLoadSessionKey(funParam) != ACK_OK){
		LOGD_FMT(">>>__macMKSKLoadSessionKey failed");
		return ACK_ERR;
	}

	int macMode = __getAlgorithmType(funParam->macparam->macAlgMode);
	LOGD_FMT(">>>macMode[%d]",macMode);
	if(macMode == ACK_ERR){
		return ACK_ERR;
	}

	if(macMode == SELF_SEC_MAC_CBC){
		return __MacCBCBase(funParam);
	}else{
		funParam->macMode = macMode;
	}
	return __MacMKSKBase(funParam);
}
static int __MacDukptBase(pStMacFunParam funParam)
{
	if(!EXEC_NDK("NDK_SecGetMacDukpt",
				   NDK_SecGetMacDukpt(
						   funParam->macparam->keyIndex,
						   funParam->macparam->macData,
						   funParam->macparam->macDataLen,
						   funParam->resultMac,
						   funParam->ksn,funParam->macMode),
				   NDK_OK,PINPAD_DATAMAC)){
		return ACK_ERR;
	}
	LOGD_FMT(">>>__MacDukptBase SUCC");
	LOGD_STR("KSN",funParam->ksn, sizeof(funParam->ksn));
	LOGD_STR("ResultMac",funParam->resultMac, 16);
	return ACK_OK;
}
static int __MacDukpt(pStMacFunParam funParam)
{
	int macMode = __getAlgorithmType(funParam->macparam->macAlgMode);
	LOGD_FMT(">>>macMode[%d]",macMode);
	if(macMode == ACK_ERR){
		return ACK_ERR;
	}
	if(macMode ==  SELF_SEC_MAC_CBC){
        return  __MacCBCBase(funParam);
	} else{
		funParam->macMode = macMode;
	}
	return __MacDukptBase(funParam);
}

int Pinpad_DataMac(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen)
{
	StPinMacParam macparam;
	memset(&macparam,0,sizeof(macparam));
	Pin_GetDataMacParam(&macparam,pbuf,buf_len);
	StMacFunParam funParam;
	memset(&funParam,0,sizeof(funParam));
	funParam.macparam = &macparam;

	if(0 != macparam.keyDataLen && 8 != macparam.keyDataLen && 16 != macparam.keyDataLen && 24 != macparam.keyDataLen && 32 != macparam.keyDataLen){
		LOGD_FMT(">>keyDataLen[%d] error.",macparam.keyDataLen);
		ERRMSG(SDK_ERR_PARAM,PINPAD_DATAMAC);
		goto ON_ERR;
	}
    if(macparam.keySys < MKSK || macparam.keySys > DUKPT){
        LOGD_FMT(">>>keySys[%d] error",macparam.keySys);
		ERRMSG(SDK_ERR_PARAM,PINPAD_DATAMAC);
        goto ON_ERR;
    }
	if(macparam.macDataLen > 4096){

	}

    if(macparam.keySys == MKSK){
        if(__MacMKSK(&funParam)!=ACK_OK){
            goto ON_ERR;
        }
    }else if(macparam.keySys == DUKPT){
        if(__MacDukpt(&funParam)!=ACK_OK){
            goto ON_ERR;
        }
    }
	memcpy(funParam.ackCode,PIN_ACK_OK,2);
	int offset = 2;
	nlMpos_Command.mpos_setvar(pOut+offset, macparam.keyIndex, _VAR_BIT8);offset++;
	memcpy(pOut+offset, funParam.ackCode, 2);offset += 2;
	int macLen = 8;
	if(macparam.macAlgMode == MAC_ALG_SM4||macparam.macAlgMode == MAC_ALG_AES ||
	   macparam.macAlgMode == MAC_ALG_SM4_9606){
		macLen = 16;
	}
	nlMpos_Command.mpos_writelen(pOut+offset,macLen, _VAR_BIT16);offset += 2;
	memcpy(pOut+offset, funParam.resultMac, macLen);offset += macLen;//mac
	memcpy(pOut+offset, funParam.ksn, 10);offset += 10;
	responseCmd(pOut,offset-2,outLen,CMD_OK);
    return 0;
ON_ERR:
	responseCmd(pOut,0,outLen,CMD_ERR_OTHER);
	return 0;
}

#define PWDINPUT
static int __GetTimeDistance(struct timeval *startTime)
{
	struct timeval currTime;
	gettimeofday(&currTime,NULL);
	int disms = (currTime.tv_sec-startTime->tv_sec)*1000+(currTime.tv_usec-startTime->tv_usec)/1000;
	LOGD_FMT(">>>disms[%d]",disms);
	return disms;
}
static void Pin_SetVppInitFlag(int flag) {
	g_secVppTpInit = flag;
    LOGD_FMT(">>>g_secVppTpInit[%d]", g_secVppTpInit);
}
/*********************************************************************************************
函数原型：密码键盘类
功能描述：用于随机出密码键盘按键布局
输入参数：可变参数为布局中每个按键的左上角坐标和右下角坐标(从左到右，
					从上到下的顺序，每个坐标用4个字节表示，2字节横坐标，2字节纵坐标)。
输出参数：返回随机出来的键值布局(从左到右，从上到下的顺序)，其中取消键、退格键、确认键以及*键和#键
					是固定的。
返回值：  无
调用关系：
***********************************************************************************************/
int Pinpad_VppInit(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
	int i, j;
	int n1 = 0, n2 = 0;
	int DataLen;
	int keySeqLen;
	int numRamFlag = 0;
	int funcRamFlag = 0;
	int uKey;
	uchar mode = 0;
	int x1, y1, x2, y2;
	uchar mRandom;
	int offset, extoffset;
	int numBtnOffset = 0;
	int funcKeyOffset = 0;
	uchar *data;
	uchar *keySeq;
	uchar *pOutSeq;
	uchar numBtn[80] = {0};
	uchar funcKey[36] = {0};
	uchar outSeq[10] = {0};
	uchar randombuf[15];
	int ucKey[5] = {K_ESC, K_BASP, K_ENTER, K_DOT, K_ZMK};

	memset(randombuf, 0, sizeof(randombuf));

	LOGE_FMT(">>>NDK_SecVppTpInit");
	offset = 0;
	extoffset = 2;
	DataLen = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);offset += 2;
	data = pbuf + offset;offset += DataLen;
	mode = pbuf[offset];offset++;
	keySeqLen = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);offset += 2;
	keySeq = pbuf + offset;
	LOGD_FMT(">>>coordinates[%d] mode[%d] keySeqLen[%d]",DataLen,mode,keySeqLen);
	LOGD_STR("keySeq",keySeq,keySeqLen);
	if (mode > 0x02) {
        LOGE_FMT("mode err[%d]",mode);
		ERRMSG(SDK_ERR_PARAM,PINPAD_VPPINIT);
		goto ON_ACK;
	}
    //mode=2:数字功能键不随机(PinKeySeq=0);都随机(PinKeySeq=2);    mode=0:数字随机,功能不随机(PinKeySeq=1);
	if (mode == 0x00 || mode == 0x01) {
		offset = 0;
		for (i = 0; i < 15; i++) {
			x1 = nlMpos_Command.mpos_getvar(data + offset, _VAR_BIT16);
			x1 = nlMpos_Command.mpos_endian_swab16(x1);
			offset += 2;

			y1 = nlMpos_Command.mpos_getvar(data + offset, _VAR_BIT16);
			y1 = nlMpos_Command.mpos_endian_swab16(y1);
			offset += 2;

			x2 = nlMpos_Command.mpos_getvar(data + offset, _VAR_BIT16);
			x2 = nlMpos_Command.mpos_endian_swab16(x2);
			offset += 2;

			y2 = nlMpos_Command.mpos_getvar(data + offset, _VAR_BIT16);
			y2 = nlMpos_Command.mpos_endian_swab16(y2);
			offset += 2;

			if (i == 11 || i == 13) {
				continue;
			} else if (i == 3 || i == 7 || i == 14) {
				memcpy(&funcKey[funcKeyOffset], &ucKey[funcKeyOffset / 12], 4);
				funcKeyOffset += 4;
				memcpy(&funcKey[funcKeyOffset], &x1, 2);
				funcKeyOffset += 2;
				memcpy(&funcKey[funcKeyOffset], &y1, 2);
				funcKeyOffset += 2;
				memcpy(&funcKey[funcKeyOffset], &x2, 2);
				funcKeyOffset += 2;
				memcpy(&funcKey[funcKeyOffset], &y2, 2);
				funcKeyOffset += 2;
			} else {
				if (mode == 1)
					numBtnOffset = (keySeq[n2++] - '0') * 8;

				memcpy(&numBtn[numBtnOffset], &x1, 2);
				numBtnOffset += 2;
				memcpy(&numBtn[numBtnOffset], &y1, 2);
				numBtnOffset += 2;
				memcpy(&numBtn[numBtnOffset], &x2, 2);
				numBtnOffset += 2;
				memcpy(&numBtn[numBtnOffset], &y2, 2);
				numBtnOffset += 2;
			}
		}
		LOGD_STR("numKey",numBtn,80);
		LOGD_STR("funcKey",funcKey,36);
		if (mode == 0)
			pOutSeq = outSeq;
		else
			pOutSeq = NULL;

        if(!EXEC_NDK("NDK_SecVppTpInit",NDK_SecVppTpInit(numBtn, funcKey, pOutSeq),NDK_OK,PINPAD_VPPINIT)){
            LOGE_FMT(">>>NDK_SecVppTpInit fail");
            goto ON_ACK;
        }else{
            LOGE_FMT(">>>NDK_SecVppTpInit succ");
        }

		if (mode == 0) {
            LOGD_STR("pOutSeq",pOutSeq,10);
		}

		n2 = 0;
		memset(randombuf, 0, sizeof(randombuf));
		for (i = 0; i < 15; i++) {
			if (i == 11 || i == 13) {
				if (i == 11) {
					randombuf[i] = K_DOT;
				}
				if (i == 13) {
					randombuf[i] = K_ZMK;
				}
			} else if (i == 3 || i == 7 || i == 14) {
				randombuf[i] = ucKey[n1++];
			} else {
				if (mode == 0)
					randombuf[i] = pOutSeq[n2++];
				if (mode == 1)
					randombuf[i] = keySeq[n2++];
			}
		}
	} else if (mode == 2) {
		if (DataLen != keySeqLen * 8) {
			LOGE_FMT("param err.");
			ERRMSG(SDK_ERR_PARAM,PINPAD_VPPINIT);
			goto ON_ACK;
		}
		g_keyClear = 0;
		//统计需要随机的数字键和功能键的个数
		for (i = 0; i < keySeqLen; i++) {
			if (keySeq[i] == 0x7E)
				numRamFlag++;
			if (keySeq[i] == 0x7F)
				funcRamFlag++;
			if (keySeq[i] == 0x9c)
				g_keyClear = 1;
		}

		//当需要随机的数字键的个数不为10的时候，则在这边随机出需要随机的那几个键的键值
		if (numRamFlag >= 0 && numRamFlag < 10) {
			for (i = 0; i < numRamFlag; i++) {
				while (1) {
					EXEC_NDK("NDK_SecGetRandom",NDK_SecGetRandom(1, &mRandom),NDK_OK,PINPAD_VPPINIT);
					mRandom %= 10;
//					Udebug.DEBUG_Levelone("mRandom[%d]", mRandom);
					mRandom += '0';
					for (j = 0; j < keySeqLen; j++) {
						if (mRandom == keySeq[j])
							break;
					}
//					Udebug.DEBUG_Levelone("j[%d]", j);
					if (j == keySeqLen) {
						for (n2 = 0; n2 < keySeqLen; n2++) {
							if (keySeq[n2] == 0x7E) {
								keySeq[n2] = mRandom;
								break;
							}
						}
						break;
					}
				}
			}
		}

		//当有功能键需要随机时，在这里随机出功能键的位置
		if (funcRamFlag) {
			for (i = 0; i < funcRamFlag; i++) {
				while (1) {
					EXEC_NDK("NDK_SecGetRandom",NDK_SecGetRandom(1, &mRandom),NDK_OK,PINPAD_VPPINIT);

					mRandom %= keySeqLen - 10;
//					Udebug.DEBUG_Levelone("mRandom[%d]", mRandom);

					mRandom = ucKey[mRandom];
//					Udebug.DEBUG_Levelone("randomFunckey[%02x]", mRandom);
					for (j = 0; j < keySeqLen; j++) {
						if (mRandom == keySeq[j])
							break;
					}
//					Udebug.DEBUG_Levelone("j[%d]",j);
					if (j == keySeqLen) {
						for (n2 = 0; n2 < keySeqLen; n2++) {
							if (keySeq[n2] == 0x7F) {
								keySeq[n2] = mRandom;
								break;
							}
						}
						break;
					}
				}
			}
		}

		LOGD_STR("data",data,DataLen);

		n2 = 0;
		offset = 0;
		for (i = 0; i < keySeqLen; i++) {
			x1 = nlMpos_Command.mpos_getvar(data + offset, _VAR_BIT16);
			x1 = nlMpos_Command.mpos_endian_swab16(x1);
			offset += 2;

			y1 = nlMpos_Command.mpos_getvar(data + offset, _VAR_BIT16);
			y1 = nlMpos_Command.mpos_endian_swab16(y1);
			offset += 2;

			x2 = nlMpos_Command.mpos_getvar(data + offset, _VAR_BIT16);
			x2 = nlMpos_Command.mpos_endian_swab16(x2);
			offset += 2;

			y2 = nlMpos_Command.mpos_getvar(data + offset, _VAR_BIT16);
			y2 = nlMpos_Command.mpos_endian_swab16(y2);
			offset += 2;

			if (numRamFlag >= 0 && numRamFlag < 10) {
				if (keySeq[n2] >= '0' && keySeq[n2] <= '9') {
					numBtnOffset = (keySeq[n2] - '0') * 8;

					memcpy(&numBtn[numBtnOffset], &x1, 2);
					numBtnOffset += 2;
					memcpy(&numBtn[numBtnOffset], &y1, 2);
					numBtnOffset += 2;
					memcpy(&numBtn[numBtnOffset], &x2, 2);
					numBtnOffset += 2;
					memcpy(&numBtn[numBtnOffset], &y2, 2);
					numBtnOffset += 2;
				}
			} else if (numRamFlag == 10) {
				if (keySeq[n2] == 0x7E) {
					memcpy(&numBtn[numBtnOffset], &x1, 2);
					numBtnOffset += 2;
					memcpy(&numBtn[numBtnOffset], &y1, 2);
					numBtnOffset += 2;
					memcpy(&numBtn[numBtnOffset], &x2, 2);
					numBtnOffset += 2;
					memcpy(&numBtn[numBtnOffset], &y2, 2);
					numBtnOffset += 2;
				}
			}

			if (keySeq[n2] == 0x1B || keySeq[n2] == 0x0A || keySeq[n2] == 0x0D ||
				keySeq[n2] == K_QUIT || keySeq[n2] == K_CLEAR) {
				uKey = keySeq[n2];
				memcpy(&funcKey[funcKeyOffset], &uKey, 4);
				funcKeyOffset += 4;
				memcpy(&funcKey[funcKeyOffset], &x1, 2);
				funcKeyOffset += 2;
				memcpy(&funcKey[funcKeyOffset], &y1, 2);
				funcKeyOffset += 2;
				memcpy(&funcKey[funcKeyOffset], &x2, 2);
				funcKeyOffset += 2;
				memcpy(&funcKey[funcKeyOffset], &y2, 2);
				funcKeyOffset += 2;
			}

			n2++;
		}

		LOGD_STR("numKey",numBtn,80);
		LOGD_STR("funcKey",funcKey,36);

		if (numRamFlag == 10)
			pOutSeq = outSeq;
		else
			pOutSeq = NULL;

		LOGD_FMT(">>>pOutSeq[%d]",pOutSeq);

        if(!EXEC_NDK("NDK_SecVppTpInit",NDK_SecVppTpInit(numBtn, funcKey, pOutSeq),NDK_OK,PINPAD_VPPINIT)){
			LOGE_FMT(">>>NDK_SecVppTpInit fail");
            goto ON_ACK;
        } else{
            LOGE_FMT(">>>NDK_SecVppTpInit succ");
        }

		if (numRamFlag == 10) {
			LOGD_STR("pOutSeq",pOutSeq,10);
		}

		n2 = 0;
		for (i = 0; i < keySeqLen; i++) {
			if (keySeq[i] >= '0' && keySeq[i] <= '9')
				randombuf[i] = keySeq[i];

			if (keySeq[i] == 0x7E)
				randombuf[i] = pOutSeq[n2++];

			if (keySeq[i] == 0x1B || keySeq[i] == 0x0A || keySeq[i] == 0x0D || keySeq[i] == 0x2E || keySeq[i] == 0x1C || keySeq[i] == 0x9C || keySeq[i] == 0x9B) {
				randombuf[i] = keySeq[i];
			}
		}
	}
    LOGD_STR(">>>Keyboard Layout",randombuf,15);

	offset = 0;
	nlMpos_Command.mpos_writelen(pOut + extoffset + offset, 15, _VAR_BIT16);
	offset += 2;
	memcpy(pOut + extoffset + offset, randombuf, 15);
	offset += 15;
	Pin_SetVppInitFlag(1);
	responseCmd(pOut, offset, outLen,CMD_OK);
	return 0;

	ON_ACK:
	Pin_SetVppInitFlag(0);
	responseCmd(pOut, 0, outLen,CMD_ERR_OTHER);
	return 0;
}

static int Pin_GetPwdInputParam(StPinPwdInputParam* parm,unsigned char*pbuf,int iLen)
{
	if(parm == NULL){
		return ACK_ERR;
	}
	int offset = 0;
    parm->keySys = pbuf[offset++];
    parm->algMode = pbuf[offset++];
    parm->keyIndex = pbuf[offset++];
	parm->acctInputType = pbuf[offset++];//account mode/no account mode/account hash mode/PINBLOCK/...etc.
	memcpy(parm->account,pbuf + offset,sizeof(parm->account));offset += sizeof(parm->account);//account/account hash/0
	parm->wkeyDataLen = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16); offset += 2;
	parm->wkeyData = pbuf + offset; offset += parm->wkeyDataLen;
	parm->inputMaxLen = pbuf[offset++];
	parm->pinFunKeyType = pbuf[offset++];
	parm->timeOuts = pbuf[offset++];
	int rangelen = parm->pwdInputRangeLen = nlMpos_Command.mpos_readlen(pbuf+offset,_VAR_BIT16);offset += 2;
	memset(parm->pwdInputRange,0,sizeof(parm->pwdInputRange));
	if(rangelen >0 && rangelen <= 20){
		memcpy(parm->pwdInputRange,pbuf+offset,rangelen);offset += rangelen;
	}
	parm->pinblockMode = pbuf[offset++];
	parm->modulusLen  = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);offset += 2;
	if(parm->modulusLen != 0){
		parm->modulus = pbuf+offset;
		offset += parm->modulusLen;
	}
	parm->exponentLen = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);offset += 2;
	if(parm->exponentLen != 0){
		parm->exponent = pbuf+offset;
		offset += parm->exponentLen;
	}
    parm->pinblockAlgMode = pbuf[offset++];
	parm->pinEventMode = pbuf[offset++];
	parm->dukptDerivateUsage = pbuf[offset++];
	parm->derivateKeyLen = pbuf[offset++];
	parm->isRNIB = pbuf[offset++];

	LOGD_FMT(">>>keySys[%d] algMode[%d] keyIndex[%d] acctInputType[%d] wkeyDataLen[%d] inputMaxLen[%d] pinFunKeyType[%d] timeOuts[%d] pwdInputRangeLen[%d] pinblockMode[%d] modulusLen[%d] exponentLen[%d] pinblockAlgMode[%d] parm->pinEventMode[%d] dukptDerivateUsage[%d] derivateKeyLen[%d] isRNIB[%d]",
			 parm->keySys,parm->algMode,parm->keyIndex,parm->acctInputType,parm->wkeyDataLen,parm->inputMaxLen,parm->pinFunKeyType,parm->timeOuts, parm->pwdInputRangeLen,parm->pinblockMode,parm->modulusLen,parm->exponentLen,parm->pinblockAlgMode,parm->pinEventMode,parm->dukptDerivateUsage,parm->derivateKeyLen,parm->isRNIB);
	//LOGD_STR("account", parm->account, sizeof(parm->account));
	LOGD_STR("pwdInputRange", parm->pwdInputRange, sizeof(parm->pwdInputRange));
	LOGD_STR("modulus", parm->modulus, parm->modulusLen);
	LOGD_STR("exponent", parm->exponent,parm->exponentLen);
    if(parm->pinblockMode == PINBLOCKMODE_PLAIN && parm->acctInputType == USE_ACCOUNT){
		LOGE_FMT(">>>pinblockMode acctInputType err.");
        return ACK_ERR;
    }
	return ACK_OK;
}

static int __getCryptoAlg(AlgorithmMode algMode){
	if(algMode == DES){
		return KEY_TYPE_DES;
	}else if(algMode == SM4){
		return KEY_TYPE_SM4;
	}else if(algMode == AES){
		return KEY_TYPE_AES;
	}
	LOGD_FMT(">>>__getCryptoAlg==-1");
	return -1;
}

static int __inputLoadOutWk(StPinPwdInputParam *param){
	LOGD_FMT("");
	if(param->keySys == DUKPT){
		ERRMSG(SDK_ERR_PARAM,PINPAD_INPUT);
		return ACK_ERR;
	}
    LOGD_FMT("algMode[%d]",param->algMode);
	int tmkType,tdkType;
	char *algMode = NULL;
	if(param->algMode == DES){
		tmkType = SEC_KEY_TYPE_TMK|SEC_KEY_DES;
		tdkType = SEC_KEY_TYPE_TPK|SEC_KEY_DES;
		algMode = "DES";
	}else if(param->algMode == SM4){
		tmkType = SEC_KEY_TYPE_TMK|SEC_KEY_SM4;
		tdkType = SEC_KEY_TYPE_TPK|SEC_KEY_SM4;
		algMode = "SM4";
	}else if(param->algMode == AES){
		tmkType = SEC_KEY_TYPE_TMK|SEC_KEY_AES;
		tdkType = SEC_KEY_TYPE_TPK|SEC_KEY_AES;
		algMode = "AES";
	} else{
		LOGE_FMT("");
		ERRMSG(SDK_ERR_PARAM,PINPAD_INPUT);
		return ACK_ERR;
	}
	ST_SEC_KEY_INFO stKeyInfoIn;
	ST_SEC_KCV_INFO stKcvInfoIn;
	memset(&stKcvInfoIn, 0, sizeof(stKcvInfoIn));
	memset(&stKeyInfoIn, 0, sizeof(stKeyInfoIn));
	stKeyInfoIn.ucScrKeyIdx = param->keyIndex;
	stKeyInfoIn.ucScrKeyType = tmkType;
	stKeyInfoIn.ucDstKeyIdx = SESSION_KEY_INDEX;
	stKeyInfoIn.ucDstKeyType = tdkType;
	if(param->algMode == AES){
		ST_EXTEND_KEYBLOCK stExtendKey;
		memset(&stExtendKey, 0, sizeof(ST_EXTEND_KEYBLOCK));
		stKeyInfoIn.nDstKeyLen = sizeof(ST_EXTEND_KEYBLOCK);
		stExtendKey.format = SEC_KEYBLOCK_FMT_AES;
		stExtendKey.len = param->wkeyDataLen;
		stExtendKey.pblock = param->wkeyData;
		memcpy(stKeyInfoIn.sDstKeyValue, &stExtendKey, sizeof(ST_EXTEND_KEYBLOCK));
	}else{
		stKeyInfoIn.nDstKeyLen = param->wkeyDataLen;
        memcpy(stKeyInfoIn.sDstKeyValue, param->wkeyData, stKeyInfoIn.nDstKeyLen);
	}
	stKcvInfoIn.nCheckMode = SEC_KCV_NONE;
	stKcvInfoIn.nLen= 0;

	char tag[128];
	memset(tag,0, sizeof(tag));
	sprintf(tag,"NDK_SecLoadKey %s",algMode);
	if(!EXEC_NDK(tag,NDK_SecLoadKey(&stKeyInfoIn, &stKcvInfoIn), NDK_OK,PINPAD_INPUT)){
		return ACK_ERR;
	}
	param->keyIndex = stKeyInfoIn.ucDstKeyIdx;
	LOGD_FMT(">>>NDK_SecLoadKey %s succ wkIndex[%d]",algMode,param->keyIndex);
	return ACK_OK;
}

/**
 *
 * @param panTrack2In  刷卡获取的卡号
 * @param panSha1      传入的卡号SHA1
 * @param panTrack2Out if(true),赋值为刷卡获取的卡号
 * @return
 */
static int __checkPanSha1(unsigned char *panTrack2In,unsigned char *panSha1,unsigned char *panTrack2Out)
{
	if(panTrack2In == NULL || panSha1 == NULL || panTrack2Out == NULL){
		LOGD_FMT(">>>param err.");
		return ACK_ERR;
	}
	unsigned char pansha1[32];
	memset(pansha1,0, sizeof(pansha1));
	LOGD_STR("panTrack2In",panTrack2In,32);
	if(!EXEC_NDK("NDK_AlgSHA1",NDK_AlgSHA1(panTrack2In, strlen(panTrack2In), pansha1), NDK_OK,PINPAD_INPUT)){
		return ACK_ERR;
	}
	LOGD_STR("pansha1",pansha1, sizeof(pansha1));
	if (0 != memcmp(panSha1, pansha1, 20))
		return ACK_ERR;
	memcpy((char *)panTrack2Out, panTrack2In, 20);
	LOGD_STR("panTrack2Out",panTrack2Out,20);
	LOGD_FMT(">>>__checkPanSha1 exec succ.");
	return ACK_OK;
}
static int __pwdInputParamPre(StPwdFunParam *funParam)
{
	int keyLen = funParam->pwdparam->wkeyDataLen;
	if(0 != keyLen && 8 != keyLen && 16 != keyLen && 24 != keyLen && 32 != keyLen){
		LOGD_FMT(">>keyDataLen[%d] error.",keyLen);
		ERRMSG(SDK_ERR_PARAM,PINPAD_INPUT);
		return ACK_ERR;
	}

    if (funParam->pwdparam->timeOuts > 200) {//NDK_SecGetPin()接口超时时常最大为200s
        funParam->pwdparam->timeOuts = 200;
    }

    memset(&funParam->rsaKey,0, sizeof(funParam->rsaKey));
	if(funParam->pwdparam->modulusLen != 0){
		memcpy(funParam->rsaKey.sModulus,funParam->pwdparam->modulus,funParam->pwdparam->modulusLen);
	}
	if(funParam->pwdparam->exponentLen != 0){
		memcpy(funParam->rsaKey.sExponent,funParam->pwdparam->exponent,funParam->pwdparam->exponentLen);
	}
    funParam->rsaKey.usBits = funParam->pwdparam->modulusLen * 8;

    int i = 0,n = 0;
    for (i = 0; i < funParam->pwdparam->pwdInputRangeLen; i++) {
        if (funParam->pwdparam->pwdInputRange[i] <= funParam->pwdparam->inputMaxLen ) {
            funParam->pwdLenRange[i] = funParam->pwdparam->pwdInputRange[i];
            funParam->pwdLenRangeCount++;
        }
    }
    if(funParam->pwdLenRangeCount == 0){
        for(i=0,n=0;i<=funParam->pwdparam->inputMaxLen;i++){
            sprintf(&funParam->apipwdLenRangIn[n], "%d%c", i, ',');
            if (i < 10) n += 2; else n += 3;
        }
        funParam->apipwdLenRangIn[n-1]='\0';
    }else{
        for(i=0,n=0;i<funParam->pwdLenRangeCount;i++){
            if(funParam->pwdLenRange[i] <= funParam->pwdparam->inputMaxLen){
                sprintf(&funParam->apipwdLenRangIn[n], "%d%c",funParam->pwdLenRange[i], ',');
                if (funParam->pwdLenRange[i] < 10) n += 2; else n += 3;
            }
        }
        funParam->apipwdLenRangIn[n-1]='\0';
    }


	//关于卡号处理的就这两个地方
    if(funParam->pwdparam->acctInputType == USE_ACCT_HASH){
		if(__checkPanSha1(getTrackPan(),funParam->pwdparam->account,funParam->pwdparam->account)!=ACK_OK){
			LOGD_FMT(">>>__checkPanSha1 err.");
			return ACK_ERR;
		}
	}
    for (i = sizeof(funParam->pwdparam->account); i >= 0; i--) {
        if (funParam->pwdparam->account[i] == 'F' || funParam->pwdparam->account[i] == 'f'|| funParam->pwdparam->account[i] == 0xFF)
            funParam->pwdparam->account[i] = 0;
    }

    uchar panTempF[41],panTemp0[41];
    memset(panTempF, 'F', sizeof(panTempF));
    memset(panTemp0, '0', sizeof(panTemp0));
    int panAllF = memcmp(funParam->pwdparam->account, panTempF, sizeof(funParam->pwdparam->account));
    int panAll0 = memcmp(funParam->pwdparam->account, panTemp0, sizeof(funParam->pwdparam->account));

	int keySys = funParam->pwdparam->keySys;
	int algMode = funParam->pwdparam->algMode;
	int acctInputType = funParam->pwdparam->acctInputType;
    LOGD_FMT(">>>keySys[%d] algMode[%d] acctInputType[%d]",keySys,algMode,acctInputType);


    if(keySys == MKSK){
		if(algMode == DES){
			if(acctInputType == UNUSE_ACCOUNT||(panAllF==0||panAll0==0)){
				funParam->secPinMode = SEC_PIN_ISO9564_2;
			} else if(acctInputType == THREE_DIMENSIONS){
				funParam->secPinMode = SEC_PIN_ISO9564_9;
			} else{
				funParam->secPinMode = SEC_PIN_ISO9564_0;
			}
		}else if(algMode == SM4){
			if (acctInputType == USE_ACCOUNT || acctInputType == USE_ACCT_HASH) {
				funParam->secPinMode = SEC_PIN_SM4_2;
			} else if (acctInputType == UNUSE_ACCOUNT) {
				funParam->secPinMode = SEC_PIN_SM4_1;
			} else {
				LOGD_FMT(">>>MKSK/SM4 acctInputType error.");
				return ACK_ERR;
			}
		}else if(algMode == AES){
			if(acctInputType == USE_ACCOUNT){
				funParam->secPinMode = SEC_PIN_AES_FMT4;
			}else{
				LOGD_FMT(">>>MKSK/AES acctInputType error.");
				return ACK_ERR;
			}
		}
	}else if(keySys == DUKPT){
		if(algMode == DES){
			if(acctInputType == UNUSE_ACCOUNT||(panAllF==0||panAll0==0)){
				funParam->secPinMode = SEC_PIN_ISO9564_2;
			} else if(acctInputType == THREE_DIMENSIONS){
				funParam->secPinMode = SEC_PIN_ISO9564_9;
			} else{
				funParam->secPinMode = SEC_PIN_ISO9564_0;
			}
			if(!EXEC_NDK("NDK_SecGetDukptKsn",NDK_SecGetDukptKsn(funParam->pwdparam->keyIndex, funParam->apiPinKsn),NDK_OK,PINPAD_INPUT)) return ACK_ERR;
		}else if(algMode == SM4){
			LOGD_FMT(">>>DUKPT SM4 nonsupport");
			return ACK_ERR;
		}else if(algMode == AES){
			LOGD_FMT(">>>DUKPT AES pinblockAlgMode[%d]",funParam->pwdparam->pinblockAlgMode);
			//return ACK_ERR;
		}
	}else{
		LOGD_FMT("keySys Error.");
		return ACK_ERR;
	}
    if(funParam->pwdparam->pinblockAlgMode != 0xFF){
		funParam->secPinMode = funParam->pwdparam->pinblockAlgMode;
	}
	LOGE_FMT("timeOuts[%d] apipwdLenRangIn[%s] secPinMode[%d]",funParam->pwdparam->timeOuts,funParam->apipwdLenRangIn,funParam->secPinMode);
	//LOGD_STR("account",funParam->pwdparam->account, sizeof(funParam->pwdparam->account));
    return ACK_OK;
}

static int __pwdInputSecGetPinPre(StPwdFunParam *funParam)
{
	int isRNIB = funParam->pwdparam->isRNIB;
    if(funParam->pwdparam->keySys == DUKPT){
		if(getUseNapi() || isRNIB ){
			ST_SEC_DUKPT_DERIVATE_DATA secDukptDerivateData;
			memset(&secDukptDerivateData,0x00, sizeof(ST_SEC_DUKPT_DERIVATE_DATA));
			secDukptDerivateData.KeyType = KEY_TYPE_AES;
			secDukptDerivateData.DerivateUsage = funParam->pwdparam->dukptDerivateUsage;
			secDukptDerivateData.nKeyLen = funParam->pwdparam->derivateKeyLen;
			int adSize = 0;
			void *pAD = NULL;
			if(funParam->pwdparam->algMode == AES ){
				adSize = sizeof(ST_SEC_DUKPT_DERIVATE_DATA);
				pAD = &secDukptDerivateData;
			}
			LOGD_FMT("adSize[%d] pAD[%d]",adSize,pAD);
			if(!EXEC_NDK("NAPI_SecVPPInit", NAPI_SecVPPInit(SEC_VPP_DUKPT,
				__getCryptoAlg(funParam->pwdparam->algMode),funParam->pwdparam->keyIndex,
				funParam->pwdparam->account,funParam->secPinMode,
				(uint) funParam->pwdparam->timeOuts,NULL,pAD,adSize),NDK_OK,PINPAD_INPUT)){
				return ACK_ERR;
			}
			LOGD_FMT(">>>NAPI_SecVPPInit(SEC_VPP_DUKPT) exec succ.");
		} else{
			if(!EXEC_NDK("NDK_SecGetPinDukpt", NDK_SecGetPinDukpt(funParam->pwdparam->keyIndex, funParam->apipwdLenRangIn, funParam->pwdparam->account, funParam->apiPinKsn, NULL, funParam->secPinMode, funParam->pwdparam->timeOuts*1000),NDK_OK,PINPAD_INPUT)){
				return ACK_ERR;
			}
			LOGD_FMT(">>>NDK_SecGetPinDukpt exec succ.");
		}
    }else{
        if (funParam->pwdparam->pinblockMode == PINBLOCKMODE_OFFLINE) {
            if (funParam->pwdparam->modulusLen == 0 && funParam->pwdparam->exponentLen == 0){//脱机明文
               	if(getUseNapi() || isRNIB){
					if(!EXEC_NDK("NAPI_SecVPPInit", NAPI_SecVPPInit(SEC_VPP_EMV_OFFLINE_CLEARPIN,
					-1,-1,NULL,-1,(uint) funParam->pwdparam->timeOuts,NULL,NULL,0),NDK_OK,PINPAD_INPUT)){
						return ACK_ERR;
					}
					LOGD_FMT(">>>NAPI_SecVPPInit(SEC_VPP_EMV_OFFLINE_CLEARPIN) exec succ.");
				} else{
					if(!EXEC_NDK("NDK_SecVerifyPlainPin", NDK_SecVerifyPlainPin(0, funParam->apipwdLenRangIn, NULL, 0, (uint) funParam->pwdparam->timeOuts*1000),NDK_OK,PINPAD_INPUT)){
						return ACK_ERR;
					}
					LOGD_FMT(">>>NDK_SecVerifyPlainPin exec succ.");
				}
            } else {
				//00 00 03 | 01 00 01
				if(getUseNapi() || isRNIB ){
					ST_NAPI_RSA_KEY stNapiRsaKey;
					memset(&stNapiRsaKey,0,sizeof(stNapiRsaKey));
					stNapiRsaKey.usBits = funParam->rsaKey.usBits;
					memcpy(&stNapiRsaKey.sModulus,&funParam->rsaKey.sModulus,sizeof(stNapiRsaKey.sModulus));
					memcpy(&stNapiRsaKey.sExponent,&funParam->rsaKey.sExponent,sizeof(stNapiRsaKey.sExponent));
					LOGD_FMT("usBits[%d]",stNapiRsaKey.usBits);
					LOGD_STR("sModulus",stNapiRsaKey.sModulus,sizeof(stNapiRsaKey.sModulus));
					LOGD_STR("sExponent",stNapiRsaKey.sExponent,sizeof(stNapiRsaKey.sExponent));
					if(!EXEC_NDK("NAPI_SecVPPInit", NAPI_SecVPPInit(SEC_VPP_EMV_OFFLINE_ENCPIN,
						-1,-1,NULL,-1,(uint) funParam->pwdparam->timeOuts,&stNapiRsaKey,NULL,0),NDK_OK,PINPAD_INPUT)){
						return ACK_ERR;
					}
					LOGD_FMT(">>>NAPI_SecVPPInit(SEC_VPP_EMV_OFFLINE_ENCPIN) exec succ.");
				} else{
					if(!EXEC_NDK("NDK_SecVerifyCipherPin", NDK_SecVerifyCipherPin(0, funParam->apipwdLenRangIn, &funParam->rsaKey, NULL, 0, funParam->pwdparam->timeOuts*1000),NDK_OK,PINPAD_INPUT)){
						return ACK_ERR;
					}
					LOGD_FMT(">>>NDK_SecVerifyCipherPin exec succ.");
				}

            }
        } else {
			if(getUseNapi() || isRNIB ){
				if(!EXEC_NDK("NAPI_SecVPPInit", NAPI_SecVPPInit(SEC_VPP_MASTER_SESSION,
					__getCryptoAlg(funParam->pwdparam->algMode),funParam->pwdparam->keyIndex,
					funParam->pwdparam->account,funParam->secPinMode,
					(uint) funParam->pwdparam->timeOuts,NULL,NULL,0),NDK_OK,PINPAD_INPUT)){
					return ACK_ERR;
				}
				LOGD_FMT(">>>NAPI_SecVPPInit(SEC_VPP_MASTER_SESSION) exec succ.");
			} else{
				if(!EXEC_NDK("NDK_SecGetPin", NDK_SecGetPin(funParam->pwdparam->keyIndex, funParam->apipwdLenRangIn, funParam->pwdparam->account, NULL, funParam->secPinMode, funParam->pwdparam->timeOuts*1000),NDK_OK,PINPAD_INPUT)){
					return ACK_ERR;
				}
				LOGD_FMT(">>>NDK_SecGetPin exec succ.");
			}
        }
    }
	if(getUseNapi() || isRNIB ){
		if(!EXEC_NDK("NAPI_SecVPPSetExpPinLenIn", NAPI_SecVPPSetExpPinLenIn(funParam->apipwdLenRangIn),NDK_OK,PINPAD_INPUT)){
			return ACK_ERR;
		}
	}
	LOGD_FMT(">>>__pwdInputSecGetPinPre exec succ.");
    return ACK_OK;
}


static int __notifyPinEvent(EM_SYS_EVENT eventNum, int msgLen, char * msg)
{
	THREAD_MUTEX_LOCK(THREAD_MUTEX_INDEX_PINEVENTSYNC);
	LOGD_FMT(">>>EM_SYS_EVENT[0x%x]",eventNum);
	ST_THREAD_COND_MSG condMsg;
	if(eventNum==SYS_EVENT_PIN){
		condMsg.pinInputStatus = PININPUTSTATUS_SYS_EVENT_PIN;
	}else if(eventNum == SYS_EVENT_NONE){
		condMsg.pinInputStatus = PININPUTSTATUS_TIMEOUT;
	}
    StPinEvent *pinEvent = (StPinEvent*)malloc(sizeof(StPinEvent));
    memset(pinEvent, 0, sizeof(StPinEvent));
    pinEvent->status = condMsg.pinInputStatus;
    list_add_tail(&pinEvent->list,&g_pinEventList);
    THREAD_COND_SIGNAL(THREAD_COND_INDEX_PIN_PININPUT,&condMsg);
    THREAD_MUTEX_UNLOCK(THREAD_MUTEX_INDEX_PINEVENTSYNC);
}
static int __registerPinEvent(int timeOuts)
{
	THREAD_MUTEX_LOCK(THREAD_MUTEX_INDEX_PINEVENTSYNC);
	int isEmpty = list_empty(&g_pinEventList);
	LOGD_FMT("isEmpty[%d]",isEmpty);
	while (!isEmpty){
		StPinEvent *pinEvent = list_first_entry(&g_pinEventList,StPinEvent,list);
		LOGE_FMT("pinEvent->status[%d]",pinEvent->status);
		list_del(&pinEvent->list);
		free(pinEvent);
		isEmpty = list_empty(&g_pinEventList);
	}
	THREAD_MUTEX_UNLOCK(THREAD_MUTEX_INDEX_PINEVENTSYNC);
	if(!EXEC_NDK("NDK_SYS_RegisterEvent", NDK_SYS_RegisterEvent(SYS_EVENT_PIN, (timeOuts+1)* 1000, __notifyPinEvent),NDK_OK,PINPAD_INPUT)){
		return ACK_ERR;
	}
	LOGD_FMT(">>>NDK_SYS_RegisterEvent PinEvent Succ.");
	return ACK_OK;
}

static int __responseKey(uchar res, int dLen, char* perrcode,int status)
{
	int outlen = 0;
	unsigned char output[128];
	output[RESPOND_DATA_OFFSET + dLen-1]=res;
	JNIEnv *env = NULL;
	jboolean isAttached = JNI_FALSE;
	int ret = (*gJavaVM)->GetEnv(gJavaVM,(void **) &env, JNI_VERSION_1_4);
	if(ret < 0 ) {
		ret =  (*gJavaVM)->AttachCurrentThread(gJavaVM,(JNIEnv **) &env, NULL);
		if (ret < 0) {
			LOGD_FMT(">>>AttachCurrentThread error.");
			return ACK_ERR;
		}
		isAttached = JNI_TRUE;
	}
	if(g_cmdRspLisObj == NULL || g_cmdRspLisMid == NULL) {
		LOGD_FMT(">>>g_cmdRspLisObj[%d] g_cmdRspLisMid[%d]",g_cmdRspLisObj,g_cmdRspLisMid);
		return ACK_ERR;
	}
	responseCmd(output, dLen, &outlen, perrcode);

	memcpy(output+outlen,&status,1);
	outlen += 1;

	jbyteArray jarrRV =(*env)->NewByteArray(env,outlen);
	jbyte *jby =(*env)->GetByteArrayElements(env,jarrRV, 0);
	memcpy(jby, output, outlen);
	(*env)->SetByteArrayRegion(env,jarrRV, 0,outlen, jby);
	(*env)->CallVoidMethod(env,g_cmdRspLisObj,g_cmdRspLisMid,(jint)status,jarrRV);
	free(jby);
	if(isAttached)
		(*gJavaVM)->DetachCurrentThread(gJavaVM);
	return ACK_OK;
}
//numkey:'0'-'9'
//funkey:ESC/BASP/ENTER
static int __pinInputting(StPwdFunParam *funParam,ST_THREAD_COND_MSG *msg)
{
	int isRNIB = funParam->pwdparam->isRNIB;
	int currPinLen = 0;
    THREAD_COND_INIT(THREAD_COND_INDEX_PIN_PININPUT);
    while (1){
        START:
		THREAD_MUTEX_LOCK(THREAD_MUTEX_INDEX_PINEVENTSYNC);
		int isEmpty = list_empty(&g_pinEventList);
		//LOGD_FMT(">>>isEmpty[%d]",isEmpty);
		if(isEmpty){
			THREAD_MUTEX_UNLOCK(THREAD_MUTEX_INDEX_PINEVENTSYNC);
			THREAD_COND_TIMEDWAIT(THREAD_COND_INDEX_PIN_PININPUT,500,msg);
			goto START;
		}else{
			StPinEvent *pinEvent = list_first_entry(&g_pinEventList,StPinEvent,list);
			msg->pinInputStatus = pinEvent->status;
			list_del(&pinEvent->list);
			free(pinEvent);
			THREAD_MUTEX_UNLOCK(THREAD_MUTEX_INDEX_PINEVENTSYNC);
		}
		LOGE_FMT(">>>START GetPin");
		struct timeval startTime;
		gettimeofday(&startTime,NULL);
        if(msg->pinInputStatus != PININPUTSTATUS_SYS_EVENT_PIN){
			funParam->resultPwdLen = 0;
            goto ON_ACK;
        }
		//NAPI
		int pinBlockLen = 0;
		uchar ksn[16];int ksnLen = 0;

		int result = NDK_ERR;
		if(getUseNapi() || isRNIB ){
			if(!EXEC_NDK("NAPI_SecVPPGetEvent", result = NAPI_SecVPPGetEvent(&funParam->keyValue,funParam->pinBlock,&pinBlockLen,ksn,&ksnLen),NDK_OK,PINPAD_INPUT)){
				if(result == NDK_ERR_SECVP_TIMED_OUT){
					msg->pinInputStatus = PININPUTSTATUS_TIMEOUT;
					goto ON_ACK;
				}
				goto ON_ERR;
			}
            if(ksnLen > 0 && ksnLen <= 16){
               memcpy(funParam->apiPinKsn,ksn,ksnLen);
            }
		} else{
			if(funParam->pwdparam->keySys == DUKPT){
				if(!EXEC_NDK("NDK_SecGetPinResultDukpt",result = NDK_SecGetPinResultDukpt(funParam->pinBlock, funParam->apiPinKsn, &funParam->keyValue),NDK_OK,PINPAD_INPUT)){
					if(result == NDK_ERR_SECVP_TIMED_OUT){
						msg->pinInputStatus = PININPUTSTATUS_TIMEOUT;
						goto ON_ACK;
					}
					goto ON_ERR;
				}
			} else{
				if(!EXEC_NDK("NDK_SecGetPinResult",result = NDK_SecGetPinResult(funParam->pinBlock,&funParam->keyValue),NDK_OK,PINPAD_INPUT)){
					if(result == NDK_ERR_SECVP_TIMED_OUT){
						msg->pinInputStatus = PININPUTSTATUS_TIMEOUT;
						goto ON_ACK;
					}
					goto ON_ERR;
				}
			}
		}
		LOGE_FMT(">>>END GetPin disMs[%d]",__GetTimeDistance(&startTime));
		LOGE_FMT(">>>START responseKey");
		gettimeofday(&startTime,NULL);
        LOGD_STR("pinBlock",funParam->pinBlock, sizeof(funParam->pinBlock));
        if(funParam->keyValue!=SEC_VPP_KEY_ENTER){
            funParam->resultPwdLen = currPinLen = funParam->pinBlock[0]&0x7F;
        }
		LOGD_FMT("currPinLen[0x%x] keyValue[%d]",currPinLen,funParam->keyValue);
		if(funParam->pwdparam->pinFunKeyType == PINFUNKEYTYPE_DISABLE_ENTER && funParam->pwdparam->inputMaxLen == currPinLen){
			funParam->keyValue = 0x8000 | K_ENTER;
            if(getUseNapi() || isRNIB){
                if(!EXEC_NDK("NAPI_SecVPPSetEvent", result = NAPI_SecVPPSetEvent(NAPI_SEC_VPP_KEY_ENTER),NDK_OK,PINPAD_INPUT)){
                    goto ON_ERR;
                }
                goto ON_ACK;
            } else{
                if (funParam->pwdparam->keySys == DUKPT) {
                    if(!EXEC_NDK("NDK_SecGetPinResultDukpt",NDK_SecGetPinResultDukpt(funParam->pinBlock, funParam->apiPinKsn, &funParam->keyValue),NDK_OK,PINPAD_INPUT)){
                        goto ON_ERR;
                    }
                } else {
                    if(!EXEC_NDK("NDK_SecGetPinResult",NDK_SecGetPinResult(funParam->pinBlock, &funParam->keyValue),NDK_OK,PINPAD_INPUT)){
                        goto ON_ERR;
                    }
                }
                goto ON_ACK;
            }
		}
		switch (funParam->keyValue){
			case SEC_VPP_KEY_PIN:
				__responseKey(0x0D,1,CMD_CURRENT_STATUS,SEC_VPP_KEY_PIN);
				break;
			case SEC_VPP_KEY_BACKSPACE:
				__responseKey(0x0A,1,CMD_CURRENT_STATUS,SEC_VPP_KEY_BACKSPACE);
				break;
			case SEC_VPP_KEY_CLEAR:
				if(getHasKeyClear()==1) {
					__responseKey(0x0F,1,CMD_CURRENT_STATUS,SEC_VPP_KEY_CLEAR);
					break;
				} else{
					funParam->resultPwdLen = 0;
                    goto ON_ACK;
				}
			case SEC_VPP_KEY_ESC:
				funParam->resultPwdLen = 0;
                goto ON_ACK;
			case SEC_VPP_KEY_ENTER:
				funParam->resultPwdLen = currPinLen;
				goto ON_ACK;
		}
		LOGE_FMT(">>>END responseKey disMs[%d]",__GetTimeDistance(&startTime));
	}
    ON_ERR:
	LOGD_FMT(">>>ACK_ERR");
	funParam->resultPwdLen = 0;
	return ACK_ERR;
    ON_ACK:
    LOGD_FMT(">>>ACK_OK");
	return ACK_OK;
}
//numkey:'0'-'9'
//funkey:ESC/BASP/ENTER
static int __pinInputting2(StPwdFunParam *funParam,ST_THREAD_COND_MSG *msg)
{
	int isRNIB = funParam->pwdparam->isRNIB;
	int currPinLen = 0;
	struct timeval startTimeOut;
	gettimeofday(&startTimeOut,NULL);
	while (1){
		if(pinCancelFlag == 1){
			msg->pinInputStatus = PININPUTSTATUS_CANCEL;
			funParam->resultPwdLen = 0;
			goto ON_ACK;
		}
		if(__GetTimeDistance(&startTimeOut) >= (funParam->pwdparam->timeOuts)*1000){
			msg->pinInputStatus = PININPUTSTATUS_TIMEOUT;
			funParam->resultPwdLen = 0;
			goto ON_ACK;
		}

		LOGE_FMT(">>>START GetPin");
		struct timeval startTime;
		gettimeofday(&startTime,NULL);

		//NAPI
		int pinBlockLen = 0;
		uchar ksn[16];int ksnLen = 0;

		int result = NDK_ERR;
		if(getUseNapi() || isRNIB){
			if(!EXEC_NDK("NAPI_SecVPPGetEvent", result = NAPI_SecVPPGetEvent(&funParam->keyValue,funParam->pinBlock,&pinBlockLen,ksn,&ksnLen),NDK_OK,PINPAD_INPUT)){
				if(result == NDK_ERR_SECVP_TIMED_OUT){
					msg->pinInputStatus = PININPUTSTATUS_TIMEOUT;
					goto ON_ACK;
				}
				goto ON_ERR;
			}
			if(ksnLen > 0 && ksnLen <= 16){
				memcpy(funParam->apiPinKsn,ksn,ksnLen);
			}
		} else{
			if(funParam->pwdparam->keySys == DUKPT){
				if(!EXEC_NDK("NDK_SecGetPinResultDukpt",result = NDK_SecGetPinResultDukpt(funParam->pinBlock, funParam->apiPinKsn, &funParam->keyValue),NDK_OK,PINPAD_INPUT)){
					if(result == NDK_ERR_SECVP_TIMED_OUT){
						msg->pinInputStatus = PININPUTSTATUS_TIMEOUT;
						goto ON_ACK;
					}
					goto ON_ERR;
				}
			} else{
				if(!EXEC_NDK("NDK_SecGetPinResult",result = NDK_SecGetPinResult(funParam->pinBlock,&funParam->keyValue),NDK_OK,PINPAD_INPUT)){
					if(result == NDK_ERR_SECVP_TIMED_OUT){
						msg->pinInputStatus = PININPUTSTATUS_TIMEOUT;
						goto ON_ACK;
					}
					goto ON_ERR;
				}
			}
		}
		LOGE_FMT(">>>END GetPin disMs[%d]",__GetTimeDistance(&startTime));
		LOGE_FMT(">>>START responseKey");
		gettimeofday(&startTime,NULL);
		LOGD_STR("pinBlock",funParam->pinBlock, sizeof(funParam->pinBlock));
		if(funParam->keyValue!=SEC_VPP_KEY_ENTER){
			funParam->resultPwdLen = currPinLen = funParam->pinBlock[0]&0x7F;
		}
		LOGD_FMT("currPinLen[0x%x] keyValue[%d]",currPinLen,funParam->keyValue);
		if(funParam->pwdparam->pinFunKeyType == PINFUNKEYTYPE_DISABLE_ENTER && funParam->pwdparam->inputMaxLen == currPinLen){
			funParam->keyValue = 0x8000 | K_ENTER;
			if(getUseNapi() || isRNIB){
				if(!EXEC_NDK("NAPI_SecVPPSetEvent", result = NAPI_SecVPPSetEvent(NAPI_SEC_VPP_KEY_ENTER),NDK_OK,PINPAD_INPUT)){
					goto ON_ERR;
				}
				goto ON_ACK;
			} else{
				if (funParam->pwdparam->keySys == DUKPT) {
					if(!EXEC_NDK("NDK_SecGetPinResultDukpt",NDK_SecGetPinResultDukpt(funParam->pinBlock, funParam->apiPinKsn, &funParam->keyValue),NDK_OK,PINPAD_INPUT)){
						goto ON_ERR;
					}
				} else {
					if(!EXEC_NDK("NDK_SecGetPinResult",NDK_SecGetPinResult(funParam->pinBlock, &funParam->keyValue),NDK_OK,PINPAD_INPUT)){
						goto ON_ERR;
					}
				}
				goto ON_ACK;
			}
		}
		switch (funParam->keyValue){
			case SEC_VPP_KEY_PIN:
				__responseKey(0x0D,1,CMD_CURRENT_STATUS,SEC_VPP_KEY_PIN);
				break;
			case SEC_VPP_KEY_BACKSPACE:
				__responseKey(0x0A,1,CMD_CURRENT_STATUS,SEC_VPP_KEY_BACKSPACE);
				break;
			case SEC_VPP_KEY_CLEAR:
				if(getHasKeyClear()==1) {
					__responseKey(0x0F,1,CMD_CURRENT_STATUS,SEC_VPP_KEY_CLEAR);
					break;
				} else{
					funParam->resultPwdLen = 0;
					goto ON_ACK;
				}
			case SEC_VPP_KEY_ESC:
				funParam->resultPwdLen = 0;
				goto ON_ACK;
			case SEC_VPP_KEY_ENTER:
				funParam->resultPwdLen = currPinLen;
				goto ON_ACK;
			case NAPI_SEC_VPP_SLID_LEFT:
			case NAPI_SEC_VPP_SLID_RIGHT:
			case NAPI_SEC_VPP_SLID_UP:
			case NAPI_SEC_VPP_SLID_DOWN:
			case NAPI_SEC_VPP_SLID_NUMKEY:
			case NAPI_SEC_VPP_SLID_ENTER:
			case NAPI_SEC_VPP_SLID_CANCLE:
			case NAPI_SEC_VPP_SLID_BACKSPACE:
			case NAPI_SEC_VPP_SLID_NODIGIT:
			case NAPI_SEC_VPP_SLID_CLEAR:
				LOGE_FMT("isRNIB[%d] keyValue[%d]",isRNIB,funParam->keyValue);
				if(isRNIB){
					__responseKey(0x10,1,CMD_CURRENT_STATUS,funParam->keyValue);
				}
				break;
		}
		LOGE_FMT(">>>END responseKey disMs[%d]",__GetTimeDistance(&startTime));
	}
	ON_ERR:
	LOGD_FMT(">>>ACK_ERR");
	funParam->resultPwdLen = 0;
	return ACK_ERR;
	ON_ACK:
	LOGD_FMT(">>>ACK_OK");
	return ACK_OK;
}

int Pinpad_Input(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen)
{
    int offset = 2;
	StPinPwdInputParam pwdparam;
    memset(&pwdparam, 0, sizeof(StPinPwdInputParam));
	if(Pin_GetPwdInputParam(&pwdparam, pbuf, buf_len)!=ACK_OK){
		ERRMSG(SDK_ERR_PARAM,PINPAD_INPUT);
		goto ON_ERR;
	}
    StPwdFunParam funParam;
    memset(&funParam, 0, sizeof(funParam));
    funParam.pwdparam = &pwdparam;
    memcpy(funParam.ackCodeHead, CMD_ERR_OTHER, 2);
    pinEventMode = funParam.pwdparam->pinEventMode;
	pinCancelFlag = 0;
    if (pwdparam.inputMaxLen < 0 || pwdparam.inputMaxLen > 12) {
		ERRMSG(SDK_ERR_PARAM,PINPAD_INPUT);
        goto ON_ERR;
    }
	if(pwdparam.keySys < MKSK || pwdparam.keySys > DUKPT){
		LOGD_FMT(">>>keySys[%d] error",pwdparam.keySys);
		ERRMSG(SDK_ERR_PARAM,PINPAD_INPUT);
		goto ON_ERR;
	}
	if(pwdparam.algMode < DES || pwdparam.algMode > AES){
		LOGE_FMT(">>>algMode[%d] error.",pwdparam.algMode);
		ERRMSG(SDK_ERR_PARAM,PINPAD_INPUT);
		goto ON_ERR;
	}
	if(pwdparam.algMode == AES && pwdparam.keySys == DUKPT){
		dukptAESFlag = 1;
	}else{
		dukptAESFlag = 0;
	}
	LOGD_FMT("dukptAESFlag[%d]",dukptAESFlag);
    if (__pwdInputParamPre(&funParam) != ACK_OK) {
        goto ON_ERR;
    }
    if(pwdparam.wkeyDataLen != 0 && __inputLoadOutWk(&pwdparam) != ACK_OK){
		goto ON_ERR;
    }
    if ((funParam.pwdparam->isRNIB == 0) && pinEventMode == 1 && __registerPinEvent(funParam.pwdparam->timeOuts)) {
        goto ON_ERR;
    }
    if (__pwdInputSecGetPinPre(&funParam) != ACK_OK) {
        goto ON_ERR;
    }
    ST_THREAD_COND_MSG msg;
    int ret = (pinEventMode == 1 ? __pinInputting(&funParam, &msg) : __pinInputting2(&funParam, &msg));
    if (ret != ACK_OK) {
        memcpy(funParam.ackCodeHead, CMD_ERR_OTHER, 2);
        goto ON_ERR;
    }
	LOGD_FMT("pinInputStatus[%d] resultPwdLen[%d] keyValue[%d]",msg.pinInputStatus,funParam.resultPwdLen,funParam.keyValue);
    if (msg.pinInputStatus == PININPUTSTATUS_TIMEOUT) {
		ERRMSG(SDK_ERR_TIMEOUT,PINPAD_INPUT);
        memcpy(funParam.ackCodeHead, CMD_ERR_TIMEOUT, 2);
        goto ON_ERR;
    } else if (msg.pinInputStatus == PININPUTSTATUS_CANCEL) {
		ERRMSG(SDK_ERR_CANCEL,PINPAD_INPUT);
        memcpy(funParam.ackCodeHead, CMD_CANCEL, 2);
        goto ON_ERR;
    } else if (msg.pinInputStatus == PININPUTSTATUS_SYS_EVENT_CARD) {
        //todo
    }
    if (funParam.pwdparam->pinblockMode == PINBLOCKMODE_PLAIN) {
		int len = 8;
		if(pwdparam.algMode == SM4 || pwdparam.algMode == AES){
			len = 16;
		}
		LOGD_FMT(">>>pinBlock plain mode len[%d]",len);
		if (!EXEC_NDK("NDK_SecCalcDes", NDK_SecCalcDes(SEC_KEY_TYPE_TPK, funParam.pwdparam->keyIndex, funParam.pinBlock, len, funParam.pinBlock, SEC_DES_DECRYPT | SEC_DES_KEYLEN_DEFAULT), NDK_OK,PINPAD_INPUT)) {
            memcpy(funParam.ackCodeHead, CMD_ERR_OTHER, 2);
            goto ON_ERR;
        }
	}
	if(funParam.keyValue == SEC_VPP_KEY_ESC || funParam.keyValue == SEC_VPP_KEY_CLEAR){
		memcpy(funParam.ackCodeHead, CMD_OK, 2);
		pOut[offset] = PIN_KEY_CANCEL;offset++;
		goto ON_ERR;
	}else if(funParam.keyValue == SEC_VPP_KEY_ENTER){
		memcpy(funParam.ackCodeHead, CMD_OK, 2);
		pOut[offset] = PIN_KEY_ENTER;offset++;//除PIN_KEY_CLEAR、PIN_KEY_SWIPCARD、PIN_KEY_ICCARD以外上层都会去获取应答值
		pOut[offset] = funParam.resultPwdLen;offset++;
		int pinBlockLen=0;
		if(pwdparam.keySys == MKSK){
			if(pwdparam.algMode == DES){
				pinBlockLen = 8;
			}else if(pwdparam.algMode == SM4 || pwdparam.algMode == AES){
				pinBlockLen = 16;
			}
		}else if(pwdparam.keySys == DUKPT){
			if(pwdparam.algMode == DES){
				pinBlockLen = 8;
			}else{
				pinBlockLen = 16;
			}
		}
		nlMpos_Command.mpos_writelen(pOut+offset,pinBlockLen, _VAR_BIT16);offset+=2;
		if(pinBlockLen > 0){
			memcpy(pOut+offset, funParam.pinBlock, pinBlockLen);offset+=pinBlockLen;
		}
		memcpy(pOut+offset, funParam.apiPinKsn, 10);offset+=10;
	} else{
		memcpy(funParam.ackCodeHead, CMD_ERR_OTHER, 2);
		goto ON_ERR;
	}
    if(funParam.resultPwdLen < 4){
        offset = 2;
        pOut[offset] = 0;offset++;
        pOut[offset] = 0;offset++;
        goto ON_ERR;
    }
ON_ERR:
	//if(pinEventMode == 1){
		NDK_UnRegisterEvent(SYS_EVENT_PIN);
	//}
    responseCmd(pOut,offset-2,outLen,funParam.ackCodeHead);
    return 0;
}

int Pin_Cancel()
{
	LOGE_FMT("");
	if(pinEventMode == 0){
	   pinCancelFlag = 1;
	   return 0;
	}
	NDK_UnRegisterEvent(SYS_EVENT_PIN);
    THREAD_MUTEX_LOCK(THREAD_MUTEX_INDEX_PINEVENTSYNC);
    StPinEvent *pinEvent = (StPinEvent*)malloc(sizeof(StPinEvent));
    memset(pinEvent, 0, sizeof(StPinEvent));
    ST_THREAD_COND_MSG condMsg;
    pinEvent->status = condMsg.pinInputStatus = PININPUTSTATUS_CANCEL;
    list_add_tail(&pinEvent->list,&g_pinEventList);
    THREAD_COND_SIGNAL(THREAD_COND_INDEX_PIN_PININPUT,&condMsg);
    THREAD_MUTEX_UNLOCK(THREAD_MUTEX_INDEX_PINEVENTSYNC);
    return 0;
}

#define OTHER

int Pinpad_CheckKey(puchar pbuf, int buf_len, unsigned char *pOut, int *outLen) {
	int offset = 0;
    int keyType = pbuf[offset];offset++;
    int algMode = pbuf[offset];offset++;
    int keyIndex= pbuf[offset];offset++;
    int kcvlen = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);offset+=2;
    uchar *kcvData = pbuf+offset;offset+=kcvlen;

	LOGD_FMT(">>>keyType[%d] algMode[%d] keyIndex[%d] kcvlen[%d]",keyType,algMode,keyIndex,kcvlen);
	if(keyIndex <= 0 || keyIndex > 255 || keyType < KCV_TLK || keyType > KCV_TDK || algMode < DES || algMode > AES){
		goto ON_ERR;
	}
	EM_SEC_KEY_TYPE secKeyType [] = {SEC_KEY_TYPE_TLK, SEC_KEY_TYPE_TMK, SEC_KEY_TYPE_TPK,SEC_KEY_TYPE_TAK,SEC_KEY_TYPE_TDK};
	EM_SEC_KEY_ALG secKeyAlg[] = {SEC_KEY_DES, SEC_KEY_SM4, SEC_KEY_AES};
	ST_SEC_KCV_INFO stKcvInfoIn;
	memset(&stKcvInfoIn, 0, sizeof(ST_SEC_KCV_INFO));
	stKcvInfoIn.nCheckMode = SEC_KCV_ZERO;
//	stKcvInfoIn.nLen = 4;
	uchar kcv[8];
	memset(kcv,0, sizeof(kcv));
	if(kcvlen <= sizeof(kcv)){
		memcpy(kcv,kcvData,kcvlen);
	}
	int targetKeyType = secKeyType[keyType]|secKeyAlg[algMode-1];
    LOGD_FMT(">>>targetKeyType[%d] keyIndex[%d]",targetKeyType,keyIndex);
	int extoffset = 2;
    if(!EXEC_NDK("NDK_SecGetKcv",NDK_SecGetKcv(targetKeyType, keyIndex, &stKcvInfoIn),NDK_OK,PINPAD_CHECKKEY)){
		pOut[extoffset] = 1;extoffset+=1;
		nlMpos_Command.mpos_writelen(pOut+extoffset,4, _VAR_BIT16);extoffset += 2;
		memcpy(pOut+extoffset,"\x00\x00\x00\x00",4);extoffset+=4;
	}else{
		int outKcvLen = stKcvInfoIn.nLen;
		LOGD_FMT(">>>KCV kcvlen[%d] outKcvLen[%d]",kcvlen,outKcvLen);
		if(kcvlen >= 4 && kcvlen >= outKcvLen){
			LOGD_STR("stKcvInfoIn.sCheckBuf",stKcvInfoIn.sCheckBuf,outKcvLen);
			LOGD_STR("kcv",kcv,kcvlen);
			if(memcmp(stKcvInfoIn.sCheckBuf, kcv, outKcvLen) == 0){
				pOut[extoffset] = 0;extoffset+=1;//OK
			}else{
				pOut[extoffset] = 1;extoffset+=1;
			}
			nlMpos_Command.mpos_writelen(pOut+extoffset,outKcvLen, _VAR_BIT16);extoffset += 2;
			memcpy(pOut+extoffset,"\x00\x00\x00\x00",outKcvLen);extoffset+=outKcvLen;
		}else{
			pOut[extoffset] = 0;extoffset+=1;
			nlMpos_Command.mpos_writelen(pOut+extoffset,outKcvLen, _VAR_BIT16);extoffset += 2;
			memcpy(pOut + extoffset, stKcvInfoIn.sCheckBuf, outKcvLen);extoffset+=outKcvLen;
		}
	}
	responseCmd(pOut,extoffset - 2,outLen,CMD_OK);
	return 0;
 	ON_ERR:
	responseCmd(pOut,0,outLen,CMD_ERR_OTHER);
	return 0;
}

int Pinpad_DelKey(puchar pbuf, int buf_len, unsigned char *pOut, int *outLen) {
	int offset = 0;
	int keyType = pbuf[offset];offset+=1;
	int algMode = pbuf[offset];offset+=1;
	int keyIndex = pbuf[offset];offset+=1;

	LOGD_FMT(">>>keyType[%d] algMode[%d] keyIndex[%d]",keyType,algMode,keyIndex);
	if(keyType < RMKEY_TLK || keyType > RMKEY_USER || algMode < DES || algMode > AES){
        ERRMSG(SDK_ERR_PARAM,PINPAD_DELKEY);
		LOGD_FMT(">>>param error.");
		goto ON_ERR;
	}
	EM_SEC_KEY_TYPE secKeyType [] = {SEC_KEY_TYPE_TLK, SEC_KEY_TYPE_TMK, SEC_KEY_TYPE_TPK,SEC_KEY_TYPE_TAK,SEC_KEY_TYPE_TDK};
	EM_SEC_KEY_ALG secKeyAlg[] = {SEC_KEY_DES, SEC_KEY_SM4, SEC_KEY_AES};
	if(keyType >= RMKEY_TLK && keyType <= RMKEY_TDK){
		int targetType = secKeyType[keyType]|secKeyAlg[algMode-1];
        LOGD_FMT(">>>targetType[%d] keyIndex[%d]",targetType,keyIndex);
		if(!EXEC_NDK("NDK_SecKeyDelete",NDK_SecKeyDelete(keyIndex,targetType),NDK_OK,PINPAD_DELKEY)){
			goto ON_ERR;
		}
	}else if(keyType == RMKEY_USER){
		if(!EXEC_NDK(" NDK_SecUserKeyDelete", NDK_SecUserKeyDelete(),NDK_OK,PINPAD_DELKEY)){
			goto ON_ERR;
		}
	}else if(keyType == RMKEY_ALL){
		if(!EXEC_NDK("NDK_SecKeyErase",NDK_SecKeyErase(),NDK_OK,PINPAD_DELKEY)){
			goto ON_ERR;
		}
//		if(loadDESTLK()!=0){
//			goto ON_ERR;
//		}
	}else{
		LOGD_FMT(">>>keyType[%d] error",keyType);
		ERRMSG(SDK_ERR_PARAM,PINPAD_DELKEY);
		goto ON_ERR;
	}
	responseCmd(pOut,0,outLen,CMD_OK);
	return 0;
	ON_ERR:
	responseCmd(pOut,0,outLen,CMD_ERR_OTHER);
	return 0;
}

int Pinpad_LoadDukpt(puchar pbuf, int buf_len, unsigned char *pOut, int *outLen) {
	int offset = 0;
	int loadMode = pbuf[offset];offset++;
	int ksnIndex = pbuf[offset];offset++;
	uchar *ksnData = pbuf+offset;offset+=10;
	int keyLen = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);offset+=2;
	uchar *keyData = pbuf+offset;offset+=keyLen;
	int mainKeyIndex =  pbuf[offset];offset++;
	int kcvLen = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);offset+=2;
	uchar *kcvData = pbuf[offset];offset+=kcvLen;
	LOGD_FMT(">>>loadMode[%d] ksnIndex[%d] keyLen[%d] kcvLen[%d] mainKeyIndex[%d]",loadMode,ksnIndex,keyLen,kcvLen,mainKeyIndex);

	if(keyLen != 8 && keyLen != 16 && keyLen != 24 && keyLen != 32 && loadMode != TR31_KEY){
		goto ON_ERR;
	}
	if(ksnIndex <= 0 || ksnIndex > 255){
		goto ON_ERR;
	}
//	uchar keyDataOut[32];
//	memset(keyDataOut,0, sizeof(keyDataOut));
	ST_SEC_KCV_INFO stKcvInfoIn;
	memset(&stKcvInfoIn, 0, sizeof(stKcvInfoIn));
	stKcvInfoIn.nLen = 0;
	stKcvInfoIn.nCheckMode = 0;
	if(loadMode == DEFAULT_TRANSFER_TYPE){
		if(__loadTLKDESSM4(16,SEC_KEY_TYPE_TLK|SEC_KEY_DES)==ACK_ERR){
			goto ON_ERR;
		}
		if(!EXEC_NDK("NDK_SecLoadTIK",NDK_SecLoadTIK(ksnIndex, 1, keyLen, keyData, ksnData, &stKcvInfoIn),NDK_OK,PINPAD_LOADDUKPT)){
			goto ON_ERR;
		}
//		if(!EXEC_NDK("NDK_SecCalcDes",NDK_SecCalcDes(SEC_KEY_TYPE_TLK, 1, keyData, keyLen, keyDataOut, SEC_DES_DECRYPT | SEC_DES_KEYLEN_DEFAULT),NDK_OK,PINPAD_LOADDUKPT)){
//			goto ON_ERR;
//		}
	}else if(loadMode == MAINKEY_TYPE || loadMode == TR31_KEY){
		if(mainKeyIndex <= 0 || mainKeyIndex > 255){
			goto ON_ERR;
		}
		if(!EXEC_NDK("NDK_SecLoadDukptKey",NDK_SecLoadDukptKey(ksnIndex,SEC_KEY_TYPE_TMK,mainKeyIndex,keyLen,keyData,ksnData,&stKcvInfoIn),NDK_OK,PINPAD_LOADDUKPT)){
			goto ON_ERR;
		}
//		if(!EXEC_NDK("NDK_SecCalcDes",NDK_SecCalcDes(SEC_KEY_TYPE_TMK,mainKeyIndex,keyData,keyLen,keyDataOut,SEC_DES_DECRYPT|SEC_DES_KEYLEN_DEFAULT),NDK_OK,PINPAD_LOADDUKPT)){
//			goto ON_ERR;
//		}
	}else if(loadMode == PLAIN_KEY){
		if(!EXEC_NDK("NDK_SecLoadDukptKey",NDK_SecLoadDukptKey(ksnIndex, 0, 0, keyLen, keyData, ksnData, &stKcvInfoIn),NDK_OK,PINPAD_LOADDUKPT)){
			goto ON_ERR;
		}
//        memcpy(keyDataOut,keyData,keyLen);
	}else{
		ERRMSG(SDK_ERR_PARAM,PINPAD_LOADDUKPT);
		goto ON_ERR;
	}

//	char enKsnData[10];
//	memset(enKsnData,0, sizeof(enKsnData));
//	if(!EXEC_NDK("NDK_AlgTDes",NDK_AlgTDes(ksnData, enKsnData, keyDataOut, keyLen, ALG_TDS_MODE_ENC),NDK_OK,PINPAD_LOADDUKPT)){
//		goto ON_ERR;
//	}
//	LOGD_STR("dukpt kcv",enKsnData, sizeof(enKsnData));
	int extoffset = 2;
	memcpy(pOut+extoffset,"\x30\x30",2);extoffset+=2;
//	nlMpos_Command.mpos_writelen(pOut+extoffset,4, _VAR_BIT16);extoffset+=2;
//	memcpy(pOut+extoffset,enKsnData,4);extoffset+=4;
	responseCmd(pOut,extoffset-2,outLen,CMD_OK);
	return 0;
	ON_ERR:
	responseCmd(pOut,0,outLen,CMD_ERR_OTHER);
	return 0;
}

int Pinpad_IncreaseKsn(puchar pbuf, int buf_len, unsigned char *pOut, int *outLen) {
	int offset = 0;
	int keyIndex = pbuf[offset];offset++;
	if(!EXEC_NDK("NDK_SecIncreaseDukptKsn",NDK_SecIncreaseDukptKsn(keyIndex),NDK_OK,PINPAD_INCREASEKSN)){
		goto ON_ERR;
	}
	responseCmd(pOut,0,outLen,CMD_OK);
	return 0;
	ON_ERR:
	responseCmd(pOut,0,outLen,CMD_ERR_OTHER);
	return 0;
}

int Pinpad_GetDukptKsn(puchar pbuf, int buf_len, unsigned char *pOut, int *outLen) {
	uchar keyIndex;
	uchar ksn[32];
	memset(ksn, 0, sizeof(ksn));
	keyIndex = nlMpos_Command.mpos_getvar(pbuf, _VAR_BIT8);
	LOGD_FMT("dukpt keyIndex[%d]", keyIndex);
	if(EXEC_NDK("NDK_SecGetDukptKsn",NDK_SecGetDukptKsn(keyIndex, ksn),NDK_OK,PINPAD_GETDUKPTKSN)){
		memcpy(pOut + 2, ksn, 10);
		LOGD_STR("KSN", ksn, 10);
		responseCmd(pOut, 10, outLen, CMD_OK);
	}else{
		responseCmd(pOut, 0, outLen, CMD_ERR_OTHER);
	}
	return 0;

}

int loadDESTLK() {
    int nRet;
    ST_SEC_KEY_INFO stKeyInfoIn;
    ST_SEC_KCV_INFO stKcvInfoIn;
    memset(&stKcvInfoIn, 0, sizeof(stKcvInfoIn));
    memset(&stKeyInfoIn, 0, sizeof(stKeyInfoIn));
    stKeyInfoIn.ucScrKeyIdx = 0;
    stKeyInfoIn.ucScrKeyType = 0;
    stKeyInfoIn.nDstKeyLen = 16;
    stKeyInfoIn.ucDstKeyIdx = 1;
    stKeyInfoIn.ucDstKeyType = SEC_KEY_TYPE_TLK;
    memset(stKeyInfoIn.sDstKeyValue, 0x31, stKeyInfoIn.nDstKeyLen);
    stKcvInfoIn.nCheckMode = 0;
    if(EXEC_NDK("NDK_SecLoadKey DES TLK",nRet = NDK_SecLoadKey(&stKeyInfoIn, &stKcvInfoIn), NDK_OK,PINPAD_DELKEY)){
        LOGD_FMT(">>>NDK_SecLoadKey DES TLK SUCC");
    }
    return nRet;
}
