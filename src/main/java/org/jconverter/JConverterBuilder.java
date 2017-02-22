package org.jconverter;

import org.jconverter.converter.Converter;
import org.jconverter.converter.ConverterManager;
import org.jconverter.converter.InterTypeConverterManager;
import org.jconverter.factory.Factory;
import org.jconverter.factory.FactoryManager;
import org.jconverter.factory.FactoryManagerImpl;
import org.jgum.JGum;


/**
 * A fluent API for instantiation JConverter contexts.
 * @author sergioc
 *
 */
public class JConverterBuilder {

	protected final ConverterManager converterManager;
	protected final FactoryManager factoryManager;
	
	
	public static JConverterBuilder create() {
		return new JConverterBuilder();
	}
	
	protected JConverterBuilder() {
		this(new JGum());
	}
	
	protected JConverterBuilder(JGum jgum) {
		this(InterTypeConverterManager.createDefault(jgum), FactoryManagerImpl.createDefault(jgum));
	}
	
	protected JConverterBuilder(ConverterManager converterManager, FactoryManager factoryManager) {
		this.converterManager = converterManager;
		this.factoryManager = factoryManager;
	}
	
	/**
	 * 
	 * @return a new JConverter context according to the configured builder.
	 */
	public JConverter build() {
		return new JConverter(converterManager, factoryManager);
	}
	
	/**
	 * Registers a converter.
	 * @param converter a converter to register.
	 */
	public JConverterBuilder register(Converter<?,?> converter) {
		converterManager.register(converter);
		return this;
	}
	
	/**
	 * Registers a converter under a given key.
	 * @param key the key under which the converter is registered.
	 * @param converter the converter to register.
	 */
	public JConverterBuilder register(Object key, Converter<?,?> converter) {
		converterManager.register(key, converter);
		return this;
	}

	/**
	 * Registers a factory.
	 * @param factory the factory to register.
	 */
	public JConverterBuilder register(Factory<?> factory) {
		factoryManager.register(factory);
		return this;
	}
	
	/**
	 * Registers a factory under a given key.
	 * @param key the key under which the instance creator is registered.
	 * @param factory the factory to register.
	 */
	public JConverterBuilder register(Object key, Factory<?> factory) {
		factoryManager.register(key, factory);
		return this;
	}

}
