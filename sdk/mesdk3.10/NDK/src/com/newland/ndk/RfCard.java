package com.newland.ndk;

import com.newland.ndk.param.FelicaParam;

public class RfCard {
    protected RfCard() {
        super();
    }

    /**
     * Get driver version.
     *
     * @param pszVersion Driver version string
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_RfidVersion(byte[] pszVersion);

    /**
     * Initialize contactless card reader.
     *
     * @param psStatus
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_RfidInit(byte[] psStatus);

    /**
     * Turn on contactless card reader.
     *
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_RfidOpenRf();

    /**
     * Turn off contactless card reader.
     *
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_RfidCloseRf();

    /**
     * Get card status.
     *
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_RfidPiccState();

    /**
     * Suspend contactless card reader.
     *
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_RfidSuspend();

    /**
     * Resume contactless card reader.
     *
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_RfidResume();

    /**
     * Set card seeking strategy.
     * Set it once before seeking card and set it to Type A for M1 card
     *
     * @param ucPicctype
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_RfidPiccType(byte ucPicctype);

    public native int NDK_RfidSetDetectType(int ucPicctype);
    /**
     * Detect card.
     *
     * @param psPicctype 0xcc: TYPE A card; 0xcb: TYPE B card
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_RfidPiccDetect(byte[] psPicctype);

    /**
     * Activate card.
     *
     * @param psPicctype 0xcc: TYPE A card; 0xcb: TYPE B card
     * @param pnDatalen  Data length
     * @param psDatabuf  Data buffer(UID for Type A card, UID for Type B card in psDataBuf[1]~[4], application or protocol information in other bytes)
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_RfidPiccActivate(byte[] psPicctype, int[] pnDatalen, byte[] psDatabuf);

    public native int NDK_RfidDetectWithCardType(int[] psPicctype, int[] pnDatalen, byte[] psDatabuf);
    /**
     * Deactivate card.
     *
     * @param ucDelayms 0: Always off; Non-zero: Time in milliseconds to power down then power up again.
     *                  (Powering down 6-10ms will invalidate card.)
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_RfidPiccDeactivate(int ucDelayms);

    /**
     * Perform APDU.
     *
     * @param nSendlen  Length of command sent
     * @param psSendbuf Command buffer
     * @param pnRecvlen Length of data received
     * @param psRecebuf Data receiving buffer
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_RfidPiccApdu(int nSendlen, byte[] psSendbuf, int[] pnRecvlen, byte[] psRecebuf);

    /**
     * Detect M1 card.
     * Card type will be set to TYPE A
     *
     * @param ucReqcode 0: Request REQA; Non-zero: Wake up WUPA (Typically WUPA is needed)
     * @param pnDatalen Length of data received (2 bytes)
     * @param psDatabuf Data receiving buffer
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_M1Request(byte ucReqcode, int[] pnDatalen, byte[] psDatabuf);

    /**
     * Perform M1 card anti-collision.
     * Usually used when card is detected
     *
     * @param pnDatalen Length of date received(UID length)
     * @param psDatabuf Data receiving buffer(UID)
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_M1Anti(int[] pnDatalen, byte[] psDatabuf);

    /**
     * Perfoem M1 card anti-collision for cascaded UID.
     *
     * @param ucSelCode PICC_ANTICOLL1/PICC_ANTICOLL2/PICC_ANTICOLL3
     * @param pnDatalen Received Data length(UID length)
     * @param psDatabuf Received Data buffer(UID)
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_M1Anti_SEL(byte ucSelCode, int[] pnDatalen, byte[] psDatabuf);

    /**
     * Select M1 card.
     * Usually used when NDK_M1Anti succeeds
     *
     * @param nUidlen  UID length
     * @param pnUidbuf UID data buffer
     * @param psSakbuf Data buffer for card selection (1 byte SAK)
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_M1Select(int nUidlen, byte[] pnUidbuf, byte[] psSakbuf);

    /**
     * Select M1 card for cascaded UID.
     *
     * @param ucSelCode 0: Request REQA; Non-zero: Wake up WUPA (Typically WUPA is needed)
     * @param nUidlen   UID length
     * @param pnUidbuf  UID data buffer
     * @param psSakbuf  Data buffer for card selection (1 byte SAK)
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_M1Select_SEL(byte ucSelCode, int nUidlen, byte[] pnUidbuf, byte[] psSakbuf);

    /**
     * Store authentication key for M1 card.
     * Same key will be stroed only once
     *
     * @param ucKeytype Authentication key type. (0x00: Type A; 0x01: Type B)
     * @param ucKeynum  Key index:0-15 (Type A and B has 16 keys for each)
     * @param psKeydata Key data,6 bytes
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_M1KeyStore(byte ucKeytype, byte ucKeynum, byte[] psKeydata);

    /**
     * Load authentication key for M1 card.
     * Same key will be loaded only once
     *
     * @param ucKeytype Authentication key type. (0x00: Type A; 0x01: Type B)
     * @param ucKeynum  Key index:0-15 (Type A and B has 16 keys for each)
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_M1KeyLoad(byte ucKeytype, byte ucKeynum);

    /**
     * Authenticate M1 card with internal key.
     *
     * @param nUidlen    UID length
     * @param psUidbuf   UID data buffer
     * @param ucKeytype  Authentication key type. (0x00: Type A; 0x01: Type B)
     * @param ucBlocknum Block to be authenticated
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_M1InternalAuthen(int nUidlen, byte[] psUidbuf, byte ucKeytype, byte ucBlocknum);

    /**
     * Authenticate M1 card with external key.
     *
     * @param nUidlen    UID length
     * @param psUidbuf   UID data
     * @param ucKeytype  Authentication key type. (0x00: Type A; 0x01: Type B)
     * @param psKeydata  Key data(6 bytes)
     * @param ucBlocknum Block to be authenticated
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_M1ExternalAuthen(int nUidlen, byte[] psUidbuf, byte ucKeytype, byte[] psKeydata, byte ucBlocknum);

    /**
     * Read M1 card block.
     *
     * @param ucBlocknum  Block to read
     * @param pnDatalen   Length of block data read
     * @param psBlockdata Block data
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_M1Read(byte ucBlocknum, int[] pnDatalen, byte[] psBlockdata);

    /**
     * Write M1 card block.
     *
     * @param ucBlocknum  Block to write
     * @param pnDataLen   Length of block data write
     * @param psBlockdata Block data
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_M1Write(byte ucBlocknum, int[] pnDataLen, byte[] psBlockdata);

    /**
     * Increment M1 card block.
     *
     * @param ucBlocknum Block to increment.
     * @param nDatalen   Incremental data length (4 bytes)
     * @param psDatabuf  Incremental data
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_M1Increment(byte ucBlocknum, int nDatalen, byte[] psDatabuf);

    /**
     * Decrement M1 card block.
     *
     * @param ucBlocknum Block to decrement.
     * @param nDanalen   Decremental data length (4 bytes)
     * @param psDatabuf  Decremental data
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_M1Decrement(byte ucBlocknum, int nDanalen, byte[] psDatabuf);

    /**
     * Transfer M1 card block after increment/decrement.
     *
     * @param ucBlocknum Block to transfer
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_M1Transfer(byte ucBlocknum);

    /**
     * Restore M1 card register to invalidate increment/decrement.
     *
     * @param ucBlocknum Block to restore
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_M1Restore(byte ucBlocknum);

    /**
     * Detect card rapidly.
     *
     * @param nModecode 0: Normal card seeking; Non-zero: Rapid card seeking
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_PiccQuickRequest(int nModecode);

    /**
     * Set ISO1443-4 protocol support.
     *
     * @param nModecode 0: Not ignore; Non-zero: Ignore
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_SetIgnoreProtocol(int nModecode);

    /**
     * Get ISO1443-4 protocol support.
     *
     * @param pnModecode 0: Not ignore; Non-zero: Ignore
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_GetIgnoreProtocol(int[] pnModecode);

    /**
     * Read contactless reader chip type.
     *
     * @param pnRfidtype Chip type (EM_RFID)
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_GetRfidType(int[] pnRfidtype);

    /**
     * Activate Type A card.
     *
     * @param cid       For RATS command
     * @param pnDatalen Data length
     * @param psDatabuf Data buffer(UID for Type A card, UID in psDataBuf[1]~[4] for Type B card and application or protocol information in other bytes)
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_RfidTypeARats(byte cid, int[] pnDatalen, byte[] psDatabuf);

    /**
     * Felica Polling
     *
     * @param psRecebuf Receive Data
     * @param pnRecvlen Receive Data length
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_RfidFelicaPoll(byte[] psRecebuf, int[] pnRecvlen);

    /**
     * Felica卡 apdu超时时间设定
     *
     * @param timeout timeout  超时时间（ms）（0-255）
     * @return
     */
    public int NDK_FelicaSetTimeout(int timeout) {
        if (timeout < 0) {
            return -6;
        }
        return NDK_FelicaSetTimeout1(timeout);
    }

