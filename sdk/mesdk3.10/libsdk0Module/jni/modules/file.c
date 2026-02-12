#include "NDK.h"
#include <string.h>
#include "unistd.h"
#include "command.h"
#include <signal.h>
#include <pthread.h>
#include "log.h"
#include "api.h"
#include "threadmutex.h"
#include "threadtool.h"
#include "comm.h"
int busy = 0;
static void filelock(int type)
{
    if(type==0)
        THREAD_MUTEX_LOCK(THREAD_MUTEX_INDEX_FILE_W);
    if(type==1)
        THREAD_MUTEX_UNLOCK(THREAD_MUTEX_INDEX_FILE_W);
}
int ME_FsWrite(const char *pszName, const unsigned char *psBuffer, uint unOffset, uint unLength) {
    int fd;
    int nRet;

    fd = NDK_FsOpen(pszName, "w");
    if (fd < 0) {
        LOGE_NDK("NDK_FsOpen", fd, NULL, 0);
        Udebug.ERROR_MSG_LOG("pszName[%s]", pszName);
        return -1;
    }

    nRet = NDK_FsSeek(fd, unOffset, SEEK_SET);
    if (nRet != NDK_OK) {
        LOGE_NDK("NDK_FsSeek", nRet, NULL, 0);
    }
    Udebug.DEBUG_string_Levelone(psBuffer, unLength);
    nRet = NDK_FsWrite(fd, (char *) psBuffer, unLength);
    if (nRet != unLength) {
        LOGE_NDK("NDK_FsWrite", nRet, psBuffer, unLength);
        nRet = NDK_FsClose(fd);
        if (nRet != NDK_OK) {
            LOGE_NDK("NDK_FsClose", nRet, NULL, 0);
        }
        return -2;
    }
    nRet = NDK_FsClose(fd);
    if (nRet != NDK_OK) {
        LOGE_NDK("NDK_FsClose", nRet, NULL, 0);
    }
    return 0;
}

int ME_FsRead(const char *pszName, unsigned char *psBuffer, uint unOffset, uint unLength) {
    int fd;
    int nRet;

    memset(psBuffer, 0x00, unLength);
    nRet = NDK_FsExist(pszName);
    if (nRet != 0) {
        Udebug.ERROR_MSG_LOG("%s %d NDK_FsExist:nRet:%d\n", __FUNCTION__, __LINE__, nRet);
        return nRet;
    }
    fd = NDK_FsOpen(pszName, "r");
    if (fd < 0) {
        LOGE_NDK("NDK_FsOpen", fd, NULL, 0);
        Udebug.ERROR_MSG_LOG("pszName[%s]", pszName);
        return -1;
    }
    nRet = NDK_FsSeek(fd, unOffset, SEEK_SET);
    if (nRet != NDK_OK) LOGE_NDK("NDK_FsSeek", nRet, NULL, 0);
    nRet = NDK_FsRead(fd, (char *) psBuffer, unLength);
    if (nRet >= 0) {
        psBuffer[nRet] = 0;
    } else {
        LOGE_NDK("NDK_FsRead", nRet, NULL, 0);
        return -3;
    }
    nRet = NDK_FsClose(fd);
    if (nRet != NDK_OK) LOGE_NDK("NDK_FsClose", nRet, NULL, 0);
    return 0;
}

int ME_RecordGetNum(const char *pszName, uint *punNum) {
    int i = 0, nRet = 0;
    uint unFileLen = 0, unRecordLen = 0, unRecordMax = 0, unRecordNum = 0;
    uchar uszFileAttr[20] = {0};

    *punNum = 0;
    unRecordLen = 0;
    unRecordMax = 0;
    unRecordNum = 0;
    nRet = NDK_FsFileSize(pszName, &unFileLen);
    if ((nRet != 0) || (unFileLen < 10)) // 至少有10个字节，因为初始化写进了10个字节
    {
        Udebug.ERROR_MSG_LOG("%s %d NDK_FsFileSize:nRet:%d\n", __FUNCTION__, __LINE__, nRet);
        return -1;
    }
    if (ME_FsRead(pszName, uszFileAttr, 0, 10) != 0) // 读取最前面的10个字节，最前面有记录的长度
        return -2;

    nRet = NDK_C2ToInt(&unRecordLen, uszFileAttr);     // 得到每条记录的长度
    if (nRet != 0) LOGE_NDK("NDK_C2ToInt", nRet, NULL, 0);
    Udebug.DEBUG_Levelone("\r\nlen %x %x %x\r\n", unFileLen, unRecordLen, (unFileLen - 10) / (unRecordLen + 2));

    if ((unFileLen - 10) % (unRecordLen + 2) != 0) // 加2是因为保存记录的时候，加入了LL
    {
        return -3;
    } else {
        unRecordMax = (unFileLen - 10) / (unRecordLen + 2); // 整数倍
    }
    if (unRecordMax == 0)
        return 0;

    for (i = 0; i < unRecordMax; i++) {
        nRet = ME_FsRead(pszName, uszFileAttr, 10 + (unRecordLen + 2) * i, 2);
        if (nRet < 0)
            return -4;
        if ((uszFileAttr[0] == 0x00) && (uszFileAttr[1] == 0x00))        //记录不存在
            ;
        else
            unRecordNum++;
    }
    *punNum = unRecordNum;
    return nRet;
}


