/**
 * Author by wuhh, Date on 2019/3/31 0022.
 */
#include <string.h>
#include <jni.h>
#include "log.h"
#include "command.h"
#include "comm.h"
#include "ndk.h"
#include "file.h"

ME_TPEDCTL g_METPEDCtl;
ME31_CONFIG_T g_me31conf;

const ST_COMMAND_API nlMpos_Command = {
        .mpos_endian_swab16 = ME_Endian_Swab16,
        .mpos_endian_swab32 = ME_Endian_Swab32,
        .mpos_getvar = ME_GetVar,
        .mpos_setvar = ME_SetVar,

        .mpos_int2bcd = ME_Int2Bcd,
        .mpos_bcd2int = ME_Bcd2Int,
        .mpos_readlen = ME_ReadLen,
        .mpos_writelen = ME_WriteLen,

        .EndianMode = BIG_ENDIAN,
        .LengthMode = BCD_MODE,
};

ushort ME_Endian_Swab16(ushort n) {
    if (nlMpos_Command.EndianMode == BIG_ENDIAN)
        return Endian_Swab16(n);
    else
        return n;
}

uint ME_Endian_Swab32(uint n) {
    if (nlMpos_Command.EndianMode == BIG_ENDIAN)
        return Endian_Swab32(n);
    else
        return n;
}

uint ME_GetVar(void *poutdata, int size) {
    uint ret = 0;
    void *paddr = (void *) poutdata;

    if (size == _VAR_BIT8)
        ret = _VAR_READ(paddr, size);
    else if (size == _VAR_BIT16) {
        ((uchar *) &ret)[0] = _VAR_READ((paddr + 0), _VAR_BIT8);
        ((uchar *) &ret)[1] = _VAR_READ((paddr + 1), _VAR_BIT8);
    } else if (size == _VAR_BIT24) {
        ((uchar *) &ret)[0] = _VAR_READ((paddr + 0), _VAR_BIT8);
        ((uchar *) &ret)[1] = _VAR_READ((paddr + 1), _VAR_BIT8);
        ((uchar *) &ret)[2] = _VAR_READ((paddr + 2), _VAR_BIT8);
    } else if (size == _VAR_BIT32) {
        ((uchar *) &ret)[0] = _VAR_READ((paddr + 0), _VAR_BIT8);
        ((uchar *) &ret)[1] = _VAR_READ((paddr + 1), _VAR_BIT8);
        ((uchar *) &ret)[2] = _VAR_READ((paddr + 2), _VAR_BIT8);
        ((uchar *) &ret)[3] = _VAR_READ((paddr + 3), _VAR_BIT8);
    }
    return ret;
}

uint ME_SetVar(void *pindata, uint indata, int size) {
    void *paddr = (void *) pindata;

    if (size == _VAR_BIT8)
        _VAR_WRITE(paddr, indata, size);
    else if (size == _VAR_BIT16) {
        _VAR_WRITE((paddr + 0), (indata), _VAR_BIT8);
        _VAR_WRITE((paddr + 1), (indata >> 8), _VAR_BIT8);
    } else if (size == _VAR_BIT24) {
        _VAR_WRITE((paddr + 0), (indata), _VAR_BIT8);
        _VAR_WRITE((paddr + 1), (indata >> 8), _VAR_BIT8);
        _VAR_WRITE((paddr + 2), (indata >> 16), _VAR_BIT8);
    } else if (size == _VAR_BIT32) {
        _VAR_WRITE((paddr + 0), (indata), _VAR_BIT8);
        _VAR_WRITE((paddr + 1), (indata >> 8), _VAR_BIT8);
        _VAR_WRITE((paddr + 2), (indata >> 16), _VAR_BIT8);
        _VAR_WRITE((paddr + 3), (indata >> 24), _VAR_BIT8);
    }
    return 0;
}

uint ME_Int2Bcd(void *poutdata, uint n, int size) {
    uint tmp = 0;

    if (size == _VAR_BIT8) {
        tmp = _INT2BCD(n);
        _VAR_WRITE(poutdata, tmp, size);
    } else if (size == _VAR_BIT16) {
        ((uchar *) &tmp)[1] = _INT2BCD(n % 100);
        ((uchar *) &tmp)[0] = _INT2BCD(n / 100);
        _VAR_WRITE(poutdata, tmp, size);
    } else if (size == _VAR_BIT32) {
        tmp = 0;
    }
    return tmp;
}

