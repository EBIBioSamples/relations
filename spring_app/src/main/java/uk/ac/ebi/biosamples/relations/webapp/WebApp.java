package uk.ac.ebi.biosamples.relations.webapp;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by tliener on 20/04/2016.
 */


@SpringBootApplication
@EnableTransactionManagement
@EnableNeo4jRepositories("uk.ac.ebi.biosamples.relations")
public class WebApp  extends Neo4jConfiguration {


    @Override
    @Bean
    public SessionFactory getSessionFactory() {
        // name of package containing domain objects here
        return new SessionFactory("uk.ac.ebi.biosamples.relations.model");
    }


    @Override
    @Bean
    public Session getSession() throws Exception {
        return super.getSession();
    }


    public static void main(String[] args) throws Exception {

        SpringApplication.run(WebApp.class, args);

    }


}
