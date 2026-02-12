/*
***********************************************************************************************
版权说明：
版本号：
生成日期：
作者：
内容：
功能：
与其它文件的关系：
修改日志：
***********************************************************************************************
*/

#ifndef _API_KB_H_
#define _API_KB_H_

#include "NDK.h"
#include "mpos_command.h"

#define ME_MAX_KEYIN_LEN			(0x7f)	/*最大输入长度*/

#define ME_WAITENTER				(1<<7)	//等待确认键结束字符输入
#define ME_KEY_MODE_NUM				(0<<0)	/*数字输入显示*/
#define ME_KEY_MODE_ALPHA			(1<<0)	/*数字字母混编，允许字母键切换*/
#define ME_KEY_MODE_PWD				(1<<1)	/*密文显示*/
#define ME_KEY_MODE_STR				(1<<2)	/*支持预显示字串功能*/
#define ME_KEY_MODE_CLR				(1<<3)	/*支持清除当前显示行功能*/
#define ME_KEY_MODE_MOVE			(1<<4)	/*支持屏幕显示满后上移功能*/
#define ME_KEY_MODE_COM				(1<<5)	/*支持输入状态下中断去处理串口*/
//#define ME_KEY_MODE_RIGHT			(1<<6)	/*靠右显示*/
#define ME_CMD_ENTER			(1<<6)	/*支持确认指令*/


#define ME_KEYALPHA					KEY_F1	/*字母数字切换键*/


#define UNVALID_PIN				(-5)	/*没有有效密码输入时返回*/
#define BROKE_INPUT 				(-4)	/*终止返回*/
#define TIMEOUT_INPUT 				(-3)	/*超时返回*/
#define QUIT_INPUT					(-2)	/*取消返回*/

#define ME_DEFAULT_WAIT_KEYUP		0
#define ME_DEFAULT_BEEP_FREQ		960
#define ME_DEFAULT_BEEP_DELAY		40

typedef struct {
	uchar key;
	int x1;
	int y1;
	int x2;
	int y2;
} KeyAttr;

void SetNumVol(char VolMode);
void SetDotVol(char VolMode);
void SetZmkVol(char VolMode);
void SetCancelVol(char VolMode);
void SetEnterVol(char VolMode);
void SetBackVol(char VolMode);
extern int me_getline_nl(unsigned char *pucBuf, unsigned char *pucLenFlg, unsigned char ucMode, unsigned char ucMask, unsigned char ucWaitTime);
extern int im_getline_nl(puchar pbuf, int len, unsigned char *pucBuf, unsigned char *pucLenFlg, unsigned char ucMode, unsigned char ucMask, unsigned char ucWaitTime);
extern void me_SetRangeBits(unsigned char *pucData, unsigned char ucMinVal, unsigned char ucMaxVal);
//extern int me_getcurrency_nl(unsigned char *buf,int maxlen,int minlen,int WaitTime, char enter);
extern int me_GetKeystr(char *buf,int maxlen,int mode,int WaitTime, char hex,char enter);
extern int im81_getline_nl(unsigned char *pbuf, uchar flag, unsigned char *pucBuf, unsigned char *pucLenFlg, unsigned char ucMode, unsigned char ucWaitTime);
extern int im81_getcurrency_nl(puchar pbuf, int len, puchar buf, int maxlen,int minlen,int WaitTime, char enter);
extern int mpos_GetPinblock(int input_max,int timeout,uchar *pin_block,uchar *random);

#endif
