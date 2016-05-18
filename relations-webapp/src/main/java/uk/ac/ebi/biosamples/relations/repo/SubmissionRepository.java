package uk.ac.ebi.biosamples.relations.repo;

import org.springframework.data.neo4j.repository.GraphRepository;
import uk.ac.ebi.biosamples.relations.model.Submission;

/**
 * Created by tliener on 20/04/2016.
 *
 */


public interface SubmissionRepository extends GraphRepository<Submission> {
/*
	public Submission findOneBySubmissionId(String submissionId);

	@Query("MATCH (sub:Submission) WHERE (sub)<-[:OWNERSHIP]-(:Group {accession:{accession}}) RETURN sub")
	public Submission findSubmissionOwningGroupByAccession(String accession);

	@Query("MATCH (sub:Submission) WHERE (sub)<-[:OWNERSHIP]-(:Sample {accession:{accession}}) RETURN sub")
	public Submission findSubmissionOwningSampleByAccession(String accession);
	*/
}