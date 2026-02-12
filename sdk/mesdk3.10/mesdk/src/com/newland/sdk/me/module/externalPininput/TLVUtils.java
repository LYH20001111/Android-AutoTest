package com.newland.sdk.me.module.externalPininput;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class TLVUtils {
    public TLVUtils() {
    }

    public static String[] getTLVList(String tlv) {
        int current = 0;
        int lenValue = 0;
        int pre = 0;
        byte[] data = BytesUtils.hexToBytes(tlv);

        ArrayList list;
        for(list = new ArrayList(); current < data.length; pre = current) {
            int tagLen = getTagLen(data, current);
            current += tagLen;
            if ((data[current] & 128) == 128) {
                int tmpLen = data[current] & 127;
                switch (tmpLen) {
                    case 1:
                        lenValue = data[current + 1] & 255;
                        break;
                    case 2:
                        lenValue = data[current + 1] << 8 & '\uff00' + (data[current + 2] & 255);
                        break;
                    case 3:
                        lenValue = data[current + 1] << 16 & 16711680 + (data[current + 2] << 8) & '\uff00' + (data[current + 3] & 255);
                }

                current += tmpLen + 1;
            } else {
                lenValue = data[current] & 255;
                ++current;
            }

            current += lenValue;
            list.add(BytesUtils.bytesToHex(data, pre, current - pre));
        }

        return (String[])list.toArray(new String[0]);
    }

    public static EmvTLV[] getTLVList(byte[] tlvData) {
        int current = 0;
        int lenValue = 0;
        List<EmvTLV> list = new ArrayList();

        while(current < tlvData.length) {
            EmvTLV item = new EmvTLV();
            int tagLen = getTagLen(tlvData, current);
            item.setTag(BytesUtils.bytesToInt(tlvData, current, tagLen));
            current += tagLen;
            if ((tlvData[current] & 128) == 128) {
                int tmpLen = tlvData[current] & 127;
                switch (tmpLen) {
                    case 1:
                        lenValue = tlvData[current + 1] & 255;
                        break;
                    case 2:
                        lenValue = tlvData[current + 1] << 8 & '\uff00' + (tlvData[current + 2] & 255);
                        break;
                    case 3:
                        lenValue = tlvData[current + 1] << 16 & 16711680 + (tlvData[current + 2] << 8) & '\uff00' + (tlvData[current + 3] & 255);
                }

                current += tmpLen + 1;
            } else {
                lenValue = tlvData[current] & 255;
                ++current;
            }

            item.setLen(lenValue);
            byte[] value = new byte[lenValue];
            System.arraycopy(tlvData, current, value, 0, lenValue);
            item.setValue(value);
            current += lenValue;
            list.add(item);
        }

        return (EmvTLV[])list.toArray(new EmvTLV[0]);
    }

    public static byte[] getValueFromTLVlist(int tag, EmvTLV[] list) {
        boolean isFind = false;

        int i;
        for(i = 0; i < list.length; ++i) {
            if (tag == list[i].getTag()) {
                isFind = true;
                break;
            }
        }

        return isFind ? list[i].getValue() : null;
    }

    private static int getTagLen(byte[] input, int offset) {
        int tagLen = 1;

        for(int i = 0; i < 2; ++i) {
            byte b = input[i + offset];
            if ((b & 31) != 31) {
                break;
            }

            ++tagLen;
        }

        return tagLen;
    }

    public static int getTagLen(String input) {
        int tagLen = 1;

        for(int i = 0; i < 2; ++i) {
            int b = Integer.valueOf(input.substring(i * 2, i * 2 + 2), 16);
            if ((b & 31) != 31) {
                break;
            }

            ++tagLen;
        }

        return tagLen;
    }

    public static int getTagLen(int tag) {
        for(int i = 2; i > 0; --i) {
            if ((tag >> i * 8 & 255) != 0) {
                return i + 1;
            }
        }

        return 1;
    }

    public static PackTLV newPackTLV() {
        return new PackTLV();
    }

    public static class PackTLV {
        private ByteArrayOutputStream bos;

        private PackTLV() {
            this.bos = new ByteArrayOutputStream();
        }

        public void append(int tag, byte[] value) {
            if (this.bos == null) {
                throw new IllegalArgumentException("pack is already close");
            } else if (value == null) {
//                LoggerUtils.d("dd append->value is null.!!!!!!!");
            } else {
                boolean mark = false;

                byte tmp;
                int i;
                for(i = 3; i >= 0; --i) {
                    tmp = (byte)(tag >> i * 8 & 255);
                    if (tmp != 0) {
                        mark = true;
                    }

                    if (mark) {
                        this.bos.write(tmp);
                    }
                }

                int len = value.length;
                if (len <= 127) {
                    this.bos.write(len);
                } else {
                    for(i = 3; i >= 0; --i) {
                        tmp = (byte)(len >> i * 8 & 255);
                        if (tmp != 0) {
                            break;
                        }
                    }

                    tmp = (byte)(128 | i + 1);
                    this.bos.write(tmp);

                    while(i >= 0) {
                        tmp = (byte)(len >> i * 8 & 255);
                        this.bos.write(tmp);
                        --i;
                    }
                }

                try {
                    this.bos.write(value);
                } catch (IOException var8) {
                    IOException e = var8;
                    e.printStackTrace();
                }

            }
        }

        public void append(int tag, String value) {
            if (value == null || value.length() == 0) {
                this.append(tag, new byte[0]);
            } else {
                try {
                    this.append(tag, value.getBytes("GBK"));
                } catch (UnsupportedEncodingException var4) {
                    UnsupportedEncodingException e = var4;
                    e.printStackTrace();
                    throw new IllegalArgumentException("data is UnsupportedEncodingException:" + e.getMessage());
                }
            }
        }

        public byte[] pack() {
            if (this.bos == null) {
                throw new IllegalArgumentException("pack is already close");
            } else {
                byte[] var1;
                try {
                    var1 = this.bos.toByteArray();
                } finally {
                    if (this.bos != null) {
                        try {
                            this.bos.close();
                        } catch (IOException var8) {
                            IOException e = var8;
                            e.printStackTrace();
                        }

                        this.bos = null;
                    }

                }

                return var1;
            }
        }
    }
}