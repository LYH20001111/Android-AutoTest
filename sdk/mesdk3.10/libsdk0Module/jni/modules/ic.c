#include <string.h>
#include "ndk.h"
#include "command.h"
#include "log.h"
#include "api.h"

int Icc_PowerOn(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int atrLen = 0;
    int nRet, icSlot, icType, extoffset;
    EM_ICTYPE emIctype;
    char headCode[2];
    memcpy(headCode, CMD_OK, 2);

    extoffset = RESPOND_DATA_OFFSET;
    icSlot = nlMpos_Command.mpos_getvar(pbuf , _VAR_BIT8);
    icType = nlMpos_Command.mpos_getvar(pbuf + 1, _VAR_BIT8);
    if ((icType != 0) && (icSlot > 2)) {
        memcpy(headCode, CMD_ERR_OTHER, 2);
        goto ON_ACK;
    }
    switch (icSlot) {
        case 0:
            switch (icType) {
                case 0x00:
                    emIctype = ICTYPE_IC;
                    break;
                case 0x05:
                    emIctype = ICTYPE_M_1;
                    break;
                case 0x06:
                    emIctype = ICTYPE_M_2;
                    break;
                case 0x07:
                    emIctype = ICTYPE_M_3;
                    break;
                case 0x08:
                    emIctype = ICTYPE_M_4;
                    break;
                case 0x09:
                    emIctype = ICTYPE_M_5;
                    break;
                case 0x0a:
                    emIctype = ICTYPE_M_6;
                    break;
                case 0x0b:
                    emIctype = ICTYPE_ISO7816;
                    break;
                case 0x0c:
                    emIctype = ICTYPE_M_7;
                    break;
                case 0x0d:
                    emIctype = ICTYPE_M_1_1;
                    break;
                case 0x0e:
                    emIctype = ICTYPE_M_1_2;
                    break;
                case 0x0f:
                    emIctype = ICTYPE_M_1_4;
                    break;
                case 0x10:
                    emIctype = ICTYPE_M_1_8;
                    break;
                case 0x11:
                    emIctype = ICTYPE_M_1_16;
                    break;
                case 0x12:
                    emIctype = ICTYPE_M_1_32;
                    break;
                case 0x13:
                    emIctype = ICTYPE_M_1_64;
                    break;
                case 0x14:
                    emIctype = ICTYPE_M_1_128;
                    break;
                case 0x15:
                    emIctype = ICTYPE_M_1_256;
                    break;
                default:
                    memcpy(headCode, CMD_ERR_PARAM, 2);
                    goto ON_ACK;
            }
            break;
        case 3:
            emIctype = ICTYPE_SAM1;
            break;
        case 4:
            emIctype = ICTYPE_SAM2;
            break;
        default:
            memcpy(headCode, CMD_ERR_PARAM, 2);
            goto ON_ACK;
    }

    if(!EXEC_NDK("NDK_IccPowerUp",NDK_IccPowerUp(emIctype, pOut + extoffset + 2, &atrLen),NDK_OK,ICC_POWERON)){
        atrLen = 0;
        memcpy(headCode, CMD_ERR_OTHER, 2);
        goto ON_ACK;
    }

    nlMpos_Command.mpos_writelen(pOut + extoffset, atrLen, _VAR_BIT16);
    responseCmd(pOut, 2 + atrLen, outLen, headCode);
    return 0;

    ON_ACK:
    responseCmd(pOut, 0, outLen, headCode);
    return 0;
}

int Icc_ReadWrite(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    unsigned int sendlen = 0, nRet, icSlot, icType;
    int recvlen = 2048;
    int extoffset;
    EM_ICTYPE emIctype;
    char headCode[2];
    memcpy(headCode, CMD_OK, 2);

    extoffset = 2;
    icSlot = nlMpos_Command.mpos_getvar(pbuf , _VAR_BIT8);
    icType = nlMpos_Command.mpos_getvar(pbuf + 1, _VAR_BIT8);
    sendlen = nlMpos_Command.mpos_readlen(pbuf + 2, _VAR_BIT16);

    if ((icType != 0) && (icSlot > 2)) {
        memcpy(headCode, CMD_ERR_OTHER, 2);
        goto ON_ACK;
    }
    switch (icSlot) {
        case 0:
            switch (icType) {
                case 0x00:
                    emIctype = ICTYPE_IC;
                    break;
                case 0x05:
                    emIctype = ICTYPE_M_1;
                    break;
                case 0x06:
                    emIctype = ICTYPE_M_2;
                    break;
                case 0x07:
                    emIctype = ICTYPE_M_3;
                    break;
                case 0x08:
                    emIctype = ICTYPE_M_4;
                    break;
                case 0x09:
                    emIctype = ICTYPE_M_5;
                    break;
                case 0x0a:
                    emIctype = ICTYPE_M_6;
                    break;
                case 0x0b:
                    emIctype = ICTYPE_ISO7816;
                    break;
                case 0x0c:
                    emIctype = ICTYPE_M_7;
                    break;
                case 0x0d:
                    emIctype = ICTYPE_M_1_1;
                    break;
                case 0x0e:
                    emIctype = ICTYPE_M_1_2;
                    break;
                case 0x0f:
                    emIctype = ICTYPE_M_1_4;
                    break;
                case 0x10:
                    emIctype = ICTYPE_M_1_8;
                    break;
                case 0x11:
                    emIctype = ICTYPE_M_1_16;
                    break;
                case 0x12:
                    emIctype = ICTYPE_M_1_32;
                    break;
                case 0x13:
                    emIctype = ICTYPE_M_1_64;
                    break;
                case 0x14:
                    emIctype = ICTYPE_M_1_128;
                    break;
                case 0x15:
                    emIctype = ICTYPE_M_1_256;
                    break;
                default:
                    memcpy(headCode, CMD_ERR_PARAM, 2);
                    goto ON_ACK;
            }
            break;
        case 3:
            emIctype = ICTYPE_SAM1;
            break;
        case 4:
            emIctype = ICTYPE_SAM2;
            break;
        default:
            memcpy(headCode, CMD_ERR_PARAM, 2);
            goto ON_ACK;
    }

    if(!EXEC_NDK("NDK_Iccrw",NDK_Iccrw(emIctype, sendlen, pbuf + 4, &recvlen, pOut + extoffset + 2),NDK_OK,ICC_READWRITE)){
        memcpy(headCode, CMD_ERR_OTHER, 2);
        goto ON_ACK;
    }
    nlMpos_Command.mpos_writelen(pOut + extoffset, recvlen, _VAR_BIT16);
    responseCmd(pOut, 2 + recvlen, outLen, headCode);
    return 0;

    ON_ACK:
    responseCmd(pOut, 0, outLen, headCode);
    return 0;
}

