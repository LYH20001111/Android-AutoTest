
#include <stddef.h>
#include <string.h>
#include "ndk.h"
#include "log.h"
#include "readeric.h"
#include "cardmgr.h"
#include "card.h"


int PubGetICStatus(char *pcStatus){
	int nStatus = 0;
	int nRet = 0;

	nRet = NDK_IccDetect(&nStatus);
	if( nRet!=NDK_OK ){
		LOGD_FMT("NDK_IccDetect, nRet=%d", nRet);
		return nRet;
	}

	nRet = (nStatus&0x0F);
	switch(nRet){
		case IC1_EXIST:
		case IC1_POWERON:
		case IC1_EXIST | IC1_POWERON:
		case IC2_EXIST:
		case IC1_EXIST | IC2_EXIST:
		case IC1_POWERON | IC2_EXIST:
		case IC1_EXIST | IC1_POWERON | IC2_EXIST:
		case IC2_POWERON:
		case IC1_EXIST | IC2_POWERON:
		case IC2_POWERON | IC1_POWERON:
		case IC1_EXIST | IC1_POWERON | IC2_POWERON:
		case IC2_EXIST | IC2_POWERON:
		case IC1_EXIST | IC2_EXIST | IC2_POWERON:
		case IC1_POWERON | IC2_EXIST | IC2_POWERON:
		case IC1_EXIST | IC1_POWERON | IC2_EXIST |IC2_POWERON:
		case SIM1_POWERON:
//		case SIM2_POWERON:
//		case SIM3_POWERON:
//		case SIM4_POWERON:
			*pcStatus = nRet;
			return SUCC;
		default:
			LOGD_FMT("NDK_IccDetect, nStatus=0x%02X", nStatus);
			return FAIL;
	}
}

