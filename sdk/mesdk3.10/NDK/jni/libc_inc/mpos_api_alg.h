#ifndef _API_ALG_H_
#define _API_ALG_H_

/*以下宏定义AsciiToBCD中ucType使用*/
#define	SEC_DES_ECBMODE  (1<<3)
#define	SEC_DES_CBCMODE  (1<<4)

#define ALIGN_BIT	(1<<0)	/*对齐类型位*/
#define FILL_BIT	(1<<1)	/*填充类型位*/

#define ALIGN_LEFT	(0<<0)	/*左对齐*/
#define ALIGN_RIGHT	(1<<0)	/*右对齐*/
#define FILL_0		(0<<1)	/*不足填充0*/
#define FILL_F		(1<<1)	/*不足填充F*/

int mpos_AscToBcd(unsigned char *pucBCDBuf, unsigned int *puiLen, char *szAscii, unsigned char ucType);
int PubAscToHex (const unsigned char* pszAsciiBuf, int nLen, char cType, unsigned char* pszBcdBuf);
int PubHexToAsc (const unsigned char* pszBcdBuf, int nLen, char cType, unsigned char* pszAsciiBuf);


int ME_SecSetCBCInitValue(uchar *pvalue);
int ME_SecCalcDesIndex(uchar ucKeyType, uchar ucKeyIdx, uchar * psDataIn, int nDataInLen, uchar *psDataOut, uchar ucMode);
int UnionPay_TrackEncrypt(uchar type,uchar index,char* szTrack,uchar *szout);
int Fuyou_TrackEncrypt(uchar key_type,uchar key_index,uchar ntrk, char *szTrack,char *szext,uchar *szout);
int MiniPay_TrackEncrypt(uchar key_type,uchar key_index,char *szTrack,uchar *szout);
int IcardPay_TrackEncrypt(uchar key_type,uchar key_index,uchar ntrk,char *szTrack,uchar *szout);
int TongLian_TrackEncrypt(uchar key_type, uchar key_index, uchar ntrk, char * szTrack, uchar * szout);
int QuaiQian_TrackEncrypt(uchar key_type,uchar key_index,uchar ntrk, char *szTrack,char *szext,uchar *szout);
int WuHanQD_TrackEncrypt(uchar key_type,uchar key_index,uchar ntrk,char *szTrack,unsigned char *szext,uchar *szout);
int ChinaUms_TrackEncrypt(uchar key_type,uchar key_index,uchar ntrk,char *szTrack,unsigned char *pszWaterNo,uchar *szout);
int Lakala_TrackEncrypt(uchar key_type,uchar key_index, char *szTrack2, char *szTrack3, uchar *szout);
int GuotongFuyou_TrackEncrypt(uchar key_type,uchar key_index,uchar ntrk, char *szTrack,char *szext,uchar *szout);
int Eptok_TrackEncrypt(uchar type,uchar index,char* szTrack,uchar *szout);
int ME_SecKeyDispersion(uchar ucKeyType, uchar ucKeyIdx, uchar * pszRandom, uint nRandomLen, uchar ucMode,uchar *pOutKey,uint *pOutKeyLen);

#endif
