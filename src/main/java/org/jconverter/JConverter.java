package org.jconverter;

import java.lang.reflect.Type;

import org.jconverter.converter.ConverterManager;
import org.jconverter.converter.ConverterManager.ConverterKey;
import org.jconverter.converter.JGumConverterManager;
import org.jconverter.instantiation.InstantiationManager;
import org.jconverter.instantiation.InstantiationManager.InstantiationKey;
import org.jconverter.instantiation.JGumInstantiationManager;
import org.jconverter.typesolver.JGumTypeSolverManager;
import org.jconverter.typesolver.TypeSolverManager;
import org.jconverter.typesolver.TypeSolverManager.TypeSolverKey;
import org.jgum.JGum;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;

public class JConverter {
	
	public static final Object DEFAULT_JCONVERTER_KEY = new Object();
	
	private final ConverterManager converterManager;
	private final InstantiationManager instantiationManager;
	private final TypeSolverManager typeSolverManager;
	protected Object defaultKey;
	
	public JConverter() {
		JGum jgum = new JGum();
		this.instantiationManager = JGumInstantiationManager.getDefault(jgum);
		this.typeSolverManager = new JGumTypeSolverManager(jgum);
		this.converterManager = JGumConverterManager.getDefault(jgum);
		defaultKey = DEFAULT_JCONVERTER_KEY;
	}
	
	public JConverter(ConverterManager converterManager, InstantiationManager instantiationManager, TypeSolverManager typeSolverManager) {
		this.converterManager = converterManager;
		this.instantiationManager = instantiationManager;
		this.typeSolverManager = typeSolverManager;
		defaultKey = DEFAULT_JCONVERTER_KEY;
	}
	
	public Object getDefaultKey() {
		return defaultKey;
	}
	
	public <T> T convert(Object source, Type targetType) {
		TypeWrapper targetTypeWrapper = TypeWrapper.wrap(targetType);
		Class targetClass = targetTypeWrapper.getRawClass();
		if(targetClass.isInstance(source)) {
			if(targetType.equals(targetClass) //the target type is a raw class (no type parameters).
					|| TypeWrapper.wrap(source.getClass()).as(targetType).equals(targetType)) //there are type parameters, and they are the same than the source object.
				return (T) source;
		}
		return converterManager.convert(new ConverterKey(getDefaultKey()), source, targetType, this);
	}
	
	public <T> T convert(Object key, Object source, Type targetType) {
		return new JConverterProxy(this, key).convert(source, targetType);
	}
	
	public <T> T instantiate(Type targetType) {
		try {
			return instantiationManager.instantiate(new InstantiationKey(getDefaultKey()), targetType);
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

	public <T> T instantiate(Object key, Type targetType) {
		return new JConverterProxy(this, key).instantiate(key, targetType);
	}
	
	public Type getType(Object object) {
		return typeSolverManager.getType(new TypeSolverKey(getDefaultKey()), object);
	}
	
	public Type getType(Object key, Object object) {
		return new JConverterProxy(this, key).getType(key, object);
	}
	
}
