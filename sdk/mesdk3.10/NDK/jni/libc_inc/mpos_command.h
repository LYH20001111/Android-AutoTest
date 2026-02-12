/*
***********************************************************************************************
版权说明：
版本号：
生成日期：
作者：
内容：
功能：
与其它文件的关系：
修改日志：
***********************************************************************************************
*/
#ifndef _MPOS_COMMAND_H_
#define _MPOS_COMMAND_H_
#include "NDK.h"
#include <time.h>
#include "mpos_api_frame.h"

#ifndef FAIL
#define FAIL 		(-1)
#endif

#ifndef SUCC
#define SUCC 		(0)
#endif

#ifndef EXT
#define EXT		extern
#endif



/* ====================================指令集====================================================*/
#define ME_ERR_FALLBACK   (-98)
#define ME_ERR_BROKEN   (-99)


//#define TRUE	1
//#define FALSE	0

#define APP_SUCC 		0
#define APP_FAIL 		-1
#define APP_QUIT             	-2		/*按键取消*/
#define APP_TIMEOUT 	-3
#define APP_CANCLE      	-4		/*指令撤销*/


#define ME_COUNTDOWN_MODE_COM			(1<<0)	/*允许中断去处理串口*/
#define ME_COUNTDOWN_MODE_NOCOUNTDOWN	(1<<1)	/*不显示倒计时*/

#define CONFIG_MAGIC_ME31APP  		"NLMAG000"			//魔数，配置文件格式修改时请修改后3位

//#define SAFETY_TRIGGER						1      //安全触发判断提示,为1是是正常版本，为0时是维修版本
//#define MPOS_EN_DEBUG				0		//调试信息开关
#define ME31_EN_MKINPUT			0
#define LCD_START_YPOS				16
//#define SESSION_KEY_INDEX			100		//会话密钥索引，不可被其他工作密钥使用
#define SECPIN_KEY_INDEX			254		//分散后的PIN秘钥索引
#define SECMAC_KEY_INDEX			253		//分散后的MAC秘钥索引
#define SECTRA_KEY_INDEX			252		//分散后的磁道秘钥索引



#define 	PIN_KSN_FILE			"/appfs/pinksn.in"
#define 	TRACK_KSN_FILE			"/appfs/trackksn.in"
#define 	PBOC_KSN_FILE			"/appfs/pbocksn.in"
#define 	KSN_FILE				"/appfs/ksn.in"		/*KSN号*/
#define 	SN_FILE					"/appfs/yssn.in"		/*设备序列号CSN*/
#define 	UMS_INDEX_FILE			"/appfs/umsindex"
#define ME_MIMA_FILE				"/appfs/extpinpad.in"
#define ME_TLV_FILE				"/appfs/Terminal.in"
#define ME_TLV_FILE_BAK			"/appfs/Termbak.in"
#define ME_APP_FILE				"/appfs/app.in"
#define ME_CONF_FILE				"/appfs/mConfig.in"
#define IM81_TAMPER_FILE			"/appfs/Tamper.in"
#define IM81_FIRST_START			"/appfs/FirstStart"
#define SETCONF_FILE				"/appfs/SetConf.sys"	//系统文件，存配置信息

#define 	REVERSAL_FILE			"/appfs/reversal.in"	/*55域冲正信息*/
#define 	REVERSAL_TC_FILE		"/appfs/revertc.in"		/*TC 冲正信息*/
#define 	REVERSAL_SPR_FILE		"/appfs/reverspr.in"	/*脚本 冲正信息*/


/* ====================================返回码====================================================*/


/* ====================================串口操作接口====================================================*/
//#define ME_Default_COM_Port			0
//#define ME_Default_COM_BPS			115200
//#define ME_Default_COM_TimeOut		1
//#define ME_Default_COM_Flap			0
#define ME_MAX_Host_Command			2048	/* 支持最大的接收长度 */


typedef struct {
	int offset;
	int datalen;
	unsigned char* pu1DataBuf; 
}ME_DATA_RF_CELL,*PME_DATA_RF_CELL;



/* ====================================报文格式相关接口====================================================*/	
#define MPOS_STX				0x02 									/* 帧头 */
#define MPOS_ETX				0x03									/* 帧尾 */
	
	
#define MPOS_STX_OFFSET		0 										/* 帧头 */
#define MPOS_STX_SIZE			0x01									/* 帧头 */
	
