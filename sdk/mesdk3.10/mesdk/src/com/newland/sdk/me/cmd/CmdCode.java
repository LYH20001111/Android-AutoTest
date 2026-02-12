package com.newland.sdk.me.cmd;

/**
 * Author by bxy, Date on 2019/8/21 0021.
 */
public class CmdCode {
    public static final int CARDREADER_OPEN        = (0xD1<<8|0x01);
    public static final int CARDREADER_CLOSE       = (0xD1<<8|0x02);

    public static final int MAG_READTRACKPLAIN     = (0xD1<<8|0x04);
    public static final int MAG_READTRACKENCRYPT   = (0xD1<<8|0x05);
    public static final int MAG_CALCULATETRACK     = (0xD1<<8|0x07);

    public static final int ICC_DETECT             = (0xE1<<8|0x01);
    public static final int ICC_POWERON            = (0xE1<<8|0x02);
    public static final int ICC_POWEROFF           = (0xE1<<8|0x03);
    public static final int ICC_READWRITE          = (0xE1<<8|0x04);

    public static final int RFID_POWERON           = (0xE2<<8|0x01);
    public static final int RFID_POWEROFF          = (0xE2<<8|0x02);
    public static final int RFID_ISEXIST           = (0xE2<<8|0x03);
    public static final int RFID_APDU              = (0xE2<<8|0x04);
    public static final int RFID_FELICAAPDU        = (0xE2<<8|0x05);
    public static final int RFID_M1AUTHKEY         = (0xE2<<8|0x06);
    public static final int RFID_M1READBLOCK       = (0xE2<<8|0x07);
    public static final int RFID_M1WRITEBLOCK      = (0xE2<<8|0x08);
    public static final int RFID_M1INCREMENT       = (0xE2<<8|0x09);
    public static final int RFID_M1DECREMENT       = (0xE2<<8|0x0A);
    public static final int RFID_M0AUTHKEY         = (0xE2<<8|0x0B);
    public static final int RFID_M0READBLOCK       = (0xE2<<8|0x0C);
    public static final int RFID_M0WRITEBLOCK      = (0xE2<<8|0x0D);

    public static final int PINPAD_INPUT           = (0x1A<<8|0x01);
    public static final int PINPAD_LOADMKEY        = (0x1A<<8|0x02);
    public static final int PINPAD_ENCORDEC        = (0x1A<<8|0x03);
    public static final int PINPAD_DATAMAC         = (0x1A<<8|0x04);
    public static final int PINPAD_LOADWKEY        = (0x1A<<8|0x05);
    public static final int PINPAD_LOADDUKPT       = (0x1A<<8|0x17);
    public static final int PINPAD_DELKEY          = (0x1A<<8|0x20);
    public static final int PINPAD_VPPINIT         = (0x1A<<8|0x22);
    public static final int PINPAD_CHECKKEY        = (0x1A<<8|0x25);
    public static final int PINPAD_INCREASEKSN     = (0x1A<<8|0x27);
    public static final int PINPAD_GETDUKPTKSN     = (0x1A<<8|0x28);

    public static final int LIGHT_SETSTATUS        = (0x1D<<8|0x12);
    public static final int LIGHT_BLINK            = (0x1D<<8|0x02);

    public static final int PRN_GETSTATUS          = (0x1B<<8|0x01);
    public static final int PRN_SETPAPERSIZE       = (0x1B<<8|0x02);
    public static final int PRN_PRINT              = (0x1B<<8|0x03);
    public static final int PRN_CUTTERPAPER        = (0x1B<<8|0x04);

    public static final int DEVICE_READINFO        = (0xF1<<8|0x01);
    public static final int DEVICE_GETRANDOMNUMBER = (0xF1<<8|0x02);
    public static final int DEVICE_GETTUSN         = (0xF1<<8|0x03);
    public static final int DEVICE_SETSN           = (0xF1<<8|0x04);
    public static final int DEVICE_SETDATETIME     = (0x1D<<8|0x04);
    public static final int DEVICE_GETDATETIME     = (0x1D<<8|0x05);

    public static final int TERM_BUZZER            = (0x1D<<8|0x01);
    public static final int TERM_CANCELRESET       = (0x1D<<8|0x08);
    public static final int TERM_SETTAGDATA        = (0x1D<<8|0x06);
    public static final int TERM_GETTAGDATA        = (0x1D<<8|0x07);
    public static final int TERM_SHUTDOWN          = (0x1D<<8|0x0B);
    public static final int TERM_CONFIRMATION      = (0x1D<<8|0x0D);
    public static final int TERM_SETKEYVOL         = (0x1D<<8|0x10);

    public static final int LED_GETVERSION         = (0x20<<8|0x01);
    public static final int LED_SETBRIGHTNESS      = (0x20<<8|0x02);
    public static final int LED_TURNON             = (0x20<<8|0x03);
    public static final int LED_TURNOFF            = (0x20<<8|0x04);

    public static final int FILE_OPENRECORDS       = (0xC1<<8|0x01);
    public static final int FILE_GETRECORDNUM      = (0xC1<<8|0x02);
    public static final int FILE_WRITERECORD       = (0xC1<<8|0x03);
    public static final int FILE_MODIFYRECORD      = (0xC1<<8|0x04);
    public static final int FILE_GETRECORD         = (0xC1<<8|0x05);
    public static final int FILE_WRITEFILE         = (0xC1<<8|0x07);
    public static final int FILE_READFILE          = (0xC1<<8|0x08);
    public static final int FILE_DELETEFILE        = (0xC1<<8|0x09);
}
