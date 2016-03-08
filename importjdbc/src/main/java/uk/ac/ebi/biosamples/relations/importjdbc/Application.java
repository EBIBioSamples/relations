package uk.ac.ebi.biosamples.relations.importjdbc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

@SpringBootApplication
public class Application implements ApplicationRunner {

	public static void main(String args[]) {
		SpringApplication.run(Application.class, args);
	}

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private JdbcTemplate jdbcTemplate;

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

	public void run(ApplicationArguments args) throws Exception {

		try (final CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(new File("samples.csv")),
				CSVFormat.DEFAULT.withHeader("accesion:ID(Sample)"))) {
			log.info("Running samples query");
			String sql = "SELECT ACC FROM BIO_PRODUCT";
			jdbcTemplate.query(sql, new CSVCallbackHandler(csvPrinter));
		}

		try (final CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(new File("groups.csv")),
				CSVFormat.DEFAULT.withHeader("accesion:ID(Group)"))) {
			log.info("Running groups query");
			String sql = "SELECT ACC FROM BIO_SMP_GRP";
			jdbcTemplate.query(sql, new CSVCallbackHandler(csvPrinter));
		}

		try (final CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(new File("submissions.csv")),
				CSVFormat.DEFAULT.withHeader("submissionId:ID(Submission)"))) {
			log.info("Running submissions query");
			String sql = "SELECT ACC FROM MSI";
			jdbcTemplate.query(sql, new CSVCallbackHandler(csvPrinter));
		}

		try (final CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(new File("group_ownership.csv")),
				CSVFormat.DEFAULT.withHeader(":START_ID(Group)", ":END_ID(Submission)"))) {
			log.info("Running group ownership query");
			String sql = "SELECT BIO_SMP_GRP.ACC, MSI.ACC FROM BIO_SMP_GRP JOIN MSI_SAMPLE_GROUP ON BIO_SMP_GRP.ID = MSI_SAMPLE_GROUP.GROUP_ID JOIN MSI ON MSI.ID = MSI_SAMPLE_GROUP.MSI_ID";
			jdbcTemplate.query(sql, new CSVCallbackHandler(csvPrinter));
		}

		try (final CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(new File("sample_ownership.csv")),
				CSVFormat.DEFAULT.withHeader(":START_ID(Sample)", ":END_ID(Submission)"))) {
			log.info("Running sample ownership query");
			String sql = "SELECT BIO_PRODUCT.ACC, MSI.ACC FROM BIO_PRODUCT JOIN MSI_SAMPLE ON BIO_PRODUCT.ID = MSI_SAMPLE.SAMPLE_ID JOIN MSI ON MSI.ID = MSI_SAMPLE.MSI_ID";
			jdbcTemplate.query(sql, new CSVCallbackHandler(csvPrinter));
		}

		try (final CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(new File("sample_membership.csv")),
				CSVFormat.DEFAULT.withHeader(":START_ID(Sample)", ":END_ID(Group)"))) {
			log.info("Running sample membership query");
			String sql = "SELECT BIO_SMP_GRP.ACC, BIO_PRODUCT.ACC FROM BIO_SMP_GRP JOIN BIO_SAMPLE_SAMPLE_GROUP ON BIO_SMP_GRP.ID = BIO_SAMPLE_SAMPLE_GROUP.GROUP_ID JOIN BIO_PRODUCT ON BIO_PRODUCT.ID = BIO_SAMPLE_SAMPLE_GROUP.SAMPLE_ID";
			jdbcTemplate.query(sql, new CSVCallbackHandler(csvPrinter));
		}

	}
}