
#include <unistd.h>
#include <comm.h>
#include <plugincard.h>
#include <stdlib.h>
#include <string.h>
#include "threadtool.h"
#include "ndk.h"
#include "cardmgr.h"
#include "nllogger.h"
#include "log.h"
#include "api.h"

extern int g_aCardAtq;
extern int g_rfMultiLevel;

int FRfid_FelicaApdu(uchar *sendData, int sendLen, uchar *recv, int *recvLen){
    LOGD_FMT(">>>sendLen[%d]",sendLen);
    uchar recvData[1024];
    memset(recvData,0, sizeof(recvData));
    int ret = NDK_RfidFelicaApdu(sendLen,sendData, recvLen, recv);
    if(!EXEC_NDK("NDK_RfidFelicaApdu",ret, NDK_OK)){
        return ret;
    }
    return 0;
}

int FRfid_FelicaApdu_retry(uchar *sendData, int sendLen, uchar *recv, int *recvLen, int timeout, int retryTimes) {
    LOGD_FMT(">>>sendLen[%d]",sendLen);
    uchar recvData[1024];
    memset(recvData,0, sizeof(recvData));
    int ret = NDK_FelicaSetTimeout(timeout);
    if (ret != 0) {
        return ret;
    }
    ret = NDK_RfidFelicaApdu(sendLen,sendData, recvLen, recv);
    if (ret == -2030) {
        while (retryTimes > 0) {
            ret = NDK_FelicaSetTimeout(timeout);
            if (ret != 0) {
                break;
            }
            ret = NDK_RfidFelicaApdu(sendLen,sendData, recvLen, recv);
            if (ret == 0) {
                break;
            }
            retryTimes--;
        }
    }
    return ret;
}

#define M1

int FRfid_M1AuthKey(int kmode,uchar *uid, int KeySector,uchar *keyData) {
	if (kmode != 0x60 && kmode != 0x00 && kmode != 0x61 && kmode != 0x01) {
		ERRMSG(NDK_ERR_PARA);
		return NDK_ERR_PARA;
	}
	if (KeySector < 0 || KeySector > 255) {
		ERRMSG(NDK_ERR_PARA);
		return NDK_ERR_PARA;
	}
	int ret = NDK_M1ExternalAuthen(4, uid, kmode, keyData, KeySector);
	if(!EXEC_NDK("NDK_M1ExternalAuthen",ret,NDK_OK)){
		return ret;
	}
	return 0;
}

int FRfid_M1ReadBlock(int number, unsigned char *data, int *len) {

	LOGD_FMT(">>>number[%d]",number);

	int ret = NDK_M1Read(number, len, data);
    if (!EXEC_NDK("NDK_M1Read", ret, NDK_OK)) return ret;

	LOGD_FMT(">>>len[%d]", *len);

	return 0;
}

int FRfid_M1WriteBlock(int number, unsigned char *data, int dataLen) {

	LOGD_FMT(">>>number[%d] dataLen[%d]", number, dataLen);

    int len = 16;
    int ret = NDK_M1Write(number, &dataLen, data);
    if (!EXEC_NDK("NDK_M1Write", ret, NDK_OK)) return ret;

	return 0;
}

int FRfid_M1Increment(int nBlockNum, unsigned char *data) {

    int ret = NDK_M1Increment(nBlockNum, 4,data);
	if(!EXEC_NDK("NDK_M1Increment", ret,NDK_OK)){
        return ret;
    }

    return 0;
}

int FRfid_M1Decrement(int nBlockNum, unsigned char *data) {
    int ret = NDK_M1Decrement(nBlockNum, 4,data);
    if(!EXEC_NDK("NDK_M1Decrement",ret,NDK_OK)){
        return ret;
    }

    return 0;
}

int FRfid_M1Transfer(int blockNum){
    int ret = NDK_M1Transfer(blockNum);
    if (!EXEC_NDK("NDK_M1Transfer",ret,NDK_OK)) return ret;

    return NDK_OK;
}

int FRfid_M1Restore(int blockNum){
    int ret = NDK_M1Restore(blockNum);
    EXEC_NDK("NDK_M1Restore", ret, NDK_OK);
    return ret;
}

#define M0

int FRfid_M0AuthKey(unsigned char *keyData, int keyLen) {
    int   uidLen=0;
    uchar uid[32],sak=-1;

    if(keyLen != 16){
		ERRMSG(NDK_ERR_PARA);
        return NDK_ERR_PARA;
    }

    int ret = NDK_M0Authen(keyData);
    if(!EXEC_NDK("NDK_M0Authen",ret, NDK_OK)){
        return ret;
    }
    LOGE_FMT(">>>NDK_M0Authen succ");

    int i=0,count = 6;
    for(i = 0; i < count; i++){
        LOGD_FMT(">>>count[%d]",i);
        if(EXEC_NDK("NDK_MifareActive2", NDK_MifareActive(0x52,uid,&uidLen,&sak), NDK_OK)){
            break;
        }
        if(i == count-1){
			return NDK_ERR;
        }
    }
    return 0;
}

int FRfid_M0ReadBlock(int number, unsigned char *data, int *len) {

    LOGD_FMT(">>>number[%d]", number);

    int ret = NDK_M0Read(number, len, data);
    if (!EXEC_NDK("NDK_M0Read", ret, NDK_OK)) return ret;

    LOGD_FMT(">>>len[%d]", *len);
    return 0;;
}

int FRfid_M0WriteBlock(int number,unsigned char *data, int dataLen) {

    LOGD_FMT(">>>number[%d] dataLen[%d]", number, dataLen);

    int ret = NDK_M0Write(number, dataLen, data);
    if (!EXEC_NDK("NDK_M0Write", ret, NDK_OK)) return ret;

    return 0;
}