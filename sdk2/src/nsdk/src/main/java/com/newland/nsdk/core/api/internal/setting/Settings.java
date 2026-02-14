package com.newland.nsdk.core.api.internal.setting;

import com.newland.nsdk.core.api.internal.devicemanager.DeviceManager;

/**
 * The keys of setting items.
 */
public class Settings {

    /**
     * The screen backlight brightness between 0 and 255.
     * <p>Note: The minimum brightness on different devices is unique:</p>
     * <ul>
     *     <li>N950S: minimum brightness is 6.</li>
     *     <li>N950U: minimum brightness is 13.</li>
     * </ul>
     */
    public static final String SCREEN_BRIGHTNESS = "screen_brightness";

    /**
     * Whether or not to display Storage settings: `Settings -> Device -> Storage`.
     * <ul>
     *     <li>0: Display</li>
     *     <li>1: Not display</li>
     * </ul>
     */
    public static final String SETTING_STORAGE_DISPLAY = "setting_storage_display";

    /**
     * Whether or not to display Home settings: `Settings -> Apps -> Find the launcher app -> Home app`.
     * <ul>
     *     <li>0: Display</li>
     *     <li>1: Not display</li>
     * </ul>
     *
     * <p>Note: this only works when there are multi launcher apps installed. </p>
     */
    public static final String SETTING_HOME_DISPLAY = "setting_home_display";

    /**
     * Whether or not to display App settings: `Settings -> Device -> Apps`
     * <ul>
     *     <li>0: Display</li>
     *     <li>1: Not display</li>
     * </ul>
     */
    public static final String SETTING_APP_DISPLAY = "setting_app_display";

    /**
     * Whether or not to display Privacy settings: `Settings -> System -> Backup & reset`
     * <ul>
     *     <li>0: Display</li>
     *     <li>1: Not display</li>
     * </ul>
     */
    public static final String SETTING_PRIVACY_DISPLAY = "setting_privacy_display";

    /**
     * Whether or not to display battery percentage on the status bar.
     * <ul>
     *     <li>0: Not display</li>
     *     <li>1: Display</li>
     * </ul>
     */
    public static final String STATUS_BAR_SHOW_BATTERY_PERCENT = "status_bar_show_battery_percent";

    /**
     * Whether or not to display Data Usage settings: `Settings -> wireless & networks -> Data Usage`
     * <ul>
     *     <li>0: Display</li>
     *     <li>1: Not display</li>
     * </ul>
     */
    public static final String SETTING_DATA_USAGE_DISPLAY = "setting_data_usage_display";

    /**
     * Whether or not do display Battery settings: `Settings -> Device -> Battery`
     * <ul>
     *     <li>0: Display</li>
     *     <li>1: Not display</li>
     * </ul>
     */
    public static final String SETTING_BATTERY_DISPLAY = "setting_battery_display";

    /**
     * Whether or not to display Accessibility settings: `Settings -> System -> Accessibility`
     * <ul>
     *     <li>0: Display</li>
     *     <li>1: Not display</li>
     * </ul>
     */
    public static final String SETTING_ACCESSIBILITY_SETTINGS_DISPLAY = "setting_accessibility_settings_display";

    /**
     * Whether or not to display Development settings: `Settings -> System -> Developer options`
     *
     * <p>This setting is not supported on some devices, e.g., N910Pro.</p>
     *
     * <ul>
     *     <li>0: Display</li>
     *     <li>1: Not display</li>
     * </ul>
     */
    public static final String SETTING_DEVELOPMENT_SETTINGS_DISPLAY = "setting_development_settings_display";

    /**
     * Whether or not to display Location settings: `Settings -> System -> Location`
     * <ul>
     *     <li>0: Display</li>
     *     <li>1: Not display</li>
     * </ul>
     */
    public static final String SETTING_LOCATION_SETTINGS_DISPLAY = "setting_location_settings_display";

    /**
     * Whether or not to display Security settings: `Settings -> System -> Security`
     * <ul>
     *     <li>0: Display</li>
     *     <li>1: Not display</li>
     * </ul>
     */
    public static final String SETTING_SECURITY_SETTINGS_DISPLAY = "setting_security_settings_display";

