/*
* 新大陆公司 版权所有(c) 2011-2015
*
* 统一平台NDK API
* 作    者：	产品开发部
* 日    期：	2012-08-17
* 版	本：	V1.00
* 最后修改人：
* 最后修改日期：
*/
#include <stdio.h>
#include <time.h>
#include <sys/socket.h>
#ifndef __NDKAPI__H
#define __NDKAPI__H

/** @addtogroup 错误码定义
* @{
*/
typedef unsigned char uint8_t;
typedef unsigned char seq_nr_t;       /* Frame Seq type */
typedef unsigned char frame_type_t;   /* Frame type type */
typedef unsigned short uint16_t;
typedef void *	NDK_HANDLE;			///<SSL、Timer句柄
typedef unsigned int 	uint;
typedef unsigned char 	uchar;
typedef unsigned short 	ushort;
typedef unsigned long 	ulong;

typedef enum {
   ICC_EMV_MODE = 1,        //EMV2000, auto adjust baud rate, nonsupport PPS
   ICC_BPS_576_MODE,        //EMV2000, baud : 57600, nonsupport PPS
   ICC_BPS_384_MODE,        //EMV2000, baud : 38400, nonsupport PPS
   ICC_BPS_192_MODE,        //EMV2000, baud : 19200, nonsupport PPS
   ICC_SSC_MODE,            //Social Security Card, auto adjust baud rate, nonsupport PPS
   ICC_ISO7816_PPS_MODE,    //ISO7816, baud : 9600, support PPS,
   ICC_ISO7816_BPS_96_MODE, //ISO7816, baud : 9600, nonsupport PPS
   ICC_ISO7816_DEFAULT_MODE //ISO7816, auto adjust baud rate, nonsupport PPS
}POWERUP_STANDARD;


typedef enum{
   ICC_VOL_OFF = 0,
   ICC_VOL_3V,
   ICC_VOL_5V,
   ICC_VOL_1P8V
}POWERUP_VOL;


typedef enum {
	NDK_OK,							/**<操作成功*/
	NDK_ERR=-1,						/**<操作失败*/
	NDK_ERR_INIT_CONFIG = -2,	 	/**<初始化配置失败*/
	NDK_ERR_CREAT_WIDGET = -3,		/**<创建界面错误*/
	NDK_ERR_OPEN_DEV = -4,			/**<打开设备文件错误*/
	NDK_ERR_IOCTL = -5,				/**<驱动调用错误*/
	NDK_ERR_PARA = -6,				/**<参数非法*/
	NDK_ERR_PATH = -7,				/**<文件路径非法*/
	NDK_ERR_DECODE_IMAGE = -8,		/**<图像解码失败*/
	NDK_ERR_MACLLOC = -9,			/**<内存空间不足*/
	NDK_ERR_TIMEOUT = -10,			/**<超时错误*/
	NDK_ERR_QUIT = -11,				/**<按取消退出*/
	NDK_ERR_WRITE = -12, 			/**<写文件失败*/
	NDK_ERR_READ = -13, 			/**<读文件失败*/
	NDK_ERR_OVERFLOW = -15,			/**<缓冲溢出*/
	NDK_ERR_SHM = -16,				/**<共享内存出错*/
	NDK_ERR_NO_DEVICES=-17,			/**<POS无该设备*/
	NDK_ERR_NOT_SUPPORT=-18, 		/**<不支持该功能*/
	NDK_ERR_NO_SNK = -19,           /**<不存在SNK*/ //add by yanb,20170707 for UNIONPAY REQUIREMENT
	NDK_ERR_NO_TUSN = -20,          /**<不存在TUSN*/ //add by yanb,20170707 for UNIONPAY REQUIREMENT


	NDK_ERR_NOSWIPED = -50,			/**<无磁卡刷卡记录*/
	NDK_ERR_SWIPED_DATA=-51,		/**<驱动磁卡数据格式错*/
	NDK_ERR_USB_LINE_UNCONNECT = -100,	/**<USB线未连接*/
	NDK_ERR_NO_SIMCARD = -201,		/**<无SIM卡*/
	NDK_ERR_PIN = -202, 			/**<SIM卡密码错误*/
	NDK_ERR_PIN_LOCKED = -203,		/**<SIM卡被锁定*/
	NDK_ERR_PIN_UNDEFINE = -204,	/**<SIM卡未定义错误*/
	NDK_ERR_EMPTY = -205,			/**<返回空串*/
	NDK_ERR_ETH_PULLOUT = -250,		/**<以太网未插线*/
	NDK_ERR_PPP_PARAM = -301,		/**<PPP参数出错*/
	NDK_ERR_PPP_DEVICE = -302,		/**<PPP无效设备*/
	NDK_ERR_PPP_OPEN = -303, 		/**<PPP已打开*/
	NDK_ERR_TCP_ALLOC = -304,		/**<无法分配*/
	NDK_ERR_TCP_PARAM = -305,		/**<无效参数*/
	NDK_ERR_TCP_TIMEOUT = -306,		/**<传输超时*/
	NDK_ERR_TCP_INVADDR = -307,		/**<无效地址*/
	NDK_ERR_TCP_CONNECT = -308,		/**<没有连接*/
	NDK_ERR_TCP_PROTOCOL = -309,	/**<协议错误*/
	NDK_ERR_TCP_NETWORK = -310,		/**<网络错误*/
	NDK_ERR_TCP_SEND = -311,		/**<发送错误*/
	NDK_ERR_TCP_RECV = -312,		/**<接收错误*/
	NDK_ERR_WLM_SEND_AT_FAIL = -320,		/**<无线发送AT失败*/

	NDK_ERR_SSL_PARAM = -350,			/**<无效参数*/
	NDK_ERR_SSL_ALREADCLOSE = -351,		/**<连接已关闭*/
	NDK_ERR_SSL_ALLOC = -352,			/**<无法分配*/
	NDK_ERR_SSL_INVADDR = -353,			/**<无效地址*/
	NDK_ERR_SSL_TIMEOUT = -354,			/**<连接超时*/
	NDK_ERR_SSL_MODEUNSUPPORTED = -355,	/**<模式不支持*/
	NDK_ERR_SSL_SEND = -356,			/**<发送错误*/
	NDK_ERR_SSL_RECV = -357,			/**<接收错误*/
	NDK_ERR_SSL_CONNECT = -358,			/**<没有连接*/

	NDK_ERR_NET_GETADDR = -401,			/**<获取本地地址或子网掩码失败*/
	NDK_ERR_NET_GATEWAY = -402,			/**<获取网关地址失败*/
	NDK_ERR_NET_ADDRILLEGAL =-403,		/**<获取地址格式错误*/	
	NDK_ERR_NET_UNKNOWN_COMMTYPE=-404,	/**<未知的通信类型*/	
	NDK_ERR_NET_INVALIDIPSTR=-405,		/**<无效的IP字符串*/
	NDK_ERR_NET_UNSUPPORT_COMMTYPE=-406,	/**<不支持的通信类型*/

	NDK_ERR_THREAD_PARAM = -450,		/**<无效参数*/
	NDK_ERR_THREAD_ALLOC = -451,		/**<无效分配*/
	NDK_ERR_THREAD_CMDUNSUPPORTED = -452,	/**<命令不支持*/
	
	NDK_ERR_MODEM_RESETFAIL = -501,			/**<MODEM 复位失败*/
	NDK_ERR_MODEM_GETSTATUSFAIL = -502,		/**<MODEM 获取状态失败*/
	NDK_ERR_MODEM_SLEPPFAIL = -503,			/**<MODEM 休眠失败*/
	NDK_ERR_MODEM_SDLCINITFAIL = -504,		/**<MODEM 同步初始化失败*/
	NDK_ERR_MODEM_INIT_NOT=-505,			/**<MODEM 未进行初始化*/
	NDK_ERR_MODEM_SDLCWRITEFAIL=-506,		/**<MODEM 同步写失败*/
	NDK_ERR_MODEM_ASYNWRITEFAIL = -507,		/**<MODEM 异步写数据失败*/	
	NDK_ERR_MODEM_ASYNDIALFAIL = -508,		/**<MODEM 异步拨号失败*/
	NDK_ERR_MODEM_ASYNINITFAIL = -509,		/**<MODEM 异步初始化失败*/
	NDK_ERR_MODEM_SDLCHANGUPFAIL=-510,		/**<MODEM 同步挂断失败*/
	NDK_ERR_MODEM_ASYNHANGUPFAIL=-511,		/**<MODEM 异步挂断失败*/
	NDK_ERR_MODEM_SDLCCLRBUFFAIL=-512,		/**<MODEM 同步清缓冲失败*/
	NDK_ERR_MODEM_ASYNCLRBUFFAIL=-513,		/**<MODEM 异步清缓冲失败*/
	NDK_ERR_MODEM_ATCOMNORESPONSE=-514,		/**<MODEM AT命令无响应*/
	NDK_ERR_MODEM_PORTWRITEFAIL=-515,		/**<MODEM 端口写数据失败*/
	NDK_ERR_MODEM_SETCHIPFAIL=-516,			/**<MODEM 模块寄存器设置失败*/
	NDK_ERR_MODEM_STARTSDLCTASK=-517,		/**<MODEM 拨号时开启SDLC 任务失败*/
	NDK_ERR_MODEM_GETBUFFLENFAIL = -518,	/**<MODEM 获取数据长度失败*/
	NDK_ERR_MODEM_QUIT=-519,				/**<MODEM 手动退出*/
	NDK_ERR_MODEM_NOPREDIAL=-520,			/**<MODEM 未拨号*/
	NDK_ERR_MODEM_NOCARRIER=-521,			/**<MODEM 没载波*/
	NDK_ERR_MODEM_NOLINE=-523,				/**<MODEM 未插线*/
	NDK_ERR_MODEM_OTHERMACHINE=-524,		/**<MODEM 存在并机*/
	NDK_ERR_MODEM_PORTREADFAIL=-525,		/**<MODEM 端口读数据失败*/
	NDK_ERR_MODEM_CLRBUFFAIL=-526,			/**<MODEM 清空缓冲失败*/
	NDK_ERR_MODEM_ATCOMMANDERR=-527,		/**<MODEM AT命令错误*/
	NDK_ERR_MODEM_STATUSUNDEFINE=-528,		/**<MODEM 状态未确认状态*/
	NDK_ERR_MODEM_GETVERFAIL=-529,			/**<MODEM获取版本失败*/
	NDK_ERR_MODEM_SDLCDIALFAIL = -530,		/**<MODEM 同步拨号失败*/
	NDK_ERR_MODEM_SELFADAPTFAIL = -530,     /**<MODEM自适应失败*/

	NDK_ERR_ICC_WRITE_ERR =			-601,	/**<写器件83c26出错*/
	NDK_ERR_ICC_COPYERR=			-602,	/**<内核数据拷贝出错*/
	NDK_ERR_ICC_POWERON_ERR=		-603,	/**<上电出错*/
	NDK_ERR_ICC_COM_ERR=			-604,	/**<命令出错*/
	NDK_ERR_ICC_CARDPULL_ERR=		-605,	/**<卡拔出了*/
	NDK_ERR_ICC_CARDNOREADY_ERR=	-606,	/**<卡未准备好*/

	NDK_ERR_USDDISK_PARAM =  -650,			/**<无效参数*/
	NDK_ERR_USDDISK_DRIVELOADFAIL =  -651,	/**<U盘或SD卡驱动加载失败*/
	NDK_ERR_USDDISK_NONSUPPORTTYPE =  -652,	/**<不支持的类型*/
	NDK_ERR_USDDISK_UNMOUNTFAIL =  -653,	/**<挂载失败*/
	NDK_ERR_USDDISK_UNLOADDRIFAIL =  -654,	/**<卸载驱动失败*/
	NDK_ERR_USDDISK_IOCFAIL =  -655,		/**<驱动调用错误*/

	NDK_ERR_APP_BASE=(-800),						/**<应用接口错误基数*/
	NDK_ERR_APP_NOT_EXIST=(NDK_ERR_APP_BASE-1),		/**<应用项不存在*/
	NDK_ERR_APP_NOT_MATCH=(NDK_ERR_APP_BASE-2),		/**<补丁包文件不匹配*/
	NDK_ERR_APP_FAIL_SEC=(NDK_ERR_APP_BASE-3),		/**<获取安全攻击状态失败*/
	NDK_ERR_APP_SEC_ATT=(NDK_ERR_APP_BASE-4),		/**<存在安全攻击*/
	NDK_ERR_APP_FILE_EXIST=(NDK_ERR_APP_BASE-5),	/**<应用中该文件已存在*/
	NDK_ERR_APP_FILE_NOT_EXIST=(NDK_ERR_APP_BASE-6),/**<应用中该文件不存在*/
	NDK_ERR_APP_FAIL_AUTH=(NDK_ERR_APP_BASE-7),		/**<证书认证失败*/
	NDK_ERR_APP_LOW_VERSION=(NDK_ERR_APP_BASE-8),	/**<补丁包的版本比应用版本低*/

	NDK_ERR_APP_MAX_CHILD=(NDK_ERR_APP_BASE-9),			/**<子应用运行数超过最大运行数目*/
	NDK_ERR_APP_CREAT_CHILD=(NDK_ERR_APP_BASE-10),		/**<创建子进程错误*/
	NDK_ERR_APP_WAIT_CHILD=(NDK_ERR_APP_BASE-11),		/**<等待子进程结束错误*/
	NDK_ERR_APP_FILE_READ=(NDK_ERR_APP_BASE-12),		/**<读文件错误*/
	NDK_ERR_APP_FILE_WRITE=(NDK_ERR_APP_BASE-13),		/**<写文件错误*/
	NDK_ERR_APP_FILE_STAT=(NDK_ERR_APP_BASE-14),		/**<获取文件信息错误*/
	NDK_ERR_APP_FILE_OPEN=(NDK_ERR_APP_BASE-15),		/**<文件打开错误*/
	NDK_ERR_APP_NLD_HEAD_LEN=(NDK_ERR_APP_BASE-16),		/**<NLD文件获取头信息长度错误*/
	NDK_ERR_APP_PUBKEY_EXPIRED=(NDK_ERR_APP_BASE-17),	/**<公钥有效期*/
	NDK_ERR_APP_MMAP=(NDK_ERR_APP_BASE-18),				/**<内存映射错误*/
	NDK_ERR_APP_MALLOC=(NDK_ERR_APP_BASE-19),			/**<动态内存分配错误*/
	NDK_ERR_APP_SIGN_DECRYPT=(NDK_ERR_APP_BASE-20),		/**<签名数据解签错误*/
	NDK_ERR_APP_SIGN_CHECK=(NDK_ERR_APP_BASE-21),		/**<签名数据校验错误*/
	NDK_ERR_APP_MUNMAP=(NDK_ERR_APP_BASE-22),			/**<内存映射释放错误*/
	NDK_ERR_APP_TAR=(NDK_ERR_APP_BASE-23),				/**<tar命令执行失败*/
	NDK_ERR_APP_KEY_UPDATE_BAN=(NDK_ERR_APP_BASE-24),				/**<调试状态禁止密钥升级*/
	NDK_ERR_APP_FIRM_PATCH_VERSION=(NDK_ERR_APP_BASE-25),			/**固件补丁增量包版本不匹配*/
	NDK_ERR_APP_CERT_HAS_EXPIRED=(NDK_ERR_APP_BASE-26),				/**证书已经失效*/
	NDK_ERR_APP_CERT_NOT_YET_VALID=(NDK_ERR_APP_BASE-27),			/**证书尚未生效*/
	NDK_ERR_APP_FILE_NAME_TOO_LONG=(NDK_ERR_APP_BASE-28),		 	/**文件名长度大于32字节*/
	NAPI_ERR_APP_CA_ALREADY_CUSTOMIZED=(NAPI_ERR_APP_BASE - 29), /**<Application CA has been customized*/
    NAPI_ERR_APP_FILE_CHK=(NAPI_ERR_APP_BASE - 30), /**<<File check error*/

	NDK_ERR_SECP_BASE = (-1000),								/**<未知错误*/
	NDK_ERR_SECP_TIMEOUT = (NDK_ERR_SECP_BASE - 1),				/**<获取键值超时*/
	NDK_ERR_SECP_PARAM = (NDK_ERR_SECP_BASE - 2),				/**<输入参数非法*/
	NDK_ERR_SECP_DBUS = (NDK_ERR_SECP_BASE - 3),				/**<DBUS通讯错误*/
	NDK_ERR_SECP_MALLOC = (NDK_ERR_SECP_BASE - 4),				/**<动态内存分配错误*/
	NDK_ERR_SECP_OPEN_SEC = (NDK_ERR_SECP_BASE - 5),			/**<打开安全设备错误*/
	NDK_ERR_SECP_SEC_DRV = (NDK_ERR_SECP_BASE - 6),				/**<安全设备操作错误*/
	NDK_ERR_SECP_GET_RNG = (NDK_ERR_SECP_BASE - 7),				/**<获取随机数*/
	NDK_ERR_SECP_GET_KEY = (NDK_ERR_SECP_BASE - 8),				/**<获取密钥值*/
	NDK_ERR_SECP_KCV_CHK = (NDK_ERR_SECP_BASE - 9),				/**<KCV校验错误*/
	NDK_ERR_SECP_GET_CALLER = (NDK_ERR_SECP_BASE - 10),			/**<获取调用者信息错误*/
	NDK_ERR_SECP_OVERRUN = (NDK_ERR_SECP_BASE - 11),			/**<运行次数出错*/
	NDK_ERR_SECP_NO_PERMIT = (NDK_ERR_SECP_BASE - 12),			/**<权限不允许*/
	NDK_ERR_SECP_TAMPER = (NDK_ERR_SECP_BASE - 13),				/**<安全攻击*/

	NDK_ERR_SECVP_BASE = (-1100),							/**<未知错误*/
	NDK_ERR_SECVP_TIMEOUT = (NDK_ERR_SECVP_BASE - 1),		/**<获取键值超时*/
	NDK_ERR_SECVP_PARAM = (NDK_ERR_SECVP_BASE - 2),			/**<输入参数非法*/
	NDK_ERR_SECVP_DBUS = (NDK_ERR_SECVP_BASE - 3),			/**<DBUS通讯错误*/
	NDK_ERR_SECVP_OPEN_EVENT0 =	(NDK_ERR_SECVP_BASE - 4),	/**<打开event0设备出错*/
	NDK_ERR_SECVP_SCAN_VAL = (NDK_ERR_SECVP_BASE - 5),		/**<扫描值超出定义*/
	NDK_ERR_SECVP_OPEN_RNG = (NDK_ERR_SECVP_BASE - 6),		/**<打开随机数设备错误*/
	NDK_ERR_SECVP_GET_RNG = (NDK_ERR_SECVP_BASE - 7),		/**<获取随机数出错*/
	NDK_ERR_SECVP_GET_ESC = (NDK_ERR_SECVP_BASE - 8),		/**<用户取消键退出*/
	NDK_ERR_SECVP_VPP = (-1120),							/**<未知错误*/
	NDK_ERR_SECVP_INVALID_KEY=(NDK_ERR_SECVP_VPP),  		/**<无效密钥,内部使用.*/
	NDK_ERR_SECVP_NOT_ACTIVE=(NDK_ERR_SECVP_VPP-1),  		/**<VPP没有激活，第一次调用VPPInit.*/
	NDK_ERR_SECVP_TIMED_OUT=(NDK_ERR_SECVP_VPP-2),			/**<已经超过VPP初始化的时间.*/
	NDK_ERR_SECVP_ENCRYPT_ERROR=(NDK_ERR_SECVP_VPP-3),		/**<按确认键后，加密错误.*/
	NDK_ERR_SECVP_BUFFER_FULL=(NDK_ERR_SECVP_VPP-4),		/**<输入BUF越界，（键入的PIN太长）*/
	NDK_ERR_SECVP_PIN_KEY=(NDK_ERR_SECVP_VPP-5),  			/**<数据键按下，回显"*".*/
	NDK_ERR_SECVP_ENTER_KEY=(NDK_ERR_SECVP_VPP-6),			/**<确认键按下，PIN处理.*/
	NDK_ERR_SECVP_BACKSPACE_KEY=(NDK_ERR_SECVP_VPP-7),		/**<退格键按下.*/
	NDK_ERR_SECVP_CLEAR_KEY=(NDK_ERR_SECVP_VPP-8),			/**<清除键按下，清除所有'*'显示.*/
	NDK_ERR_SECVP_CANCEL_KEY=(NDK_ERR_SECVP_VPP-9),			/**<取消键被按下.*/
	NDK_ERR_SECVP_GENERALERROR=(NDK_ERR_SECVP_VPP-10),		/**<该进程无法继续。内部错误.*/
	NDK_ERR_SECVP_CUSTOMERCARDNOTPRESENT=(NDK_ERR_SECVP_VPP-11),	/**<IC卡被拔出*/
	NDK_ERR_SECVP_HTCCARDERROR=(NDK_ERR_SECVP_VPP-12),		/**<访问智能卡错误.*/
	NDK_ERR_SECVP_WRONG_PIN_LAST_TRY=(NDK_ERR_SECVP_VPP-13),/**<智能卡-密码不正确，重试一次.*/
	NDK_ERR_SECVP_WRONG_PIN=(NDK_ERR_SECVP_VPP-14),			/**<智能卡-最后尝试一次.*/
	NDK_ERR_SECVP_ICCERROR=(NDK_ERR_SECVP_VPP-15),			/**<智能卡-重试太多次*/
	NDK_ERR_SECVP_PIN_BYPASS=(NDK_ERR_SECVP_VPP-16),		/**<智能卡-PIN验证通过,并且PIN是0长度*/
	NDK_ERR_SECVP_ICCFAILURE=(NDK_ERR_SECVP_VPP-17),		/**<智能卡-致命错误.*/
	NDK_ERR_SECVP_GETCHALLENGE_BAD=(NDK_ERR_SECVP_VPP-18),	/**<智能卡-应答不是90 00.*/
	NDK_ERR_SECVP_GETCHALLENGE_NOT8=(NDK_ERR_SECVP_VPP-19),	/**<智能卡-无效的应答长度.*/
 	NDK_ERR_SECVP_PIN_ATTACK_TIMER=(NDK_ERR_SECVP_VPP-20),	/**<PIN攻击定时器被激活*/

	NDK_ERR_SECCR_BASE = (-1200),							/**<未知错误*/
	NDK_ERR_SECCR_TIMEOUT = (NDK_ERR_SECCR_BASE - 1),		/**<获取键值超时*/
	NDK_ERR_SECCR_PARAM = (NDK_ERR_SECCR_BASE - 2),			/**<输入参数非法*/
	NDK_ERR_SECCR_DBUS = (NDK_ERR_SECCR_BASE - 3),			/**<DBUS通讯错误*/
	NDK_ERR_SECCR_MALLOC = (NDK_ERR_SECCR_BASE - 4),		/**<动态内存分配错误*/
	NDK_ERR_SECCR_OPEN_RNG = (NDK_ERR_SECCR_BASE - 5),		/**<打开随机数设备错误*/
	NDK_ERR_SECCR_DRV = (NDK_ERR_SECCR_BASE - 6),			/**<驱动加密错误*/
	NDK_ERR_SECCR_KEY_TYPE = (NDK_ERR_SECCR_BASE - 7),		/**<密钥类型错误*/
	NDK_ERR_SECCR_KEY_LEN = (NDK_ERR_SECCR_BASE - 8),		/**<密钥长度错误*/
	NDK_ERR_SECCR_GET_KEY = (NDK_ERR_SECCR_BASE - 9),		/**<获取密钥错误*/

	NDK_ERR_SECKM_BASE = (-1300),							/**<未知错误*/
	NDK_ERR_SECKM_TIMEOUT = (NDK_ERR_SECKM_BASE - 1),		/**<获取键值超时*/
	NDK_ERR_SECKM_PARAM = (NDK_ERR_SECKM_BASE - 2),			/**<输入参数非法*/
	NDK_ERR_SECKM_DBUS = (NDK_ERR_SECKM_BASE - 3),			/**<DBUS通讯错误*/
	NDK_ERR_SECKM_MALLOC = (NDK_ERR_SECKM_BASE - 4),		/**<动态内存分配错误*/
	NDK_ERR_SECKM_OPEN_DB = (NDK_ERR_SECKM_BASE - 5),		/**<数据库打开错误*/
	NDK_ERR_SECKM_DEL_DB = (NDK_ERR_SECKM_BASE - 6),		/**<删除数据库错误*/
	NDK_ERR_SECKM_DEL_REC = (NDK_ERR_SECKM_BASE - 7),		/**<删除记录错误*/
	NDK_ERR_SECKM_INSTALL_REC = (NDK_ERR_SECKM_BASE - 8),	/**<安装密钥记录错误*/
	NDK_ERR_SECKM_READ_REC = (NDK_ERR_SECKM_BASE - 9),		/**<读密钥记录错误*/
	NDK_ERR_SECKM_OPT_NOALLOW = (NDK_ERR_SECKM_BASE - 10),	/**<操作不允许*/
	NDK_ERR_SECKM_KEY_MAC = (NDK_ERR_SECKM_BASE - 11),		/**<密钥MAC校验错误*/
	NDK_ERR_SECKM_KEY_TYPE = (NDK_ERR_SECKM_BASE - 12),		/**<密钥类型错误*/
	NDK_ERR_SECKM_KEY_ARCH = (NDK_ERR_SECKM_BASE - 13),		/**<密钥体系错误*/
	NDK_ERR_SECKM_KEY_LEN  = (NDK_ERR_SECKM_BASE - 14),		/**<密钥长度错误*/
	NAPI_ERR_SECKM_SYS = (NAPI_ERR_SECKM_BASE - 15),				/**<系统未知错误*/
    NAPI_ERR_SECKM_UNSUPPORT = (NAPI_ERR_SECKM_BASE - 16),        /**<不支持该功能*/
    NAPI_ERR_SECKM_KEY_ALREADY_USED = (NAPI_ERR_SECKM_BASE - 17), /**<密钥已用过*/
    NAPI_ERR_SECKM_CALCKCV = (NAPI_ERR_SECKM_BASE - 18),        /**<计算KCV错误*/
    NAPI_ERR_SECKM_DEL_TABLE = (NAPI_ERR_SECKM_BASE - 19),        /**<删除数据库表错误*/
	NAPI_ERR_SECKM_SIZE_ERROR = (NAPI_ERR_SECKM_BASE - 20), 	    /**<buff长度不足*/
	NAPI_ERR_SECKM_OPT_ERROR = (NAPI_ERR_SECKM_BASE - 21), 		/**<数据库操作失败*/
	NAPI_ERR_SECKM_TABLE_ERROR = (NAPI_ERR_SECKM_BASE - 22), 		/**<数据库表不存在*/
	NAPI_ERR_SECKM_DB_NULL = (NAPI_ERR_SECKM_BASE - 23), 	    	/**<数据库为空*/
	NAPI_ERR_SECKM_NOT_SUPPORT = (NAPI_ERR_SECKM_BASE - 24), 	    /**<没有操作权限*/
	
	NDK_ERR_SECKLA_BASE = (-1500),                                    /**<未知错误*/
	NDK_ERR_SECKLA_ERR_INTERNAL = (NDK_ERR_SECKLA_BASE -1),           /*Unspecified internal error.*/
	NDK_ERR_SECKLA_INVALIDPARAM = (NDK_ERR_SECKLA_BASE -2),           /*Invalid parameter passed to function.*/
	NDK_ERR_SECKLA_ERR_INVALID_CRT = (NDK_ERR_SECKLA_BASE -3),        /*Invalid certification*/
	NDK_ERR_SECKLA_ERR_INVALID_SIG = (NDK_ERR_SECKLA_BASE -4),        /*Invalid nonce signature*/
	NDK_ERR_SECKLA_ERR_KEY_NOT_FOUND = (NDK_ERR_SECKLA_BASE -5),      /*Key not found*/
	NDK_ERR_SECKLA_ERR_INVALIDKEY_USAGE = (NDK_ERR_SECKLA_BASE -6),   /*Invalid use of the key according to the key tag*/
	
    NAPI_ERR_SECALG_BASE = (-1600),
    NAPI_ERR_SECALG_TIMEOUT = (NAPI_ERR_SECALG_BASE - 1),               /**<获取键值超时*/
    NAPI_ERR_SECALG_PARAM = (NAPI_ERR_SECALG_BASE - 2),               /**<输入参数非法*/

	NAPI_ERR_SEC_CFG_BASE = (-1700),
	NAPI_ERR_SEC_CFG_TABLE = (NAPI_ERR_SEC_CFG_BASE - 1),             /* indicate current key table, "" for app itself */
	NAPI_ERR_SEC_CFG_UNIQUE = (NAPI_ERR_SEC_CFG_BASE - 2),                /* check if installing key is unique : 0 - no check, 1 - check */
	NAPI_ERR_SEC_CFG_MISUSE = (NAPI_ERR_SEC_CFG_BASE - 3),                /* check if key is misused according to its type : 0 - no check, 1 - check */
	NAPI_ERR_SEC_CFG_TRIES_LIMIT = (NAPI_ERR_SEC_CFG_BASE - 4),           /* check if current function is overrun: 0 - no check, 1 - check */
	NAPI_ERR_SEC_CFG_STRENGTH = (NAPI_ERR_SEC_CFG_BASE - 5),              /* keys should be protected by the same or higher strength keys: 0 - no check, 1 - check */
	NAPI_ERR_SEC_CFG_KEYLEN_LIMIT = (NAPI_ERR_SEC_CFG_BASE - 6),          /* key length should be stronger than 8 bytes : 0 - no check, 1- check */
	NAPI_ERR_SEC_CFG_DPA_DEFENCE = (NAPI_ERR_SEC_CFG_BASE - 7),          /* DPA defence: 0 - disable, 1- enable */
	NAPI_ERR_SEC_CFG_CLEARKEY_LIMIT = (NAPI_ERR_SEC_CFG_BASE - 8),       /* check if the clearkey is allowed to be installed: 0 - disable, 1- enable */

	NDK_ERR_RFID_INITSTA=			-2005,  /**<非接触卡-射频接口器件故障或者未配置*/
	NDK_ERR_RFID_NOCARD=			-2008,  /**<非接触卡-无卡  0x0D*/
	NDK_ERR_RFID_MULTICARD=			-2009,  /**<非接触卡-多卡状态*/
	NDK_ERR_RFID_SEEKING=			-2010,  /**<非接触卡-寻卡/激活过程中失败*/
	NDK_ERR_RFID_PROTOCOL=			-2011,  /**<非接触卡-不支持ISO1444-4协议，如M1卡  F*/

	NDK_ERR_RFID_NOPICCTYPE=		-2012,	/**<非接触卡-未设置卡 0x01*/
	NDK_ERR_RFID_NOTDETE=			-2013,	/**<非接触卡-未寻卡   0x02*/
	NDK_ERR_RFID_AANTI=				-2014,	/**<非接触卡-A卡冲突(多张卡存在)  0x03*/
	NDK_ERR_RFID_RATS=				-2015,	/**<非接触卡-A卡RATS过程出错   0x04*/
	NDK_ERR_RFID_BACTIV=			-2016,	/**<非接触卡-B卡激活失败   0x07*/
	NDK_ERR_RFID_ASEEK=				-2017,	/**<非接触卡-A卡寻卡失败(可能多张卡存在)   0x0A*/
	NDK_ERR_RFID_BSEEK=				-2018,	/**<非接触卡-B卡寻卡失败(可能多张卡存在)   0x0B*/
	NDK_ERR_RFID_ABON=				-2019,	/**<非接触卡-A、B卡同时存在   0x0C*/
	NDK_ERR_RFID_UPED=				-2020,	/**<非接触卡-已经激活(上电)   0x0E*/
	NDK_ERR_RFID_NOTACTIV=			-2021,	/**<非接触卡-未激活*/
	NDK_ERR_RFID_COLLISION_A=       -2022,	/**<非接触卡-A卡冲突*/
	NDK_ERR_RFID_COLLISION_B=       -2023,	/**<非接触卡-B卡冲突*/

	NDK_ERR_MI_NOTAGERR=			-2030,	/**<非接触卡-无卡,				0xff*/
	NDK_ERR_MI_CRCERR=				-2031,	/**<非接触卡-CRC错,				0xfe*/
	NDK_ERR_MI_EMPTY=				-2032,	/**<非接触卡-非空,				0xfd*/
	NDK_ERR_MI_AUTHERR=				-2033,	/**<非接触卡-认证错,			0xfc*/
	NDK_ERR_MI_PARITYERR=			-2034,	/**<非接触卡-奇偶错,			0xfb*/
	NDK_ERR_MI_CODEERR=				-2035,	/**<非接触卡-接收代码错			0xfa*/
	NDK_ERR_MI_SERNRERR=			-2036,	/**<非接触卡-防冲突数据校验错	0xf8*/
	NDK_ERR_MI_KEYERR=				-2037,	/**<非接触卡-认证KEY错			0xf7*/
	NDK_ERR_MI_NOTAUTHERR=			-2038,	/**<非接触卡-未认证				0xf6*/
	NDK_ERR_MI_BITCOUNTERR=			-2039,	/**<非接触卡-接收BIT错			0xf5*/
	NDK_ERR_MI_BYTECOUNTERR=		-2040,	/**<非接触卡-接收字节错			0xf4*/
	NDK_ERR_MI_WriteFifo=			-2041,	/**<非接触卡-FIFO写错误			0xf3*/
	NDK_ERR_MI_TRANSERR=			-2042,	/**<非接触卡-传送操作错误		0xf2*/
	NDK_ERR_MI_WRITEERR=			-2043,	/**<非接触卡-写操作错误			0xf1*/
	NDK_ERR_MI_INCRERR=				-2044,	/**<非接触卡-增量操作错误		0xf0*/
	NDK_ERR_MI_DECRERR=				-2045,	/**<非接触卡-减量操作错误		0xef*/
	NDK_ERR_MI_OVFLERR=				-2046,	/**<非接触卡-溢出错误			0xed*/
	NDK_ERR_MI_FRAMINGERR=			-2047,	/**<非接触卡-帧错				0xeb*/
	NDK_ERR_MI_COLLERR=				-2048,	/**<非接触卡-冲突				0xe8*/
	NDK_ERR_MI_INTERFACEERR=		-2049,	/**<非接触卡-复位接口读写错		0xe6*/
	NDK_ERR_MI_ACCESSTIMEOUT=		-2050,	/**<非接触卡-接收超时			0xe5*/
	NDK_ERR_MI_PROTOCOLERR=			-2051,	/**<非接触卡-协议错				0xe4*/
	NDK_ERR_MI_QUIT=				-2052,	/**<非接触卡-异常终止			0xe2*/
	NDK_ERR_MI_PPSErr=				-2053,	/**<非接触卡-PPS操作错			0xe1*/
	NDK_ERR_MI_SpiRequest=			-2054,	/**<非接触卡-申请SPI失败		0xa0*/
	NDK_ERR_MI_NY_IMPLEMENTED=		-2055,	/**<非接触卡-无法确认的错误状态	0x9c*/
	NDK_ERR_MI_CardTypeErr=			-2056,	/**<非接触卡-卡类型错			0x83*/
	NDK_ERR_MI_ParaErrInIoctl=		-2057,	/**<非接触卡-IOCTL参数错		0x82*/
	NDK_ERR_MI_Para=				-2059,	/**<非接触卡-内部参数错			0xa9*/

	NDK_ERR_WIFI_INVDATA=			-3001,	/**<WIFI-无效参数*/
	NDK_ERR_WIFI_DEVICE_FAULT=		-3002,	/**<WIFI-设备状态出错*/
	NDK_ERR_WIFI_CMD_UNSUPPORTED=	-3003,	/**<WIFI-不支持的命令*/
	NDK_ERR_WIFI_DEVICE_UNAVAILABLE=-3004,	/**<WIFI-设备不可用*/
	NDK_ERR_WIFI_DEVICE_NOTOPEN=	-3005,	/**<WIFI-没有扫描到AP*/
	NDK_ERR_WIFI_DEVICE_BUSY=		-3006,	/**<WIFI-设备忙*/
	NDK_ERR_WIFI_UNKNOWN_ERROR=		-3007,	/**<WIFI-未知错误*/
	NDK_ERR_WIFI_PROCESS_INBADSTATE=-3008,	/**<WIFI-无法连接到AP*/
	NDK_ERR_WIFI_SEARCH_FAULT=		-3009,	/**<WIFI-扫描状态出错*/
	NDK_ERR_WIFI_DEVICE_TIMEOUT=	-3010,	/**<WIFI-设备超时*/
	NAPI_ERR_WIFI_NON_CONNECTED=	-3011,  /**<WIFI-非连接状态*/
	
	NDK_ERR_RFID_BUSY = -3101,				/**<射频卡状态忙*/
	NDK_ERR_PRN_BUSY = -3102,				/**<打印状态忙*/
	NDK_ERR_ICCARD_BUSY = -3103,			/**<IC卡状态忙*/
	NDK_ERR_MAG_BUSY = -3104,				/**<磁卡状态忙*/
	NDK_ERR_USB_BUSY = -3105,				/**<USB状态忙*/
	NDK_ERR_WLM_BUSY = -3106,				/**<无线状态忙*/
	NDK_ERR_PIN_BUSY = -3107,						/*正处于PIN输入状态*/
	NDK_ERR_BT_BUSY  = -3108,					/*正处于蓝牙忙状态*/
    NDK_ERR_DEV_BUSY = -3109,                    /*正处于设备忙状态**/
    NDK_ERR_BT_NOT_CONNECTED = -3201,   		/**<蓝牙连接未建立*/

	NDK_ERR_POSNDK_BASE  = 			-4000,			/** POSNDK库 Libnl_ndk.so 的 ERROR错误前缀*/
	NDK_ERR_POSNDK_BUSY  = (NDK_ERR_POSNDK_BASE-1),	/** POSNDK 硬件忙*/
	NDK_ERR_POSNDK_TRANS_BUSY  = (NDK_ERR_POSNDK_BASE-2),		/** POSNDK 事务忙*/
	NDK_ERR_POSNDK_TRANS_ALREADY = (NDK_ERR_POSNDK_BASE-3),		/** POSNDK 已经在事务中*/
	NDK_ERR_POSNDK_TRANS_NOEXIST  = (NDK_ERR_POSNDK_BASE-4),	/** POSNDK 不在事务中*/
	NDK_ERR_POSNDK_SAFE_TRIGGER  = (NDK_ERR_POSNDK_BASE-5),		/** POSNDK 硬件安全触发*/
	NDK_ERR_POSNDK_EVENT_NUM = (NDK_ERR_POSNDK_BASE-6),			/** 错误的事件号*/
	NDK_ERR_POSNDK_EVENT_REG_TWICE = (NDK_ERR_POSNDK_BASE-7),	/** 重复注册事件*/
	NDK_ERR_POSNDK_EVENT_UNREG_TWICE = (NDK_ERR_POSNDK_BASE-8),	/**并未注册*/
	NDK_ERR_POSNDK_EVENT_INIT = (NDK_ERR_POSNDK_BASE-9),		/**初始化错*/
	NDKK_ERR_POSNDK_EVENT_INUSE =  (NDK_ERR_POSNDK_BASE-10),	/**事件被其它进程占用*/

	NDK_ERR_POSNDK_VKB_INITERR =(NDK_ERR_POSNDK_BASE-17),		/**虚拟键盘应用不存在或无法启动*/
	NDK_ERR_POSNDK_VKB_DATAERR  = (NDK_ERR_POSNDK_BASE-18),		/** POSNDK 虚拟键盘数据错*/	

	NDK_ERR_POSNDK_PERMISSION_UNDEFINED  = (NDK_ERR_POSNDK_BASE-21),	/** POSNDK 权限未声明*/
	NDK_ERR_POSNDK_ACCESS_BUSY  = (NDK_ERR_POSNDK_BASE-22), 	/** POSNDK相关操作被其它进程占有 */

	NDK_ERR_LINUX_ERRNO_BASE=		-5000, /**<<LINUX>系统函数返回ERROR错误前缀*/
	NDK_ERR_LINUX_TCP_TIMEOUT=  (NDK_ERR_LINUX_ERRNO_BASE-110),	/**<TCP远程端口错误*/
	NDK_ERR_LINUX_TCP_REFUSE=  (NDK_ERR_LINUX_ERRNO_BASE-111),	/**<TCP远程端口被拒绝*/
	NDK_ERR_LINUX_TCP_NOT_OPEN=		 (NDK_ERR_LINUX_ERRNO_BASE-88),/**<TCP句柄未打开错误*/

	NDK_ERR_SCRIPT_PRN_PARA  =  -7006,

	COM_FAIL= -6000,				/**<安全模块通讯失败*/
	NDK_CALL_BACK =			-6010,	/**<用于表示回调*/
	NDK_ERR_EMVUNSUPPORT = -10000,
}EM_NDK_ERR;

