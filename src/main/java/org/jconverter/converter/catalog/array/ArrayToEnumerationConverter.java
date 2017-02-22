package org.jconverter.converter.catalog.array;

import static java.util.Arrays.asList;

import java.util.Enumeration;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;
import org.jconverter.converter.catalog.iterator.IteratorToEnumerationConverter;

public class ArrayToEnumerationConverter<T> implements Converter<T[], Enumeration<?>> {

	@Override
	public Enumeration<?> apply(T[] source, TypeDomain target, JConverter context) {
		return new IteratorToEnumerationConverter().apply(asList(source).iterator(), target, context);
	}

}
