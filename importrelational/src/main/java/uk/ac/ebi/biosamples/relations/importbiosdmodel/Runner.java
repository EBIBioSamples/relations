package uk.ac.ebi.biosamples.relations.importbiosdmodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import uk.ac.ebi.biosamples.relations.clientlib.exceptions.DuplicateSubmissionException;
import uk.ac.ebi.biosamples.relations.clientlib.rao.SubmissionRestAccessObject;
import uk.ac.ebi.biosamples.relations.model.nodes.Submission;
import uk.ac.ebi.fg.biosd.model.organizational.MSI;

@Component
public class Runner implements ApplicationRunner {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BioSDJDBCDAO biosdJDBCDAO;

	@Autowired
	private BioSDModelDAO biosdModelDao;

	public void run(ApplicationArguments args) {
		log.info("Running...");
		
		SubmissionRestAccessObject subRAO = new SubmissionRestAccessObject("http://localhost:8080");
		
		for (String subid : biosdJDBCDAO.getAllSubmissionIds()) {
			MSI msi = biosdModelDao.getSubmission(subid);
			
			Submission sub = new Submission(subid);
			
			try {
				subRAO.persistNovelSubmission(sub);
			} catch (DuplicateSubmissionException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
