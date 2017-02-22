package org.typetools;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.typetools.typewrapper.ArrayTypeWrapper;
import org.typetools.typewrapper.SingleTypeWrapper;
import org.typetools.typewrapper.TypeWrapper;
import org.typetools.typewrapper.VariableTypeWrapper;


public abstract class TypeUtil {

	/**
	 *
	 * @param types a list of types.
	 * @return a list of raw classes from the given types.
	 */
	public static List<Class<?>> asRawClasses(List<Type> types) {
		List<Class<?>> rawClasses = new ArrayList<>();
		for(Type type : types) {
			TypeWrapper wrappedType = TypeWrapper.wrap(type);
//			if(wrappedType instanceof VariableTypeWrapper)
//				throw new RuntimeException("Undefined type.");
			rawClasses.add(wrappedType.getRawClass());
		}
		return rawClasses;
	}


	/**
	 * Answers a parameterized type with its actual type arguments depending on the descendant
	 * @param ancestor is the type to parameterize
	 * @param descendant gives the type arguments to the ancestor type
	 * @return a parameterized type with its actual type arguments depending on the descendant. Null if the unification is not possible
	 */
	public static Type bindTypeGivenDescendant(Type ancestor, Type descendant) {
		return bindTypeGivenDescendant(TypeWrapper.wrap(ancestor), TypeWrapper.wrap(descendant));
	}
	
	public static Type bindTypeGivenDescendant(TypeWrapper ancestor, TypeWrapper descendant) {
		if(!ancestor.hasTypeParameters()) { //ancestor does not have any type parameters
			if(ancestor.isAssignableFrom(descendant)) //if ancestor is assignable from descendant (e.g., it is higher in the hierarchy)
				return ancestor.getWrappedType(); //just return the ancestor wrapped type
			else
				throw new IncompatibleTypesException(ancestor.getWrappedType(), descendant.getWrappedType());
		}
		
		ancestor = TypeWrapper.wrap(ancestor.wildCardReplacedType());
		Map<TypeVariable, Type> typeVariableMap = unifyWithDescendant(ancestor, descendant); //will throw an exception if the unification of types is not possible
		Type unifiedType;
		if(typeVariableMap == null) //the descendant does not provide parameterized type information
			unifiedType = ancestor.getWrappedType();
		else {
			unifiedType = ancestor.bindVariables(typeVariableMap);
			TypeWrapper wrappedUnifiedType = TypeWrapper.wrap(unifiedType);
			if(!ancestor.hasActualTypeArguments()) {
				boolean redundant = true;
				for(int i = 0; i<wrappedUnifiedType.getActualTypeArguments().length; i++) {
					TypeWrapper wrappedtypeArgument = TypeWrapper.wrap(wrappedUnifiedType.getActualTypeArguments()[i]);
					if(!(wrappedtypeArgument instanceof VariableTypeWrapper)) {  //the type variable is bound to a non-type variable
						redundant = false;
						break;
					}
					else { //the type variable is bound to another type variable
						VariableTypeWrapper variableTypeWrapper = (VariableTypeWrapper) wrappedtypeArgument;
						VariableTypeWrapper paramTypeWrapper = (VariableTypeWrapper)TypeWrapper.wrap(ancestor.getTypeParameters()[i]);
						if(!variableTypeWrapper.equivalents(paramTypeWrapper)) { //the bound variable has different bounds than the original type variable
							redundant = false;
							break;
						}
					}
				}
				if(redundant)
					unifiedType = ancestor.getWrappedType();
			}
		}
		unifiedType = TypeWrapper.wrap(unifiedType).restoreWildcardTypes();
		return unifiedType;
	}

	
	public static Map<TypeVariable, Type> unifyWithDescendant(Type ancestor, Type descendant) {
		return unifyWithDescendant(TypeWrapper.wrap(ancestor), TypeWrapper.wrap(descendant));
	}
	
