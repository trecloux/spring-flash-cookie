package com.github.trecloux.flashcookie.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.module.SimpleModule;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.AbstractFlashMapManager;
import org.springframework.web.util.WebUtils;

@Component("flashMapManager")
public class CookieFlashMapManager extends AbstractFlashMapManager {

	private static final String COOKIE_NAME = "FLASH";
	private ObjectMapper objectMapper = new ObjectMapper();
	
	public CookieFlashMapManager() {
		 SimpleModule module =  new SimpleModule("FlahsMapSerializerModule",  new Version(1, 0, 0, null));
		 module.addSerializer(new FlashMapSerializer());
		 objectMapper.registerModule(module);
	}

	@Override
	protected List<FlashMap> retrieveFlashMaps(HttpServletRequest request) {
		Cookie cookie = WebUtils.getCookie(request, COOKIE_NAME);
		if (cookie == null) {
			return new ArrayList<FlashMap>();
		} else {
			String encodedValue = cookie.getValue();
			return decodeFlashMaps(encodedValue);
		}
	}

	@Override
	protected void updateFlashMaps(List<FlashMap> flashMaps, HttpServletRequest request, HttpServletResponse response) {
		String encodedValue = encodeFlashMaps(flashMaps);
		Cookie cookie = new Cookie(COOKIE_NAME,encodedValue);
		response.addCookie(cookie);
	}

	private String encodeFlashMaps(List<FlashMap> flashMap) {
		try {
			String encodedValue = objectMapper.writeValueAsString(flashMap);
			byte[] data = encodedValue.getBytes("UTF-8");
			return Base64.encodeToString(data, false);
		} catch (IOException e) {
			throw new RuntimeException("Error encoding flash map", e);
		}
	}

	@SuppressWarnings("unchecked")
	protected List<FlashMap> decodeFlashMaps(String encodedValue) {
		try {
			byte[] data = Base64.decode(encodedValue);
			encodedValue = new String(data, "UTF-8");
			List<Map<String, Object>> maps = objectMapper.readValue(encodedValue, List.class);
			List<FlashMap> flashMaps = new ArrayList<FlashMap>();
			for (Map<String, Object> map : maps) {
				FlashMap flashMap = new FlashMap();
				flashMap.setTargetRequestPath((String) map.get("targetRequestPath"));
				flashMap.putAll((Map<String, Object>) map.get("map"));
				flashMaps.add(flashMap);
			}
			
			return flashMaps;
		} catch (IOException e) {
			throw new RuntimeException("Error decoding flash map", e);
		}
	}
	
	public class FlashMapSerializer extends JsonSerializer<FlashMap> {

		@Override
		public void serialize(FlashMap flashMap, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException,
				JsonProcessingException {
			
			jgen.writeStartObject();
			jgen.writeStringField("targetRequestPath", flashMap.getTargetRequestPath());
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.putAll(flashMap);
			serializerProvider.defaultSerializeField("map", map, jgen);
			jgen.writeEndObject();
		
			
		}

		@Override
		public Class<FlashMap> handledType() {
			return FlashMap.class;
		}
		
	}	
}
