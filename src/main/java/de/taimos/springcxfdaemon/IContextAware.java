package de.taimos.springcxfdaemon;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

import org.apache.cxf.jaxrs.ext.MessageContext;

public interface IContextAware {
	
	@Context
	public abstract void setMessageContext(MessageContext context);
	
	@Context
	public abstract void setHttpServletRequest(HttpServletRequest request);
	
	@Context
	public abstract void setHttpServletResponse(HttpServletResponse response);
	
}