#include <memory.h>
#include <stddef.h>
#include <jni.h>
#include "emvl3.h"
#include "emv.h"
#include "log.h"
extern JavaVM *gJavaVM;
extern jobject g_commLisObj;
extern jmethodID g_commLisMid;

int g_commType = TYPE_SP100;
#define RECVDATA
static uchar calcLrc(uchar *data,int len){
    uchar lrc = 0;
    int i = 0;
    for(i=0;i<len;i++){
        lrc ^= data[i];
    }
    return (lrc&0xff);
}
static int DectoBCD(int Dec, unsigned char *Bcd, int length)
{
    int i;
    int temp;
    for (i = length - 1; i >= 0; i--)
    {
        temp = Dec % 100;
        Bcd[i] = ((temp / 10) << 4) + ((temp % 10) & 0x0F);
        Dec /= 100;
    }
    return 0;
}

static int MPOSPack(uchar *type, uchar funcID, uchar *bodyData, unsigned int bodyDataLen,uchar **sendData,unsigned int *sendDataLen){
    int sendLen = 1+2+2+1+1+bodyDataLen+1+1;
    uchar *sendBuf = malloc(sendLen);
    if(sendBuf == NULL){
        LOGE_FMT(">>>MPOSPack malloc error");
        return -1;
    }
    uchar llll[2];
    memset(sendBuf,0,sendLen);
    memset(llll,0, sizeof(llll));
    DectoBCD(sendLen-5,llll, sizeof(llll));
    sendBuf[0]= 0x02;
    sendBuf[1] = llll[0];
    sendBuf[2] = llll[1];
    sendBuf[3] = type[0];
    sendBuf[4] = type[1];
    sendBuf[5] = 0x2F;
    sendBuf[6] = funcID;
    memcpy((sendBuf+7),bodyData,bodyDataLen);
    sendBuf[sendLen-2] = 0x03;
    sendBuf[sendLen-1] = calcLrc(sendBuf+1,sendLen-2);
    *sendDataLen = sendLen;
    *sendData = sendBuf;
    return 0;
}
// 1   Start of text (02h)
// 2   LLLL = Message Type + Separator + Function ID + Message Data
// 2   Message Type
// 1   Separator    (2Fh)
// 1   Function ID
//     Message Data
// 1   End of text  (03h)
// 1   LRC          (LLLL + Message Type + Separator + Function ID + Message Data  + End of text)

//example
//send: 02 [00 12 4c 30 2f 22 00 00 00 00 00 00 00 00 03] 60
//receive:06 02 [00 06 4C 31 2F 22 30 30 03] 75
static int SP100Pack(uchar *type, uchar funcID, uchar *bodyData, unsigned int bodyDataLen,uchar **sendData,unsigned int *sendDataLen){
    int sendLen = 1+2+2+1+1+bodyDataLen+1+1;
    uchar *sendBuf = malloc(sendLen+1);
    if(sendBuf == NULL){
        LOGE_FMT(">>>SP100Pack malloc error");
        return -1;
    }
    uchar llll[2];
    memset(sendBuf,0,sendLen);
    memset(llll,0, sizeof(llll));
    DectoBCD(sendLen-5,llll, sizeof(llll));
    sendBuf[0]= 0x02;
    sendBuf[1] = llll[0];
    sendBuf[2] = llll[1];
    sendBuf[3] = 'L';
    sendBuf[4] = '0';
    sendBuf[5] = 0x2F;
    sendBuf[6] = funcID;
    memcpy((sendBuf+7),bodyData,bodyDataLen);
    sendBuf[sendLen-2] = 0x03;
    sendBuf[sendLen-1] = calcLrc(sendBuf+1,sendLen-2);
    *sendDataLen = sendLen;
    *sendData = sendBuf;
    return 0;
}
static int Communication(uchar *type, uchar funcID, uchar *bodyData, unsigned int bodyDataLen, uchar *Recv_Data, unsigned int *recvDataLen)
{
    if(g_commType != TYPE_SP100 && g_commType != TYPE_MPOS){
        LOGE_FMT(">>>g_commType[%d]",g_commType);
        return -1;
    }
    JNIEnv *env = NULL;
    jboolean isAttached = JNI_FALSE;
    int ret = (*gJavaVM)->GetEnv(gJavaVM,(void **) &env, JNI_VERSION_1_4);
    if(ret < 0 ) {
        ret =  (*gJavaVM)->AttachCurrentThread(gJavaVM,(JNIEnv **) &env, NULL);
        if (ret < 0) {
            LOGE_FMT(">>>AttachCurrentThread error.");
            return -1;
        }
        isAttached = JNI_TRUE;
    }
    if(g_commLisObj == NULL || g_commLisMid == NULL) {
        LOGE_FMT(">>>g_commLisObj[%d] g_commLisMid[%d]",g_commLisObj,g_commLisMid);
        return -1;
    }

    unsigned int sendLen;uchar *sendBuf=NULL;
    if(g_commType == TYPE_SP100){
        ret = SP100Pack(type,funcID,bodyData,bodyDataLen,&sendBuf,&sendLen);
    } else if(g_commType == TYPE_MPOS){
        ret = MPOSPack(type,funcID,bodyData,bodyDataLen,&sendBuf,&sendLen);
    }
    if(ret < 0){
        LOGE_FMT(">>>Pack ret[%d]",ret);
        return -1;
    }
    LOGD_STR("UART SEND",sendBuf,sendLen);
    jbyteArray jsendBytes = (*env)->NewByteArray(env,sendLen);
    (*env)->SetByteArrayRegion(env, jsendBytes, 0, sendLen, (jbyte *)sendBuf);
    jbyteArray receiveArray = (jbyteArray) (*env)->CallObjectMethod(env,g_commLisObj,g_commLisMid,jsendBytes);
    if(sendBuf!=NULL){
        free(sendBuf);
    }
    if(receiveArray == NULL){
        LOGE_FMT(">>>Communication fail[%d]",receiveArray);
        return -1;
    }
    jbyte* receiveBuf0 = (*env)->GetByteArrayElements(env,receiveArray,NULL);//del 06
    int recvLen0 = (*env)->GetArrayLength(env,receiveArray);

    jbyte* receiveBuf;int recvLen;
    if(g_commType == TYPE_SP100){
        receiveBuf = receiveBuf0 + 1;
        recvLen = recvLen0 - 1;
        if(recvLen < 11){
            LOGD_FMT(">>>TYPE_SP100 receiveLen[%d]",recvLen);
            return -1;
        }
    }else if(g_commType == TYPE_MPOS){
        receiveBuf = receiveBuf0;
        recvLen = recvLen0;
        if(recvLen < 11){
            LOGD_FMT(">>>TYPE_MPOS receiveLen[%d]",recvLen);
            return -1;
        }
    }
    LOGD_STR("UART RECEIVE",receiveBuf,recvLen);
    uchar recvLrc =  calcLrc(receiveBuf+1,recvLen-2);
    if(recvLrc != (uchar)(receiveBuf[recvLen-1])){
        LOGD_FMT(">>>recvLrc error.[0x%x][0x%x]deviceType[%d]",recvLrc,receiveBuf[recvLen-1],g_commType);
        return -1;
    }
    int datalen = recvLen-9;
    if(recvDataLen != NULL){
        *recvDataLen = datalen;
    }
    memcpy(Recv_Data,receiveBuf+7,datalen);
    LOGD_STR("BODY",Recv_Data,datalen);
    (*env)->DeleteLocalRef(env, jsendBytes);
    (*env)->ReleaseByteArrayElements(env, receiveArray,receiveBuf0,NULL);
    if(isAttached)
        (*gJavaVM)->DetachCurrentThread(gJavaVM);
    return 1;
}

