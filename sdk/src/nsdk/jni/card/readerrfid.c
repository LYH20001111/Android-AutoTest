
#include <unistd.h>
#include <string.h>
#include "plugincard.h"
#include "stdio.h"
#include "readerrfid.h"
#include "cardmgr.h"
#include "ndk.h"
#include "log.h"
#include "card.h"

extern JavaVM *gJavaVM;
void setContactlessCardResult(jobject resultObj, int nOutBufLen, uchar *outBuf);

int PubRFPowerDown(void){
    int nRet = 0;

    nRet = NDK_RfidPiccDeactivate(0);
    LOGD_FMT("NDK_RfidPiccDeactivate nRet=%d", nRet);

    nRet += NDK_RfidCloseRf();
    LOGD_FMT("NDK_RfidCloseRf nRet=%d", nRet);
    return nRet;
}

int PubRFCPUComm(char *psSend, int nSendLen,char *psRecv, int *pnRecvLen){
    int nRet = 0;

    nRet = NDK_RfidPiccApdu(nSendLen, (uchar *)psSend, pnRecvLen, (uchar *)psRecv);
    LOGD_FMT("NDK_RfidPiccApdu nRet=%d", nRet);
    return  nRet;
}

int PubRFOpen(){
    int nRet = 0;
    char szRsep[512] = {0};

    memset(szRsep, 0, sizeof(szRsep));
    nRet = NDK_RfidInit((uchar *)szRsep);
    if(nRet != NDK_OK){
        return FAIL;
    }

    nRet = NDK_RfidOpenRf();
    if(nRet != NDK_OK){
        return FAIL;
    }

    LOGD_FMT("NDK_RfidOpenRf");
    return SUCC;
}

int PubRFSeekCard(int nPiccType){
    int nRet = 0;
    uchar ucType = NDK_RFID_AB;

    LOGD_FMT("ucPiccType=0x%02X", nPiccType);

    if (HAS_RFID_A(nPiccType) && HAS_RFID_B(nPiccType) && HAS_RFID_F(nPiccType)){
        ucType = NDK_RFID_ABF;
    } else if (HAS_RFID_A(nPiccType)){
        ucType = NDK_RFID_A;
        if (HAS_RFID_B(nPiccType)){
            ucType = NDK_RFID_AB;
        } else if (HAS_RFID_F(nPiccType)){
            ucType = NDK_RFID_AF;
        }
    } else if (HAS_RFID_B(nPiccType)){
        ucType  = NDK_RFID_B;
        if (HAS_RFID_F(nPiccType)){
            ucType = NDK_RFID_BF;
        }
    } else if (HAS_RFID_F(nPiccType)){
        ucType = NDK_RFID_F;
    }

    LOGD_FMT("ucType=0x%02X", ucType);

    nRet = NDK_RfidPiccType(nPiccType);
    if(nRet != NDK_OK){
        return FAIL;
    }

    nRet = NDK_RfidPiccDetect(&ucType);
    LOGD_FMT("NDK_RfidPiccDetect nRet=%d", nRet);
    if(nRet != NDK_OK){
        if(nRet == NDK_ERR_RFID_UPED){
            LOGD_FMT("NDK_RfidPiccDetect->NDK_ERR_RFID_UPED");
            return SUCC;
        }else if (nRet == NDK_ERR_RFID_ASEEK
                 || nRet == NDK_ERR_RFID_BSEEK
                 || nRet == NDK_ERR_RFID_ABON) {//多卡冲突
            return MULTI_CARD;

        }else if (nRet == NDK_ERR_MI_QUIT){ //按了取消键
            return QUIT;
        }
        return FAIL;
    }
    return SUCC;
}

