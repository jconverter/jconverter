package org.jconverter.converter.catalog.iterator;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;

public class IteratorToMapConverter<T extends Map> implements Converter<Iterator<Entry>, T> {

	@Override
	public T apply(Iterator<Entry> source, Type targetType, JConverter context) {
		T map = context.instantiate(targetType);
		TypeWrapper wrappedType = TypeWrapper.wrap(targetType);
		while(source.hasNext()) {
			Entry entry = source.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			if(wrappedType.hasActualTypeArguments()) {
				key = context.convert(key, wrappedType.getActualTypeArguments()[0]);
				value = context.convert(value, wrappedType.getActualTypeArguments()[1]);
			}
			map.put(key, value);
		}
		return map;
	}

}