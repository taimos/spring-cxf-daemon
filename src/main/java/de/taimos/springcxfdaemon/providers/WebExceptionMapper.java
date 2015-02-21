package de.taimos.springcxfdaemon.providers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class WebExceptionMapper implements ExceptionMapper<WebApplicationException> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WebExceptionMapper.class);
	
	
	@Override
	public Response toResponse(WebApplicationException ex) {
		Response r = ex.getResponse();
		if (r == null) {
			r = Response.serverError().build();
		}
		
		if (this.logError(ex, r)) {
			WebExceptionMapper.LOGGER.warn(ex.getMessage(), ex);
		}
		
		return r;
	}
	
	@SuppressWarnings("unused")
	protected boolean logError(WebApplicationException ex, Response r) {
		// Only log server exceptions
		return (r.getStatus() >= 500) && (r.getStatus() <= 599);
	}
	
}
