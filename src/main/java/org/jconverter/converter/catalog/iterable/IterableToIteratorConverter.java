package org.jconverter.converter.catalog.iterable;

import java.lang.reflect.Type;
import java.util.Iterator;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.catalog.iterator.IteratorToIteratorConverter;

public class IterableToIteratorConverter implements Converter<Iterable<?>, Iterator<?>> {

	@Override
	public Iterator<?> apply(Iterable<?> source, Type targetType, JConverter context) {
		return new IteratorToIteratorConverter().apply(source.iterator(), targetType, context);
	}

}
