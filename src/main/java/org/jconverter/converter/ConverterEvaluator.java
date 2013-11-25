package org.jconverter.converter;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgum.strategy.ChainOfResponsibility;

import com.google.common.base.Function;

public class ConverterEvaluator<T,U> implements Function<Object, U> {

	private final T sourceObject;
	private final Type targetType;
	
	public ConverterEvaluator(T sourceObject, Type targetType) {
		this.sourceObject = sourceObject;
		this.targetType = targetType;
	}
	
	@Override
	public U apply(Object processingObject) {
		if(processingObject instanceof Converter) {
			return applyConverter((Converter)processingObject);
		} else if(processingObject instanceof Function) {
			return applyChain((ConverterRegister)processingObject);
		} else
			throw new RuntimeException("Wrong processing object.");
	}

	public U applyConverter(Converter<T,U> processingObject) {
		return processingObject.apply(sourceObject);
	}
	
	public U applyChain(ConverterRegister processingObject) {
		List<Converter<T,U>> converters = (List)processingObject.orderedConverters(targetType);
		ChainOfResponsibility<Converter<T,U>,U> chain = new ChainOfResponsibility<>(converters, ConversionException.class);
		return chain.apply((Function)this);
	}

	
	static class NonRedundantConverterEvaluator<T,U> extends ConverterEvaluator<T,U> {

		private final Set<Converter<T, U>> visited;
		
		public NonRedundantConverterEvaluator(T sourceObject, Type targetType) {
			super(sourceObject, targetType);
			visited = new HashSet<>();
		}
		
		@Override
		public U applyConverter(Converter<T,U> processingObject) {
			if(visited.contains(processingObject))
				throw new ConversionException();
			else {
				visited.add(processingObject);
				return super.applyConverter(processingObject);
			}
		}
		
	}
}
