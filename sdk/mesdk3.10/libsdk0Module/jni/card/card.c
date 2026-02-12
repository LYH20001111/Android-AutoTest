/**
 *  Created by wuhh on 2019/4/11 0011.
 */
#include <string.h>
#include "desc.h"
#include "threadtool.h"
#include "cardmgr.h"
#include "log.h"
#include "comm.h"
#include "api.h"
#include "event.h"
#include "nllogger.h"
#include "card.h"
#include <unistd.h>
#include <memory.h>
#include "rfid.h"
#include "readerrfid.h"
static int g_openFlag = 0;
static int g_cancelFlag=0;
static int g_readCardMode;//保存由OpenCard接口打开的读卡模式
extern uchar g_ucMagSwiped;
static void Card_SetReadCardMode(int mode){
    THREAD_MUTEX_LOCK(THREAD_MUTEX_INDEX_READCARDMODE);
    g_readCardMode |= mode;
    THREAD_MUTEX_UNLOCK(THREAD_MUTEX_INDEX_READCARDMODE);
}
int Card_GetReadCardMode(){
    int mode = 0;
    THREAD_MUTEX_LOCK(THREAD_MUTEX_INDEX_READCARDMODE);
    mode =  g_readCardMode;
    THREAD_MUTEX_UNLOCK(THREAD_MUTEX_INDEX_READCARDMODE);
    return mode;
}

void Card_SetCancelFlag(int flag){
    THREAD_MUTEX_LOCK(THREAD_MUTEX_INDEX_OPENCARD_CANCEL);
    g_cancelFlag = flag;
    THREAD_MUTEX_UNLOCK(THREAD_MUTEX_INDEX_OPENCARD_CANCEL);
}
int Card_GetCancelFlag(){
    int flag = 0;
    THREAD_MUTEX_LOCK(THREAD_MUTEX_INDEX_OPENCARD_CANCEL);
    flag = g_cancelFlag;
    THREAD_MUTEX_UNLOCK(THREAD_MUTEX_INDEX_OPENCARD_CANCEL);
    return flag;
}
int CardLock(int nReadCardMode,int cmd){
    int ret = -1,lockFlagMag = 0, lockFlagIc = 0;
    if(HAS_CARD_MAG(nReadCardMode)){
       ret = THREAD_MUTEX_TRYLOCK2(THREAD_MUTEX_INDEX_CARD_MAG);
       if(ret == EBUSY||ret == -1){
           LOGD_FMT(">>>The mag reader is busy. [%d]",ret);
           ERRMSG(SDK_ERR_CARD_MAG_BUSY,cmd);
           return 1;
       }
       lockFlagMag = 1;
    }
    if(HAS_CARD_IC(nReadCardMode)){
        ret = THREAD_MUTEX_TRYLOCK2(THREAD_MUTEX_INDEX_CARD_IC);
        if(ret == EBUSY||ret == -1){
            LOGD_FMT(">>>The ic reader is busy. [%d]",ret);
            if(lockFlagMag==1){
                THREAD_MUTEX_UNLOCK(THREAD_MUTEX_INDEX_CARD_MAG);
            }
            ERRMSG(SDK_ERR_CARD_IC_BUSY,cmd);
            return 1;
        }
        lockFlagIc = 1;
    }
    if(HAS_CARD_RFID(nReadCardMode)){
        ret = THREAD_MUTEX_TRYLOCK2(THREAD_MUTEX_INDEX_CARD_RF);
        if(ret == EBUSY||ret == -1){
            LOGD_FMT(">>>The rf reader is busy. [%d]",ret);
            if(lockFlagMag==1){
                THREAD_MUTEX_UNLOCK(THREAD_MUTEX_INDEX_CARD_MAG);
            }
            if(lockFlagIc == 1){
                THREAD_MUTEX_UNLOCK(THREAD_MUTEX_INDEX_CARD_IC);
            }
            ERRMSG(SDK_ERR_CARD_RFID_BUSY,cmd);
            return 1;
        }
    }
    LOGD_FMT(">>>__cardLock succ.");
    return 0;
}
void CardUnLock(int nReadCardMode){
    if(HAS_CARD_MAG(nReadCardMode)){
       THREAD_MUTEX_UNLOCK(THREAD_MUTEX_INDEX_CARD_MAG);
    }
    if(HAS_CARD_IC(nReadCardMode)){
       THREAD_MUTEX_UNLOCK(THREAD_MUTEX_INDEX_CARD_IC);
    }
    if(HAS_CARD_RFID(nReadCardMode)){
       THREAD_MUTEX_UNLOCK(THREAD_MUTEX_INDEX_CARD_RF);
    }
    LOGD_FMT(">>>__cardUnLock succ.");
}

