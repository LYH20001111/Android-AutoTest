/**
 * Author by wuhh, Date on 2019/4/17 0022.
 */
#include <unistd.h>
#include <comm.h>
#include <rfid.h>
#include <memory.h>
#include "threadtool.h"
#include "ndk.h"
#include "rfid.h"
#include "cardmgr.h"
#include "nllogger.h"
#include "event.h"
#include "comm.h"
#include "log.h"
#include "api.h"
#include "readerrfid.h"

extern int g_readCardMode;
extern int g_aCardAtq;
extern int g_rfMultiLevel;
static int g_rfidPowerOnCancelFlag = 0;
static int g_powerOnFlag = 0;
static void __setPowerOnCancelFlag(int flag)
{
	THREAD_MUTEX_LOCK(THREAD_MUTEX_INDEX_POWERON_CANCEL);
	g_rfidPowerOnCancelFlag = flag;
	THREAD_MUTEX_UNLOCK(THREAD_MUTEX_INDEX_POWERON_CANCEL);
}

static int __getPowerOnCancelFlag()
{
	int flag = 0;
	THREAD_MUTEX_LOCK(THREAD_MUTEX_INDEX_POWERON_CANCEL);
	flag = g_rfidPowerOnCancelFlag;
	THREAD_MUTEX_UNLOCK(THREAD_MUTEX_INDEX_POWERON_CANCEL);
	return flag;
}

int g_mifareCardType = MIFARECARD_M0;//0=M1

static void msleep(int ms)
{
	usleep(ms*1000);
}

static long __GetTimeDistance(struct timeval *startTime)
{
	LOGE_FMT("startTime[%d]",startTime);
	struct timeval currTime;
	gettimeofday(&currTime,NULL);
	long disms = (currTime.tv_sec-startTime->tv_sec)*1000+(currTime.tv_usec-startTime->tv_usec)/1000;
	LOGE_FMT(">>>disms[%d]",disms);
	return disms;
}

static int Rfid_getStRFPowerOnParam(StRFPowerUpParam* param,unsigned char*pbuf,int buf_len)
{
	if(param == NULL){
		return ACK_ERR;
	}
	int offset = 0;
	memset(param, 0, sizeof(StRFPowerUpParam));
	param->rfCardType = nlMpos_Command.mpos_getvar(pbuf+offset, _VAR_BIT16);
	param->rfCardType = nlMpos_Command.mpos_endian_swab16(param->rfCardType);offset += 2;
	if(param->rfCardType <= 0){
		LOGE_FMT("rfCardType[%d]",param->rfCardType);
		return ACK_ERR;
	}
	param->timeOut = nlMpos_Command.mpos_getvar(pbuf+offset, _VAR_BIT16);
	param->timeOut = nlMpos_Command.mpos_endian_swab16(param->timeOut);offset += 2;

	param->sak = nlMpos_Command.mpos_getvar(pbuf+offset, _VAR_BIT8);offset += 1;

	param->felicaParamLen = nlMpos_Command.mpos_readlen(pbuf+offset, _VAR_BIT16);offset += 2;
	param->felicaParam = pbuf+offset;offset += param->felicaParamLen;

 	if(param->felicaParamLen!=0 && param->felicaParamLen % 4 !=0){
		LOGE_FMT("felicaParamLen[%d]",param->felicaParamLen);
		return ACK_ERR;
	}
	if(param->rfCardType == RFID_FELICA && param->felicaParamLen == 0){
		LOGE_FMT("rfCardType[%d] felicaParamLen[%d]",param->rfCardType,param->felicaParamLen);
		return ACK_ERR;
	}
	gettimeofday(&param->startTime,NULL);
	LOGD_FMT(">>>rfCardType[0x%x] timeOut[%d] sak[0x%x] felicaParamLen[%d]",\
	param->rfCardType,param->timeOut,param->sak,param->felicaParamLen);
	LOGD_STR("felicaParam",param->felicaParam,param->felicaParamLen);
	return ACK_OK;
}

static int Rfid_ReaderOpen(void *pstRfidPowerUpParam,void *pstCardInfo)
{
	if(pstRfidPowerUpParam == NULL||pstCardInfo == NULL){
		LOGD_FMT(">>>pstRfidPowerUpParam[%d] pstCardInfo[%d] return.",pstRfidPowerUpParam,pstCardInfo);
		ERRMSG(SDK_ERR_PARAM,RFID_POWERON);
		return NL_FAILED;
	}
	StRFPowerUpParam *pRfidPowerUpParam = (StRFPowerUpParam *)pstRfidPowerUpParam;
	StCardInfo* cardInfo = (StCardInfo*)pstCardInfo;
	if(!EXEC_NDK("NDK_RfidInit", NDK_RfidInit(NULL), NDK_OK,RFID_POWERON)){
		return NL_FAILED;
	}
	return NL_OK;
}

static int Rfid_ReaderResume(void *pstRfidPowerUpParam)
{
	if(pstRfidPowerUpParam == NULL){
		LOGD_FMT(">>>pstRfidPowerUpParam[%d] return.",pstRfidPowerUpParam);
		ERRMSG(SDK_ERR_PARAM,RFID_POWERON);
		return NL_FAILED;
	}
	StRFPowerUpParam *pRfidPowerUpParam = (StRFPowerUpParam *)pstRfidPowerUpParam;
	if(!EXEC_NDK("NDK_RfidPiccDeactivate(10)", NDK_RfidPiccDeactivate(10), NDK_OK,RFID_POWERON)){
		return NL_FAILED;
	}
//	msleep(100);
	int rfCardType = pRfidPowerUpParam->rfCardType;
    int hasACard = 0,hasBCard = 0,hasFCard = 0;
	if(HAS_RFID_A(rfCardType)||HAS_RFID_M1(rfCardType)||HAS_RFID_M0(rfCardType)){
        hasACard = 1;
    }
    if(HAS_RFID_B(rfCardType)){
        hasBCard = 1;
    }
    if(HAS_RFID_F(rfCardType)){
        hasFCard = 1;
    }

    uchar ucPicctype = NDK_RFID_ABF;
	if(hasACard == 1 && hasBCard == 0 && hasFCard == 0){
		ucPicctype = NDK_RFID_A;
        LOGD_FMT(">>>NDK_RFID_A");
	}
    if(hasACard == 0 && hasBCard == 1 && hasFCard == 0){
        ucPicctype = NDK_RFID_B;
        LOGD_FMT(">>>NDK_RFID_B");
    }
    if(hasACard == 0 && hasBCard == 0 && hasFCard == 1){
        ucPicctype = NDK_RFID_F;
        LOGD_FMT(">>>NDK_RFID_F");
    }

    if(hasACard == 1 && hasBCard == 1 && hasFCard == 0){
        ucPicctype = NDK_RFID_AB;
        LOGD_FMT(">>>NDK_RFID_AB");
    }
    if(hasACard == 1 && hasBCard == 0 && hasFCard == 1){
        ucPicctype = NDK_RFID_AF;
        LOGD_FMT(">>>NDK_RFID_AF");
    }
    if(hasACard == 0 && hasBCard == 1 && hasFCard == 1){
        ucPicctype = NDK_RFID_BF;
        LOGD_FMT(">>>NDK_RFID_BF");
    }

    if(hasACard == 1 && hasBCard == 1 && hasFCard == 1){
        ucPicctype = NDK_RFID_ABF;
        LOGD_FMT(">>>NDK_RFID_ABF");
    }
	LOGD_FMT(">>>ucPicctype[0x%x]",ucPicctype);
	if(!EXEC_NDK("NDK_RfidPiccType", NDK_RfidPiccType(ucPicctype), NDK_OK,RFID_POWERON))
		return NL_FAILED;
	return NL_OK;
}

//为什么放这里,这是个谜......
//概率性,'stack corruption detected',方不慌.
static int offset = 0;
static int atqLen=0,snrLen=0,iRet = -1,atsLen = 0;
static uchar picctype=0,atq[16],snr[64],atsbuf[32];
static int cardType = -1;
static int uidLen = 0,ret = 0;
static uchar uid[64],sak[2];
static int UIDlen = 0;
static uchar selCmd[3] = {0x93, 0x95, 0x97},UID[64];
static felica_param_t felicaParam;
static int IDmPMmlen=0;
static uchar IDmPMm[512];

