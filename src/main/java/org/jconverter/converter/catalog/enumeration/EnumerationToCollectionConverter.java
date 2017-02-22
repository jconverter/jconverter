package org.jconverter.converter.catalog.enumeration;

import java.util.Collection;
import java.util.Enumeration;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;
import org.jconverter.converter.catalog.iterator.IteratorToCollectionConverter;

import com.google.common.collect.Iterators;

public class EnumerationToCollectionConverter<T extends Collection<?>> implements Converter<Enumeration<?>, T> {

	@Override
	public T apply(Enumeration<?> source, TypeDomain target, JConverter context) {
		return new IteratorToCollectionConverter<T>().apply(Iterators.forEnumeration(source), target, context);
	}

}
