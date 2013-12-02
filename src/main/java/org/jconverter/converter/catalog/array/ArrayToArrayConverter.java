package org.jconverter.converter.catalog.array;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.catalog.iterator.IteratorToArrayConverter;

public class ArrayToArrayConverter<T, U> implements Converter<T[], U[]> {

	@Override
	public U[] apply(T[] source, Type targetType, JConverter context) {
		return new IteratorToArrayConverter<U>().apply(asList(source).iterator(), targetType, context);
	}

}
