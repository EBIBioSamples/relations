package uk.ac.ebi.biosamples.relations.importbiosdmodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class BioSDJDBCDAO {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private RowMapper<String> stringRowMapper = new RowMapper<String>() {
    	@Override
    	public String mapRow(ResultSet rs, int rowNum) throws SQLException {
    		return rs.getString(1);
    	}
    };
	
    public List<String> getPublicSamples() {
        log.debug("Fetching Public Sample Accessions . . .");
        String sql = "SELECT SA,COUNT(SA) AS NS\n" +
			 "  FROM (\n" +
			 "    SELECT b.ACC AS SA, b.PUBLIC_FLAG AS PF, b.RELEASE_DATE AS SRD, m.RELEASE_DATE AS MRD \n" +
			 "    FROM BIO_PRODUCT b LEFT JOIN MSI_SAMPLE ms ON b.ID = ms.SAMPLE_ID LEFT JOIN MSI m ON m.ID = ms.MSI_ID \n" +
			 "    WHERE (\n" +
			 "      (b.PUBLIC_FLAG IS NULL OR b.PUBLIC_FLAG = 1) AND \n" +
			 "      (b.RELEASE_DATE IS NULL OR b.RELEASE_DATE < CURRENT_DATE)\n" +
			 "    )\n" +
			 "  ) \n" +
			 "  WHERE\n" +
			 "    (SRD < CURRENT_DATE) OR\n" +
			 "    (SRD IS NULL AND MRD < CURRENT_DATE)\n" +
			 "  GROUP BY SA";
        
        return jdbcTemplate.query(sql, stringRowMapper);	
    }
    
    public List<String> getPublicGroups() {
        log.debug("Fetching Public Groups Accessions . . .");
        String sql = "  SELECT GA,COUNT(GA) AS NS\n" +
                "  FROM (\n" +
                "    SELECT bsg.ACC AS GA, bsg.PUBLIC_FLAG AS PF, bsg.RELEASE_DATE AS GRD, m.RELEASE_DATE AS MRD \n" +
                "    FROM BIO_SMP_GRP bsg LEFT JOIN MSI_SAMPLE_GROUP msg ON bsg.ID = msg.GROUP_ID LEFT JOIN MSI m ON msg.MSI_ID = m.ID \n" +
                "    WHERE (\n" +
                "      (bsg.PUBLIC_FLAG IS NULL OR bsg.PUBLIC_FLAG = 1) AND \n" +
                "      (bsg.RELEASE_DATE IS NULL OR bsg.RELEASE_DATE < CURRENT_DATE)\n" +
                "    )\n" +
                "  ) \n" +
                "  WHERE\n" +
                "    (GRD < CURRENT_DATE) OR\n" +
                "    (GRD IS NULL AND MRD < CURRENT_DATE)\n" +
                "  GROUP BY GA";
        
        return jdbcTemplate.query(sql, stringRowMapper);	
    }
    
    public List<String> getAllSubmissionIds() {
    	String sql = "SELECT ACC FROM MSI"; 
        return jdbcTemplate.query(sql, stringRowMapper);
    }


}