static int Rfid_ReaderRead(void *pstRfidPowerUpParam,void *pstCardInfo)
{
	if(pstRfidPowerUpParam == NULL||pstCardInfo == NULL){
		LOGD_FMT(">>>pstRfidPowerUpParam[%d] pstCardInfo[%d] return.",pstRfidPowerUpParam,pstCardInfo);
		ERRMSG(SDK_ERR_PARAM,RFID_POWERON);
		return NL_FAILED;
	}
	StRFPowerUpParam *pRfidPowerUpParam = (StRFPowerUpParam *)pstRfidPowerUpParam;
	StCardInfo* cardInfo = (StCardInfo*)pstCardInfo;
	
	int rfCardType = pRfidPowerUpParam->rfCardType,timeUse=0,timeOutMs = pRfidPowerUpParam->timeOut*1000;

	memset(cardInfo,0,sizeof(pstCardInfo));

	int targetSak = pRfidPowerUpParam->sak;

	int i=0,j=0,count = pRfidPowerUpParam->felicaParamLen/4;

START_AB:
	if(HAS_RFID_A(rfCardType)||HAS_RFID_B(rfCardType)){
		LOGD_FMT("START_AB");
		atqLen=0,snrLen=0,iRet = -1,atsLen = 0,picctype=0;
		memset(atq,0,sizeof(atq));
		memset(snr,0,sizeof(snr));
		memset(atsbuf,0,sizeof(atsbuf));
		LOGD_FMT(">>>NdkIsSupportACardAtq[%d]",g_aCardAtq);
		EXEC_NDK("NDK_RfidPiccType(NDK_RFID_AB)", NDK_RfidPiccType(NDK_RFID_AB), NDK_OK,RFID_POWERON);
		if(g_aCardAtq == 1){
			EXEC_NDK("NDK_RfidPiccDetect_Atq", NDK_RfidPiccDetect_Atq(&picctype,&atqLen,atq), NDK_OK,RFID_POWERON);
		}else{
			EXEC_NDK("NDK_RfidPiccDetect", NDK_RfidPiccDetect(&picctype), NDK_OK,RFID_POWERON);
			atqLen = 0;
		}
		if(!EXEC_NDK("NDK_RfidPiccActivate", iRet = NDK_RfidPiccActivate(&picctype, &snrLen, snr), NDK_OK,RFID_POWERON)){
			if(iRet == NDK_ERR_RFID_AANTI){
				offset = 0;
				memcpy(cardInfo->data,CMD_RFID_AANTI,2);offset +=2;
				cardInfo->validLen = offset;		
				LOGD_FMT(">>>NDK_ERR_RFID_AANTI...!!!");
				return NL_ERR_ACK;
			}
			goto END_AB;
		}else{
			LOGD_FMT(">>>NDK_RfidPiccActivate picctype[0x%02x]",picctype);
			if(picctype != NDK_RFID_A && picctype != NDK_RFID_B){
				offset = 0;
				memcpy(cardInfo->data,CMD_ERR_OTHER,2);offset +=2;
				cardInfo->validLen = offset;
				return NL_ERR_ACK;
			}
			if(picctype == NDK_RFID_A && !HAS_RFID_A(rfCardType)){
				goto END_AB;
			}
            if(pRfidPowerUpParam->sak != 0xFF && 0x20 !=pRfidPowerUpParam->sak){
            	offset = 0;
                memcpy(cardInfo->data,CMD_ERR_OTHER,2);offset +=2;
                cardInfo->validLen = offset;
                return NL_ERR_ACK;
            }
            offset = 0,cardType = -1;
			if(picctype == NDK_RFID_A){
//				EXEC_NDK("NDK_RfidTypeARats",NDK_RfidTypeARats(0, &atsLen, atsbuf),NDK_OK,RFID_POWERON);
				cardType = RFID_A;
				pRfidPowerUpParam->targetCard = cardType;
				cardType = nlMpos_Command.mpos_endian_swab16(cardType);
				memcpy(cardInfo->data,&cardType,2);offset +=2;
				nlMpos_Command.mpos_writelen(cardInfo->data+offset, snrLen, _VAR_BIT16);offset +=2;
				memcpy(cardInfo->data+offset,snr,snrLen);offset+=snrLen;//snr
				if(atqLen != 0){
					LOGD_STR("Type-A Atq",atq,2);
					memcpy(cardInfo->data+offset,atq,2);//atq
				}else{
					memcpy(cardInfo->data+offset,"\xFF\xFF",2);//atq
				}
				offset += 2;
				memcpy(cardInfo->data+offset,"\x20",1);offset += 1;//sak
				nlMpos_Command.mpos_writelen(cardInfo->data+offset, 0, _VAR_BIT16);offset +=2;//felica
				if(atsLen != 0){
					nlMpos_Command.mpos_writelen(cardInfo->data+offset, atsLen, _VAR_BIT16);offset +=2;//ats
					memcpy(cardInfo->data+offset,atsbuf,atsLen);offset +=atsLen;
				}else{
					nlMpos_Command.mpos_writelen(cardInfo->data+offset, 0, _VAR_BIT16);offset +=2;//ats
				}
				cardInfo->validLen = offset;
				return NL_OK;
			}else if(picctype == NDK_RFID_B){
				cardType = RFID_B;
				pRfidPowerUpParam->targetCard = cardType;
				cardType = nlMpos_Command.mpos_endian_swab16(cardType);
				memcpy(cardInfo->data,&cardType,2);offset +=2;
				nlMpos_Command.mpos_writelen(cardInfo->data+offset, snrLen, _VAR_BIT16);offset +=2;
				memcpy(cardInfo->data+offset,snr,snrLen);offset+=snrLen;//snr
				memcpy(cardInfo->data+offset,"\xFF\xFF",2);offset += 2;//atq
				memcpy(cardInfo->data+offset,"\xFF",1);offset += 1;//sak
				nlMpos_Command.mpos_writelen(cardInfo->data+offset, 0, _VAR_BIT16);offset +=2;//felica
				nlMpos_Command.mpos_writelen(cardInfo->data+offset, 0, _VAR_BIT16);offset +=2;//ats
				cardInfo->validLen = offset;
				return NL_OK;
			}else{
				goto END_AB;
			}
		}

	}
END_AB:

START_M1:
	if(HAS_RFID_M1(rfCardType)||HAS_RFID_M0(rfCardType)){
		LOGD_FMT("START_M1..");
		atqLen = 0,uidLen = 0,iRet = -1,ret = 0;
		memset(atq,0,sizeof(atq));
		memset(uid,0,sizeof(uid));
		memset(sak,0,sizeof(sak));
        #if 0
		if(rfCardType == RFID_M0){
			int i=0,count = 6;
			for(i = 0; i < count; i++){
				LOGD_FMT(">>>M0 count[%d]",i);
				int ret = -1;
				if(EXEC_NDK("NDK_MifareActive1", ret = NDK_MifareActive(0x52, uid,&uidLen,sak), NDK_OK,RFID_POWERON)){
					if(pRfidPowerUpParam->sak != 0xFF && sak[0] !=pRfidPowerUpParam->sak){
						int offset = 0;
						memcpy(cardInfo->data,CMD_ERR_OTHER,2);offset +=2;
						cardInfo->validLen = offset;
						return NL_ERR_ACK;
					}
					LOGD_FMT(">>>NDK_MifareActive sak[%d]",sak[0]);
					int offset = 0,cardType = RFID_M0;
					if(sak[0] != 0x00){
						cardType = RFID_M1;
					}
					if(!(cardType & pRfidPowerUpParam->rfCardType)){
                   		goto END_M1;
                	}
					pRfidPowerUpParam->targetCard = cardType;
					cardType = nlMpos_Command.mpos_endian_swab16(cardType);
					memcpy(cardInfo->data+offset,&cardType,2);offset +=2;
					nlMpos_Command.mpos_writelen(cardInfo->data+offset, uidLen, _VAR_BIT16);offset+=2;
					memcpy(cardInfo->data+offset,uid,uidLen);offset+=uidLen;
					memcpy(cardInfo->data+offset,"\xFF\xFF",2);offset += 2;//atq
					memcpy(cardInfo->data+offset,sak,1);offset+=1;
					nlMpos_Command.mpos_writelen(cardInfo->data+offset, 0, _VAR_BIT16);offset +=2;//felica
					nlMpos_Command.mpos_writelen(cardInfo->data+offset, 0, _VAR_BIT16);offset +=2;//ats
					cardInfo->validLen = offset;
					return NL_OK;
				}
				if(ret == SDK_ERR_NDK_NOT_SUPPORT){
					break;
				}
			}
		}
        #endif

		EXEC_NDK("NDK_RfidPiccType(NDK_RFID_A)", NDK_RfidPiccType(NDK_RFID_A), NDK_OK,RFID_POWERON);

		LOGD_FMT("NdkIsSupportRfMultiLevel[%d]",g_rfMultiLevel);
		if(g_rfMultiLevel == 0){
			if(!EXEC_NDK("NDK_M1Request", ret = NDK_M1Request(0x52, &atqLen, atq), NDK_OK,RFID_POWERON)){
				if(ret == NDK_ERR_IOCTL){//兼容旧固件
                    EXEC_NDK("NDK_RfidPiccDeactivate",NDK_RfidPiccDeactivate(10), NDK_OK,RFID_POWERON);
                    usleep(150000);
					if(!EXEC_NDK("NDK_M1Request", NDK_M1Request(0x52, &atqLen, atq), NDK_OK,RFID_POWERON)){
						goto END_M1;
					}
				}else{
					goto END_M1;
				}
			}
			if(!EXEC_NDK("NDK_M1Anti", NDK_M1Anti(&uidLen, uid), NDK_OK,RFID_POWERON)){
				goto END_M1;
			}
			if(EXEC_NDK("NDK_M1Select", NDK_M1Select(uidLen, uid, sak), NDK_OK,RFID_POWERON)){
				LOGD_FMT(">>>Level sak[0x%x]",sak[0]);
				if(sak[0] == 0x20){
					goto END_M1;
				}
				if(targetSak != 0xff && targetSak != sak[0]){
				    offset = 0;
					memcpy(cardInfo->data,CMD_ERR_OTHER,2);offset +=2;
					cardInfo->validLen = offset;
					return NL_ERR_ACK;
				}
				offset = 0,cardType = RFID_M1;
				if(sak[0] == 0x00){
					cardType = RFID_M0;
				}
                if(!(cardType & pRfidPowerUpParam->rfCardType)){
                    goto END_M1;
                }
				pRfidPowerUpParam->targetCard = cardType;
				cardType = nlMpos_Command.mpos_endian_swab16(cardType);
				memcpy(cardInfo->data+offset,&cardType,2);offset +=2;
				nlMpos_Command.mpos_writelen(cardInfo->data+offset, uidLen, _VAR_BIT16);offset+=2;
				memcpy(cardInfo->data+offset,uid,uidLen);offset+=uidLen;
				memcpy(cardInfo->data+offset,atq,2);offset+=2;
				memcpy(cardInfo->data+offset,sak,1);offset+=1;
				nlMpos_Command.mpos_writelen(cardInfo->data+offset, 0, _VAR_BIT16);offset +=2;//felica
				nlMpos_Command.mpos_writelen(cardInfo->data+offset, 0, _VAR_BIT16);offset +=2;//ats
				cardInfo->validLen = offset;
				return NL_OK;
			}
		}
		
		if(g_rfMultiLevel == 1){
			atqLen = 0,UIDlen = 0,snrLen=0,i=0,ret = 0;
			memset(atq,0,sizeof(atq));
			memset(snr,0,sizeof(snr));
			memset(sak,0,sizeof(sak));
			memset(UID,0,sizeof(UID));
			if(!EXEC_NDK("NDK_M1Request", ret = NDK_M1Request(0x52, &atqLen, atq), NDK_OK,RFID_POWERON)){
				LOGE_FMT("NDK_M1Request ret[%d]",ret);
				if(ret == NDK_ERR_IOCTL){//兼容旧固件
                    EXEC_NDK("NDK_RfidPiccDeactivate",NDK_RfidPiccDeactivate(10), NDK_OK,RFID_POWERON);
                    usleep(150000);
					if(!EXEC_NDK("NDK_M1Request", NDK_M1Request(0x52, &atqLen, atq), NDK_OK,RFID_POWERON)){
						goto END_M1;
					}
				}else{
					LOGE_FMT("END_M1");
					goto END_M1;
				}
			}
			UIDlen = (atq[0] >> 6) + 1;
			do{
				if(!EXEC_NDK("NDK_M1Anti_SEL", NDK_M1Anti_SEL(selCmd[i], &snrLen, snr), NDK_OK,RFID_POWERON)){
					goto END_M1;
				}
				if(!EXEC_NDK("NDK_M1Select_SEL", NDK_M1Select_SEL(selCmd[i], snrLen, snr, sak), NDK_OK,RFID_POWERON)){
					goto END_M1;
				}
				memcpy((UID + (i << 2)), snr, 4);
				//LOGD_FMT("selCmd[%d][%d] *sak[%d]",i,selCmd[i],*sak);
				if((*sak & 0x04) == 0x00) break;	//UID transfer complete
			}while(i++ < 3);
			
			if((UIDlen == 1) && (UID[0] == 0x88)){
				LOGD_FMT("UID ERR 1");
				goto END_M1;
			}
			if((UIDlen == 2) && (UID[4] == 0x88)){
				LOGD_FMT("UID ERR 2");
				goto END_M1;
			}
			switch(UIDlen){
				case 1:
					UIDlen = 4;
                    break;
				case 2:
					UIDlen = 7;
					memcpy(UID,&UID[1],3);
                    memcpy(&(UID[3]),&UID[4],4);
                    break;
				case 3:
					UIDlen = 10;
					memcpy(UID,&UID[1],3);
                    memcpy(&(UID[3]),&UID[5],3);
					memcpy(&(UID[6]),&UID[8],4);
                    break;
				default:
					UIDlen = 0;
					UID[0] = 0x00;
					goto END_M1;
			}
			LOGD_FMT(">>>MultiLevel sak[0x%x]",sak[0]);
			LOGD_STR("snr",UID,UIDlen);
			offset = 0,cardType = RFID_M1;
			if(sak[0]==0x20){
				goto END_M1;
			}
			if(targetSak != 0xff && targetSak != sak[0]){
				offset = 0;
				memcpy(cardInfo->data,CMD_ERR_OTHER,2);offset +=2;
				cardInfo->validLen = offset;
				return NL_ERR_ACK;
			}
			if(sak[0] == 0x00){
				cardType = RFID_M0;
			}
            if(!(cardType & pRfidPowerUpParam->rfCardType)){
                goto END_M1;
            }
			pRfidPowerUpParam->targetCard = cardType;
			cardType = nlMpos_Command.mpos_endian_swab16(cardType);
			memcpy(cardInfo->data+offset,&cardType,2);offset +=2;
			nlMpos_Command.mpos_writelen(cardInfo->data+offset, UIDlen, _VAR_BIT16);offset+=2;
			memcpy(cardInfo->data+offset,UID,UIDlen);offset+=UIDlen;
			memcpy(cardInfo->data+offset,atq,atqLen);offset+=atqLen;
			memcpy(cardInfo->data+offset,sak,1);offset+=1;
			nlMpos_Command.mpos_writelen(cardInfo->data+offset, 0, _VAR_BIT16);offset +=2;//felica
			nlMpos_Command.mpos_writelen(cardInfo->data+offset, 0, _VAR_BIT16);offset +=2;//ats
			cardInfo->validLen = offset;
			return NL_OK;
		}
	}
END_M1:

START_F:
	if(HAS_RFID_F(rfCardType)&&pRfidPowerUpParam->felicaParamLen!=0){
        LOGD_FMT("START_F");
		EXEC_NDK("NDK_RfidPiccType(NDK_RFID_F)", NDK_RfidPiccType(NDK_RFID_F), NDK_OK,RFID_POWERON);
		LOGD_STR("felicaParam",pRfidPowerUpParam->felicaParam,pRfidPowerUpParam->felicaParamLen);
		i=0,j=0,count = pRfidPowerUpParam->felicaParamLen/4;
		IDmPMmlen=0;
        memset(IDmPMm,0,sizeof(IDmPMm));
		for(i=0; i< count; i++){
			LOGD_FMT(">>>count[%d]",count);
            memset(&felicaParam,0,sizeof(felica_param_t));
            memcpy(&felicaParam,pRfidPowerUpParam->felicaParam+i*4,4);
            LOGD_STR("felicaParam",&felicaParam,4);
            for(j=0;j<3;j++){
                if(EXEC_NDK("NDK_FelicaPoll", NDK_FelicaPoll(felicaParam,IDmPMm, &IDmPMmlen), NDK_OK,RFID_POWERON)){
                	offset = 0,cardType = RFID_FELICA;
					pRfidPowerUpParam->targetCard = cardType;
					cardType = nlMpos_Command.mpos_endian_swab16(cardType);
                    memcpy(cardInfo->data+offset,&cardType,2);offset +=2;
                    nlMpos_Command.mpos_writelen(cardInfo->data+offset,0, _VAR_BIT16);offset+=2;
                    memcpy(cardInfo->data+offset,"\xFF\xFF",2);offset += 2;//atq
                    memcpy(cardInfo->data+offset,"\xFF",1);offset += 1;//sak
                    nlMpos_Command.mpos_writelen(cardInfo->data+offset,IDmPMmlen, _VAR_BIT16);offset +=2;//felica
                    memcpy(cardInfo->data+offset,IDmPMm,IDmPMmlen);offset +=IDmPMmlen;
                    nlMpos_Command.mpos_writelen(cardInfo->data+offset, 0, _VAR_BIT16);offset +=2;//ats
                    cardInfo->validLen = offset;
                    return NL_OK;
                }
            }
		}
	}
END_F:

	LOGE_FMT("Rfid_ReaderRead return FAILED");
	return NL_FAILED;
}

