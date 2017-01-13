package org.jconverter.converter.catalog.iterator;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.internal.reification.ParameterizedTypeImpl;
import org.jconverter.util.typewrapper.TypeWrapper;

public class IteratorToEnumerationConverter implements Converter<Iterator<?>, Enumeration<?>> {

	@Override
	public Enumeration<?> apply(Iterator<?> source, Type targetType, JConverter context) {
		TypeWrapper wrappedTargetType = TypeWrapper.wrap(targetType);
		Type componentType = null;
		TypeWrapper enumerationTypeWrapper = wrappedTargetType.as(Enumeration.class);
		if(enumerationTypeWrapper.hasActualTypeArguments())
			componentType = enumerationTypeWrapper.getActualTypeArguments()[0];
		else
			componentType = Object.class;
		
		Type listType = new ParameterizedTypeImpl(new Type[]{componentType}, null, List.class);
		
		List list = (List) new IteratorToCollectionConverter().apply(source, listType, context);
		return Collections.enumeration(list);
	}

}
