package uk.ac.ebi.biosamples.relations.model.nodes;

import org.neo4j.ogm.annotation.NodeEntity;


@NodeEntity
public class Sample extends SampleOrGroup {
        
	public Sample() {
		super();
	}
	
	public Sample(String accession) {
		super(accession);
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
