package org.jconverter.factory;

import java.lang.reflect.Type;

import org.typetools.typewrapper.TypeWrapper;

public class SingletonFactory<T> implements Factory<T> {

	private T singleton;
	
	public SingletonFactory(T singleton) {
		this.singleton = singleton;
	}
	
	@Override
	public T instantiate(Type targetType) {
		TypeWrapper targetTypeWrapper = TypeWrapper.wrap(targetType);
		
		//Implementation note: the code below (commented out) does not work correctly if the target type has type parameters but the source type does not.
		//In that scenario, the problem is that the source type does not provide type data about its components.
//		if(targetTypeWrapper.isWeakAssignableFrom(source.getClass())) 
//			return (T) source;
		
		Class<?> targetClass = targetTypeWrapper.getRawClass();
		if(targetClass.isInstance(singleton)) {
			if(!targetTypeWrapper.hasActualTypeArguments()) //the target type does not have actual type arguments.
				return singleton;
			else {
				Type asTargetType = TypeWrapper.wrap(singleton.getClass()).asType(targetClass);
				if(asTargetType.equals(targetType)) //there are type parameters, and they are the same than the source object.
					return singleton;
			}
		}
		throw new RuntimeException("Target type: " + targetType + " is not compatible with object: " + singleton + ".");
	}

}
