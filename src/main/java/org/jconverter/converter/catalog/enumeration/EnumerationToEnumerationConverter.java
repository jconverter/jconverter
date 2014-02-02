package org.jconverter.converter.catalog.enumeration;

import java.lang.reflect.Type;
import java.util.Enumeration;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.catalog.iterator.IteratorToEnumerationConverter;

import com.google.common.collect.Iterators;

public class EnumerationToEnumerationConverter implements Converter<Enumeration<?>, Enumeration<?>> {

	@Override
	public Enumeration<?> apply(Enumeration<?> source, Type targetType, JConverter context) {
		return new IteratorToEnumerationConverter().apply(Iterators.forEnumeration(source), targetType, context);
	}

}
