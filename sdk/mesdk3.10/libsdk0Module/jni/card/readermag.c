#include <string.h>
#include "mag.h"
#include "ndk.h"
#include "readermag.h"
#include "cardmgr.h"
#include "log.h"

extern uchar g_ucMagSwiped;
extern char g_szPanCode[32];
extern char g_szTrack1[128];
extern char g_szTrack2[200];
extern char g_szTrack3[200];

#define TRACKNUM    		3
#define BUFMAXLEN   		128

int MagReader_Open(void* pstCardReaderParam,void* pstCardInfo)
{	
	if(pstCardReaderParam == NULL||pstCardInfo == NULL){
		LOGD_FMT(">>>pstCardReaderParam[%d] pstCardInfo[%d] return.",pstCardReaderParam,pstCardInfo);
		ERRMSG(SDK_ERR_PARAM,CARDREADER_OPEN);
		return NL_FAILED;
	}
	StCardReaderParam* stCardReaderParam = (StCardReaderParam*)pstCardReaderParam;
    StCardInfo* cardInfo = (StCardInfo*)pstCardInfo;
	cardInfo->validLen = 0;
	
	EXEC_NDK("NDK_MagClose",NDK_MagClose(),NDK_OK,CARDREADER_OPEN);
	EXEC_NDK("NDK_MagOpen",NDK_MagOpen(),NDK_OK,CARDREADER_OPEN);

    return NL_OK;
}

