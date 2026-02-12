#include <string.h>
#include <log.h>
#include "ndk.h"
#include "unistd.h"
#include "tlv.h"

#define PARM_SIZE    200
uchar uszTlvBuf[PARM_SIZE * 7];

#define IS_EMV_TAG(x)  (x <= 0xffff)//当小于2个字节时，为EMV标签//(!(IS_CPOS_RPIVATE_TAG(x)))


extern int PubAscToHex(const unsigned char *pszAsciiBuf, int nLen, char cType, unsigned char *pszBcdBuf);

/**
* @brief 往oTlvBuf中加入一个Tag-Len-Value
* @param [in] unTag     T
* @param [in] nInLen     L
* @param [in] pszInValue    V
* @param [out] pszOutTlvBuf   
* @param [out] pnOutTlvBufLen   
* @return 
* @li APP_SUCC 
* @li APP_FAIL
* @author 
* @date
*/
int TlvAdd(const unsigned int unTag, const int nInLen, const uchar *pszInValue, uchar *pszOutTlvBuf, int *pnOutTlvBufLen) {
    int i, nTagLen, nLen, nLenLen;
    char sTag[8];
    char sLenBuf[8];

    if (unTag == 0xD01) {
        Udebug.DEBUG_Levelone("pszInValue-->");
        Udebug.DEBUG_string_Levelone(pszInValue, nInLen);
    }

    sprintf(sTag, "%X", unTag);
    nTagLen = strlen(sTag);

    Udebug.DEBUG_Levelone("sTag-->%s  nTagLen=%d", sTag, nTagLen);
    PubAscToHex((unsigned char *) sTag, nTagLen, 0,
                (unsigned char *) (pszOutTlvBuf + *pnOutTlvBufLen));
    nTagLen >>= 1;
    *pnOutTlvBufLen += nTagLen;

    if (nInLen > 127) {
        nLen = nInLen;
        nLenLen = nInLen / 256 + 1;    //跟随n个字节
        sLenBuf[0] = 128 | nLenLen;    //zj &改成|
        for (i = 0; i < nLenLen; i++) {
            sLenBuf[nLenLen - i] = nLen % 256;
            nLen /= 256;
        }
    } else {
        sLenBuf[0] = nInLen;
        nLenLen = 0;
    }
    memcpy(pszOutTlvBuf + *pnOutTlvBufLen, sLenBuf, nLenLen + 1);
    *pnOutTlvBufLen += nLenLen + 1;

    memcpy(pszOutTlvBuf + *pnOutTlvBufLen, pszInValue, nInLen);
    *pnOutTlvBufLen += nInLen;

    return 0;
}

/******************************************************************************
* 函数名称： GetTagByBuf
* 函数介绍： 获取Tag值
* 函数描述： 
* 入口参数： psBuf    数据缓冲区
* 出口参数： pnTag    Tag的int类型值
            pnTagLen Tag的长度
* 返回值  ：APP_SUCC 成功  APP_FAIL 失败
* 修改者  ： 刘罡
* 修改日期： 2011-09-20
*******************************************************************************/
int GetTagByBuf(const char *psBuf, int *pnTag, int *pnTagLen) {
    int i, nTagLen, nTag;

    nTagLen = 1;
    if (0x1F == ((psBuf[nTagLen - 1]) & (0x1F))) {
        nTagLen++;
        while (0x80 == ((psBuf[nTagLen - 1]) & (0x80))) {
            nTagLen++;
        }
    }

    if (pnTagLen != NULL) {
        *pnTagLen = nTagLen;
    }
    if (pnTag != NULL) {
        nTag = (unsigned char) psBuf[0];
        for (i = 1; i < nTagLen; i++) {
            nTag = nTag << 8 | ((unsigned char) psBuf[i]);
        }
        *pnTag = nTag;
    }
    return 0;
}

/**
* @fn Tlv_Tag2List
* @brief 解析Tag串，将tag串解析成tag list
* @param in psTag Tag串指针
* @param in nLen Tag串长度
* @param out nTagList 得到的tag list数组
* @param in nSize  tag list数组的大小
* @return
* @li 得到的nTaglist大小
* @li APP_FAIL
*/
int Tlv_Tag2List(unsigned char *psTag, const unsigned int nLen, unsigned int nTagList[], int nSize) {
    unsigned char *p = psTag;
    int nCount = nLen;
    int i = 0;
    int nTagLen = 0;
    if (NULL == nTagList || nSize <= 0) {
        return -1;
    }
    memset(nTagList, 0, sizeof(int) * nSize);

    while (*p != 0x00 && nCount > 0 && i < nSize) {
        GetTagByBuf((char *) p, (int *) &nTagList[i], &nTagLen);
        p += nTagLen;
        nCount -= nTagLen;
        i++;
    }
    return i;
}


