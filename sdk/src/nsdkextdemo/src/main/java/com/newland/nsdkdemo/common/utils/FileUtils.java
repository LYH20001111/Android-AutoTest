package com.newland.nsdkdemo.common.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {
    public static String readFromAssets(Context context, String fileName){
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName), "utf-8");
            BufferedReader bufReader = new BufferedReader(inputReader);
            StringBuilder stringBuilder = new StringBuilder();
            String tempString;
            while ((tempString = bufReader.readLine()) != null) {
                if ("-----END CERTIFICATE-----".equals(tempString)) {
                    stringBuilder.append(tempString);
                } else {
                    stringBuilder.append(tempString).append("\r\n");
                }
            }
            bufReader.close();
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] readPicDataFromAssets(Context context, String fileName) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream in = assetManager.open(fileName);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] tempbytes = new byte[in.available()];
            for (int i = 0; (i = in.read(tempbytes)) != -1;) {
                baos.write(tempbytes, 0, i);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
