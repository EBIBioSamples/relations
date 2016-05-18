package uk.ac.ebi.biosamples.relations;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
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

@Configuration
@EnableTransactionManagement
@EnableNeo4jRepositories("uk.ac.ebi.biosamples.relations.repo")
public class MyNeo4JConfig extends Neo4jConfiguration {

	@Value("${ogm.uri:}")
	private String uri;

	@Value("${ogm.driver:error}")
	private String driver;

	// ****** Try to get rid of ogm.properties by defining neo4 driver within
	// java
	@Bean
	public org.neo4j.ogm.config.Configuration getConfiguration() {
		if (uri != null && uri.length() == 0)
			uri = null;

		org.neo4j.ogm.config.Configuration config = new org.neo4j.ogm.config.Configuration();
		config.driverConfiguration().setDriverClassName(driver.trim()).setURI(uri.trim());
		return config;
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigIn() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	// ****** End experimental part

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
