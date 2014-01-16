package org.jconverter.converter;

import java.lang.reflect.Type;
import java.util.List;

import org.jconverter.JConverter;
import org.jconverter.converter.ConverterRegisterEvaluator.NonRedundantConverterEvaluator;
import org.jgum.JGum;
import org.jgum.category.Category;
import org.jgum.strategy.ChainOfResponsibility;

public class JGumConverter<T,U> implements Converter<T,U> {

	protected final JGum jgum;
	protected final Object key;
	
	public JGumConverter(JGum jgum) {
		this(jgum, ConverterManager.DEFAULT_KEY);
	}
	
	public JGumConverter(JGum jgum, Object key) {
		this.jgum = jgum;
		this.key = key;
	}
	
	@Override
	public U apply(T source, Type targetType, JConverter context) {
		List<ConverterRegister> converterRegisters = getConverters(source.getClass());
		return evalConverters(converterRegisters, source, targetType, context);
	}
	
	protected U evalConverters(List<ConverterRegister> converterRegisters, Object source, Type targetType, JConverter context) {
		ChainOfResponsibility chain = new ChainOfResponsibility(converterRegisters, ConversionException.class);
		ConverterEvaluator converterEvaluator = new ConverterEvaluator(source, targetType, context);
		ConverterRegisterEvaluator evaluator = new ConverterRegisterEvaluator(new NonRedundantConverterEvaluator(converterEvaluator), targetType);
		return (U) chain.apply(evaluator);
	}
	
	protected List<ConverterRegister> getConverters(Class<?> clazz) {
		Category sourceTypeCategory = jgum.forClass(clazz);
		List<ConverterRegister> converterRegisters = sourceTypeCategory.<ConverterRegister>bottomUpProperties(key);
		return converterRegisters;
	}
	
}