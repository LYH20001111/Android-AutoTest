
#ifndef __CARD_H_
#define __CARD_H_
#include "ndk.h"
#define SUCC 0
#define FAIL -1
#define QUIT -11
#define TIME_OUT -10
#define MGR_STATUS_ERROR -32
#define MGR_FORMAT_ERROR -31
#define MULTI_CARD -2009
#define MULTI_FELICA -2027

#define HAS_CARD_MAG(x)			(x & CARD_MAG)
#define HAS_CARD_IC(x)		    (x & CARD_IC)
#define HAS_CARD_RFID(x)		(x & CARD_RFID)
#define HAS_CARD_IC_2(x)        (x & CARD_IC_2)

typedef struct{
    uint    cardReadMode;
    uint    unTimeout;			/**超时时间,单位秒*/
    uint    clCardType;
    uint    unLenParamTypeF;
    uchar   *pParamTypeF;
    uint    unLenParamTypeV;
    uchar   *pParamTypeV;
    uint    unIsVerifyTrack;    //是否对磁道进行LRC校验等,一些行业磁条卡不校验磁道
    jobject cardResult;
}STREADCARDPARAM;


//self
typedef enum {
    CARD_MAG = (1<<0),
    CARD_IC  = (1<<1),
    CARD_RFID = (1<<2),
    CARD_IC_2 = (1<<3),
}ReadCardMode;

typedef enum{
    RF_CPU = 0,
    RF_M0,
    RF_M1,
    RF_FELICA,

}RFCardType;


typedef enum{
    IC_CPUCARD = (0),
    IC_SLE44X2 = (6),
    IC_SLE44X8 = (7),
    IC_AT88SC102 = (8),
    IC_AT88SC1604 = (9),
    IC_AT88SC1608 = (10),
    IC_ISO7816 = (11),
    IC_IC_AT88SC153 = (12),
    IC_AT24C01 = (13),
    IC_AT24C02 = (14),
    IC_AT24C04 = (15),
    IC_AT24C08 = (16),
    IC_AT24C16 = (17),
    IC_AT24C32 = (18),
    IC_AT24C64 = (19),
}ICTYPE;

typedef int (*NotifyEvent)(EM_SYS_EVENT eventNum,int msgLen, char * msg);

extern jint jnigeticcardstatus(JNIEnv* env, jobject obj);
extern jint jnicardrw(JNIEnv* env, jobject obj, jint nCardPort, jint nCommandLen, jbyteArray pszCommand, jintArray pnResponseLen, jbyteArray pszResponse);
extern jint jnicardpowerdown(JNIEnv* env, jobject obj);
extern jint jnicardpowerup(JNIEnv* env, jobject obj, jbyteArray pszRes, jintArray pnAtrLen);
extern jint openCardReader(JNIEnv *env, jobject obj, jint readtype, jint timeout, jobject cardResult);
extern jint cancelCardReader(JNIEnv *env, jobject obj);


#endif //__CARD_H_
