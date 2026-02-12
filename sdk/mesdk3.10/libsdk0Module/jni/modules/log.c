#include "log.h"
#include <android/log.h>
#include <stdint.h>
#include <sys/system_properties.h>
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <unistd.h>
#include <stdlib.h>
#include "comm.h"
#include "desc.h"
#include "threadtool.h"
#include "api.h"
#include <jni.h>

extern JavaVM *gJavaVM;
static jclass packagerClass =NULL;
static jfieldID errCodeId = NULL;
static jfieldID errMsgId = NULL;
static jfieldID otherMsgId = NULL;
static char g_errMsg[128];
static char g_otherMsg[128];
int Udebugopen2 = 0;
int Udebuglevel2 = 0;
ST_DEBUG_API Udebug;
FILE *fp_debug2 = NULL;

#define LOG_TAG   "libsdk0"
#define LOGI(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

int property_get(const char *key, char *value, const char *default_value)
{
    int len;
    len = __system_property_get(key, value);
    if(len > 0) {
        return len;
    }
    if(default_value) {
        len = strlen(default_value);
        memcpy(value, default_value, len + 1);
    }
    return len;
}

static void Log_DebugLevel(void)
{
    int ret = 0;
    char propBuf[5] = {0};
    ret = property_get("persist.sys.nl_lib_debug", propBuf, "0");
    if(ret < 0) {
        return;
    }
    Udebugopen2 = propBuf[0] - '0';
    Udebuglevel2 = propBuf[1] - '0';
    LOGI("Udebuglevel2=%d",Udebuglevel2);
    return;
}
void printf_fmt(char * fmt,...)
{
	int len;
	va_list arg;
	char str[4096];
	va_start( arg, fmt );
	if((len = vsprintf(str, fmt, arg)) < 0) {
		return;
	}
	if(Udebugopen2 == 1)
	{
		if (fp_debug2 == NULL) {
			fp_debug2 = fopen("/Share/debug_mpos.log", "a+");
			if (fp_debug2 == NULL) {
				LOGI("fopen /Share/debug_mpos.log Err!\n");
				return;
			}
		}

		fseek(fp_debug2,0,SEEK_END);
		fwrite(str, len, sizeof(char), fp_debug2);
	}
	else
		LOGI(str);
	va_end( arg );
	return;
}

void printf_string(char *BUF,int LEN){
	int i;
	int len;
	int size;
	int temp;
	int offset;
	char s[2048];
	size = LEN;
	for(i=0; i < LEN; ) {
		offset = 0;
		memset(s, 0, sizeof(s));
		len = (size > 256) ? 256 : size;
		for(temp=0; temp < len; temp++) {
			offset += sprintf(s + offset, "%02x ", BUF[temp+i]);
		}
		i += len;
		size -= len;
		s[offset-1] = '\n';
		printf_fmt("%s", s);
	}
}

void printf_null(char * fmt,...){
	return;
}
void printf_string_null(char *BUF,int LEN){
	return;
}

void Log_DebugInit()
{
    Log_DebugLevel();
    if(Udebuglevel2 == 2){
        Udebug.DEBUG_Levelone = printf_fmt;
        Udebug.DEBUG_Leveltwo = printf_fmt;
        Udebug.DEBUG_string_Levelone = printf_string;
        Udebug.DEBUG_string_Leveltwo = printf_string;
    }
    else if(Udebuglevel2 == 1){
        Udebug.DEBUG_Levelone = printf_fmt;
        Udebug.DEBUG_Leveltwo = printf_null;
        Udebug.DEBUG_string_Levelone = printf_string;
        Udebug.DEBUG_string_Leveltwo = printf_string_null;
    }
    else{
        Udebug.DEBUG_Levelone = printf_null;
        Udebug.DEBUG_Leveltwo = printf_null;
        Udebug.DEBUG_string_Levelone = printf_string_null;
        Udebug.DEBUG_string_Leveltwo = printf_string_null;
    }
    Udebug.ERROR_MSG_LOG = printf_fmt;
    Udebug.ERROR_MSG_LOG_String = printf_string;
}

