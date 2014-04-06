package org.jconverter.converter.catalog.map;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.catalog.iterator.IteratorToMapConverter;

public class MapToMapConverter<T extends Map<?,?>> implements Converter<Map<?,?>, T> {

	@Override
	public T apply(Map<?,?> source, Type targetType, JConverter context) {
		return (T)new IteratorToMapConverter<T>().apply((Iterator)source.entrySet().iterator(), targetType, context);
	}

}
