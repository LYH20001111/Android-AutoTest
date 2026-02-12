#include <string.h>
#include <stdlib.h>
#include <comm.h>
#include "NDK.h"
#include "alg.h"
#include "command.h"
#include "unistd.h"
#include "log.h"
#include "api.h"

#define MINI_NDK_VERSION     "NDK_V1.0.08"

#define Response_Code_Good                    "00"
#define Response_Code_No_Snk_error            "01"
#define Response_Code_Get_Tusn_error          "02"
#define Response_Code_Ndk_Ver_error           "03"

extern const char app_ver[16];
extern ME_TPEDCTL g_METPEDCtl;
extern ME31_CONFIG_T g_me31conf;

// 首次获取设备信息的时候或者PN、SN、CSN等设备信息里的值被改后该标志为0，其余情况为1
char g_readInfoFlag = 0;

int setReadInfoFlag(){
    LOGD_FMT("reset readInfoFlag.")
    g_readInfoFlag = 0;
}

int Sys_GetDeviceAbility(void) {
    int nRet;
    uint nLen;
    uchar nDeviceAblity;
    char szDevBuf[32];

    nDeviceAblity = 0;

    nRet = NDK_PortOpen(PORT_NUM_AUDIO, NULL);//音频
    if (nRet != NDK_OK) LOGE_NDK("NDK_PortOpen", nRet, NULL, 0);
    if ((nRet != NDK_ERR_NOT_SUPPORT) && (nRet != NDK_ERR_NO_DEVICES) && (nRet != NDK_ERR_OPEN_DEV))
        nDeviceAblity |= 0x80;

    nRet = NDK_PortOpen(PORT_NUM_WIRELESS, NULL);//蓝牙
    if (nRet != NDK_OK) LOGE_NDK("NDK_PortOpen", nRet, NULL, 0);
    if ((nRet != NDK_ERR_NOT_SUPPORT) && (nRet != NDK_ERR_NO_DEVICES) && (nRet != NDK_ERR_OPEN_DEV))
        nDeviceAblity |= 0x40;

    nRet = NDK_SysGetPosInfo(SYS_HWINFO_GET_HARDWARE_INFO, &nLen, szDevBuf);
    if (nRet != NDK_OK) {
        LOGE_NDK("NDK_SysGetPosInfo", nRet, szDevBuf, sizeof(szDevBuf));
    }
	char ndkVer[32],caps[8];int capabilityFlag = 0;
	memset(ndkVer,0, sizeof(ndkVer));
	memset(caps,0, sizeof(caps));
	EXEC_NDK("NDK_Getlibver",NDK_Getlibver(ndkVer),NDK_OK,DEVICE_READINFO);
	LOGD_FMT("NDK_Getlibver[%s]",ndkVer);
	if(strcmp(ndkVer,"NDK_V3.0.04") >= 0){
		if(EXEC_NDK("NDK_SysGetCapability",NDK_SysGetCapability(sizeof(caps),caps),NDK_OK,DEVICE_READINFO)){
			//4：打印 | 5：非接 | 6：ic卡 | 7：磁卡 | 8：密码键盘
			LOGD_STR("NDK_SysGetCapability",caps, sizeof(caps));
			capabilityFlag = 1;
		}
	}
    LOGE_FMT("capabilityFlag[%d]",capabilityFlag);
    if ((nRet == 0) && (szDevBuf[6] != 0xff))//offline
        nDeviceAblity &= ~0x20;

    //mag
	if(capabilityFlag == 1){
		if(caps[6]=='Y')
			nDeviceAblity |= 0x10;
	}else{
		if((nRet==0)&&(szDevBuf[2]!=0xff))
			nDeviceAblity |= 0x10;
	}

    //rfid
	if(capabilityFlag == 1){
		if(caps[4]=='Y'){
			nDeviceAblity |= 0x04;
			nDeviceAblity |= 0x20;
		}
	}else{
		if((nRet==0)&&(szDevBuf[1]!=0xff)){
			nDeviceAblity |= 0x04;
			nDeviceAblity |= 0x20;
		}
	}

    EXEC_NDK("NDK_IccDetect",nRet = NDK_IccDetect((int *) &nLen),NDK_OK,DEVICE_READINFO);
    //IC
    if(capabilityFlag == 1){
        if(caps[5]=='Y') {
            nDeviceAblity |= 0x08;
            nDeviceAblity |= 0x20;
        }
    }else{
        if(nRet==0){
            nDeviceAblity |= 0x08;
            nDeviceAblity |= 0x20;				//有ic卡就认为有脱机交易能力
        }
    }

    //pin
	if(capabilityFlag == 1){
		if(caps[3]=='Y')
			nDeviceAblity |= 0x02;
	}else{
		if((nRet==0)&&(szDevBuf[10]!=0xff))
			nDeviceAblity |= 0x02;
	}

	nDeviceAblity &= ~0x01;	//不支持屏幕显示

	return nDeviceAblity;
}

