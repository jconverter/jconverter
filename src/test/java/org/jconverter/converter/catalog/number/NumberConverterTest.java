package org.jconverter.converter.catalog.number;

import static org.jconverter.JConverter.jConverter;
import static org.junit.Assert.assertEquals;

import org.jconverter.JConverter;
import org.junit.Test;

public class NumberConverterTest {

	@Test
	public void testToObject() {
		JConverter jconverter = jConverter();
		assertEquals(new Integer(123), jconverter.convert(123, Object.class));
	}
	
	@Test
	public void testToString() {
		JConverter jconverter = jConverter();
		assertEquals("123", jconverter.convert(123, String.class));
	}
	
	@Test
	public void testIntToLong() {
		JConverter jconverter = jConverter();
		assertEquals(new Long(123), jconverter.convert(123, Long.class));
		assertEquals(new Long(123), jconverter.convert(123, long.class));
	}

}
