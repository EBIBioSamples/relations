package uk.ac.ebi.biosamples.relations.importjdbc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
public class Application implements ApplicationRunner {

	public static void main(String args[]) {
		SpringApplication.run(Application.class, args);
	}

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private List<Future<Void>> futures = new ArrayList<>();

	private static class CSVCallbackHandler implements RowCallbackHandler {
		private final Logger log = LoggerFactory.getLogger(this.getClass());
		private final CSVPrinter csvPrinter;

		public CSVCallbackHandler(CSVPrinter csvPrinter) {
			this.csvPrinter = csvPrinter;
		}

		@Override
		public void processRow(ResultSet rs) throws SQLException {
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			try {
				for (int i = 0; i < columnCount; i++) {
					csvPrinter.print(rs.getString(i + 1));
				}
				csvPrinter.println();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	
	private final class FileCallable implements Callable<Void> {

		private final String filename;
		private final String[] headers;
		private final String sql;
		
		public FileCallable(String filename, String[] headers, String sql) {
			this.filename = filename;
			this.headers = headers;
			this.sql = sql;
		}
		
		@Override
		public Void call() throws Exception {
			try (final CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(new File(filename)),
					CSVFormat.DEFAULT.withHeader(headers))) {
				jdbcTemplate.query(sql, new CSVCallbackHandler(csvPrinter));
			}
			return null;
		}
		
	}

	@Transactional
	public void run(ApplicationArguments args) throws Exception {

		ExecutorService threadPool = null; 
		try {
			threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			String sql;
			
			sql = "SELECT ACC FROM BIO_PRODUCT";
			futures.add(threadPool.submit(new FileCallable("samples.csv", new String[]{"accession:ID(Sample)"}, sql)));
			
			sql = "SELECT ACC FROM BIO_SMP_GRP";
			futures.add(threadPool.submit(new FileCallable("groups.csv", new String[]{"accession:ID(Group)"}, sql)));
			
			sql = "SELECT ACC FROM MSI";
			futures.add(threadPool.submit(new FileCallable("submissions.csv", new String[]{"submissionId:ID(Submission)"}, sql)));
			
			sql = "SELECT BIO_SMP_GRP.ACC, MSI.ACC FROM BIO_SMP_GRP JOIN MSI_SAMPLE_GROUP ON BIO_SMP_GRP.ID = MSI_SAMPLE_GROUP.GROUP_ID JOIN MSI ON MSI.ID = MSI_SAMPLE_GROUP.MSI_ID";
			futures.add(threadPool.submit(new FileCallable("group_ownership.csv", new String[]{":START_ID(Group)", ":END_ID(Submission)"}, sql)));
			
			sql = "SELECT BIO_PRODUCT.ACC, MSI.ACC FROM BIO_PRODUCT JOIN MSI_SAMPLE ON BIO_PRODUCT.ID = MSI_SAMPLE.SAMPLE_ID JOIN MSI ON MSI.ID = MSI_SAMPLE.MSI_ID";
			futures.add(threadPool.submit(new FileCallable("sample_ownership.csv", new String[]{":START_ID(Sample)", ":END_ID(Submission)"}, sql)));
			
			sql = "SELECT BIO_PRODUCT.ACC, BIO_SMP_GRP.ACC FROM BIO_SMP_GRP JOIN BIO_SAMPLE_SAMPLE_GROUP ON BIO_SMP_GRP.ID = BIO_SAMPLE_SAMPLE_GROUP.GROUP_ID JOIN BIO_PRODUCT ON BIO_PRODUCT.ID = BIO_SAMPLE_SAMPLE_GROUP.SAMPLE_ID";
			futures.add(threadPool.submit(new FileCallable("sample_membership.csv", new String[]{":START_ID(Sample)", ":END_ID(Group)"}, sql)));
			
			sql = "SELECT b.ACC, b2.ACC FROM BIO_PRODUCT b JOIN PRODUCT_PV pv ON b.ID = pv.OWNER_ID JOIN EXP_PROP_VAL epv ON pv.PV_ID = epv.ID JOIN EXP_PROP_TYPE ept ON epv.TYPE_ID = ept.ID JOIN BIO_PRODUCT b2 ON epv.TERM_TEXT LIKE b2.ACC WHERE REGEXP_LIKE(ept.TERM_TEXT, 'Derived From', 'i')";
			futures.add(threadPool.submit(new FileCallable("sample_derivation.csv", new String[]{":START_ID(Sample)", ":END_ID(Sample)"}, sql)));

	        //wait for all other futures to finish
	        log.info("Waiting for futures...");
			for (Future<Void> future : futures) {
				try {
					future.get();
				} catch (ExecutionException e) {
					log.error("Problem getting future", e);
				}
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
	}
}