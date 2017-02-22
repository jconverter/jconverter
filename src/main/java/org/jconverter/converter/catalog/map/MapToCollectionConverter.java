package org.jconverter.converter.catalog.map;

import java.util.Collection;
import java.util.Map;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;
import org.jconverter.converter.catalog.iterator.IteratorToCollectionConverter;

public class MapToCollectionConverter<T extends Collection<?>> implements Converter<Map<?,?>, T> {

	@Override
	public T apply(Map<?,?> source, TypeDomain target, JConverter context) {
		return new IteratorToCollectionConverter<T>().apply(source.entrySet().iterator(), target, context);
	}

}
