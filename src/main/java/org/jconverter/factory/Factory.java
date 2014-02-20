package org.jconverter.factory;

import java.lang.reflect.Type;

/**
 * Interface implemented by the JConverter factories.
 * @author sergioc
 *
 * @param <T> the type this factory instantiates.
 */
public interface Factory<T> {

	/**
	 * 
	 * @param type the type to instantiate.
	 * @return an instance of the requested type.
	 */
	public T instantiate(Type type);

}
