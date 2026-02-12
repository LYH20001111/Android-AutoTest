#include "log.h"
#include "NDK.h"
#include "string.h"
#include "alg.h"
#include "command.h"
#include "unistd.h"

#define  TK1        0x01
#define  TK2        0x02
#define  TK3        0x04
static uchar CbC_Value[16] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
extern ME_TPEDCTL g_METPEDCtl;
int PubAscToHex(const unsigned char *pszAsciiBuf, int nLen, char cType, unsigned char *pszBcdBuf) {
    int i = 0;
    char cTmp, cTmp1;

    if (pszAsciiBuf == NULL) {
        return -1;
    }

    if (nLen & 0x01 && cType)/*判别是否为奇数以及往那边对齐*/
    {
        cTmp1 = 0;
    } else {
        cTmp1 = 0x55;
    }

    for (i = 0; i < nLen; pszAsciiBuf++, i++) {
        if (*pszAsciiBuf >= 'a') {
            cTmp = *pszAsciiBuf - 'a' + 10;
        } else if (*pszAsciiBuf >= 'A') {
            cTmp = *pszAsciiBuf - 'A' + 10;
        } else if (*pszAsciiBuf >= '0') {
            cTmp = *pszAsciiBuf - '0';
        } else {
            cTmp = *pszAsciiBuf;
            cTmp &= 0x0f;
        }

        if (cTmp1 == 0x55) {
            cTmp1 = cTmp;
        } else {
            *pszBcdBuf++ = cTmp1 << 4 | cTmp;
            cTmp1 = 0x55;
        }
    }
    if (cTmp1 != 0x55) {
        *pszBcdBuf = cTmp1 << 4;
    }

    return 0;
}

/*
***********************************************************************************************
函数原型：int mpos_AscToBcd(unsigned char *pucBCDBuf, unsigned int *puiLen, char *szAscii, unsigned char ucType)
功能描述：将ASCII字符串转为BCD码字符
输入参数：szAscii：ASCII码字符串
		  ucType：转换类型，对齐方式填充类型等
输出参数：pucBCDBuf：转换输出的BCD码数据
		  puiLen：BCD码数据长度
返回值：  FAIL: 失败
          SUCC: 成功
调用关系：
***********************************************************************************************
*/
int mpos_AscToBcd(unsigned char *pucBCDBuf, unsigned int *puiLen, char *szAscii, unsigned char ucType) {
    int i;
    unsigned char ucTmp;
    unsigned char ucOffset;    /*记录bcd码的偏移位置*/

    if (strlen(szAscii) == 0) {
        return 0;
    }

    /*'0'~'9' 'a'~'f' 'A'~'F'以外字符不处理*/
    for (i = 0; i < strlen(szAscii); i++) {
        if (((szAscii[i] >= '0') && (szAscii[i] <= '9'))
            || ((szAscii[i] >= 'a') && (szAscii[i] <= 'f'))
            || ((szAscii[i] >= 'A') && (szAscii[i] <= 'F'))) { ;
        } else {
            break;
        }
    }

    if (i < strlen(szAscii))/*含有不可转换的ascii字符*/
    {
        return -1;
    }
    i = strlen(szAscii) - 1;
    *puiLen = (i / 2) + 1;    /*转换后的长度*/

    ucOffset = 0;    /*bcd码偏移默认为0*/
    if ((i & 1) == 0)/*长度为奇数，有对齐问题*/
    {
        switch (ucType & ALIGN_BIT) {
            case ALIGN_LEFT:/*左对齐*/
                switch (ucType & FILL_BIT) {
                    case FILL_0:
                        pucBCDBuf[i / 2] = 0x00;/*末尾半字节赋0*/
                        break;
                    case FILL_F:
                        pucBCDBuf[i / 2] = 0x0f;/*末尾半字节赋f*/
                        break;
                    default:
                        return -1;
                        break;
                }

                break;
            case ALIGN_RIGHT:/*右对齐*/
                switch (ucType & FILL_BIT) {
                    case FILL_0:
                        pucBCDBuf[0] = 0x00;/*起始半字节赋0*/
                        break;
                    case FILL_F:
                        pucBCDBuf[0] = 0xf0;/*起始半字节赋f*/
                        break;
                    default:
                        return -1;
                        break;
                }

                ucOffset = 1;    /*右对齐方式bcd码需向右偏移1个半字节*/
                break;
            default:
                return -1;
                break;
        }
    }

    for (; i >= 0; i--) {
        ucTmp = szAscii[i];
        if (ucTmp > 'F') {
            ucTmp = (unsigned char) (ucTmp - 'a' + 10);
        } else if (ucTmp > '9') {
            ucTmp = (unsigned char) (ucTmp - 'A' + 10);
        } else {
            ucTmp -= '0';
        }

        if ((i + ucOffset) & 1)/*低半字节*/
        {
            pucBCDBuf[(i + ucOffset) / 2] = (unsigned char) ((pucBCDBuf[(i + ucOffset) / 2] & 0xf0) | ucTmp);
        } else/*高半字节*/
        {
            pucBCDBuf[(i + ucOffset) / 2] = (unsigned char) ((pucBCDBuf[(i + ucOffset) / 2] & 0x0f) | (ucTmp << 4));
        }
    }
    return 0;
}


