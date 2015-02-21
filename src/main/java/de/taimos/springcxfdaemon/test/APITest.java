package de.taimos.springcxfdaemon.test;

import javax.ws.rs.core.Response.Status;

import org.apache.http.HttpResponse;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;

import de.taimos.daemon.spring.SpringDaemonTestRunner;
import de.taimos.httputils.HTTPRequest;
import de.taimos.httputils.WS;

@RunWith(SpringDaemonTestRunner.class)
public abstract class APITest {
	
	@Value("${server.url:http://localhost:${jaxrs.bindport:${svc.port:8080}}}")
	private String serverURL;
	
	
	protected final String getServerURL() {
		return this.serverURL;
	}
	
	protected final HTTPRequest request(String path) {
		return WS.url(this.serverURL + path);
	}
	
	protected final void assertOK(HttpResponse res) {
		Assert.assertTrue(WS.isStatusOK(res));
	}
	
	protected final void assertStatus(HttpResponse res, Status status) {
		Assert.assertTrue(WS.getStatus(res) == status.getStatusCode());
	}
	
}