#define MPOS_LEN_OFFSET		(MPOS_STX_OFFSET+MPOS_STX_SIZE)		/*数据长度偏移量*/
#define MPOS_LEN_SIZE			0x02									/*数据长度偏移量*/
	
	
#define MPOS_COMMAND_OFFSET		(MPOS_LEN_OFFSET+MPOS_LEN_SIZE)		/*命令码偏移量*/
#define MPOS_COMMAND_SIZE			0x02									/*命令码偏移量*/
	
#define MPOS_SEP_OFFSET		(MPOS_COMMAND_OFFSET+MPOS_COMMAND_SIZE)		/*分割符号*/
#define MPOS_SEP_SIZE			0x01									/*分割符号偏移量*/
	
#define MPOS_SN_OFFSET			(MPOS_SEP_OFFSET+MPOS_SEP_SIZE)		/*序列号偏移量*/
#define MPOS_SN_SIZE			0x01									/*序列号偏移量*/
	
#define MPOS_DATA_OFFSET		(MPOS_SN_OFFSET+MPOS_SN_SIZE)		/*可变数据偏移量*/
	
#define MPOS_ERRCODE_OFFSET		(MPOS_SN_OFFSET+MPOS_SN_SIZE)		/*响应码偏移量*/
#define MPOS_ERRCODE_SIZER	0x02			/*响应码长度*/
	
#define RESPOND_DATA_OFFSET		(MPOS_ERRCODE_OFFSET+MPOS_ERRCODE_SIZER)		/*响应报文可变数据偏移量*/

#define MPOS_ETX_SIZE			0x01			/* 帧尾长度 */

#if 0
/* ====================================报文格式相关接口====================================================*/
#define MPOS_STX				0x02 									/* 帧头 */
#define MPOS_STX_OFFSET			0 										/* 帧头 */
#define MPOS_STX_SIZE			0x01									/* 帧头 */

#define MPOS_LEN_OFFSET			(MPOS_STX_OFFSET+MPOS_STX_SIZE)			/*数据长度偏移量*/
#define MPOS_LEN_SIZE			0x02									/*数据长度偏移量*/


#define MPOS_CMD_OFFSET			(MPOS_LEN_OFFSET+MPOS_LEN_SIZE)			/*命令码偏移量*/
#define MPOS_CMD_SIZE			0x02									/*命令码偏移量*/

#define MPOS_SEP_OFFSET			(MPOS_CMD_OFFSET+MPOS_CMD_SIZE)			/*分割符号*/
#define MPOS_SEP_SIZE			0x01									/*分割符号偏移量*/

#define MPOS_SN_OFFSET			(MPOS_SEP_OFFSET+MPOS_SEP_SIZE)			/*序列号偏移量*/
#define MPOS_SN_SIZE			0x01									/*序列号偏移量*/

#define MPOS_ETX				0x03									/* 帧尾 */


#define MPOS_VARIABLE_OFFSET    (MPOS_SN_OFFSET+MPOS_SN_SIZE)			/*数据偏移量*/
#define MPOS_VARIABLE_READ(SRC) nlMpos_Command.mpos_readlen(SRC+MPOS_LEN_OFFSET, _VAR_BIT16)

#define MPOS_EXT_OFFSET				12

#define MPOS_VARIABLE_MINLEN		(4)		//(MPOS_CMD_SIZE+MPOS_SEP_SIZE+MPOS_SN_SIZE)		//可变数据的最小长度
#endif
/* ====================================大端小端相关接口====================================================*/
#define BIG_ENDIAN						1
#define LITTLE_ENDIAN					0

#define ENDIAN_MODE						1
#define BCD_MODE						0



#define _VAR_BIT8        				1       
#define _VAR_BIT16       	 			2
#define _VAR_BIT24       	 			3
#define _VAR_BIT32        				4

#define _VAR_WRITE(addr,data,size) 		if(size == _VAR_BIT8)\
												*((unsigned char*)(addr)) = (unsigned char)(data) ;\
											else if(size == _VAR_BIT16)\
												*((unsigned short*)(addr)) = (unsigned short)(data) ;\
											else \
												*((unsigned long*)(addr)) = (unsigned long)(data)

