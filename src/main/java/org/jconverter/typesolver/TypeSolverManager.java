package org.jconverter.typesolver;

import java.lang.reflect.Type;


public interface TypeSolverManager {

	public void register(Object typeSolverKey, final TypeSolver typeSolver);
	
	public Type getType(Object typeSolverKey, Object object);

}
