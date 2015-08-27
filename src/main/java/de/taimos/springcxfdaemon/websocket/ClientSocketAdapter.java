package de.taimos.springcxfdaemon.websocket;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.WriteCallback;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.taimos.springcxfdaemon.MapperFactory;

/**
 * Copyright 2015 Taimos GmbH<br>
 * <br>
 *
 * Socket adapter for clients
 *
 * @author thoeger
 *		
 */
public class ClientSocketAdapter extends WebSocketAdapter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientSocketAdapter.class);
	
	private ObjectMapper mapper = MapperFactory.createDefault();
	
	
	/**
	 * this method is called when the request to the server is created. You can use it to modify the request like settings headers for
	 * example.
	 * 
	 * @param req the created request that can be modified
	 */
	@SuppressWarnings("unused")
	public void modifyRequest(ClientUpgradeRequest req) {
		//
	}
	
	/**
	 * send the given object to the server using JSON serialization
	 * 
	 * @param o the object to send to the server
	 */
	public final void sendObjectToSocket(Object o) {
		Session sess = this.getSession();
		if (sess != null) {
			String json;
			try {
				json = this.mapper.writeValueAsString(o);
			} catch (JsonProcessingException e) {
				ClientSocketAdapter.LOGGER.error("Failed to serialize object", e);
				return;
			}
			sess.getRemote().sendString(json, new WriteCallback() {
				
				@Override
				public void writeSuccess() {
					ClientSocketAdapter.LOGGER.info("Send data to socket");
				}
				
				@Override
				public void writeFailed(Throwable x) {
					ClientSocketAdapter.LOGGER.error("Error sending message to socket", x);
				}
			});
		}
	}
	
	/**
	 * reads the received string into the given class by parsing JSON
	 * 
	 * @param <T> the expected type
	 * @param message the JSON string
	 * @param clazz the target class of type T
	 * @return the parsed object or null if parsing was not possible or message is null
	 */
	protected final <T> T readMessage(String message, Class<T> clazz) {
		if ((message == null) || message.isEmpty()) {
			ClientSocketAdapter.LOGGER.info("Got empty session data");
			return null;
		}
		try {
			return this.mapper.readValue(message, clazz);
		} catch (IOException e1) {
			ClientSocketAdapter.LOGGER.info("Got invalid session data", e1);
			return null;
		}
	}
}
