package api;

import com.newland.nsdk.core.api.common.utils.ISOUtils;

import org.junit.Assert;
import org.junit.Test;

public class ISOUtilTest {
    @Test
    public void bcd2str(){
        byte[] data = new byte[] {0x12, 0x34, 0x56, 0x78, 0x19};
        String s1 = ISOUtils.bcd2str(data, 1, 6, true);
        String s2 = ISOUtils.bcd2str(data, 1, 6, false);
        System.out.println(s1);
        System.out.println(s2);
        String s3 = ISOUtils.bcd2str(data, 1, 5, true);
        String s4 = ISOUtils.bcd2str(data, 1, 5, false);
        System.out.println(s3);
        System.out.println(s4);
        String s5 = ISOUtils.bcd2str(data, 1, 7, true);
        String s6 = ISOUtils.bcd2str(data, 1, 7, false);
        System.out.println(s5);
        System.out.println(s6);
        String s7 = ISOUtils.bcd2str(data, 1, 8, true);
        String s8 = ISOUtils.bcd2str(data, 1, 8, false);
        System.out.println(s7);
        System.out.println(s8);
        String s9 = ISOUtils.bcd2str(data, 1, 9, true);
        String s10 = ISOUtils.bcd2str(data, 1, 9, false);
        System.out.println(s9);
        System.out.println(s10);
        String s11 = ISOUtils.bcd2str(data, 1, 0, false);
        System.out.println(s11);
        String s12 = ISOUtils.bcd2str(data, 1, -1, false);
        System.out.println(s12);
        String s13 = ISOUtils.bcd2str(data, -1, 5, false);
        System.out.println(s13);
    }

    @Test
    public void getHexDump(){
        byte[] data = new byte[] {0x12, 0x34, 0x56, 0x78, 0x19};
        String s1 = ISOUtils.getHexDump(data, -1, 5);
        Assert.assertEquals(s1, "");

        String s2 = ISOUtils.getHexDump(data, 1, 0);
        Assert.assertEquals(s2, "");

        String s3 = ISOUtils.getHexDump(data, 1, -1);
        Assert.assertEquals(s3, "");
//        System.out.println(s1);
    }
}