int Device_ReadInfo(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen) {
    int len;
    int nRet;
    int nLen, extoffset, offset;
    DEVICE_DATA *dev_data;
    char ret_code[2];
    char *tmp;
    char szTmpbuf[32];
    static uint snLen = 0;
    static uint msaterVerLen = 0;
    static uint csnLen = 0;
    static uint bootVerLen = 0;
    static char master_ver[50];
    static uchar uDeviceAblity = 0;
    static char app_ver[16];
    static char boot_ver[40];
    static char posUSN[100];
    static char posCSN[101];
    static char posType[2];

    offset = 0;
    memcpy(ret_code, CMD_OK, 2);
    dev_data = &(g_me31conf.ME_TDeviceData);
    nLen = buf_len;
    extoffset = RESPOND_DATA_OFFSET;

    if (g_readInfoFlag == 0) {
        LOGD_FMT("g_readInfoFlag==0");
        //POS_USN
        LOGD_FMT("POS_USN");
        memset(posUSN, 0, sizeof(posUSN));
        int ret = NDK_ERR;
        EXEC_NDK("NDK_SysGetPosInfo",ret = NDK_SysGetPosInfo(SYS_HWINFO_GET_POS_USN, (uint *) &snLen, posUSN),NDK_OK,DEVICE_READINFO);
        if(ret == SDK_ERR_NDK_NOT_SUPPORT){
            goto ON_ERR;
        }
        memcpy((char *) pOut + extoffset, posUSN, snLen);//SN
        offset += 12;
        LOGD_STR("POS_USN",posUSN,snLen);

        //设备个人化状态
        memcpy(pOut + extoffset + offset, &(dev_data->DevicePersonalStatus), 1); /*设备个人化状态	1 DATA[6]	0xFF：出厂默认状态	0x00：个人化完成*/
        offset += 1;

        //获取k21端指令集版本
        memset(app_ver, 0, sizeof(app_ver));
        EXEC_NDK("NDk_SysGetK21Version",NDk_SysGetK21Version(app_ver),NDK_OK,DEVICE_READINFO);//获取k21端指令集版本
        memcpy(pOut + extoffset + offset, app_ver, 16);/*应用版本16 */
        offset += 16;

        //保留,全0
        memset(pOut + extoffset + offset, 0x00, 11);/*保留,全0 */
        offset += 10;

        //硬件设备能力
        LOGD_FMT("GetDeviceAbility");
        uDeviceAblity = Sys_GetDeviceAbility();
        pOut[extoffset + offset] = uDeviceAblity;/*mpos硬件设备能力*/
        offset += 1;
        //支持音频/支持蓝牙/支持脱机交易/支持磁条卡/支持接触式IC卡/支持非接触IC卡/支持打印/支持屏幕显示
        LOGD_FMT(">>Audio/Bluetooth/Offline/MAG/IC/RFID/Printer/LCD  DeviceAblity[0x%x]",uDeviceAblity);


        //固件版本
        memset(master_ver, 0, sizeof(master_ver));
        EXEC_NDK("NDK_SysGetPosInfo",NDK_SysGetPosInfo(SYS_HWINFO_GET_BIOS_VER, (uint *) &msaterVerLen, master_ver),NDK_OK,DEVICE_READINFO);/*固件版本*/
        tmp = strstr(master_ver, "(");
        if (tmp != NULL && msaterVerLen > 16)
            len = tmp - master_ver;
        else
            len = msaterVerLen;
        memset(pOut + extoffset + offset, 0, 16);
        memcpy(pOut + extoffset + offset, master_ver, len);
        offset += 16;

        //客户序列号(CSN)
        memset(posCSN, 0, sizeof(posCSN));
        nRet = Sys_GetPosInfo(SYS_HWINFO_GET_CSN, (uint *) &csnLen, posCSN);
        memcpy((char *) pOut + extoffset + offset + 2, posCSN, csnLen);
        if (nRet != 0) {
            memcpy(pOut + extoffset + offset, "\x00\x00", 2);  /*客户序列号*/
            offset += 2;
        } else {
            nRet = 2;
            NDK_IntToBcd(pOut + extoffset + offset, &nRet, csnLen);
            offset += 2;
            offset += csnLen;
        }

        //客户密钥序列号(KSN)
        nRet = Sys_GetPosInfo(SYS_HWINFO_GET_KSN, (uint *) &nLen, (char *) pOut + extoffset + offset + 2);
        if (nRet != 0) {
            memcpy(pOut + extoffset + offset, "\x00\x00", 2);/*密钥序列号*/
            offset += 2;
        } else {
            nRet = 2;
            EXEC_NDK("NDK_IntToBcd",NDK_IntToBcd(pOut + extoffset + offset, &nRet, nLen),NDK_OK,DEVICE_READINFO);
            offset += 2;
            offset += nLen;
        }

        //产品ID
        memset(szTmpbuf, 0, sizeof(szTmpbuf));
        nRet = NDK_SysGetPosInfo(SYS_HWINFO_GET_POS_TYPE, (uint *) &nLen, szTmpbuf);
        if (nRet != 0) {
            memcpy(posType, "\x00\x01", 2);                            /*产品ID .银商要求此字节为0x01*/
            LOGE_NDK("NDK_SysGetPosInfo", nRet, pbuf, buf_len);
            Udebug.ERROR_MSG_LOG_String(szTmpbuf, sizeof(szTmpbuf));
            Udebug.ERROR_MSG_LOG("nLen[%d]", nLen);
        } else if (memcmp(szTmpbuf, "ME30", 4) == 0)
            memcpy(posType, "\x00\x30", 2);                            /*产品ID */
        else if (memcmp(szTmpbuf, "ME31HW_FULL_", 12) == 0)
            memcpy(posType, "\x00\x31", 2);                            /*产品ID*/
        else if (memcmp(szTmpbuf, "IM81", 4) == 0)
            memcpy(posType, "\x00\x81", 2);                            /*产品ID*/
        else if (memcmp(szTmpbuf, "N900", 4) == 0)
            memcpy(posType, "\x09\x00", 2);                            /*产品ID */
        else
            memcpy(posType, "\x00\x01", 2);                            /*产品ID */
        memcpy(pOut + extoffset + offset, posType, 2);
        offset += 2;


        //厂商ID VID
        nRet = Sys_GetPosInfo(SYS_HWINFO_GET_VID, (uint *) &nLen, (char *) pOut + extoffset + offset);
        if (nRet != 0) {
            memcpy(pOut + extoffset + offset, "\x00\x01", 2);/*客户ID*/
        }
        offset += 2;

        //生产SN号
        LOGD_FMT("PRODUCE_SN");
        nRet = Sys_GetPosInfo(SYS_HWINFO_GET_PRODUCE_SN, (uint *) &nLen, (char *) pOut + extoffset + offset + 2);
        if (nRet != 0) {
            memcpy(pOut + extoffset + offset, "\x00\x00", 2);/*生产SN*/
            offset += 2;
        } else {
            nRet = 2;
            EXEC_NDK("NDK_IntToBcd",NDK_IntToBcd(pOut + extoffset + offset, &nRet, nLen),NDK_OK,DEVICE_READINFO);
            offset += 2;
            offset += nLen;
        }

        //Boot版本
        memset(boot_ver, 0, sizeof(boot_ver));
        nRet = NDK_SysGetPosInfo(SYS_HWINFO_GET_BOOT_VER, (uint *) &bootVerLen, boot_ver); // boot 版本
        memcpy((char *) pOut + extoffset + offset + 2, boot_ver, bootVerLen);
        if (nRet != NDK_OK) {
            LOGE_NDK("NDK_SysGetPosInfo", nRet,NULL,0);
            memcpy(pOut + extoffset + offset, "\x00\x00", 2);                        /* boot 版本*/
            offset += 2;
        } else {
            nRet = 2;
            LOGD_FMT("NDK_IntToBcd",NDK_IntToBcd(pOut + extoffset + offset, &nRet, bootVerLen),NDK_OK);
            offset += 2;
            offset += bootVerLen;
        }
        // 标志置1
        g_readInfoFlag = 1;
    } else {
        LOGD_FMT("g_readInfoFlag==1");
        //POS_USN
        memcpy((char *) pOut + extoffset, posUSN, snLen); /* SN 码*/
        offset += 12;
        LOGD_STR("POS_USN",posUSN,snLen);

        //设备个人化状态
        memcpy(pOut + extoffset + offset, &(dev_data->DevicePersonalStatus), 1); /*设备个人化状态	1	DATA[6]	0xFF：出厂默认状态	0x00：个人化完成*/
        offset += 1;

        //应用版本
        memcpy(pOut + extoffset + offset, app_ver, strlen(app_ver)); /*应用版本	1	DATA[7] */
        offset += 16;

        //保留
        memset(pOut + extoffset + offset, 0x00, 11); /*保留,全0 */
        offset += 10;

        LOGD_FMT("GetDeviceAbility");
        pOut[extoffset + offset] = uDeviceAblity; /*mpos硬件设备能力，银商SDK要求此字节为0x00*/
        offset += 1;
        //支持音频/支持蓝牙/支持脱机交易/支持磁条卡/支持接触式IC卡/支持非接触IC卡/支持打印/支持屏幕显示
        LOGD_FMT(">>Audio/Bluetooth/Offline/MAG/IC/RFID/Printer/LCD  DeviceAblity[%d]",uDeviceAblity);

        //固件版本
        tmp = strstr(master_ver, "(");
        if (tmp != NULL && msaterVerLen > 16)
            len = tmp - master_ver;
        else
            len = msaterVerLen;
        memset(pOut + extoffset + offset, 0, 16);
        memcpy(pOut + extoffset + offset, master_ver, len);
        offset += 16;

        //客户序列号(CSN)
        memcpy((char *) pOut + extoffset + offset + 2, posCSN, csnLen);/*客户序列号*/
        nRet = 2;
        NDK_IntToBcd(pOut + extoffset + offset, &nRet, csnLen);
        offset += 2;
        offset += csnLen;

        //客户密钥序列号(KSN)
        nRet = -1;//Sys_GetPosInfo(SYS_HWINFO_GET_KSN, (uint *) &nLen, (char *) pOut + extoffset + offset + 2);
        if (nRet != 0) {
            memcpy(pOut + extoffset + offset, "\x00\x00", 2); /*密钥序列号*/
            offset += 2;
        } else {
            nRet = 2;
            NDK_IntToBcd(pOut + extoffset + offset, &nRet, nLen);
            offset += 2;
            offset += nLen;
        }

        //产品ID
        memcpy(pOut + extoffset + offset, posType, 2);/*产品ID */
        offset += 2;

        //厂商ID VID
        nRet = Sys_GetPosInfo(SYS_HWINFO_GET_VID, (uint *) &nLen, (char *) pOut + extoffset + offset);
        if (nRet != 0) {
            memcpy(pOut + extoffset + offset, "\x00\x01", 2); /*客户ID*/
        }
        offset += 2;

        //生产SN号
        LOGD_FMT("PRODUCE_SN");
        nRet = Sys_GetPosInfo(SYS_HWINFO_GET_PRODUCE_SN, (uint *) &nLen, (char *) pOut + extoffset + offset + 2);
        if (nRet != 0) {
            memcpy(pOut + extoffset + offset, "\x00\x00", 2);/*生产SN*/
            offset += 2;
        } else {
            nRet = 2;
            NDK_IntToBcd(pOut + extoffset + offset, &nRet, nLen);
            offset += 2;
            offset += nLen;
        }

        //Boot版本
        memcpy((char *) pOut + extoffset + offset + 2, boot_ver, bootVerLen); // boot 版本
        nRet = 2;
        NDK_IntToBcd(pOut + extoffset + offset, &nRet, bootVerLen);
        offset += 2;
        offset += bootVerLen;
    }
    responseCmd(pOut, offset, outLen, ret_code);
    return 0;

    ON_ERR:
    responseCmd(pOut, 0, outLen, CMD_ERR_OTHER);
    return 0;
}

