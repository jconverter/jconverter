package org.jconverter;

import java.lang.reflect.Type;

import org.jconverter.converter.ConverterManager;
import org.jconverter.converter.TypeDomain;
import org.jconverter.factory.FactoryManager;

//This class is inspired by the Gson class from Google's Gson library (http://code.google.com/p/google-gson/)
/**
 * A conversion context.
 * Its main components are:
 *  - The converter manager.
 *  - The instantiation manager.
 *  - The type solver manager.
 * @author sergioc
 *
 */
public interface JConverter {

	Object DEFAULT_CONTEXT_ID = new Object();

	static JConverter jConverter() {
		return new JConverterImpl();
	}

	ConverterManager getConverterManager();

	FactoryManager getFactoryManager();

	<T> T convert(Object source, Type targetType);

	<T> T convert(Object source, TypeDomain target);

	<T> T instantiate(Type targetType);
}