	/**
	 * This method unifies the variable type arguments in an ancestor type according to the types in a descendant
	 * @param ancestor An ancestor type with some type variables
	 * @param descendant A descendant type without type variables
	 * @return a map of type variables to concrete types, or null if no parameterized type information is provided by the descendant.
	 */
	public static Map<TypeVariable, Type> unifyWithDescendant(TypeWrapper ancestor, TypeWrapper descendant) {
		Map<TypeVariable, Type> typeVariables = new HashMap<TypeVariable, Type>(); //the map to return
		if(descendant instanceof VariableTypeWrapper)
			throw new TypeUnificationException();
		if(ancestor instanceof VariableTypeWrapper) {
			unify(ancestor, descendant, typeVariables); //bound the variable type to the descendant
			return typeVariables;
		}
		if(ancestor instanceof ArrayTypeWrapper) {
			if(descendant instanceof ArrayTypeWrapper)
			return unifyWithDescendant(((ArrayTypeWrapper) ancestor).getComponentType(), ((ArrayTypeWrapper) descendant).getComponentType());
		}
		if(!ancestor.getRawClass().isAssignableFrom(descendant.getRawClass())) {
			throw new IncompatibleTypesException(ancestor.getWrappedType(), descendant.getWrappedType());
		}
		if(!ancestor.hasTypeParameters())  //if there are no type parameters, there are no type variables to unify (for example, if the ancestor is Object)
			return typeVariables;  //return empty map
		
		
		SingleTypeWrapper ancestorWithoutTypeArguments = (SingleTypeWrapper)TypeWrapper.wrap(ancestor.getRawClass()); //type arguments (if any) suppressed
		TypeWrapper[] ancestorBoundTypes = findAncestorTypeParameters(ancestorWithoutTypeArguments, (SingleTypeWrapper)descendant); //how the parameter types in the ancestor looks like when looking at the descendant types
		TypeWrapper[] ancestorUnboundTypes; //the original types in the ancestor
		if(ancestor.hasActualTypeArguments())
			ancestorUnboundTypes = TypeWrapper.wrap(ancestor.getActualTypeArguments());  //the current type arguments in the ancestor. Some type arguments should include unbound type variables
		else
			ancestorUnboundTypes = TypeWrapper.wrap(ancestor.getTypeParameters());
		
		if(ancestorUnboundTypes.length > 0 && ancestorBoundTypes.length == 0)
			return null;
		else
			unify(ancestorUnboundTypes, ancestorBoundTypes, typeVariables);
		
//		for(int i=0; i<ancestorUnboundTypes.length; i++) {
//			TypeWrapper ancestorUnboundType = ancestorUnboundTypes[i];
//			TypeWrapper ancestorBoundType = ancestorBoundTypes[i];
//			unify(ancestorUnboundType, ancestorBoundType, typeVariables);
//		}
		return typeVariables;
	}

	
	/**
	 * Unify the unbound variables of a type with the concrete types of a second type
	 * @param unboundType a type with unbound type variables
	 * @param boundType a concrete type
	 * @param typeVariables a map of bindings
	 */
	public static void unify(TypeWrapper unboundType, TypeWrapper boundType, Map<TypeVariable, Type> typeVariables) {
		if(unboundType instanceof VariableTypeWrapper) {
			VariableTypeWrapper variableType = (VariableTypeWrapper)unboundType;
			if(!variableType.isWildcard()) {
				Type existingBoundType = typeVariables.get(variableType.getWrappedType());
				if(existingBoundType == null) { //the type variable is not present in the map.
					typeVariables.put((TypeVariable) variableType.getWrappedType(), boundType.getWrappedType()); //associating the type variable with its bound concrete type
				} else { //the type variable already existed in the map
					if(!existingBoundType.equals(boundType.getWrappedType())) //the existing value should be identical to the bound value
						throw new TypeUnificationException("Variable " + variableType.getWrappedType() + " is already bound to a different value. " +
								"Bound type: " + existingBoundType + ".  " +
								"New type: " + boundType.getWrappedType());
				}
			}
		} else if(boundType instanceof VariableTypeWrapper) {
			return;
		} else if(unboundType instanceof ArrayTypeWrapper) {
			if(!(boundType instanceof ArrayTypeWrapper))
				throw new TypeUnificationException();
			else {
				ArrayTypeWrapper unboundArrayType = (ArrayTypeWrapper) unboundType;
				ArrayTypeWrapper boundArrayType = (ArrayTypeWrapper) boundType;
				if(unboundType.getDimension() != boundArrayType.getDimension())
					throw new TypeUnificationException();
				else
					unify(unboundArrayType.getBaseType(), boundArrayType.getBaseType(), typeVariables);
			}
		} else {
			SingleTypeWrapper unboundSingleType = (SingleTypeWrapper)unboundType;
			if(!unboundType.getRawClass().equals(boundType.getRawClass()))
				throw new TypeUnificationException();
			if(unboundSingleType.hasActualTypeArguments()) { //the unbound type has type arguments
				SingleTypeWrapper boundSingleType = (SingleTypeWrapper)boundType;
				if(boundSingleType.hasActualTypeArguments()) { //the bound type also has type arguments
					TypeWrapper[] nestedUnboundTypes = TypeWrapper.wrap(unboundSingleType.getActualTypeArguments());
					TypeWrapper[] nestedBoundTypes = TypeWrapper.wrap(boundSingleType.getActualTypeArguments());
					for(int i = 0; i<nestedUnboundTypes.length; i++) {
						unify(nestedUnboundTypes[i], nestedBoundTypes[i], typeVariables);
					}
				}
			}
		}
	}
	
