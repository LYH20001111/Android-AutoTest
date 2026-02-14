
#ifndef __READERRFID_H_
#define __READERRFID_H_

#include "card.h"

#define	WUPV			(0x01)

typedef enum{
    RF_TYPE_A = (1<<0),
    RF_TYPE_B = (1<<1),
    RF_TYPE_F = (1<<2),
    RF_TYPE_V = (1<<3),
}RFTYPE;

#define HAS_RFID_A(x)         (x&RF_TYPE_A)
#define HAS_RFID_B(x)         (x&RF_TYPE_B)
#define HAS_RFID_F(x)         (x&RF_TYPE_F)


#define NDK_RFID_A            (0xcc)
#define NDK_RFID_B            (0xcb)
#define NDK_RFID_F            (0xcf)
#define NDK_RFID_AB           (0xcd)
#define NDK_RFID_AF           (0xca)
#define NDK_RFID_BF           (0xc9)
#define NDK_RFID_ABF          (0xc8)

typedef struct {
    int nUidLen;
    char szUID[50]; //4字节/7字节/10字节 ?
    int nAtqaLen;
    char szAtqa[50]; //2字节
    int nAtqblen;
    char szAtqb[256]; //13字节
    int nAtsLen;
    char szAts[256]; // ??
    int nSakLen;
    char szSak[50]; //3字节
}STACTIVATERESULT;

extern int PubRFOpen();
extern int PubRFSeekCard(int nPiccType);
extern int PubRFDetectCard(const STREADCARDPARAM *pstReadCardParm, int *pOutType);
extern int PubRFCPUComm(char *psSend, int nSendLen,char *psRecv, int *pnRecvLen);
extern int PubRFActivate (int cardType, STACTIVATERESULT *pResult);
extern int PubRFActivate2(STACTIVATERESULT *pResult);
extern int PubRFDetectLpcd(const STREADCARDPARAM *streadcardparam, int *cardType, int *state);
extern int PubRFPowerDown(void);
#endif
