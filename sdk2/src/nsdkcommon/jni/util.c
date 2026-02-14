#include <sys/types.h>
#include <sys/time.h>
#include <unistd.h>
#include <stdio.h>
#include <errno.h>
#include <sys/time.h>
#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <sys/types.h>
#include <signal.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <errno.h>
#include <sys/time.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <stdio.h>
#include <time.h>
#include <errno.h>
#include <fcntl.h>
#include <signal.h>
#include <termios.h>
#include <sys/time.h>
#include <sys/ioctl.h>
#include <sys/socket.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <syslog.h>
#include <assert.h>
#include "util.h"



#define GS_MAJOR            127
#define GS_IOC_MAGIC        GS_MAJOR
#define GS_GETSTATUS        _IOW(GS_IOC_MAGIC, 0, int)//add
#define IOCTL_SERIAL_CLR_RXBUF _IOW(GS_IOC_MAGIC, 1, int)//add

#define USB_SERIAL_DEV "/dev/ttyHSL0"
#define K21PORT "/dev/ttyHSL2"
#define ACM0 "/dev/ttyACM0"
#define GS0 "/dev/ttyGS0"
#define RS232A "/dev/ttyHSL3"
#define RS232B "/dev/ttyHSL4"
#define PINPAD_CPOS_A14 "/dev/ttyHS4"
#define RS232_WAKEUP_1 "/sys/class/rs232-0_wakeup/wakeup_rs232"
#define RS232_WAKEUP_2 "/sys/class/rs232-1_wakeup/wakeup_rs232"
#define RS232B_EXTERNAL_POWERUP "/sys/class/rs232-1_wakeup/rs232_pwr_out"
#define U2000_RADAR_GAIN  "/sys/class/at581x_radar/gain"
#define U2000_RADAR_DELTA "/sys/class/at581x_radar/delta"
#define U2000_RADAR_ENABLE "sys/class/at581x_radar/rf_enable"
#define U2000_HEATER_ENABLE "/sys/class/lcd_heater/enable"
#define U2000_ETHERNET_MODE "sys/class/nl_usb_pwr_ctrl/nl_eth_mode"


void openDebug(int mode){
    Common_setDebugLevel(mode);
}

void serial_printf_string(char *BUF, int LEN) {
    int i;
    int len;
    int size;
    int temp;
    int offset;
    char s[2048];
    size = LEN;
    for (i = 0; i < LEN;) {
        offset = 0;
        memset(s, 0, sizeof(s));
        len = (size > 256) ? 256 : size;
        for (temp = 0; temp < len; temp++) {
            offset += sprintf(s + offset, "%02x ", BUF[temp + i]);

        }
        i += len;
        size -= len;
        s[offset - 1] = '\n';
        LOGD_FMT("HSL0 read data =%s", s);

    }
}

