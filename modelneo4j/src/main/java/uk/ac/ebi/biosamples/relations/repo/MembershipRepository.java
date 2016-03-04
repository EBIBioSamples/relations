package uk.ac.ebi.biosamples.relations.repo;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import uk.ac.ebi.biosamples.relations.model.edges.Membership;

@Repository
public interface MembershipRepository extends GraphRepository<Membership> {
	
}
