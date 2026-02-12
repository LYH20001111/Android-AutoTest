//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.newland.sdk.me.module.externalPininput;

import java.nio.charset.Charset;
import java.util.Arrays;

public class BytesUtils {
    private static final String GBK = "GBK";
    private static final String HEX = "0123456789ABCDEF";
    private static final char[] ASCII = "0123456789ABCDEF".toCharArray();

    public BytesUtils() {
    }

    public static byte toByte(char hexC) {
        String s = String.valueOf(hexC);
        return (byte)"0123456789ABCDEF".indexOf(s.toUpperCase());
    }

    public static byte[] getBytes(short data) {
        byte[] bytes = new byte[]{(byte)((data & '\uff00') >> 8), (byte)(data & 255)};
        return bytes;
    }

    public static byte[] getBytes(char data) {
        byte[] bytes = new byte[]{(byte)(data >> 8), (byte)data};
        return bytes;
    }

    public static byte[] getBytes(boolean data) {
        byte[] bytes = new byte[]{(byte)(data ? 1 : 0)};
        return bytes;
    }

    public static byte[] getBytes(int data) {
        byte[] bytes = new byte[]{(byte)((data & -16777216) >> 24), (byte)((data & 16711680) >> 16), (byte)((data & '\uff00') >> 8), (byte)(data & 255)};
        return bytes;
    }

    public static byte[] getBytes(long data) {
        byte[] bytes = new byte[]{(byte)((int)(data >> 56 & 255L)), (byte)((int)(data >> 48 & 255L)), (byte)((int)(data >> 40 & 255L)), (byte)((int)(data >> 32 & 255L)), (byte)((int)(data >> 24 & 255L)), (byte)((int)(data >> 16 & 255L)), (byte)((int)(data >> 8 & 255L)), (byte)((int)(data & 255L))};
        return bytes;
    }

    public static byte[] getBytes(float data) {
        int intBits = Float.floatToIntBits(data);
        return getBytes(intBits);
    }

    public static byte[] getBytes(double data) {
        long intBits = Double.doubleToLongBits(data);
        return getBytes(intBits);
    }

    public static byte[] getBytes(String data, String charsetName) {
        Charset charset = Charset.forName(charsetName);
        return data.getBytes(charset);
    }

    public static byte[] getBytes(String data) {
        return getBytes(data, "GBK");
    }

    public static boolean getBoolean(byte[] bytes) {
        return bytes[0] == 1;
    }

    public static boolean getBoolean(byte[] bytes, int index) {
        return bytes[index] == 1;
    }

    public static short getShort(byte[] bytes) {
        return (short)('\uff00' & bytes[0] << 8 | 255 & bytes[1]);
    }

    public static short getShort(byte[] bytes, int startIndex) {
        return (short)('\uff00' & bytes[startIndex] << 8 | 255 & bytes[startIndex + 1]);
    }

    public static char getChar(byte[] bytes) {
        return (char)('\uff00' & bytes[0] << 8 | 255 & bytes[1]);
    }

    public static char getChar(byte[] bytes, int startIndex) {
        return (char)('\uff00' & bytes[startIndex] << 8 | 255 & bytes[startIndex + 1]);
    }

    public static int getInt(byte[] bytes) {
        return -16777216 & bytes[0] << 24 | 16711680 & bytes[1] << 16 | '\uff00' & bytes[2] << 8 | 255 & bytes[3];
    }

    public static int getInt(byte[] bytes, int startIndex) {
        return -16777216 & bytes[startIndex] << 24 | 16711680 & bytes[startIndex + 1] << 16 | '\uff00' & bytes[startIndex + 2] << 8 | 255 & bytes[startIndex + 3];
    }

