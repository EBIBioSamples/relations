package uk.ac.ebi.biosamples.relations.repo;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

import uk.ac.ebi.biosamples.relations.model.Sample;

/**
 * Created by tliener on 20/04/2016.
 *
 */


public interface SampleRepository extends GraphRepository<Sample> {


	public Sample findOneByAccession(String accession);

	@Query("MATCH (sample:Sample) WHERE (sample)-[:OWNERSHIP]->(:Submission {submissionId:{submissionId}}) RETURN sample")
	public Iterable<Sample> findSamplesOwnedBySubmissionBySubmissionId(@Param("submissionId") String submissionId);
	
}