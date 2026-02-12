#ifndef	NDK_CRYPTO_H
#define	NDK_CRYPTO_H

#define AD_MAX_SIZE 128
#define DATAIN_MAX_LEN 2048
#define MAX_KEYID 255
#define MIN_KEYID 1
#define IV_MAX_SIZE 16

typedef enum {
	NAPI_OK,							/**<操作成功*/
	NAPI_ERR=-1,						/**<操作失败*/
	NAPI_ERR_INIT_CONFIG = -2,	 	/**<初始化配置失败*/
	NAPI_ERR_CREAT_WIDGET = -3,		/**<创建界面错误*/
	NAPI_ERR_OPEN_DEV = -4,			/**<打开设备文件错误*/
	NAPI_ERR_IOCTL = -5,				/**<驱动调用错误*/
	NAPI_ERR_PARA = -6,				/**<参数非法*/
	NAPI_ERR_PATH = -7,				/**<文件路径非法*/
	NAPI_ERR_DECODE_IMAGE = -8,		/**<图像解码失败*/
	NAPI_ERR_MACLLOC = -9,			/**<内存空间不足*/
	NAPI_ERR_TIMEOUT = -10,			/**<超时错误*/
	NAPI_ERR_QUIT = -11,				/**<按取消退出*/
	NAPI_ERR_WRITE = -12, 			/**<写文件失败*/
	NAPI_ERR_READ = -13, 			/**<读文件失败*/
	NAPI_ERR_OVERFLOW = -15,			/**<缓冲溢出*/
	NAPI_ERR_SHM = -16,				/**<共享内存出错*/
	NAPI_ERR_NO_DEVICES=-17,			/**<POS无该设备*/
	NAPI_ERR_NOT_SUPPORT=-18, 		/**<不支持该功能*/
    NAPI_ALREADY_DONE=- 19,          /**< New logo`s checksum is the same to the checksum stored in flash partition*/
	NAPI_ERR_NOSWIPED = -50,			/**<无磁卡刷卡记录*/
	NAPI_ERR_SWIPED_DATA=-51,		/**<驱动磁卡数据格式错*/
	NAPI_ERR_USB_LINE_UNCONNECT = -100,  /**<USB线未连接*/
	NAPI_ERR_NO_SIMCARD = -201,		/**<无SIM卡*/
	NAPI_ERR_PIN = -202, 			/**<SIM卡密码错误*/
	NAPI_ERR_PIN_LOCKED = -203,		/**<SIM卡被锁定*/
	NAPI_ERR_PIN_UNDEFINE = -204,	/**<SIM卡未定义错误*/
	NAPI_ERR_EMPTY = -205,			/**<返回空串*/
	NAPI_ERR_ETH_PULLOUT = -250,		/**<以太网未插线*/
	NAPI_ERR_PPP_PARAM = -301,		/**<PPP参数出错*/
	NAPI_ERR_PPP_DEVICE = -302,		/**<PPP无效设备*/
	NAPI_ERR_PPP_OPEN = -303, 		/**<PPP已打开*/
	NAPI_ERR_TCP_ALLOC = -304,	/**<无法分配*/
	NAPI_ERR_TCP_PARAM = -305,	/**<无效参数*/
	NAPI_ERR_TCP_TIMEOUT = -306,	/**<传输超时*/
	NAPI_ERR_TCP_INVADDR = -307,	/**<无效地址*/
	NAPI_ERR_TCP_CONNECT = -308,	/**<没有连接*/
	NAPI_ERR_TCP_PROTOCOL = -309,/**<协议错误*/
	NAPI_ERR_TCP_NETWORK = -310,	/**<网络错误*/
	NAPI_ERR_TCP_SEND = -311,	/**<发送错误*/
	NAPI_ERR_TCP_RECV = -312,	/**<接收错误*/
	
	NAPI_ERR_WLM_SEND_AT_FAIL = -320,		/**<无线发送AT失败*/

	NAPI_ERR_SSL_PARAM = -350,       	/**<无效参数*/
	NAPI_ERR_SSL_ALREADCLOSE = -351, 	/**<连接已关闭*/
	NAPI_ERR_SSL_ALLOC = -352,       	/**<无法分配*/
	NAPI_ERR_SSL_INVADDR = -353,     	/**<无效地址*/
	NAPI_ERR_SSL_TIMEOUT = -354,     	/**<连接超时*/
	NAPI_ERR_SSL_MODEUNSUPPORTED = -355, /**<模式不支持*/
	NAPI_ERR_SSL_SEND = -356,        	/**<发送错误*/
	NAPI_ERR_SSL_RECV = -357,        	/**<接收错误*/
	NAPI_ERR_SSL_CONNECT = -358,       	/**<没有连接*/

	NAPI_ERR_NET_GETADDR = -401,			/**<获取本地地址或子网掩码失败*/
	NAPI_ERR_NET_GATEWAY = -402,			/**<获取网关地址失败*/
	NAPI_ERR_NET_ADDRILLEGAL =-403,		/**<获取地址格式错误*/	
	NAPI_ERR_NET_UNKNOWN_COMMTYPE=-404,	/**<未知的通信类型*/	
	NAPI_ERR_NET_INVALIDIPSTR=-405,		/**<无效的IP字符串*/
	NAPI_ERR_NET_UNSUPPORT_COMMTYPE=-406,	/**<不支持的通信类型*/

	NAPI_ERR_THREAD_PARAM = -450,     	/**<无效参数*/
	NAPI_ERR_THREAD_ALLOC = -451,     	/**<无效分配*/
	NAPI_ERR_THREAD_CMDUNSUPPORTED = -452,     /**<命令不支持*/

	NAPI_ERR_MODEM_RESETFAIL = -501,			/**<MODEM 复位失败*/
	NAPI_ERR_MODEM_GETSTATUSFAIL = -502,		/**<MODEM 获取状态失败*/
	NAPI_ERR_MODEM_SLEPPFAIL = -503,			/**<MODEM 休眠失败*/
	NAPI_ERR_MODEM_SDLCINITFAIL = -504,		/**<MODEM 同步初始化失败*/
	NAPI_ERR_MODEM_INIT_NOT=-505,			/**<MODEM 未进行初始化*/
	NAPI_ERR_MODEM_SDLCWRITEFAIL=-506,		/**<MODEM 同步写失败*/
	NAPI_ERR_MODEM_ASYNWRITEFAIL = -507,		/**<MODEM 异步写数据失败*/	
	NAPI_ERR_MODEM_ASYNDIALFAIL = -508,		/**<MODEM 异步拨号失败*/
	NAPI_ERR_MODEM_ASYNINITFAIL = -509,		/**<MODEM 异步初始化失败*/	
	NAPI_ERR_MODEM_SDLCHANGUPFAIL=-510,		/**<MODEM 同步挂断失败*/
	NAPI_ERR_MODEM_ASYNHANGUPFAIL=-511,		/**<MODEM 异步挂断失败*/
	NAPI_ERR_MODEM_SDLCCLRBUFFAIL=-512,		/**<MODEM 同步清缓冲失败*/
	NAPI_ERR_MODEM_ASYNCLRBUFFAIL=-513,		/**<MODEM 异步清缓冲失败*/
	NAPI_ERR_MODEM_ATCOMNORESPONSE=-514,		/**<MODEM AT命令无响应*/
	NAPI_ERR_MODEM_PORTWRITEFAIL=-515,		/**<MODEM 端口写数据失败*/
	NAPI_ERR_MODEM_SETCHIPFAIL=-516,			/**<MODEM 模块寄存器设置失败*/
	NAPI_ERR_MODEM_STARTSDLCTASK=-517,		/**<MODEM 拨号时开启SDLC 任务失败*/
	NAPI_ERR_MODEM_GETBUFFLENFAIL = -518,	/**<MODEM 获取数据长度失败*/
	NAPI_ERR_MODEM_QUIT=-519,				/**<MODEM 手动退出*/
	NAPI_ERR_MODEM_NOPREDIAL=-520,			/**<MODEM 未拨号*/
	NAPI_ERR_MODEM_NOCARRIER=-521,			/**<MODEM 没载波*/
	NAPI_ERR_MODEM_NOLINE=-523,				/**<MODEM 未插线*/
	NAPI_ERR_MODEM_OTHERMACHINE=-524,		/**<MODEM 存在并机*/
	NAPI_ERR_MODEM_PORTREADFAIL=-525,		/**<MODEM 端口读数据失败*/
	NAPI_ERR_MODEM_CLRBUFFAIL=-526,			/**<MODEM 清空缓冲失败*/
	NAPI_ERR_MODEM_ATCOMMANDERR=-527,		/**<MODEM AT命令错误*/
	NAPI_ERR_MODEM_STATUSUNDEFINE=-528,		/**<MODEM 状态未确认状态*/
	NAPI_ERR_MODEM_GETVERFAIL=-529,			/**<MODEM获取版本失败*/
	NAPI_ERR_MODEM_SDLCDIALFAIL = -530,		/**<MODEM 同步拨号失败*/
	NAPI_ERR_MODEM_SELFADAPTFAIL = -531,		/**<MODEM自适应失败*/
	NAPI_ERR_MODEM_SELFADAPTCANCEL = -532, 	/**<MODEM自适应取消*/

	NAPI_ERR_ICC_WRITE_ERR =			-601,	/**<写器件83c26出错*/
	NAPI_ERR_ICC_COPYERR=			-602,	/**<内核数据拷贝出错*/
	NAPI_ERR_ICC_POWERON_ERR=		-603,	/**<上电出错*/
	NAPI_ERR_ICC_COM_ERR=			-604,	/**<命令出错*/
	NAPI_ERR_ICC_CARDPULL_ERR=		-605,	/**<卡拔出了*/
	NAPI_ERR_ICC_CARDNOREADY_ERR=	-606,	/**<卡未准备好*/

	NAPI_ERR_USDDISK_PARAM =  -650,          /**<无效参数*/
	NAPI_ERR_USDDISK_DRIVELOADFAIL =  -651,  /**<U盘或SD卡驱动加载失败*/
	NAPI_ERR_USDDISK_NONSUPPORTTYPE =  -652, /**<不支持的类型*/
	NAPI_ERR_USDDISK_UNMOUNTFAIL =  -653,    /**<挂载失败*/
	NAPI_ERR_USDDISK_UNLOADDRIFAIL =  -654,  /**<卸载驱动失败*/
	NAPI_ERR_USDDISK_IOCFAIL =  -655,        /**<驱动调用错误*/

    NAPI_ERR_APP_BASE               = -800, /**<Unknown error*/
	NAPI_ERR_APP_NOT_EXIST=(NAPI_ERR_APP_BASE-1),		/**<应用项不存在*/
	NAPI_ERR_APP_NOT_MATCH=(NAPI_ERR_APP_BASE-2),	    /**<补丁包文件不匹配*/
	NAPI_ERR_APP_FAIL_SEC=(NAPI_ERR_APP_BASE-3),	   	/**<获取安全攻击状态失败*/
	NAPI_ERR_APP_SEC_ATT=(NAPI_ERR_APP_BASE-4),	  	/**<存在安全攻击*/
	NAPI_ERR_APP_FILE_EXIST=(NAPI_ERR_APP_BASE-5),	/**<应用中该文件已存在*/
	NAPI_ERR_APP_FILE_NOT_EXIST=(NAPI_ERR_APP_BASE-6),/**<应用中该文件不存在*/
	NAPI_ERR_APP_FAIL_AUTH=(NAPI_ERR_APP_BASE-7),	  	/**<证书认证失败*/
	NAPI_ERR_APP_LOW_VERSION=(NAPI_ERR_APP_BASE-8),	/**<补丁包的版本比应用版本低*/

	NAPI_ERR_APP_MAX_CHILD=(NAPI_ERR_APP_BASE-9),			/**<子应用运行数超过最大运行数目*/
	NAPI_ERR_APP_CREAT_CHILD=(NAPI_ERR_APP_BASE-10),		/**<创建子进程错误*/
	NAPI_ERR_APP_WAIT_CHILD=(NAPI_ERR_APP_BASE-11),		/**<等待子进程结束错误*/
	NAPI_ERR_APP_FILE_READ=(NAPI_ERR_APP_BASE-12),		/**<读文件错误*/
	NAPI_ERR_APP_FILE_WRITE=(NAPI_ERR_APP_BASE-13),		/**<写文件错误*/
	NAPI_ERR_APP_FILE_STAT=(NAPI_ERR_APP_BASE-14),		/**<获取文件信息错误*/
	NAPI_ERR_APP_FILE_OPEN=(NAPI_ERR_APP_BASE-15),		/**<文件打开错误*/
	NAPI_ERR_APP_NLD_HEAD_LEN=(NAPI_ERR_APP_BASE-16),		/**<NLD文件获取头信息长度错误*/
	NAPI_ERR_APP_PUBKEY_EXPIRED=(NAPI_ERR_APP_BASE-17),	/**<公钥有效期*/
	NAPI_ERR_APP_MMAP=(NAPI_ERR_APP_BASE-18),				/**<内存映射错误*/
	NAPI_ERR_APP_MALLOC=(NAPI_ERR_APP_BASE-19),			/**<动态内存分配错误*/
	NAPI_ERR_APP_SIGN_DECRYPT=(NAPI_ERR_APP_BASE-20),		/**<签名数据解签错误*/
	NAPI_ERR_APP_SIGN_CHECK=(NAPI_ERR_APP_BASE-21),		/**<签名数据校验错误*/
	NAPI_ERR_APP_MUNMAP=(NAPI_ERR_APP_BASE-22),			/**<内存映射释放错误*/
	NAPI_ERR_APP_TAR=(NAPI_ERR_APP_BASE-23),				/**<tar命令执行失败*/
	NAPI_ERR_APP_KEY_UPDATE_BAN=(NAPI_ERR_APP_BASE-24),				/**<调试状态禁止密钥升级*/
	NAPI_ERR_APP_FIRM_PATCH_VERSION=(NAPI_ERR_APP_BASE-25),				/**固件补丁增量包版本不匹配*/
    NAPI_ERR_APP_CERT_HAS_EXPIRED=(NAPI_ERR_APP_BASE-26),				/**证书已经失效*/
    NAPI_ERR_APP_CERT_NOT_YET_VALID=(NAPI_ERR_APP_BASE-27),             /**证书尚未生效*/
	NAPI_ERR_APP_FILE_NAME_TOO_LONG=(NAPI_ERR_APP_BASE-28),    /**文件名长度大于32字节*/
    NAPI_ERR_APP_CA_ALREADY_CUSTOMIZED    = (NAPI_ERR_APP_BASE - 29), /**<Application CA has been customized*/
    NAPI_ERR_APP_FILE_CHK                 = (NAPI_ERR_APP_BASE - 30), /**<<File check error*/
	
    NAPI_ERR_SECP_BASE = (-1000),								/**<未知错误*/
    NAPI_ERR_SECP_TIMEOUT = (NAPI_ERR_SECP_BASE - 1),             /**<获取键值超时*/
    NAPI_ERR_SECP_PARAM = (NAPI_ERR_SECP_BASE - 2),               /**<输入参数非法*/
    NAPI_ERR_SECP_DBUS = (NAPI_ERR_SECP_BASE - 3),                /**<DBUS通讯错误*/
    NAPI_ERR_SECP_MALLOC = (NAPI_ERR_SECP_BASE - 4),              /**<动态内存分配错误*/
    NAPI_ERR_SECP_OPEN_SEC = (NAPI_ERR_SECP_BASE - 5),            /**<打开安全设备错误*/
    NAPI_ERR_SECP_SEC_DRV = (NAPI_ERR_SECP_BASE - 6),             /**<安全设备操作错误*/
    NAPI_ERR_SECP_GET_RNG = (NAPI_ERR_SECP_BASE - 7),             /**<获取随机数*/
    NAPI_ERR_SECP_GET_KEY = (NAPI_ERR_SECP_BASE - 8),             /**<获取密钥值*/
    NAPI_ERR_SECP_KCV_CHK = (NAPI_ERR_SECP_BASE - 9),             /**<KCV校验错误*/
    NAPI_ERR_SECP_GET_CALLER = (NAPI_ERR_SECP_BASE - 10),         /**<获取调用者信息错误*/
    NAPI_ERR_SECP_OVERRUN = (NAPI_ERR_SECP_BASE - 11),            /**<运行次数出错*/
    NAPI_ERR_SECP_NO_PERMIT = (NAPI_ERR_SECP_BASE - 12),          /**<权限不允许*/
    NAPI_ERR_SECP_TAMPER = (NAPI_ERR_SECP_BASE - 13),          	/**<安全攻击*/
    NAPI_ERR_SECP_UNSUPPORT = (NAPI_ERR_SECP_BASE - 14),			/**<不支持该功能*/
    NAPI_ERR_SECVP_BASE = (-1100),                           /**<未知错误*/
    NAPI_ERR_SECVP_TIMEOUT = (NAPI_ERR_SECVP_BASE - 1),       /**<获取键值超时*/
    NAPI_ERR_SECVP_PARAM = (NAPI_ERR_SECVP_BASE - 2),         /**<输入参数非法*/
    NAPI_ERR_SECVP_DBUS = (NAPI_ERR_SECVP_BASE - 3),          /**<DBUS通讯错误*/
    NAPI_ERR_SECVP_OPEN_EVENT0 =	(NAPI_ERR_SECVP_BASE - 4),   /**<打开event0设备出错*/
    NAPI_ERR_SECVP_SCAN_VAL = (NAPI_ERR_SECVP_BASE - 5),      /**<扫描值超出定义*/
    NAPI_ERR_SECVP_OPEN_RNG = (NAPI_ERR_SECVP_BASE - 6),      /**<打开随机数设备错误*/
    NAPI_ERR_SECVP_GET_RNG = (NAPI_ERR_SECVP_BASE - 7),       /**<获取随机数出错*/
    NAPI_ERR_SECVP_GET_ESC = (NAPI_ERR_SECVP_BASE - 8),       /**<用户取消键退出*/
    NAPI_ERR_SECVP_VPP = (-1120),                            /**<未知错误*/
    NAPI_ERR_SECVP_INVALID_KEY=(NAPI_ERR_SECVP_VPP),  		/**<无效密钥,内部使用.*/
	NAPI_ERR_SECVP_NOT_ACTIVE=(NAPI_ERR_SECVP_VPP-1),  		/**<VPP没有激活，第一次调用VPPInit.*/
	NAPI_ERR_SECVP_TIMED_OUT=(NAPI_ERR_SECVP_VPP-2),			/**<已经超过VPP初始化的时间.*/
	NAPI_ERR_SECVP_ENCRYPT_ERROR=(NAPI_ERR_SECVP_VPP-3),		/**<按确认键后，加密错误.*/
	NAPI_ERR_SECVP_BUFFER_FULL=(NAPI_ERR_SECVP_VPP-4),		/**<输入BUF越界，（键入的PIN太长）*/
	NAPI_ERR_SECVP_PIN_KEY=(NAPI_ERR_SECVP_VPP-5),  			/**<数据键按下，回显"*".*/
	NAPI_ERR_SECVP_ENTER_KEY=(NAPI_ERR_SECVP_VPP-6),			/**<确认键按下，PIN处理.*/
	NAPI_ERR_SECVP_BACKSPACE_KEY=(NAPI_ERR_SECVP_VPP-7),		/**<退格键按下.*/
	NAPI_ERR_SECVP_CLEAR_KEY=(NAPI_ERR_SECVP_VPP-8),  		/**<清除键按下，清除所有'*'显示.*/
	NAPI_ERR_SECVP_CANCEL_KEY=(NAPI_ERR_SECVP_VPP-9),  		/**<取消键被按下.*/
	NAPI_ERR_SECVP_GENERALERROR=(NAPI_ERR_SECVP_VPP-10),  	/**<该进程无法继续。内部错误.*/
	NAPI_ERR_SECVP_CUSTOMERCARDNOTPRESENT=(NAPI_ERR_SECVP_VPP-11), /**<IC卡被拔出*/
	NAPI_ERR_SECVP_HTCCARDERROR=(NAPI_ERR_SECVP_VPP-12),  	/**<访问智能卡错误.*/
	NAPI_ERR_SECVP_WRONG_PIN_LAST_TRY=(NAPI_ERR_SECVP_VPP-13),/**<智能卡-密码不正确，重试一次.*/
	NAPI_ERR_SECVP_WRONG_PIN=(NAPI_ERR_SECVP_VPP-14), 		/**<智能卡-最后尝试一次.*/
	NAPI_ERR_SECVP_ICCERROR=(NAPI_ERR_SECVP_VPP-15),  		/**<智能卡-重试太多次*/
	NAPI_ERR_SECVP_PIN_BYPASS=(NAPI_ERR_SECVP_VPP-16),  		/**<智能卡-PIN验证通过,并且PIN是0长度*/
	NAPI_ERR_SECVP_ICCFAILURE=(NAPI_ERR_SECVP_VPP-17),  		/**<智能卡-致命错误.*/
	NAPI_ERR_SECVP_GETCHALLENGE_BAD=(NAPI_ERR_SECVP_VPP-18),  /**<智能卡-应答不是90 00.*/
	NAPI_ERR_SECVP_GETCHALLENGE_NOT8=(NAPI_ERR_SECVP_VPP-19), /**<智能卡-无效的应答长度.*/
 	NAPI_ERR_SECVP_PIN_ATTACK_TIMER=(NAPI_ERR_SECVP_VPP-20),  /**<PIN攻击定时器被激活*/

    NAPI_ERR_SECCR_BASE = (-1200),                           /**<未知错误*/
    NAPI_ERR_SECCR_TIMEOUT = (NAPI_ERR_SECCR_BASE - 1),       /**<获取键值超时*/
    NAPI_ERR_SECCR_PARAM = (NAPI_ERR_SECCR_BASE - 2),         /**<输入参数非法*/
    NAPI_ERR_SECCR_DBUS = (NAPI_ERR_SECCR_BASE - 3),          /**<DBUS通讯错误*/
    NAPI_ERR_SECCR_MALLOC = (NAPI_ERR_SECCR_BASE - 4),        /**<动态内存分配错误*/
    NAPI_ERR_SECCR_OPEN_RNG = (NAPI_ERR_SECCR_BASE - 5),      /**<打开随机数设备错误*/
    NAPI_ERR_SECCR_DRV = (NAPI_ERR_SECCR_BASE - 6),           /**<驱动加密错误*/
    NAPI_ERR_SECCR_KEY_TYPE = (NAPI_ERR_SECCR_BASE - 7),      /**<密钥类型错误*/
    NAPI_ERR_SECCR_KEY_LEN = (NAPI_ERR_SECCR_BASE - 8),       /**<密钥长度错误*/
    NAPI_ERR_SECCR_GET_KEY = (NAPI_ERR_SECCR_BASE - 9),       /**<获取密钥错误*/

    NAPI_ERR_SECKM_BASE = (-1300),								/**<未知错误*/
    NAPI_ERR_SECKM_TIMEOUT = (NAPI_ERR_SECKM_BASE - 1),           /**<获取键值超时*/
    NAPI_ERR_SECKM_PARAM = (NAPI_ERR_SECKM_BASE - 2),             /**<输入参数非法*/
    NAPI_ERR_SECKM_DBUS = (NAPI_ERR_SECKM_BASE - 3),              /**<DBUS通讯错误*/
    NAPI_ERR_SECKM_MALLOC = (NAPI_ERR_SECKM_BASE - 4),            /**<动态内存分配错误*/
    NAPI_ERR_SECKM_OPEN_DB = (NAPI_ERR_SECKM_BASE - 5),           /**<数据库打开错误*/
    NAPI_ERR_SECKM_DEL_DB = (NAPI_ERR_SECKM_BASE - 6),            /**<删除数据库错误*/
    NAPI_ERR_SECKM_DEL_REC = (NAPI_ERR_SECKM_BASE - 7),           /**<删除记录错误*/
    NAPI_ERR_SECKM_INSTALL_REC = (NAPI_ERR_SECKM_BASE - 8),       /**<安装密钥记录错误*/
    NAPI_ERR_SECKM_READ_REC = (NAPI_ERR_SECKM_BASE - 9),          /**<读密钥记录错误*/
    NAPI_ERR_SECKM_OPT_NOALLOW = (NAPI_ERR_SECKM_BASE - 10),      /**<操作不允许*/
    NAPI_ERR_SECKM_KEY_MAC = (NAPI_ERR_SECKM_BASE - 11),          /**<密钥MAC校验错误*/
    NAPI_ERR_SECKM_KEY_TYPE = (NAPI_ERR_SECKM_BASE - 12),         /**<密钥类型错误*/
    NAPI_ERR_SECKM_KEY_ARCH = (NAPI_ERR_SECKM_BASE - 13),         /**<密钥体系错误*/
    NAPI_ERR_SECKM_KEY_LEN  = (NAPI_ERR_SECKM_BASE - 14),         /**<密钥长度错误*/
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
	
    //key store
    NAPI_ERR_SECKS_BASE = (-1400),
    NAPI_ERR_SECKS_TIMEOUT = (NAPI_ERR_SECKS_BASE - 1),               /**<获取键值超时*/
    NAPI_ERR_SECKS_PARAM = (NAPI_ERR_SECKS_BASE - 2),               /**<输入参数非法*/
    //kla
    NAPI_ERR_SECKLA_BASE = (-1500),
    NAPI_ERR_SECKLA_ERR_INTERNAL = (NAPI_ERR_SECKLA_BASE -1),				/*Unspecified internal error.*/
	NAPI_ERR_SECKLA_PARAM = (NAPI_ERR_SECKLA_BASE -2),				/*Invalid parameter passed to function.*/
	NAPI_ERR_SECKLA_ERR_INVALID_CRT = (NAPI_ERR_SECKLA_BASE -3),		/*Invalid certification*/
	NAPI_ERR_SECKLA_ERR_INVALID_SIG = (NAPI_ERR_SECKLA_BASE -4),			/*Invalid nonce signature*/
	NAPI_ERR_SECKLA_ERR_KEY_NOT_FOUND = (NAPI_ERR_SECKLA_BASE -5),		/*Key not found*/
	NAPI_ERR_SECKLA_ERR_INVALIDKEY_USAGE = (NAPI_ERR_SECKLA_BASE -6),		/*Invalid use of the key according to the key tag*/
    //NAPI algorithm
    NAPI_ERR_SECALG_BASE = (-1600),
    NAPI_ERR_SECALG_TIMEOUT = (NAPI_ERR_SECALG_BASE - 1),               /**<获取键值超时*/
    NAPI_ERR_SECALG_PARAM = (NAPI_ERR_SECALG_BASE - 2),               /**<输入参数非法*/
    
	//
	NAPI_ERR_SEC_CFG_BASE = (-1700),
	NAPI_ERR_SEC_CFG_TABLE = (NAPI_ERR_SEC_CFG_BASE - 1),             /* indicate current key table, "" for app itself */
	NAPI_ERR_SEC_CFG_UNIQUE = (NAPI_ERR_SEC_CFG_BASE - 2),                /* check if installing key is unique : 0 - no check, 1 - check */
	NAPI_ERR_SEC_CFG_MISUSE = (NAPI_ERR_SEC_CFG_BASE - 3),                /* check if key is misused according to its type : 0 - no check, 1 - check */
	NAPI_ERR_SEC_CFG_TRIES_LIMIT = (NAPI_ERR_SEC_CFG_BASE - 4),           /* check if current function is overrun: 0 - no check, 1 - check */
	NAPI_ERR_SEC_CFG_STRENGTH = (NAPI_ERR_SEC_CFG_BASE - 5),              /* keys should be protected by the same or higher strength keys: 0 - no check, 1 - check */
	NAPI_ERR_SEC_CFG_KEYLEN_LIMIT = (NAPI_ERR_SEC_CFG_BASE - 6),          /* key length should be stronger than 8 bytes : 0 - no check, 1- check */
	NAPI_ERR_SEC_CFG_DPA_DEFENCE = (NAPI_ERR_SEC_CFG_BASE - 7),          /* DPA defence: 0 - disable, 1- enable */
	NAPI_ERR_SEC_CFG_CLEARKEY_LIMIT = (NAPI_ERR_SEC_CFG_BASE - 8),       /* check if the clearkey is allowed to be installed: 0 - disable, 1- enable */
	NAPI_ERR_RFID_INITSTA=			-2005,  /**<非接触卡-射频接口器件故障或者未配置*/
	NAPI_ERR_RFID_NOCARD=			-2008,  /**<非接触卡-无卡  0x0D*/
	NAPI_ERR_RFID_MULTICARD=			-2009,  /**<非接触卡-多卡状态*/
	NAPI_ERR_RFID_SEEKING=			-2010,  /**<非接触卡-寻卡/激活过程中失败*/
	NAPI_ERR_RFID_PROTOCOL=			-2011,  /**<非接触卡-不支持ISO1444-4协议，如M1卡  F*/

	NAPI_ERR_RFID_NOPICCTYPE=		-2012,  /**<非接触卡-未设置卡 0x01*/
	NAPI_ERR_RFID_NOTDETE=			-2013,  /**<非接触卡-未寻卡   0x02*/
	NAPI_ERR_RFID_AANTI=				-2014,  /**<非接触卡-A卡冲突(多张卡存在)  0x03*/
	NAPI_ERR_RFID_RATS=				-2015,  /**<非接触卡-A卡RATS过程出错   0x04*/
	NAPI_ERR_RFID_BACTIV=			-2016,  /**<非接触卡-B卡激活失败   0x07*/
	NAPI_ERR_RFID_ASEEK=				-2017,  /**<非接触卡-A卡寻卡失败(可能多张卡存在)   0x0A*/
	NAPI_ERR_RFID_BSEEK=				-2018,  /**<非接触卡-B卡寻卡失败(可能多张卡存在)   0x0B*/
	NAPI_ERR_RFID_ABON=				-2019,  /**<非接触卡-A、B卡同时存在   0x0C*/
	NAPI_ERR_RFID_UPED=				-2020,  /**<非接触卡-已经激活(上电)   0x0E*/
	NAPI_ERR_RFID_NOTACTIV=			-2021,  /**<非接触卡-未激活*/
	NAPI_ERR_RFID_COLLISION_A=       -2022,  /**<非接触卡-A卡冲突*/
	NAPI_ERR_RFID_COLLISION_B=       -2023,  /**<非接触卡-B卡冲突*/

	NAPI_ERR_MI_NOTAGERR=			-2030,  /**<非接触卡-无卡,				0xff*/
	NAPI_ERR_MI_CRCERR=				-2031,  /**<非接触卡-CRC错,				0xfe*/
	NAPI_ERR_MI_EMPTY=				-2032,  /**<非接触卡-非空,				0xfd*/
	NAPI_ERR_MI_AUTHERR=				-2033,  /**<非接触卡-认证错,			0xfc*/
	NAPI_ERR_MI_PARITYERR=			-2034,  /**<非接触卡-奇偶错,			0xfb*/
	NAPI_ERR_MI_CODEERR=				-2035,  /**<非接触卡-接收代码错			0xfa*/
	NAPI_ERR_MI_SERNRERR=            -2036,  /**<非接触卡-防冲突数据校验错	0xf8*/
	NAPI_ERR_MI_KEYERR=              -2037,  /**<非接触卡-认证KEY错			0xf7*/
	NAPI_ERR_MI_NOTAUTHERR=          -2038,  /**<非接触卡-未认证				0xf6*/
	NAPI_ERR_MI_BITCOUNTERR=         -2039,  /**<非接触卡-接收BIT错			0xf5*/
	NAPI_ERR_MI_BYTECOUNTERR=        -2040,  /**<非接触卡-接收字节错			0xf4*/
	NAPI_ERR_MI_WriteFifo=           -2041,  /**<非接触卡-FIFO写错误			0xf3*/
	NAPI_ERR_MI_TRANSERR=            -2042,  /**<非接触卡-传送操作错误		0xf2*/
	NAPI_ERR_MI_WRITEERR=            -2043,  /**<非接触卡-写操作错误			0xf1*/
	NAPI_ERR_MI_INCRERR=				-2044,  /**<非接触卡-增量操作错误		0xf0*/
	NAPI_ERR_MI_DECRERR=             -2045,  /**<非接触卡-减量操作错误		0xef*/
	NAPI_ERR_MI_OVFLERR=             -2046,  /**<非接触卡-溢出错误			0xed*/
	NAPI_ERR_MI_FRAMINGERR=          -2047,  /**<非接触卡-帧错				0xeb*/
	NAPI_ERR_MI_COLLERR=             -2048,  /**<非接触卡-冲突				0xe8*/
	NAPI_ERR_MI_INTERFACEERR=        -2049,  /**<非接触卡-复位接口读写错		0xe6*/
	NAPI_ERR_MI_ACCESSTIMEOUT=       -2050,  /**<非接触卡-接收超时			0xe5*/
	NAPI_ERR_MI_PROTOCOLERR=			-2051,  /**<非接触卡-协议错				0xe4*/
	NAPI_ERR_MI_QUIT=                -2052,  /**<非接触卡-异常终止			0xe2*/
	NAPI_ERR_MI_PPSErr=				-2053,  /**<非接触卡-PPS操作错			0xe1*/
	NAPI_ERR_MI_SpiRequest=			-2054,  /**<非接触卡-申请SPI失败		0xa0*/
	NAPI_ERR_MI_NY_IMPLEMENTED=		-2055,  /**<非接触卡-无法确认的错误状态	0x9c*/
	NAPI_ERR_MI_CardTypeErr=			-2056,  /**<非接触卡-卡类型错			0x83*/
	NAPI_ERR_MI_ParaErrInIoctl=		-2057,  /**<非接触卡-IOCTL参数错		0x82*/
	NAPI_ERR_MI_Para=				-2059,  /**<非接触卡-内部参数错			0xa9*/

	NAPI_ERR_WIFI_INVDATA=           -3001,  /**<WIFI-无效参数*/
    NAPI_ERR_WIFI_DEVICE_FAULT=      -3002,  /**<WIFI-设备状态出错*/
    NAPI_ERR_WIFI_CMD_UNSUPPORTED=   -3003,  /**<WIFI-不支持的命令*/
    NAPI_ERR_WIFI_DEVICE_UNAVAILABLE=-3004,  /**<WIFI-设备不可用*/
    NAPI_ERR_WIFI_DEVICE_NOTOPEN=    -3005,  /**<WIFI-没有扫描到AP*/
    NAPI_ERR_WIFI_DEVICE_BUSY=       -3006,  /**<WIFI-设备忙*/
    NAPI_ERR_WIFI_UNKNOWN_ERROR=     -3007,  /**<WIFI-未知错误*/
    NAPI_ERR_WIFI_PROCESS_INBADSTATE=-3008,  /**<WIFI-无法连接到AP*/
    NAPI_ERR_WIFI_SEARCH_FAULT=      -3009,  /**<WIFI-扫描状态出错*/
    NAPI_ERR_WIFI_DEVICE_TIMEOUT=    -3010,  /**<WIFI-设备超时*/
    NAPI_ERR_WIFI_NON_CONNECTED=	    -3011,  /**<WIFI-非连接状态*/

    NAPI_ERR_RFID_BUSY = -3101,                      /**<射频卡状态忙*/
    NAPI_ERR_PRN_BUSY = -3102,                       /**<打印状态忙*/
    NAPI_ERR_ICCARD_BUSY = -3103,                /**<IC卡状态忙*/
    NAPI_ERR_MAG_BUSY = -3104,                       /**<磁卡状态忙*/
    NAPI_ERR_USB_BUSY = -3105,                       /**<USB状态忙*/
    NAPI_ERR_WLM_BUSY = -3106,                    /**<无线状态忙*/
	NAPI_ERR_PIN_BUSY = -3107,					/*正处于PIN输入状态*/
	NAPI_ERR_BT_BUSY = -3108,					/*正处于蓝牙忙状态*/
    NAPI_ERR_DEV_BUSY= -3109,                    /*正处于设备忙状态**/
    NAPI_ERR_BT_NOT_CONNECTED = -3201,   /**<蓝牙连接未建立*/
    

	NAPI_ERR_LINUX_ERRNO_BASE=		-5000, /**<<LINUX>系统函数返回ERROR错误前缀*/
	NAPI_ERR_LINUX_TCP_TIMEOUT=  (NAPI_ERR_LINUX_ERRNO_BASE-110),/**<TCP远程端口错误*/
	NAPI_ERR_LINUX_TCP_REFUSE=  (NAPI_ERR_LINUX_ERRNO_BASE-111),/**<TCP远程端口被拒绝*/
	NAPI_ERR_LINUX_TCP_NOT_OPEN=		 (NAPI_ERR_LINUX_ERRNO_BASE-88),/**<TCP句柄未打开错误*/
}EM_NAPI_ERR;