    private native int NDK_FelicaSetTimeout1(int timeout);


    private native int NDK_FelicaPoll(byte[] param, byte[] psRecebuf, int[] pnRecvlen);

    /**
     * Felica Polling
     *
     * @param param     Felica param
     * @param psRecebuf Receive Data
     * @param pnRecvlen Receive Data length
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public int NDK_FelicaPoll(FelicaParam param, byte[] psRecebuf, int[] pnRecvlen) {
        byte[] felica = new byte[4];
        System.arraycopy(param.systemCode, 0, felica, 0, 2);
        felica[2] = param.requestCode;
        felica[3] = param.timeSlot;
        return NDK_FelicaPoll(felica, psRecebuf, pnRecvlen);
    }

    /**
     * Perform APDU.
     *
     * @param nSendlen  Length of command sent
     * @param psSendbuf Command buffer
     * @param pnRecvlen Length of data received
     * @param psRecebuf Data receiving buffer
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_RfidFelicaApdu(int nSendlen, byte[] psSendbuf, int[] pnRecvlen, byte[] psRecebuf);

    /**
     * mifare card active
     *
     * @param ucReqCode 0: Request REQA; Non-zero: Wake up WUPA (Typically WUPA is needed)
     * @param psUID     UID
     * @param pnUIDLen  UID length
     * @param psSak     Data buffer for card selection (1 byte SAK)
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_MifareActive(byte ucReqCode, byte[] psUID, int[] pnUIDLen, byte[] psSak);

    /**
     * Read page for M0 card.
     *
     * @param ucPageNum  Page number
     * @param pnDataLen  Data length read
     * @param psPageData Page data
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_M0Read(byte ucPageNum, int[] pnDataLen, byte[] psPageData);

    /**
     * Write page for M0 card.
     *
     * @param ucPageNum  Page number
     * @param pnDataLen  Data length read
     * @param psPageData Page data
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_M0Write(byte ucPageNum, int pnDataLen, byte[] psPageData);

    /**
     * Authenticate M0 card.
     *
     * @param psKey Authentication key
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_M0Authen(byte[] psKey);

    /**
     * Authenticate M0 card.
     *
     * @param nSendlen  send data len
     * @param psSendbuf send data
     * @param pnRecvlen receive data len
     * @param psRecebuf receive data
     * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
     */
    public native int NDK_M0Authen_Release(int nSendlen, byte[] psSendbuf, int[] pnRecvlen, byte[] psRecebuf);