int ME_RecordGetOffset(const char *pszName, int nRecNo, int *pnRecOffset) {
    int i = 0, nRet = 0;
    uint unFileLen = 0, unRecordLen = 0, unRecordMax = 0, unRecordNum = 0;
    uchar uszFileAttr[20] = {0};

    unRecordLen = 0;
    unRecordMax = 0;
    unRecordNum = 0;
    nRet = NDK_FsFileSize(pszName, &unFileLen);

    if ((nRet != 0) || (unFileLen < 10)) // 至少有10个字节，因为初始化写进了10个字节
    {
        Udebug.ERROR_MSG_LOG("%s %d NDK_FsFileSize:nRet:%d\n", __FUNCTION__, __LINE__, nRet);
        return -1;
    }
    if (ME_FsRead(pszName, uszFileAttr, 0, 10) != 0) // 读取最前面的10个字节，最前面有记录的长度
        return -2;

    nRet = NDK_C2ToInt(&unRecordLen, uszFileAttr);     // 得到每条记录的长度
    if (nRet != NDK_OK) LOGE_NDK("NDK_C2ToInt", nRet, NULL, 0);
    Udebug.DEBUG_Levelone("\r\nfilelen 0x%x,recordlen 0x%x,filenum 0x%x\r\n", unFileLen, unRecordLen, (unFileLen - 10) / (unRecordLen + 2));

    if ((unFileLen - 10) % (unRecordLen + 2) != 0) // 加2是因为保存记录的时候，加入了LL
    {
        return -3;
    } else {
        unRecordMax = (unFileLen - 10) / (unRecordLen + 2); // 整数倍
    }

    for (i = 0; i < unRecordMax; i++) {
        nRet = ME_FsRead(pszName, uszFileAttr, 10 + (unRecordLen + 2) * i, 2);
        if (nRet < 0)
            return -4;
        if ((uszFileAttr[0] == 0x00) && (uszFileAttr[1] == 0x00))        //记录不存在
            ;
        else
            unRecordNum++;
        if (nRecNo == unRecordNum) {
            *pnRecOffset = 10 + (unRecordLen + 2) * i;
            break;
        }
    }
    if (i == unRecordMax)
        return -5;
    return nRet;
}

