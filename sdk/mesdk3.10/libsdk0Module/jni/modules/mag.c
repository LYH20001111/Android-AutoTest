#include <string.h>
#include "NDK.h"
#include "command.h"
#include "alg.h"
#include "unistd.h"
#include <pthread.h>
#include <comm.h>
#include "cardmgr.h"
#include "mag.h"
#include "log.h"
#include "api.h"
#include "desc.h"
uchar g_ucMagSwiped;
char g_szPanCode[32];
char g_szTrack1[128];
char g_szTrack2[200];
char g_szTrack3[200];
const int nTrackMaxLen[TRACKNUM] = {TRACK1_MAXLEN, TRACK2_MAXLEN, TRACK3_MAXLEN};

extern ME_TPEDCTL g_METPEDCtl;
extern ME31_CONFIG_T g_me31conf;

int Pan_Shield_Mask(uchar *pmask, uchar *pindata, uchar *poutdata, uchar len) {
    uchar i;
    int nRet = 0;
    uchar uszMask[21], uszout[21]; //之前开辟20字节出现刷卡崩溃

    if (len > 20)
        return -1;
    memset(uszout, 0x0f, 20);
    memset(uszout, 0x0e, len);
    nRet = NDK_HexToAsc(pmask, 20, 0, uszMask);
    if (nRet != NDK_OK) LOGE_NDK("NDK_HexToAsc", nRet, NULL, 0);

    for (i = 0; i < len; i++) {
        if ((uszMask[i] == 'F') || (uszMask[i] == 'f'))
            continue;
        else
            uszout[i] = pindata[i];
    }
    nRet = NDK_AscToHex(uszout, 20, 0, poutdata);
    if (nRet != NDK_OK) LOGE_NDK("NDK_AscToHex", nRet, NULL, 0);
    return 0;
}

static void ProDealMainTk(uchar *pucMainTk, const char *pTk0, const char *pTk1, const char *pTk2) {
    LOGD_FMT("stripecheckflag[%d]", *pucMainTk);
    switch (*pucMainTk) {
        case MAINTK1_2:
            if ((pTk0[0] == TRACK_NODATA)) {
                *pucMainTk = MAINTK2;
            }
            break;
        case MAINTK1_3:
            if ((pTk0[0] == TRACK_NODATA)) {
                *pucMainTk = MAINTK3;
            }
            break;
        case MAINTK2_3:
            if (pTk2[0] == TRACK_NODATA) {
                *pucMainTk = MAINTK2;
            }
            break;
        case MAINTK1_2_3:
            if ((pTk2[0] == TRACK_NODATA) && (pTk0[0] == TRACK_NODATA)) {
                *pucMainTk = MAINTK2;
            } else if ((pTk2[0] == TRACK_NODATA)) {
                *pucMainTk = MAINTK1_2;
            } else if (pTk0[0] == TRACK_NODATA) {
                *pucMainTk = MAINTK2_3;
            }
            break;
        default:
            break;
    }
}

/**
 * @brief 格式化磁道信息
 * @param [in] pcTkDate ---- 磁道信息
 * @param [in] nMaxLen ---- 磁道最大长度
 * @param [out] pcTkDate ---- 规范后磁道信息
 * @return
 * @li 无
 * @author 廖华仔
 * @date 2012-11-30
 */
static void ProFormatTKData(char *pcTkData, int nMaxLen) {
    int nI, nLen;

    if ((pcTkData[0] == TRACK_NODATA) || (pcTkData[0] == 0x00)) /* 错误/无数据 */
    {
        pcTkData[0] = 0x00;
    } else {
        nLen = strlen(pcTkData);
        for (nI = 0; nI < nLen; nI++) {
            /* 磁道结束符，要将字符串及时结束 */
            if ((pcTkData[nI] == TRACK_NODATA) || (pcTkData[nI] == '?')) {
                pcTkData[nI] = 0x00;
                break;
            } else if ((pcTkData[nI] == '\'') || (pcTkData[nI] == '>')) /* 出现分割符 */
            {
                pcTkData[nI] = '=';
            }
        }

        if (pcTkData[0] == ':')   /* IBM格式校验错第一字符会返回':' */
        {
            memmove(pcTkData, pcTkData + 1, strlen(pcTkData) - 1);
        }

        if ((nMaxLen > 0) && (nMaxLen < strlen(pcTkData))) {
            pcTkData[nMaxLen] = 0x00;
        }
    }
}

