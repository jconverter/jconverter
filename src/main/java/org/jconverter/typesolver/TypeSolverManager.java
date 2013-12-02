package org.jconverter.typesolver;

import java.lang.reflect.Type;

import org.jconverter.JConverter;
import org.jgum.JGum;


public abstract class TypeSolverManager {
	
	/**
	 * @param jgum a JGum categorization context.
	 * @return the default type solver manager.
	 */
	public static TypeSolverManager getDefault(JGum jgum) {
		return new JGumTypeSolverManager(jgum);
	}
	
	public void register(TypeSolver typeSolver) {
		register(JConverter.DEFAULT_JCONVERTER_KEY, typeSolver);
	}
	
	public abstract void register(Object typeSolverKey, final TypeSolver typeSolver);
	
	public Type getType(Object object) {
		return getType(JConverter.DEFAULT_JCONVERTER_KEY, object);
	}
	
	public abstract Type getType(Object typeSolverKey, Object object);

}
