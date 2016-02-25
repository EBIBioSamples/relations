package uk.ac.ebi.biosamples.relations.webapp.repo;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import uk.ac.ebi.biosamples.relations.model.nodes.Group;
import uk.ac.ebi.biosamples.relations.model.nodes.Sample;

@Repository
public interface GroupRepository extends GraphRepository<Group> {

	public Group findOneByAccession(String accession);
	
	@Query("MATCH (group:Group) WHERE (group)-[:OWNERSHIP]->(:Submission {submissionId:{submissionId}}) RETURN group")
	public Iterable<Group> findGroupsOwnedBySubmissionBySubmissionId(String submissionId);
}
