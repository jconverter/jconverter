package org.jconverter.converter.catalog.iterable;

import java.util.Collection;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;
import org.jconverter.converter.catalog.iterator.IteratorToCollectionConverter;

public class IterableToCollectionConverter<T extends Collection<?>> implements Converter<Iterable<?>, T> {

	@Override
	public T apply(Iterable<?> source, TypeDomain target, JConverter context) {
		return new IteratorToCollectionConverter<T>().apply(source.iterator(), target, context);
	}

}
