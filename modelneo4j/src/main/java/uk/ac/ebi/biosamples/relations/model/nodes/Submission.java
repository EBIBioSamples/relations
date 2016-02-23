package uk.ac.ebi.biosamples.relations.model.nodes;

import org.neo4j.ogm.annotation.GraphId;

public class Submission {

    @GraphId
    private Long id;
    
    private String submissionId;
    
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
