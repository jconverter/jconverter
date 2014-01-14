package org.jconverter;

import java.lang.reflect.Type;

import org.jconverter.converter.ConverterManager;
import org.jconverter.converter.JGumConverterManager;
import org.jconverter.instantiation.InstantiationManager;
import org.jconverter.instantiation.JGumInstantiationManager;
import org.jconverter.instantiation.SingletonInstanceCreator;
import org.jgum.JGum;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;

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
	protected final InstantiationManager instantiationManager; //responsible of instantiating objects.
	
	public JConverter() {
		this(new JGum());
	}
	
	/**
	 * @param jgum a categorization context.
	 */
	protected JConverter(JGum jgum) {
		this.converterManager = JGumConverterManager.createDefault(jgum);
		this.instantiationManager = JGumInstantiationManager.createDefault(jgum);
	}
	
	/**
	 * @param converterManager a converter manager responsible of converting objects.
	 * @param instantiationManager an instance creator manager responsible of instantiating objects.
	 */
	public JConverter(ConverterManager converterManager, InstantiationManager instantiationManager) {
		this.converterManager = converterManager;
		this.instantiationManager = instantiationManager;
	}

	protected ConverterManager getConverterManager() {
		return converterManager;
	}

	protected InstantiationManager getInstantiationManager() {
		return instantiationManager;
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
			return new SingletonInstanceCreator<T>((T) source).instantiate(targetType); //will launch an exception if the object source is not compatible with the target type.
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
		return instantiate(InstantiationManager.DEFAULT_KEY, targetType);
	}

	/**
	 * 
	 * @param key constrains the instance creators that will be looked up in this operation.
	 * @param targetType the type to instantiate.
	 * @return
	 */
	public <T> T instantiate(Object key, Type targetType) {
		try {
			return instantiationManager.instantiate(key, targetType);
		} catch(RuntimeException e) {
			TypeWrapper targetTypeWrapper = TypeWrapper.wrap(targetType);
			Class targetClass = targetTypeWrapper.getRawClass();
			try {
				return (T) targetClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e1) {
				throw new RuntimeException(e1);
			}
		}
	}
	
}