/**
 * @brief 根据优先道判断nLrc
 * @param [in] ucMainTk ---- 优先磁道
 * @param [in] nLrc
 * @return
 * @li APP_FAIL 失败
 * @li APP_SUCC 成功
 * @author 廖华仔
 * @date 2012-11-30
 */
static int ProJudgeLrc(uchar ucMainTk, int nLrc) {
    LOGD_FMT("stripecheckflag[%d] NDK_MagReadNormal pnErrorCode[%d]", ucMainTk, nLrc);
    enum SYS_ERRORCODE {
        MAGCARD_TK1_LRC_ERR = 0xC01,                                         // 一道LRC校验失败
        MAGCARD_TK2_LRC_ERR = 0xC02,                                         // 二道LRC校验失败
        MAGCARD_TK3_LRC_ERR = 0xC08,                                         // 三道LRC校验失败
        MAGCARD_TK12_LRC_ERR = MAGCARD_TK1_LRC_ERR | MAGCARD_TK2_LRC_ERR,     // 一、二道LRC校验失败
        MAGCARD_TK23_LRC_ERR = MAGCARD_TK2_LRC_ERR | MAGCARD_TK3_LRC_ERR,     // 二、三道LRC校验失败
        MAGCARD_TK13_LRC_ERR = MAGCARD_TK1_LRC_ERR | MAGCARD_TK3_LRC_ERR,     // 一、三道LRC校验失败
        MAGCARD_TK123_LRC_ERR
        = MAGCARD_TK1_LRC_ERR | MAGCARD_TK2_LRC_ERR | MAGCARD_TK3_LRC_ERR     // 一、二、三道LRC校验失败
    };

    if (nLrc == APP_SUCC) {
        return APP_SUCC;
    } else {
        switch (ucMainTk) {
            case MAINTK1:
                if ((nLrc & MAGCARD_TK1_LRC_ERR) == 0xC00) {
                    return APP_SUCC;
                }
                break;
            case MAINTK2:
                if ((nLrc & MAGCARD_TK2_LRC_ERR) == 0xC00) {
                    return APP_SUCC;
                }
                break;
            case MAINTK3:
                if ((nLrc & MAGCARD_TK3_LRC_ERR) == 0xC00) {
                    return APP_SUCC;
                }
                break;
            case MAINTK1_2:
                if ((nLrc & MAGCARD_TK12_LRC_ERR) == 0xC00) {
                    return APP_SUCC;
                }
                break;
            case MAINTK1_3:
                if ((nLrc & MAGCARD_TK13_LRC_ERR) == 0xC00) {
                    return APP_SUCC;
                }
                break;
            case MAINTK2_3:
                if ((nLrc & MAGCARD_TK23_LRC_ERR) == 0xC00) {
                    return APP_SUCC;
                }
                break;

            case MAINTK1_2_3:
                if ((nLrc & MAGCARD_TK123_LRC_ERR) == 0xC00) {
                    return APP_SUCC;
                }
                break;
            default:
                if ((nLrc & MAGCARD_TK123_LRC_ERR) != MAGCARD_TK123_LRC_ERR) {
                    return APP_SUCC;
                }
                break;
        }
    }
    return APP_FAIL;
}
/**
 * @brief 磁道信息处理
 * @param [in] pcTkDate ---- 磁道信息
 * @param [in] nLrc ---- 磁道nLrc
 * @param [out] pcTkDate ---- 规范后磁道信息
 * @return
 * @li 无
 * @author 廖华仔
 * @date 2012-11-30
 */
