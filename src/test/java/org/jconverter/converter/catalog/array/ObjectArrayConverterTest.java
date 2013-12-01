package org.jconverter.converter.catalog.array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jconverter.JConverter;
import org.junit.Test;

import com.google.common.reflect.TypeToken;

public class ObjectArrayConverterTest {

	private static final Integer[] arrayIntegers = new Integer[]{1,2,3};
	private static final Integer[][] arraysIntegers = new Integer[][]{{1,2,3}};
	
	@Test
	public void testArrayToCollection() {
		JConverter jconverter = new JConverter();
		Type type;
		Collection collection;
		
		type = new TypeToken<Collection<String>>(){}.getType();
		collection = jconverter.convert(arrayIntegers, type);
		assertEquals(3, collection.size());
		assertTrue(collection.contains("1"));
	}
	
	@Test
	public void testArrayToArray() {
		JConverter jconverter = new JConverter();
		Type type;
		Double[] newArray;
		
		type = new Double[]{}.getClass();
		newArray = jconverter.convert(arrayIntegers, type);
		assertEquals(3, newArray.length);
		assertEquals(new Double(1.0), newArray[0]);
	}
	
	@Test
	public void testArraysToArrays() {
		JConverter jconverter = new JConverter();
		Type type;
		Double[][] newArray;
		
		type = new Double[][]{}.getClass();
		newArray = jconverter.convert(arraysIntegers, type);
		assertEquals(1, newArray.length);
		assertEquals(3, newArray[0].length);
		assertEquals(new Double(1.0), newArray[0][0]);
	}
	
	@Test
	public void testArraysToCollectionArray() {
		JConverter jconverter = new JConverter();
		Type type;
		Collection<Double>[] newArray;
		
		type = new Collection[]{}.getClass();
		newArray = jconverter.convert(arraysIntegers, type);
		assertEquals(1, newArray.length);
		assertEquals(3, newArray[0].size());
	}
	
	@Test
	public void testArraysToCollectionOfArrays() {
		JConverter jconverter = new JConverter();
		Type type;
		List<Double[]> collection = new ArrayList<>();
		
		type = new TypeToken<List<Double[]>>(){}.getType();
		collection = jconverter.convert(arraysIntegers, type);
		assertEquals(1, collection.size());
		assertEquals(3, collection.get(0).length);
		assertEquals(new Double(1.0), collection.get(0)[0]);
	}
	
}
