package com.newland.nsdk.core.api.common;

/**
 * NSDK error code.
 */
public class ErrorCode {
    /**
     * <b>[0]</b> OK.
     */
    public static final int OK = 0;

    /**
     * <b>[-1]</b> General error.
     */
    public static final int ERROR = -1;

    /**
     * <b>[-4]</b> Failed to open device.
     */
    public static final int OPEN_DEV_ERROR = -4;

    /**
     * <b>[-5]</b> Failed to call driver.
     */
    public static final int IOCTL_ERROR = -5;

    /**
     * <b>[-6]</b> Invalid parameter.
     */
    public static final int PARAM_ERROR = -6;

    /**
     * <b>[-7]</b> Invalid file path.
     */
    public static final int PATH_ERROR = -7;

    /**
     * <b>[-8]</b> Failed to decode image.
     */
    public static final int DECODE_IMAGE_ERROR = -8;

    /**
     * <b>[-9]</b> Out of memory
     */
    public static final int MACLLOC_ERROR = -9;

    /**
     * <b>[-10]</b> Timeout.
     */
    public static final int TIMEOUT = -10;

    /**
     * <b>[-11]</b> Cancelled.
     */
    public static final int CANCELLED = -11;

    /**
     * <b>[-12]</b> Failed to write into file.
     */
    public static final int WRITE_ERROR = -12;

    /**
     * <b>[-13]</b> Failed to read from file.
     */
    public static final int READ_ERROR = -13;

    /**
     * <b>[-15]</b> Buffer overflow.
     */
    public static final int OVERFLOW = -15;

    /**
     * <b>[-17]</b> Device not available.
     */
    public static final int NO_DEVICES = -17;

    /**
     * <b>[-18]</b> Not supported.
     */
    public static final int NOT_SUPPORTED = -18;

    /**
     * <b>[-31]</b> Mag track data format error.
     */
    public static final int TRACK_FORMAT_ERROR = -31;

    /**
     * <b>[-32]</b> Mag track status error.
     */
    public static final int TRACK_STATUS_ERROR = -32;

    /**
     * <b>[-50]</b> No mag card swiped.
     */
    public static final int NO_CARD_SWIPED = -50;

    /**
     * <b>[-51]</b> Invalid mag card data.
     */
    public static final int SWIPED_DATA_ERROR = -51;

    /**
     * <b>[-201]</b> No SIM card.
     */
    public static final int NO_SIM_CARD = -201;

    /**
     * <b>[-202]</b> Wrong SIM card password.
     */
    public static final int PIN_ERROR = -202;

    /**
     * <b>[-203]</b> SIM card locked.
     */
    public static final int PIN_LOCKED = -203;

    /**
     * <b>[-204]</b> Undefined SIM card error.
     */
    public static final int PIN_UNDEFINED = -204;

    /**
     * <b>[-601]</b> Failed to write.
     */
    public static final int ICC_WRITE_ERROR = -601;

    /**
     * <b>[-602]</b> Failed to copy kernel data.
     */
    public static final int ICC_COPY_ERROR = -602;

    /**
     * <b>[-603]</b> Failed to power up.
     */
    public static final int ICC_POWER_UP_ERROR = -603;

    /**
     * <b>[-604]</b> Command error.
     */
    public static final int ICC_COMMAND_ERROR = -604;

    /**
     * <b>[-605]</b> Card is pulled out.
     */
    public static final int ICC_CARD_PULL_ERROR = -605;

    /**
     * <b>[-606]</b> Card not ready.
     */
    public static final int ICC_CARD_NOT_READY = -606;

    /**
     * <b>[-1000]</b> Unknown error.
     */
    public static final int SECP_BASE_ERROR = -1000;

    /**
     * <b>[-1001]</b> Get key value timeout.
     */
    public static final int SECP_TIMEOUT = SECP_BASE_ERROR - 1;

    /**
     * <b>[-1002]</b> Invalid parameter.
     */
    public static final int SECP_PARAM_ERROR = SECP_BASE_ERROR - 2;

    /**
     * <b>[-1003]</b> DBUS communication error.
     */
    public static final int SECP_DBUS_ERROR = SECP_BASE_ERROR - 3;

    /**
     * <b>[-1004]</b> Out of memory.
     */
    public static final int SECP_MALLOC_ERROR = SECP_BASE_ERROR - 4;

    /**
     * <b>[-1005]</b> Failed to open security device.
     */
    public static final int SECP_OPEN_SEC_ERROR = SECP_BASE_ERROR - 5;

    /**
     * <b>[-1006]</b> Failed to call driver function.
     */
    public static final int SECP_SEC_DRIVER_ERROR = SECP_BASE_ERROR - 6;

    /**
     * <b>[-1007]</b> Failed to get random number.
     */
    public static final int SECP_GET_RANDOM_ERROR = SECP_BASE_ERROR - 7;

    /**
     * <b>[-1008]</b> Failed to get key value.
     */
    public static final int SECP_GET_KEY_ERROR = SECP_BASE_ERROR - 8;

    /**
     * <b>[-1009]</b> KCV check error.
     */
    public static final int SECP_KCV_CHECK_ERROR = SECP_BASE_ERROR - 9;

    /**
     * <b>[-1010]</b> Failed to get caller info.
     */
    public static final int SECP_GET_CALLER_ERROR = SECP_BASE_ERROR - 10;

    /**
     * <b>[-1011]</b> Overrun.
     */
    public static final int SECP_OVERRUN = SECP_BASE_ERROR - 11;

    /**
     * <b>[-1012]</b> Operation not allowed.
     */
    public static final int SECP_NO_PERMISSION = SECP_BASE_ERROR - 12;

    /**
     * <b>[-1013]</b> Tamper detected.
     */
    public static final int SECP_TAMPER = SECP_BASE_ERROR - 13;

    /**
     * <b>[-1014]</b> The feature is not supported.
     */
    public static final int SECP_UNSUPPORTED = SECP_BASE_ERROR - 14;

    /**
     * <b>[-1100]</b> Unknown error.
     */
    public static final int SECVP_BASE_ERROR = -1100;

    /**
     * <b>[-1101]</b> Get key value timeout.
     */
    public static final int SECVP_TIMEOUT = SECVP_BASE_ERROR - 1;

    /**
     * <b>[-1102]</b> Invalid parameter.
     */
    public static final int SECVP_PARAM_ERROR = SECVP_BASE_ERROR - 2;

    /**
     * <b>[-1103]</b> DBUS communication error.
     */
    public static final int SECVP_DBUS_ERROR = SECVP_BASE_ERROR - 3;

    /**
     * <b>[-1104]</b> Failed to open event device.
     */
    public static final int SECVP_OPEN_EVENT0_ERROR = SECVP_BASE_ERROR - 4;

    /**
     * <b>[-1105]</b> Scanning value out of range.
     */
    public static final int SECVP_SCAN_VALUE_ERROR = SECVP_BASE_ERROR - 5;

    /**
     * <b>[-1106]</b> Failed to open random number device.
     */
    public static final int SECVP_OPEN_RANDOM_ERROR = SECVP_BASE_ERROR - 6;