/* Func:       解析TLV字符串
 * Para:       parent  父结点的序数
 * *pobj       存放解析的TLV结构体
 * objspace    存放解析的TLV结构体容量上限
 * deflag      解析标识
 *             SINGLE_TLVOBJ        a constructed object
 *             STRING_TLVOBJ        a tlv object string not in a constructed
 *             DECODE_LEVEL1        只解析出子结点TLV字串(不继续解析子结点的后继结点)
 * Return:     成功        0
 *             失败        <0 (Notice:如果是primitive标签,其childnum =-1,而contruct标签没有子结点,childnum =0)
 */
static int decode_tlv_recur(int parent, tlv_t *pobj, int objspace, int deflag) {

    int itrack = 0, i, tmplen;
    int tlvpos = parent + 1;
    unsigned int tmptag;
    unsigned char *ptlvstr = pobj[parent].pvalue;
    int tlvlen = pobj[parent].valuelen;

    while (itrack < tlvlen) {
        /* omit the 00H and FFH between objects */
        if ((*(ptlvstr + itrack) == 0x00) || (*(ptlvstr + itrack) == 0xFF)) {
            itrack++;
            continue;
        }

        if (tlvpos == objspace) {
            return -1;
        }
        if (is_primitive(*(ptlvstr + itrack))) {
            pobj[tlvpos].childnum = -1; /* without any children */
        } else {
            pobj[tlvpos].childnum = 0;/* may have, but now zero */
        }
        /* tag name coding */
        tmptag = 0;
        pobj[tlvpos]._tagptr = ptlvstr + itrack;
        if (has_subsequent(ptlvstr[itrack])) {
            i = 0;
            do {
                tmptag = (tmptag | ptlvstr[itrack++]) << 8;
                if (++i == 4 || itrack == tlvlen) {
                    return -1;
                }
            } while (another_byte_follow(ptlvstr[itrack]));
        }
        pobj[tlvpos].tagname = tmptag | ptlvstr[itrack++];
        if (itrack == tlvlen) {
            return -1;
        }
        /* length field */
        pobj[tlvpos]._lenptr = ptlvstr + itrack;
        tmplen = ptlvstr[itrack] & 0x7F;
        if (lenlen_exceed_one(ptlvstr[itrack++])) {
            if (tmplen > 4 || tmplen > tlvlen - itrack) {
                return -1;
            }
            i = tmplen;
            tmplen = 0;
            while (i > 0) {
                tmplen |= ptlvstr[itrack++] << ((--i) << 3);
            }
        }
        if (tmplen > tlvlen - itrack) {
            return -1;
        }

        pobj[tlvpos].valuelen = tmplen;
        pobj[tlvpos].parent = parent;
        pobj[tlvpos].pvalue = ptlvstr + itrack;
        itrack += tmplen;
        pobj[tlvpos]._len = ptlvstr + itrack - pobj[tlvpos]._tagptr;

        if (pobj[tlvpos].childnum == 0 && !(deflag & DECODE_LEVEL1)) {
            if ((tmplen = decode_tlv_recur(tlvpos, pobj, objspace, deflag)) < 0) {
                return tmplen;
            }
            tlvpos += pobj[tlvpos].childnum;
        }
        tlvpos++;
    }

    pobj[parent].childnum = tlvpos - parent - 1;
    return 0;

}

/*  unc:     解析TLV字符串到tlv_t  *pobj结构体.(pobj[0]记录TLV的相关信息不存放解析出的TLV对象,实际存放从pobj[1]开始)
             pobj[0].pvalue             保存TLV字符串指针
             pobj[0].valuelen           保存TLV字符串长度
             pobj[0].childnum           保存此次共解析多少个TLV结点
  * Para:    *ptlvstr        传入TLV字符串的首指针
  *          tlvlen          TLV字符串长度
  *          *pobj           存放解析好的TLV数据结构体数组指针
  *          objspace        存放的TLV的pobj的最大空间限制
  *          deflag          SINGLE_TLVOBJ        TLV字符串为单个结构数据对象
  *                          STRING_TLVOBJ        TLV对象字符串集(tag+len+value+tag+len+value...)
                             DECODE_LEVEL1        只解析出子结点TLV字串(如果子结点为结构数据对象将不继续解析其子结点)
  * Return:  解析成功        0
  *          解析失败        <0
  */
