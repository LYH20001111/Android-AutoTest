#ifndef __NDK_H_
#define __NDK_H_

#include <stdio.h>
#include <time.h>
#include <sys/socket.h>
#include "napi.h"

/** @addtogroup 错误码定义
* @{
*/
typedef void *	NDK_HANDLE;         ///<SSL、Timer句柄
typedef unsigned int 	uint;
typedef unsigned char 	uchar;
typedef unsigned short 	ushort;
typedef unsigned long 	ulong;

/**
 *@brief 函数错误码返回
*/
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
	NDK_ERR_NO_SNK = -19,				/**<不存在SNK**/
	NDK_ERR_NO_TUSN = -20,				 /**<不存在TUSN*/
	NDK_ERR_NOSWIPED = -50,			/**<无磁卡刷卡记录*/
	NDK_ERR_SWIPED_DATA=-51,		/**<驱动磁卡数据格式错*/
	NDK_ERR_USB_LINE_UNCONNECT = -100,  /**<USB线未连接*/
	NDK_ERR_NO_SIMCARD = -201,		/**<无SIM卡*/
	NDK_ERR_PIN = -202, 			/**<SIM卡密码错误*/
	NDK_ERR_PIN_LOCKED = -203,		/**<SIM卡被锁定*/
	NDK_ERR_PIN_UNDEFINE = -204,	/**<SIM卡未定义错误*/
	NDK_ERR_EMPTY = -205,			/**<返回空串*/
	NDK_ERR_ETH_PULLOUT = -250,		/**<以太网未插线*/
	NDK_ERR_PPP_PARAM = -301,		/**<PPP参数出错*/
	NDK_ERR_PPP_DEVICE = -302,		/**<PPP无效设备*/
	NDK_ERR_PPP_OPEN = -303, 		/**<PPP已打开*/
	NDK_ERR_TCP_ALLOC = -304,	/**<无法分配*/
	NDK_ERR_TCP_PARAM = -305,	/**<无效参数*/
	NDK_ERR_TCP_TIMEOUT = -306,	/**<传输超时*/
	NDK_ERR_TCP_INVADDR = -307,	/**<无效地址*/
	NDK_ERR_TCP_CONNECT = -308,	/**<没有连接*/
	NDK_ERR_TCP_PROTOCOL = -309,/**<协议错误*/
	NDK_ERR_TCP_NETWORK = -310,	/**<网络错误*/
	NDK_ERR_TCP_SEND = -311,	/**<发送错误*/
	NDK_ERR_TCP_RECV = -312,	/**<接收错误*/
	
	NDK_ERR_WLM_SEND_AT_FAIL = -320,		/**<无线发送AT失败*/

	NDK_ERR_SSL_PARAM = -350,       	/**<无效参数*/
	NDK_ERR_SSL_ALREADCLOSE = -351, 	/**<连接已关闭*/
	NDK_ERR_SSL_ALLOC = -352,       	/**<无法分配*/
	NDK_ERR_SSL_INVADDR = -353,     	/**<无效地址*/
	NDK_ERR_SSL_TIMEOUT = -354,     	/**<连接超时*/
	NDK_ERR_SSL_MODEUNSUPPORTED = -355, /**<模式不支持*/
	NDK_ERR_SSL_SEND = -356,        	/**<发送错误*/
	NDK_ERR_SSL_RECV = -357,        	/**<接收错误*/
	NDK_ERR_SSL_CONNECT = -358,       	/**<没有连接*/

	NDK_ERR_NET_GETADDR = -401,			/**<获取本地地址或子网掩码失败*/
	NDK_ERR_NET_GATEWAY = -402,			/**<获取网关地址失败*/
	NDK_ERR_NET_ADDRILLEGAL =-403,		/**<获取地址格式错误*/	
	NDK_ERR_NET_UNKNOWN_COMMTYPE=-404,	/**<未知的通信类型*/	
	NDK_ERR_NET_INVALIDIPSTR=-405,		/**<无效的IP字符串*/
	NDK_ERR_NET_UNSUPPORT_COMMTYPE=-406,	/**<不支持的通信类型*/

	NDK_ERR_THREAD_PARAM = -450,     	/**<无效参数*/
	NDK_ERR_THREAD_ALLOC = -451,     	/**<无效分配*/
	NDK_ERR_THREAD_CMDUNSUPPORTED = -452,     /**<命令不支持*/

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
	NDK_ERR_MODEM_SELFADAPTFAIL = -531,		/**<MODEM自适应失败*/
	NDK_ERR_MODEM_SELFADAPTCANCEL = -532, 	/**<MODEM自适应取消*/

	NDK_ERR_ICC_WRITE_ERR =			-601,	/**<写器件83c26出错*/
	NDK_ERR_ICC_COPYERR=			-602,	/**<内核数据拷贝出错*/
	NDK_ERR_ICC_POWERON_ERR=		-603,	/**<上电出错*/
	NDK_ERR_ICC_COM_ERR=			-604,	/**<命令出错*/
	NDK_ERR_ICC_CARDPULL_ERR=		-605,	/**<卡拔出了*/
	NDK_ERR_ICC_CARDNOREADY_ERR=	-606,	/**<卡未准备好*/

	NDK_ERR_USDDISK_PARAM =  -650,          /**<无效参数*/
	NDK_ERR_USDDISK_DRIVELOADFAIL =  -651,  /**<U盘或SD卡驱动加载失败*/
	NDK_ERR_USDDISK_NONSUPPORTTYPE =  -652, /**<不支持的类型*/
	NDK_ERR_USDDISK_UNMOUNTFAIL =  -653,    /**<挂载失败*/
	NDK_ERR_USDDISK_UNLOADDRIFAIL =  -654,  /**<卸载驱动失败*/
	NDK_ERR_USDDISK_IOCFAIL =  -655,        /**<驱动调用错误*/

	NDK_ERR_APP_BASE=(-800),						/**<应用接口错误基数*/
	NDK_ERR_APP_NOT_EXIST=(NDK_ERR_APP_BASE-1),		/**<应用项不存在*/
	NDK_ERR_APP_NOT_MATCH=(NDK_ERR_APP_BASE-2),	    /**<补丁包文件不匹配*/
	NDK_ERR_APP_FAIL_SEC=(NDK_ERR_APP_BASE-3),	   	/**<获取安全攻击状态失败*/
	NDK_ERR_APP_SEC_ATT=(NDK_ERR_APP_BASE-4),	  	/**<存在安全攻击*/
	NDK_ERR_APP_FILE_EXIST=(NDK_ERR_APP_BASE-5),	/**<应用中该文件已存在*/
	NDK_ERR_APP_FILE_NOT_EXIST=(NDK_ERR_APP_BASE-6),/**<应用中该文件不存在*/
	NDK_ERR_APP_FAIL_AUTH=(NDK_ERR_APP_BASE-7),	  	/**<证书认证失败*/
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
	NDK_ERR_APP_FIRM_PATCH_VERSION=(NDK_ERR_APP_BASE-25),				/**固件补丁增量包版本不匹配*/
    NDK_ERR_APP_CERT_HAS_EXPIRED=(NDK_ERR_APP_BASE-26),				/**证书已经失效*/
    NDK_ERR_APP_CERT_NOT_YET_VALID=(NDK_ERR_APP_BASE-27),             /**证书尚未生效*/
	NDK_ERR_APP_FILE_NAME_TOO_LONG=(NDK_ERR_APP_BASE-28),    /**文件名长度大于32字节*/
	
    NDK_ERR_SECP_BASE = (-1000),								/**<未知错误*/
    NDK_ERR_SECP_TIMEOUT = (NDK_ERR_SECP_BASE - 1),             /**<获取键值超时*/
    NDK_ERR_SECP_PARAM = (NDK_ERR_SECP_BASE - 2),               /**<输入参数非法*/
    NDK_ERR_SECP_DBUS = (NDK_ERR_SECP_BASE - 3),                /**<DBUS通讯错误*/
    NDK_ERR_SECP_MALLOC = (NDK_ERR_SECP_BASE - 4),              /**<动态内存分配错误*/
    NDK_ERR_SECP_OPEN_SEC = (NDK_ERR_SECP_BASE - 5),            /**<打开安全设备错误*/
    NDK_ERR_SECP_SEC_DRV = (NDK_ERR_SECP_BASE - 6),             /**<安全设备操作错误*/
    NDK_ERR_SECP_GET_RNG = (NDK_ERR_SECP_BASE - 7),             /**<获取随机数*/
    NDK_ERR_SECP_GET_KEY = (NDK_ERR_SECP_BASE - 8),             /**<获取密钥值*/
    NDK_ERR_SECP_KCV_CHK = (NDK_ERR_SECP_BASE - 9),             /**<KCV校验错误*/
    NDK_ERR_SECP_GET_CALLER = (NDK_ERR_SECP_BASE - 10),         /**<获取调用者信息错误*/
    NDK_ERR_SECP_OVERRUN = (NDK_ERR_SECP_BASE - 11),            /**<运行次数出错*/
    NDK_ERR_SECP_NO_PERMIT = (NDK_ERR_SECP_BASE - 12),          /**<权限不允许*/
	NDK_ERR_SECP_TAMPER = (NDK_ERR_SECP_BASE - 13),          	/**<安全攻击*/

    NDK_ERR_SECVP_BASE = (-1100),                           /**<未知错误*/
    NDK_ERR_SECVP_TIMEOUT = (NDK_ERR_SECVP_BASE - 1),       /**<获取键值超时*/
    NDK_ERR_SECVP_PARAM = (NDK_ERR_SECVP_BASE - 2),         /**<输入参数非法*/
    NDK_ERR_SECVP_DBUS = (NDK_ERR_SECVP_BASE - 3),          /**<DBUS通讯错误*/
    NDK_ERR_SECVP_OPEN_EVENT0 =	(NDK_ERR_SECVP_BASE - 4),   /**<打开event0设备出错*/
    NDK_ERR_SECVP_SCAN_VAL = (NDK_ERR_SECVP_BASE - 5),      /**<扫描值超出定义*/
    NDK_ERR_SECVP_OPEN_RNG = (NDK_ERR_SECVP_BASE - 6),      /**<打开随机数设备错误*/
    NDK_ERR_SECVP_GET_RNG = (NDK_ERR_SECVP_BASE - 7),       /**<获取随机数出错*/
    NDK_ERR_SECVP_GET_ESC = (NDK_ERR_SECVP_BASE - 8),       /**<用户取消键退出*/
    NDK_ERR_SECVP_VPP = (-1120),                            /**<未知错误*/
    NDK_ERR_SECVP_INVALID_KEY=(NDK_ERR_SECVP_VPP),  		/**<无效密钥,内部使用.*/
	NDK_ERR_SECVP_NOT_ACTIVE=(NDK_ERR_SECVP_VPP-1),  		/**<VPP没有激活，第一次调用VPPInit.*/
	NDK_ERR_SECVP_TIMED_OUT=(NDK_ERR_SECVP_VPP-2),			/**<已经超过VPP初始化的时间.*/
	NDK_ERR_SECVP_ENCRYPT_ERROR=(NDK_ERR_SECVP_VPP-3),		/**<按确认键后，加密错误.*/
	NDK_ERR_SECVP_BUFFER_FULL=(NDK_ERR_SECVP_VPP-4),		/**<输入BUF越界，（键入的PIN太长）*/
	NDK_ERR_SECVP_PIN_KEY=(NDK_ERR_SECVP_VPP-5),  			/**<数据键按下，回显"*".*/
	NDK_ERR_SECVP_ENTER_KEY=(NDK_ERR_SECVP_VPP-6),			/**<确认键按下，PIN处理.*/
	NDK_ERR_SECVP_BACKSPACE_KEY=(NDK_ERR_SECVP_VPP-7),		/**<退格键按下.*/
	NDK_ERR_SECVP_CLEAR_KEY=(NDK_ERR_SECVP_VPP-8),  		/**<清除键按下，清除所有'*'显示.*/
	NDK_ERR_SECVP_CANCEL_KEY=(NDK_ERR_SECVP_VPP-9),  		/**<取消键被按下.*/
	NDK_ERR_SECVP_GENERALERROR=(NDK_ERR_SECVP_VPP-10),  	/**<该进程无法继续。内部错误.*/
	NDK_ERR_SECVP_CUSTOMERCARDNOTPRESENT=(NDK_ERR_SECVP_VPP-11), /**<IC卡被拔出*/
	NDK_ERR_SECVP_HTCCARDERROR=(NDK_ERR_SECVP_VPP-12),  	/**<访问智能卡错误.*/
	NDK_ERR_SECVP_WRONG_PIN_LAST_TRY=(NDK_ERR_SECVP_VPP-13),/**<智能卡-密码不正确，重试一次.*/
	NDK_ERR_SECVP_WRONG_PIN=(NDK_ERR_SECVP_VPP-14), 		/**<智能卡-最后尝试一次.*/
	NDK_ERR_SECVP_ICCERROR=(NDK_ERR_SECVP_VPP-15),  		/**<智能卡-重试太多次*/
	NDK_ERR_SECVP_PIN_BYPASS=(NDK_ERR_SECVP_VPP-16),  		/**<智能卡-PIN验证通过,并且PIN是0长度*/
	NDK_ERR_SECVP_ICCFAILURE=(NDK_ERR_SECVP_VPP-17),  		/**<智能卡-致命错误.*/
	NDK_ERR_SECVP_GETCHALLENGE_BAD=(NDK_ERR_SECVP_VPP-18),  /**<智能卡-应答不是90 00.*/
	NDK_ERR_SECVP_GETCHALLENGE_NOT8=(NDK_ERR_SECVP_VPP-19), /**<智能卡-无效的应答长度.*/
 	NDK_ERR_SECVP_PIN_ATTACK_TIMER=(NDK_ERR_SECVP_VPP-20),  /**<PIN攻击定时器被激活*/

    NDK_ERR_SECCR_BASE = (-1200),                           /**<未知错误*/
    NDK_ERR_SECCR_TIMEOUT = (NDK_ERR_SECCR_BASE - 1),       /**<获取键值超时*/
    NDK_ERR_SECCR_PARAM = (NDK_ERR_SECCR_BASE - 2),         /**<输入参数非法*/
    NDK_ERR_SECCR_DBUS = (NDK_ERR_SECCR_BASE - 3),          /**<DBUS通讯错误*/
    NDK_ERR_SECCR_MALLOC = (NDK_ERR_SECCR_BASE - 4),        /**<动态内存分配错误*/
    NDK_ERR_SECCR_OPEN_RNG = (NDK_ERR_SECCR_BASE - 5),      /**<打开随机数设备错误*/
    NDK_ERR_SECCR_DRV = (NDK_ERR_SECCR_BASE - 6),           /**<驱动加密错误*/
    NDK_ERR_SECCR_KEY_TYPE = (NDK_ERR_SECCR_BASE - 7),      /**<密钥类型错误*/
    NDK_ERR_SECCR_KEY_LEN = (NDK_ERR_SECCR_BASE - 8),       /**<密钥长度错误*/
    NDK_ERR_SECCR_GET_KEY = (NDK_ERR_SECCR_BASE - 9),       /**<获取密钥错误*/

    NDK_ERR_SECKM_BASE = (-1300),								/**<未知错误*/
    NDK_ERR_SECKM_TIMEOUT = (NDK_ERR_SECKM_BASE - 1),           /**<获取键值超时*/
    NDK_ERR_SECKM_PARAM = (NDK_ERR_SECKM_BASE - 2),             /**<输入参数非法*/
    NDK_ERR_SECKM_DBUS = (NDK_ERR_SECKM_BASE - 3),              /**<DBUS通讯错误*/
    NDK_ERR_SECKM_MALLOC = (NDK_ERR_SECKM_BASE - 4),            /**<动态内存分配错误*/
    NDK_ERR_SECKM_OPEN_DB = (NDK_ERR_SECKM_BASE - 5),           /**<数据库打开错误*/
    NDK_ERR_SECKM_DEL_DB = (NDK_ERR_SECKM_BASE - 6),            /**<删除数据库错误*/
    NDK_ERR_SECKM_DEL_REC = (NDK_ERR_SECKM_BASE - 7),           /**<删除记录错误*/
    NDK_ERR_SECKM_INSTALL_REC = (NDK_ERR_SECKM_BASE - 8),       /**<安装密钥记录错误*/
    NDK_ERR_SECKM_READ_REC = (NDK_ERR_SECKM_BASE - 9),          /**<读密钥记录错误*/
    NDK_ERR_SECKM_OPT_NOALLOW = (NDK_ERR_SECKM_BASE - 10),      /**<操作不允许*/
    NDK_ERR_SECKM_KEY_MAC = (NDK_ERR_SECKM_BASE - 11),          /**<密钥MAC校验错误*/
    NDK_ERR_SECKM_KEY_TYPE = (NDK_ERR_SECKM_BASE - 12),         /**<密钥类型错误*/
    NDK_ERR_SECKM_KEY_ARCH = (NDK_ERR_SECKM_BASE - 13),         /**<密钥体系错误*/
    NDK_ERR_SECKM_KEY_LEN  = (NDK_ERR_SECKM_BASE - 14),         /**<密钥长度错误*/

	NDK_ERR_RFID_INITSTA=			-2005,  /**<非接触卡-射频接口器件故障或者未配置*/
	NDK_ERR_RFID_NOCARD=			-2008,  /**<非接触卡-无卡  0x0D*/
	NDK_ERR_RFID_MULTICARD=			-2009,  /**<非接触卡-多卡状态*/
	NDK_ERR_RFID_SEEKING=			-2010,  /**<非接触卡-寻卡/激活过程中失败*/
	NDK_ERR_RFID_PROTOCOL=			-2011,  /**<非接触卡-不支持ISO1444-4协议，如M1卡  F*/

	NDK_ERR_RFID_NOPICCTYPE=		-2012,  /**<非接触卡-未设置卡 0x01*/
	NDK_ERR_RFID_NOTDETE=			-2013,  /**<非接触卡-未寻卡   0x02*/
	NDK_ERR_RFID_AANTI=				-2014,  /**<非接触卡-A卡冲突(多张卡存在)  0x03*/
	NDK_ERR_RFID_RATS=				-2015,  /**<非接触卡-A卡RATS过程出错   0x04*/
	NDK_ERR_RFID_BACTIV=			-2016,  /**<非接触卡-B卡激活失败   0x07*/
	NDK_ERR_RFID_ASEEK=				-2017,  /**<非接触卡-A卡寻卡失败(可能多张卡存在)   0x0A*/
	NDK_ERR_RFID_BSEEK=				-2018,  /**<非接触卡-B卡寻卡失败(可能多张卡存在)   0x0B*/
	NDK_ERR_RFID_ABON=				-2019,  /**<非接触卡-A、B卡同时存在   0x0C*/
	NDK_ERR_RFID_UPED=				-2020,  /**<非接触卡-已经激活(上电)   0x0E*/
	NDK_ERR_RFID_NOTACTIV=			-2021,  /**<非接触卡-未激活*/
	NDK_ERR_RFID_COLLISION_A=       -2022,  /**<非接触卡-A卡冲突*/
	NDK_ERR_RFID_COLLISION_B=       -2023,  /**<非接触卡-B卡冲突*/
	NDK_ERR_RFID_AFON=				-2024,	/**<非接触卡-AF卡冲突*/
	NDK_ERR_RFID_BFON=				-2025,	/**<非接触卡-BF卡冲突*/

	NDK_ERR_MI_NOTAGERR=			-2030,  /**<非接触卡-无卡,				0xff*/
	NDK_ERR_MI_CRCERR=				-2031,  /**<非接触卡-CRC错,				0xfe*/
	NDK_ERR_MI_EMPTY=				-2032,  /**<非接触卡-非空,				0xfd*/
	NDK_ERR_MI_AUTHERR=				-2033,  /**<非接触卡-认证错,			0xfc*/
	NDK_ERR_MI_PARITYERR=			-2034,  /**<非接触卡-奇偶错,			0xfb*/
	NDK_ERR_MI_CODEERR=				-2035,  /**<非接触卡-接收代码错			0xfa*/
	NDK_ERR_MI_SERNRERR=            -2036,  /**<非接触卡-防冲突数据校验错	0xf8*/
	NDK_ERR_MI_KEYERR=              -2037,  /**<非接触卡-认证KEY错			0xf7*/
	NDK_ERR_MI_NOTAUTHERR=          -2038,  /**<非接触卡-未认证				0xf6*/
	NDK_ERR_MI_BITCOUNTERR=         -2039,  /**<非接触卡-接收BIT错			0xf5*/
	NDK_ERR_MI_BYTECOUNTERR=        -2040,  /**<非接触卡-接收字节错			0xf4*/
	NDK_ERR_MI_WriteFifo=           -2041,  /**<非接触卡-FIFO写错误			0xf3*/
	NDK_ERR_MI_TRANSERR=            -2042,  /**<非接触卡-传送操作错误		0xf2*/
	NDK_ERR_MI_WRITEERR=            -2043,  /**<非接触卡-写操作错误			0xf1*/
	NDK_ERR_MI_INCRERR=				-2044,  /**<非接触卡-增量操作错误		0xf0*/
	NDK_ERR_MI_DECRERR=             -2045,  /**<非接触卡-减量操作错误		0xef*/
	NDK_ERR_MI_OVFLERR=             -2046,  /**<非接触卡-溢出错误			0xed*/
	NDK_ERR_MI_FRAMINGERR=          -2047,  /**<非接触卡-帧错				0xeb*/
	NDK_ERR_MI_COLLERR=             -2048,  /**<非接触卡-冲突				0xe8*/
	NDK_ERR_MI_INTERFACEERR=        -2049,  /**<非接触卡-复位接口读写错		0xe6*/
	NDK_ERR_MI_ACCESSTIMEOUT=       -2050,  /**<非接触卡-接收超时			0xe5*/
	NDK_ERR_MI_PROTOCOLERR=			-2051,  /**<非接触卡-协议错				0xe4*/
	NDK_ERR_MI_QUIT=                -2052,  /**<非接触卡-异常终止			0xe2*/
	NDK_ERR_MI_PPSErr=				-2053,  /**<非接触卡-PPS操作错			0xe1*/
	NDK_ERR_MI_SpiRequest=			-2054,  /**<非接触卡-申请SPI失败		0xa0*/
	NDK_ERR_MI_NY_IMPLEMENTED=		-2055,  /**<非接触卡-无法确认的错误状态	0x9c*/
	NDK_ERR_MI_CardTypeErr=			-2056,  /**<非接触卡-卡类型错			0x83*/
	NDK_ERR_MI_ParaErrInIoctl=		-2057,  /**<非接触卡-IOCTL参数错		0x82*/
	NDK_ERR_MI_Para=				-2059,  /**<非接触卡-内部参数错			0xa9*/

	NDK_ERR_WIFI_INVDATA=           -3001,  /**<WIFI-无效参数*/
    NDK_ERR_WIFI_DEVICE_FAULT=      -3002,  /**<WIFI-设备状态出错*/
    NDK_ERR_WIFI_CMD_UNSUPPORTED=   -3003,  /**<WIFI-不支持的命令*/
    NDK_ERR_WIFI_DEVICE_UNAVAILABLE=-3004,  /**<WIFI-设备不可用*/
    NDK_ERR_WIFI_DEVICE_NOTOPEN=    -3005,  /**<WIFI-没有扫描到AP*/
    NDK_ERR_WIFI_DEVICE_BUSY=       -3006,  /**<WIFI-设备忙*/
    NDK_ERR_WIFI_UNKNOWN_ERROR=     -3007,  /**<WIFI-未知错误*/
    NDK_ERR_WIFI_PROCESS_INBADSTATE=-3008,  /**<WIFI-无法连接到AP*/
    NDK_ERR_WIFI_SEARCH_FAULT=      -3009,  /**<WIFI-扫描状态出错*/
    NDK_ERR_WIFI_DEVICE_TIMEOUT=    -3010,  /**<WIFI-设备超时*/
    NDK_ERR_WIFI_NON_CONNECTED=	    -3011,  /**<WIFI-非连接状态*/

    NDK_ERR_RFID_BUSY = -3101,                      /**<射频卡状态忙*/
    NDK_ERR_PRN_BUSY = -3102,                       /**<打印状态忙*/
    NDK_ERR_ICCARD_BUSY = -3103,                /**<IC卡状态忙*/
    NDK_ERR_MAG_BUSY = -3104,                       /**<磁卡状态忙*/
    NDK_ERR_USB_BUSY = -3105,                       /**<USB状态忙*/
    NDK_ERR_WLM_BUSY = -3106,                    /**<无线状态忙*/
	NDK_ERR_PIN_BUSY = -3107,					/*正处于PIN输入状态*/
	NDK_ERR_BT_BUSY = -3108,					/*正处于蓝牙忙状态*/

    NDK_ERR_BT_NOT_CONNECTED = -3201,   /**<蓝牙连接未建立*/
    

    NDK_ERR_POSNDK_BASE  = -4000,		/** POSNDK库 Libnl_ndk.so 的 ERROR错误前缀*/
    NDK_ERR_POSNDK_BUSY  = (NDK_ERR_POSNDK_BASE-1),		/** POSNDK 硬件忙*/
    NDK_ERR_POSNDK_TRANS_BUSY  = (NDK_ERR_POSNDK_BASE-2),		/** POSNDK 事务忙*/
    NDK_ERR_POSNDK_TRANS_ALREADY = (NDK_ERR_POSNDK_BASE-3),		/** POSNDK 已经在事务中*/
    NDK_ERR_POSNDK_TRANS_NOEXIST  = (NDK_ERR_POSNDK_BASE-4),		/** POSNDK 不在事务中*/
    NDK_ERR_POSNDK_SAFE_TRIGGER  = (NDK_ERR_POSNDK_BASE-5),		/** POSNDK 硬件安全触发*/

    NDK_ERR_POSNDK_VKB_INITERR =(NDK_ERR_POSNDK_BASE-17),	  /**虚拟键盘应用不存在或无法启动*/
    NDK_ERR_POSNDK_VKB_DATAERR  = (NDK_ERR_POSNDK_BASE-18),		/** POSNDK 虚拟键盘数据错*/

    NDK_ERR_POSNDK_EVENT_NUM = (NDK_ERR_POSNDK_BASE-6),		/** 错误的事件号*/
    NDK_ERR_POSNDK_EVENT_REG_TWICE = (NDK_ERR_POSNDK_BASE-7),		/** 重复注册事件*/
    NDK_ERR_POSNDK_EVENT_UNREG_TWICE = (NDK_ERR_POSNDK_BASE-8),		/**并未注册*/
    NDK_ERR_POSNDK_EVENT_INIT = (NDK_ERR_POSNDK_BASE-9),		/**初始化错*/
    NDKK_ERR_POSNDK_EVENT_INUSE =  (NDK_ERR_POSNDK_BASE-10),		/**事件被其它进程占用*/

    NDK_ERR_POSNDK_PERMISSION_UNDEFINED  = (NDK_ERR_POSNDK_BASE-21),		/** POSNDK 权限未声明*/
    NDK_ERR_POSNDK_ACCESS_BUSY  = (NDK_ERR_POSNDK_BASE-22),		/** POSNDK相关操作被其它进程占有 */

	NDK_ERR_LINUX_ERRNO_BASE=		-5000, /**<<LINUX>系统函数返回ERROR错误前缀*/
	NDK_ERR_LINUX_TCP_TIMEOUT=  (NDK_ERR_LINUX_ERRNO_BASE-110),/**<TCP远程端口错误*/
	NDK_ERR_LINUX_TCP_REFUSE=  (NDK_ERR_LINUX_ERRNO_BASE-111),/**<TCP远程端口被拒绝*/
	NDK_ERR_LINUX_TCP_NOT_OPEN=		 (NDK_ERR_LINUX_ERRNO_BASE-88),/**<TCP句柄未打开错误*/

	NDK_ERR_UNSUPPORT = -9999,
}EM_NDK_ERR;

/** @} */ // 函数错误码结束

typedef struct {
    unsigned char systemcode[2];
    unsigned char request_code;		
    unsigned char timeslot;			
}felica_param_t;


typedef enum{
	CARD_ALL_INFO,
	CARD_UID = 1,
	CARD_TYPEA_ATQA,
	CARD_TYPEA_ATS,
	CARD_TYPEB_ATQB,
	CARD_TYPEA_SAK,
	RFID_TYPE_END,
}card_info_type_t;

/** @addtogroup 显示
* @{
*/
#ifndef NL_COLOR_T
#define NL_COLOR_T
typedef unsigned int color_t;/**<RGB色彩数值,0(黑色) - 0xFFFF(白色)*/
#endif

/**
 *@brief 显示模式定义
*/
typedef enum {
	TEXT_ATTRIBUTE_NORMAL = 1<<0,			/**<字体正常显示*/
	TEXT_ATTRIBUTE_REVERSE = 1<<1,		/**<字体反向显示*/
	TEXT_ATTRIBUTE_UNDERLINE = 1<<2,		/**<字体加下划线*/
	TEXT_ATTRIBUTE_NOBACKCOLOR = 1<<3,		/**<字体无背景色*/
}EM_TEXT_ATTRIBUTE;

/**
 *@brief 背光控制
*/
typedef enum {
	BACKLIGHT_OFF ,			/**<点亮液晶背光*/
	BACKLIGHT_ON,			/**<关闭液晶*/
	BACKLIGHT_LOCKON,		/**<禁止液晶背光自动关闭*/
}EM_BACKLIGHT;

/**
 *@brief 系统字体选择定义
*/
typedef enum {
	DISPFONT_CUSTOM,			/**<系统默认的显示字体*/
	DISPFONT_EXTRA,			/**<额外尺寸的显示字体*/
	DISPFONT_USER,				/**<用户自定义字体*/
}EM_DISPFONT;

/**
 *@brief 系统字体颜色设置定义
*/
typedef enum {
	FONTCOLOR_NORMAL,				/**<字体正常显示时字体颜色*/
	FONTCOLOR_REVERSE,				/**<字体反向显示时字体颜色*/
	FONTCOLOR_BG_REVERSE,				/**<字体反向显示时背景颜色*/
}EM_FONTCOLOR;

/**
 *@brief 矩形填充模式定义
*/
typedef	enum  {
    RECT_PATTERNS_NO_FILL,        /**<非填充，只绘制边框*/
    RECT_PATTERNS_SOLID_FILL      /**<填充模式*/
}EM_RECT_PATTERNS;

