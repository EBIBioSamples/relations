package uk.ac.ebi.biosamples.relations.repo;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import uk.ac.ebi.biosamples.relations.model.Group;
import uk.ac.ebi.biosamples.relations.model.Sample;

/**
 * Created by tliener on 20/04/2016.
 *
 */


public interface GroupRepository extends GraphRepository<Group> {

	public Group findOneByAccession(String accession);

	@Query("MATCH (group:Group) WHERE (group)-[:OWNERSHIP]->(:Submission {submissionId:{submissionId}}) RETURN group")
	public Iterable<Group> findGroupsOwnedBySubmissionBySubmissionId(String submissionId);

}