    /**
     * <b>[-1107]</b> Failed to get random number.
     */
    public static final int SECVP_GET_RANDOM_ERROR = SECVP_BASE_ERROR - 7;

    /**
     * <b>[-1108]</b> User cancelled.
     */
    public static final int SECVP_GET_ESC = SECVP_BASE_ERROR - 8;

    /**
     * <b>[-1109]</b> The feature is not supported.
     */
    public static final int SECVP_UNSUPPORTED = SECVP_BASE_ERROR - 9;

    /**
     * <b>[1120]</b> Unknown error.
     */
    public static final int SECVP_VPP_BASE_ERROR = -1120;

    /**
     * <b>[-1121]</b> VPP not activated.
     */
    public static final int SECVP_VPP_NOT_ACTIVATED = SECVP_VPP_BASE_ERROR - 1;

    /**
     *<b>[-1122]</b>  VPP initialization timeout.
     */
    public static final int SECVP_VPP_TIMEOUT = SECVP_VPP_BASE_ERROR - 2;

    /**
     * <b>[-1123]</b> Failed to encrypt.
     */
    public static final int SECVP_VPP_ENCRYPT_ERROR = SECVP_VPP_BASE_ERROR - 3;

    /**
     * <b>[-1124]</b> Buffer full.
     */
    public static final int SECVP_VPP_BUFFER_FULL = SECVP_VPP_BASE_ERROR - 4;

    /**
     * <b>[-1125]</b> Data key pressed, echo "*".
     */
    public static final int SECVP_VPP_PIN_KEY_ERROR = SECVP_VPP_BASE_ERROR - 5;

    /**
     * <b>[-1126]</b> Enter key pressed, process PIN.
     */
    public static final int SECVP_VPP_ENTER_KEY_PRESSED = SECVP_VPP_BASE_ERROR - 6;

    /**
     * <b>[-1127]</b> Backspace key pressed.
     */
    public static final int SECVP_VPP_BACKSPACE_KEY_PRESSED = SECVP_VPP_BASE_ERROR - 7;

    /**
     * <b>[-1128]</b> Clear key pressed, remove all the '*'.
     */
    public static final int SECVP_VPP_CLEAR_KEY_PRESSED = SECVP_VPP_BASE_ERROR - 8;

    /**
     * <b>[-1129]</b> Cancel key pressed.
     */
    public static final int SECVP_VPP_CANCEL_KEY_PRESSED = SECVP_VPP_BASE_ERROR - 9;

    /**
     * <b>[-1130]</b> Internal error.
     */
    public static final int SECVP_VPP_GENERAL_ERROR = SECVP_VPP_BASE_ERROR - 10;

    /**
     * <b>[-1131]</b> Card removed.
     */
    public static final int SECVP_VPP_CUSTOMER_CARD_NOT_PRESENT = SECVP_VPP_BASE_ERROR - 11;

    /**
     * <b>[-1132]</b> Failed to access smart card.
     */
    public static final int SECVP_VPP_HTC_CARD_ERROR = SECVP_VPP_BASE_ERROR - 12;

    /**
     * <b>[-1133]</b> Wrong password, try again.
     */
    public static final int SECVP_VPP_WRONG_PIN_LAST_TRY = SECVP_VPP_BASE_ERROR - 13;

    /**
     * <b>[-1134]</b> Try last time.
     */
    public static final int SECVP_VPP_WRONG_PIN = SECVP_VPP_BASE_ERROR - 14;

    /**
     * <b>[-1135]</b> Try too many times.
     */
    public static final int SECVP_VPP_ICC_ERROR = SECVP_VPP_BASE_ERROR - 15;

    /**
     * <b>[-1136]</b> PIN verification is successful, but PIN length is zero.
     */
    public static final int SECVP_VPP_PIN_BYPASS = SECVP_VPP_BASE_ERROR - 16;

    /**
     * <b>[-1137]</b> Fatal error.
     */
    public static final int SECVP_VPP_ICC_FAILURE = SECVP_VPP_BASE_ERROR - 17;

    /**
     * <b>[-1138]</b> Response is not 90, 00.
     */
    public static final int SECVP_VPP_GETCHALLENGE_BAD = SECVP_VPP_BASE_ERROR - 18;

    /**
     * <b>[-1139]</b> Invalid response length.
     */
    public static final int SECVP_VPP_GETCHALLENGE_NOT8 = SECVP_VPP_BASE_ERROR - 19;

    /**
     * <b>[-1140]</b> PIN attack timer activated.
     */
    public static final int SECVP_VPP_PIN_ATTACK_TIMER = SECVP_VPP_BASE_ERROR - 20;

    /**
     * <b>[-1141]</b> PIN too short.
     */
    public static final int SECVP_VPP_PIN_TOO_SHORT = SECVP_VPP_BASE_ERROR - 21;

    /**
     * <b>[-1200]</b> Unknown error.
     */
    public static final int SECCR_BASE_ERROR = -1200;

    /**
     * <b>[-1201]</b> Get key value timeout.
     */
    public static final int SECCR_TIMEOUT = SECCR_BASE_ERROR - 1;

    /**
     * <b>[-1202]</b> Invalid parameter.
     */
    public static final int SECCR_PARAM_ERROR = SECCR_BASE_ERROR - 2;

    /**
     * <b>[-1203]</b> DBUS communication error.
     */
    public static final int SECCR_DBUS_ERROR = SECCR_BASE_ERROR - 3;

    /**
     * <b>[-1204]</b> Out of memory.
     */
    public static final int SECCR_MALLOC_ERROR = SECCR_BASE_ERROR - 4;

    /**
     * <b>[-1205]</b> Failed to open random number device.
     */
    public static final int SECCR_OPEN_RANDOM_ERROR = SECCR_BASE_ERROR - 5;

    /**
     * <b>[-1206]</b> Failed to call driver function.
     */
    public static final int SECCR_DRIVER_ERROR = SECCR_BASE_ERROR - 6;

    /**
     * <b>[-1207]</b> Wrong key type.
     */
    public static final int SECCR_KEY_TYPE_ERROR = SECCR_BASE_ERROR - 7;

    /**
     * <b>[-1208]</b> Wrong key length.
     */
    public static final int SECCR_KEY_LEN_ERROR = SECCR_BASE_ERROR - 8;

    /**
     * <b>[-1209]</b> Failed to get key.
     */
    public static final int SECCR_GET_KEY_ERROR = SECCR_BASE_ERROR - 9;

    /**
     * <b>[-1300]</b> Unknown error.
     */
    public static final int SECKM_BASE_ERROR = -1300;

    /**
     * <b>[-1301]</b> Get key value timeout.
     */
    public static final int SECKM_TIMEOUT = SECKM_BASE_ERROR - 1;

    /**
     * <b>[-1302]</b> Invalid parameter.
     */
    public static final int SECKM_PARAM_ERROR = SECKM_BASE_ERROR - 2;

    /**
     * <b>[-1303]</b> DBUS communication error.
     */
    public static final int SECKM_DBUS_ERROR = SECKM_BASE_ERROR - 3;

