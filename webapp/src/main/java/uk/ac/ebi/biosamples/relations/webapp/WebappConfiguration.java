package uk.ac.ebi.biosamples.relations.webapp;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.server.Neo4jServer;
import org.springframework.data.neo4j.server.RemoteServer;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
// name of package containing repositories here
@EnableNeo4jRepositories("uk.ac.ebi.biosamples.relations.webapp.repo")
@EnableTransactionManagement
public class WebappConfiguration extends Neo4jConfiguration {

	@Autowired
	private Environment env;

	@Override
	@Bean
	public SessionFactory getSessionFactory() {
		// name of package containing domain objects here
		return new SessionFactory("uk.ac.ebi.biosamples.relations.model");
	}

	@Override
	@Bean
	public Neo4jServer neo4jServer() {
		return new RemoteServer(env.getProperty("neo4j.url"));
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

	public static void main(String[] args) throws Exception {
		SpringApplication.run(WebappConfiguration.class, args);
	}

}
