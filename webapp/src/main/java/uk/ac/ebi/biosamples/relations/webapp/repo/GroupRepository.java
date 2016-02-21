package uk.ac.ebi.biosamples.relations.webapp.repo;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import uk.ac.ebi.biosamples.relations.model.nodes.Group;

@Repository
public interface GroupRepository extends GraphRepository<Group> {
	
}