/**
 *@brief	用户程序界面初始化。
 *@details  建议在程序最初调用，该函数成功调用后，显示模块的API才能正常使用。
 *@return
 *@li	    NDK_OK		                        操作成功
 *@li       \ref NDK_ERR_INIT_CONFIG "NDK_ERR_INIT_CONFIG"                  初始化配置失败
 *@li       \ref NDK_ERR_CREAT_WIDGET "NDK_ERR_CREAT_WIDGET"                 创建界面错误
*/
extern int NDK_ScrInitGui(void);/**	*@example NDK_disp_example.c**/

/**
 *@brief	获取显示模块版本。
 *@retval	pszVer	返回模块版本,输入的pszVer应该不小于16字节。
 *@return
 *@li	    NDK_OK	                操作成功
 *@li       \ref NDK_ERR_PARA "NDK_ERR_PARA" 		    参数非法
*/
extern int NDK_ScrGetVer(char *pszVer);

/**
 *@brief	设置显示模式，并获取之前的显示模式。
 *@param	emNewAttr 	要设置的新显示模式。
 *@retval	pemOldAttr 	输出之前的显示模式，pemOldAttr为NULL时不返回。
 *@return
 *@li		NDK_OK					操作成功
 *@li		\ref NDK_ERR_PARA "NDK_ERR_PARA" 		    参数非法
*/
extern int NDK_ScrSetAttr(EM_TEXT_ATTRIBUTE emNewAttr, EM_TEXT_ATTRIBUTE *pemOldAttr);

/**
 *@brief	保存当前屏幕
 *@details	包括显示内容、光标位置及显示模式，该保存结果可调用\ref NDK_ScrPop "NDK_ScrPop"快速恢复显示。
			NDK_ScrPush与\ref NDK_ScrPop "NDK_ScrPop"成对使用，不能嵌套。
 *@return
 *@li	NDK_OK				操作成功
*/
extern int NDK_ScrPush(void);

/**
 *@brief	快速恢复利用\ref NDK_ScrPush "NDK_ScrPush"保存的显示状态，包括显示内容、光标位置及文本显示属性。
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败（未保存显示状态）
*/
extern int NDK_ScrPop(void);

/**
 *@brief	设置背景图片。图片文件支持格式请查看各机型的图片格式限制。
 *@param	pszFilePath 	图片文件路径
 *@return
 *@li	NDK_OK						操作成功
 *@li	\ref NDK_ERR_PATH "NDK_ERR_PATH" 		        文件路径非法
 *@li   \ref NDK_ERR_DECODE_IMAGE "NDK_ERR_DECODE_IMAGE"         图像解码失败
*/
extern int NDK_ScrSetbgPic(char *pszFilePath);

/**
 *@brief	取消背景图片。
 *@return
 *@li	NDK_OK				操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"		操作失败
*/
extern int NDK_ScrClrbgPic(void);

/**
 *@brief	清屏，把光标移到像素坐标(0,0)，同时将屏幕显示模式设置为\ref TEXT_ATTRIBUTE_NORMAL "TEXT_ATTRIBUTE_NORMAL"。
 *@return
 *@li	NDK_OK				操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"		操作失败
*/
extern int NDK_ScrClrs(void);

/**
 *@brief	清除若干行(单位：像素)，把光标移到(0,unStartY)处。\n
		  	输入参数超出视口边界时，以视口边界为准
 *@param	unStartY		开始行号（纵坐标，单位：像素），从0开始计数
 *@param	unEndY			结束行号（纵坐标，单位：像素），从0开始计数
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
*/
extern int NDK_ScrClrLine(uint unStartY,uint unEndY);


/**
 *@brief	液晶显示光标位置移到像素坐标(unX,unY)处。
			如果输入参数非法，则光标保留位置不变,返回错误信息。
 *@param	unX 	横坐标（单位：像素）
 *@param	unY 	纵坐标（单位：像素）
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
*/
extern int NDK_ScrGotoxy(uint unX, uint unY);


/**
 *@brief	获取当前像素光标位置的横坐标和纵坐标。
 *@retval	punX 返回光标位置的横坐标（单位：像素）。punX为NULL时表示不获取横坐标
 *@retval	punY 返回光标位置的纵坐标（单位：像素）。punY为NULL时表示不获取纵坐标
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
*/
extern int NDK_ScrGetxy(uint *punX, uint *punY);


/**
 *@brief	设置显示区域尺寸。
 *@details  未设置情况下显示区域为实际屏幕尺寸，通过该接口设置显示区域后，其它API的显示操作只在该区域内有效。\n
			如设置(10,10,100,100)为应用显示区域，则应用程序中使用的像素坐标(0,0)实际上
			是屏幕像素坐标(10,10),清屏操作也只清除像素坐标(10,10,100,100)范围内的显示数据。
 *@param	unX		应用程序显示区域起始坐标的横坐标（单位：像素）。
 *@param	unY		应用程序显示区域起始坐标的横坐标（单位：像素）。
 *@param	unWidth	应用程序显示区域宽度（单位：像素）。
 *@param	unHeight 	应用程序显示区域高度（单位：像素）。
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
*/
extern int NDK_ScrSetViewPort(uint unX,uint unY,uint unWidth, uint unHeight);

/**
 *@brief	获取当前显示区域尺寸。
 *@retval	punX		显示区域起始坐标的横坐标（单位：像素）。
 *@retval	punY		显示区域起始坐标的纵坐标（单位：像素）。
 *@retval	punWidth	显示区域高度（单位：像素）。
 *@retval	punHeight	显示区域高度（单位：像素）。
 *@return
 *@li	NDK_OK				操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"		操作失败
*/
extern int NDK_ScrGetViewPort(uint *punX,uint *punY,uint *punWidth,uint *punHeight);


/**
 *@brief	在显示区域显示Bitmap图片。
 *@details  bitmap格式：1byte对应8个像素点,0表示白点，1表示黑点，显示数据横向排列，如下图所示:\n
-----------------D7~~D0--------------D7~~D0------------------\n
Byte 1: ○ ○ ○ ○ ○ ○ ○ ○  ○  ○ ○ ○ ○ ○ ○ ○ Byte2	\n
Byte 3: ○ ○ ○ ○ ○ ○ ○ ○  ○  ○ ○ ○ ○ ○ ○ ○ Byte4	\n
Byte 5: ○ ○ ○ ○ ○ ○ ○ ○  ○  ○ ○ ○ ○ ○ ○ ○ Byte6	\n
Byte 7: ○ ○ ● ● ● ● ● ●  ●  ● ● ● ● ● ○ ○ Byte8	\n
Byte 9: ○ ○ ○ ○ ○ ○ ○ ○  ○  ○ ○ ○ ○ ○ ○ ○ Byte10	\n
Byte11: ○ ○ ○ ○ ○ ○ ○ ○  ○  ○ ○ ○ ○ ○ ○ ○ Byte12	\n
Byte13: ○ ○ ○ ○ ○ ○ ○ ○  ○  ○ ○ ○ ○ ○ ○ ○ Byte14	\n
Byte15: ○ ○ ○ ○ ○ ○ ○ ○  ○  ○ ○ ○ ○ ○ ○ ○ Byte16	\n
---------------------------------------------------------------\n
	如果显示图片范围超出屏幕范围或通过\ref NDK_ScrSetViewPort "NDK_ScrSetViewPort()"设置的用户使用区域时，则该函数操作无效，返回调用失败。
 *@param	unX 		图片在显示区域的左上角横坐标（单位：像素）
 *@param	unY 		图片在显示区域的左上角纵坐标（单位：像素）
 *@param	unWidth 	图片宽度（单位：像素）
 *@param	unHeight 	图片高度（单位：像素）
 *@param	psBuf 		Bitmap图片数据
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
*/
extern int NDK_ScrDrawBitmap(uint unX,uint unY,uint unWidth, uint unHeight, const char *psBuf);
extern int NDK_ScrDrawBitmapV(uint unX,uint unY,uint unWidth,uint unHeight, const uchar *psBuf);

/**
 *@brief	从显示区域上像素坐标(unStartX,unStartY)到(unEndX,unEndY)画直线，unColor表示画线的RGB色彩值。
 *@param	unStartX 	直线的起点横坐标（单位：像素）
 *@param	unStartY 	直线的起点纵坐标（单位：像素）
 *@param	unEndX 		直线的终点横坐标（单位：像素）
 *@param	unEndY 		直线的终点纵坐标（单位：像素）
 *@param	unColor 		颜色数值 <0-0xFFFF>
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
*/
extern int NDK_ScrLine(uint unStartX, uint unStartY, uint unEndX, uint unEndY, color_t unColor);


/**
 *@brief	在显示区域画一个矩形。
 *@details	如果矩形边界超出屏幕范围或通过\ref NDK_ScrSetViewPort "NDK_ScrSetViewPort()"设置的用户使用区域时，则该函数操作无效，返回调用失败。
 *@param	unX 		矩形的起点横坐标（单位：像素）
 *@param	unY 		矩形的起点纵坐标（单位：像素）
 *@param	unWidth 		矩形的宽（单位：像素）
 *@param	unHeight 		矩形的高（单位：像素）
 *@param	emFillPattern 	0为填充模式，1为填充模式
 *@param	unColor	颜色数值 <0-0xFFFF>
 *@return
 *@li	NDK_OK				操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"		操作失败
*/
extern int NDK_ScrRectangle(uint unX, uint unY, uint unWidth, uint unHeight, EM_RECT_PATTERNS emFillPattern, color_t unColor);


/**
 *@brief	在显示区域显示指定的图片，图片文件支持格式请查看各机型的图片格式限制。
 *@details	如果显示图片范围超出屏幕范围或通过\ref NDK_ScrSetViewPort "NDK_ScrSetViewPort()"设置的用户使用区域时，则该函数操作无效，返回调用失败。
 *@param	unX 		图片显示的左上角横坐标（单位：像素）
 *@param	unY 		图片显示的左上角纵坐标（单位：像素）
 *@param	unWidth 	图片的宽（单位：像素）
 *@param	unHeight 	图片显示的高（单位：像素）
 *@param	pszPic 	图片文件所在的路径。
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
 *@li	\ref NDK_ERR_DECODE_IMAGE "NDK_ERR_DECODE_IMAGE" 		图像解码失败
*/
extern int NDK_ScrDispPic(uint unX,uint unY,uint unWidth, uint unHeight, const char *pszPic);

/**
 *@brief	取显示区域上指定像素坐标点的颜色数值。
 *@param	unX 		横坐标（单位：像素）
 *@param	unY 		纵坐标（单位：像素）
 *@retval	punColor	返回的颜色值。
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
*/
extern int NDK_ScrGetPixel(uint unX, uint unY, color_t *punColor);


/**
 *@brief	在显示区域上指定像素坐标画点。
 *@param	unX 		横坐标（单位：像素）
 *@param	unY		纵坐标（单位：像素）
 *@param	unColor 	颜色数值 <0-0xFFFF>
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
*/
extern int NDK_ScrPutPixel(uint unX, uint unY, color_t unColor);


/**
 *@brief	将显存中用户设置的显示区域内的数据刷新到液晶屏上显示。
 *@details	系统缺省为自动刷新。为避免闪屏，可以通过\ref NDK_ScrAutoUpdate "NDK_ScrAutoUpdate()"设置为非自动刷新，在NDK_ScrRefresh
			调用后系统才将显存中的数据刷新到液晶屏上。
 *@return
 *@li	NDK_OK				操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"		操作失败
*/
extern int NDK_ScrRefresh(void);

/**
 *@brief	将显存中全部数据刷新到LCD全屏上显示。
 *@details	该接口与\ref NDK_ScrRefresh "NDK_ScrRefresh()"区别在于不受限于显示区域的大小，当通过\ref NDK_ScrSetViewPort "NDK_ScrSetViewPort()"设置的显示区域为全屏时
			，则\ref NDK_ScrRefresh "NDK_ScrRefresh()"与该接口效果一样
 *@return
 *@li	NDK_OK				操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"		操作失败
*/
extern int NDK_ScrFullRefresh(void);

/**
 *@brief	设置是否自动刷新。
 *@param	nNewAuto
					非0:自动刷新
					0:不自动刷新，只有调用\ref NDK_ScrRefresh "NDK_ScrRefresh()"才显示显存中的数据。
 *@retval	pnOldAuto	返回设置之前的自动刷新状态。
 *@return
 *@li	NDK_OK				操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"		操作失败
*/
extern int NDK_ScrAutoUpdate(int nNewAuto, int *pnOldAuto);


/**
 *@brief	获取液晶屏尺寸。
 *@retval		punWidth	返回LCD宽度（单位：像素）。支持punWidth为NULL
 *@retval		punHeight	返回LCD高度（单位：像素）。支持punHeight为NULL
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
*/
extern int NDK_ScrGetLcdSize(uint *punWidth,uint *punHeight);

/**
 *@brief	获取液晶屏色深。
 *@details	可用于判断液晶屏是单色屏或彩屏
 *@retval		punColorDepth	返回液晶屏色深：1----黑白两色,
										16----16位色，彩屏
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
*/
extern int NDK_ScrGetColorDepth(uint *punColorDepth);

/**
 *@brief	开关背光操作。
 *@param	emBL	BACKLIGHT_OFF ? 关闭液晶背光
 					BACKLIGHT_ON ?打开液晶背光
 					BACKLIGHT_LOCKON ?液晶背光常亮
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
*/
extern int NDK_ScrBackLight(EM_BACKLIGHT emBL);

/**
 *@brief		设置屏幕对比度，只对黑白屏有效。
 *@param		unContrast	对比度级别为0~63,0最淡,63最深,默认为32。
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		打开设备文件错误
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误
*/
extern int NDK_ScrSetContrast(uint unContrast);


/**
 *@brief	设置使用的显示字体。
 *@details  系统默认支持2种尺寸点阵大小的字体可供切换,不同机型设定的系统默认字体尺寸不一定相同\n\n
			彩屏支持:\n汉字16x16 ASCII:8x16 (DISPFONT_CUSTOM)\n
					 汉字24x24 ASCII:12x24 (DISPFONT_EXTRA)\n
			黑白屏支持:\n汉字12x12 ASCII:6x12 (DISPFONT_CUSTOM)\n
						汉字16x16 ASCII:8x16 (DISPFONT_EXTRA)\n\n
			设置显示字体对后续显示字体起作用，对之前已刷新显示的内容无效
 *@param	emType	选择字体
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
 *@li	\ref NDK_ERR "NDK_ERR" 		操作失败(设置用户自定义字体失败)
*/
extern int NDK_ScrSetFontType(EM_DISPFONT emType);


/**
 *@brief	获取当前系统使用的汉字显示字体宽和高。
 *@details  系统使用的ASCII字符字体点阵宽度固定为汉字的一半
 *@retval	punWidth	返回当前系统当前显示字体的汉字点阵宽,支持为NULL
 *@retval	punHeight	返回当前系统当前显示字体的汉字点阵高,支持为NULL
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
*/
extern int NDK_ScrGetFontSize(uint *punWidth,uint *punHeight);


/**
 *@brief		添加用户自定义字体。
 *@details		设置成功后，通过 \ref NDK_ScrSetFontType "NDK_ScrSetFontType" 设置类型\ref DISPFONT_USER来使用自定义的字体\n
				注意:为了系统统一处理时显示排版，英文字库宽必须为汉字字库宽的一半，高度与汉字字库相同。
 *@param		psCnPath	汉字字库。
 *@param		psEnPath	英文字库。
 *@return
 *@li	NDK_OK				操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"		操作失败
*/
extern int NDK_ScrFontAdd(const char *psCnPath,const char *psEnPath);


/**
 *@brief	设置行间距和字间距。
 *@details  unWspace大于当前使用的英文字体像素宽度的2倍时，字间距设为英文字体像素宽度的2倍
			unHspace大于当前使用的英文字体像素高度时，行间距设为英文字体像素高度
 *@param	unWspace	字间距（单位：像素）
 *@param	unHspace		行间距（单位：像素）
 *@return
 *@li	NDK_OK				操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"		操作失败
*/
extern int NDK_ScrSetSpace(uint unWspace,uint unHspace);


/**
 *@brief	设置字体颜色，包含正显、反显、反显背景色。
 *@param	unColor	颜色数值
 *@param	emType	选择设置对象

 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
*/
extern int NDK_ScrSetFontColor(color_t unColor, EM_FONTCOLOR emType);



/**
 *@brief	显示字符串。
 *@param	unX	显示字符串位置的横坐标
 *@param	unY	显示字符串位置的纵坐标
 *@param	pszS	要显示的字符串指针
 *@param	unMode	设置显示ASCII字符时尺寸\n
					1：使用小号英文字体，不适用于包含汉字的字符串\n
						黑白屏：8x8尺寸英文字体\n
						彩屏：8x16尺寸英文字体\n
					0：系统当前使用的显示字体中ASCII点阵尺寸\n
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
*/
extern int NDK_ScrDispString(uint unX,uint unY,const char *pszS,uint unMode);

/**
 *@brief	屏幕显示格式化输出，使用方法同printf
 *@param	psFormat	参数输出的格式
  *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
*/
extern int NDK_ScrPrintf(const char *psFormat, ...);

/**
 *@brief	控制屏幕底部显示状态栏
 *@details	未调用该接口时，系统默认是关闭状态栏显示，通过该接口关闭状态栏时，原状态栏显示位置的数据不会恢复，建议在刷新显示数据前设置关闭或打开状态栏
 *@param	unFlag	0 关闭状态栏显示\n
					1 完全显示状态栏，包括无线信号、时间、电量\n
					2 只显示时间\n
					4 只显示电量\n
					8 只显示无线信号
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
*/
extern int NDK_ScrStatusbar(uint unFlag);

/**
 *@brief	加载BDF字体
 *@details	用户自定义BDF格式的字体，支持加载多个BDF字体文件
 *@param	unFontId 自定义字体ID,如0,1,2等。如果unFontId与之前设置的BDF字体相同，规替换之前BDF字体
 *@param    pszFile BDF文件路径+文件名
 *@param    punWidth 获取该BDF字体最大像素宽,支持值为NULL
 *@param    punHeight 获取该BDF字体最大像素高,支持值为NULL
 *@return
 *@li	NDK_OK	操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
 *@li	\ref NDK_ERR "NDK_ERR" 		BDF字体文件格式无法识别
*/
extern int NDK_ScrLoadBDF(uint unFontId,char *pszFile,uint *punWidth,uint *punHeight);


/**
 *@brief	使用已加载的BDF字体显示数据
 *@details
 *@param	unFontId \ref NDK_ScrLoadBDF "NDK_ScrLoadBDF()"加载字体的相应ID号
 *@param	unX	显示字符串位置的横坐标
 *@param	unY	显示字符串位置的纵坐标
 *@param    pusText 显示的字符串，其值是BDF文件中字符相对应的编码值，内码或UNICODE，具体视BDF文件而定
 *@return
 *@li	NDK_OK	操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
 *@li	\ref NDK_ERR "NDK_ERR" 		BDF字体文件格式无法识别
*/
extern int NDK_ScrDispBDFText(uint unFontId,uint unX,uint unY,ushort *pusText);

/** @} */ // 显示模块结束

/** @addtogroup 键盘
* @{
*/
/**
 *@brief 功能键值定义
*/
#define  K_F1		0x01
#define  K_F2		0x02
#define  K_F3		0x03
#define  K_F4		0x04
#define  K_F5		0x05
#define  K_F6		0x06
#define	 K_F7		0x07
#define	 K_F8		0x08
#define	 K_F9		0x09
#define  K_BASP		0x0a    /**<退格键*/
#define  K_ENTER		0x0D	/**<确认键*/
#define  K_ESC		0x1B	/**<取消键*/
#define	 K_ZMK		0x1C	/**<字母键*/
#define	 K_DOT		0x2E	/**<小数点*/
#define	 K_QUIT		0x9B	//退出键
#define	 K_CLEAR	0x9C  	//清空键

/**
 *@brief 数字键值定义
*/
#define K_ZERO		0x30    /**<数字0键*/
#define K_ONE		0x31    /**<数字1键*/
#define K_TWO		0x32	/**<数字2键*/
#define K_THREE		0x33	/**<数字3键*/
#define K_FOUR		0x34	/**<数字4键*/
#define K_FIVE		0x35	/**<数字5键*/
#define K_SIX		0x36	/**<数字6键*/
#define K_SEVEN		0x37	/**<数字7键*/
#define K_EIGHT		0x38	/**<数字8键*/
#define K_NINE		0x39	/**<数字9键*/

/**
 *@brief 输入控制
*/
typedef enum {
	INPUT_CONTRL_NOLIMIT, 		/**<可输入任意ASCII码字符，输满后直接返回*/
	INPUT_CONTRL_LIMIT,		/**<只允许输入数字与小数点，输满后直接返回*/
	INPUT_CONTRL_NOLIMIT_ERETURN,		/**<可输入任意ASCII码字符，输满后等待确认键*/
	INPUT_CONTRL_LIMIT_ERETURN,			/**<只允许输入数字与小数点，输满后等待确认键返回*/
}EM_INPUT_CONTRL;

/**
 *@brief 输入字符显示控制
*/
typedef enum {
	INPUTDISP_NORMAL, 		/**<输入内容正常显示字符*/
	INPUTDISP_PASSWD,			/**<输入内容显示为'*' */
	INPUTDISP_OTHER,			/**<支持带预设值的接收输入缓冲*/
}EM_INPUTDISP;

/**
 *@brief 输入法类型
*/
typedef enum {
    IME_NUMPY,	  /**<数拼*/
    IME_ENGLISH,  /**<英文*/
	IME_NUM,	  /**<数字*/
	IME_BIHUA,	  /**<笔画*/
    IME_QUWEI,	  /**<区位*/
    IME_GBK,      /**<内码*/
	IME_HANDWRITE, /**<手写*/
    IME_MAXNUM=IME_HANDWRITE,	/**<仅用于错误判断*/
}EM_IME;
/**
 *@brief 触屏坐标状态
*/
typedef enum{
        PADSTATE_UP,             /**<像素坐标点弹起*/
        PADSTATE_DOWN,       /**<像素坐标点按下*/
        PADSTATE_KEY,           /**<有按键被按下*/
}EM_PADSTATE;
/**
 *@brief 触屏信息
*/
typedef struct __st_PADDATA{
        uint unPadX;    /**<横坐标*/
        uint unPadY;    /**<纵坐标*/
        uint unKeycode;/**<按键值*/
        EM_PADSTATE emPadState;     /**<坐标状态*/
}ST_PADDATA;
/**
 *@brief	清除键盘缓冲区。
 *@return
 *@li		NDK_OK			   操作成功
*/
extern int (*NDK_KbFlush)(void);

/**
 *@brief	开关长按键或组合键功能（暂未支持）。
 *@param	nSelect 	0   关闭 1   开启
 *@param	nMode 		0   长按键  1   组合键
 *@param	pnState 	获取长按键或组合键原来的状态，0--关闭 1---开启。
 *@return
 *@li		NDK_OK				  操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"    操作失败
*/
extern int NDK_KbSwitch(int nSelect, int nMode,int *pnState);

/**
 *@brief 	获取长按键或组合键的开关状态（暂未支持）。
 *@param	nMode 	0   长按键  1   组合键
 *@param	pnState 	获取长按键或组合键状态，0--关闭 1---开启。
 *@return
 *@li		NDK_OK 				   操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"	   操作失败
*/
extern int NDK_KbGetSwitch(int nMode,int *pnState);

/**
 *@brief 	超时时间内读取键盘按键值
 *@details	在规定的时间里读按键，读键过程如下:按下一个键，等待放开，返回键码。
 *@param	unTime	小于等于0 :无超时，一直等待读按键
							其他:为等待时间(以秒为单位)
 *@param	pnCode	获取输入键码，若在规定的时间内没有按键按下，pnCode的值为0
 *@return
 *@li       NDK_OK 				   操作成功
 *@li   	\ref NDK_ERR "NDK_ERR" 	                操作失败
*/
extern int (*NDK_KbGetCode)(uint unTime, int *pnCode);

/**
 *@brief	获取缓冲区中的首个键盘键值，立即返回
 *@details	检查按键缓冲区是否有按键，若有读键，返回键码,若没有键按下立即返回0。
   			一般该API是在一个程序循环体使用，并且使用之前应该\ref NDK_KbFlush "NDK_KbFlush"把缓冲区清除。
 			与\ref NDK_KbGetCode "NDK_KbGetCode"区别于本函数不进行等待，而是立即返回。
 *@param	pnCode	获取输入键码，无按键按下时pnCode的值为0
 *@return
 *@li        	NDK_OK 				   操作成功
 *@li   		\ref NDK_ERR_PARA "NDK_ERR_PARA" 	   参数非法
*/
extern int (*NDK_KbHit)(int *pnCode);

/**
*@brief		输入字符串
*@details	从键盘读入一个以换行符为终结符的字符串，将其存入缓冲区pszBuf中。
			ESC键返回操作失败,回车读键完成返回,其他功能键无效。
*@param		pszBuf	接收字符串数据
*@param		unMin	最小输入串长
*@param		unMaxLen	最大输入串长
*@param		punLen	获取实际输入串的长度(>0)
*@param		emMode	显示类型，
					取值INPUTDISP_NORMAL时显示字符，
					取值INPUTDISP_PASSWD时显示'*'。
					取值为INPUTDISP_OTHER时，pcBuf若有内容（以\0为结尾的字符串）相当于已经从键盘上输入的数据,并且用明码显示出来。
*@param		unWaitTime	等待输入的时间，若是0一直等待，其他值为等待的秒数。若超时没有按下回车键，自动返回，返回TimeOut。
*@param		emControl	INPUT_CONTRL_NOLIMIT：任意ASCII码字符，输满后直接返回\n
						INPUT_CONTRL_LIMIT：只读数字与小数点，输满后直接返回\n
						INPUT_CONTRL_NOLIMIT_ERETURN：任意ASCII码字符，输满后等待确认键返回\n
						INPUT_CONTRL_LIMIT_ERETURN，只读数字与小数点，输满后等待确认键返回\n
 *@return
 *@li        	NDK_OK 			   操作成功
 *@li   		\ref NDK_ERR_PARA "NDK_ERR_PARA" 	   参数非法
 *@li           \ref NDK_ERR "NDK_ERR"             操作失败
*/
extern int (*NDK_KbGetInput)(char *pszBuf,uint unMin,uint unMaxLen,uint *punLen,EM_INPUTDISP emMode,uint unWaitTime, EM_INPUT_CONTRL emControl);

/**
 *@brief	汉字输入法
 *@details 	通过按“字母键”选择输入法。
			输入步骤：
			a. 在拼音输入栏直接输入拼音如“xin”输入“946”，并选择。按“退格”清除输入，按方向键进行左右移动拼音选择。
			b. 按确认”进入备选汉字栏，选择需要的汉字，方向键进行左移与右移。如果按“退格”键可退回‘a’步骤。
    					按“确认”键选中需要的汉字。
			c. 在输入法状态下，在选字的时候，可以通过方向键左右切换。
	 		标点符号输入：
				在增加开启输入法的情况下，按数字键“0”出现标点符号，按方向键进行左右移动选择符号，按确认键返选定符号；

 *@param	pszS 		接收数据字符串，pcS若有内容（以\0为结尾的字符串）相当于已经从键盘上输入的数据。
 *@param	unMaxLen 	接收数据字符串的最大长度。
 *@param	emMethod		输入法选择,若是emMethod取EM_IME之外的其他值，则该函数默认激活数拼输入法。
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
 *@li   \ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC"              内存空间不足
 *@li   \ref NDK_ERR_NOT_SUPPORT "NDK_ERR_NOT_SUPPORT"              不支持该功能
*/
extern int NDK_KbHZInput(char *pszS,uint unMaxLen,EM_IME emMethod);

/**
 *@brief	获取像素坐标点的触屏状态
  *@details	该API可以获取像素点的触屏状态(\ref EM_PADSTATE "EM_PADSTATE"),该API也可以获取按键值。\n
                                在使用该API的时候要注意:如果在调用该API之后再调用\ref NDK_KbHit "NDK_KbHit()"或者\ref NDK_KbGetCode "NDK_KbGetCode()"
                                会影响触屏坐标状态的返回。\n通过该API获取按键值时，当状态值返回\ref   PADSTATE_KEY "PADSTATE_KEY" 时就
                                表示此时有按键被按下，所获取的按键值在pstPaddata->unKeycode中。                                                           
 *@param	pstPaddata	触屏点坐标信息(参考\ref ST_PADDATA "ST_PADDATA")
 *@param	unTimeOut	超时时间(0表示阻塞，大于0表示非阻塞超时时间值为unTimeOut单位为:毫秒)
 *@return
 *@li        	NDK_OK 				   操作成功
 *@li   	\ref NDK_ERR_PARA     "NDK_ERR_PARA"	   参数非法(pstPaddata为NULL)
 *@li   	\ref NDK_ERR_TIMEOUT  "NDK_ERR_TIMEOUT"	  超时错误
*/
extern int NDK_KbGetPad(ST_PADDATA *pstPaddata,uint unTimeOut);

/** @} */ // 键盘模块结束


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
	PRN_HZ_FONT_12x12A ,			/**<宋体12x12点阵*/
	PRN_HZ_FONT_16x24 ,
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
	PRN_ZM_FONT_12x16A ,       /**<MSGothic粗体12x16点阵*/
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
	PRN_ZM_FONT_12x24BL
}EM_PRN_ZM_FONT;

/**
 *@brief  打印机状态以及错误定义，取打印机状态返回值存在两个或多个或上的关系
*/

typedef enum{
	PRN_STATUS_OK = 0,			/**<打印机正常*/
	PRN_STATUS_BUSY = 8,		/**<打印机正在打印*/
	PRN_STATUS_NOPAPER = 2,       /**<打印机缺纸*/
	PRN_STATUS_OVERHEAT = 4,      /**<打印机过热*/
	PRN_STATUS_VOLERR = 112,       /**<打印机电压异常*/
	PRN_STATUS_DESTROYED = 1024,		/**<打印机故障*/
	PRN_STATUS_PPSERR = 2048, 	   /**<打印滚轴不在正确位置*/
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
    PRN_TYPE_TP = 0,             /**<热敏打印机*/
    PRN_TYPE_HIP,              /**<穿孔针打*/
    PRN_TYPE_FIP,              /**<摩擦针打*/
    PRN_TYPE_END               /**<无*/
}EM_PRN_TYPE;

