#include "threadcond.h"
#include "threadtool.h"
#include "event.h"
#include "nllogger.h"
#include "ndk.h"
#include "cardmgr.h"
#include "card.h"
#include "log.h"

int events[]={SYS_EVENT_MAGCARD, SYS_EVENT_ICCARD, SYS_EVENT_RFID};
static int hadEvent;
static setHadEvent(EM_SYS_EVENT eventNum){
    if(hadEvent == -1){
        hadEvent = eventNum;
    }
}
static void __eventSignal(int index,EM_SYS_EVENT eventNum){
    ST_THREAD_COND_MSG condMsg;
    condMsg.cardEvent = eventNum;
    THREAD_COND_SIGNAL(index,&condMsg);
}
static int __eventCallBackMag(EM_SYS_EVENT eventNum, int msgLen, char * msg){
    setHadEvent(eventNum);
    __eventSignal(THREAD_COND_INDEX_CARD_MAG,eventNum);
    return 0;
}
static int __eventCallBackIc(EM_SYS_EVENT eventNum, int msgLen, char * msg){
    setHadEvent(eventNum);
    __eventSignal(THREAD_COND_INDEX_CARD_IC,eventNum);
    return 0;
}
static int __eventCallBackRfid(EM_SYS_EVENT eventNum, int msgLen, char * msg){
    setHadEvent(eventNum);
    __eventSignal(THREAD_COND_INDEX_CARD_RFID,eventNum);
    return 0;
}
static int __eventCallBackMagIc(EM_SYS_EVENT eventNum, int msgLen, char * msg){
    setHadEvent(eventNum);
    __eventSignal(THREAD_COND_INDEX_CARD_MAG_IC,eventNum);
    return 0;
}
static int __eventCallBackMagRfid(EM_SYS_EVENT eventNum, int msgLen, char * msg){
    setHadEvent(eventNum);
    __eventSignal(THREAD_COND_INDEX_CARD_MAG_RFID,eventNum);
    return 0;
}
static int __eventCallBackIcRfid(EM_SYS_EVENT eventNum, int msgLen, char * msg){
    setHadEvent(eventNum);
    __eventSignal(THREAD_COND_INDEX_CARD_IC_RFID,eventNum);
    return 0;
}
static int __eventCallBackMagIcRfid(EM_SYS_EVENT eventNum, int msgLen, char * msg){
    setHadEvent(eventNum);
    __eventSignal(THREAD_COND_INDEX_CARD_MAG_IC_RFID,eventNum);
    return 0;
}

static int __cardCondInit(int readCardMode){
	if(readCardMode == CARD_MAG){
		THREAD_COND_INIT(THREAD_COND_INDEX_CARD_MAG);
	}else if(readCardMode == CARD_IC){
		THREAD_COND_INIT(THREAD_COND_INDEX_CARD_IC);
	}else if(readCardMode == CARD_RFID){
		THREAD_COND_INIT(THREAD_COND_INDEX_CARD_RFID);
	} else if(readCardMode == CARD_MAG_IC){
		THREAD_COND_INIT(THREAD_COND_INDEX_CARD_MAG_IC);
	}else if(readCardMode == CARD_MAG_RFID){
		THREAD_COND_INIT(THREAD_COND_INDEX_CARD_MAG_RFID);
	}else if(readCardMode == CARD_IC_RFID){
		THREAD_COND_INIT(THREAD_COND_INDEX_CARD_IC_RFID);
	}else if(readCardMode == CARD_MAG_IC_RFID){
		THREAD_COND_INIT(THREAD_COND_INDEX_CARD_MAG_IC_RFID);
	}else{
		return NL_ERR_PARAMETERS;
	}
	return NL_OK;
}
static int __cardCondWait(int readCardMode,ST_THREAD_COND_MSG *msg){
	if(readCardMode == CARD_MAG){
		THREAD_COND_WAIT(THREAD_COND_INDEX_CARD_MAG,msg);
	}else if(readCardMode == CARD_IC){
		THREAD_COND_WAIT(THREAD_COND_INDEX_CARD_IC,msg);
	}else if(readCardMode == CARD_RFID){
		THREAD_COND_WAIT(THREAD_COND_INDEX_CARD_RFID,msg);
	} else if(readCardMode == CARD_MAG_IC){
		THREAD_COND_WAIT(THREAD_COND_INDEX_CARD_MAG_IC,msg);
	}else if(readCardMode == CARD_MAG_RFID){
		THREAD_COND_WAIT(THREAD_COND_INDEX_CARD_MAG_RFID,msg);
	}else if(readCardMode == CARD_IC_RFID){
		THREAD_COND_WAIT(THREAD_COND_INDEX_CARD_IC_RFID,msg);
	}else if(readCardMode == CARD_MAG_IC_RFID){
		THREAD_COND_WAIT(THREAD_COND_INDEX_CARD_MAG_IC_RFID,msg);
	}
}
static int __cardCondSignalNone(){
	ST_THREAD_COND_MSG msg;
	msg.cardEvent = SYS_EVENT_NONE;
	THREAD_COND_SIGNAL(THREAD_COND_INDEX_CARD_MAG,&msg);
	THREAD_COND_SIGNAL(THREAD_COND_INDEX_CARD_IC,&msg);
	THREAD_COND_SIGNAL(THREAD_COND_INDEX_CARD_RFID,&msg);
	THREAD_COND_SIGNAL(THREAD_COND_INDEX_CARD_MAG_IC,&msg);
	THREAD_COND_SIGNAL(THREAD_COND_INDEX_CARD_MAG_RFID,&msg);
	THREAD_COND_SIGNAL(THREAD_COND_INDEX_CARD_IC_RFID,&msg);
	THREAD_COND_SIGNAL(THREAD_COND_INDEX_CARD_MAG_IC_RFID,&msg);
	return 0;
}


