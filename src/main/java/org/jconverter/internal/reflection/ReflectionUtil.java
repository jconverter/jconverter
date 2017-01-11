package org.jconverter.internal.reflection;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jconverter.internal.reflection.typevisitor.TypeVisitor;
import org.jconverter.internal.reflection.typevisitor.FindFirstTypeVisitor;
import org.jconverter.internal.reflection.typewrapper.TypeWrapper;

import com.google.common.reflect.TypeToken;

public class ReflectionUtil {

	public static boolean isFileLoaded(File file, URLClassLoader classLoader) {
		try {
			return isUrlLoaded(file.toURI().toURL(), classLoader);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static boolean isUrlLoaded(URL url, URLClassLoader classLoader) {
		for(URL urlClassLoader : classLoader.getURLs()) {
			if(urlClassLoader.equals(url))
				return true;
		}
		return false;
	}

	/**
	 * Answers whether a resource is in a given package
	 * @param resource the name of the resource
	 * @param packageName the name of the package
	 * @return whether the resouce is in a package
	 */
	public static boolean isResourceInPackage(String resource, String packageName) {
		return isResourceInPackage(resource, packageName, true);
	}
	
	/**
	 * Answers whether a resource is in a given package
	 * For example, resource 'p1/p2/r' is in package p1.p2
	 * @param resource the name of the resource
	 * @param packageName the name of the package
	 * @param includeSubPackages whether subpackages should be ignored
	 * @return whether the resouce is in a package
	 */
	public static boolean isResourceInPackage(String resource, String packageName, boolean includeSubPackages) {
		String packageAsPath = packageName!=null?packageName.replace(".", "/"):"";
		if(resource.startsWith(packageAsPath)) {
			if(!includeSubPackages) {
				String subPath = resource.substring(packageAsPath.length()); //path relative to the given package
				if(subPath.startsWith("/"))
					subPath = subPath.substring(1);
				if(subPath.contains("/"))
					return false;
			}
			return true;
		} else
			return false;
	}

	/**
	 * Answers a String representing the package where the resource is located. An empty String if the resource is in the default package
	 * @param resource the resource to inspect
	 * @return a String representing the package where the resource is located. An empty String if the resource is in the default package
	 */
	public static String resourcePackage(String resource) {
		String[] splitted = resource.split("/");
		String fileResourceName = splitted[splitted.length-1];
		String parentPackage = resource.substring(0, resource.length() - fileResourceName.length());
		parentPackage = parentPackage.replace("/", ".");
		return parentPackage;
	}

	/**
	 * Answers if the two methods can handle the same message
	 * This is true if they have the same name and same number and type of parameters
	 * (the return type is not relevant)
	 * @param m1 a method
	 * @param m2 a method
	 * @return if the two methods have the same argument types and number
	 */
	public static boolean handleSameMessage(Method m1, Method m2) {
		/*
		if(!m1.getReturnType().equals(m2.getReturnType()))
			return false;
		*/
		if(!m1.getName().equals(m2.getName()))
			return false;
		
		Class<?>[] params1 = m1.getParameterTypes();
		Class<?>[] params2 = m2.getParameterTypes();
		if(params1.length != params2.length)
			return false;
		
		for(int i = 0; i<params1.length; i++) {
			if(!params1[i].equals(params2[i]))
				return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @param method
	 * @param methods
	 * @return true if the first parameter method handle the same message than one method in the second parameter collection, false otherwise
	 */
	private static boolean isHandled(Method method, Iterable<Method> methods) {
		for(Method m : methods) {
			if(handleSameMessage(method, m))
				return true;
		}
		return false;
	}

	public static List<Method> getAllAbstractMethods(Class<?> clazz) {
		List<Method> publicAbstractMethods = new ArrayList<>();
		getAllPublicAbstractMethods(clazz, publicAbstractMethods);
		List<Method> nonPublicAbstractMethods = new ArrayList<>();
		getAllNonPublicAbstractMethods(clazz, nonPublicAbstractMethods);
		List<Method> allAbstractMethods = publicAbstractMethods;
		allAbstractMethods.addAll(nonPublicAbstractMethods);
		return allAbstractMethods;
	}
	
	private static void getAllPublicAbstractMethods(Class<?> clazz, List<Method> abstractMethods) {
		if(clazz == null)
			return;
		for(Method method : clazz.getMethods()) //only answers public methods (both declared and inherited). It includes any method declared in the class interfaces (methods in interfaces must be public)
			if(isAbstract(method) && !isHandled(method, abstractMethods))
				abstractMethods.add(method);
		getAllNonPublicAbstractMethods(clazz.getSuperclass(), abstractMethods);
	}
	
	private static void getAllNonPublicAbstractMethods(Class<?> clazz, List<Method> abstractMethods) {
		if(clazz == null)
			return;
		for(Method method : clazz.getDeclaredMethods()) //answers ALL the methods declared by the class. Methods in the class interfaces are ignored.
			if(!isPublic(method) && isAbstract(method) && !isHandled(method, abstractMethods))
				abstractMethods.add(method);
		getAllNonPublicAbstractMethods(clazz.getSuperclass(), abstractMethods);
	}
	
	

	
	/**
	 * 
	 * @param clazz a class
	 * @param interfaze an interface
	 * @return a boolean indicating if a class adds an interface to its class hierarchy
	 */
	public static boolean includesInterfaceInHierarchy(Class<?> clazz, Class<?> interfaze) {
		//Object.class will never answer true to the first condition, so the call to getSuperclass() in the second is safe
		return (interfaze.isAssignableFrom(clazz) && !interfaze.isAssignableFrom(clazz.getSuperclass()));
	}
	
	/**
	 * 
	 * @param clazz a class
	 * @return an array with all the interfaces included by {@code clazz}
	 */
	public static Class<?>[] includedInterfaces(Class<?> clazz) {
		List<Class<?>> includedInterfaces = new ArrayList<>();
		for(Class<?> interfaze : clazz.getInterfaces()) {
			if(includesInterfaceInHierarchy(clazz, interfaze))
				includedInterfaces.add(interfaze);
		}
		return includedInterfaces.toArray(new Class[] {});
	}
	

	/**
	 * @param ancestor the ancestor in the hierarchy
	 * @param descendant the descendant in the hierarchy
	 * @return All the classes between {@code ancestor} and {@code descendant} ({@code ancestor} and {@code descendant} are also included)
	 * @throws IncompatibleTypesException in case {@code ancestor} is not an ancestor of {@code descendant}
	 */
	public static Class<?>[] getClassesInHierarchy(Class<?> ancestor, Class<?> descendant) {
		List<Class<?>> hierarchy = new ArrayList<>();
		
		Class<?> currentDescendant = descendant;
		while(true) {
			hierarchy.add(0, currentDescendant);
			if(currentDescendant.equals(ancestor)) { //done, we reach the ancestor in the hierarchy
				return hierarchy.toArray(new Class[] {});
			} else if(currentDescendant.equals(Object.class)) {
					throw new IncompatibleTypesException(ancestor, descendant);
			} else {
				currentDescendant = currentDescendant.getSuperclass();
			}
		}
	}
	
	/*
	public static Field getField(Object target, String propertyName) {
		return getFieldInHierarchy(target.getClass(), propertyName);
	}
	
	private static Field getFieldInHierarchy(Class clazz, String propertyName) {
		Field field = null;
		try {
			field = clazz.getField(propertyName); //does not work for non public fields
		} catch(NoSuchFieldException e1) { //Unknown property
			try {
				field = getFieldInHierarchyAux(clazz, propertyName);
			} catch(Exception e2) {
				throw new RuntimeException(e1);
			}
		}
		return field;
	}
	
	private static Field getFieldInHierarchyAux(Class clazz, String propertyName) {
		Field field = null;
		try {
			field = clazz.getDeclaredField(propertyName); //all the fields declared by the current class
		} catch(NoSuchFieldException e2) { //Unknown property
			if(clazz.equals(Object.class))
				throw new RuntimeException(e2);
			else
				field = getFieldInHierarchyAux(clazz.getSuperclass(), propertyName);
		}
		return field;
	}
*/
	
	
	public static boolean isVisible(Class<?> clazz, Field field) {
		Field visibleField = getVisibleField(clazz, field.getName());
		return visibleField != null && visibleField.equals(field);
	}
	
	public static Field getVisibleField(Class<?> clazz, String fieldName) {
		return visibleFields(clazz).get(fieldName);
	}
	
	/**
	 * Returns a map with all the visible fields in a class:
	 * - all the fields declared in the class, 
	 * - the public and protected fields of the ancestor classes, and 
	 * - the "package" fields of superclasses located in the same package
	 * @param clazz a class
	 * @return the visible fields of a class
	 */
	public static Map<String, Field> visibleFields(Class<?> clazz) {
		Map<String, Field> visibleFields = new HashMap<String, Field>();
		Field[] declaredFields = clazz.getDeclaredFields();
		for(Field declaredField : declaredFields) {
			visibleFields.put(declaredField.getName(), declaredField);
		}
		visibleSuperFields(clazz, visibleFields);
		return visibleFields;
	}
	
	public static void visibleSuperFields(final Class<?> clazz, final Map<String, Field> visibleFields) {
		Class<?> superClass = clazz.getSuperclass();
		if(superClass != null) {
			TypeVisitor typeVisitor = new TypeVisitor(TypeVisitor.InterfaceMode.EXCLUDE_INTERFACES) {
				@Override public boolean doVisit(Class<?> clazzInHierarchy) {
					Field[] declaredFields = clazzInHierarchy.getDeclaredFields();
					for(Field declaredField : declaredFields) {
						if(!visibleFields.containsKey(declaredField.getName())) //check if the field is already there
							if(!Modifier.isPrivate(declaredField.getModifiers())) { //exclude private fields in super classes
								if(!hasPackageAccessModifier(declaredField) || clazzInHierarchy.getPackage().equals(clazz.getPackage())) //exclude 'package' fields in classes declared in different packages
									visibleFields.put(declaredField.getName(), declaredField);
							}
					}
					return true;
				}
			};
			typeVisitor.visit(superClass);
		}
	}
	
	public static boolean hasPackageAccessModifier(int modifiers) {
		return !Modifier.isPrivate(modifiers) && !Modifier.isProtected(modifiers) && !Modifier.isPublic(modifiers);
	}
	
	public static boolean isAbstract(Type type) {
		return Modifier.isAbstract(TypeToken.of(type).getRawType().getModifiers()); //primitive type classes answer yes to this
	}
	
	public static boolean isInterface(Type type) {
		return TypeToken.of(type).getRawType().isInterface();
	}
	
	public static boolean isAbstract(Method method) {
		return Modifier.isAbstract(method.getModifiers());
	}
	
	public static boolean isPublic(Method method) {
		return Modifier.isPublic(method.getModifiers());
	}
	
	public static boolean isProtected(Method method) {
		return Modifier.isProtected(method.getModifiers());
	}
	
	public static boolean isPrivate(Method method) {
		return Modifier.isPrivate(method.getModifiers());
	}
	
	public static boolean hasPackageAccessModifier(Method method) {
		return hasPackageAccessModifier(method.getModifiers());
	}
	
	public static boolean isPublic(Field field) {
		return Modifier.isPublic(field.getModifiers());
	}
	
	public static boolean isProtected(Field field) {
		return Modifier.isProtected(field.getModifiers());
	}
	
	public static boolean isPrivate(Field field) {
		return Modifier.isPrivate(field.getModifiers());
	}
	
	public static boolean isFloatingPoint(Number number) {
		return number instanceof BigDecimal || number instanceof Float || number instanceof Double;
	}
	
	public static boolean hasPackageAccessModifier(Field field) {
		return hasPackageAccessModifier(field.getModifiers());
	}


	public static <A extends Annotation> A getParameterAnnotation(Method method, int position, Class<A> annotationClass) {
		for(Annotation anAnnotation : method.getParameterAnnotations()[position]) {
			if(anAnnotation.annotationType().equals(annotationClass))
				return (A) anAnnotation;
		}
		return null;
	}

	public static Class<?> findFirstNonSyntheticClass(Class<?> candidateClass) {
		FindFirstTypeVisitor finderVisitor = new FindFirstTypeVisitor(TypeVisitor.InterfaceMode.INCLUDE_INTERFACES) {
			@Override
			public boolean match(Class<?> clazz) {
				return !clazz.isSynthetic();
			}
		};
		finderVisitor.visit(candidateClass);
		return finderVisitor.getFoundType();
	}
	
	/**
	 * Unchecked instantiation of a class
	 * @param clazz the class to instantiate
	 * @return the instantiated object
	 */
	public static <T> T newInstance(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Object newArray(Type type, int length) {
		return Array.newInstance(TypeWrapper.wrap(type).getRawClass(), length);
	}
	
	public static boolean instanceOfOne(Object o, Class<?>... classes) {
		for(Class<?> clazz : classes)
			if(clazz.isAssignableFrom(o.getClass()))
				return true;
		return false;
	}

}
