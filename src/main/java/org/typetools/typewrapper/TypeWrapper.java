package org.typetools.typewrapper;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Array;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.typetools.IncompatibleTypesException;

import com.google.common.reflect.TypeToken;

/*
 * The objective of this class is to reduce the amount of castings and instanceof operations that otherwise classes dealing with Java type classes will have to do
 * 
 */
public abstract class TypeWrapper implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected Type wrappedType;
	
	public TypeWrapper(Type wrappedType) {
		setWrappedType(wrappedType);
	}
	
	public Type getWrappedType() {
		return wrappedType;
	}
	
	private void setWrappedType(Type wrappedType) {
		this.wrappedType = wrappedType;
	}
	
	/**
	 * Answers true if the receiver can be assigned from the parameter type (considering any type parameters).
	 * Returns false if the receiver has type parameters and the parameter does not.
	 * @param type a type
	 * @return if the receiver can be assigned from the parameter type
	 */
	public boolean isAssignableFrom(Type type) {
		return isAssignableFrom(wrap(type));
	}
	
	/**
	 * Answers true if the receiver can be assigned from the parameter type (without considering type parameters).
	 * If the receiver or the argument are variable types it returns true.
	 * @param type a type
	 * @return if the receiver can be assigned from the parameter type
	 */