int ME_RecordSearch(const char *pszName, uint nSearch1Len, uchar *psSearch1, uint nSearch2Len, uchar *psSearch2) {
    int i = 0, nRet = 0, iRet = -1;
    int unFileOffset = 0;
    uint unDataLen = 0, unRecordNum = 0, unRecordLen = 0, unField1Len = 0, unField1Offset = 0, unField2Len = 0, unField2Offset = 0;
    uchar uszFileAttr[20] = {0};
    uchar uszRecords[1026] = {0};                //160

    unFileOffset = 0;
    nRet = ME_RecordGetNum(pszName, &unRecordNum);
    if ((nRet != 0)) {
        return nRet;
    }

    nRet = ME_FsRead(pszName, uszFileAttr, 0, 10);
    if (nRet != 0) {
        return -1;
    }

    iRet = NDK_C2ToInt(&unRecordLen, uszFileAttr); // 得到每条记录的长度
    if (iRet != NDK_OK) {
        LOGE_NDK("NDK_C2ToInt", iRet, NULL, 0);
    }
    iRet = NDK_C2ToInt(&unField1Offset, uszFileAttr + 2);
    if (iRet != NDK_OK) {
        LOGE_NDK("NDK_C2ToInt", iRet, NULL, 0);
    }
    iRet = NDK_C2ToInt(&unField1Len, uszFileAttr + 4);
    if (iRet != NDK_OK) {
        LOGE_NDK("NDK_C2ToInt", iRet, NULL, 0);
    }
    iRet = NDK_C2ToInt(&unField2Offset, uszFileAttr + 6);
    if (iRet != NDK_OK) {
        LOGE_NDK("NDK_C2ToInt", iRet, NULL, 0);
    }
    iRet = NDK_C2ToInt(&unField2Len, uszFileAttr + 8);
    if (iRet != NDK_OK) {
        LOGE_NDK("NDK_C2ToInt", iRet, NULL, 0);
    }

    NDK_FsFileSize(pszName, &unDataLen);

    if ((unDataLen - 10) % (unRecordLen + 2) != 0) // 加2是因为保存记录的时候，加入了LL
    {
        return -3;
    } else {
        unRecordNum = (unDataLen - 10) / (unRecordLen + 2); // 整数倍
    }

    Udebug.DEBUG_Levelone("unRecordNum=%d,unFileOffset=%x\r\n", unRecordNum, unFileOffset);

    for (i = 0; i < unRecordNum; i++) {

        unFileOffset = 10 + (unRecordLen + 2) * i;
        nRet = ME_FsRead(pszName, uszRecords, unFileOffset, 2);
        if (nRet != NDK_OK) {
            Udebug.DEBUG_Levelone("ME_RecordGetOffset err %d\r\n", nRet);
            nRet = -2;
            goto on_ack;
        }
        NDK_BcdToInt(uszRecords, (int *) &unDataLen);
        if ((unDataLen == 0) || (unDataLen < unField1Offset) || (unDataLen < unField2Offset))
            continue;
        nRet = ME_FsRead(pszName, uszRecords, unFileOffset + 2, unDataLen);
        if (nRet != NDK_OK) {
            Udebug.DEBUG_Levelone("FsRead err\r\n");
            nRet = -2;
            goto on_ack;
        }
        Udebug.DEBUG_Levelone("\r\n1 %x,2 %x,datalen %x", nSearch1Len, nSearch2Len, unDataLen);
        Udebug.DEBUG_string_Levelone(uszRecords, unDataLen); // 打出记录
        if ((nSearch1Len != 0) && (nSearch2Len != 0)) {
            if ((memcmp(uszRecords + unField1Offset, psSearch1, nSearch1Len) == 0) &&
                (memcmp(uszRecords + unField2Offset, psSearch2, nSearch2Len) == 0))
                break;
        } else if (nSearch1Len != 0) {
            if (memcmp(uszRecords + unField1Offset, psSearch1, nSearch1Len) == 0)
                break;
        } else if (nSearch2Len != 0) {
            if (memcmp(uszRecords + unField2Offset, psSearch2, nSearch2Len) == 0)
                break;
        }

    }

    if (i == unRecordNum) {
        nRet = -2;
    } else {
        //nRet = i+1;
        nRet = unFileOffset;
    }
    on_ack:
    //NDK_FsClose(fd);
    return nRet;
}


/**********************************************************************************************
** 函数原型：文件类
** 功能描述：打开文件
** 输入参数：
** 输出参数：无
** 返回值：  无
** 调用关系：
**********************************************************************************************/
int File_OpenRecords(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int ret = 0, ndkRet = -1;
    int fd = 0, offset = 0;
    int nRet = 0, nFileNameLen = 0;
    char ret_code[2];
    char szFileName[64] = "/appfs/";

/* ====================== 处理 ===================*/
    offset = MPOS_VARIABLE_OFFSET;
    memcpy(ret_code, CMD_OK, 2);

    nFileNameLen = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);
    offset += 2;

    memcpy(szFileName + 7, pbuf + offset, nFileNameLen); // 记录名
    szFileName[nFileNameLen + 7] = '\0';

    offset += nFileNameLen; //


    ndkRet = NDK_FsDel(szFileName);
    if (ndkRet != NDK_OK) LOGE_NDK("NDK_FsDel", ndkRet, NULL, 0);
    fd = NDK_FsOpen(szFileName, "w");
    if (fd < 0) {
        LOGE_NDK("NDK_FsOpen", fd, pbuf, buf_len);
        Udebug.ERROR_MSG_LOG("szFileName[%s]", szFileName);
        ret = 6;
        //Udebug.DEBUG_Levelone("open err,fd=%d\r\n",fd);
        memcpy(ret_code, CMD_ERR_OTHER, 2);
        goto on_ack;
    }
    ndkRet = NDK_FsSeek(fd, 0, 0);
    if (ndkRet != NDK_OK) LOGE_NDK("NDK_FsSeek", ndkRet, NULL, 0);