/** @} */ // 函数错误码结束


/** @addtogroup 打印
* @{
*/

/**
 *@brief 字体定义值
*/
typedef enum {
	PRN_HZ_FONT_24x24 = 1,
	PRN_HZ_FONT_16x32 ,
	PRN_HZ_FONT_32x32 ,
	PRN_HZ_FONT_32x16 ,
	PRN_HZ_FONT_24x32 ,
	PRN_HZ_FONT_16x16 ,
	PRN_HZ_FONT_12x16 ,
	PRN_HZ_FONT_16x8 ,
	PRN_HZ_FONT_24x24A ,			/**<自有宋体24x24点阵*/
	PRN_HZ_FONT_24x24B ,			/**<仿宋24x24点阵*/
	PRN_HZ_FONT_24x24C ,			/**<楷体24x24点阵*/
	PRN_HZ_FONT_24x24USER ,
	PRN_HZ_FONT_12x12A ,			/**<宋体12x12点阵(不支持)*/
	PRN_HZ_FONT_16x24 ,
	PRN_HZ_FONT_16x16BL,
	PRN_HZ_FONT_24x24BL,
	PRN_HZ_FONT_48x24A ,
	PRN_HZ_FONT_48x24B ,
	PRN_HZ_FONT_48x24C ,
	PRN_HZ_FONT_24x48A ,
	PRN_HZ_FONT_24x48B ,
	PRN_HZ_FONT_24x48C ,
	PRN_HZ_FONT_48x48A ,
	PRN_HZ_FONT_48x48B ,
	PRN_HZ_FONT_48x48C ,
}EM_PRN_HZ_FONT;

typedef enum {
	PRN_ZM_FONT_8x16 = 1,
	PRN_ZM_FONT_16x16 ,
	PRN_ZM_FONT_16x32 ,
	PRN_ZM_FONT_24x32 ,
	PRN_ZM_FONT_6x8 ,
	PRN_ZM_FONT_8x8 ,
	PRN_ZM_FONT_5x7 ,
	PRN_ZM_FONT_5x16 ,
	PRN_ZM_FONT_10x16 ,
	PRN_ZM_FONT_10x8 ,
	PRN_ZM_FONT_12x16A ,				/**<MSGothic粗体12x16点阵*/
	PRN_ZM_FONT_12x24A ,				/**<Gulimche字体12x24点阵*/
	PRN_ZM_FONT_16x32A ,				/**<MSGothic粗体16x32点阵*/
	PRN_ZM_FONT_12x16B ,				/**<MSGothic粗体12x16点阵*/
	PRN_ZM_FONT_12x24B ,				/**<MSGothic粗体12x24点阵*/
	PRN_ZM_FONT_16x32B ,				/**<MSGothic粗体16x32点阵*/
	PRN_ZM_FONT_12x16C ,				/**<楷体粗体12x16点阵*/
	PRN_ZM_FONT_12x24C ,				/**<楷体粗体12x24点阵*/
	PRN_ZM_FONT_16x32C ,				/**<楷体粗体16x32点阵*/
	PRN_ZM_FONT_24x24A ,
	PRN_ZM_FONT_32x32A ,
	PRN_ZM_FONT_24x24B ,
	PRN_ZM_FONT_32x32B ,
	PRN_ZM_FONT_24x24C ,
	PRN_ZM_FONT_32x32C ,
	PRN_ZM_FONT_12x12 ,
	PRN_ZM_FONT_12x12A ,
	PRN_ZM_FONT_12x12B ,
	PRN_ZM_FONT_12x12C ,
	PRN_ZM_FONT_8x12 ,
	PRN_ZM_FONT_8x24,
	PRN_ZM_FONT_8x32,
	PRN_ZM_FONT_12x32A,
	PRN_ZM_FONT_12x32B,
	PRN_ZM_FONT_12x32C,
	PRN_ZM_FONT_8x16BL,		/*8x16粗体,枚举值36*/
	PRN_ZM_FONT_16x16BL,
	PRN_ZM_FONT_12x24BL,
    PRN_ZM_FONT_8x16SR, /*宋体常规8X16,枚举值39*/
    PRN_ZM_FONT_8x16SBL, /*宋体粗体8X16,枚举值40*/
    PRN_ZM_FONT_12x24SR, /*宋体常规12X24,枚举值41*/
    PRN_ZM_FONT_12x24SBL, /*宋体粗体12X24,枚举值42*/
}EM_PRN_ZM_FONT;

/**
 *@brief  打印机状态以及错误定义，取打印机状态返回值存在两个或多个或上的关系
*/

typedef enum{
	PRN_STATUS_OK = 0,				/**<打印机正常*/
	PRN_STATUS_BUSY = 8,			/**<打印机正在打印*/
	PRN_STATUS_NOPAPER = 2,			/**<打印机缺纸*/
	PRN_STATUS_OVERHEAT = 4,		/**<打印机过热*/
	PRN_STATUS_VOLERR = 112			/**<打印机电压异常*/
}EM_PRN_STATUS;

/**
 *@brief  打印机AD通道号
*/
typedef enum{
	PRN_CHANELNUM_VOLTAGE, 		/**<电压采样*/
	PRN_CHANELNUM_TEMPERATURE	/**<温度采样*/
}EM_PRN_CHANELNUM;

/**
 *@brief 打印机类型枚举类型值
*/

typedef enum {
	PRN_TYPE_TP = 0,			/**<热敏打印机*/
	PRN_TYPE_HIP,				/**<穿孔针打*/
	PRN_TYPE_FIP,				/**<摩擦针打*/
	PRN_TYPE_END				/**<无*/
}EM_PRN_TYPE;

/**
 *@brief  打印机打印模式
*/
typedef enum{
	PRN_MODE_ALL_DOUBLE = 0,		/**<横向放大、纵向放大*/
	PRN_MODE_WIDTH_DOUBLE ,			/**<横向放大、纵向正常*/
	PRN_MODE_HEIGHT_DOUBLE,			/**<横向正常、纵向放大*/
	PRN_MODE_NORMAL					/**<横向正常、纵向正常*/
}EM_PRN_MODE;


/**
 *@brief 下划线功能开关的枚举类型值
*/
typedef enum{
	PRN_UNDERLINE_STATUS_OPEN = 0,			/**<下划线功能开*/
	PRN_UNDERLINE_STATUS_CLOSE			  	/**<下划线功能关*/
}EM_PRN_UNDERLINE_STATUS;

/**
 *@brief 字库注册信息
*/
typedef struct
{
	int nOffset;					/**<需要获取的字在字库中的依移量*/
	int nFontByte;					/**<需要获取的字在字库中占字节数*/
}ST_PRN_RECMSG;


/**
 *@brief 下划线功能开关的枚举类型值
*/
typedef struct
{
	char *pszName;					/**<字库名或字库所在路径*/
	int  nNum;						/**<注册号,支持0~7注册号*/
	int  nDirection;				/**<0:横向字体； 1:纵向字体*/
	int  w;							/**<字体显示的宽度*/
	int  h;							/**<字体显示的高度*/
	int  (*func)(char *, ST_PRN_RECMSG *);	/**<获取依移地址和占用字节数的函数*/
	int  nIsHZ;						/**<注册的字库是汉字字库还是ASCII字库，1:HZ  0:ZM*/
}ST_PRN_FONTMSG;

/*
*@brief 操作热敏打印机在打印单页前或打印完成后的进退纸操作的枚举类型
*/
typedef enum {
    PRN_FEEDPAPER_BEFORE = 0,		/**<单页打印前退纸*/
    PRN_FEEDPAPER_AFTER				/**<单页打印完成后进纸*/
}EM_PRN_FEEDPAPER;

typedef enum {
	TP_SET_PARAM_FOR_GRAYSCALE_LEVE, /*设置打印浓度等级 */
	TP_SET_PARAM_FOR_CHARGE_PRINTER_MODE, /* 系统通知打印是否进入充电打印模式 */
	TP_SET_PARAM_FOR_DRIVER_LOGIC, /* 打印驱动逻辑选择 */
	TP_SET_PARAM_FOR_FEEL_WELL_SPEED_MODE, /* 打印速度设置到最大的同时保证打印效果，不考虑马达失步风险 */
	TP_SET_PARAM_END = 0xFFFFFFFF,
} EM_PRN_SET_PARAM_TYPE;

/**
 *@brief 	打印机初始化
 *@details	包含清缓冲区,重置打印参数(包括字体、边距和模式)。
 *@param	unPrnDirSwitch	是否开启边送边打功能。
						0--关闭边送边打功能(默认)
							在该模式下，所有的NDK_PrnStr,NDK_PrnImage都完成点阵转换工作，将点阵数据存到数据缓冲区，
							在调用NDK_PrnStart之后才开始所有和打印相关的工作，包括走纸和打印。
						1--开启边送边打功能
							在该模式下，只要满一行数据，就会送驱动打印，调用\ref NDK_PrnFeedByPixel "NDK_PrnFeedByPixel()"，将立即走纸返回。
 *@return
 *@li	NDK_OK											操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 				参数错误
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		打印设备打开失败
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 				驱动调试错误(清打印缓冲区失败、打印重新设置失败)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 			内存空间不足
*/
extern int (*NDK_PrnInit)(uint unPrnDirSwitch);
/**
 *@brief 	打印字符串
 *@details 	该函数负责转换所有打印的字符串到点阵缓冲区，打印工作在调用Start之后开始送数打印。该函数为纯软件操作。
 *@param	pszBuf 		为以\0为结尾的串,串的内容可为ASC码，汉字 换行"\n"或"\r"(表示结束本行，对于空行则直接进纸)。
 						当pszBuf里面有汉字和ASCII的混合串时,字母和汉字只和最近一次设置有关。
 *@return
 *@li	NDK_OK												操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 					操作失败
 *@li	\ref NDK_ERR_INIT_CONFIG "NDK_ERR_INIT_CONFIG" 		初始化配置失败(打印未初始化)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 				内存空间不足
 *@li   \ref EM_PRN_STATUS   "EM_PRN_STATUS"				打印机状态值
*/
extern int (*NDK_PrnStr)(const char *pszBuf);

/**
 *@brief	开始启动打印（规范打印流程必须在调用打印接口之后调用NDK_PrnStart）
 *@details	NDK_PrnStr和NDK_PrnImage都是完成数据转换成点阵存储到缓冲区中工作，调用该函数开始送数打印。
			调用NDK_PrnStart打印结束后要判断返回值是否为0，如果返回-1则说明向打印送数失败，则立即返回打印机状态值，不进行继续送数操作。
			\ref NDK_PrnStart "NDK_PrnStart()"打印结束之后会阻塞等待返回打印机状态的值(包括边送边打和等待打印模式)。应用可根据NDK_PrnStart返回的值来判断打印机状态是否正常。
			(如果返回的非打印机状态值或者NDK_OK，即其他系统错误时需要应用去取打印机状态，该可能性比较小)
 *@return
 *@li	NDK_OK												打印结束且取打印机状态正常
 *@li	\ref NDK_ERR_INIT_CONFIG "NDK_ERR_INIT_CONFIG"		初始化配置失败(打印未初始化)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC"				内存空间不足
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"			打印设备打开失败
 *@li	\ref EM_PRN_STATUS   "EM_PRN_STATUS"				打印机状态值
*/
extern int (*NDK_PrnStart)(void);

/**
 *@brief 	打印图形(该函数也是转换打印点阵到点阵缓冲区，调用NDK_PrnStart开始打印)
 *@details	热敏打最大宽度384个点。如果unXsize和unXpos相加之和大于上述宽度限制会返回失败，如果是横向放大模式的话不能超过384/2。
 *@param 	unXsize 	图形的宽度（像素）
 *@param 	unYsize 	图形的高度（像素）
 *@param 	unXpos  	图形的左上角的列位置，且必须满足unXpos+unXsize<=ndk_PR_MAXLINEWIDE（正常模式为384，横向放大时为384/2，该参数为绝对坐标不受左边距影响）
 *@param 	psImgBuf 	图象点阵数据,为横向排列，第一个字节第一行的前8个点，D7为第一个点
 *@return
 *@li	NDK_OK									操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误
*/
extern int (*NDK_PrnImage)(uint unXsize,uint unYsize,uint unXpos,const char *psImgBuf);

