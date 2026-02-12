/*
 * jPOS Project [http://jpos.org]
 * Copyright (C) 2000-2012 Alejandro P. Revilla
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.newland.sdk.me.module.emv;

import android.newland.os.NlBuild;
import android.os.Build;

import com.newland.emv.jni.type.capk;
import com.newland.emv.jni.type.emv_opt;
import com.newland.emv.jni.type.emvparam;
import com.newland.emv.jni.type.ep_opt;
import com.newland.emv.jni.type.rf_transdata;
import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.module.emv.EmvPackager;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.module.common.emv.SimpleEmvPackager;
import com.newland.sdk.mtypex.tlv.SimpleTLVMsg;
import com.newland.sdk.mtypex.tlv.SimpleTLVPackage;
import com.newland.sdk.utils.TLVMsg;
import com.newland.sdk.utils.TLVPackage;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * ISO utility class
 *
 * @since ver3.10.01
 */
public class EMVInnerUtils {
    private static boolean isIndicatorsAndBeep;

    private EMVInnerUtils() {
        throw new AssertionError();
    }

    public static final String[] hexStrings;

    static {
        hexStrings = new String[256];
        for (int i = 0; i < 256; i++) {
            StringBuilder d = new StringBuilder(2);
            char ch = Character.forDigit(((byte) i >> 4) & 0x0F, 16);
            d.append(Character.toUpperCase(ch));
            ch = Character.forDigit((byte) i & 0x0F, 16);
            d.append(Character.toUpperCase(ch));
            hexStrings[i] = d.toString();
        }

    }

    /**
     * pad to the left
     *
     * @param s   - original string
     * @param len - desired len
     * @param c   - padding char
     * @return padded string
     * @ on error
     */
    public static String padleft(String s, int len, char c)

