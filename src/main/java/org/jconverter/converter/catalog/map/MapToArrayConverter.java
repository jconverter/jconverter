package org.jconverter.converter.catalog.map;

import java.lang.reflect.Type;
import java.util.Map;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.catalog.iterator.IteratorToArrayConverter;

public class MapToArrayConverter<T> implements Converter<Map, T[]> {

	@Override
	public T[] apply(Map source, Type targetType, JConverter context) {
		return new IteratorToArrayConverter<T>().apply(source.entrySet().iterator(), targetType, context);
	}

}
