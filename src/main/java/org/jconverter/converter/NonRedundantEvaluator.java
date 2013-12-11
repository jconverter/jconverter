package org.jconverter.converter;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Function;

public abstract class NonRedundantEvaluator<T,V> implements Function<T, V> {

	private final Set<T> visited;
	private final Function<T,V> evaluator;
	
	public NonRedundantEvaluator(Function<T,V> evaluator) {
		this.evaluator = evaluator;
		visited = new HashSet<>();
	}
	
	@Override
	public V apply(T evaluated) {
		if(visited.contains(evaluated))
			return onAlreadyVisited(evaluated);
		else {
			visited.add(evaluated);
			return evaluator.apply(evaluated);
		}
	}
	
	protected abstract V onAlreadyVisited(T alreadyVisited);
	
}