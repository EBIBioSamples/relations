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
	private Set<Sample> samples = new HashSet<>();

    public Group() {
    	super();
    }
    
    public Set<Sample> getSamples() {
    	return samples;
    }
	
    public void addSample(Sample sample) {
    	if (!samples.contains(sample)) {
    		samples.add(sample);
    		sample.addGroup(this);
    	}
    }

    public void removeSample(Sample sample) {
    	if (samples.contains(sample)) {
    		samples.remove(sample);
    		sample.removeGroup(this);
    	}
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
