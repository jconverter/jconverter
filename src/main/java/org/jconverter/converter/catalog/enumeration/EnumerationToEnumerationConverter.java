package org.jconverter.converter.catalog.enumeration;

import java.util.Enumeration;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;
import org.jconverter.converter.catalog.iterator.IteratorToEnumerationConverter;

import com.google.common.collect.Iterators;

public class EnumerationToEnumerationConverter implements Converter<Enumeration<?>, Enumeration<?>> {

	@Override
	public Enumeration<?> apply(Enumeration<?> source, TypeDomain target, JConverter context) {
		return new IteratorToEnumerationConverter().apply(Iterators.forEnumeration(source), target, context);
	}

}
