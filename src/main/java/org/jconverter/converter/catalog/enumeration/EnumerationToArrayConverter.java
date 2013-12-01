package org.jconverter.converter.catalog.enumeration;

import java.lang.reflect.Type;
import java.util.Enumeration;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.catalog.iterator.IteratorToArrayConverter;

import com.google.common.collect.Iterators;

public class EnumerationToArrayConverter<T> implements Converter<Enumeration, T[]> {

	@Override
	public T[] apply(Enumeration source, Type targetType, JConverter context) {
		return (T[]) new IteratorToArrayConverter().apply(Iterators.forEnumeration(source), targetType, context);
	}

}
