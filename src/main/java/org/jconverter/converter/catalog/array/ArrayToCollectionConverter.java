package org.jconverter.converter.catalog.array;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.util.Collection;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.catalog.iterator.IteratorToCollectionConverter;

public class ArrayToCollectionConverter<T,U extends Collection<?>> implements Converter<T[], U> {

	@Override
	public U apply(T[] source, Type targetType, JConverter context) {
		return new IteratorToCollectionConverter<U>().apply(asList(source).iterator(), targetType, context);
	}

}