#define _VAR_READ(addr,size) 			((size == _VAR_BIT8) ?  *((unsigned char*)(addr)) :\
											((size == _VAR_BIT16) ? *((unsigned short*)(addr)) :\
											*((unsigned long*)(addr))))

#define Endian_Swab16(x)   				((((unsigned short)(x) & 0xff00) >> 8)|(((unsigned short)(x) & 0x00ff) << 8)) 
#define Endian_Swab32(x)   				((( (unsigned long)(x) & 0xff000000) >> 24) | \
											(( (unsigned long)(x) & 0x00ff0000) >> 8) | \
											(( (unsigned long)(x) & 0x0000ff00) << 8) | \
											(( (unsigned long)(x) & 0x000000ff) << 24)) 


//#define _BCD2INT(n)						(((((n) >> 4) & 0x0F) * 10) + ((n) & 0x0F))
//#define _INT2BCD(n)						((((n) / 10) << 4) | ((n) % 10))


/* ====================================结构体定义====================================================*/



/*设备信息，存储设备应用编号、应用程序版本等信息*/
typedef struct {
	uchar							DevicePersonalStatus ;			/**< 设备个人化状态	 	0xFF：出厂默认状态 	0x00：个人化完成 */
	uchar 							DeviceRersved[16];				/**< 应用版本。留着感觉也没什么用，不够用的时候可以把它去掉??????*/
	uchar 							DeviceAppSN[10];				/**< 设备应用编号（UDID）	*/
	uchar							DeviceWorkStatus;				/**< 设备状态信息		0x00：无源待工状态	0x01：无源工作状态 	0x10: 有源设备待工状态 	0x11  有源设备工作状态 */
	uchar							DeviceValidity[4];				/**< 设备有效期	YYMM */
	uchar 							DeviceAdditionalInfo[20];	    /**< 设备附加信息 */
	uchar 							PublicKeySerialNumber[5];		/**< 公钥序号 5字节 */
	uchar 							PublicKey[1024>>3];				/**< 敏感信息（磁道）加密公钥（1024）	密钥长度为1024比特 */
	uchar							PublicKeyValidity[6];			/**< 公钥有效期	YYMMDD */
	uchar							AuthenticationKey[16];			/**< 认证密钥	对称16字节 */
	uchar							MagneticTrackPubKeySerNum[5];	/**< 磁道公钥序号 5字节 */
	uchar 							PINPublicKeySerialNumber[5];	/**< PIN公钥序号（预留） */
}DEVICE_DATA;		//1+16+10+1+4+20+5+128+6+16+5+5=217

/*不需要存储的配置信息*/
typedef struct
{
	unsigned char u1MposSn;					    /*当前指令包序列号*/
	unsigned char u1MposCmd[2]; 	/*当前指令包密令码*/
	unsigned char u1Swiped;						/*Mag Card Swiped*/
	unsigned char u1RandCode[8];				/*当前随机码*/
	unsigned char uszWaterNo[12];				//流水号
	char cCardType;								//当前操作的卡类型
	unsigned char u1PanCode[20];				/*上次主账户号*/
	unsigned int  m_u4MAClen; 					/* MAC数据 长度 */
	unsigned char m_u1fonttype;				    /*字体类型*/
	EM_PRN_MODE   m_PrnMode;				    /* 字体设置 */
	int ucKey;
	int autorun;
	int u1SecTamperStatus; 
	char cIsDebug;  
	unsigned char u1KeyExt[12];				   /* 用来填充ISO9564-0格式的PINBLOCK明文*/
	unsigned char u1StartFlag;		           /* 升级固件中的开始位*/
	unsigned int  u4MagCardTime;                /* 刷卡后用来显示卡号的时间*/
}ME_TPEDCTL;	/*PED控制结构*/


