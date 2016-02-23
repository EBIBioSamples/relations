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
    	
    	Sample s1 = new Sample("SAMETST1");
    	sampleRepository.save(s1);
    	Sample s2 = new Sample("SAMETST2");
    	sampleRepository.save(s2);
    	
    	Group g1 = new Group("SAMEGTST2");
    	groupRepository.save(g1);
    	
    	OwnedBy s1own = new OwnedBy(s1, sub);
    	s1.setOwnedBy(s1own);
//    	ownedByRepository.save(s1own);
    	OwnedBy s2own = new OwnedBy(s2, sub);
    	s2.setOwnedBy(s2own);
//    	ownedByRepository.save(s2own);
    	OwnedBy g1own = new OwnedBy(g1, sub);
    	g1.setOwnedBy(g1own);
//    	ownedByRepository.save(g1own);
    	groupRepository.save(g1);
    	
    	MemberOf s1g1 = new MemberOf(s1, g1);
    	g1.addMemberOf(s1g1);
//    	memberOfRepository.save(s1g1);
    	MemberOf s2g1 = new MemberOf(s2, g1);
    	g1.addMemberOf(s2g1);
//    	memberOfRepository.save(s2g1);
    	groupRepository.save(g1);
    	
    	
    	//some quick tests
    	submissionRepository.findOneBySubmissionId("GSB-TEST");
    	sampleRepository.findOneByAccession("SAMETST1");
    	groupRepository.findOneByAccession("SAMEGTST2");
    }

}
