package uk.ac.ebi.biosamples.relations.webapp;

import org.springframework.boot.SpringApplication;

public class Main {

    
    public static void main(String[] args) throws Exception {
        SpringApplication.run(new Class[]{MyWebAppConfig.class, MyNeo4JConfig.class}, args);
    }
}