int Device_GetTusn(puchar pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int iRet;
    int extoffset=2;
    static uint snLen = 0;
    char szTusn[50 + 1];
    static char NDK_ver[16];
    char response_err[2];

    memset(szTusn, 0, sizeof(szTusn));
    memset(NDK_ver, 0, sizeof(NDK_ver));
    memcpy(response_err, Response_Code_Good, 2);

    if(!EXEC_NDK("NDK_Getlibver",NDK_Getlibver(NDK_ver),NDK_OK,DEVICE_GETTUSN)){
        goto ON_ERR;
    }
    if (strcmp(NDK_ver, MINI_NDK_VERSION) >= 0)//NDK版本比NDK_V1.0.08大
    {
        if(!EXEC_NDK("NDK_SysGetPosInfo",iRet = NDK_SysGetPosInfo(SYS_HWINFO_GET_POS_TUSN,(uint*)&snLen,szTusn),NDK_OK,DEVICE_GETTUSN)){
            goto ON_ERR;
        }
        LOGD_FMT(">>>NDK_SysGetPosInfo Ret[%d]",iRet);
        if (iRet == NDK_OK) {
            memcpy(pOut + extoffset, response_err, 2);
            extoffset += 2;
            memcpy((char *) pOut + extoffset, szTusn, snLen);
            extoffset += 20;
        } else if (iRet == -19) {
            memcpy(response_err, Response_Code_No_Snk_error, 2);
            goto ON_ERR;
        } else if (iRet == -20) {
            memcpy(response_err, Response_Code_Get_Tusn_error, 2);
            goto ON_ERR;
        }
    } else {
        memcpy(response_err, Response_Code_Ndk_Ver_error, 2);
        goto ON_ERR;

    }
    responseCmd(pOut, extoffset-2, outLen, CMD_OK);
    return 0;

    ON_ERR:
    extoffset = 2;
    LOGE_NDK("NDK_SysGetPosInfo", iRet, NULL, 0);
    memcpy(pOut + extoffset, response_err, 2);
    extoffset += 2;
    responseCmd(pOut, extoffset-2, outLen, CMD_OK);
    return 0;

}

