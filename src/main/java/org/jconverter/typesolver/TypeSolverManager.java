package org.jconverter.typesolver;

import java.lang.reflect.Type;

import org.jconverter.JConverter;
import org.jgum.category.Key;


public abstract class TypeSolverManager {
	
	public static class TypeSolverKey extends Key {
		public TypeSolverKey(Object key) {
			super(JConverter.DEFAULT_JCONVERTER_KEY);
		}
	}
	
	public static final Object DEFAULT_KEY = new TypeSolverKey(JConverter.DEFAULT_JCONVERTER_KEY);
	
	public void register(TypeSolver typeSolver) {
		register(DEFAULT_KEY, typeSolver);
	}
	
	public abstract void register(Object typeSolverKey, final TypeSolver typeSolver);
	
	public Type getType(Object object) {
		return getType(DEFAULT_KEY, object);
	}
	
	public abstract Type getType(Object typeSolverKey, Object object);

}