/**
 *@brief  打印机打印模式
*/
typedef enum{
	PRN_MODE_ALL_DOUBLE = 0,			/**<横向放大、纵向放大*/
	PRN_MODE_WIDTH_DOUBLE ,			/**<横向放大、纵向正常*/
	PRN_MODE_HEIGHT_DOUBLE,       /**<横向正常、纵向放大*/
	PRN_MODE_NORMAL,               /**<横向正常、纵向正常*/
	PRN_MODE_ALL_TRIPLE,				
	PRN_MODE_WIDTH_TRIPLE,				
	PRN_MODE_HEIGHT_TRIPLE				
}EM_PRN_MODE;


/**
 *@brief 下划线功能开关的枚举类型值
*/
typedef enum{
	PRN_UNDERLINE_STATUS_OPEN = 0,			/**<下划线功能开*/
	PRN_UNDERLINE_STATUS_CLOSE			   /**<下划线功能关*/
}EM_PRN_UNDERLINE_STATUS;

/**
 *@brief 字库注册信息
*/
typedef struct
{
	int nOffset;						/**<需要获取的字在字库中的依移量*/
	int nFontByte;					   /**<需要获取的字在字库中占字节数*/
}ST_PRN_RECMSG;


/**
 *@brief 下划线功能开关的枚举类型值
*/
typedef struct
{
	char *pszName;	    			/**<字库名或字库所在路径*/
	int  nNum;				    	/**<注册号,支持0~7注册号*/
	int  nDirection;   				/**<0:横向字体； 1:纵向字体*/
	int  w;							/**<字体显示的宽度*/
	int  h;							/**<字体显示的高度*/
	int  (*func)(char *, ST_PRN_RECMSG *);	/**<获取依移地址和占用字节数的函数*/
	int  nIsHZ;                     /**<注册的字库是汉字字库还是ASCII字库，1:HZ  0:ZM*/
}ST_PRN_FONTMSG;

/*
*@brief 操作热敏打印机在打印单页前或打印完成后的进退纸操作的枚举类型
*/
typedef enum {
    PRN_FEEDPAPER_BEFORE = 0,         /**<单页打印前退纸*/
    PRN_FEEDPAPER_AFTER             /**<单页打印完成后进纸*/
}EM_PRN_FEEDPAPER;

/**
 *@brief  打印纸张尺寸
*/
typedef enum{
	PRN_PAPER_SIZE_2INCH = 2, /**2英寸*/
    PRN_PAPER_SIZE_3INCH = 3  /**3英寸*/    
}EM_PRN_PAPER_SIZE;


/**
用在X5机器
*/
extern int (*NDK_PrnModuleInit)();
extern int (*NDK_PrnCutterPerformance)();
extern int (*TTF_PrnExit)();
extern int (*TTF_PrnSetPaperSize)(EM_PRN_PAPER_SIZE size);
extern int (*TTF_ScriptPrint)(const char* pszBuf);
extern int (*TTF_PrnApiLoad)();
extern int (*TTF_GetVersion)(char *version);

typedef enum {
	PRN_GET_VALUE_UNKNOWN,
	PRN_GET_BATTERY_VOL_VALUE,
	PRN_GET_HEAT_VOL_VALUE,
	PRN_GET_PAPER_VALUE,
	PRN_GET_TEMP_VALUE,
	PRN_GET_VALUE_END = 0xffffffff,
} EM_PRN_GET_ERR_STATUS_VALUE;

/**
 *@brief        获取adc值
 *@details      该接口只允许非打印状态下调用
 *@param        type  获取adc值类型，具体参照EM_PRN_GET_ERR_STATUS_VALUE枚举
 *@retval       alg_value 返回硬件真实值(比如打印机真实温度值，机器当前电压值)
 *@retval       dgt_value adc值
 *@return
 *@li   NDK_OK              操作成功
 *@li   \ref NDK_ERR_PARA "NDK_ERR_PARA"        参数错误
 *@li   \ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"        打印设备打开失败
 *@li   \ref NDK_ERR_NOT_SUPPORT "NDK_ERR_NOT_SUPPORT"        不支持该功能
*/
extern int (*NDK_PrnGetStatusValue)(EM_PRN_GET_ERR_STATUS_VALUE type_value, int *alg_value, int *dgt_value);


extern int (*Png_Pint)(char * file,uint pos,int au);
extern int (*setyu)(int a);
/**
 *@brief 		  打印机初始化
 *@details  	包含清缓冲区,重置打印参数(包括字体、边距和模式)。
 *@param      unPrnDirSwitch  是否开启边送边打功能。
              0--关闭边送边打功能(默认)
	              在该模式下，所有的NDK_PrnStr,NDK_PrnImage都完成点阵转换工作，将点阵数据存到数据缓冲区，
	              在调用NDK_PrnStart之后才开始所有和打印相关的工作，包括走纸和打印。
              1--开启边送边打功能
	              在该模式下，只要满一行数据，就会送驱动打印，调用\ref NDK_PrnFeedByPixel "NDK_PrnFeedByPixel()"，将立即走纸返回。
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		打印设备打开失败
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调试错误(清打印缓冲区失败、打印重新设置失败)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 		内存空间不足
*/
extern int (*NDK_PrnInit)(uint unPrnDirSwitch);
/**
 *@brief 		打印字符串
 *@details 		该函数负责转换所有打印的字符串到点阵缓冲区，打印工作在调用Start之后开始送数打印。该函数为纯软件操作。
 *@param		pszBuf 为以\0为结尾的串,串的内容可为ASC码，汉字 换行"\n"或"\r"(表示结束本行，对于空行则直接进纸)。
 				当pszBuf里面有汉字和ASCII的混合串时,字母和汉字只和最近一次设置有关。
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		操作失败
 *@li	\ref NDK_ERR_INIT_CONFIG "NDK_ERR_INIT_CONFIG" 		初始化配置失败(打印未初始化)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 		内存空间不足
 *@li   \ref EM_PRN_STATUS   "EM_PRN_STATUS"   打印机状态值
*/
extern int (*NDK_PrnStr)(const char *pszBuf);
/**
 *@brief 		获取打印机通道采样值(底层暂未支持)
 *@details 	打印机通道采样值获取。
 *@param	emChanelNum    	AD通道号(参考\ref EM_PRN_CHANELNUM "EM_PRN_CHANELNUM")
 *@retval 	punAdValue     	AD采样值(电压的值以V为单位，温度以0.1C为单位)
 *@return
 *@li	NDK_OK				操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"		操作失败
*/
extern int (*NDK_PrnGetAd)(EM_PRN_CHANELNUM emChanelNum,uint *punAdValue);
/**
 *@brief 		开始启动打印.
 *@details 	NDK_PrnStr和NDK_PrnImage都是完成数据转换成点阵存储到缓冲区中工作，调用该函数开始送数打印。
			 			调用NDK_PrnStart打印结束后要判断返回值是否为0，如果返回-1则说明向打印送数失败，则立即返回打印机状态值，不进行继续送数操作。
			 			\ref NDK_PrnStart "NDK_PrnStart()"打印结束之后会阻塞等待返回打印机状态的值(包括边送边打和等待打印模式)。应用可根据NDK_PrnStart返回的值来判断打印机状态是否正常。
			 			(如果返回的非打印机状态值或者NDK_OK，即其他系统错误时需要应用去取打印机状态，该可能性比较小)
 *@return
 *@li	NDK_OK				打印结束且取打印机状态正常
 *@li	\ref NDK_ERR_INIT_CONFIG "NDK_ERR_INIT_CONFIG" 		初始化配置失败(打印未初始化)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 		内存空间不足
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		打印设备打开失败
 *@li   \ref EM_PRN_STATUS   "EM_PRN_STATUS"   打印机状态值
*/
extern int (*NDK_PrnStart)(void);
/**
 *@brief 		打印图形(该函数也是转换打印点阵到点阵缓冲区，调用NDK_PrnStart开始打印)
 *@details  	热敏打最大宽度384个点。如果unXsize和unXpos相加之和大于上述宽度限制会返回失败，如果是横向放大模式的话不能超过384/2。
 *@param 		unXsize 图形的宽度（像素）
 *@param 		unYsize 图形的高度（像素）
 *@param 		unXpos  图形的左上角的列位置，且必须满足unXpos+unXsize<=ndk_PR_MAXLINEWIDE（正常模式为384，横向放大时为384/2，该参数为绝对坐标不受左边距影响）
 *@param 		psImgBuf 图象点阵数据,为横向排列，第一个字节第一行的前8个点，D7为第一个点
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误
*/
extern int (*NDK_PrnImage)(uint unXsize,uint unYsize,uint unXpos,const char *psImgBuf);
/**
 *@brief 		取打印驱动的版本信息.
 *@retval   pszVer 用于存储返回版本字符串
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		打印设备打开失败
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用失败(获取打印版本失败)
*/
extern int (*NDK_PrnGetVersion)(char *pszVer);
/**
 *@brief 		设置打印字体
 *@details  设置ASCII打印字体和汉字字体。应用层可参看底层和应用层的接口文件中的相关定义。
 *@param 	emHZFont 	设置汉字字体格式，0保持当前字体不变。
 *@param    emZMFont	设置ASCII字体格式，0保持当前字体不变。
 *@return
 *@li	NDK_OK				操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"		操作失败
*/
extern int (*NDK_PrnSetFont)(EM_PRN_HZ_FONT emHZFont,EM_PRN_ZM_FONT emZMFont);
/**
 *@brief		获取打印机状态值.
 *@details		在打印之前可使用该函数判断打印机是否缺纸。
 *@retval	    pemStatus 用于返回打印机状态值
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		打印设备打开失败
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用失败(获取打印状态失败)
*/
extern int (*NDK_PrnGetStatus)(EM_PRN_STATUS *pemStatus);
/**
 *@brief 	设置打印模式.
 *@param 	emMode 打印模式(默认是使用正常模式)
 *@param     unSigOrDou 打印单双向选择0--单向 1--双向(只对针打有效，热敏忽略)
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误
*/
extern int (*NDK_PrnSetMode)(EM_PRN_MODE emMode,uint unSigOrDou);
/**
 *@brief		设置打印灰度
 *@details		设置打印灰度(加热时间)，以便对于不同的打印纸进行打印效果微调.(只对热敏有效，对针打无效)
 *@param    unGrey 灰度值，范围0~5；0为最淡的效果，5为最浓的打印效果。打印驱动默认的灰度级别为3。
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误
*/
extern int (*NDK_PrnSetGreyScale)(uint unGrey);
/**
 *@brief  	设置打印左边界、字间距、行间距。在对打印机有效设置后将一直有效，直至下次
 *@param  	unBorder 左边距 值域为：0-288(默认为0)
 *@param    unColumn 字间距 值域为：0-255(默认为0)
 *@param    unRow 行间距 值域为：0-255(默认为0)
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		打印设备打开失败
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用失败(左间距、字间距、行间距设置失败、图形对齐方式设置失败)
*/
extern int (*NDK_PrnSetForm)(uint unBorder,uint unColumn, uint unRow);
/**
 *@brief 	  按像素走纸
 *@details	让打印机走纸，参数为像素点。(边送边打模式下该函数为直接走纸，而等待Star打印模式下存储步数到缓冲区，等待star开始走纸)
 *@param    unPixel 走纸像素点 热敏值域为（0<=unPixel<=1024），针打为（-792<=unPixel<=792）
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误
 *@li	\ref NDK_ERR_INIT_CONFIG "NDK_ERR_INIT_CONFIG" 		初始化配置失败(打印未初始化配置)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 		内存空间不足
*/
extern int (*NDK_PrnFeedByPixel)(uint unPixel);

/**
 *@brief	打印是否开启下划线功能.
 *@param  emStatus 0：开下划线；1：关下划线
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误
*/
extern int (*NDK_PrnSetUnderLine)(EM_PRN_UNDERLINE_STATUS emStatus);

/**
 *@brief		自定义字体注册。(只对热敏有效)
 *@param		pstMsg \ref ST_PRN_FONTMSG "ST_PRN_FONTMSG"类型指针，使用自定义注册要完成相应信息的填写
 *@return
 *@li	NDK_OK				操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"		操作失败
*/
extern int (*NDK_PrnFontRegister)(ST_PRN_FONTMSG *pstMsg);
/**
 *@brief	根据注册号来选择打印字体.(只对热敏有效)
 *@param  unFontId 注册字体的id(该设置后会覆盖\ref NDK_PrnSetFont "NDK_PrnSetFont()"中设定的字体)
 *@return
 *@li	NDK_OK				操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"		操作失败
*/
extern int (*NDK_PrnSetUsrFont)(uint unFontId);
/**
 *@brief	获得该次打印的点阵行数.
 *@details 该函数只适用于等待调用star开启打印的情况，如果边送边打模式调用该函数，返回值是不准确的。该函数可以使用在调用NDK_PrnStar之前取缓冲区有多少数据的情况
 *@retval  punLine 返回行数
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 				参数错误
 *@li	\ref NDK_ERR_INIT_CONFIG "NDK_ERR_INIT_CONFIG" 		初始化配置失败(打印未初始化配置)
*/
extern int (*NDK_PrnGetDotLine)(uint *punLine);
/**
 *@brief	打印bmp，png和jpg格式的图片
 *@details  该函数将存储在pos上的图片进行解码后存储到点阵缓冲区，  不过图片解码会暂用一定的时间，必要的时候需要有一定的等待时间
 *@param  pszPath 图片所在的路径
 *@param  unXpos  图形的左上角的列位置，且必须满足xpos+xsize(解码后图片的宽度值)<=ndk_PR_MAXLINEWIDE（正常模式为384，横向放大时为384/2）
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 				参数错误
 *@li	\ref NDK_ERR_INIT_CONFIG "NDK_ERR_INIT_CONFIG" 		初始化配置失败(打印未初始化配置)
 *@li	\ref NDK_ERR_DECODE_IMAGE "NDK_ERR_DECODE_IMAGE" 				图像解码失败
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC"                  内存空间不足
*/
extern int (*NDK_PrnPicture)(uint unXpos,const char *pszPath);

/**
 *@brief	设置打印页长（只对针打有效）
 *@details   对打印机打印页长进行设置
 *@param  unLen	页长 值域为(0<=unLen <=792)
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误
 *@li	\ref NDK_ERR_NOT_SUPPORT "NDK_ERR_NOT_SUPPORT"        不支持该功能
*/
extern int (*NDK_PrnSetPageLen)(uint unLen);
/**
 *@brief	加载BDF字体
 *@details  使用该函数加载BDF字体到内存中，比较大的字体会耗费一些时间。
 *@param  pszPath BDF所在的路径
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_NOT_SUPPORT "NDK_ERR_NOT_SUPPORT"        不支持该功能
 *@li	\ref NDK_ERR "NDK_ERR"        操作失败(初始化链表头节点，使用calloc保证数据清0失败)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC"        内存空间不足
*/

extern int (*NDK_PrnLoadBDFFont)(const char *pszPath);
/**
 *@brief	打印BDF字体中的内容
 *@param  	pusText 要打印的数据。
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_NOT_SUPPORT "NDK_ERR_NOT_SUPPORT"        不支持该功能
*/

extern int (*NDK_PrnBDFStr)(ushort *pusText);

/**
 *@brief	设置BDF字体属性
 *@param  unXpos  左偏移 值域为：0-288(默认为0)
 *@param  unLineSpace  行间距 值域为：0-255(默认为0)
 *@param  unWordSpace  字间距 值域为：0-255(默认为0)
 *@param  unXmode  横向放大倍数(注意，字体的MaxWidth*unXmode必须不能超过384，否则失败)
 *@param  unYmode  纵向放大倍数(注意，字体的MaxHeight*unYmode必须不能超过48，否则失败)
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR "NDK_ERR"        操作失败(初始化链表头节点，使用calloc保证数据清0失败)
 *@li	\ref NDK_ERR_NOT_SUPPORT "NDK_ERR_NOT_SUPPORT"        不支持该功能
*/

extern int (*NDK_PrnSetBDF)(uint unXpos,uint unLineSpace,uint unWordSpace,uint unXmode,uint unYmode);

typedef enum {
	NAPI_PRN_FEEDPAPER_BEFORE = 0,         /**单页打印前退纸,暂不支持*/
	NAPI_PRN_FEEDPAPER_AFTER,             /**单页打印后进纸*/
} EM_NAPI_PRN_FEEDPAPER;
extern int (*NDK_PrnFeedPaper_Extern)(EM_NAPI_PRN_FEEDPAPER operation);

extern int (*NDK_PrnFeedPaper)();
extern void (*PrnInit)(void);
extern int (*NDK_Script_Print)(char* prndata,int indata_len);
extern int (*NDK_PrnQrcode)(char *in_str,uchar prn_p,uint qrcode_size,uchar err_corr_level);//打印二维码
extern int (*NDK_PrnBarcode)(char *in_str,uint in_len,uchar prn_p,uchar barcode_width,uint barcode_high);//打印一维码
extern int (*NDK_PrnImageZoom)(char *ptmp13,uchar prn_p,uint  pictureSizeX,uint pictureSizeY,uchar yspace);//打印log

/** @} */ // 打印模块结束


/** @addtogroup 文件系统
* @{
*/

/**
 *@brief 		打开文件.
 *@details
 *@param    pszName 文件名
 *@param    pszMode 打开模式 "r"(以只读方式打开，如果不存在则失败) or "w"(以写的方式打开，如果文件不存在则创建)。
 *@return
 *@li	 fd				操作成功返回文件描述符
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误(文件名为NULL、模式不正确)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		文件打开失败
*/
extern int (*NDK_FsOpen)(const char *pszName,const char *pszMode);


/**
 *@brief 		关闭文件.
 *@details
 *@param    nHandle 文件句柄
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 		操作失败(所关闭的文件不是调用NDK_FsOpen打开的、调用close()关闭失败)
*/
extern int (*NDK_FsClose)(int nHandle);

/**
 *@brief 		从打开的nHandle文件当前指针读unLength个字符到缓冲区psBuffer.
 *@details
 *@param    nHandle 文件句柄
 *@param    unLength	需要读取的字符的长度
 *@retval   psBuffer	需要读入的缓冲区注意要足够读入unLength字节
 *@return
 *@li	返回实际读到数据长度
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 				参数错误(psBuffer为NULL)
 *@li	\ref NDK_ERR_READ "NDK_ERR_READ" 		读文件失败(所读的文件不是调用NDK_FsOpen打开的、调用read()失败返回)
*/
extern int (*NDK_FsRead)(int nHandle, char *psBuffer, uint unLength );

/**
 *@brief 		向打开的nHandle文件写入unLength个字节.
 *@details
 *@param    nHandle 文件句柄
 *@param    psBuffer	需要写入文件内容的缓冲区
 *@param    unLength	需要写入的长度
 *@return
 *@li	返回实际写入数据长度
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 				参数错误(psBuffer为NULL)
 *@li	\ref NDK_ERR_WRITE "NDK_ERR_WRITE" 		写文件失败(所写的文件不是调用NDK_FsOpen打开的、调用write()失败返回)
*/
extern int (*NDK_FsWrite)(int nHandle, const char *psBuffer, uint unLength );

/**
 *@brief 		移动文件指针到从unPosition起距ulDistance的位置
 *@details
 *@param    nHandle 文件句柄
 *@param    ulDistance	根据参数unPosition来移动读写位置的位移数。
 *@param    unPosition	需要读取的字符的长度
 						SEEK_SET 参数offset即为新的读写位置。
						SEEK_CUR 以目前的读写位置往后增加offset个位移量。
						SEEK_END 将读写位置指向文件尾后再增加offset个位移量。
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 		操作失败(所移动的文件不是调用NDK_FsOpen打开的、调用lseek()失败返回)
*/
extern int (*NDK_FsSeek)(int nHandle, ulong ulDistance, uint unPosition );

/**
 *@brief 		删除指定文件
 *@details
 *@param    pszName 要删除的文件名
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误(pszName为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 				操作失败(调用remove()失败返回)
*/
extern int (*NDK_FsDel)(const char *pszName);

/**
 *@brief 		文件长度
 *@details
 *@param    pszName 文件名
 *@retval   punSize 文件大小返回值
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误(pszName、punSize为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 				操作失败(打开文件失败、调用fstat()失败返回)
*/
extern int (*NDK_FsFileSize)(const char *pszName,uint *punSize);

/**
 *@brief 		文件重命名
 *@details
 *@param    pszSrcName 原文件名
 *@param    pszDstName 目标文件名
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误(pszsSrcname、pszDstname为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败(调用rename()失败返回)
*/
extern int (*NDK_FsRename)(const char *pszSrcName, const char *pszDstName );

/**
 *@brief 		测试文件是否存在
 *@details
 *@param    pszName 文件名
 *@return
 *@li	NDK_OK				操作成功(文件存在)
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误(pszName为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 		操作失败(调用access()失败返回) 
*/

extern int (*NDK_FsExist)(const char *pszName);

/**
 *@brief 		文件截短
 *@details   NDK_FsTruncate()会将参数pszPath 指定的文件大小改为参数unLen 指定的大小。如果原来的文件大小比参数unLen大，则超过的部分会被删去。
 		   			如果原来文件的大小比unLen小的话，不足的部分将补上0xff
 *@param    pszPath 文件路径
 *@param    unLen 所要截短长度
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误(pszPath为NULL)
 *@li	\ref NDK_ERR_PATH "NDK_ERR_PATH" 		文件路径非法
 *@li	\ref NDK_ERR "NDK_ERR" 		操作失败(计算文件大小失败、调用lseek()失败返回、调用truncate()失败返回)
 *@li	\ref NDK_ERR_WRITE "NDK_ERR_WRITE" 		写文件失败(调用write()失败返回)
*/
extern int (*NDK_FsTruncate)(const char *pszPath ,uint unLen );

/**
 *@brief 	  	读取文件流位置
 *@details   用来取得文件流目前的读写位置
 *@param    nHandle 文件句柄
 *@retval    pulRet 文件流位置
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误(pulRet为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 				操作失败(操作的文件不是调用NDK_FsOpen打开的文件、调用lseek()失败返回)
*/
extern int (*NDK_FsTell)(int nHandle,ulong *pulRet);

/**
 *@brief 	  	取文件系统磁盘空间的使用情况
 *@details
 *@param    	unWhich :0--已经使用的磁盘空间1--剩余的磁盘空间
 *@retval     pulSpace 返回磁盘空间使用量或者剩余量
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数错误(pulSpace为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 				操作失败(调用statfs()失败返回)
*/
extern int (*NDK_FsGetDiskSpace)(uint unWhich,ulong *pulSpace);
/**
 *@brief 		创建目录.
 *@details
 *@param    pszName 目录名称
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pszName为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 		操作失败(调用mkdir命令失败返回)
*/

extern int (*NDK_FsCreateDirectory)(const char *pszName);


/**
 *@brief 		删除目录.
 *@details
 *@param    pszName 目录名称
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pszName为NULL)
 *@li	 \ref NDK_ERR "NDK_ERR" 		操作失败(调用remove()失败返回)
*/

extern int (*NDK_FsRemoveDirectory)(const char *pszName);
/**
 *@brief 	文件系统格式化(暂为支持)
 *@details	该功能仅限于传统pos上gp平台pos直接返回-1
 *@return
 *@li	 NDK_OK				操作成功返回
 *@li	\ref NDK_ERR_NOT_SUPPORT "NDK_ERR_NOT_SUPPORT" 		未支持该功能
*/

extern int (*NDK_FsFormat)(void);


/**
 *@brief 		列出制定目录下的所有文件
 *@details	传入的psBuf的的size一定要够大，否则会出现溢出情况psBuf 每20个字节存储一个文件名
 						前19 为文件名，超过自动截短。第20字节如果为1则表示该文件为文件夹，0为普通文件
 *@param       psPath 指定要读取的目录
 *@retval      psBuf 将文件名存储到psBuf总返回
 *@retval      punNum 返回该文件夹目录的文件总数
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pPath、psBuf、punNum为NULL)
 *@li	 \ref NDK_ERR_PATH "NDK_ERR_PATH" 		文件路径非法(调用opendir()失败返回)
*/
extern int (*NDK_FsDir)(const char *psPath,char *psBuf,uint *punNum);
extern int (*NDK_CopyFileToSecMod)(const unsigned char* sourcefile, const unsigned char* destfile);

/** @} */ // 文件系统模块结束


/** @addtogroup 应用管理
* @{
*/

#define NDK_APP_NAME_MAX_LEN		32	/**< 应用名称最大长度*/
#define NDK_APP_VER_MAX_LEN			16	/**< 版本最大长度*/
#define NDK_APP_BUILD_TIME_LEN		32	/**< 创建时间最大长度*/

/**
 *@brief 应用信息,该结构在NLD结构中使用，涉及补齐问题。结构定义不允许修改，否则会和下载工具打包的信息部一致
*/
typedef struct APPINFO{
    uchar 	szAppName[NDK_APP_NAME_MAX_LEN + 1];		/**<用户程序名称*/
    uchar	szVerBuf[NDK_APP_VER_MAX_LEN + 1];			/**<用户程序版本信息*/
    int		nSeriNo;									/**<编号,V2版保留,但意义不同	*/
    uchar	szBuildTime[NDK_APP_BUILD_TIME_LEN + 1];	/**<文件编译时间*/
    int		nIsMaster;									/**<主控标志 */
    uint	sunReverse[3];
}ST_APPINFO;

typedef int (*CallbackMock)(int, void *,int,void **,int *);

/**
 *@brief	设置回调函数指针
 *@param	NDK_EventMain	函数地址
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(NDK_EventMain为NULL)
*/
extern int NDK_AppSetEventCallBack(CallbackMock NDK_EventMain);

/**
 *@brief	运行应用程序
 *@param	pszAppName	应用名称。
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pszAppName为NULL)
 *@li	\ref NDK_ERR_APP_MAX_CHILD "NDK_ERR_APP_MAX_CHILD" 	子应用运行数超过最大运行数目
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败(读写应用配置文件失败)
 *@li	\ref NDK_ERR_APP_NOT_EXIST "NDK_ERR_APP_NOT_EXIST" 	应用项不存在
 *@li	\ref NDK_ERR_READ "NDK_ERR_READ" 	读文件失败
 *@li	\ref NDK_ERR_WRITE "NDK_ERR_WRITE" 	写文件失败
 *@li	\ref NDK_ERR_APP_CREAT_CHILD "NDK_ERR_APP_CREAT_CHILD" 	等待子进程结束错误
*/
extern int (*NDK_AppRun)(const char *pszAppName);
extern int NDK_LibLoad(const char * pszLibName, void ** apiStruct);
/**
 *@brief	执行事件操作
 *@param	pszFileName	应用名称
 *@param	nModuleId		执行事件ID
 *@param	pvInEventMsg	输入事件消息
 *@param	nInlen			输入事件长度
 *@param	nMaxOutLen		允许的输出事件的长度
 *@retval	pvOutEventMsg	获取到的应用信息
 *@retval	pnOutLen		实际输出的数据长度
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法 
 *@li	\ref NDK_ERR_APP_MAX_CHILD "NDK_ERR_APP_MAX_CHILD" 	子应用运行数超过最大运行数目
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败(读写应用配置文件失败)
 *@li	\ref NDK_ERR_APP_NOT_EXIST "NDK_ERR_APP_NOT_EXIST" 	应用项不存在
 *@li	\ref NDK_ERR_READ "NDK_ERR_READ" 	读文件失败
 *@li	\ref NDK_ERR_WRITE "NDK_ERR_WRITE" 	写文件失败
 *@li	\ref NDK_ERR_APP_CREAT_CHILD "NDK_ERR_APP_CREAT_CHILD" 	等待子进行结束错误
*/
extern int NDK_AppDoEvent(const uchar *pszFileName,int nModuleId,const void *pvInEventMsg, int nInlen, void *pvOutEventMsg, int nMaxOutLen, int *pnOutLen);

/**
 *@brief	获取安装完应用程序是否需要重启的标志位
 *@param	pnRebootFlag		输出参数1表示安装完应用程序需要重启才能生效，0不需要重启
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pnRebootFlag为NULL)
*/
extern int NDK_AppGetReboot(int *pnRebootFlag);
/**
 *@brief	装载应用
 *@param	pszFileName		应用名称
 *@param	nRebootFlag		安装应用程序成功后，后台返回是否需要重启。1-需要重启。0-不需要重启
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pszFilename为NULL)
 *@li	\ref NDK_ERR_APP_FILE_STAT "NDK_ERR_APP_FILE_STAT" 		获取文件信息错误
 *@li	\ref NDK_ERR_APP_FILE_OPEN "NDK_ERR_APP_FILE_OPEN" 		文件打开错误
 *@li	\ref NDK_ERR_APP_FILE_READ "NDK_ERR_APP_FILE_READ" 		读文件错误
 *@li	\ref NDK_ERR_APP_FILE_WRITE "NDK_ERR_APP_FILE_WRITE" 		写文件错误
 *@li	\ref NDK_ERR_APP_MALLOC "NDK_ERR_APP_MALLOC" 		动态内存分配错误
 *@li	\ref NDK_ERR_APP_NLD_HEAD_LEN "NDK_ERR_APP_NLD_HEAD_LEN" 	NLD文件获取头信息长度错误
 *@li	\ref NDK_ERR_APP_SIGN_CHECK "NDK_ERR_APP_SIGN_CHECK" 	签名数据校验错误
 *@li	\ref NDK_ERR_APP_SIGN_DECRYPT "NDK_ERR_APP_SIGN_DECRYPT" 	签名数据解签错误
*/
extern int (*NDK_AppLoad)(const char *pszFileName, int nRebootFlag);

/**
 *@brief	获取应用信息
 *@param	pszAppName	应用名称, 可以输入为NULL。
 *@param	nPos		偏移，该偏移位置为在系统中信息表偏移，和下载顺序相关。
 *@param	nSizeofInfo	输出应用信息的最大长度。
 *@retval	pstAppInfo	获取到的应用信息
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
 *@li	\ref NDK_ERR "NDK_ERR" 		操作失败(应用配置文件操作失败)
 *@li	\ref NDK_ERR_APP_NOT_EXIST "NDK_ERR_APP_NOT_EXIST" 		应用项不存在
*/
extern int NDK_AppGetInfo(const uchar *pszAppName, int nPos, ST_APPINFO *pstAppInfo, int nSizeofInfo);