/**********************************************************************
* 函数名称： set_com_config
* 功能描述： 设置串口参数等
* 接口描述
*   输入参数：  int fd - 串口对应文件描述符
*		  					int baud_rate - 波特率
*		  					int data_bits - 数据长
*		  					char parity    - 校验位 N或n或S或s=无校验 E或e=偶校验 O或o=奇校验
*		  					int stop_bits - 停止位
*		  					char ir_en	  - 红外通讯防止反射串扰,即自发自收防护功能 I或i或Y或y=开启 , N或n=关闭
*		  					char block_en - 是否开启读写阻塞  B或b或Y或y=阻塞 N或n=非阻塞

*   返回参数： int  - 			0 表示成功, 非0失败,-1=fd错 -2=设置串口参数出错 -3=设置阻塞失败

*   影响的参数：

* 补充说明 :
***********************************************************************/
int set_com_config(int fd, int baud_rate, int data_bits, char parity, int stop_bits, char ir_en,
                   char block_en, char flow_ctrl) {
    struct termios new_cfg, old_cfg;
    speed_t speed, flag;

    /*保存测试现有串口参数设置，在这里如果串口号等出错，会有相关的出错信息*/
    if (tcgetattr(fd, &old_cfg) != 0) {
        LOGD_FMT("tcgetattr error");
        return -1;
    }

    /*设置字符大小*/
    new_cfg = old_cfg;
    cfmakeraw(&new_cfg);
    new_cfg.c_cflag &= ~CSIZE;

    /*设置波特率*/
    switch (baud_rate) {
        case 2400:
            speed = B2400;
            break;
        case 4800:
            speed = B4800;
            break;
        case 9600:
            speed = B9600;
            break;
        case 19200:
            speed = B19200;
            break;
        case 38400:
            speed = B38400;
            break;
        case 57600:
            speed = B57600;
            break;
        case 230400:
            speed = B230400;
            break;
        case 460800:
            speed = B460800;
            break;
        case 921600:
            speed = B921600;
            break;
        case 1500000:
            speed = B1500000;
            break;
        case 3000000:
            speed = B3000000;
            break;
        case 4000000:
            speed = B4000000;
            break;
        case 115200:
            speed = B115200;
            break;
        default:
            speed = B115200;
    }
    cfsetispeed(&new_cfg, speed);

    cfsetospeed(&new_cfg, speed);

    /*设置数据位*/
    switch (data_bits) {
        case 5: {
            new_cfg.c_cflag |= CS5;
        }
            break;

        case 6: {
            new_cfg.c_cflag |= CS6;
        }
            break;

        case 7: {
            new_cfg.c_cflag |= CS7;
        }
            break;

        default:
            LOGD_FMT("data_bits not support,force to 8!!");

        case 8: {
            new_cfg.c_cflag |= CS8;
        }
            break;
    }

    /*设置奇偶校验位*/
    switch (parity) {
        default:
            LOGD_FMT("parity not support,force to null!!");
        case 'n':
        case 'N':
            new_cfg.c_cflag &= ~PARENB;
            new_cfg.c_iflag &= ~INPCK;
            break;
        case 'o':
        case 'O':
            new_cfg.c_cflag |= (PARODD | PARENB);
            new_cfg.c_iflag |= INPCK;
            break;
        case 'e':
        case 'E':
            new_cfg.c_cflag |= PARENB;
            new_cfg.c_cflag &= ~PARODD;
            new_cfg.c_iflag |= INPCK;
            break;
        case 's':  /*as no parity*/
        case 'S':
            new_cfg.c_cflag &= ~PARENB;
            new_cfg.c_cflag &= ~CSTOPB;
            break;
    }

    /*设置停止位*/
    switch (stop_bits) {
        default:
            LOGD_FMT("stop_bits not support,force to 1!!");
        case 1:
            new_cfg.c_cflag &= ~CSTOPB;
            break;
        case 2:
            new_cfg.c_cflag |= CSTOPB;
            break;
    }

    /*设置等待时间和最小接收字符*/
    new_cfg.c_cc[VTIME] = 0;
    new_cfg.c_cc[VMIN] = 1;

    /* 设置红外自发自收防护功能 @@@2011/09/16 */
    switch (ir_en) {
        // 红外自发自收防护功能开启
        case 'Y':
        case 'y':
        case 'I':
        case 'i':
            new_cfg.c_iflag |= IMAXBEL;
            break;

            // 红外自发自收防护功能关闭
        default:
        case 'N':
        case 'n':
            new_cfg.c_iflag &= ~IMAXBEL;
            break;

    }

    switch (flow_ctrl) {
        case 'Y':
            new_cfg.c_cflag |= CRTSCTS;
            break;
        case 'N':
            new_cfg.c_cflag &= ~CRTSCTS;
            break;
    }

    /*处理未接收字符*/
    tcflush(fd, TCIFLUSH);

    /*激活新配置*/
    if ((tcsetattr(fd, TCSANOW, &new_cfg)) != 0) {
        perror("tcsetattr");
        return -2;
    }

    /*处理阻塞状态设置*/
    flag = fcntl(fd, F_GETFL, 0);
    if ((block_en == 'Y') || (block_en == 'y') || (block_en == 'B') || (block_en == 'b')) {
        // 设置串口为阻塞状态
        flag &= ~O_NONBLOCK;

    } else {    // 设置串口为非阻塞状态
        flag |= O_NONBLOCK;
    }
    if (fcntl(fd, F_SETFL, flag) < 0)
    {
        LOGD_FMT("fcntl F_SETFL\n");
        return -3;
    }

    return 0;
}