/**
 *@brief	取打印驱动的版本信息.
 *@retval	pszVer	用于存储返回版本字符串
 *@return
 *@li	NDK_OK											操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 				参数错误
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		打印设备打开失败
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 				驱动调用失败(获取打印版本失败)
*/
extern int (*NDK_PrnGetVersion)(char *pszVer);
/**
 *@brief 	设置打印字体
 *@details	设置ASCII打印字体和汉字字体。应用层可参看底层和应用层的接口文件中的相关定义。
 *@param 	emHZFont	设置汉字字体格式，0保持当前字体不变。
 *@param	emZMFont	设置ASCII字体格式，0保持当前字体不变。
 *@return
 *@li	NDK_OK									操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"		操作失败
*/
extern int (*NDK_PrnSetFont)(EM_PRN_HZ_FONT emHZFont,EM_PRN_ZM_FONT emZMFont);
/**
 *@brief	获取打印机状态值.
 *@details	在打印之前可使用该函数判断打印机是否缺纸。
 *@retval	pemStatus 用于返回打印机状态值
 *@return
 *@li	NDK_OK										操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"			参数错误
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"	打印设备打开失败
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用失败(获取打印状态失败)
*/
extern int (*NDK_PrnGetStatus)(EM_PRN_STATUS *pemStatus);
/**
 *@brief 	设置打印模式.
 *@param 	emMode 打印模式(默认是使用正常模式)
 *@param    unSigOrDou 		打印单双向选择0--单向 1--双向(只对针打有效，热敏忽略)
 *@return
 *@li	NDK_OK									操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误
*/
extern int (*NDK_PrnSetMode)(EM_PRN_MODE emMode,uint unSigOrDou);
/**
 *@brief	设置打印灰度
 *@details	设置打印灰度(加热时间)，以便对于不同的打印纸进行打印效果微调.(只对热敏有效，对针打无效)
 *@param	unGrey		灰度值，范围0~5；0为最淡的效果，5为最浓的打印效果。打印驱动默认的灰度级别为3。
 *@return
 *@li	NDK_OK									操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"		参数错误
*/
extern int (*NDK_PrnSetGreyScale)(uint unGrey);
/**
 *@brief	设置打印左边界、字间距、行间距。在对打印机有效设置后将一直有效，直至下次
 *@param	unBorder	左边距 值域为：0-288(默认为0)
 *@param	unColumn	字间距 值域为：0-255(默认为0)
 *@param	unRow		行间距 值域为：0-255(默认为0)
 *@return
 *@li	NDK_OK											操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 				参数错误
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		打印设备打开失败
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 				驱动调用失败(左间距、字间距、行间距设置失败、图形对齐方式设置失败)
*/
extern int (*NDK_PrnSetForm)(uint unBorder,uint unColumn, uint unRow);
/**
 *@brief 	按像素走纸
 *@details	让打印机走纸，参数为像素点。(边送边打模式下该函数为直接走纸，而等待Star打印模式下存储步数到缓冲区，等待star开始走纸)
 *@param	unPixel 	走纸像素点 热敏值域为（0<=unPixel<=1024），针打为（-792<=unPixel<=792）
 *@return
 *@li	NDK_OK												操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 					参数错误
 *@li	\ref NDK_ERR_INIT_CONFIG "NDK_ERR_INIT_CONFIG"		初始化配置失败(打印未初始化配置)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC"				内存空间不足
*/
extern int (*NDK_PrnFeedByPixel)(uint unPixel);

/**
 *@brief	打印是否开启下划线功能.
 *@param	emStatus	0：开下划线；1：关下划线
 *@return
 *@li	NDK_OK									操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"		参数错误
*/
extern int (*NDK_PrnSetUnderLine)(EM_PRN_UNDERLINE_STATUS emStatus);

/**
 *@brief	脚本打印接口
 *@details	脚本打印图片宽度超过384时，会自动对图片做缩放，按384来打印
 *@param	prndata		脚本打印数据(脚本打印命令格式详见《高端平台指令集规范》中的附录A)
 *@param	indata_len	脚本命令长度
 *@return
 *@li	NDK_OK									操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败,命令解析错误
 *@li	\ref EM_PRN_STATUS	"EM_PRN_STATUS"		打印机状态值
*/
extern int (*NDK_Script_Print)(char* prndata,int indata_len);

/**
 *@brief        设置打印机参数
 *@details      当@type 为TP_SET_PARAM_FOR_DRIVER_LOGIC类型的时候，@value只能为EM_TP_LOGIC_DRIVER_TYPE当中的枚举值
 *@details      替换原先NDK_PrnSetGreyScale设置浓度函数,成功设置参数接口的类型之一
 *@param        type 设置参数类型
 *@param        value 设置对应参数的值
 *@return
 *@li   NDK_OK              操作成功
 *@li   \ref NDK_ERR_PARA "NDK_ERR_PARA"              参数错误
 *@li   \ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"      打开设备文件错误
 *@li   \ref NDK_ERR_NOT_SUPPORT "NDK_ERR_NOT_SUPPORT"  不支持该功能
 */
extern int (*NDK_PrnSetParam)(EM_PRN_SET_PARAM_TYPE type, int value);
/** @} */ // 打印模块结束


/** @addtogroup 文件系统
* @{
*/

/**
 *@brief	打开文件.
 *@details
 *@param    pszName 	文件名
 *@param    pszMode 	打开模式 "r"(以只读方式打开，如果不存在则失败) or "w"(以写的方式打开，如果文件不存在则创建)。
 *@return
 *@li	 fd											操作成功返回文件描述符
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数错误(文件名为NULL、模式不正确)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	文件打开失败
*/
extern int (*NDK_FsOpen)(const char *pszName,const char *pszMode);


/**
 *@brief 	关闭文件.
 *@details
 *@param    nHandle 	文件句柄
 *@return
 *@li	NDK_OK						操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 		操作失败(所关闭的文件不是调用NDK_FsOpen打开的、调用close()关闭失败)
*/
extern int (*NDK_FsClose)(int nHandle);

/**
 *@brief 	从打开的nHandle文件当前指针读unLength个字符到缓冲区psBuffer.
 *@details
 *@param    nHandle 	文件句柄
 *@param    unLength	需要读取的字符的长度
 *@retval   psBuffer	需要读入的缓冲区注意要足够读入unLength字节
 *@return
 *@li	返回实际读到数据长度
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误(psBuffer为NULL)
 *@li	\ref NDK_ERR_READ "NDK_ERR_READ" 		读文件失败(所读的文件不是调用NDK_FsOpen打开的、调用read()失败返回)
*/
extern int (*NDK_FsRead)(int nHandle, char *psBuffer, uint unLength );

/**
 *@brief 	向打开的nHandle文件写入unLength个字节.
 *@details
 *@param    nHandle 	文件句柄
 *@param    psBuffer	需要写入文件内容的缓冲区
 *@param    unLength	需要写入的长度
 *@return
 *@li	返回实际写入数据长度
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误(psBuffer为NULL)
 *@li	\ref NDK_ERR_WRITE "NDK_ERR_WRITE" 		写文件失败(所写的文件不是调用NDK_FsOpen打开的、调用write()失败返回)
*/
extern int (*NDK_FsWrite)(int nHandle, const char *psBuffer, uint unLength );

/**
 *@brief 	移动文件指针到从unPosition起距ulDistance的位置
 *@details
 *@param    nHandle 	文件句柄
 *@param    ulDistance	根据参数unPosition来移动读写位置的位移数。
 *@param    unPosition	需要读取的字符的长度
 						SEEK_SET 参数offset即为新的读写位置。
						SEEK_CUR 以目前的读写位置往后增加offset个位移量。
						SEEK_END 将读写位置指向文件尾后再增加offset个位移量。
 *@return
 *@li	NDK_OK						操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 		操作失败(所移动的文件不是调用NDK_FsOpen打开的、调用lseek()失败返回)
*/
extern int (*NDK_FsSeek)(int nHandle, ulong ulDistance, uint unPosition );

/**
 *@brief 	删除指定文件
 *@details
 *@param    pszName 	要删除的文件名
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误(pszName为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败(调用remove()失败返回)
*/
extern int (*NDK_FsDel)(const char *pszName);

/**
 *@brief 	文件长度
 *@details
 *@param    pszName 	文件名
 *@retval   punSize 	文件大小返回值
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误(pszName、punSize为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败(打开文件失败、调用fstat()失败返回)
*/
extern int (*NDK_FsFileSize)(const char *pszName,uint *punSize);

/**
 *@brief 	文件重命名
 *@details
 *@param    pszSrcName 		原文件名
 *@param    pszDstName 		目标文件名
 *@return
 *@li	NDK_OK									操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误(pszsSrcname、pszDstname为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败(调用rename()失败返回)
*/
extern int (*NDK_FsRename)(const char *pszSrcName, const char *pszDstName );

/**
 *@brief 	测试文件是否存在
 *@details
 *@param   	pszName 	文件名
 *@return
 *@li	NDK_OK									操作成功(文件存在)
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误(pszName为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败(调用access()失败返回) 
*/
extern int (*NDK_FsExist)(const char *pszName);

/**
 *@brief 	文件截短
 *@details   NDK_FsTruncate()会将参数pszPath 指定的文件大小改为参数unLen 指定的大小。如果原来的文件大小比参数unLen大，则超过的部分会被删去。
 		   			如果原来文件的大小比unLen小的话，不足的部分将补上0xff
 *@param    pszPath 	文件路径
 *@param    unLen 		所要截短长度
 *@return
 *@li	NDK_OK									操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误(pszPath为NULL)
 *@li	\ref NDK_ERR_PATH "NDK_ERR_PATH" 		文件路径非法
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败(计算文件大小失败、调用lseek()失败返回、调用truncate()失败返回)
 *@li	\ref NDK_ERR_WRITE "NDK_ERR_WRITE" 		写文件失败(调用write()失败返回)
*/
extern int (*NDK_FsTruncate)(const char *pszPath ,uint unLen );

/**
 *@brief 	读取文件流位置
 *@details   用来取得文件流目前的读写位置
 *@param    nHandle 	文件句柄
 *@retval   pulRet 		文件流位置
 *@return
 *@li	NDK_OK									操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误(pulRet为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败(操作的文件不是调用NDK_FsOpen打开的文件、调用lseek()失败返回)
*/
extern int (*NDK_FsTell)(int nHandle,ulong *pulRet);

/**
 *@brief 	文件系统格式化(暂为不支持)
 *@details	该功能仅限于传统pos上gp平台pos直接返回-1
 *@return
 *@li	 NDK_OK												操作成功返回
 *@li	\ref NDK_ERR_NOT_SUPPORT "NDK_ERR_NOT_SUPPORT" 		未支持该功能
*/
extern int (*NDK_FsFormat)(void);

/**
 *@brief 	文件拷贝
 *@details	将pad端的指定文件拷贝到k21端/appfs/目录下，文件名长度不能超过12个字节
 *@param    sourcefile 原文件名(包含路径)
 *@param    destfile 	 目标文件名（包含路径/appfs/）
 *@return
 *@li	 NDK_OK				操作成功返回
 *@li	 NDK_ERR			错误
*/
extern int (*NDK_CopyFileToSecMod)(const unsigned char* sourcefile, const unsigned char* destfile);

/** @} */ // 文件系统模块结束

/** @addtogroup 算法
* @{
*/

#define MAX_RSA_MODULUS_LEN		512				/**< 最大模长度 */
#define MAX_RSA_PRIME_LEN		256				/**< 最大模素数长度 */

/**
 *@brief 对称密钥算法
*/
typedef enum{
	ALG_TDS_MODE_ENC = 0,		/**< DES加密 */
	ALG_TDS_MODE_DEC = 1,		/**< DES解密 */
}EM_ALG_TDS_MODE;

/**
 *@brief RSA算法密钥长度
*/
typedef enum{
	RSA_KEY_LEN_512  = 512,
	RSA_KEY_LEN_1024 = 1024,
	RSA_KEY_LEN_2048 = 2048,
}EM_RSA_KEY_LEN;

/**
 *@brief RSA算法指数
*/
typedef enum{
	RSA_EXP_3 = 0x03,
	RSA_EXP_10001 = 0x10001,
}EM_RSA_EXP;

/**
 *@brief RSA公钥
*/
typedef struct {
    ushort bits;       								/**< 模位数 */
    uchar modulus[MAX_RSA_MODULUS_LEN+1];      		/**< 模 */
	uchar publicExponent[MAX_RSA_MODULUS_LEN+1]; 	/**< 公钥指数 */
    uchar exponent[MAX_RSA_MODULUS_LEN+1];     		/**< 私钥指数 */
    uchar prime[2][MAX_RSA_PRIME_LEN+1];      		/**< pq素数 */
    uchar primeExponent[2][MAX_RSA_PRIME_LEN+1]; 	/**< 素数与指数除法值 */
    uchar coefficient[MAX_RSA_PRIME_LEN+1];  	 	/**< 素数与素数除法值 */
}ST_RSA_PRIVATE_KEY;

/**
 *@brief RSA私钥
*/
typedef struct {
    ushort bits;                    			/**< 模位数 */
    uchar modulus[MAX_RSA_MODULUS_LEN+1];  		/**< 模 */
    uchar exponent[MAX_RSA_MODULUS_LEN+1];		/**< 指数 */
}ST_RSA_PUBLIC_KEY;

/**
 *@brief	计算des
 *@param	psDataIn	加密数据缓冲
 *@param	psKey		密钥缓冲,长度8,16,24
 *@param    nKeyLen     密钥长度，值只能为8,16,24
 *@param	nMode		加密模式 参见\ref EM_ALG_TDS_MODE "EM_ALG_TDS_MODE"
 *@retval	psDataOut	输出数据
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(psDataIn/psDataOut/psKey为NULL、密钥长度值不是8/16/24、加密模式非法)
*/
extern int (*NDK_AlgTDes)(uchar *psDataIn, uchar *psDataOut, uchar *psKey, int nKeyLen, int nMode);


/**
 *@brief	计算sha1
 *@param	psDataIn	输入数据
 *@param	nInlen		数据长度
 *@retval	psDataOut	输出数据（sha1计算结果长度为20字节）
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(psDataIn/psDataOut为NULL、nInlen<0、加密模式非法)	
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败
*/
extern int (*NDK_AlgSHA1)(uchar *psDataIn, int nInlen, uchar *psDataOut);

/**
 *@brief	计算sha256
 *@param	psDataIn	输入数据
 *@param	nInlen		数据长度
 *@retval	psDataOut	输出数据（sha256计算结果长度为  字节）
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(psDataIn/psDataOut为NULL、nInlen<0、加密模式非法)	
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败
*/
extern int (*NDK_AlgSHA256)(uchar *psDataIn, int nInlen, uchar *psDataOut);

/**
 *@brief	计算sha512
 *@param	psDataIn	输入数据
 *@param	nInlen		加密模式
 *@retval	psDataOut	输出数据（sha512计算结果长度为 字节）
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(psDataIn/psDataOut为NULL、nInlen<0、加密模式非法)	
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败
*/
extern int (*NDK_AlgSHA512)(uchar *psDataIn, int nInlen, uchar *psDataOut);

/**
 *@brief	RSA密钥对生成
 *@param	nProtoKeyBit		密钥位数，当前支持512、1024和2048位 参考\ref EM_RSA_KEY_LEN "EM_RSA_KEY_LEN"
 *@param	nPubEType			指数类型，参考\ref EM_RSA_EXP "EM_RSA_EXP"
 *@retval	pstPublicKeyOut		公钥
 *@retval	pstPrivateKeyOut	私钥
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(nProtoKeyBit密钥位数非法、pstPublicKeyOut\pstPrivateKeyOut为NULL、nPubEType指数类型非法)	
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败
*/
extern int (*NDK_AlgRSAKeyPairGen)( int nProtoKeyBit, int nPubEType, ST_RSA_PUBLIC_KEY *pstPublicKeyOut, ST_RSA_PRIVATE_KEY *pstPrivateKeyOut);

/**
 *@brief	RSA密钥对加解密
 *@details	该函数进行RSA加密或解密运算,加密或解密通过选用不同的密钥实现。如(Modul,Exp)选用私有密钥,则进行加密;如选用公开密钥,则进行解密。
 			psDataIn的第一个字节必须小于psModule的第一个字节。 该函数可实现长度不超过2048 bits 的RSA运算。
 			输入的数据开辟的缓冲须是模长度+1。
 *@param	psModule		模缓冲,字符串的形式存入,如"31323334"
 *@param	nModuleLen	模的长度 只有三种选择512/8,1024/8,2048/8
 *@param	psExp			存放RSA运算的指数缓冲区指针。就是e.按高位在前,低位在后的顺序存储,如"10001"
 *@param	psDataIn		数据缓冲,缓冲区的大小须比模的长度大1
 *@retval	psDataOut		输出数据,输出的数据长度等于模的长度。
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(nModuleLen模的长度非法、psModule\psExp\psDataIn\psDataOut为NULL)	
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败
*/
extern int (*NDK_AlgRSARecover)(uchar *psModule, int nModuleLen, uchar *psExp, uchar *psDataIn, uchar *psDataOut);

/**
 *@brief	RSA密钥对校验
 *@param	pstPublicKey		公钥
 *@param	pstPrivateKey		私钥
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pstPublicKey\pstPrivateKey为NULL)	
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败
*/
extern int (*NDK_AlgRSAKeyPairVerify)(ST_RSA_PUBLIC_KEY *pstPublicKey, ST_RSA_PRIVATE_KEY *pstPrivateKey);

/**
 *@brief	生成SM2非对称公私钥对的功能（国密算法）
 *@retval	eccpubkey		公钥
 *@retval	eccprikey		私钥
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pstPublicKey\pstPrivateKey为NULL)	
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败
*/
extern int (*NDK_AlgSM2KeyPairGen)( unsigned char *eccpubkey, unsigned char *eccprikey );

/**
 *@brief	SM2非对称公私钥对中的公钥进行加密的功能（国密算法）
 *@param	eccpubkey		公钥
 *@param	Message			被加密数据
 *@param	MessageLen		数据长度
 *@retval	Crypto			加密后密文数据
 *@retval	CryptoLen		加密后数据长度
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败
*/
extern int (*NDK_AlgSM2Encrypt)( unsigned char *eccpubkey, unsigned char *Message, unsigned short MessageLen, unsigned char *Crypto, unsigned short *CryptoLen );

/**
 *@brief	SM2非对称公私钥对中的私钥进行解密的功能（国密算法）
 *@param	eccprikey		私钥
 *@param	Crypto			待解密密文数据
 *@param	CryptoLen		密文数据长度
 *@retval	Message			解密后数据
 *@retval	MessageLen		解密后数据长度
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pstPublicKey\pstPrivateKey为NULL)	
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败
*/
extern int (*NDK_AlgSM2Decrypt)( unsigned char *eccprikey, unsigned char *Crypto, unsigned short CryptoLen, unsigned char *Message, unsigned short *MessageLen );

/**
 *@brief	SM2非对称公私钥对中的私钥对摘要（32字节）计算数字签名（64字节）的功能（国密算法）
 *@param	eccprikey		私钥
 *@param	e				摘要数据
 *@retval	output			签名数据
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pstPublicKey\pstPrivateKey为NULL)	
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败
*/
extern int (*NDK_AlgSM2Sign)(unsigned char *eccprikey, unsigned char *e, unsigned char *output );

/**
 *@brief	SM2非对称公私钥对中的公钥对数字签名（64字节）计算摘要（32字节）的功能（国密算法）
 *@param	pPublicKey		公钥
 *@param	e				摘要数据
 *@retval	pSignedData		签名数据
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pstPublicKey\pstPrivateKey为NULL)	
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败
*/
extern int (*NDK_AlgSM2Verify)( unsigned char *pPublicKey, unsigned char *e, unsigned char *pSignedData );

/**
 *@brief	生成SM2非对称公私钥对对应的数字摘要（32字节）的功能（注意：在该算法中，运算还需要用到ID、对应SM2非对称公私钥对的公钥等数据）（国密算法）
 *@param	usID			标签长度
 *@param	pID				标签内容
 *@param	usM				原数据长度
 *@param	pM				原数据
 *@param	pubKey			公钥
 *@retval	pHashData		摘要
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pstPublicKey\pstPrivateKey为NULL)	
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败
*/
extern int (*NDK_AlgSM2GenE)( unsigned short usID, unsigned char *pID, unsigned short usM, unsigned char *pM, unsigned char *pubKey, unsigned char *pHashData);

/**
 *@brief	国密sm3算法(启动CCP运算)
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
*/
extern int (*NDK_AlgSM3Start)(void);

/**
 *@brief	根据SM3算法以分组方式（除最后一个分组以外，每个分组长度须为64字节整数倍）生成数字摘要（32字节）的功能（国密算法）
 *@details	update一个分组数据，数据为64字节整数倍
 *@param	pDat			一个分组数据，64字节整数倍
 *@param	len				更新数据长度
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pstPublicKey\pstPrivateKey为NULL)	
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败
*/
extern int (*NDK_AlgSM3Update)( unsigned char *pDat,  unsigned int len );

/**
 *@brief	根据SM3算法以分组方式注入最后一组数据后生成数字摘要（32字节）（国密算法）
 *@param	pDat			最后一个分组数据
 *@param	len				最后一组数据长度
 *@retval	pHashDat		生成摘要
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pstPublicKey\pstPrivateKey为NULL)	
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败
*/
extern int (*NDK_AlgSM3Final)( unsigned char *pDat, unsigned int len, unsigned char *pHashDat );

/**
 *@brief	SM3算法以一次性传入数据方式生成数字摘要（32字节）（国密算法）
 *@param	pDat			数据
 *@param	len				数据长度
 *@retval	pHashDat		生成摘要
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pstPublicKey\pstPrivateKey为NULL)	
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败
*/
extern int (*NDK_AlgSM3Compute)( unsigned char *pDat, unsigned int len, unsigned char *pHashDat );

/**
 *@brief	SM4算法以ECB、CBC等模式进行对称密钥加解密运算
 *@param	pKey			密钥
 *@param	pIVector		初始向量值
 *@param	len				数据长度
 *@param	pSm4Input		待加解密数据
 *@param	mode			加解密模式
 *@retval	pSm4Output		加解密生成数据
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pstPublicKey\pstPrivateKey为NULL)	
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败
*/
extern int (*NDK_AlgSM4Compute)(unsigned char *pKey, unsigned char *pIVector, unsigned int len, unsigned char *pSm4Input, unsigned char *pSm4Output, unsigned char mode);

/** @} */ // 算法模块结束

/** @addtogroup 安全
* @{
*/

/**
 *@brief 终端密钥类型
*/
typedef enum{
	SEC_KEY_TYPE_TLK=0,		/**<终端装载密钥*/
	SEC_KEY_TYPE_TMK,		/**<终端主密钥*/
	SEC_KEY_TYPE_TPK,		/**<终端PIN密钥*/
	SEC_KEY_TYPE_TAK,		/**<终端MAC密钥*/
	SEC_KEY_TYPE_TDK,		/**<终端数据加解密密钥*/
	SEC_KEY_TYPE_TDUKPT,	/**<终端dukpt数据加解密密钥*/
}EM_SEC_KEY_TYPE;

typedef enum{
	SEC_KEY_DES = 0,			/**<DES/TDES 算法*/
	SEC_KEY_SM4 = (1<<6),		/**<SM4 算法*/
}EM_SEC_KEY_ALG;

/**
 *@brief 密钥校验模式
*/
typedef enum{
	SEC_KCV_NONE=0,		/**<无验证*/
	SEC_KCV_ZERO,		/**<对8个字节的0x00计算DES/TDES加密,得到的密文的前4个字节即为KCV*/
	SEC_KCV_VAL,		/**<首先对密钥明文进行奇校验,再对"\x12\x34x56\x78\x90\x12\x34\x56"进行DES/TDES加密运算,得到密文的前4个字节即为KCV,暂不支持*/
	SEC_KCV_DATA,		/**<传入一串数据KcvData,使用源密钥对[aucDstKeyValue(密文) + KcvData]进行指定模式的MAC运算,得到8个字节的MAC即为KCV,暂不支持 */
}EM_SEC_KCV;

/**
 *@brief MAC算法
*/
typedef enum{
	SEC_MAC_X99=0,      /**< X99算法：数据分为8字节block，不足补0，每个block加密后与下一个block异或后按密钥长度加密*/
	SEC_MAC_X919,       /**< X99算法：数据分为8字节block，不足补0，每个block加密后与下一个block异或后按密钥DES加密，
                            最后帧如果密钥长度为16字节则按3DES，如果为8字节按DES*/
	SEC_MAC_ECB,        /**< 全部数据异或后，将异或后数据做DES后进行变换，参考银联规范中关于ECB算法说明*/
	SEC_MAC_9606,       /**< 全部数据异或后，最后将异或数据做des运算*/
	SEC_MAC_SM4,        /**< 数据分为16字节的block，不足补0，每个block进行SM4加密后与下一个block异或后按SM4加密*/
	SEC_ROOT_MAC_X919=0x10,	/**< 使用根MAC密钥计算*/
}EM_SEC_MAC;
/**
 * 用于实现PIN输入过程的超时控制的变量
 */
typedef enum {
	SEC_PIN_ISO9564_0=3,    /**<使用主账号加密，密码不足位数补'F'*/
	SEC_PIN_ISO9564_1=4,    /**<不使用主账号加密，密码不足位数补随机数*/
	SEC_PIN_ISO9564_2=5,    /**<不使用主账号加密，密码不足位数补'F'*/
	SEC_PIN_ISO9564_3=6,    /**<使用主账号加密，密码不足位数补随机数*/
	SEC_PIN_SM4_1,			/**<不使用主账号，密码不足位数补'F'*/
	SEC_PIN_SM4_2,			/**<使用主账号填充方式1，密码不足位数补'F'*/
	SEC_PIN_SM4_3,			/**<使用主账号填充方式1，密码不足位数补随机数*/
	SEC_PIN_SM4_4,			/**<使用主账号填充方式2，密码不足位数补'F'*/
	SEC_PIN_SM4_5,			/**<使用主账号填充方式2，密码不足位数补随机数*/
}EM_SEC_PIN;