#define MAX_RSA_MODULUS_BITS 2048
#define MAX_RSA_MODULUS_LENGTH  ((MAX_RSA_MODULUS_BITS + 7) / 8)
#define MAX_RSA_PRIME_BITS      ((MAX_RSA_MODULUS_BITS + 1) / 2)
#define MAX_RSA_PRIME_LENGTH    ((MAX_RSA_PRIME_BITS + 7) / 8)
#define MAX_RSA_MODULUS_LEN		512		/**<RSA最大模长度*/

/**
 *@brief RSA密钥信息
*/
typedef struct {
    uint usBits;                    			/**< RSA密钥位数 */
    uchar sModulus[MAX_RSA_MODULUS_LEN];  	/**< 模 */
    uchar sExponent[MAX_RSA_MODULUS_LEN]; 	/**< 指数 */
}ST_NAPI_RSA_KEY;

/**
 *@brief 密钥校验模式
*/
typedef enum{
	NAPI_SEC_KCV_NONE=0,		/**<无验证*/
	NAPI_SEC_KCV_ZERO, /**<对8个字节的0x00计算DES/TDES加密,或对16字节的0x00进行SM4加密,得到的密文的前4个字节即为KCV*/
	NAPI_SEC_KCV_VAL,		/**<首先对密钥明文进行奇校验,再对"\x12\x34x56\x78\x90\x12\x34\x56"进行DES/TDES加密运算,得到密文的前4个字节即为KCV,暂不支持*/
	NAPI_SEC_KCV_DATA,		/**<传入一串数据KcvData,使用源密钥对[aucDstKeyValue(密文) + KcvData]进行指定模式的MAC运算,得到8个字节的MAC即为KCV,暂不支持 */
}EM_NAPI_SEC_KCV;

