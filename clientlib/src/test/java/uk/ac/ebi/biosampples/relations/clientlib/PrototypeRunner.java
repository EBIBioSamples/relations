package uk.ac.ebi.biosampples.relations.clientlib;

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
import uk.ac.ebi.biosamples.relations.clientlib.rao.GroupRestAccessObject;
import uk.ac.ebi.biosamples.relations.clientlib.rao.SubmissionRestAccessObject;
import uk.ac.ebi.biosamples.relations.model.edges.Membership;
import uk.ac.ebi.biosamples.relations.model.edges.Ownership;
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
    	Sample s1 = new Sample("SAMETST3", sub);
    	Sample s2 = new Sample("SAMETST4", sub);
    	Group g1 = new Group("SAMEGTST3", sub);
    	
    	g1.addMembership(new Membership(s1, g1));
    	g1.addMembership(new Membership(s2, g1));

    	
		//test out the other class
		SubmissionRestAccessObject subao = new SubmissionRestAccessObject("http://localhost:8080");
		
		GroupRestAccessObject gao = new GroupRestAccessObject("http://localhost:8080");
		//link the access objects for nested queries
		gao.setSubRAO(subao);
		subao.setGroupRAO(gao);
		//share a rest template for performance
		//is multi-threaded once constructed
		gao.setRestTemplate(subao.getRestTemplate());
		
		log.info(""+subao.getSubmission("GSB-TEST"));
		
		try {
			subao.persistNovelSubmission(sub);
		} catch (DuplicateSubmissionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			subao.updateSubmission(sub);
		} catch (NoSuchSubmissionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		log.info(""+gao.getGroup("SAMEGTST2").get().getContent().getAccession());
		log.info(""+gao.getGroupWithNeighbours("SAMEGTST2").get().getContent().getAccession());
		for (Group group: gao.getGroupsOwnedBySubmission("GSB-TEST").getContent()) {
			log.info("foo"+group.getAccession());
		}
		
		
		log.info(""+subao.getSubmission("GSB-TEST2"));
		
		log.info("Complete");
	}
}