static int Rfid_ReaderClose(void *pstRfidPowerUpParam)
{
	if(pstRfidPowerUpParam == NULL){
		LOGD_FMT(">>>pstRfidPowerUpParam[%d] return.",pstRfidPowerUpParam);
		ERRMSG(SDK_ERR_PARAM,RFID_POWERON);
		return NL_FAILED;
	}
	StRFPowerUpParam *pRfidPowerUpParam = (StRFPowerUpParam *)pstRfidPowerUpParam;
	LOGD_FMT("PowerOn targetCard[0x%x]",pRfidPowerUpParam->targetCard);
	if(pRfidPowerUpParam->targetCard != RFID_NONE){
		return NL_OK;
	}
	if(EXEC_NDK("NDK_RfidCloseRf",NDK_RfidCloseRf(),NDK_OK,RFID_POWERON)){
		LOGD_FMT("Close rf card successfully./");
	}else {
		LOGD_FMT("Close rf card failed./");
		return NL_FAILED;
	}	
    return NL_OK;
}

int Rfid_CreateCardReader(StCardReader** pCardReader)
{
	*pCardReader = CardMgr_CreateCardReader();
	if(*pCardReader == NULL){
		return NL_FAILED;
	}
	(*pCardReader)->openCardDev  = Rfid_ReaderOpen;
	(*pCardReader)->readCardInfo = Rfid_ReaderRead;
	(*pCardReader)->resumeCardDev= Rfid_ReaderResume;
	(*pCardReader)->closeCardDev = Rfid_ReaderClose;
	return NL_OK;
}