    /**
     * Whether administrator login is required or not when launching the Settings app.
     *
     * <p>Note: Login password can be set by "SETTING_APK_LOGIN_PASSWORD" setting.</p>
     *
     * <ul>
     *     <li>0: No need to login</li>
     *     <li>1: Need to login</li>
     * </ul>
     */
    public static final String SETTING_APK_NEED_LOGIN = "setting_apk_need_login";

    /**
     * Sets administrator login password.
     */
    public static final String SETTING_APK_LOGIN_PASSWORD = "setting_apk_login_passwd";

    /**
     * Whether or not to display Printing settings: `Settings -> System -> Printing`
     * <ul>
     *     <li>0: Display</li>
     *     <li>1: Not display</li>
     * </ul>
     */
    public static final String SETTING_PRINT_SETTINGS_DISPLAY = "setting_print_settings_display";

    /**
     * Sets navigation bar for the following device:
     * <ul>
     *     <li>N850 A7</li>
     *     <li>CPOS X5 device</li>
     * </ul>
     * <ul> Value range:
     *     <li>0: Layout back key left.</li>
     *     <li>1: Layout back key right.</li>
     *     <li>2: Hide navigation bar.</li>
     * </ul>
     * <p>Note: Reboot is required after setting.</p>
     */
    public static final String RELAYOUT_NAVIGATION_BAR = "relayout_navigation_bar";

    /**
     * Sets navigation bar:
     * <ul>
     *     <li>N910 Pro</li>
     * </ul>
     * <ul>Value range:
     *     <li>0: (No hidden button) Back key on the left.</li>
     *     <li>1: (No hidden button) Back key on the right.</li>
     *     <li>2: (No hidden button) Back key on the left and show notification panel button.</li>
     *     <li>3: (No hidden button) Back key on the right and show notification panel button on the right.</li>
     *     <li>16: (Show hidden button) Back key on the left.</li>
     *     <li>17: (Show hidden button) Back key on the right.</li>
     *     <li>18: (Show hidden button) Back key on the left and show notification panel button.</li>
     *     <li>19: (Show hidden button) Back key on the right and show notification panel button.</li>
     * </ul>
     */
    public static final String NAVIGATION_BAR_CONFIG  = "navigationbar_config";

    /**
     * Whether or not to disable App Switch Key.
     * <ul>
     *     <li>0: Enable</li>
     *     <li>1: Disable</li>
     * </ul>
     * <p>Note: Supported on CPOS, not supported on N910 A7, N850 A7, N700 A7</p>
     */
    public static final String SETTING_DISABLE_APP_SWITCH_KEY = "setting_disable_app_switch_key";

    /**
     * Whether or not to display VPN settings: `Settings -> Wireless & networks -> More -> VPN`
     * <ul>
     *     <li>0: Display</li>
     *     <li>1: Not display</li>
     * </ul>
     */
    public static final String SETTING_VPN_DISPLAY = "setting_vpn_display";

    /**
     * Whether or not to enable and display Lockscreen settings: `Settings -> Device -> Screen lock`
     * <ul>
     *     <li>0: Enable</li>
     *     <li>1: Disable</li>
     * </ul>
     */
    public static final String LOCKSCREEN_DISABLED = "lockscreen.disabled";

    /**
     * Whether or not to enable Status Bar Pull-down.
     * <ul>
     *     <li>0: Enable</li>
     *     <li>1: Disable</li>
     * </ul>
     */
    public static final String STATUS_BAR_ENABLED = "status_bar_enabled";

    /**
     * Whether or not to display the following settings on status bar.
     * <ul>
     *     <li>wifi</li>
     *     <li>bt</li>
     *     <li>airplane</li>
     *     <li>location</li>
     *     <li>cell</li>
     *     <li>inversion</li>
     *     <li>rotation</li>
     *     <li>flashlight</li>
     *     <li>cast</li>
     *     <li>hotspot</li>
     * </ul>
     * Multi items separated by "," can be set, e.g., "wifi,bt".
     */
    public static final String STATUS_BAR_QS_TILE = "status_bar_qs_tile";

