package org.jconverter.converter.catalog.iterable;

import java.lang.reflect.Type;
import java.util.Collection;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.catalog.iterator.IteratorToCollectionConverter;

public class IterableToCollectionConverter<T extends Collection> implements Converter<Iterable, T> {

	@Override
	public T apply(Iterable source, Type targetType, JConverter context) {
		return (T) new IteratorToCollectionConverter().apply(source.iterator(), targetType, context);
	}

}
