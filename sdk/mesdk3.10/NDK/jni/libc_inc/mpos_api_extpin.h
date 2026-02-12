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

#ifndef _MPOS_API_MIMA_H_
#define _MPOS_API_MIMA_H_

#include "NDK.h"
#include "mpos_api_frame.h"

int InitCOM1Para(void);
int CmdParse_A(uchar Mkey_num, uchar Nkey_num, puint des_flag, puchar rdata);
int CmdParse_B(uint bps, puchar rdata);
int CmdParse_C(puchar rdata);
int CmdParse_D(uint line, uchar *str, uint len, puchar rdata);
int CmdParse_E(uint type, puchar rdata);
int CmdParse_F(uint type, puchar rdata);
int CmdParse_G(puchar rdata);
int CmdParse_H(uint type, puchar str, uint len, puchar rdata);
int CmdParse_I(uint type, puchar card_num, uchar timeout, puchar rdata);
int CmdParse_J(uint type, puchar card_num, puchar str, uint len, puchar rdata);
int CmdParse_K(uint Mkey_num, puchar str, uint len, puchar rdata);
int CmdParse_L(uint max_pinlen, puchar rdata);
int CmdParse_M(uint Mkey_num, puchar OriKey, uint oklen, puchar NewKey, uint nklen, puchar rdata);
int CmdParse_N(uint min_pinlen, puchar rdata);
int CmdParse_O(puchar rdata);
int CmdParse_P(puchar str, uint len, puchar rdata);
int CmdParse_Q(puchar rdata);
int CmdParse_R(puchar pin_group, puchar rdata);
int CmdParse_S(uint Mkey_num, uint Nkey_num, uint len, puchar str, puchar rdata);
int CmdParse_T(uint mode, uint len, puchar macdata, puchar rdata);
int CmdParse_U(uint Mkey_num, uint Nkey_num, puchar Mkey, uint Mkeylen, puchar Ukey, uint Ukeylen, puchar rdata);
int CmdParse_V(puchar rdata);
int CmdParse_W(uint x, uint y, puchar data, uint len, puchar rdata);
int CmdParse_X(uint type, puchar card_num, puchar rdata);
int CmdParse_Z(puchar rdata);
int CmdParse_b(uint bps, puchar rdata);
int CmdParse_x(uint mode, puchar data, uint len, puchar rdata);
int CmdParse_h(uint y, puchar data, uint len, puchar rdata);
int CmdParse_p(uint mode, puchar data, uint len, puchar rdata);
int ext_SecSetCBCInitValue(uchar *pvalue);
int ext_SecCalcDesIndex(uchar ucKeyIdx, uchar * psDataIn, int nDataInLen, uchar *psDataOut, uchar ucMode);


#endif
