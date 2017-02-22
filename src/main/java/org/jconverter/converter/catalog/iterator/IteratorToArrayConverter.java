package org.jconverter.converter.catalog.iterator;

import static org.jconverter.converter.TypeDomain.typeDomain;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;
import org.typetools.reification.ParameterizedTypeImpl;
import org.typetools.typewrapper.ArrayTypeWrapper;
import org.typetools.typewrapper.TypeWrapper;

public class IteratorToArrayConverter<T> implements Converter<Iterator<?>, T[]> {

	@Override
	public T[] apply(Iterator<?> source, TypeDomain target, JConverter context) {
		TypeWrapper wrappedType = TypeWrapper.wrap(target.getType());
		ArrayTypeWrapper arrayTypeWrapper = (ArrayTypeWrapper) wrappedType;
		Type arrayComponentType = arrayTypeWrapper.getComponentType();
		TypeWrapper componentTypeWrapper = TypeWrapper.wrap(arrayComponentType);
		
		Type listType = new ParameterizedTypeImpl(new Type[]{arrayComponentType}, null, List.class);
		List<T> list = (List)new IteratorToCollectionConverter().apply(source, typeDomain(listType), context);
		T[] array = (T[]) Array.newInstance(componentTypeWrapper.getRawClass(), list.size());
		return list.toArray(array);
	}

}
