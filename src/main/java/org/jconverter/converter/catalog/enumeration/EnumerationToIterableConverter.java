package org.jconverter.converter.catalog.enumeration;

import java.lang.reflect.Type;
import java.util.Enumeration;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.catalog.iterator.IteratorToIterableConverter;

import com.google.common.collect.Iterators;

public class EnumerationToIterableConverter implements Converter<Enumeration, Iterable> {

	@Override
	public Iterable apply(Enumeration source, Type targetType, JConverter context) {
		return new IteratorToIterableConverter().apply(Iterators.forEnumeration(source), targetType, context);
	}

}