static StCardReader* __get_magiccard_reader(StCardReader* readers[])
{
    return readers[CARDREADER_INDEX_MAG];
}

static StCardReader* __get_iccard_reader(StCardReader* readers[])
{
    return readers[CARDREADER_INDEX_IC];
}

static StCardReader* __get_rfcard_reader(StCardReader* readers[])
{
    return readers[CARDREADER_INDEX_RFID];
}

static int __set_event(int *event_set, int event)
{
    SET_EVENT(*event_set, event);
    return NL_OK;
}
static int __read_card(StCardReaderParam* stCardReaderParam,StCardReader* reader, StCardInfo* pstCardInfoOutput)
{
    if(reader == NULL){
        ERRMSG(SDK_ERR_PARAM,CARDREADER_OPEN);
        return NL_ERR_PARAMETERS;
    }
    if(!LOGGER_IS_EXPECT_RET(reader->readCardInfo(stCardReaderParam,pstCardInfoOutput), NL_OK))return NL_FAILED;

    return NL_OK;
}
static int __cardDealEvent(StCardReaderParam* stCardReaderParam,StCardReader* readersInput[], StCardReader** readerOutput,int *eventList)
{
    int events = 0, hasEvent=0;
    int readCardMode = stCardReaderParam->readCardMode;

    _IF_COND_TRUE_THEN_DO(HAS_CARD_MAG(readCardMode),__set_event(&events, SYS_EVENT_MAGCARD));
    _IF_COND_TRUE_THEN_DO(HAS_CARD_IC(readCardMode),__set_event(&events, SYS_EVENT_ICCARD));
    _IF_COND_TRUE_THEN_DO(HAS_CARD_RFID(readCardMode),__set_event(&events, SYS_EVENT_RFID));

    *eventList = events;

    int nRfidTimes = stCardReaderParam->rfidTimes;
    if(nRfidTimes < 1){
        LOGGER_DEBUG("Wrong number of card reader parameters. [%d]",nRfidTimes);
        stCardReaderParam->rfidTimes = nRfidTimes = 1;
    }
    int counter = nRfidTimes;//1 2

    while(counter--)
    {
        LOGD_FMT("counter[%d]",counter);//0 1
        if(Card_GetCancelFlag()  == 1){
            LOGD_FMT("Cancel. hasEvent[%d]",hasEvent);
            return NL_CANCEL;
        }

        int nRet = Event_Wait(counter+1,stCardReaderParam,&hasEvent);
        if(nRet < 0)
        {
            LOGE_FMT("Polling event in failure!, ret=%d", nRet);
            return nRet;
        }

        if(IS_EVENT_IN_SET(hasEvent, SYS_EVENT_RFID) && counter!=0)
        {
            __get_rfcard_reader(readersInput)->resumeCardDev(stCardReaderParam);
            continue;
        }
        if(Card_GetCancelFlag() == 1){
            LOGD_FMT("Cancel. hasEvent[%d] nRet[%d]",hasEvent, nRet);
            return NL_CANCEL;
        }
        break;
    }

    if(IS_EVENT_IN_SET(hasEvent, SYS_EVENT_MAGCARD)) {
        *readerOutput = __get_magiccard_reader(readersInput);
    }
    else if(IS_EVENT_IN_SET(hasEvent, SYS_EVENT_ICCARD)) {
        *readerOutput = __get_iccard_reader(readersInput);
    }
    else if(IS_EVENT_IN_SET(hasEvent, SYS_EVENT_RFID)) {
        *readerOutput = __get_rfcard_reader(readersInput);
    }
    else{
        ERRMSG(SDK_ERR_PARAM,CARDREADER_OPEN);
        LOGE_FMT("invalid event =%d", hasEvent);
        *readerOutput = NULL;
        return NL_FAILED;
    }
    return NL_OK;
}

