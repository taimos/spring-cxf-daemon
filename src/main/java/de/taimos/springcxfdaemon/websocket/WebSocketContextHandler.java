/**
 * 
 */
package de.taimos.springcxfdaemon.websocket;

import javax.annotation.PostConstruct;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Copyright 2015 Taimos GmbH<br>
 * <br>
 * 
 * @author thoeger
 * 		
 */
public class WebSocketContextHandler extends ServletContextHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketContextHandler.class);
	
	@Value("${websocket.baseuri:/websocket}")
	private String baseURI;
	
	@Autowired
	private ListableBeanFactory beanFactory;
	
	
	@PostConstruct
	public void init() {
		this.setContextPath(this.baseURI);
		
		String[] socketBeans = this.beanFactory.getBeanNamesForAnnotation(WebSocket.class);
		for (String sb : socketBeans) {
			WebSocket ann = this.beanFactory.findAnnotationOnBean(sb, WebSocket.class);
			WebSocketContextHandler.LOGGER.info("Found bean {} for path {}", sb, ann.pathSpec());
			this.addServlet(new ServletHolder(this.createServletForBeanName(sb)), ann.pathSpec());
		}
	}
	
	private WebSocketServlet createServletForBeanName(final String beanName) {
		return new WebSocketServlet() {
			
			private static final long serialVersionUID = 1L;
			
			
			@Override
			public void configure(WebSocketServletFactory factory) {
				WebSocketContextHandler.LOGGER.info("Configuring WebSocket Servlet for {}", beanName);
				factory.getPolicy().setIdleTimeout(10000);
				factory.setCreator(new WebSocketCreator() {
					
					@Override
					public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
						return WebSocketContextHandler.this.beanFactory.getBean(beanName);
					}
				});
			}
		};
	}
	
}
