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


#define LOG_TYPE_PR     2   /* 产权下载信息 */
#define LOG_TYPE_ICON   4   /* 图标下载信息 */
#define DEVICE_ICON_FLAG    0xc3    /* 图标有效标志 */

typedef struct{
	int lrc;				      /* 扇区异或值 */
	unsigned char  UserLen;       /* 客户序列号长度 */
	unsigned char Propertylenl;   /* 产权信息长度低位 */
	unsigned char Propertylenh;   /* 产权信息长度高位 */
    unsigned char  Producelen;    /* 生产机号长度 */
}TFlag;

typedef struct{
	unsigned char iconflag;       /* 图标有效标志 */
	unsigned char iconwidth;	  /* 图标宽 */
	unsigned char iconhigh;       /* 图标高 */
	unsigned char iconreserve[5]; /* 保留 */
}TIconindex;

typedef  struct{
	char m_cType;			/* 类型 */
	char m_cTime[6];		/* 时间（年月日时分秒－BCD）*/
	char m_cReserve[5];		/* 保留字段 */
}TLog;		/*信息结构*/

typedef struct{
	TFlag m_flag;
	char  m_TFUser[64];           /* 客户序列号 */
	char  m_Property[512];        /* 产权信息 */
	TIconindex m_IconIndex[3];	  /* 图标索引信息 */
	char  m_TFProduce[24];        /* 生产机号 */
	TLog  m_TLInfo[10];		      /* 下载信息记录 */
}NEWTSNManage;

typedef struct{
	unsigned char lenl;    /* 产权信息长度低位 */
	unsigned char lenh;    /* 产权信息长度高位 */
	char m_cData[512];
}TProperty;

typedef struct{
    unsigned char lenl;    /* 图标长度低位 */
    unsigned char lenh;    /* 图标长度低位 */
    unsigned char width;   /* 图标宽度 */
	unsigned char high;    /* 图标高度 */
	char icondata[1024];
}TICON;

struct postime {
    char		yearh;      /*  年份的高字节 BCD表示*/
    char		yearl;		/*  年份的低字节 BCD表示*/
    char		month;		/*  月份	 BCD表示1--12*/
    char		day;		/*  日		 BCD表示1--31*/
    char		week;		/*	0--6对应星期日--星期六 */
    char		hour;		/*  小时	 BCD表示0--23*/
    char		minute;		/*	分	 BCD表示0--59*/
    char		second;		/*	秒	 BCD表示0--59*/
};


int unpackPR(TProperty *pTPInfo, uint *punLen, const char *cFieldBuf);
int unpackICON(TICON *pTIcon, uint *punLen, const char *cFieldBuf);
int snWriteICON(TICON *pTIcon);
int snWritePR(TProperty *pTPR);
int snReadPR(TProperty *pTPR);
int snReadICON(TICON *pTIcon);


#endif