static int __eventCallBackRfid(EM_SYS_EVENT eventNum, int msgLen, char * msg){
	ST_THREAD_COND_MSG condMsg;
	condMsg.cardEvent = eventNum;
	THREAD_COND_SIGNAL(THREAD_COND_INDEX_RFID_POWERON,&condMsg);
	return 0;
}

static int __addRfidEvent(int timeOutMs)
{
	int ret = NDK_ERR;
	if(!EXEC_NDK("NDK_SYS_RegisterEvent",ret = NDK_SYS_RegisterEvent(SYS_EVENT_RFID,timeOutMs,__eventCallBackRfid),NDK_OK,RFID_POWERON)){
		if(ret == NDK_ERR_PARA && timeOutMs == 0){
			if(!EXEC_NDK("NDK_SYS_RegisterEvent2",NDK_SYS_RegisterEvent(SYS_EVENT_RFID,SYS_EVENT_TIMEOUT,__eventCallBackRfid),NDK_OK,RFID_POWERON)){
				return NL_FAILED;
			}
		} else{
			return NL_FAILED;
		}
	}
	LOGE_FMT(">>>rfid event registered successful.");
	return NL_OK;
}
static int __removeRfidEvent()
{
	if(EXEC_NDK("#NDK_SYS_UnRegisterEvent", NDK_SYS_UnRegisterEvent(SYS_EVENT_RFID), NDK_OK,COMMAND_NONE)){
		LOGE_FMT(">>>rfid event unregistered successful.");
		return NL_OK;
	}
	return NL_FAILED;
}


static int __waitRfidEvent(int count,StRFPowerUpParam* stRFPowerUpParam)
{
	int timeOut = stRFPowerUpParam->timeOut,hasEvent = 0;
	
	THREAD_COND_INIT(THREAD_COND_INDEX_RFID_POWERON);

	if((count == 1) && __addRfidEvent(timeOut*1000)!=NL_OK) return NL_FAILED;

	__setPowerOnCancelFlag(0);

	ST_THREAD_COND_MSG msg;
	THREAD_COND_WAIT(THREAD_COND_INDEX_RFID_POWERON,&msg);
	hasEvent = msg.cardEvent;

    int nRet = (hasEvent == SYS_EVENT_NONE)?NL_ERR_TIMEOUT:NL_OK;
	if(__getPowerOnCancelFlag()==1){
		nRet = NL_CANCEL;
	}
	return nRet;
}
static int __rfCardExec(StRFPowerUpParam* stRFPowerUpParam,StCardReader* pstCardReaders,void* pstCardInfoOutput)
{
	if(stRFPowerUpParam==NULL|| pstCardReaders == NULL || pstCardInfoOutput==NULL  ){
		LOGE_FMT(">>>stRFPowerUpParam[%d] pstCardReaders[%d] pstCardInfoOutput[%d] return NL_FAILED!",stRFPowerUpParam,pstCardReaders,pstCardInfoOutput);
		return NL_FAILED;
	}
	if(!LOGGER_IS_EXPECT_RET(pstCardReaders->openCardDev(stRFPowerUpParam,pstCardInfoOutput),NL_OK)){
		return NL_FAILED;
	}
    int count = 0;
ON_START:
    count++;//1 2
	LOGE_FMT(">>>count[%d]",count);
	if(!LOGGER_IS_EXPECT_RET(pstCardReaders->resumeCardDev(stRFPowerUpParam),NL_OK)){
		return NL_FAILED;
	}
	int nRet = __waitRfidEvent(count,stRFPowerUpParam);

	LOGD_FMT(">>>__waitRfidEvent nRet[%d]",nRet);
	if(nRet == NL_ERR_TIMEOUT){
		LOGE_FMT(">>>__waitRfidEvent timeout!");
		return nRet;
	}else if(nRet == NL_CANCEL){
		LOGE_FMT(">>>__waitRfidEvent cancel!");
		return nRet;
	}else if(nRet < 0){
		LOGE_FMT(">>>__waitRfidEvent error! [%d]", nRet);
		return NL_FAILED;
	}

	//stRFPowerUpParam = NULL;
	LOGE_FMT("stRFPowerUpParam[%d] pstCardInfoOutput.[%d]",stRFPowerUpParam,pstCardInfoOutput);

	nRet = pstCardReaders->readCardInfo(stRFPowerUpParam,pstCardInfoOutput);
	LOGE_FMT(">>>readCardInfo nRet[%d]",nRet);
	if(nRet != NL_OK){
		long disTime = __GetTimeDistance(&(stRFPowerUpParam->startTime));
		long timeOutMs = (stRFPowerUpParam->timeOut)*1000;
		LOGE_FMT("disTime[%d] timeOutMs[%d]",disTime,timeOutMs);
		if( disTime < timeOutMs){
			LOGE_FMT(">>>__rfCardExec once again!");
			goto ON_START;
		}else{
			LOGE_FMT(">>>ReadCardInfo timeout!");
			return NL_ERR_TIMEOUT;
		}
	}
	LOGE_FMT(">>>__rfCardExec success!");
	return nRet;

}

static int __rfCardExit(StRFPowerUpParam* stRFPowerUpParam,StCardReader* pstCardReaders)
{
	if(stRFPowerUpParam==NULL || pstCardReaders == NULL){
		LOGE_FMT(">>>stRFPowerUpParam[%d] pstCardReaders[%d]return NL_FAILED!",stRFPowerUpParam,pstCardReaders);
		return NL_FAILED;
	}
	
	pstCardReaders->closeCardDev(stRFPowerUpParam);
	
	LOGGER_IS_EXPECT_RET(CardMgr_ReleaseReader(pstCardReaders),  NL_OK);
	return NL_OK;
}

int Rfid_Cancle()
{
	__setPowerOnCancelFlag(1);
	ST_THREAD_COND_MSG msg;
	msg.cardEvent = SYS_EVENT_NONE;
	THREAD_COND_SIGNAL(THREAD_COND_INDEX_RFID_POWERON,&msg);
	return ACK_OK;
}
int Rfid_PowerOn(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen)
{
	int dataLen;
	char ackCode[2+1];
	memset(ackCode,0,sizeof(ackCode));

	if(CardLock(CARD_RFID,RFID_POWERON)){
		responseCmd(pOut,0,outLen,CMD_ERR_OTHER);
		return 0;
	}
    g_powerOnFlag = 1;
	StRFPowerUpParam stRFPowerUpParam;
	memset(&stRFPowerUpParam,0, sizeof(StRFPowerUpParam));
	int ret = Rfid_getStRFPowerOnParam(&stRFPowerUpParam,pbuf,buf_len);
	if(ret != ACK_OK){
		dataLen =0;
		ERRMSG(SDK_ERR_PARAM,RFID_POWERON);
		memcpy(ackCode,CMD_ERR_OTHER,2);
		goto ON_ERR;
	}
    stRFPowerUpParam.targetCard = RFID_NONE;

	StCardReader* pstCardReaders=NULL;
	ret = Rfid_CreateCardReader(&pstCardReaders);
	if(ret != NL_OK){
		dataLen =0;
		ERRMSG(SDK_ERR_MALLOC_FAILED,RFID_POWERON);
		memcpy(ackCode,CMD_ERR_OTHER,2);
		goto ON_ERR;
	}

	//StCardInfo* pstCardInfo = (StCardInfo*)CardMgr_CreateCardInfo();
    StCardInfo pstCardInfo;
	memset(&pstCardInfo,0,sizeof(pstCardInfo));
	ret =  __rfCardExec(&stRFPowerUpParam,pstCardReaders,&pstCardInfo);

	__removeRfidEvent();


	dataLen = pstCardInfo.validLen;
	if(ret == NL_OK && dataLen != 0){
		memcpy(pOut+2,pstCardInfo.data,dataLen);
		memcpy(ackCode,CMD_OK,2);
	}else if(ret == NL_ERR_ACK && dataLen == 2){
		dataLen =0;
		memcpy(ackCode,pstCardInfo.data,2);
	}else if(ret == NL_ERR_TIMEOUT){
		dataLen =0;
		ERRMSG(SDK_ERR_TIMEOUT,RFID_POWERON);
		memcpy(ackCode,CMD_ERR_TIMEOUT,2);
	}else if(ret == NL_CANCEL){
		dataLen =0;
		ERRMSG(SDK_ERR_CANCEL,RFID_POWERON);
		memcpy(ackCode,CMD_CANCEL,2);
	}else{
		dataLen =0;
		memcpy(ackCode,CMD_ERR_OTHER,2);
	}
ON_ERR:
	__rfCardExit(&stRFPowerUpParam,pstCardReaders);
	responseCmd(pOut,dataLen,outLen,ackCode);
	CardUnLock(CARD_RFID);
    THREAD_COND_SIGNAL(THREAD_COND_INDEX_RFID_POWEROFF,NULL);
    g_powerOnFlag = 0;
    return 0;
}

