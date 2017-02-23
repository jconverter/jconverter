package org.jconverter.converter;

import java.util.List;
import java.util.function.Function;

public class ConverterRegisterEvaluator<T,U> implements Function<Object, U> {
	
	private final ConverterEvaluator<T,U> converterEvaluator;
	
	public ConverterRegisterEvaluator(ConverterEvaluator<T,U> converterEvaluator) {
		this.converterEvaluator = converterEvaluator;
	}
	
	@Override
	public U apply(Object processingObject) {
		if (processingObject instanceof Converter) {
			return (U) converterEvaluator.apply((Converter) processingObject);
		} else if (processingObject instanceof ConverterRegister) {
			return applyChain((ConverterRegister) processingObject);
		} else {
			throw new RuntimeException("Wrong processing object.");
		}
	}


	public U applyChain(ConverterRegister processingObject) {
		List<Converter<T,U>> converters = (List) processingObject.orderedConverters(
				converterEvaluator.getConversionGoal().getTarget());
		ConverterChain<Converter<T,U>,U> chain =
				new ConverterChain<>(converterEvaluator.getConversionGoal(), converters);
		return (U) chain.apply((Function) this);
	}
	
}