/**
 *@brief DES计算类型，对于不同位可以进行或运算，
        例如：SEC_DES_ENCRYPT|SEC_DES_KEYLEN_8
            表示，用8字节密钥长度，使用ECB模式进行加密运算
*/
typedef enum{
	SEC_DES_ENCRYPT=0,                  /**<DES加密*/
	SEC_DES_DECRYPT=1,                  /**<DES解密*/
	SEC_DES_KEYLEN_DEFAULT=(0<<1),      /**<使用安装长度的密钥进行加密*/
    SEC_DES_KEYLEN_8=(1<<1),            /**<使用8字节密钥进行加密*/
    SEC_DES_KEYLEN_16=(2<<1),           /**<使用16字节密钥进行加密*/
    SEC_DES_KEYLEN_24=(3<<1),           /**<使用24字节密钥进行加密*/
    SEC_DES_MASK=0x07,					/**<des计算类型使用的映射值，超过该映射值位数无效*/
    SEC_SM4_ENCRYPT=(1<<4),             /**<SM4加密*/
	SEC_SM4_DECRYPT=(1<<5),             /**<SM4解密*/
}EM_SEC_DES;

/**
 *@brief VPP 服务返回的键值定义
*/
typedef enum{
    SEC_VPP_KEY_PIN,					/**< 有PIN键码按下，应用应该显示'*'*/
    SEC_VPP_KEY_BACKSPACE,				/**< 退格键按下*/
    SEC_VPP_KEY_CLEAR,					/**< 清除键按下*/
    SEC_VPP_KEY_ENTER,					/**< 确认键按下*/
    SEC_VPP_KEY_ESC,					/**< pin输入取消*/
    SEC_VPP_KEY_NULL					/**< pin无事件产生*/
}EM_SEC_VPP_KEY;

/**
 *@brief 脱机终端校验模式
 */
typedef enum{
	SEC_OFFLINE_PIN_MODE_EMV=0,
}ST_SEC_OFFLINE_PIN_MODE;

/**
 *@brief 终端安全攻击状态
 */
typedef enum{
	SEC_TAMPER_STATUS_NONE = 0,					/**< 无安全攻击*/
	SEC_TAMPER_STATUS_HW = (1<<0),				/**< 硬件安全攻击*/
	SEC_TAMPER_STATUS_SEC_CONFIG = (1<<1),		/**< 安全寄存器值错误*/
	SEC_TAMPER_STATUS_CHECKFILE = (1<<2),		/**< 文件校验错误*/
	SEC_TAMPER_DEVICE_DISABLED = (1<<8),		/**< 设备未使能*/
}EM_SEC_TAMPER_STATUS;

#define SEC_KEYBLOCK_FMT_TR31		(0x54523331)	/**<扩展的TR-31 Key block密钥安装包格式,0x54523331即"TR31" */
/**
 *@brief 扩展密钥安装包信息，用于实现TR-31等扩展的密钥安装包格式
 *		  当用户想使用TR-31封装包来安装密钥的时候，需要将密钥数据封装成ST_EXTEND_KEYBLOCK结构，
 *		  并存储到ST_SEC_KEY_INFO结构的sDstKeyValue[24]成员，传递给密钥安装接口，系统将会尝试使用该格式解析安装密钥。
*/
typedef struct {
	unsigned int format;		/**< 扩展密钥安装包格式,目前仅支持TR-31格式 SEC_KEYBLOCK_FMT_TR31*/
	unsigned int len;			/**< 密钥安装包数据(pblock)长度*/
	char *pblock;				/**< 密钥数据指针*/
}ST_EXTEND_KEYBLOCK;

/**
 *@brief 密钥信息
*/
typedef struct{
    uchar 	ucScrKeyType; 		/**< 发散该密钥的源密钥的密钥类型，参考\ref EM_SEC_KEY_TYPE "EM_SEC_KEY_TYPE", 不得低于ucDstKeyType所在的密钥级别*/
    uchar 	ucDstKeyType; 		/**< 目的密钥的密钥类型，参考\ref EM_SEC_KEY_TYPE "EM_SEC_KEY_TYPE" */
    uchar 	ucScrKeyIdx;		/**< 发散该密钥的源密钥索引,索引一般从1开始,如果该变量为0,则表示这个密钥的写入是明文形式 */
    uchar 	ucDstKeyIdx;		/**< 目的密钥索引 */
    int 	nDstKeyLen;			/**< 目的密钥长度,8,16,24,或12=sizeof(ST_EXTEND_KEYBLOCK) */
    uchar 	sDstKeyValue[24];	/**< 写入密钥的内容，当密钥长度等于12时， 当做ST_EXTEND_KEYBLOCK结构使用*/
}ST_SEC_KEY_INFO;

/**
 *@brief 校验信息
*/
typedef struct{
    int 	nCheckMode; 		/**< 校验模式 参考\ref EM_SEC_KCV "EM_SEC_KCV"*/
    int 	nLen;				/**< 校验数据区长度 */
    uchar 	sCheckBuf[128];		/**< 校验数据缓冲区 */
}ST_SEC_KCV_INFO;

/**
 *@brief 脱机密文PIN密钥
*/
typedef struct
{
	uint	unModlen;					/**< 加密公钥模数长  */
	uchar	sMod[MAX_RSA_MODULUS_LEN];  /**< 加密公钥模数,高字节在前,低字节在后,不足位前补0 */
	uchar	sExp[4];       				/**< 加密公钥指数,高字节在前,低字节在后,不足位前补0 */
	uchar	ucIccRandomLen;   			/**< 从卡片取得的随机数长  */
	uchar	sIccRandom[8];   			/**< 从卡片取得的随机数  */
}ST_SEC_RSA_PINKEY;

/**
 *@brief RSA密钥信息
*/
typedef struct {
    ushort usBits;                    			/**< RSA密钥位数 */
    uchar sModulus[MAX_RSA_MODULUS_LEN+1];  	/**< 模 */
    uchar sExponent[MAX_RSA_MODULUS_LEN+1]; 	/**< 指数 */
    uchar reverse[4];							/**< 补齐4字节，用于密钥存储*/
}ST_SEC_RSA_KEY;

/**
 *@brief	读取安全接口版本
 *@retval	pszVerInfoOut	版本信息（小于16字节）
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pszVerInfoOut为NULL)
*/
extern int (*NDK_SecGetVer)(uchar * pszVerInfoOut);

/**
 *@brief	获取随机数
 *@param	nRandLen		需要获取的长度
 *@retval	pvRandom		随机数缓冲
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pvRandom为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败
*/
extern int (*NDK_SecGetRandom)(int nRandLen , void *pvRandom);

/**
 *@brief	读取密钥kcv值
 *@details	获取密钥的KCV值,以供对话双方进行密钥验证,用指定的密钥及算法对一段数据进行加密,并返回部分数据密文。
 *@param	ucKeyType		密钥类型
 *@param	ucKeyIdx		密钥序号
 *@param	pstKcvInfoOut	KCV加密模式
 *@retval	pstKcvInfoOut	KCV值
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pstKcvInfoOut为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败
*/
extern int (*NDK_SecGetKcv)(uchar ucKeyType, uchar ucKeyIdx, ST_SEC_KCV_INFO *pstKcvInfoOut);

/**
 *@brief	擦除所有密钥
 *@return
 *@li	NDK_OK		操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 		操作失败
*/
extern int (*NDK_SecKeyErase)(void);

/**
 *@brief	写入一个密钥,包括TLK,TMK和TWK的写入、发散,并可以选择使用KCV验证密钥正确性。
 *@details
 	PED采用三层密钥体系,自上到下的顺序依次为：
	TLK－Terminal Key Loading Key
    	收单行或POS运营商的私有密钥,由收单行或者POS运营商在安全环境下直接写入。
    	该密钥每个PED终端只有一个,其索引号自1至1

	TMK－Terminal Master Key＝Acquirer Master Key
		终端主密钥,或者称为收单行主密钥。该类密钥可有100个,索引号自1至100
		TMK可以在安全环境下直接写入,直接写入TMK,并通过TMK分散TWK的方式与MK/SK的密钥体系一致。
	TWK－Transaction working key = Transaction Pin Key + Transaction MAC Key + Terminal DES Enc Key + Terminal DES DEC/ENC Key
		终端工作密钥,进行PIN密文、MAC等运算的密钥。该类密钥可有100个,索引号自1至100。
		TPK:用于应用输入PIN后,计算PIN Block。
		TAK:用于应用报文通讯中,计算MAC。
		TEK:用于对应用中敏感数据进行DES/TDES加密传输或存储。
		TDK:用于对应用中敏感数据进行DES/TDES加解密运用
	TWK可以在安全环境下直接写入,直接写入TWK与Fixed Key密钥体系一致。每个密钥有其索引号,长度,用途和标签。
	其中密钥的标签是在写入密钥前通过API设定的,以授权该密钥的使用权限并保证密钥不会被滥用。

	DUKPT密钥机制：
	DUKPT【Derived Unique Key Per Transaction】密钥体系是一次交易一密钥的密钥体系,其每笔交易的工作密钥【PIN、MAC】是不同的。
	它引入了KSN【Key Serial Number】的概念,KSN是能实现一次一密的关键因子。 每个KSN对应的密钥，根据密钥用途，产生出不同的密钥。
 	该类密钥可有10组。在写入TIK的时候,需要选择组的索引号,在使用DUKPT密钥时选择对应的组索引。
 *@param	pstKeyInfoIn		密钥信息
 *@param	pstKcvInfoIn		密钥校验信息
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pstKeyInfoIn、pstKcvInfoIn为NULL、密钥长度不等于8/16/24、不是扩展TR-31格式的安装包)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 	内存空间不足
 *@li	\ref NDK_ERR "NDK_ERR" 		              操作失败
*/
extern int (*NDK_SecLoadKey)(ST_SEC_KEY_INFO * pstKeyInfoIn, ST_SEC_KCV_INFO * pstKcvInfoIn);

/**
 *@brief	计算MAC
 *@param	ucKeyIdx		密钥序号
 *@param	psDataIn		输入数据
 *@param	nDataInLen		输入数据长度
 *@param	ucMod			MAC计算模式 参考\ref EM_SEC_MAC "EM_SEC_MAC"
 *@retval	psMacOut		MAC值，长度8字节
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 						操作失败
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 		内存空间不足
*/
extern int (*NDK_SecGetMac)(uchar ucKeyIdx, uchar *psDataIn, int nDataInLen, uchar *psMacOut, uchar ucMod);

/**
 *@brief	获取PIN Block
 *@param	ucKeyIdx		密钥序号
 *@param	pszExpPinLenIn	密码长度，可使用,进行分割，例如：0,4,6
 *@param	pszDataIn		按ISO9564要求的输入PIN BLOCK
 *@param	ucMode			计算模式 参考\ref EM_SEC_PIN "EM_SEC_PIN"
 *@param	nTimeOutMs		超时时间（不允许小于5秒或者大于200秒）单位:ms
 *@retval	psPinBlockOut	PIN Block输出,该参数传入NULL时，PIN结果通过\ref NDK_SecGetPinResult "NDK_SecGetPinResult()"函数获取
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 			内存空间不足
 *@li	\ref NDK_ERR_SECP_PARAM "NDK_ERR_SECP_PARAM" 	参数非法(计算模式非法)
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 				参数非法(时间参数非法)
*/
extern int (*NDK_SecGetPin)(uchar ucKeyIdx, uchar *pszExpPinLenIn,const uchar * pszDataIn, uchar *psPinBlockOut, uchar ucMode, uint nTimeOutMs);

/**
 *@brief	计算DES
 *@details 	使用指定密钥进行des计算，注意：1~255序号进行加解密
 *@param	ucKeyType		DES密钥类型
 *@param	ucKeyIdx		DES密钥序号
 *@param	psDataIn		数据信息
 *@param	nDataInLen		数据长度
 *@param	ucMode			加密模式 参考\ref EM_SEC_DES "EM_SEC_DES"
 *@retval	psDataOut		数据输出信息
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 				参数非法
 *@li	\ref NDK_ERR "NDK_ERR"							操作失败
 *@li	\ref NDK_ERR_SECP_PARAM "NDK_ERR_SECP_PARAM" 	参数非法(数据长度不是8的整数倍)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 			内存空间不足
*/
extern int (*NDK_SecCalcDes)(uchar ucKeyType, uchar ucKeyIdx, uchar * psDataIn, int nDataInLen, uchar *psDataOut, uchar ucMode);

/**
 *@brief	校验脱机明文PIN
 *@details 	获取明文PIN,然后按照应用提供的卡片命令与卡片通道号,将明文PIN BLOCK直接发送给卡片(PIN BLOCK格式在用法部分描述)。
 *@param	ucIccSlot		IC卡号
 *@param	pszExpPinLenIn	密码长度，可使用,进行分割，例如：0,4,6
 *@param	ucMode			IC卡计算模式(只支持EMV)
 *@param	unTimeoutMs		超时时间
 *@retval	psIccRespOut	卡片应答码,该参数传入NULL时，PIN结果通过\ref NDK_SecGetPinResult "NDK_SecGetPinResult()"函数获取
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 					参数非法(超时参数非法)
 *@li	\ref NDK_ERR_SECP_PARAM "NDK_ERR_SECP_PARAM" 		参数非法(ucMode非法等)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 	            内存空间不足
 *@li	\ref NDK_ERR "NDK_ERR" 	                            操作失败
*/
extern int (*NDK_SecVerifyPlainPin)(uchar ucIccSlot, uchar *pszExpPinLenIn, uchar *psIccRespOut, uchar ucMode,  uint unTimeoutMs);

/**
 *@brief	校验脱机明文PIN
 *@details 	先获取明文PIN,再用应用提供的RsaPinKey对明文PIN按照EMV规范进行加密,然后用应用提供的卡片命令与卡片通道号,将密文PIN直接发送给卡片
 *@param	ucIccSlot		IC卡号
 *@param	pszExpPinLenIn	密码长度，可使用,进行分割，例如：0,4,6
 *@param	pstRsaPinKeyIn	RSA密钥数据
 *@param	ucMode			IC卡计算模式(只支持EMV)
 *@param	unTimeoutMs		超时时间
 *@retval	psIccRespOut	卡片应答码,该参数传入NULL时，PIN结果通过\ref NDK_SecGetPinResult "NDK_SecGetPinResult()"函数获取
 *@return
 *@li	NDK_OK				    						操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 				参数非法(超时参数非法)
 *@li	\ref NDK_ERR_SECP_PARAM "NDK_ERR_SECP_PARAM" 	参数非法(ucMode非法等)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 	        内存空间不足
 *@li	\ref NDK_ERR "NDK_ERR" 	                		操作失败
*/
extern int (*NDK_SecVerifyCipherPin)(uchar ucIccSlot, uchar *pszExpPinLenIn, ST_SEC_RSA_KEY *pstRsaPinKeyIn, uchar *psIccRespOut, uchar ucMode, uint unTimeoutMs);

/**
 *@brief	安装DUKPT密钥
 *@param	ucGroupIdx		密钥组ID
 *@param	ucSrcKeyIdx		原密钥ID（用来加密初始密钥值的密钥ID）
 *@param	ucKeyLen		密钥长度
 *@param	psKeyValueIn	初始密钥值
 *@param	psKsnIn		    KSN值
 *@param	pstKcvInfoIn	Kcv信息
 *@return
 *@li	NDK_OK		                			  操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		  参数非法
 *@li	\ref NDK_ERR "NDK_ERR" 		              操作失败
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC"    内存空间不足
*/
extern int (*NDK_SecLoadTIK)(uchar ucGroupIdx, uchar ucSrcKeyIdx, uchar ucKeyLen, uchar * psKeyValueIn, uchar * psKsnIn, ST_SEC_KCV_INFO * pstKcvInfoIn);

/**
 *@brief	获取DUKPT值
 *@param	ucGroupIdx		DUKPT密钥组ID
 *@retval	psKsnOut		当前KSN号
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(psKsnOut为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
*/
extern int (*NDK_SecGetDukptKsn)(uchar ucGroupIdx, uchar * psKsnOut);

/**
 *@brief	KSN号增加
 *@param	ucGroupIdx		DUKPT密钥组ID
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
*/
extern int (*NDK_SecIncreaseDukptKsn)(uchar ucGroupIdx);

/**
 *@brief	安装dukpt密钥
 *@param	ucGroupIdx		DUKPT密钥组ID
 *@param	ucSrcKeyType	原密钥类型
 *@param	ucSrcKeyIdx		原密钥ID
 *@param	ucKeyLen		密钥长度
 *@param	psKeyValueIn	初始密钥值
 *@param	psKsnIn		    KSN值
 *@param	pstKcvInfoIn	Kcv信息
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
 *@li	\ref ERR_SECKM_KEY_TYPE "ERR_SECKM_KEY_TYPE" 			密钥类型错误
 *@li	\ref ERR_SECP_GET_CALLER "ERR_SECP_GET_CALLER" 			获取调用者信息错误
*/
extern int (*NDK_SecLoadDukptKey)(uchar ucGroupIdx, uchar ucSrcKeyType, uchar ucSrcKeyIdx, uchar ucKeyLen, uchar * psKeyValueIn, uchar * psKsnIn, ST_SEC_KCV_INFO * pstKcvInfoIn);

/**
 *@brief	获取DUKPT密钥的PIN Block
 *@param	ucGroupIdx		密钥序号
 *@param	pszExpPinLenIn	密码长度，可使用,进行分割，例如：0,4,6
 *@param	psDataIn		按ISO9564要求的输入PIN BLOCK
 *@param	ucMode			计算模式
 *@param	unTimeoutMs		超时时间
 *@retval	psKsnOut		当前KSN号
 *@retval	psPinBlockOut	PIN Block输出,该参数传入NULL时，PIN结果通过\ref NDK_SecGetPinResult "NDK_SecGetPinResult()"函数获取
 *@return
 *@li	NDK_OK				    操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 				参数非法(超时参数非法)
 *@li	\ref NDK_ERR_SECP_PARAM "NDK_ERR_SECP_PARAM" 	参数非法(ucMode非法等)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 	        内存空间不足
 *@li	\ref NDK_ERR "NDK_ERR" 	                		操作失败
*/
extern int (*NDK_SecGetPinDukpt)(uchar ucGroupIdx, uchar *pszExpPinLenIn, uchar * psDataIn, uchar* psKsnOut, uchar *psPinBlockOut, uchar ucMode, uint unTimeoutMs);

/**
 *@brief	计算DUKPT密钥MAC
 *@param	ucGroupIdx		密钥组号
 *@param	psDataIn		输入数据
 *@param	nDataInLen		输入数据长度
 *@param	ucMode			MAC计算模式
 *@retval	psMacOut		MAC值，长度8字节
 *@retval	psKsnOut		当前KSN号
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 	内存空间不足
 *@li	\ref NDK_ERR "NDK_ERR"  				操作失败
*/
extern int (*NDK_SecGetMacDukpt)(uchar ucGroupIdx, uchar *psDataIn, int nDataInLen, uchar *psMacOut, uchar *psKsnOut, uchar ucMode);

/**
 *@brief	计算DES
 *@details 	使用指定密钥进行des计算
 *@param	ucGroupIdx		DUKPT密钥组号
 *@param	ucKeyVarType	密钥类型
 *@param	psIV			初始向量
 *@param	psDataIn		数据信息
 *@param	usDataInLen		数据长度
 *@param	ucMode			加密模式
 *@retval	psDataOut		数据输出信息
 *@retval	psKsnOut		当前KSN号
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 				参数非法
 *@li	\ref NDK_ERR_SECP_PARAM "NDK_ERR_SECP_PARAM" 	参数非法(数据长度不是8的整数倍)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 			内存空间不足
 *@li	\ref NDK_ERR "NDK_ERR" 							操作失败
*/
extern int (*NDK_SecCalcDesDukpt)(uchar ucGroupIdx, uchar ucKeyVarType, uchar *psIV, ushort usDataInLen, uchar *psDataIn,uchar *psDataOut,uchar *psKsnOut ,uchar ucMode);

/**
 *@brief	安装RSA密钥
 *@param	ucRsaKeyIndex	密钥序号
 *@param 	pstRsaKeyIn		RSA密钥信息
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
 *@li	\ref NDK_ERR "NDK_ERR" 		操作失败
*/
extern int (*NDK_SecLoadRsaKey)(uchar ucRsaKeyIndex, ST_SEC_RSA_KEY *pstRsaKeyIn);

/**
 *@brief	RSA密钥对加解密
 *@details	该函数进行RSA加密或解密运算,加密或解密通过选用不同的密钥实现。如(Modul,Exp)选用私有密钥,则进行加密;如选用公开密钥,则进行解密。
 			psDataIn的第一个字节必须小于psModule的第一个字节。 该函数可实现长度不超过2048 bits 的RSA运算。
 			输入的数据开辟的缓冲须是模长度+1。
 *@param	ucRsaKeyIndex	密钥序号
 *@param 	psDataIn		待加密数据,长度和模等长。使用BCD码存储。
 *@param	nDataLen		输入数据长度
 *@retval	psDataOut		输出数据,和模等长，使用BCD码存储。
 *@return
 *@li	NDK_OK				操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"		操作失败
*/
extern int (*NDK_SecRecover)(uchar ucRsaKeyIndex, const uchar *psDataIn, int nDataLen, uchar *psDataOut);
/**
 *@brief	获取键盘输入状态(DUKPT)
 *@retval	psPinBlock		pinblock数据
 *@retval	psKsn			当前DUKPT的KSN值
 *@retval 	nStatus			状态值
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(psPinBlock/psKsn/nStatus为NULL)
 *@li	\ref NDK_ERR_SECVP_NOT_ACTIVE "NDK_ERR_SECVP_NOT_ACTIVE" 	VPP没有激活，第一次调用VPPInit
*/
extern int (*NDK_SecGetPinResultDukpt)(uchar *psPinBlock, uchar *psKsn, int *nStatus);
/**
 *@brief	获取键盘输入状态
 *@retval	psPinBlock		pinblock数据
							在SEC_VPP_KEY_PIN,SEC_VPP_KEY_BACKSPACE,SEC_VPP_KEY_CLEAR这三种状态中，首字节保存已输入PIN的长度
 *@retval 	nStatus			状态值
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 							参数非法(psPinBlock、nStatus为NULL)
 *@li	\ref NDK_ERR_SECP_PARAM "NDK_ERR_SECP_PARAM" 				参数非法
 *@li	\ref NDK_ERR "NDK_ERR"          							操作失败
 *@li	\ref NDK_ERR_SECVP_NOT_ACTIVE "NDK_ERR_SECVP_NOT_ACTIVE" 	VPP没有激活，第一次调用VPPInit
*/
extern int (*NDK_SecGetPinResult)(uchar *psPinBlock, int *nStatus);

/**
 *@brief	初始化PIN输入的触摸屏键盘，输入10个数字键盘的按钮坐标，以及3个功能键(退格／取消／确认)的按键信息。输出返回随机排列后的数字按键值
 *@param	num_btn			10个数字按钮(vpp_button)，每个按钮占8字节，总大小为10*8=80字节。每个按钮由“左上”“右下”两个点组成，一个点由"x""y"2个uint16_t型坐标组成(uint16_t x, uint16_t y)
 *@param					例如第一个按钮为0点开始的16像素(0x10)正方形，那么num_btn[0] = ((0x0000,0x0000),(0x0010, 0x0010))
 *@param	func_key		3个功能按键(退格、取消、确认键，vpp_key[3])，总大小为3*(4+8)=36字节，每个按键组成结构为键值+按钮(int key, vpp_button)，key为4字节int型，取值为K_BASP/K_ENTER/K_ESC三者之一
 *@retval	out_seq			输出存储10字节随机排列的按键值('0'-'9')，与输入参数num_btn[10]一一对应，可用于键盘显示。
 *@return
 *@li	NDK_OK							操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
*/
extern int (*NDK_SecVppTpInit)(uchar *num_btn, uchar *func_key, uchar *out_seq);

/**
 *@brief	设置密钥属主应用名称
 *@details 	仅供系统应用(Keyloader)使用，通过该接口指定后续安装密钥的属主名称。
 *			当安装密钥的时候，系统安全服务将会判断调用者身份，再决定是否采用该函数设置的密钥属主名称：
 *			-针对普通用户程序：
 *				该设置无效，系统安全服务会直接指定安装密钥的属主为当前用户程序
 *			-针对系统应用程序：
 *				判断若是Keyloader系统程序，则安全服务采用\ref NDK_SecSetKeyOwner "NDK_SecSetKeyOwner()"设置的应用名为当前安装密钥的属主，
 *					如果Keyloader未设置过密钥属主，则默认密钥属主指定为Keyloader本身
 *				若非Keyloader系统程序，则直接以当前系统应用为密钥属主
 *@param	pszName			密钥属主应用名称(长度小于256)，若传递的是空字符串，则会清空之前设置的密钥属主
 *@return
 *@li	NDK_OK									操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pszName为NULL或者应用名称长度大于等于256)
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败
*/
extern int (*NDK_SecSetKeyOwner)(char *pszName);

/**
 *@brief	获取安全攻击状态
 *@retval	pnStatus		安全攻击状态参考\ref EM_SEC_TAMPER_STATUS "EM_SEC_TAMPER_STATUS"
 *@return
 *@li	NDK_OK									操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pnStatus为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败
*/
extern int (*NDK_SecGetTamperStatus)(int *pnStatus);


/**
 *@brief 	擦除指定类型密钥, 注：若指定密钥存在则会执行删除，若不存在则直接返回成功:NDK_OK
 *@param 	ucKeyIdx		密钥序号
 *@param 	ucKeyType		密钥类型,该值必须是EM_SEC_KEY_TYPE中定义的类型
 *@return
 *@li NDK_OK				操作成功
 *@li 其它EM_NDK_ERR		操作失败
*/
extern int (*NDK_SecKeyDelete)(uchar ucKeyIdx,uchar ucKeyType);

/**
 *@brief	用于判断安全是否触发
 *@param	
 *@return
 *@li	返回POS安全状态
 *@li	
 *@li	
*/
extern int (*NDK_GetTamperStatus)();

/**
 *@brief	用于获取安全寄存器值
 *@param	pnVal		安全寄存器值
 *@return
 *@li	NDK_OK				操作成功
 *@li	NDK_ERR_PARA		参数非法
 *@li	
*/
extern int (*NDK_SecGetDrySR)(int *pnVal);

/**
 *@brief	用于清安全寄存器值
 *@param	
 *@return
 *@li	NDK_OK				操作成功
*/
extern int (*NDK_SecClear)(void);

/**
 *@brief	用于删除1~200索引的密钥（只能删共享密钥表）
 *@param	
 *@return
 *@li	NDK_OK				操作成功
*/
extern int (*NDK_SecUserKeyDelete)(void);

