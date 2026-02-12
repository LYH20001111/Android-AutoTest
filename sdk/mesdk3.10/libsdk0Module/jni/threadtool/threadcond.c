/**
 * Author by wuhh, Date on 2019/3/22 0022.
 */
#include "threadcond.h"
#include "log.h"
#define DEBUG_COND  0
ST_THREAD_COND_CTL* threadCondCtls[THREAD_COND_CTL_MAX]={NULL,NULL,NULL,NULL,NULL,
                                                         NULL,NULL,NULL,NULL,NULL,
                                                         NULL,NULL};

static int condInit(ST_THREAD_COND *stcond){
    if(stcond==NULL){
        LOGD_FMT(">>>stcond null.");
        return -1;
    }
    memset(&stcond->msg,0,sizeof(ST_THREAD_COND_MSG));
    pthread_mutex_init(&stcond->mutex,NULL);
    pthread_cond_init(&stcond->cond,NULL);
    #if DEBUG_COND
    LOGD_FMT(">>>init succ.");
    #endif
    return 1;
}
static int condSignal(ST_THREAD_COND *stcond,ST_THREAD_COND_MSG *msg){
    if(stcond==NULL){
        LOGD_FMT(">>>stcond null.");
        return -1;
    }
    pthread_mutex_lock(&stcond->mutex);
    if(msg != NULL){
        stcond->msg.pinInputStatus = msg->pinInputStatus;
        stcond->msg.cardEvent = msg->cardEvent;
    }
    pthread_cond_signal(&stcond->cond);
    pthread_mutex_unlock(&stcond->mutex);
    #if DEBUG_COND
    LOGD_FMT(">>>signal succ.");
    #endif
    return 1;
}
static int condBroadcast(ST_THREAD_COND *stcond,ST_THREAD_COND_MSG *msg){
    if(stcond==NULL){
        LOGD_FMT(">>>stcond null.");
        return -1;
    }
    pthread_mutex_lock(&stcond->mutex);
    if(msg != NULL){
        stcond->msg.pinInputStatus = msg->pinInputStatus;
        stcond->msg.cardEvent = msg->cardEvent;
    }
    pthread_cond_broadcast(&stcond->cond);
    pthread_mutex_unlock(&stcond->mutex);
    #if DEBUG_COND
    LOGD_FMT(">>>broadcast succ.");
    #endif
    return 1;
}

static int condWait(ST_THREAD_COND *stcond,ST_THREAD_COND_MSG *msg){
    if(stcond==NULL){
        LOGD_FMT(">>>stcond null.");
        return -1;
    }
    #if DEBUG_COND
    LOGD_FMT(">>>pid[%d] threadid[%lu] wait ing ...",getpid(),pthread_self());
    #endif
    pthread_mutex_lock(&stcond->mutex);
    pthread_cond_wait(&stcond->cond,&stcond->mutex);
    if(msg != NULL){
        *msg = stcond->msg;
    }
    pthread_mutex_unlock(&stcond->mutex);
    #if DEBUG_COND
    LOGD_FMT(">>>pid[%d] threadid[%lu] wait end.",getpid(),pthread_self());
    #endif
    return 1;
}
static int condTimedwait(ST_THREAD_COND *stcond,int timeoutMs,ST_THREAD_COND_MSG *msg){
    if(stcond==NULL || timeoutMs < 0){
        LOGD_FMT(">>>stcond[%d] timeoutMs[%d].",timeoutMs);
        return -1;
    }
    #if DEBUG_COND
    LOGD_FMT(">>>timeoutMs[%d]",timeoutMs);
    #endif
    struct timespec abstime;
    struct timeval now;
    gettimeofday(&now, NULL);
    int nsec = now.tv_usec * 1000 + (timeoutMs % 1000) * 1000000;
    abstime.tv_nsec = nsec % 1000000000;
    abstime.tv_sec = now.tv_sec + nsec / 1000000000 + timeoutMs / 1000;
    #if DEBUG_COND
    LOGD_FMT(">>>pid[%d] threadid[%lu] timedwait ing ...",getpid(),pthread_self());
    #endif
    pthread_mutex_lock(&stcond->mutex);
    pthread_cond_timedwait(&stcond->cond, &stcond->mutex,&abstime);
    if(msg != NULL){
        *msg = stcond->msg;
    }
    pthread_mutex_unlock(&stcond->mutex);
    #if DEBUG_COND
    LOGD_FMT(">>>pid[%d] threadid[%lu] timedwait end.",getpid(),pthread_self());
    #endif
    return 1;
}
static int condDestroy(ST_THREAD_COND *stcond){
    if(stcond==NULL){
        LOGD_FMT(">>>stcond null.");
        return -1;
    }
    pthread_mutex_destroy(&stcond->mutex);
    pthread_cond_destroy(&stcond->cond);
    #if DEBUG_COND
    LOGD_FMT(">>>destroy succ.");
    #endif
    return 1;
}

