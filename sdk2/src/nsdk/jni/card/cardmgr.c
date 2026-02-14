#include <stdio.h>
#include <malloc.h>
#include <string.h>
#include "log.h"
#include "ndk.h"
#include "cardmgr.h"
#include "readerrfid.h"
#include "readeric.h"
#include "card.h"


/**
* @brief  格式化磁道信息
* @param [in]pcTkDate --磁道数据
* @param [in] nMaxLen ---- 磁道最大长度
* @param [out] pcTkDate ---- 规范处理后的磁道数据
* @return
* @li
*/
static void ProFormatTKData(char * pcTkData, int nMaxLen){
	int i = 0;

	for (i=0; i<strlen(pcTkData); i++){
		if (pcTkData[i] == '?'){
			pcTkData[i] = 0x00;
			break;
		} else if ((pcTkData[i] == '\'') || (pcTkData[i] == '>')){
			pcTkData[i] = '=';
		}
	}

	if (pcTkData[0] == ':'){
		memmove (pcTkData, pcTkData + 1, strlen (pcTkData) - 1 );
	}
	if (strlen(pcTkData) > nMaxLen){
		pcTkData[nMaxLen] = 0x00;
	}

}

static int validTrack(char *pszTk){
	int i;

	for(i=0; i<strlen(pszTk); i++)
	{
		if ( (pszTk[i] < '0' || pszTk[i] > '9') && pszTk[i] != '=' )
		{
			return FAIL;
		}
	}
	return SUCC;
}

int DealTrackWithoutVerify(char pchTk[][BUFMAXLEN], char *pTrackStatus,  char *pszTk1, char *pszTk2, char *pszTk3){
	int i = 0;
	char cFistByte = 0;

	for (i = 0; i < 3; i++){
		cFistByte = pchTk[i][0];
		if (cFistByte == TRACK_NODATA || cFistByte == 0x00){
			pTrackStatus[i] = TRACK_STATUS_EMPTY;
			continue;
		}

		if (cFistByte == TRACK_ERROR){
			pTrackStatus[i] = TRACK_STATUS_ERROR;
			continue;
		}
		pTrackStatus[i] = TRACK_STATUS_OK;
	}

	LOGD_STR( "szTrackStatus:", pTrackStatus, 3);

	if (pTrackStatus[0] != TRACK_STATUS_OK
		&& pTrackStatus[1] != TRACK_STATUS_OK
		&& pTrackStatus[2] != TRACK_STATUS_OK){
		LOGE_FMT("----all tracks EMPTY/ERROR-----");
		return MGR_STATUS_ERROR;
	}

	if (pTrackStatus[0] == TRACK_STATUS_OK){
		memcpy(pszTk1, pchTk[0], strlen(pchTk[0]));
	}

	if (pTrackStatus[1] == TRACK_STATUS_OK){
		memcpy(pszTk2, pchTk[1], strlen(pchTk[1]));
	}

	if (pTrackStatus[2] == TRACK_STATUS_OK){
		memcpy(pszTk3, pchTk[2], strlen(pchTk[2]));
	}


//	LOGD_STR("gszTrack1:", pszTk1, strlen(pszTk1));
//	LOGD_STR("gszTrack2:", pszTk2, strlen(pszTk2));
//	LOGD_STR("gszTrack3:", pszTk3, strlen(pszTk3));

	return SUCC;
}