/**
 *@brief	获取密钥属主应用名称
 *@param	nLenOfOwnerBuffer			buff长度
 *@retval	*pszOwner					密钥属主名称
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref ERR_SECP_GET_CALLER "ERR_SECP_GET_CALLER" 		获取调用者信息错误
 *@li	\ref NDK_ERR "NDK_ERR" 								操作失败
*/
extern int (*NDK_SecGetKeyOwner)(int nLenOfOwnerBuffer,char *pszOwner);

/**
* @brief	生成用于校验KLD的随机数
* @param	nLenRandom	随机数长度（24字节）
* @retval	psRandom	生成随机数
* @return
* @li NDK_OK		成功生成随机数
* @li NDK_ERR_SECKLA_INVALIDPARAM	psRandom is NULL
* @li NDK_ERR_SECKLA_ERR_INTERNAL	生成随机数失败
*/
extern int (*NDK_SecKlaGenNonce)(int nLenRandom,  uchar* psRandom);

/**
* @brief	验证KLD证书、签名，传递用PKLD_ENC加密的会话密钥。
* @param	[in]	nLenAuthData	Length of psAuthData
* @param	[in]	psAuthData		nonce signed by PKLD_ENC
* @param	[in]	nLenAuthCert	Length of PKLD_AUTH certification
* @param	[in]	psAuthCert		PKLD_AUTH certification
* @param	[in]	nLenEncCert		Length of PKLD_ENC certification
* @param	[in]	psEncCert		PKLD_ENC certification
* @retval	[out]	pnLenSsKeyCy	Length of session key cipher-text
* @retval	[out]	psSsKeyCyTxt	cipher-text session key encrypted by PKLD_ENC
* @return
* @li NDK_OK 							succ
* @li NDK_ERR_SECKLA_ERR_INTERNAL		fail
* @li NDK_ERR_SECKLA_INVALIDPARAM		Invalid parameter passed to function
* @li NDK_ERR_SECKLA_ERR_INVALID_CRT	Invalid certification
* @li NDK_ERR_SECKLA_ERR_INVALID_SIG	Invalid nonce signature
*/
int (*NDK_SecKlaMKLDAuth)(int nLenAuthData, uchar * psAuthData, int nLenAuthCert, uchar * psAuthCert, int nLenEncCert, uchar * psEncCert, int * pnLenSsKeyCyTxt, uchar * psSsKeyCyTxt);

int (*NDK_SecKlaMKLDAuthV2)(int nLenAuthData, uchar *psAuthData, int nLenAuthCert, uchar *psAuthCert, int nLenEncCert, uchar *psEncCert, int *pnLenSsKeyCyTxt, uchar *psSsKeyCyTxt, uchar *keyowner, int *ownerlen);

/**
 *@brief	由SP100完成PIN输入，并返回加密的PIN BLOCK。上位机实现PIN BLOCK的转加密处理（由安全模块解密出明文PIN，再封装后实现脱机明文、密文PIN验证）。上位机（CPOS）与SP100出厂需要预装配对的KEK(TMK)。
 *@param	ucKeyID			密钥ID，当nTSKLen长度为0，表示PIN密钥ID，当nTSKLen不为0，表示TMK密钥ID
 *@param	ucSKType		会话密钥算法类型 0 - DES, 1 - AES
 *@param	nTSKLen			PIN加密密钥的实际长度
 *@param	* psTSK			PIN加密密钥密文，是加密算法块的整数倍
 *@param	*psPan			随机PAN字符串，由应用程序每次随机生成
 *@param	PinBlockLen		密文PIN Block长度
 *@param	*psPinBlockIn	密文PIN Block数据，当会话密钥算法为DES，PINBLOCK格式为ISO9564_0，若算法为AES，格式为ISO9564_4
 *@param	*pstRsaPinKeyIn	脱机PIN加密的卡片公钥，若为脱机明文PIN验证时可为NULL
 *@param	ucMode			脱机PIN验证方式： 0 - 脱机明文PIN， 1 - 脱机密文PIN
 *@retval	*psIccRespOut	卡片应答码
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 								操作失败
*/
int (*NDK_SecVerifyPIN)(uchar ucKeyID, uchar ucSKType, int nTSKLen, uchar* psTSK, uchar *psPan, int PinBlockLen,uchar *psPinBlockIn,ST_SEC_RSA_KEY *pstRsaPinKeyIn,uchar *psIccRespOut, uchar ucMode);

/**
*@brief   CBC模式加解密计算
*@details    使用指定密钥进行CBC加解密计算，注意：1~255序号进行加解密
*@param     ucKeyType            密钥类型
*@param     ucKeyIdx            密钥序号
*@param     psIv,               初始向量
*@param     nIvLen               初始向量长度
*@param     psDataIn            数据信息
*@param     nDataInLen           数据长度
*@param     ucMode                加密模式 参考\ref EM_SEC_DES "EM_SEC_DES"
*@retval      psDataOut            数据输出信息 *@return
*@li      NDK_OK               操作成功
*@li      \ref NDK_ERR_PARA "NDK_ERR_PARA"            参数非法
*@li      \ref NDK_ERR "NDK_ERR"           操作失败
*@li      \ref NDK_ERR_SECP_PARAM "NDK_ERR_SECP_PARAM" 
           参数非法(数据长度不是对应类型所需长度的整数倍和初始向量不是对应类型所需长度)
*@li      \ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC"    内存空间不足
*/
int (*NDK_SecCalcDesCBC)(uchar ucKeyType, uchar ucKeyIdx, uchar *psIv, int nIvLen, uchar * psDataIn, int nDataInLen, uchar *psDataOut, uchar ucMode);

/**
 *@brief    设置安全配置
 *@details  1、用户一旦通过此函数设置了安全配置信息，则后续操作根据此设置的配置信息进行控制。
            2、每个应用拥有自己的配置，不会影响到其他应用的安全配置。
 *@param    cfg       配置信息，按比特位进行设置，bit0保留暂不使用
 *			比特位置1 表示开启限制， 置0表示关闭限制，具体定义如下
			bit[1] - 判断密钥唯一性 
			bit[2] - 判断密钥专用性 
			bit[3] - 判断敏感次数限制 
			bit[4] - 判断密钥保护强度
			bit[5] - 判断密钥长度限制
			bit[6] - 判断DPA防护限制
			bit[7] - 判断DUKPT密钥强制派生限制
 *@return
 *@li   NDK_OK              操作成功
 *@li  其它                操作失败
*/
int NDK_SecSetCfg(uint32_t cfg);
/**
 *@brief    读取安全配置
 *@retval   cfg      配置信息
 *@return
 *@li   NDK_OK              操作成功
 *@li   NDK_ERR_PARA        参数非法(cfg)
*/
int NDK_SecGetCfg(uint32_t *cfg);

int (*NDK_OnlineActiveIsICReady*)(void);
int (*NDK_OnlineActiveVerifyICPwd)(uchar* psICPwd);
int (*NDK_OnlineActiveAuthServer)(int nLenServerCert, uchar* psServerCert, int* pnLenSsKey1CyTxt, uchar* psSsKey1CyTxt, int* pnLenIcCert, uchar* psIcCert);
int (*NDK_OnlineActiveGetSkMac)(int nLenSsKey2CyTxt, uchar* psSsKey2CyTxt, int* pnMacLen, uchar * psMacData);
int (*NDK_OnlineActiveRequestAuth)(int nMacLen, uchar * psMacData, int* pnAuthDataLen, uchar* psAuthData);
int (*NDK_OnlineActiveDone)(int nAuthResultLen, uchar * psAuthResult);

int (*NDK_SecInit)();
int (*NDK_SysSetShutdownDelay)(uint unSeconds);

/** @} */ // 安全模块结束

/** @addtogroup 串口通讯
* @{
*/


/**
 *@brief  串口选择
*/
typedef enum {
    PORT_NUM_COM1 = 0,		/**<串口1*/
    PORT_NUM_COM2 = 1,		/**<串口2*/
    PORT_NUM_WIRELESS = 2,	/**<无线模块*/
    PORT_NUM_MUX1 = 3,		/**<多路复用1*/
    PORT_NUM_MUX2 = 4,		/**<多路复用2*/
    PORT_NUM_MUX3 = 5,		/**<多路复用3*/
    PORT_NUM_MODEM = 6,		/**<有线模块*/
    PORT_NUM_WIFI = 7,		/**<Wifi模块*/
    PORT_NUM_USB = 8,		/**<USB模块*/
    PORT_NUM_SCAN = 9,  	/**<扫描模块*/
    PORT_NUM_BT = 10,    	/**<蓝牙模块*/
    PORT_NUM_AUDIO = 11,	/**<音频模块*/
    PORT_NUM_CCID = 12, 	/**<CCID模块*/
    PORT_NUM
} EM_PORT_NUM;


/**
 *@brief	初始化串口，对串口波特率，数据位、奇偶位、停止位等进行设置。建议每次使用串口之前先调用该初始化函数。(USB是不需要波特率，但调用函数时还是要传一个，否则会报参数错误)\n
			 		支持的波特率分别为{300,1200,2400,4800,9600,19200,38400,57600,115200}\n
			 		支持的数据位分别为{8,7,6,5}\n
			 		校验方式选择分别为{N(n):无校验;O(o):奇校验;E(e):偶校验}\n
			 		支持的停止位分别为{1,2}
 *@param	emPort	指定的串口
 *@param	pszAttr	通讯率和格式串,例"115200,8,N,1",如果只写波特率则缺省为"8,N,1"。
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(pszAttr为NULL、emPort串口类型非法)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败
 *@li	\ref NDK_ERR "NDK_ERR" 						操作失败(获取与aux_fd相关的参数失败等)
*/
extern int (*NDK_PortOpen)(EM_PORT_NUM emPort, const char *pszAttr);

/**
 *@brief	关闭串口
 *@param	emPort	指定的串口
 *@return
 *@li	NDK_OK		操作成功
 *@li	\ref NDK_ERR "NDK_ERR"  			操作失败
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 	参数非法(emPort串口类型非法)
*/
extern int (*NDK_PortClose)(EM_PORT_NUM emPort);

/**
 *@brief	在设定超时时间里从指定的串口，读取指定长度的数据，存放于pszOutbuf
 *@param	emPort		指定的串口
 *@param	unLen		表示要读的数据长度,>0(小于4K)
 *@param	nTimeoutMs	等待时间，单位为毫秒
 *@retval	pszOutBuf	接收数据缓冲区的头指针
 *@retval	pnReadLen	返回读的实际长度
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pszOutbuf\pnReadlen为NULL、emPort串口类型非法、unLen数据长度非法、nTimeoutMs超时时间参数非法)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	打开设备文件错误(设备未打开或打开失败) 
 *@li	\ref NDK_ERR_USB_LINE_UNCONNECT "NDK_ERR_USB_LINE_UNCONNECT" 		USB线未插
 *@li	\ref NDK_ERR_READ "NDK_ERR_READ" 				读文件失败
 *@li	\ref NDK_ERR_TIMEOUT "NDK_ERR_TIMEOUT" 		超时错误(串口读超时)
 *@li	\ref NDK_ERR "NDK_ERR" 				操作失败
*/
extern int (*NDK_PortRead)(EM_PORT_NUM emPort, uint unLen, char *pszOutBuf,int nTimeoutMs, int *pnReadLen);

/**
 *@brief	给指定的串口送指定长度的数据
 *@param	emPort		指定的串口
 *@param	unLen		表示要写的数据长度
 *@param	pszInbuf	数据发送的缓冲区
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pszInbuf为NULL、emPort串口类型非法)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	打开设备文件错误(设备未打开或打开失败) 
 *@li	\ref NDK_ERR_USB_LINE_UNCONNECT "NDK_ERR_USB_LINE_UNCONNECT" 		USB线未插
 *@li	\ref NDK_ERR_WRITE "NDK_ERR_WRITE" 				写文件失败
 *@li	\ref NDK_ERR "NDK_ERR" 				操作失败
*/
extern int (*NDK_PortWrite)(EM_PORT_NUM emPort, uint unLen,const char *pszInbuf);

/**
 *@brief	判断指定串口发送缓冲区是否为空
 *@param	emPort	指定的串口
 *@return
 *@li	NDK_OK					缓冲区无数据
 *@li	\ref NDK_ERR "NDK_ERR" 	缓冲区有数据
*/
extern int (*NDK_PortTxSendOver)(EM_PORT_NUM emPort);

/**
 *@brief	清除指定串口的接收缓冲区
 *@param	emPort	指定的串口
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(emPort串口类型非法)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	打开设备文件错误(设备未打开或打开失败) 
 *@li	\ref NDK_ERR "NDK_ERR" 				操作失败
*/
extern int (*NDK_PortClrBuf)(EM_PORT_NUM emPort);

/**
 *@brief	取缓冲区里有多少字节要被读取(一次未取到预期的数据长度，配合\ref NDK_PortRead "NDK_PortRead()"函数进行多次获取，将每次获取的长度累加)
 *@param	emPort	指定的串口
 *@retval	pnReadLen	返回缓冲区被读取的长度
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(pnReadlen为NULL、emPort串口类型非法)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	打开设备文件错误(设备未打开或打开失败) 
 *@li	\ref NDK_ERR "NDK_ERR" 				操作失败
*/
extern int (*NDK_PortReadLen)(EM_PORT_NUM emPort,int *pnReadLen);

/** @} */ // 串口通讯模块结束

/** @addtogroup 磁卡
* @{
*/

/**
 *@brief	打开磁卡设备
 *@param	无
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 						操作失败(mag设备节点已打开)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	打开设备文件错误(打开磁卡设备文件错误)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(磁卡驱动接口调用失败返回)
*/
extern int (*NDK_MagOpen)(void);

/**
 *@brief	关闭磁卡设备
 *@param	无
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败(磁卡设备未打开、调用close()失败返回、驱动调用失败)
*/
extern int (*NDK_MagClose)(void);

/**
 *@brief	复位磁头
 *@details	 复位磁头且清除磁卡缓冲区数据
 *@param	无
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败(磁卡未打开)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(磁卡驱动接口MAG_IOCS_RESET调用失败返回)
*/
extern int (*NDK_MagReset)(void);

/**
 *@brief	判断是否刷过卡
 *@retval	psSwiped	1----已刷卡    0-----未刷卡
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(psSwiped非法)
 *@li	\ref NDK_ERR "NDK_ERR" 				操作失败(磁卡设备未打开)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(磁卡驱动接口MAG_IOCG_SWIPED调用失败返回)
*/
extern int (*NDK_MagSwiped)(uchar * psSwiped);

/**
 *@brief	读取磁卡缓冲区的1、2、3磁道的数据
 *@details	与\ref MagSwiped "MagSwiped()"函数配合使用。如果不需要某磁道数据,可以将该磁道对应的指针置为NULL,这时将不会输出该磁道的数据
 *@retval	pszTk1	磁道1
 *@retval	pszTk2	磁道2
 *@retval	pszTk3	磁道3
 *@retval	pnErrorCode	磁卡错误代码
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败(磁卡设备未打开)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(磁卡驱动接口调用失败返回)
 *@li	\ref NDK_ERR_NOSWIPED "NDK_ERR_NOSWIPED" 	无磁卡刷卡记录
*/
extern int (*NDK_MagReadNormal)(char *pszTk1, char *pszTk2, char *pszTk3, int *pnErrorCode);

/**
 *@brief	读取磁卡缓冲区的1、2、3磁道的原始数据
 *@details	与\ref MagSwiped "MagSwiped()"函数配合使用,如果不需要某磁道数据,可以将该磁道对应的指针置为NULL,这时将不会输出该磁道的数据
 *@retval	pszTk1	磁道1
 *@retval	pusTk1Len	磁道1数据长度
 *@retval	pszTk2	磁道2
 *@retval	pusTk2Len	磁道2数据长度
 *@retval	pszTk3	磁道3
 *@retval	pusTk3Len	磁道3数据长度
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pszTk2/pszTk3/pszTk1为NULL、长度为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 					操作失败(磁卡设备未打开)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(磁卡驱动接口调用失败返回)
*/
extern int (*NDK_MagReadRaw)(uchar *pszTk1, ushort* pusTk1Len, uchar *pszTk2, ushort* pusTk2Len,uchar *pszTk3, ushort* pusTk3Len );

/** @} */ // 磁卡模块结束

/** @addtogroup IC卡
* @{
*/

typedef enum{
  ICTYPE_IC,  		/**<接触式IC卡*/
  ICTYPE_SAM1, 		/**<SAM1卡*/
  ICTYPE_SAM2, 		/**<SAM2卡*/
  ICTYPE_SAM3, 		/**<SAM3卡*/
  ICTYPE_SAM4, 		/**<SAM4卡*/
  ICTYPE_M_1, 		/**<at24c01 at24c02 at24c04 at24c08 at24c16 at24c32 at24c64 */
  ICTYPE_M_2, 		/**<sle44x2*/
  ICTYPE_M_3, 		/**<sle44x8*/
  ICTYPE_M_4, 		/**<at88sc102*/
  ICTYPE_M_5, 		/**<at88sc1604*/
  ICTYPE_M_6, 		/**<at88sc1608*/
  ICTYPE_ISO7816, 	/**<ISO7816 standard*/
  ICTYPE_M_7, 		/**<at88sc153*/
  ICTYPE_M_1_1,		/*<at24c01 */
  ICTYPE_M_1_2,		/*<at24c02 */
  ICTYPE_M_1_4,		/*<at24c04 */
  ICTYPE_M_1_8,		/*<at24c08 */
  ICTYPE_M_1_16,	/*<at24c16 */
  ICTYPE_M_1_32,	/*<at24c32 */
  ICTYPE_M_1_64,	/*<at24c64 */
  ICTYPE_M_1_128,	/*<at24c128 */
  ICTYPE_M_1_256,	/*<at24c256 */
}EM_ICTYPE;

typedef enum{
	SCCFG_PWRMODE = 1,			// power up mode
	SCCFG_PWRVOL = 2,			// power up voltage(now, only IC card is valid)
}EM_CFGTYPE;


/**
 *@brief	获取驱动程序版本号
 *@retval 	pszVersion   　　驱动程序版本号,要求缓冲大小不低于16字节　 
 *@return 	
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(pszVersion为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	打开设备文件错误(打开IC卡设备文件失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(IC驱动接口ioctl_getvision调用失败返回)
*/
extern int  (*NDK_IccGetVersion)(char *pszVersion);

/**
 *@brief	获取卡片状态
 *@retval 	pnSta   bit0：如果IC卡1已插卡，为“1”，否则，为“0”\n
 *					bit1：如果IC卡1已上电，为“1”，否则，为“0”\n
 *                  bit2：保留，返回值“0”\n
 *                  bit3：保留，返回值“0”\n
 *					bit4：如果SAM卡1已上电，为“1”，否则，为“0”\n
 *					bit5：如果SAM卡2已上电，为“1”，否则，为“0”\n
 *					bit6：如果SAM卡3已上电，为“1”，否则，为“0”\n
 *					bit7：如果SAM卡4已上电，为“1”，否则，为“0”\n
 *　
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数错误(pnSta为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	打开设备文件错误(打开IC卡设备文件失败)
*/
extern int (*NDK_IccDetect)(int *pnSta);


/**
 *@brief	上电
 *@param	emIcType 	卡类型(参考\ref EM_ICTYPE "EM_ICTYPE")
 *@retval 　psAtrBuf  	表示ATR数据
 *@retval   pnAtrLen  	表示ATR数据的长度
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(psAtrBuf/pnAtrLen为NULL、emIcType非法)
 *@li	\ref NDK_ERR "NDK_ERR" 						操作失败(MEMORY卡检测失败)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(IC卡设备文件打开失败)
 *@li	\ref NDK_ERR_ICC_CARDNOREADY_ERR "NDK_ERR_ICC_CARDNOREADY_ERR" 	卡未准备好
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误
*/
extern int (*NDK_IccPowerUp) (EM_ICTYPE emIcType, uchar *psAtrBuf,int *pnAtrLen);

/**
 *@brief	下电
 *@param	emIcType 	卡类型(参考\ref EM_ICTYPE "EM_ICTYPE")
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(emIcType非法)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	打开设备文件错误(打开IC卡设备文件失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误
*/
extern int (*NDK_IccPowerDown)(EM_ICTYPE emIcType);

/**
 *@brief	IC卡操作
 *@param	emIcType	卡类型(参考\ref EM_ICTYPE "EM_ICTYPE")
 *@param	nSendLen	发送数据长度
 *@param	psSendBuf	发送的数据
 *@retval 	pnRecvLen   接收数据长度
 *@retval 	psRecvBuf   接收的数据
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(psSendBuf/pnRecvLen/psRecvBuf为NULL、nSendLen小于0、emIcType非法)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	打开设备文件错误(打开IC卡设备文件失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误
*/
extern int (*NDK_Iccrw)(EM_ICTYPE emIcType, int nSendLen,  uchar *psSendBuf, int *pnRecvLen,  uchar *psRecvBuf);

/**
 *@brief	设置IC卡上电模式
 *@param	pnMode		IC卡标准类型
 *@param	pnVoltage	IC卡GPIO值
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误
*/
extern int (*NDK_IccSetPowerUpMode)(int pnMode, int pnVoltage);

/**
 *@brief	获取IC协议
 *@param	emIctype		IC卡类型
 *@param	pnProtocol		IC卡协议
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误
*/
extern int (*NDK_IccGetProtocol)(EM_ICTYPE emIctype, int *pnProtocol);

/**
 *@brief	设置IC和SAM卡的上电电压、上电模式
 *@param	emIctype 	卡类型(参考\ref EM_ICTYPE "EM_ICTYPE")
 *@param	emCfgtype   配置类型(参考\ref EM_CFGTYPE "EM_CFGTYPE")
 *@param	cfgValue	配置参数，与emCfgtype参数配合使用
 	 	 	当emCfgtype == SCCFG_PWRMODE 时，cfgValue 参考  EM_PWR_MODE
			当emCfgtype == SCCFG_PWRVOL  时，cfgValue 参考  EM_PWR_VOL
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 	参数非法
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 	驱动调用错误
*/
extern int (*NDK_IccSetConfig)(EM_ICTYPE emIctype, EM_CFGTYPE emCfgtype, unsigned int cfgValue);


/** @} */ // IC卡模块结束

/** @addtogroup 射频卡
* @{
*/
typedef enum {
	RFID_Autoscan=		0x00,
	RFID_RC531=			0x01,
	RFID_PN512=			0x02,
	RFID_No=			0xFF,
}EM_RFID;

typedef struct {
    unsigned char systemcode[2];
    unsigned char request_code;
    unsigned char timeslot;
}felica_param_t;

/**
 *@brief	获取射频驱动版本号
 *@retval	pszVersion	返回的驱动版本号字符串
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(pszVersion为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(射频驱动接口RFID_IOCG_GETVISION调用失败返回)
*/
extern int  (*NDK_RfidVersion)(uchar *pszVersion);


/**
 *@brief	射频接口器件初始化，可用于判断器件是否装配或可工作
 *@retval 	psStatus  扩充备用
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(pszVersion为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(射频驱动接口RFID_IOCG_INIT调用失败返回)
 *@li	\ref NDK_ERR_RFID_INITSTA "NDK_ERR_RFID_INITSTA" 	非接触卡-射频接口器件故障或者未配置
*/
extern int (*NDK_RfidInit)(uchar *psStatus);


/**
 *@brief	开射频输出
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(射频驱动接口RFID_IOCG_RFOPEN调用失败返回)
*/
extern int (*NDK_RfidOpenRf)(void);


/**
 *@brief	关闭射频输出，可降低功耗并保护射频器件。
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(射频驱动接口RFID_IOCG_RFCLOSE调用失败返回)
*/
extern int (*NDK_RfidCloseRf)(void);


/**
 *@brief	获取卡片状态(是否已上电，用于判断是否可休眠)
 *@return
 *@li	NDK_OK			操作成功(已上电成功，忙状态)
 *@li	\ref NDK_ERR_RFID_NOTACTIV "NDK_ERR_RFID_NOTACTIV" 	非接触卡-未激活(未上电成功，非忙状态)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 			设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 					驱动调用错误(射频驱动接口RFID_IOCG_WORKSTATUS调用失败返回)
*/
extern int (*NDK_RfidPiccState)(void);

/**
 *@brief    设置超时控制
 *@param　　ucTimeOutCtl 值０是不执行，值不为０是执行
 *@retval
 *@return
 *@li   NDK_OK          操作成功
 *@li   \ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"    设备文件打开失败(射频设备文件打开失败)
 *@li   \ref NDK_ERR_IOCTL "NDK_ERR_IOCTL"      驱动调用错误(射频驱动接口RFID_IOCG_TIMEOUTCTL调用失败返回)
*/
extern int (*NDK_RfidTimeOutCtl)(uchar ucTimeOutCtl);

/**
 *@brief	设备强制休眠
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(射频驱动接口RFID_IOCG_SUSPEND调用失败返回)
*/
extern int (*NDK_RfidSuspend)(void);


/**
 *@brief	设备唤醒
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_RESUME调用失败返回)
*/
extern int (*NDK_RfidResume)(void);


/**
 *@brief	设置寻卡策略(寻卡操作前设置一次即可，M1卡操作时需要设置成TYPE-A卡模式)
 *@param	ucPiccType = 0xcc，表示寻卡时只针对TYPE-A的卡.
 *			   0xcb，表示寻卡时只针对TYPE-B的卡.
 *			   0xcd，表示寻卡时针对TYPE-A及TYPE-B的卡.
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(ucPiccType非法)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(射频驱动接口RFID_IOCG_SETPICCTYPE调用失败返回)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 		非接触卡-无卡,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 			非接触卡-CRC错,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 			非接触卡-非空,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 		非接触卡-认证错,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	非接触卡-奇偶错,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 		非接触卡-接收代码错			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 		非接触卡-防冲突数据校验错	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 			非接触卡-认证KEY错			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 		非接触卡-未认证				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	非接触卡-接收BIT错			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	非接触卡-接收字节错			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 		非接触卡-FIFO写错误			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 		非接触卡-传送操作错误		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 		非接触卡-写操作错误			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 		非接触卡-增量操作错误		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 		非接触卡-减量操作错误		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 		非接触卡-溢出错误			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 	非接触卡-帧错				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 		非接触卡-冲突				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 		非接触卡-复位接口读写错		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	非接触卡-接收超时			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 		非接触卡-协议错				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 						非接触卡-异常终止			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 					非接触卡-PPS操作错			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 			非接触卡-申请SPI失败		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	非接触卡-无法确认的错误状态	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 		非接触卡-卡类型错			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	非接触卡-IOCTL参数错		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 						非接触卡-内部参数错			0xa9
*/
extern int (*NDK_RfidPiccType)(uchar ucPiccType);

