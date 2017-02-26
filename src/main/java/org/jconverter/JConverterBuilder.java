package org.jconverter;

import static java.util.Collections.emptyList;

import org.jconverter.converter.Converter;
import org.jconverter.converter.ConverterManager;
import org.jconverter.converter.InterTypeConverterManager;
import org.jconverter.factory.Factory;
import org.jconverter.factory.FactoryManager;
import org.jconverter.factory.FactoryManagerImpl;
import org.jcategory.JCategory;
import org.jcategory.category.Key;


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
		this(new JCategory());
	}
	
	protected JConverterBuilder(JCategory categorization) {
		this(InterTypeConverterManager.createDefault(categorization), FactoryManagerImpl.createDefault(categorization));
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
		return new JConverterImpl(converterManager, factoryManager, emptyList());
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
	public JConverterBuilder register(Key key, Converter<?,?> converter) {
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
	public JConverterBuilder register(Key key, Factory<?> factory) {
		factoryManager.register(key, factory);
		return this;
	}

}