int Log_SetLevel(unsigned char *pbuf,  int buf_len, unsigned char *pOut, int *outLen)
{
    int sdkLev =  nlMpos_Command.mpos_readlen(pbuf, _VAR_BIT8);
    int ndkLev  =  nlMpos_Command.mpos_readlen(pbuf+1, _VAR_BIT8);
    int sdtpLev =  nlMpos_Command.mpos_readlen(pbuf+2, _VAR_BIT8);

    LOGI(">>>SetLogLevel sdkLev[%d] ndkLev[%d] sdtpLev[%d]",sdkLev,ndkLev,sdtpLev);

    EXEC_NDK("#NDK_SysOpenDebug",NDK_SysOpenDebug(ndkLev,sdtpLev),NDK_OK,COMMAND_NONE);

    if(sdkLev == 0){
        Udebug.DEBUG_Levelone = printf_null;
        Udebug.DEBUG_Leveltwo = printf_null;
        Udebug.DEBUG_string_Levelone = printf_string_null;
        Udebug.DEBUG_string_Leveltwo = printf_string_null;
    }else if(sdkLev == 1){
        Udebug.DEBUG_Levelone = printf_fmt;
        Udebug.DEBUG_Leveltwo = printf_fmt;
        Udebug.DEBUG_string_Levelone = printf_string;
        Udebug.DEBUG_string_Leveltwo = printf_string;
    }

//    responseCmd(pOut, 0, outLen, CMD_OK);
    return 0;
}
static char *getErrMsg(int errCode)
{
    memset(g_errMsg,0,sizeof(g_errMsg));
    switch (errCode){
        case NDK_OK                               :sprintf(g_errMsg,"%s","Success");break;//= 0     /**<Success*/
        case NDK_ERR                              :sprintf(g_errMsg,"%s","Fail");break;//= -1,   /**<Fail*/
        case NDK_ERR_INIT_CONFIG                  :sprintf(g_errMsg,"%s","Failed to initialize configuration");break;//= -2,   /**<Failed to initialize configuration*/
        case NDK_ERR_CREAT_WIDGET                 :sprintf(g_errMsg,"%s","Failed to error creating interface");break;//= -3,   /**<Failed to error creating interface*/
        case NDK_ERR_OPEN_DEV                     :sprintf(g_errMsg,"%s","Failed to error opening device file");break;//= -4,   /**<Failed to error opening device file*/
        case NDK_ERR_IOCTL                        :sprintf(g_errMsg,"%s","Failed to call driver function");break;//= -5,   /**<Failed to call driver function*/
        case NDK_ERR_PARA                         :sprintf(g_errMsg,"%s","Invalid parameter");break;//= -6,   /**<Invalid parameter*/
        case NDK_ERR_PATH                         :sprintf(g_errMsg,"%s","Invalid file path");break;//= -7,   /**<Invalid file path*/
        case NDK_ERR_DECODE_IMAGE                 :sprintf(g_errMsg,"%s","Failed to decode image");break;//= -8,   /**<Failed to decode image*/
        case NDK_ERR_MACLLOC                      :sprintf(g_errMsg,"%s","Out of memory");break;//= -9,   /**<Out of memory*/
        case NDK_ERR_TIMEOUT                      :sprintf(g_errMsg,"%s","Timeout error");break;//= -10,  /**<Timeout error*/
        case NDK_ERR_QUIT                         :sprintf(g_errMsg,"%s","Press Cancel to exit");break;//= -11,  /**<Press Cancel to exit*/
        case NDK_ERR_WRITE                        :sprintf(g_errMsg,"%s","Failed to write into file");break;//= -12,  /**<Failed to write into file*/
        case NDK_ERR_READ                         :sprintf(g_errMsg,"%s","Failed to read from file");break;//= -13,  /**<Failed to read from file*/
        case NDK_ERR_OVERFLOW                     :sprintf(g_errMsg,"%s","Buffer overflow");break;//= -15,  /**<Buffer overflow*/
        case NDK_ERR_SHM                          :sprintf(g_errMsg,"%s","Failed to share memory");break;//= -16,  /**<Failed to share memory*/
        case NDK_ERR_NO_DEVICES                   :sprintf(g_errMsg,"%s","Device not available");break;//= -17,  /**<Device not available*/
        case NDK_ERR_NOT_SUPPORT                  :sprintf(g_errMsg,"%s","Feature not supported");break;//= -18,  /**<Feature not supported*/
        case NDK_ERR_NOSWIPED                     :sprintf(g_errMsg,"%s","No magnetSmart card swiping");break;//= -50,  /**<No magnetSmart card swiping*/
        case NDK_ERR_SWIPED_DATA                  :sprintf(g_errMsg,"%s","Wrong magnetSmart card data");break;//= -51,  /**<Wrong magnetSmart card data*/
        case NDK_ERR_USB_LINE_UNCONNECT           :sprintf(g_errMsg,"%s","Usb cable not connected");break;//= -100, /**<Usb cable not connected*/
        case NDK_ERR_NO_SIMCARD                   :sprintf(g_errMsg,"%s","No SIM card");break;//= -201, /**<No SIM card*/
        case NDK_ERR_PIN                          :sprintf(g_errMsg,"%s","Wrong SIM card password");break;//= -202, /**<Wrong SIM card password*/
        case NDK_ERR_PIN_LOCKED                   :sprintf(g_errMsg,"%s","SIM card locked");break;//= -203, /**<SIM card locked*/
        case NDK_ERR_PIN_UNDEFINE                 :sprintf(g_errMsg,"%s","Undefined SIM card error");break;//= -204, /**<Undefined SIM card error*/
        case NDK_ERR_EMPTY                        :sprintf(g_errMsg,"%s","Empty string returned");break;//= -205, /**<Empty string returned*/
        case NDK_ERR_ETH_PULLOUT                  :sprintf(g_errMsg,"%s","Ethernet cable not plugged");break;//= -250, /**<Ethernet cable not plugged*/
        case NDK_ERR_PPP_PARAM                    :sprintf(g_errMsg,"%s","Invalid PPP parameter");break;//= -301, /**<Invalid PPP parameter*/
        case NDK_ERR_PPP_DEVICE                   :sprintf(g_errMsg,"%s","Invalid PPP device");break;//= -302, /**<Invalid PPP device*/
        case NDK_ERR_PPP_OPEN                     :sprintf(g_errMsg,"%s","PPP already open");break;//= -303, /**<PPP already open*/
        case NDK_ERR_TCP_ALLOC                    :sprintf(g_errMsg,"%s","PPP already open");break;//= -304, /**<Failed to allocate*/
        case NDK_ERR_TCP_PARAM                    :sprintf(g_errMsg,"%s","Invalid parameter");break;//= -305, /**<Invalid parameter*/
        case NDK_ERR_TCP_TIMEOUT                  :sprintf(g_errMsg,"%s","Transmission timeout");break;//= -306, /**<Transmission timeout*/
        case NDK_ERR_TCP_INVADDR                  :sprintf(g_errMsg,"%s","Invalid address");break;//= -307, /**<Invalid address*/
        case NDK_ERR_TCP_CONNECT                  :sprintf(g_errMsg,"%s","No connection");break;//= -308, /**<No connection*/
        case NDK_ERR_TCP_PROTOCOL                 :sprintf(g_errMsg,"%s","Protocol error");break;//= -309, /**<Protocol error*/
        case NDK_ERR_TCP_NETWORK                  :sprintf(g_errMsg,"%s","Network error");break;//= -310, /**<Network error*/
        case NDK_ERR_TCP_SEND                     :sprintf(g_errMsg,"%s","Failed to send");break;//= -311, /**<Failed to send*/
        case NDK_ERR_TCP_RECV                     :sprintf(g_errMsg,"%s","Failed to send");break;//= -312, /**<Failed to receive*/
        case NDK_ERR_WLM_SEND_AT_FAIL             :sprintf(g_errMsg,"%s","Failed to transmit AT");break;//= -320, /**<Failed to transmit AT*/
        case NDK_ERR_SSL_PARAM                    :sprintf(g_errMsg,"%s","Invalid parameter");break;//= -350, /**<Invalid parameter*/
        case NDK_ERR_SSL_ALREADCLOSE              :sprintf(g_errMsg,"%s","Connection already closed");break;//= -351, /**<Connection already closed*/
        case NDK_ERR_SSL_ALLOC                    :sprintf(g_errMsg,"%s","Failed to allocate");break;//= -352, /**<Failed to allocate*/
        case NDK_ERR_SSL_INVADDR                  :sprintf(g_errMsg,"%s","Invalid address");break;//= -353, /**<Invalid address*/
        case NDK_ERR_SSL_TIMEOUT                  :sprintf(g_errMsg,"%s","Connection Timeout");break;//= -354, /**<Connection Timeout*/
        case NDK_ERR_SSL_MODEUNSUPPORTED          :sprintf(g_errMsg,"%s","Mode not supported");break;//= -355, /**<Mode not supported*/
        case NDK_ERR_SSL_SEND                     :sprintf(g_errMsg,"%s","Failed to send");break;//= -356, /**<Failed to send*/
        case NDK_ERR_SSL_RECV                     :sprintf(g_errMsg,"%s","Failed to receive");break;//= -357, /**<Failed to receive*/
        case NDK_ERR_SSL_CONNECT                  :sprintf(g_errMsg,"%s","No connection");break;//= -358, /**<No connection*/
        case NDK_ERR_NET_GETADDR                  :sprintf(g_errMsg,"%s","Failed to obtain local address or subnet mask");break;//= -401, /**<Failed to obtain local address or subnet mask*/
        case NDK_ERR_NET_GATEWAY                  :sprintf(g_errMsg,"%s","Failed to obtain gateway address");break;//= -402, /**<Failed to obtain gateway address*/
        case NDK_ERR_NET_ADDRILLEGAL              :sprintf(g_errMsg,"%s","Failed to obtain address format");break;//= -403, /**<Failed to obtain address format*/
        case NDK_ERR_NET_UNKNOWN_COMMTYPE         :sprintf(g_errMsg,"%s","Unknown type of communication");break;//= -404, /**<Unknown type of communication*/
        case NDK_ERR_NET_INVALIDIPSTR             :sprintf(g_errMsg,"%s","Invalid IP string");break;//= -405, /**<Invalid IP string*/
        case NDK_ERR_NET_UNSUPPORT_COMMTYPE       :sprintf(g_errMsg,"%s","Type of communication not supported");break;//= -406, /**<Type of communication not supported*/
        case NDK_ERR_THREAD_PARAM                 :sprintf(g_errMsg,"%s","Invalid address");break;//= -450, /**<Invalid address*/
        case NDK_ERR_THREAD_ALLOC                 :sprintf(g_errMsg,"%s","Failed to allocate");break;//= -451, /**<Failed to allocate*/
        case NDK_ERR_THREAD_CMDUNSUPPORTED        :sprintf(g_errMsg,"%s","Command not supported");break;//= -452, /**<Command not supported*/
        case NDK_ERR_MODEM_RESETFAIL              :sprintf(g_errMsg,"%s","Failed to reset");break;//= -501, /**<Failed to reset*/
        case NDK_ERR_MODEM_GETSTATUSFAIL          :sprintf(g_errMsg,"%s","Failed to get status");break;//= -502, /**<Failed to get status*/
        case NDK_ERR_MODEM_SLEPPFAIL              :sprintf(g_errMsg,"%s","Failed to sleep");break;//= -503, /**<Failed to sleep*/
        case NDK_ERR_MODEM_SDLCINITFAIL           :sprintf(g_errMsg,"%s","Failed to initialize in sync mode");break;//= -504, /**<Failed to initialize in sync mode*/
        case NDK_ERR_MODEM_INIT_NOT               :sprintf(g_errMsg,"%s","Not initialized");break;//= -505, /**<Not initialized*/
        case NDK_ERR_MODEM_SDLCWRITEFAIL          :sprintf(g_errMsg,"%s","Failed to write in sync mode");break;//= -506, /**<Failed to write in sync mode*/
        case NDK_ERR_MODEM_ASYNWRITEFAIL          :sprintf(g_errMsg,"%s","Failed to write in async mode");break;//= -507, /**<Failed to write in async mode*/
        case NDK_ERR_MODEM_ASYNDIALFAIL           :sprintf(g_errMsg,"%s","Failed to dial in async mode");break;//= -508, /**<Failed to dial in async mode*/
        case NDK_ERR_MODEM_ASYNINITFAIL           :sprintf(g_errMsg,"%s","Failed to initialize in async mode");break;//= -509, /**<Failed to initialize in async mode*/
        case NDK_ERR_MODEM_SDLCHANGUPFAIL         :sprintf(g_errMsg,"%s","Failed to hangup in sync mode");break;//= -510, /**<Failed to hangup in sync mode*/
        case NDK_ERR_MODEM_ASYNHANGUPFAIL         :sprintf(g_errMsg,"%s","Failed to hangup in async mode");break;//= -511, /**<Failed to hangup in async mode*/
        case NDK_ERR_MODEM_SDLCCLRBUFFAIL         :sprintf(g_errMsg,"%s","Failed to clear buffer in sync mode");break;//= -512, /**<Failed to clear buffer in sync mode*/
        case NDK_ERR_MODEM_ASYNCLRBUFFAIL         :sprintf(g_errMsg,"%s","Failed to clear buffer in async mode");break;//= -513, /**<Failed to clear buffer in async mode*/
        case NDK_ERR_MODEM_ATCOMNORESPONSE        :sprintf(g_errMsg,"%s","No response for AT command");break;//= -514, /**<No response for AT command*/
        case NDK_ERR_MODEM_PORTWRITEFAIL          :sprintf(g_errMsg,"%s","Failed to write data to modem port");break;//= -515, /**<Failed to write data to modem port*/
        case NDK_ERR_MODEM_SETCHIPFAIL            :sprintf(g_errMsg,"%s","Failed to set register");break;//= -516, /**<Failed to set register*/
        case NDK_ERR_MODEM_STARTSDLCTASK          :sprintf(g_errMsg,"%s","Failed to start SDLC task");break;//= -517, /**<Failed to start SDLC task*/
        case NDK_ERR_MODEM_GETBUFFLENFAIL         :sprintf(g_errMsg,"%s","Failed to get data lenth");break;//= -518, /**<Failed to get data lenth*/
        case NDK_ERR_MODEM_QUIT                   :sprintf(g_errMsg,"%s","Hand out");break;//= -519, /**<Hand out*/
        case NDK_ERR_MODEM_NOPREDIAL              :sprintf(g_errMsg,"%s","No predial");break;//= -520, /**<No predial*/
        case NDK_ERR_MODEM_NOCARRIER              :sprintf(g_errMsg,"%s","No carrier");break;//= -521, /**<No carrier*/
        case NDK_ERR_MODEM_NOLINE                 :sprintf(g_errMsg,"%s","No cable");break;//= -523, /**<No cable*/
        case NDK_ERR_MODEM_OTHERMACHINE           :sprintf(g_errMsg,"%s","Collision detected");break;//= -524, /**<Collision detected*/
        case NDK_ERR_MODEM_PORTREADFAIL           :sprintf(g_errMsg,"%s","Failed to read data from modem port");break;//= -525, /**<Failed to read data from modem port*/
        case NDK_ERR_MODEM_CLRBUFFAIL             :sprintf(g_errMsg,"%s","Failed to clear buffer");break;//= -526, /**<Failed to clear buffer*/
        case NDK_ERR_MODEM_ATCOMMANDERR           :sprintf(g_errMsg,"%s","AT command error");break;//= -527, /**<AT command error*/
        case NDK_ERR_MODEM_STATUSUNDEFINE         :sprintf(g_errMsg,"%s","State unrecognized");break;//= -528, /**<State unrecognized*/
        case NDK_ERR_MODEM_GETVERFAIL             :sprintf(g_errMsg,"%s","Failed to get version");break;//= -529, /**<Failed to get version*/
        case NDK_ERR_MODEM_SDLCDIALFAIL           :sprintf(g_errMsg,"%s","Failed to dial in sync mode");break;//= -530, /**<Failed to dial in sync mode*/
        case NDK_ERR_MODEM_SELFADAPTFAIL          :sprintf(g_errMsg,"%s","Failed to auto-negotiation");break;//= -531, /**<Failed to auto-negotiation*/
        case NDK_ERR_MODEM_SELFADAPTCANCEL        :sprintf(g_errMsg,"%s","Auto-negotiation canceled");break;//= -532, /**<Auto-negotiation canceled*/
        case NDK_ERR_ICC_WRITE_ERR                :sprintf(g_errMsg,"%s","Failed to write");break;//= -601, /**<Failed to write*/
        case NDK_ERR_ICC_COPYERR                  :sprintf(g_errMsg,"%s","Failed to copy kernel data");break;//= -602, /**<Failed to copy kernel data*/
        case NDK_ERR_ICC_POWERON_ERR              :sprintf(g_errMsg,"%s","Failed to powerup");break;//= -603, /**<Failed to powerup*/
        case NDK_ERR_ICC_COM_ERR                  :sprintf(g_errMsg,"%s","Command error");break;//= -604, /**<Command error*/
        case NDK_ERR_ICC_CARDPULL_ERR             :sprintf(g_errMsg,"%s","Card not present");break;//= -605, /**<Card not present*/
        case NDK_ERR_ICC_CARDNOREADY_ERR          :sprintf(g_errMsg,"%s","Card not ready");break;//= -606, /**<Card not ready*/
        case NDK_ERR_USDDISK_PARAM                :sprintf(g_errMsg,"%s","Invalid parameter");break;//= -650, /**<Invalid parameter*/
        case NDK_ERR_USDDISK_DRIVELOADFAIL        :sprintf(g_errMsg,"%s","Failed to load USB stick or SD card");break;//= -651, /**<Failed to load USB stick or SD card*/
        case NDK_ERR_USDDISK_NONSUPPORTTYPE       :sprintf(g_errMsg,"%s","Type not supported");break;//= -652, /**<Type not supported*/
        case NDK_ERR_USDDISK_UNMOUNTFAIL          :sprintf(g_errMsg,"%s","Failed to mount");break;//= -653, /**<Failed to mount*/
        case NDK_ERR_USDDISK_UNLOADDRIFAIL        :sprintf(g_errMsg,"%s","Failed to unload driver");break;//= -654, /**<Failed to unload driver*/
        case NDK_ERR_USDDISK_IOCFAIL              :sprintf(g_errMsg,"%s","Failed to call driver function");break;//= -655, /**<Failed to call driver function*/
        case NDK_ERR_APP_BASE                     :sprintf(g_errMsg,"%s","Unknown error");break;//= -800, /**<Unknown error*/
        case NDK_ERR_APP_NOT_EXIST                :sprintf(g_errMsg,"%s","Application not exist");break;//= (NDK_ERR_APP_BASE - 1), /**<Application not exist*/
        case NDK_ERR_APP_NOT_MATCH                :sprintf(g_errMsg,"%s","Patch not match");break;//= (NDK_ERR_APP_BASE - 2), /**<Patch not match*/
        case NDK_ERR_APP_FAIL_SEC                 :sprintf(g_errMsg,"%s","Failed to access tamper status");break;//= (NDK_ERR_APP_BASE - 3), /**<Failed to access tamper status*/
        case NDK_ERR_APP_SEC_ATT                  :sprintf(g_errMsg,"%s","Tamper detected");break;//= (NDK_ERR_APP_BASE - 4), /**<Tamper detected*/
        case NDK_ERR_APP_FILE_EXIST               :sprintf(g_errMsg,"%s","Application file already exists");break;//= (NDK_ERR_APP_BASE - 5), /**<Application file already exists*/
        case NDK_ERR_APP_FILE_NOT_EXIST           :sprintf(g_errMsg,"%s","Application file not exist");break;//= (NDK_ERR_APP_BASE - 6), /**<Application file not exist*/
        case NDK_ERR_APP_FAIL_AUTH                :sprintf(g_errMsg,"%s","Failed to authenticate certificate");break;//= (NDK_ERR_APP_BASE - 7), /**<Failed to authenticate certificate*/
        case NDK_ERR_APP_LOW_VERSION              :sprintf(g_errMsg,"%s","Patch version lower than the application version");break;//= (NDK_ERR_APP_BASE - 8), /**<Patch version lower than the application version*/
        case NDK_ERR_APP_MAX_CHILD                :sprintf(g_errMsg,"%s","More than maximum number of running applications");break;//= (NDK_ERR_APP_BASE - 9), /**<More than maximum number of running applications*/
        case NDK_ERR_APP_CREAT_CHILD              :sprintf(g_errMsg,"%s","Failed to create child process");break;//= (NDK_ERR_APP_BASE - 10), /**<Failed to create child process*/
        case NDK_ERR_APP_WAIT_CHILD               :sprintf(g_errMsg,"%s","Failed to wait for the child to exit");break;//= (NDK_ERR_APP_BASE - 11), /**<Failed to wait for the child to exit*/
        case NDK_ERR_APP_FILE_READ                :sprintf(g_errMsg,"%s","Failed to read file");break;//= (NDK_ERR_APP_BASE - 12), /**<Failed to read file*/
        case NDK_ERR_APP_FILE_WRITE               :sprintf(g_errMsg,"%s","Failed to write file");break;//= (NDK_ERR_APP_BASE - 13), /**<Failed to write file*/
        case NDK_ERR_APP_FILE_STAT                :sprintf(g_errMsg,"%s","Failed to get file information");break;//= (NDK_ERR_APP_BASE - 14), /**<Failed to get file information*/
        case NDK_ERR_APP_FILE_OPEN                :sprintf(g_errMsg,"%s","Failed to open file");break;//= (NDK_ERR_APP_BASE - 15), /**<Failed to open file*/
        case NDK_ERR_APP_NLD_HEAD_LEN             :sprintf(g_errMsg,"%s","Wrong length of the NLD file header information");break;//= (NDK_ERR_APP_BASE - 16), /**<Wrong length of the NLD file header information*/
        case NDK_ERR_APP_PUBKEY_EXPIRED           :sprintf(g_errMsg,"%s","Public key expired");break;//= (NDK_ERR_APP_BASE - 17), /**<Public key expired*/
        case NDK_ERR_APP_MMAP                     :sprintf(g_errMsg,"%s","Failed to map memory");break;//= (NDK_ERR_APP_BASE - 18), /**<Failed to map memory*/
        case NDK_ERR_APP_MALLOC                   :sprintf(g_errMsg,"%s","Out of memory");break;//= (NDK_ERR_APP_BASE - 19), /**<Out of memory*/
        case NDK_ERR_APP_SIGN_DECRYPT             :sprintf(g_errMsg,"%s","Failed to decrypt signature data");break;//= (NDK_ERR_APP_BASE - 20), /**<Failed to decrypt signature data*/
        case NDK_ERR_APP_SIGN_CHECK               :sprintf(g_errMsg,"%s","Failed to validate signature data");break;//= (NDK_ERR_APP_BASE - 21), /**<Failed to validate signature data*/
        case NDK_ERR_APP_MUNMAP                   :sprintf(g_errMsg,"%s","Failed to unmap memory");break;//= (NDK_ERR_APP_BASE - 22), /**<Failed to unmap memory*/
        case NDK_ERR_APP_TAR                      :sprintf(g_errMsg,"%s","Failed to untar data");break;//= (NDK_ERR_APP_BASE - 23), /**<Failed to untar data*/
        case NDK_ERR_APP_KEY_UPDATE_BAN           :sprintf(g_errMsg,"%s","Key update is prohibited");break;//= (NDK_ERR_APP_BASE - 24), /**<Key update is prohibited*/
        case NDK_ERR_APP_FIRM_PATCH_VERSION       :sprintf(g_errMsg,"%s","Firmware patch version do not match");break;//= (NDK_ERR_APP_BASE - 25), /**<Firmware patch version do not match*/
        case NDK_ERR_APP_CERT_HAS_EXPIRED         :sprintf(g_errMsg,"%s","Certificate expired");break;//= (NDK_ERR_APP_BASE - 26), /**<Certificate expired*/
        case NDK_ERR_APP_CERT_NOT_YET_VALID       :sprintf(g_errMsg,"%s","Invalid certificate");break;//= (NDK_ERR_APP_BASE - 27), /**<Invalid certificate*/
        case NDK_ERR_APP_FILE_NAME_TOO_LONG       :sprintf(g_errMsg,"%s","File name length larger than 32 bytes");break;//= (NDK_ERR_APP_BASE - 28), /**<File name length larger than 32 bytes*/
        case NDK_ERR_SECP_BASE                    :sprintf(g_errMsg,"%s","Unknown error");break;//= (-1000),                  /**<Unknown error*/
        case NDK_ERR_SECP_TIMEOUT                 :sprintf(g_errMsg,"%s","Get key value timeout");break;//= (NDK_ERR_SECP_BASE - 1),  /**<Get key value timeout*/
        case NDK_ERR_SECP_PARAM                   :sprintf(g_errMsg,"%s","Invalid parameter");break;//= (NDK_ERR_SECP_BASE - 2),  /**<Invalid parameter*/
        case NDK_ERR_SECP_DBUS                    :sprintf(g_errMsg,"%s","DBUS communication error");break;//= (NDK_ERR_SECP_BASE - 3),  /**<DBUS communication error*/
        case NDK_ERR_SECP_MALLOC                  :sprintf(g_errMsg,"%s","Out of memory");break;//= (NDK_ERR_SECP_BASE - 4),  /**<Out of memory*/
        case NDK_ERR_SECP_OPEN_SEC                :sprintf(g_errMsg,"%s","Failed to open security device");break;//= (NDK_ERR_SECP_BASE - 5),  /**<Failed to open security device*/
        case NDK_ERR_SECP_SEC_DRV                 :sprintf(g_errMsg,"%s","Failed to call driver function");break;//= (NDK_ERR_SECP_BASE - 6),  /**<Failed to call driver function*/
        case NDK_ERR_SECP_GET_RNG                 :sprintf(g_errMsg,"%s","Failed to get random number");break;//= (NDK_ERR_SECP_BASE - 7),  /**<Failed to get random number*/
        case NDK_ERR_SECP_GET_KEY                 :sprintf(g_errMsg,"%s","Failed to get key value");break;//= (NDK_ERR_SECP_BASE - 8),  /**<Failed to get key value*/
        case NDK_ERR_SECP_KCV_CHK                 :sprintf(g_errMsg,"%s","KCV check error");break;//= (NDK_ERR_SECP_BASE - 9),  /**<KCV check error*/
        case NDK_ERR_SECP_GET_CALLER              :sprintf(g_errMsg,"%s","Failed to get caller info");break;//= (NDK_ERR_SECP_BASE - 10), /**<Failed to get caller info*/
        case NDK_ERR_SECP_OVERRUN                 :sprintf(g_errMsg,"%s","Overrun");break;//= (NDK_ERR_SECP_BASE - 11), /**<Overrun*/
        case NDK_ERR_SECP_NO_PERMIT               :sprintf(g_errMsg,"%s","Operation not allowed");break;//= (NDK_ERR_SECP_BASE - 12), /**<Operation not allowed*/
        case NDK_ERR_SECP_TAMPER                  :sprintf(g_errMsg,"%s","Tamper detected");break;//= (NDK_ERR_SECP_BASE - 13), /**<Tamper detected*/
        case NDK_ERR_SECVP_BASE                   :sprintf(g_errMsg,"%s","Unknown error");break;//= (-1100),                  /**<Unknown error*/
        case NDK_ERR_SECVP_TIMEOUT                :sprintf(g_errMsg,"%s","Get key value timeout");break;//= (NDK_ERR_SECVP_BASE - 1), /**<Get key value timeout*/
        case NDK_ERR_SECVP_PARAM                  :sprintf(g_errMsg,"%s","Invalid parameter");break;//= (NDK_ERR_SECVP_BASE - 2), /**<Invalid parameter*/
        case NDK_ERR_SECVP_DBUS                   :sprintf(g_errMsg,"%s","DBUS communication error");break;//= (NDK_ERR_SECVP_BASE - 3), /**<DBUS communication error*/
        case NDK_ERR_SECVP_OPEN_EVENT0            :sprintf(g_errMsg,"%s","Failed to open event device");break;//= (NDK_ERR_SECVP_BASE - 4), /**<Failed to open event device*/
        case NDK_ERR_SECVP_SCAN_VAL               :sprintf(g_errMsg,"%s","Scan value out of range");break;//= (NDK_ERR_SECVP_BASE - 5), /**<Scan value out of range*/
        case NDK_ERR_SECVP_OPEN_RNG               :sprintf(g_errMsg,"%s","Failed to open random number device");break;//= (NDK_ERR_SECVP_BASE - 6), /**<Failed to open random number device*/
        case NDK_ERR_SECVP_GET_RNG                :sprintf(g_errMsg,"%s","Failed to get random number");break;//= (NDK_ERR_SECVP_BASE - 7), /**<Failed to get random number*/
        case NDK_ERR_SECVP_GET_ESC                :sprintf(g_errMsg,"%s","User cancel");break;//= (NDK_ERR_SECVP_BASE - 8), /**<User cancel*/
        case NDK_ERR_SECVP_INVALID_KEY            :sprintf(g_errMsg,"%s","Invalid key");break;//= (NDK_ERR_SECVP_VPP),      /**<Invalid key*/
        case NDK_ERR_SECVP_NOT_ACTIVE             :sprintf(g_errMsg,"%s","VPPIs not active");break;//= (NDK_ERR_SECVP_VPP - 1),  /**<VPPIs not active*/
        case NDK_ERR_SECVP_TIMED_OUT              :sprintf(g_errMsg,"%s","VPP initialization timeout");break;//= (NDK_ERR_SECVP_VPP - 2),  /**<VPP initialization timeout*/
        case NDK_ERR_SECVP_ENCRYPT_ERROR          :sprintf(g_errMsg,"%s","Failed to encrypt");break;//= (NDK_ERR_SECVP_VPP - 3),  /**<Failed to encrypt*/
        case NDK_ERR_SECVP_BUFFER_FULL            :sprintf(g_errMsg,"%s","Buffer full");break;//= (NDK_ERR_SECVP_VPP - 4),  /**<Buffer full*/
        case NDK_ERR_SECVP_PIN_KEY                :sprintf(g_errMsg,"%s","Data key pressed, echo *");break;//= (NDK_ERR_SECVP_VPP - 5),  /**<Data key pressed, echo "*".*/
        case NDK_ERR_SECVP_ENTER_KEY              :sprintf(g_errMsg,"%s","Enter key pressed, process PIN");break;//= (NDK_ERR_SECVP_VPP - 6),  /**<Enter key pressed, process PIN*/
        case NDK_ERR_SECVP_BACKSPACE_KEY          :sprintf(g_errMsg,"%s","Backspace key pressed");break;//= (NDK_ERR_SECVP_VPP - 7),  /**<Backspace key pressed.*/
        case NDK_ERR_SECVP_CLEAR_KEY              :sprintf(g_errMsg,"%s","Clear key pressed, remove all the *");break;//= (NDK_ERR_SECVP_VPP - 8),  /**<Clear key pressed, remove all the '*'.*/
        case NDK_ERR_SECVP_CANCEL_KEY             :sprintf(g_errMsg,"%s","Cancel key pressed");break;//= (NDK_ERR_SECVP_VPP - 9),  /**<Cancel key pressed.*/
        case NDK_ERR_SECVP_GENERALERROR           :sprintf(g_errMsg,"%s","Internal error");break;//= (NDK_ERR_SECVP_VPP - 10), /**<Internal error.*/
        case NDK_ERR_SECVP_CUSTOMERCARDNOTPRESENT :sprintf(g_errMsg,"%s","Smart card not present");break;//= (NDK_ERR_SECVP_VPP - 11), /**<Smart card not present*/
        case NDK_ERR_SECVP_HTCCARDERROR           :sprintf(g_errMsg,"%s","Failed to access smart card");break;//= (NDK_ERR_SECVP_VPP - 12), /**<Failed to access smart card*/
        case NDK_ERR_SECVP_WRONG_PIN_LAST_TRY     :sprintf(g_errMsg,"%s","Wrong password, try again");break;//= (NDK_ERR_SECVP_VPP - 13), /**<Wrong password, try again*/
        case NDK_ERR_SECVP_WRONG_PIN              :sprintf(g_errMsg,"%s","Try last time");break;//= (NDK_ERR_SECVP_VPP - 14), /**<Try last time.*/
        case NDK_ERR_SECVP_ICCERROR               :sprintf(g_errMsg,"%s","Try too many times");break;//= (NDK_ERR_SECVP_VPP - 15),  /**<Try too many times*/
        case NDK_ERR_SECVP_PIN_BYPASS             :sprintf(g_errMsg,"%s","PIN verification succeed, but PIN length is zero");break;//= (NDK_ERR_SECVP_VPP - 16),  /**<PIN verification succeed, but PIN length is zero*/
        case NDK_ERR_SECVP_ICCFAILURE             :sprintf(g_errMsg,"%s","Fatal error");break;//= (NDK_ERR_SECVP_VPP - 17),  /**<Fatal error.*/
        case NDK_ERR_SECVP_GETCHALLENGE_BAD       :sprintf(g_errMsg,"%s","Response is not 90, 00");break;//= (NDK_ERR_SECVP_VPP - 18),  /**<Response is not 90, 00.*/
        case NDK_ERR_SECVP_GETCHALLENGE_NOT8      :sprintf(g_errMsg,"%s","Invalid response length");break;//= (NDK_ERR_SECVP_VPP - 19),  /**<Invalid response length.*/
        case NDK_ERR_SECVP_PIN_ATTACK_TIMER       :sprintf(g_errMsg,"%s","PIN attack timer activated");break;//= (NDK_ERR_SECVP_VPP - 20),  /**<PIN attack timer activated*/
        case NDK_ERR_SECCR_BASE                   :sprintf(g_errMsg,"%s","Unknown error");break;//= (-1200),                   /**<Unknown error*/
        case NDK_ERR_SECCR_TIMEOUT                :sprintf(g_errMsg,"%s","Get key value timeout");break;//= (NDK_ERR_SECCR_BASE - 1),  /**<Get key value timeout*/
        case NDK_ERR_SECCR_PARAM                  :sprintf(g_errMsg,"%s","Invalid parameter");break;//= (NDK_ERR_SECCR_BASE - 2),  /**<Invalid parameter*/
        case NDK_ERR_SECCR_DBUS                   :sprintf(g_errMsg,"%s","DBUS communication error");break;//= (NDK_ERR_SECCR_BASE - 3),  /**<DBUS communication error*/
        case NDK_ERR_SECCR_MALLOC                 :sprintf(g_errMsg,"%s","Out of memory");break;//= (NDK_ERR_SECCR_BASE - 4),  /**<Out of memory*/
        case NDK_ERR_SECCR_OPEN_RNG               :sprintf(g_errMsg,"%s","Failed to open random number device");break;//= (NDK_ERR_SECCR_BASE - 5),  /**<Failed to open random number device*/
        case NDK_ERR_SECCR_DRV                    :sprintf(g_errMsg,"%s","Failed to call driver function");break;//= (NDK_ERR_SECCR_BASE - 6),  /**<Failed to call driver function*/
        case NDK_ERR_SECCR_KEY_TYPE               :sprintf(g_errMsg,"%s","Wrong key type");break;//= (NDK_ERR_SECCR_BASE - 7),  /**<Wrong key type*/
        case NDK_ERR_SECCR_KEY_LEN                :sprintf(g_errMsg,"%s","Wrong key length");break;//= (NDK_ERR_SECCR_BASE - 8),  /**<Wrong key length*/
        case NDK_ERR_SECCR_GET_KEY                :sprintf(g_errMsg,"%s","Failed to get key");break;//= (NDK_ERR_SECCR_BASE - 9),  /**<Failed to get key*/
        case NDK_ERR_SECKM_BASE                   :sprintf(g_errMsg,"%s","Unknown error");break;//= (-1300),                   /**<Unknown error*/
        case NDK_ERR_SECKM_TIMEOUT                :sprintf(g_errMsg,"%s","Get key value timeout");break;//= (NDK_ERR_SECKM_BASE - 1),  /**<Get key value timeout*/
        case NDK_ERR_SECKM_PARAM                  :sprintf(g_errMsg,"%s","Invalid parameter");break;//= (NDK_ERR_SECKM_BASE - 2),  /**<Invalid parameter*/
        case NDK_ERR_SECKM_DBUS                   :sprintf(g_errMsg,"%s","DBUS communication error");break;//= (NDK_ERR_SECKM_BASE - 3),  /**<DBUS communication error*/
        case NDK_ERR_SECKM_MALLOC                 :sprintf(g_errMsg,"%s","Out of memory");break;//= (NDK_ERR_SECKM_BASE - 4),  /**<Out of memory*/
        case NDK_ERR_SECKM_OPEN_DB                :sprintf(g_errMsg,"%s","Failed to open database");break;//= (NDK_ERR_SECKM_BASE - 5),  /**<Failed to open database*/
        case NDK_ERR_SECKM_DEL_DB                 :sprintf(g_errMsg,"%s","Failed to delete database");break;//= (NDK_ERR_SECKM_BASE - 6),  /**<Failed to delete database*/
        case NDK_ERR_SECKM_DEL_REC                :sprintf(g_errMsg,"%s","Failed to delete record");break;//= (NDK_ERR_SECKM_BASE - 7),  /**<Failed to delete record*/
        case NDK_ERR_SECKM_INSTALL_REC            :sprintf(g_errMsg,"%s","Failed to install key record");break;//= (NDK_ERR_SECKM_BASE - 8),  /**<Failed to install key record*/
        case NDK_ERR_SECKM_READ_REC               :sprintf(g_errMsg,"%s","Failed to read key record");break;//= (NDK_ERR_SECKM_BASE - 9),  /**<Failed to read key record*/
        case NDK_ERR_SECKM_OPT_NOALLOW            :sprintf(g_errMsg,"%s","Operation not allowed");break;//= (NDK_ERR_SECKM_BASE - 10), /**<Operation not allowed*/
        case NDK_ERR_SECKM_KEY_MAC                :sprintf(g_errMsg,"%s","MAC error");break;//= (NDK_ERR_SECKM_BASE - 11), /**<MAC error*/
        case NDK_ERR_SECKM_KEY_TYPE               :sprintf(g_errMsg,"%s","Wrong key type");break;//= (NDK_ERR_SECKM_BASE - 12), /**<Wrong key type*/
        case NDK_ERR_SECKM_KEY_ARCH               :sprintf(g_errMsg,"%s","Wrong key architecture");break;//= (NDK_ERR_SECKM_BASE - 13), /**<Wrong key architecture*/
        case NDK_ERR_SECKM_KEY_LEN                :sprintf(g_errMsg,"%s","Wrong key length");break;//= (NDK_ERR_SECKM_BASE - 14), /**<Wrong key length*/
        case NDK_ERR_RFID_INITSTA                 :sprintf(g_errMsg,"%s","RF chip error or not configured");break;//= -2005, /**<RF chip error or not configured*/
        case NDK_ERR_RFID_NOCARD                  :sprintf(g_errMsg,"%s","No card");break;//= -2008, /**<No card  0x0D*/
        case NDK_ERR_RFID_MULTICARD               :sprintf(g_errMsg,"%s","Multi card detected");break;//= -2009, /**<Multi card detected*/
        case NDK_ERR_RFID_SEEKING                 :sprintf(g_errMsg,"%s","Failed to seek and activate card");break;//= -2010, /**<Failed to seek and activate card*/
        case NDK_ERR_RFID_PROTOCOL                :sprintf(g_errMsg,"%s","Not compliant with ISO1444-4 protocol, e.g. M1 card F");break;//= -2011, /**<Not compliant with ISO1444-4 protocol, e.g. M1 card F*/
        case NDK_ERR_RFID_NOPICCTYPE              :sprintf(g_errMsg,"%s","Card not set 0x01");break;//= -2012, /**<Card not set 0x01*/
        case NDK_ERR_RFID_NOTDETE                 :sprintf(g_errMsg,"%s","Card not detected  0x02");break;//= -2013, /**<Card not detected  0x02*/
        case NDK_ERR_RFID_AANTI                   :sprintf(g_errMsg,"%s","Type A card collision (Multiple cards exist)");break;//= -2014, /**<Type A card collision (Multiple cards exist) 0x03*/
        case NDK_ERR_RFID_RATS                    :sprintf(g_errMsg,"%s","Type A card RATS processing error");break;//= -2015, /**<Type A card RATS processing error 0x04*/
        case NDK_ERR_RFID_BACTIV                  :sprintf(g_errMsg,"%s","Failed to activate Type B card 0x07");break;//= -2016, /**<Failed to activate Type B card 0x07*/
        case NDK_ERR_RFID_ASEEK                   :sprintf(g_errMsg,"%s","Failed to seek type A card (Probably multiple cards exist)");break;//= -2017, /**<Failed to seek type A card (Probably multiple cards exist) 0x0A*/
        case NDK_ERR_RFID_BSEEK                   :sprintf(g_errMsg,"%s","Failed to seek type B card (Probably multiple cards exist)");break;//= -2018, /**<Failed to seek type B card (Probably multiple cards exist) 0x0B*/
        case NDK_ERR_RFID_ABON                    :sprintf(g_errMsg,"%s","Both type A and B cards exist 0x0C");break;//= -2019, /**<Both type A and B cards exist 0x0C*/
        case NDK_ERR_RFID_UPED                    :sprintf(g_errMsg,"%s","Already activated");break;//= -2020, /**<Already activated 0x0E*/
        case NDK_ERR_RFID_NOTACTIV                :sprintf(g_errMsg,"%s","Not activated");break;//= -2021, /**<Not activated*/
        case NDK_ERR_RFID_COLLISION_A             :sprintf(g_errMsg,"%s","Type A Card collision");break;//= -2022, /**<Type A Card collision*/
        case NDK_ERR_RFID_COLLISION_B             :sprintf(g_errMsg,"%s","Type B Card collision");break;//= -2023, /**<Type B Card collision*/
        case NDK_ERR_MI_NOTAGERR                  :sprintf(g_errMsg,"%s","No card");break;//= -2030, /**<No card,                        0xff*/
        case NDK_ERR_MI_CRCERR                    :sprintf(g_errMsg,"%s","CRC error");break;//= -2031, /**<CRC error,                      0xfe*/
        case NDK_ERR_MI_EMPTY                     :sprintf(g_errMsg,"%s","Not empty");break;//= -2032, /**<Not empty,                      0xfd*/
        case NDK_ERR_MI_AUTHERR                   :sprintf(g_errMsg,"%s","Failed to authenticate");break;//= -2033, /**<Failed to authenticate,         0xfc*/
        case NDK_ERR_MI_PARITYERR                 :sprintf(g_errMsg,"%s","Parity error");break;//= -2034, /**<Parity error,                   0xfb*/
        case NDK_ERR_MI_CODEERR                   :sprintf(g_errMsg,"%s","Receiving code error");break;//= -2035, /**<Receiving code error            0xfa*/
        case NDK_ERR_MI_SERNRERR                  :sprintf(g_errMsg,"%s","Anti-collision data check error");break;//= -2036, /**<Anti-collision data check error 0xf8*/
        case NDK_ERR_MI_KEYERR                    :sprintf(g_errMsg,"%s","KEY authentication error");break;//= -2037, /**<KEY authentication error        0xf7*/
        case NDK_ERR_MI_NOTAUTHERR                :sprintf(g_errMsg,"%s","Not authenticated");break;//= -2038, /**<Not authenticated               0xf6*/
        case NDK_ERR_MI_BITCOUNTERR               :sprintf(g_errMsg,"%s","Failed to receive BIT");break;//= -2039, /**<Failed to receive BIT           0xf5*/
        case NDK_ERR_MI_BYTECOUNTERR              :sprintf(g_errMsg,"%s","Failed to receive byte");break;//= -2040, /**<Failed to receive byte          0xf4*/
        case NDK_ERR_MI_WriteFifo                 :sprintf(g_errMsg,"%s","Failed to write FIFO");break;//= -2041, /**<Failed to write FIFO            0xf3*/
        case NDK_ERR_MI_TRANSERR                  :sprintf(g_errMsg,"%s","Failed to send");break;//= -2042, /**<Failed to send                  0xf2*/
        case NDK_ERR_MI_WRITEERR                  :sprintf(g_errMsg,"%s","Failed to error write");break;//= -2043, /**<Failed to error write           0xf1*/
        case NDK_ERR_MI_INCRERR                   :sprintf(g_errMsg,"%s","Failed to increment");break;//= -2044, /**<Failed to increment             0xf0*/
        case NDK_ERR_MI_DECRERR                   :sprintf(g_errMsg,"%s","Failed to decrement");break;//= -2045, /**<Failed to decrement             0xef*/
        case NDK_ERR_MI_OVFLERR                   :sprintf(g_errMsg,"%s","Overflow");break;//= -2046, /**<Overflow                        0xed*/
        case NDK_ERR_MI_FRAMINGERR                :sprintf(g_errMsg,"%s","Frame error");break;//= -2047, /**<Frame error                     0xeb*/
        case NDK_ERR_MI_COLLERR                   :sprintf(g_errMsg,"%s","Collision detected");break;//= -2048, /**<Collision detected              0xe8*/
        case NDK_ERR_MI_INTERFACEERR              :sprintf(g_errMsg,"%s","Fialed to reset interface");break;//= -2049, /**<Fialed to reset interface       0xe6*/
        case NDK_ERR_MI_ACCESSTIMEOUT             :sprintf(g_errMsg,"%s","Receive timeout");break;//= -2050, /**<Receive timeout                 0xe5*/
        case NDK_ERR_MI_PROTOCOLERR               :sprintf(g_errMsg,"%s","Protocol error");break;//= -2051, /**<Protocol error                  0xe4*/
        case NDK_ERR_MI_QUIT                      :sprintf(g_errMsg,"%s","Abnormal abortion");break;//= -2052, /**<Abnormal abortion               0xe2*/
        case NDK_ERR_MI_PPSErr                    :sprintf(g_errMsg,"%s","PPS operation error");break;//= -2053, /**<PPS operation error             0xe1*/
        case NDK_ERR_MI_SpiRequest                :sprintf(g_errMsg,"%s","Failed to request SPI");break;//= -2054, /**<Failed to request SPI           0xa0*/
        case NDK_ERR_MI_NY_IMPLEMENTED            :sprintf(g_errMsg,"%s","Unknown error");break;//= -2055, /**<Unknown error                   0x9c*/
        case NDK_ERR_MI_CardTypeErr               :sprintf(g_errMsg,"%s","Wrong card type");break;//= -2056, /**<Wrong card type                 0x83*/
        case NDK_ERR_MI_ParaErrInIoctl            :sprintf(g_errMsg,"%s","Wrong IOCTL parameter");break;//= -2057, /**<Wrong IOCTL parameter           0x82*/
        case NDK_ERR_MI_Para                      :sprintf(g_errMsg,"%s","Invalid parameter");break;//= -2059, /**<Invalid parameter               0xa9*/
        case NDK_ERR_WIFI_INVDATA                 :sprintf(g_errMsg,"%s","Invalid parameter");break;//= -3001, /**<Invalid parameter*/
        case NDK_ERR_WIFI_DEVICE_FAULT            :sprintf(g_errMsg,"%s","Invalid device state");break;//= -3002, /**<Invalid device state*/
        case NDK_ERR_WIFI_CMD_UNSUPPORTED         :sprintf(g_errMsg,"%s","Command not supported");break;//= -3003, /**<Command not supported*/
        case NDK_ERR_WIFI_DEVICE_UNAVAILABLE      :sprintf(g_errMsg,"%s","Device unavailable");break;//= -3004, /**<Device unavailable*/
        case NDK_ERR_WIFI_DEVICE_NOTOPEN          :sprintf(g_errMsg,"%s","No AP scanned");break;//= -3005, /**<No AP scanned*/
        case NDK_ERR_WIFI_DEVICE_BUSY             :sprintf(g_errMsg,"%s","Device busy");break;//= -3006, /**<Device busy*/
        case NDK_ERR_WIFI_UNKNOWN_ERROR           :sprintf(g_errMsg,"%s","Unknown Error");break;//= -3007, /**<Unknown Error*/
        case NDK_ERR_WIFI_PROCESS_INBADSTATE      :sprintf(g_errMsg,"%s","Failed to connect");break;//= -3008, /**<Failed to connect*/
        case NDK_ERR_WIFI_SEARCH_FAULT            :sprintf(g_errMsg,"%s","Invalied scanning state");break;//= -3009, /**<Invalied scanning state*/
        case NDK_ERR_WIFI_DEVICE_TIMEOUT          :sprintf(g_errMsg,"%s","Device timeout");break;//= -3010, /**<Device timeout*/
        case NDK_ERR_WIFI_NON_CONNECTED           :sprintf(g_errMsg,"%s","Not connected");break;//= -3011, /**<Not connected*/
        case NDK_ERR_RFID_BUSY                    :sprintf(g_errMsg,"%s","Rf card busy");break;//= -3101, /**<Rf card busy*/
        case NDK_ERR_PRN_BUSY                     :sprintf(g_errMsg,"%s","Printer busy");break;//= -3102, /**<Printer busy*/
        case NDK_ERR_ICCARD_BUSY                  :sprintf(g_errMsg,"%s","Samer card busy");break;//= -3103, /**<Samer card busy*/
        case NDK_ERR_MAG_BUSY                     :sprintf(g_errMsg,"%s","MagnetSmart card busy");break;//= -3104, /**<MagnetSmart card busy*/
        case NDK_ERR_USB_BUSY                     :sprintf(g_errMsg,"%s","USB module busy");break;//= -3105, /**<USB module busy*/
        case NDK_ERR_WLM_BUSY                     :sprintf(g_errMsg,"%s","Wireless module busy");break;//= -3106, /**<Wireless module busy*/
        case NDK_ERR_PIN_BUSY                     :sprintf(g_errMsg,"%s","PIN input");break;//= -3107, /**<PIN input*/
        case NDK_ERR_BT_BUSY                      :sprintf(g_errMsg,"%s","Bluetooth module busy");break;//= -3108, /**<Bluetooth module busy*/
        case NDK_ERR_BT_NOT_CONNECTED             :sprintf(g_errMsg,"%s","Bluetooth not connected");break;//= -3201, /**<Bluetooth not connected*/
        case NDK_ERR_POSNDK_BASE                  :sprintf(g_errMsg,"%s","The ERROR prefix of the libnl_ndk.so");break;//= -4000,		                /** The ERROR prefix of the libnl_ndk.so*/
        case NDK_ERR_POSNDK_BUSY                  :sprintf(g_errMsg,"%s","POSNDK Hardware is busy");break;//= (NDK_ERR_POSNDK_BASE-1),	/** POSNDK Hardware is busy*/
        case NDK_ERR_POSNDK_TRANS_BUSY            :sprintf(g_errMsg,"%s","POSNDK transaction is busy");break;//= (NDK_ERR_POSNDK_BASE-2),	/** POSNDK transaction is busy*/
        case NDK_ERR_POSNDK_TRANS_ALREADY         :sprintf(g_errMsg,"%s","POSNDK is already in the transaction");break;//= (NDK_ERR_POSNDK_BASE-3),	/** POSNDK is already in the transaction*/
        case NDK_ERR_POSNDK_TRANS_NOEXIST         :sprintf(g_errMsg,"%s","POSNDK is not in the transaction");break;//= (NDK_ERR_POSNDK_BASE-4),	/** POSNDK is not in the transaction*/
        case NDK_ERR_POSNDK_SAFE_TRIGGER          :sprintf(g_errMsg,"%s","POSNDK Hardware safety trigger");break;//= (NDK_ERR_POSNDK_BASE-5),	/** POSNDK Hardware safety trigger*/
        case NDK_ERR_POSNDK_EVENT_NUM             :sprintf(g_errMsg,"%s","Error Event number");break;//= (NDK_ERR_POSNDK_BASE-6),	/** Error Event number*/
        case NDK_ERR_POSNDK_EVENT_REG_TWICE       :sprintf(g_errMsg,"%s","Duplicate registration event");break;//= (NDK_ERR_POSNDK_BASE-7),	/** Duplicate registration event*/
        case NDK_ERR_POSNDK_EVENT_UNREG_TWICE     :sprintf(g_errMsg,"%s","Did not register");break;//= (NDK_ERR_POSNDK_BASE-8),	/** Did not register*/
        case NDK_ERR_POSNDK_EVENT_INIT            :sprintf(g_errMsg,"%s","init error");break;//= (NDK_ERR_POSNDK_BASE-9),	/** init error*/
        case NDKK_ERR_POSNDK_EVENT_INUSE          :sprintf(g_errMsg,"%s","Events are held by other processes");break;//=  (NDK_ERR_POSNDK_BASE-10),	/** Events are held by other processes*/
        case NDK_ERR_POSNDK_VKB_INITERR           :sprintf(g_errMsg,"%s","Virtual keyboard applications do not exist or cannot be started");break;//=(NDK_ERR_POSNDK_BASE-17),	/** Virtual keyboard applications do not exist or cannot be started*/
        case NDK_ERR_POSNDK_VKB_DATAERR           :sprintf(g_errMsg,"%s","POSNDK VKB data error");break;//= (NDK_ERR_POSNDK_BASE-18),	/** POSNDK VKB data error
        case NDK_ERR_POSNDK_PERMISSION_UNDEFINED  :sprintf(g_errMsg,"%s","POSNDK the permission undefinded");break;//= (NDK_ERR_POSNDK_BASE-21),	/** POSNDK the permission undefinded
        case NDK_ERR_POSNDK_ACCESS_BUSY           :sprintf(g_errMsg,"%s","POSNDK Related operations are held by other processes");break;//= (NDK_ERR_POSNDK_BASE-22),	/** POSNDK Related operations are held by other processes
        case NDK_ERR_LINUX_ERRNO_BASE             :sprintf(g_errMsg,"%s","Error prefix from system function");break;//= -5000, /**<Error prefix from system function*/
        case NDK_ERR_LINUX_TCP_TIMEOUT            :sprintf(g_errMsg,"%s","Wrong TCP remote port");break;//= (NDK_ERR_LINUX_ERRNO_BASE - 110), /**<Wrong TCP remote port*/
        case NDK_ERR_LINUX_TCP_REFUSE             :sprintf(g_errMsg,"%s","TCP remote port not allowed");break;//= (NDK_ERR_LINUX_ERRNO_BASE - 111), /**<TCP remote port not allowed*/
        case NDK_ERR_LINUX_TCP_NOT_OPEN           :sprintf(g_errMsg,"%s","TCP not open");break;//= (NDK_ERR_LINUX_ERRNO_BASE - 88),  /**<TCP not open*/
        //SDK_ERR
        case SDK_ERR_NDK_NOT_SUPPORT              :sprintf(g_errMsg,"%s","Ndk not support");break;
        case SDK_ERR_PARAM                        :sprintf(g_errMsg,"%s","Invalid parameter");break;
        case SDK_ERR_MALLOC_FAILED                :sprintf(g_errMsg,"%s","Out of memory");break;
        case SDK_ERR_TIMEOUT                      :sprintf(g_errMsg,"%s","Timeout");break;
        case SDK_ERR_CANCEL                       :sprintf(g_errMsg,"%s","Cancel");break;
        case SDK_ERR_CARD_MAG_BUSY                :sprintf(g_errMsg,"%s","Magnetic stripe reader is busy");break;
        case SDK_ERR_CARD_IC_BUSY                 :sprintf(g_errMsg,"%s","IC card reader is busy");break;
        case SDK_ERR_CARD_RFID_BUSY               :sprintf(g_errMsg,"%s","RF card reader is busy");break;
        case SDK_ERR_CARD_NO_EXPECT               :sprintf(g_errMsg,"%s","Not expected card type");break;
        case SDK_ERR_MAG_NO_SWIPED                :sprintf(g_errMsg,"%s","Not swiped");break;
        case SDK_ERR_PRN_TTF                      :sprintf(g_errMsg,"%s","Print error");break;
        case SDK_ERR_FILE_OPEN                    :sprintf(g_errMsg,"%s","Failed to open file");break;
        default:
            sprintf(g_errMsg,"%s","unknow error");
            break;
    }
    return g_errMsg;
}
void Log_GetErrInfoFieldID(JNIEnv *env){
    //noto:Java层必须改为static
    LOGD_FMT("");
    jclass temp = (*env)->FindClass(env, "com/newland/sdk/mtype/common/ErrorMsg");
    if(temp == NULL){
        LOGD_FMT(">>>ErrorMsg.Class==NULL");
        return;
    }
    packagerClass = (jclass)(*env)->NewGlobalRef(env,temp);
    errCodeId = (*env)->GetStaticFieldID(env,packagerClass,"errCode","Ljava/lang/String;");
    errMsgId = (*env)->GetStaticFieldID(env,packagerClass,"errMsg","Ljava/lang/String;");
    otherMsgId = (*env)->GetStaticFieldID(env,packagerClass,"otherMsg","Ljava/lang/String;");
}
static void __setErrInfo(int errCode,char *otherMsg){
    if(packagerClass == NULL || errCodeId == NULL || errMsgId == NULL || otherMsgId == NULL){
        LOGD_FMT("packagerClass[%d] errCodeId[%d] errMsgId[%d] otherMsgId[%d]",packagerClass,errCodeId,errMsgId,otherMsgId);
        return;
    }
    JNIEnv *env = NULL;
    jboolean isAttached = JNI_FALSE;
    int ret = (*gJavaVM)->GetEnv(gJavaVM,(void **) &env, JNI_VERSION_1_4);
    if(ret < 0 ) {
        ret =  (*gJavaVM)->AttachCurrentThread(gJavaVM,(JNIEnv **) &env, NULL);
        if (ret < 0) {
            LOGD_FMT(">>>AttachCurrentThread error.");
            return;
        }
        isAttached = JNI_TRUE;
    }

    char err[8];
    memset(err,0,sizeof(err));
    sprintf(err,"%d",errCode);
    jstring errcode = (*env)->NewStringUTF(env,err);
    (*env)->SetStaticObjectField(env,packagerClass,errCodeId,errcode);
    (*env)->DeleteLocalRef(env,errcode);

    char *errMsg = getErrMsg(errCode);
    jstring errmsg = (*env)->NewStringUTF(env,errMsg);
    (*env)->SetStaticObjectField(env,packagerClass,errMsgId,errmsg);
    (*env)->DeleteLocalRef(env,errmsg);

    jstring othermsg = (*env)->NewStringUTF(env,otherMsg);
    (*env)->SetStaticObjectField(env,packagerClass,otherMsgId,othermsg);
    (*env)->DeleteLocalRef(env,othermsg);
    if(err!=NULL && errMsg!=NULL && otherMsg!=NULL){
        LOGE_FMT(">>>errCode[%s] errMsg[%s] otherMsg[%s]",err,errMsg,otherMsg);
    }else{
        LOGE_FMT(">>>errCode[%d] errMsg[%d] otherMsg[%d]",err,errMsg,otherMsg);
    }
    if(isAttached)
        (*gJavaVM)->DetachCurrentThread(gJavaVM);
}

