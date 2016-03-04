package uk.ac.ebi.biosamples.relations.importbiosdmodel;

import java.util.Collections;
import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Component;

@Component
public class IndexCreator {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired 
	private Neo4jOperations neo4jTemplate;
	
    @PostConstruct
    public void createIndexes() {
    	log.info("Creating constraint indexs");
    	neo4jTemplate.query("CREATE CONSTRAINT ON (sample:Sample) ASSERT sample.accession IS UNIQUE", new HashMap<String,String>());
    	neo4jTemplate.query("CREATE CONSTRAINT ON (group:Group) ASSERT group.accession IS UNIQUE", null);
    	neo4jTemplate.query("CREATE CONSTRAINT ON (sub:Submission) ASSERT sub.submissionId IS UNIQUE", null);
    	
    	//cant create constraint that each node has to have a relationship of specific type?
    	//CREATE CONSTRAINT ON (sample:Sample) ASSERT exists((sample)-[OWNED_BY]-())
    }
}