    /**
     * Whether or not to show ADB notification on status bar.
     * <ul>
     *     <li>0: Display</li>
     *     <li>1: Not display</li>
     * </ul>
     */
    public static final String STATUS_BAR_ADB_NOTIFY = "status_bar_adb_notify";

    /**
     * Whether or not to display Personal Dictionary options: `Settings -> System -> Language & input -> Personal dictionary`
     * <ul>
     *     <li>0: Display</li>
     *     <li>1: Not display</li>
     * </ul>
     */
    public static final String SETTING_LANGUAGE_USER_DICTIONARY_DISPLAY = "setting_language_user_dictionary_display";

    /**
     * Whether or not to display Notification items settings: `Settings -> Device -> Sound`
     * <ul>
     *     <ul>Bit 1:
     *     <li>0: Display media volume settings.</li>
     *     <li>1: Not display media volume settings.</li>
     *     </ul>
     *     <ul>Bit 2:
     *     <li>0: Display ringtone volume settings.</li>
     *     <li>1: Not display ringtone volume settings.</li>
     *     </ul>
     *     <ul>Bit 3:
     *     <li>0: Display device ringtone settings.</li>
     *     <li>1: Not display device ringtone settings.</li>
     *     </ul>
     *     <ul>Bit 4:
     *     <li>0: Display default notification ringtone settings.</li>
     *     <li>1: Not display default notification ringtone settings.</li>
     *     </ul>
     *     <ul>Bit 5<b>(Not supported for overseas devices)</b>:
     *     <li>0: Display dial pad sound settings.</li>
     *     <li>1: Not display dial pad sound settings.</li>
     *     </ul>
     *     <ul>Bit 6<b>(Not supported for overseas devices)</b>:
     *     <li>0: Display screen lock sound settings.</li>
     *     <li>1: Not display screen lock sound settings.</li>
     *     </ul>
     *     <ul>Bit 7<b>(Not supported for overseas devices)</b>:
     *     <li>0: Display touch sound settings.</li>
     *     <li>1: Not display touch sound settings.</li>
     *     </ul>
     *     <ul>Bit 8<b>(Not supported for overseas devices)</b>:
     *     <li>0: Display touch vibration settings.</li>
     *     <li>1: Not display touch vibration settings.</li>
     *     </ul>
     * </ul>
     *
     * <p>Example: </p>
     * <pre>
     *     // Media volume settings: Display
     *     // Ringtone volume settings: Display
     *     // Device ringtone settings: Display
     *     // Default notification ringtone settings: Display
     *     settingsManager.set(Settings.SETTING_NOTIFICATION_ITEMS_DISPLAY, "00000000");
     *     // Media volume settings: Display
     *     // Ringtone volume settings: Not display
     *     // Device ringtone settings: Display
     *     // Default notification ringtone settings: Display
     *     settingsManager.set(Settings.SETTING_NOTIFICATION_ITEMS_DISPLAY, "01000000");
     *     // Media volume settings: Display
     *     // Ringtone volume settings: Not display
     *     // Device ringtone settings: Not display
     *     // Default notification ringtone settings: Display
     *     settingsManager.set(Settings.SETTING_NOTIFICATION_ITEMS_DISPLAY, "01100000");
     *     // Media volume settings: Not display
     *     // Ringtone volume settings: Not display
     *     // Device ringtone settings: Not display
     *     // Default notification ringtone settings: Not display
     *     settingsManager.set(Settings.SETTING_NOTIFICATION_ITEMS_DISPLAY, "11110000");
     * </pre>
     */
    public static final String SETTING_NOTIFICATION_ITEMS_DISPLAY = "setting_notification_items_display";

    /**
     * The amount of time in milliseconds before the device goes to sleep or begins
     * to dream after a period of inactivity. This value is also known as the
     * user activity timeout period since the screen isn't necessarily turned off
     * when it expires. -1(never sleep) or >0.
     *
     * <p>Check the timeout from: `Settings -> Device -> Display -> Sleep`.</p>
     */
    public static final String SCREEN_OFF_TIMEOUT = "screen_off_timeout";

    /**
     * Whether or not to display credentials in WIFI advanced settings: `Settings -> Wireless & networks -> WLAN -> Click menu on right top -> Advanced -> Install certificates`
     * <ul>
     *     <li>0: Display</li>
     *     <li>1: Not display</li>
     * </ul>
     */
    public static final String WIFI_INSTALL_CREDENTIALS_DISPLAY = "wifi_install_credentials_display";

