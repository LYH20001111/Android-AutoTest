package com.newland.sdk.mtype;

import java.util.concurrent.TimeUnit;

/**
 * Device session management<p>
 * 
 * @see #beginTransaction(long, TimeUnit)
 *
 * @since 2.0.0
 */
public interface DeviceTransationManager {
	
	/**
	 * Start a session<p>
	 * After the device calls a session operation, it will exclusively occupy the device resource till the session is released.  <p>
	 * The held session may be released in the following circumustances.<p>
	 * <ul>
	 * <li>manual call for <tt>{@link #endTransaction()}</tt></li>
	 * <li>The device idle time exceeds the preset value.(<tt>timeout</tt>)。</li>
	 * </ul>
	 * 
	 * After the device invokes a transaction method, if there is no explicit calling for {@link #endTransaction()}operation, it will be deemed that the device must have been working in transaction session. <p>
	 * In device invoking, if it is found that the device currently does not in exclusive session, the method invoking will throw exception of {@link TransactionNeededException}.<p>
	 * 
	 * <b>What needs attention is :</b><p>
	 * The throwing of the exception of {@link TransactionNeededException} only a judgment of the current transaction state so as to keep the transaction holding state.	 
* It does not guarantee that the implementation process must occur in the transaction. <p>
	 *
	 *
	 * @param timeout
	 * @param timeUnit
	 * @throws OpenTrasactionException
	 * 
	 */
	public void beginTransaction(long timeout, TimeUnit timeUnit) throws OpenTrasactionException;
	
	/**
	 * Close the current transaction
	 * @see #beginTransaction(long, TimeUnit)
	 */
	public void endTransaction()throws OpenTrasactionException;
	
	/**
	 * Get the current transaction state
	 * @return
	 */
	public TransactionStatus getTransactionStatus();
	
	/**
	 * Judge if the current device is busy
	 * @return
	 */
	public boolean isBusy();
}
