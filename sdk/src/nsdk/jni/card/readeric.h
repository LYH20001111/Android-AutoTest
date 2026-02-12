/**
 *  Created by wuhh on 2019/4/11 0011.
 */
#ifndef __READERIC_H_
#define __READERIC_H_


#define IC1_EXIST		0x01
#define IC1_POWERON	    0x02
#define IC2_EXIST       0x04
#define IC2_POWERON     0x08
#define SIM1_POWERON	0x10
#define SIM2_POWERON	0x20
#define SIM3_POWERON	0x40
#define SIM4_POWERON	0x80

extern int PubGetICStatus(char *pcStatus);
extern int PubICPowerUp (const int nCardPort, char *psAtr, int *pnAtrLen);
extern int PubICPowerDown(const int nCardPort);
extern int PubICrw(const int nCardPort, const int nCommandLen, const char *pszCommand, int *pnLen, char *pszResponse);

#endif
