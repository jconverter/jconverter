package org.typetools.typewrapper;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.typetools.IncompatibleTypesException;


public class VariableTypeWrapper extends TypeWrapper {

	private static final long serialVersionUID = 1L;
	
	public VariableTypeWrapper(Type wrappedType) {
		super(wrappedType);
	}

	@Override
	public boolean isRawType() {
		return false;
	}
	
	@Override
	public boolean hasTypeParameters() {
		//if(true) throw new UnsupportedOperationException();
		return false;
	}
	
	@Override
	public TypeVariable[] getTypeParameters() {
		//if(true) throw new UnsupportedOperationException();
		return new TypeVariable[] {};
	}

	@Override
	public Type[] getActualTypeArguments() {
		//if(true) throw new UnsupportedOperationException();
		return new Type[] {};
	}
	
	@Override
	public boolean hasActualTypeArguments() {
		//if(true) throw new UnsupportedOperationException();
		return false;
	}
	
	public GenericDeclaration getGenericDeclaration() {
		GenericDeclaration genericDeclaration = null;
		if(!isWildcard())
			genericDeclaration = ((TypeVariable)wrappedType).getGenericDeclaration();
		return genericDeclaration;
	}
	
	/**
	 * Answers if a Variable type has a default upper bound (Object.class).
	 * @return
	 */
	public boolean hasDefaultUpperBounds() {
		for(Type type : getUpperBounds()) {
			if(!type.equals(Object.class))
				return false;
		}
		return true;
	}
	
	public Type[] getUpperBounds() {
		if(isWildcard())
			return ((WildcardType)wrappedType).getUpperBounds();
		else
			return ((TypeVariable)wrappedType).getBounds();
	}

	public Type[] getLowerBounds() {
		if(isWildcard())
			return ((WildcardType)wrappedType).getLowerBounds();
		else
			return new Type[]{};
	}
	
	private boolean isCompatibleWithUpperBounds(TypeWrapper typeWrapper) {
		for(Type uppderBoundType : getUpperBounds()) {
			if(!wrap(uppderBoundType).isWeakAssignableFrom(typeWrapper))
				return false;
		}
		return true;
	}
	
	private boolean isCompatibleWithLowerBounds(TypeWrapper typeWrapper) {
		for(Type lowerBoundType : getLowerBounds()) {
			if(!typeWrapper.isWeakAssignableFrom(lowerBoundType))
				return false;
		}
		return true;
	}
	
	/**
	 * Two variable types are equivalent if the only difference is the name of the variable.
	 * @param typeWrapper the object to compare equivalence.
	 * @return true if the types are equivalent, false otherwise.
	 */
	public boolean equivalents(VariableTypeWrapper typeWrapper) {
		Set<Type> thisUpperBounds = new HashSet<>(Arrays.asList(getUpperBounds()));
		Set<Type> thisLowerBounds = new HashSet<>(Arrays.asList(getLowerBounds()));
		Set<Type> thatUpperBounds = new HashSet<>(Arrays.asList(typeWrapper.getUpperBounds()));
		Set<Type> thatLowerBounds = new HashSet<>(Arrays.asList(typeWrapper.getLowerBounds()));
		return thisUpperBounds.equals(thatUpperBounds) && thisLowerBounds.equals(thatLowerBounds);
	}
	
	@Override
	public boolean isWeakAssignableFrom(TypeWrapper typeWrapper) {
		return isCompatibleWithUpperBounds(typeWrapper) && isCompatibleWithLowerBounds(typeWrapper);
	}
	
	public boolean isWeakAssignableTo(TypeWrapper typeWrapper) {
		for(Type upperBoundType : getUpperBounds()) {
			TypeWrapper upperBoundTypeWrapper = wrap(upperBoundType);
			if (! (typeWrapper.isWeakAssignableFrom(upperBoundTypeWrapper) ||  //the target type is a subtype of the upper bound.
					upperBoundTypeWrapper.isWeakAssignableFrom(typeWrapper)) ) //the target type is a supertype of the upper bound.
				return false;
		}
		for(Type lowerBoundType : getLowerBounds()) {
			TypeWrapper lowerBoundTypeWrapper = wrap(lowerBoundType);
			if (! (typeWrapper.isWeakAssignableFrom(lowerBoundTypeWrapper)) )
				return false;
		}
		return true;
	}
	
