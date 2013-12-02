package org.jconverter.converter.catalog.iterable;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.catalog.iterator.IteratorToMapConverter;

public class IterableToMapConverter<T extends Map> implements Converter<Iterable<Entry>, T> {

	@Override
	public T apply(Iterable<Entry> source, Type targetType, JConverter context) {
		return new IteratorToMapConverter<T>().apply(source.iterator(), targetType, context);
	}

}
