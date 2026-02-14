/**
 * Author by wuhh, Date on 2019/3/22 0022.
 */
#ifndef _THREAD_COND_H_
#define _THREAD_COND_H_
#include <pthread.h>
#include <unistd.h>

#define THREAD_COND_CTL_MAX                    12
#define THREAD_COND_INDEX_PIN_PININPUT         0
#define THREAD_COND_INDEX_CARD_CLOSECARD       1
#define THREAD_COND_INDEX_RFID_POWEROFF        2
#define THREAD_COND_INDEX_CARD_MAG             3//MAG
#define THREAD_COND_INDEX_CARD_IC              4//IC
#define THREAD_COND_INDEX_CARD_RFID            5//RFID
#define THREAD_COND_INDEX_CARD_MAG_IC          6//MAG_IC
#define THREAD_COND_INDEX_CARD_MAG_RFID        7//MAG_RFID
#define THREAD_COND_INDEX_CARD_IC_RFID         8//IC_RFID
#define THREAD_COND_INDEX_CARD_MAG_IC_RFID     9//MAG_IC_RFID
#define THREAD_COND_INDEX_RFID_POWERON         10
#define THREAD_COND_INDEX_PIN_NOTIFY           11

extern void threadCondCtlsCreate();
extern void threadCondCtlsDestroy();
extern void* getThreadCondDesc(int index);

typedef struct {
    uint pinInputStatus;
    uint cardEvent;
}ST_THREAD_COND_MSG;
typedef struct{
    pthread_mutex_t mutex;
    pthread_cond_t cond;
    ST_THREAD_COND_MSG msg;
}ST_THREAD_COND;

typedef struct{
    ST_THREAD_COND *stcond;
    int (*init)(void*);
    int (*signal)(void*,void*);
    int (*broadcast)(void*,void*);
    int (*wait)(void*,void*);
    int (*timedwait)(void*,void*,void*);
    int (*destroy)(void*);
}ST_THREAD_COND_CTL;
#endif