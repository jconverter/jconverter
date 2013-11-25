package org.jconverter.instantiation;

import java.lang.reflect.Type;

public interface InstanceCreator<T> {

	public T instantiate(Type type);

}
