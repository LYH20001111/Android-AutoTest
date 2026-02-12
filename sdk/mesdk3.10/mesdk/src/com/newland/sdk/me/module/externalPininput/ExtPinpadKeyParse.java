package com.newland.sdk.me.module.externalPininput;

import android.content.Context;
import android.util.Xml;

import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ExtPinpadKeyParse {
    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("ExtPinpadKeyParse");
    private static String fileName = null;


    public ExtPinpadKeyParse(Context context) {
        fileName = "data" + File.separator + "share" + File.separator + "ParameterFile" + File.separator + context.getPackageName() + ".xml";
        File file = new File(fileName);
        File tracesDir = file.getParentFile();
        if (!tracesDir.exists()) {
            tracesDir.mkdirs();
            tracesDir.setWritable(true, false);
            tracesDir.setExecutable(true, false);
            tracesDir.setReadable(true, false);
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
                file.setWritable(true, false);
                file.setExecutable(true, false);
                file.setReadable(true, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, ExtPinpadKey> getExtPinpadKeys() {
        Map<String, ExtPinpadKey> keyMap = new HashMap<String, ExtPinpadKey>();
        ExtPinpadKey extPinpadKey = null;
        InputStream is = null;
        try {
            is = new FileInputStream(new File(fileName));
            XmlPullParser pullParser = Xml.newPullParser();
            pullParser.setInput(is, "UTF-8");
            int event = pullParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        keyMap = new HashMap<String, ExtPinpadKey>();
                        break;
                    case XmlPullParser.START_TAG://触发开始标签事件
                        if ("ExtPinpadKey".equals(pullParser.getName())) {
                            String id = pullParser.getAttributeValue(0);
                            extPinpadKey = new ExtPinpadKey();    //对象实例化
                            extPinpadKey.setKeyIndex(id);
                        }
                        if ("pinKey".equals(pullParser.getName())) {
                            String pinKey = pullParser.nextText();
                            extPinpadKey.setPinKey(pinKey);
                        }
                        if ("pinSm4Key".equals(pullParser.getName())) {
                            String pinSm4Key = pullParser.nextText();
                            extPinpadKey.setPinSm4Key(pinSm4Key);
                        }
                        if ("trackKey".equals(pullParser.getName())) {
                            String trackKey = pullParser.nextText();
                            extPinpadKey.setTrackKey(trackKey);
                        }
                        if ("trackSm4Key".equals(pullParser.getName())) {
                            String trackSm4Key = pullParser.nextText();
                            extPinpadKey.setTrackSm4Key(trackSm4Key);
                        }
                        if("dataEncryKey".equals(pullParser.getName())){
                            String dataEncryKey = pullParser.nextText();
                            extPinpadKey.setDataEncryKey(dataEncryKey);
                        }
                        if("dataEncrySm4Key".equals(pullParser.getName())){
                            String dataEncrySm4Key = pullParser.nextText();
                            extPinpadKey.setDataEncrySm4Key(dataEncrySm4Key);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("ExtPinpadKey".equals(pullParser.getName())) {
                            keyMap.put(extPinpadKey.getKeyIndex(), extPinpadKey);
                            extPinpadKey = null;
                        }
                        break;
                }
                event = pullParser.next();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            devicelogger.error("[getExtPinpadKeys]failed to invoke getExtPinpadKeys method:" + ex.getMessage());
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return keyMap;
    }

    public boolean setKeys(Map<String, ExtPinpadKey> keyMap) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(fileName));
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fileOutputStream, "UTF-8");
            serializer.startDocument("UTF-8", true);
            serializer.startTag(null, "ExtPinpadKeys");
            for (String key : keyMap.keySet()) {
                ExtPinpadKey extPinpadKey = keyMap.get(key);
                serializer.startTag(null, "ExtPinpadKey");
                serializer.attribute(null, "keyIndex", extPinpadKey.getKeyIndex());

                serializer.startTag(null, "pinKey");
                if (null != extPinpadKey.getPinKey())
                    serializer.text(extPinpadKey.getPinKey());
                serializer.endTag(null, "pinKey");

                serializer.startTag(null, "pinSm4Key");
                if (null != extPinpadKey.getPinSm4Key())
                    serializer.text(extPinpadKey.getPinSm4Key());
                serializer.endTag(null, "pinSm4Key");

                serializer.startTag(null, "trackKey");
                if (null != extPinpadKey.getTrackKey())
                    serializer.text(extPinpadKey.getTrackKey());
                serializer.endTag(null, "trackKey");

                serializer.startTag(null, "trackSm4Key");
                if (null != extPinpadKey.getTrackSm4Key())
                    serializer.text(extPinpadKey.getTrackSm4Key());
                serializer.endTag(null, "trackSm4Key");

                serializer.startTag(null, "macKey");
                if (null != extPinpadKey.getMacKey())
                    serializer.text(extPinpadKey.getMacKey());
                serializer.endTag(null, "macKey");

                serializer.startTag(null, "macSm4Key");
                if (null != extPinpadKey.getMacSm4Key())
                    serializer.text(extPinpadKey.getMacSm4Key());
                serializer.endTag(null, "macSm4Key");

                serializer.startTag(null, "dataEncryKey");
                if (null != extPinpadKey.getDataEncryKey())
                    serializer.text(extPinpadKey.getDataEncryKey());
                serializer.endTag(null, "dataEncryKey");

                serializer.startTag(null, "dataEncrySm4Key");
                if (null != extPinpadKey.getDataEncrySm4Key())
                    serializer.text(extPinpadKey.getDataEncrySm4Key());
                serializer.endTag(null, "dataEncrySm4Key");

                serializer.endTag(null, "ExtPinpadKey");
            }
            serializer.endTag(null, "ExtPinpadKeys");
            serializer.endDocument();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            devicelogger.error("[setKeys]failed to invoke setKeys method:" + ex.getMessage());
            return false;
        } finally {
            if (null != fileOutputStream) {
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
