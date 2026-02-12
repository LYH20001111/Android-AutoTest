#include <unistd.h>
#include "rfid.h"
#include "stdio.h"
#include "readerrfid.h"
#include "cardmgr.h"
#include "NDK.h"
#include "log.h"
#include "card.h"

extern int g_aCardAtq;
extern int g_rfMultiLevel;
#if 0
static int __seekM1(uchar *atq_len, uchar *atq, uchar *snr_len, uchar *snr, uchar *sak_len, uchar *sak) {
    int nAtqLen, nSnrLen, UIDlen = 0;
    unsigned char sel_cmd[3] = {0x93, 0x95, 0x97}, UID[12], i;
    LOGD_FMT("M1Active g_rfMultiLevel[%d]", g_rfMultiLevel)
    if (g_rfMultiLevel == 0) {
        if (!EXEC_NDK("NDK_M1Request", NDK_M1Request(0x52, &nAtqLen, atq), NDK_OK))
            return ACK_ERR;
        if (!EXEC_NDK("NDK_M1Anti", NDK_M1Anti(&nSnrLen, snr), NDK_OK)) return ACK_ERR;
        if (!EXEC_NDK("NDK_M1Select", NDK_M1Select(nSnrLen, snr, sak), NDK_OK)) return ACK_ERR;
        LOGD_STR("SNR", snr, nSnrLen);
        *atq_len = nAtqLen;
        *snr_len = nSnrLen;
        *sak_len = 1;
        return ACK_OK;
    } else {
        if (!EXEC_NDK("NDK_M1Request", NDK_M1Request(0x52, &nAtqLen, atq), NDK_OK))
            return ACK_ERR;
        UIDlen = (atq[0] >> 6) + 1;
        i = 0;
        do {
            if (!EXEC_NDK("NDK_M1Anti_SEL", NDK_M1Anti_SEL(sel_cmd[i], &nSnrLen, snr),
                            NDK_OK))
                return ACK_ERR;
            if (!EXEC_NDK("NDK_M1Select_SEL", NDK_M1Select_SEL(sel_cmd[i], nSnrLen, snr, sak),
                            NDK_OK))
                return ACK_ERR;
            memcpy((UID + (i << 2)), snr, 4);
            LOGD_FMT("sel_cmd[%d][%d] *sak[%d]", i, sel_cmd[i], *sak);
            if ((*sak & 0x04) == 0x00) break;//UID transfer complete
        } while (i++ < 3);

        if ((UIDlen == 1) && (UID[0] == 0x88)) {
            LOGD_FMT("uid error1");
            return ACK_ERR;
        }
        if ((UIDlen == 2) && (UID[4] == 0x88)) {
            LOGD_FMT("uid error2");
            return ACK_ERR;
        }
        switch (UIDlen) {
            case 1:
                UIDlen = 4;
                break;
            case 2:
                UIDlen = 7;
                memcpy(UID, &UID[1], 3);/*first*/
                memcpy(&(UID[3]), &UID[4], 4);
                break;
            case 3:
                UIDlen = 10;
                memcpy(UID, &UID[1], 3);/*first*/
                memcpy(&(UID[3]), &UID[5], 3);
                memcpy(&(UID[6]), &UID[8], 4);
                break;
            default:
                UIDlen = 0;
                UID[0] = 0x00;
                return ACK_ERR;
        }
        LOGD_STR("UID", UID, UIDlen);
        memcpy(snr, UID, UIDlen);
        *atq_len = nAtqLen;
        *snr_len = UIDlen;
        *sak_len = 1;
        return ACK_OK;
    }

}
static int __seekCard(char *psz, int *pnLen, int nIsReadM1, int *pnInputType) {
    LOGD_FMT(">>>nIsReadM1[%d]", nIsReadM1);
    int nRet = 0, nLen = 0;
    uchar ucSnrLen = 0, ucAtqLen = 0, ucSakLen = 0, ucPiccType;
    uchar uszAtq[24] = {0}, uszSak[32] = {0}, uszAts[32] = {0}, snr[64] = {0};

    if (nIsReadM1) {
        if (!EXEC_NDK("NDK_RfidPiccType", NDK_RfidPiccType(0xcc), NDK_OK)) return ACK_ERR;

        if (__seekM1(&ucAtqLen, uszAtq, &ucSnrLen, snr, &ucSakLen, uszSak) != ACK_OK)
            return ACK_ERR;

        if (!EXEC_NDK("NDK_RfidPiccType", NDK_RfidPiccType(0xcd), NDK_OK)) return ACK_ERR;

        LOGD_FMT("ReadM1 __seekM1 nRet[%d] ucAtqLen[%d] ucSnrLen[%d] ucSakLen[%d]", nRet, ucAtqLen,
                 ucSnrLen, ucSakLen);
        LOGD_STR("ReadM1 ATQ", uszAtq, ucAtqLen);
        LOGD_STR("ReadM1 SNR", snr, ucSnrLen);
        LOGD_STR("ReadM1 SAK", uszSak, ucSakLen);

        if (uszSak[0] == 0x20) {
            LOGD_FMT(">>>A CARD EXIST!!!!!");
            int atsLen;
            char atsbuf[32];
            memset(atsbuf, 0, sizeof(atsbuf));
            if (!EXEC_NDK("NDK_RfidTypeARats", NDK_RfidTypeARats(0, &atsLen, atsbuf), NDK_OK))
                return ACK_ERR;
            *pnInputType = 0x14;
        } else {
            LOGD_FMT(">>>M1 CARD EXIST!!!!!");
            *pnInputType = 0x44;
        }
        nLen = (int) ucSnrLen;
        *(psz + nLen) = uszSak[0];
    } else {
        if (!EXEC_NDK("NDK_RfidPiccType", NDK_RfidPiccType(0xcd), NDK_OK)) return ACK_ERR;
        if (!EXEC_NDK("NDK_RfidPiccDetect", NDK_RfidPiccDetect(&ucPiccType), NDK_OK))
            return ACK_ERR;
        if (!EXEC_NDK("NDK_RfidPiccActivate",
                        NDK_RfidPiccActivate(&ucPiccType, &nLen, (uchar *) snr), NDK_OK))
            return ACK_ERR;

        if (0xcc == ucPiccType) {
            LOGD_FMT("A CARD EXIST!!!!!");
            *pnInputType = 0x14;
            *(psz + nLen) = 0x20;
        } else if (0xcb == ucPiccType) {
            LOGD_FMT("B CARD EXIST!!!!!");
            *pnInputType = 0x24;
            *(psz + nLen) = 0x00;
        }
    }
    memcpy(psz, snr, nLen);
    *pnLen = nLen;
    return ACK_OK;
}
#endif
int RfidReader_Open(void *pstCardReaderParam, void *pstCardInfo) {
    if (pstCardReaderParam == NULL || pstCardInfo == NULL) {
        LOGD_FMT(">>>pstCardReaderParam[%d] pstCardInfo[%d] return.", pstCardReaderParam, pstCardInfo);
        ERRMSG(SDK_ERR_PARAM,CARDREADER_OPEN);
        return NL_FAILED;
    }
    RfidReader_LedLt1118Status(1);

    StCardReaderParam *stCardReaderParam = (StCardReaderParam *) pstCardReaderParam;
    StCardInfo *cardInfo = (StCardInfo *) pstCardInfo;
    cardInfo->validLen = 0;

    if (!EXEC_NDK("NDK_RfidInit", NDK_RfidInit(NULL), NDK_OK, CARDREADER_OPEN))
        return NL_FAILED;

    if(stCardReaderParam->searchCardRule == 0x06){
        return RfidReader_Read(stCardReaderParam, cardInfo);
    }else{
        if (!EXEC_NDK("NDK_RfidPiccDeactivate", NDK_RfidPiccDeactivate(10), NDK_OK,CARDREADER_OPEN)) return NL_FAILED;

        if(stCardReaderParam->felicaParamLen != 0){
            if(!EXEC_NDK("NDK_RfidPiccType", NDK_RfidPiccType(NDK_RFID_ABF), NDK_OK,CARDREADER_OPEN)){
                if (!EXEC_NDK("NDK_RfidPiccType", NDK_RfidPiccType(NDK_RFID_AB), NDK_OK,CARDREADER_OPEN)) return NL_FAILED;
            }
        } else{
            if (!EXEC_NDK("NDK_RfidPiccType", NDK_RfidPiccType(NDK_RFID_AB), NDK_OK,CARDREADER_OPEN)) return NL_FAILED;
        }

        int i = 0;
        for (; i < 3; i++) {
            if (EXEC_NDK("NDK_RfidPiccDetect", NDK_RfidPiccDetect(NULL), NDK_OK,CARDREADER_OPEN)) {
                return RfidReader_Read(stCardReaderParam, cardInfo);
            } else {
                LOGD_FMT(">>>No card");
                usleep(10);
            }
        }
        return NL_OK;
    }
}

