package uk.ac.ebi.biosamples.relations.webapp;

import java.util.Collections;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Component;

import uk.ac.ebi.biosamples.relations.model.edges.Membership;
import uk.ac.ebi.biosamples.relations.model.edges.Ownership;
import uk.ac.ebi.biosamples.relations.model.nodes.Group;
import uk.ac.ebi.biosamples.relations.model.nodes.Sample;
import uk.ac.ebi.biosamples.relations.model.nodes.Submission;
import uk.ac.ebi.biosamples.relations.repo.GroupRepository;
import uk.ac.ebi.biosamples.relations.repo.SampleRepository;
import uk.ac.ebi.biosamples.relations.repo.SubmissionRepository;

@Component
public class DummyPopulator {


	@Autowired 
	private SubmissionRepository submissionRepository;
	
	@Autowired 
	private SampleRepository sampleRepository;
	
	@Autowired 
	private GroupRepository groupRepository;
	
	
	@Autowired 
	private Neo4jOperations neo4jTemplate;

	
    @PostConstruct
    public void populateOnStart() {
    	//clean up any nodes or edges from previous sessions
    	//woulnd't want to do this in production...
    	neo4jTemplate.query("MATCH (n) DETACH DELETE n", Collections.emptyMap());
    	
    	Submission sub = new Submission();
    	sub.setSubmissionId("GSB-TEST");
    	submissionRepository.save(sub);    	   	
    	
    	Sample s1 = new Sample();
    	s1.setAccession("SAMETST1");
    	s1.setOwner(sub);
    	sampleRepository.save(s1, 1);
    	Sample s2 = new Sample();
    	s2.setAccession("SAMETST2");
    	s2.setOwner(sub);
    	sampleRepository.save(s2, 1);
    	
    	Group g1 = new Group();
    	g1.setAccession("SAMEGTST1");
    	g1.setOwner(sub);
    	groupRepository.save(g1, 1);
    	
    	Membership s1g1 = new Membership(s1, g1);
    	g1.addMembership(s1g1);
    	Membership s2g1 = new Membership(s2, g1);
    	g1.addMembership(s2g1);
    	groupRepository.save(g1, 1);
    	
    	
    	//some quick tests
    	submissionRepository.findOneBySubmissionId("GSB-TEST");
    	sampleRepository.findOneByAccession("SAMETST1");
    	groupRepository.findOneByAccession("SAMEGTST2");
    }

}
