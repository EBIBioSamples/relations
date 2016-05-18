package uk.ac.ebi.biosamples.relations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

	public static void main(String[] args) throws Exception {
		//SpringApplication.run(new Class[] { MyWebAppConfig.class, MyNeo4JConfig.class }, args);
		SpringApplication.run(Main.class, args);
	}
}