int ProDealWithTKData(uchar *pucMainTk, char *pszTkData, int nLrc, int *pnReadNum) {
    //*pucMainTk:刷磁卡时数据有效性判断原则高四位  *pszTkData:三磁道信息  nLrc:刷卡返回的错误码 pnReadNum:无效
    LOGD_FMT("stripecheckflag[%d] NDK_MagReadNormal pnErrorCode[%d]", *pucMainTk, nLrc);
    int nI;
    char pchTk[TRACKNUM][BUFMAXLEN];

    memset(pchTk, 0, sizeof(pchTk));
    memcpy(pchTk, pszTkData, TRACKNUM * BUFMAXLEN);

    /* 根据磁道信息判断优先道*/
    ProDealMainTk(pucMainTk, pchTk[0], pchTk[1], pchTk[2]);

    /* 格式化一、二、三磁道数据 */
    for (nI = 0; nI < TRACKNUM; nI++) {
        ProFormatTKData(pchTk[nI], nTrackMaxLen[nI]);
    }

    /* 根据优先道判断lrc */
    if (ProJudgeLrc(*pucMainTk, nLrc) == APP_SUCC) {
        LOGD_FMT("format success!!!");
        memset(pszTkData, 0, TRACKNUM * BUFMAXLEN);
        memcpy(pszTkData, pchTk, TRACKNUM * BUFMAXLEN);
        return APP_SUCC;
    }
    Udebug.DEBUG_Levelone("ProDealWithTKData FAIL");
    return APP_FAIL;
}

int ProJudgeResult(char *pszTk1, char *pszTk2, char *pszTk3, const char *pszTkData, uchar ucMainTk) {
    char pchTk[TRACKNUM][BUFMAXLEN];
    const uchar gucTrackFlag[TRACKNUM] = {MAINTK1, MAINTK2, MAINTK3};
    int nI;
    uchar ucTkLenFlag = 0x00, ucTkErrFlag = 0x00;

    memset(pchTk, 0, sizeof(pchTk));
    memcpy(pchTk, pszTkData, TRACKNUM * BUFMAXLEN);

    for (nI = 0; nI < TRACKNUM; nI++) {
        if (pchTk[nI][0] != TRACK_ERROR) {
            ucTkErrFlag |= gucTrackFlag[nI];
        }
        if (pchTk[nI][0] != TRACK_NODATA && pchTk[nI][0] != 0x00) {
            ucTkLenFlag |= gucTrackFlag[nI];
        }
    }
    LOGD_FMT("stripeflag[%d] ucTkErrFlag[%d] ucTkLenFlag[%d]", ucMainTk, ucTkErrFlag, ucTkLenFlag);
    LOGD_FMT("lenFlag[%d] errFlag[%d]", (ucMainTk & ucTkLenFlag), (ucMainTk & ucTkErrFlag));
    if (ucMainTk == NOMAINTK) {
        if (ucTkLenFlag == 0x00) {
            return APP_FAIL;
        }
        if (ucTkErrFlag == 0x00) {
            return APP_FAIL;
        }
    } else {
        if ((ucMainTk & ucTkLenFlag) != ucMainTk) {
            return APP_FAIL;
        }
        if ((ucMainTk & ucTkErrFlag) != ucMainTk) {
            return APP_FAIL;
        }
    }

    if (pchTk[0][0] != TRACK_ERROR) {
        memcpy(pszTk1, pchTk[0], strlen(pchTk[0]));
    }
    if (pchTk[1][0] != TRACK_ERROR) {
        memcpy(pszTk2, pchTk[1], strlen(pchTk[1]));
    }
    if (pchTk[2][0] != TRACK_ERROR) {
        memcpy(pszTk3, pchTk[2], strlen(pchTk[2]));
    }

    return APP_SUCC;
}

static int __trackAlgUnionPay(int cmd,uchar type, uchar index, char *trackData, uchar *trackDataOut) {
    unsigned char trackBlock[8 + 1];
    unsigned char trackBlockEnc[8 + 1];
    int trackLen,nOff;
    char encSrcData[16 + 1];
    char encDestData[16 + 1];

    memset(trackBlock, 0, sizeof(trackBlock));
    memset(trackBlockEnc, 0, sizeof(trackBlockEnc));
    memset(encSrcData, 0, sizeof(encSrcData));
    memset(encDestData, 0, sizeof(encDestData));
    trackLen = strlen(trackData);
    LOGD_FMT("type[%d] index[%d] trackLen[%d]", type, index, trackLen);
    LOGD_STR("trackData", trackData, strlen(trackData));//363232373030313835323531303937343637343D3431303835323036303131303230303030
    if (trackLen < 17)
        return -1;
    if (trackLen % 2 != 0)
        nOff = 17;//29->
    else
        nOff = 18;//30->

    memcpy(encSrcData, trackData + trackLen - nOff, 16);//截取二磁道倒数第二字节往前16字节 34313038353230363031313032303030
    LOGD_STR("encSrcData", encSrcData, sizeof(encSrcData));
    if(!EXEC_NDK("NDK_AscToHex",NDK_AscToHex((unsigned char *) encSrcData, 16, 0, (unsigned char *) trackBlock),NDK_OK,cmd)){
        return -1;
    }

    if(!EXEC_NDK("NDK_SecCalcDes",NDK_SecCalcDes(type, index, trackBlock, 8, trackBlockEnc, SEC_DES_ENCRYPT),NDK_OK,cmd)){
        return -1;
    }
    LOGD_STR("trackBlockEnc", trackBlockEnc, sizeof(trackBlockEnc));

    if(!EXEC_NDK("NDK_HexToAsc",NDK_HexToAsc((unsigned char *) trackBlockEnc, 16, 0, (unsigned char *) encDestData),NDK_OK,cmd)){
        return -1;
    }
    LOGD_STR("encDestData", encDestData, sizeof(encDestData));
    memcpy(trackData + trackLen - nOff, encDestData, 16);
    if (trackDataOut != NULL)
        memcpy(trackDataOut, trackData, trackLen);
    return trackLen;
}

