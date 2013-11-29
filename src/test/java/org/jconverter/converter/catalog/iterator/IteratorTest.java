package org.jconverter.converter.catalog.iterator;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.jconverter.JConverter;
import org.junit.Test;

import com.google.common.reflect.TypeToken;

public class IteratorTest {

	private Iterator<Integer> getIterator() {
		return asList(1,2,3).iterator();
	}
	@Test
	public void testIteratorToCollection() {
		JConverter jconverter = new JConverter();
		Type type;
		Collection collection;
		
		type = new TypeToken<Collection<String>>(){}.getType();
		collection = jconverter.convert(getIterator(), type);
		assertEquals(3, collection.size());
		assertTrue(collection.contains("1"));
		
		type = new TypeToken<List<String>>(){}.getType();
		collection = jconverter.convert(getIterator(), type);
		assertEquals(3, collection.size());
		assertTrue(collection.contains("1"));
		
		type = new TypeToken<ArrayList<String>>(){}.getType();
		collection = jconverter.convert(getIterator(), type);
		assertEquals(3, collection.size());
		assertTrue(collection.contains("1"));
	}
	
	@Test
	public void testIteratorToIterator() {
		JConverter jconverter = new JConverter();
		Type type = new TypeToken<Iterator<String>>(){}.getType();
		
		Iterator it = jconverter.convert(getIterator(), type);
		assertEquals("1", it.next());
	}
	
	@Test
	public void testIteratorToSequence() {
		JConverter jconverter = new JConverter();
		Type type = new TypeToken<Enumeration<String>>(){}.getType();
		
		Enumeration en = jconverter.convert(getIterator(), type);
		assertEquals("1", en.nextElement());
	}
	
	@Test
	public void testIteratorToArray() {
		JConverter jconverter = new JConverter();
		Type type = new String[]{}.getClass();
		
		String[] array = jconverter.convert(getIterator(), type);
		assertEquals(3, array.length);
		assertEquals("1", array[0]);
	}
	
}