uint ME_ReadLen(void *pindata, int size) {
    uint ret = 0;

    if (nlMpos_Command.LengthMode == BCD_MODE) {
        ret = nlMpos_Command.mpos_bcd2int(pindata, size);
    } else if (nlMpos_Command.LengthMode == ENDIAN_MODE) {
        ret = nlMpos_Command.mpos_getvar(pindata, size);
        ret = nlMpos_Command.mpos_endian_swab16(ret);
    }
    return (ret);
}

uint ME_WriteLen(void *poutdata, uint n, int size) {
    uint ret = 0;

    if (nlMpos_Command.LengthMode == BCD_MODE) {
        ret = nlMpos_Command.mpos_int2bcd(poutdata, n, size);
    } else if (nlMpos_Command.LengthMode == ENDIAN_MODE) {
        ret = nlMpos_Command.mpos_endian_swab16((ushort) n);
        ret = nlMpos_Command.mpos_setvar(poutdata, ret, size);
    }
    return (ret);
}

uint ME_Bcd2Int(void *pindata, int size) {
    uint ret = 0, tmp = 0;
    void *paddr = (void *) pindata;

    ret = _VAR_READ(paddr, size);
    if (size == _VAR_BIT8) {
        tmp = _BCD2INT(ret);
    } else if (size == _VAR_BIT16) {
        tmp = _BCD2INT(ret) * 100;
        ret = (ret >> 8);
        tmp += _BCD2INT(ret);
    } else if (size == _VAR_BIT32) {
        tmp = _BCD2INT(ret) * 1000000;
        ret = (ret >> 8);
        tmp += _BCD2INT(ret) * 10000;
        ret = (ret >> 8);
        tmp += _BCD2INT(ret) * 100;
        ret = (ret >> 8);
        tmp += _BCD2INT(ret);
    }
    return tmp;
}

jbyteArray chartobyteArray(JNIEnv* env, const char* p,int len)
{
    jbyteArray str;
    str = (*env)->NewByteArray(env, len);
    (*env)->SetByteArrayRegion(env, str, 0, len, (jbyte*)p);
    return str;
}

int responseCmd(unsigned char* pOut, int dataLen, int *outLen, char* ackCode){
    if((memcmp(ackCode, CMD_OK, 2) != 0) && (memcmp(ackCode, CMD_ERR_TAMPER, 2) != 0) ){
        if(NDK_GetTamperStatus() == TAMPSING){
            memcpy(ackCode, CMD_ERR_TAMPER, 2);
        }
    }
    if(pOut == NULL){
        return 0;
    }
    if(dataLen < 0){
        dataLen = 0;
    }
    memcpy(&pOut[0], ackCode, 2);
    *outLen = RESPOND_DATA_OFFSET + dataLen;
    if(memcmp(ackCode, CMD_OK, 2) != 0){
        LOGD_STR("ackCode", pOut,2);
    }
    int len = *outLen;
    LOGD_FMT("response len[%d]",len);
    if(len > 0 && len <= 4000){
        LOGD_STR("response",pOut,*outLen);
    }
    return 0;
}

