package org.jconverter.converter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.jgum.JGum;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;

public class ConverterRegister {

	private final static Logger logger = Logger.getLogger(ConverterRegister.class);
	
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
		TreeSet<ContextedConverter> contextedConverters = new TreeSet<>(); //TreeSet will keep the natural ordering of its members.
		for(int i = 0; i<typedConverters.size(); i++) {
			TypedConverter typedConverter = typedConverters.get(i);
			if(typedConverter.isReturnTypeCompatible(targetType)) {
				ContextedConverter contextedConverter = new ContextedConverter(typedConverter, i, targetType);
				contextedConverters.add(contextedConverter);
			}
		}
		List<Converter<?,?>> converters = new ArrayList<>();
		for(ContextedConverter contextedConverter : contextedConverters) {
			converters.add(contextedConverter.typedConverter);
		}
		return converters;
	}
	

	
	private class ContextedConverter implements Comparable<ContextedConverter> {

		private final TypedConverter typedConverter;
		private final int index;
		private final Type targetType;
		private final int distanceToTarget;
		
		
		/**
		 * The constructor assumes that the processed converter is compatible with the target type. No further verifications are accomplished.
		 * @param typedConverter a processed converter.
		 * @param index the index of the converter in the converter register.
		 * @param targetType the target conversion type.

		 */
		public ContextedConverter(TypedConverter typedConverter, int index, Type targetType) {
			this.typedConverter = typedConverter;
			this.index = index;
			this.targetType = targetType;
			if(typedConverter.hasVariableReturnType()) //the converter has different target types (quantified with upper bounds).
				distanceToTarget = 0; //assuming the target type is compatible with the typedConverter, the converter return type can be the current target type.
			else {
				Class targetClass = TypeWrapper.wrap(targetType).getRawClass();
				distanceToTarget = jgum.forClass(typedConverter.getReturnClass()).distance(targetClass);
			}
		}
		
		@Override
		public int compareTo(ContextedConverter contextedConverter) {
			if(distanceToTarget != contextedConverter.distanceToTarget)
				return distanceToTarget - contextedConverter.distanceToTarget;
			else
				return index - contextedConverter.index;
		}
		
	}
	
}

