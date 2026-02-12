#include <stdio.h>
#include <malloc.h>
#include <string.h>
#include "log.h"
#include "ndk.h"
#include "cardmgr.h"
#include "readerrfid.h"
#include "readeric.h"
#include "readermag.h"
#include "card.h"

void* CardMgr_CreateCardInfo()
{
	StCardInfo* p = (StCardInfo*)malloc(sizeof(StCardInfo));
	memset(p, 0,sizeof(StCardInfo));
	return p;
}

int CardMgr_GetOpenCardParam(StCardReaderParam *stCardReaderParam,unsigned char*pbuf,int buf_len)
{
    if(stCardReaderParam == NULL){
        return ACK_ERR;
    }
	memset(stCardReaderParam,0, sizeof(StCardReaderParam));
	int offset = 0;
	stCardReaderParam->readCardMode = nlMpos_Command.mpos_getvar(pbuf+offset, _VAR_BIT8);offset+=1;
	stCardReaderParam->expectedRfTypes = nlMpos_Command.mpos_getvar(pbuf+offset, _VAR_BIT8);offset+=1;
	stCardReaderParam->timeout = nlMpos_Command.mpos_getvar(pbuf+offset, _VAR_BIT8);offset+=1;
	stCardReaderParam->tk2Validity = nlMpos_Command.mpos_getvar(pbuf+offset, _VAR_BIT8);offset+=1;
	stCardReaderParam->rfidTimes = nlMpos_Command.mpos_getvar(pbuf+offset, _VAR_BIT8);offset+=1;
	EXEC_NDK("NDK_C2ToInt",NDK_C2ToInt(&stCardReaderParam->rfidiInterval,pbuf+offset),NDK_OK,COMMAND_NONE);offset+=2;
	stCardReaderParam->searchCardRule = nlMpos_Command.mpos_getvar(pbuf+offset, _VAR_BIT8);offset+=1;

	stCardReaderParam->felicaParamLen = nlMpos_Command.mpos_readlen(pbuf+offset, _VAR_BIT16);offset += 2;
	stCardReaderParam->felicaParam = pbuf+offset;offset += stCardReaderParam->felicaParamLen;

	if(stCardReaderParam->felicaParamLen!=0 && stCardReaderParam->felicaParamLen % 4 !=0){
		LOGE_FMT("felicaParamLen[%d]",stCardReaderParam->felicaParamLen);
		return ACK_ERR;
	}
	stCardReaderParam->enablePreParam = nlMpos_Command.mpos_getvar(pbuf+offset, _VAR_BIT8);offset+=1;

    stCardReaderParam->vasEnable = nlMpos_Command.mpos_getvar(pbuf+offset, _VAR_BIT8);offset+=1;
    stCardReaderParam->vasParamLen = nlMpos_Command.mpos_readlen(pbuf+offset, _VAR_BIT16);offset += 2;
    stCardReaderParam->vasParam = pbuf+offset;offset += stCardReaderParam->vasParamLen;

	LOGD_FMT(">>>ParseResult readCardMode[0x%x] expectedRfTypes[0x%x] timeout[%d] tk2Validity[0x%x] rfidTimes[%d] rfidiInterval[%d] searchCardRule[0x%x] felicaParamLen[%d] enablePreParam[%d] vasEnable[%d] vasParamLen[%d] ",\
		stCardReaderParam->readCardMode,stCardReaderParam->expectedRfTypes,stCardReaderParam->timeout,stCardReaderParam->tk2Validity,\
		stCardReaderParam->rfidTimes,stCardReaderParam->rfidiInterval,stCardReaderParam->searchCardRule,stCardReaderParam->felicaParamLen,stCardReaderParam->enablePreParam,stCardReaderParam->vasEnable,stCardReaderParam->vasParamLen);
    return ACK_OK;
}

void* CardMgr_CreateCardReader()
{
    StCardReader* p = (StCardReader*)malloc(sizeof(StCardReader));
	if(NULL == p){
		return NULL;
	}
	memset(p, 0,sizeof(StCardReader));
	return p;	
}
int CardMgr_ObtainReader(ReadCardMode inputMode, StCardReader** ptCardReaders)
{
	*ptCardReaders = CardMgr_CreateCardReader();
	if(*ptCardReaders == NULL){
		return NL_FAILED;
	}
	if(HAS_CARD_RFID(inputMode)){
		//LOGD_FMT("__readersInit RFID[%d]",(*ptCardReaders));
		(*ptCardReaders)->openCardDev  = RfidReader_Open;
		(*ptCardReaders)->readCardInfo = RfidReader_Read;
		(*ptCardReaders)->closeCardDev = RfidReader_Close;
		(*ptCardReaders)->resumeCardDev= RfidReader_Resume;
	}
    if(HAS_CARD_IC(inputMode)){
		//LOGD_FMT("__readersInit IC[%d]",(*ptCardReaders));
		(*ptCardReaders)->openCardDev  = ICReader_Open;
		(*ptCardReaders)->readCardInfo = ICReader_Read;
		(*ptCardReaders)->closeCardDev = ICReader_Close;
		(*ptCardReaders)->resumeCardDev= ICReader_Resume;

	}
	if(HAS_CARD_MAG(inputMode)){
		//LOGD_FMT("__readersInit MAG[%d]",(*ptCardReaders));
		(*ptCardReaders)->openCardDev  = MagReader_Open;
		(*ptCardReaders)->readCardInfo = MagReader_Read;
		(*ptCardReaders)->closeCardDev = MagReader_Close;
		(*ptCardReaders)->resumeCardDev= MagReader_Resume;
	}
    return NL_OK;
}

int CardMgr_ReleaseReader(StCardReader *ptCardReaders)
{
//	LOGD_FMT("_release_readers ReleaseReader[%d]",ptCardReaders);
 	if(ptCardReaders != NULL){
		free(ptCardReaders);
	}    
    return NL_OK;
}

int CardMgr_CreateCardReaders(int nRequiredCardInputMode,StCardReader* readers[])
{
	int ret = NL_OK;
	if(HAS_CARD_MAG(nRequiredCardInputMode)){
		ret = CardMgr_ObtainReader(CARD_MAG,&readers[CARDREADER_INDEX_MAG]);
	}
	if(HAS_CARD_IC(nRequiredCardInputMode)){
		ret = CardMgr_ObtainReader(CARD_IC,&readers[CARDREADER_INDEX_IC]);
	}
	if(HAS_CARD_RFID(nRequiredCardInputMode)){
		ret = CardMgr_ObtainReader(CARD_RFID,&readers[CARDREADER_INDEX_RFID]);
	}
	return ret;

}