int Device_SetSN(puchar pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int offset;
    int nMode;
    unsigned int nLen;
    unsigned char *pCSN;

    offset = 0;
    nMode = nlMpos_Command.mpos_getvar(pbuf + offset, _VAR_BIT8);
    offset += 1;
    nLen = nlMpos_Command.mpos_readlen(pbuf + offset, _VAR_BIT16);
    offset += 2;
    pCSN = pbuf + offset;
    offset += nLen;
    pCSN[nLen] = 0;

    LOGD_FMT(">>>nMode[%d] nLen[%d]",nMode,nLen);
    if (nLen > 100) {
        ERRMSG(SDK_ERR_PARAM,DEVICE_SETSN);
        goto ON_ACK;
    }

    if (nMode == 1) {
        EXEC_NDK("NDK_SP_SysSetPosInfo USN", NDK_SP_SysSetPosInfo(SYS_HWINFO_GET_POS_USN, (char *) pCSN), NDK_OK,DEVICE_SETSN);
        if(!EXEC_NDK("NDK_SP_SysSetPosInfo USN",NDK_SysSetPosInfo(SYS_HWINFO_GET_POS_USN, (char *) pCSN),NDK_OK,DEVICE_SETSN)){
            goto ON_ACK;
        }
    } else if (nMode == 2) {
        EXEC_NDK("NDK_SP_SysSetPosInfo PSN", NDK_SP_SysSetPosInfo(SYS_HWINFO_GET_POS_PSN, (char *) pCSN), NDK_OK,DEVICE_SETSN);
        if(!EXEC_NDK("NDK_SP_SysSetPosInfo PSN",NDK_SysSetPosInfo(SYS_HWINFO_GET_POS_PSN, (char *) pCSN),NDK_OK,DEVICE_SETSN)){
            goto ON_ACK;
        }
    } else if (nMode == 4) {
        if(!EXEC_NDK("Sys_SetPosInfo CSN",Sys_SetPosInfo(SYS_HWINFO_GET_CSN, (char *) pCSN, nLen),NDK_OK,DEVICE_SETSN)){
            goto ON_ACK;
        }
    } else if (nMode == 8) {
        EXEC_NDK("NDK_SP_SysSetPosInfo VER", NDK_SP_SysSetPosInfo(SYS_HWINFO_GET_BOARD_VER, (char *) pCSN), NDK_OK,DEVICE_SETSN);
        if(!EXEC_NDK("NDK_SP_SysSetPosInfo VER",NDK_SysSetPosInfo(SYS_HWINFO_GET_BOARD_VER, (char *) pCSN),NDK_OK,DEVICE_SETSN)){
            goto ON_ACK;
        }
    } else if (nMode == 0x10) {
        if(!EXEC_NDK("Sys_SetPosInfo KSN",Sys_SetPosInfo(SYS_HWINFO_GET_KSN, (char *) pCSN, nLen),NDK_OK,DEVICE_SETSN)){
            goto ON_ACK;
        }
    } else if (nMode == 0x20) {
        if(!EXEC_NDK("Sys_SetPosInfo SN",Sys_SetPosInfo(SYS_HWINFO_GET_PRODUCE_SN, (char *) pCSN, nLen),NDK_OK,DEVICE_SETSN)){
            goto ON_ACK;
        }
    } else
        goto ON_ACK;

    g_readInfoFlag = 0;
    responseCmd(pOut, 0, outLen, CMD_OK);
    return 0;
    ON_ACK:
    g_readInfoFlag = 0;
    responseCmd(pOut, 0, outLen, CMD_ERR_OTHER);
    return 0;
}

