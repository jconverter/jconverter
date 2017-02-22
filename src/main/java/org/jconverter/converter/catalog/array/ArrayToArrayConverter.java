package org.jconverter.converter.catalog.array;

import static java.util.Arrays.asList;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;
import org.jconverter.converter.catalog.iterator.IteratorToArrayConverter;

public class ArrayToArrayConverter<T, U> implements Converter<T[], U[]> {

	@Override
	public U[] apply(T[] source, TypeDomain target, JConverter context) {
		return new IteratorToArrayConverter<U>().apply(asList(source).iterator(), target, context);
	}

}
