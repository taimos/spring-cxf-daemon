package de.taimos.springcxfdaemon.remote;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface RemoteService {
	
	/**
	 * @return the name of the service. leave blank to use base URL
	 */
	String name() default "";
	
	/**
	 * @return the base URL to use. leave blank to use property resolution by service name
	 */
	String baseURL() default "";
	
}