int PubRFDetectCard(const STREADCARDPARAM *pstReadCardParm, int *pOutType){
    int nRet = 0;
    int nclType = 0;
    int nOutBufLen = 0;
    uchar outBuf[512];
    int nPiccType = pstReadCardParm->clCardType;

    LOGD_FMT("ucPiccType=0x%02X", nPiccType);

    nRet = NDK_RfidSetDetectType(nPiccType);
    if(nRet != NDK_OK){
        LOGD_FMT("NDK_RfidSetDetectType，nRet=%d", nRet);
        return nRet;
    }

    if ((nPiccType & RF_TYPE_F) && pstReadCardParm->unLenParamTypeF > 0){
        //TODO Felica设置参数需要驱动修改一下
//        NDK_RfidSetPiccParam();
    }

    if ((nPiccType & RF_TYPE_V) && pstReadCardParm->unLenParamTypeV > 0){
        NDK_RfidSetPiccParam(WUPV, pstReadCardParm->unLenParamTypeV, pstReadCardParm->pParamTypeV);
    }

    // felica 要获取 idmpmm 返回给 java 层
    nRet = NDK_RfidDetectWithCardType(&nclType, &nOutBufLen, outBuf);
    LOGD_FMT("NDK_RfidDetectWithCardType nRet=%d, *outType=0x%02X, nOutBufLen=%d", nRet, nclType, nOutBufLen);
    if(nRet != NDK_OK){
        if(nRet == NDK_ERR_RFID_UPED){ //返回这个状态可能是上一次用卡没有下电,此时无法返回卡片类型
            LOGD_FMT("NDK_RfidDetectWithCardType->NDK_ERR_RFID_UPED");
            return  nRet;
        }else if (nRet == NDK_ERR_RFID_ASEEK
                  || nRet == NDK_ERR_RFID_BSEEK
                  || nRet == NDK_ERR_RFID_ABON
                  || nRet == NDK_ERR_RFID_AFON
                  || nRet == NDK_ERR_RFID_BFON) {//多卡冲突
            return MULTI_CARD;

        }else if (nRet == NDK_ERR_MI_QUIT){ //按了取消键
            return QUIT;
        } else if (nRet == NDK_ERR_FELICA_COLLISION) {
            return MULTI_FELICA;
        }
        return nRet;
    }

    if (pOutType != NULL){
        *pOutType = nclType;
    }

    if (nPiccType & RF_TYPE_F && nOutBufLen > 0 && pstReadCardParm->cardResult != NULL) {
        // F 卡需要在寻卡的时候返回 idmpmm
        LOGD_FMT(">>> idmpmmLen: %d", nOutBufLen);
        setContactlessCardResult(pstReadCardParm->cardResult, nOutBufLen, outBuf);
    }
    return SUCC;
}

void setContactlessCardResult(jobject resultObj, int nOutBufLen, uchar *outBuf) {
    JNIEnv *env = NULL;
    jboolean isAttached = JNI_FALSE;

    int ret = (*gJavaVM)->GetEnv(gJavaVM,(void **) &env, JNI_VERSION_1_4);
    if(ret < 0 ) {
        ret =  (*gJavaVM)->AttachCurrentThread(gJavaVM,(JNIEnv **) &env, NULL);
        if (ret < 0) {
            LOGE_FMT(">>>AttachCurrentThread error.");
            return;
        }
        isAttached = JNI_TRUE;
    }

    jclass resultCls = (*env)->GetObjectClass(env, resultObj);
    jobject contactlessCardInfo = (*env)->GetObjectField(env, resultObj, (*env)->GetFieldID(env, resultCls, "contactlessResult", "Lcom/newland/nsdk/core/internal/cardreader/ContactlessResult;"));
    jclass  class_contactlessCardInfo = (*env)->GetObjectClass(env, contactlessCardInfo);

    (*env)->SetIntField(env,contactlessCardInfo,(*env)->GetFieldID(env,class_contactlessCardInfo, "idmpmmLen", "I"),nOutBufLen);

    jbyteArray idmpmmData = (jbyteArray)(*env)->GetObjectField(env, contactlessCardInfo, (*env)->GetFieldID(env,class_contactlessCardInfo, "idmpmm", "[B"));
    (*env)->SetByteArrayRegion(env, idmpmmData, 0, nOutBufLen, outBuf);
    (*env)->DeleteLocalRef(env, idmpmmData);

    if (isAttached){
        (*gJavaVM)->DetachCurrentThread(gJavaVM);
    }
}

