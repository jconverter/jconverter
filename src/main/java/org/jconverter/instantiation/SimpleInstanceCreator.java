package org.jconverter.instantiation;

import java.lang.reflect.Type;

import org.minitoolbox.reflection.typewrapper.TypeWrapper;


public class SimpleInstanceCreator<T> implements InstanceCreator<T> {
	
	private final Class<? extends T> instantiationClass;
	private final TypeWrapper instantiationClassWrapper;
	
	public SimpleInstanceCreator(Class<? extends T> instantiationClass) {
		this.instantiationClass = instantiationClass;
		this.instantiationClassWrapper = TypeWrapper.wrap(instantiationClass);
	}
	
	/**
	 * Instantiates the type received as argument.
	 * This class just invokes the default constructor of the type. 
	 * Specialization classes could guide the instantiation according to type parameters (if any).
	 * @param type
	 * @return an instance of the type send as argument.
	 */
	@Override
	public T instantiate(Type targetType) {
		TypeWrapper targetTypeWrapper = TypeWrapper.wrap(targetType);
		Class targetTypeRawClass = targetTypeWrapper.getRawClass();
		if(!targetTypeRawClass.isAssignableFrom(instantiationClass))
			throw new RuntimeException("Uncompatible types.");
		try {
			return (T) instantiationClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
