package uk.ac.ebi.biosamples.relations.importbiosdmodel;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
// name of package containing repositories here
@EnableNeo4jRepositories("uk.ac.ebi.biosamples.relations.repo")
@EnableTransactionManagement
public class Application extends Neo4jConfiguration {

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

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

}
