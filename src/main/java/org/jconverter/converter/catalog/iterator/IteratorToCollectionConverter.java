package org.jconverter.converter.catalog.iterator;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;
import org.typeutils.typewrapper.TypeWrapper;

public class IteratorToCollectionConverter<T extends Collection<?>> implements Converter<Iterator<?>, T> {

	@Override
	public T apply(Iterator<?> source, TypeDomain target, JConverter context) {
		TypeWrapper targetTypeWrapper = TypeWrapper.wrap(target.getType());
		TypeWrapper itTypeWrapper = targetTypeWrapper.as(Collection.class);
		Type componentType = null;
		if(itTypeWrapper.hasActualTypeArguments()) {
			componentType = itTypeWrapper.getActualTypeArguments()[0];
		} else {
			componentType = Object.class;
		}
		T collection = null;
		collection = context.instantiate(target.getType());
		while(source.hasNext()) {
			Object next = source.next();
			Object converted = context.convert(next, componentType);
			((Collection) collection).add(converted);
		}
		return collection;
	}

}
