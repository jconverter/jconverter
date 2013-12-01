package org.jconverter.converter.catalog.iterable;

import java.lang.reflect.Type;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.catalog.iterator.IteratorToArrayConverter;

public class IterableToArrayConverter<T> implements Converter<Iterable, T[]> {

	@Override
	public T[] apply(Iterable source, Type targetType, JConverter context) {
		return (T[]) new IteratorToArrayConverter().apply(source.iterator(), targetType, context);
	}

}
