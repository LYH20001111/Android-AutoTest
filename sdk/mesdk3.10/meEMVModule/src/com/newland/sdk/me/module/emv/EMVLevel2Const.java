package com.newland.sdk.me.module.emv;

import java.util.HashSet;
import java.util.Set;

public class EMVLevel2Const {
    public static final class InnerEmvTransType {
        public static final byte EMV_TRANS_GOODS = 0x01;
        /**
         * < 货物
         */
        public static final byte EMV_TRANS_SERVICES = 0x02;
        /**
         * < 服务
         */
        public static final byte EMV_TRANS_CASH = 0x03;
        /**
         * < 现金
         */
        public static final byte EMV_TRANS_CASHBACK = 0x04;
        /**
         * < 返现
         */
        public static final byte EMV_TRANS_INQUIRY = 0x05;
        /**
         * < 查询
         */
        public static final byte EMV_TRANS_TRANFER = 0x06;
        /**
         * < 转账
         */
        public static final byte EMV_TRANS_ADMIN = 0x07;
        /**
         * < 管理
         */
        public static final byte EMV_TRANS_CASHDEPOSIT = 0x08;
        /**
         * < 存款
         */
        public static final byte EMV_TRANS_PAYMENT = 0x09;
        /**
         * < 支付
         */
        public static final byte EMV_TRANS_PBOCLOG = 0x0A;
        /**
         * < 获取PBOC 或电子现金日志 (当EMV_Start()函数的返回值为 　 EMV_TRANS_GOON_PBOC2LOG时，才表示获
         * 　 取日志成功，否则获取日志失败)
         */
        public static final byte EMV_TRANS_SALE = 0x0B;
        /**
         * < 消费
         */
        public static final byte EMV_TRANS_PREAUTH = 0x0C;
        /**
         * < 预授权
         */
        public static final byte EMV_TRANS_BALANCE = 0x0D;
        /**
         * < 余额
         */
        public static final byte EMV_TRANS_ECLOADLOG = 0x0E;
        /**
         * < 电子现金圈存日志
         */

        public static final byte EMV_TRANS_EC_GOODS = EMV_TRANS_GOODS;
        /**
         * < 电子现金货物
         */
        public static final byte EMV_TRANS_EC_SERVICES = EMV_TRANS_SERVICES;
        /**
         * < 电子现金服务
         */
        public static final byte EMV_TRANS_EC_SALE = EMV_TRANS_SALE;
        /**
         * < 电子现金消费
         */
        public static final byte EMV_TRANS_EC_BINDLOAD = 0x21;
        /**
         * < 电子现金指定账户圈存
         */
        public static final byte EMV_TRANS_EC_NOBINDLOAD = 0x22;
        /**
         * < 电子现金非指定账户圈存
         */
        public static final byte EMV_TRANS_EC_CASHLOAD = 0x23;
        /**
         * < 电子现金现金圈存
         */
        public static final byte EMV_TRANS_EC_UPLOAD = 0x24;
        /**
         * < 电子现金圈提(暂未实现)
         */
        public static final byte EMV_TRANS_EC_INQUIRE_LOG = EMV_TRANS_PBOCLOG;
        /**
         * < 电子现金日志(和PBOC日志一样)
         */
        public static final byte EMV_TRANS_EC_INQUIRE_AMOUNT = 0x25;
        /**
         * < 电子现金余额查询 　 (当EMV_start( )函数返回为: 　 TRANS_EC_GOON_AMOUNT, 才表示获
         * 取余额成功，否则失败)
         */
        public static final byte EMV_TRANS_EC_CASHLOAD_VOID = 0x26;
        /**
         * < 电子现金现金圈存撤销
         */
        public static final byte EMV_TRANS_RF_START = 0x30;
        /**
         * < *<只做标识不做交易类型
         */
        public static final byte EMV_TRANS_RF_GOODS = EMV_TRANS_GOODS;
        /**
         * < QPBOC/MSD货物
         */
        public static final byte EMV_TRANS_RF_SERVICES = EMV_TRANS_SERVICES;
        /**
         * < QPBOC/MSD服务
         */
        public static final byte EMV_TRANS_RF_SALE = EMV_TRANS_SALE;
        /**
         * < QPBOC/MSD消费
         */
        public static final byte EMV_TRANS_RF_BINDLOAD = 0x31;
        /**
         * < 非接指定账户圈存
         */
        public static final byte EMV_TRANS_RF_NOBINDLOAD = 0x32;
        /**
         * < 非接现金非指定账户圈存
         */
        public static final byte EMV_TRANS_RF_CASHLOAD = 0x33;
        /**
         * < 非接现金现金圈存
         */
        public static final byte EMV_TRANS_RF_INQUIRE_AMOUNT = 0x34;
        /**
         * < QPBOC余额查询 　 (当EMV_rf_start( )函数返回为: 　 TRANS_RF_GOON_AMOUNT, 才表示获
         * 取余额成功(并可以通过EMV_getdata获取 9F77电子现金余额上限)，否则失败)
         */
        public static final byte EMV_TRANS_RF_UPLOAD = 0x35;
        /**
         * < 非接现金圈提(暂未实现)
         */
        public static final byte EMV_TRANS_RF_CASHLOAD_VOID = 0x36;
        /**
         * < 非接现金现金圈存撤销
         */
        public static final byte EMV_TRANS_RF_PBOCLOG = 0x37;
        /**
         * < 非接取PBOC明细
         */
        public static final byte EMV_TRANS_RF_UPTCARDINFO = 0x38;
        /**
         * < 卡片信息写入
         */
        public static final byte EMV_TRANS_RF_PBOC_SALE = 0x39;
        public static final byte EMV_TRANS_RF_ECLOADLOG = 0x40;
        /** < 非接取圈存明细 */
    }

