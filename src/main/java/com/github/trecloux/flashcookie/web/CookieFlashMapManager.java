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
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.module.SimpleModule;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.AbstractFlashMapManager;
import org.springframework.web.util.WebUtils;

/**
 * Cookie based FlashMapManager.
 * Stores the FlashMap in a cookie name "FLASH" as JSON encoded data.
 * @author Thomas Recloux 
 */
@Component("flashMapManager")
public class CookieFlashMapManager extends AbstractFlashMapManager {

	private static final String COOKIE_NAME = "FLASH";
	private static final String MAP_ATTR = "map";
	private static final String REQUEST_PATH_ATTR = "targetRequestPath";
	private static final String ENCODING = "UTF-8";
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	public CookieFlashMapManager() {
		 initCustomSerializer();
	}

	protected void initCustomSerializer() {
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
	
	@SuppressWarnings("unchecked")
	protected List<FlashMap> decodeFlashMaps(String base64EncodedValue) {
		try {
			byte[] data = Base64.decode(base64EncodedValue);
			String stringEncodedValue = new String(data, ENCODING);
			List<Map<String, Object>> maps = objectMapper.readValue(stringEncodedValue, List.class);
			List<FlashMap> flashMaps = rebuildFlashMap(maps);
			return flashMaps;
		} catch (IOException e) {
			throw new RuntimeException("Error decoding flash map", e);
		}
	}

	@SuppressWarnings("unchecked")
	private List<FlashMap> rebuildFlashMap(List<Map<String, Object>> maps) {
		List<FlashMap> flashMaps = new ArrayList<FlashMap>();
		for (Map<String, Object> map : maps) {
			FlashMap flashMap = new FlashMap();
			flashMap.setTargetRequestPath((String) map.get(REQUEST_PATH_ATTR));
			flashMap.putAll((Map<String, Object>) map.get(MAP_ATTR));
			flashMaps.add(flashMap);
		}
		return flashMaps;
	}

	@Override
	protected void updateFlashMaps(List<FlashMap> flashMaps, HttpServletRequest request, HttpServletResponse response) {
		String encodedValue = encodeFlashMaps(flashMaps);
		Cookie cookie = new Cookie(COOKIE_NAME,encodedValue);
		response.addCookie(cookie);
	}

	protected String encodeFlashMaps(List<FlashMap> flashMap) {
		try {
			String encodedValue = objectMapper.writeValueAsString(flashMap);
			byte[] data = encodedValue.getBytes(ENCODING);
			return Base64.encodeToString(data, false);
		} catch (IOException e) {
			throw new RuntimeException("Error encoding flash map", e);
		}
	}

	
	/*
	 * Custom JSON Serialiser of the FlasMap.
	 * Flashmap extends java.util.Map and Jackson does not encode specific attributes.   
	 */
	private class FlashMapSerializer extends JsonSerializer<FlashMap> {

		@Override
		public void serialize(FlashMap flashMap, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
			jgen.writeStartObject();
			jgen.writeStringField(REQUEST_PATH_ATTR, flashMap.getTargetRequestPath());
			serializerProvider.defaultSerializeField(MAP_ATTR, new HashMap<String, Object>(flashMap), jgen);
			jgen.writeEndObject();
		}

		@Override
		public Class<FlashMap> handledType() {
			return FlashMap.class;
		}
		
	}	
}
