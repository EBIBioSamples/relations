package uk.ac.ebi.biosamples.relations.webapp;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.neo4j.config.Neo4jConfiguration;

/**
 * Created by tliener on 17/05/2016.
 */

@Configuration
public class MyNeo4JConfig extends Neo4jConfiguration{

    @Value("${ogm.uri:}")
    private String uri;

    @Value("${ogm.driver:error}")
    private String driver;


    //****** Try to get rid of ogm.properties by defining neo4 driver within java
    @Bean
    public org.neo4j.ogm.config.Configuration getConfiguration() {
    	if (uri != null && uri.length() == 0) uri = null;
    	
        org.neo4j.ogm.config.Configuration config = new org.neo4j.ogm.config.Configuration();
        config
                .driverConfiguration()
                .setDriverClassName(driver.trim())
                .setURI(uri.trim());
        return config;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigIn() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    //****** End experimental part


    @Override
    @Bean
    public SessionFactory getSessionFactory() {
        // name of package containing domain objects here
        return new SessionFactory(getConfiguration(), "uk.ac.ebi.biosamples.relations.model");
    }
}