/**
 * 支持的PINBOCK模式
 */
typedef enum {
	NAPI_SEC_PIN_ISO9564_0=3,    /**<使用主账号加密，密码不足位数补'F'*/
	NAPI_SEC_PIN_ISO9564_1=4,    /**<不使用主账号加密，密码不足位数补随机数*/
	NAPI_SEC_PIN_ISO9564_2=5,    /**<不使用主账号加密，密码不足位数补'F'*/
	NAPI_SEC_PIN_ISO9564_3=6,    /**<使用主账号加密，密码不足位数补随机数*/
	NAPI_SEC_PIN_SM4_1,		/**<不使用主账号，密码不足位数补'F'*/
	NAPI_SEC_PIN_SM4_2,		/**<使用主账号填充方式1，密码不足位数补'F'*/
	NAPI_SEC_PIN_SM4_3,		/**<使用主账号填充方式1，密码不足位数补随机数*/
	NAPI_SEC_PIN_SM4_4,		/**<使用主账号填充方式2，密码不足位数补'F'*/
	NAPI_SEC_PIN_SM4_5,		/**<使用主账号填充方式2，密码不足位数补随机数*/
    NAPI_SEC_PIN_ISO9564_4 = 12,
}NAPI_EM_SEC_PIN;

/**
 *@brief VPP 服务返回的键值定义
*/
typedef enum{
    NAPI_SEC_VPP_KEY_PIN,					/**< 有PIN键码按下，应用应该显示'*'*/
    NAPI_SEC_VPP_KEY_BACKSPACE,				/**< 退格键按下*/
    NAPI_SEC_VPP_KEY_CLEAR,					/**< 清除键按下*/
    NAPI_SEC_VPP_KEY_ENTER,					/**< 确认键按下*/
    NAPI_SEC_VPP_KEY_ESC,					/**< pin输入取消*/
    NAPI_SEC_VPP_KEY_NULL					/**< pin无事件产生*/
}NAPI_EM_SEC_VPP_KEY;

