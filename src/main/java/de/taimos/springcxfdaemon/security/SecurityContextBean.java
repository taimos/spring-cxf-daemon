/**
 * 
 */
package de.taimos.springcxfdaemon.security;

import java.util.UUID;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.MessageContextImpl;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.taimos.restutils.RESTAssert;
import de.taimos.springcxfdaemon.monitoring.InvocationInstance;

/**
 * Copyright 2015 Taimos GmbH<br>
 * <br>
 *
 * @author thoeger
 * 		
 */
@Component
public class SecurityContextBean {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	public final SecurityContext getSC() {
		return this.getContext().getSecurityContext();
	}
	
	public final void assertSC() {
		if ((this.getSC() == null) || (this.getSC().getUserPrincipal() == null)) {
			throw new NotAuthorizedException(Response.status(Status.UNAUTHORIZED).entity("Invalid credentials or session").build());
		}
	}
	
	public final void assertLoggedIn() {
		this.assertSC();
	}
	
	public final String getUser() {
		SecurityContext sc = this.getSC();
		if ((sc != null) && (sc.getUserPrincipal() != null)) {
			return sc.getUserPrincipal().getName();
		}
		return null;
	}
	
	public final boolean hasRole(String role) {
		SecurityContext sc = this.getSC();
		if (sc != null) {
			return sc.isUserInRole(role);
		}
		return false;
	}
	
	public final UUID requestId() {
		final InvocationInstance ii = this.getContext().getContent(InvocationInstance.class);
		RESTAssert.assertNotNull(ii, Status.INTERNAL_SERVER_ERROR);
		return ii.getMessageId();
	}
	
	public final boolean isLoggedIn() {
		return this.getUser() != null;
	}
	
	private MessageContext getContext() {
		return new MessageContextImpl(PhaseInterceptorChain.getCurrentMessage());
	}
}