void threadCondCtlsCreate(){
    int i = 0;
    for(i=0;i<THREAD_COND_CTL_MAX;i++) {
        ST_THREAD_COND_CTL *control = (ST_THREAD_COND_CTL *) malloc(sizeof(ST_THREAD_COND_CTL));
        if (control == NULL) {
            LOGD_FMT(">>>malloc[%d] ST_THREAD_COND_CTL null.",i);
            return;
        }
        memset(control, 0, sizeof(ST_THREAD_COND_CTL));
        ST_THREAD_COND *stcond = (ST_THREAD_COND*)malloc(sizeof(ST_THREAD_COND));
        if (stcond == NULL) {
            LOGD_FMT(">>>malloc[%d] ST_THREAD_COND null.",i);
            return;
        }
        memset(stcond,0, sizeof(ST_THREAD_COND));
        control->stcond = stcond;
        control->init = condInit;
        control->signal = condSignal;
        control->broadcast = condBroadcast;
        control->wait = condWait;
        control->timedwait = condTimedwait;
        control->destroy = condDestroy;
        threadCondCtls[i] = control;
        threadCondCtls[i]->init(threadCondCtls[i]->stcond);
        #if DEBUG_COND
        LOGD_FMT(">>>ST_THREAD_COND_CTL index[%d] addr[%lu] create succ.",i,threadCondCtls[i]);
        #endif
    }
}

void threadCondCtlsDestroy(){
    int i = 0;
    for(i=0;i<THREAD_COND_CTL_MAX;i++) {
        if(threadCondCtls[i] == NULL){
            LOGD_FMT(">>>destroy[%d] ST_THREAD_COND_CTL continue.",i);
            continue;
        }
        threadCondCtls[i]->destroy(threadCondCtls[i]->stcond);
        if(threadCondCtls[i]->stcond!=NULL){
            free(threadCondCtls[i]->stcond);
        }
        #if DEBUG_COND
        LOGD_FMT(">>>ST_THREAD_COND_CTL index[%d] addr[%lu] destroy succ.",i,threadCondCtls[i]);
        #endif
        free(threadCondCtls[i]);
        threadCondCtls[i] = NULL;
    }
}

void* getThreadCondDesc(int index){
    if(index == THREAD_COND_INDEX_PIN_PININPUT){
        return "THREAD_COND_INDEX_PIN_PININPUT";
    }else if(index == THREAD_COND_INDEX_CARD_CLOSECARD){
        return "THREAD_COND_INDEX_CARD_CLOSECARD";
    }else if(index == THREAD_COND_INDEX_RFID_POWEROFF){
        return "THREAD_COND_INDEX_RFID_POWEROFF";
    }else if(index == THREAD_COND_INDEX_CARD_MAG){
        return "THREAD_COND_INDEX_CARD_MAG";
    }else if(index == THREAD_COND_INDEX_CARD_IC){
        return "THREAD_COND_INDEX_CARD_IC";
    }else if(index == THREAD_COND_INDEX_CARD_RFID){
        return "THREAD_COND_INDEX_CARD_RFID";
    }else if(index == THREAD_COND_INDEX_CARD_MAG_IC){
        return "THREAD_COND_INDEX_CARD_MAG_IC";
    } else if(index == THREAD_COND_INDEX_CARD_MAG_RFID){
        return "THREAD_COND_INDEX_CARD_MAG_RFID";
    } else if(index == THREAD_COND_INDEX_CARD_IC_RFID){
        return "THREAD_COND_INDEX_CARD_IC_RFID";
    }else if(index == THREAD_COND_INDEX_CARD_MAG_IC_RFID){
        return "THREAD_COND_INDEX_CARD_MAG_IC_RFID";
    }else if(index == THREAD_COND_INDEX_RFID_POWERON){
        return "THREAD_COND_INDEX_RFID_POWERON";
    }else if(index == THREAD_COND_INDEX_PIN_NOTIFY){
        return "THREAD_COND_INDEX_PIN_NOTIFY";
    }
    return "THREAD_COND_INDEXX_UNKNOWN";
}