int NAPI_L3Init(char *filePath, char *config)
{
    int nRet = 0;
    unsigned int DataLen = 0;
    uchar SendData[8] = {0};
    uchar RecvData[BUFFER_LEN_SMALL] = {0};

    if(config == NULL)
    {
        return COMMAND_ERR_INVALID_PARAM;
    }

    memcpy(SendData, config, 8);
    DataLen = 8;
    nRet = Communication(MP_Init_EMV_Kernel, COMMAND_INIT_EMV_KERNEL, SendData, DataLen, RecvData,NULL);
    if(nRet < 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    if(memcmp(RecvData, "00", 2) == 0)
    {
        return COMMAND_ERR_GOOD;
    }
    else if(memcmp(RecvData, "01", 2) == 0)
    {
        return COMMAND_ERR_INVALID_PARAM;
    }
    else if(memcmp(RecvData, "02", 2) == 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    return COMMAND_ERR_INVALID_PARAM;
}

int NAPI_L3SetData(unsigned int tag, void *data, unsigned int len)
{
    int nRet = 0;
    unsigned int DataLen = 0;
    uchar *SendData = NULL;
    uchar RecvData[BUFFER_LEN_SMALL] = {0};

    if(data == NULL)
    {
        return COMMAND_ERR_INVALID_PARAM;
    }

    SendData = malloc(sizeof(uchar) * (4 + 2 + len));
    if(SendData == NULL)
    {
        LOGD_FMT("----OUT OF MEMORY---");
        return L3_ERR_OVERFLOW;
    }
    memset(SendData, 0, sizeof(uchar) * (4 + 2 + len));
    NDK_IntToC40(SendData, tag);
    NDK_IntToC20(SendData + 4, len);
    memcpy(SendData + 6, data, len);
    DataLen = 4 + 2 + len;
    nRet = Communication(MP_Set_Data, COMMAND_SET_DATA, SendData, DataLen, RecvData, NULL);
    free(SendData);
    if(nRet < 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    if(memcmp(RecvData, "00", 2) == 0)
    {
        return COMMAND_ERR_GOOD;
    }
    else if(memcmp(RecvData, "01", 2) == 0)
    {
        return COMMAND_ERR_INVALID_PARAM;
    }
    else if(memcmp(RecvData, "02", 2) == 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    return COMMAND_ERR_INVALID_PARAM;
}

int NAPI_L3GetData(int type, uchar KeyIndex, void *data, int maxLen,int *realLen)
{
    int nRet = 0;
    unsigned int DataLen = 0;
    unsigned int TagValueLen = 0;
    uchar *SendData = NULL;
    uchar RecvData[BUFFER_LEN_BIG] = {0};

    if(data == NULL)
    {
        return COMMAND_ERR_INVALID_PARAM;
    }

    SendData = malloc(sizeof(uchar) * (4 + 2 + 1));
    if(SendData == NULL)
    {
        LOGD_FMT("----OUT OF MEMORY---");
        return L3_ERR_OVERFLOW;
    }
    memset(SendData, 0, sizeof(uchar) * (4 + 2 + 1));
    SendData[0] = KeyIndex;
    NDK_IntToC40(SendData + 1, type);
    NDK_IntToC20(SendData + 5, maxLen);
    DataLen = 4 + 2 + 1;
    nRet = Communication(MP_Get_Data, COMMAND_GET_DATA, SendData, DataLen, RecvData, NULL);
    free(SendData);
    if(nRet < 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    if(memcmp(RecvData, "00", 2) == 0)
    {
        if(RecvData[2] == 0x00)
        {
            NDK_C2ToInt0(&TagValueLen, RecvData + 3);
            *realLen = TagValueLen;
            memcpy(data, RecvData + 5, TagValueLen);
            return COMMAND_ERR_GOOD;
        }
        else if(RecvData[2] == 0x01)//FAIL
        {
            return -1;
        }
        else if(RecvData[2] == 0x02)//NO EXIST
        {
            return 0;
        }
    }
    else if(memcmp(RecvData, "01", 2) == 0)
    {
        return COMMAND_ERR_INVALID_PARAM;
    }
    else if(memcmp(RecvData, "02", 2) == 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    return COMMAND_ERR_INVALID_PARAM;
}

int NAPI_L3SetTLVData(uchar *TLV_List, unsigned int len)
{
    int nRet = 0;
    unsigned int DataLen = 0;
    uchar *SendData = NULL;
    uchar RecvData[BUFFER_LEN_SMALL] = {0};

    if(TLV_List == NULL)
    {
        return COMMAND_ERR_INVALID_PARAM;
    }

    SendData = malloc(sizeof(uchar) * (2 + len));
    if(SendData == NULL)
    {
        LOGD_FMT("----OUT OF MEMORY---");
        return L3_ERR_OVERFLOW;
    }
    memset(SendData, 0, sizeof(uchar) * (2 + len));
    NDK_IntToC20(SendData, len);
    memcpy(SendData + 2, TLV_List, len);
    DataLen = 2 + len;
    nRet = Communication(MP_Set_TLV_List_Data, COMMAND_SET_TLV_LIST, SendData, DataLen, RecvData, NULL);
    free(SendData);
    if(nRet < 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    if(memcmp(RecvData, "00", 2) == 0)
    {
        return COMMAND_ERR_GOOD;
    }
    else if(memcmp(RecvData, "01", 2) == 0)
    {
        return COMMAND_ERR_INVALID_PARAM;
    }
    else if(memcmp(RecvData, "02", 2) == 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    return COMMAND_ERR_INVALID_PARAM;
}

int NAPI_L3GetTlvData(uchar *tagList,int tagListLen, unsigned int tagNum, uchar KeyIndex, uchar *tlvData, unsigned int maxLen,int ctl,int *realLen)
{
    int nRet = 0;
    unsigned int DataLen = 0;
    unsigned int TagValueLen = 0;
    uchar *SendData = NULL;
    uchar RecvData[BUFFER_LEN_BIG] = {0};

    if(tagList == NULL || tlvData == NULL)
    {
        return COMMAND_ERR_INVALID_PARAM;
    }

    SendData = malloc(sizeof(uchar) * (7 + tagListLen));
    if(SendData == NULL)
    {
        LOGD_FMT("----OUT OF MEMORY---");
        return L3_ERR_OVERFLOW;
    }
    memset(SendData, 0, sizeof(uchar) * (7 + tagListLen));
    SendData[0] = KeyIndex;
    SendData[1] = tagNum;
    NDK_IntToC20(SendData + 2, maxLen);
    SendData[4] = (uchar)ctl;
    NDK_IntToC20(SendData + 5, tagListLen);
    memcpy(SendData + 7, tagList, tagListLen);
    DataLen = 7 + tagListLen;
    nRet = Communication(MP_Get_TLV_List_Data, COMMAND_GET_TLV_LIST, SendData, DataLen, RecvData, NULL);
    free(SendData);
    if(nRet < 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    if(memcmp(RecvData, "00", 2) == 0)
    {
        if(RecvData[2] == 0x00)
        {
            NDK_C2ToInt0(&TagValueLen, RecvData + 3);
            *realLen = TagValueLen;
            memcpy(tlvData, RecvData + 5, TagValueLen);
            return COMMAND_ERR_GOOD;
        }
        else if(RecvData[2] == 0x01)
        {
            return -1;
        }
        else if(RecvData[2] == 0x02)
        {
            return 0;
        }
    }
    else if(memcmp(RecvData, "01", 2) == 0)
    {
        return COMMAND_ERR_INVALID_PARAM;
    }
    else if(memcmp(RecvData, "02", 2) == 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    return COMMAND_ERR_INVALID_PARAM;
}

int NAPI_L3SetDebugMode(int debugLV)
{
    int nRet = 0;
    unsigned int DataLen = 0;
    uchar SendData[1] = {0};
    uchar RecvData[BUFFER_LEN_SMALL] = {0};

    if(debugLV < 0)
    {
        return COMMAND_ERR_INVALID_PARAM;
    }

    SendData[0] = (uchar)debugLV;
    DataLen = 1;
    nRet = Communication(MP_Set_Debug_Mode, COMMAND_SET_DEBUG_MODE, SendData, DataLen, RecvData, NULL);
    if(nRet < 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    if(memcmp(RecvData, "00", 2) == 0)
    {
        return COMMAND_ERR_GOOD;
    }
    else if(memcmp(RecvData, "01", 2) == 0)
    {
        return COMMAND_ERR_INVALID_PARAM;
    }
    else if(memcmp(RecvData, "02", 2) == 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    return COMMAND_ERR_INVALID_PARAM;
}

int NAPI_L3GetVersion(L3_MODULE module, uchar *Version)
{
    int nRet = 0;
    unsigned int DataLen = 0;
    unsigned int VersionLen = 0;
    uchar SendData[1] = {0};
    uchar RecvData[64] = {0};

    if(Version == NULL)
    {
        return COMMAND_ERR_INVALID_PARAM;
    }

    SendData[0] = (uchar)module;
    DataLen = 1;
    nRet = Communication(MP_Get_Version, COMMAND_GET_VERSION, SendData, DataLen, RecvData, NULL);
    if(nRet < 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    if(memcmp(RecvData, "00", 2) == 0)
    {
        VersionLen = (unsigned int)RecvData[2];
        if(VersionLen<=0){
            return COMMAND_ERR_COMMAND_FAIL;
        }
        memcpy(Version, RecvData + 3, VersionLen);
        return COMMAND_ERR_GOOD;
    }
    else if(memcmp(RecvData, "01", 2) == 0)
    {
        return COMMAND_ERR_INVALID_PARAM;
    }
    else if(memcmp(RecvData, "02", 2) == 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    return COMMAND_ERR_INVALID_PARAM;
}

int NAPI_L3CancelTransaction(void)
{
    int nRet = 0;
    uchar SendBuf[50] = {0};
    
    memcpy(SendBuf, "+++CANCEL", 9);
//    nRet = NDK_PortWrite(PinpadPort, 9, SendBuf);
//    if(nRet != NDK_OK)
//    {
//        LOGD_FMT("----Send Cancel Command Error---");
//    }

    return COMMAND_ERR_GOOD;
    // if(memcmp(RecvData, "00", 2) == 0)
    // {
    //     return COMMAND_ERR_GOOD;
    // }
    // else if(memcmp(RecvData, "01", 2) == 0)
    // {
    //     return COMMAND_ERR_INVALID_PARAM;
    // }
    // else if(memcmp(RecvData, "02", 2) == 0)
    // {
    //     return COMMAND_ERR_COMMAND_FAIL;
    // }
    // return COMMAND_ERR_INVALID_PARAM;
}


///////////////////////////////////////////////////////////////////////////
//						Transaction Command    					         //
///////////////////////////////////////////////////////////////////////////

int NAPI_L3PerformTransaction(JNIEnv *env,char *data, int dataLen, jobject txnResultObj)
{
    int nRet = 0;
    unsigned int DataLen = 0;
    int Errorcode = 0;
    uchar *SendData = NULL;
    uchar RecvData[BUFFER_LEN_BIG] = {0};
    uint recvDataLen = 0;

    if(data == NULL)
    {
        return COMMAND_ERR_INVALID_PARAM;
    }

    SendData = malloc(sizeof(uchar) * (dataLen + 2));
    if(SendData == NULL)
    {
        LOGD_FMT("----OUT OF MEMORY---");
        return L3_ERR_OVERFLOW;
    }
    memset(SendData, 0, sizeof(uchar) * (dataLen + 2));

    memcpy(SendData, data, 5);
    NDK_IntToC20(SendData + 5, dataLen - 5);
    memcpy(SendData + 7, data + 5, dataLen - 5);
    DataLen = dataLen + 2;
//    PubDisplayStr(DISPLAY_MODE_CENTER, 4, 1, "PROCESSING...");
//	PubUpdateWindow();
    nRet = Communication(MP_Performed_Transaction, COMMAND_PERFORM_TRANSACTION, SendData, DataLen, RecvData,&recvDataLen);
    free(SendData);
    if(nRet < 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    jclass txnResultCls = (*env)->GetObjectClass(env, txnResultObj);
    if(txnResultCls == NULL){
        LOGD_FMT(">>>txnResultCls[%d]",txnResultCls);
        return COMMAND_ERR_INVALID_PARAM;
    }
    int index = 0;
    if(memcmp(RecvData+index, "00", 2) == 0) {
        (*env)->SetIntField(env,txnResultObj,(*env)->GetFieldID(env,txnResultCls, "returnCode", "I"),COMMAND_RESCODE_SUCC);index +=2;
        (*env)->SetIntField(env,txnResultObj,(*env)->GetFieldID(env,txnResultCls, "resultCode", "I"),*(RecvData+index));index+=1;
        (*env)->SetIntField(env,txnResultObj,(*env)->GetFieldID(env,txnResultCls, "cvmStatus", "I"),*(RecvData+index));index+=1;
        NDK_C4ToInt0((unsigned int*)&Errorcode, RecvData + index);index+=4;
        (*env)->SetIntField(env,txnResultObj,(*env)->GetFieldID(env,txnResultCls, "errorCode", "I"),Errorcode);


        int flag1F8131 = (*env)->GetIntField(env,txnResultObj,(*env)->GetFieldID(env,txnResultCls, "flag1F8131", "I"));
        int keyIndex = (*env)->GetIntField(env,txnResultObj,(*env)->GetFieldID(env,txnResultCls, "keyIndex", "I"));
        if(flag1F8131){
            uchar status = *(RecvData + index);index+=1;
            if(status == 0){//succ
                int tempLen = 0; int actualLen; int tlvLen;
                NDK_C2ToInt0((unsigned int*)&tempLen, RecvData+index);index+=2;
                if(keyIndex == 0){
                    tlvLen = tempLen;
                    (*env)->SetIntField(env,txnResultObj,(*env)->GetFieldID(env,txnResultCls, "tlvLen", "I"),tlvLen);
                } else{
                    actualLen = tempLen;
                    (*env)->SetIntField(env,txnResultObj,(*env)->GetFieldID(env,txnResultCls, "actualTlvLen", "I"),actualLen);

                    NDK_C2ToInt0((unsigned int*)&tlvLen, RecvData+index);index+=2;
                    (*env)->SetIntField(env,txnResultObj,(*env)->GetFieldID(env,txnResultCls, "tlvLen", "I"),tlvLen);
                }
                uchar tlvData[tlvLen];
                memset(tlvData, 0, sizeof(tlvData));
                memcpy(tlvData,RecvData+index,tlvLen);index+=tlvLen;
                jbyteArray tlvDataBuf = (jbyteArray)(*env)->GetObjectField(env,txnResultObj,(*env)->GetFieldID(env,txnResultCls, "tlvData", "[B"));
                (*env)->SetByteArrayRegion(env,tlvDataBuf,0,tlvLen,tlvData);
                (*env)->DeleteLocalRef(env, tlvDataBuf);

                LOGD_FMT(">>>actualLen[%d] tlvLen[%d]",actualLen,tlvLen);
                LOGD_STR("tlvData",tlvData,tlvLen);
            }
        }
        LOGE_FMT(">>>recvDataLen[%d]",recvDataLen);
        if(recvDataLen > index){
            uchar cardSchemeId = *(RecvData + index);index +=1;
            (*env)->SetIntField(env,txnResultObj,(*env)->GetFieldID(env,txnResultCls, "cardSchemeId", "I"),cardSchemeId);

            int l3tlvLen = 0;
            NDK_C4ToInt0((unsigned int*)&l3tlvLen, RecvData+index);index+=4;
            (*env)->SetIntField(env,txnResultObj,(*env)->GetFieldID(env,txnResultCls, "l3TlvLen", "I"),l3tlvLen);

            uchar l3tlvData[l3tlvLen];
            memset(l3tlvData, 0, sizeof(l3tlvData));
            memcpy(l3tlvData,RecvData+index,l3tlvLen);index+=l3tlvLen;
            jbyteArray l3tlvDataBuf = (jbyteArray)(*env)->GetObjectField(env,txnResultObj,(*env)->GetFieldID(env,txnResultCls, "l3TlvData", "[B"));
            (*env)->SetByteArrayRegion(env,l3tlvDataBuf,0,l3tlvLen,l3tlvData);
            (*env)->DeleteLocalRef(env, l3tlvDataBuf);

            LOGD_FMT(">>>cardSchemeId[%d] l3tlvLen[%d]",cardSchemeId,l3tlvLen);
            LOGD_STR("l3tlvData",l3tlvData,l3tlvLen);
        }
        return COMMAND_ERR_GOOD;
    } else if(memcmp(RecvData, "01", 2) == 0) {
        (*env)->SetIntField(env,txnResultObj,(*env)->GetFieldID(env,txnResultCls, "returnCode", "I"),COMMAND_RESCODE_INVALID_PARAM);index+=2;
        return COMMAND_ERR_INVALID_PARAM;
    }else if(memcmp(RecvData, "03", 2) == 0) {
        (*env)->SetIntField(env,txnResultObj,(*env)->GetFieldID(env,txnResultCls, "returnCode", "I"),COMMAND_RESCODE_CANCEL);index+=2;
        return COMMAND_ERR_CANCEL;
    }
    (*env)->SetIntField(env,txnResultObj,(*env)->GetFieldID(env,txnResultCls, "returnCode", "I"),COMMAND_RESCODE_FAILED);index+=2;
    return COMMAND_ERR_COMMAND_FAIL;
}

int NAPI_L3CompleteTransaction(JNIEnv *env,char *data, int dataLen, jobject txnResultObj)
{
    int nRet = 0;
    unsigned int DataLen = 0;
    int Errorcode = 0;
    uchar *SendData = NULL;
    uchar RecvData[BUFFER_LEN_BIG] = {0};

    if(data == NULL)
    {
        return COMMAND_ERR_INVALID_PARAM;
    }

    SendData = malloc(sizeof(uchar) * (dataLen + 2));
    if(SendData == NULL)
    {
        LOGD_FMT("----OUT OF MEMORY---");
        return L3_ERR_OVERFLOW;
    }
    memset(SendData, 0, sizeof(uchar) * (dataLen + 2));

    memcpy(SendData, data, 1);
    NDK_IntToC20(SendData + 1, dataLen - 1);
    memcpy(SendData + 3, data + 1, dataLen - 1);
    DataLen = dataLen + 2;
//    PubDisplayStr(DISPLAY_MODE_CENTER, 3, 1, "PROCESSING...");
//	PubUpdateWindow();
    nRet = Communication(MP_Complete_Transaction, COMMAND_COMPLETE_TRANSACTION, SendData, DataLen, RecvData, NULL);
    free(SendData);
    if(nRet < 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    jclass txnResultCls = (*env)->GetObjectClass(env, txnResultObj);
    if(txnResultCls == NULL){
        LOGD_FMT(">>>txnResultCls[%d]",txnResultCls);
        return COMMAND_ERR_INVALID_PARAM;
    }
    int index = 0;
    if(memcmp(RecvData+index, "00", 2) == 0) {
        (*env)->SetIntField(env,txnResultObj,(*env)->GetFieldID(env,txnResultCls, "returnCode", "I"),COMMAND_RESCODE_SUCC);index+=2;
        (*env)->SetIntField(env,txnResultObj,(*env)->GetFieldID(env,txnResultCls, "resultCode", "I"),*(RecvData+index));index+=1;
        NDK_C4ToInt0((unsigned int*)&Errorcode, RecvData + index);index+=4;
        (*env)->SetIntField(env,txnResultObj,(*env)->GetFieldID(env,txnResultCls, "errorCode", "I"),Errorcode);
        return COMMAND_ERR_GOOD;
    } else if(memcmp(RecvData, "01", 2) == 0) {
        (*env)->SetIntField(env,txnResultObj,(*env)->GetFieldID(env,txnResultCls, "returnCode", "I"),COMMAND_RESCODE_INVALID_PARAM);index=+2;
        return COMMAND_ERR_INVALID_PARAM;
    }
    (*env)->SetIntField(env,txnResultObj,(*env)->GetFieldID(env,txnResultCls, "returnCode", "I"),COMMAND_RESCODE_FAILED);index=+2;
    return COMMAND_ERR_COMMAND_FAIL;


}

int NAPI_L3PreProcessTransaction(JNIEnv *env,char *data, int dataLen, jintArray errorCode)
{
    int nRet = 0;
    unsigned int DataLen = 0;
    int Errorcode = 0;
    uchar *SendData = NULL;
    uchar RecvData[BUFFER_LEN_BIG] = {0};

    if(data == NULL)
    {
        return COMMAND_ERR_INVALID_PARAM;
    }

    SendData = malloc(sizeof(uchar) * (dataLen + 2));
    if(SendData == NULL)
    {
        LOGD_FMT("----OUT OF MEMORY---");
        return L3_ERR_OVERFLOW;
    }
    memset(SendData, 0, sizeof(uchar) * (dataLen + 2));

    NDK_IntToC20(SendData, dataLen);
    memcpy(SendData + 2, data, dataLen);
    DataLen = dataLen + 2;
//    PubDisplayStr(DISPLAY_MODE_CENTER, 3, 1, "PROCESSING...");
//	PubUpdateWindow();
    nRet = Communication(MP_PreProcess_Transaction, COMMAND_PREPROCESS_TRANSACTION, SendData, DataLen, RecvData, NULL);
    free(SendData);
    if(nRet < 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    if(memcmp(RecvData, "00", 2) == 0) {
        NDK_C4ToInt0((unsigned int*)&Errorcode, RecvData+2);
        (*env)->SetIntArrayRegion(env,errorCode,0,1,&Errorcode);
        LOGD_FMT(">>>Errorcode[%d]",Errorcode);
        return COMMAND_ERR_GOOD;
    } else if(memcmp(RecvData, "01", 2) == 0) {
        return COMMAND_ERR_INVALID_PARAM;
    }
    return COMMAND_ERR_COMMAND_FAIL;


}

int NAPI_L3TerminateTransaction(JNIEnv *env,jobject txnResultObj)
{
    int nRet = 0;
    unsigned int DataLen = 0;
    uchar SendData[1] = {0};
    uchar RecvData[BUFFER_LEN_SMALL] = {0};

    nRet = Communication(MP_Terminal_Transaction, COMMAND_TERMINATE_TRANSACTION, SendData, DataLen, RecvData, NULL);
    if(nRet < 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    jclass txnResultCls = (*env)->GetObjectClass(env, txnResultObj);
    if(txnResultCls == NULL){
        LOGD_FMT(">>>txnResultCls[%d]",txnResultCls);
        return COMMAND_ERR_INVALID_PARAM;
    }
    int index = 0;
    if(memcmp(RecvData+index, "00", 2) == 0) {
        (*env)->SetIntField(env,txnResultObj,(*env)->GetFieldID(env,txnResultCls, "returnCode", "I"),COMMAND_RESCODE_SUCC);index+=2;
        return COMMAND_ERR_GOOD;
    } else if(memcmp(RecvData, "01", 2) == 0) {
        (*env)->SetIntField(env,txnResultObj,(*env)->GetFieldID(env,txnResultCls, "returnCode", "I"),COMMAND_RESCODE_INVALID_PARAM);index+=2;
        return COMMAND_ERR_INVALID_PARAM;
    }
    (*env)->SetIntField(env,txnResultObj,(*env)->GetFieldID(env,txnResultCls, "returnCode", "I"),COMMAND_RESCODE_FAILED);index+=2;
    return COMMAND_ERR_COMMAND_FAIL;
}

///////////////////////////////////////////////////////////////////////////
//						EMV Configuration Command    					 //
///////////////////////////////////////////////////////////////////////////

int NAPI_L3LoadTerminalConfig(L3_CARD_INTERFACE interface, unsigned char tlv_list[], int *tlv_len, L3_CONFIG_OP mode)
{
    int nRet = 0;
    unsigned int DataLen = 0;
    uchar *SendData = NULL;
    uchar RecvData[BUFFER_LEN_BIG] = {0};

    if(tlv_len == NULL || tlv_list == NULL)
    {
        return COMMAND_ERR_INVALID_PARAM;
    }

    
    switch (mode)
    {
    case CONFIG_UPT:
        SendData = malloc(sizeof(uchar) * ((*tlv_len) + 3));
        if(SendData == NULL)
        {
            LOGD_FMT("----OUT OF MEMORY---");
            return L3_ERR_OVERFLOW;
        }
        memset(SendData, 0, sizeof(uchar) * ((*tlv_len) + 3));
        memcpy(SendData, &interface, 1);
        NDK_IntToC20(SendData + 1, *tlv_len);
        memcpy(SendData + 3, tlv_list, *tlv_len);
        DataLen = *tlv_len + 3;
        nRet = Communication(MP_Update_Terminal, COMMAND_TERMINAL_CONFIG_UPDATE, SendData, DataLen, RecvData, NULL);
        break;
    case CONFIG_GET:
        SendData = malloc(sizeof(uchar) * 1);
        if(SendData == NULL)
        {
            LOGD_FMT("----OUT OF MEMORY---");
            return L3_ERR_OVERFLOW;
        }
        memset(SendData, 0, sizeof(uchar) * 1);

        memcpy(SendData, &interface, 1);
        DataLen = 1;
        nRet = Communication(MP_Get_Terminal, COMMAND_TERMINAL_CONFIG_GET, SendData, DataLen, RecvData, NULL);
        break;
    default:
        return COMMAND_ERR_INVALID_PARAM;
    }

    free(SendData);
    if(nRet < 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    if(memcmp(RecvData, "00", 2) == 0)
    {
        if(mode == CONFIG_GET)
        {
            NDK_C2ToInt0(tlv_len, RecvData + 2);
            memcpy(tlv_list, RecvData + 4, *tlv_len);
        }
        return COMMAND_ERR_GOOD;
    }
    else if(memcmp(RecvData, "01", 2) == 0)
    {
        return COMMAND_ERR_INVALID_PARAM;
    }
    else if(memcmp(RecvData, "02", 2) == 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    return COMMAND_ERR_INVALID_PARAM;
}

int NAPI_L3LoadAIDConfig(L3_CARD_INTERFACE interface, L3_AID_ENTRY *aidEntry, unsigned char tlv_list[], int *tlv_len, L3_CONFIG_OP mode)
{
    int nRet = 0;
    unsigned int DataLen = 0;
    uchar *SendData = NULL;
    uchar RecvData[BUFFER_LEN_BIG] = {0};

    if(mode == CONFIG_UPT && (tlv_len == NULL || tlv_list == NULL)){
        return COMMAND_ERR_INVALID_PARAM;
    } else if((mode == CONFIG_GET||mode == CONFIG_RMV) && aidEntry == NULL){
        return COMMAND_ERR_INVALID_PARAM;
    }
    switch (mode)
    {
    case CONFIG_UPT:
        SendData = malloc(sizeof(uchar) * ((*tlv_len) + 3));
        if(SendData == NULL)
        {
            LOGD_FMT("----OUT OF MEMORY---");
            return L3_ERR_OVERFLOW;
        }
        memset(SendData, 0, sizeof(uchar) * ((*tlv_len) + 3));

        memcpy(SendData, &interface, 1);
        DataLen += 1;
        NDK_IntToC20(SendData + DataLen, *tlv_len);
        DataLen += 2;
        memcpy(SendData + DataLen, tlv_list, *tlv_len);
        DataLen += *tlv_len;
        nRet = Communication(MP_Update_AID, COMMAND_AID_CONFIG_UPDATE, SendData, DataLen, RecvData, NULL);
        break;
    case CONFIG_GET:
        if(aidEntry == NULL)
        {
            return COMMAND_ERR_INVALID_PARAM;
        }
        SendData = malloc(sizeof(uchar) * (1 + 27));
        if(SendData == NULL)
        {
            LOGD_FMT("----OUT OF MEMORY---");
            return L3_ERR_OVERFLOW;
        }
        memset(SendData, 0, sizeof(uchar) * (1 + 27));

        memcpy(SendData, &interface, 1);
        DataLen += 1;
        memcpy(SendData + DataLen, aidEntry->aid, 16);
        DataLen += 16;
        memcpy(SendData + DataLen, &(aidEntry->aidLen), 1);
        DataLen += 1;
        memcpy(SendData + DataLen, aidEntry->kernelId, 8);
        DataLen += 8;
        memcpy(SendData + DataLen, &(aidEntry->externCheckFlag), 1);
        DataLen += 1;
        memcpy(SendData + DataLen, &(aidEntry->transactionType), 1);
        DataLen += 1;
        nRet = Communication(MP_Get_AID, COMMAND_AID_CONFIG_GET, SendData, DataLen, RecvData, NULL);
        break;
    case CONFIG_RMV:
        SendData = malloc(sizeof(uchar) * (1 + 27));
        if(SendData == NULL)
        {
            LOGD_FMT("----OUT OF MEMORY---");
            return L3_ERR_OVERFLOW;
        }
        memset(SendData, 0, sizeof(uchar) * (1 + 27));

        memcpy(SendData, &interface, 1);
        DataLen += 1;
        memcpy(SendData + DataLen, aidEntry->aid, 16);
        DataLen += 16;
        memcpy(SendData + DataLen, &(aidEntry->aidLen), 1);
        DataLen += 1;
        memcpy(SendData + DataLen, aidEntry->kernelId, 8);
        DataLen += 8;
        memcpy(SendData + DataLen, &(aidEntry->externCheckFlag), 1);
        DataLen += 1;
        memcpy(SendData + DataLen, &(aidEntry->transactionType), 1);
        DataLen += 1;
        nRet = Communication(MP_Remove_AID, COMMAND_AID_CONFIG_REMOVE_ONE, SendData, DataLen, RecvData, NULL);
        break;
    case CONFIG_FLUSH:
        SendData = malloc(sizeof(uchar) * 1);
        if(SendData == NULL)
        {
            LOGD_FMT("----OUT OF MEMORY---");
            return L3_ERR_OVERFLOW;
        }
        memset(SendData, 0, sizeof(uchar) * 1);

        memcpy(SendData, &interface, 1);
        DataLen += 1;
        nRet = Communication(MP_Remove_ALL_AID, COMMAND_AID_CONFIG_REMOVE_ALL, SendData, DataLen, RecvData, NULL);
        break;
    default:
        return COMMAND_ERR_INVALID_PARAM;
    }

    free(SendData);
    if(nRet < 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    if(memcmp(RecvData, "00", 2) == 0)
    {
        if(mode == CONFIG_GET)
        {
            NDK_C2ToInt0(tlv_len, RecvData + 2);
            memcpy(tlv_list, RecvData + 4, *tlv_len);
        }
        return COMMAND_ERR_GOOD;
    }
    else if(memcmp(RecvData, "01", 2) == 0)
    {
        return COMMAND_ERR_INVALID_PARAM;
    }
    else if(memcmp(RecvData, "02", 2) == 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    return COMMAND_ERR_INVALID_PARAM;
}

int NAPI_L3LoadCAPK(L3_CAPK_ENTRY *capk, L3_CONFIG_OP mode)
{
    int nRet = 0;
    unsigned int DataLen = 0;
    uchar *SendData = NULL;
    uchar RecvData[BUFFER_LEN_BIG] = {0};

    if(capk == NULL && mode != CONFIG_FLUSH)
    {
        return COMMAND_ERR_INVALID_PARAM;
    }
    
    switch (mode)
    {
    case CONFIG_UPT:
        SendData = malloc(sizeof(uchar) * (248+1+3+20+4+5+1+1+1));
        if(SendData == NULL)
        {
            LOGD_FMT("----OUT OF MEMORY---");
            return L3_ERR_OVERFLOW;
        }
        memset(SendData, 0, sizeof(uchar) * (248+1+3+20+4+5+1+1+1));

        memcpy(SendData + DataLen, capk->pkModulus, 248);
        DataLen += 248;
        memcpy(SendData + DataLen, &(capk->pkModulusLen), 1);
        DataLen += 1;
        memcpy(SendData + DataLen, capk->pkExponent, 3);
        DataLen += 3;
        memcpy(SendData + DataLen, capk->hashValue, 20);
        DataLen += 20;
        memcpy(SendData + DataLen, capk->expiredDate, 4);
        DataLen += 4;
        memcpy(SendData + DataLen, capk->rid, 5);
        DataLen += 5;
        memcpy(SendData + DataLen, &(capk->index), 1);
        DataLen += 1;
        memcpy(SendData + DataLen, &(capk->pkAlgorithmIndicator), 1);
        DataLen += 1;
        memcpy(SendData + DataLen, &(capk->hashAlgorithmIndicator), 1);
        DataLen += 1;
        nRet = Communication(MP_Update_CAPK, COMMAND_CAPK_UPDATE, SendData, DataLen, RecvData, NULL);
        break;
    case CONFIG_GET:
        SendData = malloc(sizeof(uchar) * (5+1));
        if(SendData == NULL)
        {
            LOGD_FMT("----OUT OF MEMORY---");
            return L3_ERR_OVERFLOW;
        }
        memset(SendData, 0, sizeof(uchar) * (5+1));

        memcpy(SendData + DataLen, capk->rid, 5);
        DataLen += 5;
        memcpy(SendData + DataLen, &(capk->index), 1);
        DataLen += 1;
        nRet = Communication(MP_Get_CAPK, COMMAND_CAPK_GET, SendData, DataLen, RecvData, NULL);
        break;
    case CONFIG_RMV:
        SendData = malloc(sizeof(uchar) * (5+1));
        if(SendData == NULL)
        {
            LOGD_FMT("----OUT OF MEMORY---");
            return L3_ERR_OVERFLOW;
        }
        memset(SendData, 0, sizeof(uchar) * (5+1));

        memcpy(SendData + DataLen, capk->rid, 5);
        DataLen += 5;
        memcpy(SendData + DataLen, &(capk->index), 1);
        DataLen += 1;
        nRet = Communication(MP_Remove_One_CAPK, COMMAND_CAPK_REMOVE_ONE, SendData, DataLen, RecvData, NULL);
        break;
    case CONFIG_FLUSH:
        SendData = malloc(sizeof(uchar) * 1);
        if(SendData == NULL)
        {
            LOGD_FMT("----OUT OF MEMORY---");
            return L3_ERR_OVERFLOW;
        }
        memset(SendData, 0, sizeof(uchar) * 1);

        nRet = Communication(MP_Remove_ALL_CAPK, COMMAND_CAPK_REMOVE_ALL, SendData, DataLen, RecvData, NULL);
        break;
    default:
        return COMMAND_ERR_INVALID_PARAM;
    }

    free(SendData);
    if(nRet < 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    if(memcmp(RecvData, "00", 2) == 0)
    {
        if(mode == CONFIG_GET)
        {
            int Offset = 2;
            memcpy(capk->pkModulus, RecvData + Offset, 248);
            Offset += 248;
            memcpy(&(capk->pkModulusLen), RecvData + Offset, 1);
            Offset += 1;
            memcpy(capk->pkExponent, RecvData + Offset, 3);
            Offset += 3;
            memcpy(capk->hashValue, RecvData + Offset, 20);
            Offset += 20;
            memcpy(capk->expiredDate, RecvData + Offset, 4);
            Offset += 4;
            memcpy(capk->rid, RecvData + Offset, 5);
            Offset += 5;
            memcpy(&(capk->index), RecvData + Offset, 1);
            Offset += 1;
            memcpy(&(capk->pkAlgorithmIndicator), RecvData + Offset, 1);
            Offset += 1;
            memcpy(&(capk->hashAlgorithmIndicator), RecvData + Offset, 1);
            Offset += 1;
        }
        return COMMAND_ERR_GOOD;
    }
    else if(memcmp(RecvData, "01", 2) == 0)
    {
        return COMMAND_ERR_INVALID_PARAM;
    }
    else if(memcmp(RecvData, "02", 2) == 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    return COMMAND_ERR_INVALID_PARAM;
}

//证书
int NAPI_L3LoadRevocationList(L3_CRL_ENTRY *crl, L3_CONFIG_OP mode)
{
    int nRet = 0;
    unsigned int DataLen = 0;
    uchar *SendData = NULL;
    uchar RecvData[BUFFER_LEN_BIG] = {0};

    if(crl == NULL && mode != CONFIG_FLUSH)
    {
        return COMMAND_ERR_INVALID_PARAM;
    }
    
    switch (mode)
    {
    case CONFIG_UPT:
        SendData = malloc(sizeof(uchar) * (5+1+3));
        if(SendData == NULL)
        {
            LOGD_FMT("----OUT OF MEMORY---");
            return L3_ERR_OVERFLOW;
        }
        memset(SendData, 0, sizeof(uchar) * (5+1+3));

        memcpy(SendData + DataLen, crl->rid, 5);
        DataLen += 5;
        memcpy(SendData + DataLen, &(crl->index), 1);
        DataLen += 1;
        memcpy(SendData + DataLen, crl->csn, 3);
        DataLen += 3;
        nRet = Communication(Rquest_ContactlessCommand, COMMAND_CERT_BLACK_UPDATE, SendData, DataLen, RecvData, NULL);
        break;
    case CONFIG_GET:
        SendData = malloc(sizeof(uchar) * (5+1+3));
        if(SendData == NULL)
        {
            LOGD_FMT("----OUT OF MEMORY---");
            return L3_ERR_OVERFLOW;
        }
        memset(SendData, 0, sizeof(uchar) * (5+1+3));

        memcpy(SendData + DataLen, crl->rid, 5);
        DataLen += 5;
        memcpy(SendData + DataLen, &(crl->index), 1);
        DataLen += 1;
        memcpy(SendData + DataLen, crl->csn, 3);
        DataLen += 3;
        nRet = Communication(Rquest_ContactlessCommand, COMMAND_CERT_BLACK_GET, SendData, DataLen, RecvData, NULL);
        break;
    case CONFIG_RMV:
        SendData = malloc(sizeof(uchar) * (5+1+3));
        if(SendData == NULL)
        {
            LOGD_FMT("----OUT OF MEMORY---");
            return L3_ERR_OVERFLOW;
        }
        memset(SendData, 0, sizeof(uchar) * (5+1+3));

        memcpy(SendData + DataLen, crl->rid, 5);
        DataLen += 5;
        memcpy(SendData + DataLen, &(crl->index), 1);
        DataLen += 1;
        memcpy(SendData + DataLen, crl->csn, 3);
        DataLen += 3;
        nRet = Communication(Rquest_ContactlessCommand, COMMAND_CERT_BLACK_REMOVE_ONE, SendData, DataLen, RecvData, NULL);
        break;
    case CONFIG_FLUSH:
        SendData = malloc(sizeof(uchar) * 1);
        if(SendData == NULL)
        {
            LOGD_FMT("----OUT OF MEMORY---");
            return L3_ERR_OVERFLOW;
        }
        memset(SendData, 0, sizeof(uchar) * 1);

        nRet = Communication(Rquest_ContactlessCommand, COMMAND_CERT_BLACK_REMOVE_ALL, SendData, DataLen, RecvData, NULL);
        break;
    default:
        return COMMAND_ERR_INVALID_PARAM;
    }

    free(SendData);
    if(nRet < 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    if(memcmp(RecvData, "00", 2) == 0)
    {
        if(mode == CONFIG_GET)
        {
            if(RecvData[2] == 0x01)     //Not Find
            {
                return 0;
            }
            else
            {
                return 1;               //Find
            }
            
        }
        return COMMAND_ERR_GOOD;
    }
    else if(memcmp(RecvData, "01", 2) == 0)
    {
        return COMMAND_ERR_INVALID_PARAM;
    }
    else if(memcmp(RecvData, "02", 2) == 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    return COMMAND_ERR_INVALID_PARAM;
}

int NAPI_L3GetAIDCount(JNIEnv *env,jint cardIntf,jintArray len,jbyteArray data)
{
    int nRet;
    uchar SendData[1];
    int DataLen = 0;
    uchar RecvData[BUFFER_LEN_BIG];
    uint recvDataLen=0;

    memset(SendData, 0, sizeof(SendData));
    memset(RecvData, 0, sizeof(RecvData));
    SendData[0] = cardIntf;
    DataLen += 1;
    nRet = Communication(MP_Get_AID_Count, COMMAND_AID_GET_COUNT, SendData, DataLen, RecvData, &recvDataLen);
    if(nRet < 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    if(memcmp(RecvData, "00", 2) == 0) {
        int dataLen = recvDataLen - 2;
        (*env)->SetIntArrayRegion(env,len,0,1,&dataLen);
        (*env)->SetByteArrayRegion(env,data,0,dataLen,RecvData+2);
        return COMMAND_ERR_GOOD;
    } else if(memcmp(RecvData, "01", 2) == 0) {
        return COMMAND_ERR_INVALID_PARAM;
    }
    return COMMAND_ERR_COMMAND_FAIL;

}

int NAPI_L3GetCAPKCount(JNIEnv *env,jintArray len,jbyteArray numRidIndex)
{
    int nRet;
    uchar SendData[1];
    int DataLen = 0;
    uchar RecvData[BUFFER_LEN_BIG];
    uint recvDataLen=0;

    memset(SendData, 0, sizeof(SendData));
    memset(RecvData, 0, sizeof(RecvData));

    nRet = Communication(MP_Get_CAPK_Count, COMMAND_CAPK_GET_COUNT, SendData, DataLen, RecvData, &recvDataLen);
    if(nRet < 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    if(memcmp(RecvData, "00", 2) == 0) {
        int dataLen = recvDataLen - 2;
        (*env)->SetIntArrayRegion(env,len,0,1,&dataLen);
        (*env)->SetByteArrayRegion(env,numRidIndex,0,dataLen,RecvData+2);
        return COMMAND_ERR_GOOD;
    } else if(memcmp(RecvData, "01", 2) == 0) {
        return COMMAND_ERR_INVALID_PARAM;
    }
    return COMMAND_ERR_COMMAND_FAIL;
}
//card
int NAPI_L3LoadExceptionList(L3_EXCEPTION_FILE_ENTRY *exceptionList, L3_CONFIG_OP mode)
{
    int nRet = 0;
    unsigned int DataLen = 0;
    uchar *SendData = NULL;
    uchar RecvData[BUFFER_LEN_BIG] = {0};

    if(exceptionList == NULL && mode != CONFIG_FLUSH)
    {
        return COMMAND_ERR_INVALID_PARAM;
    }
    
    switch (mode)
    {
    case CONFIG_UPT:
        SendData = malloc(sizeof(uchar) * (10+1+1));
        if(SendData == NULL)
        {
            LOGD_FMT("----OUT OF MEMORY---");
            return L3_ERR_OVERFLOW;
        }
        memset(SendData, 0, sizeof(uchar) * (10+1+1));

        memcpy(SendData + DataLen, exceptionList->pan, 10);
        DataLen += 10;
        memcpy(SendData + DataLen, &(exceptionList->panLen), 1);
        DataLen += 1;
        memcpy(SendData + DataLen, &(exceptionList->panSN), 1);
        DataLen += 1;
        nRet = Communication(Rquest_ContactlessCommand, COMMAND_CARD_BLACK_UPDATE, SendData, DataLen, RecvData, NULL);
        break;
    case CONFIG_GET:
        SendData = malloc(sizeof(uchar) * (10+1+1));
        if(SendData == NULL)
        {
            LOGD_FMT("----OUT OF MEMORY---");
            return L3_ERR_OVERFLOW;
        }
        memset(SendData, 0, sizeof(uchar) * (10+1+1));

        memcpy(SendData + DataLen, exceptionList->pan, 10);
        DataLen += 10;
        memcpy(SendData + DataLen, &(exceptionList->panLen), 1);
        DataLen += 1;
        memcpy(SendData + DataLen, &(exceptionList->panSN), 1);
        DataLen += 1;
        nRet = Communication(Rquest_ContactlessCommand, COMMAND_CARD_BLACK_GET, SendData, DataLen, RecvData, NULL);
        break;
    case CONFIG_RMV:
        SendData = malloc(sizeof(uchar) * (10+1+1));
        if(SendData == NULL)
        {
            LOGD_FMT("----OUT OF MEMORY---");
            return L3_ERR_OVERFLOW;
        }
        memset(SendData, 0, sizeof(uchar) * (10+1+1));

        memcpy(SendData + DataLen, exceptionList->pan, 10);
        DataLen += 10;
        memcpy(SendData + DataLen, &(exceptionList->panLen), 1);
        DataLen += 1;
        memcpy(SendData + DataLen, &(exceptionList->panSN), 1);
        DataLen += 1;
        nRet = Communication(Rquest_ContactlessCommand, COMMAND_CARD_BLACK_REMOVE_ONE, SendData, DataLen, RecvData, NULL);
        break;
    case CONFIG_FLUSH:
        SendData = malloc(sizeof(uchar) * 1);
        if(SendData == NULL)
        {
            LOGD_FMT("----OUT OF MEMORY---");
            return L3_ERR_OVERFLOW;
        }
        memset(SendData, 0, sizeof(uchar) * 1);

        nRet = Communication(Rquest_ContactlessCommand, COMMAND_CARD_BLACK_REMOVE_ALL, SendData, DataLen, RecvData, NULL);
        break;
    default:
        return COMMAND_ERR_INVALID_PARAM;
    }

    free(SendData);
    if(nRet < 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    if(memcmp(RecvData, "00", 2) == 0)
    {
        if(mode == CONFIG_GET)
        {
            if(RecvData[2] == 0x01)     //Not Find
            {
                return 0;
            }
            else
            {
                return 1;               //Find
            }
            
        }
        return COMMAND_ERR_GOOD;
    }
    else if(memcmp(RecvData, "01", 2) == 0)
    {
        return COMMAND_ERR_INVALID_PARAM;
    }
    else if(memcmp(RecvData, "02", 2) == 0)
    {
        return COMMAND_ERR_COMMAND_FAIL;
    }
    return COMMAND_ERR_INVALID_PARAM;
}

/*
int Command_Test_2(void)
{
    int nRet = 0;
    int nLen = 0;
    uchar usTLV[50] = {0};
    uchar usBuf[1500] = {0};
    EntryLRC crl;
    EntryNoitpecxe stException;
	int nSelcItem = 1, nStartItem = 1;
	char *pszItems[] = {
        tr("1.Cert Black Get"),
		tr("2.Cert Black Remove"),
		tr("3.Cert Black Flush"),
		tr("4.Card Black Upt"),
		tr("5.Card Black Get"),
		tr("6.Card Black Remove"),
		tr("7.Card Black Flush"),
		tr("8.Set TLV List"),
		tr("9.Get TLV List"),
	};
	
	while(1)
	{
 		nRet = PubShowMenuItems(tr("Command Test"), pszItems, sizeof(pszItems)/sizeof(char *), &nSelcItem, &nStartItem,60);
		if (nRet==APP_QUIT || nRet==APP_TIMEOUT)
		{
			return nRet;
		}
		switch(nSelcItem)
		{ 
        case 1:
			memset(&crl, 0, sizeof(EntryLRC));
            memcpy(crl.rid, "\xA0\x00\x00\x00\x25", 5);
            crl.index = 0x98;
            memcpy(crl.csn, "\x00\x00\x10", 3);
            nRet = NAPI_L3LoadRevocationList(&crl, CONFIG_GET);
            LOGD_FMT("NAPI_L3LoadRevocationList CONFIG_GET nRet = %d", nRet);
			break;
		case 2:
            memset(&crl, 0, sizeof(EntryLRC));
            memcpy(crl.rid, "\xA0\x00\x00\x00\x25", 5);
            crl.index = 0x98;
            memcpy(crl.csn, "\x00\x00\x10", 3);
            nRet = NAPI_L3LoadRevocationList(&crl, CONFIG_RMV);
            LOGD_FMT("NAPI_L3LoadRevocationList CONFIG_RMV nRet = %d", nRet);
			break;
		case 3:
			memset(&crl, 0, sizeof(EntryLRC));
            nRet = NAPI_L3LoadRevocationList(&crl, CONFIG_FLUSH);
            LOGD_FMT("NAPI_L3LoadRevocationList CONFIG_FLUSH nRet = %d", nRet);
			break;	
		case 4:
			memset(&stException, 0, sizeof(EntryNoitpecxe));
            memcpy(stException.pan, "\x34\x51\x58\x22\x73\x68\x02\x85\x31\x0F", 10);
            stException.panLen = 10;
            stException.panSN = 0x25;
            nRet = NAPI_L3LoadExceptionList(&stException, CONFIG_UPT);
            LOGD_FMT("NAPI_L3LoadExceptionList CONFIG_UPT nRet = %d", nRet);
			break;
		case 5:
            memset(&stException, 0, sizeof(EntryNoitpecxe));
            memcpy(stException.pan, "\x34\x51\x58\x22\x73\x68\x02\x85\x31\x0F", 10);
            stException.panLen = 10;
            stException.panSN = 0x25;
            nRet = NAPI_L3LoadExceptionList(&stException, CONFIG_GET);
            LOGD_FMT("NAPI_L3LoadExceptionList CONFIG_GET nRet = %d", nRet);
			break;
		case 6:
            memset(&stException, 0, sizeof(EntryNoitpecxe));
            memcpy(stException.pan, "\x34\x51\x58\x22\x73\x68\x02\x85\x31\x0F", 10);
            stException.panLen = 10;
            stException.panSN = 0x25;
            nRet = NAPI_L3LoadExceptionList(&stException, CONFIG_RMV);
            LOGD_FMT("NAPI_L3LoadExceptionList CONFIG_RMV nRet = %d", nRet);
			break;
		case 7:
			memset(&stException, 0, sizeof(EntryNoitpecxe));
            nRet = NAPI_L3LoadExceptionList(&stException, CONFIG_FLUSH);
            LOGD_FMT("NAPI_L3LoadExceptionList CONFIG_FLUSH nRet = %d", nRet);
			break;
		case 8:
			memset(usBuf, 0, sizeof(usBuf));
            memcpy(usBuf, "\x82\x02\x30\x00\x94\x08\x08\x01\x02\x00\x10\x01\x02\x01\x9F\x36\x02\x00\x03\x57\x13\x62\x28\x00\x01\x00\x00\x11\x17\xD2\x01\x21\x20\x00\x12\x33\x99\x00\x03\x1F\x9F\x10\x13\x07\x01\x01\x03\x90\x00\x00\x01\x0A\x01\x00\x00\x02\x00\x00\xAA\xBB\xCC\xDD\x9F\x26\x08\xAA\xBB\xCC\xDD\xEE\xFF\x11\x22\x5F\x34\x01\x01\x9F\x6C\x02\x20\x00\x9F\x5D\x06\x00\x00\x00\x01\x00\x00\x9F\x4B\x81\x80\x7A\x21\x03\xF8\xAA\xE4\xA1\x88\xCA\x7F\x58\x4A\x0E\x2E\x12\xF2\x14\x51\x86\x05\xCF\x06\x0D\x98\x48\xF7\xC0\xE1\x8B\x75\xB9\x9C\x02\x91\xAF\xDD\x55\xCA\x35\xFA\xAE\xA9\x8A\xDC\x42\x3A\x85\x23\xF6\x21\x7C\xAD\x4E\x21\x54\x07\xF7\xC3\xE2\x2F\xB5\x2E\x44\x40\xBF\xA9\x95\x3C\x01\xC3\x2E\xB9\x5C\xD5\x43\x60\xC2\xC1\x4A\x18\x7B\x85\xED\x46\x95\x3B\x41\x95\x4C\x94\x61\x1D\x1C\x28\x8E\xDA\x2B\xDA\x4F\x19\x7B\xBE\x18\xE6\xD6\xF7\x74\x49\xFA\x18\x3E\xD3\xD2\x8F\xC6\x2E\x1B\xE4\x6B\x7B\x12\x5B\x18\xFD\x88\xC4\x03\x83", 223);
            nLen = 223;
			nRet = NAPI_L3SetTLVData(usBuf, nLen);
            LOGD_FMT("NAPI_L3SetTLVData nRet = %d", nRet);
			break;
		case 9:
            memset(usBuf, 0, sizeof(usBuf));
            memset(usTLV, 0, sizeof(usTLV));
            memcpy(usTLV, "\x82\x94\x9F\x36\x57\x9F\x10\x9F\x26\x5F\x34\x9F\x6C\x9F\x5D\x9F\x4B", 17);
			nRet = NAPI_L3GetTlvData(usTLV, 10, 0, usBuf, sizeof(usBuf), 0);
            LOGD_FMT("NAPI_L3GetTlvData nRet = %d", nRet);
			break;
		
		default:
			break;
		}
	}
	return APP_SUCC;
}

int Command_Test_1(void)
{
    int nRet = 0;
    int nLen = 0;
    uchar usBuf[1500] = {0};
    EntryDIA stAidEntry;
    EntryKPAC stCapk;
    EntryLRC crl;
	int nSelcItem = 1, nStartItem = 1;
	char *pszItems[] = {
		tr("1.Term Config_Get"),
		tr("2.AID Config_Get"),
		tr("3.AID Config_Remove"),
		tr("4.AID Config_Flush"),
		tr("5.CAPK Config_Get"),
		tr("6.CAPK Config_Remove"),
		tr("7.CAPK Config_Flush"),
		tr("8.Cert Black Upt"),
		tr("9.COMMAND TEST 2"),
	};
	
	while(1)
	{
 		nRet = PubShowMenuItems(tr("Command Test"), pszItems, sizeof(pszItems)/sizeof(char *), &nSelcItem, &nStartItem,60);
		if (nRet==APP_QUIT || nRet==APP_TIMEOUT)
		{
			return nRet;
		}
		switch(nSelcItem)
		{ 
		case 1:
            memset(usBuf, 0, sizeof(usBuf));
            nLen = 0;
			nRet = NAPI_L3LoadTerminalConfig(L3_CONTACTLESS, usBuf, &nLen, CONFIG_GET);
            LOGD_FMT("NAPI_L3LoadTerminalConfig Config_Get nRet = %d", nRet);
			break;
		case 2:
			memset(usBuf, 0, sizeof(usBuf));
            memset(&stAidEntry, 0, sizeof(EntryDIA));
            nLen = 0;
            memcpy(stAidEntry.aid, "\xA0\x00\x00\x00\x25\x01", 6);
            stAidEntry.aidLen = 6;
            memcpy(stAidEntry.kernelId, "\x04\x00\x00\x00\x00\x00\x00\x00", 6);
			nRet = NAPI_L3LoadAIDConfig(L3_CONTACTLESS, &stAidEntry, usBuf, &nLen, CONFIG_GET);
            LOGD_FMT("NAPI_L3LoadAIDConfig Config_Get nRet = %d", nRet);
			break;	
		case 3:
			memset(usBuf, 0, sizeof(usBuf));
            memset(&stAidEntry, 0, sizeof(EntryDIA));
            nLen = 0;
            memcpy(stAidEntry.aid, "\xA0\x00\x00\x00\x25\x01", 6);
            stAidEntry.aidLen = 6;
            memcpy(stAidEntry.kernelId, "\x04\x00\x00\x00\x00\x00\x00\x00", 6);
			nRet = NAPI_L3LoadAIDConfig(L3_CONTACTLESS, &stAidEntry, usBuf, &nLen, CONFIG_RMV);
            LOGD_FMT("NAPI_L3LoadAIDConfig Config_Remove nRet = %d", nRet);
			break;
		case 4:
            memset(usBuf, 0, sizeof(usBuf));
            memset(&stAidEntry, 0, sizeof(EntryDIA));
            nLen = 0;
			nRet = NAPI_L3LoadAIDConfig(L3_CONTACTLESS, &stAidEntry, usBuf, &nLen, CONFIG_FLUSH);
            LOGD_FMT("NAPI_L3LoadAIDConfig Config_Remove_All nRet = %d", nRet);
			break;
		case 5:
            memset(&stCapk, 0, sizeof(EntryKPAC));
            memcpy(stCapk.rid, "\xA0\x00\x00\x00\x25", 5);
            stCapk.index = 0x98;
			nRet = NAPI_L3LoadCAPK(&stCapk, CONFIG_GET);
            LOGD_FMT("NAPI_L3LoadCAPK CONFIG_GET nRet = %d", nRet);
			break;
		case 6:
			memset(&stCapk, 0, sizeof(EntryKPAC));
            memcpy(stCapk.rid, "\xA0\x00\x00\x00\x25", 5);
            stCapk.index = 0x98;
			nRet = NAPI_L3LoadCAPK(&stCapk, CONFIG_RMV);
            LOGD_FMT("NAPI_L3LoadCAPK CONFIG_RMV nRet = %d", nRet);
			break;
		case 7:
			memset(&stCapk, 0, sizeof(EntryKPAC));
			nRet = NAPI_L3LoadCAPK(&stCapk, CONFIG_FLUSH);
            LOGD_FMT("NAPI_L3LoadCAPK CONFIG_FLUSH nRet = %d", nRet);
			break;
		case 8:
			memset(&crl, 0, sizeof(EntryLRC));
            memcpy(crl.rid, "\xA0\x00\x00\x00\x25", 5);
            crl.index = 0x98;
            memcpy(crl.csn, "\x00\x00\x10", 3);
            nRet = NAPI_L3LoadRevocationList(&crl, CONFIG_UPT);
            LOGD_FMT("NAPI_L3LoadRevocationList CONFIG_UPT nRet = %d", nRet);
			break;
		case 9:
			Command_Test_2();
			break;
		default:
			break;
		}
	}
	return APP_SUCC;
}

typedef unsigned int uint32;
#define OP_ONLINE_PIN                       0x20
#define PIN_PAD_PRESENT_BUT_PIN_NOT_ENTERED         0x0208
#define ONLINE_PIN_ENTERED                          0x0204

static int SetTvr(int nOffSet)
{
    int nRet = 0;
    uchar TVR[5] = {0};
    nRet = NAPI_L3GetData(_EMVPARAM_95_TVR, 0, TVR, 5);
    if(nRet != 5)
    {
        return L3_ERR_FAIL;
    }
	TVR[(uint32)nOffSet>> 8] |= ((uint32)nOffSet & 0x00FF);
    return 0;
}

static int UnsetTvr(int nOffSet)
{
    int nRet = 0;
    uchar TVR[5] = {0};
    nRet = NAPI_L3GetData(_EMVPARAM_95_TVR, 0, TVR, 5);
    if(nRet != 5)
    {
        return L3_ERR_FAIL;
    }
	TVR[((uint32)(nOffSet)) >> 8] &= ~(((uint32)(nOffSet)) & 0x00FF);
    return 0;
}
*/

/*
int Start_Pin_Entry(void)
{
    int nRet = 0;
    uchar ICS[7] = {0};
    uchar ucOPCvm = 0;

    LOGD_FMT("Start_Pin_Entry");
    nRet = NAPI_L3GetData(L3_DATA_ONLINE_PIN, 0, &ucOPCvm, 1);
    if(nRet != 1)
    {
        LOGD_FMT("NAPI_L3GetData L3_DATA_ONLINE_PIN nRet = %d", nRet);
        return L3_ERR_SUCC;
    }
    LOGD_FMT("L3_DATA_ONLINE_PIN: %d", ucOPCvm);
    if(ucOPCvm != OP_ONLINE_PIN)
    {
        return L3_ERR_SUCC;
    }

    while (1)
    {
        nRet = Func_GET_PIN(L3_PIN_ONLINE, 0, NULL, NULL);
        if(nRet == L3_ERR_BYPASS)
        {
            nRet = NAPI_L3GetData(_EMVPARAM_DF24_ICS, 0, ICS, 7);
            if(nRet != 7)
            {
                LOGD_FMT("NAPI_L3GetData DF24 nRet = %d", nRet);
                return L3_ERR_FAIL;
            }
            TRACE_HEX(ICS, 7, "_EMVPARAM_DF24_ICS:");
            if(ics_opt_get(CV_Support_Bypass_PIN, ICS))
            {
                SetTvr(PIN_PAD_PRESENT_BUT_PIN_NOT_ENTERED);
                UnsetTvr(ONLINE_PIN_ENTERED);
                return L3_ERR_FAIL;
            }
            continue;
        }
        if (nRet < 0)
		{
            UnsetTvr(ONLINE_PIN_ENTERED);
			return nRet; /* terminate */
//		}
//        break;
//    }
//    return L3_ERR_SUCC;
//}
/*
void GetKernelVersion(void)
{
	char sMsg[50] = {0};
	uchar API_Version[50] = {0};
	uchar EMV_Version[50] = {0};
	uchar EP_Version[50] = {0};
	uchar QPBOC_Version[50] = {0};
	uchar PAYPASS_Version[50] = {0};
	uchar PAYWAVE_Version[50] = {0};
	uchar EXPRESSPAY_Version[50] = {0};
	uchar DPAS_Version[50] = {0};
	uchar JCB_Version[50] = {0};
	uchar PURE_Version[50] = {0};
	uchar RUPAY_Version[50] = {0};
	uchar INTERAC_Version[50] = {0};
	uchar MIR_Version[50] = {0};
	uchar MULTIBANCO_Version[50] = {0};
	
	NAPI_L3GetVersion(L3_MODULE_API, API_Version);
	NAPI_L3GetVersion(L3_MODULE_EMV, EMV_Version);
	NAPI_L3GetVersion(L3_MODULE_EP, EP_Version);
	NAPI_L3GetVersion(L3_MODULE_QPBOC, QPBOC_Version);
	NAPI_L3GetVersion(L3_MODULE_PAYPASS, PAYPASS_Version);
	NAPI_L3GetVersion(L3_MODULE_PAYWAVE, PAYWAVE_Version);
	NAPI_L3GetVersion(L3_MODULE_EXPRESSPAY, EXPRESSPAY_Version);
	NAPI_L3GetVersion(L3_MODULE_DPAS, DPAS_Version);
	NAPI_L3GetVersion(L3_MODULE_PURE, JCB_Version);
	NAPI_L3GetVersion(L3_MODULE_PURE, PURE_Version);
	NAPI_L3GetVersion(L3_MODULE_RUPAY, RUPAY_Version);
	NAPI_L3GetVersion(L3_MODULE_INTERAC, INTERAC_Version);
	NAPI_L3GetVersion(L3_MODULE_MIR, MIR_Version);
	NAPI_L3GetVersion(L3_MDDULE_MULTIBANCO, MULTIBANCO_Version);

	memset(sMsg, 0, sizeof(sMsg));
	sprintf(sMsg, "API_Version:\r\n%s\r\n", API_Version);
	PubMsgDlg(NULL, sMsg, 0, 15);

	memset(sMsg, 0, sizeof(sMsg));
	sprintf(sMsg, "EMV_Version:\r\n%s\r\n", EMV_Version);
	PubMsgDlg(NULL, sMsg, 0, 15);

	memset(sMsg, 0, sizeof(sMsg));
	sprintf(sMsg, "EP_Version:\r\n%s\r\n", EP_Version);
	PubMsgDlg(NULL, sMsg, 0, 15);

	memset(sMsg, 0, sizeof(sMsg));
	sprintf(sMsg, "QPBOC_Version:\r\n%s\r\n", QPBOC_Version);
	PubMsgDlg(NULL, sMsg, 0, 15);

	memset(sMsg, 0, sizeof(sMsg));
	sprintf(sMsg, "PAYPASS_Version:\r\n%s\r\n", PAYPASS_Version);
	PubMsgDlg(NULL, sMsg, 0, 15);

	memset(sMsg, 0, sizeof(sMsg));
	sprintf(sMsg, "PAYWAVE_Version:\r\n%s\r\n", PAYWAVE_Version);
	PubMsgDlg(NULL, sMsg, 0, 15);

	memset(sMsg, 0, sizeof(sMsg));
	sprintf(sMsg, "EXPRESSPAY_Version:\r\n%s\r\n", EXPRESSPAY_Version);
	PubMsgDlg(NULL, sMsg, 0, 15);

	memset(sMsg, 0, sizeof(sMsg));
	sprintf(sMsg, "DPAS_Version:\r\n%s\r\n", DPAS_Version);
	PubMsgDlg(NULL, sMsg, 0, 15);

	memset(sMsg, 0, sizeof(sMsg));
	sprintf(sMsg, "JCB_Version:\r\n%s\r\n", JCB_Version);
	PubMsgDlg(NULL, sMsg, 0, 15);

	memset(sMsg, 0, sizeof(sMsg));
	sprintf(sMsg, "PURE_Version:\r\n%s\r\n", PURE_Version);
	PubMsgDlg(NULL, sMsg, 0, 15);

	memset(sMsg, 0, sizeof(sMsg));
	sprintf(sMsg, "RUPAY_Version:\r\n%s\r\n", RUPAY_Version);
	PubMsgDlg(NULL, sMsg, 0, 15);

	memset(sMsg, 0, sizeof(sMsg));
	sprintf(sMsg, "INTERAC_Version:\r\n%s\r\n", INTERAC_Version);
	PubMsgDlg(NULL, sMsg, 0, 15);

	memset(sMsg, 0, sizeof(sMsg));
	sprintf(sMsg, "MIR_Version:\r\n%s\r\n", MIR_Version);
	PubMsgDlg(NULL, sMsg, 0, 15);
}
 */
//#if !IS_EMVL3
/**
 *@brief	整型转换为4字节字符数组（高位在前）
 *@param	unNum		需要转换的整型数
 *@retval	psBuf		转换输出的字符串
 *@return
 *@li	NDK_OK				操作成功
 *@li	其它EM_NDK_ERR		操作失败
*/
int NDK_IntToC40(uchar* psBuf, uint unNum )
{
    if (psBuf == NULL) {
        return NDK_ERR_PARA;
    }
    *( psBuf ) = unNum >> 24;
    *( psBuf + 1 ) = (unNum >> 16) ;
    *( psBuf + 2 ) = (unNum >> 8) ;
    *( psBuf + 3 ) = unNum %256;
    return NDK_OK;
}

/**
 *@brief	整型转换为2字节字符数组（高位在前）
 *@param	unNum		需要转换的整型数
 *@retval	psBuf		转换输出的字符串
 *@return
 *@li	NDK_OK				操作成功
 *@li	其它EM_NDK_ERR		操作失败
*/
int NDK_IntToC20(uchar* psBuf, uint unNum )
{
    if (psBuf == NULL) {
        return NDK_ERR_PARA;
    }

    if (unNum <= 65535) {
        *(psBuf + 1) = unNum % 256;
        *psBuf = unNum >> 8;
    }
    return NDK_OK;
}
/**
 *@brief	4字节字符数组转换为整型（高位在前）
 *@param	psBuf		需要转换的字符串
 *@retval	unNum		转换输出的整型数
 *@return
 *@li	NDK_OK				操作成功
 *@li	其它EM_NDK_ERR		操作失败
*/
int NDK_C4ToInt0(uint* unNum, uchar* psBuf )
{
    if ((unNum == NULL) || (psBuf == NULL)) {
        return NDK_ERR_PARA;
    }

    *unNum = ((*psBuf) << 24) + (*(psBuf+1) << 16) + (*(psBuf+2) << 8) + (*(psBuf + 3));
    return NDK_OK;
}

/**
 *@brief	2字节字符数组转换为整型（高位在前）
 *@details	psBuf长度要>=2
 *@param	psBuf		需要转换的字符串
 *@retval	unNum		转换输出的整型数
 *@return
 *@li	NDK_OK				操作成功
 *@li	其它EM_NDK_ERR		操作失败
*/

int NDK_C2ToInt0(uint *unNum, uchar *psBuf)
{
    if ((unNum == NULL) || (psBuf == NULL)) {
        return NDK_ERR_PARA;
    }
    *unNum = ((*psBuf) << 8) + (*(psBuf + 1));
    return NDK_OK;
}
//#endif