    public static final class EmvPinInputType {
        public static final int EMV_OFFLINEPIN_INPUT = 1; /* 脱机密码 */
        public static final int EMV_OFFLINE_ONLY_INPUT = 2; /* 最后一次脱机密码 */
        public static final int EMV_ONLINEPIN_INPUT = 3; /* 联机密码 */
        public static final int EC_ONLINEPIN_INPUT = 11; /* 电子现金阈值超出联机密码 */
    }

    public static final class PinEntryRslt {
        public static final int INPUT_FAILED = -1;
        public static final int BYPASS = -2;
        public static final int INTERRUPTED_OR_TIMEOUT = -3;
    }

    /**
     * < AID操作错误值定义
     */
    public static final class AIDOperatorError {

        public static final int AIDERR_BASE = (-5000);
        /**
         * < 文件打开错误
         */
        public static final int AIDERR_FILEOPEN = (AIDERR_BASE - 1);
        /**
         * < 写文件错误
         */
        public static final int AIDERR_FILEWRITE = (AIDERR_BASE - 2);
        /**
         * < 读文件错误
         */
        public static final int AIDERR_FILEREAD = (AIDERR_BASE - 3);
        /**
         * < 公钥checksum错误
         */
        public static final int AIDERR_CHKSUM = (AIDERR_BASE - 4);
        /**
         * < 未找到此AID
         */
        public static final int AIDERR_LOST = (AIDERR_BASE - 5);
        /**
         * < 参数错误
         */
        public static final int AIDERR_PARAM = (AIDERR_BASE - 6);
        /**
         * < 文件长度错误
         */
        public static final int AIDERR_FILELEN = (AIDERR_BASE - 7);
        /**
         * < 更新终端配置参数时同步更新AID相应数据失败
         */
        public static final int AIDERR_UPTAID = (AIDERR_BASE - 8);

    }

    /**
     * < AID操作mode
     */
    public static final class AIDOperatorModel {
        /**
         * < 删除一个AID
         */
        public static final int AID_RMV = (0x01);
        /**
         * < 更新一个AID若不存在则新增一个
         */
        public static final int AID_UPT = (0x02);
        /**
         * < 获取一个AID
         */
        public static final int AID_GET = (0x10);
        /**
         * < 读取终端配置参数
         */
        public static final int AID_CONFIG_R = (0x20);
        /**
         * < 写入终端配置参数
         */
        public static final int AID_CONFIG_W = (0x40);
        /**
         * < 清空全部AID参数(不影响终端配置参数)
         */
        public static final int AID_CLR = (0x80);
        /**
         * < 清空全部AID参数(终端配置参数一并清空)
         */
        public static final int AID_RESET = (0x04);

    }

    public static final class PBOCLOG_OPERATOR {
        public static final int PBOCLOG_RECNUM = -2;
        public static final int PBOCLOG_SFI = -1;
        public static final int PBOCLOG_FMT = 0;
    }

