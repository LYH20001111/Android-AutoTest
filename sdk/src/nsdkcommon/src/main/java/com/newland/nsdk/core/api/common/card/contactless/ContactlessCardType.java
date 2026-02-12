package com.newland.nsdk.core.api.common.card.contactless;

/**
 * Contactless card types.
 */
public enum ContactlessCardType {
    /**
     * Type A card.
     *
     * <p>It could be the following cards:</p>
     * <ul>
     *     <li>CPU</li>
     *     <li>M0: Supported by NSDK card plugin.</li>
     *     <li>M1: Supported by NSDK card plugin.</li>
     * </ul>
     */
    TYPE_A,
    /**
     * Type B card.
     */
    TYPE_B,
    /**
     * Type F card. Set this type of card to support Felica card. Supported by NSDK card plugin.
     */
    TYPE_F,
    /**
     * Type V card. Set this type of card to support Apple VAS. Supported by NSDK card plugin.
     */
    TYPE_V
}