/**
 *@brief	射频卡寻卡(探测刷卡区域是否有卡)
 *@retval 	psPiccType 	激活的卡类型 0xcc=TYPE-A卡，0xcb=TYPE-B卡(该参数无效，传NULL进行调用).
 *@return
 *@li	NDK_OK			操作成功（寻卡成功）
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(射频驱动接口RFID_IOCG_PICCDEDECT调用失败返回)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 		非接触卡-无卡,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 			非接触卡-CRC错,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 			非接触卡-非空,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 		非接触卡-认证错,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	非接触卡-奇偶错,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 		非接触卡-接收代码错			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 		非接触卡-防冲突数据校验错	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 			非接触卡-认证KEY错			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	非接触卡-未认证				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	非接触卡-接收BIT错			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	非接触卡-接收字节错			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 		非接触卡-FIFO写错误			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 			非接触卡-传送操作错误		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 			非接触卡-写操作错误			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 			非接触卡-增量操作错误		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 			非接触卡-减量操作错误		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 			非接触卡-溢出错误			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 		非接触卡-帧错				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 			非接触卡-冲突				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	非接触卡-复位接口读写错		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	非接触卡-接收超时			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 		非接触卡-协议错				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 						非接触卡-异常终止			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 					非接触卡-PPS操作错			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 			非接触卡-申请SPI失败		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	非接触卡-无法确认的错误状态	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 		非接触卡-卡类型错			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	非接触卡-IOCTL参数错		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 						非接触卡-内部参数错			0xa9
*/
extern int (*NDK_RfidPiccDetect)(uchar *psPiccType);

/**
 *@brief	射频卡激活(已探测有卡的情况下),相当于powerup , 按改进的流程（原先生产版本）。
 *@retval 	psPiccType	激活的卡类型 0xcc=TYPE-A卡，0xcb=TYPE-B卡
 *@retval	pnDataLen	数据长度
 *@retval	psDataBuf	数据缓冲区(A卡为UID；B卡psDataBuf[1]~[4]为UID，其它是应用及协议信息)
 *@return
 *@li	NDK_OK			激活成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(psPiccType/pnDataLen/psDataBuf为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(射频驱动接口RFID_IOCG_PICCACTIVATE调用失败返回)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 		非接触卡-无卡,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 			非接触卡-CRC错,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 			非接触卡-非空,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 		非接触卡-认证错,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	非接触卡-奇偶错,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 		非接触卡-接收代码错			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 		非接触卡-防冲突数据校验错	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 			非接触卡-认证KEY错			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	非接触卡-未认证				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	非接触卡-接收BIT错			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	非接触卡-接收字节错			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 		非接触卡-FIFO写错误			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 			非接触卡-传送操作错误		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 			非接触卡-写操作错误			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 			非接触卡-增量操作错误		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 			非接触卡-减量操作错误		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 			非接触卡-溢出错误			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 		非接触卡-帧错				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 			非接触卡-冲突				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	非接触卡-复位接口读写错		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	非接触卡-接收超时			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 		非接触卡-协议错				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 						非接触卡-异常终止			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 					非接触卡-PPS操作错			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 			非接触卡-申请SPI失败		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	非接触卡-无法确认的错误状态	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 		非接触卡-卡类型错			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	非接触卡-IOCTL参数错		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 						非接触卡-内部参数错			0xa9
*/
extern int (*NDK_RfidPiccActivate)(uchar *psPiccType, int *pnDataLen,  uchar *psDataBuf);

/**
 *@brief	关闭射频使卡失效。读写操作结束后应该执行该操作，相当于powerdown 。
 *@param	ucDelayMs	等0则一直关闭RF;不等0则关闭ucDelayMs毫秒后再打开RF。
                      关闭6～10ms可使卡失效。如果后续没有连续的读卡操作，应该置0以关闭RF 。
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(射频驱动接口ioctl_PiccDeselect调用失败返回)
*/
extern int (*NDK_RfidPiccDeactivate)(uchar ucDelayMs);


/**
 *@brief	射频卡APDU交互,即读写卡过程(已激活的情况下))
 *@param	nSendLen		发送的命令长度
 *@param	psSendBuf		发送命令缓冲区
 *@retval 	pnRecvLen	接收数据长度
 *@retval	psReceBuf	接收数据缓冲区
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(psSendBuf/pnRecvLen/psReceBuf为NULL、nSendLen小于5)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(射频驱动接口RFID_IOCG_PICCAPDU调用失败返回)
*/
extern int (*NDK_RfidPiccApdu)(int nSendLen, uchar *psSendBuf, int *pnRecvLen,  uchar *psReceBuf);


/**
 *@brief	M1寻卡(寻卡类型必须已经设置为TYPE-A)
 *@param	ucReqCode		0=请求REQA, 非0=唤醒WUPA, 一般情况下需要使用WUPA
 *@retval 	pnDataLen	 接收数据长度(2字节)
 *@retval	psDataBuf	接收数据缓冲区
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(pnDataLen/psDataBuf为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(射频驱动接口RFID_IOCG_M1REQUEST调用失败返回)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 		非接触卡-无卡,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 			非接触卡-CRC错,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 			非接触卡-非空,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 		非接触卡-认证错,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	非接触卡-奇偶错,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 		非接触卡-接收代码错			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 		非接触卡-防冲突数据校验错	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 			非接触卡-认证KEY错			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	非接触卡-未认证				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	非接触卡-接收BIT错			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	非接触卡-接收字节错			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 		非接触卡-FIFO写错误			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 			非接触卡-传送操作错误		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 			非接触卡-写操作错误			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 			非接触卡-增量操作错误		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 			非接触卡-减量操作错误		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 			非接触卡-溢出错误			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 		非接触卡-帧错				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 			非接触卡-冲突				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	非接触卡-复位接口读写错		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	非接触卡-接收超时			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 		非接触卡-协议错				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 						非接触卡-异常终止			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 					非接触卡-PPS操作错			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 			非接触卡-申请SPI失败		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	非接触卡-无法确认的错误状态	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 		非接触卡-卡类型错			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	非接触卡-IOCTL参数错		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 						非接触卡-内部参数错			0xa9
*/
extern int (*NDK_M1Request)(uchar ucReqCode, int *pnDataLen, uchar *psDataBuf);


/**
 *@brief	M1卡防冲突(NDK_M1Request有卡的情况下)
 *@retval 	pnDataLen	接收数据长度(UID长度)
 *@retval	psDataBuf	接收数据缓冲区(UID)
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(pnDataLen/psDataBuf为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(射频驱动接口RFID_IOCG_M1ANTI调用失败返回)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 		非接触卡-无卡,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 			非接触卡-CRC错,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 			非接触卡-非空,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 		非接触卡-认证错,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	非接触卡-奇偶错,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 		非接触卡-接收代码错			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 		非接触卡-防冲突数据校验错	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 			非接触卡-认证KEY错			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	非接触卡-未认证				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	非接触卡-接收BIT错			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	非接触卡-接收字节错			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 		非接触卡-FIFO写错误			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 			非接触卡-传送操作错误		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 			非接触卡-写操作错误			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 			非接触卡-增量操作错误		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 			非接触卡-减量操作错误		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 			非接触卡-溢出错误			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 		非接触卡-帧错				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 			非接触卡-冲突				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	非接触卡-复位接口读写错		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	非接触卡-接收超时			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 		非接触卡-协议错				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 						非接触卡-异常终止			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 					非接触卡-PPS操作错			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 			非接触卡-申请SPI失败		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	非接触卡-无法确认的错误状态	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 		非接触卡-卡类型错			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	非接触卡-IOCTL参数错		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 						非接触卡-内部参数错			0xa9
*/
extern int (*NDK_M1Anti)(int *pnDataLen, uchar *psDataBuf);


/**
 *@brief	M1卡防冲突(NDK_M1Request有卡的情况下),针对多级级联的UID .
 *@param	ucSelCode	PICC_ANTICOLL1/PICC_ANTICOLL2/PICC_ANTICOLL3
 *@retval 	pnDataLen	接收数据长度(UID长度)
 *@retval	psDataBuf	接收数据缓冲区(UID)
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(pnDataLen/psDataBuf为NULL、ucSelcode非法)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(射频驱动接口RFID_IOCG_M1ANTI调用失败返回)
*/
extern int  (*NDK_M1Anti_SEL)(uchar ucSelCode, int *pnDataLen, uchar *psDataBuf);


/**
*@brief		M1卡选卡(NDK_M1Anti成功的情况下)
*@param		nUidLen			uid长度
*@param		psUidBuf	    uid数据缓冲区
*@retval 	psSakBuf	    选卡返回数据(1字节SAK)
*@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(psUidBuf/psSakBuf为NULL、nUidLen不等于4)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(射频驱动接口RFID_IOCG_M1SELECT调用失败返回)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	非接触卡-无卡,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 		非接触卡-CRC错,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 		非接触卡-非空,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	非接触卡-认证错,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	非接触卡-奇偶错,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 		非接触卡-接收代码错			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 		非接触卡-防冲突数据校验错	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 			非接触卡-认证KEY错			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	非接触卡-未认证				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	非接触卡-接收BIT错			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	非接触卡-接收字节错			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 		非接触卡-FIFO写错误			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 	非接触卡-传送操作错误		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 	非接触卡-写操作错误			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 	非接触卡-增量操作错误		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 	非接触卡-减量操作错误		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 	非接触卡-溢出错误			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 		非接触卡-帧错				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 			非接触卡-冲突				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	非接触卡-复位接口读写错		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	非接触卡-接收超时			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 	非接触卡-协议错				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 					非接触卡-异常终止			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 				非接触卡-PPS操作错			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 		非接触卡-申请SPI失败		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	非接触卡-无法确认的错误状态	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 		非接触卡-卡类型错			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	非接触卡-IOCTL参数错		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 						非接触卡-内部参数错			0xa9
*/
extern int (*NDK_M1Select)(int nUidLen, uchar *psUidBuf, uchar *psSakBuf);


/**
 *@brief	M1卡选卡(NDK_M1Anti成功的情况下),针对多级级联的UID .
 *@param	ucSelCode	PICC_ANTICOLL1/PICC_ANTICOLL2/PICC_ANTICOLL3
 *@param	nUidLen		uid长度
 *@param	psUidBuf	uid数据缓冲区
 *@retval	psSakBuf	选卡返回数据(1字节SAK)
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(psUidBuf/psSakBuf为NULL、nUidLen不等于4、ucSelcode非法)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(射频驱动接口RFID_IOCG_M1SELECT调用失败返回)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	非接触卡-无卡,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 		非接触卡-CRC错,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 		非接触卡-非空,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	非接触卡-认证错,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	非接触卡-奇偶错,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 		非接触卡-接收代码错			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 		非接触卡-防冲突数据校验错	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 			非接触卡-认证KEY错			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	非接触卡-未认证				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	非接触卡-接收BIT错			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	非接触卡-接收字节错			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 		非接触卡-FIFO写错误			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 	非接触卡-传送操作错误		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 	非接触卡-写操作错误			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 	非接触卡-增量操作错误		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 	非接触卡-减量操作错误		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 	非接触卡-溢出错误			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 		非接触卡-帧错				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 			非接触卡-冲突				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	非接触卡-复位接口读写错		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	非接触卡-接收超时			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 		非接触卡-协议错				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 						非接触卡-异常终止			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 					非接触卡-PPS操作错			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 			非接触卡-申请SPI失败		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	非接触卡-无法确认的错误状态	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 		非接触卡-卡类型错			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	非接触卡-IOCTL参数错		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 						非接触卡-内部参数错			0xa9
*/
extern int  (*NDK_M1Select_SEL)(uchar ucSelCode, int nUidLen, uchar *psUidBuf, uchar *psSakBuf);

/**
 *@brief	M1卡直接外带KEY认证
 *@param	nUidLen		uid长度
 *@param	psUidBuf	uid数据(NDK_M1Anti获取的)
 *@param	ucKeyType	认证密钥类型 A=0x00 ；B=0x01
 *@param	psKeyData	key(6字节)
 *@param    ucBlockNum	要认证的块号(注意:不是扇区号!)

 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 		             	 操作失败
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(ucKeyType、nUidLen非法、psKeyData为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(射频驱动接口RFID_IOCG_M1INTERAUTHEN调用失败返回)
*/
extern int (*NDK_M1ExternalAuthen)(int nUidLen, uchar *psUidBuf, uchar ucKeyType, uchar *psKeyData, uchar ucBlockNum);

/**
 *@brief	M1卡'块'读取操作
 *@param	ucBlockNum	要读的块号
 *@param	pnDataLen	读取的块数据长度
 *@retval	psBlockData	块数据
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(pnDataLen、psBlockData为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(射频驱动接口RFID_IOCG_M1READ调用失败返回)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	非接触卡-无卡,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 		非接触卡-CRC错,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 		非接触卡-非空,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	非接触卡-认证错,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	非接触卡-奇偶错,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 		非接触卡-接收代码错			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 		非接触卡-防冲突数据校验错	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 			非接触卡-认证KEY错			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	非接触卡-未认证				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	非接触卡-接收BIT错			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	非接触卡-接收字节错			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 		非接触卡-FIFO写错误			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 			非接触卡-传送操作错误		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 			非接触卡-写操作错误			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 			非接触卡-增量操作错误		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 			非接触卡-减量操作错误		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 			非接触卡-溢出错误			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 		非接触卡-帧错				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 			非接触卡-冲突				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	非接触卡-复位接口读写错		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	非接触卡-接收超时			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 		非接触卡-协议错				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 						非接触卡-异常终止			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 					非接触卡-PPS操作错			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 			非接触卡-申请SPI失败		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	非接触卡-无法确认的错误状态	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 		非接触卡-卡类型错			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	非接触卡-IOCTL参数错		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 						非接触卡-内部参数错			0xa9
*/
extern int (*NDK_M1Read)(uchar ucBlockNum, int *pnDataLen, uchar *psBlockData);

/**
 *@brief	M1卡'块'写操作
 *@param	ucBlockNum	要写的块号
 *@param	pnDataLen	读取的块数据长度
 *@param	psBlockData	块数据
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pnDataLen、psBlockData为NULL、pnDataLen非法)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_M1WRITE调用失败返回)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	非接触卡-无卡,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 	非接触卡-CRC错,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 	非接触卡-非空,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	非接触卡-认证错,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	非接触卡-奇偶错,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 	非接触卡-接收代码错			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 	非接触卡-防冲突数据校验错	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 	非接触卡-认证KEY错			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	非接触卡-未认证				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	非接触卡-接收BIT错			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	非接触卡-接收字节错			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 	非接触卡-FIFO写错误			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 	非接触卡-传送操作错误		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 	非接触卡-写操作错误			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 	非接触卡-增量操作错误		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 	非接触卡-减量操作错误		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 	非接触卡-溢出错误			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 	非接触卡-帧错				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 	非接触卡-冲突				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	非接触卡-复位接口读写错		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	非接触卡-接收超时			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 	非接触卡-协议错				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 	非接触卡-异常终止			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 	非接触卡-PPS操作错			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 	非接触卡-申请SPI失败		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	非接触卡-无法确认的错误状态	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 	非接触卡-卡类型错			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	非接触卡-IOCTL参数错		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 	非接触卡-内部参数错			0xa9
*/
extern int (*NDK_M1Write)(uchar ucBlockNum, int *pnDataLen, uchar *psBlockData);

/**
 *@brief	M1卡'块'增量操作
 *@param	ucBlockNum	执行增量操作的块号。注意：增量操作只针对寄存器，未真正写入块数据区。另外，卡的块数据必须满足增/减量格式。
 *@param	nDataLen	增量数据长度(4字节)
 *@param	psDataBuf	增量数据
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(psDataBuf为NULL、nDataLen不等于4)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_M1INCREMENT调用失败返回)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	非接触卡-无卡,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 	非接触卡-CRC错,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 	非接触卡-非空,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	非接触卡-认证错,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	非接触卡-奇偶错,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 	非接触卡-接收代码错			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 	非接触卡-防冲突数据校验错	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 	非接触卡-认证KEY错			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	非接触卡-未认证				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	非接触卡-接收BIT错			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	非接触卡-接收字节错			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 	非接触卡-FIFO写错误			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 	非接触卡-传送操作错误		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 	非接触卡-写操作错误			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 	非接触卡-增量操作错误		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 	非接触卡-减量操作错误		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 	非接触卡-溢出错误			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 	非接触卡-帧错				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 	非接触卡-冲突				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	非接触卡-复位接口读写错		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	非接触卡-接收超时			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 	非接触卡-协议错				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 	非接触卡-异常终止			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 	非接触卡-PPS操作错			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 	非接触卡-申请SPI失败		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	非接触卡-无法确认的错误状态	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 	非接触卡-卡类型错			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	非接触卡-IOCTL参数错		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 	非接触卡-内部参数错			0xa9
*/
extern int (*NDK_M1Increment)(uchar ucBlockNum, int nDataLen, uchar *psDataBuf);

/**
 *@brief	M1卡'块'减量操作
 *@param	ucBlockNum	执行减量操作的块号。注意：减量操作只针对寄存器，未真正写入块数据区。另外，卡的块数据必须满足增/减量格式。
 *@param	nDataLen	增量数据长度(4字节)
 *@param	psDataBuf	增量数据
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(psDataBuf为NULL、nDataLen不等于4)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_M1DECREMENT调用失败返回)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	非接触卡-无卡,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 	非接触卡-CRC错,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 	非接触卡-非空,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	非接触卡-认证错,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	非接触卡-奇偶错,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 	非接触卡-接收代码错			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 	非接触卡-防冲突数据校验错	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 	非接触卡-认证KEY错			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	非接触卡-未认证				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	非接触卡-接收BIT错			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	非接触卡-接收字节错			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 	非接触卡-FIFO写错误			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 	非接触卡-传送操作错误		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 	非接触卡-写操作错误			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 	非接触卡-增量操作错误		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 	非接触卡-减量操作错误		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 	非接触卡-溢出错误			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 	非接触卡-帧错				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 	非接触卡-冲突				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	非接触卡-复位接口读写错		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	非接触卡-接收超时			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 	非接触卡-协议错				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 	非接触卡-异常终止			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 	非接触卡-PPS操作错			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 	非接触卡-申请SPI失败		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	非接触卡-无法确认的错误状态	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 	非接触卡-卡类型错			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	非接触卡-IOCTL参数错		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 	非接触卡-内部参数错			0xa9
*/
extern int (*NDK_M1Decrement)(uchar ucBlockNum, int nDataLen, uchar *psDataBuf);

/**
 *@brief	M1卡增/减量操作后的传送操作(寄存器值真正写入卡的块数据区)
 *@param	ucBlockNum	执行减量操作的块号
 *@return
*@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_M1TRANSFER调用失败返回)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	非接触卡-无卡,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 	非接触卡-CRC错,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 	非接触卡-非空,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	非接触卡-认证错,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	非接触卡-奇偶错,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 	非接触卡-接收代码错			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 	非接触卡-防冲突数据校验错	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 	非接触卡-认证KEY错			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	非接触卡-未认证				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	非接触卡-接收BIT错			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	非接触卡-接收字节错			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 	非接触卡-FIFO写错误			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 	非接触卡-传送操作错误		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 	非接触卡-写操作错误			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 	非接触卡-增量操作错误		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 	非接触卡-减量操作错误		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 	非接触卡-溢出错误			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 	非接触卡-帧错				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 	非接触卡-冲突				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	非接触卡-复位接口读写错		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	非接触卡-接收超时			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 	非接触卡-协议错				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 	非接触卡-异常终止			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 	非接触卡-PPS操作错			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 	非接触卡-申请SPI失败		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	非接触卡-无法确认的错误状态	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 	非接触卡-卡类型错			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	非接触卡-IOCTL参数错		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 	非接触卡-内部参数错			0xa9
*/
extern int (*NDK_M1Transfer)(uchar ucBlockNum);

/**
 *@brief	M1卡寄存器值恢复操作(恢复寄存器初始值，使之前的增/减量操作无效)
 *@param	ucBlockNum		执行减量操作的块号
 *@return
*@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_M1RESTORE调用失败返回)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	非接触卡-无卡,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 	非接触卡-CRC错,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 	非接触卡-非空,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	非接触卡-认证错,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	非接触卡-奇偶错,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 	非接触卡-接收代码错			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 	非接触卡-防冲突数据校验错	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 	非接触卡-认证KEY错			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	非接触卡-未认证				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	非接触卡-接收BIT错			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	非接触卡-接收字节错			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 	非接触卡-FIFO写错误			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 	非接触卡-传送操作错误		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 	非接触卡-写操作错误			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 	非接触卡-增量操作错误		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 	非接触卡-减量操作错误		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 	非接触卡-溢出错误			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 	非接触卡-帧错				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 	非接触卡-冲突				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	非接触卡-复位接口读写错		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	非接触卡-接收超时			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 	非接触卡-协议错				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 	非接触卡-异常终止			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 	非接触卡-PPS操作错			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 	非接触卡-申请SPI失败		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	非接触卡-无法确认的错误状态	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 	非接触卡-卡类型错			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	非接触卡-IOCTL参数错		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 	非接触卡-内部参数错			0xa9
*/
extern int (*NDK_M1Restore)(uchar ucBlockNum);


/**
*@brief	简易快速寻卡(用于测试等操作中加快速度)
*@param	nModeCode	  =0正常寻卡；非0快速寻卡
*@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 		操作失败
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口ioctl_PiccQuickRequest调用失败返回)
*/
extern int (*NDK_PiccQuickRequest)(int nModeCode);


/**
 *@brief	屏蔽对ISO1443-4协议支持的判断
 *@param	nModeCode	非0则执行屏蔽
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口ioctl_PiccQuickRequest调用失败返回)
*/
extern int (*NDK_SetIgnoreProtocol)(int nModeCode);


/**
*@brief	读取屏蔽ISO1443-4协议支持的设置
*@retval	pnModeCode	非0则执行屏蔽
*@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误(pnModeCode为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口ioctl_GetIgnoreProtocol调用失败返回)
*/
extern int (*NDK_GetIgnoreProtocol)(int *pnModeCode);


/**
 *@brief	读取射频接口芯片类型
 *@retval	pnRfidType	见\ref EM_RFID "EM_RFID"
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误(pnRfidType为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口ioctl_Get_rfid_ic_type调用失败返回)
*/
extern int  (*NDK_GetRfidType)(int *pnRfidType);

/**
 *@brief	M1卡寻卡防冲突和选卡的基础上激活A卡进行读写操作
 *@param	cid			ID
 *@param	pnDatalen	返回数据长度
 *@param	psDatabuf	返回数据
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	非接触卡-无卡
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 		非接触卡-接收字节错
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 		非接触卡-CRC错
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 		非接触卡-奇偶错
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 		非接触卡-冲突
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 		非接触卡-认证错
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 		非接触卡-未认证
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 		非接触卡-增量操作错误
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 		非接触卡-减量操作错误
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 		非接触卡-卡类型错
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 		非接触卡-IOCTL参数错
*/
extern int (*NDK_RfidTypeARats)(uchar cid,int *pnDatalen, uchar *psDatabuf);

/**
 *@brief	在寻卡的基础上获取ATQA和ATQB
 *@param	psPicctype	卡片类型
 *@param	pnAtqlen	返回数据长度
 *@param	psAtqbuf	返回数据
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_IOCTL" 	驱动调用错误
*/
extern int (*NDK_RfidPiccDetect_Atq)(uchar *psPicctype, int *pnAtqlen, uchar* psAtqbuf);

/**
 *@brief	Mifare 寻卡、防碰撞、选卡(寻卡类型必须已经设置为TYPE-A)
 *@param	ucReqCode	0=请求REQA, 非0=唤醒WUPA, 一般情况下需要使用WUPA
 *@param 	psUID		UID数据缓冲区
 *@param 	psUIDLen	UID数据长度
 *@retval 	psSak		最后一次选卡操作返回的SAK数据
 *@retval
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pnDataLen/psDataBuf为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_M1REQUEST调用失败返回)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	非接触卡-无卡,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 	非接触卡-CRC错,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 	非接触卡-非空,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	非接触卡-认证错,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	非接触卡-奇偶错,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 	非接触卡-接收代码错			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 	非接触卡-防冲突数据校验错	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 	非接触卡-认证KEY错			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	非接触卡-未认证				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	非接触卡-接收BIT错			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	非接触卡-接收字节错			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 	非接触卡-FIFO写错误			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 	非接触卡-传送操作错误		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 	非接触卡-写操作错误			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 	非接触卡-增量操作错误		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 	非接触卡-减量操作错误		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 	非接触卡-溢出错误			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 	非接触卡-帧错				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 	非接触卡-冲突				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	非接触卡-复位接口读写错		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	非接触卡-接收超时			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 	非接触卡-协议错				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 	非接触卡-异常终止			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 	非接触卡-PPS操作错			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 	非接触卡-申请SPI失败		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	非接触卡-无法确认的错误状态	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 	非接触卡-卡类型错			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	非接触卡-IOCTL参数错		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 	非接触卡-内部参数错			0xa9
*/
extern int (*NDK_MifareActive)(uchar ucReqCode,uchar *psUID, uchar *pnUIDLen, uchar *psSak);

/**
 *@brief	M0卡 '页'读取操作
 *@param	ucPageNum	要读的起始页号
 *@retval	pnDataLen	读取的数据长度(返回16, Mifare UltraLight系列卡片的读操作返回16字节数据)
 *@retval	psPageData	读取的数据
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pnDataLen、psBlockData为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_M1READ调用失败返回)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	非接触卡-无卡,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 	非接触卡-CRC错,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 	非接触卡-非空,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	非接触卡-认证错,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	非接触卡-奇偶错,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 	非接触卡-接收代码错			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 	非接触卡-防冲突数据校验错	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 	非接触卡-认证KEY错			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	非接触卡-未认证				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	非接触卡-接收BIT错			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	非接触卡-接收字节错			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 	非接触卡-FIFO写错误			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 	非接触卡-传送操作错误		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 	非接触卡-写操作错误			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 	非接触卡-增量操作错误		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 	非接触卡-减量操作错误		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 	非接触卡-溢出错误			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 	非接触卡-帧错				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 	非接触卡-冲突				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	非接触卡-复位接口读写错		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	非接触卡-接收超时			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 	非接触卡-协议错				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 	非接触卡-异常终止			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 	非接触卡-PPS操作错			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 	非接触卡-申请SPI失败		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	非接触卡-无法确认的错误状态	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 	非接触卡-卡类型错			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	非接触卡-IOCTL参数错		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 	非接触卡-内部参数错			0xa9
*/
extern int (*NDK_M0Read)(uchar ucPageNum, int *pnDataLen, uchar *psPageData);

