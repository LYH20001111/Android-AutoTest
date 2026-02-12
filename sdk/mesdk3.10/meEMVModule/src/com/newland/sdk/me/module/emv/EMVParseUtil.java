package com.newland.sdk.me.module.emv;

import android.content.Context;

import com.newland.sdk.module.emv.CardInterface;
import com.newland.sdk.module.emv.EMVModule;
import com.newland.sdk.mtype.common.Const;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.utils.TLVPackage;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class EMVParseUtil {
    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger(EMVParseUtil.class);

    public boolean initializeEMVXml(Context context, String fileName, EMVModule emvModule,boolean isL3) {
        deviceLogger.debug("[initializeEMVXml]");
        InputStream is = null;
        try {
            boolean isContact = true;
            boolean isRoot = false;
            TLVPackage tlvPackage = null;
            byte[] pptlv = null;
            //---drl data---
            byte[] drlData = null;
            byte[] finalDrlData = null;
            int drlType = -1; //0:paywave  1:amex
            String[] parseFilePath = fileName.split(File.separator);
            if (null == parseFilePath) {
                deviceLogger.error("[initializeEMVXml]The file cannot be parsed.");
                return false;
            }
            if (parseFilePath.length == 1)
                is = context.getAssets().open(fileName);
            else {
                File file = new File(fileName);
                if (!file.exists() || !file.isFile() || !file.canRead()) {
                    deviceLogger.error("[initializeEMVXml]The file cannot be parsed.exist:" + file.exists() + ",isFile:" + file.isFile() + ",canRead:" + file.canRead());
                    return false;
                }
                is = new FileInputStream(file);
            }
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser xp = xmlPullParserFactory.newPullParser();
            xp.setInput(is, "utf-8");
            int eventType = xp.getEventType();
            PropertityType propertityType = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (xp.getName().equals("config")) {
                            if (xp.getAttributeValue(0).equalsIgnoreCase("CONTACT"))
                                isContact = true;
                            if (xp.getAttributeValue(0).equalsIgnoreCase("CONTACTLESS"))
                                isContact = false;
                            break;
                        }
                        if (xp.getName().equals("entry")) {
                            tlvPackage = EMVInnerUtils.newTlvPackage();
                            if (xp.getAttributeValue(0).equalsIgnoreCase("Terminal Configuration")) {
                                propertityType = PropertityType.TERMINAL_CONFIG;
                            } else if (xp.getAttributeValue(0).startsWith("AID")) {
                                propertityType = PropertityType.AID;
                            } else if (xp.getAttributeValue(0).startsWith("CAPK")) {
                                propertityType = PropertityType.CAPK;
                            }
                            break;
                        }
                        if (xp.getName().equals("item")) {
                            boolean isDrl = false;
                            String tagName = xp.getAttributeValue(0);
                            if (tagName.equalsIgnoreCase("RID"))
                                tagName = "9f06";
                            if (tagName.equalsIgnoreCase("Index"))
                                tagName = "9f22";
                            if (tagName.equalsIgnoreCase("Hash"))
                                tagName = "DF03";
                            if (tagName.equalsIgnoreCase("Exponent"))
                                tagName = "DF04";
                            if (tagName.equalsIgnoreCase("Modulus"))
                                tagName = "DF02";
                            if (tagName.equalsIgnoreCase("Hash Algorithm"))
                                tagName = "DF06";
                            if (tagName.equalsIgnoreCase("Sign Algorithm"))
                                tagName = "DF07";
                            if(!isL3){
                                if (tagName.equalsIgnoreCase("DF24"))
                                    tagName = "DF64";
                            }
                            //paywave drl
                            if (tagName.equalsIgnoreCase("APP ID")) {
                                isDrl = true;
                                String appID = xp.getAttributeValue(1);
                                byte[] lenbs = InnerUtils.intToBCD(appID.length() / 2, 2, true);
                                drlData[0] = lenbs[0];
                                byte[] appid = InnerUtils.hex2byte(appID);
                                System.arraycopy(appid, 0, drlData, 1, appid.length);
                            }
                            if (tagName.equalsIgnoreCase("ClLimitExist")) {
                                isDrl = true;
                                String ClLimitExist = xp.getAttributeValue(1);
                                System.arraycopy(InnerUtils.hex2byte(ClLimitExist), 0, drlData, 17, 1);
                            }
                            if (tagName.equalsIgnoreCase("Clss Transaction Limit")) {
                                isDrl = true;
                                String transLimit = xp.getAttributeValue(1);
                                if (drlType == 0)
                                    System.arraycopy(InnerUtils.hex2byte(transLimit), 0, drlData, 18, 6);
                                else if (drlType == 1)
                                    System.arraycopy(InnerUtils.hex2byte(transLimit), 0, drlData, 3, 6);
                                else
                                    deviceLogger.error("[initializeEMVXml] unknow drlType:" + drlType);
                            } //paywave/amex
                            if (tagName.equalsIgnoreCase("Clss Offline Limit")) {
                                isDrl = true;
                                String offlineLimit = xp.getAttributeValue(1);
                                if (drlType == 0)
                                    System.arraycopy(InnerUtils.hex2byte(offlineLimit), 0, drlData, 24, 6);
                                else if (drlType == 1)
                                    System.arraycopy(InnerUtils.hex2byte(offlineLimit), 0, drlData, 9, 6);
                                else
                                    deviceLogger.error("[initializeEMVXml] unknow drlType:" + drlType);
                            }//paywave/amex
                            if (tagName.equalsIgnoreCase("Cvm Limit")) {
                                isDrl = true;
                                String cvmLimit = xp.getAttributeValue(1);
                                if (drlType == 0)
                                    System.arraycopy(InnerUtils.hex2byte(cvmLimit), 0, drlData, 30, 6);
                                else if (drlType == 1)
                                    System.arraycopy(InnerUtils.hex2byte(cvmLimit), 0, drlData, 15, 6);
                                else
                                    deviceLogger.error("[initializeEMVXml] unknow drlType:" + drlType);
                            }//paywave/amex
                            //AMEX drl
                            if (tagName.equalsIgnoreCase("DRL Exist")) {
                                isDrl = true;
                                String drlExist = xp.getAttributeValue(1);
                                drlData[0] = InnerUtils.hex2byte(drlExist)[0];
                            }
                            if (tagName.equalsIgnoreCase("DRL ID")) {
                                isDrl = true;
                                String drlID = xp.getAttributeValue(1);
                                drlData[1] = InnerUtils.hex2byte(drlID)[0];
                            }
                            if (tagName.equalsIgnoreCase("Limist Exist") || tagName.equalsIgnoreCase("Limit Exist")) {
                                isDrl = true;
                                String limitExist = xp.getAttributeValue(1);
                                drlData[2] = InnerUtils.hex2byte(limitExist)[0];
                            }
                            if (!isDrl)
                                tlvPackage.append(Integer.valueOf(tagName, 16), xp.getAttributeValue(1));
                        } else if (xp.getName().equals("DRL")) {
                            if (xp.getAttributeValue(0).startsWith("PAYWAVE")) {
                                deviceLogger.debug("[initializeEMVXml] drl PAYWAVE:" + isContact);
                                drlType = 0x00;
                                drlData = new byte[36];
                            } else if (xp.getAttributeValue(0).startsWith("AMEX")) {
                                drlType = 0x01;
                                drlData = new byte[24];
                            } else {
                                deviceLogger.error("[initializeEMVXml] unknow XML tag[DRL],type:" + xp.getText());
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (xp.getName().equals("entry")) {
                            if (propertityType.equals(PropertityType.TERMINAL_CONFIG)) {
                                boolean result = false;
                                byte[] tlvData = tlvPackage.pack();
                                deviceLogger.debug("[initializeEMVXml] add terminal configuration isContact=" + isContact+" tlvData="+(tlvData==null?"null":InnerUtils.hexString(tlvData)));
                                if (isContact)
                                    result = emvModule.setTerminalConfiguration(tlvData, CardInterface.CONTACT);
                                else
                                    result = emvModule.setTerminalConfiguration(tlvData, CardInterface.CONTACTLESS);
                                if(!result){
                                    deviceLogger.error("[initializeEMVXml] load setTerminalConfiguration fail. isContact="+isContact);
                                    return false;
                                }
                            } else if (propertityType.equals(PropertityType.AID)) {
                                boolean result = false;
                                byte[] tlvData = tlvPackage.pack();
                                if (isContact) {
                                    deviceLogger.debug("[initializeEMVXml.] add aid isContact="+isContact + " tlvData=" + (tlvData==null?"null":InnerUtils.hexString(tlvData)));
                                    result = emvModule.addAID(tlvData, CardInterface.CONTACT);
                                }else {
                                    if (null != finalDrlData) {
                                        byte[] aid = tlvPackage.getValue(0x9F06);
                                        //deviceLogger.debug("[initializeEMVXml] aid="+InnerUtils.hexString(aid)+" finalDrlData.length:" + finalDrlData.length + " finalDrlData=" + (InnerUtils.hexString(finalDrlData)));
                                        if(InnerUtils.hexString(aid).startsWith("A000000025")){
                                            deviceLogger.debug("[initializeEMVXml] AMEX DRL");
                                            tlvPackage.append(Const.EmvSelfDefinedReference.DRLDATA_EXP, finalDrlData);
                                        }else {
                                            deviceLogger.debug("[initializeEMVXml] PAYWAVE DRL");
                                            tlvPackage.append(Integer.valueOf("DF3F", 16), finalDrlData);
                                        }
                                        finalDrlData = null;
                                    }
                                    deviceLogger.debug("[initializeEMVXml..] add aid isContact="+isContact + " tlvData=" + (tlvData==null?"null":InnerUtils.hexString(tlvData)));
                                    result = emvModule.addAID(tlvPackage.pack(), CardInterface.CONTACTLESS);
                                }
                                if(!result){
                                    deviceLogger.error("[initializeEMVXml] load addAID fail. isContact="+isContact);
                                    return false;
                                }
                            } else if (propertityType.equals(PropertityType.CAPK)) {
                                byte[] tlvData = tlvPackage.pack();
                                deviceLogger.debug("[initializeEMVXml] add capk tlvData="+(tlvData==null?"null":InnerUtils.hexString(tlvData)));
                                String aid = tlvPackage.getString(0x9f06);
                                if (aid.contains("A000000025")) {
                                    deviceLogger.debug("[initializeEMVXml] amex capk:");
                                }
                                boolean result = emvModule.addCAPublicKey(tlvData);
                                if(!result){
                                    deviceLogger.error("[initializeEMVXml] load addCAPublicKey fail.");
                                    return false;
                                }
                            }


                        }
                        if (xp.getName().equals("root")) {
                            isRoot = true;
                        }
                        if (xp.getName().equals("DRL")) {
                            if (null == finalDrlData) {
                                finalDrlData = drlData;
                            } else {
                                byte[] temp = new byte[finalDrlData.length + drlData.length];
                                System.arraycopy(finalDrlData, 0, temp, 0, finalDrlData.length);
                                System.arraycopy(drlData, 0, temp, finalDrlData.length, drlData.length);
                                finalDrlData = temp;
                            }
                        }
                        break;
                }
                if (isRoot)
                    eventType = xp.next();
                else
                    eventType = xp.nextTag();
            }
            deviceLogger.error("[initializeEMVXml] load succ.");
            return true;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public enum PropertityType {
        TERMINAL_CONFIG,
        AID,
        CAPK
    }
}