    public static final class CAPKOperatorModel {
        /**
         * < 公钥删除
         */
        public static final int CAPK_RMV = (0x01);
        /**
         * < 公钥更新若不存在则新增一个
         */
        public static final int CAPK_UPT = (0x02);
        /**
         * < 公钥去激活
         */
        public static final int CAPK_DIS = (0x04);
        /**
         * < 公钥激活
         */
        public static final int CAPK_ENB = (0x08);
        /**
         * < 公钥获取
         */
        public static final int CAPK_GET = (0x10);
        /**
         * < 公钥清空
         */
        public static final int CAPK_CLR = (0x20);

    }

    /**
     * < emv_opt._trans_ret && emv_opt._online_result= (交易结果返回值);
     */
    public static final class EmvExecRslt {
        /**
         * < 交易取消
         */
        public static final int EMV_TRANS_CANCEL = (-13);
        /**
         * < 未出示卡片
         */
        public static final int EMV_TRANS_NOCARD = (-12);
        /**
         * < 多张卡
         */
        public static final int EMV_TRANS_MORECARD = (-11);
        /**
         * < fallback
         */
        public static final int EMV_TRANS_FALLBACK = (-2);
        /**
         * < 交易中止
         */
        public static final int EMV_TRANS_TERMINATE = (-1);
        /**
         * < 交易授受
         */
        public static final int EMV_TRANS_ACCEPT = (1);
        /**
         * < 交易拒绝
         */
        public static final int EMV_TRANS_DENIAL = (2);
        /**
         * < 联机
         */
        public static final int EMV_TRANS_GOONLINE = (3);
        /**
         * < 第二个Generate AC返回AAC 二次授权失败后的拒绝
         */
        public static final int EMV_TRANS_2GAC_AAC = (4);
        /**
         * < emv_opt._online_result联机失败
         */
        public static final int EMV_TRANS_ONLINEFAIL = (5);
        /**
         * < emv_opt._online_result联机成功并授受交易
         */
        public static final int EMV_TRANS_ONLINESUCC_ACCEPT = (6);
        /**
         * < emv_opt._online_result联机成功并拒绝参考
         */
        public static final int EMV_TRANS_ONLINESUCC_DENIAL = (7);
        /**
         * < emv_opt._online_result联机成功并返回参考
         */
        public static final int EMV_TRANS_ONLINESUCC_ISSREF = (8);
        /**
         * < 成功获取PBOC2日志
         */
        public static final int EMV_TRANS_GOON_PBOC2LOG = (9);
        /**
         * < 成功获取圈存日志
         */
        public static final int EMV_TRANS_GOON_ECLOADLOG = (10);
        //public static final int  EMV_TRANS_EC_GOON_LOG             = (11);          /**< 返回电子现金日志和PBOC2日志相同 */
        /**
         * < 成功获取EC余额
         */
        public static final int EMV_TRANS_EC_GOON_AMOUNT = (12);
        /**
         * < 非接触QPBOC交易接受
         */
        public static final int EMV_TRANS_QPBOC_ACCEPT = (13);
        /**
         * < 非接触QPBOC交易拒绝
         */
        public static final int EMV_TRANS_QPBOC_DENIAL = (14);
        /**
         * < 非接触QPBOC交易联机
         */
        public static final int EMV_TRANS_QPBOC_GOONLINE = (15);
        /**
         * < 非接触MSD交易联机
         */
        public static final int EMV_TRANS_MSD_GOONLINE = (16);
        /**
         * < 成功获取QPBOC余额
         */
        public static final int EMV_TRANS_RF_GOON_AMOUNT = (17);
        /**
         * < 请激活射频卡片
         */
        public static final int EMV_TRANS_RF_ACTIVECARD = (18);
        /**
         * < 请求下一个AID
         */
        public static final int EMV_TRANS_SLECT_NEXTAID = (19);
        /**
         * < 预处理输入金额超出限额
         */
        public static final int EMV_TRANS_AMT_LIMITOVER = (-2105);
        /**
         * < pboc交易继续
         */
        public static final int EMV_TRANS_PBOC_CONTINUE = (65281);
        /**
         * < qpboc交易继续
         */
        public static final int EMV_TRANS_QPBOC_CONTINUE = (65282);
        /**
         * < msd交易继续
         */
        public static final int EMV_TRANS_MSD_CONTINUE = (65283);

//		//--------------self-define 由于ep定义与国内定义冲突，此处对ep状态进行转换
////		public static final int EMV_TRANS_RF_MCHIP_ACCEPT = 11;        /*RF M/CHIP transaction succ */
//		public static final int EMV_TRANS_RF_MCHIP_ACCEPT=111;
////		public static final int EMV_TRANS_RF_MCHIP_DENIAL = 12;        /*RF M/CHIP transaction denial */
//		public static final int EMV_TRANS_RF_MCHIP_DENIAL = 112; 
////		public static final int EMV_TRANS_RF_MCHIP_GOONLINE = 13;        /*RF M/CHIP transaction go online */
//		public static final int EMV_TRANS_RF_MCHIP_GOONLINE = 113; 
    }

