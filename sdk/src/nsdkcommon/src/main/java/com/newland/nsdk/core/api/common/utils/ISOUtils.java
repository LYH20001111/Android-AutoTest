package com.newland.nsdk.core.api.common.utils;

/**
 * Tool class for converting between buffer and string.
 */
public class ISOUtils {
    public static final String[] hexStrings;
    private static final byte[] highDigits;
    private static final byte[] lowDigits;

    // initialize lookup tables
    static {
        final byte[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        int i;
        byte[] high = new byte[256];
        byte[] low = new byte[256];

        for (i = 0; i < 256; i++) {
            high[i] = digits[i >>> 4];
            low[i] = digits[i & 0x0F];
        }

        highDigits = high;
        lowDigits = low;
    }

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
     * Convert a byte buffer to a hex string in upper case.
     *
     * <p>Example:</p>
     * <pre>
     *     byte[] data = new byte[]{0x12, 0x34, 0xdf, 0x2e};
     *     String result = hexString(data);
     *     // The result will be: "1234DF2E"
     *
     *     data = null;
     *     result = hexString(data);
     *     // The result will be an empty string.
     *
     *     data = new byte[0];
     *     result = hexString(data);
     *     // The result will be an empty string.
     * </pre>
     *
     * @param data The byte buffer to convert to hex string.
     * @return Hex string of the data buffer. Returns empty string when the buffer is null or empty.
     */
    public static String hexString(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }
        StringBuilder d = new StringBuilder(data.length * 2);
        for (byte b : data) {
            d.append(hexStrings[(int) b & 0xFF]);
        }
        return d.toString();
    }

    /**
     * This is mainly used for track data. If there is "d" in the track data, it will be converted to "=".
     *
     * <p>Example:</p>
     * <pre>
     *     byte[] data = new byte[]{0x12, 0x34, 0x56, 0x78};
     *     String result = bcd2str(data, 0, 5, true);
     *     // The result will be: "23456"
     *
     *     String result = bcd2str(data, 0, 5, false);
     *     // The result will be: "12345"
     * </pre>
     *
     * @param data    The byte buffer to convert.
     * @param offset  Indicates the index of the start byte in data.
     * @param len     The length of the result string. If the length is more than the length from the offset to the end of the data, it will only return the string from the offset to the end of the data.
     * @param padLeft This will work when the length is odd. For example, when data is {0x12, 0x34, 0x56}, offset is 1, length is 3:
     *                <ul>
     *                    <li>Set this param to true: it will start to convert from the right number of the second byte(0x34), that is "4", the result will be "456".</li>
     *                    <li>Set this param to false: it will start to convert from the left number of the second byte(0x34), that is "3", the result will be "345".</li>
     *                </ul>
     *                When the length is event, e.g., data is {0x12, 0x34, 0x56}, offset is 1, length is 4, the result will always be "3456".
     * @return Hex string of the data buffer in upper case. Returns empty string when:
     * <ul>
     *     <li>The data buffer is null or empty.</li>
     *     <li>offset is bigger than the length of the data buffer.</li>
     * </ul>
     */
    public static String bcd2str(byte[] data, int offset, int len, boolean padLeft) {
        if (data == null || data.length == 0 || len <= 0) {
            return "";
        }
        if (offset >= data.length || offset < 0) {
            return "";
        }

        StringBuilder d = new StringBuilder(len);
        int start = (((len & 1) == 1) && padLeft) ? 1 : 0;
        for (int i = start; i < len + start; i++) {
            int shift = ((i & 1) == 1 ? 0 : 4);
            int currentOffset = offset + (i >> 1);
            if (currentOffset >= data.length) {
                break;
            }
            char c = Character.forDigit(
                    ((data[currentOffset] >> shift) & 0x0F), 16);
            if (c == 'd') {
                c = '=';
            }
            d.append(Character.toUpperCase(c));
        }
        return d.toString();
    }

    /**
     * Convert the byte buffer from the offset to a readable character string expressed in hexadecimal system.
     *
     * <p>Example: </p>
     * <pre>
     *     byte[] data = new byte[]{0x12, 0x34, 0x56, 0x78};
     *     String result = getHexDump(data, 1, 3);
     *     // The result will be: "34 56 78"
     *
     *     result = getHexDump("123.?".getBytes(), 1, 2);
     *     // The result will be: "32 33"
     * </pre>
     *
     * @param data   Bytes entered
     * @param offset Indicates the index of the start byte in data.
     * @param length How many bytes from the offset that converted to hex string.
     * @return Hex string of data buffer in upper case. Returns empty string when:
     * <ul>
     *     <li>The data buffer is null or empty.</li>
     *     <li>Offset is <0 or bigger than the length of the data buffer.</li>
     * </ul>
     */
    public static String getHexDump(byte[] data, int offset, int length) {
        if (data == null || data.length == 0 || length <= 0) {
            return "";
        }
        if (offset >= data.length || offset < 0) {
            return "";
        }

        StringBuffer out = new StringBuffer();

        int byteValue = data[offset] & 0xFF;
        out.append((char) highDigits[byteValue]);
        out.append((char) lowDigits[byteValue]);

        for (int i = offset + 1; (i < data.length && (i - offset) < length); i++) {
            out.append(' ');
            byteValue = data[i] & 0xFF;
            out.append((char) highDigits[byteValue]);
            out.append((char) lowDigits[byteValue]);
        }
        return out.toString();
    }

    /**
     * Convert the byte buffer into a readable character string expressed in hexadecimal system.
     *
     * <p>Example: </p>
     * <pre>
     *     byte[] data = new byte[]{0x12, 0x34, 0x56, 0x78};
     *     String result = getHexDump(data);
     *     // The result will be: "12 34 56 78"
     *
     *     result = getHexDump("123.?".getBytes());
     *     // The result will be: "31 32 33 2E 3F"
     * </pre>
     *
     * @param bytes Byte entered
     * @return Hex string of data buffer in upper case. Returns empty string when:
     * <ul>
     *     <li>The data buffer is null or empty.</li>
     *     <li>offset is bigger than the length of the data buffer.</li>
     * </ul>
     * @since ver3.10.01
     */
    public static String getHexDump(byte[] bytes) {
        return getHexDump(bytes, 0, bytes.length);
    }


    /**
     * Convert a hex string to a byte buffer.
     *
     * <p>Example:</p>
     * <pre>
     *      byte[] result = hex2byte("1234abCD");
     *      // The result will be: {0x12, 0x34, 0xAB, 0xCD}
     *
     *      result = hex2byte("12345");
     *      // The result will be: {0x01, 0x23, 0x45}
     *
     *      result = hex2byte("123.?;2Da48/@#");
     *      // The result will be: {0x12, 0xFF, 0xFF, 0x2D, 0xA4, 0xFF, 0xFF}
     * </pre>
     *
     * @param s The hex string that only contains [0-9][a-f][A-F]. If there are unsupported characters, they will be converted to byte 0xFF.
     * @return Byte buffer of the hex string. Returns null when the hex string is null or empty.
     */
    public static byte[] hex2byte(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }

        if (s.length() % 2 == 0) {
            return hex2byte(s.getBytes(), 0, s.length() >> 1);
        } else {
            // Padding left zero to make it even size #Bug raised by tommy
            return hex2byte("0" + s);
        }
    }

    private static byte[] hex2byte(byte[] b, int offset, int len) {
        byte[] d = new byte[len];
        for (int i = 0; i < len * 2; i++) {
            int shift = i % 2 == 1 ? 0 : 4;
            d[i >> 1] |= Character.digit((char) b[offset + i], 16) << shift;
        }
        return d;
    }
}
