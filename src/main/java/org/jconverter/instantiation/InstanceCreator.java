package org.jconverter.instantiation;

import java.lang.reflect.Type;

/**
 * Interface implemented by all the JConverter instance creators.
 * @author sergioc
 *
 * @param <T> the type this converter instantiates.
 */
public interface InstanceCreator<T> {

	/**
	 * 
	 * @param type the type to instantiate.
	 * @return an instance of the requested type.
	 */
	public T instantiate(Type type);

}
