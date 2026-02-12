package com.newland.nsdk.core.api.external.setting;

/**
 * The keys of external setting items.
 */
public class ExtSettings {

    /**
     * product model (read-only)
     */
    public static final String RO_BUILD_MODEL = "ro.build.model";

    /**
     * boot version (read-only)
     */
    public static final String RO_BUILD_BOOT_VERSION = "ro.build.boot_version";

    /**
     * device config version (read-only)
     */
    public static final String RO_BUILD_DEVCFG_VERSION = "ro.build.devcfg_version";

    /**
     * os version (read-only)
     */
    public static final String RO_OS_VERSION = "ro.build.os_version";

    /**
     * napi api version (read-only)
     */
    public static final String RO_NAPI_API_VERSION = "ro.napi_api_version";

    /**
     * napi lib version (read-only)
     */
    public static final String RO_NAPI_LIB_VERSION = "ro.napi_lib_version";

    /**
     * pci firmware version (read-only)
     */
    public static final String RO_PCI_FW_VERSION = "ro.build.pci_fw_version";

    /**
     * pci hardware version (read-only)
     */
    public static final String RO_PCI_HW_VERSION = "ro.build.pci_hw_version";

    /**
     * cpu type (read-only)
     */
    public static final String RO_POS_CPU_TYPE = "ro.pos.cpu_type";

    /**
     * External device's SN (read-only)
     */
    public static final String RO_POS_SN = "ro.pos.SN";

    /**
     * External device's PN (read-only)
     */
    public static final String RO_POS_PN = "ro.pos.PN";

    /**
     * board version (read-only)
     */
    public static final String RO_POS_BOARD_VER = "ro.pos.board_version";

    /**
     * board number (read-only)
     */
    public static final String RO_POS_BOARD_NUM = "ro.pos.board_number";

    /**
     * rfid version (read-only)
     */
    public static final String RO_RFID_VERSION = "ro.rfid.version";

    /**
     * customer id (read-only)
     */
    public static final String RO_POS_CUSTOMERID = "ro.pos.customer_id";

    /**
     * rfid type, for the specific return value,please refer to the below comment of the chip type. (read-only)
     * <ul>
     *     <li>autoscan</li>
     *     <li>rc531</li>
     *     <li>pn512</li>
     *     <li>as3911</li>
     *     <li>rc663</li>
     *     <li>mh1608</li>
     *     <li>fm17550</li>
     *     <li>mh1608d</li>
     *     <li>fm17550x</li>
     *     <li>pn5180</li>
     *     <li>yc5118</li>
     *     <li>thm3070</li>
     *     <li>none</li>
     *     <li>no</li>
     * </ul>
     */
    public static final String RO_RFID_TYPE = "ro.rfid.type";