int Device_SetDateTime(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    char pdata[5];
    int year, month, day, hour, minute, second;
    struct tm pstTime;
    char headCode[2];
    int leap_flag;
    int leap[2][12] = {
            {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31},  //平年
            {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}   //闰年
    };

    int i = 0;

    memcpy(headCode, CMD_OK, 2);

    for (i = 0; i < 14; i++) {
        if ((pbuf[i] < 0x30) || (pbuf[i] > 0x39)) {
            memcpy(headCode, CMD_ERR_PARAM, 2);
            goto ON_ACK;
        }
    }
    memset(&pstTime, 0x00, sizeof(pstTime));

    // year
    memcpy(pdata, pbuf + 0, 4);
    pdata[4] = 0;
    year = atoi(pdata);
    if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0))
        leap_flag = 1;
    else
        leap_flag = 0;
    pstTime.tm_year = year - 1900;

    // month
    memcpy(pdata, pbuf + 4, 2);
    pdata[2] = 0;
    month = atoi(pdata);
    if (month > 12) {
        memcpy(headCode, CMD_ERR_PARAM, 2);//月份大于12则返回参数错误应答码
        goto ON_ACK;
    }
    pstTime.tm_mon = month - 1;

    // date
    memcpy(pdata, pbuf + 6, 2);
    pdata[2] = 0;
    day = atoi(pdata);
    if (day > leap[leap_flag][month - 1]) {
        memcpy(headCode, CMD_ERR_PARAM, 2);//日期超过当月最大天数则返回参数错误应答码
        goto ON_ACK;
    }
    pstTime.tm_mday = day;

    // tm_hour
    memcpy(pdata, pbuf + 8, 2);
    pdata[2] = 0;
    hour = atoi(pdata);
    if (hour > 24) {
        memcpy(headCode, CMD_ERR_PARAM, 2);//时超过24则返回参数错误应答码
        goto ON_ACK;
    }
    pstTime.tm_hour = hour;

    // tm_min
    memcpy(pdata, pbuf + 10, 2);
    pdata[2] = 0;
    minute = atoi(pdata);
    if (minute >= 60) {
        memcpy(headCode, CMD_ERR_PARAM, 2);//分钟大于59则返回参数错误应答码
        goto ON_ACK;
    }
    pstTime.tm_min = minute;

    // tm_sec
    memcpy(pdata, pbuf + 12, 2);
    pdata[2] = 0;
    second = atoi(pdata);
    if (second >= 60) {
        memcpy(headCode, CMD_ERR_PARAM, 2);//秒数大于59则返回参数错误应答码
        goto ON_ACK;
    }
    pstTime.tm_sec = second;

    if(!EXEC_NDK("NDK_SysSetPosTime",NDK_SysSetPosTime(pstTime),NDK_OK,DEVICE_SETDATETIME)){
        memcpy(headCode, CMD_ERR_OTHER, 2);
        goto ON_ACK;
    }
    ON_ACK:
    responseCmd(pOut, 0, outLen, headCode);
    return 0;
}

