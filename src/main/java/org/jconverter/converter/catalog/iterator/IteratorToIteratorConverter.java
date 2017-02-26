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

public class IteratorToIteratorConverter implements Converter<Iterator<?>, Iterator<?>> {

	@Override
	public Iterator<?> apply(Iterator<?> source, TypeDomain target, JConverter context) {
		TypeWrapper wrappedTargetType = TypeWrapper.wrap(target.getType());
		Type componentType = null;
		TypeWrapper iteratorTypeWrapper = wrappedTargetType.as(Iterator.class);
		if(iteratorTypeWrapper.hasActualTypeArguments())
			componentType = iteratorTypeWrapper.getActualTypeArguments()[0];
		else
			componentType = Object.class;
		
		Type listType = new ParameterizedTypeImpl(new Type[]{componentType}, null, List.class);
		
		List list = (List) new IteratorToCollectionConverter().apply(source, typeDomain(listType), context);
		return list.iterator();
	}

}
