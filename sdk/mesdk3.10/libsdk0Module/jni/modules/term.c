#include <fcntl.h>
#include "desc.h"
#include "NDK.h"
#include "unistd.h"
#include "stdlib.h"
#include "string.h"
#include "command.h"
#include "comm.h"
#include "log.h"
#include "api.h"
#include "comm.h"
extern ME_TPEDCTL g_METPEDCtl;
extern char g_readInfoFlag;
int Cmd_Enter_Flag;
extern uint g_preCmd;
extern ME31_CONFIG_T g_me31conf;

int Term_Buzzer(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int i;
    int offset;
    uint count;
    uint freq;
    uint time;
    uint interval;
    char headCode[2];
    memcpy(headCode, CMD_OK, 2);
    offset = 0;
    count = nlMpos_Command.mpos_getvar(pbuf + offset, _VAR_BIT16);offset += 2;
    count = nlMpos_Command.mpos_endian_swab16(count);
    freq = nlMpos_Command.mpos_getvar(pbuf + offset, _VAR_BIT16);offset += 2;
    freq = nlMpos_Command.mpos_endian_swab16(freq);
    time = nlMpos_Command.mpos_getvar(pbuf + offset, _VAR_BIT16);offset += 2;
    time = nlMpos_Command.mpos_endian_swab16(time);
    interval = nlMpos_Command.mpos_getvar(pbuf + offset, _VAR_BIT16);offset += 2;
    interval = nlMpos_Command.mpos_endian_swab16(interval);

    if (count == 0 || time == 0) {
        memcpy(headCode, CMD_ERR_PARAM, 2);
        goto ON_ACK;
    }

    for (i = 0; i < count; i++) {
        if(!EXEC_NDK("NDK_SysTimeBeep",NDK_SysTimeBeep(1000, time),NDK_OK,TERM_BUZZER)){
            memcpy(headCode, CMD_ERR_OTHER, 2);
            goto ON_ACK;
        }
        if (count > 1) {
            if(!EXEC_NDK("NDK_SysMsDelay",NDK_SysMsDelay(interval + time),NDK_OK,TERM_BUZZER)){
                memcpy(headCode, CMD_ERR_OTHER, 2);
                goto ON_ACK;
            }
        }
    }
    ON_ACK:
    responseCmd(pOut, 0, outLen, headCode);
    return 0;
}

int Term_CancelReset(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int status = 0;
    uchar pinBlock[32];
    uchar ksn[16];
    if(g_preCmd == CARDREADER_OPEN){
        EXEC_NDK("NDK_RfidCloseRf",NDK_RfidCloseRf(),NDK_OK,TERM_CANCELRESET);
        EXEC_NDK("NDK_RfidCloseRf",NDK_MagClose(),NDK_OK,TERM_CANCELRESET);
    }
    if(g_preCmd == RFID_POWERON){
        EXEC_NDK("NDK_RfidCloseRf",NDK_RfidCloseRf(),NDK_OK,TERM_CANCELRESET);
    }
    if(g_preCmd == PINPAD_INPUT){
        status = 0x8000 | K_ESC;
        NDK_SecGetPinResult(pinBlock, &status);//EXEC_NDK("NDK_SecGetPinResult",NDK_SecGetPinResult(pinBlock, &status),NDK_OK);
        NDK_SecGetPinResultDukpt(pinBlock, ksn, &status);//EXEC_NDK("NDK_SecGetPinResultDukpt",NDK_SecGetPinResultDukpt(pinBlock, ksn, &status),NDK_OK);
    }
    responseCmd(pOut, 0, outLen, CMD_OK);
    g_preCmd = 0;
    return 0;
}

int Term_ShutDown(puchar pbuf, int buf_len, unsigned char *pOut, int *outLen) {

    char headCode[2];
    memcpy(headCode, CMD_OK, 2);

    memcpy(headCode, CMD_ERR_UNSUPPORT, 2);    //指令不支持


    responseCmd(pOut, 0, outLen, headCode);
    return 0;
}

int Term_Confirmation(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    char headCode[2];
    memcpy(headCode, CMD_OK, 2);

    if (g_preCmd == PINPAD_INPUT) {
        Cmd_Enter_Flag = 1;
        EXEC_NDK("NDK_MagClose",NDK_MagClose(),NDK_OK,TERM_CONFIRMATION);
    }
    return 0;
}

