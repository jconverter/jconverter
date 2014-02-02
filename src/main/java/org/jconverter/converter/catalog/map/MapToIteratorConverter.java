package org.jconverter.converter.catalog.map;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.catalog.iterator.IteratorToIteratorConverter;

public class MapToIteratorConverter implements Converter<Map<?,?>, Iterator<?>> {

	@Override
	public Iterator<?> apply(Map<?,?> source, Type targetType, JConverter context) {
		return new IteratorToIteratorConverter().apply(source.keySet().iterator(), targetType, context);
	}

}