//	private boolean isRawClassAssignableFrom(Type type) {
//		return isRawClassAssignableFrom(wrap(type));
//	}
//	
//	private boolean isRawClassAssignableFrom(TypeWrapper typeWrapper) {
//		return getRawClass().isAssignableFrom(typeWrapper.getRawClass());
//	}
	
	public boolean isAssignableFrom(TypeWrapper typeWrapper) {
		return TypeToken.of(wrappedType).isSupertypeOf(typeWrapper.wrappedType);
	}
	
	
	public boolean isWeakAssignableFrom(Type type) {
		return isWeakAssignableFrom(wrap(type));
	}
	
	public abstract boolean isWeakAssignableFrom(TypeWrapper typeWrapper);
	
	public boolean isRawType() {
		return !hasActualTypeArguments();
	}
	
	public abstract boolean isVariable();
	
	/**
	 * The base type in a (multidimensional) array corresponds to the component type of the most nested array
	 * For all the other types it corresponds to the type itself
	 * @return the base type of an array. The type itself if it is not an array
	 */
	public Type getBaseType() {
		return wrappedType;
	}
	
	/**
	 * For an array returns the number of dimensions of the array.
	 * For any other type returns 0
	 * @return the array dimension. 0 if it is not an array.
	 */
	public int getDimension() {
		return 0;
	}
	
	public abstract Class<?> getRawClass();
	//public abstract boolean isGenericType();
	public abstract boolean hasTypeParameters();
	public abstract TypeVariable[] getTypeParameters();
	public abstract Type[] getActualTypeArguments();
	public abstract boolean hasActualTypeArguments();
	
	
	protected boolean hasAllActualTypeArgumentsUninstantiated() {
		Type[] actualTypeArguments = getActualTypeArguments();
		if(actualTypeArguments.length == 0)
			return false;
		for(Type type : actualTypeArguments) {
			if(!(wrap(type) instanceof VariableTypeWrapper))
				return false;
		}
		return true;
	}
	
	/**
	 * Answers an array with the actual type arguments of a type. If one of the types arguments is a TypeVariable or Wildcard type, it will be substituted by a corresponding upper bound type
	 * If the type does not have actual type arguments the method will return an array with the upper bounds of the parameterized types
	 * @return either the actual type arguments or the upper bounds
	 */
	public Type[] getActualTypeArgumentsOrUpperBounds() {
		Type[] oneUpperBoundTypeParameters = getOneUpperBoundTypeParameters();
		Type[] types = null;
		if(hasActualTypeArguments()) {
			types = getActualTypeArguments();
			for(int i=0; i<types.length; i++) {
				if(TypeWrapper.wrap(types[i]) instanceof VariableTypeWrapper)
					types[i] = oneUpperBoundTypeParameters[i];
			}
		}
		else
			types = oneUpperBoundTypeParameters;
		return types;
	}
	
	public Type[] getOneUpperBoundTypeParameters() {
		TypeVariable[] typeParameters = getTypeParameters();
		Type[] upperBounds = new Type[typeParameters.length];
		for(int i=0; i<typeParameters.length; i++) {
			upperBounds[i] = typeParameters[i].getBounds()[0];
		}
		return upperBounds;
	}
	
	public Type[][] getUpperBoundsTypeParameters() {
		TypeVariable[] typeParameters = getTypeParameters();
		Type[][] upperBounds = new Type[typeParameters.length][];
		for(int i=0; i<typeParameters.length; i++) {
			upperBounds[i] = typeParameters[i].getBounds();
		}
		return upperBounds;
	}

	
	public TypeWrapper as(Type targetType) {
		return wrap(asType(targetType));
	}

	/**
	 * Binds the unbound types arguments of the parameter type according to the bindings of the receiving type
	 * For example, if this type is: {@code ArrayList<String>} and the parameter is the raw type Collection, it should return {@code Collection<String>}
	 * Viceversa, if this type is {@code Collection<String>} and the bound type is ArrayList, it will return {@code ArrayList<String>}
	 * Will throw a IncompatibleTypesException exception if the types are not compatible
	 * @param targetType a type
	 * @return the unbound types arguments of the parameter type according to the bindings of the receiving type
	 */
	public abstract Type asType(Type targetType);
	
	public Type mostSpecificType(Type targetType) {
		if(wrappedType.equals(targetType))
			return wrappedType;
		TypeWrapper targetTypeWrapper = wrap(targetType);
		if(targetTypeWrapper instanceof VariableTypeWrapper)
			return targetTypeWrapper.mostSpecificType(wrappedType);
		Type type = null;
		if(isWeakAssignableFrom(targetType))
			type = asType(targetType);
		else if (targetTypeWrapper.isWeakAssignableFrom(this))
			type = targetTypeWrapper.asType(this.wrappedType);
		else
			throw new IncompatibleTypesException(wrappedType, targetType);
		return type;
	}

	public boolean isAbstract() {
		return Modifier.isAbstract(getRawClass().getModifiers()); //primitive type classes answer yes to this
	}
	
	public boolean isPrimitive() {return getRawClass().isPrimitive();}
	
	public boolean isMemberClass() {return getRawClass().isMemberClass(); }
	
	public Class getEnclosingClass() {return getRawClass().getEnclosingClass(); }
	/**
	 * 
	 * @param length the length of the returned array
	 * @return an array of the wrapped type
	 * the component type of the returned array is given by the class representation of the wrapped type
	 * This implies that for Variable Type the returned array will be Object[] and not VariableType[]
	 * This is because the class representation (in the current implementation) of variable types is Object
	 * Then the component type of an array of the wrapped type should be consistent with this class representation
	 */
	public Object[] asArray(int length) {
		return (Object[]) Array.newInstance(getRawClass(), length);
	}
	

	/**
	 * Collects all the type variables nested in the type. Type Variables are inserted in the order they are found from left to right. No duplicates are collected. Wildcard Types are also included.
	 * @param types is the list collection the found type variable.
	 */
	protected abstract void collectTypeVariables(List<Type> types);
	
	/**
	 * @return all the type variables nested in the type. Type Variables are inserted in the order they are found from left to right. No duplicates are collected. Wildcard Types are also included.
	 */
	public List<Type> getTypeVariables() {
		List<Type> typeVariables = new ArrayList<Type>();
		collectTypeVariables(typeVariables);
		return typeVariables;
	}
	
	/**
	 * @return the named type variables nested in the type. Type Variables are inserted in the order they are found from left to right. No duplicates are collected. Wildcard Types are NOT included.
	 */
	public List<TypeVariable> getNamedTypeVariables() {
		List<TypeVariable> namedTypeVariables = new ArrayList<TypeVariable>();
		for(Type typeVariable : getTypeVariables()) {
			if(typeVariable instanceof TypeVariable)
				namedTypeVariables.add((TypeVariable) typeVariable);
		}
		return namedTypeVariables;
	}
	
	/**
	 *  
	 * @return a boolean indicating if the type has named type variables
	 */
	public boolean hasNamedTypeVariables() {
		return !getNamedTypeVariables().isEmpty();
	}
	
	/**
	 * 
	 * @param typeVariableMap is a map containing mappings from type variables to concrete types
	 * @return an equivalent type to the wrapped time, with the difference that all its named type variables have been substituted by types given by a map
	 */
	public abstract Type bindVariables(Map<TypeVariable, Type> typeVariableMap);

	
	public static Type[] bindVariables(Type[] types, Map<TypeVariable, Type> typeVariableMap) {
		Type[] boundTypes = new Type[types.length];
		for(int i=0; i<boundTypes.length; i++)
			boundTypes[i] = wrap(types[i]).bindVariables(typeVariableMap);
		return boundTypes;
	}

	@Override
	public String toString() {
		return "("+getClass().getSimpleName()+")" + getWrappedType().toString();
	}
	
	public void print() {
		System.out.println(toString());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((wrappedType == null) ? 0 : wrappedType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypeWrapper other = (TypeWrapper) obj;
		if (wrappedType == null) {
			if (other.wrappedType != null)
				return false;
		} else if (!wrappedType.equals(other.wrappedType))
			return false;
		return true;
	}
	
	
	public static TypeWrapper wrap(Type type) {
		if(ParameterizedType.class.isAssignableFrom(type.getClass()) || (Class.class.isAssignableFrom(type.getClass()) && !((Class)type).isArray()) )
			return new SingleTypeWrapper(type);
		else if(ArrayTypeWrapper.isArray(type))
			return new ArrayTypeWrapper(type);
		else
			return new VariableTypeWrapper(type);
	}
	
	public static TypeWrapper[] wrap(Type[] types) {
		TypeWrapper[] typeWrappers = new TypeWrapper[types.length];
		for(int i=0; i<types.length; i++) {
			typeWrappers[i] = wrap(types[i]);
		}
		return typeWrappers;
	}
	
	public static Type[] unwrap(TypeWrapper[] typeWrappers) {
		Type[] types = new Type[typeWrappers.length];
		for(int i=0; i<typeWrappers.length; i++) {
			types[i] = typeWrappers[i].getWrappedType();
		}
		return types;
	}
	
	
	
	private static class DummyGenericDeclaration implements GenericDeclaration {
		@Override
		public TypeVariable<?>[] getTypeParameters() {
			return new TypeVariable[]{};
		}

		@Override
		public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
			return null;
		}

		@Override
		public Annotation[] getAnnotations() {
			return new Annotation[]{};
		}

		@Override
		public Annotation[] getDeclaredAnnotations() {
			return new Annotation[]{};
		}
	}
	
	private static final DummyGenericDeclaration dummyGenericDeclaration = new DummyGenericDeclaration();
	
	
	protected class ReplacedWildcardType implements TypeVariable {

		//private final Type[] lowerBounds;
		private final WildcardType wildcardType;
		private final String name;
		
		public ReplacedWildcardType(String name, WildcardType wildcardType) {
			this.name = name;
			this.wildcardType = wildcardType;
			//this.lowerBounds = wildcardType.getLowerBounds();
		}

		public WildcardType asWildCardType() {
			return wildcardType;
		}


		@Override
		public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
			return null;
		}


		@Override
		public Annotation[] getAnnotations() {
			return new Annotation[]{};
		}


		@Override
		public Annotation[] getDeclaredAnnotations() {
			return new Annotation[]{};
		}


		@Override
		public Type[] getBounds() {
			return wildcardType.getUpperBounds();
		}


		@Override
		public GenericDeclaration getGenericDeclaration() {
			return dummyGenericDeclaration;
		}


		@Override
		public String getName() {
			return name;
		}

		@Override
		public AnnotatedType[] getAnnotatedBounds() {
			return new AnnotatedType[]{};
		}
	}
	
	
	protected class WildcardTypeReplacementContext {
		private static final String VARIABLE_TYPE_PREFIX = "WILD_CARD_REPLACEMENT_VAR_";
		private int counter;
		
		public String nextName() {
			return VARIABLE_TYPE_PREFIX + counter++;
		}
	}
	
	public Type wildCardReplacedType() {
		return wildcardReplacedType(new WildcardTypeReplacementContext());
	}

	protected abstract Type wildcardReplacedType(WildcardTypeReplacementContext context);
	
	public abstract Type restoreWildcardTypes();

}