    public native int NDK_RfidSetPiccParam(byte ucPiccparamtype, int pnParamlen, byte[] psParambuf);

    /**
     * @param nSendlen  发送的命令长度
     * @param psSendbuf 发送命令缓冲区
     * @param timeout   超时时间设置， timeout = 0 时采用返回的默认时间
     * @return
     * @brief APDU读写接口，透传指令
     * @retval psDatabuf 返回的数据
     * @retval pnDatalen 数据长度
     * @li \ref NDK_OK
     * @li \ref NDK_ERR_PARA：							参数非法
     * @li \ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
     * @li \ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(射频驱动接口 IOCTL_RFID_SET_FELICA_TIMEOUT 调用失败返回)
     * @li \ref ..............
     */
    public native int NDK_RfidPiccApduInTransMode(byte[] psSendbuf, int nSendlen, byte[] psRecebuf, int[] pnRecvlen, int timeout);

    /**
     *@brief	设置射频卡读卡模式
     *@param	rf_mode	(EM_RF_MODE)射频卡类型，如身份证设置为0x02
     *@return
     *@li	\ref NDK_OK
     *@li	\ref NDK_ERR_PARA：							参数非法
     *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	设备文件打开失败(射频设备文件打开失败)
     *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			驱动调用错误(射频驱动接口 IOCTL_RFID_SET_FELICA_TIMEOUT 调用失败返回)
     *@li   \ref ..............
     */

    public native int NDK_RfidConfig(int rf_mode);

    public native int NDK_RfidEMVTest(int option, int inSendlen, byte[] psSendbuf, int[] pnRecvlen,byte[] psRecebuf);

    public native int NDK_CEInit();
    public native int NDK_CEParamSet(int type,int len,byte[] buf);

    //mode:0:read,1:write.
    public native int NDK_CEDataSync(int mode,int type,byte[] buf,int[] len);
    public native int NDK_CEGetState(int type,int[] state);
}
