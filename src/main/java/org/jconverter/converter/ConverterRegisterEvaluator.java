package org.jconverter.converter;

import java.lang.reflect.Type;
import java.util.List;

import org.jgum.strategy.ChainOfResponsibility;

import com.google.common.base.Function;

public class ConverterRegisterEvaluator<T,U> implements Function<Object, U> {
	
	private final Function<Converter<T,U>, U> converterEvaluator;
	private final Type targetType;
	
	public ConverterRegisterEvaluator(Function<Converter<T,U>, U> converterEvaluator, Type targetType) {
		this.converterEvaluator = converterEvaluator;
		this.targetType = targetType;
	}
	
	@Override
	public U apply(Object processingObject) {
		if(processingObject instanceof Converter) {
			return converterEvaluator.apply((Converter)processingObject);
		} else if(processingObject instanceof ConverterRegister) {
			return applyChain((ConverterRegister)processingObject);
		} else
			throw new RuntimeException("Wrong processing object.");
	}


	public U applyChain(ConverterRegister processingObject) {
		List<Converter<T,U>> converters = (List)processingObject.orderedConverters(targetType);
		ChainOfResponsibility<Converter<T,U>,U> chain = new ChainOfResponsibility<>(converters, ConversionException.class);
		return chain.apply((Function)this);
	}

	
	static class NonRedundantConverterEvaluator<T,V> extends NonRedundantEvaluator<Converter<T,V>,V> {
		
		public NonRedundantConverterEvaluator(Function<Converter<T,V>,V> evaluator) {
			super(evaluator);
		}
		
		@Override
		protected V onAlreadyVisited(Converter<T,V> alreadyVisited) {
			throw new ConversionException();
		}
	}
	
}
