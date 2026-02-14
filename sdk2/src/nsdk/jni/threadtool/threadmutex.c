/**
 * Author by wuhh, Date on 2019/3/23 0023.
 */
#include "threadmutex.h"
#include "threadtool.h"
#define DEBUG_MUTEX   0
ST_THREAD_MUTEX_CTL *threadMutexCtls[THREAD_MUTEX_CTL_MAX]={NULL,NULL,NULL,NULL,NULL,
                                                            NULL,NULL,NULL,NULL,NULL};

static int mutexInit(ST_THREAD_MUTEX *stmutex){
    if(stmutex == NULL){
        LOGD_FMT(">>>stmutex null.");
        return -1;
    }
    pthread_mutex_init(&stmutex->mutex,NULL);
    #if DEBUG_MUTEX
    LOGD_FMT(">>>init succ.[%s]", strerror(errno));
    #endif
    return 1;
}

static int mutexLock(ST_THREAD_MUTEX *stmutex){
    if(stmutex == NULL){
        LOGD_FMT(">>>stmutex null.");
        return -1;
    }
    #if DEBUG_MUTEX
    LOGD_FMT(">>>lock pid[%d] threadid[%lu]",getpid(),pthread_self());
    #endif
    return pthread_mutex_lock(&stmutex->mutex);
}

static int mutexUnlock(ST_THREAD_MUTEX *stmutex){
    if(stmutex == NULL){
        LOGD_FMT(">>>stmutex null.");
        return -1;
    }
    #if DEBUG_MUTEX
    LOGD_FMT(">>>unlock pid[%d] threadid[%lu]",getpid(),pthread_self());
    #endif
    return pthread_mutex_unlock(&stmutex->mutex);
}

static int mutexTrylock(ST_THREAD_MUTEX *stmutex){
    if(stmutex == NULL){
        LOGD_FMT(">>>stmutex null.");
        return -1;
    }
    int ret = pthread_mutex_trylock(&stmutex->mutex);
    #if DEBUG_MUTEX
    LOGD_FMT(">>>trylock pid[%d] threadid[%lu] ret[%d][%s]",getpid(),pthread_self(),ret,strerror(ret));
    #endif
    return ret;
}

static int mutexDestroy(ST_THREAD_MUTEX *stmutex){
    if(stmutex == NULL){
        LOGD_FMT(">>>stmutex null.");
        return -1;
    }
    pthread_mutex_destroy(&stmutex->mutex);
    #if DEBUG_MUTEX
    LOGD_FMT(">>>destroy succ.[%s]",strerror(errno));
    #endif
    return 1;
}

void threadMutexCtlsCreate(){
    int i = 0;
    for(i=0;i<THREAD_MUTEX_CTL_MAX;i++) {
        ST_THREAD_MUTEX_CTL *control = (ST_THREAD_MUTEX_CTL*)malloc(sizeof(ST_THREAD_MUTEX_CTL));
        if (control == NULL) {
            LOGD_FMT(">>>malloc[%d] ST_THREAD_MUTEX_CTL null.",i);
            return;
        }
        memset(control, 0, sizeof(ST_THREAD_MUTEX_CTL));
        ST_THREAD_MUTEX *stmutex = (ST_THREAD_MUTEX*)malloc(sizeof(ST_THREAD_MUTEX));
        if (stmutex == NULL) {
            LOGD_FMT(">>>malloc[%d] ST_THREAD_MUTEX null.",i);
            return;
        }
        memset(stmutex,0, sizeof(ST_THREAD_MUTEX));
        control->stmutex = stmutex;
        control->init = mutexInit;
        control->lock = mutexLock;
        control->unlock = mutexUnlock;
        control->trylock = mutexTrylock;
        control->destroy = mutexDestroy;
        threadMutexCtls[i] = control;
        threadMutexCtls[i]->init(threadMutexCtls[i]->stmutex);
        #if DEBUG_MUTEX
        LOGD_FMT(">>>ST_THREAD_MUTEX_CTL index[%d] addr[%lu] create succ.",i,threadMutexCtls[i]);
        #endif
    }
}

void threadMutexCtlsDestroy(){
    int i = 0;
    for(i=0;i<THREAD_MUTEX_CTL_MAX;i++) {
        if(threadMutexCtls[i] == NULL){
            LOGD_FMT(">>>destroy[%d] ST_THREAD_MUTEX_CTL continue.",i);
            continue;
        }
        threadMutexCtls[i]->destroy(threadMutexCtls[i]->stmutex);
        if(threadMutexCtls[i]->stmutex!=NULL){
            free(threadMutexCtls[i]->stmutex);
        }
        #if DEBUG_MUTEX
        LOGD_FMT(">>>ST_THREAD_MUTEX_CTL index[%d] addr[%lu] destroy succ.",i,threadMutexCtls[i]);
        #endif
        free(threadMutexCtls[i]);
        threadMutexCtls[i] = NULL;
    }
}

void* getThreadMutexDesc(int index){
    if(index == THREAD_MUTEX_INDEX_CARDS){
        return "THREAD_MUTEX_INDEX_CARDS";
    } else if(index == THREAD_MUTEX_INDEX_FILE_W){
        return "THREAD_MUTEX_INDEX_FILE_W";
    }else if(index == THREAD_MUTEX_INDEX_CARD_MAG){
        return "THREAD_MUTEX_INDEX_CARD_MAG";
    }else if(index == THREAD_MUTEX_INDEX_CARD_IC){
        return "THREAD_MUTEX_INDEX_CARD_IC";
    }else if(index == THREAD_MUTEX_INDEX_CARD_RF){
        return "THREAD_MUTEX_INDEX_CARD_RF";
    }else if(index == THREAD_MUTEX_INDEX_READCARDMODE){
        return "THREAD_MUTEX_INDEX_READCARDMODE";
    }else if(index == THREAD_MUTEX_INDEX_OPENCARD_CANCEL){
        return "THREAD_MUTEX_INDEX_OPENCARD_CANCEL";
    }else if(index == THREAD_MUTEX_INDEX_POWERON_CANCEL){
        return "THREAD_MUTEX_INDEX_POWERON_CANCEL";
    }else if(index == THREAD_MUTEX_INDEX_ERRINFO){
        return "THREAD_MUTEX_INDEX_ERRINFO";
    }else if(index == THREAD_MUTEX_INDEX_PINEVENTSYNC){
        return "THREAD_MUTEX_INDEX_PINEVENTSYNC";
    }
    return "THREAD_MUTEX_INDEXX_UNKNOWN";
}