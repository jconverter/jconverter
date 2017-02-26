package org.jconverter.factory;

import java.util.List;
import java.util.function.Function;

import org.jcategory.ChainOfResponsibilityExhaustedException;
import org.jcategory.strategy.ChainOfResponsibility;

public class FactoryChain<T> extends ChainOfResponsibility<Factory<T>, T> {

	public FactoryChain(List<Factory<T>> responsibilityChain) {
		super(responsibilityChain, FactoryException.class);
	}

	@Override
	public T apply(Function<Factory<T>, T> evaluator) {
		try {
			return super.apply(evaluator);
		} catch(ChainOfResponsibilityExhaustedException e) {
			throw new FactoryException(e);
		}
	}

}
