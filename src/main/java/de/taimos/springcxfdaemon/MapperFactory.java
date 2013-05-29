package de.taimos.springcxfdaemon;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public final class MapperFactory {
	
	private MapperFactory() {
		//
	}
	
	public static ObjectMapper createDefault() {
		ObjectMapper m = new ObjectMapper();
		m.registerModule(new JodaModule());
		m.setSerializationInclusion(Include.NON_NULL);
		m.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		m.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		m.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
		m.enable(MapperFeature.AUTO_DETECT_GETTERS);
		return m;
	}
	
}
