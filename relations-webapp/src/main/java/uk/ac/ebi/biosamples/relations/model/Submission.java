package uk.ac.ebi.biosamples.relations.model;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by tliener on 20/04/2016.
 */
@NodeEntity
public class Submission {

    @GraphId
    private Long id;
/*
    private String submissionId;

    @Relationship(type="OWNERSHIP", direction=Relationship.INCOMING )
    private Set<Sample> samples = new HashSet<>();

    @Relationship(type="OWNERSHIP", direction= Relationship.INCOMING)
    private Set<Group> groups = new HashSet<>();

    public Submission() {};

    public Long getId() {
        return id;
    }
    
    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }
*/
}
