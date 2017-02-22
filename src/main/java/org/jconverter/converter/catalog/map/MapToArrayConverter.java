package org.jconverter.converter.catalog.map;

import java.util.Map;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;
import org.jconverter.converter.catalog.iterator.IteratorToArrayConverter;

public class MapToArrayConverter<T> implements Converter<Map<?,?>, T[]> {

	@Override
	public T[] apply(Map<?,?> source, TypeDomain target, JConverter context) {
		return new IteratorToArrayConverter<T>().apply(source.entrySet().iterator(), target, context);
	}

}
