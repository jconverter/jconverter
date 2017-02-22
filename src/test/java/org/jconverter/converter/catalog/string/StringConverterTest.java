package org.jconverter.converter.catalog.string;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.jconverter.JConverter;
import org.jconverter.converter.NotSuitableConverterException;
import org.junit.Test;

public class StringConverterTest {
	
	@Test
	public void testToObject() {
		JConverter jconverter = new JConverter();
		assertEquals("hello", jconverter.convert("hello", Object.class));
	}
	
	@Test
	public void testToString() {
		JConverter jconverter = new JConverter();
		assertEquals("hello", jconverter.convert("hello", String.class));
	}
	
	@Test
	public void testToCharacter() {
		JConverter jconverter = new JConverter();
		assertEquals(new Character('h'), jconverter.convert("h", Character.class));
		try {
			assertEquals(new Character('h'), jconverter.convert("hello", Character.class));
			fail();
		} catch(NotSuitableConverterException e) {}
		
	}
	
	@Test
	public void testToBoolean() {
		JConverter jconverter = new JConverter();
		assertEquals(true, jconverter.convert("true", Boolean.class));
		assertEquals(false, jconverter.convert("false", Boolean.class));
	}
	
	@Test
	public void testToByte() {
		JConverter jconverter = new JConverter();
		assertEquals(new Byte((byte) 123), jconverter.convert("123", Byte.class));
	}
	
	@Test
	public void testToShort() {
		JConverter jconverter = new JConverter();
		assertEquals(new Short((short) 123), jconverter.convert("123", Short.class));
	}
	
	@Test
	public void testToInteger() {
		JConverter jconverter = new JConverter();
		assertEquals(new Integer(123), jconverter.convert("123", Integer.class));
	}
	
	@Test
	public void testToLong() {
		JConverter jconverter = new JConverter();
		assertEquals(new Long(123L), jconverter.convert("123", Long.class));
	}
	
	@Test
	public void testToFloat() {
		JConverter jconverter = new JConverter();
		assertEquals(new Float(123F), jconverter.convert("123", Float.class));
	}
	
	@Test
	public void testToDouble() {
		JConverter jconverter = new JConverter();
		assertEquals(new Double(123D), jconverter.convert("123", Double.class));
	}
	
	@Test
	public void testToBigInteger() {
		JConverter jconverter = new JConverter();
		assertEquals(new BigInteger(("123")), jconverter.convert("123", BigInteger.class));
	}
	
	@Test
	public void testToBigDecimal() {
		JConverter jconverter = new JConverter();
		assertEquals(new BigDecimal(123), jconverter.convert("123", BigDecimal.class));
	}
	
	@Test
	public void testToAtomicInteger() {
		JConverter jconverter = new JConverter();
		AtomicInteger ai = jconverter.convert("123", AtomicInteger.class);
		assertEquals(123, ai.get());
	}
	
	@Test
	public void testToAtomicLong() {
		JConverter jconverter = new JConverter();
		AtomicLong al = jconverter.convert("123", AtomicLong.class);
		assertEquals(123L, al.get());
	}
	
}

