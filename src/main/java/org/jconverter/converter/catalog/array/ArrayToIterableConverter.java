package org.jconverter.converter.catalog.array;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.catalog.iterator.IteratorToIterableConverter;

public class ArrayToIterableConverter<T> implements Converter<T[], Iterable<?>> {

	@Override
	public Iterable<?> apply(T[] source, Type targetType, JConverter context) {
		return new IteratorToIterableConverter().apply(asList(source).iterator(), targetType, context);
	}

}