/*旧格式的配置文件，大小固定为256,已不用，升级新版本时强制转化为新格式的配置文件*/
typedef struct
{
    unsigned char u1BTConfigFlag;            /*作为配置区初始化标志使用 0x00初始化过；其他：未被初始化*/
	unsigned char u1CommuMode;				/*通信方式:0  usb  1蓝牙 2普通串口*/
	DEVICE_DATA   ME_TDeviceData;
	unsigned char u1SwitchFlag;				//是否允许终端参数设置
	/*需要添加配置信息请在这里添加，同时更改u1Rerseved大小*/
	unsigned char  uszRerseved[256-5-sizeof(unsigned short)-sizeof(unsigned short)-sizeof(DEVICE_DATA)];
	unsigned short u1StandByTime;		//进入待机的时间
	unsigned short u1SleepTime;			//进入休眠的时间
	unsigned char  uszCrcValue[2];
}ME31_OLDCONFIG_T;	


/*新格式的配置文件*/
typedef struct {
    unsigned char	magic[8];           //"NLMAG000"
    unsigned char	u1AppOwner;         //应用程序归属厂商(1 富友、2 青岛银商、3 海尔/海科、4、其他)
    unsigned char	u1CommMode;         //通信方式: 0 无（串口） 、1 USB、 2 蓝牙、4 音频
    unsigned short	u1StandByTime;	    //进入待机的时间
    unsigned short	u1SleepTime;		//进入休眠的时间
    char  szRersved[16];                //保留
    char  szAdminPassword[16];          //管理员密码
    char  szKlaPassword[16];            //KLA程序密码
    char  szAppExitPassword[16];        //应用程序退出密码 
    DEVICE_DATA   ME_TDeviceData;       //设备信息
    unsigned char u1RersevedNo;         //uszRersved数组的大小 
    unsigned char  uszRersved[0];       //后续使用
}ME31_CONFIG_T;



/*系统参数*/
typedef struct{
	unsigned long language 		:1;                 // 1 english, 0 chinese 
	unsigned long beepvolumn 	:1;                 // 按键音量 0无：1开
	unsigned long autorun 		:1;                 // =1 运行 mapp
	unsigned long lcd_blswitch 	:1;             	// 背光开关  =0关闭  =1打开
	unsigned long mag_type		:2;					// 00= giga 01 = mesh
	unsigned long unuse 		:26;                // no use
}t_setCfgbit;


/*使用PosTime模拟的一个定时器*/
typedef struct {
    unsigned int timestamp;
    unsigned int  lastinc;
    unsigned int  end;
	unsigned int  flag;
}ME_TIMEOUT_T;

typedef enum {
	SYS_HWINFO_GET_POS_TYPE_ME=0,      		/**<取pos机器类型   			*/
	SYS_HWINFO_GET_HARDWARE_INFO_ME,       /**<获取POS机上所支持硬件类型，详细返回值如上所述*/
	SYS_HWINFO_GET_BIOS_VER_ME,        		/**<取bios版本信息 			 */
	SYS_HWINFO_GET_POS_USN_ME,        		/**<取机器序列号    		*/
	SYS_HWINFO_GET_POS_PSN_ME,        		/**<取机器机器号    		*/
	SYS_HWINFO_GET_BOARD_VER_ME,       		/**<取主板号        			*/
	SYS_HWINFO_GET_CREDITCARD_COUNT_ME,		/**<取pos刷卡总数					*/
	SYS_HWINFO_GET_PRN_LEN_ME,				/**<取pos打印总长度    		*/
	SYS_HWINFO_GET_POS_RUNTIME_ME,          /**<取pos机开机运行时间  */
	SYS_HWINFO_GET_KEY_COUNT_ME,            /**<取pos机按键次数  */
	SYS_HWINFO_GET_CPU_TYPE_ME,           /**<取pos机cpu类型  */
	SYS_HWINFO_GET_BOOT_VER_ME,
	SYS_HWINFO_GET_PIN_KSN=100,			/*PIN KSN (银商总公司)*/	//到100就没人和我抢了吧!!!!!!!!!
	SYS_HWINFO_GET_TRACK_KSN,			/*TRACK KSN (银商总公司)*/	
	SYS_HWINFO_GET_PBOC_KSN,			/*PBOC KSN (银商总公司)*/	
	SYS_HWINFO_GET_KSN,					/*ALL KSN (银商总公司)*/	
	SYS_HWINFO_GET_CSN,					/*CSN (银商总公司)*/
	SYS_HWINFO_GET_PRODUCE_SN,			/*生产SN*/	
	SYS_HWINFO_GET_VID,					/*厂家ID*/	
	SYS_HWINFO_GET_KCV,					/*检测密钥是否存在*/	
	SYS_HWINFO_GET_TLVSWITCH,			/*终端参数读写开关,参数读写开关关闭时无法写终端号和商户号(青岛银商需求)*/
	SYS_HWINFO_GET_TLVSWITCH1,			/*终端参数读写开关,参数读写开关关闭时无法写终端号和商户号(青岛银商双终端需求)*/	

} EM_SYS_HWINFO_ME;

