
#include <string.h>
#include <stdlib.h>
#include "threadtool.h"
#include "cardmgr.h"
#include "log.h"
#include "comm.h"
#include "api.h"
#include "nllogger.h"
#include "card.h"
#include "readeric.h"
#include "readerrfid.h"
#include "cardmgr.h"
#include "time.h"

extern JavaVM *gJavaVM;

static int gnIsCancelReadCard = 0;
static int gnIsReadingCard = 0;
static int gnIsLpcd = 0;
int cardEventNum = -1;
pthread_mutex_t mutex;
pthread_cond_t cond = PTHREAD_COND_INITIALIZER;

static void setMagResult(jobject resultObj,int nIsCheckTrack, uchar *pTrackstatus,  uchar *pTrack1, uchar *pTrack2, uchar *pTrack3);
static void setMagResult2(jobject resultObj, int nIsCheckTrack, uchar *pTrackFormats, uchar *pTrackStatus, uchar *pTrack1, uchar *pTrack2, uchar *pTrack3, uchar *pTrack4, uchar *pTrack5, uchar *pTrack6);

int GetICType(int icCardSlot, int icCardType,  char *outICType){

    if ((icCardType != 0) && (icCardSlot > 3)) {
        return NDK_ERR_PARA;
    }
    switch (icCardSlot) {
        case 0:
            switch (icCardType) {
                case 0x00:
                case 0x0b: // 7816 卡因为历史遗留原因，不能用 ICTYPE_ISO7816 这个类型上电，要用 ICTYPE_IC 这个上电，否则就会出现上电返回 -5 错误
                    *outICType = ICTYPE_IC;
                    break;
                case 0x05:
                    *outICType = ICTYPE_M_1;
                    break;
                case 0x06:
                    *outICType = ICTYPE_M_2;
                    break;
                case 0x07:
                    *outICType = ICTYPE_M_3;
                    break;
                case 0x08:
                    *outICType = ICTYPE_M_4;
                    break;
                case 0x09:
                    *outICType = ICTYPE_M_5;
                    break;
                case 0x0a:
                    *outICType = ICTYPE_M_6;
                    break;
                case 0x0c:
                    *outICType = ICTYPE_M_7;
                    break;
                case 0x0d:
                    *outICType = ICTYPE_M_1_1;
                    break;
                case 0x0e:
                    *outICType = ICTYPE_M_1_2;
                    break;
                case 0x0f:
                    *outICType = ICTYPE_M_1_4;
                    break;
                case 0x10:
                    *outICType = ICTYPE_M_1_8;
                    break;
                case 0x11:
                    *outICType = ICTYPE_M_1_16;
                    break;
                case 0x12:
                    *outICType = ICTYPE_M_1_32;
                    break;
                case 0x13:
                    *outICType = ICTYPE_M_1_64;
                    break;
                default:
                    return NDK_ERR_PARA;
            }
            break;
        case 1:
            *outICType = ICTYPE_IC_2;
            break;
        case 2:
            *outICType = ICTYPE_SAM1;
            break;
        case 3:
            *outICType = ICTYPE_SAM2;
            break;
        case 4:
            *outICType = ICTYPE_SAM3;
            break;
        default:
            return NDK_ERR_PARA;
    }

    return NDK_OK;
}

