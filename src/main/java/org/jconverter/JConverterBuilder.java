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

	private final ConverterManager converterManager;
	private final InstantiationManager instantiationManager;
	private final TypeSolverManager typeSolverManager;
	
	public static JConverterBuilder create() {
		return new JConverterBuilder();
	}
	
	private JConverterBuilder() {
		JGum jgum = new JGum();
		this.instantiationManager = JGumInstantiationManager.getDefault(jgum);
		this.typeSolverManager = new JGumTypeSolverManager(jgum);
		this.converterManager = JGumConverterManager.getDefault(jgum);
	}
	
	public JConverter build() {
		return new JConverter(converterManager, instantiationManager, typeSolverManager);
	}
	
	public void register(Converter converter) {
		converterManager.register(converter);
	}
	
	public void register(Object converterKey, Converter converter) {
		converterManager.register(converterKey, converter);
	}

	public void register(InstanceCreator instanceCreator) {
		instantiationManager.register(instanceCreator);
	}
	
	public void register(Object converterKey, InstanceCreator instanceCreator) {
		instantiationManager.register(converterKey, instanceCreator);
	}
	
	public void register(TypeSolver typeSolver) {
		typeSolverManager.register(typeSolver);
	}
	
	public void register(Object converterKey, TypeSolver typeSolver) {
		typeSolverManager.register(converterKey, typeSolver);
	}
	
}