int Sys_GetPosInfo(EM_SYS_HWINFO_ME emFlag, uint *punLen, char *psBuf) {
    LOGD_FMT(">>>SYS_HWINFO_FLAG[%d]",emFlag);
    int nRet;
    char szCsn[101];
    char ksn[101] = {0};
    ST_SEC_KCV_INFO stKcvInfoIn;

    memset(szCsn, 0, sizeof(szCsn));
    memset(ksn, 0, sizeof(ksn));
    tlv_t tlv_switch;
    uchar szbuf[TERMINAL_SIZE];

    memset(&tlv_switch, 0x00, sizeof(tlv_t));

    if (emFlag == SYS_HWINFO_GET_PIN_KSN) {
        nRet = NDK_FsExist(PIN_KSN_FILE);
        if (nRet != NDK_OK) {
            Udebug.ERROR_MSG_LOG("%s %d NDK_FsExist:nRet:%d\n", __FUNCTION__, __LINE__, nRet);
            nRet = ME_FsWrite(PIN_KSN_FILE,
                              (const uchar *) "\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00", 0, 10);
            nRet = ME_FsWrite(KSN_FILE, (const uchar *) "\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00",
                              0, 10);
        }
        nRet = NDK_FsExist(TRACK_KSN_FILE);
        if (nRet != NDK_OK) {
            Udebug.ERROR_MSG_LOG("%s %d NDK_FsExist:nRet:%d\n", __FUNCTION__, __LINE__, nRet);
            nRet = ME_FsWrite(TRACK_KSN_FILE,
                              (const uchar *) "\x10\x00\x00\x00\x00\x00\x00\x00\x00\x00", 0, 10);
            nRet = ME_FsWrite(KSN_FILE, (const uchar *) "\x10\x00\x00\x00\x00\x00\x00\x00\x00\x00",
                              10, 10);
        }
        nRet = NDK_FsExist(PBOC_KSN_FILE);
        if (nRet != NDK_OK) {
            Udebug.ERROR_MSG_LOG("%s %d NDK_FsExist:nRet:%d\n", __FUNCTION__, __LINE__, nRet);
            nRet = ME_FsWrite(PBOC_KSN_FILE,
                              (const uchar *) "\x20\x00\x00\x00\x00\x00\x00\x00\x00\x00", 0, 10);
            nRet = ME_FsWrite(KSN_FILE, (const uchar *) "\x20\x00\x00\x00\x00\x00\x00\x00\x00\x00",
                              20, 10);
        }
        *punLen = 10;
        nRet = ME_FsRead(PIN_KSN_FILE, (unsigned char *) psBuf, 0, 10);
    } else if (emFlag == SYS_HWINFO_GET_TRACK_KSN) {
        nRet = NDK_FsExist(PIN_KSN_FILE);
        if (nRet != NDK_OK) {
            nRet = ME_FsWrite(PIN_KSN_FILE,
                              (const uchar *) "\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00", 0, 10);
            nRet = ME_FsWrite(KSN_FILE, (const uchar *) "\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00",
                              0, 10);
        }
        nRet = NDK_FsExist(TRACK_KSN_FILE);
        if (nRet != NDK_OK) {
            nRet = ME_FsWrite(TRACK_KSN_FILE,
                              (const uchar *) "\x10\x00\x00\x00\x00\x00\x00\x00\x00\x00", 0, 10);
            nRet = ME_FsWrite(KSN_FILE, (const uchar *) "\x10\x00\x00\x00\x00\x00\x00\x00\x00\x00",
                              10, 10);
        }
        nRet = NDK_FsExist(PBOC_KSN_FILE);
        if (nRet != NDK_OK) {
            nRet = ME_FsWrite(PBOC_KSN_FILE,
                              (const uchar *) "\x20\x00\x00\x00\x00\x00\x00\x00\x00\x00", 0, 10);
            nRet = ME_FsWrite(KSN_FILE, (const uchar *) "\x20\x00\x00\x00\x00\x00\x00\x00\x00\x00",
                              20, 10);
        }
        *punLen = 10;
        nRet = ME_FsRead(TRACK_KSN_FILE, (unsigned char *) psBuf, 0, 10);
    } else if (emFlag == SYS_HWINFO_GET_PBOC_KSN) {
        nRet = NDK_FsExist(PIN_KSN_FILE);
        if (nRet != NDK_OK) {
            nRet = ME_FsWrite(PIN_KSN_FILE,
                              (const uchar *) "\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00", 0, 10);
            nRet = ME_FsWrite(KSN_FILE, (const uchar *) "\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00",
                              0, 10);
        }
        nRet = NDK_FsExist(TRACK_KSN_FILE);
        if (nRet != NDK_OK) {
            nRet = ME_FsWrite(TRACK_KSN_FILE,
                              (const uchar *) "\x10\x00\x00\x00\x00\x00\x00\x00\x00\x00", 0, 10);
            nRet = ME_FsWrite(KSN_FILE, (const uchar *) "\x10\x00\x00\x00\x00\x00\x00\x00\x00\x00",
                              10, 10);
        }
        nRet = NDK_FsExist(PBOC_KSN_FILE);
        if (nRet != NDK_OK) {
            nRet = ME_FsWrite(PBOC_KSN_FILE,
                              (const uchar *) "\x20\x00\x00\x00\x00\x00\x00\x00\x00\x00", 0, 10);
            nRet = ME_FsWrite(KSN_FILE, (const uchar *) "\x20\x00\x00\x00\x00\x00\x00\x00\x00\x00",
                              20, 10);
        }
        *punLen = 10;
        nRet = ME_FsRead(PBOC_KSN_FILE, (unsigned char *) psBuf, 0, 10);
    } else if (emFlag == SYS_HWINFO_GET_KSN) {
        nRet = ME_FsRead(KSN_FILE, (unsigned char *) ksn, 0, MAX_SIZE);
        if (nRet == NDK_OK) {
            nRet = NDK_FsFileSize(KSN_FILE, punLen);
            if (nRet != NDK_OK) {
                Udebug.ERROR_MSG_LOG("%s %d NDK_FsFileSize:nRet:%d\n", __FUNCTION__, __LINE__,
                                     nRet);
                *punLen = 0;
            }
            memcpy(psBuf, ksn, *punLen);
        }
    } else if (emFlag == SYS_HWINFO_GET_CSN) {
        nRet = ME_FsRead(SN_FILE, (unsigned char *) szCsn, 0, MAX_SIZE);
        if (nRet == 0) {
            *punLen = strlen(szCsn);
            memcpy(psBuf, szCsn, *punLen);
            Udebug.DEBUG_Levelone("*punLen=%d\n", *punLen);
        }
        LOGD_STR("SYS_HWINFO_GET_CSN",szCsn, sizeof(szCsn));
    } else if (emFlag == SYS_HWINFO_GET_PRODUCE_SN) {
        nRet = NDK_ERR;
    } else if (emFlag == SYS_HWINFO_GET_VID) {
        nRet = 0;
        *punLen = 2;
        memcpy(psBuf, "\x00\x01", 2);
    } else if (emFlag == SYS_HWINFO_GET_KCV) {
        *punLen = 1;
        memset(&stKcvInfoIn, 0, sizeof(stKcvInfoIn));
        stKcvInfoIn.nCheckMode = SEC_KCV_NONE;
        stKcvInfoIn.nLen = 0;
        nRet = NDK_SecGetKcv(SEC_KEY_TYPE_TAK, 2, &stKcvInfoIn);
        nRet += NDK_SecGetKcv(SEC_KEY_TYPE_TAK, 3, &stKcvInfoIn);
        nRet += NDK_SecGetKcv(SEC_KEY_TYPE_TAK, 4, &stKcvInfoIn);
        if (nRet != 0) {
            psBuf[0] = 0x04;
            LOGE_NDK("NDK_SecGetKcv", nRet, NULL, 0);
            Udebug.ERROR_MSG_LOG("nCheckMode[%d] nLen[%d]", stKcvInfoIn.nCheckMode,
                                 stKcvInfoIn.nLen);
        } else
            psBuf[0] = 0x00;
        nRet = 0;    //不管成功失败都返回0
    } else if (emFlag == SYS_HWINFO_GET_TLVSWITCH) {
        nRet = NDK_ERR;
    } else if (emFlag == SYS_HWINFO_GET_TLVSWITCH1) {
        nRet = NDK_ERR;
    } else {
        nRet = NDK_SysGetPosInfo(emFlag, punLen, psBuf);
        if (nRet != NDK_OK) {
            LOGE_NDK("NDK_SysGetPosInfo", nRet, NULL, 0);
            Udebug.ERROR_MSG_LOG("emFlag[%d] punLen[%d]", emFlag, *punLen);
        }
    }
    return nRet;
}
int Sys_SetPosInfo(EM_SYS_HWINFO_ME emFlag, char *psBuf, int len) {
    int i;
    char buf[100] = {0};
    int nRet, iRet;
    int flag = 0;

    if (emFlag == SYS_HWINFO_GET_CSN) {
        iRet = NDK_FsDel(SN_FILE);
        if (iRet != NDK_OK) LOGE_NDK("NDK_FsDel", iRet, NULL, 0);
        nRet = ME_FsWrite(SN_FILE, (const unsigned char *) buf, 0, MAX_SIZE);    //清0
        nRet = ME_FsWrite(SN_FILE, (const unsigned char *) psBuf, 0, len);
    } else if (emFlag == SYS_HWINFO_GET_KSN) {
        iRet = NDK_FsDel(KSN_FILE);
        if (iRet != NDK_OK) LOGE_NDK("NDK_FsDel", iRet, NULL, 0);
        for (i = 0; i < len; i++) {
            if ((psBuf[i] < '0') || (psBuf[i] > '9' && psBuf[i] < 'A')
                || (psBuf[i] > 'F' && psBuf[i] < 'a') || (psBuf[i] > 'f')) {
                flag = 1;
                break;
            }
        }

        if (flag) {
            nRet = ME_FsWrite(KSN_FILE, (const uchar *) psBuf, 0, len);
        } else {
            iRet = NDK_AscToHex((unsigned char *) psBuf, len, 0, buf);
            if (iRet != NDK_OK) LOGE_NDK("NDK_AscToHex", iRet, NULL, 0);
            nRet = ME_FsWrite(KSN_FILE, (const uchar *) buf, 0, (len + 1) / 2);
        }
    } else if (emFlag == SYS_HWINFO_GET_PRODUCE_SN) {
        nRet = -1;
    } else {
        EXEC_NDK("NDK_SP_SysSetPosInfo", NDK_SP_SysSetPosInfo(emFlag, psBuf), NDK_OK,DEVICE_SETSN);
        nRet = NDK_SysSetPosInfo(emFlag, psBuf);
    }
    return nRet;
}