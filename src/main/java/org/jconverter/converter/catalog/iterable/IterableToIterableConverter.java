package org.jconverter.converter.catalog.iterable;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;
import org.jconverter.converter.catalog.iterator.IteratorToIterableConverter;

public class IterableToIterableConverter implements Converter<Iterable<?>, Iterable<?>> {

	@Override
	public Iterable<?> apply(Iterable<?> source, TypeDomain target, JConverter context) {
		return new IteratorToIterableConverter().apply(source.iterator(), target, context);
	}

}
