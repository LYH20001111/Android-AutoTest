/**
 *  Created by wuhh on 2019/4/11 0011.
 */

#ifndef __CARD_H_
#define __CARD_H_

typedef struct{
    int readCardMode;
    int expectedRfTypes;
    int timeout;
    int tk2Validity;
    int rfidTimes;
    int rfidiInterval;
    int searchCardRule;
    int felicaParamLen;
    uchar *felicaParam;
    int enablePreParam;
    int vasEnable;
    int vasParamLen;
    uchar *vasParam;
    //other
    int targetCardType;
}StCardReaderParam,*pStCardReaderParam;
typedef enum {
    OPENCARD_TYPE_NONE = 0,
    OPENCARD_TYPE_MAG  = 1,
    OPENCARD_TYPE_IC   = 2,
    OPENCARD_TYPE_RF   = 3,
}EmTargetCardType;

//self
typedef enum {
    CARD_MAG = (1<<0),
    CARD_IC  = (1<<1),
    CARD_RFID= (1<<2),
    CARD_MAG_IC = (CARD_MAG|CARD_IC),
    CARD_MAG_RFID = (CARD_MAG|CARD_RFID),
    CARD_IC_RFID = (CARD_IC|CARD_RFID),
    CARD_MAG_IC_RFID = (CARD_MAG|CARD_IC|CARD_RFID),
}ReadCardMode;

#define HAS_CARD_MAG(x)			(x & CARD_MAG)
#define HAS_CARD_IC(x)			(x & CARD_IC)
#define HAS_CARD_RFID(x)		(x & CARD_RFID)

extern int CardLock(int nReadCardMode,int cmd);
extern void CardUnLock(int nReadCardMode);

typedef enum{
    CARDREADER_INDEX_MAG  = 0,
    CARDREADER_INDEX_IC   = 1,
    CARDREADER_INDEX_RFID = 2,
}CardReaderIndex;

extern int Card_GetReadCardMode();
extern void Card_SetCancelFlag(int flag);
extern int Card_GetCancelFlag();
extern int CardReader_Cancel(int waitTimeMs);
extern void CardReader_GetMethodID(JNIEnv *env);
extern void CardReader_NotifyJava();

#endif //__CARD_H_
