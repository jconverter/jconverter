package org.jconverter.converter.catalog.enumeration;

import java.util.Enumeration;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;
import org.jconverter.converter.catalog.iterator.IteratorToArrayConverter;

import com.google.common.collect.Iterators;

public class EnumerationToArrayConverter<T> implements Converter<Enumeration<?>, T[]> {

	@Override
	public T[] apply(Enumeration<?> source, TypeDomain target, JConverter context) {
		return new IteratorToArrayConverter<T>().apply(Iterators.forEnumeration(source), target, context);
	}

}
