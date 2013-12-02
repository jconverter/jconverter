JConverter
==========

A Java framework to categorize and apply conversions between arbitrary objects.

Why JConverter ?
================

Some months ago I needed a library that simplifies the task of bi-directional convertions between arbitrary Java objects and their JSON representation.
After a bit of research I found Google's [Gson](https://code.google.com/p/google-gson/ "Google's Gson"), and I decided that it was pretty cool and intuitive to use.

Some time after this finding, I needed this time a library for converting between arbitrary Java objects and their Prolog term representations. Unfortunately, nothing like that existed at the moment. However, I still had fresh in my mind the Gson library and its simple mechanism for applying Java-JSON conversions. So I re-implemented Gson (for the scenario of Java - Prolog terms bi-directional conversions) and... it worked nicely! 
I presented it at an [ECOOP co-lacated workshop](http://wasdett.org/2013/ "WASDeTT") and the feedback I received was positive in general.

Nevertheless, I was not completely satisfied after my implementation. My library duplicated the work of other programmers, and although it targeted another domain, it still remained, from a functional perspective, quite similar.
It seemed to me that Gson (and now my newly created library) made use of a general pattern for converting between distinct representation of certain objects. The pattern seems to work fine in at least two different domains, but no library had generalized it until the moment.

Therefore, JConverter is my effort on providing a generalization of this conversion pattern in a simple and intuitive framework.




Getting Started
===============

JConverter allows to register converters between different representations of Java objects.
To facilitate its usage, many converters between common Java types are already pre-included in the library.
Therefore, to execute simple conversions often the only steps required are:

- Instantiate a JConverter context.
- Obtain the object to convert.
- Define the target type of the conversion.
- Accomplish the conversion.


An intuitive example is presented in the code extract below:

Pre-Defined Converters
----------------------

    JConverter context = new JConverter(); //a default JConverter context
    List source = asList(2,1,3); //a list of integers
    //target type is TreeSet<String> (set ordered according to the natural ordering of its members)
    Type targetType = new TypeToken<TreeSet<String>>(){}.getType(); 
    
    //Conversion:
    Set<String> orderedSet = context.convert(source, targetType); //converting source object to target type
    //TESTING RESULT:
    assertTrue(Arrays.equals(new String[]{"1","2","3"}, orderedSet.<String>toArray()));
    
    //Or if you prefer:
    Convertable convertable = new Convertable(source, context);
    orderedSet = convertable.as(targetType);
    //TESTING RESULT:
    assertTrue(Arrays.equals(new String[]{"1","2","3"}, orderedSet.<String>toArray()));
		
		

Custom Converters
----------------------		
		
    class Person {
    	String name;
    	public Person(String name) {
    		this.name = name;
    	}
    }
		
    JConverterBuilder builder = JConverterBuilder.create();
    builder.register(new Converter<String, Person>() {
    	@Override
    	public Person apply(String name, Type targetType, JConverter context) {
    		return new Person(name);
    	}
    });
    JConverter context = builder.build(); //a custom JConverter context
    Map<String, String> map = new HashMap<String, String>() {{
    	put("1", "Sarah");
    	put("2", "Abraham");
    	put("3", "Isaac");
    }};
		
    Type targetType = new TypeToken<Map<Integer,Person>>(){}.getType(); //target type is Map<Integer,Person>
		
    Convertable convertable = new Convertable(map, context);
    Map<Integer, Person> convertedMap = convertable.as(targetType);
    //TESTING RESULT:
    assertEquals(3, convertedMap.entrySet().size());
    assertEquals("Sarah", convertedMap.get(1).name);
    assertEquals("Abraham", convertedMap.get(2).name);
    assertEquals("Isaac", convertedMap.get(3).name);


License
=======
JConverter is open source, distributed under the terms of this [license](LICENSE.txt).


Try it !
=============

If you found JConverter interesting, you may want to take a look to the [API](http://jconverter.github.com/apidocs/ "API documentation ") documentation.

In case you are using Maven, JConverter is available at the [Sonatype Spanshots repository](https://oss.sonatype.org/index.html#nexus-search;quick~jconverter "Sonatype Spanshots repository"). Just add this dependency to your POM to include it into your project.





    <dependency>
      <groupId>com.github.jconverter</groupId>
      <artifactId>jconverter</artifactId>
      <version>0.0.1-alpha-SNAPSHOT</version>
    </dependency>



Contact
=======

Questions, constructive feedback and criticism can be sent to \[uclouvain(dot)be (that-symbol-for-emails) sergio(dot)castro\] (inversing the order).
Or just drop me a line if you want to say hello :-)
