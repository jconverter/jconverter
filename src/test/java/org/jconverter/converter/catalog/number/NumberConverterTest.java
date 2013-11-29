package org.jconverter.converter.catalog.number;

import static org.junit.Assert.assertEquals;

import org.jconverter.JConverter;
import org.junit.Test;

public class NumberConverterTest {

	@Test
	public void testToObject() {
		JConverter jconverter = new JConverter();
		assertEquals(123, jconverter.convert(123, Object.class));
	}
	
	@Test
	public void testToString() {
		JConverter jconverter = new JConverter();
		assertEquals("123", jconverter.convert(123, String.class));
	}
	
	@Test
	public void testIntToLong() {
		JConverter jconverter = new JConverter();
		assertEquals(123L, jconverter.convert(123, Long.class));
	}

}