/**
 *@brief	删除应用程序
 *@param	pszAppName	应用名称
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pszAppName为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败(读写应用配置文件失败)
 *@li	\ref NDK_ERR_APP_NOT_EXIST "NDK_ERR_APP_NOT_EXIST" 	应用项不存在
*/
extern int (*NDK_AppDel)(const char *pszAppName);

/**
 *@brief	运行后台服务程序
 *@param	pszAppName	应用名称。
 *@param	psArgv    应用运行参数
 *@param	cBlock   1 阻塞直到后台应用退出
                    0 并行运行

 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
 *@li	\ref NDK_ERR_APP_NOT_EXIST "NDK_ERR_APP_NOT_EXIST" 		应用项不存在
 *@li	\ref NDK_ERR_READ "NDK_ERR_READ" 		读文件失败
 *@li	\ref NDK_ERR_WRITE "NDK_ERR_WRITE" 		写文件失败
*/
extern int NDK_AppEXECV(const uchar *pszAppName, char * const psArgv[],char cBlock);
/** @} */ // 多应用模块结束

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
    ushort bits;       							/**< 模位数 */
    uchar modulus[MAX_RSA_MODULUS_LEN+1];      	/**< 模 */
	uchar publicExponent[MAX_RSA_MODULUS_LEN+1]; 	/**< 公钥指数 */
    uchar exponent[MAX_RSA_MODULUS_LEN+1];     	/**< 私钥指数 */
    uchar prime[2][MAX_RSA_PRIME_LEN+1];      	/**< pq素数 */
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
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
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
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
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
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
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
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
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
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
*/
extern int (*NDK_AlgRSARecover)(uchar *psModule, int nModuleLen, uchar *psExp, uchar *psDataIn, uchar *psDataOut);

/**
 *@brief	RSA密钥对校验
 *@param	pstPublicKey		公钥
 *@param	pstPrivateKey		私钥
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pstPublicKey\pstPrivateKey为NULL)	
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
*/
extern int (*NDK_AlgRSAKeyPairVerify)(ST_RSA_PUBLIC_KEY *pstPublicKey, ST_RSA_PRIVATE_KEY *pstPrivateKey);


/** @} */ // 算法模块结束


/** @addtogroup 安全
* @{
*/


typedef enum{
	SEC_KEY_DES = 0,	/**<DES/TDES 算法*/
	SEC_KEY_SM4 = (1<<6),		/**<SM4 算法*/
	SEC_KEY_AES = (1<<7),	
}EM_SEC_KEY_ALG;

/**
 *@brief 终端密钥类型
*/
typedef enum{
	SEC_KEY_TYPE_TLK=0,	/**<终端装载密钥*/
	SEC_KEY_TYPE_TMK,	/**<终端主密钥*/
	SEC_KEY_TYPE_TPK,	/**<终端PIN密钥*/
	SEC_KEY_TYPE_TAK,	/**<终端MAC密钥*/
	SEC_KEY_TYPE_TDK,	/**<终端数据加解密密钥*/
	SEC_KEY_TYPE_TDUKPT,	/**<终端dukpt数据加解密密钥*/
}EM_SEC_KEY_TYPE;

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
	SEC_MAC_AES,        /**< 数据分为16字节的block，不足补0，每个block进行SM4加密后与下一个block异或后按SM4加密*/
	SEC_MAC_SM4_UNIONPAY,/**银联卡sm4算mac模式**/
	SEC_ROOT_MAC_X919=0x10,	/**< 使用根MAC密钥计算*/
	SEC_MAC_ALG_MAX,
}EM_SEC_MAC;
/**
 * 用于实现PIN输入过程的超时控制的变量
 */
typedef enum {
	SEC_PIN_ISO9564_0=3,    /**<使用主账号加密，密码不足位数补'F'*/
	SEC_PIN_ISO9564_1=4,    /**<不使用主账号加密，密码不足位数补随机数*/
	SEC_PIN_ISO9564_2=5,    /**<不使用主账号加密，密码不足位数补'F'*/
	SEC_PIN_ISO9564_3=6,     /**<使用主账号加密，密码不足位数补随机数*/
	SEC_PIN_ISO9564_9=9,     /**<三维度PIN加密算法*/
	SEC_PIN_ISO9564_10=10     /**<上海拉卡拉PIN 加密算法*/
}EM_SEC_PIN;

typedef enum{
	SEC_PIN_SM4_1 = 7,		/**<不使用主账号，密码不足位数补'F'*/
	SEC_PIN_SM4_2,		/**<使用主账号填充方式1，密码不足位数补'F'*/
	SEC_PIN_SM4_3,		/**<使用主账号填充方式1，密码不足位数补随机数*/
	SEC_PIN_SM4_4,		/**<使用主账号填充方式2，密码不足位数补'F'*/
	SEC_PIN_SM4_5,		/**<使用主账号填充方式2，密码不足位数补随机数*/
	SEC_PIN_AES_FMT4,
}EM_SEC_PIN1;
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
	SEC_DES_MASK=0x07,			/**<des计算类型使用的映射值，超过该映射值位数无效*/
	SEC_SM4_ENCRYPT=(1<<4),                  /**<SM4加密*/
	SEC_SM4_DECRYPT=(1<<5),                  /**<SM4解密*/
	SEC_AES_ENCRYPT=(1<<6), 				 /**<AES加密*/
	SEC_AES_DECRYPT=(1<<7),					/**<AES解密*/
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
	SEC_TAMPER_STATUS_HW = (1<<0),					/**< 硬件安全攻击*/
	SEC_TAMPER_STATUS_SEC_CONFIG = (1<<1),			/**< 安全寄存器值错误*/
	SEC_TAMPER_STATUS_CHECKFILE = (1<<2),			/**< 文件校验错误*/
	SEC_TAMPER_DEVICE_DISABLED = (1<<8),		/**< 设备未使能*/
}EM_SEC_TAMPER_STATUS;

typedef struct {
	unsigned int ivlen;			/**< length of IV*/
	char *iv;					/**< IV value for CBC encryption*/
	unsigned int len;			/**< length of cipher text key data*/
	char *input;				/**< pointer to cipher text key data*/
}ST_KEYBLOCK_CBC;

#define SEC_KEYBLOCK_FMT_TR31		(0x54523331)	/**<扩展的TR-31 Key block密钥安装包格式,0x54523331即"TR31" */
#define SEC_KEYBLOCK_FMT_AES		(0x4145534B)	/**<AES密钥包格式,"AESK"*/
#define SEC_KEYBLOCK_FMT_CBC		(0x43424300)	/**<Extension of CBC mode cipher text key injection */

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
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
*/
extern int (*NDK_SecGetRandom)(int nRandLen , void *pvRandom);

/**
 *@brief	设置安全配置
 *@details	1、用户一旦通过此函数设置了安全配置信息，则后续操作根据此设置的配置信息进行控制。
 			如果没有调用此函数设置，则后续操作会按照默认的最低安全配置进行。
 			2、通常安全配置信息只允许升高，不允许降低（配置信息参数表中任意一位由1降至0都被认为安全性降低）。
 *@param	unCfgInfo		配置信息
 *@return
 *@li	NDK_OK				操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"		操作失败
*/
extern int (*NDK_SecSetCfg)(uint unCfgInfo);

/**
 *@brief	读取安全配置
 *@retval	punCfgInfo		配置信息
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(punCfgInfo为NULL)
*/
extern int (*NDK_SecGetCfg)(uint *punCfgInfo);

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
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
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
 *@brief	设置两次计算PINBlock或者计算MAC之间最小间隔时间
 *@details 	PINBLOCK间隔时间的计算方式：
 			默认为120秒那只能调用4次,即TPKIntervalTimeMs默认值为30秒,调用该函数重新设置后,限制为4*TPKIntervalTimeMs时间内只能调用4次。
 			比如传入的TPKIntervalTimeMs为20000(ms),则80秒内只能调用4次
 *@param	unTPKIntervalTimeMs	PIN密钥计算间隔时间，0-采用默认值，0xFFFFFFFF，不改变
 *@param	unTAKIntervalTimeMs	MAC密钥计算间隔时间，0-采用默认值，0xFFFFFFFF，不改变
 *@return
 *@li	NDK_OK				操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"		操作失败

*/
extern int (*NDK_SecSetIntervaltime)(uint unTPKIntervalTimeMs, uint unTAKIntervalTimeMs);

/**
 *@brief	设置功能键功能
 *@details 	对密码输入过程中，功能键用途进行定义
 *@param	ucType	功能用途类型定义
 *@return
 *@li	NDK_OK				操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"		操作失败

*/
extern int (*NDK_SecSetFunctionKey)(uchar ucType);

/**
 *@brief	计算MAC
 *@param	ucKeyIdx		密钥序号
 *@param	psDataIn		输入数据
 *@param	nDataInLen		输入数据长度
 *@param	ucMod			MAC计算模式 参考\ref EM_SEC_MAC "EM_SEC_MAC"
 *@retval	psMacOut		MAC值，长度8字节
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
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
 *@retval	psPinBlockOut	    PIN Block输出,该参数传入NULL时，PIN结果通过\ref NDK_SecGetPinResult "NDK_SecGetPinResult()"函数获取
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 		内存空间不足
 *@li	\ref NDK_ERR_SECP_PARAM "NDK_ERR_SECP_PARAM" 		参数非法(计算模式非法)
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(时间参数非法)
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
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
 *@li	\ref NDK_ERR "NDK_ERR"		操作失败
 *@li	\ref NDK_ERR_SECP_PARAM "NDK_ERR_SECP_PARAM" 	参数非法(数据长度不是8的整数倍)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 	内存空间不足
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
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(超时参数非法)
 *@li	\ref NDK_ERR_SECP_PARAM "NDK_ERR_SECP_PARAM" 		参数非法(ucMode非法等)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 	              内存空间不足
 *@li	\ref NDK_ERR "NDK_ERR" 	                              操作失败
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
 *@li	NDK_OK				    操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(超时参数非法)
 *@li	\ref NDK_ERR_SECP_PARAM "NDK_ERR_SECP_PARAM" 		参数非法(ucMode非法等)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 	        内存空间不足
 *@li	\ref NDK_ERR "NDK_ERR" 	                操作失败
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
 *@li	NDK_OK		                操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		  参数非法
 *@li	\ref NDK_ERR "NDK_ERR" 		                操作失败
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC"               内存空间不足
*/
extern int (*NDK_SecLoadTIK)(uchar ucGroupIdx, uchar ucSrcKeyIdx, uchar ucKeyLen, uchar * psKeyValueIn, uchar * psKsnIn, ST_SEC_KCV_INFO * pstKcvInfoIn);


/**
*@brief   安装DUKPT密钥  (新接口)
*@param ucGroupIdx          密钥组ID
*@param ucSrcKeyType        源密钥类型（用来加密初始密钥值的密钥类型）
*@param ucSrcKeyIdx         源密钥ID（用来加密初始密钥值的密钥ID）
*@param ucKeyLen        	密钥长度
*@param psKeyValueIn  		初始密钥值
*@param psKsnIn             KSN值
*@param pstKcvInfoIn   		Kcv信息
*@return
*@li   NDK_OK                     操作成功
*@li   其它EM_NDK_ERR           操作失败
*/
extern int (*NDK_SecLoadDukptKey)(uchar ucGroupIdx, uchar ucSrcKeyType, uchar ucSrcKeyIdx, uchar ucKeyLen, uchar * psKeyValueIn, uchar * psKsnIn, ST_SEC_KCV_INFO * pstKcvInfoIn);

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
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(超时参数非法)
 *@li	\ref NDK_ERR_SECP_PARAM "NDK_ERR_SECP_PARAM" 		参数非法(ucMode非法等)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 	        内存空间不足
 *@li	\ref NDK_ERR "NDK_ERR" 	                操作失败
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
 *@li	\ref NDK_ERR "NDK_ERR"  	操作失败
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
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
 *@li	\ref NDK_ERR_SECP_PARAM "NDK_ERR_SECP_PARAM" 		参数非法(数据长度不是8的整数倍)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 		内存空间不足
 *@li	\ref NDK_ERR "NDK_ERR" 		操作失败
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
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(psPinBlock、nStatus为NULL)
 *@li	\ref NDK_ERR_SECP_PARAM "NDK_ERR_SECP_PARAM" 	参数非法
 *@li	\ref NDK_ERR "NDK_ERR"          	操作失败
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
 *@li	NDK_OK				操作成功
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
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pszName为NULL或者应用名称长度大于等于256)
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
*/
extern int (*NDK_SecSetKeyOwner)(char *pszName);

/**
 *@brief	获取安全攻击状态
 *@retval	pnStatus			安全攻击状态参考\ref EM_SEC_TAMPER_STATUS "EM_SEC_TAMPER_STATUS"
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pnStatus为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
*/
extern int (*NDK_SecGetTamperStatus)(int *pnStatus);


/**(新增接口)
 *@brief 擦除指定类型密钥, 注：若指定密钥存在则会执行删除，若不存在则直接返回成功:NDK_OK
 *@param ucKeyIdx
密钥序号
 *@param ucKeyType
密钥类型,该值必须是EM_SEC_KEY_TYPE中定义的类型
 *@return
 *@li NDK_OK
操作成功
 *@li 其它EM_NDK_ERR
操作失败
*/
extern int (*NDK_SecKeyDelete)(uchar ucKeyIdx,uchar ucKeyType);

//用于判断安全是否触发
extern int (*NDK_GetTamperStatus)(void);

//用于获取安全寄存器值
extern int (*NDK_SecGetDrySR)(int *pnVal);

//用于清安全寄存器值
extern int (*NDK_SecClear)(void);

//用于删除1~200索引的密钥
extern int (*NDK_SecUserKeyDelete)(void);

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
/** @} */ // 安全模块结束



/** @addtogroup 串口通讯
* @{
*/


/**
 *@brief  串口选择
*/
typedef enum {
    PORT_NUM_COM1 = 0,	/**<串口1*/
    PORT_NUM_COM2 = 1,	/**<串口2*/
    PORT_NUM_WIRELESS = 2,	/**<无线模块*/
    PORT_NUM_MUX1 = 3,	/**<多路复用1*/
    PORT_NUM_MUX2 = 4,	/**<多路复用2*/
    PORT_NUM_MUX3 = 5,	/**<多路复用3*/
    PORT_NUM_MODEM = 6,	/**<有线模块*/
    PORT_NUM_WIFI = 7,	/**<Wifi模块*/
    PORT_NUM_USB = 8,	/**<USB模块*/
    PORT_NUM_SCAN = 9,   /**<扫描模块*/
    PORT_NUM_BT = 10,    /**<蓝牙模块*/
    PORT_NUM_AUDIO = 11,/**<音频模块*/
    PORT_NUM_CCID = 12, /**<CCID模块*/
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
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pszAttr为NULL、emPort串口类型非法)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败(获取与aux_fd相关的参数失败等)
*/
extern int (*NDK_PortOpen)(EM_PORT_NUM emPort, const char *pszAttr);

/**
 *@brief	关闭串口
 *@param	emPort	指定的串口
 *@return
 *@li	NDK_OK		操作成功
 *@li	\ref NDK_ERR "NDK_ERR"  	操作失败
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 	参数非法(emPort串口类型非法)
*/
extern int (*NDK_PortClose)(EM_PORT_NUM emPort);

/**
 *@brief	在设定超时时间里从指定的串口，读取指定长度的数据，存放于pszOutbuf
 *@param	emPort	指定的串口
 *@param	unLen	表示要读的数据长度,>0(小于4K)
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
 *@param	emPort	指定的串口
 *@param	unLen	表示要写的数据长度
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
 *@li	NDK_OK	缓冲区无数据
 *@li	\ref NDK_ERR "NDK_ERR" 	缓冲区有数据
*/
extern int (*NDK_PortTxSendOver)(EM_PORT_NUM emPort);

/**
 *@brief	清除指定串口的接收缓冲区
 *@param	emPort	指定的串口
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(emPort串口类型非法)
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
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pnReadlen为NULL、emPort串口类型非法)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	打开设备文件错误(设备未打开或打开失败) 
 *@li	\ref NDK_ERR "NDK_ERR" 				操作失败
*/
extern int (*NDK_PortReadLen)(EM_PORT_NUM emPort,int *pnReadLen);

/**
*@brief AudioTX电压定义
*/
typedef enum AudioTX_Vol
{
	AudioTX_Vol_200mV=1,
	AudioTX_Vol_100mV=2,
	AudioTX_Vol_50mV=3,
}EM_AudioTX_Vol;

/**
*@brief AudioTX发送频率定义
*/
typedef enum AudioTX_Freq
{
	AudioTX_Freq_LF,
	AudioTX_Freq_HF,
}EM_AudioTX_Freq;

extern int NDK_AudioTXSel(EM_AudioTX_Vol tx_Vol);
extern int NDK_AudioTX_FreqSel(EM_AudioTX_Freq tx_Freq);


/** @} */ // 串口通讯模块结束

/** @addtogroup modem模块
* @{
*/

/**
*@brief  modem连接状态定义
*/
typedef enum MDM_STATUS
{
	MDMSTATUS_NORETURN_AFTERPREDIAL=0,
	MDMSTATUS_OK_AFTERPREDIAL=1,
	MDMSTATUS_CONNECT_AFTERPREDIAL=2,
	MDMSTATUS_MS_NODIALTONE = -2,
	MDMSTATUS_MS_NOCARRIER =	-3,
	MDMSTATUS_MS_BUSY = -4,
	MDMSTATUS_MS_ERROR = -5,
	MDMSTATUS_NOPREDIAL = -11,
}EM_MDMSTATUS;
/**
*@brief  modem初始化函数的补丁参数类型定义
*/
typedef enum MDM_Patchtype
{
	MDM_PatchType5=5, 	/**<默认的补丁包方式*/
	MDM_PatchType4=4,	/**<特殊环境下使用的补丁方式*/
	MDM_PatchType3=3,	/**<快速连接补丁包*/
	MDM_PatchType2=2,	/**<预留*/
	MDM_PatchType1=1,	/**<预留*/
	MDM_PatchType0=0,	/**<预留*/
}EM_MDM_PatchType;

/**
 *@brief	同步拨号初始化函数。
 *@param	emType		补丁包参数，对应不同的线路环境而设置使用。
 *@return	无
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(emType补丁包参数非法)
 *@li	\ref NDK_ERR_MODEM_ATCOMNORESPONSE "NDK_ERR_MODEM_ATCOMNORESPONSE" 	MODEM AT命令无响应
 *@li	\ref NDK_ERR_MODEM_PORTWRITEFAIL "NDK_ERR_MODEM_PORTWRITEFAIL" 		MODEM 端口写数据失败
 *@li	\ref NDK_ERR_MODEM_SETCHIPFAIL "NDK_ERR_MODEM_SETCHIPFAIL" 	MODEM 模块寄存器设置失败
*/
extern int NDK_MdmSdlcInit(EM_MDM_PatchType emType);

/**
 *@brief	异步modem初始化(目前异步modem，emType为任意值都可以)。
 *@param	emType 	补丁包参数，对应不同的线路环境而设置使用
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_MODEM_PORTWRITEFAIL "NDK_ERR_MODEM_PORTWRITEFAIL" 		MODEM 端口写数据失败
 *@li	\ref NDK_ERR_MODEM_ATCOMMANDERR "NDK_ERR_MODEM_ATCOMMANDERR" 		MODEM AT命令错误
*/
extern int NDK_MdmAsynInit(EM_MDM_PatchType emType);

/**
 *@brief	modem拨号函数。
 *@param	pszDailNum 	拨号号码
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pszDailNum为NULL、pszDailNum长度大于25)
 *@li	\ref NDK_ERR_MODEM_NOLINE "NDK_ERR_MODEM_NOLINE" 		未插线
 *@li	\ref NDK_ERR_MODEM_OTHERMACHINE "NDK_ERR_MODEM_OTHERMACHINE" 		存在并机
 *@li	\ref NDK_ERR_MODEM_STARTSDLCTASK "NDK_ERR_MODEM_STARTSDLCTASK" 		MODEM 拨号时开启SDLC 任务失败
 *@li	\ref NDK_ERR_MODEM_PORTWRITEFAIL "NDK_ERR_MODEM_PORTWRITEFAIL" 		MODEM 端口写数据失败
 *@li	\ref NDK_ERR_MODEM_INIT_NOT "NDK_ERR_MODEM_INIT_NOT" 		MODEM未进行初始化
*/
extern int NDK_MdmDial(const char * pszDailNum);

/**
 *@brief	检测modem状态。
 *@param	pemStatus 	modem状态的实际返回值
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pemStatus为NULL)
 *@li	\ref NDK_ERR_MODEM_INIT_NOT "NDK_ERR_MODEM_INIT_NOT" 		MODEM未进行初始化
 *@li	\ref NDK_ERR_MODEM_STATUSUNDEFINE "NDK_ERR_MODEM_STATUSUNDEFINE" 		MODEM状态未确认状态
 *@li	\ref NDK_ERR_MODEM_QUIT "NDK_ERR_MODEM_QUIT" 				MODEM 手动退出
*/
extern int NDK_MdmCheck(EM_MDMSTATUS  *pemStatus);

/**
 *@brief	modem数据发送。
 *@param	pszData 	发送的数据
 *@param	unDataLen 	发送的数据长度
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pszData为NULL、unDatalen非法)
 *@li	\ref NDK_ERR_MODEM_INIT_NOT "NDK_ERR_MODEM_INIT_NOT" 		MODEM未进行初始化
 *@li	\ref NDK_ERR_MODEM_STATUSUNDEFINE "NDK_ERR_MODEM_STATUSUNDEFINE" 		MODEM状态未确认状态
 *@li	\ref NDK_ERR_MODEM_QUIT "NDK_ERR_MODEM_QUIT" 			MODEM 手动退出
 *@li	\ref NDK_ERR_MODEM_NOPREDIAL "NDK_ERR_MODEM_NOPREDIAL" 		MODEM 未拨号
 *@li	\ref NDK_ERR_MODEM_SDLCWRITEFAIL "NDK_ERR_MODEM_SDLCWRITEFAIL"      MODEM同步写失败
 *@li	\ref NDK_ERR_MODEM_NOCARRIER "NDK_ERR_MODEM_NOCARRIER"          MODEM 没载波
 *@li	\ref NDK_ERR_TIMEOUT "NDK_ERR_TIMEOUT" 		超时错误
*/
extern int NDK_MdmWrite(const char *pszData,uint unDataLen);

/**
 *@brief	modem数据接收。
 *@param	pszData 	接收的数据
 *@param	punDataLen 	接收的数据长度(异步时，应该需输入希望读取的数据长度)
 *@param	unSenconds	超时时间，以s为单位
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pszData、punDataLen为NULL、unSenconds非法)
 *@li	\ref NDK_ERR_MODEM_INIT_NOT "NDK_ERR_MODEM_INIT_NOT" 		MODEM未进行初始化
 *@li	\ref NDK_ERR_MODEM_STATUSUNDEFINE "NDK_ERR_MODEM_STATUSUNDEFINE" 		MODEM状态未确认状态
 *@li	\ref NDK_ERR_MODEM_QUIT "NDK_ERR_MODEM_QUIT" 			MODEM 手动退出
 *@li	\ref NDK_ERR_MODEM_NOPREDIAL "NDK_ERR_MODEM_NOPREDIAL" 		MODEM 未拨号
 *@li	\ref NDK_ERR_MODEM_SDLCWRITEFAIL "NDK_ERR_MODEM_SDLCWRITEFAIL"      MODEM同步写失败
 *@li	\ref NDK_ERR_MODEM_NOCARRIER "NDK_ERR_MODEM_NOCARRIER"          MODEM 没载波
 *@li	\ref NDK_ERR_TIMEOUT "NDK_ERR_TIMEOUT" 		超时错误
*/
extern int NDK_MdmRead(char  *pszData,uint *punDataLen,uint unSenconds);

/**
 *@brief	modem挂断函数。
 *@param	无
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_MODEM_SDLCHANGUPFAIL "NDK_ERR_MODEM_SDLCHANGUPFAIL" 		MODEM同步挂断失败
 *@li	\ref NDK_ERR_MODEM_ASYNHANGUPFAIL "NDK_ERR_MODEM_ASYNHANGUPFAIL" 		MODEM异步挂断失败
 *@li	\ref NDK_ERR_MODEM_INIT_NOT "NDK_ERR_MODEM_INIT_NOT" 		MODEM未进行初始化
*/
extern int NDK_MdmHangup(void);

/**
 *@brief	清除modem缓冲区。
 		1.同步拨号时，在同步初始化后并且在同步拨号前可以使用，如果同步拨号后则返回失败；
 		2.异步拨号时，在只要异步初始化后就可以使用这个函数。
 *@param
 *@param	无
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_MODEM_SDLCCLRBUFFAIL "NDK_ERR_MODEM_SDLCCLRBUFFAIL" 		MODEM同步清缓冲失败
 *@li	\ref NDK_ERR_MODEM_ASYNCLRBUFFAIL "NDK_ERR_MODEM_ASYNCLRBUFFAIL" 		MODEM异步清缓冲失败
 *@li	\ref NDK_ERR_MODEM_INIT_NOT "NDK_ERR_MODEM_INIT_NOT" 		MODEM未进行初始化
*/
extern int NDK_MdmClrbuf(void);

/**
 *@brief	读取modem长度。
 *@param	punReadLen 	返回的长度值
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(punReadLen为NULL)
 *@li	\ref NDK_ERR_MODEM_INIT_NOT "NDK_ERR_MODEM_INIT_NOT" 		MODEM未进行初始化
*/
extern int NDK_MdmGetreadlen(uint *punReadLen);

/**
 *@brief	modem复位函数。
 *@return
 *@li	NDK_OK				操作成功
*/
extern int NDK_MdmReset(void);

/**
 *@brief	AT命令交互函数。
 *@param	psCmdStr 	输入的命令串
 *@param	pszRespData 	返回的响应数据
 *@param	punLen 	返回的数据长度
 *@param	unTimeout 	超时时间
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(psCmdStr\pszRespData\punLen为NULL、unTimeout小于0、命令串长度大于52)
 *@li	\ref NDK_ERR_MODEM_PORTWRITEFAIL "NDK_ERR_MODEM_PORTWRITEFAIL" 		MODEM 端口写数据失败
 *@li	\ref NDK_ERR_MODEM_ATCOMMANDERR "NDK_ERR_MODEM_ATCOMMANDERR" 		MODEM AT命令错误
 *@li	\ref NDK_ERR_TIMEOUT "NDK_ERR_TIMEOUT" 			超时错误
*/
extern int NDK_MdmExCommand(uchar *psCmdStr,uchar *pszRespData,uint *punLen,uint unTimeout);

/** @} */ // modem模块结束

/** @addtogroup 无线模块
* @{
*/

/**
 *@brief  无线模块常用命令定义
*/
typedef enum {
    WLM_CMD_E0=0,				/**<关闭回显*/
    WLM_CMD_CSQ=1,				/**<取信号值*/
    WLM_CMD_CREG=2,				/**<网络注册状态*/
    WLM_CMD_CPIN=3,				/**<查询卡PIN*/
    WLM_CMD_CPIN0=4,			/**<设置PIN码*/
    WLM_CMD_CGATT0=5,			/**<注销GPRS网络*/
    WLM_CMD_CGATT1=6,			/**<注册GPRS网络*/
    WLM_CMD_DIAL=7,				/**<拨号*/
    WLM_CMD_D2=8,				/**<启用硬件挂断功能*/
    WLM_CMD_COPS=9,				/**<注册网络*/
    WLM_CMD_CGMR=10,			/**<取模块版本*/
    WLM_CMD_CGSN,         		/**<获取GSN号(imei或meid)*/
    WLM_CMD_CCID,				/**<获取无线SIM卡序列号*/
    WLM_CMD_END,          		/**<命令结束，如果新增命令都放这之前位置*/
    WLM_CMD_UNDEFINE=1024,		/**<列表中未定义*/
    WLM_CMD_PUBLIC=255
} EM_WLM_CMD;

/**
 *@brief  AT指令数据包
*/
typedef struct NDK_WL_ATCMD{
		EM_WLM_CMD AtCmdNo;		/**<命令号*/
		char *pcAtCmd;			/**<命令字串*/
		char *pcAddParam;		/**<附加参数*/
}ST_ATCMD_PACK;

/**
 *@brief  无线模块返回状态定义
*/
typedef enum{
    WLM_STATUS_UNTYPED=4,	/**<未定义返回*/
    WLM_STATUS_NO_CARRIER=3,/**<无载波*/
    WLM_STATUS_RING=2,		/**<RING串*/
    WLM_STATUS_CONNECT=1,	/**<连接串*/
    WLM_STATUS_OK=0,				/**<成功*/
    WLM_STATUS_ERROR=-1,			/**<错误*/
    WLM_STATUS_RET_ERROR=-114,		/**<返回出错*/
}EM_WLM_STATUS;

/**
 *@brief  无线CCID,IMSI,IMEI
*/
typedef enum{
    WLM_INFO_CCID=0,					/**<无线CCID枚举*/
    WLM_INFO_IMSI=1,		/**<无线IMSI枚举*/
    WLM_INFO_IMEI=2,		/**<无线IMEI枚举*/
    WLM_INFO_UNDEFINE=1024,	/**<未定义返回*/
}EM_WLM_TYPE_INFO;

/**
 *@brief  基站信息
*/
typedef struct station_info
{
	uint unMCC;  /**<移动国家号*/
	uint unMNC;	/**<移动网络号*/
	//float longitude;
	//float latitude;
	uint unLac;/**<位置区号码*/
	uint unCi;/**<小区号码*/
	int ndbm;/**<信号强度*/
}ST_STATION_INFO;

typedef struct
{
	ST_STATION_INFO stMainStation;/**<GPRS模块附着主基站*/
	ST_STATION_INFO stNeighborStation[6];/**<GPRS无线模块的临近基站*/
}ST_GPRS_STATION_INFO;

typedef struct
{
	uint unMCC;		/**<移动国家号*/
	uint unMNC;		/**<移动网络号*/
	uint unBandClass;/**<带宽类型*/
	uint unChannel;		/**<频道*/
	uint unBid;		/**<系统识别码*/
	uint unSid;		/**<网络识别码*/
	uint unNid;		/**<网络中的某一个小区*/
}ST_CDMA_STATION_INFO;