static int __GetTimeDistance(struct timeval *startTime)
{
    struct timeval currTime;
    gettimeofday(&currTime,NULL);
    int disms = (currTime.tv_sec-startTime->tv_sec)*1000+(currTime.tv_usec-startTime->tv_usec)/1000;
    LOGD_FMT(">>>disms[%d]",disms);
    return disms;
}

static int __cardExec2(StCardReaderParam* stCardReaderParam,StCardReader* pastCardReaders[],void* pstCardInfoOutput)
{
    if(stCardReaderParam==NULL||pstCardInfoOutput==NULL){
        LOGE_FMT(">>>stCardReaderParam[%d] pstCardInfoOutput[%d] return NL_FAILED!",stCardReaderParam,pstCardInfoOutput);
        ERRMSG(SDK_ERR_PARAM,CARDREADER_OPEN);
        return NL_FAILED;
    }
    StCardInfo* cardInfo = (StCardInfo*)pstCardInfoOutput;
    cardInfo->validLen = 0;

    if(HAS_CARD_RFID(stCardReaderParam->readCardMode)){

        RfidReader_LedLt1118Status(1);

        if(!EXEC_NDK("NDK_RfidInit", NDK_RfidInit(NULL), NDK_OK,CARDREADER_OPEN)) return NL_FAILED;

        if(!EXEC_NDK("NDK_RfidPiccDeactivate", NDK_RfidPiccDeactivate(10), NDK_OK,CARDREADER_OPEN)) return NL_FAILED;

        if (!EXEC_NDK("NDK_RfidPiccType", NDK_RfidPiccType(NDK_RFID_AB), NDK_OK,CARDREADER_OPEN)) return NL_FAILED;
    }

    if(HAS_CARD_IC(stCardReaderParam->readCardMode)){
        if(!stCardReaderParam->enablePreParam){
            if(!EXEC_NDK("NDK_IccPowerDown",NDK_IccPowerDown(ICTYPE_IC),NDK_OK,CARDREADER_OPEN)) return NL_FAILED;
        }
    }

    if(HAS_CARD_MAG(stCardReaderParam->readCardMode)){
        EXEC_NDK("NDK_MagClose",NDK_MagClose(),NDK_OK,CARDREADER_OPEN);
        EXEC_NDK("NDK_MagOpen",NDK_MagOpen(),NDK_OK,CARDREADER_OPEN);
    }

    Card_SetCancelFlag(0);

    CardReader_NotifyJava();

    struct timeval startTime;
    gettimeofday(&startTime,NULL);
    int nRet = NL_FAILED;

    while(1) {
        if(HAS_CARD_RFID(stCardReaderParam->readCardMode)){
            nRet = __read_card(stCardReaderParam,pastCardReaders[CARDREADER_INDEX_RFID], (StCardInfo*)pstCardInfoOutput);
            if(nRet == NL_OK || nRet == NL_RFACTIVATE_FAIL ){
                break;
            }
        }

        if(HAS_CARD_IC(stCardReaderParam->readCardMode)){
            if((nRet = __read_card(stCardReaderParam,pastCardReaders[CARDREADER_INDEX_IC], (StCardInfo*)pstCardInfoOutput)) == NL_OK){
                break;
            }
        }

        if(HAS_CARD_MAG(stCardReaderParam->readCardMode)){
            nRet = __read_card(stCardReaderParam,pastCardReaders[CARDREADER_INDEX_MAG], (StCardInfo*)pstCardInfoOutput);
            if(nRet == NL_OK || nRet == NL_MAGREAD_FAIL){
                break;
            }
        }
        if(Card_GetCancelFlag() == 1){
            nRet = NL_CANCEL;
            break;
        }
        if(__GetTimeDistance(&startTime) >= stCardReaderParam->timeout*1000){
            nRet = NL_ERR_TIMEOUT;
            break;
        }
    }
    LOGE_FMT(">>>__cardExec2 ret[%d]", nRet);
    return nRet;
}