    /**
     * <b>[-1304]</b> Out of memory.
     */
    public static final int SECKM_MALLOC_ERROR = SECKM_BASE_ERROR - 4;

    /**
     * <b>[-1305]</b> Failed to open database.
     */
    public static final int SECKM_OPEN_DATABASE_ERROR = SECKM_BASE_ERROR - 5;

    /**
     * <b>[-1306]</b> Failed to delete database.
     */
    public static final int SECKM_DELETE_DATABASE_ERROR = SECKM_BASE_ERROR - 6;

    /**
     * <b>[-1307]</b> Failed to delete record.
     */
    public static final int SECKM_DELETE_RECORD_ERROR = SECKM_BASE_ERROR - 7;

    /**
     * <b>[-1308]</b> Failed to install key record.
     */
    public static final int SECKM_INSTALL_RECORD_ERROR = SECKM_BASE_ERROR - 8;

    /**
     * <b>[-1309]</b> Failed to read key record.
     */
    public static final int SECKM_READ_RECORD_ERROR = SECKM_BASE_ERROR - 9;

    /**
     * <b>[-1310]</b> Operation not allowed.
     */
    public static final int SECKM_OPTION_NOT_ALLOWED = SECKM_BASE_ERROR - 10;

    /**
     * <b>[-1311]</b> MAC error.
     */
    public static final int SECKM_KEY_MAC_ERROR = SECKM_BASE_ERROR - 11;

    /**
     * <b>[-1312]</b> Wrong key type.
     */
    public static final int SECKM_KEY_TYPE_ERROR = SECKM_BASE_ERROR - 12;

    /**
     * <b>[-1313]</b> Wrong key architecture.
     */
    public static final int SECKM_KEY_ARCHITECTURE_ERROR = SECKM_BASE_ERROR - 13;

    /**
     * <b>[-1314]</b> Wrong key length.
     */
    public static final int SECKM_KEY_LEN_ERROR = SECKM_BASE_ERROR - 14;

    /**
     * <b>[-1315]</b> System unknown error
     */
    public static final int SECKM_SYS_ERROR = SECKM_BASE_ERROR - 15;

    /**
     * <b>[-1316]</b> The feature is not supported.
     */
    public static final int SECKM_UNSUPPORTED = SECKM_BASE_ERROR - 16;

    /**
     * <b>[-1317]</b> The key is used.
     */
    public static final int SECKM_KEY_ALREADY_USED = SECKM_BASE_ERROR - 17;

    /**
     * <b>[-1318]</b> KCV calculation error.
     */
    public static final int SECKM_CALCULATE_KCV_ERROR = SECKM_BASE_ERROR - 18;

    /**
     * <b>[-1319]</b> Asym random generation process busy.
     */
    public static final int SECKM_ASYM_GENERATE_BUSY = SECKM_BASE_ERROR - 19;

    /**
     * <b>[-1320]</b> Failed to init asym key.
     */
    public static final int SECKM_ASYM_GENERATE_INIT = SECKM_BASE_ERROR - 20;

    /**
     * <b>[-1321]</b> Failed to generate asym key.
     */
    public static final int SECKM_ASYM_GENERATE_PROCESSING = SECKM_BASE_ERROR - 21;

    /**
     * <b>[-1400]</b> Unknown error.
     */
    public static final int SECKS_BASE_ERROR = -1400;

    /**
     * <b>[-1401]</b> Get key value timeout.
     */
    public static final int SECKS_TIMEOUT = SECKS_BASE_ERROR - 1;

    /**
     * <b>[-1402]</b> Invalid parameter.
     */
    public static final int SECKS_PARAM_ERROR = SECKS_BASE_ERROR - 2;

    /**
     * <b>[-1500]</b> KLA base error.
     */
    public static final int SECKLA_BASE_ERROR = -1500;

    /**
     * <b>[-1501]</b> Unspecified internal error.
     */
    public static final int SECKLA_INTERNAL_ERROR = SECKLA_BASE_ERROR - 1;

    /**
     * <b>[-1502]</b> Invalid parameter.
     */
    public static final int SECKLA_PARAM_ERROR = SECKLA_BASE_ERROR - 2;

    /**
     * <b>[-1503]</b> Invalid certificate.
     */
    public static final int SECKLA_INVALID_CRT = SECKLA_BASE_ERROR - 3;

    /**
     * <b>[-1504]</b> Invalid signature.
     */
    public static final int SECKLA_INVALID_SIG = SECKLA_BASE_ERROR - 4;

    /**
     * <b>[-1505]</b> Key not found.
     */
    public static final int SECKLA_KEY_NOT_FOUND = SECKLA_BASE_ERROR - 5;

    /**
     * <b>[-1506]</b> Invalid use of the key according to the key tag.
     */
    public static final int SECKLA_INVALIDKEY_USAGE = SECKLA_BASE_ERROR - 6;

    /**
     * <b>[-1600]</b> Algorithm base error.
     */
    public static final int SECALG_BASE_ERROR = -1600;

    /**
     * <b>[-1601]</b> Get key value timeout.
     */
    public static final int SECALG_TIMEOUT = SECALG_BASE_ERROR - 1;

    /**
     * <b>[-1602]</b> Invalid parameter.
     */
    public static final int SECALG_PARAM_ERROR = SECALG_BASE_ERROR - 2;

    /**
     * <b>[-1603]</b> Failed to update cipher text.
     */
    public static final int SECALG_UPDATE_ERROR = SECALG_BASE_ERROR - 3;

    /**
     * <b>[-1604]</b> Error occurred when cipher calculation finished.
     */
    public static final int SECALG_FINISH_ERROR = SECALG_BASE_ERROR - 4;

    /**
     * <b>[-1605]</b> Asym calculation error.
     */
    public static final int SECALG_ASYM_CALCULATE_ERROR = SECALG_BASE_ERROR - 5;

    /**
     * <b>[-1606]</b> ECC calculation error.
     */
    public static final int SECALG_ECC_CALCULATE_ERROR = SECALG_BASE_ERROR - 6;

    /**
     * <b>[-1700]</b> Unknown error.
     */
    public static final int SEC_CFG_BASE_ERROR = -1700;

    /**
     * <b>[-1701]</b> Current key table is invalid.
     */
    public static final int SEC_CFG_TABLE = SEC_CFG_BASE_ERROR - 1;

    /**
     * <b>[-1702]</b> The key value is not unique.
     */
    public static final int SEC_CFG_UNIQUE = SEC_CFG_BASE_ERROR - 2;

    /**
     * <b>[-1703]</b> The key is misused according to its type.
     */
    public static final int SEC_CFG_MISUSE = SEC_CFG_BASE_ERROR - 3;

    /**
     * <b>[-1704]</b> Current function is overrun.
     */
    public static final int SEC_CFG_TRIES_LIMIT = SEC_CFG_BASE_ERROR - 4;

    /**
     * <b>[-1705]</b> Key is not protected by the same or higher strength key.
     */
    public static final int SEC_CFG_STRENGTH = SEC_CFG_BASE_ERROR - 5;