int parse_tlv(unsigned char *ptlvstr, int tlvlen, tlv_t *pobj, int objspace, int deflag) {
    int ret;

    if (ptlvstr == NULL || pobj == NULL || objspace == 0) {
        return -1;
    }

    if (*ptlvstr == 0x00) {
        /* wrong starting */
        return -1;
    }

    memset(pobj, 0x00, sizeof(tlv_t) * objspace);
    pobj[0].pvalue = ptlvstr;        /*保存TLV字符串指针*/
    pobj[0].valuelen = tlvlen;        /*保存TLV字符串长度*/

    /*调用实际解析函数*/
    if ((ret = decode_tlv_recur(0, pobj, objspace, deflag)) < 0) {
        return ret;
    }

    if (deflag & SINGLE_TLVOBJ) {
        if (pobj[1]._len != tlvlen) {
            return -1;
        }
    }
    return 0;
}

/*  Func:   在tlv_t pobj结构体中获取tagname的TLV数据.
 *  Para:   parent       从这个父结点开始搜索
 *          tagname      要查找的标签名
 *          *pobj        要搜索的tlv_t结构体
 *          level        搜索的层度
                         SEARCH_ONLY_SON        只在自己子结点查找
                         SEARCH_ALL_DESC        在所有后续结点查找
 *    Return:  查找成功        返回子结点序号
               查找失败        0
 */
int fetch_tlv(int parent, unsigned int tagname, tlv_t *pobj, int level) {
    int i = (parent < 0 ? 0 : parent) + 1;
    int end;

    /* if no objects or parent referred to a primitive element */
    if (pobj == NULL || pobj[i - 1].childnum < 0) {
        return 0;
    }

    end = pobj[i - 1].childnum + i;

    while (i < end) {
        if (pobj[i].tagname == tagname)
            return i;
        if (level == SEARCH_ONLY_SON) {
            /*跳过子结点的子结点*/
            if (pobj[i].childnum > 0) {
                i += pobj[i].childnum;
            }
        }
        i++;
    }
    return 0;
}

int find_dup_tlv(int pos, tlv_t *pobjects) {
    int i;
    int end;

    if (pos <= 0 || pobjects == NULL) {
        return 0;
    }

    end = pobjects[0].childnum;
    i = pos + 1;
    while (i <= end) {
        if (pobjects[i].tagname == pobjects[pos].tagname)
            return i;
        i++;
    }
    return 0;
}
/************************************************************************************
 *    Func:    解析TLV标签
 *    Para:    *ptlvstr		传入TLV标签字符串的首指针
 *             tlvlen	    TLV标签字符串长度
 *             *tagname		存放解析好的TLV标签指针
 *             objspace		存放的TLV的pobj的最大空间限制
 *    Return:  				成功	返回解析的TLV标签个数
 *							失败	<0
 ************************************************************************************/
int decode_tag(unsigned char *ptlvstr, int tlvlen, unsigned int *tagname, int objspace) {
    int i;
    int itrack = 0;
    int tagnum = 0;
    unsigned int tmptag;
    if (tlvlen > 1024)
        return -1;
    while (itrack < tlvlen) {

        /* omit the 00H and FFH between objects */
        if ((*(ptlvstr + itrack) == 0x00) || (*(ptlvstr + itrack) == 0xFF)) {
            itrack++;
            continue;
        }

        if (tagnum == objspace) {
            return -1;
        }

        /* tag name coding */
        tmptag = 0;
        if (has_subsequent(ptlvstr[itrack])) {
            i = 0;
            do {
                tmptag = (tmptag | ptlvstr[itrack++]) << 8;
                if (++i == 4 || itrack == tlvlen) {
                    return -1;
                }
            } while (another_byte_follow(ptlvstr[itrack]));
        }
        tagname[tagnum] = tmptag | ptlvstr[itrack++];

        tagnum++;
    }

    return tagnum;
}