int PubRFActivate(int cardType, STACTIVATERESULT *pResult){
    int nRet = 0;
    int nLen = 0;
    int nInfoLen = 0;
    int nIndex = 0;
    uchar ucPiccType = 0;
    char szRsep[512] = {0};
    uchar uszInfobuf[1024] = {0};
    uchar szSak[10] = {0};

    switch (cardType){
        case RF_CPU:
            nRet = NDK_RfidPiccActivateWithInfo(&ucPiccType,  &nLen,  (uchar *)szRsep, &nInfoLen, uszInfobuf);
            LOGD_FMT(">>> PubRFActivate, NDK_RfidPiccActivateWithInfo nRet=%d, nInfoLen=%d, ucPiccType=0x%02X", nRet, nInfoLen, ucPiccType);
            if(nRet != NDK_OK){
                if( nRet == NDK_ERR_RFID_UPED  ){
                    LOGD_FMT("NDK_RfidPiccActivate->NDK_ERR_RFID_UPED SUCC");
                    // CPU 卡如果重复发指令，可能会导致出错，所以底层固件会维护一个状态，如果是已经上电过了，就不再上电了。
                }else if(nRet == NDK_ERR_RFID_AANTI
                         || nRet == NDK_ERR_RFID_ABON
                         || nRet == NDK_ERR_RFID_MULTICARD){
                    return MULTI_CARD;
                }
                return nRet;
            }
            break;
        case RF_M0:
        case RF_M1:
            nRet = NDK_MifareActiveWithInfo(0, szRsep, &nLen, szSak, &nInfoLen, uszInfobuf);
            LOGD_FMT(">>> PubRFActivate, NDK_MifareActiveWithInfo nRet=%d, nInfoLen=%d", nRet, nInfoLen);
            if (nRet != NDK_OK){
                // TODO
                return nRet;
            }
            break;
        case RF_FELICA:
        default:
        LOGD_FMT("unknown Activate cardType=0x%02X", cardType);
            return SUCC;
    }
    Result:
    LOGD_FMT("Activate SUCC");
    LOGD_STR(">>> PubRFActivate, uid:", szRsep, nLen);
    LOGD_STR(">>> PubRFActivate, Infobuf:", uszInfobuf, nInfoLen);
    //UID
    nIndex = 0;
    if (uszInfobuf[nIndex] == 0) {
        // 3652 的设备上（如 N910 A10）获取的 info 没有 uid，要使用接口出参返回的 uid
        pResult->nUidLen = nLen;
        memcpy(pResult->szUID, szRsep, pResult->nUidLen);
        nIndex += 1;
    } else {
        pResult->nUidLen = uszInfobuf[nIndex];
        nIndex += 1;
        memcpy(pResult->szUID, uszInfobuf+nIndex, pResult->nUidLen);
        nIndex += pResult->nUidLen;
    }

    //ATQA
    pResult->nAtqaLen  = uszInfobuf[nIndex];
    nIndex += 1;
    memcpy(pResult->szAtqa, uszInfobuf+nIndex, pResult->nAtqaLen);
    nIndex += pResult->nAtqaLen;

    //ATS
    pResult->nAtsLen = uszInfobuf[nIndex];
    nIndex += 1;
    memcpy(pResult->szAts, uszInfobuf+nIndex, pResult->nAtsLen);
    nIndex += pResult->nAtsLen;

    //ATQB
    pResult->nAtqblen = uszInfobuf[nIndex];
    nIndex += 1;
    memcpy(pResult->szAtqb, uszInfobuf+nIndex, pResult->nAtqblen);
    nIndex += pResult->nAtqblen;

    //SAK
    pResult->nSakLen = uszInfobuf[nIndex];
    nIndex += 1;
    memcpy(pResult->szSak, uszInfobuf+nIndex, pResult->nSakLen);
    nIndex += pResult->nSakLen;

    return SUCC;
}

int PubRFActivate2(STACTIVATERESULT *pResult){
    int nRet = 0;
    int nLen = 0;
    int nInfoLen = 0;
    int nIndex = 0;
    uchar ucPiccType = 0;
    char szRsep[512] = {0};
    uchar uszInfobuf[1024] = {0};
    uchar szSak[10] = {0};
    uchar UID[16] = {0};
    int uidLen = 0;

    nRet = NDK_MifareActive(1, UID, &uidLen, szSak);
    LOGD_FMT(">>> PubRFActivate, NDK_MifareActive nRet=%d", nRet);
    if (nRet != NDK_OK){
        // TODO
        return nRet;
    }

    Result:
    LOGD_FMT("Activate SUCC");
    LOGD_STR(">>> PubRFActivate, uid:", szRsep, nLen);
    LOGD_STR(">>> PubRFActivate, Infobuf:", uszInfobuf, nInfoLen);

    LOGD_FMT("uidLen[%d]", uidLen);
    pResult->nUidLen = uidLen;
    memcpy(pResult->szUID, UID, pResult->nUidLen);
    LOGD_FMT("nSakLen[%d]", sizeof(szSak));
    pResult->nSakLen = sizeof(szSak);
    memcpy(pResult->szSak, szSak, pResult->nSakLen);
    pResult->nAtqaLen = 0;
    pResult->nAtsLen = 0;
    pResult->nAtqblen = 0;

    return SUCC;
}

int PubRFDetectLpcd(const STREADCARDPARAM *streadcardparam, int *cardType, int *state) {
    int nState = 0;
    int nRet = NDK_RfidLpcdGetState(&nState);
    *state = nState;
    LOGD_FMT("NDK_RfidLpcdGetState ret[%d], state[%d]", nRet, nState);
    if (nRet != NDK_OK) {
        return nRet;
    }
    if (nState == 2){
        NDK_RfidLpcdStopDetect();
        PubRFOpen();
        //为了保证 Apple Wallet 弹出所需要的两次连续 WUPA，四次寻卡是为了提高成功率
        for (int i = 0; i < 4; i++) {
            nRet = PubRFDetectCard(streadcardparam, cardType);
            if (nRet == NDK_OK) {
                break;
            }
            usleep(100000);
        }
    }
    return nRet;
}



