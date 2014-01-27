JConverter
==========

A Java library to modularize, categorize and apply conversions between arbitrary objects.

Why JConverter?
--------------

Some months ago I was in need of a library that simplifies the task of bi-directional conversions between arbitrary Java objects and their JSON representation.
After a bit of research I found [Google's Gson](https://code.google.com/p/google-gson/ "Google's Gson") and decided that it was pretty cool and intuitive to use.

Some time after this finding, I needed this time [a library facilitating custom conversions between arbitrary Java objects and their Prolog term representations](https://github.com/java-prolog-connectivity/jpc/ "JPC"). Unfortunately, nothing like that existed at the moment. However, I still had fresh in my mind the Gson library and its simple mechanism for applying Java-JSON conversions. So I re-implemented Gson for my scenario (Java - Prolog terms bi-directional conversions) and... it worked nicely! 
I presented it at an [ECOOP co-located workshop](http://wasdett.org/2013/ "WASDeTT") and the feedback I received was reasonably positive.

Nevertheless, I was not completely satisfied with my implementation. My library somehow duplicated the work of other programmers and, although it targeted another domain, it still remained, from a functional perspective, quite similar.
It seemed to me that Gson (and now my newly created library) made use of a new architectural pattern for converting between distinct representations of certain objects. The pattern seemed to work fine for at least two different domains related to inter-language conversions, but no one had generalized it until the moment.

Therefore, JConverter is an effort on providing a generalization of this conversion pattern in a simple and intuitive framework.


Where to go from here?
---------------------

In case you would like to know more about JConverter, you may find interesting this short [tutorial](http://jconverter.github.com/tutorial/index.html "JConverter Tutorial") 
and the [API](http://jconverter.github.com/apidocs/ "API documentation ") documentation.


License
-------

JConverter is open source, distributed under the terms of this [license](LICENSE.txt).


Contact
-------

Questions, wish lists and constructive feedback can be sent to [uclouvain(dot)be (that symbol for emails) sergio(dot)castro]  \(inversing the order\).