typedef union
{
	ST_CDMA_STATION_INFO CDMA_STATION;	/**<CDMA基站*/
	ST_GPRS_STATION_INFO GPRS_STATION;	/**<GPRS基站*/
}MODULE_UNION_INFO;

typedef enum 
{
	MODULE_TYPE_GPRS,		/**<GPRS模块*/
	MODULE_TYPE_CDMA,		/**<CDMA模块*/
	MODULE_TYPE_WCDMA,		/**<WCDMA模块*/
	MODULE_TYPE_TDSCDMA,	/**<TDSCDMA模块*/
}EM_MODULE_TYPE;

typedef struct
{
	EM_MODULE_TYPE emModuleType;		/**<模块类型*/
	MODULE_UNION_INFO ModuleStationInfo;/**<模块基站信息*/
}ST_MOBILE_STATION_INFO;

/**
 *@brief	无线MODEM的硬件复位
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		打开设备文件失败
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用失败
*/
extern int NDK_WlModemReset(void);

/**
 *@brief	关闭无线MODEM模块
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		打开设备文件失败
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用失败
*/
extern int NDK_WlModemClose(void);

/**
 *@brief	无线MODEM初始化，切换串口到无线并判断模块AT指令能否正常响应，检测SIM卡
 *@param	nTimeout	超时时间，单位MS
 *@param	pszPinPassWord	PIN码
 *@retval	pemStatus	执行成功返回无线状态，失败返回 NDK_FAIL	失败
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pemStatus为NULL,nTimeout超时参数非法)
 *@li	\ref NDK_ERR_OVERFLOW "NDK_ERR_OVERFLOW" 	缓冲溢出
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
 *@li	\ref NDK_ERR_TIMEOUT "NDK_ERR_TIMEOUT" 	超时错误
 *@li	\ref NDK_ERR_PIN_LOCKED "NDK_ERR_PIN_LOCKED" 	SIM卡被锁定 
 *@li	\ref NDK_ERR_PIN "NDK_ERR_PIN" 	SIM卡密码错误  
 *@li	\ref NDK_ERR_PIN_UNDEFINE "NDK_ERR_PIN_UNDEFINE" 	SIM卡未定义错误  
 *@li	\ref NDK_ERR_NO_SIMCARD "NDK_ERR_NO_SIMCARD" 	无SIM卡  
*/
extern int NDK_WlInit(int nTimeout,const char *pszPinPassWord,EM_WLM_STATUS *pemStatus);


/**
 *@brief	获取无线MODEM信号强度
 *@retval	pnSq	取到的信号强度，取到的值	0-31 为成功，99	为未知,-1 为失败
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pnSq为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
*/
extern int NDK_WlModemGetSQ(int *pnSq);


/**
 *@brief	向无线模块发送AT指令和接收返回响应
 *@param	pstATCmdPack	AT指令数据包
 *@param	unMaxLen	缓冲最大长度（pszOutput缓冲区长度）（=0时使用缺省长度1024）
 *@param	unTimeout	命令超时时间，单位：MS
 *@retval	pszOutput	输出串
 *@retval	pemStatus	执行成功返回无线状态，失败返回 WLM_STATUS_ERROR	失败
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pemStatus、pstATCmdPack、pszOutput为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	打开设备文件错误(设备未打开或打开失败) 
 *@li	\ref NDK_ERR_USB_LINE_UNCONNECT "NDK_ERR_USB_LINE_UNCONNECT" 		USB线未插
 *@li	\ref NDK_ERR_WRITE "NDK_ERR_WRITE" 				写文件失败
 *@li	\ref NDK_ERR "NDK_ERR" 				操作失败
 *@li	\ref NDK_ERR_EMPTY "NDK_ERR_EMPTY" 			返回空串
 *@li	\ref NDK_ERR_OVERFLOW "NDK_ERR_OVERFLOW" 	缓冲溢出
*/
extern int NDK_WlSendATCmd(const ST_ATCMD_PACK *pstATCmdPack,char *pszOutput,uint unMaxLen,uint unTimeout,EM_WLM_STATUS *pemStatus);

/**
 *@fn 		NDK_WlGetInfo(EM_WLM_TYPE_INFO emType,char *pszValue,uint unBufLen);
 *@brief 		获取无线CCID,IMSI,IMEI
 *@param		emtype		无线CCID,IMSI,IMEI的等枚举
 *@param		pszValue	无线CCID,IMSI,IMEI的信息	
 *@param		nBufLen		缓冲区长度需>=21,CCID总长20位，由0-F组成,IMSI总长不超过15，由0-9组成，IMEI总长不超过20，由0-F组成.
 *@return	
 *@li   NDK_OK			   操作成功
 *@li   \ref NDK_ERR_PARA "NDK_ERR_PARA" 	   参数非法(emType非法、pszValue为NULL)
 *@li   \ref NDK_ERR "NDK_ERR" 			   操作失败
*/

extern int NDK_WlGetInfo(EM_WLM_TYPE_INFO emType,char *pszValue,uint unBufLen);

/**
 *@brief	获取本基站和相邻基站的信息，包括运营商，位置区号码，小区号

 *@retval	pstStationInfo	基站信息
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 	参数非法(pstStationInfo为NULL)
*/
extern int NDK_WlGetStationInfo(ST_MOBILE_STATION_INFO * pstStationInfo);

/**
 *@brief	关闭射频（暂未支持）
 *@return
 *@li	NDK_OK				操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"		操作失败
*/
extern int NDK_WlCloseRF(void);

/**
 *@brief	选择SIM卡（暂未支持）
 *@param	ucSimNo	卡号
 *@return
 *@li	NDK_OK				操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"		操作失败
*/
extern int NDK_WlSelSIM(uchar ucSimNo);


/** @} */ // 无线模块结束

/** @addtogroup Socket通讯
* @{
*/


/**
 *@brief	打开TCP通讯通道
 *@retval	punFd	返回TCP通道句柄
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(punFd为NULL)
 *@li	\ref NDK_ERR_TCP_ALLOC "NDK_ERR_TCP_ALLOC" 		无法非配
 *@li	\ref NDK_ERR_TCP_PARAM "NDK_ERR_TCP_PARAM" 		无效参数
 *@li	\ref NDK_ERR_TCP_TIMEOUT "NDK_ERR_TCP_TIMEOUT" 		传输超时
 *@li	\ref NDK_ERR_TCP_INVADDR "NDK_ERR_TCP_INVADDR" 		无效地址
 *@li	\ref NDK_ERR_TCP_CONNECT "NDK_ERR_TCP_CONNECT" 		没有连接
 *@li	\ref NDK_ERR_TCP_PROTOCOL "NDK_ERR_TCP_PROTOCOL" 		协议错误
 *@li	\ref NDK_ERR_TCP_NETWORK "NDK_ERR_TCP_NETWORK" 		网络错误
*/
extern int NDK_TcpOpen(uint *punFd);

/**
 *@brief	关闭TCP通讯通道
 *@param	unFd	要关闭的TCP通道句柄
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 		操作失败
*/
extern int NDK_TcpClose(uint unFd);

/**
 *@brief	等待TCP关闭成功，一旦关闭及时退出，调用NDK_TcpClose()后，必须继续调用该函数确保TCP链路完全关闭
 *@param	unFd	TCP通道句柄
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 		操作失败(unFd非法等)
*/
extern int NDK_TcpWait(uint unFd);

/**
 *@brief	绑定本端的IP地址和端口号
 *@param	unFd	TCP通道句柄
 *@param	pszMyIp	源地址
 *@param	usMyPort	源端口
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 	参数非法(pszMyIp为NULL)
 *@li	\ref NDK_ERR_TCP_PARAM "NDK_ERR_TCP_PARAM" 	无效参数(unFd非法、源地址不合法)
 *@li	\ref NDK_ERR_TCP_ALLOC "NDK_ERR_TCP_ALLOC" 		无法非配
 *@li	\ref NDK_ERR_TCP_TIMEOUT "NDK_ERR_TCP_TIMEOUT" 		传输超时
 *@li	\ref NDK_ERR_TCP_INVADDR "NDK_ERR_TCP_INVADDR" 		无效地址
 *@li	\ref NDK_ERR_TCP_CONNECT "NDK_ERR_TCP_CONNECT" 		没有连接
 *@li	\ref NDK_ERR_TCP_PROTOCOL "NDK_ERR_TCP_PROTOCOL" 		协议错误
 *@li	\ref NDK_ERR_TCP_NETWORK "NDK_ERR_TCP_NETWORK" 		网络错误
*/
extern int NDK_TcpBind(uint unFd, const char *pszMyIp, ushort usMyPort);

/**
 *@brief	连接服务器
 *@param	unFd	TCP通道句柄
 *@param	pszRemoteIp	远程地址
 *@param	usRemotePort	远程端口
 *@param	unTimeout	远程连接超时时间，单位为秒
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 	参数非法(pszRemoteIp为NULL) 
 *@li	\ref NDK_ERR "NDK_ERR"          	操作失败
 *@li	\ref NDK_ERR_TCP_TIMEOUT "NDK_ERR_TCP_TIMEOUT"          	超时错误
 *@li	\ref NDK_ERR_LINUX_TCP_TIMEOUT "NDK_ERR_LINUX_TCP_TIMEOUT"          TCP远程端口错误
 *@li	\ref NDK_ERR_LINUX_TCP_REFUSE "NDK_ERR_LINUX_TCP_REFUSE"          TCP远程端口被拒绝
 *@li	\ref NDK_ERR_LINUX_TCP_NOT_OPEN "NDK_ERR_LINUX_TCP_NOT_OPEN"          TCP句柄未打开错误
*/
extern int NDK_TcpConnect(uint unFd, const char *pszRemoteIp, ushort usRemotePort, uint unTimeout);

/**
 *@brief	监听申请的连接
 *@param	unFd	TCP通道句柄
 *@param	nBacklog	等待连接队列的最大长度
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_TCP_ALLOC "NDK_ERR_TCP_ALLOC" 		无法非配
 *@li	\ref NDK_ERR_TCP_PARAM "NDK_ERR_TCP_PARAM" 		无效参数
 *@li	\ref NDK_ERR_TCP_TIMEOUT "NDK_ERR_TCP_TIMEOUT" 		传输超时
 *@li	\ref NDK_ERR_TCP_INVADDR "NDK_ERR_TCP_INVADDR" 		无效地址
 *@li	\ref NDK_ERR_TCP_CONNECT "NDK_ERR_TCP_CONNECT" 		没有连接
 *@li	\ref NDK_ERR_TCP_PROTOCOL "NDK_ERR_TCP_PROTOCOL" 		协议错误
 *@li	\ref NDK_ERR_TCP_NETWORK "NDK_ERR_TCP_NETWORK" 		网络错误
*/
extern int NDK_TcpListen(uint unFd, int nBacklog);

/**
 *@brief	接收连接请求
 *@param	unFd	TCP通道句柄
 *@param	pszPeerIp	接收连接实体的地址
 *@param	usPeerPort	接收连接实体的端口
 *@retval	punNewFd	返回TCP通道句柄
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pszPeerIp/punNewFd为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 		操作失败(调用accept()失败返回)
*/
extern int NDK_TcpAccept(uint unFd, const char *pszPeerIp, ushort usPeerPort, uint *punNewFd);

/**
 *@brief	发送数据
 *@param	unFd	TCP通道句柄
 *@param	pvInbuf	发送缓冲区的地址
 *@param	unLen	发送数据的长度
 *@param	unTimeout	超时时间，单位为秒
 *@retval	punWriteLen	接收实际发送长度,如果为NULL则不接收
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pInbuf为NULL)
 *@li	\ref NDK_ERR_TCP_SEND "NDK_ERR_TCP_SEND" 		发送错误(调用send()失败返回)
*/
extern int NDK_TcpWrite(uint unFd, const void *pvInbuf, uint unLen, uint unTimeout, uint *punWriteLen);
/**
 *@brief	接收数据
 *@param	unFd	TCP通道句柄
 *@param	unLen	接收数据的长度
 *@param	unTimeout	超时时间，单位为秒
 *@retval	pvOutBuf	接收缓冲区的地址
 *@retval	punReadLen	返回实际发送长度
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pvOutBuf/punReadLen为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
 *@li	\ref NDK_ERR_TCP_TIMEOUT "NDK_ERR_TCP_TIMEOUT" 			超时错误
 *@li	\ref NDK_ERR_TCP_RECV "NDK_ERR_TCP_RECV" 			接收错误
*/
extern int NDK_TcpRead(uint unFd, void *pvOutBuf, uint unLen, uint unTimeout, uint *punReadLen);


/** @} */ // Socket通讯模块结束


/** @addtogroup PPP通讯
* @{
*/

/**
 *@brief  长连接标识的宏定义
*/
#define STATUS_OPEN			0x03
#define STATUS_CLOSE		0x02
#define LCP_ECHO_OPEN  STATUS_OPEN  /**<表示ECHO回拨检测开关状态*/
#define LCP_ECHO_CLOSE  STATUS_CLOSE
#define LCP_DCD_OPEN  (STATUS_OPEN<<2)/**<表示DCD开关状态*/
#define LCP_DCD_CLOSE  (STATUS_CLOSE<<2)
#define LCP_PPP_KEEP  (STATUS_OPEN<<4)/**<表示PPP长链接维持开关状态*/
#define LCP_PPP_UNKEEPED  (STATUS_CLOSE<<4)

/**
 *@brief  PPP连接状态定义
*/
typedef enum {
    PPP_STATUS_DISCONNECT=0,		/**<PPP未连接*/
    PPP_STATUS_CONNECTING=2,		/**<PPP正在连接*/
    PPP_STATUS_CONNECTED=5,			/**<PPP已连接*/
    PPP_STATUS_DISCONNECTING=6		/**<正在挂断*/
} EM_PPP_STATUS;

/**
 *@brief  PPP配置参数
*/
typedef struct {
    int nDevType;			/**<类型，区别是无线MODEM还是有线MODEM，0表示无线，1表示有线*/
    unsigned int nPPPFlag;	/**<是否支持维持长连接标识，根据需要传入相对应的宏定义*/
    char szApn[64];			/**<APN设置*/
    char szDailNum[32];		/**<拨号号码,前面不带D*/
    int (*ModemDial)(void);/**<拨号函数*/
    unsigned int PPPIntervalTimeOut; /**<维持长链接的数据包发送的时间间隔,<30S将不会进行长链接的维持*/
    unsigned char nMinSQVal;	/**<初始化时，允许的最小的信号值*/
    char szPin[31];				/**<SIM卡PIN码*/
    char nPPPHostIP[20];		/**<维持长链接需要PIN的主机IP*/
} ST_PPP_CFG;

/**
 *@brief	配置PPP参数
 *@param	pstPPPCfg	ppp参数结构(设备类型设置缺省时为无线)
 *@param	unValidLen	参数pstPPPCfg的有效长度
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pstPPPCfg为NULL)
 *@li	\ref NDK_ERR_PPP_DEVICE "NDK_ERR_PPP_DEVICE" 		PPP无效设备
 *@li	\ref NDK_ERR_SHM "NDK_ERR_SHM" 		共享内存出错
 *@li	\ref NDK_ERR_PPP_PARAM "NDK_ERR_PPP_PARAM" 		PPP参数出错
*/
extern int NDK_PppSetCfg(ST_PPP_CFG *pstPPPCfg, uint unValidLen);

/**
 *@brief	PPP拨号
 *@param	pszUserName	用户名
 *@param	pszPassword	密码
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pszUserName/pszPassword为NULL)
 *@li	\ref NDK_ERR_PPP_OPEN "NDK_ERR_PPP_OPEN" 	PPP已打开
 *@li	\ref NDK_ERR_SHM "NDK_ERR_SHM" 		共享内存出错
 *@li	\ref NDK_ERR_PPP_DEVICE "NDK_ERR_PPP_DEVICE" 		PPP无效设备
*/
extern int NDK_PppDial(const char *pszUserName,const char *pszPassword);

/**
 *@brief	PPP挂断
 *@param	nHangupType	挂断类型 0 非阻塞挂断 1 阻塞挂断
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(nHangupType非法)
 *@li	\ref NDK_ERR_TIMEOUT "NDK_ERR_TIMEOUT" 		超时错误
 *@li	\ref NDK_ERR_SHM "NDK_ERR_SHM" 		共享内存出错
*/
extern int NDK_PppHangup(int nHangupType);

/**
*@brief	获取PPP状态
 *@retval	pemStatus	返回PPP状态,为NULL则不执行该操作
 *@retval	pnErrCode	返回PPP连接错误,为NULL则不执行该操作
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 	        参数非法(pemStatus/pnErrCode为NULL)
 *@li	\ref NDK_ERR_SHM "NDK_ERR_SHM" 				共享内存出错
*/
extern int NDK_PppCheck(EM_PPP_STATUS *pemStatus, int *pnErrCode);

/**
 *@brief	获取本地地址和主机地址
 *@retval	pulLocalAddr	返回本地地址,为NULL则不接收
 *@retval	pulHostAddr	返回主机地址,为NULL则不接收
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pulLocalAddr/pulHostAddr为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败(获取PPP地址信息出错)
*/
extern int NDK_PppGetAddr(ulong *pulLocalAddr, ulong *pulHostAddr);

/**
 *@brief	将一个点分十进制的IP转换成一个长整数型数
 *@param	pszIp	IP地址字符串
 *@retval	pulIpAddr	返回转换后的长整形数
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pszIp/pulIpAddr为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败(地址非法)
*/
extern int NDK_PppAddrChange(register const char *pszIp, ulong *pulIpAddr);

/** @} */ // PPP通讯模块结束

/** @addtogroup 以太网通讯
* @{
*/

typedef enum{
                COMM_TYPE_ETH,                  /**<以太网*/
                COMM_TYPE_WIFI,                 /**<WIFI*/
                COMM_TYPE_PPP,                  /**<PPP*/
                COMM_TYPE_BTH,                  /**<蓝牙*/
                COMM_TYPE_UNKNOW,          /**<未知*/
}EM_COMM_TYPE;


typedef enum {
    BIT_DATA = 1,
    WIDE_DATA = 2,
} ENUM_MAG_DATA_TYPE;



/**
 *@brief	根据提供的网络类型获取网络地址,网络地址参数都为NULL返回参数错误
 *@param emComtype	要获取网络地址的网络类型
 *@retval	pszIp	返回IP地址,为NULL则不取IP地址
 *@retval	pszGateway	返回网关地址,为NULL则不取Gateway地址
 *@retval	pszMask	返回子网掩码,为NULL则不取Mask地址
 *@retval	pszDns	DNS服务器IP地址,为NULL则不取DNS地址,一次性取出所以DNS,之间以分号符';'隔开
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(地址全为NULL或者emComtype错误)
 *@li	\ref NDK_ERR_NET_GETADDR "NDK_ERR_NET_GETADDR" 		获取本地地址或子网掩码或网关失败
 *@li	\ref NDK_ERR "NDK_ERR" 		操作失败(获取DNS失败)
*/
extern int NDK_NetGetAddr(EM_COMM_TYPE emComtype,char *pszIp, char *pszMask, char *pszGateway, char *pszDns);
/**
 *@brief	设置网络地址,参数都为NULL返回参数错误(设置后如果原已动态分配的地址,也会被修改)
 *@param	pszIp	本地IP 地址字符串的首指针,为NULL则不设IP地址.仅支持IPV4协议
 *@param	pszMask	本地子网掩码字符串的首指针,为NULL则不设Mask地址.仅支持IPV4协议
 *@param	pszGateway	本地网关地址字符串的首指针,为NULL则不设Gateway地址.仅支持IPV4协议
 *@param 	pszDns	本地DNS服务器IP地址,为NULL则不设DNS地址,最多可设置3个DNS,之间以分号符';'隔开
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(地址为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	打开设备文件错误
 *@li	\ref NDK_ERR "NDK_ERR" 	操作失败
 *@li	\ref NDK_ERR_NET_ADDRILLEGAL "NDK_ERR_NET_ADDRILLEGAL" 	获取地址格式错误
*/
extern int NDK_EthSetAddress(const char *pszIp, const char *pszMask, const char *pszGateway, const char *pszDns);

/**
 *@brief	获取网络MAC地址
 *@retval	pszMacAddr	返回MAC地址
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pszMacAddr为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 				操作失败(获取mac地址失败)
*/
extern int NDK_EthGetMacAddr(char *pszMacAddr);

/**
 *@brief	获取网络地址,参数都为NULL返回参数错误
 *@retval	pszIp	返回IP地址,为NULL则不取IP地址
 *@retval	pszGateway	返回网关地址,为NULL则不取Gateway地址
 *@retval	pszMask	返回子网掩码,为NULL则不取Mask地址
 *@retval	pszDns	DNS服务器IP地址,为NULL则不取DNS地址,一次性取出所以DNS,之间以分号符';'隔开
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法
 *@li	\ref NDK_ERR_NET_GETADDR "NDK_ERR_NET_GETADDR" 		获取本地地址或子网掩码失败
 *@li	\ref NDK_ERR "NDK_ERR" 		操作失败(获取DNS失败)
*/
extern int NDK_EthGetNetAddr(char *pszIp, char *pszMask, char *pszGateway, char *pszDns);


/**
 *@brief	使用dhcp获取网络地址
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 		操作失败(以太网驱动加载失败等)
 *@li	\ref NDK_ERR_NET_GETADDR "NDK_ERR_NET_GETADDR" 		获取本地地址或子网掩码失败
 *@li	\ref NDK_ERR_NET_ADDRILLEGAL "NDK_ERR_NET_ADDRILLEGAL" 	获取地址格式错误
 *@li	\ref NDK_ERR_NET_GATEWAY "NDK_ERR_NET_GATEWAY" 	    获取网关地址失败
*/
extern int NDK_NetDHCP(void);


/**
 *@brief	网络PING功能
 *@param	pszIp	本地IP地址字符串的首指针,不可为空指针.仅支持IPV4协议
 *@param	nTimeout	超时时间,单位为秒
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pszIp为NULL、pszIp长度大于15)
 *@li	\ref NDK_ERR "NDK_ERR" 				操作失败
*/
extern int NDK_NetPing(char *pszIp, uint nTimeout);


/**
 *@brief	域名解析
 *@param	pszDnsIp	解析得到的IP地址
 *@param	pszDnsName	域名
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pszDnsIp/pszDnsName为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败(调用gethostbyname()失败返回)
*/
extern int NDK_GetDnsIp(char *pszDnsIp,const char *pszDnsName);

/**
 *@brief 设置数据转发使用的通讯接口 
 *@param	 emCommType		通讯方式
 *@param     pszDestIP		设置服务器的地址
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pszDestIP为NULL)
 *@li	\ref NDK_ERR_NET_UNKNOWN_COMMTYPE "NDK_ERR_NET_UNKNOWN_COMMTYPE"                未知通讯方式类型
 *@li	\ref NDK_ERR_NET_INVALIDIPSTR "NDK_ERR_NET_INVALIDIPSTR" 	无效IP字符串
 *@li	\ref NDK_ERR_NET_UNSUPPORT_COMMTYPE "NDK_ERR_NET_UNSUPPORT_COMMTYPE" 	不支持的通信类型
 */

extern int NDK_NetAddRouterTable(EM_COMM_TYPE emCommType,char *pszDestIP);

/** @} */ // 以太网通讯模块结束
typedef enum {
    TK1 = 1,
    Tk2 = 2,
    TK3 = 3,
} ENUM_MAG_TRACK;

/** @addtogroup 磁卡
* @{
*/

/**
 *@brief	打开磁卡设备
 *@param	无
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败(mag设备节点已打开)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	打开设备文件错误(打开磁卡设备文件错误)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(磁卡驱动接口调用失败返回)
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
 *@li	\ref NDK_ERR_NOSWIPED "NDK_ERR_NOSWIPED" 		无磁卡刷卡记录
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
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(pszTk2/pszTk3/pszTk1为NULL、长度为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 				操作失败(磁卡设备未打开)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(磁卡驱动接口调用失败返回)
*/
extern int (*NDK_MagReadRaw)(uchar *pszTk1, ushort* pusTk1Len, uchar *pszTk2, ushort* pusTk2Len,uchar *pszTk3, ushort* pusTk3Len );

extern int (*NDK_MagReadRawData)(ENUM_MAG_DATA_TYPE type, ENUM_MAG_TRACK track, uint off, uint unLen, uchar *tkdata, uint *pnReadlen);

typedef struct {
	int card_flag;
	char channel[3][156];
	int pnErrorCode;
}mag_doublecard_t;

extern int (*NDK_MagReadCards)(mag_doublecard_t *card);
/** @} */ // 磁卡模块结束

/** @addtogroup IC卡
* @{
*/

typedef enum{
  ICTYPE_IC,  /**<接触式IC卡*/
  ICTYPE_SAM1, /**<SAM1卡*/
  ICTYPE_SAM2, /**<SAM2卡*/
  ICTYPE_SAM3, /**<SAM3卡*/
  ICTYPE_SAM4, /**<SAM4卡*/
  ICTYPE_M_1, /**<at24c01 at24c02 at24c04 at24c08 at24c16 at24c32 at24c64 */
  ICTYPE_M_2, /**<sle44x2*/
  ICTYPE_M_3, /**<sle44x8*/
  ICTYPE_M_4, /**<at88sc102*/
  ICTYPE_M_5, /**<at88sc1604*/
  ICTYPE_M_6, /**<at88sc1608*/
  ICTYPE_ISO7816, /**<ISO7816 standard*/
  ICTYPE_M_7, /**<at88sc153*/
  ICTYPE_M_1_1,/*<at24c01 */
  ICTYPE_M_1_2,/*<at24c02 */
  ICTYPE_M_1_4,/*<at24c04 */
  ICTYPE_M_1_8,/*<at24c08 */
  ICTYPE_M_1_16,/*<at24c16 */
  ICTYPE_M_1_32,/*<at24c32 */
  ICTYPE_M_1_64,/*<at24c64 */
}EM_ICTYPE;

typedef enum{
	SCCFG_PWRMODE = 1,		  // power up mode
	SCCFG_PWRVOL = 2, 		  // power up voltage(now, only IC card is valid)
}EM_CFGTYPE;

typedef enum {
	EMV_ADJBAUD_MODE = 1,		//EMV2000 + auto-adjust ATR transfer baud
	EMV_BPS_57600_MODE,			//EMV2000 + ATR transfer baud : 57600bps
	EMV_BPS_38400_MODE,			//EMV2000 + ATR transfer baud : 38400bps
	EMV_BPS_19200_MODE,			//EMV2000 + ATR transfer baud : 19200bps
	SSC_ADJBAUD_MODE,			//Social Security Card + auto-adjust ATR transfer baud
	ISO7816_BAUD_CFG_MODE,		//ISO7816 + ATR transfer baud : 9600bps + (Special Mode && PPS exchange)
	ISO7816_BPS_9600_MODE,		//ISO7816 + ATR transfer baud : 9600bps
	ISO7816_ADJBAUD_MODE,		//ISO7816 + auto-adjust ATR transfer baud
}EM_PWR_MODE;
/**
 *@brief	获取驱动程序版本号
 *@retval 	pszVersion   　　驱动程序版本号,要求缓冲大小不低于16字节　 
 *@return 	
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(pszVersion为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		打开设备文件错误(打开IC卡设备文件失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(IC驱动接口ioctl_getvision调用失败返回)
*/
int  (*NDK_IccGetVersion)(char *pszVersion);

/**
 *@brief	设置卡类型
 *@param	emIcType  	卡类型(参考\ref EM_ICTYPE "EM_ICTYPE")　
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(emIctype非法)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		打开设备文件错误(打开IC卡设备文件失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 	驱动调用错误(IC驱动接口ioctl_SetICType调用失败返回)
*/
extern int (*NDK_IccSetType)(EM_ICTYPE emIcType);

/**
 *@brief	获取卡类型
 *@retval 	pemIcType 	卡类型(参考\ref EM_ICTYPE "EM_ICTYPE")　
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(pemIcType非法)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		打开设备文件错误(打开IC卡设备文件失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 	驱动调用错误(IC驱动接口ioctl_GetICType调用失败返回)
*/
extern int (*NDK_IccGetType)(EM_ICTYPE *pemIcType);

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
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		打开设备文件错误(打开IC卡设备文件失败)
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
 *@li	\ref NDK_ERR "NDK_ERR" 				操作失败(MEMORY卡检测失败)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		设备文件打开失败(IC卡设备文件打开失败)
 *@li	\ref NDK_ERR_ICC_CARDNOREADY_ERR "NDK_ERR_ICC_CARDNOREADY_ERR" 	卡未准备好
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 	驱动调用错误
*/
extern int (*NDK_IccPowerUp) (EM_ICTYPE emIcType, uchar *psAtrBuf,int *pnAtrLen);

/**
 *@brief	下电
 *@param	emIcType 	卡类型(参考\ref EM_ICTYPE "EM_ICTYPE")
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			参数非法(emIcType非法)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		打开设备文件错误(打开IC卡设备文件失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 	驱动调用错误
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
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		打开设备文件错误(打开IC卡设备文件失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 	驱动调用错误
*/
extern int (*NDK_Iccrw)(EM_ICTYPE emIcType, int nSendLen,  uchar *psSendBuf, int *pnRecvLen,  uchar *psRecvBuf);

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

/**
*@brief 设置IC卡上电电压、遵循的规范 *@param pnMode 上电模式
0x01 EMV模式
0x02 57600波特率
0x03 38400波特率
0x04 19200波特率
0x05 社保卡模式
0x06 ISO规范模式，支持PPS
0x07 ISO规范上电，9600波特率，不支持PPS
*@param pnVoltage 上电电压
0x01 3V
0x02 5V
0x03 1.8V
*@return
*@li NDK_OK 操作成功
*@li \ref NDK_ERR_PARA "NDK_ERR_PARA" *@li \ref NDK_ERR_IOCTL "NDK_ERR_IOCTL"
参数非法(mode、voltage取值不在范围内) 驱动调用错误
*/
extern int (*NDK_IccSetPowerUpMode)(int pnMode, int pnVoltage);
/**
*@brief
*@param
*@retval
*@return
*@li NDK_OK 操作成功
*@li \ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 驱动调用错误
*/
extern int (*NDK_IccGetProtocol)(EM_ICTYPE emIctype, int *pnProtocol);

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