static void __setNdkErr(char *tag,int funRet,const char*file,const char*function,long line,int cmd){
    char flag = 'A';
    if(tag != NULL && strlen(tag) >= 1){
        memcpy(&flag,tag,1);
    }
    if(flag == '#'){
        return;
    }
    memset(g_otherMsg,0, sizeof(g_otherMsg));
    sprintf(g_otherMsg,"%s|%s|%s|%d|%s",VERSION,file,function,line,tag);
    SetErrInfo(cmd,funRet,g_otherMsg);
}
int Log_ExecNdkFun(char *tag,int funRet,int expectRet,const char*file,const char*function,long line,int cmd){
    if(funRet != expectRet){
        Udebug.ERROR_MSG_LOG("[%s][NDK][%s][%s][%d][%s:%d]\n",VERSION,file,function,line,tag,funRet);
        __setNdkErr(tag,funRet,file,function,line,cmd);
        return (1==0);
    }
    return (1==1);
}

void Log_SetErrMsg(int errCode,const char*file,const char*function,long line,int cmd){
    memset(g_otherMsg,0, sizeof(g_otherMsg));
    sprintf(g_otherMsg,"%s|%s|%s|%d",VERSION,file,function,line);
    SetErrInfo(cmd,errCode,g_otherMsg);
}