/**********************************************************************
* 功能描述: 清空tlv
*
* 输入:pobj	-	tlv标签指针
*		pszName	-	
*
***********************************************************************/
int mpos_clearonetlv(const char *pszName, unsigned int tagname) {
    int fd;
    int i;
    int iRet;
    int nTlvNum;
    uint tag;
    uchar buf[TERMINAL_SIZE];

    /* 解析传入的tagname是否合法，不合法则直接返回*/
    fd = 0;
    iRet = NDK_IntToC4(buf, tagname);
    if (iRet != NDK_OK) LOGE_NDK("NDK_IntToC4", iRet, NULL, 0);
    Udebug.DEBUG_Levelone("\r\ndecoding tag %08x", tagname);
    iRet = decode_tag(buf, 4, &tagname, 1);
    if (iRet != 1) {
        Udebug.DEBUG_Levelone("\r\nfail to decode tag");
        goto on_err_ack;
    }
    Udebug.DEBUG_Levelone("\r\ndecode tag succ,result %08x", tagname);

    /* 如果tlv文件不存在则先创建文件*/
    memset(buf, 0xff, TERMINAL_SIZE);
    if (NDK_FsExist(pszName) != NDK_OK) {
        Udebug.DEBUG_Levelone("\r\nfile %s no exist", pszName);
        fd = NDK_FsOpen(pszName, "w");
        if (fd < 0) {
            LOGE_NDK("NDK_FsOpen", fd, NULL, 0);
            goto on_err_ack;
        }
        iRet = NDK_FsWrite(fd, (char *) buf, TERMINAL_SIZE);
        if (iRet < 0) {
            LOGE_NDK("NDK_FsWrite", iRet, NULL, 0);
        }
        iRet = NDK_FsClose(fd);
        if (iRet != NDK_OK) LOGE_NDK("NDK_FsClose", iRet, NULL, 0);
    }

    /* 获取tlv文件内的tlv个数*/
    iRet = NDK_FsFileSize(pszName, &tag);
    nTlvNum = tag / TERMINAL_SIZE;
    if ((fd = NDK_FsOpen(pszName, "w")) < 0) {
        LOGE_NDK("NDK_FsOpen", fd, NULL, 0);
        goto on_err_ack;
    }

    /* 找到指定的tlv并清空该tlv*/
    for (i = 0; i < nTlvNum; i++) {
        if ((iRet = NDK_FsSeek(fd, TERMINAL_SIZE * i, SEEK_SET)) != NDK_OK) {
            LOGE_NDK("NDK_FsSeek", iRet, NULL, 0);
            goto on_err_ack;
        }
        if ((iRet = NDK_FsRead(fd, (char *) &tag, 4)) != 4) {
            LOGE_NDK("NDK_FsRead", iRet, NULL, 0);
            Udebug.ERROR_MSG_LOG("tag[%d]", tag);
            goto on_err_ack;
        }
        Udebug.DEBUG_Levelone("\r\nread tag %08x ,index %02x", tag, i);
        iRet = NDK_IntToC4(buf, tag);
        if (iRet != NDK_OK) LOGE_NDK("NDK_IntToC4", iRet, NULL, 0);
        iRet = decode_tag(buf, 4, &tag, 1);
        if ((tagname == tag) && (iRet == 1)) {
            Udebug.DEBUG_Levelone("\r\nsame tag found %08x ,index %02x", tagname, i);
            if ((iRet = NDK_FsSeek(fd, TERMINAL_SIZE * i, SEEK_SET)) != NDK_OK) {
                LOGE_NDK("NDK_FsSeek", iRet, NULL, 0);
                goto on_err_ack;
            }
            memset(buf, 0xff, TERMINAL_SIZE);
            iRet = NDK_FsWrite(fd, (char *) buf, TERMINAL_SIZE);
            if (iRet < 0) {
                LOGE_NDK("NDK_FsWrite", iRet, buf, TERMINAL_SIZE);
            }
            break;
        }
    }

    iRet = NDK_FsClose(fd);
    if (iRet != NDK_OK) LOGE_NDK("NDK_FsClose", iRet, NULL, 0);
    return 0;
    on_err_ack:
    iRet = NDK_FsClose(fd);
    if (iRet != NDK_OK) LOGE_NDK("NDK_FsClose", iRet, NULL, 0);
    return -1;
}

