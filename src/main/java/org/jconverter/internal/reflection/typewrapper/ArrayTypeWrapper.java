package org.jconverter.internal.reflection.typewrapper;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;

import org.jconverter.internal.reflection.IncompatibleTypesException;
import org.jconverter.internal.reflection.reification.GenericArrayTypeImpl;


public class ArrayTypeWrapper extends TypeWrapper {

	private static final long serialVersionUID = 1L;
	
	public ArrayTypeWrapper(Type wrappedType) {
		super(wrappedType);
	}
	
	public static boolean isArray(Type type) {
		return ( GenericArrayType.class.isAssignableFrom(type.getClass()) || (Class.class.isAssignableFrom(type.getClass()) && ((Class)type).isArray()) );
	}
	
	public static Type createArrayType(Type baseType, int dimension) {
		if(dimension < 0)
			throw new RuntimeException("Invalid dimension");
		if(dimension == 0)
			return baseType;
		TypeWrapper baseTypeWrapper = TypeWrapper.wrap(baseType);
		if(baseTypeWrapper.isRawType()) {
			Type componentType = createArrayType(baseType, dimension-1);
			return Array.newInstance((Class)componentType, 0).getClass();
		} else {
			return new GenericArrayTypeImpl(createArrayType(baseType, dimension-1));
		}
	}

	@Override
	public boolean hasTypeParameters() {
		return TypeWrapper.wrap(getBaseType()).hasTypeParameters();
	}

	@Override
	public TypeVariable[] getTypeParameters() {
		return TypeWrapper.wrap(getBaseType()).getTypeParameters();
	}
	
	@Override
	public Type[] getActualTypeArguments() {
		return TypeWrapper.wrap(getBaseType()).getActualTypeArguments();
	}
	
	@Override
	public boolean hasActualTypeArguments() {
		return TypeWrapper.wrap(getBaseType()).hasActualTypeArguments();
	}
	
	@Override
	public boolean isWeakAssignableFrom(TypeWrapper typeWrapper) {
		if(typeWrapper instanceof VariableTypeWrapper) {
			VariableTypeWrapper variableTypeWrapper = (VariableTypeWrapper) typeWrapper;
			return variableTypeWrapper.isWeakAssignableFrom(this); //the type variable can unify with this array.
		}
		
		if(!(typeWrapper instanceof ArrayTypeWrapper))
			return false;
		ArrayTypeWrapper arrayTypeWrapper = (ArrayTypeWrapper) typeWrapper;
		
		Type thisBaseType = getBaseType();
		Type thatBaseType = typeWrapper.getBaseType();
		if(wrap(thisBaseType).isPrimitive() || wrap(thatBaseType).isPrimitive()) { //if the base type of one of the arrays is a primitive, they are compatible only if identical.
			return thisBaseType.equals(thatBaseType) && getDimension() == arrayTypeWrapper.getDimension();
		}
		
		if(arrayTypeWrapper.isVariable()) {//the base type of the array is a variable type
			if(wrap(arrayTypeWrapper.getBaseType()).isWeakAssignableFrom(getBaseType()) //the type variable can unify with the base type of this array.
					&& getDimension() >= arrayTypeWrapper.getDimension())
				return true;
		}
		
		if(getBaseType().equals(Object.class) || 
				(wrap(getBaseType()) instanceof VariableTypeWrapper && ((VariableTypeWrapper)wrap(getBaseType())).hasDefaultUpperBounds()) ) {
			if( getDimension() <= arrayTypeWrapper.getDimension() )
				return true;
		} 
		if(getDimension() == arrayTypeWrapper.getDimension()) {
			return wrap(getBaseType()).isWeakAssignableFrom(arrayTypeWrapper.getBaseType());
		}
		return false;
	}
	
	@Override
	public Type asType(Type targetType) {
		if(wrappedType.equals(targetType))
			return wrappedType;
		TypeWrapper targetTypeWrapper = TypeWrapper.wrap(targetType);
		
		if(targetTypeWrapper instanceof VariableTypeWrapper && ((VariableTypeWrapper)targetTypeWrapper).hasDefaultUpperBounds())
			return wrappedType;
		
		if(!(targetTypeWrapper instanceof ArrayTypeWrapper))
				throw new IncompatibleTypesException(wrappedType, targetType);
				
		ArrayTypeWrapper arrayTargetTypeWrapper = (ArrayTypeWrapper) targetTypeWrapper;
		
		TypeWrapper thisBaseType = wrap(getBaseType());
		TypeWrapper thatBaseType = wrap(arrayTargetTypeWrapper.getBaseType());
		
		if(arrayTargetTypeWrapper.getDimension() < getDimension()) {
			if(thatBaseType instanceof VariableTypeWrapper && ((VariableTypeWrapper) thatBaseType).hasDefaultUpperBounds())
				return wrappedType;
			else
				throw new IncompatibleTypesException(wrappedType, targetType);
		}
			

		if(arrayTargetTypeWrapper.getDimension() > getDimension()) {
			if(getBaseType().equals(Object.class) || 
					(thisBaseType instanceof VariableTypeWrapper && ((VariableTypeWrapper)thisBaseType).hasDefaultUpperBounds()) ) {
				if(!(thatBaseType instanceof VariableTypeWrapper))
					return targetType;
				else
					return createArrayType(getBaseType(), arrayTargetTypeWrapper.getDimension());
			} else 
				throw new IncompatibleTypesException(wrappedType, targetType);	
		}
		
		//at this point: arrayTargetTypeWrapper.getDimension() == getDimension()
		Type asBaseType = wrap(getBaseType()).asType(arrayTargetTypeWrapper.getBaseType());
		return createArrayType(asBaseType, arrayTargetTypeWrapper.getDimension());
	}

/*
	@Override
	public Type[] getParameters() {
		if(isParameterized()) {
			if( ((GenericArrayType)wrappedType).getGenericComponentType() instanceof ParameterizedType) 
				return ((ParameterizedType)((GenericArrayType)wrappedType).getGenericComponentType()).getActualTypeArguments();
			else
				return new Type[] {((GenericArrayType)wrappedType).getGenericComponentType()};
		} else
		return new Type[] {};
	}
*/
	