    /**
     * < (交易结果错误码)
     */
    public static final class EmvExecErrorCode {
        /**
         * < 读重点配置失败
         */
        public static final int READ_CONFIG_FAILURE = (1);
        /**
         * < 读终端列表失败
         */
        public static final int READ_TERMINAL_LIST = (2);
        /**
         * < IC卡无法上电
         */
        public static final int IC_POWERON_FAILURE = (3);
        /**
         * < IC卡不支持的指令
         */
        public static final int IC_UNSUPPORT_CMD = (4);
        /**
         * < 应用锁定
         */
        public static final int APP_LOCK = (5);
        /**
         * < 找不到支持的应用
         */
        public static final int NOT_SUPPORT_APP = (6);
        /**
         * < 放弃交易
         */
        public static final int GIVEUP_TRANS = (7);
        /**
         * < 应用选择失败
         */
        public static final int AID_CHOOSE_FAILURE = (8);
        /**
         * < 应用初始化失败
         */
        public static final int AID_INIT_FAILURE = (9);
        /**
         * < 读应用数据失败
         */
        public static final int READ_AID_DATA_FAILUER = (10);
        /**
         * < 脱机数据认证失败
         */
        public static final int OFFLINE_DATA_AUTHENTICATION_FAILURE = (11);
        /**
         * < 处理限制失败
         */
        public static final int MANAGER_LIMIT_FAILURE = (12);
        /**
         * < 持卡人认证失败
         */
        public static final int CARDHOLDER_AUTHENTICATION_FAILURE = (13);
        /**
         * < 终端风险管理失败
         */
        public static final int TERMINAL_RISK_MANAGER_FAILURE = (14);
        /**
         * < 终端行为分析失败
         */
        public static final int TERMINAL_BEHAIVOR_ANALY_FAILURE = (15);
        /**
         * < 不支持的服务
         */
        public static final int UNSUPPORT_SERVICE = (16);
        /**
         * < 无随机数
         */
        public static final int NOT_RANDOM_NUMBER = (17);
        /**
         * < 卡片锁定
         */
        public static final int CARD_LOCK = (18);
        /**
         * < 电子现金拒绝
         */
        public static final int E_CASH_REFUSE = (1415);
        /**
         * < GPO返回错误
         */
        public static final int GPO_RETURN_ERROR = (1416);
        /**
         * < 非接应用不支持
         */
        public static final int RF_AID_UNSUPPORT = (1440);
        /**
         * <纯电子现金卡不支持联机
         */
        public static final int PURE_E_CASH_CARD_UNSUPPORT_ONLINE = (1441);
        /**
         * <纯电子现金卡不支持CVM
         */
        public static final int PURE_E_CASH_CARD_UNSUPPORT_CVM = (1442);
        /**
         * < 检测到磁条卡
         */
        public static final int DETECT_MAGNETIC_CARD = (1443);
        /**
         * < 纯电子现金卡无授权响应码
         */
        public static final int PURE_E_CASH_CARD_NOT_AUTH_RESPONSE_CODE = (1521);
        /**
         * <纯电子现金卡余额读取失败
         */
        public static final int PURE_E_CASH_CARD_READ_BALANCE_FAILURE = (1522);
        /**
         * <卡片已过期
         */
        public static final int EXPIRE_CARD = (1531);
        /**
         * < 卡片未生效
         */
        public static final int CARD_NOT_EFFECT = (1532);
        /**
         * < 电子现金余额不足
         */
        public static final int E_CASH_BALANCE_NOT_ENOUGH = (1822);
        /**
         * <纯电子现金卡要求EC联机则拒绝
         */
        public static final int PURE_E_CASH_CARD_NEED_EC_ONLINE_REFUSE = (1823);
        /**
         * <EC余额<交易金额+阈值,转联机
         */
        public static final int EC_BALANCE = (1824);