int port_init(int port, int data1,char *buf, char* nodeName)
{
    int aux_fd;
    char *openPort = NULL;
    struct termios myCfg, *cfg=&myCfg;
    int ret;

    if (port == 1){
        openPort = USB_SERIAL_DEV;
    } else if (port == 2){
        openPort = K21PORT;
    } else if (port == 3){
        char cmd[12] = "/dev/ttyACM";
        for (int i = 5; i > -1; --i) {
            sprintf(cmd+11,"%d",i);
            openPort = cmd;
            if (access(openPort,F_OK) != -1){
                break;
            }
            LOGD_FMT("access %s err =\n" ,openPort );
        }

    }else if (port == 4){
        openPort = GS0;
    }
    else if (port == 5) {
        openPort = RS232A;
    } else if (port == 6) {
        openPort = RS232B;
    } else if (port == 7) {
        openPort = PINPAD_CPOS_A14;
    } else if (port == -1) {
        LOGD_FMT("nodeName = %s", nodeName);
        openPort = nodeName;
    }

    if (access(openPort,R_OK | W_OK) == -1){
        return -2;
    }

    if ((aux_fd=open(openPort, O_RDWR | O_NOCTTY | O_NDELAY))<0) {
        LOGD_FMT("open %s err = %d\n" ,openPort, aux_fd);
        return -1;
    }

    LOGD_FMT("open %s succ = %d\n" ,openPort, aux_fd);
    //ret = ioctl(aux_fd,IOCTL_SERIAL_CLR_RXBUF);
    //LOGD_FMT(stderr,"ret:%d,errno:%d\n",ret,errno);
    //return aux_fd;
    if (tcgetattr(aux_fd, cfg)<0) {
        LOGD_FMT("tcgetattr err\n");
        return -1;
    }
    cfmakeraw(cfg);

    cfsetispeed(cfg,B115200);
    cfsetospeed(cfg,B115200);
    //cfsetispeed(cfg,B9600);
    //cfsetospeed(cfg,B9600);


    //cfg->c_iflag |= BRKINT;//modify 2012-4-27 15:10:04 无线模块M72在自检复位的时候会出现自检飞掉的情况，查出原因是
    // 复位的时候串口RX引脚会出现短暂的被拉低，如果设置此位，导致CPU认为是ctrl+c信号，故此退出。2012-4-27 15:13:09
//  cfg->c_iflag |= IXON;
    cfg->c_iflag &= ~IXON;
    cfg->c_iflag &= ~IGNBRK;
//  cfg->c_lflag |= ISIG;
    cfg->c_cflag &= ~CSIZE;
    cfg->c_cflag &= ~PARENB;
    cfg->c_cflag |= CS8;
    cfg->c_cflag&=~CSTOPB;

    if (tcsetattr(aux_fd,TCSANOW,cfg)<0) {
        LOGD_FMT("tcsetattr  TCSANOW err\n");
        return -1;
    }

    if (ioctl(aux_fd, TCFLSH, 0)<0) {
        LOGD_FMT("tcgetattr TCFLSH err\n");
        return -1;
    }
    LOGD_FMT("filefd===%d\n",aux_fd);
    LOGD_FMT("data1===%d\n",data1);

    int data_bits = buf[0] -'0';
    char parity = buf[1];
    int stop_bits = buf[2] -'0';
    char ir_en = buf[3];
    char block_en = buf[4];
    char flow_crtl = buf[5];
    LOGD_FMT("set_com_config data_bits=%d parity=%02x\n",data_bits,parity);
    ret = set_com_config(aux_fd,data1,data_bits,parity,stop_bits,ir_en,block_en, flow_crtl);
    if (ret != 0){
        LOGD_FMT("set_com_config===%d\n",ret);
        return ret;
    }
    LOGD_FMT("fd:%d", aux_fd);
    return aux_fd;
}

