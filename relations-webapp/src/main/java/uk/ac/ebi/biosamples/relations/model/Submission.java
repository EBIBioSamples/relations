package uk.ac.ebi.biosamples.relations.model;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by tliener on 20/04/2016.
 */
@NodeEntity
public class Submission {

    @GraphId
    private Long id;

    @Property
    private String submissionId;

    @Relationship(type="OWNERSHIP", direction=Relationship.INCOMING)
    private Set<Sample> samples;

    @Relationship(type="OWNERSHIP", direction= Relationship.INCOMING)
    private Set<Group> groups;

    public Submission() {};

    public Long getId() {
        return id;
    }
    
    public String getSubmissionId() {
        return submissionId;
    }
    
    public ImmutableSet<Sample> getSamples() {
    	return ImmutableSet.copyOf(samples);
    }
    
/*    public ImmutableSet<Group> getGroups() {
    	return ImmutableSet.copyOf(groups);
    }*/

    public Set<Group> getGrous(){return groups;}
}