static int errInfoIndex = 0;
ErrorInfo errorInfo[ERROR_INFO_MAX];
void Log_ErrInfoInit(){
    memset(errorInfo,0, sizeof(ErrorInfo)*ERROR_INFO_MAX);
}
void SetErrInfo(int cmd,int errCode,char *otherMsg){
    if(cmd == COMMAND_NONE){
        return;
    }
    THREAD_MUTEX_LOCK(THREAD_MUTEX_INDEX_ERRINFO);
    int i=0,index = -1;
    for(i=0; i < ERROR_INFO_MAX;i++){
        if(errorInfo[i].cmd == cmd){
            index = i;
            break;
        }
    }
    if(index == -1){
        index = errInfoIndex%ERROR_INFO_MAX;
        if(errInfoIndex++ >= ERROR_INFO_MAX*2){
            errInfoIndex = 0;
        }
    }
    memset(&errorInfo[index],0, sizeof(ErrorInfo));

    errorInfo[index].cmd = cmd;

    memset(errorInfo[index].errCode,0,sizeof(errorInfo[index].errCode));
    sprintf(errorInfo[index].errCode,"%d",errCode);

    char *errMsg = getErrMsg(errCode);
    memcpy(errorInfo[index].errMsg,errMsg,strlen(errMsg));

    memcpy(errorInfo[index].otherMsg,otherMsg,strlen(otherMsg));

    LOGE_FMT(">>>command[0x%x] errCode[%s] errMsg[%s] otherMsg[%s] MsgIndex[%d]",errorInfo[index].cmd,errorInfo[index].errCode,errorInfo[index].errMsg,errorInfo[index].otherMsg,index);

    THREAD_MUTEX_UNLOCK(THREAD_MUTEX_INDEX_ERRINFO);
}

