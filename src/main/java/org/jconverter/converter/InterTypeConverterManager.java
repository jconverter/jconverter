package org.jconverter.converter;

import static java.util.Arrays.asList;
import static org.jconverter.converter.TypeDomain.typeDomain;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.jconverter.JConverter;
import org.typetools.TypeUtil;
import org.typetools.typewrapper.ArrayTypeWrapper;
import org.typetools.typewrapper.TypeWrapper;
import org.typetools.typewrapper.VariableTypeWrapper;
import org.jgum.JGum;
import org.jgum.category.CategorizationListener;
import org.jgum.category.type.TypeCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.Primitives;

public class InterTypeConverterManager extends ConverterManager {

	private static final Logger logger = LoggerFactory.getLogger(InterTypeConverterManager.class);
	
	/**
	 * @param categorization a JGum categorization context.
	 * @return an instance of JGumConverterManager configured with default converters.
	 */
	public static InterTypeConverterManager createDefault(JGum categorization) {
		InterTypeConverterManager converterManager = new InterTypeConverterManager(categorization);
		registerDefaults(converterManager);
		return converterManager;
	}
	
	protected final JGum categorization;
	
	public InterTypeConverterManager(JGum categorization) {
		this.categorization = categorization;
	}
	
	private List<TypeCategory<?>> getMatchingCategories(TypeWrapper wrappedType) {
		if (wrappedType instanceof VariableTypeWrapper) {
			VariableTypeWrapper variableTypeWrapper = (VariableTypeWrapper) wrappedType;
			List<Type> upperBoundariesTypes = asList(variableTypeWrapper.getUpperBounds());
			List<Class<?>> upperBoundariesClasses = TypeUtil.asRawClasses(upperBoundariesTypes);
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


	private ConverterRegister getOrCreateConverterRegister(Object key, TypeCategory<?> typeCategory) {
		Optional<ConverterRegister> chainOpt = typeCategory.getLocalProperty(key);
		ConverterRegister chain;
		if (chainOpt.isPresent()) {
			chain =  chainOpt.get();
		} else {
			chain = new ConverterRegister(categorization);
			typeCategory.setProperty(key, chain);
		}
		return chain;
	}
	
	@Override
	public void register(Object key, Converter<?,?> converter) {
		TypedConverter<?,?> typedConverter = TypedConverter.forConverter(converter);

		Type sourceType = typedConverter.getConversionDomains().getSource().getType();
		TypeWrapper sourceTypeWrapper = TypeWrapper.wrap(sourceType);
		if (!sourceTypeWrapper.isVariable()) {
			TypeCategory<?> sourceTypeCategory = categorization.forClass(sourceTypeWrapper.getRawClass());
			getOrCreateConverterRegister(key, sourceTypeCategory).addFirst(typedConverter);
		} else { //the type argument is a variable type.
			List<TypeCategory<?>> matchedCategories = getMatchingCategories(sourceTypeWrapper);
			for (TypeCategory<?> matchedCategory : matchedCategories) {
				getOrCreateConverterRegister(key, matchedCategory).addFirst(typedConverter); //set the type solver for all the known types that are in the boundaries.
			}
			categorization.getTypeCategorization().addCategorizationListener(new CategorizationListener<TypeCategory<?>>() { //set the type solver for future known types that are in the boundaries.
				@Override
				public void onCategorization(TypeCategory<?> category) {
					//if (category.isInBoundaries(upperBoundariesClasses))
					if (sourceTypeWrapper.isWeakAssignableFrom(category.getLabel())) {
						getOrCreateConverterRegister(key, category).addFirst(typedConverter);
					}
				}
			});
		}
	}
	
	@Override
	public <T> T convert(Object key, Object source, TypeDomain target, JConverter context) {
		Converter jGumConverter = new ConverterImpl(categorization, key);
		return convert(jGumConverter, source, target, context);
	}		
	
	protected <T> T convert(Converter converter, Object source, TypeDomain target, JConverter context) {
		try {
			return (T) converter.apply(source, target, context);
		} catch(NotSuitableConverterException e) {
			if (TypeWrapper.wrap(target.getType()).isPrimitive()) {
				return convert(converter, source, typeDomain(Primitives.wrap(target.getRawClass())), context); //inboxing of the target type
			}
			TypeWrapper sourceWrappedType = TypeWrapper.wrap(source.getClass());
			//if the source object is an array of primitives
			if (sourceWrappedType instanceof ArrayTypeWrapper &&
					sourceWrappedType.getBaseType() instanceof Class && ((Class) sourceWrappedType.getBaseType()).isPrimitive()) {
				//return convert(jGumConverter, Iterators.forArray(source), targetType, context);
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
