package uk.ac.ebi.biosamples.relations.webapp.repo;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import uk.ac.ebi.biosamples.relations.model.nodes.Sample;
import uk.ac.ebi.biosamples.relations.model.nodes.Submission;

@Repository
public interface SubmissionRepository extends GraphRepository<Submission> {
	
	public Submission findOneBySubmissionId(String submissionId);
	
	@Query("MATCH (sub:Submission) WHERE (sub)<-[:OWNERSHIP]-(:Group {accession:{accession}}) RETURN sub")
	public Submission findSubmissionOwningGroupByAccession(String accession);
	
	@Query("MATCH (sub:Submission) WHERE (sub)<-[:OWNERSHIP]-(:Sample {accession:{accession}}) RETURN sub")
	public Submission findSubmissionOwningSampleByAccession(String accession);
}