int Log_GetErrInfo(JNIEnv *env,int cmd,jbyteArray errCode,jbyteArray errMsg,jbyteArray otherMsg) {
    ErrorInfo info;
    int command = (cmd&0xFFFF),index = -1;
    memset(&info,0,sizeof(ErrorInfo));
    THREAD_MUTEX_LOCK(THREAD_MUTEX_INDEX_ERRINFO);
    int i = 0;
    for(i=0; i < ERROR_INFO_MAX;i++){
        if(command == errorInfo[i].cmd){
            index = i;
            memcpy(&info,&errorInfo[i],sizeof(ErrorInfo));
            memset(&errorInfo[i],0,sizeof(ErrorInfo));
            break;
        }
    }
    THREAD_MUTEX_UNLOCK(THREAD_MUTEX_INDEX_ERRINFO);

    if(info.cmd != command){
        LOGE_FMT(">>>command[0x%x] msg null",command);
        return -1;
    }
    LOGE_FMT(">>>command[0x%x] errCode[%s] errMsg[%s] otherMsg[%s] MsgIndex[%d]",info.cmd,info.errCode,info.errMsg,info.otherMsg,index);
    (*env)->SetByteArrayRegion(env, errCode, 0, strlen(info.errCode), info.errCode);
    (*env)->SetByteArrayRegion(env, errMsg, 0, strlen(info.errMsg), info.errMsg);
    (*env)->SetByteArrayRegion(env, otherMsg, 0, strlen(info.otherMsg), info.otherMsg);
    return 0;
}