int port_write(int filefd,char *buf,int count, int timeout)
{
    if (filefd < 0) {
        LOGD_FMT("write failed: not open");
        return -1;
    }
    fd_set readfds, writefds;
    int nfds, wlen, rlen;
    struct timeval tv;
    int fd ;
    fd = filefd;
    int cnt;
    int recv;
    int sendCount = 0;
    cnt  = count;

    if(fd < 0)
        LOGD_FMT("fd err\n");

    tv.tv_sec=timeout/1000;
    tv.tv_usec=(timeout%1000)*1000;

    while(sendCount < count) {
        FD_ZERO(&writefds);
        FD_SET(fd, &writefds);
        nfds=select(fd+1, NULL, &writefds, NULL, &tv);
        if (nfds <=0) {
            if (EINTR == errno) {
                LOGD_FMT("EINTR err\n");
                continue;
            }
            return -1;
        }
        serial_printf_string(buf,cnt - sendCount);
        wlen = write(fd, buf + sendCount, cnt - sendCount);
        LOGD_FMT("write len:%d", wlen);
        if (wlen <= 0) {
            LOGD_FMT("write error, ret:%d", wlen);
            return -1;
        }
        sendCount += wlen;
    }

    return sendCount;
}
int port_read(int filefd,char *pszOutbuf,int count, int timeout)
{
    if (filefd < 0) {
        LOGD_FMT("read failed: not open");
        return -1;
    }
    int aux_fd = -1;
    int nfds;
    int ret = -1;
    struct timeval tv, startTime, currentTime;
    struct termios myCfg;
    fd_set readfds;
    int ret_nCount = 0;
    int read_nCount = 0;


    tv.tv_sec=timeout/1000;
    tv.tv_usec=(timeout%1000)*1000;

    aux_fd = filefd;
    ret_nCount=0;
    read_nCount=count;
    LOGD_FMT("start port_read is %d\n",filefd);
    LOGD_FMT("start port_read is %d\n",count);

    while (1) {
        FD_ZERO(&readfds);
        FD_SET(aux_fd, &readfds);

        nfds=select(aux_fd+1, &readfds, NULL, NULL, &tv);
        LOGD_FMT("select nfds=%d\n",nfds);
        if (nfds < 0) {
            if (EINTR == errno)
                continue;
            LOGD_FMT("select with err now errno=%d\n",errno);
            return -1;
        }
        if (nfds > 0) {
            if (FD_ISSET(aux_fd, &readfds)) {
                ret = read( aux_fd, pszOutbuf+ret_nCount, read_nCount);
                LOGD_FMT("port_read is %d\n",ret);
                LOGD_FMT("port_read is %d\n",read_nCount);

                serial_printf_string(pszOutbuf,ret);

                if (ret < 0) {
                    LOGD_FMT("read err\n");
                    return -2;
                } else if (0 == ret) {
                    LOGD_FMT("time out\n");
                    //*pnReadlen = ret_nCount;
                    break;
                } else if (ret < read_nCount) {
                    read_nCount=read_nCount-ret;
                    ret_nCount=ret_nCount+ret;
                    continue;
                }
                ret_nCount = ret_nCount+ret;
                //*pnReadlen = ret_nCount;
                break;
            }
        } else {
            if (0 == ret_nCount) {
                break;
            }
            break;
        }
    }
    LOGD_FMT("end read_port %d and count is %d \n",filefd,read_nCount);

    serial_printf_string(pszOutbuf,read_nCount);
    return ret_nCount;
}

int port_clearBuf(int filefd, int type) {
    int comm_fd = filefd;
    if (comm_fd < 0) {
        LOGD_FMT("clearBuf failed: not open");
        return -1;
    }
    int ret = -1;
    if (type == 0){
        ret = tcflush(comm_fd, TCIFLUSH);    //清空输入缓存
    } else if (type == 1){
        ret = tcflush(comm_fd, TCOFLUSH);    //清空输出缓存
    } else{
        ret = tcflush(comm_fd, TCIOFLUSH);   //清空输入输出缓存
    }

    return ret;
}