/*
  这10个字节包括，每条记录的长度，检索字段1和检索字段2分别的
  偏移量和长度

*/
    nRet = NDK_FsWrite(fd, (char *) pbuf + offset, 10);
    if (nRet != 10) {
        LOGE_NDK("NDK_FsWrite", nRet, pbuf, buf_len)
        //Udebug.DEBUG_Levelone("write err\r\n");
        memcpy(ret_code, CMD_ERR_OTHER, 2);
    }
/* ====================== 应答 ===================*/
    on_ack:
    ndkRet = NDK_FsClose(fd);
    if (ndkRet != NDK_OK) LOGE_NDK("NDK_FsClose", ndkRet, pbuf, buf_len);
    responseCmd(pOut, 0, outLen, ret_code);
    return ret;
}


/**********************************************************************************************
** 函数原型：文件类
** 功能描述：获取文件长度
** 输入参数：
** 输出参数：无
** 返回值：  无
** 调用关系：
**********************************************************************************************/
int File_GetRecordNum(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int nRet = 0, offset = 0, extoffset = 0;
    unsigned int unLen = 0, unFileNameLen = 0;
    char ret_code[2];
    char szFileName[64] = "/appfs/";
    offset = MPOS_VARIABLE_OFFSET;
    memcpy(ret_code, CMD_OK, 2);

    unLen = buf_len;
    unFileNameLen = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);
    offset += 2;
    memcpy(szFileName + 7, pbuf + offset, unFileNameLen);
    szFileName[unFileNameLen + 7] = '\0';
    offset += unFileNameLen;

    extoffset = RESPOND_DATA_OFFSET;

    nRet = ME_RecordGetNum(szFileName, &unFileNameLen); // 获取记录个数
    if (nRet != 0) {
        memcpy(ret_code, CMD_ERR_OTHER, 2);
        responseCmd(pOut, 0, outLen, ret_code);
        return 6;
    } else {
        nRet = NDK_IntToC4(pOut + extoffset, unFileNameLen);
        if (nRet != NDK_OK) LOGE_NDK("NDK_IntToC4", nRet, NULL, 0);

    }
    responseCmd(pOut, 4, outLen, ret_code);
    return 0;
}

