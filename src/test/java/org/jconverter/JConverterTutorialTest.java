package org.jconverter;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jconverter.converter.Converter;
import org.junit.Test;

import com.google.common.reflect.TypeToken;

/**
 * Tests for the short demo in the JConverter site.
 * @author sergioc
 *
 */
public class JConverterTutorialTest {

	@Test
	public void testDefaultConverts() {
		JConverter context = new JConverter(); //a default JConverter context
		List<Integer> source = asList(2,1,3); //a list of integers
		Type targetType = new TypeToken<TreeSet<String>>(){}.getType(); //target type is TreeSet<String> (set ordered according to the natural ordering of its members)
		
		//Conversion:
		Set<String> orderedSet = context.convert(source, targetType); //converting source object to target type
		//TESTING RESULT:
		assertTrue(Arrays.equals(new String[]{"1","2","3"}, orderedSet.<String>toArray()));
		
		//Or if you prefer:
		Convertable convertable = new Convertable(source, context);
		orderedSet = convertable.as(targetType);
		//TESTING RESULT:
		assertTrue(Arrays.equals(new String[]{"1","2","3"}, orderedSet.<String>toArray()));
	}
	
	@Test
	public void testCustomConverters() {
		
		class Person {
			String name;
			public Person(String name) {
				this.name = name;
			}
		}
		
		JConverterBuilder builder = JConverterBuilder.create();
		builder.register(new Converter<String, Person>() {
			@Override
			public Person apply(String name, Type targetType, JConverter context) {
				return new Person(name);
			}
		});
		JConverter context = builder.build(); //a custom JConverter context
		Map<String, String> map = new HashMap<String, String>() {{
			put("1", "Sarah");
			put("2", "Abraham");
			put("3", "Isaac");
		}};
		
		Type targetType = new TypeToken<Map<Integer,Person>>(){}.getType(); //target type is Map<Integer,Person>
		
		Convertable convertable = new Convertable(map, context);
		Map<Integer, Person> convertedMap = convertable.as(targetType);
		//TESTING RESULT:
		assertEquals(3, convertedMap.entrySet().size());
		assertEquals("Sarah", convertedMap.get(1).name);
		assertEquals("Abraham", convertedMap.get(2).name);
		assertEquals("Isaac", convertedMap.get(3).name);
	}
	
}