/**********************************************************************
* 功能描述: 往文件内写入TLV标签
*
* 输入:pobj	    -tlv标签指针
*	  pszName	-
*

***********************************************************************/
int mpos_writeonetlv(const char *pszName, tlv_t *pobj) {
    int fd;
    int i;
    int iRet;
    int nTlvNum;
    uint tag;
    uchar buf[TERMINAL_SIZE];

    /* 解析传入的tagname是否合法，不合法则直接返回*/
    fd = 0;
    iRet = NDK_IntToC4(buf, pobj->tagname);
    if (iRet != NDK_OK) LOGE_NDK("NDK_IntToC4", iRet, NULL, 0);
    iRet = decode_tag(buf, 4, &(pobj->tagname), 1);
    if (iRet != 1) {
        Udebug.DEBUG_Levelone("\r\nfail to decode tag");
        goto on_err_ack;
    }
    /* 如果tlv文件不存在则先创建文件*/
    memset(buf, 0xff, TERMINAL_SIZE);
    if (NDK_FsExist(pszName) != NDK_OK) {
        Udebug.DEBUG_Levelone("\r\nfile %s no exist", pszName);
        fd = NDK_FsOpen(pszName, "w");
        if (fd < 0) {
            LOGE_NDK("NDK_FsOpen", fd, NULL, 0);
            goto on_err_ack;
        }
        int nRet = NDK_FsWrite(fd, (char *) buf, TERMINAL_SIZE);
        if (nRet < 0) {
            LOGE_NDK("NDK_FsWrite", nRet, buf, TERMINAL_SIZE);
        }
        nRet = NDK_FsClose(fd);
        if (nRet != NDK_OK) LOGE_NDK("NDK_FsClose", nRet, NULL, 0);
    }

    /* 获取tlv文件内的tlv个数*/
    iRet = NDK_FsFileSize(pszName, &tag);
    if (iRet != NDK_OK)
        Udebug.ERROR_MSG_LOG("%s %d NDK_FsFileSize:iRet:%d\n", __FUNCTION__, __LINE__, iRet);
    nTlvNum = tag / TERMINAL_SIZE;
    if ((fd = NDK_FsOpen(pszName, "w")) < 0) {
        LOGE_NDK("NDK_FsOpen", fd, NULL, 0);
        goto on_err_ack;
    }

    for (i = 0; i < nTlvNum; i++) {               /* 寻找tlv文件中是否已经存在该标签*/
        if ((iRet = NDK_FsSeek(fd, TERMINAL_SIZE * i, SEEK_SET)) != NDK_OK) {
            LOGE_NDK("NDK_FsSeek", iRet, NULL, 0);
            goto on_err_ack;
        }
        if ((iRet = NDK_FsRead(fd, (char *) &tag, 4)) != 4) {
            LOGE_NDK("NDK_FsRead", iRet, NULL, 0);
            Udebug.ERROR_MSG_LOG("tag[%d]", tag);
            goto on_err_ack;
        }
        iRet = NDK_IntToC4(buf, tag);
        if (iRet != NDK_OK) LOGE_NDK("NDK_IntToC4", iRet, NULL, 0);

        iRet = decode_tag(buf, 4, &tag, 1);
        if ((pobj->tagname == tag) && (iRet == 1)) {        /* 找到匹配的tlv标签*/
            if ((iRet = NDK_FsSeek(fd, TERMINAL_SIZE * i, SEEK_SET)) != NDK_OK) {
                LOGE_NDK("NDK_FsSeek", iRet, NULL, 0);
                goto on_err_ack;
            }
            memcpy(buf, (void *) &pobj->tagname, 4);
            memcpy(&buf[4], (void *) &pobj->valuelen, 4);
            memcpy(&buf[8], (void *) pobj->pvalue, pobj->valuelen);
            iRet = NDK_FsWrite(fd, (char *) buf, pobj->valuelen + 8);
            if (iRet < 0) {
                LOGE_NDK("NDK_FsWrite", iRet, buf, pobj->valuelen + 8);
            }
            break;
        }
    }
    if (i >= nTlvNum)        /* tlv文件内不存在该标签，找一个空的地方存起来*/
    {
        for (i = 0; i < nTlvNum; i++) {
            if ((iRet = NDK_FsSeek(fd, TERMINAL_SIZE * i, SEEK_SET)) != NDK_OK) {
                LOGE_NDK("NDK_FsSeek", iRet, NULL, 0);
                goto on_err_ack;
            }
            if ((iRet = NDK_FsRead(fd, (char *) &tag, 4)) != 4) {
                LOGE_NDK("NDK_FsRead", iRet, NULL, 0);
                Udebug.ERROR_MSG_LOG("tag[%d]", tag);
                goto on_err_ack;
            }
            if (tag == 0xFFFFFFFF) {
                if ((iRet = NDK_FsSeek(fd, TERMINAL_SIZE * i, SEEK_SET)) != NDK_OK) {
                    LOGE_NDK("NDK_FsSeek", iRet, NULL, 0);
                    goto on_err_ack;
                }
                memcpy(buf, (void *) &pobj->tagname, 4);
                memcpy(&buf[4], (void *) &pobj->valuelen, 4);
                memcpy(&buf[8], (void *) pobj->pvalue, pobj->valuelen);
                iRet = NDK_FsWrite(fd, (char *) buf, pobj->valuelen + 8);
                if (iRet < 0) {
                    LOGE_NDK("NDK_FsWrite", iRet, buf, pobj->valuelen + 8);
                }
                break;
            }
        }
    }
    if ((i >= nTlvNum) && (i < TERMINAL_NUM))        /* 什么都没有，自己创建一个*/
    {
        if ((iRet = NDK_FsSeek(fd, TERMINAL_SIZE * i, SEEK_SET)) != NDK_OK) {

            LOGE_NDK("NDK_FsSeek", iRet, NULL, 0);
            goto on_err_ack;
        }
        memset(buf, 0xFF, TERMINAL_SIZE); // FF
        memcpy(buf, (void *) &pobj->tagname, 4);
        memcpy(&buf[4], (void *) &pobj->valuelen, 4);
        memcpy(&buf[8], (void *) pobj->pvalue, pobj->valuelen);
        iRet = NDK_FsWrite(fd, (char *) buf, TERMINAL_SIZE);
        if (iRet < 0) {
            LOGE_NDK("NDK_FsWrite", iRet, buf, TERMINAL_SIZE);
        }
        nTlvNum++;
    }
    iRet = NDK_FsClose(fd);
    if (iRet != NDK_OK) LOGE_NDK("NDK_FsClose", iRet, NULL, 0);
    return 0;
    on_err_ack:

    iRet = NDK_FsClose(fd);
    if (iRet != NDK_OK) LOGE_NDK("NDK_FsClose", iRet, NULL, 0);
    return -1;
}
/**********************************************************************
* 功能描述: 从文件中读取出指定的tlv
*
* 输入:pobj	    - 	tlv标签指针
*	   pszName	-	文件名
*
* 输出:pobj	-	tlv标签指针
***********************************************************************/
int mpos_readonetlv(const char *pszName, tlv_t *pobj) {
    int fd;
    int i;
    int iRet;
    int nTlvNum;
    uint tag;
    uint nSize;
    uchar buf[TERMINAL_SIZE];

    /* 解析传入的tagname是否合法，不合法则直接返回*/
    fd = 0;
    iRet = NDK_IntToC4(buf, pobj->tagname);
    if (iRet != NDK_OK) LOGE_NDK("NDK_IntToC4", iRet, NULL, 0);

    iRet = decode_tag(buf, 4, &(pobj->tagname), 1);
    if (iRet != 1) {
        Udebug.DEBUG_Levelone("\r\nfail to decode tag");
        goto on_err_ack;
    }

    /* 如果tlv文件不存在则先创建文件*/
    memset(buf, 0xff, TERMINAL_SIZE);
    fd = 0;
    if (NDK_FsExist(pszName) != NDK_OK) {
        goto on_err_ack;
    }

    /* 获取tlv文件内的tlv个数*/
    iRet = NDK_FsFileSize(pszName, &tag);
    nTlvNum = tag / TERMINAL_SIZE;
    if ((fd = NDK_FsOpen(pszName, "w")) < 0) {
        LOGE_NDK("NDK_FsOpen", fd, NULL, 0);
        goto on_err_ack;
    }


    for (i = 0; i < nTlvNum; i++) {  /* 寻找tlv文件中是否已经存在该标签*/
        if ((iRet = NDK_FsSeek(fd, TERMINAL_SIZE * i, SEEK_SET)) != NDK_OK) {
            LOGE_NDK("NDK_FsSeek", iRet, NULL, 0);
            goto on_err_ack;
        }
        if ((iRet = NDK_FsRead(fd, (char *) buf, 8)) != 8) {
            LOGE_NDK("NDK_FsRead", iRet, NULL, 0);
            goto on_err_ack;
        }
        memcpy((char *) &tag, buf, 4);
        memcpy((char *) &nSize, &buf[4], 4);
        iRet = NDK_IntToC4(buf, tag);
        if (iRet != NDK_OK) LOGE_NDK("NDK_IntToC4", iRet, NULL, 0);
        iRet = decode_tag(buf, 4, &tag, 1);

        if ((iRet == 1) && (tag == pobj->tagname)) {            /* 找到匹配的tlv标签*/
            if ((iRet = NDK_FsRead(fd, (char *) buf + 8, nSize)) != nSize) {
                LOGE_NDK("NDK_FsRead", iRet, NULL, 0);
                goto on_err_ack;
            } else {
                pobj->valuelen = nSize;
                memcpy(pobj->pvalue, &buf[8], nSize);
            }
            break;
        }
    }
    if (i >= nTlvNum) {
        goto on_err_ack;
    }
    iRet = NDK_FsClose(fd);
    if (iRet != NDK_OK) LOGE_NDK("NDK_FsClose", iRet, NULL, 0);
    return 0;
    on_err_ack:
    iRet = NDK_FsClose(fd);
    if (iRet != NDK_OK) LOGE_NDK("NDK_FsClose", iRet, NULL, 0);
    return -1;
}


