package com.newland.ndk;

public class FileN {
	
	protected FileN(){
		super();
	}

	/**
	 * Open file.
	 * @param pszName File name
	 * @param pszMode "r" (Read only) or "w"(Write only).
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_FsOpen(String pszName,String pszMode);

	/**
	 * Close file.
	 * @param nHandle File handle
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_FsClose(int nHandle);

	/**
	 * Read file.
	 * @param nHandle File handle
	 * @param psBuffer Buffer to save data
	 * @param unLength Length of bytes to be read
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_FsRead(int nHandle, byte[] psBuffer, int unLength );

	/**
	 * Write file.
	 * @param nHandle File handle
	 * @param psBuffer Data buffer with data to be written
	 * @param unLength Length of bytes to be written
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_FsWrite(int nHandle, byte[] psBuffer, int unLength );

	/**
	 * Set read/write position.
	 * @param nHandle File handle
	 * @param ulDistance New position.
	 * @param unPosition SEEK_SET: From start of file.
	                     SEEK_CUR: From current position.
	                     SEEK_END: From end of file.
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_FsSeek(int nHandle, int ulDistance, int unPosition );

	/**
	 * Delete file.
	 * @param pszName File name
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_FsDel(String pszName);

	/**
	 * Get file length.
	 * @param pszName File name
	 * @param punSize File length
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_FsFileSize(String pszName,int[] punSize);

	/**
	 * Check if file exists.
	 * @param pszName File name
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_FsExist(String pszName);

	/**
	 * Truncate file.
	 * It will change file length to unLen. If previous file length is larger than unLen, it will be truncated. If previous file length is smaller than unLen, it will be padded with 0xff.
	 * @param pszPath File path
	 * @param unLen New file length
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_FsTruncate(String pszPath ,int unLen );

	/**
	 * Get read/write position.
	 * @param nHandle File handle
	 * @param pulRet Current read/write postion
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_FsTell(int nHandle,int[] pulRet);

	/**
	 * Rename file.
	 * @param pszSrcName Old file name
	 * @param pszDstName New file name
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_FsRename(String pszSrcName, String pszDstName );

	/**
	 * Format file system (Not valid for Phoenix OS)
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_FsFormat();

	/**
	 *
	 * @param sourcefile
	 * @param destfile
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_CopyFileToSecMod(String sourcefile, String destfile);//新增
}
