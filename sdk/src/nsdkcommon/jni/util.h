//
// Created by Administrator on 2019/10/31.
//

#ifndef USBTEST_UTIL_H
#define USBTEST_UTIL_H

#include <android/log.h>
#include "jni.h"
#include <stdint.h>
#include <fcntl.h>
#include <unistd.h>
#include <termios.h>
#include <stdio.h>
#include <errno.h>
#include <sys/time.h>
#include <string.h>
#include <sys/stat.h>
#include <malloc.h>

#include <stdlib.h>
#include "common_log.h"


#define _DEBUG 1
#ifdef _DEBUG
#else
#define LOGI(...) 	;
#endif

#define bool int
#define TRUE 1
#define FALSE 0
#define ACCESS_FAIL -5

#define MAX_FRAME_SIZE	(1024*16)


//int _read(int *pnRecvlen, uchar *psRecebuf,int timeout,int readLen);
//int  _read(int *pnRecvlen,char *sRecvBuf,uint uiTimeout,int nLen);
//int _write(int len,char *ucDelayms);

//int set_com_config(int fd,int baud_rate, int data_bits, char parity, int stop_bits,char ir_en,char block_en);
//int _reccomm(int fd, int len, char *outbuf, int timeout);
//int _clearBuf();
//int _close();


int port_init(int port, int data1,char *buf, char *nodeName);
int port_write(int filefd,char *buf,int count, int timeout);
int port_read(int filefd,char *pszOutbuf,int count, int timeout);
int port_clearBuf(int filefd, int type);
int port_isBufferEmpty(int filefd, int type);
int port_close(int fd);
int port_ioctl(int fd, int cmd, char* args);
int port_readLen(int fd, int* len);
int u2000_awakeExternalDevice();
int u2000_getExternalPowerSupply();
int u2000_setRadarDetectionDistance(char *gain, char *delta);
int u2000_setRadarAndHeaterConfig(char* radarConfig, char* heaterConfig);
int setEthernetMode(char* mode);
int getEthernetMode();
void openDebug(int fd);

#endif //USBTEST_UTIL_H

