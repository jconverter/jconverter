package org.jconverter.converter;

import java.lang.reflect.Type;

import org.jconverter.util.typewrapper.TypeWrapper;
import org.jconverter.util.typewrapper.VariableTypeWrapper;

/**
 * A converter that explicitly specifies its source and target types.
 * @author sergioc
 *
 * @param <T> the source type of the converter.
 * @param <U> the target type of the converter.
 */
public abstract class TypedConverter<T,U> implements Converter<T,U> {

	public static <T,U> TypedConverter<T,U> forConverter(Converter<T,U> converter) {
		if(converter instanceof TypedConverter)
			return (TypedConverter<T, U>) converter;
		else
			return TypedConverterProxy.forConverter(converter);
	}
	
	private final Type sourceType;
	private final Type returnType;
	private final Class<?> returnClass;
	
	public TypedConverter(Type sourceType, Type returnType) {
		this.sourceType = sourceType;
		this.returnType = returnType;
		
		TypeWrapper targetTypeWrapper = TypeWrapper.wrap(returnType);
		if(!(targetTypeWrapper instanceof VariableTypeWrapper)) {
			returnClass = targetTypeWrapper.getRawClass();
		} else {
			returnClass = null;
		}
			
	}
	
	
	public Type getSourceType() {
		return sourceType;
	}
	
	public Type getReturnType() {
		return returnType;
	}
	
	public boolean hasVariableReturnType() {
		return returnClass == null;
	}

	public Class<?> getReturnClass() {
		return returnClass;
	}
	
	public boolean isSourceTypeCompatible(Type type) {
		//TODO this may be a bit inaccurate in certain cases, to improve.
		return TypeWrapper.wrap(sourceType).isWeakAssignableFrom(type); 
	}
	
	public boolean isReturnTypeCompatible(Type type) {
		return TypeWrapper.wrap(type).isWeakAssignableFrom(returnType);
	}
	
}
