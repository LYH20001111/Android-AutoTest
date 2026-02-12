#include <memory.h>
#include <string.h>
#include <comm.h>
#include <stdlib.h>
#include "prnttf.h"
#include "log.h"
#include "api.h"
#include "desc.h"
void Prn_ModuleInit()
{
    static int initFlag = 0;
    if(initFlag == 0){
        initFlag = 1;
        LOGE_FMT("Prn_ModuleInit");
        EXEC_NDK("#NDK_PrnModuleInit",NDK_PrnModuleInit(),NDK_OK,COMMAND_NONE);
    }
}

static void TTF_dlload()
{
    static int g_loadTTF = 0;
    if(g_loadTTF == 0){
        g_loadTTF = 1;
        TTF_PrnApiLoad();
        LOGD_FMT("init libnlprintex.so...");
    }
}
static int Prn_TTFPrint(const char* pszBuf)
{
    NDK_MagClose();

    LOGD_FMT("TTF_ScriptPrint start!!");
    int ret = -1;
    char version[36];
    memset(version,0,sizeof(version));
    ret = TTF_GetVersion(version);
    if(ret == NDK_OK)
    LOGD_FMT("TTF Version[%s]",version);

    TTF_dlload();

    ret = TTF_ScriptPrint(pszBuf);
    LOGD_FMT("TTF_ScriptPrint ret[%d]",ret);

    if(ret == NDK_OK){
        ret = TTF_PrnExit();
    }
    LOGD_FMT("TTF_ScriptPrint end!![%d]",ret);
    return ret;
}
int Prn_GetStatus(unsigned char * pbuf,  int buf_len, unsigned char *pOut, int *outLen)
{
    char headCode[2];
    int extoffset;
    EM_PRN_STATUS prnStatus;

    if(!EXEC_NDK("NDK_PrnGetStatus",NDK_PrnGetStatus(&prnStatus),NDK_OK,PRN_GETSTATUS)){
        memcpy(headCode, CMD_ERR_PARAM, 2);
        goto ON_ACK;
    }
    memcpy(headCode, CMD_OK, 2);
    extoffset = 2;
     if(prnStatus == PRN_STATUS_OK){
        pOut[extoffset] = 0x00;
    } else if(prnStatus == PRN_STATUS_BUSY){
        pOut[extoffset] = 0x80;
    }else if(prnStatus == PRN_STATUS_NOPAPER){
        pOut[extoffset] = 0x04;
    }else if(prnStatus == PRN_STATUS_OVERHEAT){
        pOut[extoffset] = 0x08;
    }else if(prnStatus == PRN_STATUS_VOLERR){
        pOut[extoffset] = 0x40;
    }else if(prnStatus == PRN_STATUS_DESTROYED){
        pOut[extoffset] = 0x81;
    }else if(prnStatus == PRN_STATUS_PPSERR){
        pOut[extoffset] = 0x82;
    } else{
        pOut[extoffset] = 0x81;
    }
    ON_ACK:
    responseCmd(pOut,extoffset,outLen,headCode);
    return 0;
}

int Prn_Print(puchar pbuf,  int buf_len, unsigned char *pOut, int *outLen)
{
    int offset = 0;
    uchar mode = nlMpos_Command.mpos_getvar(pbuf+offset, _VAR_BIT8);offset+=1;
    uint lenFlag = nlMpos_Command.mpos_readlen(pbuf+offset, _VAR_BIT8);offset+=1;
    uint dataLen = 0;
    if(lenFlag == 4){
        dataLen = nlMpos_Command.mpos_readlen(pbuf+offset, _VAR_BIT32);offset+=4;
    } else {
        dataLen = nlMpos_Command.mpos_readlen(pbuf+offset, _VAR_BIT16);offset+=2;
    }
    char *data = pbuf+offset;offset += dataLen;
    int reserve = nlMpos_Command.mpos_getvar(pbuf+offset, _VAR_BIT8);offset+=1;
    LOGD_FMT(">>>mode[%d] lenFlag[%d] dataLen[%d] reserve[%d]",mode,lenFlag,dataLen,reserve);
    int extoffset = 2;

    int ret = Prn_TTFPrint(data);
    if(ret != NDK_OK){
        ERRMSG(SDK_ERR_PRN_TTF,PRN_PRINT);
    }
    if(ret == NDK_OK){
        memcpy(pOut + extoffset,"00",2);
    } else if(ret==NDK_ERR){
        memcpy(pOut + extoffset,"01",2);
    }else if(ret == NDK_ERR_PARA){
        memcpy(pOut + extoffset,"02",2);
    }else if(ret == NDK_ERR_PATH){
        memcpy(pOut + extoffset,"03",2);
    }else if(ret==PRN_STATUS_BUSY)	{
        memcpy(pOut + extoffset,"04",2);
    }else if(ret==PRN_STATUS_NOPAPER){
        memcpy(pOut + extoffset,"05",2);
    }else if(ret==PRN_STATUS_OVERHEAT){
        memcpy(pOut + extoffset,"06",2);
    }else if(ret == PRN_STATUS_VOLERR){
        memcpy(pOut + extoffset,"07",2);
    }else if(ret == PRN_STATUS_DESTROYED){
        memcpy(pOut + extoffset,"08",2);
    }else if(ret == PRN_STATUS_PPSERR){
        memcpy(pOut + extoffset,"09",2);
    }else{
        memcpy(pOut + extoffset,"01",2);
    }
    responseCmd(pOut, 2, outLen, CMD_OK);
    return 0;
}

int Prn_CutterPaper(puchar pbuf, int buf_len, unsigned char *pOut, int *outLen)
{
    int status = -1;

    if(EXEC_NDK("NDK_PrnGetStatus",NDK_PrnGetStatus(&status),NDK_OK,PRN_CUTTERPAPER)){
        if(status == PRN_STATUS_OK){
            if(EXEC_NDK("NDK_PrnCutterPerformance",NDK_PrnCutterPerformance(),NDK_OK,PRN_CUTTERPAPER)){
                memcpy(pOut+2,"\x30\x31",2);//ok
                responseCmd(pOut,2,outLen,CMD_OK);
                return 0;
            }
        }
    }
    memcpy(pOut+2,"\x30\x32",2);//fail
    responseCmd(pOut,2,outLen,CMD_OK);
    return 0;
}

int Prn_SetPaperSize(puchar pbuf, int buf_len, unsigned char *pOut, int *outLen)
{
    TTF_dlload();
    int size =  nlMpos_Command.mpos_getvar(pbuf, _VAR_BIT8);
    LOGD_FMT(">>>PaperSize[%d]",size);
    if(EXEC_NDK("TTF_PrnSetPaperSize",TTF_PrnSetPaperSize(size),NDK_OK,PRN_SETPAPERSIZE)){
        memcpy(pOut+2,"\x30\x31",2);//ok
        responseCmd(pOut,2,outLen,CMD_OK);
    }else{
        memcpy(pOut+2,"\x30\x32",2);//fail
        responseCmd(pOut,2,outLen,CMD_OK);
    }
    return 0;
}