    /**
     * <b>[-1706]</b> Key length should be stronger than 8 bytes.
     */
    public static final int SEC_CFG_KEYLEN_LIMIT = SEC_CFG_BASE_ERROR - 6;

    /**
     * <b>[-1707]</b> NO DPA defence.
     */
    public static final int SEC_CFG_DPA_DEFENCE = SEC_CFG_BASE_ERROR - 7;

    /**
     * <b>[-1708]</b> Clear key is not allowed to be installed.
     */
    public static final int SEC_CFG_CLEARKEY_LIMIT = SEC_CFG_BASE_ERROR - 8;

    /**
     * <b>[-1709]</b> The static numerical key layout is not allowed to be in sequence.
     */
    public static final int SEC_CFG_VPP_STATIC_KEY_LAYOUT_LIMIT = SEC_CFG_BASE_ERROR - 9;

    /**
     * <b>[-1710]</b> Symmetric keys is not allowed to be installed by asymmetric keys.
     */
    public static final int SEC_CFG_ASYM_LOADKEY_LIMIT = SEC_CFG_BASE_ERROR - 10;

    /**
     * <b>[-1800]</b> Unknown error.
     */
    public static final int SEC_CSR_BASE_ERROR = -1800;

    /**
     * <b>[-1801]</b> Get key value timeout.
     */
    public static final int SEC_CSR_TIMEOUT = SEC_CSR_BASE_ERROR - 1;

    /**
     * <b>[-1802]</b> Invalid parameter.
     */
    public static final int SEC_CSR_PARAM_ERROR = SEC_CSR_BASE_ERROR - 2;

    /**
     * <b>[-1803]</b> DBUS communication error.
     */
    public static final int SEC_CSR_DBUS_ERROR = SEC_CSR_BASE_ERROR - 3;

    /**
     * <b>[-1804]</b> Out of memory.
     */
    public static final int SEC_CSR_MALLOC_ERROR = SEC_CSR_BASE_ERROR - 4;

    /**
     * <b>[-1805]</b> CSR handle error.
     */
    public static final int SEC_CSR_HANDLE_ERROR = SEC_CSR_BASE_ERROR - 5;

    /**
     * <b>[-1806]</b> mbedtls library operation error.
     */
    public static final int SEC_CSR_WRITE_ERROR = SEC_CSR_BASE_ERROR - 6;

    /**
     * <b>[-1807]</b> CSR handle have not released.
     */
    public static final int SEC_CSR_IN_PROCESS = SEC_CSR_BASE_ERROR - 7;

    /**
     * <b>[-1900]</b> RKI base error.
     */
    public static final int SEC_RKI_BASE = -1900;

    /**
     * <b>[-1901]</b> Timeout.
     */
    public static final int SEC_RKI_TIMEOUT = SEC_RKI_BASE - 1;

    /**
     * <b>[-1902]</b> Parameter error.
     */
    public static final int SEC_RKI_PARAM_ERROR = SEC_RKI_BASE - 2;

    /**
     * <b>[-1903]</b> Failed to back up key database file.
     */
    public static final int SEC_RKI_BACKUP_ERROR = SEC_RKI_BASE - 3;

    /**
     * <b>[-1904]</b> Failed to restore key database file.
     */
    public static final int SEC_RKI_RESTORE_ERROR = SEC_RKI_BASE - 4;

    /**
     * <b>[-1905]</b> Failed to verify certificate.
     */
    public static final int SEC_RKI_VERIFY_ERROR = SEC_RKI_BASE - 5;

    /**
     * <b>[-2005]</b> RF error or not configured.
     */
    public static final int RFID_INITSTA = -2005;

    /**
     * <b>[-2008]</b> No card.
     */
    public static final int RFID_NO_CARD = -2008;

    /**
     * <b>[-2009]</b> Multi cards detected.
     */
    public static final int RFID_MULTI_CARDS = -2009;

    /**
     * <b>[-2010]</b> Failed to seek and activate card.
     */
    public static final int RFID_SEEKING = -2010;

    /**
     * <b>[-2011]</b> Not compliant with ISO1444-4 protocol, e.g. M1 card F.
     */
    public static final int RFID_PROTOCOL_ERROR = -2011;

    /**
     * <b>[-2012]</b> Card type not set.
     */
    public static final int RFID_NOT_PICC_TYPE = -2012;

    /**
     * <b>[-2013]</b> Card not detected
     */
    public static final int RFID_NOT_DETECTED = -2013;

    /**
     * <b>[-2014]</b> Type A card collision (Multiple cards exist).
     */
    public static final int RFID_A_ANTI = -2014;

    /**
     * <b>[-2015]</b> Type A card RATS processing error.
     */
    public static final int RFID_RATS_ERROR = -2015;

    /**
     * <b>[-2016]</b> Failed to activate Type B card.
     */
    public static final int RFID_B_ACTIVATE_ERROR = -2016;

    /**
     * <b>[-2017]</b> Failed to seek type A card (Probably multiple cards exist).
     */
    public static final int RFID_A_SEEK_ERROR = -2017;

    /**
     * <b>[-2018]</b> Failed to seek type B card (Probably multiple cards exist).
     */
    public static final int RFID_B_SEEK_ERROR = -2018;

    /**
     * <b>[-2019]</b> Both type A and B cards exist.
     */
    public static final int RFID_AB_ON = -2019;

    /**
     * <b>[-2020]</b> Already activated.
     */
    public static final int RFID_UPED = -2020;

    /**
     * <b>[-2021]</b> Not activated.
     */
    public static final int RFID_NOT_ACTIVATED = -2021;

    /**
     * <b>[-2022]</b> Type A Card collision.
     */
    public static final int RFID_COLLISION_A = -2022;

    /**
     * <b>[-2023]</b> Type B Card collision.
     */
    public static final int RFID_COLLISION_B = -2023;

    /**
     * <b>[-2027]</b> Felica Card collision.
     */
    public static final int FELICA_COLLISION = -2027;

    /**
     * <b>[-2030]</b> No card.
     */
    public static final int MI_NOTAGERR = -2030;

    /**
     * <b>[-2031]</b> CRC error.
     */
    public static final int MI_CRC_ERR = -2031;

    /**
     * <b>[-2032]</b> Not empty.
     */
    public static final int MI_EMPTY = -2032;

    /**
     * <b>[-2033]</b> Failed to authenticate.
     */
    public static final int MI_AUTH_ERROR = -2033;

    /**
     * <b>[-2034]</b> Parity error.
     */
    public static final int MI_PARITY_ERROR = -2034;

    /**
     * <b>[-2035]</b> Receiving code error.
     */
    public static final int MI_CODE_ERROR = -2035;

    /**
     * <b>[-2036]</b> Anti-collision data check error.
     */
    public static final int MI_SERNR_ERROR = -2036;

    /**
     * <b>[-2037]</b> Authentication key error.
     */
    public static final int MI_KEY_ERROR = -2037;

    /**
     * <b>[-2038]</b> Not authenticated.
     */
    public static final int MI_NOT_AUTH_ERROR = -2038;

    /**
     * <b>[-2039]</b> Failed to receive BIT.
     */
    public static final int MI_BIT_COUNT_ERROR = -2039;

