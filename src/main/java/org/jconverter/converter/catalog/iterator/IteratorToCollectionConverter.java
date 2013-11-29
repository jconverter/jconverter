package org.jconverter.converter.catalog.iterator;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter.DefaultConverter;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;

public class IteratorToCollectionConverter extends DefaultConverter<Iterator, Collection> {

	@Override
	public Collection apply(Iterator source, Type targetType, JConverter context) {
		TypeWrapper targetTypeWrapper = TypeWrapper.wrap(targetType);
		Class targetClass = targetTypeWrapper.getRawClass();
		TypeWrapper itTypeWrapper = targetTypeWrapper.as(Iterator.class);
		Type componentType = null;
		if(itTypeWrapper.hasActualTypeArguments()) {
			componentType = itTypeWrapper.getActualTypeArguments()[0];
		} else {
			componentType = Object.class;
		}
		Collection collection = null;
		collection = context.instantiate(targetType);
		while(source.hasNext()) {
			Object next = source.next();
			Object converted = context.convert(source, componentType);
			collection.add(converted);
		}
		return collection;
	}

}