int Term_SetKeyVol(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int nRet;
    int nLen;
    int offset;
    char keyVol_Mode;
    char headCode[2];
    memcpy(headCode, CMD_OK, 2);

    nLen = buf_len;

    offset = 0;
    keyVol_Mode = nlMpos_Command.mpos_getvar(pbuf + MPOS_VARIABLE_OFFSET + offset, _VAR_BIT8);
    offset += 2;

    if (keyVol_Mode != 0 && keyVol_Mode != 1) {
        memcpy(headCode, CMD_ERR_PARAM, 2);
        goto ON_ACK;
    }

    nRet = NDK_SysKeyVolSet(keyVol_Mode);
    if (nRet != NDK_OK) {
        Udebug.ERROR_MSG_LOG("%s %d NDK_SysKeyVolSet:nRet:%d\n", __FUNCTION__, __LINE__, nRet);
        Udebug.ERROR_MSG_LOG_String(pbuf, buf_len);
        memcpy(headCode, CMD_ERR_OTHER, 2);
        goto ON_ACK;
    }

    ON_ACK:
    responseCmd(pOut, 0, outLen, headCode);
    return 0;
}

int Term_SetTagData(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen)
{
    int tlvlen,offset;
    uchar start, end;
    char headCode[2];
    tlv_t tlv_tmp[TERMINAL_NUM];
    memcpy(headCode, CMD_OK, 2);

    offset = 0;
    tlvlen = nlMpos_Command.mpos_readlen(pbuf+offset, _VAR_BIT16);
    if (parse_tlv(pbuf+offset+2, tlvlen, tlv_tmp, sizeof(tlv_tmp)/sizeof(tlv_t), STRING_TLVOBJ) < 0) {
        memcpy(headCode, CMD_ERR_PARAM, 2);
        goto ON_ACK;
    }
    start = 1;
    end = tlv_tmp[0].childnum+1;
    while(start < end) {
        int ret = mpos_writeonetlv(ME_TLV_FILE, &tlv_tmp[start]);
        if(ret!=0) {
            memcpy(headCode, CMD_ERR_OTHER, 2);
            goto ON_ACK;
        }
        start ++;
    }
    ON_ACK:
    responseCmd(pOut, 0, outLen, headCode);
    return 0;
}


