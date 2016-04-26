Before use you must create an application.properties file that points to a neo4j instance
There is an example file in src/main/resouces/application.properties.example

Note: you will need to pass -parameters to the compiler for some of the search end points 
to work. Maven will do this for you, but if using an IDE then you may need to configure it. 
For example, Eclipse Mars.1 (4.5.1) it is at:
Window -> preferences -> compiler -> "store information about method parameters"