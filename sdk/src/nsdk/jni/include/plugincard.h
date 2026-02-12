/**
 * Author by wuhh, Date on 2019/4/17 0022.
 */
#ifndef __RFIF_H_
#define __RFIF_H_

#include <time.h>
#include "comm.h"

typedef struct{
	//in
	int rfCardType;
	int timeOut;
	uchar sak;
	int felicaParamLen;
	uchar *felicaParam;
	//self
	struct timeval startTime;
	JNIEnv *env;
	jobject cardInfo;

	//out
	int targetCard;
}StRFPowerUpParam,*pStRFPowerUpParam;



typedef enum {
	MIFARE_1K = 1,
	MIFARE_4K,
	MIFARE_CL1_ANY,
	MIFARE_UL_CL2,
	MIFARE_ULC_CL2,
	MIFARE_MINI,
	MIFARE_PLUS_2K_SL1,
	MIFARE_PLUS_4K_SL1,
	MIFARE_PLUS_2K_SL2,
	MIFARE_PLUS_4K_SL2,
	MIFARE_PLUS_ANY_SL3,
	MIFARE_PLUS_CL2_2K_SL1,
	MIFARE_PLUS_CL2_4K_SL1,
	MIFARE_PLUS_CL2_2K_SL2,
	MIFARE_PLUS_CL2_4K_SL2,
	MIFARE_PLUS_CL2_ANY_SL3,
	MIFARE_DESFIRE_CL1,
	MIFARE_DESFIRE_CL2,
	MIFARE_DESFIRE_EV1_CL2,
	SMART_MX,
	ISO14443_4_CARD,
	MIFARE_NONE,
	MIFARE_ERR,
}MIFARE_CARD_TYPE; 

#define SAK_BIT1	(0x01)
#define SAK_BIT2	(0x02)
#define SAK_BIT3	(0x04)
#define SAK_BIT4	(0x08)
#define SAK_BIT5	(0x10)
#define SAK_BIT6	(0x20)
#define SAK_BIT7	(0x40)
#define SAK_BIT8	(0x80)

static int hasFelicaPoll = 0;
#define RFCARD_A        (0x80)
#define RFCARD_B        (0x81)
#define RFCARD_M1       (0x82)
#define RFCARD_FELICA   (0x83)
#define RFCARD_MIFARE   (0x84)



typedef struct{
	uchar rfCardType;
	int timeOut;
	int showMsgLen;
	char showMsg[128];
	int sakLen;
	uchar sak;
	int felicaDataLen;
	uchar *felicaData;
	int mifareDataLen;
	uchar *mifareData;
}StRFPowerOnParam,*pStRFPowerOnParam;
typedef enum{
	TARGETCARD_MIFARE,
	
}EM_TARGET_CARD;

typedef struct{	
	StRFPowerOnParam *poweronparam;
	struct timeval startTime;
	int   uidLen; 
	uchar uid[32];
	uchar sak;
	EM_TARGET_CARD  targetCard;
	//out
	uchar ackHead[2];
	
}StPowerOnFunParam,*pStPowerOnFunParam;

typedef enum{
	MIFARECARD_M1 = 0,
	MIFARECARD_M0 = 1,
}EmMifareCardType;

#endif