/**
 *@brief          展示非接图标操作
 *@param        onoff  是否展示 1-on 0-off
 *@return
 *@li        NDK_OK（0）                        操作成功
 *@li        NDK_ERR_OPEN_DEV（-4）    socket打开或连接失败
 *@li        NDK_ERR_WRITE（-12）        socket写失败
*/
extern int (*NDK_RfidLogoDisplay)(int onoff);

typedef enum{
	HCE_FUNC = 0,
	LPCD_FUNC,
}functionType;

/*@brief	获取设备是否支持HCE或LPCD模式
 *@param	functionType		0: HCE function
                                1：LPCD function
 *@param	isSupport		     0:不支持
                                 1:支持
                                 2:无法确定,需要上层再判断
 *@return
 *@li	\ref NDK_OK
 *@li	\ref NDK_ERR_PARA     "NDK_ERR_PARA"参数错误
 *@li	\ref NDK_ERR_OPEN_DEV   "NDK_ERR_OPEN_DEV" 驱动调用失败
*/
extern int (*NDK_RfidFunisSupport)(functionType Type , uchar* isSupport);


/**
 *@brief	获取射频驱动版本号
 *@param	pszVersion	返回的驱动版本号字符串
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pszVersion为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_GETVISION调用失败返回)
*/
extern int  (*NDK_RfidVersion)(uchar *pszVersion);


/**
 *@brief	射频接口器件初始化，可用于判断器件是否装配或可工作
 *@retval 	psStatus  扩充备用
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pszVersion为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_INIT调用失败返回)
 *@li	\ref NDK_ERR_RFID_INITSTA "NDK_ERR_RFID_INITSTA" 	非接触卡-射频接口器件故障或者未配置
*/
extern int (*NDK_RfidInit)(uchar *psStatus);


/**
 *@brief	开射频输出
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_RFOPEN调用失败返回)
*/
extern int (*NDK_RfidOpenRf)(void);


/**
 *@brief	关闭射频输出，可降低功耗并保护射频器件。
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_RFCLOSE调用失败返回)
*/
extern int (*NDK_RfidCloseRf)(void);


/**
 *@brief	获取卡片状态(是否已上电，用于判断是否可休眠)
 *@return
 *@li	NDK_OK			操作成功(已上电成功，忙状态)
 *@li	\ref NDK_ERR_RFID_NOTACTIV "NDK_ERR_RFID_NOTACTIV" 	非接触卡-未激活(未上电成功，非忙状态)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_WORKSTATUS调用失败返回)
*/
extern int (*NDK_RfidPiccState)(void);


/**
 *@brief	设置超时控制
 *@param　　ucTimeOutCtl 值０是不执行，值不为０是执行
 *@retval
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_TIMEOUTCTL调用失败返回)
*/
extern int  (*NDK_RfidTimeOutCtl)(uchar ucTimeOutCtl);


/**
 *@brief	设备强制休眠
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_SUSPEND调用失败返回)
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
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(ucPiccType非法)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_SETPICCTYPE调用失败返回)
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
extern int (*NDK_RfidPiccType)(uchar ucPiccType);


/**
 *@brief	射频卡寻卡(探测刷卡区域是否有卡)
 *@retval 	psPiccType 	激活的卡类型 0xcc=TYPE-A卡，0xcb=TYPE-B卡(该参数无效，传NULL进行调用).
 *@return
 *@li	NDK_OK			操作成功（寻卡成功）
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_PICCDEDECT调用失败返回)
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
extern int (*NDK_RfidPiccDetect)(uchar *psPiccType);

/**
 *@brief	射频卡寻卡(探测刷卡区域是否有卡)
 *@retval 	psPiccType 	激活的卡类型 0xcc=TYPE-A卡，0xcb=TYPE-B卡(该参数无效，传NULL进行调用).
 *@retval 	pnAtqlen	接收数据长度
 *@retval	psAtqbuf	接收数据缓冲区
 *@return
 *@li	NDK_OK			操作成功（寻卡成功）
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_PICCDEDECT调用失败返回)
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
extern int (*NDK_RfidPiccDetect_Atq)(uchar *psPiccType,int *pnAtqlen,uchar* psAtqbuf);

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
extern int (*NDK_RfidDetectWithCardType)(uint32_t *psPicctype, int* outputBufLen, uchar* outputBuf);

/**
 *@brief	射频卡激活(已探测有卡的情况下),相当于powerup , 按改进的流程（原先生产版本）。
 *@retval 	psPiccType	激活的卡类型 0xcc=TYPE-A卡，0xcb=TYPE-B卡
 *@retval	pnDataLen	数据长度
 *@retval	psDataBuf	数据缓冲区(A卡为UID；B卡psDataBuf[1]~[4]为UID，其它是应用及协议信息)
 *@return
 *@li	NDK_OK			激活成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(psPiccType/pnDataLen/psDataBuf为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_PICCACTIVATE调用失败返回)
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
extern int (*NDK_RfidPiccActivate)(uchar *psPiccType, int *pnDataLen,  uchar *psDataBuf);
extern int (*NDK_RfidPiccActivateWithInfo)(uchar *psPicctype, int *pnDatalen, uchar *psDatabuf, int *pnInfolen, uchar *psInfobuf);

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
 *@param	Piccparamtype		需要获取的卡片参数类型，目前支持： CARD_UID
                       											CARD_TYPEA_ATQA
                       											CARD_TYPEA_ATS
                       											CARD_TYPEB_ATQB
 *@param	Paramlen			参数长度
 *@param	Parambuf			参数缓冲区
 *@return
 *@li	\ref NDK_OK									操作成功
 *@li	\ref NDK_ERR_PARA：							参数非法(Piccparamtype非法/Paramlen、Parambuf为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(射频驱动接口 IOCTL_RFID_SET_FELICA_TIMEOUT 调用失败返回)
 *@li   \ref ..............
 */
extern int (*NDK_RfidGetPiccInfo)(uchar ucPiccinfotype, int *pnInfolen, uchar *psInfobuf);


/**
 *@brief	射频卡激活(已探测有卡的情况下),相当于powerup ,按EMV L1 MAINLOOP流程。
 *@retval 	psPiccType	激活的卡类型 0xcc=TYPE-A卡，0xcb=TYPE-B卡
 *@retval	pnDataLen	数据长度
 *@retval	psDataBuf	数据缓冲区(A卡为UID；B卡psDataBuf[1]~[4]为UID，其它是应用及协议信息)
 *@return
 *@li	NDK_OK			激活成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(psPiccType/pnDataLen/psDataBuf为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_PICCACTIVATE_EMV调用失败返回)
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
extern int  (*NDK_RfidPiccActivate_EMV)(uchar *psPiccType, int *pnDataLen,  uchar *psDataBuf);




/**
 *@brief	关闭射频使卡失效。读写操作结束后应该执行该操作，相当于powerdown 。
 *@param	ucDelayMs	等0则一直关闭RF;不等0则关闭ucDelayMs毫秒后再打开RF。
                      关闭6～10ms可使卡失效。如果后续没有连续的读卡操作，应该置0以关闭RF 。
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口ioctl_PiccDeselect调用失败返回)
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
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(psSendBuf/pnRecvLen/psReceBuf为NULL、nSendLen小于5)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_PICCAPDU调用失败返回)
*/
extern int (*NDK_RfidPiccApdu)(int nSendLen, uchar *psSendBuf, int *pnRecvLen,  uchar *psReceBuf);

/**
 *@brief   LPCD射频卡非阻塞寻卡(探测刷卡区域是否有卡)
 *@param	ucPicctype		LPCD寻卡，卡片类型
 *							ucPicctype&0x01 == 0x01 寻TYPE A卡
 *							ucPicctype&0x02 == 0x02 寻TYPE B卡
 *							ucPicctype&0x04 == 0x04 寻TYPE F卡
 *
 *@param	detectMode      LPCD检卡模式
 *							0：纯LPCD检卡，
 *								->开启后，如LPCD内检测到卡lpcdState设置检卡状态为POLLING_DETECT_CARD，否则设置检卡状态为DETECT_NO_CARD
 *
 *							1：复合LPCD检卡模式一
 *								->开启后，如LPCD内检测到卡，执行Detect操作确认是否真实有卡，是lpcdState设置检卡状态POLLING_DETECT_CARD，否lpcdState设置检卡状态LPCD_DETECT_CARD
 *								->开启后，如LPCD内未检测到卡，每隔3000ms进行一次Detect操作检测是否有卡片在位
 *
 *							2：复合LPCD检卡模式二
 *								->开启后，如LPCD内检测到卡，执行Detect操作确认是否真实有卡，是lpcdState设置检卡状态POLLING_DETECT_CARD，否lpcdState设置检卡状态LPCD_DETECT_CARD
 *@retval
 *@return
 *@li   NDK_OK          		操作成功
 *@li   NDK_ERR          		重复开启LPCD阻塞寻卡，或者已开启非阻塞寻卡
 *@li   NDK_ERR_OPEN_DEV    	设备文件打开失败(射频设备文件打开失败)
 *@li   NDK_ERR_IOCTL       	驱动调用错误
 *@li   NDK_ERR_PARA			参数错误
*/
extern int (*NDK_RfidLpcdStartDetect)(void);

/**
 *@brief   获取LPCD射频卡非阻塞寻卡状态
 *@param
 *@retval  lpcdState		LPCD检卡状态
 *							0（DETECT_NO_CARD）：无卡
 *							1（LPCD_DETECT_CARD）：LPCD检测到场强变化，应用获取到该状态后，需上电、detect确认是否卡真实在位
 *							2（POLLING_DETECT_CARD）：DETECT到卡片，且非接触场已打开，应用获取到该状态后，可调用active接口进行卡片激活
 *@return  psPicctype	    非接卡卡类型
 *							psPicctype&0x01 == 0x01 说明寻到TYPE A卡
 *							psPicctype&0x02 == 0x02 说明寻到TYPE B卡
 *							psPicctype&0x04 == 0x04 说明寻到TYPE F卡
 *
 *@li   NDK_OK          		操作成功
 *@li   NDK_ERR          		操作失败，未开启非阻塞LPCD寻卡
 *@li   NDK_ERR_OPEN_DEV    	设备文件打开失败(射频设备文件打开失败)
 *@li   NDK_ERR_IOCTL       	驱动调用错误
 *@li   NDK_ERR_PARA			参数错误
*/
extern int (*NDK_RfidLpcdGetState)(uchar *lpcdState);

/**
 *@brief   关闭LPCD射频卡非阻塞寻卡(探测刷卡区域是否有卡)
 *@param
 *@retval
 *@return
 *@li   NDK_OK          		操作成功
 *@li   NDK_ERR          		操作失败，未开启非阻塞LPCD寻卡
*/
extern int (*NDK_RfidLpcdStopDetect)(void);


/**
 *@brief	M1寻卡(寻卡类型必须已经设置为TYPE-A)
 *@param	ucReqCode		0=请求REQA, 非0=唤醒WUPA, 一般情况下需要使用WUPA
 *@retval 	pnDataLen	 接收数据长度(2字节)
 *@retval	psDataBuf	接收数据缓冲区
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
extern int (*NDK_M1Request)(uchar ucReqCode, int *pnDataLen, uchar *psDataBuf);


/**
 *@brief	M1卡防冲突(NDK_M1Request有卡的情况下)
 *@retval 	pnDataLen	接收数据长度(UID长度)
 *@retval	psDataBuf	接收数据缓冲区(UID)
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pnDataLen/psDataBuf为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_M1ANTI调用失败返回)
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
extern int (*NDK_M1Anti)(int *pnDataLen, uchar *psDataBuf);


/**
 *@brief	M1卡防冲突(NDK_M1Request有卡的情况下),针对多级级联的UID .
 *@param	ucSelCode	PICC_ANTICOLL1/PICC_ANTICOLL2/PICC_ANTICOLL3
 *@retval 	pnDataLen	接收数据长度(UID长度)
 *@retval	psDataBuf	接收数据缓冲区(UID)
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pnDataLen/psDataBuf为NULL、ucSelcode非法)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_M1ANTI调用失败返回)
*/
extern int  (*NDK_M1Anti_SEL)(uchar ucSelCode, int *pnDataLen, uchar *psDataBuf);


/**
*@brief		M1卡选卡(NDK_M1Anti成功的情况下)
*@param		nUidLen			uid长度
*@param		psUidBuf	     uid数据缓冲区
*@retval 	psSakBuf	    选卡返回数据(1字节SAK)
*@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(psUidBuf/psSakBuf为NULL、nUidLen不等于4)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_M1SELECT调用失败返回)
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
extern int (*NDK_M1Select)(int nUidLen, uchar *psUidBuf, uchar *psSakBuf);


/**
 *@brief	M1卡选卡(NDK_M1Anti成功的情况下),针对多级级联的UID .
 *@param	ucSelCode	PICC_ANTICOLL1/PICC_ANTICOLL2/PICC_ANTICOLL3
 *@param	nUidLen		uid长度
 *@retval	psUidBuf	uid数据缓冲区
 *@retval	psSakBuf	选卡返回数据(1字节SAK)
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(psUidBuf/psSakBuf为NULL、nUidLen不等于4、ucSelcode非法)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_M1SELECT调用失败返回)
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
extern int  (*NDK_M1Select_SEL)(uchar ucSelCode, int nUidLen, uchar *psUidBuf, uchar *psSakBuf);


/**
 *@brief	M1卡认证密钥存储(同一个密钥存储一次即可)
 *@param	ucKeyType		认证密钥类型 A=0x00 ；B=0x01
 *@param	ucKeyNum	 密钥序列号(0~15)
 *@param	psKeyData		密钥,6字节
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(ucKeyType、ucKeyNum非法、psKeyData为NULL)
 *@li	\ref NDK_ERR "NDK_ERR" 		              操作失败
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_M1KEYSTORE调用失败返回)
*/
extern int (*NDK_M1KeyStore)(uchar ucKeyType,  uchar ucKeyNum, uchar *psKeyData);


/**
 *@brief	M1卡装载已存储的密钥(同一个密钥加载一次即可)
 *@param	ucKeyType	认证密钥类型 A=0x00 ；B=0x01
 *@param    ucKeyNum	 密钥序列号(0~15，A/B各有16个密钥)
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(ucKeyType、ucKeyNum非法)
 *@li	\ref NDK_ERR "NDK_ERR" 		              操作失败
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_M1KEYLOAD调用失败返回)
*/
extern int (*NDK_M1KeyLoad)(uchar ucKeyType,  uchar ucKeyNum);


/**
 *@brief	M1卡用已加载的密钥认证
 *@param	nUidLen	uid长度
 *@param	psUidBuf	uid数据(NDK_M1Anti获取的)
 *@param	ucKeyType	认证密钥类型 A=0x00 ；B=0x01
 *@param    ucBlockNum	要认证的块号(注意:不是扇区号!)
 *@return
 *@li	NDK_OK			操作成功
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
extern int (*NDK_M1InternalAuthen)(int nUidLen, uchar *psUidBuf, uchar ucKeyType, uchar ucBlockNum);


/**
 *@brief	M1卡直接外带KEY认证
 *@param	nUidLen	uid长度
 *@param	psUidBuf	uid数据(NDK_M1Anti获取的)
 *@param	ucKeyType	认证密钥类型 A=0x00 ；B=0x01
 *@param	psKeyData	key(6字节)
 *@param    ucBlockNum	要认证的块号(注意:不是扇区号!)

 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 		              操作失败
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(ucKeyType、nUidLen非法、psKeyData为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		驱动调用错误(射频驱动接口RFID_IOCG_M1INTERAUTHEN调用失败返回)
*/
extern int (*NDK_M1ExternalAuthen)(int nUidLen, uchar *psUidBuf, uchar ucKeyType, uchar *psKeyData, uchar ucBlockNum);


/**
 *@brief	M1卡'块'读取操作
 *@param	ucBlockNum	要读的块号
 *@retval	pnDataLen	读取的块数据长度
 *@retval	psBlockData	块数据
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
 *@brief    felica卡寻卡
 *@param 	
 *@return   psRecebuf返回数据buf，pnRecvlen返回数据长度
 *@li   NDK_OK          操作成功
 *@li   NDK_ERR_PARA        参数错误(pnRfidtype为NULL)
 *@li   NDK_ERR_OPEN_DEV    设备文件打开失败(射频设备文件打开失败)
 *@li   NDK_ERR_IOCTL       驱动调用错误(射频驱动接口ioctl_Get_rfid_ic_type调用失败返回)
 *@li   NDK_ERR_RFID_NOCARD  场强范围内无卡片
*/
extern  int (*NDK_RfidFelicaPoll)(uchar *psRecebuf,int *pnRecvlen);
/**
 *@brief	felica卡轮询接口
 *@retval	fex :	felica polling指令格式，用于发送不同的polling指令
 *	psRecebuf为轮询之后返回的felica数据，pnRecvlen轮询后felica返回的数据长度
 *          psRecebuf包含:出厂识别码(IDm)和出厂参数(PMm),这两个参数固化在芯片中,不得修改
 *@return
 *@li	NDK_OK			操作成功
 *@li\ref NDK_ERR_PARA "NDK_ERR_PARA" 	参数错误(psRecebuf或者pnRecvlen为NULL)
 *@li\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 	驱动调用错误(射频驱动接口IOCTL_RFID_FELICA_POLL调用失败返回)
 *@li	\ref NDK_ERR_RFID_NOCARD             没有寻到felica卡
*/
extern int (*NDK_FelicaPoll)(felica_param_t fex, uchar *psRecebuf, uint *pnRecvlen);

/**
 *@brief    felica卡数据读写，传入和返回数据格式为Len+cmd+data
 *@param 	nSendlen 发送数据长度，psSendbuf 发送数据buf
 *@return   psRecebuf返回数据buf，pnRecvlen返回数据长度
 *@li   NDK_OK          操作成功
 *@li   NDK_ERR_PARA        参数错误(pnRfidtype为NULL)
 *@li   NDK_ERR_OPEN_DEV    设备文件打开失败(射频设备文件打开失败)
 *@li   NDK_ERR_IOCTL       驱动调用错误(射频驱动接口ioctl_Get_rfid_ic_type调用失败返回)
 *@li   其他  参照Mifara卡的错误码
*/
extern int  (*NDK_RfidFelicaApdu)(int nSendlen, uchar *psSendbuf, int *pnRecvlen,  uchar *psRecebuf);
/**
 *@brief	Mifare 寻卡、防碰撞、选卡(寻卡类型必须已经设置为TYPE-A)
 *@param	ucReqCode	0=请求REQA, 非0=唤醒WUPA, 一般情况下需要使用WUPA
 *@retval 	psUID		UID数据缓冲区
 *@retval 	psUIDLen	UID数据长度
 *@retval 	psSak		最后一次选卡操作返回的SAK数据
 *@retval
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pnDataLen/psDataBuf为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
 *@li	 NDK_ERR_MI_WriteFifo=           -2041
  *@li	NDK_ERR_MI_NOTAGERR=			-2030,
  *@li	NDK_ERR_MI_CRCERR=				-2031,
*@li	NDK_ERR_MI_PARITYERR=			-2034,
*@li	NDK_ERR_MI_COLLERR=             -2048,
*@li	NDK_ERR_MI_CardTypeErr=			-2056,
*@li	NDK_ERR_MI_ParaErrInIoctl=		-2057,
*@li	NDK_ERR_MI_PROTOCOLERR=			-2051,
*/
extern int (*NDK_MifareActive)(uchar ucReqCode,uchar *psUID, uchar *pnUIDLen, uchar *psSak);
extern int (*NDK_MifareActiveWithInfo)(uchar ucReqCode,uchar *psUID, uchar *pucUIDLen, uchar *psSak, int *pnInfolen, uchar *psInfobuf);
/**
 *@brief	M0卡 '页'读取操作
 *@param	ucPageNum	要读的起始页号
 *@retval	pnDataLen	读取的数据长度(返回16, Mifare UltraLight系列卡片的读操作返回16字节数据)
 *@retval	psPageData	读取的数据
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pnDataLen、psBlockData为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
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
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pnDataLen、psBlockData为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
*/
extern int (*NDK_M0Write)(uchar ucPageNum, int pnDataLen, uchar *psPageData);

/**
 *@brief	M0卡 3次相互认证
 *@param	psKey		认证密钥,  密钥为16字节
 *@return
 *@li	NDK_OK			操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(ucKeyType、nUidLen非法、psUidBuf为NULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
*/
extern int (*NDK_M0Authen)(uchar *psKey);



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
    0xFF     :没有无线模块\n
    0x02    :MC39	\n
    0x03    :sim300	\n
    0x06    :M72	\n
	高位为0x80表示CDMA模块\n
    0x81    :dtgs800	\n
    0x82    :dtm228c	\n\n
   pinfo[1]:射频模块	\n
    0xFF     :没有射频模块	\n
    0x01     :RC531	\n
    0x02    :PN512	\n\n
   pinfo[2]:磁卡模块	\n
    0xFF     :没有磁卡模块	\n
    0x01     :mesh	\n
    0x02     :giga	\n\n
   pinfo[3]:扫描头模块	\n
    0xFF     :没有磁卡模块	\n
    0x01     :EM1300	\n
    0x02     :EM3000	\n
    0x03     :SE955	\n\n
   pinfo[4]:是否支持外接密码键盘	\n
    0xFF     :不支持	\n
    0x01     :支持	\n\n
   pinfo[5]:串口个数	\n
    0xFF     :无	\n
    0x01     :1个	\n
    0x02     :2个	\n\n
   pinfo[6]:是否支持USB	\n
    0xFF     :否	\n
    0x01     :是	\n\n
   pinfo[7]:MODEM模块	\n
    0xFF     :否	\n
    0x01     :是	\n\n
   pinfo[8]:wifi模块	\n
    0xFF     :无wifi模块	\n
    0x01     :是"USI WM-G-MR-09"模块	\n\n
   pinfo[9]:是否支持以太网	\n
    0xFF     :否	\n
    0x01     :dm9000	\n
    0x02     :bcm589xcore       \n\n
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
	SYS_HWINFO_GET_POS_TYPE=0,      		/**<取pos机器类型   			*/
	SYS_HWINFO_GET_HARDWARE_INFO,       /**<获取POS机上所支持硬件类型，详细返回值如上所述*/
	SYS_HWINFO_GET_BIOS_VER,        		/**<取bios版本信息 			 */
	SYS_HWINFO_GET_POS_USN,        		/**<取机器序列号    		*/
	SYS_HWINFO_GET_POS_PSN,        		/**<取机器机器号    		*/
	SYS_HWINFO_GET_BOARD_VER,       		/**<取主板号        			*/
	SYS_HWINFO_GET_CREDITCARD_COUNT,		/**<取pos刷卡总数					*/
	SYS_HWINFO_GET_PRN_LEN,				/**<取pos打印总长度    		*/
	SYS_HWINFO_GET_POS_RUNTIME,          /**<取pos机开机运行时间  */
	SYS_HWINFO_GET_KEY_COUNT,            /**<取pos机按键次数  */
	SYS_HWINFO_GET_CPU_TYPE,           /**<取pos机cpu类型  */
	SYS_HWINFO_GET_BOOT_VER,
	SYS_HWINFO_GET_POS_TUSN = 15,        		/**<取机器TUSN 		*/

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
    LED_RED_X_ON = 0x10000,        /**<   控制红灯亮           */
    LED_RED_X_OFF = 0x20000,        /**<   控制红灯灭           */
    LED_RED_X_FILCK = 0x30000,        /**<   控制红灯闪           */
    LED_YELLOW_X_ON = 0x40000,        /**<   控制黄灯亮           */
    LED_YELLOW_X_OFF = 0x80000,        /**<   控制黄灯灭           */
    LED_YELLOW_X_FILCK = 0xc0000,        /**<   控制黄灯闪           */
    LED_GREEN_X_ON = 0x100000,        /**<   控制绿灯亮           */
    LED_GREEN_X_OFF = 0x200000,        /**<   控制绿灯灭           */
    LED_GREEN_X_FILCK = 0x300000,        /**<   控制绿灯闪           */
    LED_BLUE_X_ON = 0x400000,        /**<   控制蓝灯亮           */
    LED_BLUE_X_OFF = 0x800000,        /**<   控制蓝灯灭          */
    LED_BLUE_X_FILCK = 0xc00000,        /**<   控制蓝灯闪           */
    LED_RFID_RED_ON = 0x01,             /**<   控制射频红色灯亮         */
    LED_RFID_RED_OFF = 0x02,        /**<   控制射频红色灯灭         */
    LED_RFID_RED_FLICK = 0x03,          /**<   控制射频红色灯闪         */
    LED_RFID_YELLOW_ON = 0x04,              /**<   控制射频黄色灯亮         */
    LED_RFID_YELLOW_OFF = 0x08,         /**<   控制射频黄色灯灭         */
    LED_RFID_YELLOW_FLICK = 0x0c,           /**<   控制射频黄色灯闪         */
    LED_RFID_GREEN_ON = 0x10,           /**<   控制射频绿色灯亮         */
    LED_RFID_GREEN_OFF = 0x20,          /**<   控制射频绿色灯灭         */
    LED_RFID_GREEN_FLICK = 0x30,            /**<   控制射频绿色灯闪         */
    LED_RFID_BLUE_ON = 0x40,            /**<   控制射频蓝色灯亮         */
    LED_RFID_BLUE_OFF = 0x80,           /**<   控制射频蓝色灯灭         */
    LED_RFID_BLUE_FLICK = 0xc0,         /**<   控制射频蓝色灯闪         */
    LED_COM_ON = 0x100,             /**<   控制通讯灯亮           */
    LED_COM_OFF = 0x200,        /**<   控制通讯灯灭           */
    LED_COM_FLICK = 0x300,          /**<   控制通讯灯闪           */
    LED_ONL_ON = 0x400,             /**<   控制联机灯亮           */
    LED_ONL_OFF = 0x800,        /**<   控制联机灯灭           */
    LED_ONL_FLICK = 0xc00,          /**<   控制联机灯闪           */
    LED_DETECTOR_ON = 0x1000,
    LED_DETECTOR_OFF = 0x2000,
    LED_DETECTOR_FLICK = 0x3000,
    LED_MAG_ON = 0x4000,        /**<   控制磁卡灯亮           */
    LED_MAG_OFF = 0x8000,       /**<   控制磁卡灯灭           */
    LED_MAG_FLICK = 0xc000,     /**<   控制磁卡灯闪           */
} EM_LED;

typedef struct {
	ulong unFlickOn;   /**<Flickering on duration (Unit:100 ms)*/
	ulong unFlickOff;  /**<Flickering off duration (Unit:100 ms)*/
	ulong unRverse[2];
	int version;
	int alwaysDisplayBackground;
    int x;
    int y;
	int horizontal;
} ST_NDK_LED_FLICK;

/*主设备号*/
typedef enum {
    SS_TYPE_KEYBOARD,		/**<按键*/
    SS_TYPE_PRINTER,		/**<打印机*/
    SS_TYPE_MAG,			/**<磁卡*/
    SS_TYPE_ICCARD,			/**<IC卡*/
    SS_TYPE_RFID,			/**<射频卡*/
    SS_TYPE_MODEM,		/**<MODEM*/
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
	SS_KEYBOARD_ONE = (SS_TYPE_KEYBOARD<<16|26),		/**<统计数字1键*/
	SS_KEYBOARD_TWO = (SS_TYPE_KEYBOARD<<16|25),		/**<统计数字2键*/
	SS_KEYBOARD_THREE = (SS_TYPE_KEYBOARD<<16|24),	/**<统计数字3键*/
	SS_KEYBOARD_FOUR = (SS_TYPE_KEYBOARD<<16|22),		/**<统计数字4键*/
	SS_KEYBOARD_FIVE = (SS_TYPE_KEYBOARD<<16|21),		/**<统计数字5键*/
	SS_KEYBOARD_SIX = (SS_TYPE_KEYBOARD<<16|20),		/**<统计数字6键*/
	SS_KEYBOARD_SEVEN = (SS_TYPE_KEYBOARD<<16|18),	/**<统计数字7键*/
	SS_KEYBOARD_EIGHT = (SS_TYPE_KEYBOARD<<16|17),	/**<统计数字8键*/
	SS_KEYBOARD_NINE = (SS_TYPE_KEYBOARD<<16|16),		/**<统计数字9键*/
	
              SS_KEYBOARD_ENTER = (SS_TYPE_KEYBOARD<<16|8),	/**<统计确认键*/
              SS_KEYBOARD_ESC = (SS_TYPE_KEYBOARD<<16|10),		/**<统计取消键*/
              SS_KEYBOARD_F2 = (SS_TYPE_KEYBOARD<<16|29),		/**<统计F2(菜单键)*/
              SS_KEYBOARD_F1 = (SS_TYPE_KEYBOARD<<16|28),		/**<统计F1(向上键)*/
              SS_KEYBOARD_DOT = (SS_TYPE_KEYBOARD<<16|14),		/**<统计小数点键*/
              SS_KEYBOARD_ZMK = (SS_TYPE_KEYBOARD<<16|12),		/**<统计字母键*/
              SS_KEYBOARD_F3 = (SS_TYPE_KEYBOARD<<16|30),		/**<统计F3(向下键)*/             
	SS_KEYBOARD_BASP = (SS_TYPE_KEYBOARD<<16|9),	/**<统计退格键*/
	SS_KEYBOARD_0_ID = (SS_TYPE_KEYBOARD<<16|2),	/**<统计其他键*/
	SS_KEYBOARD_1_ID = (SS_TYPE_KEYBOARD<<16|3),	/**<统计其他键*/
	SS_KEYBOARD_2_ID = (SS_TYPE_KEYBOARD<<16|4),	/**<统计其他键*/
	SS_KEYBOARD_3_ID = (SS_TYPE_KEYBOARD<<16|5),	/**<统计其他键*/
	SS_KEYBOARD_F4 = (SS_TYPE_KEYBOARD<<16|31),		/**<统计F4(关机键)*/
	SS_KEYBOARD_TOTAL = (SS_TYPE_KEYBOARD<<16|33),	/**<统计所有键*/	

	SS_PRT_PAPER_ID = (SS_TYPE_PRINTER<<16|0),    		/**<打印米数(单位为毫米mm)*/
	SS_PRT_HEAT_ID = (SS_TYPE_PRINTER<<16|1),			/**<热敏头加热时长(单位为秒)*/

	SS_MAG_TIMES_ID = (SS_TYPE_MAG<<16|0),			/**<刷卡次数*/

	SS_ICCARD_BASE_ID = (SS_TYPE_ICCARD<<16|0),		/**<插卡次数*/

	SS_RFID_TIMES_ID = (SS_TYPE_RFID<<16|0),		/**<寻卡次数*/

	SS_MODEM_TIMES_ID = (SS_TYPE_MODEM<<16|0),		/**<MODEM连接次数*/
	SS_MODEM_FAILTIMES_ID = (SS_TYPE_MODEM<<16|1),	/**<MODEM连接失败次数*/
	SS_MODEM_SDLCTIME_ID = (SS_TYPE_MODEM<<16|2),	/**<MODEM时长(SDLC)*/
	SS_MODEM_ASYNTIME_ID = (SS_TYPE_MODEM<<16|3),	/**<MODEM时长(异步)*/

	SS_WLS_TIMES_ID = (SS_TYPE_WLS<<16|0),			/**<无线连接次数*/
	SS_WLS_FAILTIMES_ID = (SS_TYPE_WLS<<16|1),		/**<无线连接失败次数*/
	SS_WLS_PPPTIME_ID = (SS_TYPE_WLS<<16|2),		/**<无线连接时长(单位为毫秒)*/

	SS_WIFI_TIMES_ID = (SS_TYPE_WIFI<<16|0),		/**<WIFI连接次数*/
	SS_WIFI_TIME_ID = (SS_TYPE_WIFI<<16|1),			/**<WIFI连接时长(单位为毫秒)*/

	SS_POWER_TIMES_ID = (SS_TYPE_POWER<<16|0),		/**<开关机次数*/
	SS_POWERUP_TIME_ID = (SS_TYPE_POWER<<16|1)		/**<开机时间(单位为秒)*/
}EM_SS_DEV_ID;

