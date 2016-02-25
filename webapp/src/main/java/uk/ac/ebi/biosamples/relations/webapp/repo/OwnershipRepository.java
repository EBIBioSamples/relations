package uk.ac.ebi.biosamples.relations.webapp.repo;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import uk.ac.ebi.biosamples.relations.model.edges.Ownership;

@Repository
public interface OwnershipRepository extends GraphRepository<Ownership> {
	
}
