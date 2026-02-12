#ifndef __EMV_H_
#define __EMV_H_

#include <jni.h>
#include "emvl3.h"
#include "log.h"

typedef unsigned int 	uint;
typedef unsigned char 	uchar;
typedef unsigned short 	ushort;
typedef unsigned long 	ulong;

/** Pinpad Port Error Code */
#define PORT_ERR_NONE        0 
#define PORT_ERR_CLOSE      -1       
#define PORT_ERR_READ       -2
#define PORT_ERR_WRITE      -3
#define PORT_ERR_PARAM      -4
#define PORT_ERR_LENGTH     -5
#define PORT_ERR_CANCEL     -6


#define COMMAND_RESCODE_SUCC           0
#define COMMAND_RESCODE_INVALID_PARAM  1
#define COMMAND_RESCODE_FAILED         2
#define COMMAND_RESCODE_CANCEL         3

#define COMMAND_ERR_GOOD               0
#define COMMAND_ERR_INVALID_PARAM     -11
#define COMMAND_ERR_COMMAND_FAIL      -12
#define COMMAND_ERR_CANCEL            -13
#define COMMAND_ERR_READER_NOT_CONFIG -13

/** Message Type */
#define Rquest_ContactlessCommand           "L0"
#define Response_ContactlessCommand         "L1"
#define Rquest_PinEntryCommand              "32"
#define Response_PinEntryCommand            "33"

/** EMV Configuration File Command Function ID*/
#define COMMAND_TERMINAL_CONFIG_UPDATE      0x01
#define COMMAND_TERMINAL_CONFIG_GET         0x02
#define COMMAND_AID_CONFIG_UPDATE           0x03
#define COMMAND_AID_CONFIG_GET              0x04
#define COMMAND_AID_CONFIG_REMOVE_ONE       0x05
#define COMMAND_AID_CONFIG_REMOVE_ALL       0x06
#define COMMAND_CAPK_UPDATE                 0x07
#define COMMAND_CAPK_GET                    0x08
#define COMMAND_CAPK_REMOVE_ONE             0x09
#define COMMAND_CAPK_REMOVE_ALL             0x0A
#define COMMAND_CERT_BLACK_UPDATE           0x0B
#define COMMAND_CERT_BLACK_GET              0x0C
#define COMMAND_CERT_BLACK_REMOVE_ONE       0x0D
#define COMMAND_CERT_BLACK_REMOVE_ALL       0x0E
#define COMMAND_CARD_BLACK_UPDATE           0x0F
#define COMMAND_CARD_BLACK_GET              0x10
#define COMMAND_CARD_BLACK_REMOVE_ONE       0x11
#define COMMAND_CARD_BLACK_REMOVE_ALL       0x12
#define COMMAND_AID_GET_COUNT               0x13
#define COMMAND_CAPK_GET_COUNT              0x14

/** Function Command Function ID*/
#define COMMAND_DEBUG_MASSAGE               0x21
#define COMMAND_INIT_EMV_KERNEL             0x22
#define COMMAND_SET_DATA                    0x23
#define COMMAND_GET_DATA                    0x24
#define COMMAND_SET_TLV_LIST                0x25
#define COMMAND_GET_TLV_LIST                0x26
#define COMMAND_SET_DEBUG_MODE              0x27
#define COMMAND_GET_VERSION                 0x28
#define COMMAND_CANCEL_TRANSACTION          0x29

/** Transaction Command Function ID*/
#define COMMAND_PERFORM_TRANSACTION         0x31
#define COMMAND_COMPLETE_TRANSACTION        0x32
#define COMMAND_TERMINATE_TRANSACTION       0x33
#define COMMAND_PREPROCESS_TRANSACTION      0x34

