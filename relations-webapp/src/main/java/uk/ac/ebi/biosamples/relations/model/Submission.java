package uk.ac.ebi.biosamples.relations.model;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by tliener on 20/04/2016.
 */
public class Submission {

    @GraphId
    private Long id;

    private String submissionId;

    public Submission() {};

    @Relationship(type="OWNERSHIP", direction=Relationship.INCOMING )
    private Set<Sample> samples = new HashSet<>();

    @Relationship(type="OWNERSHIP", direction= Relationship.INCOMING)
    private Set<Group> groups = new HashSet<>();


    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }


    public Long getId() {
        return id;
    }

    /*Combining samples and groups temporary, since they both represent the ownership*/
    public Set<Object> getOwnerships(){
        HashSet tmp=new HashSet<>();
        tmp.add(samples);
        tmp.add(groups);
        return tmp;
    }



}
