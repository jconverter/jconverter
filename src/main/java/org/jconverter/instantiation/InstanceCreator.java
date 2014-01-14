package org.jconverter.instantiation;

import java.lang.reflect.Type;

/**
 * Interface implemented by all the JConverter instance creators.
 * @author sergioc
 *
 * @param <T> the type this converter instantiates.
 */
//Disclaimer: This class receives its name (instead of just "Factory") to follow the same naming used in Google's Gson library.
public interface InstanceCreator<T> {

	/**
	 * 
	 * @param type the type to instantiate.
	 * @return an instance of the requested type.
	 */
	public T instantiate(Type type);

}
