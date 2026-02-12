/**
 * Author by wuhh, Date on 2019/3/23 0023.
 */
#ifndef _THREAD_MUTEX_H_
#define _THREAD_MUTEX_H_

#include <pthread.h>
#include <errno.h>
#include <unistd.h>

#define THREAD_MUTEX_CTL_MAX               10
#define THREAD_MUTEX_INDEX_CARDS           0
#define THREAD_MUTEX_INDEX_FILE_W          1
#define THREAD_MUTEX_INDEX_CARD_MAG        2
#define THREAD_MUTEX_INDEX_CARD_IC         3
#define THREAD_MUTEX_INDEX_CARD_RF         4
#define THREAD_MUTEX_INDEX_READCARDMODE    5
#define THREAD_MUTEX_INDEX_OPENCARD_CANCEL 6
#define THREAD_MUTEX_INDEX_POWERON_CANCEL  7
#define THREAD_MUTEX_INDEX_ERRINFO         8
#define THREAD_MUTEX_INDEX_PINEVENTSYNC    9

extern void  threadMutexCtlsCreate();
extern void  threadMutexCtlsDestroy();
extern void* getThreadMutexDesc(int index);

typedef struct {
    pthread_mutex_t mutex;
}ST_THREAD_MUTEX;

typedef struct {
    ST_THREAD_MUTEX *stmutex;
    int (*init)(void*);
    int (*lock)(void*);
    int (*unlock)(void*);
    int (*trylock)(void*);
    int (*destroy)(void*);
}ST_THREAD_MUTEX_CTL;
#endif //_THREAD_MUTEX_H_
