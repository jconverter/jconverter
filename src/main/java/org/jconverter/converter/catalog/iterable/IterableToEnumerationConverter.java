package org.jconverter.converter.catalog.iterable;

import java.lang.reflect.Type;
import java.util.Enumeration;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.catalog.iterator.IteratorToEnumerationConverter;

public class IterableToEnumerationConverter implements Converter<Iterable, Enumeration> {

	@Override
	public Enumeration apply(Iterable source, Type targetType, JConverter context) {
		return new IteratorToEnumerationConverter().apply(source.iterator(), targetType, context);
	}

}