#define KEY_USAGE(usage)        (((usage)[0] << 8) | (usage)[1])
#define KEY_USAGE_BDK           0x4230          /* B0:KT_BDK BASE Derivation Key */
#define KEY_USAGE_PRI           0x4430          /* D0:KT_PRI  */
#define KEY_USAGE_MST           0x4B30          /* K0:KT_MST */
#define KEY_USAGE_MAC           0x4D30          /* M0:KT_MAC */
#define KEY_USAGE_PIN           0x5030          /* P0:KT_PIN */
#define KEY_USAGE_MAG           0x4330          /* C0:KT_MAG */
#define KEY_USAGE_IDK           0x4231          /* B1:KT_IDK DUKPT INITIAL Key */
#define KEY_USAGE_DEN 	0X4430	//‘D0’	0x44, 0x30	Data Encryption 
#define KEY_USAGE_IV 	0X4930  //‘I0’	0x49, 0x30	IV 
#define KEY_USAGE_CTL 	0X5430  //‘T0’	0x54, 0x30	‘T’ for conTrol vector
#define KEY_USAGE_KEW 	0X4B30  //‘K0’	0x4B, 0x30	Key Encryption or wrapping
#define KEY_USAGE_GMAC 	0X4730  //‘G0’	0x47, 0x30	MAC Generation
#define KEY_USAGE_VMAC 	0X4D30  //‘M0’	0x4D, 0x30	MAC Verification
//#define KEY_USAGE_PIN 	0X5030  //‘P0’	0x50, 0x30	Pin Encryption
#define KEY_USAGE_KPV 	0X5630  //‘V0’	0x56, 0x30	PIN verification, KPV
#define KEY_USAGE_CVK 	0X4330  //‘C0’	0x43, 0x30	CVK Card Verification Key 
#define KEY_USAGE_KC 	0X6330  //‘c0’	0x63, 0x30	Key component
//#define KEY_USAGE_BDK 	0X4230  //‘B0’	0x42, 0x30	BDK Base Derivation Key
#define KEY_USAGE_MAC1 	0X3030  //‘00’	0x30, 0x30	ISO 9797-1 MAC Algorithm 1 – 56 bits
#define KEY_USAGE_MAC2 	0X3130  //‘10’	0x31, 0x30	ISO 9797-1 MAC Algorithm 1 – 112 bits
#define KEY_USAGE_MAC3 	0X3230  //‘20’	0x32, 0x30	ISO 9797-1 MAC Algorithm 2 – 112 bits
#define KEY_USAGE_MAC4 	0X3330  //‘30’	0x33, 0x30	ISO 9797-1 MAC Algorithm 3 – 112 bits
#define KEY_USAGE_MAC5 	0X3430  //‘40’	0x34, 0x30	ISO 9797-1 MAC Algorithm 4 – 112 bits
#define KEY_USAGE_MAC6 	0X3530  //‘50’	0x35, 0x30	ISO 9797-1 MAC Algorithm 5 – 56 bits
#define KEY_USAGE_MAC7 	0X3630  //‘60’	0x36, 0x30	ISO 9797-1 MAC Algorithm 5 – 112 bits

