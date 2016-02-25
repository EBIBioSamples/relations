package uk.ac.ebi.biosamples.relations.model.nodes;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.fasterxml.jackson.annotation.JsonIgnore;

import uk.ac.ebi.biosamples.relations.model.edges.Membership;

@NodeEntity
public class Group extends SampleOrGroup {

    @Relationship(type = "MEMBERSHIP", direction=Relationship.INCOMING)
    @JsonIgnore 
	private Set<Membership> memberships = new HashSet<>();

    /**
     * Dummy constructor for use by Jackson and Neo4j
     * Do not use
     */
    public Group() {
    	super();
    }
    
	public Group(String accession, Submission sub) {
		super(accession, sub);
	}
	
    public void addMembership(Membership membership) {
    	memberships.add(membership);
    }

    public void removeMembership(Membership membership) {
    	memberships.remove(membership);
    }

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Group)) {
            return false;
        }

        Group other = (Group) o;

        if (id != null && other.id != null) {
        	return id.equals(other.id);
        } else {
        	return true;
        }
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
