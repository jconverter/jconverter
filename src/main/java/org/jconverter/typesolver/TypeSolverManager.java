package org.jconverter.typesolver;

import java.lang.reflect.Type;

import org.jcategory.category.Key;


public abstract class TypeSolverManager {

	public void register(TypeSolver<?> typeSolver) {
		register(TypeSolverKey.DEFAULT_KEY, typeSolver);
	}
	
	public abstract void register(Key typeSolverKey, TypeSolver<?>  typeSolver);
	
/*	public Type inferType(Object object) {
		return inferType(DEFAULT_KEY, object);
	}*/
	
	public abstract Type inferType(Key typeSolverKey, Object object);

}
