/**
 * Author by wuhh, Date on 2019/4/17 0022.
 */
#ifndef __MAG_H_
#define __MAG_H_

#include "comm.h"
extern int ProDealWithTKData(uchar *pucMainTk, char *pszTkData, int nLrc, int *pnReadNum);
extern int ProJudgeResult(char *pszTk1, char *pszTk2, char *pszTk3, const char *pszTkData, uchar ucMainTk);

#define     TK1        0x01                /*只读一磁道*/
#define     TK2        0x02                /*只读二道*/
#define     TK3        0x04                /*只读三道*/
typedef enum {
    STA_OK = 0x00,
    STA_ERR_PARAM = 0x61,
    STA_ERR_VARLEN = 0x62,
    STA_ERR_LEN = 0x63,
    STA_ERR_TYPE = 0x64,
    STA_ERR_CARD_FRAME = 0x65,
    STA_ERR_CARD_TIMEOUT = 0x92,
    STA_ERR_CARD_SWIPED = 0x93,
} EM_STA_CODE;

typedef struct {
    unsigned char public_key;      // 公钥
    unsigned char read_mode;       // 读取模式
    unsigned char *pMask_Pan;      // 主账号屏蔽掩码
    unsigned int enc_flag;         // 加密算法标示
    unsigned char key_index;       // 密钥索引
    unsigned char EncKey_Len;      // 密钥长度
    unsigned char *pEncKey;        // 密钥
    unsigned char *pRandCode;      // 随机数
    unsigned char *pszWaterNo;     // 平台流水号
    unsigned char alg_mode;        // 算法模式
    unsigned int taglen;           //tag值
    unsigned int tagname;          //tag值
} InMagCard_ENC_t;

#define TRACK_ERROR         0x7f
#define TRACK_NODATA        0x7e
#define TRACKNUM            3
#define BUFMAXLEN           128


#define TRACK1_MAXLEN 79
#define TRACK2_MAXLEN 37
#define TRACK3_MAXLEN 104

enum EM_MAINTK {
    NOMAINTK = 0x00,                             /**<没有优先道，只要有1个磁道对	 */
    MAINTK1 = (1 << 0),                             /**<一磁道为优先道				 */
    MAINTK2 = (1 << 1),                             /**<二磁道为优先道				 */
    MAINTK3 = (1 << 2),                             /**<三磁道为优先道				 */
    MAINTK1_2 = (MAINTK1 | MAINTK2),         /**<一、二磁道为优先道			 */
    MAINTK1_3 = (MAINTK1 | MAINTK3),         /**<一、三磁道为优先道			 */
    MAINTK2_3 = (MAINTK2 | MAINTK3),         /**<二、三磁道为优先道			 */
    MAINTK1_2_3 = (MAINTK1 | MAINTK2 | MAINTK3) /**<一、二、三磁道为优先道		 */
};


typedef enum {
    MSDALG_UNIONPAY = 0x01,
}MAG_MSDAlgFlag;
#endif //__MAG_H_
