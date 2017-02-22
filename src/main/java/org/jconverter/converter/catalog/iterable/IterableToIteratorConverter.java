package org.jconverter.converter.catalog.iterable;

import java.util.Iterator;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;
import org.jconverter.converter.catalog.iterator.IteratorToIteratorConverter;

public class IterableToIteratorConverter implements Converter<Iterable<?>, Iterator<?>> {

	@Override
	public Iterator<?> apply(Iterable<?> source, TypeDomain target, JConverter context) {
		return new IteratorToIteratorConverter().apply(source.iterator(), target, context);
	}

}
