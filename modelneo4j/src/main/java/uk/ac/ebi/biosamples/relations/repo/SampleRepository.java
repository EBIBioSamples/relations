package uk.ac.ebi.biosamples.relations.repo;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import uk.ac.ebi.biosamples.relations.model.nodes.Sample;

@Repository
public interface SampleRepository extends GraphRepository<Sample> {

	public Sample findOneByAccession(String accession);
	
	@Query("MATCH (sample:Sample) WHERE (sample)-[:OWNERSHIP]->(:Submission {submissionId:{submissionId}}) RETURN sample")
	public Iterable<Sample> findSamplesOwnedBySubmissionBySubmissionId(String submissionId);
}
