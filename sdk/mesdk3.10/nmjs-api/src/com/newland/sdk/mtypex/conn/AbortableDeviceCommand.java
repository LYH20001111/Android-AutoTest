package com.newland.sdk.mtypex.conn;

import com.newland.sdk.mtypex.cmd.DeviceCommand;

/**
 * Revocable instruction abstract class
 * <p>
 * If the instruction is needed to be revoked within the mechanism, the default is to be inherited by default
 * 
 *
 *
 */
public abstract class AbortableDeviceCommand implements Abortable, DeviceCommand {

	private volatile Boolean isAbort = false;

	private Abortable outAbortController;

	public void setOutAbortController(Abortable outAbortController) {
		this.outAbortController = outAbortController;
	}

	/**
	 * Pay attention when you undo
	 * <p>
	 * The revocation must ensure that the last transaction can be revoked. 
	 * If the method cannot be revoked, the method may fail to detect the response of the revocation transaction and trigger the closing device event.
	 * <p>
	 */
	public void abort() {
		synchronized (this) {
			if (!isAbort) {
				if (outAbortController != null) {
					outAbortController.abort();
				}
				isAbort = true;
			}
		}
	}

	@Override
	public void abort(int keyCode) {
		synchronized (this) {
			if (!isAbort) {
				if (outAbortController != null) {
					outAbortController.abort(keyCode);
				}
				isAbort = true;
			}
		}
	}

	public boolean isAbort() {
		return isAbort;
	}

	/**
	 * Get the revocation instruction, which will be sent directly to the device to revocate the last operation when it is timeout
	 * <p>
	 * This method could not return null, otherwise unable to confirm the specific implementation results
	 * 
	 * @return
	 */
	public abstract DeviceCommand getAbortCommand();

}