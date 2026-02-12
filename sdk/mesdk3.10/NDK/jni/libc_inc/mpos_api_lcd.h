/*
***********************************************************************************************
版权说明：lcd api
版本号：
生成日期：
作者：
内容：
功能：
与其它文件的关系：
修改日志：
***********************************************************************************************
*/

#ifndef _MPOS_API_LCD_H_
#define _MPOS_API_LCD_H_

#include "NDK.h"


#define APP_CANCLE      -4		/*撤销*/
#define APP_SUCC 		0
#define APP_FAIL 		-1
#define APP_QUIT             -2
#define APP_TIMEOUT 	-3

#define LINE1    16
#define LINE2    28
#define LINE3    40
#define LINE4    52
#define LINE_END 64
extern int ClrBackChar(void);
extern int ClrLine(void);
extern int DispOneChar(unsigned char c);
extern int DispAlphaChar(unsigned char ucAlphanumericEn, unsigned char ucData, unsigned char ucMaskFlg, unsigned char ucMask);
extern void LCD_DispStrLine( int LineNumber, char *strLineText, int color );
extern void LCD_DispTitle(const char *szTitle);
extern void me_ScrClrs();
extern int me_ScrGotoxy(uint unX,uint unY);
extern int me_ScrGetxy(uint *x,uint *y);
extern int me_ScrDispString(uint unX, uint unY, const char * pszS, uint unMode);
extern void ME_DispStaBar(void);
extern void mpos_DispStrXY( int nStarty, char *strLineText, char uLen );
void me_show_bt_icon(void);
extern void ME_DispStaBar(void);
extern void ME_Disp_Bitmap(void);
int me_SysGetPowerVol(uint *punChargeFlag,uint *punVol);



#endif