/**
 *@brief 固件版本类型
*/
typedef enum {
	SYS_FWINFO_PRO,      		/**<正式版本   			*/
	SYS_FWINFO_DEV				/**<开发版本   			*/
} EM_SYS_FWINFO;

/**
 *@brief	获取NDK库版本号
 *@retval   pszVer	版本号字符串,缓冲大小不低于16字节
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"		参数非法(pszVer为NULL)
*/
extern int (*NDK_Getlibver)(char *pszVer);

/**
 *@brief 		系统初始化(暂屏蔽该API)
 *@details
 *@return
 *@li	 NDK_OK				操作成功
 *@li	其它NDK_ERRCODE		操作失败
*/
extern int (*NDK_SysInit)(void);
/**
 *@brief 		系统退出
 *@details	nErrCode为0表示正常退出.非零表示异常退出，nErrCode会返回给系统。
 *@param    nErrCode	系统退出的返回值
 *@return
 *@li	 NDK_OK				操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"		操作失败
*/
extern int (*NDK_SysExit)(int nErrCode);
/**
 *@brief 		POS重启
 *@details
 *@return
 *@li	 NDK_OK				操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"		操作失败
*/
extern int (*NDK_SysReboot)(void);
/**
 *@brief 		POS关机
 *@details
 *@return
 *@li	 NDK_OK				操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"		操作失败
*/
extern int (*NDK_SysShutDown)(void);
/**
 *@brief 		Beep只响一声，如果要连续响多声，可以在中间加延时
 *@details
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"		打开设备文件失败
 *@li	 \ref NDK_ERR_IOCTL "NDK_ERR_IOCTL"			驱动调用错误
*/
extern int (*NDK_SysBeep)(void);
/**
 *@brief 		设置beep的音量
 *@details
 *@param    unVolNum    所要设置的音量的参数，参数范围为0~5，不设置底层默认为5
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"		参数错误(unVolNum非法)
 *@li	 \ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"	打开设备文件失败
*/
extern int (*NDK_SysSetBeepVol)(uint unVolNum);

typedef enum {
	IS_GET_BATTRRY_TEMP_SUPPORT = 0,     //是否支持获取电池温度，0不支持，1支持
	IS_GET_CHARGE_CURRENT_SUPPORT,       //是否支持获取充电电流，0不支持，1支持
	BATTERY_TEMP,                        //当前的电池温度，单位0.1℃
	ADAPTER_VOLTAGE,                     //当前的适配器电压，单位uV
	CHARGE_CURRENT,                      //当前的充电电流，单位uA
} EM_BATTERY_PROPERTY;

extern int (*NDK_SysGetBatteryProperty)(EM_BATTERY_PROPERTY type, int bufLen, char *value);
/**
 *@brief 		取beep的音量
 *@details
 *@retval    punVolNum    所要设置的音量的参数
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"		参数错误(unVolNum非法)
 *@li	 \ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"	打开设备文件失败
*/
extern int (*NDK_SysGetBeepVol)(uint *punVolNum);
/**
 *@brief 		按一定的频率响一定的时间
 *@details
 *@param    unFrequency		声音的频率，单位:Hz,范围为0 < unFrequency <=4000
 *@param    unSeconds		声音持续的时间，单位:ms,范围为unSeconds > 0
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"		参数错误(unFrequency非法、unSeconds小于0)
 *@li	 \ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"	打开设备文件失败
 *@li	 \ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误
*/
extern int (*NDK_SysTimeBeep)(uint unFrequency,uint unSeconds);
/**
 *@brief 		设置是否允许自动进入休眠
 *@param    unFlag  0:不允许自动进入休眠，1:允许自动进入休眠，其他值参数不合法
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"		参数错误(unFlag非法)
 *@li	 \ref NDK_ERR "NDK_ERR"	操作失败
*/
extern int (*NDK_SysSetSuspend)(uint unFlag);
/**
 *@brief 		设置是否立即进入休眠
 *@details  设置是否自动进入休眠开关对此函数无影响。只要调用机器立即进入休眠
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR "NDK_ERR"	操作失败
*/
extern int (*NDK_SysGoSuspend)(void);
/**
 *@brief 		取电源电量
 *@retval   punVol  只插电源则为0，否则返回电池电量
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"		参数非法(punVol为NULL)
 *@li	 \ref NDK_ERR "NDK_ERR"	操作失败
*/
extern int (*NDK_SysGetPowerVol)(uint *punVol);
/**
 *@brief        取电源电量范围
 *@retval   punMax:最大值 punMin:最小值
 *@return
 *@li    NDK_OK             操作成功
 *@li    \ref NDK_ERR_PARA "NDK_ERR_PARA"       参数非法(punVol为NULL)
 *@li    \ref NDK_ERR "NDK_ERR" 操作失败
*/
extern int (*NDK_SysGetPowerVolRange)(uint * punMax, uint * punMin);
/**
 *@brief 		单位延时( 单位时间为0.1s)
 *@details
 *@param    unDelayTime 延时时间，范围unDelayTime > 0
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"		参数非法(unDelayTime小于0)
*/
extern int (*NDK_SysDelay)(uint unDelayTime);
/**
 *@brief 		单位延时 (单位时间为1ms)
 *@details
 *@param    unDelayTime 延时时间，范围unDelayTime > 0
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"		参数非法(unDelayTime<=0)
*/
extern int (*NDK_SysMsDelay)(uint unDelayTime);
/**
 *@brief 		取POS当前时间
 *@details
 *@param     pstTime  传入tm结构体类型的指针，返回当前pos时间
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"		参数非法(pstTime为NULL)
*/
extern int (*NDK_SysGetPosTime)(struct tm *pstTime);

/**
 *@brief 		设置POS当前时间
 *@details
 *@param     stTime  传入tm结构体类型的变量，设置pos时间为变量stTime的时间
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"		参数非法(stTime非法)
 *@li	 \ref NDK_ERR "NDK_ERR"		操作失败(调用mktime()/stime()失败返回)
*/
extern int (*NDK_SysSetPosTime)(struct tm stTime);

/**
 *@brief 		  读取指定字库的内容(接口暂定，未实现)
 *@details    根据偏移取指定字库的n个字节的内容
 *@param      psPath 字库所在的路径
 *@param      unOffSet 字库偏移量
 *@param      unLen 要取字库字节数
 *@retval     psBuf 用于存储取出来的字库内容信息
 *@return
 *@li	 NDK_OK				操作成功
 *@li	其它\ref EM_NDK_ERR "EM_NDK_ERR"		操作失败
*/
extern int (*NDK_SysReadFont)(const char * psPath,uint unOffSet,char *psBuf,uint unLen);
/**
 *@brief 		设置POS上面所有led灯的亮灭情况
 *@details
*@param      emStatus    枚举类型的变量，控制各个灯的亮灭，不同的各个灯之间可通过相或进行控制。
							 					如果相应灯的枚举变量为0(即不或上相应的值)，则相对应的灯的状态不变，如:
							 					NDK_LedStatus(LED_RFID_RED_ON|LED_RFID_YELLOW_FLICK),该设置为设置红灯亮，黄灯闪，其他等状态不变。
							 					所以控制玩相应的灯之后需要注意是否恢复。
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(emStatus)
 *@li	 \ref NDK_ERR "NDK_ERR"   		操作失败
*/
extern int (*NDK_LedStatus)(EM_LED emStatus);
/**
 *@brief 		启动跑表，开始计时
 *@details  由NDK_SysStartWatch()和NDK_SysStopWatch()配合使用。精度在1毫秒以内
 *@return
 *@li	 NDK_OK				操作成功
*/
extern int (*NDK_SysStartWatch)(void);
/**
 *@brief 		停止跑表并保存计数值
 *@details
 *@retval   punTime 跑表结束时的计数值
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(punTime为NULL)
*/
extern int (*NDK_SysStopWatch)(uint *punTime);

/**
 *@brief 		获取计数值
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
 *@brief    获取固件类型
 *@retval  	emFWinfo 	返回的固件类型,参考\ref EM_SYS_FWINFO "EM_SYS_FWINFO".
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(emFWinfo为NULL)
*/
extern int (*NDK_SysGetFirmwareInfo)(EM_SYS_FWINFO *emFWinfo);
/**
 *@brief 		获取POS当前时间单位为秒
 *@details	获取的时间以秒单位，从1970年1月1日0时0分0秒开始计算到现在经过了多少秒的时间。
 *@retval   ulTime 	返回所经过的秒
 *@return
 *@li	 NDK_OK				操作成功
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(ulTime为NULL)
*/
extern int (*NDK_SysTime)(ulong *ulTime);
/**
 *@brief   设置休眠自动唤醒的时间,最小设置时间为60秒,589X平台（SP60机型）定时唤醒的精度较低，误差在128秒左右。
 *@retval  unSec  单位:秒
 *@return
 *@li	 NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	     参数非法(unSec小于60)
 *@li	\ref NDK_ERR "NDK_ERR"	     操作失败
*/
extern int (*NDK_SysSetSuspendDuration)(uint unSec);

/**
 *@brief	获取k21端指令集版本号(新增NDK接口)
 *@retval   pszVer	版本号字符串,缓冲大小不低于16字节
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"		参数非法(pszVer为NULL)
*/
extern int (*NDk_SysGetK21Version)(char *version);

/**
*/
extern int (*NDK_SP_SysSetPosInfo)(EM_SYS_HWINFO emFlag, const char *psBuf);
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
extern int (*NDK_SP_SysGetPosInfo)(EM_SYS_HWINFO emFlag, unsigned int *punLen, char *psBuf);

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
extern int (*NDK_IncNum)(uchar * pszStrNum );

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


/** @addtogroup SslSockets模块
* @{
*/

typedef enum
{
	NDK_NOWAIT = 0,				       ///< 非阻塞
	NDK_SUSPEND = 0xFFFFFFFF,		 ///< 阻塞
}EM_BLOCK_OPT;

typedef struct st_socket_addr
{
	uint  unAddrType;	///< 地址类型
	char *psAddr;					      ///< IP 地址（ASCIIz）.
	ushort usPort;					      ///< IP 端口.
}ST_SOCKET_ADDR;

typedef enum
{
    HANDSHAKE_SSLv2,	  //!< 仅SSLv2
    HANDSHAKE_SSLv23, 	//!< SSLv3 SSLv2
    HANDSHAKE_SSLv3,	  //!< 仅SSLv3
    HANDSHAKE_TLSv1,	  //!< TLSv1
}EM_SSL_HANDSHAKE_OPT;

typedef enum
{
    SSL_AUTH_NONE,			///< 服务器模式:服务器不会给一个客户端证书请求客户机,那么客户端将不会发送一个证书。
												///< 客户机模式:如果不使用一个匿名的密码(默认情况下禁用),服务器就会发送一个证书,然后将被检查。
	SSL_AUTH_CLIENT,		  ///< 服务器模式:服务器发送一个客户端证书请求的客户机。返回的证书(如果有的话)是检查。
							          ///< 客户机模式:验证的服务器证书。
}EM_SSL_AUTH_OPT;

typedef enum
{
        SSL3_CIPHER_RSA_NULL_MD5 = 1,			///< 密码套件-RSA_NULL_MD5
        SSL3_CIPHER_RSA_NULL_SHA,				///< 密码套件-RSA_NULL_SHA
        SSL3_CIPHER_RSA_RC4_40_MD5,				///< 密码套件-RSA_RC4_40_MD5
        SSL3_CIPHER_RSA_RC4_128_MD5,			///< 密码套件-RSA_RC4_128_MD5
        SSL3_CIPHER_RSA_RC4_128_SHA,			///< 密码套件-RSA_RC4_128_SHA
        SSL3_CIPHER_RSA_RC2_40_MD5,				///< 密码套件-RSA_RC2_40_MD5
        SSL3_CIPHER_RSA_IDEA_128_SHA,			///< 密码套件-IDEA_128_SHA
        SSL3_CIPHER_RSA_DES_40_CBC_SHA,			///< 密码套件-DES_40_CBC_SHA
        SSL3_CIPHER_RSA_DES_64_CBC_SHA,			///< 密码套件-DES_64_CBC_SHA
        SSL3_CIPHER_RSA_DES_192_CBC3_SHA,		///< 密码套件-DES_192_CBC3_SHA
        SSL3_CIPHER_DH_RSA_DES_192_CBC3_SHA,	///< 密码套件-DH_DSS_DES_192_CBC3_SHA
        SSL3_CIPHER_DH_DSS_DES_40_CBC_SHA,		///< 密码套件-DH_DSS_DES_40_CBC_SHA
        SSL3_CIPHER_DH_DSS_DES_64_CBC_SHA,		///< 密码套件-DH_DSS_DES_64_CBC_SHA
        SSL3_CIPHER_DH_DSS_DES_192_CBC3_SHA,	///< 密码套件-DH_DSS_DES_192_CBC3_SHA
        SSL3_CIPHER_DH_RSA_DES_40_CBC_SHA,		///< 密码套件-DH_RSA_DES_40_CBC_SHA
        SSL3_CIPHER_DH_RSA_DES_64_CBC_SHA,		///< 密码套件-DH_RSA_DES_64_CBC_SHA
        SSL3_CIPHER_EDH_DSS_DES_40_CBC_SHA,		///< 密码套件-EDH_DSS_DES_40_CBC_SHA
        SSL3_CIPHER_EDH_DSS_DES_64_CBC_SHA,		///< 密码套件-EDH_DSS_DES_64_CBC_SHA
        SSL3_CIPHER_EDH_DSS_DES_192_CBC3_SHA,	///< 密码套件-EDH_DSS_DES_192_CBC3_SHA
        SSL3_CIPHER_EDH_RSA_DES_40_CBC_SHA,		///< 密码套件-EDH_RSA_DES_40_CBC_SHA
        SSL3_CIPHER_EDH_RSA_DES_64_CBC_SHA,		///< 密码套件-EDH_RSA_DES_64_CBC_SHA
        SSL3_CIPHER_EDH_RSA_DES_192_CBC3_SHA,	///< 密码套件-EDH_RSA_DES_192_CBC3_SHA
        SSL3_CIPHER_ADH_RC4_40_MD5,				///< 密码套件-ADH_RC4_40_MD5
        SSL3_CIPHER_ADH_RC4_128_MD5,			///< 密码套件-ADH_RC4_128_MD5
        SSL3_CIPHER_ADH_DES_40_CBC_SHA,			///< 密码套件-ADH_DES_40_CBC_SHA
        SSL3_CIPHER_FZA_DMS_NULL_SHA,			///< 密码套件-FZA_DMS_NULL_SHA
        SSL3_CIPHER_CK_FZA_DMS_FZA_SHA,			///< 密码套件-CK_FZA_DMS_FZA_SHA
        SSL3_CIPHER_CK_FZA_DMS_RC4_SHA,			///< 密码套件-CK_FZA_DMS_RC4_SHA
        SSL3_CIPHER_CK_ADH_DES_64_CBC_SHA,		///< 密码套件-CK_ADH_DES_64_CBC_SHA
        SSL3_CIPHER_CK_ADH_DES_192_CBC_SHA,		///< 密码套件-CK_ADH_DES_192_CBC_SHA
}EM_SSL_CIPHER_OPT;

typedef enum
{
    SSL_IS_DISCONNECTED,			/**<未连接*/
    SSL_CONNECTION_IN_PROGRESS,		/**<请求连接中*/
    SSL_IS_CONNECTED				/**<已连接*/
}EM_SSL_CONNECTION_STATE;

typedef enum
{
    SSL_FILE_DER,		/**<DER(ASN1) 文件格式*/
    SSL_FILE_PEM,		/**<PEM (BASE64) 文件格式*/
}EM_SSL_FILE_FORMAT;

typedef enum
{
	SSL_ADDR_IPV4,		/**<IP v4*/
    SSL_ADDR_IPV6,		/**<IP v6*/
}EM_ADDR_TYPE;

/**
 *@brief	加载本地证书
 *@param  pvHandle  SSL句柄
 *@param  psFileName  证书文件
 *@param  nFormat    证书文件格式(参考\ref EM_SSL_FILE_FORMAT "EM_SSL_FILE_FORMAT")
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_SSL_PARAM "NDK_ERR_SSL_PARAM"	无效参数(SSL未打开、psFileName证书文件为NULL)
 *@li	\ref NDK_ERR_SSL_ALREADCLOSE "NDK_ERR_SSL_ALREADCLOSE"	连接已关闭(SSL连接已关闭)
 *@li	\ref NDK_ERR_SSL_MODEUNSUPPORTED "NDK_ERR_SSL_MODEUNSUPPORTED"	模式不支持(format证书格式不支持)
 *@li	\ref NDK_ERR "NDK_ERR"	操作失败（加载证书失败）
*/
extern int NDK_LoadClientCertificate(NDK_HANDLE pvHandle, const char *psFileName, int nFormat);
/**
 *@brief	加载证书私钥
 *@param  pvHandle  SSL句柄
 *@param  psFileName  私钥文件
 *@param  nFormat    文件格式(参考\ref EM_SSL_FILE_FORMAT "EM_SSL_FILE_FORMAT")
 *@param  pszPassword  私钥口令（明文传NULL）
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_SSL_PARAM "NDK_ERR_SSL_PARAM"	无效参数(SSL未打开、psFileName证书文件为NULL)
 *@li	\ref NDK_ERR_SSL_ALREADCLOSE "NDK_ERR_SSL_ALREADCLOSE"	连接已关闭(SSL连接已关闭)
 *@li	\ref NDK_ERR_SSL_MODEUNSUPPORTED "NDK_ERR_SSL_MODEUNSUPPORTED"	模式不支持(format证书格式不支持)
 *@li	\ref NDK_ERR "NDK_ERR"	操作失败（加载证书失败）
*/
extern int NDK_LoadClientPrivateKey(NDK_HANDLE pvHandle, const char *psFileName, int nFormat,char *pszPassword);
/**
 *@brief	加载CA证书
 *@param  pvHandle  SSL句柄
 *@param  psFileName  CA证书文件
 *@param  nFormat    CA证书文件格式(参考\ref EM_SSL_FILE_FORMAT "EM_SSL_FILE_FORMAT")
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_SSL_PARAM "NDK_ERR_SSL_PARAM"	无效参数(SSL未打开、psFileName证书文件为NULL)
 *@li	\ref NDK_ERR_SSL_ALREADCLOSE "NDK_ERR_SSL_ALREADCLOSE"	连接已关闭(SSL连接已关闭)
 *@li	\ref NDK_ERR_SSL_MODEUNSUPPORTED "NDK_ERR_SSL_MODEUNSUPPORTED"	模式不支持(format证书格式不支持)
 *@li	\ref NDK_ERR "NDK_ERR"	操作失败（加载证书失败）
*/
extern int NDK_LoadServerCertificate(NDK_HANDLE pvHandle, const char *psFileName, int nFormat);

/**
 *@brief	创建一个SSL socket
 *@param	nType		握手协议类型(参考\ref EM_SSL_HANDSHAKE_OPT "EM_SSL_HANDSHAKE_OPT")
 *@param  	nAuthOpt 	认证模式(参考\ref EM_SSL_AUTH_OPT "EM_SSL_AUTH_OPT")
 *@param  	pnCipher   	所支持的算法(参考\ref EM_SSL_CIPHER_OPT "EM_SSL_CIPHER_OPT")
 *@return
 *@li	SSL句柄				操作成功
 *@li	NULL		操作失败
*/
NDK_HANDLE NDK_OpenSSLSocket(int nType,int nAuthOpt,int* pnCipher);
/**
 *@brief	关闭SSL socket
 *@param  	pvHandle  SSL句柄
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_SSL_PARAM "NDK_ERR_SSL_PARAM"	无效参数(SSL未打开)
 *@li	\ref NDK_ERR_SSL_ALREADCLOSE "NDK_ERR_SSL_ALREADCLOSE"	连接已关闭(SSL连接已关闭)
 *@li	\ref NDK_ERR "NDK_ERR"	操作失败（SSL连接关闭失败）
*/
extern int NDK_CloseSSLSocket(NDK_HANDLE pvHandle);
/**
 *@brief	断开SSL连接
 *@param  	pvHandle  SSL句柄
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_SSL_PARAM "NDK_ERR_SSL_PARAM"	无效参数(SSL未打开)
 *@li	\ref NDK_ERR_SSL_ALREADCLOSE "NDK_ERR_SSL_ALREADCLOSE"	连接已关闭(SSL连接已关闭)
 *@li	\ref NDK_ERR "NDK_ERR"	操作失败（断开SSL连接失败）
*/
extern int NDK_SSLDisconnect(NDK_HANDLE pvHandle);
/**
 *@brief	连接服务器
 *@param  	pvHandle  	SSL句柄
 *@param  	pstServer  	地址
 *@param  	nTimeOut  	超时时间（ms）
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_SSL_PARAM "NDK_ERR_SSL_PARAM"	无效参数(SSL未打开、pServer地址为NULL、nTimeOut超时参数非法)
 *@li	\ref NDK_ERR_SSL_ALREADCLOSE "NDK_ERR_SSL_ALREADCLOSE"	连接已关闭(SSL连接已关闭)
 *@li	\ref NDK_ERR_SSL_ALLOC "NDK_ERR_SSL_ALLOC"	无法分配(创建套接字失败)
 *@li	\ref NDK_ERR "NDK_ERR"	操作失败（绑定套接字失败等）
 *@li	\ref NDK_ERR_SSL_TIMEOUT "NDK_ERR_SSL_TIMEOUT"	连接超时
 *@li	\ref NDK_ERR_SSL_INVADDR "NDK_ERR_SSL_INVADDR"	无效地址（主机地址转网络地址失败）
*/
extern int NDK_SSLConnect(NDK_HANDLE pvHandle, ST_SOCKET_ADDR *pstServer, int nTimeOut);
/**
 *@brief	获取阻塞模式
 *@param  	pvHandle  	SSL句柄
 *@return
 *@li	\ref NDK_NOWAIT "NDK_NOWAIT"(非阻塞)		\ref NDK_SUSPEND "NDK_SUSPEND"(阻塞)		操作成功
 *@li	\ref NDK_ERR_SSL_PARAM "NDK_ERR_SSL_PARAM"	无效参数(SSL未打开)
 *@li	\ref NDK_ERR_SSL_ALLOC "NDK_ERR_SSL_ALLOC"	无法分配(获取套接字失败)
 *@li	\ref NDK_ERR_SSL_ALREADCLOSE "NDK_ERR_SSL_ALREADCLOSE"	连接已关闭(SSL连接已关闭)
 *@li	\ref NDK_ERR "NDK_ERR"	操作失败（断开SSL连接失败）
*/
extern int NDK_GetSSLBlockingMode(NDK_HANDLE pvHandle);
/**
 *@brief	设置阻塞模式
 *@param  	pvHandle  	SSL句柄
 *@param  	nMode    阻塞模式(参考\ref EM_BLOCK_OPT "EM_BLOCK_OPT")
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_SSL_PARAM "NDK_ERR_SSL_PARAM"	无效参数(SSL未打开)
 *@li	\ref NDK_ERR_SSL_ALLOC "NDK_ERR_SSL_ALLOC"	无法分配(获取套接字失败)
 *@li	\ref NDK_ERR_SSL_ALREADCLOSE "NDK_ERR_SSL_ALREADCLOSE"	连接已关闭(SSL连接已关闭)
 *@li	\ref NDK_ERR "NDK_ERR"	操作失败（调用fcntl()失败返回）
 *@li	\ref NDK_ERR_SSL_MODEUNSUPPORTED "NDK_ERR_SSL_MODEUNSUPPORTED"	模式不支持（nMode模式不支持）
*/
extern int NDK_SetSSLBlockingMode(NDK_HANDLE pvHandle,int nMode);
/**
 *@brief	发送数据
 *@param  	pvHandle  	SSL句柄
 *@param  	psBuffer 	要发送的数据
 *@param  	unBufferLen 预期发送大小
 *@retval 	punSendLen    实际发送大小
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_SSL_PARAM "NDK_ERR_SSL_PARAM"	无效参数(SSL未打开、psBuffer/punSendLen为NULL)
 *@li	\ref NDK_ERR_SSL_SEND "NDK_ERR_SSL_SEND"	发送错误(调用SSL_write（）失败返回)
 *@li	\ref NDK_ERR "NDK_ERR"	操作失败（SSL未连接成功）
*/
extern int NDK_SSLSend(NDK_HANDLE pvHandle, const char *psBuffer, uint unBufferLen, uint *punSendLen);
/**
 *@brief	接收数据
 *@param  	pvHandle  	SSL句柄
 *@param  	pvBuffer 	要发送的数据
 *@param  	unBufferLen 预期接收大小
 *@retval 	punRecvLen  实际接收大小
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_SSL_PARAM "NDK_ERR_SSL_PARAM"	无效参数(SSL未打开、pvBuffer/punRecvLen为NULL)
 *@li	\ref NDK_ERR_SSL_RECV "NDK_ERR_SSL_RECV"	接收错误(调用SSL_read（）失败返回)
 *@li	\ref NDK_ERR "NDK_ERR"	操作失败（SSL未连接成功）
*/
extern int NDK_SSLReceive(NDK_HANDLE pvHandle, void *pvBuffer, const uint unBufferLen, uint *punRecvLen);
/**
 *@brief	绑定
 *@param  	pvHandle  	SSL句柄
 *@param  	pstAddr 	地址
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_SSL_PARAM "NDK_ERR_SSL_PARAM"	无效参数(SSL未打开、pstAddr为NULL)
 *@li	\ref NDK_ERR_SSL_ALREADCLOSE "NDK_ERR_SSL_ALREADCLOSE"	连接已关闭(SSL连接已关闭)
 *@li	\ref NDK_ERR_SSL_ALLOC "NDK_ERR_SSL_ALLOC"	无法分配（获取套接字失败）
 *@li	\ref NDK_ERR_SSL_INVADDR "NDK_ERR_SSL_INVADDR"	无效地址（bind失败返回）
 *@li	\ref NDK_ERR "NDK_ERR"	操作失败（设置地址重用失败）
*/
extern int NDK_SSLBind(NDK_HANDLE pvHandle, ST_SOCKET_ADDR *pstAddr);
/**
 *@brief	获取连接状态
 *@param  	pvHandle  	SSL句柄
 *@retval  	pnState  	连接状态(参考\ref EM_SSL_CONNECTION_STATE "EM_SSL_CONNECTION_STATE")
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_SSL_PARAM "NDK_ERR_SSL_PARAM"	无效参数(SSL未打开、pnState为NULL)
 *@li	\ref NDK_ERR_SSL_ALREADCLOSE "NDK_ERR_SSL_ALREADCLOSE"	连接已关闭(SSL连接已关闭)
 *@li	\ref NDK_ERR "NDK_ERR"	操作失败（设置地址重用失败）
*/
extern int NDK_GetSSLConnectStatus(NDK_HANDLE pvHandle, int *pnState);
/**
 *@brief	检查数据有效性
 *@param  	pvHandle  	SSL句柄
 *@param  	unTimeOut  	超时时间（10s）
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_SSL_PARAM "NDK_ERR_SSL_PARAM"	无效参数(SSL未打开、timeout<0)
 *@li	\ref NDK_ERR_SSL_ALREADCLOSE "NDK_ERR_SSL_ALREADCLOSE"	连接已关闭(SSL连接已关闭)
 *@li	\ref NDK_ERR_SSL_CONNECT "NDK_ERR_SSL_CONNECT"	没有连接（SSL连接失败）
 *@li	\ref NDK_ERR_SSL_TIMEOUT "NDK_ERR_SSL_TIMEOUT"	超时错误
*/
extern int NDK_SSLDataAvailable(NDK_HANDLE pvHandle, uint unTimeOut);


/** @} */ // SslSocket模块结束

/** @addtogroup Timer模块
* @{
*/

#define ONEMILLISECOND 1     					/**<一毫秒*/
#define ONESECOND ( 100 * ONEMILLISECOND )  		/**<一秒*/
#define ONEMINUTE ( 60 * ONESECOND )	/**<一分钟*/
typedef void        ( *pvFUNC )( void );
typedef const pvFUNC       pcFUNC;

