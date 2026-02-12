package com.newland.sdk.utils;

import android.os.Bundle;

import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.util.Dump;
import com.newland.sdk.mtypex.tlv.SimpleTLVMsg;
import com.newland.sdk.mtypex.tlv.SimpleTLVPackage;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.StringTokenizer;

/**
 * ISO utility class
 *
 * @since ver3.10.01
 */
public class ISOUtils {
    private ISOUtils() {
        throw new AssertionError();
    }

    private static final String[] hexStrings;

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
     * padding the specified char to the left of the data.
     *
     * @param s   - original string
     * @param len - desired length
     * @param c   - padding char
     * @return padded string
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

    /**
     * padding the specified char to the right of the data.
     *
     * @param s   -original string
     * @param len -desired length
     * @param c   -padding char
     * @return padded string
     */
    public static String padright(String s, int len, char c) {
        s = s.trim();
        if (s.length() > len)
            throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "invalid param:" + s + " invalid len " + s.length() + "/" + len);
        StringBuilder d = new StringBuilder(len);
        int fill = len - s.length();
        d.append(s);
        while (fill-- > 0)
            d.append(c);
        return d.toString();
    }


    /**
     * convert string to BCD.
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
     * @param b      source byte array
     * @param offset starting offset
     * @param len    number of bytes in destination (processes len*2)
     * @return byte[len]
     */
    private static byte[] hex2byte(byte[] b, int offset, int len) {
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
     * Concatenates two byte arrays (array1 and array2)
     *
     * @param array1 Source data.
     * @param array2 Source data.
     * @return the concatenated array
     */
    public static byte[] concat(byte[] array1, byte[] array2) {
        byte[] concatArray = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, concatArray, 0, array1.length);
        System.arraycopy(array2, 0, concatArray, array1.length, array2.length);
        return concatArray;
    }

    /**
     * Return true if the string represent a number
     * in the specified radix.
     * <br><br>
     **/
    public static boolean isNumeric(String s, int radix) {
        int i = 0, len = s.length();
        while (i < len && Character.digit(s.charAt(i), radix) > -1) {
            i++;
        }
        return (i >= len && len > 0);
    }


    /**
     * get a instance of TLVPackage.{@link TLVPackage}
     *
     * @return
     */
    public static TLVPackage newTlvPackage() {
        return new SimpleTLVPackage();
    }

    /**
     * get a instance of TLVMsg.{@link TLVMsg}
     *
     * @return
     */
    public static TLVMsg newTlvMsg() {
        return new SimpleTLVMsg();
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
    private static byte[] str2bcd(String s, boolean padLeft, byte[] d, int offset) {
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
     * convert bundle to string
     * @param bundle
     * @return
     */
    public static String bundleToString(Bundle bundle) {
        if (bundle == null) return null;
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (String key : bundle.keySet()) {
            if (sb.length() > 1) sb.append(',');
            Object v = bundle.get(key);
            v = (v instanceof String[]) ? Arrays.asList((String[]) v) : v;
            v = (v instanceof byte[]) ? (((byte[]) v).length > 200 ? "too long" : hexString((byte[]) v)) : v;
            sb.append(key).append('=').append(v);
        }
        return sb.append('}').toString();
    }
}