        /**
         * < 脚本超限
         */
        public static final int SCRIPT_OVERRUN = (1901);
        /**
         * < EC脚本空
         */
        public static final int EC_SCRIPT_NULL = (1902);
        /**
         * <EC圈存金额超出限额
         */
        public static final int EC_DEPOSIT_AMOUNT_BEYOND_LIMIT = (1903);
        /**
         * <脚本执行错误
         */
        public static final int SCRIPT_EXEC_FAILURE = (1904);
        /**
         * <脚本错误
         */
        public static final int SCRIPT_ERROR = (1905);

        /**
         * < 预处理参数文件错误
         */
        public static final int PRE_PARA_FILE_ERROR = (2101);
        /**
         * < 预处理输入金额用户退出
         */
        public static final int PRE_INPUT_AMOUNT_USER_EXIT = (2102);
        /**
         * < 预处理输入金额超时
         */
        public static final int PRE_INPUT_AMOUNT_TIMEOUT = (2103);
        /**
         * < 预处理输入金额失败
         */
        public static final int PRE_INPUT_AMOUNT_FAILURE = (2104);
        /**
         * < 预处理输入金额超出限额
         */
        public static final int PRE_INPUT_AMOUNT_BEYOND_LIMIT = (2105);
        /**
         * < 预处理要求联机,终端不能联机
         */
        public static final int PRE_REQUIRE_ONLINE_FAILURE = (2106);
        /**
         * < 射频卡去卡失败
         */
        public static final int RF_CARD_FAILURE = (2111);
        /**
         * < 卡片返回错误
         */
        public static final int CARD_RETURN_FAILURE = (2112);
        /**
         * <读应用数据失败
         */
        public static final int READ_AID_DATA_FAILURE = (2113);
        /**
         * < 卡片黑名单
         */
        public static final int CARD_BLACKLIST = (2114);
        /**
         * < 卡片未生效
         */
        public static final int CARD_NO_EFFECT = (2115);
        /**
         * < 卡片已失效
         */
        public static final int CARD_LOSE_EFFECT = (2116);
        /**
         * < 卡片数据认证失败
         */
        public static final int CARD_DATA_AUTHENTICATION_FAILURE = (2117);
        /**
         * < 卡片二磁等价数据失败
         */
        public static final int CARD_TWO_MAGNETIC_DATA_FAILURE = (2118);
        /**
         * < 频度检查超限
         */
        public static final int FREQUENCY_CHECK_OVERRUN = (2119);
        /**
         * < 纯电子现金卡余额不足
         */
        public static final int PURE_E_CASH_CARD_BACLANCE_NO_ENOUGH = (2120);
        /**
         * < 卡片拒绝
         */
        public static final int CARD_REFUSE = (2121);
        /**
         * < 卡片AIP没有数据认证
         */
        public static final int CARD_AIP_NOT_DATA_AUTH = (2122);
        /**
         * < 卡片9F10中返回交易结果错误
         */
        public static final int CARD_9F10_RETURN_FAILURE = (2123);
    }

    public enum ICCardGetDataType {
        ATC,
        LASTONLINE_ATC,
        PWD_RETRY,
        LOG_FMT,
        EC_BALANCE,
        EC_RESET_THRESHOLD,
        RF_BALANCE,
        EC_BALANCE_LIMIT,
        EC_SINGLE_AMOUNT_LIMIT,
        EC_LOAD_LOG_FMT,
        EC_BALANCE_SEC,
        EC_APPCURR_CODE,
        EC_SECCURR_CODE,
        GETDATA_TOTAL
    }

    public static final Set<Integer> TAGS_PBOC_FIELD55 = new HashSet<Integer>();