/**********************************************************************
* 功能描述: 写入多个TLV标签到缓冲区(最多10个)
*
* 输入:pobj	-	tlv标签指针
*		num	-	要写入的标签个数(最多10个)
*
* 输出:pobj	-	tlv标签指针

***********************************************************************/
int mpos_writemacdata(tlv_t *pobj, int num) {
    int offset;
    int i, j;
    int iRet;
    uint tag;
    int nTlvNum;
    uchar buf[8];

    /* 获取tlv缓冲内的tlv个数*/
    offset = 0;
    nTlvNum = sizeof(uszTlvBuf) / PARM_SIZE;
    if (num > nTlvNum)
        goto on_err_ack;


    for (i = 0; i < num; i++) {
        iRet = NDK_IntToC4(buf, pobj[i].tagname);
        if (iRet != NDK_OK) LOGE_NDK("NDK_IntToC4", iRet, NULL, 0);
        iRet = decode_tag(buf, 4, &(pobj[i].tagname), 1);
        if (iRet != 1) {
            continue;
        }
        Udebug.DEBUG_Levelone("\r\ndecode tag succ,result %08x", pobj[i].tagname);

        for (j = 0; j < nTlvNum; j++) {        /* 寻找是否已经存在该标签*/
            offset = j * PARM_SIZE;
            memcpy((char *) &tag, &uszTlvBuf[offset], 4);
            Udebug.DEBUG_Levelone("\r\nread tag %08x ,index %02x", tag, j);
            iRet = NDK_IntToC4(buf, tag);
            if (iRet != NDK_OK) LOGE_NDK("NDK_IntToC4", iRet, NULL, 0);
            iRet = decode_tag(buf, 4, &tag, 1);
            if (pobj[i].tagname == tag) {
                Udebug.DEBUG_Levelone("\r\nsame tag found %08x ,index %02x", pobj[i].tagname, j);
                pobj[i].tagname = (pobj[i].tagname | 0xFF0000);
                memcpy(&uszTlvBuf[offset], (void *) &(pobj[i].tagname), 4);
                memcpy(&uszTlvBuf[offset + 4], (void *) &(pobj[i].valuelen), 4);
                memcpy(&uszTlvBuf[offset + 8], (void *) pobj[i].pvalue, pobj[i].valuelen);
                break;
            }
        }
        if (j >= nTlvNum)        /* 不存在该标签，找一个空的地方存起来*/
        {
            Udebug.DEBUG_Levelone("\r\n tag %08x no exists now,finding free tag", pobj[i].tagname);
            for (j = 0; j < nTlvNum; j++) {
                offset = j * PARM_SIZE;
                memcpy((char *) &tag, &uszTlvBuf[offset], 4);
                Udebug.DEBUG_Levelone("\r\nread tag %08x ,index %02x", tag, j);
                if (tag == 0xffffffff) {
                    Udebug.DEBUG_Levelone("\r\nfree tag found ,index %02x", j);
                    Udebug.DEBUG_string_Levelone(pobj[i].pvalue, pobj[i].valuelen);
                    memcpy(&uszTlvBuf[offset], (void *) &(pobj[i].tagname), 4);
                    memcpy(&uszTlvBuf[offset + 4], (void *) &(pobj[i].valuelen), 4);
                    memcpy(&uszTlvBuf[offset + 8], (void *) pobj[i].pvalue, pobj[i].valuelen);
                    break;
                }
            }
        }
    }
    return 0;
    on_err_ack:
    return -1;
}

