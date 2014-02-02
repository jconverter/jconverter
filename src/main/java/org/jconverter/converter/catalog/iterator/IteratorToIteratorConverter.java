package org.jconverter.converter.catalog.iterator;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.minitoolbox.reflection.javatype.ParameterizedTypeImpl;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;

public class IteratorToIteratorConverter implements Converter<Iterator<?>, Iterator<?>> {

	@Override
	public Iterator<?> apply(Iterator<?> source, Type targetType, JConverter context) {
		TypeWrapper wrappedTargetType = TypeWrapper.wrap(targetType);
		Type componentType = null;
		TypeWrapper iteratorTypeWrapper = wrappedTargetType.as(Iterator.class);
		if(iteratorTypeWrapper.hasActualTypeArguments())
			componentType = iteratorTypeWrapper.getActualTypeArguments()[0];
		else
			componentType = Object.class;
		
		Type listType = new ParameterizedTypeImpl(new Type[]{componentType}, null, List.class);
		
		List list = (List) new IteratorToCollectionConverter().apply(source, listType, context);
		return list.iterator();
	}

}