/**
 *@brief VPP 服务返回的键值定义
*/

/**
 *@brief        Initialises the Virtual (internal) PIN pad. Start the PIN entry mode.
 *@param[in] SessionType    For SessionType "SEC_VPP_MASTER_SESSION", pAD will be an encrypted session key, see ST_SEC_SESSION_KEY.
 *@param[in] CipherID       PIN Key Algorithm: TDES or AES
 *@param[in] ucKeyIdx       PIN Key index, 1~255.
 *@param[in] pPAN           Primary Account Number, NULL terminated character string.
 *@param[in] PINBlockFmt    PIN BLOCK per ISO9564, format 0~4.
 *@param[in] unTimeOut      Timeout value (seconds), 5-200.
 *@param[in] pRSAKey        RSA public key for the offline ciphertext PIN encryption.
 *@param[in] pAD            Additional data, for Master Session this is packed encrypted session key, given by the structure ST_SEC_SESSION_KEY.
 *@param[in] unADSize       Size of Additional Data.
 *@return
  On success, it returns \ref NDK_OK "NDK_OK"; on error, it returns \ref EM_NDK_ERR "EM_NDK_ERR". 
*/
int NAPI_SecVPPInit( EM_SEC_VPP_SESSION_TYPE SessionType,
                EM_SEC_CRYPTO_KEY_TYPE KeyType,
                uchar ucKeyIdx,
                char *pPAN,
                uint PINBlockFmt,
                uint unTimeOut,
                ST_NAPI_RSA_KEY *pRSAKey,
                void *pAD,
                uint unADSize );