static int ProReadCard(const STREADCARDPARAM *pstReadCardParm, int *pnInputType, int *pnCLType, jboolean isLpcd){

    int nRet = 0;
    uchar ucSwiped;
    int nIcState=0;
    int nErrorCode;
    int nRFCardType = 0;
    int nState = 0;
    char pchTk[TRACKNUM][BUFMAXLEN];
    char szTk1[300] = {0};
    char szTk2[300] = {0};
    char szTk3[300] = {0};
    char szTrackStatus[6+1] = {0};
    memset(szTrackStatus, 0x02, sizeof(szTrackStatus));
    struct timespec startTime, currentTime;
    //Dual Msr
    mag_doublecard_t magDoublecard[2];
    memset(magDoublecard, 0x00, sizeof(magDoublecard));
    int track1Type = 0xFF;
    int track2Type = 0xFF;
    char channel1[3][156];
    char channel2[3][156];
    memset(channel1, 0, sizeof(channel1));
    memset(channel2, 0, sizeof(channel2));
    char szTrackFormats[6];
    memset(szTrackFormats, 0xFF, sizeof(szTrackFormats));

    clock_gettime(CLOCK_MONOTONIC, &startTime);
    int pollingTimes = 2;
    while (1){
        if (isLpcd && pollingTimes == 0) {
            nRet = NDK_RfidLpcdStartDetect();
            if (nRet != NDK_OK) {
                return nRet;
            }
            pollingTimes--;
        }
        clock_gettime(CLOCK_MONOTONIC, &currentTime);
        if (((currentTime.tv_sec - startTime.tv_sec) >= pstReadCardParm->unTimeout )
            && pstReadCardParm->unTimeout != 0 ){
            return TIME_OUT;
        }

        if (gnIsCancelReadCard == 1){
            LOGD_FMT("Cancel read card");
            return  QUIT;
        }

        /**磁条卡*/
        if (HAS_CARD_MAG(pstReadCardParm->cardReadMode)){
            ucSwiped = 0 ;
            nRet = NDK_MagSwiped(&ucSwiped);
            if (nRet != NDK_OK){
                LOGD_FMT("NDK_MagSwiped nRet=%d,ucSwiped=%d", nRet, ucSwiped);
                return FAIL;
            }
            if (ucSwiped){
                nRet = NDK_MagReadCards(magDoublecard);
                LOGD_FMT("NDK_MagReadCards ret[%d]", nRet);
                if (nRet == -9999) {
                    memset(pchTk, 0, sizeof(pchTk));
                    nRet = NDK_MagReadNormal(pchTk[0], pchTk[1], pchTk[2], &nErrorCode);
                    LOGD_FMT("NDK_MagReadNormal nRet=%d,nErrorCode=%d", nRet, nErrorCode);
                    if (nRet != NDK_OK){
                        return FAIL;
                    }

                    if (pstReadCardParm->unIsVerifyTrack == 1){
                        nRet = DealTrack(pchTk, nErrorCode, szTrackStatus, szTk1, szTk2, szTk3);
                        if (nRet == SUCC) {
                            memset(szTrackFormats, 0x00, 3);
                        }
                    } else {
                        nRet = DealTrackWithoutVerify(pchTk, szTrackStatus, szTk1, szTk2, szTk3);
                    }
                    if( nRet != SUCC ){
                        return  nRet;
                    }

                    setMagResult2(pstReadCardParm->cardResult, pstReadCardParm->unIsVerifyTrack, szTrackFormats, szTrackStatus, szTk1, szTk2, szTk3, NULL, NULL, NULL);
                    *pnInputType = CARD_MAG;
                    return SUCC;
                } else if (nRet != 0) {
                    return nRet;
                } else {
                    track1Type = magDoublecard[0].card_flag;
                    track2Type = magDoublecard[1].card_flag;
                    memcpy(channel1, magDoublecard[0].channel, sizeof(magDoublecard[0].channel));
                    memcpy(channel2, magDoublecard[1].channel, sizeof(magDoublecard[1].channel));
                    LOGD_FMT("track1Type[%d], track2Type[%d], pnErrorCode1[%d], pnErrorCode2[%d]", track1Type, track2Type, magDoublecard[0].pnErrorCode, magDoublecard[1].pnErrorCode);
                    //双磁道都有数据
                    if (track1Type != 0xFF && track2Type != 0xFF) {
                        memset(szTrackFormats, track1Type, 3);
                        szTrackFormats[3] = track2Type;
                        szTrackFormats[4] = track2Type;
                        szTrackFormats[5] = track2Type;

                        if (track1Type == 0x01 && track2Type == 0x01) {
                            if (pstReadCardParm-> unIsVerifyTrack == 1) {
                                return MGR_FORMAT_ERROR;
                            }
                            memset(szTrackStatus, TRACK_STATUS_OK, 6);
                            setMagResult2(pstReadCardParm->cardResult, pstReadCardParm-> unIsVerifyTrack, szTrackFormats, szTrackStatus, channel1[0], channel1[1], channel1[2], channel2[0], channel2[1], channel2[2]);
                        } else {
                            if (track1Type == 0x00) {
                                if (pstReadCardParm-> unIsVerifyTrack == 1) {
                                    nRet = DealTrack(channel1, magDoublecard[0].pnErrorCode, szTrackStatus, szTk1, szTk2, szTk3);
                                } else {
                                    nRet = DealTrackWithoutVerify(channel1, szTrackStatus, szTk1, szTk2, szTk3);
                                }
                                if (magDoublecard[1].pnErrorCode != 0) {
                                    szTrackStatus[3] = TRACK_STATUS_ERROR;
                                    szTrackStatus[4] = TRACK_STATUS_ERROR;
                                    szTrackStatus[5] = TRACK_STATUS_ERROR;
                                    setMagResult2(pstReadCardParm->cardResult, pstReadCardParm->unIsVerifyTrack, szTrackFormats, szTrackStatus, szTk1, szTk2, szTk3, NULL, NULL, NULL);
                                } else {
                                    szTrackStatus[3] = TRACK_STATUS_OK;
                                    szTrackStatus[4] = TRACK_STATUS_OK;
                                    szTrackStatus[5] = TRACK_STATUS_OK;
                                    setMagResult2(pstReadCardParm-> cardResult, pstReadCardParm-> unIsVerifyTrack, szTrackFormats, szTrackStatus, szTk1, szTk2, szTk3, channel2[0], channel2[1], channel2[2]);
                                }
                            } else if (track2Type == 0x00) {
                                if (pstReadCardParm-> unIsVerifyTrack == 1) {
                                    nRet = DealTrack(channel2, magDoublecard[1].pnErrorCode, szTrackStatus, szTk1, szTk2, szTk3);
                                } else {
                                    nRet = DealTrackWithoutVerify(channel2, szTrackStatus, szTk1, szTk2, szTk3);
                                }
                                if (magDoublecard[0].pnErrorCode != 0x00) {
                                    szTrackStatus[3] = TRACK_STATUS_ERROR;
                                    szTrackStatus[4] = TRACK_STATUS_ERROR;
                                    szTrackStatus[5] = TRACK_STATUS_ERROR;
                                    setMagResult2(pstReadCardParm->cardResult, pstReadCardParm->unIsVerifyTrack, szTrackFormats, szTrackStatus, szTk1, szTk2, szTk3, NULL, NULL, NULL);
                                } else {
                                    szTrackStatus[3] = TRACK_STATUS_OK;
                                    szTrackStatus[4] = TRACK_STATUS_OK;
                                    szTrackStatus[5] = TRACK_STATUS_OK;
                                    setMagResult2(pstReadCardParm-> cardResult, pstReadCardParm-> unIsVerifyTrack, szTrackFormats, szTrackStatus, szTk1, szTk2, szTk3, channel1[0], channel1[1], channel1[2]);
                                }
                            } else {
                                return MGR_FORMAT_ERROR;
                            }
                        }
                    } else if (track1Type == 0xFF && track2Type == 0xFF) {
                        return -32;
                    } else if (track1Type != 0xFF) {
                        memset(szTrackFormats, track1Type, 3);
                        if (track1Type == 0x00) {
                            if (pstReadCardParm->unIsVerifyTrack == 1) {
                                nRet = DealTrack(channel1, magDoublecard[0].pnErrorCode, szTrackStatus, szTk1, szTk2, szTk3);
                            } else {
                                nRet = DealTrackWithoutVerify(channel1, szTrackStatus, szTk1, szTk2, szTk3);
                            }
                            if (nRet != SUCC) {
                                return nRet;
                            }
                        } else if (track1Type == 0x01) {
                            if (pstReadCardParm->unIsVerifyTrack == 1) {
                                return -32;
                            }
                            memcpy(szTk1, magDoublecard[0].channel[0], sizeof(magDoublecard[0].channel[0]));
                            memcpy(szTk2, magDoublecard[0].channel[1], sizeof(magDoublecard[0].channel[1]));
                            memcpy(szTk3, magDoublecard[0].channel[2], sizeof(magDoublecard[0].channel[2]));
                            memset(szTrackStatus, TRACK_STATUS_OK, 3);
                        }
                        if (magDoublecard[1].pnErrorCode != 0) {
                            szTrackStatus[3] = TRACK_STATUS_ERROR;
                            szTrackStatus[4] = TRACK_STATUS_ERROR;
                            szTrackStatus[5] = TRACK_STATUS_ERROR;
                        }
                        setMagResult2(pstReadCardParm->cardResult, pstReadCardParm->unIsVerifyTrack, szTrackFormats, szTrackStatus, szTk1, szTk2, szTk3, NULL, NULL, NULL);
                    } else if (track2Type != 0xFF) {
                        memset(szTrackFormats, track2Type, 3);
                        LOGD_STR("szTrackFormats", szTrackFormats, 6);
                        if (track2Type == 0x00) {
                            if (pstReadCardParm->unIsVerifyTrack == 1) {
                                nRet = DealTrack(channel2, magDoublecard[1].pnErrorCode, szTrackStatus, szTk1, szTk2, szTk3);
                            } else {
                                nRet = DealTrackWithoutVerify(channel2, szTrackStatus, szTk1, szTk2, szTk3);
                            }

                            if (nRet != SUCC) {
                                return nRet;
                            }
                        } else if (track2Type == 0x01){
                            if (pstReadCardParm->unIsVerifyTrack == 1) {
                                return -32;
                            }
                            memcpy(szTk1, magDoublecard[1].channel[0], sizeof(magDoublecard[1].channel[0]));
                            memcpy(szTk2, magDoublecard[1].channel[1], sizeof(magDoublecard[1].channel[1]));
                            memcpy(szTk3, magDoublecard[1].channel[2], sizeof(magDoublecard[1].channel[2]));
                            memset(szTrackStatus, TRACK_STATUS_OK, 3);
                        }
                        if (magDoublecard[0].pnErrorCode != 0) {
                            szTrackStatus[3] = TRACK_STATUS_ERROR;
                            szTrackStatus[4] = TRACK_STATUS_ERROR;
                            szTrackStatus[5] = TRACK_STATUS_ERROR;
                        }
                        setMagResult2(pstReadCardParm->cardResult, pstReadCardParm->unIsVerifyTrack, szTrackFormats, szTrackStatus, szTk1, szTk2, szTk3, NULL, NULL, NULL);
                    }
                    *pnInputType = CARD_MAG;
                    return SUCC;
                }
            }
        }
        /**IC插卡*/
        if (HAS_CARD_IC(pstReadCardParm->cardReadMode)){
            nRet = PubGetICStatus(&nIcState);
            if( nRet == SUCC && (nIcState&IC1_EXIST)){
                *pnInputType = CARD_IC;
                return SUCC;
            }
        }

        /**非接CPU卡*/
        if (HAS_CARD_RFID(pstReadCardParm->cardReadMode)){
            if (isLpcd) {
                if (pollingTimes > 0) {
                    nRet = PubRFDetectCard(pstReadCardParm, &nRFCardType);
                    if (nRet == NDK_ERR_RFID_UPED){
                        NDK_RfidPiccDeactivate(6);
                    }else if (nRet == MULTI_CARD || nRet == QUIT || nRet == MULTI_FELICA){
                        //multi-card collision
//				continue;
                        return nRet;
                    } else if( nRet == SUCC ){
                        *pnInputType = CARD_RFID;
                        *pnCLType = nRFCardType;
                        return SUCC;
                    }
                    pollingTimes--;
                    usleep(100000);
                } else {
                    nRet = PubRFDetectLpcd(pstReadCardParm, &nRFCardType, &nState);
                    if (nRet < 0) {
                        //未寻到卡片时，需要重新开启 LPCD
                        if (nRet == -2008) {
                            NDK_RfidLpcdStartDetect();
                            continue;
                        }
                        return nRet;
                    }
                    if (nState == 2) {
                        *pnInputType = CARD_RFID;
                        *pnCLType = nRFCardType;
                        return SUCC;
                    }
                }
            } else {
                nRet = PubRFDetectCard(pstReadCardParm, &nRFCardType);
                if (nRet == NDK_ERR_RFID_UPED){
                    NDK_RfidPiccDeactivate(6);
                }else if (nRet == MULTI_CARD || nRet == QUIT || nRet == MULTI_FELICA){
                    //multi-card collision
//				continue;
                    return nRet;
                } else if( nRet == SUCC ){
                    *pnInputType = CARD_RFID;
                    *pnCLType = nRFCardType;
                    return SUCC;
                }
                pollingTimes--;
            }
        }

        //delay 释放CPU资源,避免死锁
        usleep(20);
    }
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_openCardReader(JNIEnv *env, jobject thiz,
                                                                 jint read_mode,
                                                                 jint contactless_card_types,
                                                                 jboolean isVerifyTrack,
                                                                 jint timeout,
                                                                 jbyteArray paramTypeF,
                                                                 jint lenParamTypeF,
                                                                 jbyteArray paramTypeV,
                                                                 jint lenParamTypeV,
                                                                 jobject result,
                                                                 jboolean is_lpcd) {
    int nRet = 0;
    int nInputType = 0;
    int nCLCardType = 0;
    STREADCARDPARAM stReadCardParm;

    jclass resultCls = (*env)->GetObjectClass(env, result);
    jfieldID id_cardInterface = (*env)->GetFieldID(env, resultCls, "cardInterface", "B");
    jfieldID id_contactlessCardType = (*env)->GetFieldID(env, resultCls, "contactlessCardType", "B");

    memset(&stReadCardParm, 0, sizeof(STREADCARDPARAM));

    LOGD_FMT("read_mode=0x%02X", read_mode);
    LOGD_FMT("contactless_card_types=0x%02X", contactless_card_types);
    LOGD_FMT("timeout=%d", timeout);

    stReadCardParm.cardReadMode = read_mode;
    stReadCardParm.clCardType = contactless_card_types;
    stReadCardParm.unTimeout = timeout;
    stReadCardParm.cardResult = result;
    stReadCardParm.unLenParamTypeF = lenParamTypeF;
    if (stReadCardParm.unLenParamTypeF > 0){
        stReadCardParm.pParamTypeF = (*env)->GetByteArrayElements(env, paramTypeF, NULL);
    }

    stReadCardParm.unLenParamTypeV = lenParamTypeV;
    if (stReadCardParm.unLenParamTypeV > 0){
        stReadCardParm.pParamTypeV = (*env)->GetByteArrayElements(env, paramTypeV, NULL);
    }

    if (isVerifyTrack){
        stReadCardParm.unIsVerifyTrack = 1;
    }
    LOGD_FMT("unIsVerifyTrack=%d", stReadCardParm.unIsVerifyTrack);

    gnIsReadingCard = 1;

    if (HAS_CARD_MAG(stReadCardParm.cardReadMode)){
        NDK_MagOpen();
    }
    if (HAS_CARD_IC(stReadCardParm.cardReadMode)){
        //NDK_IccSetType(ICTYPE_IC);
        NDK_IccPowerDown(ICTYPE_IC);
    }
    if (HAS_CARD_RFID(stReadCardParm.cardReadMode)){
        PubRFOpen();
    }
    if (is_lpcd) {
        NDK_RfidLpcdStopDetect();
    }

    nRet = ProReadCard(&stReadCardParm, &nInputType, &nCLCardType, is_lpcd);

    if (stReadCardParm.unLenParamTypeF > 0){
        (*env)->ReleaseByteArrayElements(env, paramTypeF, stReadCardParm.pParamTypeF , 0);
    }

    if (stReadCardParm.unLenParamTypeV > 0){
        (*env)->ReleaseByteArrayElements(env, paramTypeV, stReadCardParm.pParamTypeV , 0);
    }

    if (HAS_CARD_MAG(stReadCardParm.cardReadMode)){
        NDK_MagClose();
    }
    if (is_lpcd && (nInputType != CARD_RFID)) {
        NDK_RfidLpcdStopDetect();
    }
    if(HAS_CARD_RFID(stReadCardParm.cardReadMode) && (nInputType != CARD_RFID)){
        PubRFPowerDown();
    }

    if (nRet != SUCC){
        gnIsReadingCard = 0;
        return nRet;
    }

    (*env)->SetByteField(env, result, id_cardInterface, nInputType);
    if (nInputType == CARD_RFID){
        (*env)->SetByteField(env, result, id_contactlessCardType, nCLCardType);
    }
    gnIsReadingCard = 0;
    return SUCC;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_cancelCardReader(JNIEnv *env, jobject thiz) {

    gnIsCancelReadCard = 1;
    pthread_mutex_lock(&mutex);
    cardEventNum = QUIT;
    pthread_cond_signal(&cond);
    pthread_mutex_unlock(&mutex);
    while (gnIsReadingCard == 1){
        usleep(300);
    }
    return 0;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_closeCardReader(JNIEnv *env, jobject thiz) {
    int nRet = 0;
    nRet = NDK_MagClose();
    LOGD_FMT("NDK_MagClose nRet=%d", nRet);
    if (nRet != 0 && nRet != -1) {
        // 磁卡已经关闭的情况再关闭的话会返回 -1，如果是返回其他错误码，则是关闭失败
        return nRet;
    }

    nRet = NDK_RfidCloseRf();
    LOGD_FMT("NDK_RfidCloseRf nRet=%d", nRet);
    if (nRet != 0) {
        // 非接已经关闭的情况再关闭的话也是返回 0 的，只要是返回错误码，就是关闭失败
        return nRet;
    }

    // 接触的没有关闭接口，不用关闭
    return 0;
}

JNIEXPORT void JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_resetCancelFlag(JNIEnv *env, jobject thiz) {
    gnIsCancelReadCard = 0;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_ICCheckSlotsState(JNIEnv *env, jobject thiz,
                                                                        jint slot) {
//    char cICType = 0;
//    int ret = 0;
//
//    LOGD_FMT("slot=%d", slot);
//    LOGD_FMT("card_type=%d", card_type);
//
//    ret = GetICType(slot, card_type, &cICType);
//    if (ret != NDK_OK){
//        return ret;
//    }
//    LOGD_FMT("cICType=%d", cICType);

    //TODO 这里只检测IC卡槽是否有卡，SAM卡槽目前不处理
    char cStatus = 0;

    int nRet = PubGetICStatus(&cStatus);
    if (nRet == SUCC){
        if ((slot == 0 && (cStatus & IC1_EXIST)) || (slot == 1 && (cStatus & IC2_EXIST))) {
            return SUCC;
        }
    }
    return FAIL;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_ICPowerUp(JNIEnv *env, jobject thiz, jint slot,
                                                                jint card_type, jbyteArray atr,
                                                                jintArray atr_len) {
    char cICType = 0;
    int ret = 0;
    int nAtrLen = 0;
    uchar uszAtr[512+1] = {0};

    LOGD_FMT("slot=%d", slot);
    LOGD_FMT("card_type=%d", card_type);

    ret = GetICType(slot, card_type, &cICType);
    if (ret != NDK_OK){
        return ret;
    }

    LOGD_FMT("cICType=%d", cICType);
    ret = NDK_IccPowerUp(cICType, uszAtr, &nAtrLen);
    LOGD_FMT("NDK_IccPowerUp, ret=%d", ret);
    if (ret == NDK_OK){
        (*env)->SetByteArrayRegion(env, atr,0, nAtrLen, uszAtr);
        (*env)->SetIntArrayRegion(env,atr_len,0, 1, &nAtrLen);
    }
    return ret;


}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_ICPowerDown(JNIEnv *env, jobject thiz, jint slot,
                                                                  jint card_type) {
    char cICType = 0;
    int ret = 0;

    LOGD_FMT("slot=%d", slot);
    LOGD_FMT("card_type=%d", card_type);

    ret = GetICType(slot, card_type, &cICType);
    if (ret != NDK_OK){
        return ret;
    }

    LOGD_FMT("cICType=%d", cICType);
    ret = NDK_IccPowerDown(cICType);
    LOGD_FMT("ret=%d", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_ICPerformAPDU(JNIEnv *env, jobject thiz,
                                                                    jint slot, jint card_type,
                                                                    jbyteArray command, jint commandLen,
                                                                    jbyteArray recv,
                                                                    jintArray len) {

    char cICType = 0;
    int nRet = 0;
    int nLenTemp = 0;
    char* pszCommandTemp = NULL;
    char szResponseTemp[8192+1] = {0};

    LOGD_FMT("slot=%d", slot);
    LOGD_FMT("card_type=%d", card_type);

    nRet = GetICType(slot, card_type, &cICType);
    if (nRet != NDK_OK){
        return nRet;
    }
    LOGD_FMT("cICType=%d", cICType);

    pszCommandTemp = (*env)->GetByteArrayElements(env, command, NULL);
//    LOGD_STR("NDK_Iccrw:", (uchar *)pszCommandTemp, commandLen);
    nRet = NDK_Iccrw(cICType, commandLen, pszCommandTemp, &nLenTemp, szResponseTemp);
    LOGD_FMT("  NDK_Iccrw  nRet=%d, nLen=%d", nRet, nLenTemp);
    if (nRet != NDK_OK){
        LOGD_FMT("nRet=%d, pnLen=%d", nRet, nLenTemp);
        return nRet;
    }

    (*env)->SetIntArrayRegion(env, len, 0, 1, &nLenTemp);
    (*env)->SetByteArrayRegion(env, recv, 0, nLenTemp, szResponseTemp);

    (*env)->ReleaseByteArrayElements(env, command, pszCommandTemp, 0);

    return nRet;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_RFActivate(JNIEnv *env, jobject thiz,
                                                             jint card_type,
                                                             jobject jni_activation_result) {
    int ret = 0;
    STACTIVATERESULT stResult;
    jobject obj_tmp;

    LOGD_FMT("card_type=%d", card_type);

    //TODO 缓存jfieldID 提升效率
    jclass resultCls = (*env)->GetObjectClass(env, jni_activation_result);
    jfieldID id_uid = (*env)->GetFieldID(env, resultCls, "uid", "[B");
    jfieldID id_atqa = (*env)->GetFieldID(env, resultCls, "atqa", "[B");
    jfieldID id_ats = (*env)->GetFieldID(env, resultCls, "ats", "[B");
    jfieldID id_atqb = (*env)->GetFieldID(env, resultCls, "atqb", "[B");
    jfieldID id_sak = (*env)->GetFieldID(env, resultCls, "sak", "[B");
    jfieldID id_len_uid = (*env)->GetFieldID(env, resultCls, "uidLen", "I");
    jfieldID id_len_atqa = (*env)->GetFieldID(env, resultCls, "atqaLen", "I");
    jfieldID id_len_ats = (*env)->GetFieldID(env, resultCls, "atsLen", "I");
    jfieldID id_len_atqb = (*env)->GetFieldID(env, resultCls, "atqbLen", "I");
    jfieldID id_len_sak = (*env)->GetFieldID(env, resultCls, "sakLen", "I");

    memset(&stResult, 0, sizeof(STACTIVATERESULT));
    ret = PubRFActivate(card_type, &stResult);
    if (ret < 0) {
        return ret;
    }
    LOGD_STR("UID:",  stResult.szUID, stResult.nUidLen);
    LOGD_STR("ATQA:",  stResult.szAtqa, stResult.nAtqaLen);
    LOGD_STR("ATS:",  stResult.szAts, stResult.nAtsLen);
    LOGD_STR("ATQB:",  stResult.szAtqb, stResult.nAtqblen);
    LOGD_STR("SAK:",  stResult.szSak, stResult.nSakLen);

    // TODO 太多SetxxxField影响效率？？
    LOGD_FMT("Set Activate Result->start");
    (*env)->SetIntField(env, jni_activation_result, id_len_uid, stResult.nUidLen);
    (*env)->SetIntField(env, jni_activation_result, id_len_atqa, stResult.nAtqaLen);
    (*env)->SetIntField(env, jni_activation_result, id_len_ats, stResult.nAtsLen);
    (*env)->SetIntField(env, jni_activation_result, id_len_atqb, stResult.nAtqblen);
    (*env)->SetIntField(env, jni_activation_result, id_len_sak, stResult.nSakLen);
    if (stResult.nUidLen > 0){
        obj_tmp = (*env)->GetObjectField(env, jni_activation_result, id_uid);
        (*env)->SetByteArrayRegion(env, obj_tmp, 0, stResult.nUidLen, stResult.szUID);
    }
    if (stResult.nAtqaLen > 0){
        obj_tmp = (*env)->GetObjectField(env, jni_activation_result, id_atqa);
        (*env)->SetByteArrayRegion(env, obj_tmp, 0, stResult.nAtqaLen, stResult.szAtqa);
    }
    if (stResult.nAtsLen > 0){
        obj_tmp = (*env)->GetObjectField(env, jni_activation_result, id_ats);
        (*env)->SetByteArrayRegion(env, obj_tmp, 0, stResult.nAtsLen, stResult.szAts);
    }
    if (stResult.nAtqblen > 0){
        obj_tmp = (*env)->GetObjectField(env, jni_activation_result, id_atqb);
        (*env)->SetByteArrayRegion(env, obj_tmp, 0, stResult.nAtqblen, stResult.szAtqb);
    }
    if (stResult.nSakLen > 0){
        obj_tmp = (*env)->GetObjectField(env, jni_activation_result, id_sak);
        (*env)->SetByteArrayRegion(env, obj_tmp, 0, stResult.nSakLen, stResult.szSak);
    }
    LOGD_FMT("Set Activate Result->End");
    return SUCC;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_RFDeactivate(JNIEnv *env, jobject thiz) {
    return PubRFPowerDown();
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_RFIsCardPresent(JNIEnv *env, jobject thiz, jint cl_type) {

    int i = 0;
    int nRet = 0;
    STREADCARDPARAM stReadCardParm;
    memset(&stReadCardParm, 0, sizeof(STREADCARDPARAM));

    if (cl_type > 0) {
        stReadCardParm.clCardType = cl_type;
        nRet = PubRFDetectCard(&stReadCardParm, NULL);
        LOGD_FMT("PubRFDetectCard nRet=%d", nRet);
        if (nRet == NDK_ERR_RFID_UPED){
            NDK_RfidPiccDeactivate(0);
        }else if(nRet == SUCC || nRet == MULTI_CARD){
            return SUCC;
        }
    } else {
        stReadCardParm.clCardType = RF_TYPE_A | RF_TYPE_B | RF_TYPE_F | RF_TYPE_V;
        for (i = 0; i < 3; i++){
            nRet = PubRFDetectCard(&stReadCardParm, NULL);
            LOGD_FMT("PubRFDetectCard nRet=%d", nRet);
            if (nRet == NDK_ERR_RFID_UPED){
                NDK_RfidPiccDeactivate(0);
            }else if(nRet == SUCC || nRet == MULTI_CARD){
                return SUCC;
            }
            NDK_SysMsDelay(50);
        }
    }
    return FAIL;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_RFPerformAPDU(JNIEnv *env, jobject thiz,
                                                                    jbyteArray command, jint commandLen,
                                                                    jbyteArray recv, jintArray recv_len) {
    int nRet = 0;
    int nLenTemp = 0;
    char* pszCommandTemp = NULL;
    char szResponseTemp[8192+1] = {0};

    pszCommandTemp = (*env)->GetByteArrayElements(env, command, NULL);
    nRet = PubRFCPUComm(pszCommandTemp, commandLen, szResponseTemp, &nLenTemp);
    LOGD_FMT("nRet=%d, pnLen=%d", nRet, nLenTemp);
    if (nRet != NDK_OK){
        return nRet;
    }

    (*env)->SetIntArrayRegion(env, recv_len, 0, 1, &nLenTemp);
    (*env)->SetByteArrayRegion(env, recv, 0, nLenTemp, szResponseTemp);

    (*env)->ReleaseByteArrayElements(env, command, pszCommandTemp, 0);
    return 0;

}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_RFGetVersion(JNIEnv *env, jobject thiz, jint n_len,
                                                                   jbyteArray version_buf) {
    uchar outBuf[n_len];
    memset(outBuf, 0, sizeof(outBuf));
    int ret = NDK_RfidVersion(outBuf);
    if (ret == 0) {
        (*env)->SetByteArrayRegion(env, version_buf, 0, n_len, outBuf);
    }
    LOGD_FMT(">>>NDK_RfidVersion ret[%d] version[%s]", ret, outBuf);
    return ret;
}

static void setMagResult(jobject resultObj,int nIsCheckTrack, uchar *pTrackstatus,  uchar *pTrack1, uchar *pTrack2, uchar *pTrack3){
    JNIEnv *env = NULL;
    jboolean isAttached = JNI_FALSE;
    int nLen = 0;
    int i = 0;
    int j = 0;
    char szPan[19+1] = {0};
    char szExpDate[4+1] = {0};
    char szServiceCode[3+1] = {0};

    int ret = (*gJavaVM)->GetEnv(gJavaVM,(void **) &env, JNI_VERSION_1_4);
    if(ret < 0 ) {
        ret =  (*gJavaVM)->AttachCurrentThread(gJavaVM,(JNIEnv **) &env, NULL);
        if (ret < 0) {
            LOGE_FMT(">>>AttachCurrentThread error.");
            return;
        }
        isAttached = JNI_TRUE;
    }

    if (nIsCheckTrack == 1){
        if (pTrack2[0] != 0){
            GetDataFromTrack2(szPan, szExpDate, szServiceCode, pTrack2);

        } else if (pTrack1[0] != 0) {
            GetDataFromTrack1(szPan, szExpDate, szServiceCode, NULL, pTrack1);
        }
    }


    jclass resultCls = (*env)->GetObjectClass(env, resultObj);

    jobject magResult = (*env)->GetObjectField(env, resultObj,(*env)->GetFieldID(env, resultCls, "magResult", "Lcom/newland/nsdk/core/internal/cardreader/MagResult;"));
    jclass  class_magResult = (*env)->GetObjectClass(env, magResult);


    //   uchar hash[20];
    //   EXEC_NDK("NDK_AlgSHA1",NDK_AlgSHA1((unsigned char*)pan, strlen((char*) pan), hash),NDK_OK);//sha
    //   jbyteArray accountHash = (jbyteArray)(*env)->GetObjectField(env, magResult, (*env)->GetFieldID(env,class_magResult, "accountHash", "[B"));
    //   (*env)->SetByteArrayRegion(env, accountHash, 0, nLen, hash);
    //   (*env)->DeleteLocalRef(env, accountHash);

    //   (*env)->SetByteField(env,magResult,(*env)->GetFieldID(env,class_magResult, "trackIndicatingbit", "B"),nReadTrackMode);

    jbyteArray trackStatus = (jbyteArray)(*env)->GetObjectField(env, magResult, (*env)->GetFieldID(env,class_magResult, "trackStatus", "[B"));
    (*env)->SetByteArrayRegion(env, trackStatus, 0, 3, pTrackstatus);
    (*env)->DeleteLocalRef(env, trackStatus);

    //   if (nReadTrackMode & MAG_TK1) {
    nLen = strlen((char *) pTrack1);
    (*env)->SetByteField(env,magResult,(*env)->GetFieldID(env,class_magResult, "firstTrackLen", "B"),nLen);
    if (nLen > 0){
        jbyteArray firstTrackData = (jbyteArray)(*env)->GetObjectField(env, magResult, (*env)->GetFieldID(env,class_magResult, "firstTrackData", "[B"));
        (*env)->SetByteArrayRegion(env, firstTrackData, 0, nLen, pTrack1);
        (*env)->DeleteLocalRef(env, firstTrackData);
    }
    //   }
    //   if (nReadTrackMode & MAG_TK2) {
    nLen = strlen((char *) pTrack2);
    (*env)->SetByteField(env,magResult,(*env)->GetFieldID(env,class_magResult, "secondTrackLen", "B"),nLen);
    if (nLen > 0 ){
        jbyteArray secondTrackData = (jbyteArray)(*env)->GetObjectField(env, magResult, (*env)->GetFieldID(env,class_magResult, "secondTrackData", "[B"));
        (*env)->SetByteArrayRegion(env, secondTrackData, 0, nLen, pTrack2);
        (*env)->DeleteLocalRef(env, secondTrackData);
    }
    //   }
    //   if (nReadTrackMode & MAG_TK3) {
    nLen = strlen((char *) pTrack3);
    (*env)->SetByteField(env,magResult,(*env)->GetFieldID(env,class_magResult, "thirdTrackLen", "B"),nLen);
    if (nLen > 0){
        jbyteArray thirdTrackData = (jbyteArray)(*env)->GetObjectField(env, magResult, (*env)->GetFieldID(env,class_magResult, "thirdTrackData", "[B"));
        (*env)->SetByteArrayRegion(env, thirdTrackData, 0, nLen, pTrack3);
        (*env)->DeleteLocalRef(env, thirdTrackData);
    }
    //   }

    if (nIsCheckTrack == 1){

        nLen = strlen((char *)szPan);

        (*env)->SetByteField(env,magResult, (*env)->GetFieldID(env, class_magResult, "accountLen", "B"),nLen);

        jbyteArray account = (jbyteArray)(*env)->GetObjectField(env, magResult, (*env)->GetFieldID(env,class_magResult, "account", "[B"));
        (*env)->SetByteArrayRegion(env, account, 0, nLen, szPan);
        (*env)->DeleteLocalRef(env, account);


        jbyteArray validDate = (jbyteArray)(*env)->GetObjectField(env, magResult, (*env)->GetFieldID(env,class_magResult, "validDate", "[B"));
        (*env)->SetByteArrayRegion(env, validDate, 0, 4, szExpDate);
        (*env)->DeleteLocalRef(env, validDate);

        jbyteArray serviceCode = (jbyteArray)(*env)->GetObjectField(env, magResult, (*env)->GetFieldID(env,class_magResult, "serviceCode", "[B"));
        (*env)->SetByteArrayRegion(env, serviceCode, 0, 3, szServiceCode);
        (*env)->DeleteLocalRef(env, serviceCode);
    }

    if (isAttached){
        (*gJavaVM)->DetachCurrentThread(gJavaVM);
    }
}

//用于再次校验磁道数据是否为 0x7E，即磁道为空的状态码
void checkTrackStatus(uchar *pTrackStatus, uchar **pTrack1,  uchar **pTrack2, uchar **pTrack3, uchar **pTrack4, uchar **pTrack5, uchar **pTrack6) {
    if (*pTrack1 != NULL && strlen(*pTrack1) == 1 && (*pTrack1)[0] == 0x7E) {
        pTrackStatus[0] = TRACK_STATUS_EMPTY;
        *pTrack1 = NULL;
    }
    if (*pTrack2 != NULL && strlen(*pTrack2) == 1 && (*pTrack2)[0] == 0x7E) {
        pTrackStatus[1] = TRACK_STATUS_EMPTY;
        *pTrack2 = NULL;
    }
    if (*pTrack3 != NULL && strlen(*pTrack3) == 1 && (*pTrack3)[0] == 0x7E) {
        pTrackStatus[2] = TRACK_STATUS_EMPTY;
        *pTrack3 = NULL;
    }
    if (*pTrack4 != NULL && strlen(*pTrack4) == 1 && (*pTrack4)[0] == 0x7E) {
        pTrackStatus[3] = TRACK_STATUS_EMPTY;
        *pTrack4 = NULL;
    }
    if (*pTrack5 != NULL && strlen(*pTrack5) == 1 && (*pTrack5)[0] == 0x7E) {
        pTrackStatus[4] = TRACK_STATUS_EMPTY;
        *pTrack5 = NULL;
    }
    if (*pTrack6 != NULL && strlen(*pTrack6) == 1 && (*pTrack6)[0] == 0x7E) {
        pTrackStatus[5] = TRACK_STATUS_EMPTY;
        *pTrack6 = NULL;
    }
}

static void setMagResult2(jobject resultObj, int nIsCheckTrack, uchar *pTrackFormats, uchar *pTrackStatus, uchar *pTrack1, uchar *pTrack2, uchar *pTrack3, uchar *pTrack4, uchar *pTrack5, uchar *pTrack6) {
    JNIEnv *env = NULL;
    jboolean isAttached = JNI_FALSE;
    int nLen = 0;
    int i = 0;
    int j = 0;
    char szPan[19+1] = {0};
    char szExpDate[4+1] = {0};
    char szServiceCode[3+1] = {0};
    char szTrackStatus[6] = {0};
    memcpy(szTrackStatus, pTrackStatus, 6);
    int ret = (*gJavaVM)->GetEnv(gJavaVM,(void **) &env, JNI_VERSION_1_4);
    if(ret < 0 ) {
        ret =  (*gJavaVM)->AttachCurrentThread(gJavaVM,(JNIEnv **) &env, NULL);
        if (ret < 0) {
            LOGE_FMT(">>>AttachCurrentThread error.");
            return;
        }
        isAttached = JNI_TRUE;
    }

    checkTrackStatus(pTrackStatus, &pTrack1, &pTrack2, &pTrack3, &pTrack4, &pTrack5, &pTrack6);


    if (nIsCheckTrack == 1){
        if (pTrack2[0] != 0){
            GetDataFromTrack2(szPan, szExpDate, szServiceCode, pTrack2);

        } else if (pTrack1[0] != 0) {
            GetDataFromTrack1(szPan, szExpDate, szServiceCode, NULL, pTrack1);
        }
    }


    jclass resultCls = (*env)->GetObjectClass(env, resultObj);

    jobject magResult = (*env)->GetObjectField(env, resultObj,(*env)->GetFieldID(env, resultCls, "magResult", "Lcom/newland/nsdk/core/internal/cardreader/MagResult;"));
    jclass  class_magResult = (*env)->GetObjectClass(env, magResult);


    jbyteArray trackStatus = (jbyteArray)(*env)->GetObjectField(env, magResult, (*env)->GetFieldID(env,class_magResult, "trackStatus", "[B"));


    jbyteArray trackFormats = (jbyteArray)(*env)->GetObjectField(env, magResult, (*env)->GetFieldID(env, class_magResult, "trackFormats", "[B"));

    //   if (nReadTrackMode & MAG_TK1) {
    if (pTrackStatus[0] == TRACK_STATUS_OK) {
        nLen = strlen((char *) pTrack1);
        (*env)->SetByteField(env,magResult,(*env)->GetFieldID(env,class_magResult, "firstTrackLen", "B"),nLen);
        if (nLen > 0){
            jbyteArray firstTrackData = (jbyteArray)(*env)->GetObjectField(env, magResult, (*env)->GetFieldID(env,class_magResult, "firstTrackData", "[B"));
            (*env)->SetByteArrayRegion(env, firstTrackData, 0, nLen, pTrack1);
            (*env)->DeleteLocalRef(env, firstTrackData);
        } else {
            szTrackStatus[0] =TRACK_STATUS_EMPTY;
        }
    }
    //   }
    //   if (nReadTrackMode & MAG_TK2) {
    if (pTrackStatus[1] == TRACK_STATUS_OK) {
        nLen = strlen((char *) pTrack2);
        (*env)->SetByteField(env,magResult,(*env)->GetFieldID(env,class_magResult, "secondTrackLen", "B"),nLen);
        if (nLen > 0 ){
            jbyteArray secondTrackData = (jbyteArray)(*env)->GetObjectField(env, magResult, (*env)->GetFieldID(env,class_magResult, "secondTrackData", "[B"));
            (*env)->SetByteArrayRegion(env, secondTrackData, 0, nLen, pTrack2);
            (*env)->DeleteLocalRef(env, secondTrackData);
        } else {
            szTrackStatus[1] = TRACK_STATUS_EMPTY;
        }
    }

    //   }
    //   if (nReadTrackMode & MAG_TK3) {
    if (pTrackStatus[2] == TRACK_STATUS_OK) {
        nLen = strlen((char *) pTrack3);
        (*env)->SetByteField(env,magResult,(*env)->GetFieldID(env,class_magResult, "thirdTrackLen", "B"),nLen);
        if (nLen > 0){
            jbyteArray thirdTrackData = (jbyteArray)(*env)->GetObjectField(env, magResult, (*env)->GetFieldID(env,class_magResult, "thirdTrackData", "[B"));
            (*env)->SetByteArrayRegion(env, thirdTrackData, 0, nLen, pTrack3);
            (*env)->DeleteLocalRef(env, thirdTrackData);
        } else {
            szTrackStatus[2] = TRACK_STATUS_EMPTY;
        }
    }


    if (pTrack4 != NULL) {
        nLen = strlen((char *) pTrack4);
        (*env)->SetByteField(env, magResult, (*env)->GetFieldID(env, class_magResult, "fourthTrackLen", "B"), nLen);
        if (nLen > 0) {
            jbyteArray fourthTrackData = (jbyteArray) (*env)->GetObjectField(env, magResult, (*env)->GetFieldID(env, class_magResult, "fourthTrackData", "[B"));
            (*env)->SetByteArrayRegion(env, fourthTrackData, 0, nLen, pTrack4);
            (*env)->DeleteLocalRef(env, fourthTrackData);
        } else {
            pTrackFormats[3] = 0xFF;
        }
    } else {
        szTrackStatus[3] = TRACK_STATUS_EMPTY;
    }

    if (pTrack5 != NULL) {
        nLen = strlen((char *) pTrack5);
        (*env)->SetByteField(env, magResult, (*env)->GetFieldID(env, class_magResult, "fifthTrackLen", "B"), nLen);
        if (nLen > 0) {
            jbyteArray fifthTrackData = (jbyteArray) (*env)->GetObjectField(env, magResult, (*env)->GetFieldID(env, class_magResult, "fifthTrackData", "[B"));
            (*env)->SetByteArrayRegion(env, fifthTrackData, 0, nLen, pTrack5);
            (*env)->DeleteLocalRef(env, fifthTrackData);
        } else {
            pTrackFormats[4] = 0xFF;
        }
    } else {
        szTrackStatus[4] = TRACK_STATUS_EMPTY;
    }

    if (pTrack6 != NULL) {
        nLen = strlen((char *) pTrack6);
        (*env)->SetByteField(env, magResult, (*env)->GetFieldID(env, class_magResult, "sixthTrackLen", "B"), nLen);
        if (nLen > 0) {
            jbyteArray sixthTrackData = (jbyteArray) (*env)->GetObjectField(env, magResult, (*env)->GetFieldID(env, class_magResult, "sixthTrackData", "[B"));
            (*env)->SetByteArrayRegion(env, sixthTrackData, 0, nLen, pTrack6);
            (*env)->DeleteLocalRef(env, sixthTrackData);
        } else {
            pTrackFormats[5] = 0xFF;
        }
    } else {
        szTrackStatus[5] = TRACK_STATUS_EMPTY;
    }

    //   }

    if (nIsCheckTrack == 1){

        nLen = strlen((char *)szPan);

        (*env)->SetByteField(env,magResult, (*env)->GetFieldID(env, class_magResult, "accountLen", "B"),nLen);

        jbyteArray account = (jbyteArray)(*env)->GetObjectField(env, magResult, (*env)->GetFieldID(env,class_magResult, "account", "[B"));
        (*env)->SetByteArrayRegion(env, account, 0, nLen, szPan);
        (*env)->DeleteLocalRef(env, account);


        jbyteArray validDate = (jbyteArray)(*env)->GetObjectField(env, magResult, (*env)->GetFieldID(env,class_magResult, "validDate", "[B"));
        (*env)->SetByteArrayRegion(env, validDate, 0, 4, szExpDate);
        (*env)->DeleteLocalRef(env, validDate);

        jbyteArray serviceCode = (jbyteArray)(*env)->GetObjectField(env, magResult, (*env)->GetFieldID(env,class_magResult, "serviceCode", "[B"));
        (*env)->SetByteArrayRegion(env, serviceCode, 0, 3, szServiceCode);
        (*env)->DeleteLocalRef(env, serviceCode);
    }


    (*env)->SetByteArrayRegion(env, trackFormats, 0, 6, pTrackFormats);
    (*env)->DeleteLocalRef(env, trackFormats);
    (*env)->SetByteArrayRegion(env, trackStatus, 0, 6, pTrackStatus);
    (*env)->DeleteLocalRef(env, trackStatus);
    if (isAttached){
        (*gJavaVM)->DetachCurrentThread(gJavaVM);
    }
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_displayRfidLogo(JNIEnv *env, jobject thiz,
                                                                jboolean is_displayed) {
    // TODO: implement displayRfidLogo()
    int onoff = 0;
    if (is_displayed) {
        onoff = 1;
    } else {
        onoff = 0;
    }
    int ret = NDK_RfidLogoDisplay(onoff);
    LOGD_FMT(">>>NDK_RfidLogoDisplay ret = %d", ret)
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_RFActivate2(JNIEnv *env, jobject thiz,
                                                            jobject jni_activation_result) {
    int ret = 0;
    STACTIVATERESULT stResult;
    jobject obj_tmp;


    jclass resultCls = (*env)->GetObjectClass(env, jni_activation_result);
    jfieldID id_uid = (*env)->GetFieldID(env, resultCls, "uid", "[B");
    jfieldID id_atqa = (*env)->GetFieldID(env, resultCls, "atqa", "[B");
    jfieldID id_ats = (*env)->GetFieldID(env, resultCls, "ats", "[B");
    jfieldID id_atqb = (*env)->GetFieldID(env, resultCls, "atqb", "[B");
    jfieldID id_sak = (*env)->GetFieldID(env, resultCls, "sak", "[B");
    jfieldID id_len_uid = (*env)->GetFieldID(env, resultCls, "uidLen", "I");
    jfieldID id_len_atqa = (*env)->GetFieldID(env, resultCls, "atqaLen", "I");
    jfieldID id_len_ats = (*env)->GetFieldID(env, resultCls, "atsLen", "I");
    jfieldID id_len_atqb = (*env)->GetFieldID(env, resultCls, "atqbLen", "I");
    jfieldID id_len_sak = (*env)->GetFieldID(env, resultCls, "sakLen", "I");

    memset(&stResult, 0, sizeof(STACTIVATERESULT));
    ret = PubRFActivate2(&stResult);
    if (ret < 0) {
        return ret;
    }
    LOGD_STR("UID:",  stResult.szUID, stResult.nUidLen);
    LOGD_STR("ATQA:",  stResult.szAtqa, stResult.nAtqaLen);
    LOGD_STR("ATS:",  stResult.szAts, stResult.nAtsLen);
    LOGD_STR("ATQB:",  stResult.szAtqb, stResult.nAtqblen);
    LOGD_STR("SAK:",  stResult.szSak, stResult.nSakLen);

    // TODO 太多SetxxxField影响效率？？
    LOGD_FMT("Set Activate Result->start");
    (*env)->SetIntField(env, jni_activation_result, id_len_uid, stResult.nUidLen);
    (*env)->SetIntField(env, jni_activation_result, id_len_atqa, stResult.nAtqaLen);
    (*env)->SetIntField(env, jni_activation_result, id_len_ats, stResult.nAtsLen);
    (*env)->SetIntField(env, jni_activation_result, id_len_atqb, stResult.nAtqblen);
    (*env)->SetIntField(env, jni_activation_result, id_len_sak, stResult.nSakLen);
    if (stResult.nUidLen > 0){
        obj_tmp = (*env)->GetObjectField(env, jni_activation_result, id_uid);
        (*env)->SetByteArrayRegion(env, obj_tmp, 0, stResult.nUidLen, stResult.szUID);
    }
    if (stResult.nAtqaLen > 0){
        obj_tmp = (*env)->GetObjectField(env, jni_activation_result, id_atqa);
        (*env)->SetByteArrayRegion(env, obj_tmp, 0, stResult.nAtqaLen, stResult.szAtqa);
    }
    if (stResult.nAtsLen > 0){
        obj_tmp = (*env)->GetObjectField(env, jni_activation_result, id_ats);
        (*env)->SetByteArrayRegion(env, obj_tmp, 0, stResult.nAtsLen, stResult.szAts);
    }
    if (stResult.nAtqblen > 0){
        obj_tmp = (*env)->GetObjectField(env, jni_activation_result, id_atqb);
        (*env)->SetByteArrayRegion(env, obj_tmp, 0, stResult.nAtqblen, stResult.szAtqb);
    }
    if (stResult.nSakLen > 0){
        obj_tmp = (*env)->GetObjectField(env, jni_activation_result, id_sak);
        (*env)->SetByteArrayRegion(env, obj_tmp, 0, stResult.nSakLen, stResult.szSak);
    }
    LOGD_FMT("Set Activate Result->End");
    return SUCC;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_RFPerformRats(JNIEnv *env, jobject thiz,
                                                              jbyteArray data, jintArray data_len) {
    unsigned int len = 0;
    unsigned char Data[256];
    memset(Data, 0x00, sizeof(Data));
    int ret = NDK_RfidTypeARats(1, &len, Data);
    LOGD_FMT("NDK_RfidTypeARats[%d], len[%d]", ret, len);
    if (ret == 0) {
        (*env)->SetIntArrayRegion(env, data_len, 0, 1, &len);
        (*env)->SetByteArrayRegion(env, data, 0, len, Data);
    }
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_RFOn(JNIEnv *env, jobject thiz) {
    int ret = PubRFOpen();
    LOGD_FMT("PubRFOpen ret[%d]", ret);
    return ret;
}


JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_RFClose(JNIEnv *env, jobject thiz) {
    int ret = NDK_RfidCloseRf();
    LOGD_FMT("NDK_RfidCloseRf ret[%d]", ret);
    return ret;
}

static int ProReadCard2(const STREADCARDPARAM *pstReadCardParm, int *pnInputType, int *pnCLType){

    int nRet = 0;
    uchar ucSwiped;
    int nIcState=0;
    int nErrorCode;
    int nRFCardType = 0;
    char pchTk[TRACKNUM][BUFMAXLEN];
    char szTk1[300] = {0};
    char szTk2[300] = {0};
    char szTk3[300] = {0};
    char szTrackStatus[3+1] = {0};
    struct timespec startTime, currentTime;

    clock_gettime(CLOCK_MONOTONIC, &startTime);
    while (1){
        clock_gettime(CLOCK_MONOTONIC, &currentTime);
        if (((currentTime.tv_sec - startTime.tv_sec) >= pstReadCardParm->unTimeout )
            && pstReadCardParm->unTimeout != 0 ){
            return TIME_OUT;
        }

        if (gnIsCancelReadCard == 1){
            LOGD_FMT("Cancel read card");
            return  QUIT;
        }

        /**磁条卡*/
        if (HAS_CARD_MAG(pstReadCardParm->cardReadMode)){
            ucSwiped = 0 ;
            nRet = NDK_MagSwiped(&ucSwiped);
            if (nRet != NDK_OK){
                LOGD_FMT("NDK_MagSwiped nRet=%d,ucSwiped=%d", nRet, ucSwiped);
                return FAIL;
            }
            if (ucSwiped){
                memset(pchTk, 0, sizeof(pchTk));
                nRet = NDK_MagReadNormal(pchTk[0], pchTk[1], pchTk[2], &nErrorCode);
                LOGD_FMT("NDK_MagReadNormal nRet=%d,nErrorCode=%d", nRet, nErrorCode);
                if (nRet != NDK_OK){
                    return FAIL;
                }

                if (pstReadCardParm->unIsVerifyTrack == 1){
                    nRet = DealTrack(pchTk, nErrorCode, szTrackStatus, szTk1, szTk2, szTk3);
                } else {
                    nRet = DealTrackWithoutVerify(pchTk, szTrackStatus, szTk1, szTk2, szTk3);
                }
                if( nRet != SUCC ){
                    return  nRet;
                }

                setMagResult(pstReadCardParm->cardResult, pstReadCardParm->unIsVerifyTrack, szTrackStatus, szTk1, szTk2, szTk3);
                *pnInputType = CARD_MAG;
                return SUCC;
            }
        }
        /**IC插卡*/
        if (HAS_CARD_IC(pstReadCardParm-> cardReadMode) && HAS_CARD_IC_2(pstReadCardParm->cardReadMode)) {
            nRet = PubGetICStatus(&nIcState);
            if (nRet == SUCC) {
                if (((nIcState&IC1_EXIST) == IC1_EXIST) && ((nIcState&IC2_EXIST) == IC2_EXIST)) {
                    *pnInputType = (CARD_IC | CARD_IC_2);
                    return SUCC;
                } else if ((nIcState&IC1_EXIST) == IC1_EXIST) {
                    *pnInputType = CARD_IC;
                    return SUCC;
                } else if ((nIcState&IC2_EXIST) == IC2_EXIST) {
                    *pnInputType = CARD_IC_2;
                    return SUCC;
                }
            }
        } else if (HAS_CARD_IC(pstReadCardParm->cardReadMode)){
            nRet = PubGetICStatus(&nIcState);
            if( nRet == SUCC && ((nIcState&IC1_EXIST) == IC1_EXIST)){
                *pnInputType = CARD_IC;
                return SUCC;
            }
        } else if (HAS_CARD_IC_2(pstReadCardParm->cardReadMode)) {
            nRet = PubGetICStatus(&nIcState);
            if (nRet == SUCC && ((nIcState&IC2_EXIST) == IC2_EXIST)) {
                *pnInputType = CARD_IC_2;
                return SUCC;
            }
        }


        /**非接CPU卡*/
        if (HAS_CARD_RFID(pstReadCardParm->cardReadMode)){

            nRet = PubRFDetectCard(pstReadCardParm, &nRFCardType);
            if (nRet == NDK_ERR_RFID_UPED){
                NDK_RfidPiccDeactivate(6);
            }else if (nRet == MULTI_CARD || nRet == QUIT || nRet == MULTI_FELICA){
                //multi-card collision
//				continue;
                return nRet;
            } else if( nRet == SUCC ){
                *pnInputType = CARD_RFID;
                *pnCLType = nRFCardType;
                return SUCC;
            }
        }
        //delay 释放CPU资源,避免死锁
        usleep(20);
    }
}


JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_openCardReader2(JNIEnv *env, jobject thiz,
                                                                jint read_mode,
                                                                jint contactless_card_types,
                                                                jboolean is_verify_track,
                                                                jint timeout,
                                                                jbyteArray param_type_f,
                                                                jint len_param_type_f,
                                                                jbyteArray param_type_v,
                                                                jint len_param_type_v,
                                                                jobject result) {
    int nRet = 0;
    int nInputType = 0;
    int nCLCardType = 0;
    STREADCARDPARAM stReadCardParm;

    jclass resultCls = (*env)->GetObjectClass(env, result);
    jfieldID id_cardInterface = (*env)->GetFieldID(env, resultCls, "cardInterface", "B");
    jfieldID id_contactlessCardType = (*env)->GetFieldID(env, resultCls, "contactlessCardType", "B");

    memset(&stReadCardParm, 0, sizeof(STREADCARDPARAM));

    LOGD_FMT("read_mode=0x%02X", read_mode);
    LOGD_FMT("contactless_card_types=0x%02X", contactless_card_types);
    LOGD_FMT("timeout=%d", timeout);

    stReadCardParm.cardReadMode = read_mode;
    stReadCardParm.clCardType = contactless_card_types;
    stReadCardParm.unTimeout = timeout;
    stReadCardParm.cardResult = result;
    stReadCardParm.unLenParamTypeF = len_param_type_f;
    if (stReadCardParm.unLenParamTypeF > 0){
        stReadCardParm.pParamTypeF = (*env)->GetByteArrayElements(env, param_type_f, NULL);
    }

    stReadCardParm.unLenParamTypeV = len_param_type_v;
    if (stReadCardParm.unLenParamTypeV > 0){
        stReadCardParm.pParamTypeV = (*env)->GetByteArrayElements(env, param_type_v, NULL);
    }

    if (is_verify_track){
        stReadCardParm.unIsVerifyTrack = 1;
    }
    LOGD_FMT("unIsVerifyTrack=%d", stReadCardParm.unIsVerifyTrack);

    gnIsReadingCard = 1;

    if (HAS_CARD_MAG(stReadCardParm.cardReadMode)){
        NDK_MagOpen();
    }
    if (HAS_CARD_IC(stReadCardParm.cardReadMode)){
        //NDK_IccSetType(ICTYPE_IC);
        NDK_IccPowerDown(ICTYPE_IC);
    }
    if(HAS_CARD_IC_2(stReadCardParm.cardReadMode)) {
        NDK_IccPowerDown(ICTYPE_IC_2);
    }
    if (HAS_CARD_RFID(stReadCardParm.cardReadMode)){
        PubRFOpen();
    }

    nRet = ProReadCard2(&stReadCardParm, &nInputType, &nCLCardType);
    LOGD_FMT("proReadCard2 ret = %d", nRet);

    if (stReadCardParm.unLenParamTypeF > 0){
        (*env)->ReleaseByteArrayElements(env, param_type_f, stReadCardParm.pParamTypeF , 0);
    }

    if (stReadCardParm.unLenParamTypeV > 0){
        (*env)->ReleaseByteArrayElements(env, param_type_v, stReadCardParm.pParamTypeV , 0);
    }

    if (HAS_CARD_MAG(stReadCardParm.cardReadMode)){
        NDK_MagClose();
    }
    if(HAS_CARD_RFID(stReadCardParm.cardReadMode) && (nInputType != CARD_RFID)){
        PubRFPowerDown();
    }

    if (nRet != SUCC){
        gnIsReadingCard = 0;
        return nRet;
    }

    (*env)->SetByteField(env, result, id_cardInterface, nInputType);
    if (nInputType == CARD_RFID){
        (*env)->SetByteField(env, result, id_contactlessCardType, nCLCardType);
    }
    gnIsReadingCard = 0;
    return SUCC;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_RFFelicaPollingWithTimeout(JNIEnv *env,
                                                                           jobject thiz,
                                                                           jbyteArray system_code,
                                                                           jbyte request_code,
                                                                           jbyte timeslot,
                                                                           jbyteArray receive_data,
                                                                           jintArray receive_data_len,
                                                                           jint timeout) {
    int nRet = -1;
    int receiveDataLen = 0;
    unsigned char receiveData[1024] = {0};
    struct timeval startTime, currentTime;
    gettimeofday(&startTime, NULL);
    long nStartTime = startTime.tv_sec * 1000000 + startTime.tv_usec;
    felica_param_t felicaParam;
    memset(&felicaParam, 0x00, sizeof(felicaParam));
    felicaParam.timeslot = timeslot;
    felicaParam.request_code = request_code;
    unsigned char *systemCode = (*env)->GetByteArrayElements(env, system_code, NULL);
    memcpy(felicaParam.systemcode, systemCode, 2);
    while (1) {
        gettimeofday(&currentTime, NULL);
        long nCurrentTime = currentTime.tv_sec * 1000000 + currentTime.tv_usec;
        if ((nCurrentTime - nStartTime) > (timeout * 1000)) {
            nRet = TIME_OUT;
            break;
        }
        nRet = NDK_FelicaPoll(felicaParam, receiveData, &receiveDataLen);
        if (nRet == NDK_OK) {
            break;
        }
    }
    if (nRet == NDK_OK) {
        (*env)->SetIntArrayRegion(env, receive_data_len, 0, 1, &receiveDataLen);
        (*env)->SetByteArrayRegion(env, receive_data, 0, receiveDataLen, receiveData);
    }

    (*env)->ReleaseByteArrayElements(env, system_code, systemCode, 0);
    return nRet;

}

static NotifyEvent notifyRFIDEvent(EM_SYS_EVENT eventNum, int msgLen, char *msg) {
    LOGD_FMT("eventNum[%d]", eventNum);
    if (eventNum == SYS_EVENT_RFID || eventNum == SYS_EVENT_NONE) {
        pthread_mutex_lock(&mutex);
        cardEventNum = eventNum;
        pthread_cond_signal(&cond);
        pthread_mutex_unlock(&mutex);
    }
}

static NotifyEvent notifyMAGEvent(EM_SYS_EVENT eventNum, int msgLen, char *msg) {
    LOGD_FMT("eventNum[%d]", eventNum);
    if (eventNum == SYS_EVENT_MAGCARD || eventNum == SYS_EVENT_NONE) {
        pthread_mutex_lock(&mutex);
        cardEventNum = eventNum;
        pthread_cond_signal(&cond);
        pthread_mutex_unlock(&mutex);
    }
}

static NotifyEvent notifyICEvent(EM_SYS_EVENT eventNum, int msgLen, char *msg) {
    LOGD_FMT("eventNum[%d]", eventNum);
    if (eventNum == SYS_EVENT_ICCARD || eventNum == SYS_EVENT_NONE) {
        pthread_mutex_lock(&mutex);
        cardEventNum = eventNum;
        pthread_cond_signal(&cond);
        pthread_mutex_unlock(&mutex);
    }
}

static void unregisterEvent(int readMode) {
    if (HAS_CARD_RFID(readMode)) {
        NDK_SYS_UnRegisterEvent(SYS_EVENT_RFID);
    }
    if (HAS_CARD_MAG(readMode)) {
        NDK_SYS_UnRegisterEvent(SYS_EVENT_MAGCARD);
    }
    if (HAS_CARD_IC(readMode) || HAS_CARD_IC_2(readMode)) {
        NDK_SYS_UnRegisterEvent(SYS_EVENT_ICCARD);
    }
}

static void registerEvent(int readMode, int timeoutMs) {
    if (HAS_CARD_RFID(readMode)) {
        NDK_SYS_RegisterEvent(SYS_EVENT_RFID, timeoutMs, notifyRFIDEvent);
    }
    if (HAS_CARD_MAG(readMode)) {
        NDK_SYS_RegisterEvent(SYS_EVENT_MAGCARD, timeoutMs, notifyMAGEvent);
    }
    if (HAS_CARD_IC(readMode) || HAS_CARD_IC_2(readMode)) {
        NDK_SYS_RegisterEvent(SYS_EVENT_ICCARD, timeoutMs, notifyICEvent);
    }
}

static void setCardInterfaceResult(JNIEnv *env, jobject result, jbyte cardInterface, jbyte contactlessCardType) {
    jclass resultCls = (*env)->FindClass(env, "com/newland/nsdk/core/internal/cardreader/CardReaderResult");
    jfieldID fid_cardInterface = (*env)->GetFieldID(env, resultCls, "cardInterface", "B");
    jfieldID fid_contactlessCardType = (*env)->GetFieldID(env, resultCls, "contactlessCardType", "B");
    (*env)->SetByteField(env, result, fid_cardInterface, cardInterface);
    if (contactlessCardType != 0x00) {
        (*env)->SetByteField(env, result, fid_contactlessCardType, contactlessCardType);
    }
    (*env)->DeleteLocalRef(env, resultCls);
}

static int read_mag(JNIEnv *env, STREADCARDPARAM streadcardparam) {
    int nSwiped = 0;
    int nErrorCode = 0;
    int ret = 0;
    char pszTk[TRACKNUM][BUFMAXLEN];
    char szTrack1[300] = {0};
    char szTrack2[300] = {0};
    char szTrack3[300] = {0};
    char szTrackStatus[3+1] = {0};

    memset(pszTk, 0x00, sizeof(pszTk));
    ret = NDK_MagSwiped(&nSwiped);
    LOGD_FMT("nSwiped[%d]", nSwiped);
    if (ret == NDK_OK && nSwiped) {
        memset(pszTk, 0, sizeof(pszTk));
        ret = NDK_MagReadNormal(pszTk[0], pszTk[1], pszTk[2], &nErrorCode);
        if (ret == NDK_OK) {
            if (streadcardparam.unIsVerifyTrack) {
                ret = DealTrack(pszTk, nErrorCode, szTrackStatus, szTrack1, szTrack2, szTrack3);
            } else {
                ret = DealTrackWithoutVerify(pszTk, szTrackStatus, szTrack1, szTrack2, szTrack3);
            }
            if (ret == NDK_OK) {
                setMagResult(streadcardparam.cardResult, streadcardparam.unIsVerifyTrack, szTrackStatus, szTrack1, szTrack2, szTrack3);
                setCardInterfaceResult(env, streadcardparam.cardResult, CARD_MAG, 0x00);
                return SUCC;
            }
        }
    }

    return FAIL;
}

static int read_ic(JNIEnv *env, STREADCARDPARAM streadcardparam) {
    LOGD_FMT(">>> enter read_ic");
    int nIcStatus = 0;
    int ret = 0;
    int readMode = streadcardparam.cardReadMode;
    jobject result = streadcardparam.cardResult;
    LOGD_FMT("readMode[%d]", readMode);
    ret = PubGetICStatus(&nIcStatus);
    LOGD_FMT("nIcStatus[%d]", nIcStatus);
    if (ret == NDK_OK) {
        if (HAS_CARD_IC(readMode) && HAS_CARD_IC_2(readMode)) {
            if (((nIcStatus & IC1_EXIST) == IC1_EXIST) && ((nIcStatus & IC2_EXIST) == IC2_EXIST)) {
                setCardInterfaceResult(env, result, CARD_IC | CARD_IC_2, 0x00);
            } else if ((nIcStatus & IC1_EXIST) == IC1_EXIST) {
                setCardInterfaceResult(env, result, CARD_IC, 0x00);
            } else if ((nIcStatus & IC2_EXIST) == IC2_EXIST) {
                setCardInterfaceResult(env, result, CARD_IC_2, 0x00);
            }
            return SUCC;
        } else if (HAS_CARD_IC(readMode)) {
            if ((nIcStatus & IC1_EXIST) == IC1_EXIST) {
                setCardInterfaceResult(env, result, CARD_IC, 0x00);
                return SUCC;
            }
        } else if (HAS_CARD_IC_2(readMode)) {
            if ((nIcStatus & IC2_EXIST) == IC2_EXIST) {
                setCardInterfaceResult(env, result, CARD_IC_2, 0x00);
                return SUCC;
            }
        }
    }
    return FAIL;
}

static int read_rfid(JNIEnv *env, STREADCARDPARAM streadcardparam) {
    int outType = 0;
    int ret = PubRFDetectCard(&streadcardparam, &outType);
    if (ret == NDK_OK) {
        setCardInterfaceResult(env, streadcardparam.cardResult, CARD_RFID, outType);
        return SUCC;
    }
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_openCardReaderWithCardEvent(JNIEnv *env,
                                                                            jobject thiz,
                                                                            jint read_mode,
                                                                            jint contactless_card_types,
                                                                            jboolean is_verify_track,
                                                                            jint timeout,
                                                                            jbyteArray param_type_f,
                                                                            jint len_param_type_f,
                                                                            jbyteArray param_type_v,
                                                                            jint len_param_type_v,
                                                                            jobject result) {
    int nSwiped = 0;
    int ret = 0;
    char pszTk[TRACKNUM][BUFMAXLEN];
    char szTrack1[300] = {0};
    char szTrack2[300] = {0};
    char szTrack3[300] = {0};
    char szTrackStatus[3+1] = {0};
    int nErrorCode = 0;
    int nLrc = 0;
    int nClType = 0;
    int nIcStatus = 0;
    STREADCARDPARAM streadcardparam;
    memset(&streadcardparam, 0x00, sizeof(STREADCARDPARAM));
    cardEventNum = -1;
    LOGD_FMT(">>> readMode[%d], contactlessType[%d], isVerifyTrack[%d], timeout[%d]", read_mode, contactless_card_types, is_verify_track, timeout);
    gnIsReadingCard = 1;
    streadcardparam.cardReadMode = read_mode;
    streadcardparam.unIsVerifyTrack = is_verify_track;
    streadcardparam.unTimeout = timeout;
    streadcardparam.cardResult = result;
    streadcardparam.clCardType = contactless_card_types;
    streadcardparam.unLenParamTypeV = len_param_type_v;
    if (streadcardparam.unLenParamTypeV > 0) {
        streadcardparam.pParamTypeV = (*env)->GetByteArrayElements(env, param_type_v, 0);
    }
    streadcardparam.unLenParamTypeF = len_param_type_f;
    if (streadcardparam.unLenParamTypeF > 0) {
        streadcardparam.pParamTypeF = (*env)->GetByteArrayElements(env, param_type_f, 0);
    }
    if (HAS_CARD_MAG(read_mode)) {
        NDK_MagClose();
        NDK_MagOpen();
        ret = read_mag(env, streadcardparam);
        LOGD_FMT("read_mag ret[%d]", ret);
        if (ret == SUCC) {
            NDK_MagClose();
            return SUCC;
        }
    }

    ret = read_ic(env, streadcardparam);
    if (ret == NDK_OK) {
        return SUCC;
    }

    if (HAS_CARD_RFID(read_mode)) {
        PubRFOpen();
        ret = PubRFDetectCard(&streadcardparam, &nClType);
        if (ret == NDK_OK) {
            setCardInterfaceResult(env, result, CARD_RFID, nClType);
            return SUCC;
        }
    }

//    NDK_RfidPiccDeactivate(0);
//    NDK_RfidCloseRf();
    //重新设置寻卡类型，保证固件能够成功在轮询事件时寻到卡片
    int piccType = 0;
    bool isContainA = false, isContainB = false, isContainF = false, isContainV = false;
    if ((contactless_card_types & RF_TYPE_A) == RF_TYPE_A) {
        isContainA = true;
    }
    if ((contactless_card_types & RF_TYPE_B) == RF_TYPE_B) {
        isContainB = true;
    }
    if ((contactless_card_types & RF_TYPE_F) == RF_TYPE_F) {
        isContainF = true;
    }

    if (isContainA && isContainB && isContainF) {
        piccType = 0xc8;
    } else if (isContainA && isContainB) {
        piccType = 0xcd;
    } else if (isContainA && isContainF) {
        piccType = 0xca;
    } else if (isContainB && isContainF) {
        piccType = 0xc9;
    } else if (isContainA) {
        piccType = 0xcc;
    } else if (isContainB) {
        piccType = 0xcb;
    } else if (isContainF) {
        piccType = 0xcf;
    }
    NDK_RfidPiccType(piccType);


    //事件机制
    if (cardEventNum == -1) {
        //todo:驱动暂未支持 IC2 事件中断，需要等后续驱动适配后才可以使用 IC2 事件
        registerEvent(streadcardparam.cardReadMode, streadcardparam.unTimeout * 1000);
        pthread_mutex_lock(&mutex);
        while (cardEventNum == -1) {
            pthread_cond_wait(&cond, &mutex);
        }
        pthread_mutex_unlock(&mutex);
    }

    LOGD_FMT("cardEventNum[%d]", cardEventNum);

    switch (cardEventNum) {
        case SYS_EVENT_RFID:
            ret = read_rfid(env, streadcardparam);
            break;
        case SYS_EVENT_MAGCARD:
            ret = read_mag(env, streadcardparam);
            break;
        case SYS_EVENT_ICCARD:
            ret = read_ic(env, streadcardparam);
            break;
        case SYS_EVENT_NONE:
            ret = TIME_OUT;
            break;
        case QUIT:
            ret = QUIT;
            break;
        default:
            break;
    }

    unregisterEvent(streadcardparam.cardReadMode);
    cardEventNum = -1;
    gnIsReadingCard = 0;
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_ICSetConfig(JNIEnv *env, jobject thiz, jint ictype,
                                                            jint cfgtype, jint value) {
    LOGD_FMT(">>> cfgType[%d], value[%d]", cfgtype, value);
    int icType = 0;
    GetICType(ictype, 0x00, &icType);
    LOGD_FMT(">>> icType[%d]", icType);
    int ret = NDK_IccSetConfig(icType, cfgtype, value);
    LOGD_FMT(">>> NDK_IccSetConfig ret[%d]", ret);
    return ret;
}