    /**
     * <b>[-2040]</b> Failed to receive byte.
     */
    public static final int MI_BYTE_COUNT_ERROR = -2040;

    /**
     * <b>[-2041]</b> Failed to write FIFO.
     */
    public static final int MI_WRITE_FIFO_ERROR = -2041;

    /**
     * <b>[-2042]</b> Failed to send.
     */
    public static final int MI_TRANS_ERROR = -2042;

    /**
     * <b>[-2043]</b> Failed to write.
     */
    public static final int MI_WRITE_ERROR = -2043;

    /**
     * <b>[-2044]</b> Failed to increase.
     */
    public static final int MI_INCREMENT_ERROR = -2044;

    /**
     * <b>[-2045]</b> Failed to decrement.
     */
    public static final int MI_DECREMENT_ERROR = -2045;

    /**
     * <b>[-2046]</b> Overflow.
     */
    public static final int MI_OVERFLOW = -2046;

    /**
     * <b>[-2047]</b> Frame error.
     */
    public static final int MI_FRAME_ERROR = -2047;

    /**
     * <b>[-2048]</b> Collision detected.
     */
    public static final int MI_COLLISION_ERROR = -2048;

    /**
     * <b>[-2049]</b> Failed to reset interface.
     */
    public static final int MI_INTERFACE_ERROR = -2049;

    /**
     * <b>[-2050]</b> Receiving timeout.
     */
    public static final int MI_ACCESS_TIMEOUT = -2050;

    /**
     * <b>[-2051]</b> Protocol error.
     */
    public static final int MI_PROTOCOL_ERROR = -2051;

    /**
     * <b>[-2052]</b> Abnormal abortion.
     */
    public static final int MI_QUIT = -2052;

    /**
     * <b>[-2053]</b> PPS operation error.
     */
    public static final int MI_PPS_ERROR = -2053;

    /**
     * <b>[-2054]</b> Failed to request SPI.
     */
    public static final int MI_SPI_REQUEST_ERROR = -2054;

    /**
     * <b>[-2056]</b> Wrong card type.
     */
    public static final int MI_CARD_TYPE_ERROR = -2056;

    /**
     * <b>[-2057]</b> Wrong IOCTL parameter.
     */
    public static final int MI_IOCTL_PARAM_ERROR = -2057;

    /**
     * <b>[-2059]</b> Invalid parameter.
     */
    public static final int MI_PARAM_ERROR = -2059;

    /**
     * <b>[-3101]</b> RF card busy.
     */
    public static final int RFID_BUSY = -3101;

    /**
     * <b>[-3102]</b> Printer busy.
     */
    public static final int PRINTER_BUSY = -3102;

    /**
     * <b>[-3103]</b> Smart card busy.
     */
    public static final int ICCARD_BUSY = -3103;

    /**
     * <b>[-3104]</b> Mag card busy.
     */
    public static final int MAG_CARD_BUSY = -3104;

    /**
     * <b>[-3107]</b> PIN input busy.
     */
    public static final int PIN_BUSY = -3107;

    /**
     * <b>[-3109]</b> Device busy
     */
    public static final int DEV_BUSY = -3109;

    /**
     * <b>[-4000]</b> Underlying driver base error.
     */
    public static final int POS_NDK_BASE = -4000;

    /**
     * <b>[-4021]</b> Newland permissions undefined.
     */
    public static final int PERMISSION_UNDEFINED = POS_NDK_BASE - 21;

    /**
     * <b>[-4022]</b> Underlying driver is occupied by other process.
     */
    public static final int ACCESS_BUSY = POS_NDK_BASE - 22;

    /**
     * <b>[-6000]</b> Underlying driver communication error.
     */
    public static final int COM_FAIL = -6000;

    /**
     * <b>[-9998]</b> Firmware update needed.
     */
    public static final int NEED_UPDATE = -9998;

    /**
     * <b>[-9999]</b> Unsupported.
     */
    public static final int UNSUPPORTED = -9999;

    /**
     * <b>[-10000]</b> External error.
     */
    public static final int EXT_ERROR = -10000;

    /**
     *<b>[-10001]</b>  External error: Command failed.
     */
    public static final int EXT_COMMAND_FAILED = EXT_ERROR - 1;

    /**
     * <b>[-10002]</b> External error: Invalid command sequence.
     */
    public static final int EXT_INVALID_COMMAND_SEQUENCE = EXT_ERROR - 2;

    /**
     * <b>[-10003]</b> External error: Command length error.
     */
    public static final int EXT_COMMAND_LENGTH_ERROR = EXT_ERROR - 3;

    /**
     * <b>[-10004]</b> External error: Device init error.
     */
    public static final int EXT_DEVICE_INIT_ERROR = EXT_ERROR - 4;

    /**
     * <b>[-10005]</b> External error: Device open error.
     */
    public static final int EXT_DEVICE_OPEN_ERROR = EXT_ERROR - 5;

    /**
     * <b>[-10006]</b> External error: Unknown error.
     */
    public static final int EXT_UNKNOWN_ERROR = EXT_ERROR - 6;

    /**
     * <b>[-10007]</b> External error: Failed to set icon.
     */
    public static final int EXT_ICON_SET_ERROR = EXT_ERROR - 7;

    /**
     * <b>[-10008]</b> External error: Failed to open file.
     */
    public static final int EXT_FILE_OPEN_ERROR = EXT_ERROR - 8;

    /**
     * <b>[-10009]</b> External error: Failed to write file.
     */
    public static final int EXT_FILE_WRITE_ERROR = EXT_ERROR - 9;

    /**
     * <b>[-10010]</b> External error: Wrong offset.
     */
    public static final int EXT_FILE_WRONG_OFFSET = EXT_ERROR - 10;

    /**
     * <b>[-10011]</b> External error: SHA1 error.
     */
    public static final int EXT_FILE_SHA1_ERROR = EXT_ERROR - 11;

    /**
     * <b>[-10100]</b> External message error.
     */
    public static final int EXT_MESSAGE_ERROR = -10100;

    /**
     * <b>[-10101]</b> External message error: Invalid message type.
     */
    public static final int EXT_MESSAGE_INVALID_MESSAGE_TYPE = EXT_MESSAGE_ERROR - 1;

    /**
     * <b>[-10102]</b> External message error: Invalid function ID.
     */
    public static final int EXT_MESSAGE_INVALID_FUNCTION_ID = EXT_MESSAGE_ERROR - 2;

    /**
     * <b>[-10103]</b> External message error: Exceed max data length.
     */
    public static final int EXT_MESSAGE_EXCEED_MAX_LENGTH = EXT_MESSAGE_ERROR - 3;

    /**
     * <b>[-10104]</b> External message error: The value of length field is bigger than length of actual data.
     * <p>Example: {0x04, 0x01, 0x02, 0x03}, the first byte is the length of rest data, but there only 3 bytes followed it.
     * This error will be thrown in this case.</p>
     */
    public static final int EXT_MESSAGE_DATA_LEN_FIELD_ERROR = EXT_MESSAGE_ERROR - 4;

