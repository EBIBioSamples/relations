package uk.ac.ebi.biosamples.relations.model.nodes;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.fasterxml.jackson.annotation.JsonIgnore;


@NodeEntity
public class Sample extends SampleOrGroup {

    @Relationship(type = "MEMBERSHIP", direction=Relationship.OUTGOING)
    @JsonIgnore 
	private Set<Group> groups = new HashSet<>();

    @Relationship(type = "DERIVATION", direction=Relationship.INCOMING)
    @JsonIgnore 
	private Set<Sample> derivedFrom = new HashSet<>();

    @Relationship(type = "DERIVATION", direction=Relationship.OUTGOING)
    @JsonIgnore 
	private Set<Sample> derivedTo = new HashSet<>();

	public Sample() {
		super();
	}
    
    public Set<Group> getGroups() {
    	return groups;
    }
	
    public void addGroup(Group group) {
    	if (!groups.contains(group)) {
    		groups.add(group);
    		group.addSample(this);
    	}
    }

    public void removeGroup(Group group) {
    	if (groups.contains(group)) {
    		groups.remove(group);
    		group.removeSample(this);
    	}
    }
    
    public Set<Sample> getDerivedFrom() {
    	return derivedFrom;
    }
	
    public void addDerivedFrom(Sample source) {
    	if (!derivedFrom.contains(source)) {
    		derivedFrom.add(source);
    		source.addDerivedTo(this);
    	}
    }
	
    public void removeDerivedFrom(Sample source) {
    	if (derivedFrom.contains(source)) {
    		derivedFrom.remove(source);
    		source.removeDerivedTo(this);
    	}
    }
    
    public Set<Sample> getDerivedTo() {
    	return derivedTo;
    }
	
    public void addDerivedTo(Sample product) {
    	if (!derivedTo.contains(product)) {
    		derivedTo.add(product);
    		product.addDerivedFrom(this);
    	}
    }
	
    public void removeDerivedTo(Sample source) {
    	if (derivedFrom.contains(source)) {
    		derivedFrom.remove(source);
    		source.removeDerivedFrom(this);
    	}
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
        } else if (id == null && other.id == null) {
        	//if both id null, compare accession
        	return Objects.equals(accession, other.accession);
        } else {
        	return true;
        }
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