int Rfid_Apdu(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {

    int offset = 0,extoffset=2,recvLen=0;
    int sendLen = nlMpos_Command.mpos_readlen(pbuf+offset,_VAR_BIT16);offset+=2;
    uchar* sendData = pbuf+offset;offset+=sendLen;
    LOGD_FMT(">>>sendLen[%d]",sendLen);
    LOGD_STR("sendData",sendData,sendLen);
	uchar recvData[1024];
	memset(recvData,0, sizeof(recvData));

	if(!EXEC_NDK("NDK_RfidPiccApdu",NDK_RfidPiccApdu(sendLen,sendData, &recvLen,recvData),NDK_OK,RFID_APDU)){
		goto ON_ERR;
	}else{
		extoffset = 2;
		nlMpos_Command.mpos_writelen(pOut + extoffset,recvLen, _VAR_BIT16);extoffset+=2;
		memcpy(pOut + extoffset,recvData,recvLen);extoffset+=recvLen;
		responseCmd(pOut, extoffset - 2, outLen, CMD_OK);
		return 0;
	}
	ON_ERR:
	responseCmd(pOut, 0, outLen, CMD_ERR_OTHER);
	return 0;
}

int Rfid_FelicaApdu(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {

    int offset = 0,extoffset=2,recvLen=0;
    int sendLen = nlMpos_Command.mpos_readlen(pbuf+offset,_VAR_BIT16);offset+=2;
    uchar* sendData = pbuf+offset;offset+=sendLen;
    LOGD_FMT(">>>sendLen[%d]",sendLen);
	uchar recvData[1024];
	memset(recvData,0, sizeof(recvData));
    if(!EXEC_NDK("NDK_RfidFelicaApdu",NDK_RfidFelicaApdu(sendLen,sendData, &recvLen, recvData), NDK_OK,RFID_FELICAAPDU)){
        goto ON_ERR;
    }else{
        //other ......
		extoffset = 2;
        nlMpos_Command.mpos_writelen(pOut + extoffset,recvLen, _VAR_BIT16);extoffset+=2;
		memcpy(pOut + extoffset,recvData,recvLen);extoffset+=recvLen;
        responseCmd(pOut, extoffset - 2, outLen, CMD_OK);
        return 0;
    }
    ON_ERR:
    responseCmd(pOut, 0, outLen, CMD_ERR_OTHER);
    return 0;
}

int Rfid_PowerOff(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen)
{
	LOGE_FMT("START");
	RfidReader_LedLt1118Status(0);
//	msleep(100);
	Rfid_Cancle();
    if(g_powerOnFlag == 1){
        g_powerOnFlag = 0;
        THREAD_COND_INIT(THREAD_COND_INDEX_RFID_POWEROFF);
        THREAD_COND_TIMEDWAIT(THREAD_COND_INDEX_RFID_POWEROFF,2000,NULL);
    }
	EXEC_NDK("NDK_RfidCloseRf",NDK_RfidCloseRf(),NDK_OK,RFID_POWEROFF);
	responseCmd(pOut,0,outLen,CMD_OK);
	LOGE_FMT("END");
	return 0;
}

int Rfid_IsExist(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen)
{
    int offset = 0,i=0;
    int num = pbuf[offset];offset+=1;
    uint nsec = nlMpos_Command.mpos_getvar(pbuf + offset, _VAR_BIT16);
    nsec = nlMpos_Command.mpos_endian_swab16(nsec);offset += 2;
    uchar type = 0;
    LOGD_FMT("num[%d] nsec[%d]", num, nsec);

    if(num <= 0){
        num = 1;
    }
	if (!EXEC_NDK("NDK_RfidInit", NDK_RfidInit(NULL), NDK_OK,RFID_ISEXIST)) {
		goto ON_ACK;
	}

	if (!EXEC_NDK("NDK_RfidPiccDeactivate(10)", NDK_RfidPiccDeactivate(10), NDK_OK,RFID_ISEXIST)) {
		goto ON_ACK;
	}

	if (!EXEC_NDK("NDK_RfidPiccType", NDK_RfidPiccType(0xcd), NDK_OK,RFID_ISEXIST)) {
		goto ON_ACK;
	}

	for (i = 0; i < num; i++) {
		if (EXEC_NDK("NDK_RfidPiccDetect", NDK_RfidPiccDetect(&type), NDK_OK,RFID_ISEXIST)) {
			break;
		}
		if (nsec != 0)
			usleep(nsec * 1000);
	}

    if(i==num){
        goto ON_ACK;
    }
	EXEC_NDK("NDK_RfidCloseRf", NDK_RfidCloseRf(), NDK_OK,RFID_ISEXIST);
    responseCmd(pOut, 0, outLen, CMD_OK);
    return 0;
	ON_ACK:
	EXEC_NDK("NDK_RfidCloseRf", NDK_RfidCloseRf(), NDK_OK,RFID_ISEXIST);
	responseCmd(pOut, 0, outLen, CMD_ERR_OTHER);
	return 0;
}

#define M1

int Rfid_M1AuthKey(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
	int offset;
	unsigned char kmode;
	unsigned char KeySector;
	unsigned char uidbuf[32];
	uchar *keyData;
	offset = 0;
	kmode = nlMpos_Command.mpos_getvar(pbuf + offset, _VAR_BIT8);offset++;
	int snrLen = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);offset+=2;
	memcpy(uidbuf, pbuf + offset, snrLen);offset += snrLen;
	KeySector = nlMpos_Command.mpos_getvar(pbuf + offset, _VAR_BIT8);offset++;
	keyData = pbuf + offset;
	if (kmode != 0x60 && kmode != 0x00 && kmode != 0x61 && kmode != 0x01) {
		ERRMSG(SDK_ERR_PARAM,RFID_M1AUTHKEY);
		goto ON_ACK;
	}
	if (KeySector < 0 || KeySector > 255) {
		ERRMSG(SDK_ERR_PARAM,RFID_M1AUTHKEY);
		goto ON_ACK;
	}
	LOGD_FMT("snrLen[%d] kmode[0x%x] KeySector[%d]",snrLen,kmode,KeySector);
	LOGD_STR("uidbuf",uidbuf,snrLen);
	if(!EXEC_NDK("",NDK_M1ExternalAuthen(snrLen, uidbuf, kmode, keyData, KeySector),NDK_OK,RFID_M1AUTHKEY)){
		goto ON_ACK;
	}
	responseCmd(pOut, 0, outLen, CMD_OK);
	return 0;
	ON_ACK:
	responseCmd(pOut, 0, outLen, CMD_ERR_OTHER);
	return 0;
}

int Rfid_M1ReadBlock(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
	int offset = 0;
	int number = nlMpos_Command.mpos_getvar(pbuf + offset, _VAR_BIT8);
	offset += 1;
	LOGD_FMT(">>>number[%d]",number);

	int len;
	uchar data[512];
	memset(data, 0, sizeof(data));

    if (!EXEC_NDK("NDK_M1Read", NDK_M1Read(number, &len, data), NDK_OK,RFID_M1READBLOCK)) goto ON_ERR;

	LOGD_FMT(">>>len[%d]", len);
	offset = 2;
	nlMpos_Command.mpos_writelen(pOut + offset, len, _VAR_BIT16);
	offset += 2;
	memcpy(pOut + offset, data, len);
	offset += len;
	responseCmd(pOut, offset - 2, outLen, CMD_OK);
	return 0;
	ON_ERR:
	responseCmd(pOut, 0, outLen, CMD_ERR_OTHER);
	return 6;
}

int Rfid_M1WriteBlock(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
	int offset = 0;
	int number = nlMpos_Command.mpos_getvar(pbuf + offset, _VAR_BIT8);
	offset += 1;
	int dataLen = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);
	offset += 2;
	uchar *data = pbuf + offset;
	offset += dataLen;
	int cardType = g_mifareCardType;

	LOGD_FMT(">>>cardType[%d] number[%d] dataLen[%d]", cardType, number, dataLen);

    int len = 16;
    if (!EXEC_NDK("NDK_M1Write", NDK_M1Write(number, &len, data), NDK_OK,RFID_M1WRITEBLOCK)) goto ON_ERR;

	responseCmd(pOut, 0, outLen, CMD_OK);
	return 0;
	ON_ERR:
	responseCmd(pOut, 0, outLen, CMD_ERR_OTHER);
	return 6;
}

