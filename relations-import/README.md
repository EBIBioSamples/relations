

Must create your own application.properties based on src/main/resources/application.properties.example

Must create your own hibernate.properties based on src/main/resources/hibernate.properties.example

You can run this from source using:
mvn spring-boot:run -Drun.addResources

And with a subset of samples using
mvn spring-boot:run -Drun.addResources -Drun.arguments="--offsettotal=10,--offsetcount=5"

Once you package it (mvn package) you can run it as an executable jar - but you need to 
make sure that the hibernate.properties file is avaliable correctly and beware that if 
you use -jar then -cp won't work