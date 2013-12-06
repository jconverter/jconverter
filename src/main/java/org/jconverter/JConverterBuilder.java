package org.jconverter;

import org.jconverter.converter.Converter;
import org.jconverter.converter.ConverterManager;
import org.jconverter.converter.JGumConverterManager;
import org.jconverter.instantiation.InstanceCreator;
import org.jconverter.instantiation.InstantiationManager;
import org.jconverter.instantiation.JGumInstantiationManager;
import org.jconverter.typesolver.JGumTypeSolverManager;
import org.jconverter.typesolver.TypeSolver;
import org.jconverter.typesolver.TypeSolverManager;
import org.jgum.JGum;


/**
 * A fluent API for instantiation JConverter contexts.
 * @author sergioc
 *
 */
public class JConverterBuilder {

	protected final ConverterManager converterManager;
	protected final InstantiationManager instantiationManager;
	protected final TypeSolverManager typeSolverManager;
	
	public static JConverterBuilder create() {
		return new JConverterBuilder();
	}
	
	protected JConverterBuilder() {
		JGum jgum = new JGum();
		this.converterManager = JGumConverterManager.createDefault(jgum);
		this.instantiationManager = JGumInstantiationManager.createDefault(jgum);
		this.typeSolverManager = JGumTypeSolverManager.createDefault(jgum);
	}
	
	/**
	 * 
	 * @return a new JConverter context according to the configured builder.
	 */
	public JConverter build() {
		return new JConverter(converterManager, instantiationManager, typeSolverManager);
	}
	
	/**
	 * Registers a converter.
	 * @param converter a converter to register.
	 */
	public void register(Converter converter) {
		converterManager.register(converter);
	}
	
	/**
	 * Registers a converter under a given key.
	 * @param key the key under which the converter is registered.
	 * @param converter the converter to register.
	 */
	public void register(Object key, Converter converter) {
		converterManager.register(key, converter);
	}

	/**
	 * Registers an instance creator.
	 * @param instanceCreator the instance creator to register.
	 */
	public void register(InstanceCreator instanceCreator) {
		instantiationManager.register(instanceCreator);
	}
	
	/**
	 * Registers an instance creator under a given key.
	 * @param key the key under which the instance creator is registered.
	 * @param instanceCreator the instance creator to register.
	 */
	public void register(Object key, InstanceCreator instanceCreator) {
		instantiationManager.register(key, instanceCreator);
	}
	
	/**
	 * Registers a type solver.
	 * @param typeSolver the type solver to register.
	 */
	public void register(TypeSolver typeSolver) {
		typeSolverManager.register(typeSolver);
	}
	
	/**
	 * Registers a type solver under a given key. 
	 * @param key the key under which the instance creator is registered.
	 * @param typeSolver the type solver to register.
	 */
	public void register(Object key, TypeSolver typeSolver) {
		typeSolverManager.register(key, typeSolver);
	}
	
}
