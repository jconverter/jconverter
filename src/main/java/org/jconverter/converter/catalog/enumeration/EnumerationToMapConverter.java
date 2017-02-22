package org.jconverter.converter.catalog.enumeration;

import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;
import org.jconverter.converter.catalog.iterator.IteratorToMapConverter;

import com.google.common.collect.Iterators;

public class EnumerationToMapConverter<T extends Map<?,?>> implements Converter<Enumeration<Entry<?,?>>, T> {

	@Override
	public T apply(Enumeration<Entry<?,?>> source, TypeDomain target, JConverter context) {
		return new IteratorToMapConverter<T>().apply(Iterators.forEnumeration(source), target, context);
	}

}