int port_isBufferEmpty(int filefd, int type) {
    if (type == 1 || type == 2){
        return 0;
    }
    if (filefd < 0) {
        LOGD_FMT("isBufferEmpty failed: not open");
        return -1;
    }
    int aux_fd = -1;
    int nfds;
    int ret = -1;
    struct timeval tv;
    fd_set readfds;
    tv.tv_sec=0;
    tv.tv_usec=0;

    aux_fd = filefd;

    FD_ZERO(&readfds);
    FD_SET(aux_fd, &readfds);

    nfds=select(aux_fd+1, &readfds, NULL, NULL, &tv);
    if (nfds < 0) {
        if (EINTR == errno)
            LOGD_FMT("select with err now\n");
        return -1;
    } else if (nfds == 0){
        return 0;
    } else{
        if (nfds > 0) {
            ioctl(aux_fd, FIONREAD, &ret);
        }
    }

    return ret;
}

int port_close( int filefd) {
    if (filefd < 0) {
        LOGD_FMT("close failed: not open");
        return -1;
    }
    int comm_fd = filefd;
    int nRet = -1;
    if(comm_fd > 0){
        nRet =  close(comm_fd);
        if (nRet == 0) {
            comm_fd = -1;
        }
    }
    return nRet;
}


int port_ioctl(int filefd, int cmd, char* args) {
    int ret = -1;
    LOGD_FMT("cmd=%d", cmd);

    if (args != NULL) {
        LOGD_FMT("ioctl args:%d", args[0]);
        ret = ioctl(filefd, cmd, args);
        LOGD_FMT("args:%d", args[0]);
        LOGD_FMT("args:%02x%02x", args[0], args[1]);
    } else {
        ret = ioctl(filefd, cmd);
    }
    LOGD_FMT("ioctl option success.");
    return ret;
}

int port_readLen(int filefd, int* len) {
    int ret = ioctl(filefd, 0x541B, len);
    LOGD_FMT("len:%d, ret:%d", *len, ret);
    return ret;
}

int u2000_awakeExternalDevice() {
    int ret = -1;
    int fd = 0;
    char awakeFlag[1];
    memset(awakeFlag, '1', sizeof(awakeFlag));
    if (access(RS232_WAKEUP_1, W_OK ) == -1 || access(RS232_WAKEUP_2, W_OK) == -1) {
        return ACCESS_FAIL;
    }

    fd = open(RS232_WAKEUP_1, O_WRONLY);
    LOGD_FMT("fd:%d", fd, errno);
    if (fd < 0) {
        LOGD_FMT("open %s fail, error:%s", RS232_WAKEUP_1, strerror(errno));
        return -1;
    }

    ret = port_write(fd, awakeFlag, 1, 1000);
    close(fd);
    if (ret < 0) {
        return ret;
    }
    LOGD_FMT("Write %d to %s success", awakeFlag[0], RS232_WAKEUP_1);

    fd = open(RS232_WAKEUP_2, O_WRONLY);

    if (fd < 0) {
        LOGD_FMT("open %s failed", RS232_WAKEUP_2);
        return -1;
    }

    ret = port_write(fd, awakeFlag, 1, 1000);
    if (ret < 0) {
        return ret;
    }
    LOGD_FMT("Write %d to %s, ret = %d", awakeFlag[0], RS232_WAKEUP_2, ret);
    close(fd);
    return JNI_OK;
}

int u2000_getExternalPowerSupply() {
    char powerSupply[10];
    int fd = -1;
    if (access(RS232B_EXTERNAL_POWERUP, W_OK | R_OK) == -1) {
        return ACCESS_FAIL;
    }

    fd = open(RS232B_EXTERNAL_POWERUP, O_RDWR);
    if (fd < 0) {
        LOGD_FMT("open %s falied, error:%s", RS232B_EXTERNAL_POWERUP, strerror(errno));
        return -1;
    }
    int ret = read(fd, powerSupply, 1);
    if (ret < -1) {
        LOGD_FMT("read failed");
        close(fd);
        return -1;
    }
    LOGD_FMT("Read %s, result:%d", RS232B_EXTERNAL_POWERUP, powerSupply[0]);
    return powerSupply[0] - 48;
}

