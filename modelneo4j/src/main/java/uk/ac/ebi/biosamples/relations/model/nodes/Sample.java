package uk.ac.ebi.biosamples.relations.model.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.fasterxml.jackson.annotation.JsonIgnore;


@NodeEntity
public class Sample extends SampleOrGroup {
    

    
	public Sample() {
		
	}
	
	public Sample(String accession) {
		this.accession = accession;
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
