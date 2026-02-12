#ifndef _MPOS_COMMAND_H_
#define _MPOS_COMMAND_H_
#include <time.h>
#include "ndk.h"
#ifndef FAIL
#define FAIL        (-1)
#endif

#ifndef SUCC
#define SUCC        (0)
#endif

#ifndef EXT
#define EXT        extern
#endif

#define APP_SUCC           0
#define APP_FAIL          -1

#define SECPIN_KEY_INDEX            254        //分散后的PIN秘钥索引
#define SECMAC_KEY_INDEX            253        //分散后的MAC秘钥索引
#define SECTRA_KEY_INDEX            252        //分散后的磁道秘钥索引

#define PIN_KSN_FILE                "/appfs/pinksn.in"
#define TRACK_KSN_FILE              "/appfs/trackksn.in"
#define PBOC_KSN_FILE               "/appfs/pbocksn.in"
#define KSN_FILE                    "/appfs/ksn.in"
#define SN_FILE                     "/appfs/yssn.in"
#define UMS_INDEX_FILE              "/appfs/umsindex"
#define ME_APP_FILE                 "/appfs/app.in"
#define ME_TLV_FILE				    "/appfs/Terminal.in"

/*设备信息，存储设备应用编号、应用程序版本等信息*/
typedef struct {
    uchar DevicePersonalStatus;              /**< 设备个人化状态  0xFF：出厂默认状态 	0x00：个人化完成 */
    uchar DeviceRersved[16];                 /**< 应用版本。留着感觉也没什么用，不够用的时候可以把它去掉??????*/
    uchar DeviceAppSN[10];                   /**< 设备应用编号（UDID）	*/
    uchar DeviceWorkStatus;                  /**< 设备状态信息		0x00：无源待工状态	0x01：无源工作状态 	0x10: 有源设备待工状态 	0x11  有源设备工作状态 */
    uchar DeviceValidity[4];                 /**< 设备有效期	YYMM */
    uchar DeviceAdditionalInfo[20];          /**< 设备附加信息 */
    uchar PublicKeySerialNumber[5];          /**< 公钥序号 5字节 */
    uchar PublicKey[1024 >> 3];              /**< 敏感信息（磁道）加密公钥（1024）	密钥长度为1024比特 */
    uchar PublicKeyValidity[6];              /**< 公钥有效期	YYMMDD */
    uchar AuthenticationKey[16];             /**< 认证密钥	对称16字节 */
    uchar MagneticTrackPubKeySerNum[5];      /**< 磁道公钥序号 5字节 */
    uchar PINPublicKeySerialNumber[5];       /**< PIN公钥序号（预留） */
} DEVICE_DATA; //1+16+10+1+4+20+5+128+6+16+5+5=217

/*不需要存储的配置信息*/
typedef struct {
    unsigned char u1MposSn;                  /*当前指令包序列号*/
    unsigned char u1MposCmd[2];              /*当前指令包密令码*/
    unsigned char u1Swiped;                  /*Mag Card Swiped*/
    unsigned char u1RandCode[8];             /*当前随机码*/
    unsigned char uszWaterNo[12];            //流水号
    char cCardType;                          //当前操作的卡类型
    unsigned char u1PanCode[20];             /*上次主账户号*/
    unsigned int m_u4MAClen;                 /* MAC数据 长度 */
    unsigned char m_u1fonttype;              /*字体类型*/
    EM_PRN_MODE m_PrnMode;                   /* 字体设置 */
    int ucKey;
    int autorun;
    int u1SecTamperStatus;
    char cIsDebug;
    unsigned char u1KeyExt[12];               /* 用来填充ISO9564-0格式的PINBLOCK明文*/
    unsigned char u1StartFlag;                /* 升级固件中的开始位*/
    unsigned int u4MagCardTime;               /* 刷卡后用来显示卡号的时间*/
} ME_TPEDCTL;    /*PED控制结构*/

/*新格式的配置文件*/
typedef struct {
    unsigned char magic[8];                   //"NLMAG000"
    unsigned char u1AppOwner;                 //应用程序归属厂商(1 富友、2 青岛银商、3 海尔/海科、4、其他)
    unsigned char u1CommMode;                 //通信方式: 0 无（串口） 、1 USB、 2 蓝牙、4 音频
    unsigned short u1StandByTime;             //进入待机的时间
    unsigned short u1SleepTime;               //进入休眠的时间
    char szRersved[16];                       //保留
    char szAdminPassword[16];                 //管理员密码
    char szKlaPassword[16];                   //KLA程序密码
    char szAppExitPassword[16];               //应用程序退出密码
    DEVICE_DATA ME_TDeviceData;               //设备信息
    unsigned char u1RersevedNo;               //uszRersved数组的大小
    unsigned char uszRersved[0];              //后续使用
} ME31_CONFIG_T;

typedef enum {
    SYS_HWINFO_GET_POS_TYPE_ME = 0,            /**<取pos机器类型   			*/
    SYS_HWINFO_GET_HARDWARE_INFO_ME,           /**<获取POS机上所支持硬件类型，详细返回值如上所述*/
    SYS_HWINFO_GET_BIOS_VER_ME,                /**<取bios版本信息 			 */
    SYS_HWINFO_GET_POS_USN_ME,                 /**<取机器序列号    		*/
    SYS_HWINFO_GET_POS_PSN_ME,                 /**<取机器机器号    		*/
    SYS_HWINFO_GET_BOARD_VER_ME,               /**<取主板号        			*/
    SYS_HWINFO_GET_CREDITCARD_COUNT_ME,        /**<取pos刷卡总数					*/
    SYS_HWINFO_GET_PRN_LEN_ME,                 /**<取pos打印总长度    		*/
    SYS_HWINFO_GET_POS_RUNTIME_ME,             /**<取pos机开机运行时间  */
    SYS_HWINFO_GET_KEY_COUNT_ME,               /**<取pos机按键次数  */
    SYS_HWINFO_GET_CPU_TYPE_ME,                /**<取pos机cpu类型  */
    SYS_HWINFO_GET_BOOT_VER_ME,
    SYS_HWINFO_GET_PIN_KSN = 100,              /*PIN KSN (银商总公司)*/    //到100就没人和我抢了吧!!!!!!!!!
    SYS_HWINFO_GET_TRACK_KSN,                  /*TRACK KSN (银商总公司)*/
    SYS_HWINFO_GET_PBOC_KSN,                   /*PBOC KSN (银商总公司)*/
    SYS_HWINFO_GET_KSN,                        /*ALL KSN (银商总公司)*/
    SYS_HWINFO_GET_CSN,                        /*CSN (银商总公司)*/
    SYS_HWINFO_GET_PRODUCE_SN,                 /*生产SN*/
    SYS_HWINFO_GET_VID,                        /*厂家ID*/
    SYS_HWINFO_GET_KCV,                        /*检测密钥是否存在*/
    SYS_HWINFO_GET_TLVSWITCH,                  /*终端参数读写开关,参数读写开关关闭时无法写终端号和商户号(青岛银商需求)*/
    SYS_HWINFO_GET_TLVSWITCH1,                 /*终端参数读写开关,参数读写开关关闭时无法写终端号和商户号(青岛银商双终端需求)*/
} EM_SYS_HWINFO_ME;
#endif

