package org.jconverter.converter;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jconverter.JConverter;
import org.jgum.strategy.ChainOfResponsibility;

import com.google.common.base.Function;

public class ConverterEvaluator<T,U> implements Function<Object, U> {

	private final T sourceObject;
	private final Type targetType;
	private final JConverter context;
	
	public ConverterEvaluator(T sourceObject, Type targetType, JConverter context) {
		this.sourceObject = sourceObject;
		this.targetType = targetType;
		this.context = context;
	}
	
	@Override
	public U apply(Object processingObject) {
		if(processingObject instanceof Converter) {
			return applyConverter((Converter)processingObject);
		} else if(processingObject instanceof ConverterRegister) {
			return applyChain((ConverterRegister)processingObject);
		} else
			throw new RuntimeException("Wrong processing object.");
	}

	public U applyConverter(Converter<T,U> processingObject) {
		try {
			return processingObject.apply(sourceObject, targetType, context);
		} catch(ClassCastException e) {
			throw e;
		}
		
	}
	
	public U applyChain(ConverterRegister processingObject) {
		List<Converter<T,U>> converters = (List)processingObject.orderedConverters(targetType);
		ChainOfResponsibility<Converter<T,U>,U> chain = new ChainOfResponsibility<>(converters, ConversionException.class);
		return chain.apply((Function)this);
	}

	
	static class NonRedundantConverterEvaluator<T,U> extends ConverterEvaluator<T,U> {

		private final Set<Converter<T, U>> visited;
		
		public NonRedundantConverterEvaluator(T sourceObject, Type targetType, JConverter context) {
			super(sourceObject, targetType, context);
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
