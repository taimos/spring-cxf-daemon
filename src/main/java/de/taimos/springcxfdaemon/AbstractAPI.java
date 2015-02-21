package de.taimos.springcxfdaemon;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.RedirectionException;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.base.Charsets;

import de.taimos.restutils.RESTAssert;
import de.taimos.springcxfdaemon.monitoring.InvocationInstance;

public class AbstractAPI implements IContextAware {
	
	protected MessageContext context;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	
	@Value("${server.url:http://localhost:${jaxrs.bindport:${svc.port:8080}}}")
	private String serverURL;
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	@Override
	public void setMessageContext(MessageContext context) {
		this.context = context;
	}
	
	@Override
	public void setHttpServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	@Override
	public void setHttpServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	protected final String getFirstHeader(String name) {
		return this.request.getHeader(name);
	}
	
	protected final void redirectPath(String path) {
		this.redirect(this.getServerURL() + path);
	}
	
	protected String getServerURL() {
		return this.serverURL;
	}
	
	protected final void redirect(String uriString) {
		throw new RedirectionException(Status.SEE_OTHER, URI.create(uriString));
	}
	
	protected final UUID requestId() {
		final InvocationInstance ii = this.context.getContent(InvocationInstance.class);
		RESTAssert.assertNotNull(ii, Status.INTERNAL_SERVER_ERROR);
		return ii.getMessageId();
	}
	
	protected String getCurrentURIEncoded() {
		try {
			return URLEncoder.encode(this.getCurrentURI(), Charsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			throw new InternalServerErrorException(e);
		}
	}
	
	protected String getCurrentURI() {
		final String path = this.context.getHttpServletRequest().getRequestURI();
		final String query = this.context.getHttpServletRequest().getQueryString();
		if (query != null) {
			return this.getServerURL() + path + "?" + query;
		}
		return this.getServerURL() + path;
	}
	
}