/**********************************************************************************************
** 函数原型：文件类
** 功能描述：写文件
** 输入参数：
** 输出参数：无
** 返回值：  无
** 调用关系：
**********************************************************************************************/
int File_WriteRecord(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int ret = 0;
    unsigned int unLen = 0, unNum = 0;
    int nRet = 0, offset = 0, extoffset = 0;
    int nFileNameLen = 0, nRecordLen = 0, nWriteLen = 0;
    char ret_code[2];
    int i = 0;
    // puchar pRecord = NULL;
    uchar pRecord[4096] = {0};
    uchar uszFileAttr[20] = {0};
    char szFileName[64] = "/appfs/";

/* ====================== 处理 ===================*/
    for (i = 0; i < 300; i++) {
        filelock(0);
        if (busy == 0) {
            busy = 1;
            filelock(1);
            break;
        }
        filelock(1);
        usleep(10000);
    }
    if (i >= 300) {
        LOGD_FMT("File_WriteRecord timeout");
        *outLen = 0;
        responseCmd(pOut, offset, outLen, CMD_ERR_TIMEOUT);
        return 7;
    }

    offset = MPOS_VARIABLE_OFFSET;
    memcpy(ret_code, CMD_OK, 2);

    unLen = buf_len;
    nFileNameLen = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);
    offset += 2;
    memcpy(szFileName + 7, pbuf + offset, nFileNameLen);
    szFileName[nFileNameLen + 7] = '\0';
    offset += nFileNameLen;
    nWriteLen = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);
    offset += 2;
    //pRecord = pbuf + offset;
    memcpy(pRecord, pbuf + offset - 2, nWriteLen + 2);
    extoffset = RESPOND_DATA_OFFSET;

    //nRet = ME_RecordGetNum(szFileName,&unNum);		//判断文件是否存在，是否合法.如果记录数为0需重新执行初始化
    //Udebug.DEBUG_Levelone("\r\nnret %x unNum %x",nRet,unNum);
    //if((nRet == 0)&&(unNum >= 0))
    {
        unLen = 0;
        nRet = NDK_FsFileSize(szFileName, &unLen); // 先获取当前文件的大小，添加的加在后面
        LOGD_FMT("unLen=%d", unLen);
        if ((nRet != 0) || (unLen < 10)) // 至少有10个字节，因为初始化写进了10个字节
        {
            Udebug.ERROR_MSG_LOG("%s %d NDK_FsFileSize:nRet:%d\n", __FUNCTION__, __LINE__, nRet);
            Udebug.ERROR_MSG_LOG_String(pbuf, buf_len);
            ret = 6;
            pOut[extoffset] = 0x01;
            memcpy(ret_code, CMD_ERR_OTHER, 2);
            goto on_ack;
        }
        //	Udebug.DEBUG_Levelone("\r\nfilesize 0x%02x,num 0x%02x",unLen,unNum);
        if (ME_FsRead(szFileName, uszFileAttr, 0, 10) != 0) // 先读取初始化时，每条记录的长度，作为偏移
        {
            Udebug.ERROR_MSG_LOG("%s %d:nRet:%d\n", __FUNCTION__, __LINE__, nRet);
            Udebug.ERROR_MSG_LOG_String(pbuf, buf_len);
            ret = 6;
            pOut[extoffset] = 0x01;
            memcpy(ret_code, CMD_ERR_OTHER, 2);
            goto on_ack;
        }
        int ndkRet = NDK_C2ToInt((uint *) &nRecordLen, uszFileAttr);
        if (ndkRet != NDK_OK) {
            LOGE_NDK("NDK_C2ToInt", ndkRet, NULL, 0);
        }
        if (nWriteLen > nRecordLen) {
            ret = 6;
            pOut[extoffset] = 0x02;
            memcpy(ret_code, CMD_ERR_OTHER, 2);
            goto on_ack;
        }
        nRet = ME_FsWrite(szFileName, pRecord, unLen, nRecordLen + 2);
        if (nRet != 0) {
            ret = 6;
            pOut[extoffset] = 0x01;
            memcpy(ret_code, CMD_ERR_OTHER, 2);
            goto on_ack;
        }
        pOut[extoffset] = 0x00;

    }
    on_ack:
    responseCmd(pOut, 1, outLen, ret_code);
    filelock(0);
    busy = 0;
    filelock(1);
    return ret;
}


/**********************************************************************************************
** 函数原型：文件类
** 功能描述：获取存储记录
** 输入参数：
** 输出参数：无
** 返回值：  无
** 调用关系：
**********************************************************************************************/
int File_GetRecord(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int ret = 0, ndkRet = -1;
    unsigned int unLen = 0;
    int nRet = 0, offset = 0;
    int nFileNameLen = 0;
    int nDataOffset = 0;
    int nRecordIndex = 0, nRecordLen = 0;
    int nField1Len = 0, nField2Len = 0;
    puchar pField1 = NULL, pField2 = NULL, pOutput = NULL;
    char ret_code[2];
    char szFileName[64] = "/appfs/";        //底层最大长度24?


    offset = MPOS_VARIABLE_OFFSET;
    memcpy(ret_code, CMD_OK, 2);

    unLen = buf_len;
    pOutput = pOut + RESPOND_DATA_OFFSET;
    nFileNameLen = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);
    offset += 2;
    memcpy(szFileName + 7, pbuf + offset, nFileNameLen);
    szFileName[nFileNameLen + 7] = '\0';
    offset += nFileNameLen;

    ndkRet = NDK_C4ToInt((uint *) &nRecordIndex, pbuf + offset); // 记录号
    if (ndkRet != NDK_OK) {
        LOGE_NDK("NDK_C4ToInt", ndkRet, pbuf, buf_len);
    }
    offset += 4;
    nField1Len = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16); // 检索字段1
    offset += 2;
    pField1 = pbuf + offset;
    offset += nField1Len;
    nField2Len = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);  //  检索字段2
    offset += 2;
    pField2 = pbuf + offset;

    nRet = ME_RecordGetNum(szFileName, &unLen);        //判断文件是否存在，是否合法
    if ((nRet == 0) && (unLen >= 0)) { ;
    } else {
        ret = 6;
        memcpy(ret_code, CMD_ERR_OTHER, 2);
        goto on_ack;
    }
    nDataOffset = 0;
    if (nRecordIndex == 0) // 如果记录号为0，去查收实际的记录号
    {
        nDataOffset = ME_RecordSearch(szFileName, nField1Len, pField1, nField2Len, pField2);
        if (nDataOffset < 0) {
            memcpy(pOutput, "\x00\x00", 2);
            responseCmd(pOut, 2, outLen, ret_code);
            return 0;
        }
    } else {
        nRet = ME_RecordGetOffset(szFileName, nRecordIndex, &nDataOffset);
        if ((nRet < 0) || (nDataOffset < 0)) {
            memcpy(pOutput, "\x00\x00", 2);
            responseCmd(pOut, 2, outLen, ret_code);
            return 0;
        }

    }

    ME_FsRead(szFileName, pOutput, 0, 10);
    ndkRet = NDK_C2ToInt((uint *) &nRecordLen, pOutput); // 得到每条记录的长度
    if (ndkRet != NDK_OK) {
        LOGE_NDK("NDK_C2ToInt", ndkRet, NULL, 0);
    }
    nRet = ME_FsRead(szFileName, pOutput, nDataOffset, nRecordLen + 2);//须加2两字节记录长度
    if (nRet != 0) {
        ret = 6;
        memcpy(ret_code, CMD_ERR_OTHER, 2);
        goto on_ack;
    }
    NDK_BcdToInt(pOutput, &nRecordLen);

    responseCmd(pOut, nRecordLen + 2, outLen, ret_code);
    return 0;
    on_ack:
    responseCmd(pOut, 0, outLen, ret_code);
    return ret;
}