    /**
     * Whether or not to enable bluetooth file transfer.
     * <ul>
     *     <li>0: Enable. When file is coming, it will prompt to accept the file.</li>
     *     <li>1: Disable. The prompt will not be showed and file will not be accepted</li>
     * </ul>
     */
    public static final String SETTING_BLUETOOTH_FILE_TRANSFER = "setting_bluetooth_file_transfer";

    /**
     * Sets locales. Multi locales separated by "," can be set, e.g., "zh-CN,zh-HK,ja-JP,ko-KR".
     *
     * If it is set successfully, check the result in: `Settings -> System -> Language & input -> Languages`
     *
     * <p>Note: "android.permission.CHANGE_CONFIGURATION" is required.</p>
     */
    public static final String SETTING_LOCALES = "setting_locales";

    /**
     * Whether or not to display Processor info: `Settings -> System -> About device -> Processor info`
     * <ul>
     *     <li>0: Display</li>
     *     <li>1: Not display</li>
     * </ul>
     */
    public static final String SETTING_PROCESSOR_DISPLAY = "setting_processor_display";

    /**
     * Whether or not to display OTA update: `Settings -> System -> About device -> System updates`
     * <ul>
     *     <li>0: Display</li>
     *     <li>1: Not display</li>
     * </ul>
     */
    public static final String SETTING_OTA_UPDATE = "setting_ota_update";

    /**
     * Whether or not to display Wallpaper settings: `Settings -> Device -> Display -> Wallpaper`
     * <ul>
     *     <li>0: Display</li>
     *     <li>1: Not display</li>
     * </ul>
     */
    public static final String SETTING_WALLPAPER_DISPLAY = "setting_wallpaper_display";

    /**
     * Whether or not to display Payment Certification Update settings: `Settings -> System -> Security -> Update Payment Certificate`
     * <ul>
     *     <li>0: Display</li>
     *     <li>1: Not display</li>
     * </ul>
     */
    public static final String SETTING_PAYMENT_CERT_UPDATE_DISPLAY = "setting_payment_cert_update_display";

    /**
     * Sets app signature schemes.Multi schemes separated by "," can be set, e.g., "newland,allinpay".
     *
     * When using "Update Tool" in the device to install app, it will check signature of the app.
     * If it is signed correctly, it will be installed successfully.Otherwise it will fail.
     *
     * Note: The device shall have installed corresponding certificate before installing the app.
     */
    public static final String SETTINGS_APP_SIGNATURE_SCHEME = "setting_app_signature_scheme";

    /**
     * Whether or not to disable home key.
     * <ul>
     *     <li>0: Enable</li>
     *     <li>1: Disable</li>
     * </ul>
     */
    public static final String SETTING_DISABLE_HOME_KEY = "setting_disable_home_key";

    /**
     * Sets recent apps key code.
     * <ul>
     *     <li>82: Menu</li>
     *     <li>187: App Switch</li>
     * </ul>
     */
    public static final String SETTINGS_RECENT_APPS_KEY_CODE = "setting_recent_apps_key_code";

    /**
     * Whether or not to display Settings icon on right top of status bar when it is pulled down.
     * <ul>
     *     <li>0: Display</li>
     *     <li>1: Not display</li>
     * </ul>
     */
    public static final String STATUS_BAR_SETTING_ENABLED = "status_bar_setting_enabled";

    /**
     * Whether or not to display Tether settings: `Settings -> Wireless & networks -> More -> Tethering & portable hotspot`
     * <ul>
     *     <li>0: Display</li>
     *     <li>1: Not display</li>
     * </ul>
     */
    public static final String TETHER_DISPLAY = "tether_display";