/**********************************************************************
* 功能描述: 从缓冲区读取指定的tlv
*
* 输入:buf	-	信息内容
*		len	-	信息长度
*
* 输出:
***********************************************************************/
int mpos_readmacdata(tlv_t *pobj, puchar pdata) {
    int i;
    uint tag;
    int nTlvNum;
    uint nSize;
    int offset;
    int dataoffset;
    int nDatalen;
    int iRet;
    uchar buf[4];

    /* 解析传入的tagname是否合法，不合法则直接返回*/
    iRet = NDK_IntToC4(buf, pobj->tagname);
    if (iRet != NDK_OK) LOGE_NDK("NDK_IntToC4", iRet, NULL, 0);
    Udebug.DEBUG_Levelone("\r\ndecoding tag %08x", pobj->tagname);
    iRet = decode_tag(buf, 4, &(pobj->tagname), 1);
    if (iRet != 1) {
        return -1;
    }
    Udebug.DEBUG_Levelone("\r\ndecode tag succ,result %08x", pobj->tagname);

    nTlvNum = sizeof(uszTlvBuf) / PARM_SIZE;
    for (i = 0; i < nTlvNum; i++) {
        dataoffset = PARM_SIZE * i;
        memcpy((char *) &tag, &uszTlvBuf[dataoffset], 4);
        memcpy((char *) &nSize, &uszTlvBuf[dataoffset + 4], 4);
        Udebug.DEBUG_Levelone("\r\nread tag %08x ,index %02x", tag, i);

        iRet = NDK_IntToC4(buf, tag);
        if (iRet != NDK_OK) LOGE_NDK("NDK_IntToC4", iRet, NULL, 0);
        iRet = decode_tag(buf, 4, &tag, 1);

        if ((pobj->tagname == tag) && (iRet == 1)) {
            offset = (pobj->pvalue)[0] * 256 + (pobj->pvalue)[1];
            nDatalen = (pobj->pvalue)[2] * 256 + (pobj->pvalue)[3];
            if (nDatalen > nSize)
                nDatalen = nSize;
            memcpy(&(pdata[offset]), &uszTlvBuf[dataoffset + 8], nDatalen);
            Udebug.DEBUG_Levelone("\r\nsame tag found %08x ,index %02x", pobj->tagname, i);
            Udebug.DEBUG_string_Levelone((&(uszTlvBuf[dataoffset + 8])), nDatalen);
            memset(&uszTlvBuf[dataoffset], 0xff, PARM_SIZE);
        }
    }
    return 0;
}