int Device_GetDateTime(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int extoffset;
    struct tm pstTime;
    char headCode[2];
    memcpy(headCode, CMD_OK, 2);

    if(!EXEC_NDK("NDK_SysGetPosTime",NDK_SysGetPosTime(&pstTime),NDK_OK,DEVICE_GETDATETIME)){
        memcpy(headCode, CMD_ERR_OTHER, 2);
        goto ON_ACK;
    }
    extoffset = 2;
    sprintf((char *) pOut + extoffset, "%04d%02d%02d%02d%02d%02d\n", \
            pstTime.tm_year + 1900, \
            pstTime.tm_mon + 1, \
            pstTime.tm_mday, \
            pstTime.tm_hour, \
            pstTime.tm_min, \
            pstTime.tm_sec);

    responseCmd(pOut, 14, outLen, headCode);
    return 0;
    ON_ACK:
    responseCmd(pOut, 0, outLen, headCode);
    return 0;
}

int Device_GetRandomNumber(puchar pbuf, int buf_len, unsigned char *pOut, int *outLen) {

    uchar rec[513];
    int offset = 0,extoffset = 2;
    int iDatalen = nlMpos_Command.mpos_getvar(pbuf,_VAR_BIT16);offset+=2;
    iDatalen = nlMpos_Command.mpos_endian_swab16(iDatalen);
    memset(rec,0, sizeof(rec));
    LOGD_FMT(">>>len[%d]",iDatalen);
    if(iDatalen <= 0 || iDatalen > 512){
        ERRMSG(SDK_ERR_PARAM,DEVICE_GETRANDOMNUMBER);
        goto ON_ERR;
    }
    if(!EXEC_NDK("NDK_SecGetRandom",NDK_SecGetRandom(iDatalen, rec),NDK_OK,DEVICE_GETRANDOMNUMBER)){
        goto ON_ERR;
    }
    nlMpos_Command.mpos_writelen(pOut + extoffset, iDatalen, _VAR_BIT16);
    extoffset += 2;
    memcpy(pOut + extoffset, rec, iDatalen);
    extoffset += iDatalen;
    responseCmd(pOut, extoffset-2, outLen, CMD_OK);
    return 0;
    ON_ERR:
    responseCmd(pOut,0, outLen,CMD_ERR_OTHER);
    return 0;
}