	public static void unify(Type unboundType, Type boundType, Map<TypeVariable, Type> typeVariables) {
		unify(TypeWrapper.wrap(unboundType),  TypeWrapper.wrap(boundType), typeVariables);
		
	}

	public static void unify(TypeWrapper[] unboundType, TypeWrapper[] boundType, Map<TypeVariable, Type> typeVariables) {
		if(unboundType.length != boundType.length)
			throw new TypeUnificationException();
		for(int i = 0; i<unboundType.length; i++) {
			unify(unboundType[i], boundType[i], typeVariables);
		}
	}
	
//	public Map<TypeVariable, Type> unify(TypeWrapper[] unboundType, TypeWrapper[] boundType) {
//		Map<TypeVariable, Type> typeVariables = new HashMap<TypeVariable, Type>();
//		unify(unboundType, boundType, typeVariables);
//		return typeVariables;
//	}

	

	/**
	 * 
	 * @param descendant
	 * @param ancestor
	 * @return if a descendant reach an ancestor class or interface in a class inheritance chain.
	 */
	private static boolean descendantReachedAncestor(Class descendant, Class ancestor) {
		if(!descendant.isInterface()) //descendant is a class, not an interface
			return ( descendant.equals(ancestor) || (ancestor.isInterface() && includesInterfaceInHierarchy(descendant, ancestor)) );
		else
			return descendant.equals(ancestor);
	}



	/**
	 *
	 * @param clazz a class
	 * @param interfaze an interface
	 * @return a boolean indicating if a class adds an interface to its class hierarchy
	 */
	private static boolean includesInterfaceInHierarchy(Class<?> clazz, Class<?> interfaze) {
		//Object.class will never answer true to the first condition, so the call to getSuperclass() in the second is safe
		return (interfaze.isAssignableFrom(clazz) && !interfaze.isAssignableFrom(clazz.getSuperclass()));
	}

	/**
	 *
	 * @param clazz a class
	 * @return an array with all the interfaces included by {@code clazz}
	 */
	private static Class<?>[] includedInterfaces(Class<?> clazz) {
		List<Class<?>> includedInterfaces = new ArrayList<>();
		for(Class<?> interfaze : clazz.getInterfaces()) {
			if(includesInterfaceInHierarchy(clazz, interfaze))
				includedInterfaces.add(interfaze);
		}
		return includedInterfaces.toArray(new Class[] {});
	}




	/**
	 * 
	 * @param clazz
	 * @param interfaze
	 * @return the type parameters of a generic interface implemented by a class.
	 */
	private static TypeWrapper[] getActualTypeArgumentsInterface(Class clazz, Class interfaze) {
		for(Type t : clazz.getGenericInterfaces()) {
			SingleTypeWrapper interfaceWrapper = new SingleTypeWrapper(t);
			if(interfaceWrapper.getRawClass().equals(interfaze)) {
				TypeWrapper[] parameterizedTypes = TypeWrapper.wrap(interfaceWrapper.getActualTypeArguments());
				return parameterizedTypes;
			} 
		}
		return null;
	}
	