int Rfid_M1Increment(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
	int offset = 0;
	uchar nBlockNum = nlMpos_Command.mpos_getvar(pbuf + offset, _VAR_BIT8);offset++;

	if(!EXEC_NDK("NDK_M1Increment",NDK_M1Increment(nBlockNum, 4, pbuf + offset),NDK_OK,RFID_M1INCREMENT)){
        EXEC_NDK("NDK_M1Restore",NDK_M1Restore(nBlockNum),NDK_OK,RFID_M1INCREMENT);
        goto ON_ACK;
    }else{
        if(!EXEC_NDK("NDK_M1Transfer",NDK_M1Transfer(nBlockNum),NDK_OK,RFID_M1INCREMENT)){
            goto ON_ACK;
        }
    }
    responseCmd(pOut, 0, outLen, CMD_OK);
    return 0;
    ON_ACK:
    responseCmd(pOut, 0, outLen, CMD_ERR_OTHER);
    return 0;
}

int Rfid_M1Decrement(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
	int offset = 0;
	uchar nBlockNum = nlMpos_Command.mpos_getvar(pbuf + offset, _VAR_BIT8);offset++;
    if(!EXEC_NDK("NDK_M1Decrement",NDK_M1Decrement(nBlockNum, 4, pbuf  + offset),NDK_OK,RFID_M1DECREMENT)){
        EXEC_NDK("NDK_M1Restore",NDK_M1Restore(nBlockNum),NDK_OK,RFID_M1DECREMENT);
        goto ON_ACK;
    }else{
        if(!EXEC_NDK("NDK_M1Transfer",NDK_M1Transfer(nBlockNum),NDK_OK,RFID_M1DECREMENT)){
            goto ON_ACK;
        }
    }
    responseCmd(pOut, 0, outLen, CMD_OK);
    return 0;
    ON_ACK:
    responseCmd(pOut, 0, outLen, CMD_ERR_OTHER);
    return 0;
}

#define M0
int Rfid_M0AuthKey(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int offset = 0;
    int keyLen = nlMpos_Command.mpos_readlen(pbuf+offset,_VAR_BIT16);offset+=2;
    uchar* keyData = pbuf+offset;offset+=keyLen;
    int   uidLen=0;
    uchar uid[32],sak=-1;

    if(keyLen != 16){
		ERRMSG(SDK_ERR_PARAM,RFID_M0AUTHKEY);
        goto ON_ERR;
    }

    if(!EXEC_NDK("NDK_M0Authen",NDK_M0Authen(keyData), NDK_OK,RFID_M0AUTHKEY)){
        goto ON_ERR;
    }
    LOGE_FMT(">>>NDK_M0Authen succ");

    int i=0,count = 6;
    for(i = 0; i < count; i++){
        LOGD_FMT(">>>count[%d]",i);
        if(EXEC_NDK("NDK_MifareActive2", NDK_MifareActive(0x52,uid,&uidLen,&sak), NDK_OK,RFID_M0AUTHKEY)){
            break;
        }
        if(i == count-1){
            goto ON_ERR;
        }
    }
    responseCmd(pOut, 0, outLen, CMD_OK);
    return 0;
    ON_ERR:
    responseCmd(pOut, 0, outLen, CMD_ERR_OTHER);
    return 0;
}

int Rfid_M0ReadBlock(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int offset = 0;
    int number = nlMpos_Command.mpos_getvar(pbuf + offset, _VAR_BIT8);
    offset += 1;
    LOGD_FMT(">>>number[%d]", number);

    int len;
    uchar data[512];
    memset(data, 0, sizeof(data));

    if (!EXEC_NDK("NDK_M0Read", NDK_M0Read(number, &len, data), NDK_OK,RFID_M0READBLOCK)) goto ON_ERR;

    LOGD_FMT(">>>len[%d]", len);
    offset = 2;
    nlMpos_Command.mpos_writelen(pOut + offset, len, _VAR_BIT16);
    offset += 2;
    memcpy(pOut + offset, data, len);
    offset += len;
    responseCmd(pOut, offset - 2, outLen, CMD_OK);
    return 0;
    ON_ERR:
    responseCmd(pOut, 0, outLen, CMD_ERR_OTHER);
    return 0;
}

int Rfid_M0WriteBlock(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int offset = 0;
    int number = nlMpos_Command.mpos_getvar(pbuf + offset, _VAR_BIT8);
    offset += 1;
    int dataLen = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);
    offset += 2;
    uchar *data = pbuf + offset;
    offset += dataLen;

    LOGD_FMT(">>>number[%d] dataLen[%d]", number, dataLen);

    if (!EXEC_NDK("NDK_M0Write", NDK_M0Write(number, dataLen, data), NDK_OK,RFID_M0WRITEBLOCK)) goto ON_ERR;

    responseCmd(pOut, 0, outLen, CMD_OK);
    return 0;
    ON_ERR:
    responseCmd(pOut, 0, outLen, CMD_ERR_OTHER);
    return 6;
}

