package com.newland.nsdkdemo.internal.utils;

import android.util.Xml;

import com.newland.nsdk.core.api.common.utils.ISOUtils;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XMLUtils {
    public static class DataXmlElements {
        private String name;
        private byte[] data;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }
    }
    public static List<DataXmlElements> readDataXML(InputStream inputStream) {
        XmlPullParser parser = Xml.newPullParser();

        try {
            parser.setInput(inputStream, "UTF-8");
            int eventType = parser.getEventType();
            DataXmlElements currentDataXmlElement = null;
            List<DataXmlElements> dataList = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        dataList = new ArrayList<DataXmlElements>();
                        break;
                    case XmlPullParser.START_TAG:
                        String tagName = parser.getName();
                        if (tagName.equalsIgnoreCase("Element")) {
                            currentDataXmlElement = new DataXmlElements();
                        } else if (tagName.equalsIgnoreCase("name")) {
                            currentDataXmlElement.setName(parser.nextText());
                        } else if (tagName.equalsIgnoreCase("data")) {
                            currentDataXmlElement.setData(ISOUtils.hex2byte(parser.nextText()));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equalsIgnoreCase("Element") && currentDataXmlElement != null) {
                            dataList.add(currentDataXmlElement);
                            currentDataXmlElement = null;
                        }
                        break;
                }
                eventType = parser.next();
            }
            return dataList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
