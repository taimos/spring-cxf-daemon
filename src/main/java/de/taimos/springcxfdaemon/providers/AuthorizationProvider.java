/**
 *
 */
package de.taimos.springcxfdaemon.providers;

import java.io.IOException;
import java.security.Principal;

import javax.security.auth.Subject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.cxf.common.security.SimplePrincipal;
import org.apache.cxf.interceptor.security.DefaultSecurityContext;
import org.apache.cxf.jaxrs.impl.HttpHeadersImpl;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.security.SecurityContext;

import de.taimos.httputils.WSConstants;

/**
 * @author thoeger
 *
 *         Copyright 2012, Cinovo AG
 *
 */
@Provider
public abstract class AuthorizationProvider implements ContainerRequestFilter {
	
	@Override
	public final void filter(ContainerRequestContext requestContext) throws IOException {
		Message m = JAXRSUtils.getCurrentMessage();
		
		HttpHeaders head = new HttpHeadersImpl(m);
		String authHeader = head.getHeaderString(WSConstants.HEADER_AUTHORIZATION);
		if ((authHeader != null) && !authHeader.isEmpty() && (authHeader.indexOf(" ") != -1)) {
			int index = authHeader.indexOf(" ");
			String type = authHeader.substring(0, index);
			String auth = authHeader.substring(index + 1);
			SecurityContext sc = this.handleAuthHeader(requestContext, m, type, auth);
			if (sc != null) {
				m.put(SecurityContext.class, sc);
				return;
			}
		}
		
		SecurityContext sc = this.handleOther(requestContext, m, head);
		if (sc != null) {
			m.put(SecurityContext.class, sc);
			return;
		}
		
		if (this.isAuthorizationMandatory()) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}
	}
	
	/**
	 * @return <code>true</code> if the request should fail if no valid user is found
	 */
	protected abstract boolean isAuthorizationMandatory();
	
	/**
	 * handle the presence of the Authorization header
	 * 
	 * @param requestContext the CXF request context
	 * @param msg the message
	 * @param type the Authorization type (Basic|Bearer|...)
	 * @param auth the auth part of the header
	 * @return the {@link SecurityContext} if logged in or null
	 */
	protected abstract SecurityContext handleAuthHeader(ContainerRequestContext requestContext, Message msg, String type, String auth);
	
	/**
	 * handle other auth methods like sessions, custom headers, etc
	 * 
	 * @param requestContext the CXF request context
	 * @param msg the message
	 * @param head the HTTP headers
	 * @return the {@link SecurityContext} if logged in or null
	 */
	protected abstract SecurityContext handleOther(ContainerRequestContext requestContext, Message msg, HttpHeaders head);
	
	/**
	 * Create a {@link SecurityContext} to return to the provider
	 * 
	 * @param user the user principal
	 * @param roles the roles of the user
	 * @return the {@link SecurityContext}
	 */
	protected static SecurityContext createSC(String user, String... roles) {
		final Subject subject = new Subject();
		
		final Principal principal = new SimplePrincipal(user);
		subject.getPrincipals().add(principal);
		
		if (roles != null) {
			for (final String role : roles) {
				subject.getPrincipals().add(new SimplePrincipal(role));
			}
		}
		return new DefaultSecurityContext(principal, subject);
	}
	
}