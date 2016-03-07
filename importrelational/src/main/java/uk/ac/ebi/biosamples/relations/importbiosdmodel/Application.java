package uk.ac.ebi.biosamples.relations.importbiosdmodel;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.server.Neo4jServer;
import org.springframework.data.neo4j.server.RemoteServer;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
// name of package containing repositories here
@EnableNeo4jRepositories("uk.ac.ebi.biosamples.relations.repo")
@EnableTransactionManagement
//turn on advanced proxy object creation so the multi-threading and repositories work
@EnableAspectJAutoProxy(proxyTargetClass = true) 
@PropertySource("ogm.properties")
public class Application extends Neo4jConfiguration {

	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Value("${URI}")
	private String uri;
	@Value("${username}")
	private String username;
	@Value("${password}")
	private String password;
	
	//this is needed to read nonstrings from properties files
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigIn() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
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

	@Bean
	public Neo4jOperations getNeo4jTemplate() throws Exception {
		return new Neo4jTemplate(getSession());
	}
	
	//This will not be needed after spring-data-neo4j v4.0.0
	@Bean
    public Neo4jServer neo4jServer() {
		log.info("uri "+uri);
		log.info("username "+username);
		log.info("password "+password);
        return new RemoteServer(uri, username, password);
    }
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

}
