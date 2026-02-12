#ifndef __CMDAPI_H_
#define __CMDAPI_H_
#include "ndk.h"
#include "comm.h"

typedef struct {
    int cmd;
    int (*func)(unsigned char *, int, unsigned char *, int *);
} Command_Code_t;
extern int CardReader_Open(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int CardReader_Close(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen);
//
extern int Mag_ReadTrackPlain(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Mag_ReadTrackEncrypt(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Mag_CalculateTrack(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen);
//
extern int Icc_Detect(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen);
extern int Icc_PowerOn(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Icc_PowerOff(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Icc_ReadWrite(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen);
//
extern int Rfid_PowerOn(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Rfid_PowerOff(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Rfid_Apdu(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Rfid_FelicaApdu(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Rfid_M1AuthKey(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Rfid_M1ReadBlock(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Rfid_M1WriteBlock(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Rfid_M1Increment(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Rfid_M1Decrement(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Rfid_M0AuthKey(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Rfid_M0ReadBlock(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Rfid_M0WriteBlock(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Rfid_IsExist(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Rfid_ATS(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen);
//
extern int Light_SetStatus(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen);
extern int Light_Blink(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen);
//
extern int Pinpad_VppInit(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen);
extern int Pinpad_Input(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Pinpad_LoadMKey(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Pinpad_EncOrDec(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Pinpad_DataMac(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Pinpad_LoadWKey(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Pinpad_LoadDukpt(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Pinpad_DelKey(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Pinpad_CheckKey(unsigned char *pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Pinpad_IncreaseKsn(unsigned char *pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Pinpad_GetDukptKsn(unsigned char *pbuf,  int buf_len, unsigned char *pOut, int *outLen);
//
extern int Prn_GetStatus(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Prn_Print(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Prn_CutterPaper(puchar pbuf, int buf_len, unsigned char *pOut, int *outLen);
extern int Prn_SetPaperSize(puchar pbuf, int buf_len, unsigned char *pOut, int *outLen);
//
extern int Device_ReadInfo(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen) ;
extern int Device_GetTusn(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Device_SetSN(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen);
extern int Device_SetDateTime(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen);
extern int Device_GetDateTime(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen);
extern int Device_GetRandomNumber(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen) ;
//
extern int Term_Buzzer(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen);
extern int Term_CancelReset(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen);
extern int Term_ShutDown(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen);
extern int Term_Confirmation(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen);
extern int Term_SetKeyVol(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen);
extern int Term_SetTagData(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen);
extern int Term_GetTagData(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen);
//
extern int Led_GetVersion(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Led_SetBrightness(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Led_TurnOn(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Led_TurnOff(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen);
//
extern int File_OpenRecords(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen) ;
extern int File_GetRecordNum(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen) ;
extern int File_WriteRecord(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen) ;
extern int File_GetRecord(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen) ;
extern int File_ModifyRecord(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen) ;
extern int File_WriteFile(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int File_ReadFile(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int File_DeleteFile(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen);
//
extern int Log_SetLevel(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen);
extern int Global_Setting(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen);
#endif
