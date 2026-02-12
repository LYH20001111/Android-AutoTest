/*******************************************************************************
 * Copyright (C) 2019 Newland Payment Technology Co., Ltd All Rights Reserved
 ******************************************************************************/
#ifndef NAPI_PRINTER_H
#define NAPI_PRINTER_H

#ifdef __cplusplus
extern "C" {
#endif

#include <stdint.h>
#include "comm.h"
#include "../include/napi.h"

/** @addtogroup Print
* @{
*/

typedef enum {
	NAPI_PRN_STATUS_OK       = 0,    /**<Good*/
	NAPI_PRN_STATUS_BUSY     = 8,    /**<In printing*/
	NAPI_PRN_STATUS_NOPAPER  = 2,    /**<Out of paper*/
	NAPI_PRN_STATUS_OVERHEAT = 4,    /**<Overheat*/
	NAPI_PRN_STATUS_VOLERR   = 112,  /**<Abnormal voltage*/
	NAPI_PRN_STATUS_BAD = 113,       /**<No printer or printer damaged*/
}PRN_STATUS;

/**
 *@brief  Open printer
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
extern int (*NAPI_PrnOpenDev)(void);

/**
 *@brief Close printer
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
extern int (*NAPI_PrnCloseDev)(void);

/**
 *@brief  Feed paper.
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
extern int (*NAPI_PrnFeedPaper)(void);

/**
 *@brief  Start printing.
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
extern int (*NAPI_PrnStart)(void);

/**
 *@brief  Get printer status
 *@param[out]  PrnStatus   Printer status (\ref PRN_STATUS "PRN_STATUS")
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
extern int (*NAPI_PrnGetStatus)(PRN_STATUS *PrnStatus);

/**
 *@brief 		to print image (this function shall also convert bit matrix to be printed to the buffer and call NDK_PrnStart to start printing)
 *@details  	maximum width of thermal printing is 384 pixels. If the sum of xsize and xpos surpasses the limit above, the system will have return failure; as for horizontal enlargement mode, the value shall not exceed 384/2.
 *@param[in] 		unXsize width of image (pixel)
 *@param[in] 		unYsize height of image (pixel)
 *@param[in] 		unXpos Graphic column in the top left hand corner position, and must meet xpos + xsize < = ndk_PR_MAXLINEWIDE (normal mode for 384, when the lateral magnification for 384/2, the parameters for the absolute coordinates is not affected by the left margin)
 *@param[in] 		psImgBuf  dot matrix data for the image in horizontal arrangement, in the first 8 dot of line 1 of byte 1, D7 is the first dot
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
extern int (*NAPI_PrnImage)(uint Xsize,uint Ysize,uint Xpos,const char *ImgBuf);

/** @} */ // end of Print

#ifdef __cplusplus
}
#endif

#endif

/* End of this file*/