    /**
     * <b>[-10105]</b> External message error: Invalid LRC.
     */
    public static final int EXT_MESSAGE_INVALID_LRC = EXT_MESSAGE_ERROR - 5;

    /**
     * <b>[-10106]</b> External message error: No response code.
     */
    public static final int EXT_MESSAGE_NO_RESPONSE_CODE = EXT_MESSAGE_ERROR - 6;

    /**
     * <b>[-10107]</b> External message error: No response data.
     */
    public static final int EXT_MESSAGE_NO_RESPONSE_DATA = EXT_MESSAGE_ERROR - 7;

    /**
     * <b>[-10108]</b> External message error: Data length is not enough.
     */
    public static final int EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH = EXT_MESSAGE_ERROR - 8;

    /**
     * <b>[-10109]</b> External message error: Data length not correct, too long or too short.
     */
    public static final int EXT_MESSAGE_DATA_LENGTH_NOT_CORRECT = EXT_MESSAGE_ERROR - 9;

    /**
     * <b>[-10110]</b> External message error: This happens when writing byte array stream.
     */
    public static final int EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR = EXT_MESSAGE_ERROR - 10;

    /**
     * <b>[-10111]</b> External message error: Response data not started with STX.
     */
    public static final int EXT_MESSAGE_INVALID_RESPONSE_DATA = EXT_MESSAGE_ERROR - 11;

    /**
     * <b>[-10112]</b> External message error: KSN is not correct.
     */
    public static final int EXT_MESSAGE_INVALID_KSN = EXT_MESSAGE_ERROR - 12;

    /**
     * <b>[-10113]</b> External message error: MAC is not correct.
     */
    public static final int EXT_MESSAGE_INVALID_MAC = EXT_MESSAGE_ERROR - 13;

    /**
     * <b>[-10200]</b> Communication base error.
     */
    public static final int EXT_COMMUNICATION_ERROR = -10200;

    /**
     * <b>[-10201]</b> Communication error code: No response data.
     */
    public static final int EXT_COMMUNICATION_NO_RESPONSE_DATA = EXT_COMMUNICATION_ERROR - 1;

    /**
     * <b>[-10202]</b> Communication error code: Communicator not initialized.
     */
    public static final int EXT_COMMUNICATION_NOT_INITIALIZED = EXT_COMMUNICATION_ERROR - 2;

    /**
     * <b>[-10203]</b> Communication error code: Receiving data timeout.
     */
    public static final int EXT_COMMUNICATION_RECEIVE_DATA_TIMEOUT = EXT_COMMUNICATION_ERROR - 3;

    /**
     * <b>[-10204]</b> Communication error code: Failed to open.
     */
    public static final int EXT_COMMUNICATION_OPEN_ERROR = EXT_COMMUNICATION_ERROR - 4;

    /**
     * <b>[-10205]</b> Communication error code: Failed to close.
     */
    public static final int EXT_COMMUNICATION_CLOSE_ERROR = EXT_COMMUNICATION_ERROR - 5;

    /**
     * <b>[-10206]</b> Communication error code: Failed to send data.
     */
    public static final int EXT_COMMUNICATION_SEND_ERROR = EXT_COMMUNICATION_ERROR - 6;

    /**
     * <b>[-10207]</b> Communication error code: Failed to receive data.
     */
    public static final int EXT_COMMUNICATION_RECEIVE_ERROR = EXT_COMMUNICATION_ERROR - 7;

    /**
     * <b>[-10208]</b> Communication error code: No data to send.
     */
    public static final int EXT_COMMUNICATION_NO_DATA_TO_SEND = EXT_COMMUNICATION_ERROR - 8;

    /**
     * <b>[-10209]</b> Communication error code: Bluetooth not supported.
     */
    public static final int EXT_COMMUNICATION_BLUETHOOTH_NOT_SUPPORTED = EXT_COMMUNICATION_ERROR - 9;

    /**
     * <b>[-10210]</b> Communication error code: Bluetooth disabled.
     */
    public static final int EXT_COMMUNICATION_BLUETHOOTH_DISABLED = EXT_COMMUNICATION_ERROR - 10;

    /**
     * <b>[-10211]</b> Communication error code: Bluetooth not paired.
     */
    public static final int EXT_COMMUNICATION_BLUETHOOTH_NOT_PAIRED = EXT_COMMUNICATION_ERROR - 11;

    /**
     * <b>[-10212]</b> Communication error code: Bluetooth disconnected.
     */
    public static final int EXT_COMMUNICATION_BLUETHOOTH_DISCONNECTED = EXT_COMMUNICATION_ERROR - 12;

    /**
     * <b>[-10213]</b> Communication error code: User shall choose a bluetooth device from the list.
     */
    public static final int EXT_COMMUNICATION_BLUETHOOTH_DEVICE_NOT_CHOSEN = EXT_COMMUNICATION_ERROR - 13;

    /**
     * <b>[-11000]</b> External PIN pad error.
     */
    public static final int EXT_PINPAD_ERROR = -11000;

    /**
     * <b>[-11001]</b> External PIN pad error: Bad key tag.
     */
    public static final int EXT_PINPAD_BAD_KEY_TAG = EXT_PINPAD_ERROR - 1;

    /**
     * <b>[-11002]</b> External PIN pad error: Bad key index.
     */
    public static final int EXT_PINPAD_BAD_KEY_INDEX = EXT_PINPAD_ERROR - 2;

    /**
     * <b>[-11003]</b> External PIN pad error: Bad key usage.
     */
    public static final int EXT_PINPAD_BAD_KEY_USAGE = EXT_PINPAD_ERROR - 3;

    /**
     * <b>[-11004]</b> External PIN pad error: Key mode error.
     */
    public static final int EXT_PINPAD_KEY_MODE_ERROR = EXT_PINPAD_ERROR - 4;

    /**
     * <b>[-11005]</b> External PIN pad error: Line number error.
     */
    public static final int EXT_PINPAD_LINE_NUMBER_ERROR = EXT_PINPAD_ERROR - 5;

    /**
     * <b>[-11006]</b> External PIN pad error: Bad data length.
     */
    public static final int EXT_PINPAD_BAD_DATA_LENGTH = EXT_PINPAD_ERROR - 6;

    /**
     * <b>[-11007]</b> External PIN pad error: Invalid block.
     */
    public static final int EXT_PINPAD_INVALID_BLOCK = EXT_PINPAD_ERROR - 7;

    /**
     * <b>[-11008]</b> External PIN pad error: Key type error.
     */
    public static final int EXT_PINPAD_KEY_TYPE_ERROR = EXT_PINPAD_ERROR - 8;

    /**
     * <b>[-11009]</b> External PIN pad error: Format error.
     */
    public static final int EXT_PINPAD_FORMAT_ERROR = EXT_PINPAD_ERROR - 9;

    /**
     * <b>[-11010]</b> External PIN pad error: Failed to delete key.
     */
    public static final int EXT_PINPAD_DELETE_ERROR = EXT_PINPAD_ERROR - 10;

