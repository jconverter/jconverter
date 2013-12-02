package org.jconverter.converter.catalog.map;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.jconverter.JConverter;
import org.junit.Test;

import com.google.common.reflect.TypeToken;

public class MapConverterTest {

	private Map getMap() {
		return new HashMap() {{
			put(1,"1");
			put(2,"2");
			put(3,"3");
		}};
	}
	
	@Test
	public void testMapToMap() {
		Map map = getMap();
		JConverter jconverter = new JConverter();
		Type targetType = new TypeToken<Map<String, Integer>>(){}.getType();
		Map newMap = jconverter.convert(map, targetType);
		assertEquals(3, newMap.entrySet().size());
		assertEquals(1, newMap.get("1"));
		assertEquals(2, newMap.get("2"));
		assertEquals(3, newMap.get("3"));
	}
	
}
