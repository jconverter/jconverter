package org.jconverter.converter.catalog.array;

import static java.util.Arrays.asList;

import java.util.Iterator;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;
import org.jconverter.converter.catalog.iterator.IteratorToIteratorConverter;

public class ArrayToIteratorConverter<T> implements Converter<T[], Iterator<?>> {

	@Override
	public Iterator<?> apply(T[] source, TypeDomain target, JConverter context) {
		return new IteratorToIteratorConverter().apply(asList(source).iterator(), target, context);
	}

}
