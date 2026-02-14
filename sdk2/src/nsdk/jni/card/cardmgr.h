#ifndef __CARD_MGR_H
#define __CARD_MGR_H

#define TRACK_STATUS_OK 	 0x00
#define TRACK_STATUS_ERROR   0x01
#define TRACK_STATUS_EMPTY   0x02
#define TRACKNUM    	3
#define BUFMAXLEN   	156
#define TRACK1_MAXLEN 79
#define TRACK2_MAXLEN 37
#define TRACK3_MAXLEN 104

#define TRACK_ERROR  0x7f
#define TRACK_NODATA 0x7e

extern int DealTrackWithoutVerify(char pchTk[][BUFMAXLEN], char *pTrackStatus,  char *pszTk1, char *pszTk2, char *pszTk3);
extern int DealTrack(char pchTk[][BUFMAXLEN], int nLrc, char *pTrackStatus, char *pszTk1, char *pszTk2, char *pszTk3);
extern int GetDataFromTrack2(char *pszPan,char *pszExpDate, char *pSerCode, char *pszTrack2);
extern int GetDataFromTrack1(char *pszPan,char *pszExpDate, char *pSerCode, char*puserName, char *pszTrack1);
extern int checkTrack1Format(char *pszTrack1, int dataLength);

#endif