	/**
	 * Resolves the right types for an ancestor class taking into consideration a boolean flag indicating how to deal with unbound type variables.
	 * If the type variable names of the ancestor should be preserved, they will be replaced in the type list. Otherwise the type list is returned untouched.
	 * @param ancestor
	 * @param descendantParameterizedTypes
	 * @param keepVariableNamesAncestor
	 * @return
	 */
	private static TypeWrapper[] bindVariableTypes(Class clazz, TypeWrapper[] actualTypeArguments, boolean keepVariableNamesAncestor) {
		TypeWrapper[] boundVariableTypes;
		if(actualTypeArguments == null) { //no type list
			boundVariableTypes = TypeWrapper.wrap(clazz.getTypeParameters()); //just return the type variables of the ancestor
		}
		else {
			if(keepVariableNamesAncestor) {
				Map<TypeVariable, Type> replacementMap = new HashMap<TypeVariable, Type>();
				TypeVariable[] typeParameters = clazz.getTypeParameters();
				for(int i = 0; i<actualTypeArguments.length; i++) {
					if(actualTypeArguments[i].getWrappedType() instanceof TypeVariable)
						replacementMap.put((TypeVariable) actualTypeArguments[i].getWrappedType(), typeParameters[i]);
				}
				boundVariableTypes = bindVariableTypes(actualTypeArguments, replacementMap);
			} else {
				boundVariableTypes = actualTypeArguments;
			}
		}
		return boundVariableTypes;	
	}
	
	private static Type[] bindVariableTypes(Type[] types, Map<TypeVariable, Type> replacementMap) {
		return TypeWrapper.unwrap(bindVariableTypes(TypeWrapper.wrap(types), replacementMap));
	}
	
	private static TypeWrapper[] bindVariableTypes(TypeWrapper[] types, Map<TypeVariable, Type> replacementMap) {
		TypeWrapper[] boundTypes = new TypeWrapper[types.length];
		for(int i = 0; i<types.length; i++) {
			boundTypes[i] = TypeWrapper.wrap(types[i].bindVariables(replacementMap));
		}
		return boundTypes;
	}
	
	/*
	public static AbstractTypeWrapper[] bindingsForAncestor(Class ancestor, Class descendant, AbstractTypeWrapper[] descendantParameterizedTypes, boolean keepVariableNamesAncestor) {
		if(descendantParameterizedTypes != null) {
			if(ancestor.isInterface() && !descendant.isInterface()) {
				//SingleTypeWrapper interfaceWrapper = new SingleTypeWrapper(getGenericInterface(descendant, ancestor));
				//AbstractTypeWrapper[] bindingsInterface = AbstractTypeWrapper.wrap(interfaceWrapper.getParameters());
				System.out.println(descendant);
				System.out.println(ancestor);
				AbstractTypeWrapper[] bindingsInterface = getParameterizedTypesInterface(descendant, ancestor);
				AbstractTypeWrapper[] classParametersVar = AbstractTypeWrapper.wrap(descendant.getTypeParameters());
				for(int i=0; i<bindingsInterface.length; i++) {
					if(bindingsInterface[i] instanceof VariableTypeWrapper) {
						VariableTypeWrapper foundVariableType = (VariableTypeWrapper) bindingsInterface[i];
						for(int j=0; j<classParametersVar.length; j++) {
							if(foundVariableType.getName().equals(VariableTypeWrapper.class.cast(classParametersVar[j]).getName()))
								bindingsInterface[i] = descendantParameterizedTypes[j];
						}
							
					}
				}
				return bindingsInterface;
			}
			else {
				return descendantParameterizedTypes;
			}
		}
		else
			return AbstractTypeWrapper.wrap(ancestor.getTypeParameters());
	}
	*/
	
	/**
	 * 
	 * @param ancestor an ancestor in a class hierarchy
	 * @param descendant a descendant in a class hierarchy
	 * @return a map binding to each type variable in the ancestor a concrete type given by the descendant
	 */
	public static Map<TypeVariable, Type> findAncestorTypeParametersMap(Type ancestor, Type descendant) {
		Type[] typeArguments = findAncestorTypeParameters(ancestor, descendant);
		return asTypeVariableReplacementMap(ancestor, typeArguments);
	}
	
