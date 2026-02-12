/**
 * Author by wuhh, Date on 2019/3/31 0022.
 */
#ifndef __COMM_H_
#define __COMM_H_

#include <jni.h>
#include "command.h"
#include "tlv.h"

typedef unsigned int 	uint;
typedef unsigned char 	uchar;
typedef unsigned short 	ushort;
typedef unsigned long 	ulong;

typedef unsigned char*	puchar;
typedef char*			pchar;
typedef unsigned short* pushort;
typedef short* 			pshort;
typedef unsigned long* 	pulong;
typedef long* 			plong;
typedef unsigned int* 	puint;
typedef int* 			pint;

typedef struct {
    unsigned short (*mpos_endian_swab16)(unsigned short);
    unsigned int (*mpos_endian_swab32)(unsigned int);
    unsigned int (*mpos_getvar)(void *, int);
    unsigned int (*mpos_setvar)(void *, unsigned int, int);
    unsigned int (*mpos_int2bcd)(void *, unsigned int, int);
    unsigned int (*mpos_bcd2int)(void *, int);
    unsigned int (*mpos_readlen)(void *, int);
    unsigned int (*mpos_writelen)(void *, unsigned int, int);
    int EndianMode;
    int LengthMode;
} ST_COMMAND_API;

typedef enum
{
    ACK_OK   =  0,
    ACK_ERR  = -1,
}ACK_CODE;

extern ushort ME_Endian_Swab16(ushort n);
extern uint ME_Endian_Swab32(uint n);
extern uint ME_GetVar(void *poutdata, int size);
extern uint ME_SetVar(void *pindata, uint indata, int size);
extern uint ME_Int2Bcd(void *poutdata, uint n, int size);
extern uint ME_ReadLen(void *pindata, int size);
extern uint ME_WriteLen(void *poutdata, uint n, int size);
extern uint ME_Bcd2Int(void *pindata, int size);
extern jbyteArray chartobyteArray(JNIEnv* env, const char* p,int len);
extern const ST_COMMAND_API nlMpos_Command;
extern int Sys_GetPosInfo(EM_SYS_HWINFO_ME emFlag, uint *punLen, char *psBuf);
extern int Sys_SetPosInfo(EM_SYS_HWINFO_ME emFlag, char *psBuf, int len);
extern int responseCmd(unsigned char* pOut, int dataLen, int *outLen, char* ackCode);
extern int setReadInfoFlag();
#define LCD_START_YPOS                      16
#define BIG_ENDIAN                          1
#define BCD_MODE                            0
#define LITTLE_ENDIAN                       0
#define ENDIAN_MODE                         1
#define _VAR_BIT8                           1
#define _VAR_BIT16                          2
#define _VAR_BIT24                          3
#define _VAR_BIT32                          4

#define _VAR_WRITE(addr, data, size)        if(size == _VAR_BIT8)\
                                                *((unsigned char*)(addr)) = (unsigned char)(data) ;\
                                            else if(size == _VAR_BIT16)\
                                                *((unsigned short*)(addr)) = (unsigned short)(data) ;\
                                            else \
                                                *((unsigned long*)(addr)) = (unsigned long)(data)

#define _VAR_READ(addr, size)               ((size == _VAR_BIT8) ?  *((unsigned char*)(addr)) :\
                                            ((size == _VAR_BIT16) ? *((unsigned short*)(addr)) :\
                                            ((size == _VAR_BIT32) ? *((unsigned int*)(addr)) :\
                                            *((unsigned long*)(addr)))))

#define Endian_Swab16(x)                    ((((unsigned short)(x) & 0xff00) >> 8)|(((unsigned short)(x) & 0x00ff) << 8))
#define Endian_Swab32(x)                    ((( (unsigned long)(x) & 0xff000000) >> 24) | \
                                            (( (unsigned long)(x) & 0x00ff0000) >> 8) | \
                                            (( (unsigned long)(x) & 0x0000ff00) << 8) | \
                                            (( (unsigned long)(x) & 0x000000ff) << 24))

#define _BCD2INT(n)                         (((((n) >> 4) & 0x0F) * 10) + ((n) & 0x0F))
#define _INT2BCD(n)                         ((((n) / 10) << 4) | ((n) % 10))


#define CMD_OK					            "00"    /* 处理成功 */
#define CMD_ERR_UNSUPPORT		            "01"    /* 指令码不支持 */
#define CMD_ERR_PARAM			            "02"    /* 参数错误 */
#define CMD_ERR_VARLEN			            "03"    /* 可变数据域长度错误 */
#define CMD_ERR_FRAME			            "04"    /* 帧格式错误 */
#define CMD_ERR_LRC				            "05"    /* LRC 校验失败 */
#define CMD_ERR_OTHER			            "06"    /* 其他 */
#define CMD_ERR_TIMEOUT			            "07"    /* 超时 */
#define CMD_CURRENT_STATUS		            "08"    /*返回当前状态*/
#define CMD_SUCC_TAMPER			            "0A"    /* 安全触发，指令执行成功*/
#define CMD_ERR_TAMPER			            "0B"    /* 安全触发，指令执行失败*/
#define CMD_ERR_DEVICE_AUTHENTICATE			"09"    /* 设备认证失败 */
#define CMD_ERR_EXTERN_AUTHENTICATE			"0A"    /* 外部认证失败 */
#define CMD_ERR_PUBLIC_KEY					"0B"    /* 公钥灌装失败 */
#define CMD_ERR_GENERATE_KEYPAIR			"0C"    /* 生成密钥对失败 */
#define CMD_ERR_OPENFILE					"0D"    /* 打开文件失败 */
#define CMD_CANCEL  					    "10"    /*指令被取消*/
#define CMD_ERR_REGISTER 				    "11"    /*指令注册监听失败*/
#define CMD_SUCC_OTHER				        "12"    /*无卡*/
#define CMD_RFID_AANTI			            "20"    /*非接触卡-A卡冲突(多张卡存在)  */
//
#define TERMINAL_SIZE	                    1024
#define MPOS_VARIABLE_OFFSET                0
#define MPOS_VARIABLE_MINLEN		        0
#define SESSION_KEY_INDEX			        255		//会话密钥索引，不可被其他工作密钥使用
#define TAMPSING                            -6001
#define RESPOND_DATA_OFFSET		            2
#define MAX_SIZE	                        100

#define Response_Code_Good                            "00"
#define Response_Code_ERR_PARM                        "01"
#define Response_Code_General_error                   "02"
#define Response_Code_ERR_SEQUENCE                    "04"
#define Response_Code_ERR_SIGN                        "05"

#define IS_CARDS_MODE         1//是否支持多线程打开,未处理完整.3种卡7种情况,事件回调需要找出对应的锁,区分超时需要7个注册函数.业务场景暂不需要.

#endif //__COMM_H_