	@Override
	public Type asType(Type targetType) {
		if(wrappedType.equals(targetType))
			return wrappedType;
		if(isWeakAssignableFrom(targetType))
			return targetType;
		
		TypeWrapper wrappedTargetType = wrap(targetType);
		if( (wrappedTargetType instanceof VariableTypeWrapper) && wrappedTargetType.isWeakAssignableFrom(wrappedType))
			return wrappedType;
		
		throw new IncompatibleTypesException(wrappedType, targetType);
	}
	
	@Override
	public Type mostSpecificType(Type targetType) {
		TypeWrapper targetTypeWrapper = wrap(targetType);
		if(targetTypeWrapper instanceof VariableTypeWrapper) {
			return asType(targetType);
		} else {
			if(isWeakAssignableFrom(targetType))
				return targetType;
			else if(targetTypeWrapper.isWeakAssignableFrom(wrappedType))
				return wrappedType;
			else
				throw new IncompatibleTypesException(wrappedType, targetType);
		}
	}
	
/*
	@Override
	public boolean isErased() {
		return true;
	}
	
	@Override
	public Type[] getParameters() {
		if(true) throw new UnsupportedOperationException();
		return null;
	}

	@Override
	public boolean isArray() {
		if(true) throw new UnsupportedOperationException();
		return false;
	}
*/

//	@Override
//	public boolean isWeakAssignableFrom(TypeWrapper typeWrapper) {
//		return true;
//	}
//	
//	@Override
//	public boolean isRawClassAssignableFrom(TypeWrapper type) {
//		return true;
//	}
	
	/*
	 * Answers if wrappedType is an instanceof WildcardType
	 */
	public boolean isWildcard() {
		return wrappedType instanceof WildcardType;
	}


	/**
	 * Returns Object.class since "References pointing to objects of unknown type are usually expressed as a reference of type Object."
	 * Source: http://www.angelikalanger.com/GenericsFAQ/FAQSections/TypeArguments.html#FAQ001
	 */
	@Override
	public Class<?> getRawClass() {
		return Object.class;
	}
	
	public String getName() {
		if(isWildcard())
			return "?"; //no name	
		else
			return ((TypeVariable)wrappedType).getName();
	}


	@Override
	public void print() {
		super.print();
		System.out.println("Name: "+getName());
		
	}

	@Override
	public void collectTypeVariables(List<Type> typeVariables) {
		if(!typeVariables.contains(wrappedType))
			typeVariables.add(wrappedType);
	}
/*
	@Override
	public boolean canBindTypeParameters(Map<TypeVariable, Type> typeVariableMap) {
		return typeVariableMap.get(wrappedType) != null;
	}
*/
	
	@Override
	public Type bindVariables(Map<TypeVariable, Type> typeVariableMap) {
		Type mappedType = typeVariableMap.get(wrappedType);
		if(mappedType != null)
			return mappedType;
		else
			return wrappedType;
	}
	
	public boolean isVariable() {
		return true;
	}

	@Override
	protected Type wildcardReplacedType(WildcardTypeReplacementContext context) {
		if(!isWildcard()) {
			return wrappedType;
		} else {
			return new ReplacedWildcardType(context.nextName(), (WildcardType) getWrappedType());
		}
		
	}

	@Override
	public Type restoreWildcardTypes() {
		if(wrappedType instanceof ReplacedWildcardType) {
			ReplacedWildcardType replacedWildcardType = (ReplacedWildcardType) wrappedType;
			return replacedWildcardType.asWildCardType();
		} else
			return wrappedType;
	}
}