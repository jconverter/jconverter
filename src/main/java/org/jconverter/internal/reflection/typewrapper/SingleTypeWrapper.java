package org.jconverter.internal.reflection.typewrapper;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.Map;

import org.jconverter.internal.reflection.TypeUtil;
import org.jconverter.internal.reflection.reification.ParameterizedTypeImpl;


public class SingleTypeWrapper extends TypeWrapper implements GenericDeclaration, AnnotatedElement {

	private static final long serialVersionUID = 1L;

	public SingleTypeWrapper(Type wrappedType) {
		super(wrappedType);
	}

	@Override
	public Class<?> getRawClass() {
		return (Class<?>) getRawType();
	}
	
	public Type getRawType() {
		if(wrappedType instanceof ParameterizedType)
			return ((ParameterizedType)wrappedType).getRawType();
		else
			return wrappedType;
	}

	public <U> Class<? extends U> asSubclass(Class<U> clazz) {
		return getRawClass().asSubclass(clazz);
	}

	public boolean desiredAssertionStatus() {
		return getRawClass().desiredAssertionStatus();
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return (T) getRawClass().getAnnotation(annotationClass);
	}

	@Override
	public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
		return getRawClass().isAnnotationPresent(annotationClass);
	}
	
	@Override
	public Annotation[] getAnnotations() {
		return getRawClass().getAnnotations();
	}

	@Override
	public Annotation[] getDeclaredAnnotations() {
		return getRawClass().getDeclaredAnnotations();
	}
	
	public String getCanonicalName() {
		return getRawClass().getCanonicalName();
	}
	
	public Class<?>[] getClasses() {
		return getRawClass().getClasses();
	}
	
	public ClassLoader 	getClassLoader() {
		return getRawClass().getClassLoader();
	}
	
	public Constructor getConstructor(Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
		return getRawClass().getConstructor(parameterTypes);
	}
	
	public Constructor[] getConstructors() {
		return getRawClass().getConstructors();
	}
	
	public Class<?>[] getDeclaredClasses() {
		return getRawClass().getDeclaredClasses();
	}
	
	public Constructor getDeclaredConstructor(Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
		return getRawClass().getDeclaredConstructor(parameterTypes);
	}
	
	public Constructor[] getDeclaredConstructors() {
		return getRawClass().getDeclaredConstructors();
	}
	
	public Field getDeclaredField(String name) throws NoSuchFieldException, SecurityException {
		return getRawClass().getDeclaredField(name);
	}
	
	public Field[] getDeclaredFields() {
		return getRawClass().getDeclaredFields();
	}
	
	public Method getDeclaredMethod(String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
		return getRawClass().getDeclaredMethod(name);
	}
	
	public Method[] getDeclaredMethods() {
		return getRawClass().getDeclaredMethods();
	}
	
	public Class<?> getDeclaringClass() {
		return getRawClass().getDeclaringClass();
	}
	
	public Class<?> getEnclosingClass() {
		return getRawClass().getEnclosingClass();
	}
	
	public Constructor<?> getEnclosingConstructor() {
		return getRawClass().getEnclosingConstructor();
	}
	
	public Method getEnclosingMethod() {
		return getRawClass().getEnclosingMethod();
	}
	
	public Field getField(String name) throws NoSuchFieldException, SecurityException {
		return getRawClass().getField(name);
	}
	
	public Field[] getFields() {
		return getRawClass().getFields();
	}
	
	public Type[] getGenericInterfaces() {
		return getRawClass().getGenericInterfaces();
	}
	
	public Type getGenericSuperclass() {
		return getRawClass().getGenericSuperclass();
	}
	
	public Class<?>[] getInterfaces() {
		return getRawClass().getInterfaces();
	}
	
	public Method getMethod(String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
		return getRawClass().getMethod(name, parameterTypes);
	}
	
	public Method[] getMethods() {
		return getRawClass().getMethods();
	}
	
	public int getModifiers() {
		return getRawClass().getModifiers();
	}
	
	public String getName() {
		return getRawClass().getName();
	}
	
	public Package getPackage() {
		return getRawClass().getPackage();
	}
	
	public ProtectionDomain getProtectionDomain() {
		return getRawClass().getProtectionDomain();
	}
	
	public URL getResource(String name) {
		return getRawClass().getResource(name);
	}
	
	public InputStream getResourceAsStream(String name) {
		return getRawClass().getResourceAsStream(name);
	}
	
	public Object[] getSigners() {
		return getRawClass().getSigners();
	}
	
	public String getSimpleName() {
		return getRawClass().getSimpleName();
	}
	
	public Class<?> getSuperclass() {
		return getRawClass().getSuperclass();
	}
	
	public boolean isAnnotation() {
		return getRawClass().isAnnotation();
	}
	
	public boolean isAnonymousClass() {
		return getRawClass().isAnonymousClass();
	}
	
	public boolean isEnum() {
		return getRawClass().isEnum();
	}
	
	public boolean isInstance(Object obj) {
		return getRawClass().isInstance(obj);
	}

	public boolean isInterface() {
		return getRawClass().isInterface();
	}
	
	public boolean isLocalClass() {
		return getRawClass().isLocalClass();
	}
	
	public boolean isMemberClass() {
		return getRawClass().isMemberClass();
	}
	
	public boolean isPrimitive() {
		return getRawClass().isPrimitive();
	}
	
	public boolean isSynthetic() {
		return getRawClass().isSynthetic();
	}
	
	@Override
	public TypeVariable[] getTypeParameters() {
		return getRawClass().getTypeParameters();
	}
	
	@Override
	public boolean hasTypeParameters() {
		return getTypeParameters().length>0;
	}
	
	@Override
	public boolean hasActualTypeArguments() {
		return ParameterizedType.class.isAssignableFrom(wrappedType.getClass());
	}

	@Override
	public Type[] getActualTypeArguments() {
		if(hasActualTypeArguments()) {
			return ((ParameterizedType)wrappedType).getActualTypeArguments();
		} else
			return new Type[] {};
	}
	
	public Type getOwnerType() {
		if(wrappedType instanceof ParameterizedType)
			return ((ParameterizedType)wrappedType).getOwnerType();
		else
			return ((Class)wrappedType).getEnclosingClass();
	}
	
	@Override
	public boolean isWeakAssignableFrom(TypeWrapper typeWrapper) {
		if(isAssignableFrom(typeWrapper))
			return true;
		if(typeWrapper instanceof VariableTypeWrapper) { //the variable types may have upper and lower bounds
			VariableTypeWrapper variableTypeWrapper = (VariableTypeWrapper) typeWrapper;
			return variableTypeWrapper.isWeakAssignableTo(this);
		}
		if (getRawClass().isAssignableFrom(typeWrapper.getRawClass())) {
			return (!hasActualTypeArguments() || hasAllActualTypeArgumentsUninstantiated()) || (!typeWrapper.hasActualTypeArguments() || typeWrapper.hasAllActualTypeArgumentsUninstantiated());
		}
		return false;
	}

	public Type asType(Type targetType) {
		if(wrappedType.equals(targetType))
			return wrappedType;
		TypeWrapper targetTypeWrapper = wrap(targetType);
		if(targetTypeWrapper instanceof VariableTypeWrapper)
			return ((VariableTypeWrapper)targetTypeWrapper).asType(this.getWrappedType());
		Type type = null;
		if(isWeakAssignableFrom(targetType))
			type = TypeUtil.bindTypeGivenAncestor(this, targetTypeWrapper);
		else
			type = TypeUtil.bindTypeGivenDescendant(targetTypeWrapper, this); //will throw an IncompatibleTypesException if !targetTypeWrapper.isWeakAssignableFrom(this)
		return type;
	}
	
	@Override
	public void collectTypeVariables(List<Type> typeVariables) {
		for(Type typeArgument : getActualTypeArguments()) {
			TypeWrapper.wrap(typeArgument).collectTypeVariables(typeVariables);
		}
	}
	
	@Override
	public void print() {
		super.print();
		if(isInterface())
			System.out.println("Interface");
		else if(isAbstract())
			System.out.println("Abstract class");
		else
			System.out.println("Concrete class");
		System.out.println("Class: "+getRawClass().getName());
		if(hasActualTypeArguments())
			System.out.println("Parameters: "+getActualTypeArguments().length);
		for(int i = 0; i<getActualTypeArguments().length; i++) {
			System.out.println("Parameter: "+i+": "+getActualTypeArguments()[i].toString());
		}
	}
	
	private boolean bindAtLeastOneTypeVariable(TypeVariable[] typeVariables, Map<TypeVariable, Type> typeVariableMap) {
		for(TypeVariable typeVariable: typeVariables) {
			if(typeVariableMap.containsKey(typeVariable))
				return true;
		}
		return false;
	}
	
	public boolean canBindTypeParameters(Map<TypeVariable, Type> typeVariableMap) {
		if(hasTypeParameters())
			return bindAtLeastOneTypeVariable(getTypeParameters(), typeVariableMap);
		return false;
	}
	/*
	@Override
	public boolean canBindTypeVariables(Map<TypeVariable, Type> typeVariableMap) {
		return hasActualTypeArguments() || canBindTypeParameters(typeVariableMap);
	}
	*/
	
	/**
	 * Bind the type variables sent in the map to any free type variables in the actual arguments list
	 * if the wrapped type is not a ParameterizedType the method does not do anything
	 */
	@Override
	public Type bindVariables(Map<TypeVariable, Type> typeVariableMap) {
		Type boundType = null;
		if(hasTypeParameters()) {
			Type[] actualTypeArguments = null;
			if(hasActualTypeArguments())
				actualTypeArguments = bindVariables(getActualTypeArguments(), typeVariableMap);
			else if(canBindTypeParameters(typeVariableMap)) {
				actualTypeArguments = bindVariables(getTypeParameters(), typeVariableMap);
			}
			if(actualTypeArguments != null)
				boundType = new ParameterizedTypeImpl(actualTypeArguments, getOwnerType(), getRawClass());
		}
		if(boundType == null) {
			boundType = wrappedType;
		}
		return boundType;
	}

	public boolean isVariable() {
		return false;
	}

	protected Type wildcardReplacedType(WildcardTypeReplacementContext context) {
		if(wrappedType instanceof Class) {
			return wrappedType;
		} else {
			ParameterizedType parameterizedType = (ParameterizedType) wrappedType;
			Type ownerType = parameterizedType.getOwnerType();
			Class<?> rawType = (Class<?>)parameterizedType.getRawType();
			Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
			Type[] actualTypeArgumentsReplaced = new Type[actualTypeArguments.length];
			boolean argumentReplaced = false;
			for(int i = 0; i<actualTypeArguments.length; i++) {
				actualTypeArgumentsReplaced[i] = wrap(actualTypeArguments[i]).wildcardReplacedType(context);
				if(actualTypeArgumentsReplaced[i] != actualTypeArguments[i]) {
					argumentReplaced = true;
				}
			}
			if(argumentReplaced)
				return new ParameterizedTypeImpl(actualTypeArgumentsReplaced, ownerType, rawType);
			else
				return wrappedType;
		}
	}

	public Type restoreWildcardTypes() {
		if(wrappedType instanceof Class) {
			return wrappedType;
		} else {
			ParameterizedType parameterizedType = (ParameterizedType) wrappedType;
			Type ownerType = parameterizedType.getOwnerType();
			Class<?> rawType = (Class<?>)parameterizedType.getRawType();
			Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
			Type[] actualTypeArgumentsReplaced = new Type[actualTypeArguments.length];
			boolean argumentReplaced = false;
			for(int i = 0; i<actualTypeArguments.length; i++) {
				actualTypeArgumentsReplaced[i] = wrap(actualTypeArguments[i]).restoreWildcardTypes();
				if(actualTypeArgumentsReplaced[i] != actualTypeArguments[i]) {
					argumentReplaced = true;
				}
			}
			if(argumentReplaced)
				return new ParameterizedTypeImpl(actualTypeArgumentsReplaced, ownerType, rawType);
			else
				return wrappedType;
		}
	}

}