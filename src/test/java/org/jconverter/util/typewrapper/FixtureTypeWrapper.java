package org.jconverter.util.typewrapper;

import java.util.Map;

public class FixtureTypeWrapper {

	public class A<X,Y,Z> {}
	
	public class B<Y, Z> extends A<Map<Z, Y>, String, Y> {}
	
	//public class C<X> extends B<X> {}
	
	//public class D extends C<String> {}
}