/**
 *@brief        Process and get PIN entry event
 *@param[out]   nEvent        PIN entry event, see EM_SEC_VPP_KEY 
 *@param[out]   psPinBlock    Ciphertext pinblock if the user finish PIN entry and press Enter key.
                              During the PIN entry, the first byte of psPinBlock[0] indictaes length of current PIN digits.
 *@param[out]   pnOutPinLen   Pointer to size of output pinblock.
 *@param[out]   psKsn         Pointer to the output KSN for current PIN encryption if the "SessionType" is DUKPT.
 *@param[out]   pnOutKsnLen   Pointer to size of output KSN if the "SessionType" is DUKPT.
 *@return
  On success, it returns \ref NDK_OK "NDK_OK"; on error, it returns \ref EM_NDK_ERR "EM_NDK_ERR". 
*/
int NAPI_SecVPPGetEvent(int *nEvent, uchar *psPinBlock, int *pnOutPinLen, uchar *psKsn, int *pnOutKsnLen);

/**
 @brief Simulated key code to externally influence PIN entry procedure.
 *@param[in] key    The simulated key may be set externally during PIN entry: 
                    KEY_CANCEL - simulates pressing CANCEL key
 *@return
  On success, it returns \ref NDK_OK "NDK_OK"; on error, it returns \ref EM_NDK_ERR "EM_NDK_ERR". 
*/
int NAPI_SecVPPSetEvent(uint key);

