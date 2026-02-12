package com.newland.sdk.mtypex.cmd.packager;

import java.io.IOException;

/**
 * 
 *
 */
public interface ProtocalPackagerReader {

	/**
	 * 以阻塞的方式尝试读满一个<tt>buffer</tt>。<p>
	 * 该方法在未读满前，会一直阻塞。或者发生以下情况：<ol>
	 * <li><b>流末尾</b>：返回buffer实际的读取长度。若：<tt>(buffer.length != 返回值)</tt>，可以认为已经到了流末尾。</li>
	 * <li><b>读取超时</b>：抛出一个<tt>ReadTimeout</tt>异常。</li>
	 * <li><b>IO异常</b>：抛出一个<tt>IOException</tt>异常。</li>
	 * <li><b>被其他操作中断</b>：抛出一个<tt>InterruptException</tt></li></ol>
	 * 
	 * 关于<b>读取超时</b>：<ul>
	 * <li>区别与一般的SocketTimeoutException，我们认为<tt>ReadTimeout</tt>是一个可恢复的的异常。
	 * 导致异常发生的原因不是物理层面的链路中断，而是应用层面的错误数据包格式导致进一步的交易无数据，或者是传输分包导致数据延迟<tt><b>过</b>延迟</tt>到达。
	 * 该方法不会导致上层出现主动关闭链接的操作。但可能会触发类似清空缓冲区等的操作，用于重置当前状态。</li>
	 * <li>我们认为，除掉<tt>ReadTimeout</tt>以外的异常，均表示出现无可修复的异常或者是链路被外部主动中断，将会回收资源。</li></ul>
	 * 
	 * 关于<b>IO异常</b>：<ul>
	 * <li>我们认为<tt>IOException</tt>表示该链接通路出现无法修复的异常，只能将其关闭。</li>
	 * </ul>
	 * 
	 * 
	 * @since ver3.10.01
	 * @param buffer 缓冲区
	 * @return
	 * 		实际读取的buffer长度
	 * 
	 * @throws ReadTimeout 读取超时
	 * @throws IOException io操作异常
	 * @throws InterruptedException 如果中断,则抛出异常
	 */
	public int read(byte[] buffer) throws ReadTimeout,IOException, InterruptedException;

	/**
	 * @see ProtocalPackagerReader#read(byte[])
	 * 
	 * @since ver3.10.01
	 * @param buffer 缓冲区
	 * @param offset 起始偏移量
	 * @param len 读取长度
	 * @return
	 * @throws ReadTimeout 读取超时
	 * @throws IOException io操作异常
	 * @throws InterruptedException  如果中断,则抛出异常
	 */
	public int read(byte[] buffer,int offset,int len) throws ReadTimeout,IOException, InterruptedException;
	
	/**
	 * 清理缓冲区内的数据<p>
	 * 在读取超时或者其他错误原因出现时，可能需要将现有还在缓区内的数据清理。避免对新进入的数据产生影响。
	 * 
	 * @param expectedMaxmium 期望清理的最大缓冲区数据，一般表示一个交易包可能的最大的长度。提供给清理操作用于参考。
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void clearBuffer(int expectedMaxmium)throws IOException, InterruptedException;
	
}