	private static Map<TypeVariable, Type> asTypeVariableReplacementMap(Type type, Type[] typeArguments) {
		Map<TypeVariable, Type> typeArgumentsMap = new HashMap<TypeVariable, Type>();
		TypeVariable[] typeVariables = new SingleTypeWrapper(type).getTypeParameters();
		for(int i=0; i<typeArguments.length; i++) {
			Type typeArgument = typeArguments[i];
			typeArgumentsMap.put(typeVariables[i], typeArgument);
		}
		return typeArgumentsMap;
	}
	
	
	public static Type[] findAncestorTypeParameters(Type ancestor, Type descendant) {
		return TypeWrapper.unwrap(findAncestorTypeParameters(new SingleTypeWrapper(ancestor), new SingleTypeWrapper(descendant)));
	}
	

	/*
	 * Return the parameter types of an ancestor given a descendant
	 */
	private static TypeWrapper[] findAncestorTypeParameters(SingleTypeWrapper ancestor, SingleTypeWrapper descendant) {
		return findAncestorTypeParameters(ancestor, descendant, false);
	}

	public static TypeWrapper[] findAncestorTypeParameters(SingleTypeWrapper ancestor, SingleTypeWrapper descendant, boolean keepVariableNamesAncestor) {
		TypeWrapper[] descendantParameterizedTypes = null;
		if(descendant.hasActualTypeArguments()) 
			descendantParameterizedTypes = TypeWrapper.wrap(descendant.getActualTypeArguments());
		else
			descendantParameterizedTypes = TypeWrapper.wrap(descendant.getTypeParameters());
		return findAncestorTypeParameters(ancestor.getRawClass(), descendant.getRawClass(), descendantParameterizedTypes, keepVariableNamesAncestor);
	}
	
	private static TypeWrapper[] findAncestorTypeParameters(Class ancestor, Class descendant, TypeWrapper[] descendantParameterizedTypes, boolean keepVariableNamesAncestor) {
		if(!ancestor.isAssignableFrom(descendant))
			throw new IncompatibleTypesException(ancestor, descendant);
		if(ancestor.equals(descendant)) {
			return bindVariableTypes(ancestor, descendantParameterizedTypes, keepVariableNamesAncestor);
		} else if(descendantReachedAncestor(descendant, ancestor)) {  //if we are here then descendant is a class and ancestor an interface
			for(Class includedInterface : includedInterfaces(descendant)) {
				if(ancestor.isAssignableFrom(includedInterface)) {
					TypeWrapper[] bindingsInterface = getActualTypeArgumentsInterface(descendant, includedInterface);
					
					
					Map<TypeVariable, Type> replacementMap = asTypeVariableReplacementMap(descendant, TypeWrapper.unwrap(descendantParameterizedTypes));
					bindingsInterface = bindVariableTypes(bindingsInterface, replacementMap);
					
					
					
					/*
					AbstractTypeWrapper[] classParametersVar = AbstractTypeWrapper.wrap(descendant.getTypeParameters());
					for(int i=0; i<bindingsInterface.length; i++) {
						if(bindingsInterface[i] instanceof VariableTypeWrapper) {
							VariableTypeWrapper foundVariableType = (VariableTypeWrapper) bindingsInterface[i];
							for(int j=0; j<classParametersVar.length; j++) {
								if(foundVariableType.getName().equals(VariableTypeWrapper.class.cast(classParametersVar[j]).getName()) && descendantParameterizedTypes != null) {
									if(!(descendantParameterizedTypes[j] instanceof VariableTypeWrapper) || keepVariableNamesAncestor)
										bindingsInterface[i] = descendantParameterizedTypes[j];
								}
							}
						}
					}
					*/
					return findAncestorTypeParameters(ancestor, includedInterface, bindingsInterface, keepVariableNamesAncestor);
				}
			}
			throw new RuntimeException(); //we should never arrive here
		}

		Class superClass = null;
		if(!descendant.isInterface() && ancestor.isAssignableFrom(descendant.getSuperclass()))
			superClass = descendant.getSuperclass();
		else {
			Class[] includedInterfaces = null;
			
			if(!descendant.isInterface())
				includedInterfaces = includedInterfaces(descendant);
			else
				includedInterfaces = descendant.getInterfaces();
			
			for(Class includedInterface : includedInterfaces) {
				if(ancestor.isAssignableFrom(includedInterface)) {
					superClass = includedInterface;
					break;
				}
			}
			
			if(superClass == null) { 
				if(!ancestor.equals(Object.class)) //the only way to arrive here is when ancestor is Object.class
					throw new RuntimeException();
				superClass = ancestor;
			}
				
		}
		
		TypeWrapper[] superParameterizedTypes = superParameterizedTypes(descendantParameterizedTypes, descendant, superClass);  //super can be null !!!
		/*
		if(descendantReachedAncestor(descendant.getSuperclass(), ancestor)) {
			if(!ancestor.isInterface())
				return superParameterizedTypes;
			else {
				SingleTypeWrapper interfaceWrapper = new SingleTypeWrapper(getGenericInterface(descendant, ancestor));		
			}
		}
		else*/
			return findAncestorTypeParameters(ancestor, superClass, superParameterizedTypes, keepVariableNamesAncestor);
	}
	
