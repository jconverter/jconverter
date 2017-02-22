package org.jconverter.factory;

import java.lang.reflect.Type;

import java.util.function.Function;

public class FactoryEvaluator<T> implements Function<Factory<T>, T> {

	private final Type type;

	public FactoryEvaluator(Type type) {
		this.type = type;
	}

	@Override
	public T apply(Factory<T> factory) {
		return factory.instantiate(type);
	}
	
}