int DealTrack(char pchTk[][BUFMAXLEN], int nLrc, char *pTrackStatus, char *pszTk1, char *pszTk2, char *pszTk3){

	int i = 0;
	int nLen = 0;
	char cFistByte = 0;
//	char szTrackStatus[3] = {0};
	char szLRCError[3] = {0xC01, 0xC02, 0xC08};
	int sTrackMaxLen[3] = {TRACK1_MAXLEN, TRACK2_MAXLEN, TRACK3_MAXLEN};

	for (i = 0; i < 3; i++){
		cFistByte = pchTk[i][0];
		if (cFistByte == TRACK_NODATA || cFistByte == 0x00){
			pTrackStatus[i] = TRACK_STATUS_EMPTY;
			continue;
		}

		if (cFistByte == TRACK_ERROR){
			pTrackStatus[i] = TRACK_STATUS_ERROR;
			continue;
		}
		if ((nLrc & szLRCError[i]) == szLRCError[i]){
			pTrackStatus[i] = TRACK_STATUS_ERROR;
			continue;
		}

		ProFormatTKData(pchTk[i], sTrackMaxLen[i]);
		pTrackStatus[i] = TRACK_STATUS_OK;
	}

	LOGD_STR( "szTrackStatus:", pTrackStatus, 3);

	if (pTrackStatus[0] != TRACK_STATUS_OK
		&& pTrackStatus[1] != TRACK_STATUS_OK
		&& pTrackStatus[2] != TRACK_STATUS_OK){
		LOGE_FMT("----all tracks EMPTY/ERROR-----");
		return MGR_STATUS_ERROR;
	}


	if (pTrackStatus[0] == TRACK_STATUS_OK){
	    nLen = strlen(pchTk[0]);
	    if (nLen > TRACK1_MAXLEN){
	        LOGE_FMT("----error track 1-----");
	        return MGR_FORMAT_ERROR;
	    }
		memcpy(pszTk1, pchTk[0], nLen);
	}

	if (pTrackStatus[1] == TRACK_STATUS_OK){
        nLen = strlen(pchTk[1]);
        if (nLen > TRACK2_MAXLEN){
            LOGE_FMT("----error track 2-----");
            return MGR_FORMAT_ERROR;
        }
		memcpy(pszTk2, pchTk[1], nLen);
	}

	if (pTrackStatus[2] == TRACK_STATUS_OK){
		memcpy(pszTk3, pchTk[2], strlen(pchTk[2]));
	}

//	LOGD_STR("gszTrack1:", pszTk1, strlen(pszTk1));
//	LOGD_STR("gszTrack2:", pszTk2, strlen(pszTk2));
//	LOGD_STR("gszTrack3:", pszTk3, strlen(pszTk3));

	if (FAIL == validTrack(pszTk2) || FAIL == validTrack(pszTk3)){
        LOGE_FMT("----InvalidTrack-----");
        return MGR_FORMAT_ERROR;
    }

	return 0;

}

int GetDataFromTrack2(char *pszPan,char *pszExpDate, char *pSerCode, char *pszTrack2){
	int i=0;
	int trackLen = strlen(pszTrack2);

	for(i = 0; i < trackLen; i++){
		if (pszTrack2[i] == 'D' || pszTrack2[i] == '='){
			break;
		}
	}

    if (i > 19)	{
        return MGR_FORMAT_ERROR;
    }

	if (NULL != pszPan)	{
	    memcpy(pszPan, pszTrack2, i);
		pszPan[i] = 0;
	}
	if (NULL != pszExpDate)	{
		memcpy(pszExpDate, pszTrack2+i+1, 4);
	}
	if (NULL != pSerCode){
		memcpy(pSerCode, pszTrack2+i+5, 3);
	}
	return SUCC;
}

int GetDataFromTrack1(char *pszPan,char *pszExpDate, char *pSerCode, char*puserName, char *pszTrack1)
{
	int i=0;
	int j;
	int nTrackLen = strlen(pszTrack1);

	for(i=0; i<nTrackLen; i++){
		if (pszTrack1[i] == '^' ){
			break;
		}
	}
	for(j=i+1; j<nTrackLen; j++){
		if (pszTrack1[j] == '^' ){
			break;
		}
	}

	if (i > 20)	{
	    return MGR_FORMAT_ERROR;
	}

	int ret = checkTrack1Format(pszTrack1, i);
	if (ret != 0) {
		return MGR_FORMAT_ERROR;
	}

	if (pszPan != NULL) {
        memcpy(pszPan, pszTrack1+1, i-1);
    }

	if (NULL != puserName){
	    memcpy(puserName, pszTrack1+i+1, j-i-1);
	}

	if (NULL != pszExpDate)	{
		memcpy(pszExpDate, pszTrack1+j+1, 4);
	}
	if (NULL != pSerCode){
		memcpy(pSerCode, pszTrack1+j+5, 3);
	}

	return SUCC;
}

int checkTrack1Format(char *pszTrack1, int dataLength) {
	int length = 0;
	int startIndex = 0;

	if (pszTrack1[0] == '9' && pszTrack1[1] == '9') {
		length = dataLength - 2;
		startIndex = 2;
	} else if (pszTrack1[0] = 'B') {
		length = dataLength - 1;
		startIndex = 1;
	} else {
		LOGE_FMT("Format code invalid.");
		return FAIL;
	}
	LOGD_FMT("Length = %d" , length);
	if (length < 13 || length > 19) {
		LOGE_FMT("Pan date length invalid.");
		return FAIL;
	}

	for (int k = startIndex; k < length; k++) {
		if ((pszTrack1[k] < '0' || pszTrack1[k] > '9') || pszTrack1[k] == '=') {
			return FAIL;
		}
	}

	return 0;
}