int Term_GetTagData(unsigned char* pbuf, int buf_len, unsigned char *pOut, int *outLen)
{
    int fd,nRet,nLen,nSize,i,j,num,offset,extoffset,extsize,nTlvnum;
    uint tag;void *pdata;
    unsigned int uszTagName[TERMINAL_NUM];
    unsigned char tmp[TERMINAL_SIZE];

    fd = -1;
    num = 0;

    pdata = tmp;
    extoffset = 2;
    memset(pdata, 0xFF, TERMINAL_SIZE); // FF
    offset = 0;

    nLen = nlMpos_Command.mpos_readlen(pbuf+offset, _VAR_BIT16);
    offset += 2;
    Tlv_Init();	//读取终端参数时清空存在缓冲内的交易会话数据

    if(!EXEC_NDK("NDK_FsExist",NDK_FsExist(ME_TLV_FILE),NDK_OK,TERM_GETTAGDATA)){
        goto ON_ACK;
    }
    if(!EXEC_NDK("NDK_FsFileSize",NDK_FsFileSize(ME_TLV_FILE, (uint *)&nSize),NDK_OK,TERM_GETTAGDATA)){
        goto ON_ACK;
    }

    nTlvnum =  nSize/TERMINAL_SIZE;
    if((fd = NDK_FsOpen(ME_TLV_FILE, "r")) < 0) {
        ERRMSG(SDK_ERR_FILE_OPEN,TERM_GETTAGDATA);
        goto ON_ACK;
    }

    if(nLen == 0) {//全部读
        for(i=0;i<nTlvnum; i++) {
            if(!EXEC_NDK("NDK_FsSeek",NDK_FsSeek(fd, TERMINAL_SIZE*i, SEEK_SET),NDK_OK,TERM_GETTAGDATA)){
                goto ON_ACK;
            }

            if(!EXEC_NDK("NDK_FsRead",NDK_FsRead(fd, pdata, 4)!=4,NDK_OK,TERM_GETTAGDATA)){//4!=4 0  5!=4 1
                goto ON_ACK;
            }
            memcpy((char *)&tag,pdata,4);
            NDK_IntToC4((uchar *)pdata, tag);
            nRet = decode_tag(pdata, 4, &tag, 1);
            if(nRet == 1) {
                uszTagName[num++] =  tag;
            }
        }
    } else{
        nRet = decode_tag(pbuf+offset, nLen, uszTagName, sizeof(uszTagName)/sizeof(unsigned int));
        if(nRet < 0){
            goto ON_ACK;
        }
        else
            num = nRet;
    }

    extsize = 2;
    for(i=0; i<num; i++)
    {
        for(j=0; j<nTlvnum; j++) {
            if(!EXEC_NDK("NDK_FsSeek",NDK_FsSeek(fd, TERMINAL_SIZE*j, SEEK_SET),NDK_OK,TERM_GETTAGDATA)){
                goto ON_ACK;
            }
            if(!EXEC_NDK("NDK_FsRead",NDK_FsRead(fd, pdata, 8) != 8,NDK_OK,TERM_GETTAGDATA)){
                goto ON_ACK;
            }
            memcpy((char *)&tag,pdata,4);
            memcpy((char *)&nSize,pdata+4,4);
            NDK_IntToC4(pdata, tag);
            nRet = decode_tag(pdata, 4, &tag, 1);
            if((tag == uszTagName[i])&&(nRet==1)) {
                if(uszTagName[i]>>16!=0x00){
                    nlMpos_Command.mpos_setvar(pOut+extoffset+extsize, (uszTagName[i]>>16), _VAR_BIT8);
                    extsize ++;
                }
                nlMpos_Command.mpos_setvar(pOut+extoffset+extsize, (uszTagName[i]>>8), _VAR_BIT8);
                extsize ++;
                nlMpos_Command.mpos_setvar(pOut+extoffset+extsize, (uszTagName[i]), _VAR_BIT8);
                extsize ++;
                if(nSize <= 127) {
                    nlMpos_Command.mpos_setvar(pOut+extoffset+extsize, nSize, _VAR_BIT8);
                    extsize += 1;
                }
                else if(nSize <= 255) {
                    nlMpos_Command.mpos_setvar(pOut+extoffset+extsize, 0x81, _VAR_BIT8);
                    extsize += 1;
                    nlMpos_Command.mpos_setvar(pOut+extoffset+extsize, nSize, _VAR_BIT8);
                    extsize += 1;
                }
                else {
                    nlMpos_Command.mpos_setvar(pOut+extoffset+extsize, 0x82, _VAR_BIT8);
                    extsize += 1;
                    nlMpos_Command.mpos_setvar(pOut+extoffset+extsize, (nSize>>8), _VAR_BIT8);
                    extsize += 1;
                    nlMpos_Command.mpos_setvar(pOut+extoffset+extsize, (nSize&0xFF), _VAR_BIT8);
                    extsize += 1;
                }
                if(!EXEC_NDK("NDK_FsRead",NDK_FsRead(fd, pdata+8, nSize) != nSize,NDK_OK,TERM_GETTAGDATA)){
                    goto ON_ACK;
                }
                memcpy(pOut+extoffset+extsize, pdata+8, nSize);
                extsize += nSize;
                break;
            }
        }
        if(j>= nTlvnum) {
            if(uszTagName[i]>>16!=0x00){
                nlMpos_Command.mpos_setvar(pOut+extoffset+extsize, (uszTagName[i]>>16), _VAR_BIT8);
                extsize ++;
            }
            nlMpos_Command.mpos_setvar(pOut+extoffset+extsize, (uszTagName[i]>>8), _VAR_BIT8);
            extsize ++;
            nlMpos_Command.mpos_setvar(pOut+extoffset+extsize, (uszTagName[i]), _VAR_BIT8);
            extsize ++;
            nlMpos_Command.mpos_setvar(pOut+extoffset+extsize, 0, _VAR_BIT8);
            extsize ++;
        }
    }
    EXEC_NDK("NDK_FsClose",NDK_FsClose(fd),NDK_OK,TERM_GETTAGDATA);
    nlMpos_Command.mpos_writelen(pOut+extoffset, extsize-2, _VAR_BIT16);
    responseCmd(pOut, extsize, outLen, CMD_OK);
    return 0;
    ON_ACK:
    EXEC_NDK("NDK_FsClose",NDK_FsClose(fd),NDK_OK,TERM_GETTAGDATA);
    nlMpos_Command.mpos_writelen(pOut+extoffset, 0, _VAR_BIT16);
    responseCmd(pOut, 2, outLen, CMD_ERR_OTHER);
    return 0;
}