package uk.ac.ebi.biosamples.relations;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by tliener on 17/05/2016.
 */

@SpringBootApplication
@EnableTransactionManagement
@EnableNeo4jRepositories("uk.ac.ebi.biosamples.relations")
public class MyNeo4JConfig extends Neo4jConfiguration {

    @Value("${ogm.uri:}")
    private String uri;

    @Value("${ogm.driver:org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver}")
    private String driver;
    

    @Bean
    public org.neo4j.ogm.config.Configuration getConfiguration() {
    	if (uri != null && uri.trim().length() == 0) {
    		uri = null;
    	} else {
    		uri = uri.trim();
    	}
    	
        org.neo4j.ogm.config.Configuration config = new org.neo4j.ogm.config.Configuration();
        config
                .driverConfiguration()
                .setDriverClassName(driver.trim())
                .setURI(uri);
        return config;
    }


    @Override
    @Bean
    public SessionFactory getSessionFactory() {
        // name of package containing domain objects here
        return new SessionFactory(getConfiguration(), "uk.ac.ebi.biosamples.relations.model");
    }

    // needed for session in view in web-applications
    @Bean
    @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Session getSession() throws Exception {
        return super.getSession();
    }
}