	/**
	 * if the base type is a parameterized type, the array type will be a GenericArrayType
	 * this is also true for all the nested array types in a multidimensional array
	 * @return
	 */
	public Type getComponentType() {
		if(wrappedType instanceof GenericArrayType) {
			return ((GenericArrayType)wrappedType).getGenericComponentType();
		} else {
			return ((Class)wrappedType).getComponentType();
		}
	}
	
	public boolean isVariable() {
		return wrap(getBaseType()).isVariable();
	}
	
/*
	@Override
	public boolean isErased() {
		return false;
	}

	@Override
	public boolean isArray() {
		return true;
	}
*/
	
	@Override
	public int getDimension() {
		int componentDimension = 0;
		if(isArray(getComponentType())) {
			componentDimension = new ArrayTypeWrapper(getComponentType()).getDimension();
		}
		return 1 + componentDimension;
	}
	
	@Override
	public Type getBaseType() {
		Type componentType = getComponentType();
		if(isArray(componentType))
			return (new ArrayTypeWrapper(componentType)).getBaseType();
		else
			return componentType;		
	}


//	@Override
//	public boolean isWeakAssignableFrom(TypeWrapper typeWrapper) {
//		if(typeWrapper instanceof VariableTypeWrapper)
//			return true;
//		if(!(typeWrapper instanceof ArrayTypeWrapper))
//			return false;
//		return TypeWrapper.wrap(getBaseType()).isWeakAssignableFrom(ArrayTypeWrapper.class.cast(typeWrapper).getBaseType());
//	}
	


	@Override
	public Class<?> getRawClass() {
		Class<?> componentClass = TypeWrapper.wrap(getComponentType()).getRawClass();
		return Array.newInstance(componentClass, 0).getClass();
	}

	@Override
	public void print() {
		super.print();
		System.out.println("Class: "+getRawClass().getName());
		System.out.println("Dimensions: "+getDimension());
		if(hasActualTypeArguments())
			System.out.println("Parameterized array");
		System.out.println("Base type: "+getBaseType().toString());
	}

	@Override
	public void collectTypeVariables(List<Type> typeVariables) {
		TypeWrapper.wrap(getBaseType()).collectTypeVariables(typeVariables);
	}

	@Override
	public Type bindVariables(Map<TypeVariable, Type> typeVariableMap) {
		Type boundType;
		Type unboundComponentType = getComponentType();
		TypeWrapper wrappedComponentType = TypeWrapper.wrap(unboundComponentType);
		Type boundComponentType = wrappedComponentType.bindVariables(typeVariableMap);
		if(unboundComponentType.equals(boundComponentType))
			boundType = wrappedType;
		else
			boundType = new GenericArrayTypeImpl(boundComponentType);
		
		/*
		if(hasActualTypeArguments()) {
			AbstractTypeWrapper wrappedComponentType = AbstractTypeWrapper.wrap(getComponentType());
			Type componentType = wrappedComponentType.bindVariables(typeVariableMap);
			boundType = new GenericArrayTypeImpl(componentType);
		} else {
			boundType = wrappedType;
		}
		*/
		return boundType;
	}

	@Override
	protected Type wildcardReplacedType(WildcardTypeReplacementContext context) {
		if(wrappedType instanceof Class) {
			return wrappedType;
		} else {
			Type componentType = getComponentType();
			Type replacedComponentType = wrap(componentType).wildcardReplacedType(context);
			if(replacedComponentType != componentType)
				return new GenericArrayTypeImpl(replacedComponentType);
			else
				return wrappedType;
		}
	}

	@Override
	public Type restoreWildcardTypes() {
		if(wrappedType instanceof Class) {
			return wrappedType;
		} else {
			Type componentType = getComponentType();
			Type replacedComponentType = wrap(componentType).restoreWildcardTypes();
			if(replacedComponentType != componentType)
				return new GenericArrayTypeImpl(replacedComponentType);
			else
				return wrappedType;
		}
	}

}
