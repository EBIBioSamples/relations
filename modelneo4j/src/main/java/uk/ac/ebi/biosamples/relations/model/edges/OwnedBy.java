package uk.ac.ebi.biosamples.relations.model.edges;

import java.util.Objects;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import uk.ac.ebi.biosamples.relations.model.nodes.SampleOrGroup;
import uk.ac.ebi.biosamples.relations.model.nodes.Submission;

@RelationshipEntity(type="OWNED_BY")
public class OwnedBy {
	
    @GraphId   
    private Long id;

    @StartNode 
    private SampleOrGroup source;
    
    @EndNode   
    private Submission submission;

    public OwnedBy(SampleOrGroup source, Submission submission) {
    	this.source = source;
    	this.submission = submission;
    }
    
	public SampleOrGroup getSource() {
		return source;
	}

	public Submission getSubmission() {
		return submission;
	}


	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OwnedBy)) {
            return false;
        }

        OwnedBy other = (OwnedBy) o;
		return Objects.equals(source, other.source)
				&& Objects.equals(submission, other.submission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, submission);
    }
	
}