    private static long getLong(byte[] bytes) {
        return -72057594037927936L & (long)bytes[0] << 56 | 71776119061217280L & (long)bytes[1] << 48 | 280375465082880L & (long)bytes[2] << 40 | 1095216660480L & (long)bytes[3] << 32 | 4278190080L & (long)bytes[4] << 24 | 16711680L & (long)bytes[5] << 16 | 65280L & (long)bytes[6] << 8 | 255L & (long)bytes[7];
    }

    public static long getLong(byte[] bytes, int startIndex) {
        return -72057594037927936L & (long)bytes[startIndex] << 56 | 71776119061217280L & (long)bytes[startIndex + 1] << 48 | 280375465082880L & (long)bytes[startIndex + 2] << 40 | 1095216660480L & (long)bytes[startIndex + 3] << 32 | 4278190080L & (long)bytes[startIndex + 4] << 24 | 16711680L & (long)bytes[startIndex + 5] << 16 | 65280L & (long)bytes[startIndex + 6] << 8 | 255L & (long)bytes[startIndex + 7];
    }

    public static float getFloat(byte[] bytes) {
        return Float.intBitsToFloat(getInt(bytes));
    }

    public static float getFloat(byte[] bytes, int startIndex) {
        byte[] result = new byte[4];
        System.arraycopy(bytes, startIndex, result, 0, 4);
        return Float.intBitsToFloat(getInt(result));
    }

    public static double getDouble(byte[] bytes) {
        long l = getLong(bytes);
        return Double.longBitsToDouble(l);
    }

    public static double getDouble(byte[] bytes, int startIndex) {
        byte[] result = new byte[8];
        System.arraycopy(bytes, startIndex, result, 0, 8);
        long l = getLong(result);
        return Double.longBitsToDouble(l);
    }

    public static String getString(byte[] bytes, String charsetName) {
        return new String(bytes, Charset.forName(charsetName));
    }

    public static String getString(byte[] bytes) {
        return getString(bytes, "GBK");
    }

    public static byte[] str2bcd(String hex, boolean isLeft) {
        if (hex != null && !"".equals(hex)) {
            if (hex.length() % 2 != 0) {
                // if (StringUtils.isEmpty("0"))
                if ("0" == null || "0".length() == 0) {
                    hex = hex + "0";
                } else if (isLeft) {
                    hex = "0" + hex;
                } else {
                    hex = hex + "0";
                }
            }

            int len = hex.length() / 2;
            byte[] result = new byte[len];
            char[] chArr = hex.toUpperCase().toCharArray();

            for(int i = 0; i < len; ++i) {
                int pos = i * 2;
                result[i] = (byte)(toByte(chArr[pos]) << 4 | toByte(chArr[pos + 1]));
            }

            return result;
        } else {
            return null;
        }
    }

    public static byte[] hexToBytes(String hex) {
        if (hex != null && !"".equals(hex)) {
            if (hex.length() % 2 != 0) {
                throw new IllegalArgumentException("input string should be any multiple of 2!");
            } else {
                int len = hex.length() / 2;
                byte[] result = new byte[len];
                char[] chArr = hex.toUpperCase().toCharArray();

                for(int i = 0; i < len; ++i) {
                    int pos = i * 2;
                    result[i] = (byte)(toByte(chArr[pos]) << 4 | toByte(chArr[pos + 1]));
                }

                return result;
            }
        } else {
            return null;
        }
    }

