package org.jconverter.instantiation;

import java.lang.reflect.Type;

public class SingletonInstanceCreator<T> implements InstanceCreator<T> {

	private Object singleton;
	
	public SingletonInstanceCreator(Object singleton) {
		this.singleton = singleton;
	}
	
	@Override
	public T instantiate(Type type) {
		return (T) singleton;
	}

}