/**
 *@brief	M0卡 '页'写操作
 *@param	ucPageNum	数据写入的页号
 *@param	pnDataLen	写入数据的长度(只支持传入4或者16, 分别对应Mifare UltraLight系列卡片的Write命令
 *@param				和CompatibilityWrite写命令，两种写命令有效写入数据都为4字节)
 *@param	psPageData	写入数据
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pnDataLen、psBlockData为NULL、pnDataLen非法)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_M1WRITE调用失败返回)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	非接触卡-无卡,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 	非接触卡-CRC错,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 	非接触卡-非空,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	非接触卡-认证错,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	非接触卡-奇偶错,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 	非接触卡-接收代码错			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 	非接触卡-防冲突数据校验错	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 	非接触卡-认证KEY错			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	非接触卡-未认证				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	非接触卡-接收BIT错			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	非接触卡-接收字节错			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 	非接触卡-FIFO写错误			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 	非接触卡-传送操作错误		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 	非接触卡-写操作错误			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 	非接触卡-增量操作错误		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 	非接触卡-减量操作错误		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 	非接触卡-溢出错误			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 	非接触卡-帧错				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 	非接触卡-冲突				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	非接触卡-复位接口读写错		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	非接触卡-接收超时			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 	非接触卡-协议错				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 	非接触卡-异常终止			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 	非接触卡-PPS操作错			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 	非接触卡-申请SPI失败		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	非接触卡-无法确认的错误状态	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 	非接触卡-卡类型错			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	非接触卡-IOCTL参数错		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 	非接触卡-内部参数错			0xa9
*/
extern int (*NDK_M0Write)(uchar ucPageNum, int pnDataLen, uchar *psPageData);

/**
 *@brief	M0卡 3次相互认证
 *@param	psKey		认证密钥
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(ucKeyType、nUidLen非法、psUidBuf为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_M1INTERAUTHEN调用失败返回)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	非接触卡-无卡,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 	非接触卡-CRC错,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 	非接触卡-非空,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	非接触卡-认证错,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	非接触卡-奇偶错,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 	非接触卡-接收代码错			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 	非接触卡-防冲突数据校验错	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 	非接触卡-认证KEY错			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	非接触卡-未认证				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	非接触卡-接收BIT错			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	非接触卡-接收字节错			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 	非接触卡-FIFO写错误			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 	非接触卡-传送操作错误		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 	非接触卡-写操作错误			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 	非接触卡-增量操作错误		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 	非接触卡-减量操作错误		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 	非接触卡-溢出错误			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 	非接触卡-帧错				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 	非接触卡-冲突				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	非接触卡-复位接口读写错		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	非接触卡-接收超时			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 	非接触卡-协议错				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 	非接触卡-异常终止			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 	非接触卡-PPS操作错			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 	非接触卡-申请SPI失败		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	非接触卡-无法确认的错误状态	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 	非接触卡-卡类型错			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	非接触卡-IOCTL参数错		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 	非接触卡-内部参数错			0xa9
*/
extern int (*NDK_M0Authen)(uchar *psKey);

/**
 *@brief	felica卡轮询新接口
 *@param	psSendBuf		发送命令缓冲区
 *@param	nSendLen		发送的命令长度
 *@retval	psRecebuf为轮询之后返回的felica数据，pnRecvlen轮询后felica返回的数据长度
 *          psRecebuf包含:出厂识别码(IDm)和出厂参数(PMm),这两个参数固化在芯片中,不得修改
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误(psRecebuf或者pnRecvlen为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口IOCTL_RFID_FELICA_POLL调用失败返回)
 *@li	\ref NDK_ERR_RFID_NOCARD 没有寻到felica卡
*/
extern int (*NDK_FelicaPoll)(felica_param_t fex, uchar *psRecebuf, uint *pnRecvlen);

/**
 *@brief	felica卡APDU交互,即读写卡过程
 *@param	nSendLen		发送的命令长度
 *@param	psSendBuf		发送命令缓冲区
 *@retval 	pnRecvLen	接收数据长度
 *@retval	psReceBuf	接收数据缓冲区
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(psSendBuf/pnRecvLen/psReceBuf为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口IOCTL_RFID_FELICA_APDU调用失败返回)
*/
extern int (*NDK_RfidFelicaApdu)(int nSendlen, uchar *psSendbuf, int *pnRecvlen,  uchar *psRecebuf);

/**
 *@brief	M0卡 认证release接口,加解密过程由客户自己定制，目前香港八达通客户需求
 *@param	psKey		认证密钥
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(ucKeyType、nUidLen非法、psUidBuf为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_M1INTERAUTHEN调用失败返回)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	非接触卡-无卡,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 	非接触卡-CRC错,				0xfe
 *@li   \ref ..............
 */
extern int (*NDK_M0Authen_Release)(int nSendlen, uchar *psSendbuf, int *pnRecvlen,  uchar *psRecebuf);

/**
 *@brief	Mifrae Desfire EV1 Param Set
 *@param	Piccparamtype		需要设置卡片参数类型(目前只支持CARD_WUPV_ARG)
 *@param	Paramlen			参数长度
 *@param	Parambuf			参数缓冲区
 *@return
 *@li	\ref NDK_OK									操作成功
 *@li	\ref NDK_ERR_PARA：							参数非法(Piccparamtype非法/Paramlen为0/Parambuf为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(射频驱动接口 IOCTL_RFID_SET_FELICA_TIMEOUT 调用失败返回)
 *@li   \ref ..............
 */
extern int (*NDK_RfidSetPiccParam)(uchar ucPiccparamtype, int pnParamlen, uchar *psParambuf);

/**
 *@brief	获取卡片参数
 *@param	Piccparamtype		需要获取的卡片参数类型，目前支持： CARD_UID = 1
                       											CARD_TYPEA_ATQA
                       											CARD_TYPEA_ATS
                       											CARD_TYPEB_ATQB
																CARD_TYPEA_SAK
 *@param	Paramlen			参数长度
 *@param	Parambuf			参数缓冲区
 *@return
 *@li	\ref NDK_OK									操作成功
 *@li	\ref NDK_ERR_PARA：							参数非法(Piccparamtype非法/Paramlen、Parambuf为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(射频驱动接口 IOCTL_RFID_SET_FELICA_TIMEOUT 调用失败返回)
 *@li   \ref ..............
 */
extern int (*NDK_RfidGetPiccInfo)(uchar ucPiccinfotype, int nbuflen, int *pnInfolen, uchar *psInfobuf);

/**
 *@brief	APDU读写接口，透传指令
 *@param	nSendLen		发送的命令长度
 *@param	psSendBuf		发送命令缓冲区
 *@param	timeout	    			超时时间设置（us）， timeout = 0 时采用卡片返回的默认时间
 *@retval	psDatabuf               返回的数据
 *@retval	pnDatalen               数据长度
 *@return
 *@li	\ref NDK_OK									
 *@li	\ref NDK_ERR_PARA：							参数非法
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(射频驱动接口 IOCTL_RFID_SET_FELICA_TIMEOUT 调用失败返回)
 *@li   \ref ..............
 */
extern int (*NDK_RfidPiccApduInTransMode)(const uchar *psSendbuf, int nSendlen, uchar *psRecebuf, int *pnRecvlen, uint32_t timeout);

/**
 *@brief	设置寻卡类型
 *@param	ucPicctype		需要获取的卡片参数类型，目前支持： DETECT_TYPE_A(0x00000001)
                       										DETECT_TYPE_B(0x00000002)
                       										DETECT_TYPE_F(0x00000004)
                       										DETECT_TYPE_V(0x00000008)
															以及通过或运算设置的组合寻卡类型，如：
															DETECT_TYPE_A | DETECT_TYPE_B   
															DETECT_TYPE_A | DETECT_TYPE_B | DETECT_TYPE_F
															.........
 *@return
 *@li	\ref NDK_OK									寻卡类型设置成功
 *@li	\ref NDK_ERR_PARA：							参数非法(卡片不属于A、B、F、V以及相应的组合类型)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(射频驱动接口 IOCTL_RFID_SET_FELICA_TIMEOUT 调用失败返回)
 *@li   \ref ..............
 */
extern int (*NDK_RfidSetDetectType)(uint32_t ucPicctype);

/**
 *@brief	寻卡并返回卡片类型
 *@retval	ucPicctype		      Card_Type:  		0x00000001 = CARD_TYPE_A
                       							    0x00000002 = CARD_TYPE_B
                       								0x00000004 = CARD_TYPE_F
                       								0x00000008 = CARD_TYPE_V
 *@retval	psDatabuf               寻卡返回的数据
 *@retval	pnDatalen               寻卡数据长度												   
 *@return
 *@li	\ref NDK_OK									寻卡成功
 *@li	\ref NDK_ERR_PARA：							参数非法(卡片不属于A、B、F、V以及相应的组合类型)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(射频驱动接口 IOCTL_RFID_SET_FELICA_TIMEOUT 调用失败返回)
 *@li	\ref NDK_ERR_RFID_NOPICCTYPE				未设置卡片类型
 *@li	\ref NDK_ERR_RFID_ASEEK		 				A卡多卡冲突
 *@li	\ref NDK_ERR_RFID_BSEEK 					B卡多卡冲突
 *@li	\ref NDK_ERR_RFID_ABON 						AB卡多卡冲突 
 *@li	\ref NDK_ERR_RFID_AFON						AF卡多卡冲突
 *@li	\ref NDK_ERR_RFID_BFON						BF卡多卡冲突
 *@li	\ref NDK_ERR_RFID_NOCARD 					无卡
 *@li   \ref ..............
 */
extern int (*NDK_RfidDetectWithCardType)(uint32_t *psPicctype, int *pnDatalen, uchar *psDatabuf);
/** @} */ // 射频卡模块结束

/** @addtogroup 系统接口
* @{
*/
/**
 *@brief  取POS版本和硬件信息的索引号
 *@details SYS_HWINFO_GET_POS_TYPE 返回的机型字符串格式为"NLGP-XXX"，如"NLGP-730"、"NLGP-8510"\n
		   SYS_HWINFO_GET_HARDWARE_INFO 返回硬件信息定义如下：
				 获取POS硬件信息每一个字节代表不同的模块\n
				 同一字节内不同的值代表同一模块的不同芯片类型\n
				 至少支持63个外部设备，应用传入参数数组不小于64 \n\n
				 参考\ref EM_SYS_HWTYPE "EM_SYS_HWTYPE"

   pinfo[0]:无线模块\n
		0xFF    :没有无线模块\n
		0x02    :MC39	\n
		0x03    :sim300	\n
		0x06    :M72	\n
		高位为0x80表示CDMA模块\n
		0x81    :dtgs800	\n
		0x82    :dtm228c	\n\n
   pinfo[1]:射频模块	\n
		0xFF    :没有射频模块	\n
		0x01    :RC531	\n
		0x02    :PN512	\n\n
   pinfo[2]:磁卡模块	\n
		0xFF    :没有磁卡模块	\n
		0x01    :mesh	\n
		0x02    :giga	\n\n
   pinfo[3]:扫描头模块	\n
		0xFF    :没有磁卡模块	\n
		0x01    :EM1300	\n
		0x02    :EM3000	\n
		0x03    :SE955	\n\n
   pinfo[4]:是否支持外接密码键盘	\n
		0xFF    :不支持	\n
		0x01    :支持	\n\n
   pinfo[5]:串口个数	\n
		0xFF    :无	\n
		0x01    :1个	\n
		0x02    :2个	\n\n
   pinfo[6]:是否支持USB	\n
		0xFF    :否	\n
		0x01    :是	\n\n
   pinfo[7]:MODEM模块	\n
		0xFF    :否	\n
		0x01    :是	\n\n
   pinfo[8]:wifi模块	\n
		0xFF    :无wifi模块	\n
		0x01    :是"USI WM-G-MR-09"模块	\n\n
   pinfo[9]:是否支持以太网	\n
		0xFF    :否	\n
		0x01    :dm9000	\n
		0x02    :bcm589xcore       \n\n
   pinfo[10]:打印模块	\n
		0xFF     :无打印模块	\n
		0x01~0x7F     :热敏	\n
		0x82~0x7E     :针打	\n\n
   pinfo[11]:是否支持触屏	\n
		0xFF     :否	\n
		0x01:ts_2046	\n
		0x02:589x_ts	\n\n
   pinfo[12]:是否有射频LED灯	\n
		0xFF     :否	\n
		0x01     :是	\n
*/
typedef enum {
	SYS_HWINFO_GET_POS_TYPE=0,      	/**<取pos机器类型   			*/
	SYS_HWINFO_GET_HARDWARE_INFO,       /**<获取POS机上所支持硬件类型，详细返回值如上所述*/
	SYS_HWINFO_GET_BIOS_VER,        	/**<取bios版本信息 			 */
	SYS_HWINFO_GET_POS_USN,        		/**<取机器序列号    		*/
	SYS_HWINFO_GET_POS_PSN,        		/**<取机器机器号    		*/
	SYS_HWINFO_GET_BOARD_VER,       	/**<取主板号        			*/
	SYS_HWINFO_GET_CREDITCARD_COUNT,	/**<取pos刷卡总数					*/
	SYS_HWINFO_GET_PRN_LEN,				/**<取pos打印总长度    		*/
	SYS_HWINFO_GET_POS_RUNTIME,         /**<取pos机开机运行时间  */
	SYS_HWINFO_GET_KEY_COUNT,           /**<取pos机按键次数  */
	SYS_HWINFO_GET_CPU_TYPE,            /**<取pos机cpu类型  */
	SYS_HWINFO_GET_BOOT_VER,
} EM_SYS_HWINFO;

/**
 *@brief  读取系统配置信息的索引号
*/
typedef enum {
	SYS_CONFIG_SLEEP_ENABLE,	    /**<休眠使能 0:禁止 1:启用 */
	SYS_CONFIG_SLEEP_TIME,      	/**<进入休眠时间前待机时间*/
	SYS_CONFIG_SLEEP_MODE,      	/**<休眠模式 1:浅休眠 2:深休眠*/
	SYS_CONFIG_LANGUAGE,			/**<获取系统语言 0:中文 1:english */
	SYS_CONFIG_APP_AUTORUN,      	/**<开机自动运行主控程序 0:禁用 1:启用*/
} EM_SYS_CONFIG;

/**
 *@brief  硬件设备信息类型
*/
typedef enum {
	SYS_HWTYPE_WIRELESS_MODEM=0,	/**<无线modem */
	SYS_HWTYPE_RFID,				/**< 射频卡*/
	SYS_HWTYPE_MAG_CARD,			/**< 磁卡*/
	SYS_HWTYPE_SCANNER,				/**< 扫描头*/
	SYS_HWTYPE_PINPAD,				/**< 密码键盘*/
	SYS_HWTYPE_AUX,					/**< 串口*/
	SYS_HWTYPE_USB,					/**< USB*/
	SYS_HWTYPE_MODEM,				/**< modem*/
	SYS_HWTYPE_WIFI,				/**< wifi*/
	SYS_HWTYPE_ETHERNET,			/**< 以太网*/
	SYS_HWTYPE_PRINTER,				/**< 打印机*/
	SYS_HWTYPE_TOUCHSCREEN,			/**< 触摸屏*/
	SYS_HWTYPE_RFIDLED,				/**< 射频LED灯*/
	SYS_HWTYPE_BT,                  /**< 蓝牙 */
	SYS_HWTYPE_MAX					/**< 最大值，只允许放在最后*/
} EM_SYS_HWTYPE;

/**
 *@brief  CPU间通信(如IM81的安卓和K21)
*/
typedef enum {
    SYS_WAKE_PEER = 0,
    SYS_RESET_PEER
} EM_SYS_PEEROPER;


/**
 *@brief 控制POS上所有LED  亮灭情况变量
*/
typedef enum {
    LED_RED_X_ON = 0x10000,			/**<   控制红灯亮           */
    LED_RED_X_OFF = 0x20000,        /**<   控制红灯灭           */
    LED_RED_X_FILCK = 0x30000,      /**<   控制红灯闪           */
    LED_YELLOW_X_ON = 0x40000,      /**<   控制黄灯亮           */
    LED_YELLOW_X_OFF = 0x80000,     /**<   控制黄灯灭           */
    LED_YELLOW_X_FILCK = 0xc0000,   /**<   控制黄灯闪           */
    LED_GREEN_X_ON = 0x100000,      /**<   控制绿灯亮           */
    LED_GREEN_X_OFF = 0x200000,     /**<   控制绿灯灭           */
    LED_GREEN_X_FILCK = 0x300000,   /**<   控制绿灯闪           */
    LED_BLUE_X_ON = 0x400000,       /**<   控制蓝灯亮           */
    LED_BLUE_X_OFF = 0x800000,      /**<   控制蓝灯灭          */
    LED_BLUE_X_FILCK = 0xc00000,    /**<   控制蓝灯闪           */
    LED_RFID_RED_ON = 0x01,         /**<   控制射频红色灯亮         */
    LED_RFID_RED_OFF = 0x02,        /**<   控制射频红色灯灭         */
    LED_RFID_RED_FLICK = 0x03,      /**<   控制射频红色灯闪         */
    LED_RFID_YELLOW_ON = 0x04,      /**<   控制射频黄色灯亮         */
    LED_RFID_YELLOW_OFF = 0x08,     /**<   控制射频黄色灯灭         */
    LED_RFID_YELLOW_FLICK = 0x0c,   /**<   控制射频黄色灯闪         */
    LED_RFID_GREEN_ON = 0x10,       /**<   控制射频绿色灯亮         */
    LED_RFID_GREEN_OFF = 0x20,      /**<   控制射频绿色灯灭         */
    LED_RFID_GREEN_FLICK = 0x30,    /**<   控制射频绿色灯闪         */
    LED_RFID_BLUE_ON = 0x40,        /**<   控制射频蓝色灯亮         */
    LED_RFID_BLUE_OFF = 0x80,       /**<   控制射频蓝色灯灭         */
    LED_RFID_BLUE_FLICK = 0xc0,     /**<   控制射频蓝色灯闪         */
    LED_COM_ON = 0x100,             /**<   控制通讯灯亮           */
    LED_COM_OFF = 0x200,			/**<   控制通讯灯灭           */
    LED_COM_FLICK = 0x300,          /**<   控制通讯灯闪           */
    LED_ONL_ON = 0x400,             /**<   控制联机灯亮           */
    LED_ONL_OFF = 0x800,			/**<   控制联机灯灭           */
    LED_ONL_FLICK = 0xc00,          /**<   控制联机灯闪           */
    LED_DETECTOR_ON = 0x1000,
    LED_DETECTOR_OFF = 0x2000,
    LED_DETECTOR_FLICK = 0x3000,
    LED_MAG_ON = 0x4000,			/**<   控制磁卡灯亮           */
    LED_MAG_OFF = 0x8000,			/**<   控制磁卡灯灭           */
    LED_MAG_FLICK = 0xc000,			/**<   控制磁卡灯闪           */
} EM_LED;

/*主设备号*/
typedef enum {
    SS_TYPE_KEYBOARD,		/**<按键*/
    SS_TYPE_PRINTER,		/**<打印机*/
    SS_TYPE_MAG,			/**<磁卡*/
    SS_TYPE_ICCARD,			/**<IC卡*/
    SS_TYPE_RFID,			/**<射频卡*/
    SS_TYPE_MODEM,			/**<MODEM*/
    SS_TYPE_WLS,			/**<无线*/
    SS_TYPE_WIFI,			/**<WIFI*/
    SS_TYPE_POWER,			/**<开关机*/
    SS_TYPE_DEV_MAIN_NUM	/**<主设备数量*/
} EM_SS_TYPE;

/**
 *@brief 要统计的设备ID
*/
typedef enum{
	SS_KEYBOARD_ZERO = (SS_TYPE_KEYBOARD<<16|13),		/**<统计数字0键*/
	SS_KEYBOARD_ONE  = (SS_TYPE_KEYBOARD<<16|26),		/**<统计数字1键*/
	SS_KEYBOARD_TWO  = (SS_TYPE_KEYBOARD<<16|25),		/**<统计数字2键*/
	SS_KEYBOARD_THREE = (SS_TYPE_KEYBOARD<<16|24),		/**<统计数字3键*/
	SS_KEYBOARD_FOUR = (SS_TYPE_KEYBOARD<<16|22),		/**<统计数字4键*/
	SS_KEYBOARD_FIVE = (SS_TYPE_KEYBOARD<<16|21),		/**<统计数字5键*/
	SS_KEYBOARD_SIX  = (SS_TYPE_KEYBOARD<<16|20),		/**<统计数字6键*/
	SS_KEYBOARD_SEVEN = (SS_TYPE_KEYBOARD<<16|18),		/**<统计数字7键*/
	SS_KEYBOARD_EIGHT = (SS_TYPE_KEYBOARD<<16|17),		/**<统计数字8键*/
	SS_KEYBOARD_NINE  = (SS_TYPE_KEYBOARD<<16|16),		/**<统计数字9键*/
	
    SS_KEYBOARD_ENTER = (SS_TYPE_KEYBOARD<<16|8),		/**<统计确认键*/
    SS_KEYBOARD_ESC = (SS_TYPE_KEYBOARD<<16|10),		/**<统计取消键*/
    SS_KEYBOARD_F2 = (SS_TYPE_KEYBOARD<<16|29),			/**<统计F2(菜单键)*/
    SS_KEYBOARD_F1 = (SS_TYPE_KEYBOARD<<16|28),			/**<统计F1(向上键)*/
    SS_KEYBOARD_DOT = (SS_TYPE_KEYBOARD<<16|14),		/**<统计小数点键*/
    SS_KEYBOARD_ZMK = (SS_TYPE_KEYBOARD<<16|12),		/**<统计字母键*/
    SS_KEYBOARD_F3 = (SS_TYPE_KEYBOARD<<16|30),			/**<统计F3(向下键)*/             
	SS_KEYBOARD_BASP = (SS_TYPE_KEYBOARD<<16|9),		/**<统计退格键*/
	SS_KEYBOARD_0_ID = (SS_TYPE_KEYBOARD<<16|2),		/**<统计其他键*/
	SS_KEYBOARD_1_ID = (SS_TYPE_KEYBOARD<<16|3),		/**<统计其他键*/
	SS_KEYBOARD_2_ID = (SS_TYPE_KEYBOARD<<16|4),		/**<统计其他键*/
	SS_KEYBOARD_3_ID = (SS_TYPE_KEYBOARD<<16|5),		/**<统计其他键*/
	SS_KEYBOARD_F4 = (SS_TYPE_KEYBOARD<<16|31),			/**<统计F4(关机键)*/
	SS_KEYBOARD_TOTAL = (SS_TYPE_KEYBOARD<<16|33),		/**<统计所有键*/	

	SS_PRT_PAPER_ID = (SS_TYPE_PRINTER<<16|0),    		/**<打印米数(单位为毫米mm)*/
	SS_PRT_HEAT_ID = (SS_TYPE_PRINTER<<16|1),			/**<热敏头加热时长(单位为秒)*/
	SS_PRT_STITCH_ID = (SS_TYPE_PRINTER<<16|2),			/**<针打出针数*/
    SS_PRT_WASTE_ID = (SS_TYPE_PRINTER<<16|3),			/**<空走纸米数(单位为毫米mm)**/

	SS_MAG_TIMES_ID = (SS_TYPE_MAG<<16|0),				/**<刷卡次数*/

	SS_ICCARD_BASE_ID = (SS_TYPE_ICCARD<<16|0),			/**<插卡次数*/

	SS_RFID_TIMES_ID = (SS_TYPE_RFID<<16|0),			/**<寻卡次数*/

	SS_MODEM_TIMES_ID = (SS_TYPE_MODEM<<16|0),			/**<MODEM连接次数*/
	SS_MODEM_FAILTIMES_ID = (SS_TYPE_MODEM<<16|1),		/**<MODEM连接失败次数*/
	SS_MODEM_SDLCTIME_ID = (SS_TYPE_MODEM<<16|2),		/**<MODEM时长(SDLC)*/
	SS_MODEM_ASYNTIME_ID = (SS_TYPE_MODEM<<16|3),		/**<MODEM时长(异步)*/

	SS_WLS_TIMES_ID = (SS_TYPE_WLS<<16|0),				/**<无线连接次数*/
	SS_WLS_FAILTIMES_ID = (SS_TYPE_WLS<<16|1),			/**<无线连接失败次数*/
	SS_WLS_PPPTIME_ID = (SS_TYPE_WLS<<16|2),			/**<无线连接时长(单位为毫秒)*/

	SS_WIFI_TIMES_ID = (SS_TYPE_WIFI<<16|0),			/**<WIFI连接次数*/
	SS_WIFI_TIME_ID = (SS_TYPE_WIFI<<16|1),				/**<WIFI连接时长(单位为毫秒)*/

	SS_POWER_TIMES_ID = (SS_TYPE_POWER<<16|0),			/**<开关机次数*/
	SS_POWERUP_TIME_ID = (SS_TYPE_POWER<<16|1)			/**<开机时间(单位为秒)*/
}EM_SS_DEV_ID;

/**
 *@brief 固件版本类型
*/
typedef enum {
	SYS_FWINFO_PRO,      		/**<正式版本*/
	SYS_FWINFO_DEV				/**<开发版本*/
} EM_SYS_FWINFO;

/**
 *@brief 事件类型
*/
typedef enum {
	SYS_EVENT_NONE = 0,				   /**无事件*/
	SYS_EVENT_TAMPER = 0x00000001,     /**安全触发*/
	SYS_EVENT_REBOOT = 0x00000002,     /**通知系统进行重启自检*/
	SYS_EVENT_MAGCARD = 0x00000004,	   /**检测到磁卡*/
	SYS_EVENT_ICCARD = 0x00000008,	   /**检测到IC卡插入*/
	SYS_EVENT_RFID = 0x00000010, 	   /**检测到非接卡*/
	SYS_EVENT_PIN = 0x00000020,		   /**PIN输入事件*/
	SYS_EVENT_PRNTER = 0x00000040,	   /**打印机状态*/
	SYS_EVENT_KEYPAD = 0x00000080,	   /**键盘事件*/
	SYS_EVENT_MAX = 9,
} EM_SYS_EVENT;