static int __registerEvent(int eventNum, int timeOutMs,int (*notifyEvent)(EM_SYS_EVENT eventNum, int msgLen, char * msg)){
	int idx = 0;int ret = NDK_ERR;
	for(;idx <sizeof(events) / sizeof(int); idx++){

		if(!IS_EVENT_IN_SET(events[idx], eventNum))
			continue;
		LOGE_FMT("__registerEvent timeOutMs[%d]",timeOutMs);
		if(!EXEC_NDK("NDK_SYS_RegisterEvent",ret = NDK_SYS_RegisterEvent(events[idx], timeOutMs, notifyEvent),NL_OK,CARDREADER_OPEN)){
			if(timeOutMs == 0 && ret == NDK_ERR_PARA){
				if(!EXEC_NDK("NDK_SYS_RegisterEvent2",NDK_SYS_RegisterEvent(events[idx], SYS_EVENT_TIMEOUT, notifyEvent),NL_OK,CARDREADER_OPEN)){
					return NL_FAILED;
				}
			} else{
				return NL_FAILED;
			}
		}
		if(HAS_EVENT_MAG(events[idx])){
			LOGE_FMT(">>>mag event registered successful.");
		}
		if(HAS_EVENT_IC(events[idx])){
			LOGE_FMT(">>>ic event registered successful.");
		}
		if(HAS_EVENT_RFID(events[idx])){
			LOGE_FMT(">>>rfid event registered successful.");
		}
	}

	return NL_OK;
}
static int __addEvent(int readCardMode,int timeOutMs)
{
	int ret = NL_FAILED;
	if(readCardMode == CARD_MAG){
		ret = __registerEvent(SYS_EVENT_MAGCARD,timeOutMs,__eventCallBackMag);
	}else if(readCardMode == CARD_IC){
		ret = __registerEvent(SYS_EVENT_ICCARD,timeOutMs,__eventCallBackIc);
	}else if(readCardMode == CARD_RFID){
		ret = __registerEvent(SYS_EVENT_RFID,timeOutMs,__eventCallBackRfid);
	}else if(readCardMode == CARD_MAG_IC){
		ret = __registerEvent(SYS_EVENT_MAGCARD|SYS_EVENT_ICCARD,timeOutMs,__eventCallBackMagIc);
	}else if(readCardMode == CARD_MAG_RFID){
		ret = __registerEvent(SYS_EVENT_MAGCARD|SYS_EVENT_RFID,timeOutMs,__eventCallBackMagRfid);
	}else if(readCardMode == CARD_IC_RFID){
		ret = __registerEvent(SYS_EVENT_ICCARD|SYS_EVENT_RFID,timeOutMs,__eventCallBackIcRfid);
	}else if(readCardMode == CARD_MAG_IC_RFID){
		ret = __registerEvent(SYS_EVENT_MAGCARD|SYS_EVENT_ICCARD|SYS_EVENT_RFID,timeOutMs,__eventCallBackMagIcRfid);
	}else{
		return NL_ERR_PARAMETERS;
	}
	return ret;

}

int Event_Remove(int eventNum)
{
    int idx = 0;
    for(;idx <sizeof(events) / sizeof(int); idx++){
        
        if(!IS_EVENT_IN_SET(events[idx], eventNum))continue;

        if(EXEC_NDK("#NDK_SYS_UnRegisterEvent",NDK_SYS_UnRegisterEvent(events[idx]),NDK_OK,CARDREADER_OPEN)){
			if(HAS_EVENT_MAG(events[idx])){
				LOGE_FMT(">>>mag event unregistered successful.");
			}
			if(HAS_EVENT_IC(events[idx])){
				LOGE_FMT(">>>ic event unregistered successful.");
			}
			if(HAS_EVENT_RFID(events[idx])){
				LOGE_FMT(">>>rfid event unregistered successful.");
			}
		}		
    }
    return NL_OK;
}

int Event_Wait(int counter,StCardReaderParam* stCardReaderParam,int *pHasEvent)
{
    if(!LOGGER_ASSERT_EXPRESSION(pHasEvent != NULL))
        return NL_ERR_PARAMETERS;

    int readCardMode =  stCardReaderParam->readCardMode;
	LOGD_FMT("readCardMode[%d]",readCardMode);
	if(__cardCondInit(readCardMode)!=NL_OK){
		LOGD_FMT("param err.");
		return NL_ERR_PARAMETERS;
	}
    hadEvent = -1;

    if((counter == stCardReaderParam->rfidTimes) && (!EXEC_NDK("__addEvent",__addEvent(readCardMode,stCardReaderParam->timeout*1000/*ms*/),NL_OK,COMMAND_NONE))){
        return NL_FAILED;
    }

	Card_SetCancelFlag(0);

	CardReader_NotifyJava();

	if(hadEvent != -1){
        *pHasEvent = hadEvent;
	} else{
        ST_THREAD_COND_MSG msg;
        __cardCondWait(readCardMode,&msg);
        *pHasEvent = msg.cardEvent;
	}

    int nRet = (*pHasEvent == SYS_EVENT_NONE)?NL_ERR_TIMEOUT:NL_OK;

	if(Card_GetCancelFlag() == 1){
		nRet = NL_CANCEL;
	}

    LOGGER_DEBUG(">>>Event_Wait pHasEvent[0x%x] nRet[%d]",*pHasEvent,nRet);
    return nRet;
}

int Event_Cancel()
{
	__cardCondSignalNone();
	return NL_OK;
}