/**********************************************************************************************
** 函数原型：文件类
** 功能描述：更新记录
** 输入参数：
** 输出参数：无
** 返回值：  无
** 调用关系：
**********************************************************************************************/
int File_ModifyRecord(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int ret = 0, ndkRet = -1;
    unsigned int unLen = 0;
    int nRet = 0, offset = 0;
    int nFileNameLen = 0;
    int nDataOffset = 0;
    int nRecordIndex = 0, nRecordLen = 0;
    int nField1Len = 0, nField2Len = 0, nWriteLen = 0;
    puchar pField1 = NULL, pField2 = NULL, pWriteRecord = NULL, pOutput = NULL;
    char ret_code[2];
    char szFileName[64] = "/appfs/";        //底层最大长度24?

    offset = MPOS_VARIABLE_OFFSET;
    memcpy(ret_code, CMD_OK, 2);

    unLen = buf_len;
    pOutput = pOut + RESPOND_DATA_OFFSET;
    nFileNameLen = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);
    offset += 2;
    memcpy(szFileName + 7, pbuf + offset, nFileNameLen);
    szFileName[nFileNameLen + 7] = '\0';
    offset += nFileNameLen;
    nRecordIndex = nlMpos_Command.mpos_getvar(pbuf + offset, _VAR_BIT32);
    nRecordIndex = nlMpos_Command.mpos_endian_swab32(nRecordIndex);
    offset += 4;
    nField1Len = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);
    offset += 2;
    pField1 = pbuf + offset;
    offset += nField1Len;
    nField2Len = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);
    offset += 2;
    pField2 = pbuf + offset;
    offset += nField2Len;
    nWriteLen = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);
    offset += 2;
    pWriteRecord = pbuf + offset;
    offset += nWriteLen;

    if (nRecordIndex == 0) {
        nDataOffset = ME_RecordSearch(szFileName, nField1Len, pField1, nField2Len, pField2);
        if (nDataOffset < 0) {
            ret = 6;
            pOutput[0] = 1;
            memcpy(ret_code, CMD_ERR_OTHER, 2);
            goto on_ack;
        }
    } else {
        nRet = ME_RecordGetOffset(szFileName, nRecordIndex, &nDataOffset);
        if ((nRet < 0) || (nDataOffset < 0)) {
            pOutput[0] = 1;
            memcpy(ret_code, CMD_ERR_OTHER, 2);
            goto on_ack;
        }
    }

    ME_FsRead(szFileName, pOutput, 0, 10);
    ndkRet = NDK_C2ToInt((uint *) &nRecordLen, pOutput);
    if (ndkRet != NDK_OK) {
        LOGE_NDK("NDK_C2ToInt", ndkRet, NULL, 0);
    }
    if (nWriteLen > nRecordLen) {
        ret = 6;
        pOutput[0] = 2;
        memcpy(ret_code, CMD_ERR_OTHER, 2);
        goto on_ack;
    }

    nRet = ME_FsWrite(szFileName, pWriteRecord - 2, nDataOffset, nWriteLen + 2);
    if (nRet != 0) {
        ret = 6;
        pOutput[0] = 1;
        memcpy(ret_code, CMD_ERR_OTHER, 2);
        goto on_ack;
    }
    pOutput[0] = 0;
    on_ack:
    responseCmd(pOut, 1, outLen, ret_code);
    return ret;
}


