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
	
	public class TestComplexType {
		String str;
		Integer number;
		TestComplexType complex;
		public String getStr() {
			return str;
		}
		public void setStr(String str) {
			this.str = str;
		}
		public Integer getNumber() {
			return number;
		}
		public void setNumber(Integer number) {
			this.number = number;
		}
		public TestComplexType getComplex() {
			return complex;
		}
		public void setComplex(TestComplexType complex) {
			this.complex = complex;
		}
	}	
	
}
