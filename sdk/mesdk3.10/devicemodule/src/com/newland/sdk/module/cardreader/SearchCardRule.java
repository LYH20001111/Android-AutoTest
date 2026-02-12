package com.newland.sdk.module.cardreader;
/**
 * Search card rule<p>
 * 
 * @author ld
 * @since 3.5.0
 */
public enum SearchCardRule {
	/**
	 * Check the card is exists.
	 * The Rfcard is not activated.
	 */
	CARD_DETECT,
	/**
	 * Rfcard detected and immediately returned without data.<p>
	 */
	RFCARD_QUICKLY,
	RFCARD_QUICKLY2,
	/**
	 * Normal search card<p>
	 */
	NORMAL,
	NORMAL2,
	/**
	 * detected and activate A card, B card, and mifare card.(only support mifare classic 1k,4k and mifare ultra light,which they also called M1,M0)
	 */
	ACTIVATE_ABM,
}