/**********************************************************************************************
** 函数原型：文件类
** 功能描述：写文件
** 输入参数：
** 输出参数：无
** 返回值：  无
** 调用关系：
**********************************************************************************************/
int File_WriteFile(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int fd, offset;
    int nRet, nFileNameLen, nWriteLen;
    char ret_code[2];
    unsigned char *pOutput = NULL;
    char szFileName[64] = "/appfs/";
    unsigned int nFileOffset = 0, nFileSize = 0;

    fd = 0;
    offset = MPOS_VARIABLE_OFFSET;
    pOutput = pOut + RESPOND_DATA_OFFSET;
    memcpy(ret_code, CMD_OK, 2);
    memcpy(pOutput, CMD_OK, 2);


    nFileNameLen = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);

    if (nFileNameLen > 12) {
        memcpy(pOutput, "05", 2);
        goto on_ack;
    }
    offset += 2;

    memcpy(szFileName + 7, pbuf + offset, nFileNameLen); // 文件名
    szFileName[nFileNameLen + 7] = '\0';

    offset += nFileNameLen;
    fd = NDK_FsOpen(szFileName, "w");
    if (fd < 0) {
        LOGE_NDK("NDK_FsOpen", fd, pbuf, buf_len);
        memcpy(pOutput, "02", 2);
        goto on_ack;
    }

    nFileOffset = nlMpos_Command.mpos_getvar(pbuf + offset, _VAR_BIT32);
    nFileOffset = nlMpos_Command.mpos_endian_swab32(nFileOffset);


    offset += 4;
    nWriteLen = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);
    offset += 2;

    if (NDK_FsFileSize(szFileName, &nFileSize) == NDK_OK) {
        if (nFileOffset > nFileSize) {
            memcpy(pOutput, "03", 2);
            goto on_ack;
        }
    }

    if ((nFileOffset + nWriteLen > 256 * 1024) ||
        ((nRet = NDK_FsSeek(fd, nFileOffset, SEEK_SET)) != NDK_OK)) {
        LOGE_NDK("NDK_FsSeek", nRet, NULL, 0);
        memcpy(pOutput, "03", 2);
        goto on_ack;
    }
    nRet = NDK_FsWrite(fd, (char *) pbuf + offset, nWriteLen);
    if (nRet != nWriteLen) {
        LOGE_NDK("NDK_FsWrite", nRet, pbuf, buf_len);
        memcpy(pOutput, "04", 2);
    }
    on_ack:
    nRet = NDK_FsClose(fd);
    if (nRet != NDK_OK) LOGE_NDK("NDK_FsClose", nRet, pbuf, buf_len);
    responseCmd(pOut, 2, outLen, ret_code);
    return 0;
}
/**********************************************************************************************
** 函数原型：文件类
** 功能描述：读文件
** 输入参数：
** 输出参数：无
** 返回值：  无
** 调用关系：
**********************************************************************************************/
int File_ReadFile(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int fd, offset, ret = -1;
    int nRet, nFileNameLen, nReadLen;
    char ret_code[2];
    char pOutput[2];
    char szFileName[64] = "/appfs/";
    unsigned long nFileOffset = 0, nFileSize = 0;
    int iLen, extoffset;

    fd = 0;
    offset = MPOS_VARIABLE_OFFSET;
    memcpy(ret_code, CMD_OK, 2);
    memcpy(pOutput, CMD_OK, 2);

    iLen = buf_len;
    extoffset = RESPOND_DATA_OFFSET;

    nFileNameLen = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);

    if (nFileNameLen > 12) {
        memcpy(pOutput, "05", 2);
        goto on_err_ack;
    }
    offset += 2;

    memcpy(szFileName + 7, pbuf + offset, nFileNameLen); // 文件名
    szFileName[nFileNameLen + 7] = '\0';
    offset += nFileNameLen;

    nFileOffset = nlMpos_Command.mpos_getvar(pbuf + offset, _VAR_BIT32);
    nFileOffset = nlMpos_Command.mpos_endian_swab32(nFileOffset);

    offset += 4;

    if (NDK_FsFileSize(szFileName, (uint *) &nFileSize) == NDK_OK) {
        if (nFileOffset >= nFileSize) {
            memcpy(pOutput, "06", 2);
            goto on_err_ack1;
        }
    }

    fd = NDK_FsOpen(szFileName, "r");
    if (fd < 0) {
        LOGE_NDK("NDK_FsOpen", fd, pbuf, buf_len);
        memcpy(pOutput, "02", 2);
        goto on_err_ack;
    }


    nReadLen = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);
    offset += 2;

    if ((nReadLen > 4000) || ((ret = NDK_FsSeek(fd, nFileOffset, SEEK_SET)) != NDK_OK)) {
        LOGE_NDK("NDK_FsSeek", ret, NULL, 0);
        memcpy(pOutput, "07", 2);
        goto on_err_ack;
    }

    nRet = NDK_FsRead(fd, (char *) pOut + extoffset + 8, nReadLen);
    if (nRet >= 0) {
        offset = 0;
        memcpy(pOut + extoffset, pOutput, 2);
        offset += 2;
        nFileSize = nlMpos_Command.mpos_endian_swab32(nFileSize);
        nlMpos_Command.mpos_setvar(pOut + extoffset + offset, (uint) nFileSize, _VAR_BIT32);
        offset += 4;
        nlMpos_Command.mpos_writelen(pOut + extoffset + offset, nRet, _VAR_BIT16);
        offset += 2;
        offset += nRet;
    } else {
        LOGE_NDK("NDK_FsRead", nRet, pbuf, buf_len);
        memcpy(pOutput, "07", 2);
        goto on_err_ack;
    }

    ret = NDK_FsClose(fd);
    if (ret != NDK_OK) LOGE_NDK("NDK_FsClose", ret, pbuf, buf_len);
    responseCmd(pOut, offset, outLen, ret_code);
    return 0;

    on_err_ack:
    ret = NDK_FsClose(fd);
    if (ret != NDK_OK) LOGE_NDK("NDK_FsClose", ret, pbuf, buf_len);
    on_err_ack1:
    offset = 0;
    memcpy(pOut + extoffset + offset, pOutput, 2);
    offset += 2;
    nFileSize = nlMpos_Command.mpos_endian_swab32(nFileSize);
    nlMpos_Command.mpos_setvar(pOut + extoffset + offset, (uint) nFileSize, _VAR_BIT32);
    offset += 4;
    memset(pOut + extoffset + offset, 0, 2);
    offset += 2;
    responseCmd(pOut, offset, outLen, ret_code);
    return 0;
}
/**********************************************************************************************
** 函数原型：文件类
** 功能描述：删除文件
** 输入参数：
** 输出参数：无
** 返回值：  无
** 调用关系：
**********************************************************************************************/
int File_DeleteFile(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int fd, offset;
    int nRet, nFileNameLen, nReadLen;
    char ret_code[2];
    char pOutput[2];
    char szFileName[64] = "/appfs/";
    int iLen, extoffset;

    fd = 0;
    offset = MPOS_VARIABLE_OFFSET;
    memcpy(ret_code, CMD_OK, 2);
    memcpy(pOutput, CMD_OK, 2);

    iLen = buf_len;
    extoffset = RESPOND_DATA_OFFSET;

    nFileNameLen = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);

    if (nFileNameLen > 12) {
        memcpy(pOutput, "05", 2);
        goto on_err_ack;
    }
    offset += 2;

    memcpy(szFileName + 7, pbuf + offset, nFileNameLen); // 文件名
    szFileName[nFileNameLen + 7] = '\0';

    if ((nRet = NDK_FsExist(szFileName)) != NDK_OK) {
        Udebug.ERROR_MSG_LOG("%s %d NDK_FsExist:nRet:%d\n", __FUNCTION__, __LINE__, nRet);
        Udebug.ERROR_MSG_LOG_String(pbuf, buf_len);
        memcpy(pOutput, "02", 2);
        goto on_err_ack;
    }

    if ((nRet = NDK_FsDel(szFileName)) != NDK_OK) {
        LOGE_NDK("NDK_FsDel", nRet, pbuf, buf_len);
        memcpy(pOutput, "08", 2);
        goto on_err_ack;
    } else {
        offset = 0;
        memcpy(pOut + extoffset, pOutput, 2);
        offset += 2;
    }

    responseCmd(pOut, offset, outLen, ret_code);
    return 0;

    on_err_ack:
    offset = 0;
    memcpy(pOut + extoffset + offset, pOutput, 2);
    offset += 2;
    responseCmd(pOut, offset, outLen, ret_code);
    return 0;
}

