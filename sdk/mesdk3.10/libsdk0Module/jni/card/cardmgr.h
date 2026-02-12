#ifndef __CARD_MGR_H
#define __CARD_MGR_H
#include "card.h"
#include "comm.h"
typedef struct __stCardReader
{
    int (*openCardDev)(void* ,void*);
    int (*readCardInfo)(void*,void*);
    int (*closeCardDev)(void*);
    int (*resumeCardDev)(void*);
}StCardReader;

typedef struct _stCardInfo
{
	int   userInputMode;
	uint  validLen;
	uchar data[512];
}StCardInfo;

enum NL_ERROR_CODE
{
	NL_RFACTIVATE_FAIL = -7,
	NL_MAGREAD_FAIL = -6,
	NL_ERR_ACK = -5,
	NL_CANCEL = -4,
	NL_ERR_PARAMETERS = -3,
	NL_ERR_TIMEOUT = -2, // error occured, due to the timeout.
	NL_FAILED  = -1, // general failure message.
	NL_OK      = 0,  // general success message.
};

extern void* CardMgr_CreateCardReader();
extern int CardMgr_ObtainReader(ReadCardMode inputMode, StCardReader** ptCardReaders);
extern int CardMgr_ReleaseReader(StCardReader *ptCardReaders);
extern int CardMgr_CreateCardReaders(int nRequiredCardInputMode,StCardReader* readers[]);
extern void* CardMgr_CreateCardInfo();
extern int CardMgr_GetOpenCardParam(StCardReaderParam *g_stCardReaderParam,unsigned char*pbuf,int buf_len);

#endif