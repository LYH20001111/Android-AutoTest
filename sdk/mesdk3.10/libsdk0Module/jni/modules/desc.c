#include <stdio.h>
#include <string.h>
#include "desc.h"
#include "log.h"

void Cmd_PrintDesc(char cmdMain,char cmdSub,int isStart)
{
	int cmd = cmdMain<<8|cmdSub;
	char cmdDesc[256];
	memset(cmdDesc,0,sizeof(cmdDesc));
	switch(cmd)
	{
        case CARDREADER_OPEN               :sprintf(cmdDesc,"%s","CardReader_Open");break;
        case CARDREADER_CLOSE              :sprintf(cmdDesc,"%s","CardReader_Close");break;

        case MAG_READTRACKPLAIN            :sprintf(cmdDesc,"%s","Mag_ReadTrackPlain");break;
        case MAG_READTRACKENCRYPT          :sprintf(cmdDesc,"%s","Mag_ReadTrackEncrypt");break;
        case MAG_CALCULATETRACK            :sprintf(cmdDesc,"%s","Mag_CalculateTrack");break;

        case ICC_DETECT                    :sprintf(cmdDesc,"%s","Icc_Detect");break;
        case ICC_POWERON                   :sprintf(cmdDesc,"%s","Icc_PowerOn");break;
        case ICC_POWEROFF                  :sprintf(cmdDesc,"%s","Icc_PowerOff");break;
        case ICC_READWRITE                 :sprintf(cmdDesc,"%s","Icc_ReadWrite");break;

        case RFID_POWERON                  :sprintf(cmdDesc,"%s","Rfid_PowerOn");break;
        case RFID_POWEROFF                 :sprintf(cmdDesc,"%s","Rfid_PowerOff");break;
        case RFID_APDU                     :sprintf(cmdDesc,"%s","Rfid_Apdu");break;
        case RFID_FELICAAPDU               :sprintf(cmdDesc,"%s","Rfid_FelicaApdu");break;
        case RFID_M1AUTHKEY                :sprintf(cmdDesc,"%s","Rfid_M1AuthKey");break;
        case RFID_M1READBLOCK              :sprintf(cmdDesc,"%s","Rfid_M1ReadBlock");break;
        case RFID_M1WRITEBLOCK             :sprintf(cmdDesc,"%s","Rfid_M1WriteBlock");break;
        case RFID_M1INCREMENT              :sprintf(cmdDesc,"%s","Rfid_M1Increment");break;
        case RFID_M1DECREMENT              :sprintf(cmdDesc,"%s","Rfid_M1Decrement");break;
        case RFID_M0AUTHKEY                :sprintf(cmdDesc,"%s","Rfid_M0AuthKey");break;
        case RFID_M0READBLOCK              :sprintf(cmdDesc,"%s","Rfid_M0ReadBlock");break;
        case RFID_M0WRITEBLOCK             :sprintf(cmdDesc,"%s","Rfid_M0WriteBlock");break;
        case RFID_ISEXIST                  :sprintf(cmdDesc,"%s","Rfid_IsExist");break;
        case RFID_ATS                      :sprintf(cmdDesc,"%s","Rfid_ATS");break;

        case PINPAD_INPUT                  :sprintf(cmdDesc,"%s","Pinpad_Input");break;
        case PINPAD_LOADMKEY               :sprintf(cmdDesc,"%s","Pinpad_LoadMKey");break;
        case PINPAD_ENCORDEC               :sprintf(cmdDesc,"%s","Pinpad_EncOrDec");break;
        case PINPAD_DATAMAC                :sprintf(cmdDesc,"%s","Pinpad_DataMac");break;
        case PINPAD_LOADWKEY               :sprintf(cmdDesc,"%s","Pinpad_LoadWKey");break;
        case PINPAD_LOADDUKPT              :sprintf(cmdDesc,"%s","Pinpad_LoadDukpt");break;
        case PINPAD_DELKEY                 :sprintf(cmdDesc,"%s","Pinpad_DelKey");break;
        case PINPAD_VPPINIT                :sprintf(cmdDesc,"%s","Pinpad_VppInit");break;
        case PINPAD_CHECKKEY               :sprintf(cmdDesc,"%s","Pinpad_CheckKey");break;
        case PINPAD_INCREASEKSN            :sprintf(cmdDesc,"%s","Pinpad_IncreaseKsn");break;
        case PINPAD_GETDUKPTKSN            :sprintf(cmdDesc,"%s","Pinpad_GetDukptKsn");break;

        case LIGHT_SETSTATUS               :sprintf(cmdDesc,"%s","Light_SetStatus");break;
        case LIGHT_BLINK                   :sprintf(cmdDesc,"%s","Light_Blink");break;
        case PRN_GETSTATUS                 :sprintf(cmdDesc,"%s","Prn_GetStatus");break;
        case PRN_SETPAPERSIZE              :sprintf(cmdDesc,"%s","Prn_SetPaperSize");break;
        case PRN_PRINT                     :sprintf(cmdDesc,"%s","Prn_Print");break;
        case PRN_CUTTERPAPER               :sprintf(cmdDesc,"%s","Prn_CutterPaper");break;

        case DEVICE_READINFO               :sprintf(cmdDesc,"%s","Device_ReadInfo");break;
        case DEVICE_GETRANDOMNUMBER        :sprintf(cmdDesc,"%s","DEVICE_GETRANDOMNUMBER");break;
        case DEVICE_GETTUSN                :sprintf(cmdDesc,"%s","Device_GetTusn");break;
        case DEVICE_SETSN                  :sprintf(cmdDesc,"%s","Device_SetSN");break;
        case DEVICE_SETDATETIME            :sprintf(cmdDesc,"%s","Device_SetDateTime");break;
        case DEVICE_GETDATETIME            :sprintf(cmdDesc,"%s","Device_GetDateTime");break;

        case TERM_BUZZER                   :sprintf(cmdDesc,"%s","Term_Buzzer");break;
        case TERM_CANCELRESET              :sprintf(cmdDesc,"%s","Term_CancelReset");break;
        case TERM_SHUTDOWN                 :sprintf(cmdDesc,"%s","Term_ShutDown");break;
        case TERM_CONFIRMATION             :sprintf(cmdDesc,"%s","Term_Confirmation");break;
        case TERM_SETKEYVOL                :sprintf(cmdDesc,"%s","Term_SetKeyVol");break;
        case TERM_SETTAGDATA               :sprintf(cmdDesc,"%s","Term_SetTagData");break;
        case TERM_GETTAGDATA               :sprintf(cmdDesc,"%s","Term_GetTagData");break;

        case LED_GETVERSION                :sprintf(cmdDesc,"%s","Led_GetVersion");break;
        case LED_SETBRIGHTNESS             :sprintf(cmdDesc,"%s","Led_SetBrightness");break;
        case LED_TURNON                    :sprintf(cmdDesc,"%s","Led_TurnOn");break;
        case LED_TURNOFF                   :sprintf(cmdDesc,"%s","Led_TurnOff");break;

        case FILE_OPENRECORDS              :sprintf(cmdDesc,"%s","File_OpenRecords");break;
        case FILE_GETRECORDNUM             :sprintf(cmdDesc,"%s","File_GetRecordNum");break;
        case FILE_WRITERECORD              :sprintf(cmdDesc,"%s","File_WriteRecord");break;
        case FILE_MODIFYRECORD             :sprintf(cmdDesc,"%s","File_ModifyRecord");break;
        case FILE_GETRECORD                :sprintf(cmdDesc,"%s","File_GetRecord");break;
        case FILE_WRITEFILE                :sprintf(cmdDesc,"%s","File_WriteFile");break;
        case FILE_READFILE                 :sprintf(cmdDesc,"%s","File_ReadFile");break;
        case FILE_DELETEFILE               :sprintf(cmdDesc,"%s","File_DeleteFile");break;

        case LOG_SETLEVEL                  :sprintf(cmdDesc,"%s","Log_SetLevel");break;
        default:sprintf(cmdDesc,"%s","unknow");break;
    }
	if(isStart==1){
		Udebug.ERROR_MSG_LOG(">>>[%s|IN|0x%02x%02x|%s]",VERSION,cmdMain,cmdSub,cmdDesc);
	}else if(isStart==0){
		Udebug.ERROR_MSG_LOG(">>>[%s|OUT|0x%02x%02x|%s]",VERSION,cmdMain,cmdSub,cmdDesc);
	}

}