int Rfid_ATS(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
	unsigned char UIDlen = 0;
	unsigned char rece_buf[30];
	unsigned char aum_read[2048] = {0};
	unsigned char UID[64] = {0,};
	int recv_len;
	uchar recv_buf[128];

	if(!EXEC_NDK("NDK_RfidPiccType",NDK_RfidPiccType(0xcc),NDK_OK,RFID_ATS)){
		goto ERR_ACK;
	}
	if(!EXEC_NDK("NDK_RfidPiccDeactivate",NDK_RfidPiccDeactivate(10),NDK_OK,RFID_ATS)){
		goto ERR_ACK;
	}
    LOGE_FMT(">>>g_rfMultiLevel[%d].",g_rfMultiLevel);
    if(g_rfMultiLevel == 0){
        if(!EXEC_NDK("NDK_M1Request",NDK_M1Request(0, &UIDlen, rece_buf),NDK_OK,RFID_ATS)){
            goto ERR_ACK;
        }
        if(!EXEC_NDK("NDK_M1Anti",NDK_M1Anti(&UIDlen, UID),NDK_OK,RFID_ATS)){
            goto ERR_ACK;
        }
        if(!EXEC_NDK("NDK_M1Select",NDK_M1Select(UIDlen, UID, aum_read),NDK_OK,RFID_ATS)){
            goto ERR_ACK;
        }
    } else{
        LOGD_FMT(">>>g_rfMultiLevel==1");
        int atqLen = 0, UIDlen = 0, snrLen = 0, i = 0;
        uchar atq[16], snr[64], selCmd[3] = {0x93, 0x95, 0x97}, sak[2], UID[64];
        memset(atq, 0, sizeof(atq));
        memset(snr, 0, sizeof(snr));
        memset(sak, 0, sizeof(sak));
        memset(UID, 0, sizeof(UID));
        if (!EXEC_NDK("NDK_M1Request", NDK_M1Request(0x52, &atqLen, atq), NDK_OK,RFID_ATS)) {
            goto ERR_ACK;
        }
        UIDlen = (atq[0] >> 6) + 1;
        do {
            if (!EXEC_NDK("NDK_M1Anti_SEL", NDK_M1Anti_SEL(selCmd[i], &snrLen, snr), NDK_OK,RFID_ATS)) {
                goto ERR_ACK;
            }
            if (!EXEC_NDK("NDK_M1Select_SEL", NDK_M1Select_SEL(selCmd[i], snrLen, snr, sak), NDK_OK,RFID_ATS)) {
                goto ERR_ACK;
            }
            memcpy((UID + (i << 2)), snr, 4);
            LOGD_FMT("selCmd[%d][%d] *sak[%d]", i, selCmd[i], *sak);
            if ((*sak & 0x04) == 0x00) break;
        } while (i++ < 3);

        if ((UIDlen == 1) && (UID[0] == 0x88)) {
            LOGD_FMT("UID ERR 1");
            goto ERR_ACK;
        }
        if ((UIDlen == 2) && (UID[4] == 0x88)) {
            LOGD_FMT("UID ERR 2");
            goto ERR_ACK;
        }
        switch (UIDlen) {
            case 1:
                UIDlen = 4;
                break;
            case 2:
                UIDlen = 7;
                memcpy(UID, &UID[1], 3);
                memcpy(&(UID[3]), &UID[4], 4);
                break;
            case 3:
                UIDlen = 10;
                memcpy(UID, &UID[1], 3);
                memcpy(&(UID[3]), &UID[5], 3);
                memcpy(&(UID[6]), &UID[8], 4);
                break;
            default:
                UIDlen = 0;
                UID[0] = 0x00;
                goto ERR_ACK;
        }
    }

	memset(recv_buf, 0, sizeof(recv_buf));
	if(!EXEC_NDK("NDK_RfidTypeARats",NDK_RfidTypeARats(0, &recv_len, recv_buf),NDK_OK,RFID_ATS)){
		goto ERR_ACK;
	}else{
		LOGD_FMT("recv_len[%d]", recv_len);
		if (recv_len <= sizeof(recv_buf)) {
			LOGD_STR("recv_buf", recv_buf, recv_len);
			int offset = 0;
			offset += RESPOND_DATA_OFFSET;
			nlMpos_Command.mpos_writelen(pOut + offset, recv_len, _VAR_BIT16);
			offset += 2;
			memcpy(pOut + offset, recv_buf, recv_len);
			offset += recv_len;
//            EXEC_NDK("NDK_RfidCloseRf", NDK_RfidCloseRf(), NDK_OK,RFID_ATS);
            responseCmd(pOut, offset - 2, outLen, CMD_OK);
			return 0;
		} else {
			LOGE_FMT("out of len recv_len[%d]", recv_len);
			goto ERR_ACK;
		}
	}
	ERR_ACK:
//    EXEC_NDK("NDK_RfidCloseRf", NDK_RfidCloseRf(), NDK_OK,RFID_ATS);
    responseCmd(pOut, 0, outLen, CMD_ERR_OTHER);
	return 0;
}
#if 0
#define MIFARE_CARD
int Rfid_GetPowerOnParam(StRFPowerOnParam* parm,unsigned char*pbuf,int buf_len)
{
	if(parm == NULL){
		return ACK_ERR;
	}
	int offset = 0;
	parm->sak = 0xFF;
	parm->rfCardType = nlMpos_Command.mpos_getvar(pbuf+offset, _VAR_BIT8);offset += 1;
	parm->timeOut = nlMpos_Command.mpos_getvar(pbuf+offset, _VAR_BIT16);
	parm->timeOut = nlMpos_Command.mpos_endian_swab16(parm->timeOut);offset += 2;
	if(buf_len > offset){
		parm->showMsgLen = nlMpos_Command.mpos_readlen(pbuf+offset, _VAR_BIT16);offset += 2;
		offset += parm->showMsgLen;
	}
	if(buf_len > offset){
		parm->sakLen = nlMpos_Command.mpos_readlen(pbuf+offset, _VAR_BIT16);offset += 2;
		parm->sak = nlMpos_Command.mpos_getvar(pbuf+offset, _VAR_BIT8);offset += 1;
	}
	
	if(buf_len > offset){
		parm->felicaDataLen = nlMpos_Command.mpos_readlen(pbuf+offset, _VAR_BIT16);offset += 2;
		if(parm->felicaDataLen != 0) 
			parm->felicaData = pbuf+offset;
		else  
			parm->felicaData = NULL;
		offset += parm->felicaDataLen;
	}
	if(buf_len > offset){
		parm->mifareDataLen = nlMpos_Command.mpos_readlen(pbuf+offset, _VAR_BIT16);offset += 2;
		if(parm->mifareDataLen != 0)
			parm->mifareData = pbuf+offset;
		else
			parm->mifareData = NULL;
		offset += parm->mifareDataLen;
	}
	LOGD_FMT(">>>ParseResult rfCardType[0x%x] timeOut[%d] showMsgLen[%d] sakLen[%d] sak[0x%x] felicaDataLen[%d] mifareDataLen[%d]",\
	parm->rfCardType,parm->timeOut,parm->showMsgLen,parm->sakLen,parm->sak,parm->felicaDataLen,parm->mifareDataLen);
	return ACK_OK;
}

static int __MifareSelect(uchar sak, uchar *psKey, MIFARE_CARD_TYPE *mifaretype)
{
	int ret = 0;
	MIFARE_CARD_TYPE cardtype = 0;
	
	if(sak & SAK_BIT2){
		cardtype = MIFARE_NONE;
	} else {
		if(sak & SAK_BIT4){
			if(sak & SAK_BIT5){
				cardtype = MIFARE_4K;			//MIFARE_4K select
			}else {
				if(sak & SAK_BIT1){
					cardtype = MIFARE_MINI;		//MIFARE_MINI select
				}else {
					cardtype = MIFARE_1K;		//MIFARE_1K select
				}
			}
		} else{
			if(sak & SAK_BIT5){
				if(sak & SAK_BIT1){
					cardtype = MIFARE_PLUS_4K_SL2;			//MIFARE_PLUS_4K_SL2 select
				}else {
					cardtype = MIFARE_PLUS_2K_SL2;			//MIFARE_PLUS_2K_SL2 select
				}
			}else {
				if(sak & SAK_BIT6){
					cardtype = ISO14443_4_CARD;				//ISO14443-4 card select
				}else {
					if(psKey == NULL){
						uchar seckey[16] = {"\x49\x45\x4D\x4B\x41\x45\x52\x42\x21\x4E\x41\x43\x55\x4F\x59\x46"};
						psKey = seckey;	
						LOGD_FMT(">>>use default key.");
					}
					if(EXEC_NDK("NDK_M0Authen", ret = NDK_M0Authen(psKey), NDK_OK)){
						LOGD_FMT(">>>NDK_M0Authen succ");
					}
					if(ret == NDK_OK){
						cardtype = MIFARE_ULC_CL2;			//MIFARE_ULC_CL2 select
					}else if(ret == NDK_ERR_MI_NOTAGERR){
						cardtype = MIFARE_UL_CL2;			//MIFARE_UL_CL2 select
					}else {
						cardtype = MIFARE_ERR;
						return ret;							//有可能认证失败、CRC校验失败等错误
					}
				}
			}
		}
	}
	*mifaretype = cardtype;
	return NDK_OK;
}

static int __PowerOnMifare(StPowerOnFunParam *funParam)
{
	if(!EXEC_NDK("NDK_MifareActive1", NDK_MifareActive(0x52,
		funParam->uid,&funParam->uidLen,&funParam->sak), NDK_OK)){
		return ACK_ERR;
	}
	if(funParam->sak&SAK_BIT6){
		LOGD_FMT(">>>CPU CARD");
		return ACK_ERR;
	}
	funParam->targetCard = TARGETCARD_MIFARE;

	g_mifareCardType = MIFARECARD_M0;
	#if 1
	MIFARE_CARD_TYPE cardType = 0;
	__MifareSelect(funParam->sak,funParam->poweronparam->mifareData,&cardType);
	LOGD_FMT(">>>__MifareSelect cardType[%d]",cardType);
	if(cardType == 0 || cardType == MIFARE_NONE || cardType == MIFARE_ERR || cardType == ISO14443_4_CARD){
		LOGD_FMT(">>>cardType Fail.");
		return ACK_ERR;
	}
	if(cardType == MIFARE_UL_CL2 && (!EXEC_NDK("NDK_MifareActive2", NDK_MifareActive(0x52,
		funParam->uid,&funParam->uidLen,&funParam->sak), NDK_OK))){
		LOGD_FMT(">>>NDK_MifareActive2 Fail.");
		return ACK_ERR;
	}	
	funParam->targetCard = TARGETCARD_MIFARE;

	g_mifareCardType = cardType;
	#endif
	return ACK_OK;
}
void Rfid_PowerOnPolling(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen)
{	
	StRFPowerOnParam param;
	Rfid_GetPowerOnParam(&param,pbuf,buf_len);
	
	StPowerOnFunParam funParam;
	memset(&funParam,0,sizeof(funParam));
	funParam.poweronparam = &param;
	gettimeofday(&funParam.startTime,NULL);
	
	int rfCardType = param.rfCardType; 
	
	memcpy(funParam.ackHead, CMD_ERR_OTHER, 2);
	if(!EXEC_NDK("NDK_RfidInit(NULL)", NDK_RfidInit(NULL), NDK_OK)) return;
	if(!EXEC_NDK("NDK_RfidPiccDeactivate(10)", NDK_RfidPiccDeactivate(10), NDK_OK)) return;
	msleep(100);
	int ret = ACK_ERR;
//	cardstatus |= (0x04);//temporary use
ON_AGAIN:
	if(rfCardType == RFCARD_MIFARE || rfCardType == 0x10){
		ret = __PowerOnMifare(&funParam);
	}
	LOGD_FMT(">>>PowerOn ret[%d]",ret);
	//other cards
	//......

	if(__GetTimeDistance(&funParam.startTime) >= funParam.poweronparam->timeOut*1000){
		memcpy(funParam.ackHead, CMD_ERR_TIMEOUT, 2);
	}
//	else if(!(getcardstatus()&0x04)){
//		memcpy(funParam.ackHead, CMD_CANCEL, 2);
//	}
	else if(ret == ACK_OK){
		memcpy(funParam.ackHead, CMD_OK, 2);
	}else{
		msleep(25);
		goto ON_AGAIN;
	}
	
//	cardstatus &= ~(0x04);
	int offset = 2;
	if(ret != ACK_OK){
		responseCmd(pOut,offset-2,outLen,funParam.ackHead);
		return;
	}	
	if(funParam.targetCard == TARGETCARD_MIFARE){
		pOut[offset] = 0x0E;offset+=1;
		nlMpos_Command.mpos_writelen(pOut+offset,funParam.uidLen,_VAR_BIT16);offset+=2;
		memcpy(pOut+offset,funParam.uid,funParam.uidLen);offset+=funParam.uidLen;//UID
		memcpy(pOut+offset,"\x00\x00",2);offset+=2;//ATQA
		memcpy(pOut+offset,&funParam.sak,1);offset+=1;//SAK
		nlMpos_Command.mpos_writelen(pOut+offset,0,_VAR_BIT16);offset+=2;
		responseCmd(pOut, offset-2, outLen, funParam.ackHead);
	}
}