static int __cardExec(StCardReaderParam* stCardReaderParam,StCardReader* pastCardReaders[], void* pstCardInfoOutput)
{
    if(stCardReaderParam==NULL||pstCardInfoOutput==NULL){
        LOGE_FMT(">>>stCardReaderParam[%d] pstCardInfoOutput[%d] return NL_FAILED!",stCardReaderParam,pstCardInfoOutput);
        ERRMSG(SDK_ERR_PARAM,CARDREADER_OPEN);
        return NL_FAILED;
    }
    StCardReader* pStCurrentReader = NULL;
    StCardInfo* cardInfo = (StCardInfo*)pstCardInfoOutput;
    cardInfo->validLen = 0;

    if(stCardReaderParam->searchCardRule == 0x04 || stCardReaderParam->searchCardRule == 0x06){
        _IF_COND_TRUE_THEN_DO(HAS_CARD_RFID(stCardReaderParam->readCardMode), \
		pastCardReaders[CARDREADER_INDEX_RFID]==NULL?NL_FAILED:\
		pastCardReaders[CARDREADER_INDEX_RFID]->openCardDev(stCardReaderParam,pstCardInfoOutput));
        if(cardInfo->validLen != 0){
            LOGE_FMT(">>>find the rf card! [%d]",stCardReaderParam->searchCardRule);
            return NL_OK;
        } else{
            if(stCardReaderParam->searchCardRule == 0x06){
                if (stCardReaderParam->vasEnable && stCardReaderParam->vasParamLen > 0){
                    uint32_t CARD_TYPE_A = 0x00000001,CARD_TYPE_B = 0x00000002,CARD_TYPE_V = 0x00000008;
                    //if(!EXEC_NDK("NDK_RfidSetDetectType(3)", NDK_RfidSetDetectType(CARD_TYPE_A | CARD_TYPE_B | CARD_TYPE_V), NDK_OK,CARDREADER_OPEN)) return NL_FAILED;
                    if (!EXEC_NDK("NDK_RfidPiccType ADDV.", NDK_RfidPiccType(NDK_RFID_ADDV), NDK_OK,CARDREADER_OPEN)) return NL_FAILED;
                    if (!EXEC_NDK("NDK_RfidSetPiccParam", NDK_RfidSetPiccParam(0x01,stCardReaderParam->vasParamLen,stCardReaderParam->vasParam), NDK_OK,CARDREADER_OPEN))  return NL_FAILED;
                }else{
                    if (!EXEC_NDK("NDK_RfidPiccType", NDK_RfidPiccType(NDK_RFID_AB), NDK_OK,CARDREADER_OPEN)) return NL_FAILED;
                }
            }
        }

        _IF_COND_TRUE_THEN_DO(HAS_CARD_IC(stCardReaderParam->readCardMode),\
		pastCardReaders[CARDREADER_INDEX_IC]==NULL?NL_FAILED:\
		pastCardReaders[CARDREADER_INDEX_IC]->openCardDev(stCardReaderParam,pstCardInfoOutput));
        if(cardInfo->validLen != 0){
            LOGE_FMT(">>>find the ic card!");
            return NL_OK;
        }

        _IF_COND_TRUE_THEN_DO(HAS_CARD_MAG(stCardReaderParam->readCardMode), \
		pastCardReaders[CARDREADER_INDEX_MAG]==NULL?NL_FAILED:\
		pastCardReaders[CARDREADER_INDEX_MAG]->openCardDev(stCardReaderParam,pstCardInfoOutput));
    }else{
        _IF_COND_TRUE_THEN_DO(HAS_CARD_MAG(stCardReaderParam->readCardMode), \
		pastCardReaders[CARDREADER_INDEX_MAG]==NULL?NL_FAILED:\
		pastCardReaders[CARDREADER_INDEX_MAG]->openCardDev(stCardReaderParam,pstCardInfoOutput));

        _IF_COND_TRUE_THEN_DO(HAS_CARD_IC(stCardReaderParam->readCardMode),\
		pastCardReaders[CARDREADER_INDEX_IC]==NULL?NL_FAILED:\
		pastCardReaders[CARDREADER_INDEX_IC]->openCardDev(stCardReaderParam,pstCardInfoOutput));

        if(cardInfo->validLen != 0){
            LOGE_FMT(">>>find the ic card!");
            return NL_OK;
        }

        _IF_COND_TRUE_THEN_DO(HAS_CARD_RFID(stCardReaderParam->readCardMode), \
		pastCardReaders[CARDREADER_INDEX_RFID]==NULL?NL_FAILED:\
		pastCardReaders[CARDREADER_INDEX_RFID]->openCardDev(stCardReaderParam,pstCardInfoOutput));

        if(cardInfo->validLen != 0){
            LOGE_FMT(">>>find the rf card!");
            return NL_OK;
        }
    }

    int eventList = 0;
    int nRet = __cardDealEvent(stCardReaderParam,pastCardReaders,&pStCurrentReader,&eventList);
    LOGD_FMT(">>>__cardDealEvent nRet[%d]",nRet);
    if(eventList != 0){
        LOGE_FMT(">>>remove events! eventList[%d]",eventList);
        Event_Remove(eventList);
    }
    if(nRet == NL_ERR_TIMEOUT){
        LOGE_FMT(">>>__cardDealEvent timeout!");
        return nRet;
    }else if(nRet == NL_CANCEL){
        LOGE_FMT(">>>__cardDealEvent cancel!");
        return nRet;
    }else if(nRet == NL_ERR_PARAMETERS){
        LOGE_FMT(">>>__cardDealEvent param error!");
        return nRet;
    }else if(nRet < 0){
        LOGE_FMT(">>>__cardDealEvent error! code=%d", nRet);
        return NL_FAILED;
    }
    nRet = NL_OK;
    if(!LOGGER_IS_EXPECT_RET(__read_card(stCardReaderParam,pStCurrentReader, (StCardInfo*)pstCardInfoOutput), NL_OK)){
        LOGE_FMT(">>>Read the card failure!");
        nRet = NL_FAILED;
    }
    return nRet;
}

