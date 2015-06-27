package de.taimos.springcxfdaemon.test;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.http.HttpResponse;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.taimos.daemon.spring.SpringDaemonTestRunner;
import de.taimos.httputils.HTTPRequest;
import de.taimos.httputils.WS;
import de.taimos.springcxfdaemon.MapperFactory;

@RunWith(SpringDaemonTestRunner.class)
public abstract class APITest {
	
	private static final String APPLICATION_JSON = "application/json";
	
	@Value("${server.url:http://localhost:${jaxrs.bindport:${svc.port:8080}}}")
	private String serverURL;
	
	
	protected final String getServerURL() {
		return this.serverURL;
	}
	
	protected final HTTPRequest request(String path) {
		return WS.url(this.serverURL + path);
	}
	
	protected final void assertOK(HttpResponse res) {
		Assert.assertTrue(String.format("Expected OK - was %s", WS.getStatus(res)), WS.isStatusOK(res));
	}
	
	protected final void assertStatus(HttpResponse res, Status status) {
		Assert.assertTrue(String.format("Expected %s - was %s", status.getStatusCode(), WS.getStatus(res)), WS.getStatus(res) == status.getStatusCode());
	}
	
	protected final void assertOK(Response res) {
		Assert.assertTrue(String.format("Expected OK - was %s", res.getStatus()), (res.getStatus() >= 200) && (res.getStatus() <= 299));
	}
	
	protected final void assertStatus(Response res, Status status) {
		Assert.assertTrue(String.format("Expected %s - was %s", status.getStatusCode(), res.getStatus()), res.getStatus() == status.getStatusCode());
	}
	
	protected <T> T read(HttpResponse res, Class<T> clazz) {
		try {
			return MapperFactory.createDefault().readValue(res.getEntity().getContent(), clazz);
		} catch (IllegalStateException | IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected Map<String, Object> readMap(HttpResponse res) {
		return this.read(res, Map.class);
	}
	
	protected HTTPRequest jsonBody(HTTPRequest req, Object o) {
		try {
			String json = MapperFactory.createDefault().writeValueAsString(o);
			return req.contentType(APITest.APPLICATION_JSON).body(json);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
