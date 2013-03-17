package com.github.trecloux.flashcookie;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.FlashMap;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CookieFlashMapManagerTest {
	
	CookieFlashMapManager cookieFlashMapManager;

    @Before
    public void setUp() throws Exception {
        cookieFlashMapManager = new CookieFlashMapManager("myPassword");
        cookieFlashMapManager.afterPropertiesSet();
    }

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
	
	@Test
	public void shoudEncodeAndDecodeAComplexFlashMap() throws Exception {
		FlashMap flashMap = new FlashMap();
		flashMap.setTargetRequestPath("/myuri");
		TestComplexType complexValue = new TestComplexType();
		complexValue.setNumber(7);
		complexValue.setStr("foo");
		TestComplexType subComplexValue = new TestComplexType();
		subComplexValue.setNumber(11);
		subComplexValue.setStr("bar");
		complexValue.setComplex(subComplexValue);
		flashMap.put("complex", complexValue);
		
		String encodedValue = cookieFlashMapManager.encodeFlashMaps(Collections.singletonList(flashMap));
		List<FlashMap> flashMaps = cookieFlashMapManager.decodeFlashMaps(encodedValue);
		
		assertEquals(1, flashMaps.size());
		flashMap = flashMaps.get(0);
		assertEquals("/myuri", flashMap.getTargetRequestPath());
		complexValue = (TestComplexType) flashMap.get("complex");
		assertEquals("foo", complexValue.getStr());
		assertEquals(new Integer(7), complexValue.getNumber());
		subComplexValue = complexValue.getComplex();
		assertEquals("bar", subComplexValue.getStr());
		assertEquals(new Integer(11), subComplexValue.getNumber());
	}
	
	
	
}