static int __cardExit(StCardReaderParam* stCardReaderParam,StCardReader* readers[])
{
    _IF_COND_TRUE_THEN_DO(HAS_CARD_MAG(stCardReaderParam->readCardMode),\
		__get_magiccard_reader(readers)==NULL?NL_FAILED:\
		__get_magiccard_reader(readers)->closeCardDev(stCardReaderParam));
    _IF_COND_TRUE_THEN_DO(HAS_CARD_IC(stCardReaderParam->readCardMode),\
		__get_iccard_reader(readers)==NULL?NL_FAILED:\
		__get_iccard_reader(readers)->closeCardDev(stCardReaderParam));
    _IF_COND_TRUE_THEN_DO(HAS_CARD_RFID(stCardReaderParam->readCardMode),\
		__get_rfcard_reader(readers)==NULL?NL_FAILED:\
		__get_rfcard_reader(readers)->closeCardDev(stCardReaderParam));

    LOGGER_IS_EXPECT_RET(CardMgr_ReleaseReader(__get_magiccard_reader(readers)),  NL_OK);
    LOGGER_IS_EXPECT_RET(CardMgr_ReleaseReader(__get_iccard_reader(readers)), 	 NL_OK);
    LOGGER_IS_EXPECT_RET(CardMgr_ReleaseReader(__get_rfcard_reader(readers)),	 NL_OK);
    return NL_OK;
}
int CardReader_Open(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen)
{
    int dataLen = 0;
    char headCode[2];
    StCardReaderParam stCardReaderParam;
    CardMgr_GetOpenCardParam(&stCardReaderParam,pbuf,buf_len);
    stCardReaderParam.targetCardType = OPENCARD_TYPE_NONE;
    int nReadCardMode = stCardReaderParam.readCardMode;
    if(CardLock(nReadCardMode,CARDREADER_OPEN)){
        responseCmd(pOut,0,outLen,CMD_ERR_OTHER);
        return 0;
    }
    g_openFlag = 1;
    Card_SetReadCardMode(nReadCardMode);

//    StCardInfo* pstCardInfo = (StCardInfo*)CardMgr_CreateCardInfo();
    StCardInfo pstCardInfo;
    memset(&pstCardInfo,0,sizeof(pstCardInfo));
    StCardReader* pastCardReaders[3]={0};
    int ret = CardMgr_CreateCardReaders(nReadCardMode,&pastCardReaders);
    if(ret != NL_OK){
        dataLen =0;
        ERRMSG(SDK_ERR_MALLOC_FAILED,CARDREADER_OPEN);
        memcpy(headCode,CMD_ERR_OTHER,2);
        goto ON_ERR;
    }
    if(stCardReaderParam.searchCardRule == 3){
        ret = __cardExec2(&stCardReaderParam,pastCardReaders,&pstCardInfo);
    } else{
        ret = __cardExec(&stCardReaderParam,pastCardReaders,&pstCardInfo);
    }
    dataLen = pstCardInfo.validLen;
    LOGD_FMT(">>>__cardExec ret[%d] dataLen[%d]",ret,dataLen);
    if(ret == NL_OK && dataLen != 0){
        memcpy(pOut+2,pstCardInfo.data,dataLen);
        memcpy(headCode,CMD_OK,2);
    }else if(ret == NL_ERR_ACK && dataLen == 2){
        dataLen =0;
        memcpy(headCode,pstCardInfo.data,2);
    }else if(ret == NL_ERR_TIMEOUT){
        dataLen =0;
        ERRMSG(SDK_ERR_TIMEOUT,CARDREADER_OPEN);
        memcpy(headCode,CMD_ERR_TIMEOUT,2);
    }else if(ret == NL_CANCEL){
        dataLen =0;
        ERRMSG(SDK_ERR_CANCEL,CARDREADER_OPEN);
        memcpy(headCode,CMD_CANCEL,2);
    }else if(ret == NL_ERR_PARAMETERS){
        dataLen =0;
        ERRMSG(SDK_ERR_PARAM,CARDREADER_OPEN);
        memcpy(headCode,CMD_ERR_OTHER,2);
    }else{
        dataLen =0;
        memcpy(headCode,CMD_ERR_OTHER,2);
    }
ON_ERR:
    CardReader_NotifyJava();
    __cardExit(&stCardReaderParam,pastCardReaders);
    responseCmd(pOut,dataLen,outLen,headCode);
    CardUnLock(nReadCardMode);
    THREAD_COND_SIGNAL(THREAD_COND_INDEX_CARD_CLOSECARD,NULL);
    g_openFlag = 0;
    return 0;
}

