package com.newland.sdk.me.module.emv;


public interface FileHandler {
	
	/**
	 * 获得完整的路径
	 * @return
	 */
	public String getFileName();
	
	/**
	 * 获得简单的文件名
	 * @return
	 */
	public String getSimpleName();

	public int open(int rw);
	
	public int close();
    /*
     * 读取文件信息
     * byte[] buffer：数组保存读取的数据
     * int size：读取的数据长度
     * 返回值：
     * 失败小于0，成功返回读取的数据长度
     */
    public int read(byte[] buffer, int size);
    /*
     * 写入文件信息
     * byte[] buffer：要写入的数据
     * int size：写入的数据长度
     * 返回值：
     * 失败小于0，成功返回读取写入的数据长度
     */
    public int write(byte[] buffer, int size);
    /*
     * 光标定位
     * int offset：偏移长度
     * int where：起始位置
     * #define FILE_SEEK_SET 0    start of stream 
	 * #define FILE_SEEK_CUR 1    current position in stream
     * #define FILE_SEEK_END 2    end of stream  
     * 返回值：
     * 失败小于0，成功返回当前光标位置
     */
    public int seek(int offset, int where);
    
    public boolean isOpened();
    
    public int truncate(int size);
    
	
}
