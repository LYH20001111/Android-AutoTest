#include <stddef.h>
#include <string.h>
#include "ndk.h"
#include "log.h"
#include "readeric.h"
#include "cardmgr.h"
#include "card.h"


int ICReader_Open(void* pstCardReaderParam,void* pstCardInfo)
{
	if(pstCardReaderParam == NULL||pstCardInfo == NULL){
		LOGD_FMT(">>>pstCardReaderParam[%d] pstCardInfo[%d] return.",pstCardReaderParam,pstCardInfo);
		ERRMSG(SDK_ERR_PARAM,CARDREADER_OPEN);
		return NL_FAILED;
	}
	StCardReaderParam* stCardReaderParam = (StCardReaderParam*)pstCardReaderParam;
    StCardInfo* cardInfo = (StCardInfo*)pstCardInfo;
	cardInfo->validLen = 0;

	if(!stCardReaderParam->enablePreParam){
		if(!EXEC_NDK("NDK_IccPowerDown",NDK_IccPowerDown(ICTYPE_IC),NDK_OK,CARDREADER_OPEN)) return NL_FAILED;
	}
	ICReader_Read(stCardReaderParam,cardInfo);

	return NL_OK;
}

int ICReader_Read(void* pstCardReaderParam,void* pstCardInfo)
{
	if(pstCardReaderParam == NULL||pstCardInfo == NULL){
		LOGD_FMT(">>>pstCardReaderParam[%d] pstCardInfo[%d] return.",pstCardReaderParam,pstCardInfo);
		ERRMSG(SDK_ERR_PARAM,CARDREADER_OPEN);
		return NL_FAILED;
	}
	StCardReaderParam* stCardReaderParam = (StCardReaderParam*)pstCardReaderParam;
    StCardInfo* cardInfo = (StCardInfo*)pstCardInfo;
	cardInfo->userInputMode = CARD_IC;
	cardInfo->validLen = 0;

	int nIcState = 0,offset = 0,cardTypeSelf = 0x02;
	if(!EXEC_NDK("NDK_IccDetect",NDK_IccDetect(&nIcState),NDK_OK,CARDREADER_OPEN)) return NL_FAILED;

	if((nIcState&0x01) != 0x01){
		LOGE_FMT("IC card status[%d] error.",nIcState);
		return NL_FAILED;
	}
	offset = 0;
	nlMpos_Command.mpos_setvar(cardInfo->data+offset, cardTypeSelf, _VAR_BIT8);offset +=1;
	nlMpos_Command.mpos_writelen(cardInfo->data+offset, 0, _VAR_BIT16);offset += 2;
	nlMpos_Command.mpos_writelen(cardInfo->data+offset, 0, _VAR_BIT16);offset += 2;
	memset(cardInfo->data+offset,0,1);offset +=1;	
	cardInfo->validLen = offset;
    stCardReaderParam->targetCardType = OPENCARD_TYPE_IC;
    return NL_OK;
}


int ICReader_Close(void* pstCardReaderParam)
{
    if(((StCardReaderParam*)pstCardReaderParam)->targetCardType == OPENCARD_TYPE_IC){
        return NL_OK;
    }
	if(EXEC_NDK("NDK_IccPowerDown",NDK_IccPowerDown(ICTYPE_IC),NDK_OK,CARDREADER_OPEN)){
		LOGD_FMT("Close ic card successfully.");
	}else {
		LOGD_FMT("Close ic card failed.");
		return NL_FAILED;
	}
    return NL_OK;
}

int ICReader_Resume(void* pstCardReaderParam)
{
    return NL_OK;
}