int Mag_ReadTrackPlain(puchar pbuf, int buf_len, unsigned char *pOut, int *outLen)
{
    int nLen, extoffset;
    char headCode[2];
    uchar nReadTrackMode, pan[40],szDateCode[8],*point;
    puchar pTrack1, pTrack2, pTrack3;

    extoffset = 2;

    nReadTrackMode = nlMpos_Command.mpos_getvar(pbuf, _VAR_BIT8);

    memset(szDateCode, 0x00, sizeof(szDateCode));

    if(g_ucMagSwiped != 1){
        ERRMSG(SDK_ERR_MAG_NO_SWIPED,MAG_READTRACKPLAIN);
        pOut[extoffset] = STA_ERR_CARD_SWIPED;
        memcpy(headCode, CMD_ERR_OTHER, 2);
        goto ON_ERR;
    }

    g_ucMagSwiped = 0;
    pTrack1 = (uchar *) g_szTrack1;
    pTrack2 = (uchar *) g_szTrack2;
    pTrack3 = (uchar *) g_szTrack3;
    pOut[extoffset] = STA_OK;
    memcpy(headCode, CMD_OK, 2);

    EXEC_NDK("NDK_MagClose",NDK_MagClose(),NDK_OK,MAG_READTRACKPLAIN);

    memset(g_szPanCode, 0x00, sizeof(g_szPanCode));
    memset(pan, 0xFF, sizeof(pan));
    point = (uchar *) strchr((char *) (pTrack2), '=');
    int panLen = 0;
    if (point) {
        memcpy(g_szPanCode, pTrack2, (point - pTrack2));
        LOGD_FMT("g_szPanCode[%s] panLen[%d]",g_szPanCode,(point - pTrack2));
        #if 0
        int panLen =0;
        int ret = mpos_AscToBcd(pan, (uint *) &panLen, (char *) g_szPanCode, ALIGN_LEFT | FILL_F);
        if (ret != SUCC) {
            pOut[extoffset] = STA_ERR_CARD_FRAME;
            goto ON_ERR;
        }
        #endif
        panLen = strlen(g_szPanCode);
        memcpy(pan,g_szPanCode,panLen);
        memcpy(szDateCode, point + 1, 7);
        LOGD_STR("pan",pan,panLen);
        LOGD_STR("date|code",szDateCode,7);
    } else {
        memset(pan, 0x00, sizeof(pan));
        memset(g_szPanCode, 0x00, sizeof(g_szPanCode));
        memset(szDateCode, 0x00, sizeof(szDateCode));
    }

    extoffset+=1;
    nlMpos_Command.mpos_writelen(pOut + extoffset, panLen, _VAR_BIT16);extoffset+=2;//pan len
    memcpy(pOut + extoffset, pan, panLen);extoffset+=panLen;//pan
    EXEC_NDK("NDK_AlgSHA1",NDK_AlgSHA1((unsigned char*)g_szPanCode, strlen((char*) g_szPanCode), pOut+extoffset),NDK_OK,MAG_READTRACKPLAIN);extoffset+=20;//sha

    pOut[extoffset] = nReadTrackMode;extoffset+=1;

    if (nReadTrackMode & TK1) {
        nLen = strlen((char *) pTrack1);
        if ((pTrack1[0] == 0x7f) || (pTrack1[0] == 0x7e)) {
            nLen = 0;
        }
        nlMpos_Command.mpos_writelen(pOut + extoffset, nLen, _VAR_BIT16);
        extoffset += 2;
        memcpy(pOut + extoffset, pTrack1, nLen);
        extoffset += nLen;
    } else {
        nlMpos_Command.mpos_writelen(pOut + extoffset, 0x00, _VAR_BIT16);
        extoffset += 2;
    }
    if (nReadTrackMode & TK2) {
        nLen = strlen((char *) pTrack2);
        if ((pTrack2[0] == 0x7f) || (pTrack2[0] == 0x7e)) {
            nLen = 0;
        }
        nlMpos_Command.mpos_writelen(pOut + extoffset, nLen, _VAR_BIT16);
        extoffset += 2;
        memcpy(pOut + extoffset, pTrack2, nLen);
        extoffset += nLen;
    } else {
        nlMpos_Command.mpos_writelen(pOut + extoffset, 0x00, _VAR_BIT16);
        extoffset += 2;
    }
    if (nReadTrackMode & TK3) {
        nLen = strlen((char *) pTrack3);
        if ((pTrack3[0] == 0x7f) || (pTrack3[0] == 0x7e)) {
            nLen = 0;
        }
        nlMpos_Command.mpos_writelen(pOut + extoffset, nLen, _VAR_BIT16);
        extoffset += 2;
        memcpy(pOut + extoffset, pTrack3, nLen);
        extoffset += nLen;
    } else {
        nlMpos_Command.mpos_writelen(pOut + extoffset, 0x00, _VAR_BIT16);
        extoffset += 2;
    }

    memcpy(pOut + extoffset, szDateCode, 7);
    extoffset += 7;

    responseCmd(pOut, extoffset - 2, outLen, headCode);
    return 0;

    ON_ERR:
    extoffset+=1;
    nlMpos_Command.mpos_writelen(pOut + extoffset, 0x00, _VAR_BIT16);extoffset+=2;//pan len
    //memset(pOut + extoffset, 0, 20);// pan
    extoffset += 20;
    memset(pOut + extoffset, 0, 20);//sha
    extoffset += 20;
    pOut[extoffset] = nReadTrackMode;
    extoffset++;
    memset(pOut + extoffset, 0x00, 6);
    extoffset += 6;

    memset(pOut + extoffset, 0x00, 7);//有效期
    extoffset += 7;

    responseCmd(pOut,extoffset-2, outLen, headCode);
    return 0;
}