    /**
     * <b>[-11011]</b> External PIN pad error: Bad cmd length.
     */
    public static final int EXT_PINPAD_BAD_CMD_LENGTH = EXT_PINPAD_ERROR - 11;

    /**
     * <b>[-11012]</b> External PIN pad error: Bad IV length.
     */
    public static final int EXT_PINPAD_BAD_IV_LENGTH = EXT_PINPAD_ERROR - 12;

    /**
     * <b>[-11013]</b> External PIN pad error: KCV error.
     */
    public static final int EXT_PINPAD_KCV_ERROR = EXT_PINPAD_ERROR - 13;

    /**
     * <b>[-11014]</b> External PIN pad error: Key already exist.
     */
    public static final int EXT_PINPAD_KEY_EXIST = EXT_PINPAD_ERROR - 14;

    /**
     * <b>[-11015]</b> External PIN pad error: Failed to get installed key number.
     */
    public static final int EXT_PINPAD_INSTALLED_KEY_NUM_ERROR = EXT_PINPAD_ERROR - 15;

    /**
     * <b>[-11016]</b> External Keyboard error: Card Removed.
     */
    public static final int EXT_PINPAD_CARD_REMOVED = -11016;

    /**
     * <b>[-12000]</b> External card reader error.
     */
    public static final int EXT_CARD_READER_ERROR = -12000;

    /**
     * <b>[-12001]</b> External card reader error: Getting PAN error.
     */
    public static final int EXT_CARD_READER_PAN_GETTING_ERROR = EXT_CARD_READER_ERROR - 1;

    /**
     * <b>[-12002]</b> External card reader error: PAN encryption error.
     */
    public static final int EXT_CARD_READER_PAN_ENCRYPTION_ERROR = EXT_CARD_READER_ERROR - 2;

    /**
     * <b>[-12100]</b> External contact card error.
     */
    public static final int EXT_IC_CARD_ERROR = -12100;

    /**
     * <b>[-12101]</b> External contact card error: Read error.
     */
    public static final int EXT_IC_CARD_READ_ERROR = EXT_IC_CARD_ERROR - 1;

    /**
     * <b>[-12102]</b> External contact card error: Encryption error.
     */
    public static final int EXT_IC_CARD_ENCRYPTION_ERROR = EXT_IC_CARD_ERROR - 2;

    /**
     * <b>[-12103]</b> External contact card error: Decryption error.
     */
    public static final int EXT_IC_CARD_DECRYPTION_ERROR = EXT_IC_CARD_ERROR - 3;

    /**
     * <b>[-12104]</b> External contact card error: No card.
     */
    public static final int EXT_IC_CARD_NO_CARD = EXT_IC_CARD_ERROR - 4;

    /**
     * <b>[-12200]</b> External contactless card error.
     */
    public static final int EXT_RF_CARD_ERROR = -12200;

    /**
     * <b>[-12201]</b> External contactless card error: Card not present.
     */
    public static final int EXT_RF_CARD_NOT_PRESENT = EXT_RF_CARD_ERROR - 1;

    /**
     * <b>[-12202]</b> External contactless card error: Multi cards.
     */
    public static final int EXT_RF_CARD_MULTI_CARDS = EXT_RF_CARD_ERROR - 2;

    /**
     * <b>[-12203]</b> External contactless card error: Anti-collision failed.
     */
    public static final int EXT_RF_CARD_ANTI_COLLISION_FAILED = EXT_RF_CARD_ERROR - 3;

    /**
     * <b>[-12204]</b> External contactless card error: Failed to select card.
     */
    public static final int EXT_RF_CARD_SELECT_CARD_FAILED = EXT_RF_CARD_ERROR - 4;

    /**
     * <b>[-12205]</b> External contactless card error: Card reader not configured.
     */
    public static final int EXT_RF_CARD_READER_NOT_CONFIGURED = EXT_RF_CARD_ERROR - 5;

    /**
     * <b>[-12206]</b> External contactless card error: Authentication error.
     */
    public static final int EXT_RF_CARD_READER_AUTH_ERROR = EXT_RF_CARD_ERROR - 6;

    /**
     * <b>[-12207]</b> External contactless card error: Not authenticated.
     */
    public static final int EXT_RF_CARD_READER_NOT_AUTH = EXT_RF_CARD_ERROR - 7;
    /**
     * <b>[-12208]</b> External contactless card error: Not authenticated.
     */
    public static final int EXT_RF_CARD_ACTIVATE_FAIL = EXT_RF_CARD_ERROR - 8;
    /**
     * <b>[-12209]</b> External contactless card error: Not authenticated.
     */
    public static final int EXT_RF_CARD_GET_INFO_ERROR = EXT_RF_CARD_ERROR - 9;

    /**
     * <b>[-12300]</b> External mag card error.
     */
    public static final int EXT_MAG_CARD_ERROR = -12300;

    /**
     * <b>[-12301]</b> External mag card error: Failed to get track data.
     */
    public static final int EXT_MAG_GET_TRACK_DATA_ERROR = EXT_MAG_CARD_ERROR - 1;

    /**
     * <b>[-12302]</b> External mag card error: Read error.
     */
    public static final int EXT_MAG_READ_ERROR = EXT_MAG_CARD_ERROR - 2;

    /**
     * <b>[-12303]</b> External mag card error: Track 2 error.
     */
    public static final int EXT_MAG_TRACK2_ERROR = EXT_MAG_CARD_ERROR - 3;

    /**
     * <b>[-12304]</b> External mag card error： Track encryption error.
     */
    public static final int EXT_MAG_TRACK_ENCRYPTION_ERROR = EXT_MAG_CARD_ERROR - 4;

    /**
     * <b>[-12305]</b> External mag card error: Track 3 error.
     */
    public static final int EXT_MAG_TRACK3_ERROR = EXT_MAG_CARD_ERROR - 5;

    /**
     * <b>[-13000]</b> External EMV error.
     */
    public static final int EXT_EMV_ERROR = -13000;

    /**
     * <b>[-13001]</b> External EMV error: Cancelled by host.
     */
    public static final int EXT_EMV_CANCELLED_BY_HOST = EXT_EMV_ERROR - 1;

    /**
     * <b>[-14100]</b> External app error.
     */
    public static final int EXT_APP_ERROR = -14100;

    /**
     * <b>[-14101]</b> External app error: Failed to create file.
     */
    public static final int EXT_APP_FILE_CREATE_ERROR = EXT_APP_ERROR - 1;

    /**
     * <b>[-14102]</b> External app error: Failed to open file.
     */
    public static final int EXT_APP_FILE_OPEN_ERROR = EXT_APP_ERROR - 2;

    /**
     * <b>[-14103]</b> External app error: Failed to write file.
     */
    public static final int EXT_APP_FILE_WRITE_ERROR = EXT_APP_ERROR - 3;

    /**
     * <b>[-14104]</b> External app error: Failed to delete file.
     */
    public static final int EXT_APP_FILE_DELETE_ERROR = EXT_APP_ERROR - 4;

    /**
     * <b>[-14105]</b> External app error: File info error.
     */
    public static final int EXT_APP_NLD_INFO_ERROR = EXT_APP_ERROR - 5;