int u2000_setRadarDetectionDistance(char *gain, char *delta) {
    int fd = -1;
    int ret = -1;
    if (access(U2000_RADAR_GAIN, W_OK | R_OK) == -1 || access(U2000_RADAR_DELTA, W_OK | R_OK) == -1) {
        LOGD_FMT("cannot access %s or %s, error:%s", U2000_RADAR_GAIN, U2000_RADAR_DELTA, strerror(errno));
        return ACCESS_FAIL;
    }
    fd = open(U2000_RADAR_GAIN, O_RDWR);
    if (fd < 0) {
        LOGD_FMT("open %s failed, error:%s", U2000_RADAR_GAIN, strerror(errno));
        return -1;
    }
    ret = write(fd, gain, sizeof(gain));
    if (ret == -1) {
        close(fd);
        return -1;
    }
    LOGD_FMT("Write %d to %s, ret = %d", *gain, U2000_RADAR_GAIN, ret);
    close(fd);

    fd = open(U2000_RADAR_DELTA, O_RDWR);
    if (fd < 0) {
        LOGD_FMT("open %s failed, error:%s", U2000_RADAR_DELTA, strerror(errno));
        return -1;
    }
    ret = write(fd, delta, sizeof(delta));
    if (ret == -1) {
        return -1;
    }
    LOGD_FMT("Write %d to %s, ret = %d", *delta, U2000_RADAR_DELTA, ret);
    close(fd);
    return JNI_OK;
}

int u2000_setRadarAndHeaterConfig(char* radarConfig, char* heaterConfig) {
    int fd = -1;
    int lcdFd = -1;
    int ret = -1;
    if (access(U2000_RADAR_ENABLE, W_OK | R_OK) == -1 || access(U2000_HEATER_ENABLE, W_OK | R_OK) == -1) {
        LOGD_FMT("cannot access %s or %s, error:%s", U2000_RADAR_ENABLE, U2000_HEATER_ENABLE, strerror(errno));
        return ACCESS_FAIL;
    }
    fd = open(U2000_RADAR_ENABLE, O_RDWR);
    if (fd < 0) {
        LOGD_FMT("open %s failed, error:%s", U2000_RADAR_ENABLE, strerror(errno));
        return -1;
    }
    ret = write(fd, radarConfig, 1);
    if (ret == -1) {
        close(fd);
        return -1;
    }
    LOGD_FMT("Write %d to %s, ret = %d", radarConfig[0], U2000_RADAR_ENABLE, ret);
    close(fd);
    lcdFd = open(U2000_HEATER_ENABLE, O_RDWR);
    if (fd < 0) {
        LOGD_FMT("open %s failed, error:%s", U2000_HEATER_ENABLE, strerror(errno));
        return -1;
    }
    ret = write(lcdFd, heaterConfig, 1);
    if (ret == -1) {
        close(lcdFd);
        return -1;
    }
    LOGD_FMT("Write %d to %s, ret = %d", heaterConfig[0], U2000_HEATER_ENABLE, ret);
    close(lcdFd);
    return JNI_OK;
}

int setEthernetMode(char* mode) {
    int fd = -1;
    if (access(U2000_ETHERNET_MODE, W_OK | R_OK) == -1) {
        return ACCESS_FAIL;
    }
    fd = open(U2000_ETHERNET_MODE, O_RDWR);
    if (fd < 0) {
        LOGD_FMT("open %s error", U2000_ETHERNET_MODE);
        return -1;
    }
    int ret = write(fd, mode, 1);
    if (ret == -1) {
        LOGD_FMT("strerr:%s", strerror(errno));
        close(fd);
        return -1;
    }
    close(fd);
    LOGD_FMT("Write %d to %s, ret = %d", mode[0], U2000_ETHERNET_MODE, ret);
    return JNI_OK;
}

int getEthernetMode() {
    char ethernetMode[2] = {0};
    int fd = -1;
    if (access(U2000_ETHERNET_MODE, W_OK | R_OK) == -1) {
        return ACCESS_FAIL;
    }
    fd = open(U2000_ETHERNET_MODE, O_RDWR);
    if (fd < 0) {
        return -1;
    }
    int ret = read(fd, ethernetMode, 1);
    if (ret == -1) {
        return -1;
    }
    return ethernetMode[0] - 48;
}

