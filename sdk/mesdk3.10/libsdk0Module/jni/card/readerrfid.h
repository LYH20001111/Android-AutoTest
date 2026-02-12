#ifndef __READERRFID_H_
#define __READERRFID_H_

#define OPEN_CARD_RF_A       0x01
#define OPEN_CARD_RF_B       0x02
#define OPEN_CARD_RF_M1      0x04
#define OPEN_CARD_RF_FELICA  0x08
#define OPEN_CARD_RF_M0      0x10

int RfidReader_Open(void* pstCardReaderParam,void* pstCardInfo);
int RfidReader_Read(void* pstCardReaderParam,void* pstCardInfo);
int RfidReader_Close(void* pstCardReaderParam);
int RfidReader_Resume(void* pstCardReaderParam);
int RfidReader_LedLt1118Status(int isOn);
#endif
