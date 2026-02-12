package com.newland.sdk.module.font;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description
 * @Author wuhh
 * @Date 2020/11/13
 */
public class FontUtils {
    private static final String TAG = "FontUtils";

    public byte[] getDot_16x16(Context context,String msg){
        return getDot(context,msg,"Font_16x16");
    }
    public byte[] getDot(Context context,String msg,String fontName){
        try {
            if(context == null || msg == null || msg.equals("")){
                Log.d(TAG,"getDot msg="+msg+" context="+context);
                return null;
            }
            String path = getFontPath(context,fontName);
            if(path == null){
                Log.d(TAG,"getDot error.");
                return null;
            }
            int chineseNum = getChineseNum(msg);
            int asiccNum = msg.length() - chineseNum;
            String[] wh = fontName.split("_")[1].split("x");
            int width = Integer.parseInt(wh[0]);
            int height = Integer.parseInt(wh[1]);

            int chinese1Size = width*height/8;//(16*16)bit/8=32B
            int dotLen = chineseNum*chinese1Size + chinese1Size/2*asiccNum;
            byte[] dotData = new byte[dotLen];
            int allWidth = (chineseNum*2+asiccNum)*(width/2);//10*8 CW:16
            Log.d(TAG, "getDot: path="+path+" msg="+msg+" chineseNum="+chineseNum+" asiccNum="+asiccNum+" dotLen="+dotLen+" allWidth="+allWidth);
            int ret = getDot(path,msg,allWidth,height,dotLen,dotData);
            if(ret < 0){
                Log.d(TAG,"getDot ret="+ret);
                return null;
            }
            return dotData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getFontPath(Context context, String name) {
        try {
            if (name == null || context == null) {
                return null;
            }
            String fileName;
            boolean isFileOK = false;
            // search in system dir.
            fileName = "/system/fonts/" + name;
            isFileOK = FileUtils.isFileExists(fileName);
            if (isFileOK) {
                Log.d(TAG,"[getFontPath] setFonts file is exists in system dir! fileName=" + fileName);
                File file = new File(fileName);
                file.setWritable(true, false);
                file.setReadable(true, false);
                file.setExecutable(true, false);
                return fileName;
            } else {
                // search file is assert
                Log.d(TAG,"[getFontPath] search file is assert dir.");
                String dir = context.getFilesDir() + File.separator + "fonts" + File.separator;
                File propFile = new File(dir);
                if (!propFile.exists()) {
                    boolean isSuccess = propFile.mkdir();
                    if (!isSuccess) {
                        Log.d(TAG,"[getFontPath] mkdir fonts fails!!!");
                        return null;
                    }
                }
                File filePath = new File(dir + name);// The file absolute path
                boolean isExitsts = FileUtils.isFileExists(filePath);
                if(isExitsts && (filePath.length()<=0)){
                    Log.d(TAG,"[getFontPath] file err Path="+filePath+" size="+filePath.length());
                    filePath.delete();
                    isExitsts = false;
                }
                if (!isExitsts) {
                    Log.d(TAG,"[getFontPath] " + filePath + " is not exists ");
                    InputStream inputStream = null;
                    try {
                        inputStream = context.getAssets().open(name);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                    isFileOK = FileIOUtils.writeFileFromIS(filePath, inputStream);
                    if (!isFileOK) {
                        Log.d(TAG,"[getFontPath] writeFileFromIS failed.");
                        return null;
                    }
                }
                fileName = dir + name;
            }
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private int getChineseNum(String msg){
        int count = 0;

//        String regEx = "[\\u4e00-\\u9fa5]";
//        Pattern p = Pattern.compile(regEx);
//        Matcher m = p.matcher(msg);
//        while (m.find()) {
//            for (int i = 0; i <= m.groupCount(); i++) {
//                count = count + 1;
//            }
//        }

        for(int i=0;i < msg.length(); i++){
            if(msg.charAt(i) > 255){
                count = count + 1;
            }
        }
        return count;
    }

    private native int getDot(String path,String chinese,int width ,int height,int size,byte[] dotData);
}
