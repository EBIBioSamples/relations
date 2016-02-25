package uk.ac.ebi.biosamples.relations.model.nodes;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Relationship;

import com.fasterxml.jackson.annotation.JsonIgnore;

import uk.ac.ebi.biosamples.relations.model.edges.Ownership;

public class Submission {

    @GraphId
    private Long id;
    
    private String submissionId;
    
    
    @Relationship(type = "OWNERSHIP", direction=Relationship.INCOMING)
    @JsonIgnore 
	private Set<SampleOrGroup> ownerships = new HashSet<>();
	

    /**
     * Dummy constructor for use by Jackson and Neo4j
     * Do not use
     */
    public Submission() {
    	super();
    }

	public Submission(String submissionId) {
		super();
		this.submissionId = submissionId;
	}

	public Long getId() {
		return id;
	}
    
    public String getSubmissionId() {
    	return submissionId;
	}
    
    public Set<SampleOrGroup> getOwnerships() {
    	return ownerships;
    }
    
    public void addOwnership(SampleOrGroup source) {
    	if (!ownerships.contains(source)) {
    		ownerships.add(source);
    		source.setOwner(this);
    	}
    }
    
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Submission)) {
            return false;
        }

        Submission other = (Submission) o;

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
