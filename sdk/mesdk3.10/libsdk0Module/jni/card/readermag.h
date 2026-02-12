#ifndef __READERMAG_H_
#define __READERMAG_H_
int MagReader_Open(void* pstCardReaderParam,void*pstCardInfo);
int MagReader_Read(void* pstCardReaderParam,void*pstCardInfo);
int MagReader_Close(void* pstCardReaderParam);
int MagReader_Resume(void* pstCardReaderParam);
#endif