	private static TypeWrapper[] superParameterizedTypes(TypeWrapper[] classParameterizedTypesValues, Class clazz, Class superClass) {
		TypeWrapper[] superActualTypeArguments = superActualTypeArguments(clazz, superClass);
		if(classParameterizedTypesValues == null)
			return superActualTypeArguments;
		Map<TypeVariable, Type> replacementMap = asTypeVariableReplacementMap(clazz, TypeWrapper.unwrap(classParameterizedTypesValues));
		return bindVariableTypes(superActualTypeArguments, replacementMap);
		
		/*
		AbstractTypeWrapper[] classParameterizedTypes = AbstractTypeWrapper.wrap(clazz.getTypeParameters()); //the parameterized types in the class, as they are declared in the class file
		AbstractTypeWrapper[] superParameterizedTypes = AbstractTypeWrapper.wrap(superClass.getTypeParameters()); //the parameterized types in the super class, as they are declared in the class file
		
		AbstractTypeWrapper[] superActualTypeArguments = superActualTypeArguments(clazz, superClass); //the parameterized types in the super class as they are instantiated by the base class declaration

		for(int i=0; i<superActualTypeArguments.length; i++) {
			if(superActualTypeArguments[i] instanceof VariableTypeWrapper) {  //one of the super types is a variable, so probably it needs to be replaced by a value in classParameterizedTypesValues
				VariableTypeWrapper superParameterizedType = (VariableTypeWrapper) superActualTypeArguments[i]; //the name of the type variable corresponds to the name it was declared in clazz
				for(int j=0; j<classParameterizedTypes.length; j++) { //finding a variable type in the base class with the same name than superParameterizedType
					VariableTypeWrapper classParameterizedType = (VariableTypeWrapper) classParameterizedTypes[j];
					if(superParameterizedType.getName().equals(classParameterizedType.getName())) { //found it
						if(classParameterizedTypesValues != null) {
							superActualTypeArguments[i] = classParameterizedTypesValues[j];
						}
						if(superActualTypeArguments[i] instanceof VariableTypeWrapper && !keepVariableNamesAncestor) {
							superActualTypeArguments[i] = superParameterizedTypes[i]; //the variable will be replaced with another value with the same name used in the super class declaration
						}
						break;
					}
				}
			}
			
		}
		return superActualTypeArguments;
		*/
	}
	
	
	private static TypeWrapper[] superActualTypeArguments(Class clazz, Class superClass) {
		TypeWrapper[] superActualTypeArguments = null;
		if(superClass.equals(Object.class))
			superActualTypeArguments = new TypeWrapper[]{};
		else if(!clazz.isInterface() && clazz.getSuperclass().equals(superClass))
			superActualTypeArguments = TypeWrapper.wrap(new SingleTypeWrapper(clazz.getGenericSuperclass()).getActualTypeArguments());
		else {
			for(Type interfaze : clazz.getGenericInterfaces()) {
				SingleTypeWrapper interfaceWrapper = new SingleTypeWrapper(interfaze);
				if(interfaceWrapper.getRawClass().equals(superClass)) {
					superActualTypeArguments = TypeWrapper.wrap(interfaceWrapper.getActualTypeArguments());
					break;
				}
			}
		}
		return superActualTypeArguments;
	}


	/*
	 * Return the parameter types of an descendant given a ancestor
	 */
	public static Type[] findDescendantTypeParameters(Type ancestor, Type descendant) {
		return TypeWrapper.unwrap(findDescendantTypeParameters(new SingleTypeWrapper(ancestor), new SingleTypeWrapper(descendant)));
	}
	
