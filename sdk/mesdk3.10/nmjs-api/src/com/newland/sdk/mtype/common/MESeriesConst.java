package com.newland.sdk.mtype.common;

/**
 * device constant table<p>
 *
 * @since ver3.10.01
 */
public class MESeriesConst {
	
	/**
	 *  Magnetic track encryption mode <p>
	 * 
	 *
	 * @since ver3.10.01
	 */
	public static final class TrackEncryptAlgorithm{
		
		/**
		 * A full magnetic track encryption scheme with the following rules: <p>    <ol>
         * <li> Two-bit second track original length in decimal system + second track data + three-bit second track original length in decimal system + third track data</li>
		 * <li> Use 0 to make up the data to the integral multiple of 16 </li>
		 * <li> Carry out the compression BCD processing of the data</li>
		 * <li> Carry out des encryption of the generated data </li>
	 	 *</ol>
	 	 * Instance: 
	 	 *<pre><blockquote>
	     * track2 = 123
	     * track3 = 4567
	     * encrypt rslt = DES(key, (0x03 0x12 0x30 0x04 0x45 0x67 0x00 0x00));
	     * </pre></blockquote>
		 */
		public static final String BY_FULLTRACK_ENCRYPT_MODEL = "BY_FULLTRACK_ENCRYPT_MODEL";
		
		/**
		 * Use standard Unionpay second generation standard magnetic track processing mode <p>
		 * 
		 */
		public static final String BY_UNIONPAY_MODEL = "BY_UNIONPAY_MODEL";
		/**
		 * DUKPT
		 */
		public static final String BY_DUKPT_MODEL = "BY_DUKPT_MODEL";
	}


}
