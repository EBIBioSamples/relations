package uk.ac.ebi.biosamples.relations.repo;

import org.springframework.data.neo4j.repository.GraphRepository;
import uk.ac.ebi.biosamples.relations.model.Group;

/**
 * Created by tliener on 20/04/2016.
 *
 */


public interface GroupRepository extends GraphRepository<Group> {
	/*

	public Group findOneByAccession(String accession);

	@Query("MATCH (group:Group) WHERE (group)-[:OWNERSHIP]->(:Submission {submissionId:{submissionId}}) RETURN group")
	public Iterable<Group> findGroupsOwnedBySubmissionBySubmissionId(String submissionId);
	*/

}