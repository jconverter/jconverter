package org.jconverter.converter.catalog.iterator;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;
import org.typeutils.typewrapper.TypeWrapper;

public class IteratorToMapConverter<T extends Map<?,?>> implements Converter<Iterator<Entry<?,?>>, T> {

	@Override
	public T apply(Iterator<Entry<?,?>> source, TypeDomain target, JConverter context) {
		T map = context.instantiate(target.getType());
		TypeWrapper wrappedType = TypeWrapper.wrap(target.getType());
		while(source.hasNext()) {
			Entry<?,?> entry = source.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			if(wrappedType.hasActualTypeArguments()) {
				key = context.convert(key, wrappedType.getActualTypeArguments()[0]);
				value = context.convert(value, wrappedType.getActualTypeArguments()[1]);
			}
			((Map)map).put(key, value);
		}
		return map;
	}

}
