package org.jconverter.converter.catalog.iterator;

import static org.jconverter.converter.TypeDomain.typeDomain;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;
import org.typeutils.reification.ParameterizedTypeImpl;
import org.typeutils.typewrapper.TypeWrapper;

public class IteratorToIterableConverter implements Converter<Iterator<?>, Iterable<?>> {

	@Override
	public Iterable<?> apply(Iterator<?> source, TypeDomain target, JConverter context) {
		TypeWrapper wrappedTargetType = TypeWrapper.wrap(target.getType());
		Type componentType = null;
		TypeWrapper iterableTypeWrapper = wrappedTargetType.as(Iterable.class);
		if(iterableTypeWrapper.hasActualTypeArguments())
			componentType = iterableTypeWrapper.getActualTypeArguments()[0];
		else
			componentType = Object.class;
		
		Type listType = new ParameterizedTypeImpl(new Type[]{componentType}, null, List.class);
		return (List) new IteratorToCollectionConverter().apply(source, typeDomain(listType), context);
	}

}