int RfidReader_Read(void *pstCardReaderParam, void *pstCardInfo) {
    if (pstCardReaderParam == NULL || pstCardInfo == NULL) {
        LOGD_FMT(">>>pstCardReaderParam[%d] pstCardInfo[%d] return.", pstCardReaderParam, pstCardInfo);
        ERRMSG(SDK_ERR_PARAM,CARDREADER_OPEN);
        return NL_FAILED;
    }
    StCardReaderParam *stCardReaderParam = (StCardReaderParam *) pstCardReaderParam;
    StCardInfo *cardInfo = (StCardInfo *) pstCardInfo;
    cardInfo->userInputMode = CARD_RFID;
    cardInfo->validLen = 0;

    int mainType = CARD_RFID,subCardType = 0;
    if (!HAS_CARD_RFID(stCardReaderParam->readCardMode)) {
        LOGD_FMT(">>>HAS_CARD_RFID==0 return.");
        return NL_FAILED;
    }
    if(stCardReaderParam->searchCardRule == 0x03){
        uchar picctype = 0, uid[32];int len = 0,activateCount = 20,ret;
        #if 0
        if(!EXEC_NDK("#NDK_NL_RfidPiccDetect", ret = NDK_NL_RfidPiccDetect(NULL), NDK_OK,CARDREADER_OPEN)){
            if(ret == SDK_ERR_NDK_NOT_SUPPORT){
                if (!EXEC_NDK("#NDK_RfidPiccDetect", NDK_RfidPiccDetect(NULL), NDK_OK,CARDREADER_OPEN)) {
                    return NL_FAILED;
                }
            }
            return NL_FAILED;
        }
        #else
        if (!EXEC_NDK("#NDK_RfidPiccDetect", NDK_RfidPiccDetect(NULL), NDK_OK,CARDREADER_OPEN)) {
            return NL_FAILED;
        }
        #endif

        #if 0
        LOGE_FMT("NDK_RfidPiccActivate START");
        if(!EXEC_NDK("NDK_RfidPiccActivate", NDK_RfidPiccActivate(&picctype, &len,uid), NDK_OK,CARDREADER_OPEN)){
            int i = 0;
            if(!EXEC_NDK("NDK_RfidPiccDeactivate", NDK_RfidPiccDeactivate(10), NDK_OK,CARDREADER_OPEN)) return NL_FAILED;

            if(!EXEC_NDK("NDK_RfidPiccType",NDK_RfidPiccType(NDK_RFID_AB),NDK_OK,CARDREADER_OPEN)) return NL_FAILED;
            for(;i<=activateCount;i++){
                if(!EXEC_NDK("NDK_RfidPiccDetect",NDK_RfidPiccDetect(NULL),NDK_OK,CARDREADER_OPEN))
                    continue;
                if(EXEC_NDK("NDK_RfidPiccActivate", NDK_RfidPiccActivate(&picctype, &len,uid), NDK_OK,CARDREADER_OPEN))
                    break;
                if(i >= activateCount){
                    return NL_RFACTIVATE_FAIL;
                }
            }
        }
        LOGE_FMT("NDK_RfidPiccActivate END");
        #endif

        int offset = 0;
        memcpy(cardInfo->data, &mainType, 1);offset += 1;
        nlMpos_Command.mpos_writelen(cardInfo->data + offset, 0, _VAR_BIT16);offset += 2;
        nlMpos_Command.mpos_writelen(cardInfo->data + offset, 0, _VAR_BIT16);offset += 2;
        memset(cardInfo->data + offset,"\xFF", 1);offset += 1;
        cardInfo->validLen = offset;
        stCardReaderParam->targetCardType = OPENCARD_TYPE_RF;
        return NL_OK;
    }
    if (stCardReaderParam->searchCardRule == 0x01 || stCardReaderParam->searchCardRule == 0x04) {//非接优先
        LOGD_FMT(">>>quick search card.");
        uchar picctype = 0, uid[32];
        int len = 0,i = 0,atqLen = 0,atq[32];
        int activate = -1;
        memset(atq, 0, sizeof(atq));
        LOGE_FMT(">>>RFCARD_QUICKLY_MODE[%d] g_aCardAtq[%d]",stCardReaderParam->searchCardRule,g_aCardAtq);
        for(;i<10;i++){
            if(!EXEC_NDK("NDK_RfidPiccType",NDK_RfidPiccType(NDK_RFID_AB),NDK_OK,CARDREADER_OPEN))
                continue;
            //if(!EXEC_NDK("NDK_RfidPiccDetect",NDK_RfidPiccDetect(NULL),NDK_OK,CARDREADER_OPEN))
            //    continue;
            if (g_aCardAtq == 1) {
                if(!EXEC_NDK("NDK_RfidPiccDetect_Atq", NDK_RfidPiccDetect_Atq(&picctype, &atqLen, atq), NDK_OK,CARDREADER_OPEN)){
                    continue;
                }
            } else {
                if(!EXEC_NDK("NDK_RfidPiccDetect", NDK_RfidPiccDetect(&picctype), NDK_OK,CARDREADER_OPEN)){
                    continue;
                }
                atqLen = 0;
            }
            if(EXEC_NDK("NDK_RfidPiccActivate", activate = NDK_RfidPiccActivate(&picctype, &len,uid), NDK_OK,CARDREADER_OPEN))
                break;
        }
        if (picctype == NDK_RFID_A) {
            subCardType = OPEN_CARD_RF_A;
        } else if (picctype == NDK_RFID_B) {
            subCardType = OPEN_CARD_RF_B;
        }
        int offset = 0;
        memcpy(cardInfo->data, &mainType, 1);offset += 1;
//        nlMpos_Command.mpos_writelen(cardInfo->data + offset, 0, _VAR_BIT16);offset += 2;
        nlMpos_Command.mpos_writelen(cardInfo->data+offset, 1, _VAR_BIT16);offset += 2;
        memcpy(cardInfo->data+offset,&subCardType,1);offset += 1;
        //nlMpos_Command.mpos_writelen(cardInfo->data + offset, 0, _VAR_BIT16);offset += 2;
        nlMpos_Command.mpos_writelen(cardInfo->data+offset,len,_VAR_BIT16);offset += 2;
        memcpy(cardInfo->data+offset,uid,len);offset += len;
        LOGE_FMT(">>>RFCARD_QUICKLY_MODE activate1[%d]",activate);
        if(activate == 0){
            memset(cardInfo->data + offset,'\xFF', 1);offset += 1;
        } else{
            memset(cardInfo->data + offset,'\xFE', 1);offset += 1;
        }
        //IDmAndPMm length.
        nlMpos_Command.mpos_writelen(cardInfo->data+offset,0,_VAR_BIT16);offset += 2;

        if(atqLen > 0){
            nlMpos_Command.mpos_writelen(cardInfo->data+offset,atqLen,_VAR_BIT16);offset += 2;
            memcpy(cardInfo->data+offset,atq,atqLen);offset += atqLen;
        }

        cardInfo->validLen = offset;
        stCardReaderParam->targetCardType = OPENCARD_TYPE_RF;
        return NL_OK;
    }

    if (stCardReaderParam->searchCardRule == 0x02) {
        START_AB:
        {
            LOGD_FMT("START_AB");
            int atqLen = 0, snrLen = 0, iRet = -1, atsLen = 0;
            uchar picctype = 0, atq[32], snr[64], ackCode[2], atsbuf[32];
            memset(atq, 0, sizeof(atq));
            memset(snr, 0, sizeof(snr));
            memset(ackCode, 0, sizeof(ackCode));
            memset(atsbuf, 0, sizeof(atsbuf));
            LOGD_FMT(">>>NdkIsSupportACardAtq[%d]", g_aCardAtq);

            if (!EXEC_NDK("NDK_RfidPiccType", NDK_RfidPiccType(NDK_RFID_AB), NDK_OK,CARDREADER_OPEN))  goto END_AB;

            if (g_aCardAtq == 1) {
                EXEC_NDK("NDK_RfidPiccDetect_Atq", NDK_RfidPiccDetect_Atq(&picctype, &atqLen, atq), NDK_OK,CARDREADER_OPEN);
            } else {
                EXEC_NDK("NDK_RfidPiccDetect", NDK_RfidPiccDetect(&picctype), NDK_OK,CARDREADER_OPEN);
                atqLen = 0;
            }
            if (!EXEC_NDK("NDK_RfidPiccActivate", iRet = NDK_RfidPiccActivate(&picctype, &snrLen, snr), NDK_OK,CARDREADER_OPEN)) {
                if (iRet == NDK_ERR_RFID_AANTI) {
                    int offset = 0;
                    memcpy(cardInfo->data, CMD_RFID_AANTI, 2);offset += 2;
                    cardInfo->validLen = offset;
                    LOGD_FMT(">>>NDK_ERR_RFID_AANTI");
                    return NL_ERR_ACK;
                }
                goto END_AB;
            } else {
                LOGD_FMT(">>>NDK_RfidPiccActivate picctype[0x%02x]", picctype);
                if (picctype != NDK_RFID_A && picctype != NDK_RFID_B) {
                    int offset = 0;
                    memcpy(cardInfo->data, CMD_ERR_OTHER, 2);offset += 2;
                    cardInfo->validLen = offset;
                    return NL_ERR_ACK;
                }
                uchar sak = 0x20;
                if (picctype == NDK_RFID_A) {
                    subCardType = OPEN_CARD_RF_A;
                    sak = 0x20;
                } else if (picctype == NDK_RFID_B) {
                    subCardType = OPEN_CARD_RF_B;
                    sak = 0xFF;
                } else{
                    goto END_AB;
                }
                if(!(stCardReaderParam->expectedRfTypes & subCardType) && stCardReaderParam->expectedRfTypes != 0){
                    ERRMSG(SDK_ERR_CARD_NO_EXPECT,CARDREADER_OPEN);
                    goto ON_ERR;
                }
                int offset = 0;
                memcpy(cardInfo->data,&mainType,1);offset += 1;
                nlMpos_Command.mpos_writelen(cardInfo->data+offset, 1, _VAR_BIT16);offset += 2;
                memcpy(cardInfo->data+offset,&subCardType,1);offset += 1;
                nlMpos_Command.mpos_writelen(cardInfo->data+offset,snrLen,_VAR_BIT16);offset += 2;
                memcpy(cardInfo->data+offset,snr,snrLen);offset += snrLen;
                memcpy(cardInfo->data+offset,&sak,1);offset += 1;
                cardInfo->validLen = offset;
                stCardReaderParam->targetCardType = OPENCARD_TYPE_RF;
                return NL_OK;
            }
        }
        END_AB:

        START_M1:
        {
            LOGD_FMT("START_M1");
            LOGD_FMT("NdkIsSupportRfMultiLevel[%d]", g_rfMultiLevel);

            if (!EXEC_NDK("NDK_RfidPiccType", NDK_RfidPiccType(NDK_RFID_A), NDK_OK,CARDREADER_OPEN))  goto END_M1;

            if (g_rfMultiLevel == 0) {
                int atqLen = 0, uidLen = 0, iRet = -1;
                uchar atq[16], uid[64], sak[2];
                memset(atq, 0, sizeof(atq));
                memset(uid, 0, sizeof(uid));
                memset(sak, 0, sizeof(sak));
                if (!EXEC_NDK("NDK_M1Request", NDK_M1Request(0x52, &atqLen, atq), NDK_OK,CARDREADER_OPEN)) {
                    goto END_M1;
                }
                if (!EXEC_NDK("NDK_M1Anti", NDK_M1Anti(&uidLen, uid), NDK_OK,CARDREADER_OPEN)) {
                    goto END_M1;
                }
                if (EXEC_NDK("NDK_M1Select", NDK_M1Select(uidLen, uid, sak), NDK_OK,CARDREADER_OPEN)) {
                    if (sak[0] == 0x20) {
                        goto END_M1;
                    }
                    subCardType = OPEN_CARD_RF_M1;
                    if(!(stCardReaderParam->expectedRfTypes & subCardType)&& stCardReaderParam->expectedRfTypes != 0){
                        ERRMSG(SDK_ERR_CARD_NO_EXPECT,CARDREADER_OPEN);
                        goto ON_ERR;
                    }
                    int offset = 0;
                    memcpy(cardInfo->data,&mainType,1);offset += 1;
                    nlMpos_Command.mpos_writelen(cardInfo->data+offset, 1, _VAR_BIT16);offset += 2;
                    memcpy(cardInfo->data+offset,&subCardType,1);offset += 1;
                    nlMpos_Command.mpos_writelen(cardInfo->data+offset,uidLen,_VAR_BIT16);offset += 2;
                    memcpy(cardInfo->data+offset,uid,uidLen);offset += uidLen;
                    memcpy(cardInfo->data+offset,sak,1);offset += 1;
                    cardInfo->validLen = offset;
                    stCardReaderParam->targetCardType = OPENCARD_TYPE_RF;
                    return NL_OK;
                }
            }

            if (g_rfMultiLevel == 1) {
                int atqLen = 0, UIDlen = 0, snrLen = 0, i = 0;
                uchar atq[16], snr[64], selCmd[3] = {0x93, 0x95, 0x97}, sak[2], UID[64];
                memset(atq, 0, sizeof(atq));
                memset(snr, 0, sizeof(snr));
                memset(sak, 0, sizeof(sak));
                memset(UID, 0, sizeof(UID));
                if (!EXEC_NDK("NDK_M1Request", NDK_M1Request(0x52, &atqLen, atq), NDK_OK,CARDREADER_OPEN)) {
                    goto END_M1;
                }
                UIDlen = (atq[0] >> 6) + 1;
                do {
                    if (!EXEC_NDK("NDK_M1Anti_SEL", NDK_M1Anti_SEL(selCmd[i], &snrLen, snr), NDK_OK,CARDREADER_OPEN)) {
                        goto END_M1;
                    }
                    if (!EXEC_NDK("NDK_M1Select_SEL", NDK_M1Select_SEL(selCmd[i], snrLen, snr, sak), NDK_OK,CARDREADER_OPEN)) {
                        goto END_M1;
                    }
                    memcpy((UID + (i << 2)), snr, 4);
                    LOGD_FMT("selCmd[%d][%d] *sak[%d]", i, selCmd[i], *sak);
                    if ((*sak & 0x04) == 0x00) break;
                } while (i++ < 3);

                if ((UIDlen == 1) && (UID[0] == 0x88)) {
                    LOGD_FMT("UID ERR 1");
                    goto END_M1;
                }
                if ((UIDlen == 2) && (UID[4] == 0x88)) {
                    LOGD_FMT("UID ERR 2");
                    goto END_M1;
                }
                switch (UIDlen) {
                    case 1:
                        UIDlen = 4;
                        break;
                    case 2:
                        UIDlen = 7;
                        memcpy(UID, &UID[1], 3);
                        memcpy(&(UID[3]), &UID[4], 4);
                        break;
                    case 3:
                        UIDlen = 10;
                        memcpy(UID, &UID[1], 3);
                        memcpy(&(UID[3]), &UID[5], 3);
                        memcpy(&(UID[6]), &UID[8], 4);
                        break;
                    default:
                        UIDlen = 0;
                        UID[0] = 0x00;
                        goto END_M1;
                }
                LOGD_FMT("*sak[%d]", *sak);
                LOGD_STR("snr", UID, UIDlen);
                int offset = 0;
                if (sak[0] == 0x20) {
                    goto END_M1;
                }
                if(sak[0] == 0x00){
                    subCardType = OPEN_CARD_RF_M0;
                }else{
                    subCardType = OPEN_CARD_RF_M1;
                }
                if(!(stCardReaderParam->expectedRfTypes & subCardType)&& stCardReaderParam->expectedRfTypes != 0){
                    ERRMSG(SDK_ERR_CARD_NO_EXPECT,CARDREADER_OPEN);
                    goto ON_ERR;
                }
                memcpy(cardInfo->data,&mainType,1);offset += 1;
                nlMpos_Command.mpos_writelen(cardInfo->data+offset, 1, _VAR_BIT16);offset += 2;
                memcpy(cardInfo->data+offset,&subCardType,1);offset += 1;
                nlMpos_Command.mpos_writelen(cardInfo->data+offset,UIDlen,_VAR_BIT16);offset += 2;
                memcpy(cardInfo->data+offset,UID,UIDlen);offset += UIDlen;
                memcpy(cardInfo->data+offset,sak,1);offset += 1;
                cardInfo->validLen = offset;
                stCardReaderParam->targetCardType = OPENCARD_TYPE_RF;
                return NL_OK;
            }
        }
        END_M1:

        START_F:
        if(stCardReaderParam->felicaParamLen!=0){
            LOGD_FMT("START_F");
            EXEC_NDK("NDK_RfidPiccType(NDK_RFID_F)", NDK_RfidPiccType(NDK_RFID_F), NDK_OK,CARDREADER_OPEN);
            LOGD_STR("felicaParam",stCardReaderParam->felicaParam,stCardReaderParam->felicaParamLen);
            int i=0,j=0,count = stCardReaderParam->felicaParamLen/4;
            felica_param_t felicaParam;
            int IDmPMmlen=0;
            uchar IDmPMm[512];
            memset(IDmPMm,0,sizeof(IDmPMm));
            for(i=0; i< count; i++){
                LOGD_FMT(">>>count[%d]",count);
                memset(&felicaParam,0,sizeof(felica_param_t));
                memcpy(&felicaParam,stCardReaderParam->felicaParam+i*4,4);
                LOGD_STR("felicaParam",&felicaParam,4);
                for(j=0;j<3;j++){
                    if(EXEC_NDK("NDK_FelicaPoll", NDK_FelicaPoll(felicaParam,IDmPMm, &IDmPMmlen), NDK_OK,CARDREADER_OPEN)){
                        int offset = 0;
                        subCardType = OPEN_CARD_RF_FELICA;
                        if(!(stCardReaderParam->expectedRfTypes & subCardType)&& stCardReaderParam->expectedRfTypes != 0){
                            ERRMSG(SDK_ERR_CARD_NO_EXPECT,CARDREADER_OPEN);
                            goto ON_ERR;
                        }
                        memcpy(cardInfo->data,&mainType,1);offset += 1;
                        nlMpos_Command.mpos_writelen(cardInfo->data+offset, 1, _VAR_BIT16);offset += 2;
                        memcpy(cardInfo->data+offset,&subCardType,1);offset += 1;
                        nlMpos_Command.mpos_writelen(cardInfo->data+offset,0,_VAR_BIT16);offset += 2;
                        memcpy(cardInfo->data+offset,"\xFF",1);offset += 1;
                        nlMpos_Command.mpos_writelen(cardInfo->data+offset,IDmPMmlen, _VAR_BIT16);offset +=2;//felica
                        memcpy(cardInfo->data+offset,IDmPMm,IDmPMmlen);offset +=IDmPMmlen;
                        cardInfo->validLen = offset;
                        stCardReaderParam->targetCardType = OPENCARD_TYPE_RF;
                        return NL_OK;
                    }
                }
            }
        }
        END_F:;
    }

    if (stCardReaderParam->searchCardRule == 0x05) {
        START_M12:
        {
            LOGD_FMT("START_M12");
            LOGD_FMT("NdkIsSupportRfMultiLevel[%d]", g_rfMultiLevel);

            if (!EXEC_NDK("NDK_RfidPiccType", NDK_RfidPiccType(NDK_RFID_A), NDK_OK,CARDREADER_OPEN))  goto END_M12;

            if (g_rfMultiLevel == 0) {
                int atqLen = 0, uidLen = 0, iRet = -1;
                uchar atq[16], uid[64], sak[2];
                memset(atq, 0, sizeof(atq));
                memset(uid, 0, sizeof(uid));
                memset(sak, 0, sizeof(sak));
                if (!EXEC_NDK("NDK_M1Request", NDK_M1Request(0x52, &atqLen, atq), NDK_OK,CARDREADER_OPEN)) {
                    goto END_M12;
                }
                if (!EXEC_NDK("NDK_M1Anti", NDK_M1Anti(&uidLen, uid), NDK_OK,CARDREADER_OPEN)) {
                    goto END_M12;
                }
                if (EXEC_NDK("NDK_M1Select", NDK_M1Select(uidLen, uid, sak), NDK_OK,CARDREADER_OPEN)) {
                    if (sak[0] == 0x20) {
                        goto END_M12;
                    }
                    subCardType = OPEN_CARD_RF_M1;
                    if(!(stCardReaderParam->expectedRfTypes & subCardType)&& stCardReaderParam->expectedRfTypes != 0){
                        ERRMSG(SDK_ERR_CARD_NO_EXPECT,CARDREADER_OPEN);
                        goto ON_ERR;
                    }
                    int offset = 0;
                    memcpy(cardInfo->data,&mainType,1);offset += 1;
                    nlMpos_Command.mpos_writelen(cardInfo->data+offset, 1, _VAR_BIT16);offset += 2;
                    memcpy(cardInfo->data+offset,&subCardType,1);offset += 1;
                    nlMpos_Command.mpos_writelen(cardInfo->data+offset,uidLen,_VAR_BIT16);offset += 2;
                    memcpy(cardInfo->data+offset,uid,uidLen);offset += uidLen;
                    memcpy(cardInfo->data+offset,sak,1);offset += 1;
                    cardInfo->validLen = offset;
                    stCardReaderParam->targetCardType = OPENCARD_TYPE_RF;
                    return NL_OK;
                }
            }

            if (g_rfMultiLevel == 1) {
                int atqLen = 0, UIDlen = 0, snrLen = 0, i = 0;
                uchar atq[16], snr[64], selCmd[3] = {0x93, 0x95, 0x97}, sak[2], UID[64];
                memset(atq, 0, sizeof(atq));
                memset(snr, 0, sizeof(snr));
                memset(sak, 0, sizeof(sak));
                memset(UID, 0, sizeof(UID));
                if (!EXEC_NDK("NDK_M1Request", NDK_M1Request(0x52, &atqLen, atq), NDK_OK,CARDREADER_OPEN)) {
                    goto END_M12;
                }
                UIDlen = (atq[0] >> 6) + 1;
                do {
                    if (!EXEC_NDK("NDK_M1Anti_SEL", NDK_M1Anti_SEL(selCmd[i], &snrLen, snr), NDK_OK,CARDREADER_OPEN)) {
                        goto END_M12;
                    }
                    if (!EXEC_NDK("NDK_M1Select_SEL", NDK_M1Select_SEL(selCmd[i], snrLen, snr, sak), NDK_OK,CARDREADER_OPEN)) {
                        goto END_M12;
                    }
                    memcpy((UID + (i << 2)), snr, 4);
                    LOGD_FMT("selCmd[%d][%d] *sak[%d]", i, selCmd[i], *sak);
                    if ((*sak & 0x04) == 0x00) break;
                } while (i++ < 3);

                if ((UIDlen == 1) && (UID[0] == 0x88)) {
                    LOGD_FMT("UID ERR 1");
                    goto END_M12;
                }
                if ((UIDlen == 2) && (UID[4] == 0x88)) {
                    LOGD_FMT("UID ERR 2");
                    goto END_M12;
                }
                switch (UIDlen) {
                    case 1:
                        UIDlen = 4;
                        break;
                    case 2:
                        UIDlen = 7;
                        memcpy(UID, &UID[1], 3);
                        memcpy(&(UID[3]), &UID[4], 4);
                        break;
                    case 3:
                        UIDlen = 10;
                        memcpy(UID, &UID[1], 3);
                        memcpy(&(UID[3]), &UID[5], 3);
                        memcpy(&(UID[6]), &UID[8], 4);
                        break;
                    default:
                        UIDlen = 0;
                        UID[0] = 0x00;
                        goto END_M12;
                }
                LOGD_FMT("*sak1[%d]", *sak);
                LOGD_STR("snr", UID, UIDlen);
                int offset = 0;
                if (sak[0] == 0x20) {
                    goto END_M12;
                }
                if(sak[0] == 0x00){
                    subCardType = OPEN_CARD_RF_M0;
                }else{
                    subCardType = OPEN_CARD_RF_M1;
                }
                if(!(stCardReaderParam->expectedRfTypes & subCardType)&& stCardReaderParam->expectedRfTypes != 0){
                    ERRMSG(SDK_ERR_CARD_NO_EXPECT,CARDREADER_OPEN);
                    goto ON_ERR;
                }
                memcpy(cardInfo->data,&mainType,1);offset += 1;
                nlMpos_Command.mpos_writelen(cardInfo->data+offset, 1, _VAR_BIT16);offset += 2;
                memcpy(cardInfo->data+offset,&subCardType,1);offset += 1;
                nlMpos_Command.mpos_writelen(cardInfo->data+offset,UIDlen,_VAR_BIT16);offset += 2;
                memcpy(cardInfo->data+offset,UID,UIDlen);offset += UIDlen;
                memcpy(cardInfo->data+offset,sak,1);offset += 1;
                cardInfo->validLen = offset;
                stCardReaderParam->targetCardType = OPENCARD_TYPE_RF;
                return NL_OK;
            }
        }
        END_M12:

        START_AB2:
        {
            LOGD_FMT("START_AB2");
            int atqLen = 0, snrLen = 0, iRet = -1, atsLen = 0;
            uchar picctype = 0, atq[2], snr[64], ackCode[2], atsbuf[32];
            memset(atq, 0, sizeof(atq));
            memset(snr, 0, sizeof(snr));
            memset(ackCode, 0, sizeof(ackCode));
            memset(atsbuf, 0, sizeof(atsbuf));
            LOGD_FMT(">>>NdkIsSupportACardAtq..[%d]", g_aCardAtq);

            if (!EXEC_NDK("NDK_RfidPiccDeactivate", NDK_RfidPiccDeactivate(10), NDK_OK,CARDREADER_OPEN)) return NL_FAILED;

            if (!EXEC_NDK("NDK_RfidPiccType", NDK_RfidPiccType(NDK_RFID_AB), NDK_OK,CARDREADER_OPEN))  goto END_AB2;

            if (g_aCardAtq == 1) {
                EXEC_NDK("NDK_RfidPiccDetect_Atq", NDK_RfidPiccDetect_Atq(&picctype, &atqLen, atq), NDK_OK,CARDREADER_OPEN);
            } else {
                EXEC_NDK("NDK_RfidPiccDetect", NDK_RfidPiccDetect(&picctype), NDK_OK,CARDREADER_OPEN);
                atqLen = 0;
            }
            if (!EXEC_NDK("NDK_RfidPiccActivate", iRet = NDK_RfidPiccActivate(&picctype, &snrLen, snr), NDK_OK,CARDREADER_OPEN)) {
                if (iRet == NDK_ERR_RFID_AANTI) {
                    int offset = 0;
                    memcpy(cardInfo->data, CMD_RFID_AANTI, 2);offset += 2;
                    cardInfo->validLen = offset;
                    LOGD_FMT(">>>NDK_ERR_RFID_AANTI");
                    return NL_ERR_ACK;
                }
                LOGD_FMT(">>>NDK_RfidPiccActivate 5 iRet[%d]",iRet);
                int n = 0;
                for(; n < 10; n++){
                    if(!EXEC_NDK("NDK_RfidPiccType",NDK_RfidPiccType(NDK_RFID_AB),NDK_OK,CARDREADER_OPEN))
                        continue;
                    if (g_aCardAtq == 1) {
                        if(!EXEC_NDK("NDK_RfidPiccDetect_Atq", NDK_RfidPiccDetect_Atq(&picctype, &atqLen, atq), NDK_OK,CARDREADER_OPEN)){
                            continue;
                        }
                    } else {
                        if(!EXEC_NDK("NDK_RfidPiccDetect", NDK_RfidPiccDetect(&picctype), NDK_OK,CARDREADER_OPEN)){
                            continue;
                        }
                        atqLen = 0;
                    }
                    //if(!EXEC_NDK("NDK_RfidPiccDetect",NDK_RfidPiccDetect(&picctype),NDK_OK,CARDREADER_OPEN))
                    //    continue;
                    if(EXEC_NDK("NDK_RfidPiccActivate",iRet = NDK_RfidPiccActivate(&picctype, &snrLen, snr), NDK_OK,CARDREADER_OPEN))
                        break;
                }
                if(iRet == NDK_OK){
                    goto END_AB2_0;
                }else{
                    goto END_AB2;
                }
            } else {
                END_AB2_0:
                LOGD_FMT(">>>NDK_RfidPiccActivate picctype[0x%02x]", picctype);
                if (picctype != NDK_RFID_A && picctype != NDK_RFID_B) {
                    int offset = 0;
                    memcpy(cardInfo->data, CMD_ERR_OTHER, 2);offset += 2;
                    cardInfo->validLen = offset;
                    return NL_ERR_ACK;
                }
                uchar sak = 0x20;
                if (picctype == NDK_RFID_A) {
                    subCardType = OPEN_CARD_RF_A;
                    sak = 0x20;
                } else if (picctype == NDK_RFID_B) {
                    subCardType = OPEN_CARD_RF_B;
                    sak = 0xFF;
                } else{
                    goto END_AB2;
                }
                if(!(stCardReaderParam->expectedRfTypes & subCardType) && stCardReaderParam->expectedRfTypes != 0){
                    ERRMSG(SDK_ERR_CARD_NO_EXPECT,CARDREADER_OPEN);
                    goto ON_ERR;
                }
                int offset = 0;
                memcpy(cardInfo->data,&mainType,1);offset += 1;
                nlMpos_Command.mpos_writelen(cardInfo->data+offset, 1, _VAR_BIT16);offset += 2;
                memcpy(cardInfo->data+offset,&subCardType,1);offset += 1;
                nlMpos_Command.mpos_writelen(cardInfo->data+offset,snrLen,_VAR_BIT16);offset += 2;
                memcpy(cardInfo->data+offset,snr,snrLen);offset += snrLen;
                memcpy(cardInfo->data+offset,&sak,1);offset += 1;
                cardInfo->validLen = offset;
                stCardReaderParam->targetCardType = OPENCARD_TYPE_RF;
                return NL_OK;
            }
        }
        END_AB2:

        START_F2:
        if(stCardReaderParam->felicaParamLen!=0){
            LOGD_FMT("START_F2");
            EXEC_NDK("NDK_RfidPiccType(NDK_RFID_F)", NDK_RfidPiccType(NDK_RFID_F), NDK_OK,CARDREADER_OPEN);
            LOGD_STR("felicaParam",stCardReaderParam->felicaParam,stCardReaderParam->felicaParamLen);
            int i=0,j=0,count = stCardReaderParam->felicaParamLen/4;
            felica_param_t felicaParam;
            int IDmPMmlen=0;
            uchar IDmPMm[512];
            memset(IDmPMm,0,sizeof(IDmPMm));
            for(i=0; i< count; i++){
                LOGD_FMT(">>>count[%d]",count);
                memset(&felicaParam,0,sizeof(felica_param_t));
                memcpy(&felicaParam,stCardReaderParam->felicaParam+i*4,4);
                LOGD_STR("felicaParam",&felicaParam,4);
                for(j=0;j<3;j++){
                    if(EXEC_NDK("NDK_FelicaPoll", NDK_FelicaPoll(felicaParam,IDmPMm, &IDmPMmlen), NDK_OK,CARDREADER_OPEN)){
                        int offset = 0;
                        subCardType = OPEN_CARD_RF_FELICA;
                        if(!(stCardReaderParam->expectedRfTypes & subCardType)&& stCardReaderParam->expectedRfTypes != 0){
                            ERRMSG(SDK_ERR_CARD_NO_EXPECT,CARDREADER_OPEN);
                            goto ON_ERR;
                        }
                        memcpy(cardInfo->data,&mainType,1);offset += 1;
                        nlMpos_Command.mpos_writelen(cardInfo->data+offset, 1, _VAR_BIT16);offset += 2;
                        memcpy(cardInfo->data+offset,&subCardType,1);offset += 1;
                        nlMpos_Command.mpos_writelen(cardInfo->data+offset,0,_VAR_BIT16);offset += 2;
                        memcpy(cardInfo->data+offset,"\xFF",1);offset += 1;
                        nlMpos_Command.mpos_writelen(cardInfo->data+offset,IDmPMmlen, _VAR_BIT16);offset +=2;//felica
                        memcpy(cardInfo->data+offset,IDmPMm,IDmPMmlen);offset +=IDmPMmlen;
                        cardInfo->validLen = offset;
                        stCardReaderParam->targetCardType = OPENCARD_TYPE_RF;
                        return NL_OK;
                    }
                }
            }
        }
        END_F2:;
    }

    if(stCardReaderParam->searchCardRule == 0x06){
        uint32_t CARD_TYPE_A = 0x00000001,CARD_TYPE_B = 0x00000002,CARD_TYPE_V = 0x00000008;
        uint8_t sak = 0;
        uint32_t detectType = (CARD_TYPE_A|CARD_TYPE_B);
        int n = 0,ret = NDK_ERR, count = 0;
        int rece_len = 0,uidlen = 0;
        uint8_t uid_buf[10],rece_buf[256];
        int nOutBufLen = 0;
        uchar outBuf[512];

        START_ABM1M0:
        LOGE_FMT("START_ABM1M0.");

        if(!EXEC_NDK("NDK_RfidPiccDeactivate", NDK_RfidPiccDeactivate(10), NDK_OK,CARDREADER_OPEN)) return NL_FAILED;

        START_ABM1M0_DETECT:
        if(++count > 3)  {//3 times
            return NL_FAILED;
        }

        if (stCardReaderParam->vasEnable && stCardReaderParam->vasParamLen > 0){
            if(!EXEC_NDK("NDK_RfidSetDetectType(3)", NDK_RfidSetDetectType(CARD_TYPE_A | CARD_TYPE_B | CARD_TYPE_V), NDK_OK,CARDREADER_OPEN)) return NL_FAILED;

            if (!EXEC_NDK("NDK_RfidSetPiccParam", NDK_RfidSetPiccParam(0x01,stCardReaderParam->vasParamLen,stCardReaderParam->vasParam), NDK_OK,CARDREADER_OPEN))  return NL_FAILED;
        }else{
            if(!EXEC_NDK("NDK_RfidSetDetectType(3)", NDK_RfidSetDetectType(CARD_TYPE_A|CARD_TYPE_B), NDK_OK,CARDREADER_OPEN)) return NL_FAILED;
        }

        if(!EXEC_NDK("NDK_RfidDetectWithCardType.", ret = NDK_RfidDetectWithCardType(&detectType,&nOutBufLen,outBuf), NDK_OK,CARDREADER_OPEN)) goto START_ABM1M0_DETECT;


        LOGD_FMT("NDK_RfidDetectWithCardType detectType.[%d]",detectType);
        if(detectType & CARD_TYPE_A){
            if(!EXEC_NDK("NDK_MifareActive", NDK_MifareActive(0x52,uid_buf,&uidlen,&sak), NDK_OK,CARDREADER_OPEN)) {
                if (ret == NDK_ERR_RFID_AANTI) {
                    int offset = 0;
                    memcpy(cardInfo->data, CMD_RFID_AANTI, 2);offset += 2;
                    cardInfo->validLen = offset;
                    LOGD_FMT(">>>NDK_ERR_RFID_AANTI");
                    return NL_ERR_ACK;
                }
                goto START_ABM1M0;
            }
            LOGE_FMT("rf, NDK_MifareActive sak[0x%x]",sak);
            if(sak & 0x20){
                if(!EXEC_NDK("NDK_RfidTypeARats", NDK_RfidTypeARats(0,&rece_len, rece_buf), NDK_OK,CARDREADER_OPEN)) {
                    goto START_ABM1M0;
                }
                LOGD_FMT("rf, find A");
                subCardType = OPEN_CARD_RF_A;
                sak = 0x20;
            }else{
                if (sak == 0x00) {
                    LOGD_FMT("rf, find M0");
                    subCardType = OPEN_CARD_RF_M0;

                } else {
                    LOGD_FMT("rf, find M1");
                    subCardType = OPEN_CARD_RF_M1;
                }
            }
        } else{
            if(!EXEC_NDK("NDK_RfidPiccActivate", NDK_RfidPiccActivate(&detectType, &uidlen, uid_buf), NDK_OK,CARDREADER_OPEN)) goto START_ABM1M0;
            if(detectType & CARD_TYPE_B){
                LOGD_FMT("rf, find B");
                subCardType = OPEN_CARD_RF_B;
                sak = 0xFF;
            }else{
                LOGE_FMT("NDK_RfidPiccActivate unknown card type, detectType[%d]",detectType);
            }
        }
        if(!(stCardReaderParam->expectedRfTypes & subCardType) && stCardReaderParam->expectedRfTypes != 0){
            ERRMSG(SDK_ERR_CARD_NO_EXPECT,CARDREADER_OPEN);
            goto ON_ERR;
        }
        int offset = 0;
        memcpy(cardInfo->data,&mainType,1);offset += 1;
        nlMpos_Command.mpos_writelen(cardInfo->data+offset, 1, _VAR_BIT16);offset += 2;
        memcpy(cardInfo->data+offset,&subCardType,1);offset += 1;
        nlMpos_Command.mpos_writelen(cardInfo->data+offset,uidlen,_VAR_BIT16);offset += 2;
        memcpy(cardInfo->data+offset,uid_buf,uidlen);offset += uidlen;
        memcpy(cardInfo->data+offset,&sak,1);offset += 1;
        cardInfo->validLen = offset;
        stCardReaderParam->targetCardType = OPENCARD_TYPE_RF;
        LOGE_FMT("END_ABM1M0");
        return NL_OK;
    }

    ON_ERR:
    return NL_FAILED;
}

