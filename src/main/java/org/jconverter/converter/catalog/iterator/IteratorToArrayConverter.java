package org.jconverter.converter.catalog.iterator;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.minitoolbox.reflection.reification.ParameterizedTypeImpl;
import org.minitoolbox.reflection.typewrapper.ArrayTypeWrapper;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;

public class IteratorToArrayConverter<T> implements Converter<Iterator<?>, T[]> {

	@Override
	public T[] apply(Iterator<?> source, Type targetType, JConverter context) {
		TypeWrapper wrappedType = TypeWrapper.wrap(targetType);
		ArrayTypeWrapper arrayTypeWrapper = (ArrayTypeWrapper) wrappedType;
		Type arrayComponentType = arrayTypeWrapper.getComponentType();
		TypeWrapper componentTypeWrapper = TypeWrapper.wrap(arrayComponentType);
		
		Type listType = new ParameterizedTypeImpl(new Type[]{arrayComponentType}, null, List.class);
		List<T> list = (List)new IteratorToCollectionConverter().apply(source, listType, context);
		T[] array = (T[]) Array.newInstance(componentTypeWrapper.getRawClass(), list.size());
		return list.toArray(array);
	}

}