    {
        s = s.trim();
        if (s.length() > len)
            throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "invalid len " + s.length() + "/" + len);
        StringBuilder d = new StringBuilder(len);
        int fill = len - s.length();
        while (fill-- > 0)
            d.append(c);
        d.append(s);
        return d.toString();
    }

    public static byte[] padLeft(byte[] tgt, int len, byte padding) {
        if (tgt.length >= len) {
            return tgt;
        }
        ByteBuffer buffer = ByteBuffer.allocate(len);
        byte[] paddings = new byte[len - tgt.length];
        Arrays.fill(paddings, padding);

        buffer.put(paddings);
        buffer.put(tgt);

        return buffer.array();
    }

    /**
     * converts to BCD
     *
     * @param s       - the number
     * @param padLeft - flag indicating left/right padding
     * @param d       The byte array to copy into.
     * @param offset  Where to start copying into.
     * @return BCD representation of the number
     */
    public static byte[] str2bcd(String s, boolean padLeft, byte[] d, int offset) {
        int len = s.length();
        int start = (((len & 1) == 1) && padLeft) ? 1 : 0;

        for (int i = start; i < len + start; i++) {
            //兼容>10的"BCD"
            int value = s.charAt(i - start);
            if (value > '0' + 16) {
                char hex = Character.toLowerCase((char) value);
                value = hex - 'a' + 10;
            } else if (value >= '0') {
                value = value - '0';
            }

            d[offset + (i >> 1)] |= value << ((i & 1) == 1 ? 0 : 4);

        }

        return d;
    }

    /**
     * converts to BCD
     *
     * @param s       - the number
     * @param padLeft - flag indicating left/right padding
     * @return BCD representation of the number
     */
    public static byte[] str2bcd(String s, boolean padLeft) {
        int len = s.length();
        byte[] d = new byte[(len + 1) >> 1];
        return str2bcd(s, padLeft, d, 0);
    }

    /**
     * converts to BCD
     *
     * @param s       - the number
     * @param padLeft - flag indicating left/right padding
     * @param fill    - fill value
     * @return BCD representation of the number
     */
    public static byte[] str2bcd(String s, boolean padLeft, byte fill) {
        int len = s.length();
        byte[] d = new byte[(len + 1) >> 1];
        Arrays.fill(d, fill);
        int start = (((len & 1) == 1) && padLeft) ? 1 : 0;
        for (int i = start; i < len + start; i++)
            d[i >> 1] |= (s.charAt(i - start) - '0') << ((i & 1) == 1 ? 0 : 4);
        return d;
    }

    /**
     * converts a BCD representation of a number to a String
     *
     * @param b       - BCD representation
     * @param offset  - starting offset
     * @param len     - BCD field len
     * @param padLeft - was padLeft packed?
     * @return the String representation of the number
     */
    public static String bcd2str(byte[] b, int offset,
                                 int len, boolean padLeft) {
        StringBuilder d = new StringBuilder(len);
        int start = (((len & 1) == 1) && padLeft) ? 1 : 0;
        for (int i = start; i < len + start; i++) {
            int shift = ((i & 1) == 1 ? 0 : 4);
            char c = Character.forDigit(
                    ((b[offset + (i >> 1)] >> shift) & 0x0F), 16);
            if (c == 'd')
                c = '=';
            d.append(Character.toUpperCase(c));
        }
        return d.toString();
    }

    /**
     * converts a byte array to hex string
     * (suitable for dumps and ASCII packaging of Binary fields
     *
     * @param b - byte array
     * @return String representation
     */
    public static String hexString(byte[] b) {
        if(b == null){
            return "null";
        }
        StringBuilder d = new StringBuilder(b.length * 2);
        for (byte aB : b) {
            d.append(hexStrings[(int) aB & 0xFF]);
        }
        return d.toString();
    }

    /**
     * converts a byte array to hex string
     * (suitable for dumps and ASCII packaging of Binary fields
     *
     * @param b      - byte array
     * @param offset - starting position
     * @param len    the length
     * @return String representation
     */
    public static String hexString(byte[] b, int offset, int len) {
        StringBuilder d = new StringBuilder(len * 2);
        len += offset;
        for (int i = offset; i < len; i++) {
            d.append(hexStrings[(int) b[i] & 0xFF]);
        }
        return d.toString();
    }

    /**
     * @param b      source byte array
     * @param offset starting offset
     * @param len    number of bytes in destination (processes len*2)
     * @return byte[len]
     */
    public static byte[] hex2byte(byte[] b, int offset, int len) {
        byte[] d = new byte[len];
        for (int i = 0; i < len * 2; i++) {
            int shift = i % 2 == 1 ? 0 : 4;
            d[i >> 1] |= Character.digit((char) b[offset + i], 16) << shift;
        }
        return d;
    }

    /**
     * @param s source string (with Hex representation)
     * @return byte array
     */
    public static byte[] hex2byte(String s) {
        if (s.length() % 2 == 0) {
            return hex2byte(s.getBytes(), 0, s.length() >> 1);
        } else {
            // Padding left zero to make it even size #Bug raised by tommy
            return hex2byte("0" + s);
        }
    }


    /**
     * Unpad from right.
     *
     * @param s - original string
     * @param c - padding char
     * @return unPadded string.
     */
    public static String unPadRight(String s, char c) {
        int end = s.length();
        if (end == 0)
            return s;
        while ((0 < end) && (s.charAt(end - 1) == c)) end--;
        return (0 < end) ? s.substring(0, end) : s.substring(0, 1);
    }

    /**
     * Unpad from left.
     *
     * @param s - original string
     * @param c - padding char
     * @return unPadded string.
     */
    public static String unPadLeft(String s, char c) {
        int fill = 0, end = s.length();
        if (end == 0)
            return s;
        while ((fill < end) && (s.charAt(fill) == c)) fill++;
        return (fill < end) ? s.substring(fill, end) : s.substring(fill - 1, end);
    }


    /**
     * Converts a String to an integer of base radix.
     * <br><br>
     * String constraints are:
     * <li>Number must be less than 10 digits</li>
     * <li>Number must be positive</li>
     *
     * @param s     String representation of number
     * @param radix Number base to use
     * @return integer Value of number
     * @throws NumberFormatException
     */
    public static int parseInt(String s, int radix) throws NumberFormatException {
        int length = s.length();
        if (length > 9)
            throw new NumberFormatException("Number can have maximum 9 digits");
        int result = 0;
        int index = 0;
        int digit = Character.digit(s.charAt(index++), radix);
        if (digit == -1)
            throw new NumberFormatException("String contains non-digit");
        result = digit;
        while (index < length) {
            result *= radix;
            digit = Character.digit(s.charAt(index++), radix);
            if (digit == -1)
                throw new NumberFormatException("String contains non-digit");
            result += digit;
        }
        return result;
    }

    /**
     * Converts a character array to an integer of base radix.
     * <br><br>
     * Array constraints are:
     * <li>Number must be less than 10 digits</li>
     * <li>Number must be positive</li>
     *
     * @param cArray Character Array representation of number
     * @param radix  Number base to use
     * @return integer Value of number
     * @throws NumberFormatException
     */
    public static int parseInt(char[] cArray, int radix) throws NumberFormatException {
        int length = cArray.length;
        if (length > 9)
            throw new NumberFormatException("Number can have maximum 9 digits");
        int result = 0;
        int index = 0;
        int digit = Character.digit(cArray[index++], radix);
        if (digit == -1)
            throw new NumberFormatException("Char array contains non-digit");
        result = digit;
        while (index < length) {
            result *= radix;
            digit = Character.digit(cArray[index++], radix);
            if (digit == -1)
                throw new NumberFormatException("Char array contains non-digit");
            result += digit;
        }
        return result;
    }


    public static TLVPackage newTlvPackage() {
        return new SimpleTLVPackage();
    }

    public static TLVMsg newTlvMsg() {
        return new SimpleTLVMsg();
    }

    public static EmvPackager newEmvPackager() {
        return new SimpleEmvPackager();
    }

    public static boolean isSDK3() {
        String current_driver_version = NlBuild.VERSION.NL_FIRMWARE;
        if (SDKVersion().equals("SDK2.0")) {
            return false;
        } else if (SDKVersion().equals("SDK3.0")||SDKVersion().trim().equalsIgnoreCase("CHS")) {
            return true;
        } else if(SDKVersion().trim().equalsIgnoreCase("Overseas")){
            return true;
        }else if(SDKVersion().trim().equalsIgnoreCase("Brasil")){
            return true;
        }else {
            if ("SA1".equals(NlBuild.VERSION.NL_HARDWARE_ID) && Build.MODEL.equals("N900")) { //900 3G设备只支持2.0
                return false;
            }
            if (Build.MODEL.equals("N900")) {
                if (current_driver_version.equals("V2.0.28") || current_driver_version.equals("V2.1.03") || current_driver_version.equals("V2.1.05") || current_driver_version.equals("V2.1.09") || current_driver_version.equals("V2.1.18") || current_driver_version.equals("V2.1.27") || current_driver_version.equals("V2.1.37") || current_driver_version.equals("V2.1.49") || current_driver_version.equals("V2.1.53") || current_driver_version.equals("V2.1.58")
                        || current_driver_version.equals("V2.1.62") || current_driver_version.equals("V2.0.16") || current_driver_version.equals("V2.1.17") || current_driver_version.equals("V2.1.21") || current_driver_version.equals("V2.1.23") || current_driver_version.equals("V2.1.24") || current_driver_version.equals("V2.1.29") || current_driver_version.equals("V2.1.31") || current_driver_version.equals("V2.1.32") || current_driver_version.equals("V2.1.40") || current_driver_version.equals("V2.1.41")
                        || current_driver_version.equals("V2.1.44") || current_driver_version.equals("V2.0.45") || current_driver_version.equals("V2.1.46") || current_driver_version.equals("V2.1.48") || current_driver_version.equals("V2.1.51") || current_driver_version.equals("V2.1.55") || current_driver_version.equals("V2.1.56") || current_driver_version.equals("V2.1.60")) {
                    return true;
                } else {
                    return false;
                }

            } else if (Build.MODEL.equals("N910")) {
                if (current_driver_version.equals("V2.0.28") || current_driver_version.equals("V2.1.03") || current_driver_version.equals("V2.1.05") || current_driver_version.equals("V2.1.09") || current_driver_version.equals("V2.1.18") || current_driver_version.equals("V2.1.27") || current_driver_version.equals("V2.1.35") || current_driver_version.equals("V2.1.40") || current_driver_version.equals("V2.1.52") || current_driver_version.equals("V2.1.54")
                        || current_driver_version.equals("V2.1.64") || current_driver_version.equals("V2.0.16") || current_driver_version.equals("V2.1.13") || current_driver_version.equals("V2.1.21") || current_driver_version.equals("V2.1.23") || current_driver_version.equals("V2.1.24") || current_driver_version.equals("V2.1.29") || current_driver_version.equals("V2.1.30") || current_driver_version.equals("V2.1.32") || current_driver_version.equals("V2.1.43") || current_driver_version.equals("V2.0.33")
                        || current_driver_version.equals("V2.1.44") || current_driver_version.equals("V2.0.45") || current_driver_version.equals("V2.1.37") || current_driver_version.equals("V2.0.36") || current_driver_version.equals("V2.1.66") || current_driver_version.equals("V2.1.67") || current_driver_version.equals("V2.1.68") || current_driver_version.equals("V2.1.71") || current_driver_version.equals("V2.1.72") || current_driver_version.equals("V2.3.00") || current_driver_version.equals("V2.1.57") || current_driver_version.equals("V2.1.26")) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }
    }

    /**
     * 获取新大陆SDK版本信息。
     *
     * @return SDK2.0      -- 非事件机制版本
     * SDK3.0      -- 事件机制版本
     */
    public static String SDKVersion() {
        String version = "unknown";
        /**
         * ro.build.newland_sdk 后续固件版本增加的属性值
         *
         */
        version = getProperties("ro.build.newland_sdk");
        if ("unknown".equals(version)) {
            version = getProperties("ro.build.customer_id");
            if ("unknown".equals(version)) {
                // 根据MTMS之前的规则判断
                //20180719，SDK 2.0： SDK 2.0分支、银商专用、阿里千牛，其他的都是SDK 3.0。
                return version;
            } else if ("ChinaUms".equals(version) || "SDK_2.0".equals(version) || "AliQianNiu".equals(version)) {
                if("ChinaUms".equals(version) && !Build.MODEL.equals("N900")&&!Build.MODEL.equals("N910")){
                    return "SDK3.0";
                }
                return "SDK2.0";
            } else {
                return "SDK3.0";
            }
        } else {
            return version;
        }
    }

    private static String getProperties(String key) {
        String defaultValue = "unknown";
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(c, key, defaultValue));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static boolean getIndicatorsAndBeep() {
        return isIndicatorsAndBeep;
    }
    public static void setIndicatorsAndBeep(boolean isEnable) {
        EMVInnerUtils.isIndicatorsAndBeep = isEnable;
    }

    public static String toString_tags(int[] tags){
        if(tags == null){
            return "null";
        }
        String str = "|";
        for (int i = 0; i < tags.length; i++) {
            str += String.format("%x",tags[i]).toUpperCase()+"|";
        }
        return str;
    }

    public static void toString_ep_opt(DeviceLogger logger, ep_opt ep_opt){
        logger.debug("EmvJNIService->ep_opt ucTransType="+ep_opt.ucTransType+
                " emSeqTo="+ep_opt.emSeqTo+
                " emSeqStart="+ep_opt.emSeqStart+
                " nRequestAmt="+ep_opt.nRequestAmt+
                " ucCardNo="+ep_opt.ucCardNo+
                " ucRestart="+ep_opt.ucRestart+
                " nForceOnlineEnable="+ep_opt.nForceOnlineEnable+
                " nAccountTypeEnable="+ep_opt.nAccountTypeEnable+
                " pusOnlinePin="+ InnerUtils.hexString(ep_opt.pusOnlinePin)+
                " nAdviceReq="+ ep_opt.nAdviceReq +
                " nForceAcceptSupported="+ ep_opt.nForceAcceptSupported +
                " nSignatureReq="+ ep_opt.nSignatureReq +
                " pusAuthRespCode="+InnerUtils.hexString(ep_opt.pusAuthRespCode) +
                " nOnlineResult="+ep_opt.nOnlineResult +
                " nTransRet="+ep_opt.nTransRet +
                " _UI_message_id="+ep_opt._UI_message_id +
                " _UI_status="+ep_opt._UI_status +
                " _UI_hold_time="+InnerUtils.hexString(ep_opt._UI_hold_time) +
                " _UI_language_len="+ep_opt._UI_language_len +
                " _UI_language_preference="+InnerUtils.hexString(ep_opt._UI_language_preference) +
                " _UI_value_qualifier="+ep_opt._UI_value_qualifier +
                " _UI_value="+InnerUtils.hexString(ep_opt._UI_value) +
                " _UI_currency_code="+InnerUtils.hexString(ep_opt._UI_currency_code) +
                " _OP_status="+ep_opt._OP_status +
                " _OP_start="+ep_opt._OP_start +
                " _OP_online_response_data="+ep_opt._OP_online_response_data +
                " _OP_cvm="+(ep_opt._OP_cvm&0xFF) +
                " _OP_ui_request_on_outcome_present="+ep_opt._OP_ui_request_on_outcome_present +
                " _OP_ui_request_on_restart_present="+ep_opt._OP_ui_request_on_restart_present +
                " _OP_data_record_present="+ep_opt._OP_data_record_present +
                " _OP_discretionary_data_present="+ep_opt._OP_discretionary_data_present +
                " _OP_receipt="+ep_opt._OP_receipt +
                " _OP_alternate_interface_preference="+ep_opt._OP_alternate_interface_preference +
                " _OP_field_off_request="+ep_opt._OP_field_off_request +
                " _OP_removal_timeout="+ep_opt._OP_removal_timeout +
                " _ER_L1_indication="+ep_opt._ER_L1_indication +
                " _ER_L2_indication="+ep_opt._ER_L2_indication +
                " _ER_L3_indication="+ep_opt._ER_L3_indication +
                " _ER_SW1="+ep_opt._ER_SW1 +
                " _ER_SW2="+ep_opt._ER_SW2 +
                " _ER_MSG_ON_ERROR="+ep_opt._ER_MSG_ON_ERROR +
                " _refund_request_aac="+ep_opt._refund_request_aac +
                " ucCtrl="+ep_opt.ucCtrl +
                " ucForceBypssPinEnable="+ep_opt.ucForceBypssPinEnable +
                " ucRupayTerEnviron="+ep_opt.ucRupayTerEnviron +
                " _rfu="+InnerUtils.hexString(ep_opt._rfu)+
                " nIssSresLen="+ ep_opt.nIssSresLen +
                " pusIssScriptRes="+InnerUtils.hexString(ep_opt.pusIssScriptRes)+
                " nField55Len="+ep_opt.nField55Len +
                " pusField55="+InnerUtils.hexString(ep_opt.pusField55));
    }


    public static void toString_rf_transdata(DeviceLogger logger, rf_transdata rf_transdata){
        logger.debug("EmvJNIService->rf_transdata nAmount="+rf_transdata.nAmount+
        " nAmountOther="+rf_transdata.nAmountOther+
        " usDate="+InnerUtils.hexString(rf_transdata.usDate)+
        " usAid="+InnerUtils.hexString(rf_transdata.usAid)+
        " nAidLen="+rf_transdata.nAidLen+
        " usKernelId="+InnerUtils.hexString(rf_transdata.usKernelId)+
        " nFileOffSet="+rf_transdata.nFileOffSet+
        " usSW12="+InnerUtils.hexString(rf_transdata.usSW12)+
        " ucNoAmount="+rf_transdata.ucNoAmount+
        " usResv="+InnerUtils.hexString(rf_transdata.usResv)+
        " ucPreProcessIndicatorLen="+rf_transdata.ucPreProcessIndicatorLen+
        " pusPreProcessIndicator="+InnerUtils.hexString(rf_transdata.pusPreProcessIndicator)+
        " nFciLen="+rf_transdata.nFciLen+
        " pusFinalAidFci="+InnerUtils.hexString(rf_transdata.pusFinalAidFci));
    }

    public static void toString_capk(DeviceLogger logger, capk capk){
        logger.debug("EmvJNIService->capk pk_mod_len="+(capk.pk_mod_len&0xFF) +
        " pk_modulus="+InnerUtils.hexString(capk.pk_modulus) +
        " pk_exponent="+InnerUtils.hexString(capk.pk_exponent) +
        " _hashvalue="+InnerUtils.hexString(capk._hashvalue) +
        " _expired_date="+InnerUtils.hexString(capk._expired_date) +
        " _rid="+InnerUtils.hexString(capk._rid) +
        " _index="+capk._index +
        " _pk_algorithm="+capk._pk_algorithm +
        " _hash_algorithm="+capk._hash_algorithm +
        " _disable="+capk._disable +
        " _resv="+InnerUtils.hexString(capk._resv));
    }

    public static void toString_emv_opt(DeviceLogger logger, emv_opt emvOpt){
        logger.debug("EmvJNIService->emv_opt _trans_type="+emvOpt._trans_type+
        " _seq_to="+emvOpt._seq_to+
        " _request_amt="+emvOpt._request_amt+
        " _force_online_enable="+emvOpt._force_online_enable+
        " _account_type_enable="+emvOpt._account_type_enable+
        " _online_pin="+InnerUtils.hexString(emvOpt._online_pin)+
        " _advice_req="+emvOpt._advice_req+
        " _force_accept_supported="+emvOpt._force_accept_supported+
        " _signature_req="+emvOpt._signature_req+
        " _auth_resp_code="+InnerUtils.hexString(emvOpt._auth_resp_code)+
        " _online_result="+emvOpt._online_result+
        " _trans_ret="+emvOpt._trans_ret +
        " _field55="+InnerUtils.hexString(emvOpt._field55)+
        " _field55_len="+emvOpt._field55_len+
        " _iss_script_res="+InnerUtils.hexString(emvOpt._iss_script_res)+
        " _iss_sres_len="+emvOpt._iss_sres_len);
    }

    public static void toString_emvparam(DeviceLogger logger, emvparam emvparam){

        logger.debug("EmvJNIService->emvparam _tac_default="+InnerUtils.hexString(emvparam._tac_default)+ " _tac_denial="+InnerUtils.hexString(emvparam._tac_denial)+ " _tac_online="+InnerUtils.hexString(emvparam._tac_online));

        logger.debug("EmvJNIService->emvparam _target_percent="+emvparam._target_percent+
        " _max_target_percent="+emvparam._max_target_percent+
        " _threshold_value="+InnerUtils.hexString(emvparam._threshold_value)+
        " _trans_ref_conv="+InnerUtils.hexString(emvparam._trans_ref_conv)+
        " _script_dev_limit="+emvparam._script_dev_limit);

        logger.debug("EmvJNIService->emvparam _ics="+InnerUtils.hexString(emvparam._ics)+ " _aid="+InnerUtils.hexString(emvparam._aid)+ " _aid_len="+emvparam._aid_len);

        logger.debug("EmvJNIService->emvparam _status="+emvparam._status+
        " _ec_indicator="+emvparam._ec_indicator+
        " _type="+emvparam._type+
        " _cap="+InnerUtils.hexString(emvparam._cap)+
        " _add_cap="+InnerUtils.hexString(emvparam._add_cap)+
        " _app_ver="+InnerUtils.hexString(emvparam._app_ver)+
        " _pos_entry="+emvparam._pos_entry+
        " _floorlimit="+InnerUtils.hexString(emvparam._floorlimit)+
        " _acq_id="+InnerUtils.hexString(emvparam._acq_id)+
        " _mer_category_code="+InnerUtils.hexString(emvparam._mer_category_code));

        logger.debug("EmvJNIService->emvparam _merchant_id="+InnerUtils.hexString(emvparam._merchant_id)+
        " _trans_curr_code="+InnerUtils.hexString(emvparam._trans_curr_code)+
        " _trans_curr_exp="+emvparam._trans_curr_exp+
        " _trans_ref_curr_code="+InnerUtils.hexString(emvparam._trans_ref_curr_code)+
        " _trans_ref_curr_exp="+emvparam._trans_ref_curr_exp+
        " _term_country_code="+InnerUtils.hexString(emvparam._term_country_code)+
        " _ifd_serial_num="+InnerUtils.hexString(emvparam._ifd_serial_num)+
        " _terminal_id="+InnerUtils.hexString(emvparam._terminal_id));


        logger.debug("EmvJNIService->emvparam _riskmana_data_len="+emvparam._riskmana_data_len+
        " _riskmana_data="+InnerUtils.hexString(emvparam._riskmana_data)+
        " _merchant_name="+InnerUtils.hexString(emvparam._merchant_name)+
        " _app_sel_indicator="+emvparam._app_sel_indicator+
        " _fallback_posentry="+emvparam._fallback_posentry+
        " _limit_exist="+emvparam._limit_exist+
        " _ec_limit="+InnerUtils.hexString(emvparam._ec_limit)+
        " _cl_limit="+InnerUtils.hexString(emvparam._cl_limit)+
        " _cl_offline_limit="+InnerUtils.hexString(emvparam._cl_offline_limit)+
        " _cvm_limit="+InnerUtils.hexString(emvparam._cvm_limit)+
        " _trans_prop="+InnerUtils.hexString(emvparam._trans_prop)+
        " _status_check="+emvparam._status_check+
        " _appid="+emvparam._appid+
        " _resv_old="+InnerUtils.hexString(emvparam._resv_old));

        logger.debug("EmvJNIService->emvparam MagStripeIndicator="+emvparam.MagStripeIndicator+
        " MagAppVer="+InnerUtils.hexString(emvparam.MagAppVer)+
        " DataExchangeSupport="+emvparam.DataExchangeSupport+
        " KernelConfig="+emvparam.KernelConfig+
        " MaxNumTornLog="+emvparam.MaxNumTornLog+
        " BalanceReadFlag="+emvparam.BalanceReadFlag+
        " PwConfig="+InnerUtils.hexString(emvparam.PwConfig)+
        " CvmReq="+emvparam.CvmReq+
        " DdaVer="+emvparam.DdaVer+
        " ResvPw="+InnerUtils.hexString(emvparam.ResvPw)+
        " KernelId="+InnerUtils.hexString(emvparam.KernelId)+
        " VisaTtqPresent="+emvparam.VisaTtqPresent+
        " StatusCheckSupport="+emvparam.StatusCheckSupport+
        " ZeroAmountAllow="+emvparam.ZeroAmountAllow+
        " ExtendAidSupport="+emvparam.ExtendAidSupport+
        " ClssCardholderVerifyAllow="+emvparam.ClssCardholderVerifyAllow);

        logger.debug("EmvJNIService->emvparam MagStripeCvm="+emvparam.MagStripeCvm+
        " MaxLifetimeTornLog="+InnerUtils.hexString(emvparam.MaxLifetimeTornLog)+
        " MobileSupportIndicator="+emvparam.MobileSupportIndicator+
        " MagStripeNoCvm="+emvparam.MagStripeNoCvm+
        " CapNoCvm="+emvparam.CapNoCvm+
        " EXTerminalCap="+emvparam.EXTerminalCap+
        " EXRandomScope="+emvparam.EXRandomScope+
        " EXTimeExpire="+emvparam.EXTimeExpire+
        " TerminalPriority="+emvparam.TerminalPriority+
        " TransType="+emvparam.TransType+
        " CombinationOP="+InnerUtils.hexString(emvparam.CombinationOP)+
        " TIP="+InnerUtils.hexString(emvparam.TIP)+
        " TransTypeCheckFlag="+emvparam.TransTypeCheckFlag+
        " Resv="+InnerUtils.hexString(emvparam.Resv));


        logger.debug("EmvJNIService->emvparam PPTlvLen="+emvparam.PPTlvLen+ " PPTlv="+InnerUtils.hexString(emvparam.PPTlv));

        logger.debug("EmvJNIService->emvparam DrlStatus="+emvparam.DrlStatus+ " DrlData="+InnerUtils.hexString(emvparam.DrlData));

        logger.debug("EmvJNIService->emvparam _default_ddol_len="+emvparam._default_ddol_len+ " _default_ddol="+InnerUtils.hexString(emvparam._default_ddol));

        logger.debug("EmvJNIService->emvparam _default_tdol_len="+emvparam._default_tdol_len+ " _default_tdol="+InnerUtils.hexString(emvparam._default_tdol));

        logger.debug("EmvJNIService->emvparam DefaultUdolLen="+emvparam.DefaultUdolLen+ " DefaultUdol="+InnerUtils.hexString(emvparam.DefaultUdol));
    }
}