    /**
     * <b>[-14106]</b> External app error: Signature decryption error.
     */
    public static final int EXT_APP_SIGN_DECRYPTION_ERROR = EXT_APP_ERROR - 6;

    /**
     * <b>[-14107]</b> External app error: Signature check error.
     */
    public static final int EXT_APP_SIGN_CHECK_ERROR = EXT_APP_ERROR - 7;

    /**
     * <b>[-14108]</b> External app error: Failed to update.
     */
    public static final int EXT_APP_UPDATE_ERROR = EXT_APP_ERROR - 8;

    /**
     * <b>[-14200]</b> External signature error.
     */
    public static final int EXT_SIGNATURE_ERROR = -14200;

    /**
     * <b>[-14201]</b> External signature error: Failed to get SN.
     */
    public static final int EXT_SIGNATURE_GET_SN_FAILED = EXT_SIGNATURE_ERROR - 1;

    /**
     * <b>[-14202]</b> External signature error: Not supported.
     */
    public static final int EXT_SIGNATURE_NOT_SUPPORTED = EXT_SIGNATURE_ERROR - 2;

    /**
     * <b>[-14203]</b> External signature error: Failed to sign.
     */
    public static final int EXT_SIGNATURE_SIGN_FAILED = EXT_SIGNATURE_ERROR - 3;

    /**
     * <b>[-14300]</b> External scanner error.
     */
    public static final int EXT_SCANNER_ERROR = -14300;

    /**
     * <b>[-14301]</b> External scanner error: Scanning.
     */
    public static final int EXT_SCANNER_SCANNING_ERROR = EXT_SCANNER_ERROR - 1;

    /**
     * <b>[-14302]</b> External scanner error: Scanning head not supported.
     */
    public static final int EXT_SCANNER_SCANNING_HEAD_NOT_SUPPORTED = EXT_SCANNER_ERROR - 2;

    /**
     * <b>[-14303]</b> External scanner error: scanning is stopped.
     */
    public static final int EXT_SCANNER_SCANNING_STOPPED = EXT_SCANNER_ERROR - 3;

    /**
     * <b>[-14304]</b> External scanner error: Failed to stop scanning.
     */
    public static final int EXT_SCANNER_STOP_SCANNING_ERROR = EXT_SCANNER_ERROR - 4;

    /**
     * <b>[-14400]</b> External display error.
     */
    public static final int EXT_DISPLAY_ERROR = -14400;

    /**
     * <b>[-14401]</b> External display error: Customer card error.
     */
    public static final int EXT_DISPLAY_CUSTOMER_CARD_ERROR = EXT_DISPLAY_ERROR - 1;

    /**
     * <b>[-14402]</b> External display error: Beyond screen range.
     */
    public static final int EXT_DISPLAY_BEYOND_SCREEN_RANGE = EXT_DISPLAY_ERROR - 2;

    /**
     * <b>[-14403]</b> External display error: Failed to operate file.
     */
    public static final int EXT_DISPLAY_FILE_OPERATE_ERROR = EXT_DISPLAY_ERROR - 3;

    /**
     * <b>[-14404]</b> External display error: Lattice data error.
     */
    public static final int EXT_DISPLAY_LATTICE_DATA_ERROR = EXT_DISPLAY_ERROR - 4;

    /**
     * <b>[-14405]</b> External display error: Mode error.
     */
    public static final int EXT_DISPLAY_MODE_ERROR = EXT_DISPLAY_ERROR - 5;

    /**
     * <b>[-14406]</b> External display error: Data error.
     */
    public static final int EXT_DISPLAY_DATA_ERROR = EXT_DISPLAY_ERROR - 6;

    /**
     * <b>[-14407]</b> External display error: QR code correct level error.
     */
    public static final int EXT_DISPLAY_QR_CODE_ERROR_CORRECT_LEVEL = EXT_DISPLAY_ERROR - 7;

    /**
     * <b>[-14408]</b> External display error: QR code mask number error.
     */
    public static final int EXT_DISPLAY_QR_CODE_MASK_NUMBER_ERROR = EXT_DISPLAY_ERROR - 8;

    /**
     * <b>[-14409]</b> External display error: QR code type error.
     */
    public static final int EXT_DISPLAY_QR_CODE_TYPE_ERROR = EXT_DISPLAY_ERROR - 9;

    /**
     * <b>[-14410]</b> External display error: QR code auto centering error.
     */
    public static final int EXT_DISPLAY_QR_CODE_AUTO_CENTER_ERROR = EXT_DISPLAY_ERROR - 10;

    /**
     * <b>[-14411]</b> External display error: QR code initial abscissa error.
     */
    public static final int EXT_DISPLAY_QR_CODE_INITIAL_ABSCISSA_ERROR = EXT_DISPLAY_ERROR - 11;

    /**
     * <b>[-14412]</b> External display error: QR code initial ordinate error.
     */
    public static final int EXT_DISPLAY_QR_CODE_INITIAL_ORDINATE_ERROR = EXT_DISPLAY_ERROR - 12;

    /**
     * <b>[-14413]</b> External display error: QR code text position error.
     */
    public static final int EXT_DISPLAY_QR_CODE_TEXT_POSITION_ERROR = EXT_DISPLAY_ERROR - 13;

    /**
     * <b>[-14414]</b> External display error: QR code text length error.
     */
    public static final int EXT_DISPLAY_QR_CODE_TEXT_LENGTH_ERROR = EXT_DISPLAY_ERROR - 14;

    /**
     * <b>[-14415]</b> External display error: QR code length error.
     */
    public static final int EXT_DISPLAY_QR_CODE_LENGTH_ERROR = EXT_DISPLAY_ERROR - 15;

    /**
     * <b>[-14416]</b> External display error: QR code width error.
     */
    public static final int EXT_DISPLAY_QR_CODE_WIDTH_ERROR = EXT_DISPLAY_ERROR - 16;

    /**
     * <b>[-14417]</b> External display error: QR code height error.
     */
    public static final int EXT_DISPLAY_QR_CODE_HEIGHT_ERROR = EXT_DISPLAY_ERROR - 17;

    /**
     * <b>[-14418]</b> External display error: The height of text is out of screen range。
     */
    public static final int EXT_DISPLAY_TEXT_HEIGHT_ERROR = EXT_DISPLAY_ERROR - 18;

    /**
     * <b>[-14500]</b> External LED error.
     */
    public static final int EXT_LED_ERROR = -14500;

    /**
     * <b>[-14501]</b> External LED error: Lattice data error.
     */
    public static final int EXT_LED_LATTICE_DATA_ERROR = EXT_LED_ERROR - 1;

    /**
     * <b>[-14502]</b> External LED error: Mode error.
     */
    public static final int EXT_LED_MODE_ERROR = EXT_LED_ERROR - 2;

    /**
     * <b>[-14503]</b> External LED error: Data error.
     */
    public static final int EXT_LED_DATA_ERROR = EXT_LED_ERROR - 3;


    /**
     * <b>[-99999]</b> External error: Unsupported.
     */
    public static final int EXT_UNSUPPORTED = -99999;




}