    /**
     * Whether or not to display the following info:
     * <ul>
     *     <ul>Bit 1:
     *     <li>0: Display status info.</li>
     *     <li>1: Not display status info.</li>
     *     </ul>
     *     <ul>Bit 2:
     *     <li>0: Display legal info.</li>
     *     <li>1: Not display legal info.</li>
     *     </ul>
     *     <ul>Bit 3:
     *     <li>0: Display kernel version.</li>
     *     <li>1: Not display kernel version.</li>
     *     </ul>
     *     <ul>Bit 4:
     *     <li>0: Display Bootloader version.</li>
     *     <li>1: Not display Bootloader version.</li>
     *     </ul>
     *     <ul>Bit 5:
     *     <li>0: Display Baseband version.</li>
     *     <li>1: Not display Baseband version.</li>
     *     </ul>
     * </ul>
     *
     * <p>Example: </p>
     * <pre>
     *     // Status info: Display
     *     // Legal info: Display
     *     // Kernel version: Display
     *     // Bootloader: Display
     *     // Baseband version: Display
     *     settingsManager.set(Settings.SETTING_DEVICE_INFO_ITEMS_DISPLAY, "00000");
     *     // Status info: Display
     *     // Legal info: Display
     *     // Kernel version: Not display
     *     // Bootloader: Display
     *     // Baseband version: Display
     *     settingsManager.set(Settings.SETTING_DEVICE_INFO_ITEMS_DISPLAY, "00100");
     *     // Status info: Not display
     *     // Legal info: Not display
     *     // Kernel version: Not display
     *     // Bootloader: Not display
     *     // Baseband version: Not display
     *     settingsManager.set(Settings.SETTING_DEVICE_INFO_ITEMS_DISPLAY, "11111");
     * </pre>
     */
    public static final String SETTING_DEVICE_INFO_ITEMS_DISPLAY = "setting_device_info_items_display";

    /**
     * Gets product model.
     * @deprecated Replaced by {@link DeviceManager#getDeviceInfo()}.
     */
    public static final String SETTING_PRODUCT_MODEL = "setting_product_model";

    /**
     * Default input method. After it is set successfully, check it in: `Settings -> System -> Language & input -> Current Keyboard`
     * <ul>
     *     <li>PinyinIME</li>
     *     <li>LatinIME</li>
     * </ul>
     */
    public static final String DEFAULT_INPUT_METHOD = "default_input_method";

    /**
     * Sets printer grayscale, 1~5.
     * @deprecated Replaced by {@link com.newland.nsdk.core.api.internal.printer.Printer#setGray}.
     */
    public static final String PRINTER_GREY_LEVEL = "printer_grey_level";

    /**
     * Printer paper size configuration:
     * <ul>
     *     <li>2: 2 Inch printing paper, max length of each line is 384px.</li>
     *     <li>3: 3 Inch printing paper, max length of each line is 576px.</li>
     * </ul>
     *
     * <p>Note: This is supported on CPOS device.</p>
     */
    public static final String PRINTER_PAPER_SIZE = "printer_paper_size";

    /**
     * Sets charge capacity.
     * <p>Note: This ranges from 0 to 100.</p>
     */
    public static final String SETTING_PROTECTED_CHARGE_CAPACITY = "setting_protected_chg_cap";

    /**
     * Whether to enable charge protection.
     * <ul>
     *     <li>0: disabled charge protection.</li>
     *     <li>1: enabled charge protection.</li>
     * </ul>
     */
    public static final String SETTING_PROTECTED_CHARGE_ENABLE = "setting_protected_chg_enable";

    /**
     * Sets recharge capacity.
     * <p>Note: This ranges from 0 to 100.</p>
     */
    public static final String SETTING_PROTECTED_RECHARGE_CAPACITY = "setting_protected_rechg_cap";

    /**
     * Sets Launcher.
     * <p>Note: The Application to be set as default launcher shall declare authorities in AndroidManifest.xml as following: </p>
     * <li>category android:name="android.intent.category.HOME"</li>
     * <li>category android:name="android.intent.category.DEFAULT"</li>
     */
    public static final String SETTING_LAUNCHER = "setting_launcher";

    /**
     * Gets the width of the led background.
     * <p>Note: This is read only property.</p>
     */
    public static final String LED_BACKGROUND_WIDTH = "ro.product.led_layout_width";

    /**
     * Gets the height of the led background.
     * <p>Note: This is read only property.</p>
     */
    public static final String LED_BACKGROUND_HEIGHT = "ro.product.led_layout_height";

}
