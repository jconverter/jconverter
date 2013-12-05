package org.jconverter;

import java.lang.reflect.Type;

import org.jconverter.converter.ConverterManager;
import org.jconverter.converter.JGumConverterManager;
import org.jconverter.instantiation.InstantiationManager;
import org.jconverter.instantiation.JGumInstantiationManager;
import org.jconverter.typesolver.JGumTypeSolverManager;
import org.jconverter.typesolver.TypeSolverManager;
import org.jconverter.typesolver.UnrecognizedObjectException;
import org.jgum.JGum;
import org.minitoolbox.reflection.IncompatibleTypesException;
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
	
	private final ConverterManager converterManager; //responsible of converting objects.
	private final InstantiationManager instantiationManager; //responsible of instantiating objects.
	private final TypeSolverManager typeSolverManager; //responsible of recommending types for the result of a conversion.
	
	public JConverter() {
		JGum jgum = new JGum();
		this.converterManager = JGumConverterManager.createDefault(jgum);
		this.instantiationManager = JGumInstantiationManager.createDefault(jgum);
		this.typeSolverManager = JGumTypeSolverManager.createDefault(jgum);
	}
	
	/**
	 * 
	 * @param converterManager a converter manager responsible of converting objects.
	 * @param instantiationManager an instance creator manager responsible of instantiating objects.
	 * @param typeSolverManager a type solver manager responsible of recommending types for the result of a conversion.
	 */
	public JConverter(ConverterManager converterManager, InstantiationManager instantiationManager, TypeSolverManager typeSolverManager) {
		this.converterManager = converterManager;
		this.instantiationManager = instantiationManager;
		this.typeSolverManager = typeSolverManager;
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
	 * @param key constrains the registered converters, instance creators, and type solvers that will be looked up in this operation.
	 * @param source the object to convert.
	 * @param targetType the desired type.
	 * @return the conversion of the source object to the desired target type.
	 */
	public <T> T convert(Object key, Object source, Type targetType) {
		TypeWrapper targetTypeWrapper = TypeWrapper.wrap(targetType);
		Class targetClass = targetTypeWrapper.getRawClass();
		
		if(targetClass.isInstance(source)) {
			if(!targetTypeWrapper.hasActualTypeArguments()) //the target type does not have actual type arguments.
				return (T) source;
			else {
				Type asTargetType = TypeWrapper.wrap(source.getClass()).asType(targetClass);
				if(asTargetType.equals(targetType)) //there are type parameters, and they are the same than the source object.
					return (T) source;
			}
		}
		
		Type inferredType = null;
		try {
			inferredType = typeSolverManager.getType(source);
		} catch(UnrecognizedObjectException e) {}
		if(inferredType != null) {
			try {
				targetType = targetTypeWrapper.mostSpecificType(inferredType);
			} catch(IncompatibleTypesException e) {}
		}
		return converterManager.convert(key, source, targetType, this);
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
		} catch(Exception e) {
			TypeWrapper targetTypeWrapper = TypeWrapper.wrap(targetType);
			Class targetClass = targetTypeWrapper.getRawClass();
			try {
				return (T) targetClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e1) {
				throw new RuntimeException(e1);
			}
		}
	}
	
	/**
	 * 
	 * @param object the object which conversion target type to recommend.
	 * @return the recommended type.
	 */
	public Type getType(Object object) {
		return getType(TypeSolverManager.DEFAULT_KEY, object);
	}
	
	/**
	 * 
	 * @param key constrains the type solvers that will be looked up in this operation.
	 * @param object the object which conversion target type to recommend.
	 * @return the recommended type.
	 */
	public Type getType(Object key, Object object) {
		return typeSolverManager.getType(TypeSolverManager.DEFAULT_KEY, object);
	}
	
}
