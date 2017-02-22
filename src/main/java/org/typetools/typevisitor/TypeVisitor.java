package org.typetools.typevisitor;


public class TypeVisitor {

	private InterfaceMode interfaceMode;
	
	public TypeVisitor() {
		this(InterfaceMode.INCLUDE_INTERFACES);
	}
	
	public TypeVisitor(InterfaceMode interfaceMode) {
		this.interfaceMode = interfaceMode;
	}
	
	/*
	 * Answers true if the visit was never interrupted
	 */
	public boolean visit(Class<?> clazz) {
		if(clazz == null) //remember that super of primitive classes are null
			return true;
		if(doVisit(clazz)) {
			if(!clazz.equals(Object.class)) {//end of the hierarchy
				if(interfaceMode !=null && interfaceMode.equals(InterfaceMode.EXCLUDE_INTERFACES)) {
					for(Class interfaze : clazz.getInterfaces()) { //answers only the interfaces declared by the class, does not include the ones in its superclass
						if(!visit(interfaze)) //should stop visiting
							return false;
					}
				}
				if(!clazz.isInterface()) {
					return visit(clazz.getSuperclass());
				}
			}
			return true;
		} else
			return false;
	}

	
	public boolean doVisit(Class<?> clazz) {return true;}
	
	
	
	public static enum InterfaceMode {
		INCLUDE_INTERFACES, EXCLUDE_INTERFACES;
	}
}



