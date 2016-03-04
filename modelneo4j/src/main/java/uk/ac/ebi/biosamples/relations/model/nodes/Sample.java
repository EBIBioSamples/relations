package uk.ac.ebi.biosamples.relations.model.nodes;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.fasterxml.jackson.annotation.JsonIgnore;


@NodeEntity
public class Sample extends SampleOrGroup {

    @Relationship(type = "MEMBERSHIP", direction=Relationship.OUTGOING)
    @JsonIgnore 
	private Set<Group> groups = new HashSet<>();

	public Sample() {
		super();
	}
    
    public Set<Group> getGroups() {
    	return groups;
    }
	
    public void addSample(Group group) {
    	groups.add(group);
    }

    public void removeSample(Group group) {
    	groups.remove(group);
    }

	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sample)) {
            return false;
        }

        Sample other = (Sample) o;

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
