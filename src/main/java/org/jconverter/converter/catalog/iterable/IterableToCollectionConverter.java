package org.jconverter.converter.catalog.iterable;

import java.lang.reflect.Type;
import java.util.Collection;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.catalog.iterator.IteratorToCollectionConverter;

public class IterableToCollectionConverter implements Converter<Iterable, Collection> {

	@Override
	public Collection apply(Iterable source, Type targetType, JConverter context) {
		return new IteratorToCollectionConverter().apply(source.iterator(), targetType, context);
	}

}
