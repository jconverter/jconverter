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
				ContextedConverter contextedConverter = new ContextedConverter(typedConverter, i, jgum, targetType);
				contextedConverters.add(contextedConverter);
			}
		}
		List<Converter<?,?>> converters = new ArrayList<>();
		for(ContextedConverter contextedConverter : contextedConverters) {
			converters.add(contextedConverter.getConverter());
		}
		return converters;
	}
	

	
	private class ContextedConverter implements Comparable<ContextedConverter> {

		private final Type targetType;
		private final TypedConverter typedConverter;
		private final int distanceToTarget;
		private final int index;
		
		/**
		 * The constructor assumes that the processed converter is compatible with the target type. No further verifications are accomplished.
		 * @param targetType the target conversion type.
		 * @param typedConverter a processed coverter.
		 * @param jgum a jgum context.
		 */
		public ContextedConverter(TypedConverter typedConverter, int index, JGum jgum, Type targetType) {
			this.targetType = targetType;
			this.typedConverter = typedConverter;
			if(typedConverter.hasVariableReturnType()) //the converter has different target types (quantified with upper bounds).
				distanceToTarget = 0; //assuming the target type is compatible with the typedConverter, the converter return type can be the current target type.
			else {
				Class targetClass = TypeWrapper.wrap(targetType).getRawClass();
				distanceToTarget = jgum.forClass(typedConverter.getReturnClass()).distance(targetClass);
			}
			this.index = index;
		}

		public Converter getConverter() {
			return typedConverter;
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