/**
 *@brief         Encrypt Data using the algorithm and Key specified.
 *@param[in] pstDataIn       Pointer to the data for encryption (\ref ST_SEC_ENCRYPTION_DATA "ST_SEC_ENCRYPTION_DATA")
 *@param[out] psDataOut      Pointer to output data
 *@param[out] pnOutLen       Pointer to size of output data
 *@param[out] psKsnOut       Pointer to output KSN if it is a DUKPT encryption
 *@param[out] pnOutKsnLen    Pointer to the size of output KSN if it is a DUKPT encryption.
 *@return
  On success, it returns \ref NDK_OK "NDK_OK"; on error, it returns \ref EM_NDK_ERR "EM_NDK_ERR". 
*/
int NAPI_SecEncryption(ST_SEC_ENCRYPTION_DATA *pstDataIn, uchar *psDataOut, int *pnOutLen, uchar *psKsnOut, int *pnOutKsnLen);

/**
 *@brief         Decrypt Data using the algorithm and Key specified.
 *@param[in] pstDataIn       Pointer to the data for decryption (\ref ST_SEC_ENCRYPTION_DATA "ST_SEC_ENCRYPTION_DATA")
 *@param[out] psDataOut      Pointer to output data
 *@param[out] pnOutLen       Pointer to size of output data
 *@param[out] psKsnOut       Pointer to output KSN if it is a DUKPT decryption.
 *@param[out] pnOutKsnLen    Pointer to the size of output KSN if it is a DUKPT decryption.
 *@return
  On success, it returns \ref NDK_OK "NDK_OK"; on error, it returns \ref EM_NDK_ERR "EM_NDK_ERR". 
*/
int NAPI_SecDecryption(ST_SEC_ENCRYPTION_DATA *pstDataIn, uchar *psDataOut, int *pnOutLen, uchar *psKsnOut, int *pnOutKsnLen);

