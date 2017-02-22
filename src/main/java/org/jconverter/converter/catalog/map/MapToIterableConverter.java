package org.jconverter.converter.catalog.map;

import java.util.Map;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;
import org.jconverter.converter.catalog.iterator.IteratorToIterableConverter;

public class MapToIterableConverter implements Converter<Map<?,?>, Iterable<?>> {

	@Override
	public Iterable<?> apply(Map<?,?> source, TypeDomain target, JConverter context) {
		return new IteratorToIterableConverter().apply(source.entrySet().iterator(), target, context);
	}

}