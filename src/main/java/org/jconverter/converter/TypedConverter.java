package org.jconverter.converter;

import java.lang.reflect.Type;

import org.minitoolbox.reflection.typewrapper.TypeWrapper;
import org.minitoolbox.reflection.typewrapper.VariableTypeWrapper;

/**
 * A converter that explicitly specifies its source and target types.
 * @author sergioc
 *
 * @param <T> the source type of the converter.
 * @param <U> the target type of the converter.
 */
public abstract class TypedConverter<T,U> implements Converter<T,U> {

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
	
	public boolean isReturnTypeCompatible(Type type) {
		return TypeWrapper.wrap(type).isWeakAssignableFrom(returnType);
	}
	
}
