package org.jconverter.converter.catalog.array;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.util.Iterator;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.catalog.iterator.IteratorToIteratorConverter;

public class ArrayToIteratorConverter<T> implements Converter<T[], Iterator<?>> {

	@Override
	public Iterator<?> apply(T[] source, Type targetType, JConverter context) {
		return new IteratorToIteratorConverter().apply(asList(source).iterator(), targetType, context);
	}

}
