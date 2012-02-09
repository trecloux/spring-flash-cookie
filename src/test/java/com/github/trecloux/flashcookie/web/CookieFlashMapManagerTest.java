package com.github.trecloux.flashcookie.web;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.springframework.web.servlet.FlashMap;

public class CookieFlashMapManagerTest {
	
	CookieFlashMapManager cookieFlashMapManager = new CookieFlashMapManager();

	@Test
	public void shoudEncodeAndDecodeABasicFlashMap() throws Exception {
		FlashMap flashMap = new FlashMap();
		flashMap.setTargetRequestPath("/myuri");
		flashMap.put("foo", "bar");
		
		String encodedValue = cookieFlashMapManager.encodeFlashMaps(Collections.singletonList(flashMap));
		List<FlashMap> flashMaps = cookieFlashMapManager.decodeFlashMaps(encodedValue);
		
		assertEquals(1, flashMaps.size());
		flashMap = flashMaps.get(0);
		assertEquals("/myuri", flashMap.getTargetRequestPath());
		assertEquals("bar", flashMap.get("foo"));
	}
	
}