typedef enum {
		ME_SYS_ACTION_SWIPED_START=0,      		/*刷卡开始*/
		ME_SYS_ACTION_SWIPED_SUCC,				/*刷卡成功*/
		ME_SYS_ACTION_SWIPED_FAIL,				/*刷卡失败*/
		ME_SYS_ACTION_SWIPED_CHECK,				/*刷卡二磁道检测*/        
		ME_SYS_ACTION_SWIPED_TIMEOUT,			/*刷卡超时*/	
		ME_SYS_ACTION_PIN_CANCEL,				 /*输密码超时*/	
		ME_SYS_ACTION_PIN_SUCC,				     /*输密成功*/
		ME_SYS_ACTION_PIN_FAIL,				     /*输密失败*/
		ME_SYS_ACTION_PIN_TIMEOUT,				 /*输密码超时*/	
		ME_SYS_ACTION_PBOC_SUCC,				/*pboc成功*/
		ME_SYS_ACTION_PBOC_CANCEL,
		ME_SYS_ACTION_PBOC_FAIL,
		ME_SYS_ACTION_LCD_STABAR,				/*状态栏*/
		ME_SYS_ACTION_PRN_START,				/*打印开始*/
		ME_SYS_ACTION_PRN_FINISH,				/*打印结束*/
		ME_SYS_ACTION_CHECK_MAGIC,				/*检测mag 是否支持IC*/ 
		ME_SYS_ACTION_QPBOC_SUCC,
		ME_SYS_ACTION_QPBOC_FAIL,
		ME_SYS_ACTION_QPBOC_CANCEL,
		ME_SYS_ACTION_PAN_CHECK,			   /*检测mag 是否支持IC*/
		
		ME_SYS_ACTION_PAN_TIMEOUT,			   /*检测mag 是否支持IC*/
} EM_SYS_ACTION_ME;

typedef struct
{
	unsigned char uMposSn;
	unsigned char uMposCmd[2];
	unsigned char uFrame[4096];
	uint uFrameLen;
	uint time_use;
	struct timeval start;
	struct timeval end;
	int scmd;
	int busy;	//用于一问多答指令，为0表示正常，为1表示疑问多答指令还未正常退出
	char aidsel;	//aid 选择标识
	int datalen;
	char aidlist[300];
	char choosedAid[40];
	int len;
	char uKey[50];
	int time;
}Param_Sved;

/* ====================================帧发送接收相关接口====================================================*/

extern Param_Sved Me_Param;
extern void ME31_PEDMain(void);
extern ME_TPEDCTL g_METPEDCtl;	/*PED控制结构*/
extern int me_printf(const char * format,...);
extern int ME_SysSetPosInfo(EM_SYS_HWINFO_ME emFlag,char * psBuf, int len);
extern int ME_SysGetPosInfo(EM_SYS_HWINFO_ME emFlag,uint * punLen,char * psBuf);
extern int ME_SysCountdown(unsigned char ucMode,int nTimeout,int nStartY,int(* application)(void));
//extern int ME_SecIncreaseKsn(unsigned char type);
extern int ME_SecIncreaseKsn(unsigned char type, unsigned char incremental);
extern int ME_FsWrite(const char * pszName,const unsigned char * psBuffer,uint unOffset,uint unLength);
extern int ME_FsRead(const char * pszName,unsigned char * psBuffer,uint unOffset,uint unLength);
extern int ME_SecKsnInit();



#if 0 /*判断用户RAM空间是否越界*/
#define ME_AssertRAM(p)	((((void *)(p) >= (void *)USR_RAM_BASE)&&((void *)(p) <= (void *)USR_RAM_LIMIT) || ((void *)(p) >= (void *)USR_ROM_BASE)&&((void *)(p) <= (void *)USR_ROM_LIMIT))?0:1)
#else
#define ME_AssertRAM(p)	0
#endif

#endif