int MagReader_Read(void* pstCardReaderParam,void* pstCardInfo)
{
	if(pstCardReaderParam == NULL||pstCardInfo == NULL){
		LOGD_FMT(">>>pstCardReaderParam[%d] pstCardInfo[%d] return.",pstCardReaderParam,pstCardInfo);
		ERRMSG(SDK_ERR_PARAM,CARDREADER_OPEN);
		return NL_FAILED;
	}
	StCardReaderParam* stCardReaderParam = (StCardReaderParam*)pstCardReaderParam;
    StCardInfo* cardInfo = (StCardInfo*)pstCardInfo;
	cardInfo->userInputMode = CARD_MAG;
	cardInfo->validLen = 0;
	if(!EXEC_NDK("NDK_MagSwiped",NDK_MagSwiped(&g_ucMagSwiped),NDK_OK,CARDREADER_OPEN)){
		return NL_FAILED;
	}
	LOGD_FMT("NDK_MagSwiped flag[%d]",g_ucMagSwiped);
    if(g_ucMagSwiped != 1){
		LOGD_FMT(">>> MagSwiped!=1 return failed [%d]",g_ucMagSwiped);
		return NL_FAILED;
	}
    int tk2Validity = stCardReaderParam->tk2Validity;
    int stripeflag = (0x00F0 & tk2Validity) >> 4,nErrorCode=0,nSwipedState,i=0,type = CARD_MAG,nReadNum = 0,offset; 
	char *point = NULL, pchTk[TRACKNUM][BUFMAXLEN];
	memset(g_szTrack1, 0x00, sizeof(g_szTrack1));
	memset(g_szTrack2, 0x00, sizeof(g_szTrack2));
	memset(g_szTrack3, 0x00, sizeof(g_szTrack3));

	if(!EXEC_NDK("NDK_MagReadNormal", NDK_MagReadNormal(g_szTrack1, g_szTrack2, g_szTrack3, &nErrorCode),NDK_OK,CARDREADER_OPEN)){
		return NL_MAGREAD_FAIL;
	}
	LOGD_FMT("NDK_MagReadNormal tk2Validity[%d] stripeflag[%d] nErrorCode[0x%02x]",tk2Validity,stripeflag,nErrorCode);
	LOGD_STR("Track1",g_szTrack1,strlen(g_szTrack1));
	LOGD_STR("Track2",g_szTrack2,strlen(g_szTrack2));
	LOGD_STR("Track3",g_szTrack3,strlen(g_szTrack3));

	if((tk2Validity&0x0F)==0x0){//执行条件:CheckUnionCard为false不校验,低4位为0
		//默认是true,低4位为F,不会执行;
		int lrcMode = stripeflag;
		LOGE_FMT("lrcMode[%2x] nErrorCode[%2x]",lrcMode,nErrorCode);
		if((((lrcMode&TK1)==TK1) && ((nErrorCode & MAGCARD_TK1_LRC_ERR) == MAGCARD_TK1_LRC_ERR))||
		   (((lrcMode&TK2)==TK2) && ((nErrorCode & MAGCARD_TK2_LRC_ERR) == MAGCARD_TK2_LRC_ERR))||
		   (((lrcMode&TK3)==TK3) && ((nErrorCode & MAGCARD_TK3_LRC_ERR) == MAGCARD_TK3_LRC_ERR))){
			g_ucMagSwiped = 0;
			nSwipedState = 0x11;
			LOGE_FMT("LRC fail.");
		} else{
			g_ucMagSwiped = 1;
			nSwipedState = 0x1;
			LOGE_FMT("LRC succ.");
		}
		goto ON_ACK;
	}
	/**if (stripeflag == 0 && nErrorCode != 0) {
		 nSwipedState = 0x11;
		 goto ON_ACK;
    }*/
	if(0 == tk2Validity){
		 LOGD_FMT("tk2Validity==0 [%d]",tk2Validity);
		 if((0x7E == g_szTrack2[0]) || (0x7F == g_szTrack2[0]) ){
			 g_ucMagSwiped = 0;
			 nSwipedState = 0x11;
			 goto ON_ACK;
		 }
		 nSwipedState=0x01; 
		 point = strchr((char*)(g_szTrack2), '=');
		 if(point){
			memset(g_szPanCode,0x00,sizeof(g_szPanCode));
			memcpy(g_szPanCode, g_szTrack2, (point- g_szTrack2));
			LOGD_FMT("card No. len[%d]",point - g_szTrack2);
			LOGD_STR("card No.",g_szPanCode,point - g_szTrack2);
		 }
		 goto ON_ACK;
	}else if(1 == tk2Validity){			 
		 LOGD_FMT("tk2Validity==1 [%d]",tk2Validity);
		 if((0x7E == g_szTrack2[0]) || (0x7F == g_szTrack2[0]) || (NULL == strchr(g_szTrack2, '='))){
			 g_ucMagSwiped = 0;
			 nSwipedState = 0x11;
			 goto ON_ACK;
		 }
		 nSwipedState=0x01; 
		 point = strchr((char*)(g_szTrack2), '=');
		 if(point){
			 memset(g_szPanCode,0x00,sizeof(g_szPanCode));
			 memcpy(g_szPanCode, g_szTrack2, (point- g_szTrack2));
			 LOGD_FMT("card No. len[%d]",point - g_szTrack2);
			 LOGD_STR("card No.",g_szPanCode,point - g_szTrack2);
		 }
		 goto ON_ACK;
	}else if(stripeflag != 0){
		 LOGD_FMT(">>>stripeflag[%d]",stripeflag);
		 memset(pchTk, 0, sizeof(pchTk));
		 memcpy(pchTk[0], g_szTrack1, (BUFMAXLEN > strlen(g_szTrack1) ? strlen(g_szTrack1): BUFMAXLEN));
		 memcpy(pchTk[1], g_szTrack2, (BUFMAXLEN > strlen(g_szTrack2) ? strlen(g_szTrack2): BUFMAXLEN));
		 memcpy(pchTk[2], g_szTrack3, (BUFMAXLEN > strlen(g_szTrack3) ? strlen(g_szTrack3): BUFMAXLEN));
		 int nRet = ProDealWithTKData(&stripeflag, pchTk, nErrorCode, &nReadNum);
		 LOGD_FMT("ProDealWithTKData format nRet[%d]",nRet);
		 if(0 != nRet){
		 	 nSwipedState = 0x11;
		 	 goto ON_ACK;
		 }
		 if(0 == nRet){
			 LOGD_FMT("before format g_szTrack1[%s] g_szTrack2[%s] g_szTrack3[%s]",g_szTrack1,g_szTrack2,g_szTrack3);
			 LOGD_FMT("after  format pchTk1[%s] pchTk2[%s] pchTk3[%s]",pchTk[0],pchTk[1],pchTk[2]);
			 nRet = ProJudgeResult(g_szTrack1, g_szTrack2, g_szTrack3, pchTk, stripeflag);
			 LOGD_FMT("ProJudgeResult check len and nodata nRet[%d]",nRet);
			 if( nRet != 0 ){
				 nSwipedState = 0x11;
				 goto ON_ACK;
			 }
			 LOGD_FMT(">>>check1");
			 memset(g_szPanCode,0x00,sizeof(g_szPanCode));
			 if (0 != g_szTrack2[0]){
				
				for (i = 0; i < 37; i++){
					if ('=' == g_szTrack2[i]){
						LOGD_FMT(">>>check2");
						break;
					}
				}
				if (37 == i){
					 nSwipedState = 0x11;
					 LOGD_FMT(">>>check3");
					 goto ON_ACK;
				}
				if (i > 19){
					 memcpy(g_szPanCode, g_szTrack2, 19);
				}else{
					 memcpy(g_szPanCode, g_szTrack2, i);
				}
				nSwipedState = 0x1;
				LOGD_FMT(">>>check4");
				goto ON_ACK;
			 }else if (0 != g_szTrack3[0]){
				for (i = 0; i < 104; i++){
					 if ('=' == g_szTrack3[i]){
						 LOGD_FMT(">>>check5");
						 break;
					 }
				}
				if (104 == i){
					 nSwipedState = 0x11;
					 LOGD_FMT(">>>check6");
					 goto ON_ACK;
				}
				
				if (i > 19){
					 memcpy(g_szPanCode, g_szTrack3, 19);
				}else{
					 memcpy(g_szPanCode, g_szTrack3, i);
				}
				nSwipedState = 0x1;
				LOGD_FMT(">>>check7");
				goto ON_ACK;
			}else{
				 LOGD_FMT(">>>check8");
				 nSwipedState = 0x11;
				 goto ON_ACK;
			 }
		}
	}	
ON_ACK:
	offset = 0;
	memcpy(cardInfo->data,&type,1);offset += 1;
	nlMpos_Command.mpos_writelen(cardInfo->data+offset,1, _VAR_BIT16);offset +=2;
	nlMpos_Command.mpos_setvar(cardInfo->data+offset,nSwipedState, _VAR_BIT8);offset +=1;
	nlMpos_Command.mpos_writelen(cardInfo->data+offset, 0, _VAR_BIT16);offset += 2;
	memset(cardInfo->data+offset,0,1);offset++;
	cardInfo->validLen = offset;
	stCardReaderParam->targetCardType = OPENCARD_TYPE_MAG;
	return NL_OK;
}

int MagReader_Close(void* pstCardReaderParam)
{
	if(EXEC_NDK("NDK_MagClose",NDK_MagClose(),NDK_OK,CARDREADER_OPEN)){
		LOGD_FMT("Close mag card successfully.");
	}else{	
		LOGD_FMT("Close mag card failed.");
		return NL_FAILED;
	}
    return NL_OK;
}

int MagReader_Resume(void* pstCardReaderParam)
{
    return NL_OK;
}
