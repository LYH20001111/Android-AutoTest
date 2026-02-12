#ifndef __CMDAPI_H_
#define __CMDAPI_H_
#include "ndk.h"
#include "comm.h"

extern int FCardReader_Open(int readCardMode, int timeOut,jobject cardResult,int searchMode);
//
extern int FMag_ReadTrackPlain(JNIEnv *env,int nReadTrackMode,jobject result);
extern int FMag_Open();
extern int FMag_Close();
extern int FMag_IsSwiped(uchar *psSwiped);
//
extern int FIcc_Detect(uchar *pOut);
extern int FIcc_PowerOn(int icCardSlot, int icCardType,int mode,int voltagen,uchar *atr,int *atrLen,int *protocol);
extern int FIcc_PowerOff(int icCardSlot, int icCardType);
extern int FIcc_ReadWrite(int icCardSlot, int icCardType,uchar *send,int sendLen,uchar *recv,int *recvLen);
//
extern int FRfid_PowerOn(int rfCardType, int timeOut, int sak, uchar *felicas,int felicasLen,JNIEnv *env,jobject cardInfo);
extern int FRfid_PowerOff(uchar ucDelayMs);
extern int FRfid_Apdu(uchar *send, int sendLen, uchar *recv, int *recvLen);
extern int FRfid_FelicaApdu(uchar *sendData, int sendLen, uchar *recv, int *recvLen);
extern int FRfid_FelicaApdu_retry(uchar *sendData, int sendLen, uchar *recv, int *recvLen, int timeout, int retryTimes);
extern int FRfid_M1AuthKey(int kmode,uchar *uid, int KeySector,uchar *keyData);
extern int FRfid_M1ReadBlock(int number, uchar *data, int *len);
extern int FRfid_M1WriteBlock(int number, uchar *data, int dataLen);
extern int FRfid_M1Increment(int nBlockNum, uchar *data);
extern int FRfid_M1Decrement(int nBlockNum, uchar *data);
extern int FRfid_M0AuthKey(uchar *keyData, int keyLen);
extern int FRfid_M0ReadBlock(int number, uchar *data, int *len);
extern int FRfid_M0WriteBlock(int number,uchar *data, int dataLen);
extern int FRfid_M1Transfer(int blockNum);
extern int FRfid_M1Restore(int blockNum);


//
extern int FLight_SetStatus(int mode,int color);
extern int FLight_SetStatusLT1118(int buf[], jint bufLen, jint lightCount);
extern int FLight_blink_Virtual_Advanced(int x, int y, int horizontal, int alwaysDisplayBackground, int count, int color, int onDuration, int offDuration);
extern int FLight_Blink(int count,int color,int interval);
extern int FLight_Blink_Virtual(int count, int color, int interval);
//
extern int FDevice_SetDateTime(uchar *pbuf, int len);
extern int FDevice_GetDateTime(uchar *pOut, int *len);
extern int FDevice_GetSN(unsigned char *pOut);
extern int NDK_GetSN(unsigned char *pOut);
#endif