int CardReader_Cancel(int waitTimeMs)
{
    LOGE_FMT("START");
    Card_SetCancelFlag(1);

    Event_Cancel();

    int readCardMode = Card_GetReadCardMode();
    CardUnLock(readCardMode);
    if(g_openFlag == 1){
        g_openFlag = 0;
        THREAD_COND_INIT(THREAD_COND_INDEX_CARD_CLOSECARD);
        THREAD_COND_TIMEDWAIT(THREAD_COND_INDEX_CARD_CLOSECARD,waitTimeMs,NULL);
    }
    Card_SetCancelFlag(0);
    LOGE_FMT("END");
    return 0;
}


static jclass crhelperClass =NULL;
static jmethodID crhelperNotifyId = NULL;
extern JavaVM *gJavaVM;
void CardReader_GetMethodID(JNIEnv *env){
    jclass temp = (*env)->FindClass(env, "com/newland/sdk/me/module/cardreader/CardReaderHelper");
    if(temp == NULL){
        LOGE_FMT("CardReaderHelper==NULL");
    }
    crhelperClass = (jclass)(*env)->NewGlobalRef(env,temp);
    crhelperNotifyId = (*env)->GetStaticMethodID(env,crhelperClass,"openCardReaderNotify","()V");
}
void CardReader_NotifyJava(){
    if(crhelperClass == NULL || crhelperNotifyId == NULL){
        LOGE_FMT("crhelperClass[%d] crhelperNotifyId[%d]",crhelperClass,crhelperNotifyId);
        return;
    }
    JNIEnv *env = NULL;
    jboolean isAttached = JNI_FALSE;
    int ret = (*gJavaVM)->GetEnv(gJavaVM,(void **) &env, JNI_VERSION_1_4);
    if(ret < 0 ) {
        ret =  (*gJavaVM)->AttachCurrentThread(gJavaVM,(JNIEnv **) &env, NULL);
        if (ret < 0) {
            LOGE_FMT(">>>AttachCurrentThread error.");
            return;
        }
        isAttached = JNI_TRUE;
    }
    (*env)->CallStaticVoidMethod(env,crhelperClass,crhelperNotifyId);

    if(isAttached)
        (*gJavaVM)->DetachCurrentThread(gJavaVM);
}