int RfidReader_Close(void *pstCardReaderParam) {
    if (((StCardReaderParam *) pstCardReaderParam)->targetCardType == OPENCARD_TYPE_RF) {
        return NL_OK;
    }
    RfidReader_LedLt1118Status(0);
    if (EXEC_NDK("NDK_RfidCloseRf", NDK_RfidCloseRf(), NDK_OK,CARDREADER_OPEN)) {
        LOGD_FMT("Close rf card successfully..");
    } else {
        LOGD_FMT("Close rf card failed..");
        return NL_FAILED;
    }
    return NL_OK;

#if 0
    if(pstCardReaderParam == NULL){
        LOGD_FMT(">>>pstCardReaderParam[%d] return.",pstCardReaderParam);
        return NL_FAILED;
    }
    StCardReaderParam* stCardReaderParam = (StCardReaderParam*)pstCardReaderParam;
    if(HAS_CARD_RFID(stCardReaderParam->readCardMode)){
        if(EXEC_NDK("NDK_RfidCloseRf",NDK_RfidCloseRf(),NDK_OK)){
            LOGD_FMT("Close rf card successfully.");
        }else {
            LOGD_FMT("Close rf card failed.");
            return NL_FAILED;
        }
    }
#endif

}

int RfidReader_Resume(void *pstCardReaderParam) {
    if (pstCardReaderParam == NULL) {
        LOGD_FMT(">>>pstCardReaderParam[%d] return.", pstCardReaderParam);
        ERRMSG(SDK_ERR_PARAM,CARDREADER_OPEN);
        return NL_FAILED;
    }
    StCardReaderParam *stCardReaderParam = (StCardReaderParam *) pstCardReaderParam;
    int time = stCardReaderParam->rfidiInterval;
    LOGD_FMT(">>>rfidTimes[%d] rfidiInterval[%d]", stCardReaderParam->rfidTimes, time);
    if (time > 355) {
        for (; time > 355;) {
            EXEC_NDK("NDK_RfidPiccDeactivate1", NDK_RfidPiccDeactivate(255), NDK_OK,CARDREADER_OPEN);
            time = time - 355;
        }
    } else if (time <= 355 && time >= 110) {
        EXEC_NDK("NDK_RfidPiccDeactivate2", NDK_RfidPiccDeactivate(time - 100), NDK_OK,CARDREADER_OPEN);
    } else if (time < 110) {
        EXEC_NDK("NDK_RfidPiccDeactivate3", NDK_RfidPiccDeactivate(10), NDK_OK,CARDREADER_OPEN);
    }
    return NL_OK;
}
int RfidReader_LedLt1118Status(int isOn){
    EM_LED_LT1118 status = 0;
    if(isOn){
        status = LED_LT1118_RFID_RED1_ON | LED_LT1118_RFID_RED2_ON | LED_LT1118_RFID_RED3_ON | LED_LT1118_RFID_RED4_ON | LED_LT1118_RFID_RED5_ON;
    }else{
        status = LED_LT1118_RFID_RED1_OFF | LED_LT1118_RFID_RED2_OFF | LED_LT1118_RFID_RED3_OFF | LED_LT1118_RFID_RED4_OFF | LED_LT1118_RFID_RED5_OFF;
    }
    int ret = NDK_ERR;
    EXEC_NDK("NDK_LedLt1118Status", ret = NDK_LedLt1118Status(status), NDK_OK,CARDREADER_OPEN);
    return ret;
}