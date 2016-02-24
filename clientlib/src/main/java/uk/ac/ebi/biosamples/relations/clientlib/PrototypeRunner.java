package uk.ac.ebi.biosamples.relations.clientlib;

import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import uk.ac.ebi.biosamples.relations.clientlib.exceptions.DuplicateSubmissionException;
import uk.ac.ebi.biosamples.relations.clientlib.exceptions.NoSuchSubmissionException;
import uk.ac.ebi.biosamples.relations.model.edges.MemberOf;
import uk.ac.ebi.biosamples.relations.model.edges.OwnedBy;
import uk.ac.ebi.biosamples.relations.model.nodes.Group;
import uk.ac.ebi.biosamples.relations.model.nodes.Sample;
import uk.ac.ebi.biosamples.relations.model.nodes.Submission;

@Component
public class PrototypeRunner implements ApplicationRunner {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	// @Autowired
	// can't autowire this for some reason?
	private RestTemplate rest = new RestTemplate();


	public void run(ApplicationArguments args) {
		log.info("Running...");

		//create some objects hardcoded
    	
    	Submission sub = new Submission("GSB-TEST2");   	
    	Sample s1 = new Sample("SAMETST3");
    	Sample s2 = new Sample("SAMETST4");
    	Group g1 = new Group("SAMEGTST3");
    	
    	s1.setOwnedBy(new OwnedBy(s1, sub));
    	s2.setOwnedBy(new OwnedBy(s2, sub));
    	g1.setOwnedBy(new OwnedBy(g1, sub));
    	
    	g1.addMemberOf(new MemberOf(s1, g1));
    	g1.addMemberOf(new MemberOf(s2, g1));

    	
		//test out the other class
		RestAccessObject rao = new RestAccessObject("http://localhost:8080");
		
		log.info(""+rao.getSubmission("GSB-TEST"));
		
		try {
			rao.persistNovelSubmission(sub);
		} catch (DuplicateSubmissionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			rao.updateSubmission(sub);
		} catch (NoSuchSubmissionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		log.info(""+rao.getSubmission("GSB-TEST2"));
		
		log.info("Complete");
	}
}
