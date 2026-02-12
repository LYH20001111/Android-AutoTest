package com.newland.sdk.mtypex.cmd;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.newland.sdk.mtypex.serializer.Serializer;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InstructionField {
	
	/**
	 * Field name
	 * @since ver3.10.01
	 * @return
	 */
	String name();
	
	/**
	 * Index<p>
	 * 
	 * @return
	 */
	int index() default 0;
	
	/**
	 * If use the tlv to express fields, then corresponding <tt>type</tt>
	 * 
	 * @return
	 */
	byte[] type() default {0x00};
	
	/**
	 * Max length
	 * @since ver3.10.01
	 * @return
	 */
	int maxLen();
	
	/**
	 * Data length after allowing serialization
	 * @return
	 */
	int fixLen() default -1;
	
	/**
	 * Supplementary string
	 * @since ver3.10.01
	 * @return
	 */
	byte padding() default 0x00;
	
	/**
	 * Paddding or not
	 * 
	 * @since ver3.10.01
	 * @return
	 */
	PaddingType paddingType() default PaddingType.NONE;
	
	/**
	 * Serializer used
	 * @return
	 */
	Class<? extends Serializer> serializer();
	
}
