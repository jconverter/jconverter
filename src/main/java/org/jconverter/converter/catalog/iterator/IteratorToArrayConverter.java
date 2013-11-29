package org.jconverter.converter.catalog.iterator;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.minitoolbox.reflection.typewrapper.ArrayTypeWrapper;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;
import org.minitoolbox.reflection.typewrapper.VariableTypeWrapper;

public class IteratorToArrayConverter<T> implements Converter<Iterator, T[]> {

	@Override
	public T[] apply(Iterator source, Type targetType, JConverter context) {
		List collection = (List)new IteratorToCollectionConverter().apply(source, List.class, context);
		Type componentType = null;

		TypeWrapper wrappedType = TypeWrapper.wrap(targetType);
		ArrayTypeWrapper arrayTypeWrapper = (ArrayTypeWrapper) wrappedType;
		componentType = arrayTypeWrapper.getComponentType();
		TypeWrapper componentTypeWrapper = TypeWrapper.wrap(componentType);
//		if(componentTypeWrapper instanceof VariableTypeWrapper) {
//			TypeWrapper itTypeWrapper = TypeWrapper.wrap(source.getClass()).as(Iterator.class);
//			if(itTypeWrapper.hasActualTypeArguments())
//				componentType = itTypeWrapper.getActualTypeArguments()[0];
//			else
//				componentType = Object.class;
//			componentTypeWrapper = TypeWrapper.wrap(componentType);
//		}
		T[] array = (T[]) Array.newInstance(componentTypeWrapper.getRawClass(), collection.size());
		for(int i=0; i<collection.size(); i++) {
			T member = context.convert(collection.get(i), componentType);
			array[i] = member;
		}
		return array;
	}

}