    static {
        TAGS_PBOC_FIELD55.add(0x9F26);
        TAGS_PBOC_FIELD55.add(0x9F27);
        TAGS_PBOC_FIELD55.add(0x9F10);
        TAGS_PBOC_FIELD55.add(0x9F37);
        TAGS_PBOC_FIELD55.add(0x9F36);
        TAGS_PBOC_FIELD55.add(0x95);
        TAGS_PBOC_FIELD55.add(0x9A);
        TAGS_PBOC_FIELD55.add(0x9C);
        TAGS_PBOC_FIELD55.add(0x9F02);
        TAGS_PBOC_FIELD55.add(0x5F2A);
        TAGS_PBOC_FIELD55.add(0x82);
        TAGS_PBOC_FIELD55.add(0x9F1A);
        TAGS_PBOC_FIELD55.add(0x9F03);
        TAGS_PBOC_FIELD55.add(0x9F33);
        TAGS_PBOC_FIELD55.add(0x9F34);
        TAGS_PBOC_FIELD55.add(0x9F35);
        TAGS_PBOC_FIELD55.add(0x9F1E);
        TAGS_PBOC_FIELD55.add(0x84);
        TAGS_PBOC_FIELD55.add(0x9F09);
        TAGS_PBOC_FIELD55.add(0x9F41);
    }

    public static final Set<Integer> TAGS_TC = new HashSet<Integer>();

    static {
        TAGS_TC.add(0x9F27);
        TAGS_TC.add(0x9F36);
        TAGS_TC.add(0x9f37);
        TAGS_TC.add(0x95);
        TAGS_TC.add(0x9a);
        TAGS_TC.add(0x9c);
    }

    public static final Set<Integer> TAGS_SCRIPT_TEMPLETE = new HashSet<Integer>();

    static {
        TAGS_SCRIPT_TEMPLETE.add(11);
        TAGS_SCRIPT_TEMPLETE.add(0x9F26);
        TAGS_SCRIPT_TEMPLETE.add(0x9F10);
        TAGS_SCRIPT_TEMPLETE.add(0x9F37);
        TAGS_SCRIPT_TEMPLETE.add(0x9F36);
        TAGS_SCRIPT_TEMPLETE.add(0x95);
        TAGS_SCRIPT_TEMPLETE.add(0x9A);
        TAGS_SCRIPT_TEMPLETE.add(0x82);
        TAGS_SCRIPT_TEMPLETE.add(0x9f1a);
        TAGS_SCRIPT_TEMPLETE.add(0x9f33);
        TAGS_SCRIPT_TEMPLETE.add(0x9F1E);
        TAGS_SCRIPT_TEMPLETE.add(0xDF31);
    }

    /**
     * TC : Terminal Capabilities(emvparam._cap); 定义终端性能
     */
    /**
     * 手工键盘输入
     */
    public static final int TC_Manual_Key_Entry = (0x0080);
    /**
     * 磁条卡
     */
    public static final int TC_Magnetic_Stripe = (0x0040);
    /**
     * 接触式IC卡
     */
    public static final int TC_IC_With_Contacts = (0x0020);
    /**
     * 明文PIN验证
     */
    public static final int TC_Plaintext_PIN = (0x0180);
    /**
     * 联机密文PIN验证
     */
    public static final int TC_Enciphered_PIN_Online = (0x0140);
    /**
     * 签名(纸质);
     */
    public static final int TC_Signature_Paper = (0x0120);
    /**
     * 脱机密文PIN验证
     */
    public static final int TC_Enciphered_PIN_Offline = (0x0110);
    /**
     * 无需CVM
     */
    public static final int TC_No_CVM_Required = (0x0108);
    /**
     * 持卡人证件出示
     */
    public static final int TC_Cardholder_Cert = (0x0101);
    /**
     * 静态数据认证SDA
     */
    public static final int TC_SDA = (0x0280);
    /**
     * 动态数据认证DDA
     */
    public static final int TC_DDA = (0x0240);
    /**
     * 吞卡
     */
    public static final int TC_Card_Capture = (0x0220);
    /**
     * 复合动态数据认证/应用密文生成CDA
     */
    public static final int TC_CDA = (0x0208);

    //伊朗发行卡,自定义AID:A00000080340001  kernelid:0x8A068261 使用pure的内核
    //KAHROBA:这个名称是客户暂定的.
    public static final int KERNEL_ID_IRAN_KAHROBA = 0x8A068261;

    public static final int KERNEL_ID_GIRO = 0x2A;//Girocard
}
