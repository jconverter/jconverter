package org.jconverter.typesolver;

import java.lang.reflect.Type;

import org.jconverter.JConverter;
import org.jgum.category.Key;


public interface TypeSolverManager {
	
	public static class TypeSolverKey extends Key {
		public TypeSolverKey(Object key) {
			super(JConverter.DEFAULT_JCONVERTER_KEY);
		}
	}
	
	public static final Object DEFAULT_KEY = new TypeSolverKey(JConverter.DEFAULT_JCONVERTER_KEY);
	
	public abstract void register(Object typeSolverKey, final TypeSolver typeSolver);
	
	public abstract Type getType(Object typeSolverKey, Object object);

}
