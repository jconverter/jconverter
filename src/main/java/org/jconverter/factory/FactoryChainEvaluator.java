package org.jconverter.factory;

import com.google.common.base.Function;

public class FactoryChainEvaluator<T> implements Function<Object, T> {

	private final Function<Factory<T>, T> factoryEvaluator;
	
	public FactoryChainEvaluator(Function<Factory<T>, T> factoryEvaluator) {
		this.factoryEvaluator = factoryEvaluator;
	}
	
	@Override
	public T apply(Object processingObject) {
		if(processingObject instanceof Factory)
			return factoryEvaluator.apply((Factory<T>) processingObject);
		else if(processingObject instanceof FactoryChain)
			return applyChain((FactoryChain<T>)processingObject);
		else
			throw new RuntimeException("Wrong processing object.");
		
	}
	
	public T applyChain(FactoryChain<T> factoryChain) {
		return (T)factoryChain.apply((Function)this);
	}

}
