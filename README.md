JConverter
==========

A Java library to modularize, categorize and apply conversions between arbitrary objects.

Why JConverter?
==============

Some months ago I was in need of a library that simplifies the task of bi-directional conversions between arbitrary Java objects and their JSON representation.
After a bit of research I found [Google's Gson](https://code.google.com/p/google-gson/ "Google's Gson") and decided that it was pretty cool and intuitive to use.

Some time after this finding, I needed this time a library facilitating custom conversions between arbitrary Java objects and their Prolog term representations. Unfortunately, nothing like that existed at the moment. However, I still had fresh in my mind the Gson library and its simple mechanism for applying Java-JSON conversions. So I re-implemented Gson for my scenario (Java - Prolog terms bi-directional conversions) and... it worked nicely! 
I presented it at an [ECOOP co-located workshop](http://wasdett.org/2013/ "WASDeTT") and the feedback I received was reasonably positive.

Nevertheless, I was not completely satisfied with my implementation. My library somehow duplicated the work of other programmers and, although it targeted another domain, it still remained, from a functional perspective, quite similar.
It seemed to me that Gson (and now my newly created library) made use of a new architectural pattern for converting between distinct representation of certain objects. The pattern seemed to work fine for at least two different domains, but no one had generalized it until the moment.

Therefore, JConverter is an effort on providing a generalization of this conversion pattern in a simple and intuitive framework.




Getting Started
===============

Pre-Defined Converters
----------------------

JConverter allows to register converters between different representations of Java objects.
To facilitate its usage, many converters between common Java types are, by default, pre-included in the library.
Therefore, to execute simple conversions the only steps required are:

- Instantiate a JConverter context,
- Obtain the object to convert,
- Define the target type of the conversion,
- Accomplish the conversion.


An intuitive example is presented in the code extract below:


    JConverter context = new JConverter(); //a default JConverter context
    List<Integer> source = asList(2,1,3); //a list of integers
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

Often the default converters pre-defined by JConverter are not sufficient. Consider the following class:
	
    class Person {
    	String name;
    	public Person(String name) {
    		this.name = name;
    	}
    }

The following code snippet shows how to register, in a JConverter context, a simple custom converter between a String and an instance of the Person class:
		
    JConverterBuilder builder = JConverterBuilder.create();
    builder.register(new Converter<String, Person>() {
    	@Override
    	public Person apply(String name, Type targetType, JConverter context) {
    		return new Person(name);
    	}
    });
    JConverter context = builder.build(); //a custom JConverter context

Let's define our source object (i.e., the object to convert) as a map. The keys of this map are person identifiers (e.g., passport numbers) and the values are person names, as in the snippet below:
    
    
    Map<String, String> map = new HashMap<String, String>() {{
    	put("1", "Sarah");
    	put("2", "Abraham");
    	put("3", "Isaac");
    }};
    
The following code converts this map to a new map having integers as its keys and instances of Person as its values:

    //target type is Map<Integer,Person>
    Type targetType = new TypeToken<Map<Integer,Person>>(){}.getType(); 
		
    Convertable convertable = new Convertable(map, context);
    Map<Integer, Person> convertedMap = convertable.as(targetType);
    //TESTING RESULT:
    assertEquals(3, convertedMap.entrySet().size());
    assertEquals("Sarah", convertedMap.get(1).name);
    assertEquals("Abraham", convertedMap.get(2).name);
    assertEquals("Isaac", convertedMap.get(3).name);


Behind the Curtains
====================

JConverter creates and manages a categorization of converters by means of the [JGum](https://github.com/jgum/jgum "JGum library") library.
Relying on the [type categorization mechanisms](http://jgum.github.com/tutorial/index.html "JGum tutorial") provided by JGum, the best converters for a given conversion operation are found and applied, forming an implicit chain of responsibility (this procedure will be documented in more detail in a future version of this guide).



License
=======
JConverter is open source, distributed under the terms of this [license](LICENSE.txt).


Try it!
=============

If you found JConverter interesting, you may want to take a look at the [API](http://jconverter.github.com/apidocs/ "API documentation ") documentation.

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
