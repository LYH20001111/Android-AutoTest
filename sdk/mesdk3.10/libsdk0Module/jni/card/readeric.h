#ifndef __READERIC_H_
#define __READERIC_H_

int ICReader_Open(void* pstCardReaderParam,void* pstCardInfo);
int ICReader_Read(void* pstCardReaderParam,void* pstCardInfo);
int ICReader_Close(void* pstCardReaderParam);
int ICReader_Resume(void* pstCardReaderParam);

#endif
