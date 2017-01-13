package org.jconverter;

import java.lang.reflect.Type;

import org.jconverter.converter.ConverterManager;
import org.jconverter.converter.JGumConverterManager;
import org.jconverter.factory.FactoryManager;
import org.jconverter.factory.JGumFactoryManager;
import org.jconverter.factory.SingletonFactory;
import org.jconverter.util.typewrapper.TypeWrapper;
import org.jgum.JGum;

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
public class JConverter {
	
	protected final ConverterManager converterManager; //responsible of converting objects.
	protected final FactoryManager factoryManager; //responsible of instantiating objects.
	
	public JConverter() {
		this(new JGum());
	}
	
	/**
	 * @param jgum a categorization context.
	 */
	protected JConverter(JGum jgum) {
		this.converterManager = JGumConverterManager.createDefault(jgum);
		this.factoryManager = JGumFactoryManager.createDefault(jgum);
	}
	
	/**
	 * @param converterManager a converter manager responsible of converting objects.
	 * @param factoryManager an instance creator manager responsible of instantiating objects.
	 */
	public JConverter(ConverterManager converterManager, FactoryManager factoryManager) {
		this.converterManager = converterManager;
		this.factoryManager = factoryManager;
	}

	protected ConverterManager getConverterManager() {
		return converterManager;
	}

	protected FactoryManager getInstantiationManager() {
		return factoryManager;
	}

	/**
	 * 
	 * @param source the object to convert.
	 * @param targetType the desired type.
	 * @return the conversion of the source object to the desired target type.
	 */
	public <T> T convert(Object source, Type targetType) {
		return convert(ConverterManager.DEFAULT_KEY, source, targetType);
	}
	
	/**
	 * 
	 * @param key constrains the registered converters and factories that will be looked up in this operation.
	 * @param source the object to convert.
	 * @param targetType the desired type.
	 * @return the conversion of the source object to the desired target type.
	 */
	public <T> T convert(Object key, Object source, Type targetType) {
		try {
			return new SingletonFactory<T>((T) source).instantiate(targetType); //will launch an exception if the object source is not compatible with the target type.
		} catch(RuntimeException e) {
			return converterManager.convert(key, source, targetType, this);
		}
	}
	
	/**
	 * 
	 * @param targetType the type to instantiate.
	 * @return an instance of the desired type.
	 */
	public <T> T instantiate(Type targetType) {
		return instantiate(FactoryManager.DEFAULT_KEY, targetType);
	}

	/**
	 * 
	 * @param key constrains the instance creators that will be looked up in this operation.
	 * @param targetType the type to instantiate.
	 * @return
	 */
	public <T> T instantiate(Object key, Type targetType) {
		try {
			return factoryManager.instantiate(key, targetType);
		} catch(RuntimeException e) {
			TypeWrapper targetTypeWrapper = TypeWrapper.wrap(targetType);
			Class<T> targetClass = (Class<T>) targetTypeWrapper.getRawClass();
			try {
				return targetClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e1) {
				throw new RuntimeException(e1);
			}
		}
	}
	
}
