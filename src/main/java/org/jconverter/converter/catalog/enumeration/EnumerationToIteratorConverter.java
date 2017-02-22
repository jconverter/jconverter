package org.jconverter.converter.catalog.enumeration;

import java.util.Enumeration;
import java.util.Iterator;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;
import org.jconverter.converter.catalog.iterator.IteratorToIteratorConverter;

import com.google.common.collect.Iterators;

public class EnumerationToIteratorConverter implements Converter<Enumeration<?>, Iterator<?>> {

	@Override
	public Iterator<?> apply(Enumeration<?> source, TypeDomain target, JConverter context) {
		return new IteratorToIteratorConverter().apply(Iterators.forEnumeration(source), target, context);
	}

}
