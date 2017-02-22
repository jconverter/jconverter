package org.jconverter.converter;

import static org.jconverter.converter.TypeDomain.typeDomain;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.jgum.JGum;
import org.typetools.typewrapper.TypeWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConverterRegister {

	private final static Logger logger = LoggerFactory.getLogger(ConverterRegister.class);
	
	private final List<TypedConverter<?,?>> typedConverters;
	private final JGum jgum;
	
	public ConverterRegister(JGum jgum) {
		typedConverters = new ArrayList<>();
		this.jgum = jgum;
	}
	
	public void addFirst(TypedConverter<?,?> converter) {
		typedConverters.add(0, converter);
	}
	
	public List<Converter<?,?>> orderedConverters(Type targetType) {
		TreeSet<ComparableConverter> comparableConverters = new TreeSet<>(); //TreeSet will keep the natural ordering of its members.
		for (int i = 0; i<typedConverters.size(); i++) {
			TypedConverter<?,?> typedConverter = typedConverters.get(i);
			if (typedConverter.getConversionDomains().getTarget().isSubsetOf(typeDomain(targetType))) {
				ComparableConverter comparableConverter = new ComparableConverter(typedConverter, i, targetType);
				comparableConverters.add(comparableConverter);
			}
		}
		List<Converter<?,?>> converters = new ArrayList<>();
		for(ComparableConverter contextedConverter : comparableConverters) {
			converters.add(contextedConverter.typedConverter);
		}
		return converters;
	}
	

	
	private class ComparableConverter implements Comparable<ComparableConverter> {

		private final TypedConverter<?,?> typedConverter;
		private final int index;
		private final int distanceToTarget;
		
		
		/**
		 * The constructor assumes that the processed converter is compatible with the target type. No further verifications are accomplished.
		 * @param typedConverter a processed converter.
		 * @param index the index of the converter in the converter register.
		 * @param targetType the target conversion type.

		 */
		public ComparableConverter(TypedConverter<?,?> typedConverter, int index, Type targetType) {
			this.typedConverter = typedConverter;
			this.index = index;
			if (typedConverter.getConversionDomains().getTarget().isVariableType()) {//the converter has different target types (quantified with upper bounds).
				distanceToTarget = 0; //assuming the target type is compatible with the typedConverter, the converter return type can be the current target type.
			} else {
				Class<?> targetClass = TypeWrapper.wrap(targetType).getRawClass();
				distanceToTarget = jgum.forClass(typedConverter.getConversionDomains().getTarget().getRawClass()).distance(targetClass);
			}
		}
		
		@Override
		public int compareTo(ComparableConverter contextedConverter) {
			if (distanceToTarget != contextedConverter.distanceToTarget) {
				return distanceToTarget - contextedConverter.distanceToTarget;
			} else {
				return index - contextedConverter.index;
			}

		}
		
	}
	
}

