/*******************************************************************************
 * Copyright (C) 2019 Newland Payment Technology Co., Ltd All Rights Reserved
 ******************************************************************************/
#ifndef __NAPI__H
#define __NAPI__H

#ifdef __cplusplus
extern "C" {
#endif

#include <stdio.h>
#include <stdbool.h>

/** @addtogroup CommonDefinitions
* @{
*/

#ifndef uint
typedef unsigned int uint;
#endif
#ifndef uchar
typedef unsigned char uchar;
#endif
#ifndef ushort
typedef unsigned short ushort;
#endif
#ifndef ulong
typedef unsigned long ulong;
#endif


#ifndef uint8_t
typedef unsigned char uint8_t;
#endif
#ifndef uint32_t
typedef unsigned int uint32_t;
#endif
#ifndef uint16_t
typedef unsigned short uint16_t;
#endif

typedef void* NAPI_HANDLE;

#ifndef __EXPORTED_SYMBOL__
#define __EXPORTED_SYMBOL__  __attribute__((visibility("default")))
#endif

/** @}*/ // End of CommonDefinitions

/** @addtogroup ErrorCodes
* @{
*/

/**
 *@brief Error codes
*/
typedef enum {
        NAPI_OK,                                /**<Success*/
        NAPI_ERR                        = -1,   /**<Fail*/
        NAPI_ERR_INIT_CONFIG            = -2,   /**<Failed to initialize configuration*/
        NAPI_ERR_CREAT_WIDGET           = -3,   /**<Failed to error creating interface*/
        NAPI_ERR_OPEN_DEV               = -4,   /**<Failed to error opening device file*/
        NAPI_ERR_IOCTL                  = -5,   /**<Failed to call driver function*/
        NAPI_ERR_PARA                   = -6,   /**<Invalid parameter*/
        NAPI_ERR_PATH                   = -7,   /**<Invalid file path*/
        NAPI_ERR_DECODE_IMAGE           = -8,   /**<Failed to decode image*/
        NAPI_ERR_MALLOC                 = -9,   /**<Out of memory*/
        NAPI_ERR_TIMEOUT                = -10,  /**<Timeout error*/
        NAPI_ERR_QUIT                   = -11,  /**<Press Cancel to exit*/
        NAPI_ERR_WRITE                  = -12,  /**<Failed to write into file*/
        NAPI_ERR_READ                   = -13,  /**<Failed to read from file*/
        NAPI_ERR_OVERFLOW               = -15,  /**<Buffer overflow*/
        NAPI_ERR_SHM                    = -16,  /**<Failed to share memory*/
        NAPI_ERR_NO_DEVICES             = -17,  /**<Device not available*/
        NAPI_ERR_NOT_SUPPORT            = -18,  /**<Feature not supported*/
        NAPI_ALREADY_DONE               = -19,  /**<New logo's checksum is the same to the checksum stored in flash partition*/
        NAPI_ERR_NOSWIPED               = -50,  /**<No magnetSmart card swiping*/
        NAPI_ERR_SWIPED_DATA            = -51,  /**<Wrong magnetSmart card data*/
        NAPI_ERR_USB_LINE_UNCONNECT     = -100, /**<Usb cable not connected*/
        NAPI_ERR_NO_SIMCARD             = -201, /**<No SIM card*/
        NAPI_ERR_PIN                    = -202, /**<Wrong SIM card password*/
        NAPI_ERR_PIN_LOCKED             = -203, /**<SIM card locked*/
        NAPI_ERR_PIN_UNDEFINE           = -204, /**<Undefined SIM card error*/
        NAPI_ERR_EMPTY                  = -205, /**<Empty string returned*/
        NAPI_ERR_ETH_PULLOUT            = -250, /**<Ethernet cable not plugged*/

        NAPI_ERR_PPP_PARAM              = -301, /**<Invalid PPP parameter*/
        NAPI_ERR_PPP_DEVICE             = -302, /**<Invalid PPP device*/
        NAPI_ERR_PPP_OPEN               = -303, /**<PPP already open*/

        NAPI_ERR_TCP_ALLOC              = -304, /**<Failed to allocate*/
        NAPI_ERR_TCP_PARAM              = -305, /**<Invalid parameter*/
        NAPI_ERR_TCP_TIMEOUT            = -306, /**<Transmission timeout*/
        NAPI_ERR_TCP_INVADDR            = -307, /**<Invalid address*/
        NAPI_ERR_TCP_CONNECT            = -308, /**<No connection*/
        NAPI_ERR_TCP_PROTOCOL           = -309, /**<Protocol error*/
        NAPI_ERR_TCP_NETWORK            = -310, /**<Network error*/
        NAPI_ERR_TCP_SEND               = -311, /**<Failed to send*/
        NAPI_ERR_TCP_RECV               = -312, /**<Failed to receive*/
        
        NAPI_ERR_SOCKET             = -319, /**<Failed to socket*/
		NAPI_ERR_WLM_SOCKET             = -319, /**<Failed to socket*/
        NAPI_ERR_WLM_SEND_AT_FAIL       = -320, /**<Failed to transmit AT*/

        NAPI_ERR_SSL_PARAM              = -350, /**<Invalid parameter*/
        NAPI_ERR_SSL_ALREADCLOSE        = -351, /**<Connection already closed*/
        NAPI_ERR_SSL_ALLOC              = -352, /**<Failed to allocate*/
        NAPI_ERR_SSL_INVADDR            = -353, /**<Invalid address*/
        NAPI_ERR_SSL_TIMEOUT            = -354, /**<Connection Timeout*/
        NAPI_ERR_SSL_MODEUNSUPPORTED    = -355, /**<Mode not supported*/
        NAPI_ERR_SSL_SEND               = -356, /**<Failed to send*/
        NAPI_ERR_SSL_RECV               = -357, /**<Failed to receive*/
        NAPI_ERR_SSL_CONNECT            = -358, /**<No connection*/

        NAPI_ERR_NET_GETADDR            = -401, /**<Failed to obtain local address or subnet mask*/
        NAPI_ERR_NET_GATEWAY            = -402, /**<Failed to obtain gateway address*/
        NAPI_ERR_NET_ADDRILLEGAL        = -403, /**<Failed to obtain address format*/
        NAPI_ERR_NET_UNKNOWN_COMMTYPE   = -404, /**<Unknown type of communication*/
        NAPI_ERR_NET_INVALIDIPSTR       = -405, /**<Invalid IP string*/
        NAPI_ERR_NET_UNSUPPORT_COMMTYPE = -406, /**<Type of communication not supported*/

        NAPI_ERR_THREAD_PARAM           = -450, /**<Invalid address*/
        NAPI_ERR_THREAD_ALLOC           = -451, /**<Failed to allocate*/
        NAPI_ERR_THREAD_CMDUNSUPPORTED  = -452, /**<Command not supported*/

        NAPI_ERR_MODEM_RESETFAIL        = -501, /**<Failed to reset*/
        NAPI_ERR_MODEM_GETSTATUSFAIL    = -502, /**<Failed to get status*/
        NAPI_ERR_MODEM_SLEPPFAIL        = -503, /**<Failed to sleep*/
        NAPI_ERR_MODEM_SDLCINITFAIL     = -504, /**<Failed to initialize in sync mode*/
        NAPI_ERR_MODEM_INIT_NOT         = -505, /**<Not initialized*/
        NAPI_ERR_MODEM_SDLCWRITEFAIL    = -506, /**<Failed to write in sync mode*/
        NAPI_ERR_MODEM_ASYNWRITEFAIL    = -507, /**<Failed to write in async mode*/
        NAPI_ERR_MODEM_ASYNDIALFAIL     = -508, /**<Failed to dial in async mode*/
        NAPI_ERR_MODEM_ASYNINITFAIL     = -509, /**<Failed to initialize in async mode*/
        NAPI_ERR_MODEM_SDLCHANGUPFAIL   = -510, /**<Failed to hangup in sync mode*/
        NAPI_ERR_MODEM_ASYNHANGUPFAIL   = -511, /**<Failed to hangup in async mode*/
        NAPI_ERR_MODEM_SDLCCLRBUFFAIL   = -512, /**<Failed to clear buffer in sync mode*/
        NAPI_ERR_MODEM_ASYNCLRBUFFAIL   = -513, /**<Failed to clear buffer in async mode*/
        NAPI_ERR_MODEM_ATCOMNORESPONSE  = -514, /**<No response for AT command*/
        NAPI_ERR_MODEM_PORTWRITEFAIL    = -515, /**<Failed to write data to modem port*/
        NAPI_ERR_MODEM_SETCHIPFAIL      = -516, /**<Failed to set register*/
        NAPI_ERR_MODEM_STARTSDLCTASK    = -517, /**<Failed to start SDLC task*/
        NAPI_ERR_MODEM_GETBUFFLENFAIL   = -518, /**<Failed to get data lenth*/
        NAPI_ERR_MODEM_QUIT             = -519, /**<Hand out*/
        NAPI_ERR_MODEM_NOPREDIAL        = -520, /**<No predial*/
        NAPI_ERR_MODEM_NOCARRIER        = -521, /**<No carrier*/
        NAPI_ERR_MODEM_NOLINE           = -523, /**<No cable*/
        NAPI_ERR_MODEM_OTHERMACHINE     = -524, /**<Collision detected*/
        NAPI_ERR_MODEM_PORTREADFAIL     = -525, /**<Failed to read data from modem port*/
        NAPI_ERR_MODEM_CLRBUFFAIL       = -526, /**<Failed to clear buffer*/
        NAPI_ERR_MODEM_ATCOMMANDERR     = -527, /**<AT command error*/
        NAPI_ERR_MODEM_STATUSUNDEFINE   = -528, /**<State unrecognized*/
        NAPI_ERR_MODEM_GETVERFAIL       = -529, /**<Failed to get version*/
        NAPI_ERR_MODEM_SDLCDIALFAIL     = -530, /**<Failed to dial in sync mode*/
        NAPI_ERR_MODEM_SELFADAPTFAIL    = -531, /**<Failed to auto-negotiation*/
        NAPI_ERR_MODEM_SELFADAPTCANCEL  = -532, /**<Auto-negotiation canceled*/

        NAPI_ERR_ICC_WRITE_ERR          = -601, /**<Failed to write*/
        NAPI_ERR_ICC_COPYERR            = -602, /**<Failed to copy kernel data*/
        NAPI_ERR_ICC_POWERON_ERR        = -603, /**<Failed to powerup*/
        NAPI_ERR_ICC_COM_ERR            = -604, /**<Command error*/
        NAPI_ERR_ICC_CARDPULL_ERR       = -605, /**<Card not present*/
        NAPI_ERR_ICC_CARDNOREADY_ERR    = -606, /**<Card not ready*/

        NAPI_ERR_USDDISK_PARAM          = -650, /**<Invalid parameter*/
        NAPI_ERR_USDDISK_DRIVELOADFAIL  = -651, /**<Failed to load USB stick or SD card*/
        NAPI_ERR_USDDISK_NONSUPPORTTYPE = -652, /**<Type not supported*/
        NAPI_ERR_USDDISK_UNMOUNTFAIL    = -653, /**<Failed to mount*/
        NAPI_ERR_USDDISK_UNLOADDRIFAIL  = -654, /**<Failed to unload driver*/
        NAPI_ERR_USDDISK_IOCFAIL        = -655, /**<Failed to call driver function*/
        NAPI_ERR_USDDISK_PWDERR         = -656, /**<password error*/
        NAPI_ERR_USDDISK_NOPWD          = -657,	/**<no password*/
	NAPI_ERR_USDDISK_HAVEPWD        = -658, /**<have password*/

        NAPI_ERR_APP_BASE               = -800, /**<Unknown error*/
        NAPI_ERR_APP_NOT_EXIST                = (NAPI_ERR_APP_BASE - 1), /**<Application not exist*/
        NAPI_ERR_APP_NOT_MATCH                = (NAPI_ERR_APP_BASE - 2), /**<Patch not match*/
        NAPI_ERR_APP_FAIL_SEC                 = (NAPI_ERR_APP_BASE - 3), /**<Failed to access tamper status*/
        NAPI_ERR_APP_SEC_ATT                  = (NAPI_ERR_APP_BASE - 4), /**<Tamper detected*/
        NAPI_ERR_APP_FILE_EXIST               = (NAPI_ERR_APP_BASE - 5), /**<Application file already exists*/
        NAPI_ERR_APP_FILE_NOT_EXIST           = (NAPI_ERR_APP_BASE - 6), /**<Application file not exist*/
        NAPI_ERR_APP_FAIL_AUTH                = (NAPI_ERR_APP_BASE - 7), /**<Failed to authenticate certificate*/
        NAPI_ERR_APP_LOW_VERSION              = (NAPI_ERR_APP_BASE - 8), /**<Patch version lower than the application version*/
        NAPI_ERR_APP_MAX_CHILD                = (NAPI_ERR_APP_BASE - 9), /**<More than maximum number of running applications*/
        NAPI_ERR_APP_CREAT_CHILD              = (NAPI_ERR_APP_BASE - 10), /**<Failed to create child process*/
        NAPI_ERR_APP_WAIT_CHILD               = (NAPI_ERR_APP_BASE - 11), /**<Failed to wait for the child to exit*/
        NAPI_ERR_APP_FILE_READ                = (NAPI_ERR_APP_BASE - 12), /**<Failed to read file*/
        NAPI_ERR_APP_FILE_WRITE               = (NAPI_ERR_APP_BASE - 13), /**<Failed to write file*/
        NAPI_ERR_APP_FILE_STAT                = (NAPI_ERR_APP_BASE - 14), /**<Failed to get file information*/
        NAPI_ERR_APP_FILE_OPEN                = (NAPI_ERR_APP_BASE - 15), /**<Failed to open file*/
        NAPI_ERR_APP_NLD_HEAD_LEN             = (NAPI_ERR_APP_BASE - 16), /**<Wrong length of the NLD file header information*/
        NAPI_ERR_APP_PUBKEY_EXPIRED           = (NAPI_ERR_APP_BASE - 17), /**<Public key expired*/
        NAPI_ERR_APP_MMAP                     = (NAPI_ERR_APP_BASE - 18), /**<Failed to map memory*/
        NAPI_ERR_APP_MALLOC                   = (NAPI_ERR_APP_BASE - 19), /**<Out of memory*/
        NAPI_ERR_APP_SIGN_DECRYPT             = (NAPI_ERR_APP_BASE - 20), /**<Failed to decrypt signature data*/
        NAPI_ERR_APP_SIGN_CHECK               = (NAPI_ERR_APP_BASE - 21), /**<Failed to validate signature data*/
        NAPI_ERR_APP_MUNMAP                   = (NAPI_ERR_APP_BASE - 22), /**<Failed to unmap memory*/
        NAPI_ERR_APP_TAR                      = (NAPI_ERR_APP_BASE - 23), /**<Failed to untar data*/
        NAPI_ERR_APP_KEY_UPDATE_BAN           = (NAPI_ERR_APP_BASE - 24), /**<Key update is prohibited*/
        NAPI_ERR_APP_FIRM_PATCH_VERSION       = (NAPI_ERR_APP_BASE - 25), /**<Firmware patch version do not match*/
        NAPI_ERR_APP_CERT_HAS_EXPIRED         = (NAPI_ERR_APP_BASE - 26), /**<Certificate expired*/
        NAPI_ERR_APP_CERT_NOT_YET_VALID       = (NAPI_ERR_APP_BASE - 27), /**<Invalid certificate*/
        NAPI_ERR_APP_FILE_NAME_TOO_LONG       = (NAPI_ERR_APP_BASE - 28), /**<File name length larger than 32 bytes*/
        NAPI_ERR_APP_CA_ALREADY_CUSTOMIZED    = (NAPI_ERR_APP_BASE - 29), /**<Application CA has been customized*/
        NAPI_ERR_APP_FILE_CHK                 = (NAPI_ERR_APP_BASE - 30), /**<File check error*/

        NAPI_ERR_SECP_BASE                    = (-1000),                  /**<Unknown error*/
        NAPI_ERR_SECP_TIMEOUT                 = (NAPI_ERR_SECP_BASE - 1),  /**<Get key value timeout*/
        NAPI_ERR_SECP_PARAM                   = (NAPI_ERR_SECP_BASE - 2),  /**<Invalid parameter*/
        NAPI_ERR_SECP_DBUS                    = (NAPI_ERR_SECP_BASE - 3),  /**<DBUS communication error*/
        NAPI_ERR_SECP_MALLOC                  = (NAPI_ERR_SECP_BASE - 4),  /**<Out of memory*/
        NAPI_ERR_SECP_OPEN_SEC                = (NAPI_ERR_SECP_BASE - 5),  /**<Failed to open security device*/
        NAPI_ERR_SECP_SEC_DRV                 = (NAPI_ERR_SECP_BASE - 6),  /**<Failed to call driver function*/
        NAPI_ERR_SECP_GET_RNG                 = (NAPI_ERR_SECP_BASE - 7),  /**<Failed to get random number*/
        NAPI_ERR_SECP_GET_KEY                 = (NAPI_ERR_SECP_BASE - 8),  /**<Failed to get key value*/
        NAPI_ERR_SECP_KCV_CHK                 = (NAPI_ERR_SECP_BASE - 9),  /**<KCV check error*/
        NAPI_ERR_SECP_GET_CALLER              = (NAPI_ERR_SECP_BASE - 10), /**<Failed to get caller info*/
        NAPI_ERR_SECP_OVERRUN                 = (NAPI_ERR_SECP_BASE - 11), /**<Overrun*/
        NAPI_ERR_SECP_NO_PERMIT               = (NAPI_ERR_SECP_BASE - 12), /**<Operation not allowed*/
        NAPI_ERR_SECP_TAMPER                  = (NAPI_ERR_SECP_BASE - 13), /**<Tamper detected*/
	NAPI_ERR_SECP_UNSUPPORT               = (NAPI_ERR_SECP_BASE - 14), /**<the feature is not supported*/
        NAPI_ERR_SECVP_BASE                   = (-1100),                   /**<Unknown error*/
        NAPI_ERR_SECVP_TIMEOUT                = (NAPI_ERR_SECVP_BASE - 1), /**<Get key value timeout*/
        NAPI_ERR_SECVP_PARAM                  = (NAPI_ERR_SECVP_BASE - 2), /**<Invalid parameter*/
        NAPI_ERR_SECVP_DBUS                   = (NAPI_ERR_SECVP_BASE - 3), /**<DBUS communication error*/
        NAPI_ERR_SECVP_OPEN_EVENT0            = (NAPI_ERR_SECVP_BASE - 4), /**<Failed to open event device*/
        NAPI_ERR_SECVP_SCAN_VAL               = (NAPI_ERR_SECVP_BASE - 5), /**<Scan value out of range*/
        NAPI_ERR_SECVP_OPEN_RNG               = (NAPI_ERR_SECVP_BASE - 6), /**<Failed to open random number device*/
        NAPI_ERR_SECVP_GET_RNG                = (NAPI_ERR_SECVP_BASE - 7), /**<Failed to get random number*/
        NAPI_ERR_SECVP_GET_ESC                = (NAPI_ERR_SECVP_BASE - 8), /**<User cancel*/
	NAPI_ERR_SECVP_UNSUPPORT              = (NAPI_ERR_SECVP_BASE - 9), /**<the feature is not supported*/
        NAPI_ERR_SECVP_VPP                    = (-1120),                   /**<Unknown error*/
        NAPI_ERR_SECVP_INVALID_KEY            = (NAPI_ERR_SECVP_VPP),      /**<Invalid key*/
        NAPI_ERR_SECVP_NOT_ACTIVE             = (NAPI_ERR_SECVP_VPP - 1),  /**<VPPIs not active*/
        NAPI_ERR_SECVP_TIMED_OUT              = (NAPI_ERR_SECVP_VPP - 2),  /**<VPP initialization timeout*/
        NAPI_ERR_SECVP_ENCRYPT_ERROR          = (NAPI_ERR_SECVP_VPP - 3),  /**<Failed to encrypt*/
        NAPI_ERR_SECVP_BUFFER_FULL            = (NAPI_ERR_SECVP_VPP - 4),  /**<Buffer full*/
        NAPI_ERR_SECVP_PIN_KEY                = (NAPI_ERR_SECVP_VPP - 5),  /**<Data key pressed, echo "*".*/
        NAPI_ERR_SECVP_ENTER_KEY              = (NAPI_ERR_SECVP_VPP - 6),  /**<Enter key pressed, process PIN*/
        NAPI_ERR_SECVP_BACKSPACE_KEY          = (NAPI_ERR_SECVP_VPP - 7),  /**<Backspace key pressed.*/
        NAPI_ERR_SECVP_CLEAR_KEY              = (NAPI_ERR_SECVP_VPP - 8),  /**<Clear key pressed, remove all the '*'.*/
        NAPI_ERR_SECVP_CANCEL_KEY             = (NAPI_ERR_SECVP_VPP - 9),  /**<Cancel key pressed.*/
        NAPI_ERR_SECVP_GENERALERROR           = (NAPI_ERR_SECVP_VPP - 10), /**<Internal error.*/
        NAPI_ERR_SECVP_CUSTOMERCARDNOTPRESENT = (NAPI_ERR_SECVP_VPP - 11), /**<Smart card not present*/
        NAPI_ERR_SECVP_HTCCARDERROR           = (NAPI_ERR_SECVP_VPP - 12), /**<Failed to access smart card*/
        NAPI_ERR_SECVP_WRONG_PIN_LAST_TRY     = (NAPI_ERR_SECVP_VPP - 13), /**<Wrong password, try again*/
        NAPI_ERR_SECVP_WRONG_PIN              = (NAPI_ERR_SECVP_VPP - 14), /**<Try last time.*/
        NAPI_ERR_SECVP_ICCERROR               = (NAPI_ERR_SECVP_VPP - 15),  /**<Try too many times*/
        NAPI_ERR_SECVP_PIN_BYPASS             = (NAPI_ERR_SECVP_VPP - 16),  /**<PIN verification succeed, but PIN length is zero*/
        NAPI_ERR_SECVP_ICCFAILURE             = (NAPI_ERR_SECVP_VPP - 17),  /**<Fatal error.*/
        NAPI_ERR_SECVP_GETCHALLENGE_BAD       = (NAPI_ERR_SECVP_VPP - 18),  /**<Response is not 90, 00.*/
        NAPI_ERR_SECVP_GETCHALLENGE_NOT8      = (NAPI_ERR_SECVP_VPP - 19),  /**<Invalid response length.*/
        NAPI_ERR_SECVP_PIN_ATTACK_TIMER       = (NAPI_ERR_SECVP_VPP - 20),  /**<PIN attack timer activated*/
	NAPI_ERR_SECVP_PIN_TOO_SHORT          = (NAPI_ERR_SECVP_VPP - 21),  /**<PIN too short*/
        NAPI_ERR_SECCR_BASE                   = (-1200),                   /**<Unknown error*/
        NAPI_ERR_SECCR_TIMEOUT                = (NAPI_ERR_SECCR_BASE - 1),  /**<Get key value timeout*/
        NAPI_ERR_SECCR_PARAM                  = (NAPI_ERR_SECCR_BASE - 2),  /**<Invalid parameter*/
        NAPI_ERR_SECCR_DBUS                   = (NAPI_ERR_SECCR_BASE - 3),  /**<DBUS communication error*/
        NAPI_ERR_SECCR_MALLOC                 = (NAPI_ERR_SECCR_BASE - 4),  /**<Out of memory*/
        NAPI_ERR_SECCR_OPEN_RNG               = (NAPI_ERR_SECCR_BASE - 5),  /**<Failed to open random number device*/
        NAPI_ERR_SECCR_DRV                    = (NAPI_ERR_SECCR_BASE - 6),  /**<Failed to call driver function*/
        NAPI_ERR_SECCR_KEY_TYPE               = (NAPI_ERR_SECCR_BASE - 7),  /**<Wrong key type*/
        NAPI_ERR_SECCR_KEY_LEN                = (NAPI_ERR_SECCR_BASE - 8),  /**<Wrong key length*/
        NAPI_ERR_SECCR_GET_KEY                = (NAPI_ERR_SECCR_BASE - 9),  /**<Failed to get key*/

        NAPI_ERR_SECKM_BASE                   = (-1300),                   /**<Unknown error*/
        NAPI_ERR_SECKM_TIMEOUT                = (NAPI_ERR_SECKM_BASE - 1),  /**<Get key value timeout*/
        NAPI_ERR_SECKM_PARAM                  = (NAPI_ERR_SECKM_BASE - 2),  /**<Invalid parameter*/
        NAPI_ERR_SECKM_DBUS                   = (NAPI_ERR_SECKM_BASE - 3),  /**<DBUS communication error*/
        NAPI_ERR_SECKM_MALLOC                 = (NAPI_ERR_SECKM_BASE - 4),  /**<Out of memory*/
        NAPI_ERR_SECKM_OPEN_DB                = (NAPI_ERR_SECKM_BASE - 5),  /**<Failed to open database*/
        NAPI_ERR_SECKM_DEL_DB                 = (NAPI_ERR_SECKM_BASE - 6),  /**<Failed to delete database*/
        NAPI_ERR_SECKM_DEL_REC                = (NAPI_ERR_SECKM_BASE - 7),  /**<Failed to delete record*/
        NAPI_ERR_SECKM_INSTALL_REC            = (NAPI_ERR_SECKM_BASE - 8),  /**<Failed to install key record*/
        NAPI_ERR_SECKM_READ_REC               = (NAPI_ERR_SECKM_BASE - 9),  /**<Failed to read key record*/
        NAPI_ERR_SECKM_OPT_NOALLOW            = (NAPI_ERR_SECKM_BASE - 10), /**<Operation not allowed*/
        NAPI_ERR_SECKM_KEY_MAC                = (NAPI_ERR_SECKM_BASE - 11), /**<MAC error*/
        NAPI_ERR_SECKM_KEY_TYPE               = (NAPI_ERR_SECKM_BASE - 12), /**<Wrong key type*/
        NAPI_ERR_SECKM_KEY_ARCH               = (NAPI_ERR_SECKM_BASE - 13), /**<Wrong key architecture*/
        NAPI_ERR_SECKM_KEY_LEN                = (NAPI_ERR_SECKM_BASE - 14), /**<Wrong key length*/
	NAPI_ERR_SECKM_SYS                    = (NAPI_ERR_SECKM_BASE - 15), /**<system unknown error*/
	NAPI_ERR_SECKM_UNSUPPORT              = (NAPI_ERR_SECKM_BASE - 16), /**<the feature is not supported*/
	NAPI_ERR_SECKM_KEY_ALREADY_USED       = (NAPI_ERR_SECKM_BASE - 17), /**<the key is used*/
	NAPI_ERR_SECKM_CALCKCV                = (NAPI_ERR_SECKM_BASE - 18), /**< KCV calculating  error*/
	NAPI_ERR_SECKM_ASYM_GENERATE_BUSY 	  = (NAPI_ERR_SECKM_BASE - 19),	  /** asym random generate process busy **/
	NAPI_ERR_SECKM_ASYM_GENERATE_INIT     = (NAPI_ERR_SECKM_BASE - 20),      /** asym random generate INIT **/
	NAPI_ERR_SECKM_ASYM_GENERATE_PROCESSING  = (NAPI_ERR_SECKM_BASE - 21),    /** asym random generate process **/

	//key store
	NAPI_ERR_SECKS_BASE                   = (-1400),
	NAPI_ERR_SECKS_TIMEOUT                = (NAPI_ERR_SECKS_BASE - 1),  /**<Get key value timeout*/
	NAPI_ERR_SECKS_PARAM                  = (NAPI_ERR_SECKS_BASE - 2),  /**<Invalid parameter*/
	//kla
	NAPI_ERR_SECKLA_BASE                  = (-1500),
	NAPI_ERR_SECKLA_ERR_INTERNAL          = (NAPI_ERR_SECKLA_BASE -1),  /*Unspecified internal error.*/
	NAPI_ERR_SECKLA_PARAM                 = (NAPI_ERR_SECKLA_BASE -2),  /*Invalid parameter passed to function.*/
	NAPI_ERR_SECKLA_ERR_INVALID_CRT       = (NAPI_ERR_SECKLA_BASE -3),  /*Invalid certification*/
	NAPI_ERR_SECKLA_ERR_INVALID_SIG       = (NAPI_ERR_SECKLA_BASE -4),  /*Invalid nonce signature*/
	NAPI_ERR_SECKLA_ERR_KEY_NOT_FOUND     = (NAPI_ERR_SECKLA_BASE -5),  /*Key not found*/
	NAPI_ERR_SECKLA_ERR_INVALIDKEY_USAGE  = (NAPI_ERR_SECKLA_BASE -6),  /*Invalid use of the key according to the key tag*/
	//NAPI algorithm
	NAPI_ERR_SECALG_BASE                  = (-1600),
	NAPI_ERR_SECALG_TIMEOUT               = (NAPI_ERR_SECALG_BASE - 1), /**<Get key value timeout*/
	NAPI_ERR_SECALG_PARAM                 = (NAPI_ERR_SECALG_BASE - 2), /**<Invalid parameter*/
	NAPI_ERR_SECALG_UPDATE                = (NAPI_ERR_SECALG_BASE - 3),               
	NAPI_ERR_SECALG_FINISH                = (NAPI_ERR_SECALG_BASE - 4),               
	NAPI_ERR_SECALG_ASYMCALC              = (NAPI_ERR_SECALG_BASE - 5),
	NAPI_ERR_SECALG_ECCCALC               = (NAPI_ERR_SECALG_BASE - 6),
	//
	NAPI_ERR_SEC_CFG_BASE                 = (-1700),
	NAPI_ERR_SEC_CFG_TABLE                = (NAPI_ERR_SEC_CFG_BASE - 1), /* indicate current key table, "" for app itself */
	NAPI_ERR_SEC_CFG_UNIQUE               = (NAPI_ERR_SEC_CFG_BASE - 2), /* check if installing key is unique : 0 - no check, 1 - check */
	NAPI_ERR_SEC_CFG_MISUSE               = (NAPI_ERR_SEC_CFG_BASE - 3), /* check if key is misused according to its type : 0 - no check, 1 - check */
	NAPI_ERR_SEC_CFG_TRIES_LIMIT          = (NAPI_ERR_SEC_CFG_BASE - 4), /* check if current function is overrun: 0 - no check, 1 - check */
	NAPI_ERR_SEC_CFG_STRENGTH             = (NAPI_ERR_SEC_CFG_BASE - 5), /* keys should be protected by the same or higher strength keys: 0 - no check, 1 - check */
	NAPI_ERR_SEC_CFG_KEYLEN_LIMIT         = (NAPI_ERR_SEC_CFG_BASE - 6), /* key length should be stronger than 8 bytes : 0 - no check, 1- check */
	NAPI_ERR_SEC_CFG_DPA_DEFENCE          = (NAPI_ERR_SEC_CFG_BASE - 7), /* DPA defence: 0 - disable, 1- enable */
	NAPI_ERR_SEC_CFG_CLEARKEY_LIMIT       = (NAPI_ERR_SEC_CFG_BASE - 8), /* check if the clearkey is allowed to be installed: 0 - disable, 1- enable */
	NAPI_ERR_SEC_CFG_VPP_STATIC_KEY_LAYOUT_LIMIT = (NAPI_ERR_SEC_CFG_BASE - 9), /* check if the clearkey is allowed to be installed: 0 - disable, 1- enable */
	NAPI_ERR_SEC_SP_CFG_ASYM_LOADKEY_LIMIT = (NAPI_ERR_SEC_CFG_BASE - 10),  /* Check if the symmetric keys is allowed to be installed by asymmetric keys : 0 - no check, 1 - check */

        //csr cert
        NAPI_ERR_SEC_CSR_BASE 				  = (-1800),
        NAPI_ERR_SEC_CSR_TIMEOUT 			  = (NAPI_ERR_SEC_CSR_BASE - 1), /**<Get key value timeout*/
        NAPI_ERR_SEC_CSR_PARAM 				  = (NAPI_ERR_SEC_CSR_BASE - 2), /**<Invalid parameter*/
        NAPI_ERR_SEC_CSR_DBUS 				  = (NAPI_ERR_SEC_CSR_BASE - 3), /**<DBUS communication error*/
        NAPI_ERR_SEC_CSR_MALLOC 			  = (NAPI_ERR_SEC_CSR_BASE - 4), /**<Out of memory*/
        NAPI_ERR_SEC_CSR_HANDLE 			  = (NAPI_ERR_SEC_CSR_BASE - 5), /**<CSR handle error*/
        NAPI_ERR_SEC_CSR_WRITE 		          = (NAPI_ERR_SEC_CSR_BASE - 6), /**<mbedtls library operation error*/
        NAPI_ERR_SEC_CSR_INPROCESS            = (NAPI_ERR_SEC_CSR_BASE - 7), /**< CSR handle have not released*/
    //RKI
    NAPI_ERR_SECRKI_BASE                            = (-1900),
    NAPI_ERR_SECRKI_TIMEOUT                         = (NAPI_ERR_SECRKI_BASE - 1),
    NAPI_ERR_SECRKI_PARAM                           = (NAPI_ERR_SECRKI_BASE - 2),
    NAPI_ERR_SECRKI_BACKUP                          = (NAPI_ERR_SECRKI_BASE - 3),
    NAPI_ERR_SECRKI_RESTORE                         = (NAPI_ERR_SECRKI_BASE - 4),
    NAPI_ERR_SECRKI_VERIFY                          = (NAPI_ERR_SECRKI_BASE - 5),

        NAPI_ERR_RFID_INITSTA            = -2005, /**<RF chip error or not configured*/
        NAPI_ERR_RFID_NOCARD             = -2008, /**<No card  0x0D*/
        NAPI_ERR_RFID_MULTICARD          = -2009, /**<Multi card detected*/
        NAPI_ERR_RFID_SEEKING            = -2010, /**<Failed to seek and activate card*/
        NAPI_ERR_RFID_PROTOCOL           = -2011, /**<Not compliant with ISO1444-4 protocol, e.g. M1 card F*/
        NAPI_ERR_RFID_NOPICCTYPE         = -2012, /**<Card not set 0x01*/
        NAPI_ERR_RFID_NOTDETE            = -2013, /**<Card not detected  0x02*/
        NAPI_ERR_RFID_AANTI              = -2014, /**<Type A card collision (Multiple cards exist) 0x03*/
        NAPI_ERR_RFID_RATS               = -2015, /**<Type A card RATS processing error 0x04*/
        NAPI_ERR_RFID_BACTIV             = -2016, /**<Failed to activate Type B card 0x07*/
        NAPI_ERR_RFID_ASEEK              = -2017, /**<Failed to seek type A card (Probably multiple cards exist) 0x0A*/
        NAPI_ERR_RFID_BSEEK              = -2018, /**<Failed to seek type B card (Probably multiple cards exist) 0x0B*/
        NAPI_ERR_RFID_ABON               = -2019, /**<Both type A and B cards exist 0x0C*/
        NAPI_ERR_RFID_UPED               = -2020, /**<Already activated 0x0E*/
        NAPI_ERR_RFID_NOTACTIV           = -2021, /**<Not activated*/
        NAPI_ERR_RFID_COLLISION_A        = -2022, /**<Type A Card collision*/
        NAPI_ERR_RFID_COLLISION_B        = -2023, /**<Type B Card collision*/

        NAPI_ERR_MI_NOTAGERR             = -2030, /**<No card,                        0xff*/
        NAPI_ERR_MI_CRCERR               = -2031, /**<CRC error,                      0xfe*/
        NAPI_ERR_MI_EMPTY                = -2032, /**<Not empty,                      0xfd*/
        NAPI_ERR_MI_AUTHERR              = -2033, /**<Failed to authenticate,         0xfc*/
        NAPI_ERR_MI_PARITYERR            = -2034, /**<Parity error,                   0xfb*/
        NAPI_ERR_MI_CODEERR              = -2035, /**<Receiving code error            0xfa*/
        NAPI_ERR_MI_SERNRERR             = -2036, /**<Anti-collision data check error 0xf8*/
        NAPI_ERR_MI_KEYERR               = -2037, /**<KEY authentication error        0xf7*/
        NAPI_ERR_MI_NOTAUTHERR           = -2038, /**<Not authenticated               0xf6*/
        NAPI_ERR_MI_BITCOUNTERR          = -2039, /**<Failed to receive BIT           0xf5*/
        NAPI_ERR_MI_BYTECOUNTERR         = -2040, /**<Failed to receive byte          0xf4*/
        NAPI_ERR_MI_WriteFifo            = -2041, /**<Failed to write FIFO            0xf3*/
        NAPI_ERR_MI_TRANSERR             = -2042, /**<Failed to send                  0xf2*/
        NAPI_ERR_MI_WRITEERR             = -2043, /**<Failed to error write           0xf1*/
        NAPI_ERR_MI_INCRERR              = -2044, /**<Failed to increment             0xf0*/
        NAPI_ERR_MI_DECRERR              = -2045, /**<Failed to decrement             0xef*/
        NAPI_ERR_MI_OVFLERR              = -2046, /**<Overflow                        0xed*/
        NAPI_ERR_MI_FRAMINGERR           = -2047, /**<Frame error                     0xeb*/
        NAPI_ERR_MI_COLLERR              = -2048, /**<Collision detected              0xe8*/
        NAPI_ERR_MI_INTERFACEERR         = -2049, /**<Fialed to reset interface       0xe6*/
        NAPI_ERR_MI_ACCESSTIMEOUT        = -2050, /**<Receive timeout                 0xe5*/
        NAPI_ERR_MI_PROTOCOLERR          = -2051, /**<Protocol error                  0xe4*/
        NAPI_ERR_MI_QUIT                 = -2052, /**<Abnormal abortion               0xe2*/
        NAPI_ERR_MI_PPSErr               = -2053, /**<PPS operation error             0xe1*/
        NAPI_ERR_MI_SpiRequest           = -2054, /**<Failed to request SPI           0xa0*/
        NAPI_ERR_MI_NY_IMPLEMENTED       = -2055, /**<Unknown error                   0x9c*/
        NAPI_ERR_MI_CardTypeErr          = -2056, /**<Wrong card type                 0x83*/
        NAPI_ERR_MI_ParaErrInIoctl       = -2057, /**<Wrong IOCTL parameter           0x82*/
        NAPI_ERR_MI_Para                 = -2059, /**<Invalid parameter               0xa9*/

        NAPI_ERR_WIFI_INVDATA            = -3001, /**<Invalid parameter*/
        NAPI_ERR_WIFI_DEVICE_FAULT       = -3002, /**<Invalid device state*/
        NAPI_ERR_WIFI_CMD_UNSUPPORTED    = -3003, /**<Command not supported*/
        NAPI_ERR_WIFI_DEVICE_UNAVAILABLE = -3004, /**<Device unavailable*/
        NAPI_ERR_WIFI_DEVICE_NOTOPEN     = -3005, /**<No AP scanned*/
        NAPI_ERR_WIFI_DEVICE_BUSY        = -3006, /**<Device busy*/
        NAPI_ERR_WIFI_UNKNOWN_ERROR      = -3007, /**<Unknown Error*/
        NAPI_ERR_WIFI_PROCESS_INBADSTATE = -3008, /**<Failed to connect*/
        NAPI_ERR_WIFI_SEARCH_FAULT       = -3009, /**<Invalied scanning state*/
        NAPI_ERR_WIFI_DEVICE_TIMEOUT     = -3010, /**<Device timeout*/
        NAPI_ERR_WIFI_NON_CONNECTED      = -3011, /**<Not connected*/

        NAPI_ERR_RFID_BUSY               = -3101, /**<Rf card busy*/
        NAPI_ERR_PRN_BUSY                = -3102, /**<Printer busy*/
        NAPI_ERR_ICCARD_BUSY             = -3103, /**<Samer card busy*/
        NAPI_ERR_MAG_BUSY                = -3104, /**<MagnetSmart card busy*/
        NAPI_ERR_USB_BUSY                = -3105, /**<USB module busy*/
        NAPI_ERR_WLM_BUSY                = -3106, /**<Wireless module busy*/
        NAPI_ERR_PIN_BUSY                = -3107, /**<PIN input*/
        NAPI_ERR_BT_BUSY                 = -3108, /**<Bluetooth module busy*/
        NAPI_ERR_DEV_BUSY                = -3109, /**<Device busy*/
        NAPI_ERR_BT_NOT_CONNECTED        = -3201, /**<Bluetooth not connected*/

        NAPI_ERR_LINUX_ERRNO_BASE        = -5000, /**<Error prefix from system function*/
        NAPI_ERR_LINUX_TCP_TIMEOUT       = (NAPI_ERR_LINUX_ERRNO_BASE - 110), /**<Wrong TCP remote port*/
        NAPI_ERR_LINUX_TCP_REFUSE        = (NAPI_ERR_LINUX_ERRNO_BASE - 111), /**<TCP remote port not allowed*/
        NAPI_ERR_LINUX_TCP_NOT_OPEN      = (NAPI_ERR_LINUX_ERRNO_BASE - 88),  /**<TCP not open*/
	    COM_FAIL						 = -6000, /**<Underlying driver communication error*/
} NAPI_ERR_CODE;


/** @} */ // end of SysInfo

/** @addtogroup SysInfo_Deprecated
* @{
*/
/**************************************** Deprecated ********************************************/

typedef enum {
	MODEL,       /**<Model*/
	SN,          /**<USN.*/
	OS_VERSION,  /**<OS version*/
	HW           /**<All hardware info*/
}SYS_INFO_ID;

extern int (*NAPI_SysGetInfo)(SYS_INFO_ID InfoID, char *OutBuf, int *OutBufLen);
/** @}*/ // End of ErrorCodes

extern int (*NAPI_SysBeepIt)(uint unFrequency,uint unMsSeconds);

extern int (*NAPI_SecGetDeviceStatus)(uint32_t *status);

extern int (*NAPI_SecSetDeviceStatus)(uint32_t status);

#ifdef __cplusplus
}
#endif

#endif

/* End of this file*/

