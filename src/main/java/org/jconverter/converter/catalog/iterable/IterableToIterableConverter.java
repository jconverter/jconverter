package org.jconverter.converter.catalog.iterable;

import java.lang.reflect.Type;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.catalog.iterator.IteratorToIterableConverter;

public class IterableToIterableConverter implements Converter<Iterable, Iterable> {

	@Override
	public Iterable apply(Iterable source, Type targetType, JConverter context) {
		return new IteratorToIterableConverter().apply(source.iterator(), targetType, context);
	}

}
