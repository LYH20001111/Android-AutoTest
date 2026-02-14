/**
 * Author by wuhh, Date on 2019/3/22 0022.
 */
#ifndef _THREAD_COND_CTL_H_
#define _THREAD_COND_CTL_H_

#include "threadcond.h"
#include "threadmutex.h"
#include "log.h"

extern ST_THREAD_COND_CTL* threadCondCtls[THREAD_COND_CTL_MAX];

#define THREAD_COND_CTLS_CREATE  threadCondCtlsCreate()
#define THREAD_COND_CTLS_DESTROY threadCondCtlsDestroy()
#define THREAD_COND_DESC(index)  getThreadCondDesc(index)

#define THREAD_COND_INIT(index) {                                                       \
    LOGD_FMT(">>>[%s] INIT",THREAD_COND_DESC(index));                                   \
    if(index < THREAD_COND_CTL_MAX && threadCondCtls[index] != NULL){                   \
        threadCondCtls[index]->init(threadCondCtls[index]->stcond);                     \
    }                                                                                   \
}
#define THREAD_COND_SIGNAL(index,msg) {                                                 \
    LOGD_FMT(">>>[%s] SIGNAL",THREAD_COND_DESC(index));                                 \
    if(index < THREAD_COND_CTL_MAX && threadCondCtls[index] != NULL){                   \
        threadCondCtls[index]->signal(threadCondCtls[index]->stcond,msg);               \
    }                                                                                   \
}
#define THREAD_COND_BROADCAST(index,msg) {                                              \
    LOGD_FMT(">>>[%s] BROADCAST",THREAD_COND_DESC(index));                              \
    if(index < THREAD_COND_CTL_MAX && threadCondCtls[index] != NULL){                   \
        threadCondCtls[index]->broadcast(threadCondCtls[index]->stcond,msg);            \
    }                                                                                   \
}
#define THREAD_COND_WAIT(index,msg) {                                                   \
    LOGD_FMT(">>>[%s] WAIT",THREAD_COND_DESC(index));                                   \
    if(index < THREAD_COND_CTL_MAX && threadCondCtls[index] != NULL){                   \
        threadCondCtls[index]->wait(threadCondCtls[index]->stcond,msg);                 \
    }                                                                                   \
}
#define THREAD_COND_TIMEDWAIT(index,timeOutMs,msg) {                                    \
    if(index!=THREAD_COND_INDEX_PIN_PININPUT)                                           \
        LOGD_FMT(">>>[%s] TIMEDWAIT",THREAD_COND_DESC(index));                          \
    if(index < THREAD_COND_CTL_MAX && threadCondCtls[index] != NULL){                   \
        threadCondCtls[index]->timedwait(threadCondCtls[index]->stcond,timeOutMs,msg);  \
    }                                                                                   \
}
#define THREAD_COND_DESTROY(index) {                                                    \
    LOGD_FMT(">>>[%s] DESTROY",THREAD_COND_DESC(index));                                \
    if(index < THREAD_COND_CTL_MAX && threadCondCtls[index] != NULL){                   \
        threadCondCtls[index]->destroy(threadCondCtls[index]->stcond);                  \
    }                                                                                   \
}
/*********************************************************************************/
extern ST_THREAD_MUTEX_CTL *threadMutexCtls[THREAD_MUTEX_CTL_MAX];

#define THREAD_MUTEX_CTLS_CREATE  threadMutexCtlsCreate()
#define THREAD_MUTEX_CTLS_DESTROY threadMutexCtlsDestroy()
#define THREAD_MUTEX_DESC(index)  getThreadMutexDesc(index)

#define THREAD_MUTEX_INIT(index) {                                                \
    LOGD_FMT(">>>[%s] INIT",THREAD_MUTEX_DESC(index));                            \
    if(index < THREAD_MUTEX_CTL_MAX && threadMutexCtls[index] != NULL){           \
        threadMutexCtls[index]->init(threadMutexCtls[index]->stmutex);            \
    }                                                                             \
}
#define THREAD_MUTEX_LOCK(index) {                                                \
    if(index!=THREAD_MUTEX_INDEX_PINEVENTSYNC)                                    \
        LOGD_FMT(">>>[%s] LOCK",THREAD_MUTEX_DESC(index));                        \
    if(index < THREAD_MUTEX_CTL_MAX && threadMutexCtls[index] != NULL){           \
        threadMutexCtls[index]->lock(threadMutexCtls[index]->stmutex);            \
    }                                                                             \
}
#define THREAD_MUTEX_UNLOCK(index) {                                              \
    if(index!=THREAD_MUTEX_INDEX_PINEVENTSYNC)                                    \
        LOGD_FMT(">>>[%s] UNLOCK",THREAD_MUTEX_DESC(index));                      \
    if(index < THREAD_MUTEX_CTL_MAX && threadMutexCtls[index] != NULL){           \
        threadMutexCtls[index]->unlock(threadMutexCtls[index]->stmutex);          \
    }                                                                             \
}
static int __cardTryLock(int index){
    LOGD_FMT(">>>[%s] TRYLOCK2",THREAD_MUTEX_DESC(index));
    if(index < THREAD_MUTEX_CTL_MAX && threadMutexCtls[index] != NULL){
        return threadMutexCtls[index]->trylock(threadMutexCtls[index]->stmutex);
    } else{
        return -1;
    }
}

#define THREAD_MUTEX_TRYLOCK2(index) __cardTryLock(index)

#define THREAD_MUTEX_TRYLOCK(index) {                                             \
    LOGD_FMT(">>>[%s] TRYLOCK",THREAD_MUTEX_DESC(index));                         \
    if(index < THREAD_MUTEX_CTL_MAX && threadMutexCtls[index] != NULL){           \
        threadMutexCtls[index]->trylock(threadMutexCtls[index]->stmutex);         \
    }                                                                             \
}

#define THREAD_MUTEX_DESTROY(index) {                                             \
    LOGD_FMT(">>>[%s] DESTROY",THREAD_MUTEX_DESC(index));                         \
    if(index < THREAD_MUTEX_CTL_MAX && threadMutexCtls[index] != NULL){           \
        threadMutexCtls[index]->destroy(threadMutexCtls[index]->stmutex);         \
    }                                                                             \
}
#endif