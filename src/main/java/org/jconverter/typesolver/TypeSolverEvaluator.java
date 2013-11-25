package org.jconverter.typesolver;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Function;

public class TypeSolverEvaluator<T> implements Function<Object, Type> {

	private final T sourceObject;
	
	public TypeSolverEvaluator(T sourceObject) {
		this.sourceObject = sourceObject;
	}
	
	@Override
	public Type apply(Object processingObject) {
		if(processingObject instanceof TypeSolver)
			return applyTypeSolver((TypeSolver)processingObject);
		else if(processingObject instanceof TypeSolverChain)
			return applyChain((TypeSolverChain)processingObject);
		else
			throw new RuntimeException("Wrong processing object.");
		
	}

	public Type applyTypeSolver(TypeSolver<T> typeSolver) {
		return typeSolver.getType(sourceObject);
	}
	
	public Type applyChain(TypeSolverChain<T> typeSolverChain) {
		return typeSolverChain.apply((Function)this);
	}
	
	
	static class NonRedundantTypeSolverEvaluator<V> extends TypeSolverEvaluator<V> {

		private final Set<TypeSolver<V>> visited;
		
		public NonRedundantTypeSolverEvaluator(V sourceObject) {
			super(sourceObject);
			visited = new HashSet<>();
		}
		
		@Override
		public Type applyTypeSolver(TypeSolver<V> processingObject) {
			if(visited.contains(processingObject))
				throw new UnrecognizedObjectException();
			else {
				visited.add(processingObject);
				return super.applyTypeSolver(processingObject);
			}
		}
		
	}
	
}
