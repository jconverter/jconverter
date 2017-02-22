package org.jconverter.converter.catalog.map;

import java.util.Enumeration;
import java.util.Map;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;
import org.jconverter.converter.catalog.iterator.IteratorToEnumerationConverter;

public class MapToEnumerationConverter implements Converter<Map<?,?>, Enumeration<?>> {

	@Override
	public Enumeration<?> apply(Map<?,?> source, TypeDomain target, JConverter context) {
		return new IteratorToEnumerationConverter().apply(source.entrySet().iterator(), target, context);
	}

}