    public static String bcdToString(byte[] bcds) {
        if (bcds != null && bcds.length != 0) {
            byte[] temp = new byte[2 * bcds.length];

            for(int i = 0; i < bcds.length; ++i) {
                temp[i * 2] = (byte)(bcds[i] >> 4 & 15);
                temp[i * 2 + 1] = (byte)(bcds[i] & 15);
            }

            StringBuilder res = new StringBuilder();
            byte[] var3 = temp;
            int var4 = temp.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                byte b = var3[var5];
                res.append(ASCII[b]);
            }

            return res.toString();
        } else {
            return null;
        }
    }

    public static int bcdToInt(byte value) {
        return (value >> 4) * 10 + (value & 15);
    }

    public static String bytesToHex(byte[] bs) {
        StringBuilder sb = new StringBuilder();
        byte[] var2 = bs;
        int var3 = bs.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            byte b = var2[var4];
            int high = b >> 4 & 15;
            int low = b & 15;
            sb.append("0123456789ABCDEF", high, high + 1);
            sb.append("0123456789ABCDEF", low, low + 1);
        }

        return sb.toString();
    }

    public static String bytesToHex(byte[] bs, int len) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < len; ++i) {
            byte b = bs[i];
            int high = b >> 4 & 15;
            int low = b & 15;
            sb.append("0123456789ABCDEF", high, high + 1);
            sb.append("0123456789ABCDEF", low, low + 1);
        }

        return sb.toString();
    }

    public static String bytesToHex(byte[] bs, int offset, int len) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < len; ++i) {
            byte b = bs[offset + i];
            int high = b >> 4 & 15;
            int low = b & 15;
            sb.append("0123456789ABCDEF", high, high + 1);
            sb.append("0123456789ABCDEF", low, low + 1);
        }

        return sb.toString();
    }

    public static String byteToHex(byte b) {
        int high = b >> 4 & 15;
        int low = b & 15;
        return "0123456789ABCDEF".substring(high, high + 1) + "0123456789ABCDEF".substring(low, low + 1);
    }

    public static String negate(byte[] src) {
        if (src != null && src.length != 0) {
            byte[] temp = new byte[2 * src.length];

            for(int i = 0; i < src.length; ++i) {
                byte tmp = (byte)(255 ^ src[i]);
                temp[i * 2] = (byte)(tmp >> 4 & 15);
                temp[i * 2 + 1] = (byte)(tmp & 15);
            }

            StringBuilder res = new StringBuilder();
            byte[] var8 = temp;
            int var4 = temp.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                byte b = var8[var5];
                res.append(ASCII[b]);
            }

            return res.toString();
        } else {
            return null;
        }
    }

    public static byte[] xor(byte[] a, byte[] b) {
        if (a != null && a.length != 0 && b != null && b.length != 0 && a.length == b.length) {
            byte[] result = new byte[a.length];

            for(int i = 0; i < a.length; ++i) {
                result[i] = (byte)(a[i] ^ b[i]);
            }

            return result;
        } else {
            return null;
        }
    }

    public static byte[] xor(byte[] a, byte[] b, int len) {
        if (a != null && a.length != 0 && b != null && b.length != 0) {
            if (a.length >= len && b.length >= len) {
                byte[] result = new byte[len];

                for(int i = 0; i < len; ++i) {
                    result[i] = (byte)(a[i] ^ b[i]);
                }

                return result;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static byte[] shortToBytes(int num) {
        byte[] temp = new byte[2];

        for(int i = 0; i < 2; ++i) {
            temp[i] = (byte)(num >>> 8 - i * 8 & 255);
        }

        return temp;
    }

    public static byte[] intToBytes(int integer) {
        byte[] temp = new byte[4];

        for(int i = 0; i < 4; ++i) {
            temp[i] = (byte)(integer >>> 24 - i * 8 & 255);
        }

        return temp;
    }

    public static byte[] intToBytes(int integer, int byteSize) {
        if (byteSize >= 1 && byteSize <= 4) {
            byte[] temp = new byte[byteSize];

            for(int i = 0; i < byteSize; ++i) {
                temp[byteSize - 1 - i] = (byte)(integer >>> 8 * i & 255);
            }

            return temp;
        } else {
            return null;
        }
    }

    public static byte[] longToBytes(long longint) {
        byte[] temp = new byte[8];

        for(int i = 0; i < 8; ++i) {
            int rightBit = (7 - i) * 8;
            temp[i] = (byte)((int)(longint >>> rightBit & 255L));
        }

        return temp;
    }

    public static long bytesToLong(byte[] bytes) {
        if (bytes.length > 8) {
            throw new IllegalArgumentException("Byte array length must be less than 8");
        } else {
            long result = 0L;
            int end = bytes.length;

            for(int i = end - 1; i >= 0; --i) {
                byte b = bytes[i];
                int leftBit = (end - 1 - i) * 8;
                result |= (long)((b & 255) << leftBit);
            }

            return result;
        }
    }

    public static int bytesToInt(byte[] bytes) {
        if (bytes.length > 4) {
            throw new IllegalArgumentException("Byte array length must be less than 4");
        } else {
            int result = 0;
            int end = bytes.length;

            for(int i = end - 1; i >= 0; --i) {
                byte b = bytes[i];
                int leftBit = (end - 1 - i) * 8;
                result |= (b & 255) << leftBit;
            }

            return result;
        }
    }

    public static int bytesToInt(byte[] bytes, int offset, int len) {
        if (len > 4) {
            throw new IllegalArgumentException("len must be less than 4");
        } else if (bytes.length < offset + len) {
            throw new IllegalArgumentException("bytes is less than (offset + len)");
        } else {
            int result = 0;
            int end = Math.min(len, 4) + offset;

            for(int i = end - 1; i >= offset; --i) {
                byte b = bytes[i];
                int leftBit = (end - 1 - i) * 8;
                result |= (b & 255) << leftBit;
            }

            return result;
        }
    }

    public static short bytesToShort(byte[] bytes) {
        if (bytes.length > 2) {
            throw new IllegalArgumentException("Byte array length must be less than 2");
        } else {
            short result = 0;
            int end = bytes.length;

            for(int i = end - 1; i >= 0; --i) {
                byte b = bytes[i];
                int leftBit = (end - 1 - i) * 8;
                result = (short)(result | (b & 255) << leftBit);
            }

            return result;
        }
    }

    public static String bytesToBinaryString(byte[] items) {
        if (items != null && items.length != 0) {
            StringBuilder sb = new StringBuilder();
            byte[] var2 = items;
            int var3 = items.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                byte item = var2[var4];
                sb.append(byteToBinaryString(item));
            }

            return sb.toString();
        } else {
            return null;
        }
    }

    private static String byteToBinaryString(byte item) {
        byte a = item;
        StringBuilder buf = new StringBuilder();

        for(int i = 0; i < 8; ++i) {
            buf.insert(0, a % 2);
            a = (byte)(a >> 1);
        }

        return buf.toString();
    }

    public static byte checkXorSum(byte[] bytes) {
        byte sum = 0;
        byte[] var2 = bytes;
        int var3 = bytes.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            byte b = var2[var4];
            sum ^= b;
        }

        return sum;
    }

    public static String trimCharLeft(String src, char c) {
        String beTrim = String.valueOf(c);
        src = src.trim();

        for(String beginChar = src.substring(0, 1); beginChar.equalsIgnoreCase(beTrim); beginChar = src.substring(0, 1)) {
            src = src.substring(1);
        }

        return src;
    }

    public static String trimCharRight(String src, char c) {
        String beTrim = String.valueOf(c);
        src = src.trim();

        for(String endChar = src.substring(src.length() - 1); endChar.equalsIgnoreCase(beTrim); endChar = src.substring(src.length() - 1)) {
            src = src.substring(0, src.length() - 1);
        }

        return src;
    }

    public static byte[] merge(byte[] src, byte b) {
        byte[] result = Arrays.copyOf(src, src.length + 1);
        result[src.length] = b;
        return result;
    }

    public static byte[] merge(byte[] src, byte[]... adds) {
        byte[] result = src;
        byte[][] var3 = adds;
        int var4 = adds.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            byte[] add = var3[var5];
            int start = result.length;
            result = Arrays.copyOf(result, result.length + add.length);
            System.arraycopy(add, 0, result, start, add.length);
        }

        return result;
    }
}