int Tlv_Init() {
    int fd;
    int nLen, nWriteLen;
    if (NDK_FsExist(ME_TLV_FILE) != NDK_OK) {
        nLen = sizeof(uszTlvBuf) / TERMINAL_SIZE * TERMINAL_SIZE;
        memset(uszTlvBuf, 0xff, sizeof(uszTlvBuf));
        fd = NDK_FsOpen(ME_TLV_FILE, "w");
        if (fd < 0) LOGE_NDK("NDK_FsOpen", fd, NULL, 0);
        if (fd > 0) {
            nWriteLen = NDK_FsWrite(fd, (char *) uszTlvBuf, nLen);
            if (nWriteLen != nLen) {
                LOGE_NDK("NDK_FsWrite", nWriteLen, NULL, 0);
                int nRet = NDK_FsClose(fd);
                if (nRet != NDK_OK) LOGE_NDK("NDK_FsClose", nRet, NULL, 0);
                nRet = NDK_FsDel(ME_TLV_FILE);
                if (nRet != NDK_OK) LOGE_NDK("NDK_FsDel", nRet, NULL, 0);
                return -1;
            }
            int nRet = NDK_FsClose(fd);
            if (nRet != NDK_OK) LOGE_NDK("NDK_FsClose", nRet, NULL, 0);
        }
    }
    memset(uszTlvBuf, 0xff, sizeof(uszTlvBuf));
    return 0;
}
