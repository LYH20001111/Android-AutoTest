
#ifndef __FILE_H_
#define __FILE_H_

#include "comm.h"

extern int ME_FsRead(const char *pszName, unsigned char *psBuffer, uint unOffset, uint unLength);
extern int ME_FsWrite(const char *pszName, const unsigned char *psBuffer, uint unOffset, uint unLength);
#endif//__FILE_H_

