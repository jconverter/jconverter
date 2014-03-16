package org.jconverter.factory;

import java.util.List;

import org.jgum.strategy.ChainOfResponsibility;

public class FactoryChain<T> extends ChainOfResponsibility<Factory<T>, T> {

	public FactoryChain() {
		super(FactoryException.class);
	}
	
	public FactoryChain(List<Factory<T>> responsibilityChain) {
		super(responsibilityChain, FactoryException.class);
	}
	
}