/**
 *@brief	等待一段时间
 *@param  unWaitDuration  等待的时间(单位为10ms)
 *@return
 *@li	
 *@li	
*/
void NDK_Wait(uint  unWaitDuration);
/**
 *@brief	启用一个定时器，当没有设置标志过期
 *@param  	unWaitDuration   等待的时间(单位为10ms)
 *@param  	punFlag          标志是否过期
 *@return
 *@li	Timer句柄				  操作成功
 *@li	NULL 	            操作失败
*/
NDK_HANDLE NDK_WaitAndFlag(uint unWaitDuration,uint *punFlag);
/**
 *@brief	取消一个定时器在NDK_WaitAndFlag（）发起之前
 *@param  	pvHandle   Timer句柄
 *@return
 *@li	
 *@li	
*/
void NDK_CancelTimer(NDK_HANDLE  pvHandle);

/**
 *@brief	注册一个程序在指定的时间间隔
 *@param  	unTimeInterval   允许注册等待时间
 *@param  	pRoutine    程序的指针
 *@return
 *@li	NDK_OK				  操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(pRoutine为NULL、unTimeInterval<=0)
 *@li	\ref NDK_ERR "NDK_ERR"	操作失败
*/
extern int NDK_RegisterTimerRoutine (uint unTimeInterval, pvFUNC pRoutine);


/** @} */ // Timer模块结束

/** @addtogroup Thread管理模块
* @{
*/

typedef enum ndk_thread_state
{
	NDK_THREAD_STOPPED,		///< 线程停止
	NDK_THREAD_RUNNING,		///< 线程运行中
	NDK_THREAD_SUSPEND,		///< 线程暂停
} EM_THREAD_STATE;
/**
 *@brief	创建一个线程
 *@param  ppvHandle 线程句柄
 *@param  psName   线程名称
 *@param  unStackSize  线程栈大小
 *@param  pvEntryPoint 线程入口点
 *@return
 *@li	NDK_OK				        操作成功
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC"	内存空间不足
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(ppvHandle为NULL、pvEntryPoint为NULL)
 *@li	\ref NDK_ERR "NDK_ERR"	操作失败
*/
extern int NDK_CreateThread( NDK_HANDLE *ppvHandle, const char* psName, uint unStackSize,
								void (*pvEntryPoint)( void ) );
/**
 *@brief	唤醒线程
 *@param  	pvHandle    线程句柄
 *@return
 *@li	NDK_OK				        操作成功
 *@li	\ref NDK_ERR_THREAD_CMDUNSUPPORTED "NDK_ERR_THREAD_CMDUNSUPPORTED"	命令不支持
*/
extern int NDK_ResumeThread( NDK_HANDLE pvHandle );
/**
 *@brief	休眠线程
 *@param  	pvHandle    线程句柄
 *@return
 *@li	NDK_OK				        操作成功
 *@li	\ref NDK_ERR_THREAD_CMDUNSUPPORTED "NDK_ERR_THREAD_CMDUNSUPPORTED"	命令不支持
*/
extern int NDK_SuspendThread( NDK_HANDLE pvHandle );
/**
 *@brief	终止线程
 *@param  	pvHandle    线程句柄
 *@return
 *@li	NDK_OK				        操作成功
 *@li	\ref NDK_ERR "NDK_ERR"	操作失败(pvHandle为NULL)
*/
extern int NDK_TerminateThread( NDK_HANDLE pvHandle );
/**
 *@brief	允许其他线程运行
 *@return
 *@li	NDK_OK				  操作成功
*/
extern int NDK_Relinquish( void );
/**
 *@brief	获取当前线程的状态
 *@param  	pvHandle    线程句柄
 *@retval 	pemState    线程状态
 *@return
 *@li	NDK_OK				        操作成功
 *@li	\ref NDK_ERR_THREAD_CMDUNSUPPORTED "NDK_ERR_THREAD_CMDUNSUPPORTED"	命令不支持
*/
extern int NDK_GetThreadState( NDK_HANDLE pvHandle, EM_THREAD_STATE *pemState );
/**
 *@brief	暂停线程
 *@param  	nWaitMs   要暂停的时间
 *@return
 *@li	NDK_OK				        操作成功
 *@li	\ref NDK_ERR "NDK_ERR"	操作失败
*/
extern int NDK_ThreadSleep( int nWaitMs );
/**
 *@brief	创建一个信号量
 *@param  	ppvHandle   信号量句柄
 *@return
 *@li	NDK_OK				        操作成功
 *@li	\ref NDK_ERR_THREAD_ALLOC "NDK_ERR_THREAD_ALLOC"	无效分配
 *@li	\ref NDK_ERR_THREAD_PARAM "NDK_ERR_THREAD_PARAM"	无效参数（ppvHandle为NULL）
 *@li	\ref NDK_ERR "NDK_ERR"	操作失败
*/
extern int NDK_CreateSemaphore( NDK_HANDLE *ppvHandle );
/**
 *@brief	获取信号量
 *@param  	pvHandle   信号量句柄
 *@return
 *@li	NDK_OK				  操作成功
 *@li	\ref NDK_ERR_THREAD_PARAM "NDK_ERR_THREAD_PARAM"	无效参数（ppvHandle为NULL）
*/
extern int NDK_LockSemaphore( NDK_HANDLE pvHandle );
/**
 *@brief	释放信号量
 *@param  	pvHandle   信号量句柄
 *@return
 *@li	NDK_OK				  操作成功
 *@li	\ref NDK_ERR_THREAD_PARAM "NDK_ERR_THREAD_PARAM"	无效参数（ppvHandle为NULL）
*/
extern int NDK_ReleaseSemaphore( NDK_HANDLE pvHandle );
/**
 *@brief	摧毁一个信号量
 *@param  	pvHandle   信号量句柄
 *@return
 *@li	NDK_OK				  操作成功
 *@li	\ref NDK_ERR_THREAD_PARAM "NDK_ERR_THREAD_PARAM"	无效参数（ppvHandle为NULL）
*/
extern int NDK_DestroySemaphore( NDK_HANDLE pvHandle );


/** @} */ // Thread管理模块结束

/** @addtogroup U盘跟SD卡
* @{
*/

typedef enum{
	UDISK = 0,    /**<选择U盘*/
	SDDISK =1     /**<选择SD卡*/
}EM_DISKTYPE;

typedef enum
{
	DISKMOUNTSUCC=1,         /**<U盘或Sd卡可用*/
	DISKNOTOPEN=2,           /**<U盘或Sd卡未打开*/
	DISKDRIVERLOADFAIL=3,    /**<U盘或Sd卡驱动加载失败*/
	DISKMOUNTING=4,          /**<U盘或Sd卡正在加载中*/
	DISKNOEXIT=5,	         /**<未检测到SD卡*/
	DISKTIMEOUT=6            /**<检测SD卡超时*/
}EM_DISKSTATE;

typedef struct{
	uint unFreeSpace;	/**<可以用空间大小*/
	uint unTotalSpace;	/**<总空间大小*/
}ST_DISK_INFO;

/**
 *@brief	打开U盘或SD卡(注意：在U盘使用过程中，如果pos机上有大口的插槽就选大口)
 *@param	emType	类型（UDISK：表示U盘,SDDISK：表示SD卡.\ref EM_DISKTYPE "EM_DISKTYPE")
 *@param	nTimeOut 设置超时时间(单位秒，0表示阻塞，非0表示在规定的超时时间内没有返回DISKMOUNTSUCC该状态，此时获取到的状态应为DISKTIMEOUT)
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_USDDISK_PARAM "NDK_ERR_USDDISK_PARAM"	无效参数（nTimeOut<0）
 *@li	\ref NDK_ERR_USDDISK_NONSUPPORTTYPE "NDK_ERR_USDDISK_NONSUPPORTTYPE"	不支持类型（emType未支持类型）
 *@li	\ref NDK_ERR_USDDISK_DRIVELOADFAIL "NDK_ERR_USDDISK_DRIVELOADFAIL"	驱动加载失败
 *@li	\ref NDK_ERR_USDDISK_IOCFAIL "NDK_ERR_USDDISK_IOCFAIL"	驱动调用错误
*/
extern int NDK_DiskOpen(EM_DISKTYPE emType,int nTimeOut);
/**
 *@brief	获取U盘或SD卡信息
 *@param	pszDiskDir	U盘或SD卡跟目录
 *@param	pstInfo   磁盘信息结构（\ref ST_DISK_INFO "ST_DISK_INFO"
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_USDDISK_PARAM "NDK_ERR_USDDISK_PARAM"	无效参数（pszDiskDir/pstInfo为NULL）
 *@li	\ref NDK_ERR "NDK_ERR"	操作失败
*/
extern int NDK_DiskGetInfo(char *pszDiskDir,ST_DISK_INFO  *pstInfo);
/**
 *@brief	获取U盘或SD卡状态
 *@param	emType	类型（UDISK：表示U盘,SDDISK：表示SD卡.\ref EM_DISKTYPE "EM_DISKTYPE")
 *@retval	pnDiskState   状态（参考\ref EM_DISKSTATE "EM_DISKSTATE"）
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_USDDISK_PARAM "NDK_ERR_USDDISK_PARAM"	无效参数（pnDiskState为NULL）
 *@li	\ref NDK_ERR_USDDISK_NONSUPPORTTYPE "NDK_ERR_USDDISK_NONSUPPORTTYPE"	不支持类型(emType类型未支持)
*/
extern int NDK_DiskGetState(EM_DISKTYPE emType,int *pnDiskState);
/**
 *@brief	关闭U盘或SD卡
 *@param	emType	类型（UDISK：表示U盘,SDDISK：表示SD卡.\ref EM_DISKTYPE "EM_DISKTYPE")
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_USDDISK_PARAM "NDK_ERR_USDDISK_PARAM"	无效参数（pnDiskState为NULL）
 *@li	\ref NDK_ERR_USDDISK_UNMOUNTFAIL "NDK_ERR_USDDISK_UNMOUNTFAIL"	挂载失败
 *@li	\ref NDK_ERR_USDDISK_UNLOADDRIFAIL "NDK_ERR_USDDISK_UNLOADDRIFAIL"	卸载驱动失败
*/
extern int NDK_DiskClose(EM_DISKTYPE emType);
/**
 *@brief	获取U盘或SD卡根目录
 *@param	emType	类型（UDISK：表示U盘,SDDISK：表示SD卡.\ref EM_DISKTYPE "EM_DISKTYPE")
 *@retval	pszRdir  根目录
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_USDDISK_PARAM "NDK_ERR_USDDISK_PARAM"	无效参数（pszRdir为NULL）
 *@li	\ref NDK_ERR "NDK_ERR"	操作失败
*@li	\ref NDK_ERR_USDDISK_NONSUPPORTTYPE "NDK_ERR_USDDISK_NONSUPPORTTYPE"	不支持类型(emType类型未支持)
*/
extern int NDK_DiskGetRootDirName(EM_DISKTYPE emType,char **pszRdir);

/** @} */ // U盘跟SD卡模块结束

/** @addtogroup WIFI
* @{
*/

typedef enum
{
    WIFI_NET_SEC_NONE,          /**<路由器不使用安全加密模式*/
    WIFI_NET_SEC_WEP_OPEN,      /**<路由器使用开放密钥的WEP模式*/
    WIFI_NET_SEC_WEP_SHARED,    /**<路由器使用共享密钥的WEP模式*/
    WIFI_NET_SEC_WPA,           /**<路由器使用WPA模式*/
    WIFI_NET_SEC_WPA2           /**<路由器使用WPA2模式*/
} EM_WIFI_NET_SEC;

typedef enum {
    WIFI_KEY_TYPE_NOKEY,        /**<路由器不使用密码*/
    WIFI_KEY_TYPE_HEX,          /**<路由器使用十六进制的密码格式*/
	WIFI_KEY_TYPE_ASCII         /**<路由器使用ASCII的密码格式*/
}EM_WIFI_KEY_TYPE;

typedef struct
{
    uchar ucIfDHCP;             /**<是否使用DHCP*/
    EM_WIFI_KEY_TYPE emKeyType; /**<路由器的密码格式*/
    EM_WIFI_NET_SEC emSecMode;  /**<路由器的安全加密模式*/
    char *pszKey;               /**<路由器的密码*/
    char *psEthIp;              /**<终端IP地址*/
    char *psEthNetmask;         /**<网络掩码地址*/
    char *psEthGateway;         /**<网络网关地址*/
    char *psEthDnsPrimary;      /**<网络首DNS地址*/
    char *psEthDnsSecondary;    /**<网络次DNS地址*/
} ST_WIFI_PARAM;

typedef enum
{
    WIFI_WPA_CONSTATE_LINKING,      /**<正在连接指定的AP*/
    WIFI_WPA_CONSTATE_LINKED,       /**<连接已获得AP回应*/
    WIFI_WPA_CONSTATE_AUTHENTICATED,/**<连接已获得AP验证*/
    WIFI_WPA_CONSTATE_CONTEXT       /**<连接指定的AP已成功*/
} EM_WPA_CONSTATE;

#define WIFI_IW_ESSID_MAX_SIZE	128     /**<路由器名称允许的最大长度*/
#define WIFI_IW_ENCODING_TOKEN_MAX	128 /**<路由器密码允许的最大长度*/
typedef struct {
	char sEssid[WIFI_IW_ESSID_MAX_SIZE + 1];    /**<路由器名称*/
	char sKeyModeStr[128];                      /**<路由器的安全加密模式*/
	int nFrequency;                             /**<路由器的频率*/
	char sKey[WIFI_IW_ENCODING_TOKEN_MAX + 1];  /**<路由器的密码*/
	char sSignal[16];                           /**<路由器的信号值*/
}ST_WIFI_APINFO;

/**
 *@brief	模块初始化
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_WIFI_DEVICE_FAULT "NDK_ERR_WIFI_DEVICE_FAULT"	WIFI-设备状态出错
 *@li	\ref NDK_ERR_WIFI_CMD_UNSUPPORTED "NDK_ERR_WIFI_CMD_UNSUPPORTED"	WIFI-不支持的命令
 *@li	\ref NDK_ERR_WIFI_SEARCH_FAULT "NDK_ERR_WIFI_SEARCH_FAULT"	WIFI-扫描状态出错
 *@li	\ref NDK_ERR_WIFI_DEVICE_UNAVAILABLE "NDK_ERR_WIFI_DEVICE_UNAVAILABLE"	WIFI-设备不可用
*/
extern int NDK_WiFiInit(void);
/**
 *@brief	获取扫描到的AP的SSID信息
 *@retval	ppszESSIDlist	保存扫描到的AP的SSID信息的二维数组
 *@retval	pnNumList   保存扫描到的AP的个数
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_WIFI_INVDATA "NDK_ERR_WIFI_INVDATA"	WIFI-无效参数（ppszESSIDlist、pnNumList为NULL）
 *@li	\ref NDK_ERR_WIFI_DEVICE_FAULT "NDK_ERR_WIFI_DEVICE_FAULT"	WIFI-设备状态出错
*/
extern int NDK_WiFiGetNetList(char **ppszESSIDlist, int *pnNumList);
/**
 *@brief	获取指定AP的信号值
 *@param	pszNetName	AP的SSID
 *@retval	pnSignal   保存AP的信号值
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_WIFI_INVDATA "NDK_ERR_WIFI_INVDATA"	WIFI-无效参数（pszNetName、pnSignal为NULL）
 *@li	\ref NDK_ERR_WIFI_DEVICE_NOTOPEN "NDK_ERR_WIFI_DEVICE_NOTOPEN"	WIFI-没有扫描到AP
 *@li	\ref NDK_ERR_WIFI_DEVICE_TIMEOUT "NDK_ERR_WIFI_DEVICE_TIMEOUT"	WIFI-设备超时
 *@li	\ref NDK_ERR_WIFI_DEVICE_FAULT "NDK_ERR_WIFI_DEVICE_FAULT"	WIFI-设备状态出错
*/
extern int NDK_WiFiSignalCover(const char *pszNetName, int *pnSignal);
/**
 *@brief	获取指定AP的安全模式
 *@param	pszESSIDName	AP的SSID
 *@retval	pemSec	保存AP的安全模式
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_WIFI_INVDATA "NDK_ERR_WIFI_INVDATA"	WIFI-无效参数（pszESSIDName、pemSec为NULL）
 *@li	\ref NDK_ERR_WIFI_DEVICE_NOTOPEN "NDK_ERR_WIFI_DEVICE_NOTOPEN"	WIFI-没有扫描到AP
 *@li	\ref NDK_ERR_WIFI_DEVICE_TIMEOUT "NDK_ERR_WIFI_DEVICE_TIMEOUT"	WIFI-设备超时
*/
extern int NDK_GetWiFiSec(const char *pszESSIDName, EM_WIFI_NET_SEC *pemSec);
/**
 *@brief	连接到指定的AP
 *@param	pszESSIDName	AP的SSID
 *@param	pstParam	连接参数
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_WIFI_INVDATA "NDK_ERR_WIFI_INVDATA"	WIFI-无效参数（pszESSIDName、pstParam为NULL）
 *@li	\ref NDK_ERR_WIFI_DEVICE_BUSY "NDK_ERR_WIFI_DEVICE_BUSY"	WIFI-设备忙
 *@li	\ref NDK_ERR_WIFI_DEVICE_UNAVAILABLE "NDK_ERR_WIFI_DEVICE_UNAVAILABLE"	WIFI-设备不可用
 *@li	\ref NDK_ERR_WIFI_UNKNOWN_ERROR "NDK_ERR_WIFI_UNKNOWN_ERROR"	WIFI-未知错误
*/
extern int NDK_WiFiConnect(const char *pszESSIDName, const ST_WIFI_PARAM *pstParam);
/**
 *@brief	获取连接状态信息
 *@retval	pemState	连接状态
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_WIFI_INVDATA "NDK_ERR_WIFI_INVDATA"	WIFI-无效参数（pemState为NULL）
 *@li	\ref NDK_ERR_WIFI_PROCESS_INBADSTATE "NDK_ERR_WIFI_PROCESS_INBADSTATE"	WIFI-无法连接到AP
*/
extern int NDK_WiFiConnectState(EM_WPA_CONSTATE *pemState);
/**
 *@brief	判断是否已连上AP
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_WIFI_DEVICE_UNAVAILABLE "NDK_ERR_WIFI_DEVICE_UNAVAILABLE"	WIFI-设备不可用
*/
extern int NDK_WiFiIsConnected(void);
/**
 *@brief	卸载WIFI模块
 *@return
 *@li	NDK_OK				操作成功
*/
extern int NDK_WiFiShutdown(void);
/**
 *@brief	获取扫描到的AP的全部信息
 *@retval	pstList	保存扫描到的AP信息的结构体数组
 *@param	nMaxNum	希望获取到的最大AP个数
 *@retval	pnNumList   保存扫描到的AP的个数
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_WIFI_INVDATA "NDK_ERR_WIFI_INVDATA"	参数非法(pstList/pnNumList为NULL、nMaxNum非法)
 *@li	\ref NDK_ERR_WIFI_DEVICE_FAULT "NDK_ERR_WIFI_DEVICE_FAULT"	WIFI-设备状态出错
*/
extern int NDK_WiFiGetNetInfo(ST_WIFI_APINFO *pstList, unsigned int unMaxNum, int *pnNumList);
/**
 *@brief	获取WIFI的MAC地址
 *@retval	pszMac	保存MAC地址
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_WIFI_INVDATA "NDK_ERR_WIFI_INVDATA"	参数非法(pszMac为NULL)
 *@li	\ref NDK_ERR "NDK_ERR"	操作失败
*/
extern int NDK_WiFiGetMac(char *pszMac);

/** @} */ // WIFI模块结束
/** @addtogroup 扫描
* @{
*/
typedef enum{
        SCAN_SETTYPE_FLOODLIGHT =0,                 /**<照明灯设置*/
        SCAN_SETTYPE_FOCUSLIGHT = 1,                /**<对焦灯设置*/
        SCAN_SETTYPE_SENSITIVITY = 2,               /**<灵敏度设置*/
        SCAN_SETTYPE_FACTORYDEFAULT = 3        /**<出厂默认设置*/
}EM_SCAN_SETTYPE;

/**
 *@brief	初始化扫描头器件硬件，包括管脚电平初始化，出厂默认设置。
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"	设备文件打开失败(扫描设备文件打开失败)
 *@li	\ref NDK_ERR "NDK_ERR"	操作失败(获取扫描头类型失败)
*/
extern int NDK_ScanInit(void);
/**
 *@brief	扫描设置(根据\ref EM_SCAN_SETTYPE "EM_SCAN_SETTYPE"选择要设置的项，unSetValue传要设置的
 值。\n照明灯设置的值为0--闪烁(读码时闪烁发光，其他工作状态时熄灭)、1--无照明(在任何情况下照明灯都熄灭)、2--读码常量(读码时照明灯持续发光，其他工作状态时熄灭)\n
            对焦灯设置的值为0--闪烁(读码时闪烁发光，其他工作状态时熄灭)、1--无对焦(在任何情况下对焦灯都熄灭)、2--感应(将对焦灯作为感应灯，读码状态下熄灭，非读码状态下开启)\n)
 *@param        emScanSet	设置类型
 *@param        unSetValue	设置值(照明与对焦设置参考函数描述，灵敏度设置值为数值范围1~20，该数值越低，灵敏度
越高.出厂默认设置(照明对焦为闪烁、灵敏度为11))
 *@return
 *@li	NDK_OK					操作成功
 *@li	\ref NDK_ERR "NDK_ERR"       			设置失败（扫描设置没有成功）
 *@li 	\ref NDK_ERR_PARA "NDK_ERR_PARA"    		参数错误（扫描设置的值无效）
 *@li 	\ref NDK_ERR_NOT_SUPPORT "NDK_ERR_NOT_SUPPORT"		未支持（有的扫描头不支持某个设置，例如EM1300就不支持对照明对焦灯的设置）
*/
extern int NDK_ScanSet(EM_SCAN_SETTYPE emScanSet,uint unSetValue);
/**
 *@brief	扫描（该函数不支持取消键退出，退出可以通过设置超时时间参数，在超时时间结束未扫描到数据退出。）
 *@param        nTimeOut       超时设置(单位为秒)
 *@retval	    pszValue       扫描数据缓冲区
 *@retval       pnLen          扫描到的数据长度
 *@return
 *@li	NDK_OK					操作成功
 *@li	\ref NDK_ERR_TIMEOUT "NDK_ERR_TIMEOUT"      超时错误（扫描超时）
 *@li 	\ref NDK_ERR_PARA "NDK_ERR_PARA"    		参数错误（pszValue、pnLen为NULL）
 *@li 	\ref NDK_ERR_OVERFLOW "NDK_ERR_OVERFLOW"    缓冲溢出
*/
extern int NDK_ScanDoScan(int nTimeOut,char *pszValue,int *pnLen);

/** @} */ // 扫描模块结束

/** @addtogroup 音频
* @{
*/

typedef enum{
        VOICE_CTRL_START = 0,   /**<重新播放*/
        VOICE_CTRL_RESUME = 1,  /**<恢复播放*/
        VOICE_CTRL_PAUSE = 2,   /**<暂停播放*/
        VOICE_CTRL_STOP = 3,    /**<停止播放*/
        VOICE_CTRL_MUTE = 4,    /**<打开静音*/
        VOICE_CTRL_NMUTE = 5,   /**<关闭静音*/
        VOICE_CTRL_VOLUME = 6,  /**<音量控制*/
}EM_VOICE_CTRL;
/**
 *@brief	加载音频文件
 *@param        unVoiceId	音频ID
 *@param        pszFile	音频文件路径
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"	设备文件打开失败
*@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(pszFile为NULL,音频文件不存在)
*@li	\ref NDK_ERR "NDK_ERR"       		操作失败(加载音频文件失败，可能音频文件格式不对等)
*/

extern int NDK_VoiceLoadFile(uint unVoiceId,char *pszFile);
/**
 *@brief	播放控制。（播放前必须调用\ref NDK_VoiceLoadFile "NDK_VoiceLoadFile（）"将相应的音频文件解码，音频解码到链表中，通过id播放控制不同的音频文件。）
 *@param        unVoiceId	音频ID
 *@param        emCtrlId	控制选项
 *@param        unValue	音量 （音量值在0-4范围）
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"	设备文件打开失败
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	参数非法(emCtrlId非法、音量非法)
 *@li	\ref NDK_ERR "NDK_ERR"       		操作失败(加载音频文件失败，可能该ID对应的音频文件未调用NDK_VoiceLoadFile进行加载。)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL"	驱动接口调用错误(音频驱动接口调用失败返回)
*/
 extern int (*NDK_VoiceCtrl)(uint unVoiceId,EM_VOICE_CTRL emCtrlId,uint unValue);

 extern int (*NDK_RpcTransRW)(unsigned char* DataIn,int LenIn,unsigned char *DataOut,int *LenOut,int maxlen,int utimeout);

 extern int (*NDK_initSdtp)();
 
 //extern int  NDK_InitCom ( int (*send)(unsigned char * data, int len),int (*recv)(unsigned char * buf, int buflen, int timeout), int  frameFlag, char * info );

/** @} */ // 音频模块结束
extern int NDK_BTStatus(int * status);
extern int NDK_BTReset(void);
extern int NDK_BTGetLocalName(char * name);
extern int NDK_BTGetLocalMAC(char * mac);
extern int NDK_BTGetPIN(char * pinstr);
extern int NDK_BTEnterCommand(void);
extern int NDK_BTExitCommand(void);
extern int NDK_BTSetLocalName(const char * name);
extern int NDK_BTSetPIN(const char * pinstr);
extern int NDK_BTSetChannel(const char channel);
extern int NDK_BTGetChannel(char * channel);
extern int NDK_BTSetPINCode(const char pincode);
extern int NDK_BTDisconnect(void);
extern int (*NDK_SysSetPosInfo)(EM_SYS_HWINFO emFlag, const char *psBuf);
extern int (*NDK_PlayVoice)(int id);
extern int (*NDK_SysEnterBoot)(void);
extern int (*NDK_SysPeerOper)(EM_SYS_PEEROPER oper);
extern int (*NDK_KbScanCode)(int *code);
extern int (*NDK_RfidTypeARats)(uchar cid,int *pnDatalen, uchar *psDatabuf);
extern int (*NDK_DeRegisterTimer)(void);
extern int (*NDK_SysKeyVolSet)(uint sel);

typedef enum{
	TRANSACTION,    //交易提示音
	KEYPAD,         //按键音
	BEEP_TYPE_MAX,
}BEEP_TYPE;

/**
 *@brief        根据音量类型设置beep的音量
 *@details
 *@param       type             交易提示音/按键音
 *@param        unVolumn    所要设置的音量的参数，参数范围为0~5，不设置底层默认为5
 *@return
 *@li    NDK_OK             操作成功
 *@li    NDK_ERR_PARA       参数错误(unVolumn非法)
 *@li    NDK_ERR_OPEN_DEV   打开设备文件失败
*/
extern int (*NDK_SysSetBeepVol_Extern)(BEEP_TYPE type, uint unVolumn);
typedef enum {
	LED_TYPE_MAG,    //磁卡灯
	LED_TYPE_MAX,
}EM_LED_FUNC_TYPE;
extern int (*NDK_LedFuncModeSet)(EM_LED_FUNC_TYPE emType,uint16_t interval_ms);


int NDK_TSKbdGetXY(uint unTime, uint *x, uint *y);
int NDK_TSKbd_Ctrl(uint ctrl);

typedef enum {
	SYS_EVENT_NONE = 0,    			   
	SYS_EVENT_MAGCARD= 0x00000004,	   
	SYS_EVENT_ICCARD= 0x00000008,      
	SYS_EVENT_RFID= 0x00000010,		  
	SYS_EVENT_PIN= 0x00000020,		   
	SYS_EVENT_PRNTER= 0x00000040,	   
	SYS_EVENT_KEYPAD=0x00000080,	  
	SYS_EVENT_MAX = 0x00000100,
} EM_SYS_EVENT;

typedef enum {
	CANCEL_NONE = 0,    			   
	CANCEL_MAGCARD= 0x00000001,	   
	CANCEL_ICCARD= 0x00000002,      
	CANCEL_RFID= 0x0000004,		  
	CANCEL_PIN= 0x00000008,		   
	CANCEL_PRNTER= 0x00000010,	   
} CANCEL_EVENT;

extern int (*NDK_SYS_ResumeEvent)(EM_SYS_EVENT eventNum);
extern int (*Ndk_beginTransactions)(int iTimeoutSec);
extern int (*Ndk_endTransactions)();
extern int (*Ndk_getStatus)();
extern int  (*NDK_SYS_RegisterEvent)(EM_SYS_EVENT eventNum, int timeOutMs, int (* notifyEvent )( EM_SYS_EVENT eventNum, int msgLen, char * msg));
extern int  (*NDK_SYS_UnRegisterEvent)(EM_SYS_EVENT eventNum);
extern void *timeout(int *time);
//self define
//extern int NDK_CardCheck(int *uKey, int *uIcState, uchar *uSwiped, uchar *uRfidState);
extern int (*NDK_SysOpenDebug)(int ndklev,int sdtplev);

extern int (*NDK_SysGetCapability)(int nSizeOfCap, char* szCaps);

extern int Ndk_Dlload();
extern void NDK_UnRegisterEvent(int events);

typedef struct {
	char *name;
	char *code;
	char *desc;
}StNdkErrInfo;


enum MCD_ERRORCODE {
    MAGCARD_TK1_LRC_ERR=0xC01,										                    // 一道LRC校验失败
    MAGCARD_TK2_LRC_ERR=0xC02,										                    // 二道LRC校验失败
    MAGCARD_TK3_LRC_ERR=0xC08,										                    // 三道LRC校验失败
    MAGCARD_TK12_LRC_ERR=MAGCARD_TK1_LRC_ERR|MAGCARD_TK2_LRC_ERR,	                    // 一、二道LRC校验失败
    MAGCARD_TK23_LRC_ERR=MAGCARD_TK2_LRC_ERR|MAGCARD_TK3_LRC_ERR,	                    // 二、三道LRC校验失败
    MAGCARD_TK13_LRC_ERR=MAGCARD_TK1_LRC_ERR|MAGCARD_TK3_LRC_ERR,	                    // 一、三道LRC校验失败
    MAGCARD_TK123_LRC_ERR =MAGCARD_TK1_LRC_ERR|MAGCARD_TK2_LRC_ERR|MAGCARD_TK3_LRC_ERR	// 一、二、三道LRC校验失败
};

#endif
/* End of this file */