/**
 *@brief        Generate Message Authentication Code for a block of data.
 *@param[in] CipherType      Full cipher identifier (e.g. SEC_CIPHER_AES_128_CBC)
 *@param[in] ucKeyID         Key index
 *@param[in] psIV            Initial Vector
 *@param[in] unIVSize        IV size, 8 bytes for TDES, 16 bytes for AES
 *@param[in] psDataIn        Input data
 *@param[in] nDataInLen      Input data length
 *@param[in] pAD             Additional data, Pointer to a ST_SEC_SESSION_KEY structure when a session key is used to encrypt data.
                             This means that the key indicated by KeyID is a KEK
 *@param[in] unADSize        Size of additional data, could be the size of ST_SEC_SESSION_KEY
 *@param[out] psMacOut       Pointer to output MAC value
 *@param[out] pnOutLen       Pointer to size of output data
 *@param[out] psKsnOut       Pointer to output KSN if the encryption key is DUKPT key
 *@return
  On success, it returns \ref NDK_OK "NDK_OK"; on error, it returns \ref EM_NDK_ERR "EM_NDK_ERR". 
*/
int NAPI_SecGenerateMAC(EM_SEC_MAC_TYPE MacType, uchar ucKeyID, uchar *psIV, int unIVSize, uchar *psDataIn, int nDataInLen, uchar *pAD, int unADSize, 
                       uchar *psMacOut, int *pnOutLen, uchar *psKsnOut, int *nOutKsnLen);

/**
 *@brief		Returns key information such as KCV, length, etc.
 *@param[in] ucKeyID		 Key index, 1~250
 *@param[in] KeyType		 Key Type
 *@param[in] KeyUsage		 Key Usage
 *@param[in] pAD			 Additional data for key information.
 *@param[in] unADSize		 Size of Additional data.

 *@param[out] psOutInfo 	  Pointer to the output buffer
 *@param[out] pnOutInfoLen	  Pointer to the output length
 *@return
  On success, it returns \ref NDK_OK "NDK_OK"; on error, it returns \ref EM_NDK_ERR "EM_NDK_ERR".
*/
int NAPI_SecGetKeyInfo(EM_SEC_KEY_INFO_ID InfoID, uchar ucKeyID, EM_SEC_CRYPTO_KEY_TYPE KeyType, EM_SEC_KEY_USAGE KeyUsage,
					  uchar *pAD, uint unADSize, uchar *psOutInfo, int *pnOutInfoLen);
/**
 *@brief		Delete key.
 *@param[in] ucKeyID		 Key index, 1~250
 *@param[in] KeyType		 Key Type
 *@param[in] KeyUsage		 Key Usage

 *@return
  On success, it returns \ref NDK_OK "NDK_OK"; on error, it returns \ref EM_NDK_ERR "EM_NDK_ERR".
*/
int NAPI_SecDeleteKey(uchar ucKeyID, EM_SEC_CRYPTO_KEY_TYPE KeyType, EM_SEC_KEY_USAGE KeyUsage);
int NAPI_SecGetServKeyOwner(char *pszOwner);

/**
*@brief Key Injection
 *@details Generic key injection for all key types, with different algorithm...
 *@param[in] Method             Key generation method,ref EM_SEC_KEYIN_METHOD "EM_SEC_KEYIN_METHOD"
 *@param[in] pstKGData          All needed data for key injection, ref ST_SEC_KEYIN_DATA "ST_SEC_KEYIN_DATA"
 *@param[in] pstKcvData         Key check value (KCV) for the injected key, Optional
 *@return
  On success, it returns \ref NDK_OK "NDK_OK"; on error, it returns \ref EM_NDK_ERR "EM_NDK_ERR". 
*/
int NAPI_SecGenerateKey( EM_SEC_KEYIN_METHOD Method, ST_SEC_KEYIN_DATA *pstKGData, ST_SEC_KCV_DATA *pstKcvData);
int NAPI_SecKeyErase(void);
int NAPI_SecClear(void);
int NAPI_SecGetVer(uchar * pszVerInfoOut);
int NAPI_SecGetRandom(int nRandLen , void *pvRandom);
int NAPI_SecGetTamperStatus(int *pnStatus);
int NAPI_SecVppTpInit(uchar *num_btn, uchar *func_key, uchar *out_seq);
int NAPI_SecKlaMKLDAuth(int nLenAuthData, uchar *psAuthData, int nLenAuthCert, uchar *psAuthCert, int nLenEncCert, uchar *psEncCert, int *pnLenSsKeyCyTxt, uchar *psSsKeyCyTxt);
int NAPI_SecKlaMKLDAuthV2(int nLenAuthData, uchar *psAuthData, int nLenAuthCert, uchar *psAuthCert, int nLenEncCert, uchar *psEncCert, int *pnLenSsKeyCyTxt, uchar *psSsKeyCyTxt, uchar *keyowner, int *ownerlen);
int NAPI_SecGetDrySR(int *pnVal);
int NAPI_SecKlaGenNonce(int nLenRandom,  uchar* psRandom);
int NAPI_SecGetKcv(uchar ucKeyType, uchar ucKeyIdx, ST_SEC_KCV_DATA *pstKcvInfoOut);



#endif	/* KSSL_H 	*/