	public static TypeWrapper[] findDescendantTypeParameters(SingleTypeWrapper ancestorTypeWrapper, SingleTypeWrapper descendantTypeWrapper) {
		Type boundDescendantType = bindTypeGivenAncestor(ancestorTypeWrapper, descendantTypeWrapper);
		return TypeWrapper.wrap(new SingleTypeWrapper(boundDescendantType).getActualTypeArguments());
	}
	
	public static Map<TypeVariable, Type> unifyWithAncestor(Type ancestor, Type descendant) {
		return unifyWithAncestor(TypeWrapper.wrap(ancestor), TypeWrapper.wrap(descendant));
	}
	
	/**
	 * This method unifies the variable type arguments in a descendant type according to the types in an ancestor
	 * @param ancestor An ancestor type with some type variables
	 * @param descendant A descendant type without type variables
	 * @return a map of type variables to concrete types
	 */
	public static Map<TypeVariable, Type> unifyWithAncestor(TypeWrapper ancestor, TypeWrapper descendant) {
		Map<TypeVariable, Type> typeVariables = new HashMap<TypeVariable, Type>();
		if(ancestor instanceof VariableTypeWrapper)
			throw new TypeUnificationException();
		if(descendant instanceof VariableTypeWrapper) {
			// Bind the variable type to the ancestor 
			// The unify method binds variables in the first argument to concrete types in the second argument. 
			// Hence why the first argument is the descendant and the second the ancestor
			unify(descendant, ancestor, typeVariables); 
			return typeVariables;
		}
		if(descendant instanceof ArrayTypeWrapper) {
			if(ancestor instanceof ArrayTypeWrapper)
				return unifyWithAncestor(((ArrayTypeWrapper) ancestor).getComponentType(), ((ArrayTypeWrapper) descendant).getComponentType());
		}
		if(!ancestor.getRawClass().isAssignableFrom(descendant.getRawClass())) {
			throw new IncompatibleTypesException(ancestor.getWrappedType(), descendant.getWrappedType());
		}
		if(!descendant.hasTypeParameters())  //if there are no type parameters, there are no type variables to unify
			return typeVariables;  //return empty map

		SingleTypeWrapper ancestorWithoutParameters = new SingleTypeWrapper(ancestor.getRawClass());
		TypeWrapper[] ancestorBoundTypes = findAncestorTypeParameters(ancestorWithoutParameters, (SingleTypeWrapper)descendant, false);
		
		
		TypeWrapper[] ancestorUnboundTypes;
		//ancestorUnboundTypes = TypeWrapper.wrap(ancestor.getActualTypeArguments());
		
		if(ancestor.hasActualTypeArguments())
			ancestorUnboundTypes = TypeWrapper.wrap(ancestor.getActualTypeArguments());  //the current type arguments in the ancestor. Some type arguments should include unbound type variables
		else
			ancestorUnboundTypes = TypeWrapper.wrap(ancestor.getTypeParameters());
		
		
		unify(ancestorBoundTypes, //the ancestor bound types correspond to the unbound type of the descendant
				ancestorUnboundTypes, 
				typeVariables);
		return typeVariables;
	}

	public static Type bindTypeGivenAncestor(TypeWrapper ancestor, TypeWrapper descendant) {
		Type unifiedType = null;
		if(!descendant.hasTypeParameters() || !ancestor.hasActualTypeArguments()) {
			if(ancestor.isAssignableFrom(descendant)) //if ancestor is assignable from descendant (e.g., it is higher in the hierarchy)
				unifiedType = descendant.getWrappedType();
			else
				throw new IncompatibleTypesException(ancestor.getWrappedType(), descendant.getWrappedType());
		}
		else {
			descendant = TypeWrapper.wrap(descendant.wildCardReplacedType());
			Map<TypeVariable, Type> typeVariableMap = unifyWithAncestor(ancestor, descendant);  //will throw an exception if the unification of types is not possible
			unifiedType = descendant.bindVariables(typeVariableMap);
			unifiedType = TypeWrapper.wrap(unifiedType).restoreWildcardTypes();
		}
		return unifiedType;
	}
	
	public static Type bindTypeGivenAncestor(Type ancestor, Type descendant) {
		return bindTypeGivenAncestor(TypeWrapper.wrap(ancestor), TypeWrapper.wrap(descendant));
	}
	
}
