package com.newland.sdk.me.module.usb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class OTGUtils {

    private final static String OTGFile = "sys/class/usb_ctrl/otg_mode";

    public static boolean isOTGOpen() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(OTGFile));
            String status = reader.readLine().trim();
            return "1".equals(status);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void closeOTG() {
        setFileValue("0");
    }

    public static void openOTG() {
        setFileValue("1");
    }

    private static void setFileValue(String value) {
        try {
            setFileValue(OTGFile, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setFileValue(String FileName, String value) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FileName));
            writer.write(value);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
