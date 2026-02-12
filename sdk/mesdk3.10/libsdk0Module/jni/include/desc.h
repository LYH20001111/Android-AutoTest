#ifndef __CMD_DESC_H__
#define __CMD_DESC_H__

extern void Cmd_PrintDesc(char cmdMain, char cmdSub, int isStart);
#define       COMMAND_NONE                      (-1)

#define       CARDREADER_OPEN                   (0xD1<<8|0x01)
#define       CARDREADER_CLOSE                  (0xD1<<8|0x02)

#define       MAG_READTRACKPLAIN                (0xD1<<8|0x04)
#define       MAG_READTRACKENCRYPT              (0xD1<<8|0x05)
#define       MAG_CALCULATETRACK                (0xD1<<8|0x07)

#define       ICC_DETECT                        (0xE1<<8|0x01)
#define       ICC_POWERON                       (0xE1<<8|0x02)
#define       ICC_POWEROFF                      (0xE1<<8|0x03)
#define       ICC_READWRITE                     (0xE1<<8|0x04)

#define       RFID_POWERON                      (0xE2<<8|0x01)
#define       RFID_POWEROFF                     (0xE2<<8|0x02)
#define       RFID_ISEXIST                      (0xE2<<8|0x03)
#define       RFID_APDU                         (0xE2<<8|0x04)
#define       RFID_FELICAAPDU                   (0xE2<<8|0x05)
#define       RFID_M1AUTHKEY                    (0xE2<<8|0x06)
#define       RFID_M1READBLOCK                  (0xE2<<8|0x07)
#define       RFID_M1WRITEBLOCK                 (0xE2<<8|0x08)
#define       RFID_M1INCREMENT                  (0xE2<<8|0x09)
#define       RFID_M1DECREMENT                  (0xE2<<8|0x0A)
#define       RFID_M0AUTHKEY                    (0xE2<<8|0x0B)
#define       RFID_M0READBLOCK                  (0xE2<<8|0x0C)
#define       RFID_M0WRITEBLOCK                 (0xE2<<8|0x0D)
#define       RFID_ATS                          (0xE2<<8|0x15)

#define       PINPAD_INPUT                      (0x1A<<8|0x01)
#define       PINPAD_LOADMKEY                   (0x1A<<8|0x02)
#define       PINPAD_ENCORDEC                   (0x1A<<8|0x03)
#define       PINPAD_DATAMAC                    (0x1A<<8|0x04)
#define       PINPAD_LOADWKEY                   (0x1A<<8|0x05)
#define       PINPAD_LOADDUKPT                  (0x1A<<8|0x17)
#define       PINPAD_DELKEY                     (0x1A<<8|0x20)
#define       PINPAD_VPPINIT                    (0x1A<<8|0x22)
#define       PINPAD_CHECKKEY                   (0x1A<<8|0x25)
#define       PINPAD_INCREASEKSN                (0x1A<<8|0x27)
#define       PINPAD_GETDUKPTKSN                (0x1A<<8|0x28)

#define       LIGHT_SETSTATUS                   (0x1D<<8|0x12)
#define       LIGHT_BLINK                       (0x1D<<8|0x02)

#define       PRN_GETSTATUS                     (0x1B<<8|0x01)
#define       PRN_SETPAPERSIZE                  (0x1B<<8|0x02)
#define       PRN_PRINT                         (0x1B<<8|0x03)
#define       PRN_CUTTERPAPER                   (0x1B<<8|0x04)

#define       DEVICE_READINFO                   (0xF1<<8|0x01)
#define       DEVICE_GETRANDOMNUMBER            (0xF1<<8|0x02)//F104
#define       DEVICE_GETTUSN                    (0xF1<<8|0x03)//F105
#define       DEVICE_SETSN                      (0xF1<<8|0x04)//FF02

#define       DEVICE_SETDATETIME                (0x1D<<8|0x04)
#define       DEVICE_GETDATETIME                (0x1D<<8|0x05)

#define       TERM_BUZZER                       (0x1D<<8|0x01)
#define       TERM_CANCELRESET                  (0x1D<<8|0x08)
#define       TERM_SETTAGDATA                   (0x1D<<8|0x06)
#define       TERM_GETTAGDATA                   (0x1D<<8|0x07)
#define       TERM_SHUTDOWN                     (0x1D<<8|0x0B)
#define       TERM_CONFIRMATION                 (0x1D<<8|0x0D)
#define       TERM_SETKEYVOL                    (0x1D<<8|0x10)

#define       LED_GETVERSION                    (0x20<<8|0x01)
#define       LED_SETBRIGHTNESS                 (0x20<<8|0x02)
#define       LED_TURNON                        (0x20<<8|0x03)
#define       LED_TURNOFF                       (0x20<<8|0x04)

#define       FILE_OPENRECORDS                  (0xC1<<8|0x01)
#define       FILE_GETRECORDNUM                 (0xC1<<8|0x02)
#define       FILE_WRITERECORD                  (0xC1<<8|0x03)
#define       FILE_MODIFYRECORD                 (0xC1<<8|0x04)
#define       FILE_GETRECORD                    (0xC1<<8|0x05)
#define       FILE_WRITEFILE                    (0xC1<<8|0x07)
#define       FILE_READFILE                     (0xC1<<8|0x08)
#define       FILE_DELETEFILE                   (0xC1<<8|0x09)

#define       LOG_SETLEVEL                      (0xA1<<8|0x01)
#define       GLOBAL_SETTING                    (0xA1<<8|0x02)

//D1、E1、E2、1A、1D、1B、F1、1D、20、C1

#endif


