    /**
     * hardware information, for the specific return value, please refer to the below comment of hardware. (read-only)
     * <ul>
     *     <ul> val[0-1]: wireless module
     *     <li>"FF"    :No wireless module</li>
     *     <li>"02"    :GPRS + MC39</li>
     *     <li>"03"    :GPRS + sim300</li>
     *     <li>"06"    :GPRS + M72</li>
     *     <li>"07"    :GPRS + BGS2</li>
     *     <li>"08"    :GPRS + G610</li>
     *     <li>"09"    :GPRS + G500</li>
     *     <li>"10"    :GPRS + M25</li>
     *     <li>"40"    :GPRS + H330</li>
     *     <li>"43"    :GPRS + EHS6</li>
     *     <li>"64"    :EC20 + C-HC</li>
     *     <li>"65"    :EC20 CE-FDLG</li>
     *     <li>"81"    :CDMA + dtgs800</li>
     *     <li>"82"    :CDMA + dtm228c</li>
     *     <li>"83"    :GPRS + AC35</li>
     *     <li>"85"    :CDMA + CE910</li>
     *     </ul>
     *     <ul> val[2-3]: RF module
     *     <li>"FF"    :No RF module</li>
     *     <li>"01"    :[Linux]RC531    [RTOS]RC531    [SP550]</li>
     *     <li>"02"    :[Linux]PN512    [RTOS]PN512    [SP550]PN512</li>
     *     <li>"03"    :[Linux]AS3911   [RTOS]AS3911   [SP550]AS3911</li>
     *     <li>"04"    :[Linux]RC663    [RTOS]RC663    [SP550]RC663</li>
     *     <li>"05"    :[Linux]MH1608   [RTOS]MH1608   [SP550]MH1608</li>
     *     <li>"06"    :[Linux]FM17550  [RTOS]FM17550  [SP550]FM17550</li>
     *     <li>"07"    :[Linux]PN5180   [RTOS]MH1608D  [SP550]MH1608D</li>
     *     <li>"08"    :[Linux]ST3916   [RTOS]FM17550X [SP550]FM17550X</li>
     *     <li>"09"    :[Linux]FM17660  [RTOS]PN5180   [SP550]</li>
     *     <li>"0A"    :                [RTOS]YC5118   [SP550]YC5118</li>
     *     <li>"0B"    :                               [SP550]YC5018</li>
     *     <li>"0C"    :                               [SP550]FM17660</li>
     *     <li>"0D"    :                               [SP550]ST3916</li>
     *     </ul>
     *     <ul> val[4-5]: magnetic card module
     *     <li>"FF"    :No magnetic card module</li>
     *     <li>"01"    :mesh</li>
     *     <li>"02"    :giga</li>
     *     <li>"03"    :magtek</li>
     *     <li>"04"    :Idtech-dualmag</li>
     *     <li>"10"    :ADC soft decoding</li>
     *     <li>"11"    :GPIO</li>
     *     </ul>
     *     <ul> val[6-7]: Scanning head module
     *     <li>"FF"    :No magnetic card module</li>
     *     <li>"01"    :EM1300</li>
     *     <li>"02"    :EM3000</li>
     *     <li>"03"    :SE955</li>
     *     <li>"04"    :0390/UE966</li>
     *     <li>"05"    :AC35/EM3095</li>
     *     <li>"06"    :EM1365</li>
     *     <li>"07"    :E20390</li>
     *     <li>"08"    :GC0312</li>
     *     <li>"09"    :3682</li>
     *     <li>"80"    :soft decoding 0312</li>
     *     </ul>
     *     <ul> val[8-9]: Whether to support external password keyboard
     *     <li>"FF"    :Not supported</li>
     *     <li>"01"    :Support</li>
     *     </ul>
     *     <ul> val[10-11]: Number of serial ports
     *     <li>"FF"    :None </li>
     *     <li>"01"    :1 </li>
     *     <li>"02"    :2 </li>
     *     </ul>
     *     <ul> val[12-13]: Whether to support USB
     *     <li>"FF"    :No </li>
     *     <li>"01"    :yes </li>
     *     </ul>
     *     <ul> val[14-15]: MODEM module
     *     <li>"FF"    :No </li>
     *     <li>"01"    :yes </li>
     *     </ul>
     *     <ul> val[16-17]: wifi module
     *     <li>"FF"    :No wifi module</li>
     *     <li>"01"    :USI WM-G-MR-09</li>
     *     <li>"02"    :ESP8266</li>
     *     <li>"03"    :AP6181</li>
     *     <li>"04"    :Ap6212</li>
     *     <li>"05"    :AP5236</li>
     *     <li>"06"    :AP6256</li>
     *     <li>"07"    :rtl8821</li>
     *     <li>"08"    :bk7213s</li>
     *     </ul>
     *     <ul> val[18-19]: Whether to support Ethernet
     *     <li>"FF"    :No</li>
     *     <li>"01"    :dm9000</li>
     *     <li>"02"    :bcm589xcore</li>
     *     </ul>
     *     <ul> val[20-21]: print module
     *     <li>"FF": No print module</li>
     *     <li>"01"~"7F": thermal sensitive</li>
     *     <li>"82"~"FE": Stitching</li>
     *     </ul>
     *     <ul> val[22-23]: Whether to support touch screen
     *     <li>"FF"    :No</li>
     *     <li>"01"    :ts_2046</li>
     *     <li>"02"    :589x_ts</li>
     *     <li>"03"    :touch_screen</li>
     *     <li>"82"    :GT5688</li>
     *     </ul>
     *     <ul> val[24-25]: RF LED lamp
     *     <li>"FF"    :No</li>
     *     <li>"01"    :Yes</li>
     *     </ul>
     *     <ul> val[26-27]: Bluetooth
     *     <li>"FF"    :No</li>
     *     <li>"01"    :BM77/SD8787</li>
     *     <li>"02"    :AT24/AP6210</li>
     *     <li>"03"    :AP6210B</li>
     *     <li>"04"    :ALLTEC20706/AP6212</li>
     *     <li>"05"    :YC1021/AP6236</li>
     *     <li>"06"    :TC35661/AP6255</li>
     *     <li>"07"    :20707-A2</li>
     *     <li>"08"    :YC1021S</li>
     *     </ul>
     *     <ul> val[28-29]: NFC
     *     <li>"FF"    :No</li>
     *     <li>"01"    :yes</li>
     *     </ul>
     *     <ul> val[30-31]: national secret chip
     *     <li>"FF"    :No</li>
     *     <li>"01"    :THMK88</li>
     *     <li>"02"    :CCM</li>
     *     </ul>
     *     <ul> val[32-33]: MDB
     *     <li>"FF"    :No</li>
     *     <li>"01"    :5830</li>
     *     <li>"02"    :stm32</li>
     *     <li>"03"    :gd32</li>
     *     </ul>
     *     <ul> val[34-35]: IC
     *     <li>"FF"    :No</li>
     *     <li>"01"    :</li>
     *     <li>"02"    :TDA8035</li>
     *     </ul>
     *     <ul> val[36-37]: SAM
     *     <li>"FF"    :No</li>
     *     <li>"01"    :</li>
     *     <li>"02"    :</li>
     *     </ul>
     *     <ul> val[38-39]: Speaker
     *     <li>"FF"    :No</li>
     *     <li>"01"    :</li>
     *     <li>"02"    :</li>
     *     </ul>
     *     <ul> val[40-41]: SD Card
     *     <li>"FF"    :No</li>
     *     <li>"01"    :</li>
     *     <li>"02"    :</li>
     *     </ul>
     *     <ul> val[42-43]: external reader
     *     <li>"FF"    :No</li>
     *     <li>"01"    :yes</li>
     *     </ul>
     *     <ul> val[44-45]: FM
     *     <li>"FF"    :No</li>
     *     <li>"01"    :</li>
     *     <li>"02"    :mh-1902</li>
     *     </ul>
     *     <ul> val[46-47]: customer display
     *     <li>"FF"    :No</li>
     *     <li>"01"    :yes</li>
     *     </ul>
     * </ul>
     */
    public static final String RO_POS_HW = "ro.pos.HW";

