package org.jconverter;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import com.google.common.reflect.TypeToken;

public class JConverterTutorial {

	@Test
	public void testConvertable() {
		JConverter context = new JConverter();
		Convertable convertable = new Convertable(asList(2,1,3), context);
		Type targetType = new TypeToken<TreeSet<String>>(){}.getType();
		System.out.println(targetType);
		Set<String> orderedSet = convertable.as(targetType);
		System.out.println(orderedSet);
	}
	
}
