#ifndef NL_NDK_EVENT_H
#define NL_NDK_EVENT_H

#include "cardmgr.h"
#include "nllogger.h"

////以下thread cond相关
//typedef struct __stEventNotifier
//{
//    pthread_cond_t  event_cond;
//    pthread_mutex_t mutex;
//    int             eventNum;
//}StEventNotifier;
//
//static inline int __notifier_lock(StEventNotifier* notifier)
//{
//    return pthread_mutex_lock(&notifier->mutex);
//}
//
//static inline int __notifier_unlock(StEventNotifier* notifier)
//{
//    return pthread_mutex_unlock(&notifier->mutex);
//}
//
//static inline int __notifier_waitforCondion(StEventNotifier* notifier)
//{
//    return  pthread_cond_wait(&notifier->event_cond, &notifier->mutex);
//}
//
//static inline int __notifier_signalCondion(StEventNotifier* notifier)
//{
//    return pthread_cond_signal(&notifier->event_cond);
//}
//
//static inline int notifier_init(StEventNotifier* notifier)
//{
//    LOGGER_IS_EXPECT_RET(pthread_mutex_init(&notifier->mutex, NULL), 0);
//    LOGGER_IS_EXPECT_RET(pthread_cond_init(&notifier->event_cond, NULL), 0);
//    notifier->eventNum = 0;
//    return 0;
//}
//
//static inline int notifier_put_event(StEventNotifier* notifier, int eventNum)
//{
//    LOGGER_IS_EXPECT_RET(__notifier_lock(notifier), 0);
//    notifier->eventNum = eventNum;
//    LOGGER_IS_EXPECT_RET(__notifier_signalCondion(notifier), 0);
//    LOGGER_IS_EXPECT_RET(__notifier_unlock(notifier), 0);
//    return 0;
//}
//
//static inline int notifier_get_event(StEventNotifier* notifier, int *eventNumOutput)
//{
//    LOGGER_IS_EXPECT_RET(__notifier_lock(notifier), 0);
//    LOGGER_IS_EXPECT_RET(__notifier_waitforCondion(notifier), 0);
//    *eventNumOutput = notifier->eventNum;
//    LOGGER_IS_EXPECT_RET(__notifier_unlock(notifier), 0);
//    return 0;
//}
////以上thread cond相关

#define HAS_EVENT_MAG(x)   (x&SYS_EVENT_MAGCARD)
#define HAS_EVENT_IC(x)    (x&SYS_EVENT_ICCARD)
#define HAS_EVENT_RFID(x)  (x&SYS_EVENT_RFID)

#define SET_EVENT(EVENT_SET, EVENT) do{(EVENT_SET) |= (EVENT);}while(0)
#define IS_EVENT_IN_SET(EVENT_SET, EVENT) ((EVENT_SET) & (EVENT))

int Event_Wait(int counter,StCardReaderParam* stCardReaderParam,int *pHasEvent);
int Event_Cancel();
int Event_Remove(int eventNum);

#endif