    /**
     * system language
     * <ul>
     *     <li>"0": English</li>
     *     <li>"1": Chinese</li>
     * </ul>
     */
    public static final String PERSIST_SYS_LANGUAGE = "persist.sys.language";

    /**
     * Application autorun
     * <ul>
     *     <li>"0": disable</li>
     *     <li>"1": enable</li>
     * </ul>
     */
    public static final String PERSIST_SYS_AUTORUN = "persist.sys.autorun";

    /**
     * set backlight onoff
     * <ul>
     *     <li>"0": off</li>
     *     <li>"1": on</li>
     *     <li>"2": lock on</li>
     * </ul>
     */
    public static final String PERSIST_SYS_BACKLIGHT_ONOFF = "persist.sys.backlight_onoff";

    /**
     * key volume
     * <ul>
     *     <li>"0": disable</li>
     *     <li>"1": enable</li>
     * </ul>
     */
    public static final String PERSIST_SYS_KEYVOL = "persist.sys.key_vol";

    /**
     * current led color
     * <ul>
     *     <li>"1": single green</li>
     *     <li>"6": colorful</li>
     * </ul>
     */
    public static final String SYS_LED_COLOR = "sys.led.color";

    /**
     * SP Master version(read-only)
     */
    public static final String RO_BUILD_SP_MASTER_VERSION = "ro.build.sp_master_version";

    /**
     * SP MAPP verison(read-only)
     */
    public static final String RO_BUILD_SP_MAPP_VERSION = "ro.build.sp_mapp_version";

    /**
     * Firmware Type(Read-only)
     */
    public static final String RO_BUILD_FW_TYPE = "ro.build.fw_type";

    /**
     * Brightness
     */
    public static final String PERSIST_SYS_BRIGHTNESS = "persist.sys.brightness";

    /**
     * Key BackLight
     */
    public static final String PERSIST_SYS_KEY_BACKLIGHT = "persist.sys.key_backlight";

    /**
     * Whether to disable PIN function(Write-only)
     */
    public static final String PERSIST_SYS_PING_DISABLE = "persist.sys.ping_disable";

    /**
     * Set Comm priority(Write-only)
     */
    public static final String PERSIST_SYS_COMM_PRIORITY = "persist.sys.comm_priority";

    /**
     * The type of auto-connect network to be set(Write-only)
     */
    public static final String PERSIST_SYS_NET_AUTOCONNTYPE = "persist.net.auto_conn_type";

    /**
     * Sets or Gets the device time zone.
     */
    public static final String PERSIST_SYS_TIME_ZONE = "persist.sys.time_zone";

    /**
     * The time format to be set(Write-only)
     */
    public static final String PERSIST_SYS_TIME_FORMAT = "persist.sys.time_format";

    /**
     * The date format to be set(Write-only)
     */
    public static final String PERSIST_SYS_DATE_FORMAT = "persist.sys.date_format";

    /**
     * System power mode
     */
    public static final String SYS_POWER_MODE = "sys.power.mode";

    /**
     * System ethernet DHCP
     */
    public static final String SYS_ETH_DHCP = "sys.eth.dhcp";

    /**
     * System comm priority.
     */
    public static final String SYS_COMM_PRIORITY = "sys.comm.priority";

    /**
     * Sets or Gets the system beeper volume.
     */
    public static final String SYS_BEEP_VOLUME = "sys.beep.volume";

    /**
     * Statistics power run time(Read-only)
     */
    public static final String STATISTICS_POWER_RUN_TIME = "statistics.power_run_time";

    /**
     * Gets the system battery status(Read-only).
     */
    public static final String SYS_BATTERY_STATUS = "sys.battery.status";

    /**
     * Gets the system battery level(Read-only).
     */
    public static final String SYS_BATTERY_LEVEL = "sys.battery.level";
}
