package uk.ac.ebi.biosamples.relations.webapp;

import java.util.Collections;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Component;

import uk.ac.ebi.biosamples.relations.model.edges.MemberOf;
import uk.ac.ebi.biosamples.relations.model.edges.OwnedBy;
import uk.ac.ebi.biosamples.relations.model.nodes.Group;
import uk.ac.ebi.biosamples.relations.model.nodes.Sample;
import uk.ac.ebi.biosamples.relations.model.nodes.Submission;
import uk.ac.ebi.biosamples.relations.webapp.repo.GroupRepository;
import uk.ac.ebi.biosamples.relations.webapp.repo.SampleRepository;
import uk.ac.ebi.biosamples.relations.webapp.repo.SubmissionRepository;

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
    	
    	Submission sub = new Submission("GSB-TEST");
    	submissionRepository.save(sub);    	   	
    	
    	Sample s1 = new Sample("SAMETST1", sub);
    	sampleRepository.save(s1);
    	Sample s2 = new Sample("SAMETST2", sub);
    	sampleRepository.save(s2);
    	
    	Group g1 = new Group("SAMEGTST2", sub);
    	groupRepository.save(g1);
    	
    	MemberOf s1g1 = new MemberOf(s1, g1);
    	g1.addMemberOf(s1g1);
    	MemberOf s2g1 = new MemberOf(s2, g1);
    	g1.addMemberOf(s2g1);
    	groupRepository.save(g1);
    	
    	
    	//some quick tests
    	submissionRepository.findOneBySubmissionId("GSB-TEST");
    	sampleRepository.findOneByAccession("SAMETST1");
    	groupRepository.findOneByAccession("SAMEGTST2");
    }

}
