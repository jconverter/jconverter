package org.jconverter.converter.catalog.array;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.catalog.iterator.IteratorToMapConverter;

public class ArrayToMapConverter<T extends Map> implements Converter<Entry[], T> {

	@Override
	public T apply(Entry[] source, Type targetType, JConverter context) {
		return new IteratorToMapConverter<T>().apply(asList(source).iterator(), targetType, context);
	}

}
