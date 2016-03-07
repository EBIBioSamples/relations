package uk.ac.ebi.biosamples.relations.importbiosdmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Runner implements ApplicationRunner {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${neo4jIndexer.threadCount:0}")
	private int threadCount;
	
	@Value("${neo4jIndexer.fetchStep:10}")
	private int fetchStep;

	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private BioSDDAO biosdDAO;

	private List<String> msiAccs; 
	private ExecutorService threadPool = null;
	private List<Future<Void>> futures = new ArrayList<>();
	
	private int offsetCount = 0;
	private int offsetTotal = -1;

	public void run(ApplicationArguments args) throws Exception {
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
		log.info("Getting MSI accessions");
		if (args.getNonOptionArgs().size() > 0) {
			//read MSI names from command line
			msiAccs = Collections.unmodifiableList(args.getNonOptionArgs());
		} else {
			//get MSI names from DB
			if (offsetTotal > 0) {
				int count = biosdDAO.getGroupCount();
				int offsetSize = count/offsetTotal;
				int start = offsetSize*offsetCount;
				log.info("Getting MSI accessions for chunk "+offsetCount+" of "+offsetTotal);
				msiAccs =  Collections.unmodifiableList(biosdDAO.getMSIAccessions(start, offsetSize));
		        log.info("got "+msiAccs.size()+" MSIs");
			} else {
				msiAccs =  Collections.unmodifiableList(biosdDAO.getMSIAccessions());
			}
		}
        log.info("got "+msiAccs.size()+" MSIs");

        //create the thread stuff if required
		try {
			if (threadCount > 0) {
				threadPool = Executors.newFixedThreadPool(threadCount);
			}	
			
	        for (int i = 0; i < msiAccs.size(); i += fetchStep) {
	        	//have to create multiple beans via context so they all have their own dao object
	        	//this is apparently bad Inversion Of Control but I can't see a better way to do it
	        	CallableMSI callable = context.getBean(CallableMSI.class);
	        	
	        	callable.setAccessions(msiAccs.subList(i, Math.min(i+fetchStep, msiAccs.size())));
	        	
				if (threadCount == 0) {
					callable.call();
				} else {
					futures.add(threadPool.submit(callable));
				}
	        }

	        //wait for all other futures to finish
	        log.info("Waiting for futures...");
			for (Future<Void> future : futures) {
				try {
					future.get();
				} catch (ExecutionException e) {
					log.error("Problem getting future", e);
				}
			}
			
			//close down thread pool
			if (threadPool != null) {
		        log.info("Shutting down thread pool");
		        threadPool.shutdown();
		        //one day is a lot, but better safe than sorry!
				threadPool.awaitTermination(1, TimeUnit.DAYS);
			}		
		} finally {
			//handle closing of thread pool in case of error
			if (threadPool != null && !threadPool.isShutdown()) {
		        log.info("Shutting down thread pool");
		        //allow a second to cleanly terminate before forcing
		        threadPool.shutdown();
				threadPool.awaitTermination(1, TimeUnit.SECONDS);
				threadPool.shutdownNow();
			}
		}
		
		log.info("Processed "+msiAccs.size()+" in "+(System.currentTimeMillis() - startTime)/1000+"s");
		return;
		
	}
}