extern int NAPI_L3Init(char *filePath, char *config);
extern int NAPI_L3LoadTerminalConfig(L3_CARD_INTERFACE interface, unsigned char tlv_list[], int *tlv_len, L3_CONFIG_OP mode);
extern int NAPI_L3LoadAIDConfig(L3_CARD_INTERFACE interface, L3_AID_ENTRY *aidEntry, unsigned char tlv_list[], int *tlv_len, L3_CONFIG_OP mode);
extern int NAPI_L3LoadCAPK(L3_CAPK_ENTRY *capk, L3_CONFIG_OP mode);
extern int NAPI_L3LoadRevocationList(L3_CRL_ENTRY *crl, L3_CONFIG_OP mode);
extern int NAPI_L3LoadExceptionList(L3_EXCEPTION_FILE_ENTRY *exceptionList, L3_CONFIG_OP mode);
extern int NAPI_L3PerformTransaction(JNIEnv *env,char *data, int dataLen,jobject txnResultObj);
extern int NAPI_L3CompleteTransaction(JNIEnv *env,char *data, int dataLen, jobject txnResultObj);
extern int NAPI_L3PreProcessTransaction(JNIEnv *env,char *data, int dataLen, jintArray errorCode);
extern int NAPI_L3TerminateTransaction(JNIEnv *env,jobject txnResultObj);
extern int NAPI_L3CancelTransaction(void);
extern int NAPI_L3SetData(unsigned int tag, void *data, unsigned int len);
extern int NAPI_L3GetData(int type, uchar KeyIndex, void *data, int maxLen,int *realLen);
extern int NAPI_L3SetTLVData(uchar *TLV_List, unsigned int len);
extern int NAPI_L3GetTlvData(uchar *tagList, int tagListLen, unsigned int tagNum, uchar KeyIndex, uchar *tlvData, unsigned int maxLen,int ctl,int *realLen);
extern int NAPI_L3SetDebugMode(int debugLV);
extern int NAPI_L3GetVersion(L3_MODULE module, uchar *Version);
extern int NAPI_L3GetAIDCount(JNIEnv *env,jint cardIntf,jintArray len,jbyteArray count);
extern int NAPI_L3GetCAPKCount(JNIEnv *env,jintArray len,jbyteArray numRidIndex);

#define NDK_OK          0
#define NDK_ERR        -1
#define NDK_ERR_PARA   -6

#if !IS_EMVL3
extern int NDK_IntToC2(uchar* psBuf, uint unNum);
extern int NDK_C2ToInt(uint *unNum, uchar *psBuf);
extern int NDK_C4ToInt(uint* unNum, uchar* psBuf);
extern int NDK_C2ToInt(uint *unNum, uchar *psBuf);
#endif

#define MP_Update_Terminal                    "\x80\x01"
#define MP_Get_Terminal                       "\x80\x02"
#define MP_Update_AID                         "\x80\x03"
#define MP_Get_AID                            "\x80\x04"
#define MP_Remove_AID                         "\x80\x05"
#define MP_Remove_ALL_AID                     "\x80\x06"
#define MP_Update_CAPK                        "\x80\x07"
#define MP_Get_CAPK                           "\x80\x08"
#define MP_Remove_One_CAPK                    "\x80\x09"
#define MP_Remove_ALL_CAPK                    "\x80\x0A"
#define MP_Init_EMV_Kernel                    "\x80\x22"
#define MP_Set_Data                           "\x80\x23"
#define MP_Get_Data                           "\x80\x24"
#define MP_Set_TLV_List_Data                  "\x80\x25"
#define MP_Get_TLV_List_Data                  "\x80\x26"
#define MP_Set_Debug_Mode                     "\x80\x27"
#define MP_Get_Version                        "\x80\x28"
#define MP_Performed_Transaction              "\x80\x31"
#define MP_Complete_Transaction               "\x80\x32"
#define MP_Terminal_Transaction               "\x80\x33"

#define MP_PreProcess_Transaction             "MP"
#define MP_Get_AID_Count                      "MP"
#define MP_Get_CAPK_Count                     "MP"
#define TYPE_SP100    1
#define TYPE_MPOS     2

#define BUFFER_LEN_BIG      1536
#define BUFFER_LEN_SMALL    128


extern int g_commType;

extern jint registerNativesEmvL3(JNIEnv *env);
#endif
