package com.newland.sdk.me.module.emv.structure;


/**
 * Tag reference description<p>
 *
 *
 * @since v1.0
 */
public interface EMVTagRef {

    /**
     * Tag value
     *
     * @return
     */
    public int getTag();

    /**
     * Tag name
     *
     * @return
     */
    public String getName();

    /**
     * Tag value type
     *
     * @return
     */
    public EMVTagValueType getTagValueType();

    /**
     * Tag class
     *
     * @return
     */
    public EMVTagClass getTagClass();

    /**
     * Tag type
     *
     * @return
     */
    public EMVTagType getTagType();

    /**
     * Length type
     *
     * @return
     */
    public EMVLenType getEmvLenType();

    /**
     * Fixed length
     *
     * @return
     */
    public int getFixedLen();

    /**
     * If lengthened, the maximum length
     *
     * @return
     */
    public int getMaxLen();

    /**
     * If lengthened, the minimum length
     *
     * @return
     */
    public int getMinLen();

    /**
     * Whether or not the fixed length model is enabled
     *
     * @return
     */
    public boolean isModelFixedLen();

    /**
     * Whether or not the scope length setting model is enabled
     *
     * @return
     */
    public boolean isModelScopeLen();

}