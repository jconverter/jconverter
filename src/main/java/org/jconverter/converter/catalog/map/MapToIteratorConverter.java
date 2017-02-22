package org.jconverter.converter.catalog.map;

import java.util.Iterator;
import java.util.Map;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;
import org.jconverter.converter.catalog.iterator.IteratorToIteratorConverter;

public class MapToIteratorConverter implements Converter<Map<?,?>, Iterator<?>> {

	@Override
	public Iterator<?> apply(Map<?,?> source, TypeDomain target, JConverter context) {
		return new IteratorToIteratorConverter().apply(source.keySet().iterator(), target, context);
	}

}
