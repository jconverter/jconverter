package org.jconverter.converter;

import static java.util.Arrays.asList;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jcategory.JCategory;
import org.jcategory.category.CategorizationListener;
import org.jcategory.category.Key;
import org.jcategory.category.type.TypeCategory;
import org.jconverter.JConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typeutils.TypeUtils;
import org.typeutils.typewrapper.ArrayTypeWrapper;
import org.typeutils.typewrapper.TypeWrapper;
import org.typeutils.typewrapper.VariableTypeWrapper;

public class InterTypeConverterManager extends ConverterManager {

	private static final Logger logger = LoggerFactory.getLogger(InterTypeConverterManager.class);
	
	/**
	 * @param categorization a JCategory categorization context.
	 * @return an instance of ConverterManager configured with default converters.
	 */
	public static InterTypeConverterManager createDefault(JCategory categorization) {
		InterTypeConverterManager converterManager = new InterTypeConverterManager(categorization);
		registerDefaults(converterManager);
		return converterManager;
	}
	
	protected final JCategory categorization;
	
	public InterTypeConverterManager(JCategory categorization) {
		this.categorization = categorization;
	}
	
	private List<TypeCategory<?>> getMatchingCategories(TypeWrapper wrappedType) {
		if (wrappedType instanceof VariableTypeWrapper) {
			VariableTypeWrapper variableTypeWrapper = (VariableTypeWrapper) wrappedType;
			List<Type> upperBoundariesTypes = asList(variableTypeWrapper.getUpperBounds());
			List<Class<?>> upperBoundariesClasses = TypeUtils.asRawClasses(upperBoundariesTypes);
			List<TypeCategory<?>> boundTypeCategories = categorization.getTypeCategorization().findBoundedTypes(upperBoundariesClasses);
			return boundTypeCategories;
		} else if (wrappedType instanceof ArrayTypeWrapper) {
			List<TypeCategory<?>> compatibleCategories = new ArrayList<>();
			List<TypeCategory<?>> objectChildren = categorization.forClass(Object.class).getChildren();
			for (TypeCategory<?> objectChild : objectChildren) {
				if (wrappedType.isWeakAssignableFrom(objectChild.getLabel())) {
					compatibleCategories.add(objectChild);
				}
			}
			return compatibleCategories;
		} else {
			throw new RuntimeException(); //this should not happen
		}
	}


	private ConverterRegister addToConverterRegister(Key key, TypeCategory<?> typeCategory, ConversionFunction<?,?> conversionFunction) {
		ConverterRegister chain;
		List<ConverterRegister> chains = typeCategory.getLocalProperty(key);
		if (chains.isEmpty()) {
			chain = new ConverterRegister(categorization);
			typeCategory.setProperty(key, chain);
		} else {
			chain = chains.get(0);
		}
		chain.addFirst(conversionFunction);
		return chain;
	}
	
	@Override
	public void register(Key key, Converter<?,?> converter) {
		ConversionFunction<?,?> conversionFunction = ConversionFunction.forConverter(converter);

		Type sourceType = conversionFunction.getDomain().getType();
		TypeWrapper sourceTypeWrapper = TypeWrapper.wrap(sourceType);
		if (!sourceTypeWrapper.isVariable()) {
			TypeCategory<?> sourceTypeCategory = categorization.forClass(sourceTypeWrapper.getRawClass());
			addToConverterRegister(key, sourceTypeCategory, conversionFunction);
		} else { //the type argument is a variable type.
			List<TypeCategory<?>> matchedCategories = getMatchingCategories(sourceTypeWrapper);
			for (TypeCategory<?> matchedCategory : matchedCategories) {
				addToConverterRegister(key, matchedCategory, conversionFunction); //set the converter for all the known types that are in the boundaries.
			}
			categorization.getTypeCategorization().addCategorizationListener(new CategorizationListener<TypeCategory<?>>() { //set the converter for future known types that are in the boundaries.
				@Override
				public void onCategorization(TypeCategory<?> category) {
					//if (category.isInBoundaries(upperBoundariesClasses))
					if (sourceTypeWrapper.isWeakAssignableFrom(category.getLabel())) {
						addToConverterRegister(key, category, conversionFunction);
					}
				}
			});
		}
	}
	
	@Override
	public <T> T convert(Key key, Object source, TypeDomain target, JConverter context) {
		return convert(new ConverterImpl<>(categorization, key), source, target, context);
	}		
	
	protected <T> T convert(Converter converter, Object source, TypeDomain target, JConverter context) {
		try {
			return (T) converter.apply(source, target, context);
		} catch(NotSuitableConverterException e) {
			TypeWrapper sourceWrappedType = TypeWrapper.wrap(source.getClass());
			//if the source object is an array of primitives
			if (sourceWrappedType instanceof ArrayTypeWrapper &&
					sourceWrappedType.getBaseType() instanceof Class && ((Class) sourceWrappedType.getBaseType()).isPrimitive()) {
				//return convert(converter, Iterators.forArray(source), targetType, context);
				return convert(converter, new ArrayIterator(source), target, context);
			}
			throw e;
		}
	}


	private static class ArrayIterator<T> implements Iterator<T> {
		private final Object array;
		private int index;
		private final int length;

		public ArrayIterator(Object array) {
			if (!array.getClass().isArray()) {
				throw new RuntimeException("Not an array: " + array + ".");
			}
			this.array = array;
			length = Array.getLength(array);
		}

		@Override
		public synchronized boolean hasNext() {
			return index < length;
		}

		@Override
		public synchronized T next() {
			if (index == length) {
				throw new NoSuchElementException();
			}
			return (T) Array.get(array, index++);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}


	}

}