int CommandParse_M1StoreKey(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int ret = 0;
    int nRet = 0;
    int nLen, offset;
    char ret_code[2];
    unsigned char kmode;
    unsigned char KeySector;
    memcpy(ret_code, CMD_OK, 2);

    nLen = buf_len;

    offset = 0;
    kmode = nlMpos_Command.mpos_getvar(pbuf + MPOS_VARIABLE_OFFSET + offset, _VAR_BIT8);
    offset++;
    KeySector = nlMpos_Command.mpos_getvar(pbuf + MPOS_VARIABLE_OFFSET + offset, _VAR_BIT8);
    offset++;
    if (kmode != 0x60 && kmode != 0x00 && kmode != 0x61 && kmode != 0x01) {
        ret = 2;
        memcpy(ret_code, CMD_ERR_PARAM, 2);
        goto ON_ACK;
    }
    if (KeySector < 0 || KeySector > 15) {
        ret = 2;
        memcpy(ret_code, CMD_ERR_PARAM, 2);
        goto ON_ACK;
    }
    nRet = NDK_M1KeyStore(kmode, KeySector, pbuf + MPOS_VARIABLE_OFFSET + offset);
    if (nRet != 0) {
        LOGE_NDK("NDK_M1KeyStore", nRet, pbuf, buf_len);
        Udebug.ERROR_MSG_LOG("kmode[%d] KeySector[%d]", kmode, KeySector);
        ret = 6;
        memcpy(ret_code, CMD_ERR_OTHER, 2);
    }
    ON_ACK:
    responseCmd(pOut, 0, outLen, ret_code);
    return ret;
}

int CommandParse_M1LoadKey(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int ret = 0;
    int nRet;
    int nLen, offset;
    char ret_code[2];
    unsigned char kmode;
    unsigned char KeySector;
    memcpy(ret_code, CMD_OK, 2);

    nLen = buf_len;
    offset = 0;
    kmode = nlMpos_Command.mpos_getvar(pbuf + MPOS_VARIABLE_OFFSET + offset, _VAR_BIT8);
    offset++;
    KeySector = nlMpos_Command.mpos_getvar(pbuf + MPOS_VARIABLE_OFFSET + offset, _VAR_BIT8);
    offset++;
    if (kmode != 0x60 && kmode != 0x00 && kmode != 0x61 && kmode != 0x01) {
        ret = 2;
        memcpy(ret_code, CMD_ERR_PARAM, 2);
        goto ON_ACK;
    }
    if (KeySector < 0 || KeySector > 15) {
        ret = 2;
        memcpy(ret_code, CMD_ERR_PARAM, 2);
        goto ON_ACK;
    }
    nRet = NDK_M1KeyLoad(kmode, KeySector);
    if (nRet != 0) {
        LOGE_NDK("NDK_M1KeyLoad", nRet, pbuf, buf_len);
        Udebug.ERROR_MSG_LOG("kmode[%d] KeySector[%d]", kmode, KeySector);
        ret = 6;
        memcpy(ret_code, CMD_ERR_OTHER, 2);
    }
    ON_ACK:
    responseCmd(pOut, 0, outLen, ret_code);
    return ret;
}

int CommandParse_M1Auth(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int ret = 0;
    int nRet;
    int nLen, offset;
    char ret_code[2];
    unsigned char kmode;
    unsigned char KeySector;
    unsigned char uidbuf[8];
    memcpy(ret_code, CMD_OK, 2);

    nLen = buf_len;

    offset = 0;
    kmode = nlMpos_Command.mpos_getvar(pbuf + MPOS_VARIABLE_OFFSET + offset, _VAR_BIT8);
    offset++;
    memcpy(uidbuf, pbuf + MPOS_VARIABLE_OFFSET + offset, 4);
    offset += 4;
    KeySector = nlMpos_Command.mpos_getvar(pbuf + MPOS_VARIABLE_OFFSET + offset, _VAR_BIT8);
    offset++;
    if (kmode != 0x60 && kmode != 0x00 && kmode != 0x61 && kmode != 0x01) {
        ret = 2;
        memcpy(ret_code, CMD_ERR_PARAM, 2);
        goto ON_ACK;
    }

    if (KeySector < 0 || KeySector > 255) {
        ret = 2;
        memcpy(ret_code, CMD_ERR_PARAM, 2);
        goto ON_ACK;
    }
    nRet = NDK_M1InternalAuthen(4, uidbuf, kmode, KeySector);
    if (nRet != 0) {
        LOGE_NDK("NDK_M1InternalAuthen", nRet, pbuf, buf_len);
        Udebug.ERROR_MSG_LOG("kmode[%d] KeySector[%d]", kmode, KeySector);
        Udebug.ERROR_MSG_LOG_String(uidbuf, sizeof(uidbuf));
        ret = 6;
        memcpy(ret_code, CMD_ERR_OTHER, 2);
    }

    ON_ACK:
    responseCmd(pOut, 0, outLen, ret_code);
    return ret;
}

int CommandParse_RFIDATS(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int ret = -1;
    unsigned char UIDlen = 0;
    unsigned char rece_buf[30];
    unsigned char aum_read[2048] = {0};
    unsigned char UID[64] = {0,};
    int recv_len;
    uchar recv_buf[128];

    if(!EXEC_NDK("NDK_RfidPiccType",NDK_RfidPiccType(0xcc),NDK_OK)){
        goto ERR_ACK;
    }
    if(!EXEC_NDK("NDK_RfidPiccDeactivate",NDK_RfidPiccDeactivate(10),NDK_OK)){
        goto ERR_ACK;
    }
    if(!EXEC_NDK("NDK_M1Request",NDK_M1Request(0, &UIDlen, rece_buf),NDK_OK)){
        goto ERR_ACK;
    }
    if(!EXEC_NDK("NDK_M1Anti",NDK_M1Anti(&UIDlen, UID),NDK_OK)){
        goto ERR_ACK;
    }
    if(!EXEC_NDK("NDK_M1Select",NDK_M1Select(UIDlen, UID, aum_read),NDK_OK)){
        goto ERR_ACK;
    }
    memset(recv_buf, 0, sizeof(recv_buf));
    if(!EXEC_NDK("NDK_RfidTypeARats",NDK_RfidTypeARats(0, &recv_len, recv_buf),NDK_OK)){
        goto ERR_ACK;
    }else{
        LOGD_FMT("recv_len[%d]", recv_len);
        if (recv_len <= sizeof(recv_buf)) {
            LOGD_STR("recv_buf", recv_buf, recv_len);
            int offset = 0;
            offset += RESPOND_DATA_OFFSET;
            nlMpos_Command.mpos_writelen(pOut + offset, recv_len, _VAR_BIT16);
            offset += 2;
            memcpy(pOut + offset, recv_buf, recv_len);
            offset += recv_len;
            responseCmd(pOut, offset - 2, outLen, CMD_OK);
            return ret;
        } else {
            LOGE_FMT("out of len recv_len[%d]", recv_len);
            goto ERR_ACK;
        }
    }
    ERR_ACK:
    responseCmd(pOut, 0, outLen, CMD_ERR_OTHER);
    return ret;
}
#endif