package com.newland.sdk.mtypex.cmd;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Request/Response package declaration
 * 
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandEntity {

	/**
	 * If it is request, its <b>instruction expression</b><p>
	 * @return
	 */
	byte[] cmdCode();
	
	Class<? extends AbstractSuccessResponse> responseClass();
	
	Class<? extends AbstractNotificationResponse> notificationResponseClass() default AbstractNotificationResponse.class;
}