int Mag_ReadTrackEncrypt(puchar pbuf, int buf_len, unsigned char *pOut, int *outLen)
{
    int offset = 0;
    int keySys = nlMpos_Command.mpos_getvar(pbuf + offset, _VAR_BIT8);offset++;
    int msdAlgFlag = nlMpos_Command.mpos_getvar(pbuf + offset, _VAR_BIT8);offset++;
    int mode = nlMpos_Command.mpos_getvar(pbuf + offset, _VAR_BIT8);offset++;
    char *panMask = pbuf + offset; offset += 10;
    int keyIndex  = nlMpos_Command.mpos_getvar(pbuf + offset, _VAR_BIT8);offset++;
    int outKeyLen = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);offset += 2;
    char *outKeyData = pbuf + offset;offset += outKeyLen;

    LOGD_FMT(">>>keySys[%d] msdAlgFlag[%d] mode[%d] keyIndex[%d]",keySys,msdAlgFlag,mode,keyIndex);
    LOGD_STR("panMask",panMask,10);
    char szDateCode[7+1];char ksn[10];
    uchar pan[32];
    puchar pTrack1 = NULL, pTrack2 = NULL, pTrack3 = NULL;

    if(g_ucMagSwiped != 1){
        ERRMSG(SDK_ERR_MAG_NO_SWIPED,MAG_READTRACKENCRYPT);
        goto ON_ERR;
    }
    g_ucMagSwiped = 0;
    pTrack1 = (uchar*)g_szTrack1;
    pTrack2 = (uchar*)g_szTrack2;
    pTrack3 = (uchar*)g_szTrack3;

    EXEC_NDK("NDK_MagClose",NDK_MagClose(),NDK_OK,MAG_READTRACKENCRYPT);

    memset(ksn,0,sizeof(ksn));
    memset(g_szPanCode, 0x00, sizeof(g_szPanCode));
    memset(pan, 0xFF, 20);
    uchar *point = (uchar *) strchr((char *) (pTrack2), '=');
    if(point == NULL){
        goto ON_ERR;
    }
    if (point) {
        memcpy(g_szPanCode, pTrack2, (point - pTrack2));
        memset(szDateCode, 0x00, sizeof(szDateCode));
        memcpy(szDateCode, point + 1,7);
        Pan_Shield_Mask(panMask, (unsigned char *) g_szPanCode, pan, strlen((char *) g_szPanCode));
    }

    int extoffset = 2;
    char sha[20];
    memset(sha,0, sizeof(sha));

    pOut[extoffset] = STA_OK;extoffset+=1;

    memcpy(pOut + extoffset, pan, 20);extoffset+=20;

    EXEC_NDK("NDK_AlgSHA1",NDK_AlgSHA1((uchar*) g_szPanCode,strlen((char*) g_szPanCode),sha),NDK_OK,MAG_READTRACKENCRYPT);
    memcpy(pOut + extoffset, sha, 20);extoffset+=20;

    uchar track1Out[256],track2Out[256],track3Out[256];
    int track1OutLen = 0, track2OutLen = 0,track3OutLen = 0;
    //track1
    int ret = -1;
    int track1Len = strlen((char*)pTrack1);
    LOGD_FMT(">>>track1Len[%d]",track1Len);
    if((mode&TK1)&&( track1Len != 1)){
        track1OutLen = 0;
        if(msdAlgFlag == MSDALG_UNIONPAY){
            ret = __trackAlgUnionPay(MAG_READTRACKENCRYPT,SEC_KEY_TYPE_TDK,keyIndex,pTrack1,track1Out);
        }
        if(ret >0){
            track1OutLen = track1Len;
        }
    }
    //track2
    ret = -1;
    int track2Len = strlen((char*)pTrack2);
    LOGD_FMT(">>>track2Len[%d]",track2Len);
    if((mode&TK2)&&( track2Len != 1)){
        track2OutLen = 0;
        if(msdAlgFlag == MSDALG_UNIONPAY){
            ret = __trackAlgUnionPay(MAG_READTRACKENCRYPT,SEC_KEY_TYPE_TDK,keyIndex,pTrack2,track2Out);
        }
        if(ret >0){
            track2OutLen = track2Len;
        }
    }

    //track3
    ret = -1;
    int track3Len = strlen((char*)pTrack3);
    LOGD_FMT(">>>track3Len[%d]",track3Len);
    if((mode&TK3)&&(track3Len != 1)){
        track3OutLen = 0;
        if(msdAlgFlag == MSDALG_UNIONPAY){
            ret = __trackAlgUnionPay(MAG_READTRACKENCRYPT,SEC_KEY_TYPE_TDK,keyIndex,pTrack3,track3Out);
        }
        if(ret > 0){
            track3OutLen = track3Len;
        }
    }
    if(track1OutLen<=0&&track2OutLen<=0&&track3OutLen<=0){
        LOGD_FMT("trackOutLen==0 error.");
        goto ON_ERR;
    }
    nlMpos_Command.mpos_writelen(pOut + extoffset,track1OutLen, _VAR_BIT16);extoffset += 2;
    memcpy(pOut+extoffset,track1Out,track1OutLen);extoffset+=track1OutLen;
    nlMpos_Command.mpos_writelen(pOut + extoffset,track2OutLen, _VAR_BIT16);extoffset += 2;
    memcpy(pOut+extoffset,track2Out,track2OutLen);extoffset+=track2OutLen;
    nlMpos_Command.mpos_writelen(pOut + extoffset,track3OutLen, _VAR_BIT16);extoffset += 2;
    memcpy(pOut+extoffset,track3Out,track3OutLen);extoffset+=track3OutLen;
    memcpy(pOut+extoffset,szDateCode,7);extoffset+=7;
    memcpy(pOut+extoffset,ksn,10);extoffset+=10;
    responseCmd(pOut,extoffset-2,outLen,CMD_OK);
    return 0;
    ON_ERR:
    responseCmd(pOut,0,outLen,CMD_ERR_OTHER);
    return 0;
}