/**
 *@brief	获取NDK库版本号
 *@retval   pszVer	版本号字符串,缓冲大小不低于16字节
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"		参数非法(pszVer为NULL)
*/
extern int (*NDK_Getlibver)(char *pszVer);

/**
 *@brief 	重启安全模块
 *@details
 *@return
 *@li	 NDK_OK				操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"		操作失败
*/
extern int (*NDK_SysReboot)(void);
/**
 *@brief 	关闭安全模块
 *@details	关闭安全模块成功后，调用NDK接口将返回-6000
 *@return
 *@li	 NDK_OK				操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"		操作失败
*/
extern int (*NDK_SysShutDown)(void);

/**
 *@brief 	设置是否立即进入休眠
 *@details  设置是否自动进入休眠开关对此函数无影响。只要调用机器立即进入休眠
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR "NDK_ERR"	操作失败
*/
extern int (*NDK_SysGoSuspend)(void);

/**
 *@brief 	设置是否立即进入K21休眠
 *@details  是否进入K21休眠与打印机、IC卡、射频卡、触摸屏的状态有关，只有正常关闭这些状态才能进入K21休眠
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_RFID_BUSY "NDK_ERR_RFID_BUSY"		射频卡状态忙
 *@li	 \ref NDK_ERR_ICCARD_BUSY "NDK_ERR_ICCARD_BUSY"		IC卡状态忙
 *@li	 \ref NDK_ERR_PRN_BUSY "NDK_ERR_PRN_BUSY"		打印状态忙
 *@li	 \ref NDK_ERR_PIN_BUSY "NDK_ERR_PIN_BUSY"		正处于PIN输入状态
*/
extern int (*NDK_SysGoSuspend_Extern)(void);

/**
 *@brief 	添加事件
 *@details 
 *@param    event	事件类型
 *@param    pid		进程ID
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref COM_FAIL "COM_FAIL"		事件添加失败
*/
extern int (*NDK_SysAddEvent)(EM_SYS_EVENT event, unsigned int pid);

/**
 *@brief 	获取事件
 *@details  
 *@param    event		事件类型
 *@retval   len			接收数据长度
 *@retval   out_data	获取事件
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref COM_FAIL "COM_FAIL"		获取事件失败
*/
extern int (*NDK_SysGetEvent)(uint *event, int *len, uint8_t *out_data);

/**
 *@brief 	事件注销
 *@details 
 *@param    event	事件类型
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref COM_FAIL "COM_FAIL"		事件注销失败
*/
extern int (*NDK_SysDelEvent)(EM_SYS_EVENT event);

/**
 *@brief 	事件重启
 *@details 
 *@param    event	事件类型
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref COM_FAIL "COM_FAIL"		事件重启失败
*/
extern int (*NDK_SysResumeEvent)(EM_SYS_EVENT event);

/**
 *@brief 	取电源电量
 *@retval   punVol  只插电源则为0，否则返回电池电量
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"		参数非法(punVol为NULL)
 *@li	 \ref NDK_ERR "NDK_ERR"	操作失败
*/
extern int (*NDK_SysGetPowerVol)(uint *punVol);
/**
 *@brief    取电源电量范围
 *@retval   punMax:最大值 punMin:最小值
 *@return
 *@li    NDK_OK             操作成功
 *@li    \ref NDK_ERR_PARA "NDK_ERR_PARA"       参数非法(punVol为NULL)
 *@li    \ref NDK_ERR "NDK_ERR" 操作失败
*/
extern int (*NDK_SysGetPowerVolRange)(uint * punMax, uint * punMin);
/**
 *@brief 	单位延时( 单位时间为0.1s)
 *@details
 *@param    unDelayTime 延时时间，范围unDelayTime > 0
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"		参数非法(unDelayTime小于0)
*/
extern int (*NDK_SysDelay)(uint unDelayTime);
/**
 *@brief 	单位延时 (单位时间为1ms)
 *@details
 *@param    unDelayTime 延时时间，范围unDelayTime > 0
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"		参数非法(unDelayTime<=0)
*/
extern int (*NDK_SysMsDelay)(uint unDelayTime);
/**
 *@brief 	取POS当前时间
 *@details
 *@retval     pstTime  传入tm结构体类型的指针，返回当前pos时间
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"		参数非法(pstTime为NULL)
*/
extern int (*NDK_SysGetPosTime)(struct tm *pstTime);

/**
 *@brief 	设置POS当前时间
 *@details
 *@param    stTime  传入tm结构体类型的变量，设置pos时间为变量stTime的时间
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"		参数非法(stTime非法)
 *@li	 \ref NDK_ERR "NDK_ERR"		操作失败(调用mktime()/stime()失败返回)
*/
extern int (*NDK_SysSetPosTime)(struct tm stTime);
/**
 *@brief 	设置POS上面所有led灯的亮灭情况
 *@details
*@param     emStatus    枚举类型的变量，控制各个灯的亮灭，不同的各个灯之间可通过相或进行控制。
						如果相应灯的枚举变量为0(即不或上相应的值)，则相对应的灯的状态不变，如:
						NDK_LedStatus(LED_RFID_RED_ON|LED_RFID_YELLOW_FLICK),该设置为设置红灯亮，黄灯闪，其他等状态不变。
						所以控制玩相应的灯之后需要注意是否恢复。
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(emStatus)
 *@li	 \ref NDK_ERR "NDK_ERR"   		操作失败
*/
extern int (*NDK_LedStatus)(EM_LED emStatus);

typedef struct {
    uint32_t  unFlickOn;//闪烁间隔时间，unFilckOn * 100ms
    uint32_t  unFlickOff;//0:亮完直接熄灭不闪烁；其它：闪烁
    uint32_t  unRverse[2];
}ST_NAPI_LED_FLICK;

extern int (*NDK_LedSetFlickParam)(EM_LED emStatus, ST_NAPI_LED_FLICK FlickParam);

/**
 *@brief 		启动跑表，开始计时
 *@details  由NDK_SysStartWatch()和NDK_SysStopWatch()配合使用。精度在1毫秒以内
 *@return
 *@li	 NDK_OK				操作成功
*/
extern int (*NDK_SysStartWatch)(void);
/**
 *@brief 	停止跑表并保存计数值
 *@details
 *@retval   punTime 跑表结束时的计数值
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(punTime为NULL)
*/
extern int (*NDK_SysStopWatch)(uint *punTime);

/**
 *@brief 	获取计数值
 *@details
 *@retval   punTime 当前的计数值
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(punTime为NULL)
*/
extern int (*NDK_SysReadWatch)(uint *punTime);
/**
 *@brief 	读取pos硬件信息接口
 *@details	如果传入的取硬件信息的索引emFlag不在范围内，则返回参数错误，如果没取到版本信息返回NDK_ERR
 			传入的参数的数组大小可暂定为100字节,最小不少于16字节。api只返回前100个字节的信息,切可根据需求是否返回psbuf中返回数据的长度（即允许punlen传入为NULL）。
 *@param    emFlag 所要读取设备信息的索引号
 *@retval   punLen 返回传回的psBuf信息的长度(传入punLen为NULL时也允许正常允许，不返回psBuf信息长度)
 *@retval   psBuf	用于存储返回的信息
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(psBuf为NULL)
 *@li	 \ref NDK_ERR "NDK_ERR"		操作失败
*/
extern int (*NDK_SysGetPosInfo)(EM_SYS_HWINFO emFlag,uint *punLen,char *psBuf);

/**
 *@brief    读取系统配置信息
 *@param    emConfig 所要读取配置信息的索引号
 *@retval   pnValue 返回的配置值
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(pnValue为NULL)
 *@li	 \ref NDK_ERR "NDK_ERR"	操作失败
*/
extern int (*NDK_SysGetConfigInfo)(EM_SYS_CONFIG emConfig,int *pnValue);

/**
 *@brief    清除统计信息（清除应用统计信息）
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR "NDK_ERR"	操作失败(统计服务dbus通讯失败)
*/
extern int (*NDK_SysInitStatisticsData)(void);

/**
 *@brief    获取统计信息（在\ref EM_SS_DEV_ID "EM_SS_DEV_ID"选择一个ID，pulValue返回相应ID所对应的统计值）
 *@param  	emDevId 	要查询的设备ID,参考\ref EM_SS_DEV_ID "EM_SS_DEV_ID".
 *@retval   pulValue 	统计值（该统计值是一个累加值，比如打印米数统计从第一打印米数一直累加所得值）
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR "NDK_ERR"	操作失败(统计服务dbus通讯失败)
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(pulValue为NULL、emDevId小于0)
*/
extern int (*NDK_SysGetStatisticsData)(EM_SS_DEV_ID emDevId,ulong *pulValue);

/**
 *@brief 	获取POS当前时间单位为秒
 *@details	获取的时间以秒单位，从1970年1月1日0时0分0秒开始计算到现在经过了多少秒的时间。
 *@retval   ulTime 	返回所经过的秒
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(ulTime为NULL)
*/
extern int (*NDK_SysTime)(ulong *ulTime);

/**
 *@brief	获取k21端指令集版本号(新增NDK接口)
 *@retval   pszVer	版本号字符串,缓冲大小不低于16字节
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"		参数非法(pszVer为NULL)
*/
extern int (*NDk_SysGetK21Version)(char *version);

 /**
  *@brief	 数据透传函数
  *@param		 DataIn		传入数据
  *@param		 LenIn		传入数据长度
  *@param		 maxlen		传入数据最大长度
  *@param		 utimeout	超时时间
  *@param		 pid	 	进程ID
  *@param		 competence	权限值
  *@retval		 DataOut	返回数据
  *@retval		 LenOut		返回数据长度
  *@return
  *@li	 NDK_OK 			 操作成功
  *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"	 参数非法(emCtrlId非法、音量非法)
  *@li	 \ref NDK_ERR "NDK_ERR" 			 操作失败(不支持的设备接口直接返回)
 */
extern int (*NDK_RpcTransRW)(unsigned char* DataIn,int LenIn,unsigned char *DataOut,int *LenOut,int maxlen,int utimeout, unsigned int pid, unsigned int competence);

 /**
  *@brief	 K21串口初始化
  *@param		 send		指向串口发送函数
  *@param		 recv		指向数据接收函数
  *@param		 info		预留参数
  *@return
  *@li	 NDK_OK 			 操作成功
 */
extern int  (*NDK_InitCom) ( int (*send)(unsigned char * data, int len,unsigned int respondTimeoutMs),int (*recv)(unsigned char * buf, int buflen,unsigned int timeout), char * info );

 /**
  *@brief	 设置POS信息
  *@param		 emFlag		获取信息类型
  *@param		 psBuf		设置设备信息
  *@return
  *@li	 NDK_OK 			 操作成功
  *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"	 参数非法
  *@li	 \ref NDK_ERR_PARA "NDK_ERR_OVERFLOW"	 缓冲溢出
 */
extern int (*NDK_SysSetPosInfo)(EM_SYS_HWINFO emFlag, const char *psBuf);

 /**
  *@brief	 控制K21进入boot模式
  *@return
  *@li	 NDK_OK 			 操作成功
 */
extern int (*NDK_SysEnterBoot)(void);

 /**
  *@brief	控制K21复位/唤醒管脚复位
  *@param	sel		唤醒/复位
  *@return
  *@li	 NDK_OK 			 操作成功
  *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"	 参数非法
 */
extern int (*NDK_SysPeerOper)(EM_SYS_PEEROPER oper);

 /**
  *@brief	设置按键音开关
  *@param	oper		唤醒/复位
  *@return
  *@li	 NDK_OK 			 操作成功
  *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"	 参数非法
 */
extern int (*NDK_SysKeyVolSet)(uint sel);

/**
 *@brief    由K21接管触摸屏，并按键获取触摸的横纵坐标，主要用于屏幕输入密码的环境下使用。
 *@brief    此接口与NDK_TSKbd_Ctrl配合使用，只有在K21获取控制的时候才能获取到按键的坐标值。
 *@details  在规定的时间里读横纵坐标，读键过程如下:按下一个键，等待放开，返回键码。
 *@param    unTime  小于等于0 :无超时，一直等待读按键
                            其他:为等待时间(以秒为单位)
 *@retval    x  获取按键横坐标，若在规定的时间内没有按键按下，x的值为0
 *@retval    y  获取按键纵坐标，若在规定的时间内没有按键按下，y的值为0
 *@return
 *@li       NDK_OK                  操作成功
 *@li       \ref NDK_ERR "NDK_ERR"    操作失败
 *@li       \ref NDK_ERR "NDK_ERR"    K21未接管触摸屏
*/
extern int (*NDK_TSKbdGetXY)(uint unTime, uint *x, uint *y);

/**
 *@brief    由K21接管或释放触摸屏
 *@param    ctrl  非0：K21接管触摸屏  0：K21释放触摸屏的控制
 *@return
 *@li       NDK_OK                 操作成功
 *@li       \ref NDK_ERR "NDK_ERR"    操作失败
*/
extern int (*NDK_TSKbd_Ctrl)(uint ctrl);

 /**
  *@brief	 睡眠唤醒
  *@param	
  *@return
  *@li	 NDK_OK 			 操作成功
  *@li	 \ref NDK_ERR "NDK_ERR"	 唤醒失败
 */
extern int (*NDK_SysWakeUp)(void);

/**
 *@brief	获取NDK能力
 *@param	nSizeOfCap - 传入的szCaps指针所指向的BUFFER的长度 (nSizeOfCap 目前至少需要大于 8个字节，后续将根据NDK能力值定义增加长度)
 *@retval	szCaps - 能力值的字符串，“NNN” - 这样的指示值
 *@return 返回值
 *@li		NDK_ERR_PARA - 传入的nSizeOfCap < 8 或者 szCaps == NULL
 * 
 * 判断是否支持外卡：
 *		const char* caps = NDK_szGetCapability();
 *		if(strlen(caps) >= 3 && caps[2]==Y) {	
 *			// 当前版本支持外卡
 *	 	}
 *		else {
 *	 	 	// 当前版本不支持外卡
 *		}
 *
*/
extern int (*NDK_SysGetCapability)(int nSizeOfCap, char* szCaps);
/** @} */ // 系统接口结束

/** @addtogroup 工具
* @{
*/

/**
 *@brief	2个最大不超过12位的无符号数字串加法
 *@details	2个数字串逐次逐位相加，相加结果不能超过12位
 *@param	pszDigStr1		数字串1
 *@param	pszDigStr2		数字串2
 *@param	pnResultLen		结果缓冲区pszResult的大小
 *@retval	pszResult		相加和的数字串
 *@retval	pnResultLen		相加和的位数
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"		参数非法(pszDigStr1/pszDigStr2/pszResult/pnResultLen为NULL、数字串不合法)
 *@li	\ref NDK_ERR "NDK_ERR"		操作失败(两数字串相加超过12位)
*/
extern int (*NDK_AddDigitStr)(const uchar *pszDigStr1, const uchar *pszDigStr2, uchar* pszResult, int *pnResultLen );

/**
 *@brief	将6位数字串pszStrNum增加1后放回原值
 *@param	pszStrNum		需要被增加的数字串,缓冲区长度至少为7
 *@retval	pszStrNum		增加后的结果串
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"		参数非法(pszStrNum为NULL、pszStrNum长度大于6，pszStrNum数字串不合法)
*/
extern int (*NDK_IncNum) (uchar * pszStrNum );

/**
 *@brief	把带小数点的金额字符串转为不带小数点的金额字符串
 *@param	pszSource		待转换的金额字符串
 *@param	pnTargetLen		目标缓冲区的大小
 *@retval	pszTarget		转换后的字符串
 *@retval	pnTargetLen		转换后的字符串长度
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(pszSource/pszTarget/pnTargetLen为NULL)
*/
extern int (*NDK_FmtAmtStr) (const uchar* pszSource, uchar* pszTarget, int* pnTargetLen );

/**
 *@brief	将AscII码的字符串转换成压缩的HEX格式
 *@details	非偶数长度的字符串根据对齐方式，采取左右补0。
 *@param	pszAsciiBuf		被转换的ASCII字符串
 *@param	nLen			输入数据长度(ASCII字符串的长度)
 *@param	ucType			对齐方式  0－左对齐  1－右对齐
 *@retval	psBcdBuf		转换输出的HEX数据
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(pszAsciiBuf/psBcdBuf为NULL、nLen<=0)
*/
extern int (*NDK_AscToHex) (const uchar* pszAsciiBuf, int nLen, uchar ucType, uchar* psBcdBuf);

/**
 *@brief	将HEX码数据转换成AscII码字符串
 *@param	psBcdBuf		被转换的Hex数据
 *@param	nLen			转换数据长度(ASCII字符串的长度)
 *@param	ucType			对齐方式  1－左对齐  0－右对齐
 *@retval	pszAsciiBuf		转换输出的AscII码数据
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(psBcdBuf/pszAsciiBuf为NULL、nLen<0、ucType非法)
*/
extern int (*NDK_HexToAsc) (const uchar* psBcdBuf, int nLen, uchar ucType, uchar* pszAsciiBuf);

/**
 *@brief	整型转换为4字节字符数组（高位在前）
 *@param	unNum		需要转换的整型数
 *@retval	psBuf		转换输出的字符串
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(psBuf为NULL)
*/
extern int (*NDK_IntToC4) (uchar* psBuf, uint unNum );

/**
 *@brief	整型转换为2字节字符数组（高位在前）
 *@param	unNum		需要转换的整型数
 *@retval	psBuf		转换输出的字符串
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(psBuf为NULL)
*/
extern int (*NDK_IntToC2)(uchar* psBuf, uint unNum );

/**
 *@brief	4字节字符数组转换为整型（高位在前）
 *@param	psBuf		需要转换的字符串
 *@retval	unNum		转换输出的整型数
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(unNum、psBuf为NULL)
*/
extern int (*NDK_C4ToInt)(uint* unNum, uchar* psBuf );

/**
 *@brief	2字节字符数组转换为整型（高位在前）
 *@details	psBuf长度要>=2
 *@param	psBuf		需要转换的字符串
 *@retval	unNum		转换输出的整型数
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(unNum、psBuf为NULL)
*/
extern int (*NDK_C2ToInt)(uint *unNum, uchar *psBuf);

/**
 *@brief	整数(0-99)转换为一字节BCD
 *@param	nNum		需要转换的整型数(0-99)
 *@retval	psCh			转换输出的一个BCD字符
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(ch为NULL、nNum非法)
*/
extern int (*NDK_ByteToBcd)(int nNum, uchar *psCh);

/**
 *@brief	一字节BCD转换为整数(0-99)
 *@param	ucCh		需要转换的BCD字符
 *@retval	pnNum	转换输出的整数值(0-99)
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(pnNum为NULL、ch 非法)
*/
extern int (*NDK_BcdToByte)(uchar ucCh, int *pnNum);

/**
 *@brief	整数(0-9999)需要转换的整型数(0-9999)
 *@param	nNum		数字串1
 *@param	pnBcdLen	输出缓冲区的大小
 *@retval	pnBcdLen	转换后的BCD长度，如果成功此值，固定返回值为2
 *@retval	psBcd		转换输出的两字节BCD
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(psBcd、pnBcdLen为NULL、nNum 非法)
*/
extern int (*NDK_IntToBcd)(uchar *psBcd, int *pnBcdLen, int nNum);

/**
 *@brief	二字节BCD转换为整数(0-9999)
 *@details	psBcd长度应等于2
 *@param	psBcd		需要转换的两字节BCD
 *@retval	nNum		转换后的整数(0-9999)
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(psBcd、nNum为NULL、nNum 非法)
*/
extern int (*NDK_BcdToInt)(const uchar * psBcd, int *nNum);

/**
 *@brief	计算LRC
 *@details	psbuf缓冲的长度>nLen
 *@param	psBuf		需要计算LRC的字符串
 *@param	nLen		需要计算LRC的字符串的长度
 *@retval	ucLRC		计算得出的LRC
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(psBuf、ucLRC为NULL、nLen<=0)
*/
extern int (*NDK_CalcLRC)(const uchar *psBuf, int nLen, uchar *ucLRC);

/**
 *@brief	字符串去左空格
 *@param	pszBuf		存放字符串的缓冲区
 *@retval	pszBuf		去掉左空格后的字符串
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(pszBuf为NULL)
*/
extern int (*NDK_LeftTrim)(uchar *pszBuf);

/**
 *@brief	字符串去右空格
 *@param	pszBuf		存放字符串的缓冲区
 *@retval	pszBuf		去掉右空格后的字符串
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(pszBuf为NULL)
*/
extern int (*NDK_RightTrim)(uchar *pszBuf);

/**
 *@brief	字符串去左右空格
 *@param	pszBuf			存放字符串的缓冲区
 *@retval	pszBuf			去掉左右空格后的字符串
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(pszBuf为NULL)
*/
extern int (*NDK_AllTrim)(uchar *pszBuf);

/**
 *@brief	往一字符串里加入某一字符使之长度为nLen
 *@details	pszString缓冲的长度应>nlen, 字符串的长度要小于nlen
 *@param	pszString		存放字符串的缓冲区
 *@param    nLen			字符串长度
 *@param	ucCh				所要加入的字符
 *@param	nOption			操作类型
                          	0    往字符串前面加字符
                          	1    往字符串后面加字符
                          	2    往字符串前后加字符
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(pszString为NULL、pszString长度非法、nOption非法)
*/
extern int (*NDK_AddSymbolToStr)(uchar *pszString, int nLen, uchar ucCh, int nOption);

/**
 *@brief	截取子串
 *@details	子串后带'\0'结束符
 *@param	pszSouStr		需要进行截取的字符串
 *@param	nStartPos		要截取子串的起始位置 字符串的位置由1开始计数
 *@param	nNum			要截取的字符数
 *@retval	pszObjStr		存放目标串的缓冲区
 *@retval	pnObjStrLen		子串的长度
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(pszObjStr/pnObjStrLen/pszSouStr为NULL)
*/
extern int (*NDK_SubStr)(const uchar *pszSouStr, int nStartPos, int nNum, uchar *pszObjStr, int *pnObjStrLen);

/**
 *@brief	判断给定一字符是不是数字字符
 *@param	ucCh		需要判断的字符
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(ch非法)
*/
extern int (*NDK_IsDigitChar)(uchar ucCh);

/**
 *@brief	测试一字串是否为纯数字串
 *@param	pszString		需要判断的字符串
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(pszString为NULL)
*/
extern int (*NDK_IsDigitStr)(const uchar *pszString);

/**
 *@brief	判断某年是否闰年
 *@param	nYear		年份
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR "NDK_ERR"	操作失败
*/
extern int (*NDK_IsLeapYear)(int nYear);

/**
 *@brief	找出某年某月的最大天数
 *@param	nYear		年份
 *@param	nMon		月份
 *@retval	pnDays		该年份该月对应的最大天数
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(年、月、日非法)
*/
extern int (*NDK_MonthDays)(int nYear, int nMon, int *pnDays);

/**
 *@brief	判断提供的字符串是不是合法的日期格式串
 *@param	pszDate		日期格式字符串  格式为 YYYYMMDD
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(pszDate为NULL、pszDate串长度不等于8、pszDate非法)
*/
extern int (*NDK_IsValidDate)(const uchar *pszDate);

/** @} */ // 工具模块结束

/**
 *@brief   指令交互
 *@param    sendlen            发送的指令长度
 *@param    sendbuff            发送的指令缓存(最长255字节)
 *@param    recvlen            要接收的数据长度
 *@retval   recvbuff         接受的数据缓存(可以与发送缓存共用一个buff)
 *@return
 *@li   NDK_OK 操作成功
 *@li   \ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"    设备文件打开失败
 *@li   \ref NDK_ERR "NDK_ERR"              操作失败
*/
extern int (*NDK_CosCmdRW)(uint sendlen, uchar *sendbuf, uint recvlen, uchar *recvbuf);

/**
 *@brief   获取当前COS模式
 *@retval   mode
 *@return
 *@li   NDK_OK 操作成功
 *@li   \ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"    设备文件打开失败
 *@li   \ref NDK_ERR "NDK_ERR"              操作失败
*/
extern int (*NDK_CosGetMode)(uchar *mode);

/**
 *@brief   设置COS模式
 *@param    mode            设置模式
 *@return
 *@li   NDK_OK 操作成功
 *@li   \ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"    设备文件打开失败
 *@li   \ref NDK_ERR "NDK_ERR"              操作失败
*/
extern int (*NDK_CosSetMode)(uchar mode);

/**
 *@brief   获取当前COS版本
 *@retval   ver         版本号(字符串形式，最长22个字节)
 *@return
 *@li   NDK_OK 操作成功
 *@li   \ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"    设备文件打开失败
 *@li   \ref NDK_ERR "NDK_ERR"              操作失败
*/
extern int (*NDK_CosGetVer)(uchar *ver);

//self define
//extern int NDK_CardCheck(int *uKey, int *uIcState, uchar *uSwiped, uchar *uRfidState);

/**
* @brief 非接L2开启交易
* @param in   nKernelId 指示使用哪个非接内核
* @param in/out pstOpt  非接交易参数结构体
* @param in/out pstData  非接交易参数结构体
* @return 交易结果或者错误码
*/
extern int (*NDK_SDK_CLSS_Process)(int nKernelId, STENTRYPOINTOPT * pstOpt ,STRFTRANSDATA *pstData);

/**
* @brief 非接L2结束交易
* @param in   nKernelId 指示使用哪个非接内核
* @param in   nTransFinal 交易接口返回的结果(无意义)
* @return SUCC
*/
extern int (*NDK_SDK_CLSS_Suspend)(int nKernelId, int nTransFinal);

/**
* @brief 设置L2回调接口
* @param in   callbackFuncId 回调接口ID
* @param in   callbackFunction  回调接口
* @return SUCC
*/
extern int (*NDK_SDK_EP_SetCallbackFunction)(unsigned int callbackFuncId, void *callbackFunction);

/**
* @brief 获取非接L2版本号
* @param in   nKernelId 指示使用哪个非接内核
* @return 版本号字符串
*/
extern const char* (*NDK_SDK_CLSS_GetVersion)(int nKernelId);

extern int (*NDK_SYS_RegisterEvent)(int eventNum, int timeOutMs, int (* notifyEvent )( int eventNum, int msgLen, char * msg));
extern int (*NDK_SYS_UnRegisterEvent)(int eventNum);
#endif
/* End of this file */

