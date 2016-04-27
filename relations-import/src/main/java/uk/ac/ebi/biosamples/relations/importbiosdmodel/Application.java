package uk.ac.ebi.biosamples.relations.importbiosdmodel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@SpringBootApplication
public class Application {	
	
	//this is needed to read nonstrings from properties files
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigIn() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

}
