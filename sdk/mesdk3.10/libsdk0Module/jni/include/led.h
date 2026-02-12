
#ifndef _GUEST_DISPALY_H_
#define _GUEST_DISPALY_H_

#include <stdio.h>
#include <termios.h>
#include <fcntl.h>
#include <unistd.h>
#include <dlfcn.h>

#define GUESTDISPLAY_N550  1
#define DEALRESULT_OK      1
#define DEALRESULT_ERR     0
#define GUESTDISPLAY_OK    "01"

typedef struct {
    char *buf;
    uint32_t justify;
    uint32_t onoff;
    uint8_t light_nr;
}N550_DIGLED;


#define DLED_IOC_MAGIC 		'D'
#define DLED_IOCG_VER 	    _IO(DLED_IOC_MAGIC, 0)
#define DLED_IOCS_SHOW		_IO(DLED_IOC_MAGIC, 1)
#define DLED_IOCS_CLR		_IO(DLED_IOC_MAGIC, 2)
#define DLED_IOCS_BRIGHT	_IO(DLED_IOC_MAGIC, 3)
#define DLED_IOCS_LIGHT		_IO(DLED_IOC_MAGIC, 4)

#endif

