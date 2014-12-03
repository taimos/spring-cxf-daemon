/**
 * 
 */
package de.taimos.springcxfdaemon.monitoring;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * 
 * Copyright 2013 Hoegernet<br>
 * <br>
 * 
 * @author Thorsten Hoeger
 * 
 */
public class InvocationInstance {
	
	private final UUID messageId;
	
	private long startNano;
	
	private long endNano;
	
	private long duration;
	
	private Method calledMethod;
	
	private String calledClass;
	
	private String calledMethodName;
	
	
	/**
	 * @param messageId the message id
	 */
	public InvocationInstance(UUID messageId) {
		this.messageId = messageId;
	}
	
	/**
	 * start timer
	 */
	public void start() {
		this.startNano = System.nanoTime();
	}
	
	/**
	 * stop timer
	 */
	public void stop() {
		this.endNano = System.nanoTime();
		this.duration = this.endNano - this.startNano;
		this.duration = this.duration / 1000000; // convert to ms
	}
	
	@Override
	public String toString() {
		final String msgString = "Message %s was %s ms inflight. Access was to class '%s' and method '%s'";
		return String.format(msgString, this.getMessageId(), this.getDuration(), this.getCalledClass(), this.getCalledMethod());
	}
	
	// #########################################
	// getter / setter
	// #########################################
	
	/**
	 * @return the startNano
	 */
	public long getStartNano() {
		return this.startNano;
	}
	
	/**
	 * @return the endNano
	 */
	public long getEndNano() {
		return this.endNano;
	}
	
	/**
	 * @return the calledMethod
	 */
	public Method getCalledMethod() {
		return this.calledMethod;
	}
	
	/**
	 * @param calledMethod the calledMethod to set
	 */
	public void setCalledMethod(Method calledMethod) {
		this.calledMethod = calledMethod;
		if ((calledMethod != null) && (calledMethod.getDeclaringClass() != null)) {
			this.calledMethodName = calledMethod.getName();
			this.calledClass = calledMethod.getDeclaringClass().getCanonicalName();
		}
	}
	
	/**
	 * @return the duration
	 */
	public long getDuration() {
		return this.duration;
	}
	
	/**
	 * @return the messageId
	 */
	public UUID getMessageId() {
		return this.messageId;
	}
	
	/**
	 * @return the calledClass
	 */
	public String getCalledClass() {
		return this.calledClass;
	}
	
	/**
	 * @return the calledMethodName
	 */
	public String getCalledMethodName() {
		return this.calledMethodName;
	}
	
}