int Mag_CalculateTrack(puchar pbuf, int buf_len, unsigned char *pOut, int *outLen)
{
    uchar track1Out[256],track2Out[256],track3Out[256];
    int track1OutLen = 0, track2OutLen = 0,track3OutLen = 0;

    int offset = 0;
    int  keySys = nlMpos_Command.mpos_getvar(pbuf + offset, _VAR_BIT8);offset += 1;
    int  msdAlgFlag = nlMpos_Command.mpos_getvar(pbuf + offset, _VAR_BIT8);offset += 1;

    int track1Len = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);offset += 2;
    uchar *track1Data = pbuf + offset;offset += track1Len;

    int track2Len = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);offset += 2;
    uchar *track2Data = pbuf + offset;offset += track2Len;

    int track3Len = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);offset += 2;
    uchar *track3Data = pbuf + offset;offset += track3Len;

    int keyIndex = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT8);offset += 1;

    int outKeyLen = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);offset += 2;
    int outKeyDataLen = pbuf + offset;offset += outKeyLen;

    LOGD_FMT(">>>keySys[%d] msdAlgFlag[%d] keyIndex[%d] track1Len[%d] track2Len[%d] track3Len[%d] outKeyLen[%d]",keySys,msdAlgFlag,keyIndex,track1Len,track2Len,track3Len,outKeyLen);
    memset(track1Out,0, sizeof(track1Out));
    memset(track2Out,0, sizeof(track2Out));
    memset(track3Out,0, sizeof(track3Out));
    if(track1Len < 0 || track2Len < 0 || track3Len < 0 || track1Len > 256 || track2Len > 256 || track3Len > 256){
        ERRMSG(SDK_ERR_PARAM,MAG_CALCULATETRACK);
        goto ON_ACK;
    }

    if(outKeyLen!=0){
        //loadkey
    }
    char ksn[10];
    memset(ksn,0, sizeof(ksn));

    int extoffset = 2;
    pOut[extoffset] = STA_OK;extoffset+=1;
    //track1
    int ret = -1;
    if(track1Len!=0){
        track1OutLen = 0;
        if(msdAlgFlag == MSDALG_UNIONPAY){
            ret = __trackAlgUnionPay(MAG_CALCULATETRACK,SEC_KEY_TYPE_TDK,keyIndex,track1Data,track1Out);
        }
        if(ret > 0){
            track1OutLen = track1Len;
        }
    }

    //track2
    ret = -1;
    if(track2Len!=0){
        track2OutLen = 0;
        if(msdAlgFlag == MSDALG_UNIONPAY){
            ret = __trackAlgUnionPay(MAG_CALCULATETRACK,SEC_KEY_TYPE_TDK,keyIndex,track2Data,track2Out);
        }
        if(ret > 0){
            track2OutLen = track2Len;
        }
    }

    //track3
    ret = -1;
    if(track3Len!=0){
        track3OutLen = 0;
        if(msdAlgFlag == MSDALG_UNIONPAY){
            ret = __trackAlgUnionPay(MAG_CALCULATETRACK,SEC_KEY_TYPE_TDK,keyIndex,track3Data,track3Out);
        }
        if(ret > 0){
            track3OutLen = track3Len;
        }
    }
    nlMpos_Command.mpos_writelen(pOut + extoffset,track1OutLen, _VAR_BIT16);extoffset += 2;
    memcpy(pOut+extoffset,track1Out,track1OutLen);extoffset+=track1OutLen;
    nlMpos_Command.mpos_writelen(pOut + extoffset,track2OutLen, _VAR_BIT16);extoffset += 2;
    memcpy(pOut+extoffset,track2Out,track2OutLen);extoffset+=track2OutLen;
    nlMpos_Command.mpos_writelen(pOut + extoffset,track3OutLen, _VAR_BIT16);extoffset += 2;
    memcpy(pOut+extoffset,track3Out,track3OutLen);extoffset+=track3OutLen;
    memcpy(pOut+extoffset,ksn,10);extoffset+=10;//ksn
    responseCmd(pOut, extoffset - 2, outLen, CMD_OK);
    return 0;
    ON_ACK:
    responseCmd(pOut, 0, outLen, CMD_ERR_OTHER);
    return 0;
}

int CardReader_Close(puchar pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    char headCode[2];
    g_ucMagSwiped = 0;
    memcpy(headCode, CMD_OK, 2);

    EXEC_NDK("NDK_OK",NDK_MagClose(),NDK_OK,CARDREADER_CLOSE);

    responseCmd(pOut, 0, outLen, headCode);

    return 0;
}