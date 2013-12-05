package org.jconverter.typesolver;

import java.lang.reflect.Type;


public abstract class TypeSolverManager {
	
	public static final Object DEFAULT_KEY = new Object();
	
	public void register(TypeSolver typeSolver) {
		register(DEFAULT_KEY, typeSolver);
	}
	
	public abstract void register(Object typeSolverKey, final TypeSolver typeSolver);
	
	public Type getType(Object object) {
		return getType(DEFAULT_KEY, object);
	}
	
	public abstract Type getType(Object typeSolverKey, Object object);

}
