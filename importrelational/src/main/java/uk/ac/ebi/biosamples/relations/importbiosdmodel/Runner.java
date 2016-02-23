package uk.ac.ebi.biosamples.relations.importbiosdmodel;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import uk.ac.ebi.biosamples.relations.model.edges.OwnedBy;
import uk.ac.ebi.biosamples.relations.model.nodes.Group;
import uk.ac.ebi.biosamples.relations.model.nodes.Submission;
import uk.ac.ebi.fg.biosd.model.organizational.BioSampleGroup;
import uk.ac.ebi.fg.biosd.model.organizational.MSI;

@Component
public class Runner implements ApplicationRunner {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BioSDJDBCDAO biosdJDBCDAO;

	// @Autowired
	// can't autowire this for some reason?
	private RestTemplate rest = new RestTemplate();

	@Autowired
	private BioSDModelDAO biosdModelDao;

	private String uri = "http://localhost:8080";

	public void run(ApplicationArguments args) {
		log.info("Running...");

		for (String acc : biosdJDBCDAO.getPublicGroups()) {
			try {
				handleGroup(acc);
			} catch (RunnerException e) {
				log.error(e.getMessage());
				// continue anyway
			}
		}
	}

	private void handleGroup(String acc) throws RunnerException {

		log.info(acc);
		// using spring hibernate doensn't work due to lazy loading

		BioSampleGroup groupModel = biosdModelDao.getGroup(acc);

		if (groupModel.getMSIs().size() != 1) {
			throw new RunnerException("Group " + acc + " has multiple MSIs (" + groupModel.getMSIs().size() + ")");
		}
		MSI msi = groupModel.getMSIs().iterator().next();

		Group groupNeo = new Group(acc);
		Submission submissionNeo = new Submission(msi.getAcc());
		groupNeo.setOwnedBy(new OwnedBy(groupNeo, submissionNeo));
/*
		ResponseEntity<Group> response = rest.postForEntity("http://localhost:8080/groups", groupNeo, Group.class);
		log.info("" + response);
		if (response.getStatusCode() != HttpStatus.OK) {
			throw new RunnerException("" + response);
		}
*/
		
		HttpEntity<Group> httpEntity = new HttpEntity<>(groupNeo);
		httpEntity.getHeaders().setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<Resource<Group>> responseEntity = rest.exchange("http://localhost:8080/groups",
				HttpMethod.POST, httpEntity, new ParameterizedTypeReference<Resource<Group>>() {});
		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			Resource<Group> userResource = responseEntity.getBody();
			Group user = userResource.getContent();
		}
	}

	private class RunnerException extends Exception {

		private static final long serialVersionUID = 3813773101351987425L;

		public RunnerException(String string) {
			super(string);
		}

	}

}
