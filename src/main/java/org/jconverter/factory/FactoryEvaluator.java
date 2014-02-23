package org.jconverter.factory;

import java.lang.reflect.Type;

import com.google.common.base.Function;

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
