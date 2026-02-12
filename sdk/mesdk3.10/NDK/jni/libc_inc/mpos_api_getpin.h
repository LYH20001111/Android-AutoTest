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

#ifndef _API_GETPIN_H_
#define _API_GETPIN_H_

#include "NDK.h"


extern int calc_pin_block(const uchar *pszPAN, const uchar *cipherkey, uchar *sPinTmp, int nEncryptionType);
extern int ME_SecGetPlainPin(puchar pOut, uchar flag, const uchar *pszDataIn, uchar *psPinBlockOut, uchar ucMode, uchar ucKeyEnter, uchar ucMaxLen,uchar *pinLenCtl, int *ucPinLen);
#endif

