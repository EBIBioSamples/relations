package uk.ac.ebi.biosamples.relations.importbiosdmodel;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import uk.ac.ebi.biosamples.relations.model.nodes.Submission;
import uk.ac.ebi.biosamples.relations.repo.SubmissionRepository;
import uk.ac.ebi.fg.biosd.model.organizational.MSI;

@Component
public class Runner implements ApplicationRunner {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	
	@Autowired
	private BioSDDAO biosdDAO;
	
	@Autowired
	private BioSDToNeo4JMappingService biosdToNeo4J;

	private List<String> msiAccs; 
	
	private int offsetCount = 0;
	private int offsetTotal = -1;

	public void run(ApplicationArguments args) {
		log.info("Running...");

		long startTime = System.currentTimeMillis();
		

        //process arguments
		if (args.containsOption("offsetcount")) {
			//subtract one so we use 1 to Total externally and 0 to (Total-1) internally
			//better human readable and LSF compatibility
			offsetCount = Integer.parseInt(args.getOptionValues("offsetcount").get(0))-1;
		}
		if (args.containsOption("offsettotal")) {
			offsetTotal = Integer.parseInt(args.getOptionValues("offsettotal").get(0));
		}

		if (offsetTotal > 0) {
			int count = biosdDAO.getGroupCount();
			int offsetSize = count/offsetTotal;
			int start = offsetSize*offsetCount;
			log.info("Getting MSI accessions for chunk "+offsetCount+" of "+offsetTotal);
			msiAccs = biosdDAO.getMSIAccessions(start, offsetSize);
	        log.info("got "+msiAccs.size()+" MSIs");
		} else {
			log.info("Getting MSI accessions");
			msiAccs = biosdDAO.getMSIAccessions();
	        log.info("got "+msiAccs.size()+" MSIs");
		}
		
		for (String msiAcc : msiAccs) {
			log.info("processing "+msiAcc);
			Submission sub = biosdToNeo4J.handle(msiAcc);
		}
		
		log.info("Processed "+msiAccs.size()+" in "+(System.currentTimeMillis() - startTime)/1000+"s");
		return;
		
	}
}