int Icc_PowerOff(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int icSlot, icType;
    EM_ICTYPE emIctype;
    char headCode[2];

    memcpy(headCode, CMD_OK, 2);

    icSlot = nlMpos_Command.mpos_getvar(pbuf, _VAR_BIT8);
    icType = nlMpos_Command.mpos_getvar(pbuf + 1, _VAR_BIT8);
    if ((icType != 0) && (icSlot > 2)) {
        memcpy(headCode, CMD_ERR_OTHER, 2);
        goto ON_ACK;
    }
    switch (icSlot) {
        case 0:
            switch (icType) {
                case 0x00:
                    emIctype = ICTYPE_IC;
                    break;
                case 0x05:
                    emIctype = ICTYPE_M_1;
                    break;
                case 0x06:
                    emIctype = ICTYPE_M_2;
                    break;
                case 0x07:
                    emIctype = ICTYPE_M_3;
                    break;
                case 0x08:
                    emIctype = ICTYPE_M_4;
                    break;
                case 0x09:
                    emIctype = ICTYPE_M_5;
                    break;
                case 0x0a:
                    emIctype = ICTYPE_M_6;
                    break;
                case 0x0b:
                    emIctype = ICTYPE_ISO7816;
                    break;
                case 0x0c:
                    emIctype = ICTYPE_M_7;
                    break;
                case 0x0d:
                    emIctype = ICTYPE_M_1_1;
                    break;
                case 0x0e:
                    emIctype = ICTYPE_M_1_2;
                    break;
                case 0x0f:
                    emIctype = ICTYPE_M_1_4;
                    break;
                case 0x10:
                    emIctype = ICTYPE_M_1_8;
                    break;
                case 0x11:
                    emIctype = ICTYPE_M_1_16;
                    break;
                case 0x12:
                    emIctype = ICTYPE_M_1_32;
                    break;
                case 0x13:
                    emIctype = ICTYPE_M_1_64;
                    break;
                case 0x14:
                    emIctype = ICTYPE_M_1_128;
                    break;
                case 0x15:
                    emIctype = ICTYPE_M_1_256;
                    break;
                default:
                    memcpy(headCode, CMD_ERR_OTHER, 2);
                    goto ON_ACK;
            }
            break;
        case 3:
            emIctype = ICTYPE_SAM1;
            break;
        case 4:
            emIctype = ICTYPE_SAM2;
            break;
        default:
            memcpy(headCode, CMD_ERR_OTHER, 2);
            goto ON_ACK;
    }
    if(!EXEC_NDK("NDK_IccPowerDown",NDK_IccPowerDown(emIctype),NDK_OK,ICC_POWEROFF)){
        memcpy(headCode, CMD_ERR_OTHER, 2);
    }
    ON_ACK:
    responseCmd(pOut, 0, outLen, headCode);
    return 0;
}

int Icc_Detect(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int icStatus;
    int extoffset=0;
    char headCode[2];

    memcpy(headCode, CMD_OK, 2);

    extoffset = 2;

    if(!EXEC_NDK("NDK_IccDetect",NDK_IccDetect(&icStatus),NDK_OK,ICC_DETECT)){
        responseCmd(pOut, 0, outLen, CMD_ERR_OTHER);
        return 0;
    }
    if ((icStatus & 0x02) == 0x02)//IC卡1已上电
    {
        pOut[extoffset] = 0x02;
    }
     if ((icStatus & 0x01) == 0x01)//IC卡1已插卡
    {
        pOut[extoffset] = 0x01;
    } else
        pOut[extoffset] = 0x00;

    pOut[extoffset + 1] = 0x00;
    pOut[extoffset + 2] = 0x00;

    uchar atr[128];int len = 0;
    if ((icStatus & 0x10) == 0x10)//SAM1已上电
    {
        pOut[extoffset + 3] = 0x02;
    } else {
        pOut[extoffset + 3] = 0x00;
    }
    if ((icStatus & 0x20) == 0x20)//SAM2已上电
    {
        pOut[extoffset + 4] = 0x02;
    } else {
        pOut[extoffset + 4] = 0x00;
    }

    if ((icStatus & 0x40) == 0x40)//SAM2已上电
    {
        pOut[extoffset + 5] = 0x02;
    } else {
        pOut[extoffset + 5] = 0x00;
    }

    pOut[extoffset + 6] = 0x00;
    pOut[extoffset + 7] = 0x00;

    EXEC_NDK("NDK_IccPowerDown",NDK_IccPowerDown(ICTYPE_IC),NDK_OK,ICC_DETECT);

    responseCmd(pOut, 8, outLen, headCode);
    return 0;
}



