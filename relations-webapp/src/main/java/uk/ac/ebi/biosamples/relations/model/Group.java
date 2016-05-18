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
public class Group {

    @GraphId
    private Long id;

    private String accession;

    @Relationship(type = "MEMBERSHIP", direction=Relationship.INCOMING)
    private Set<Sample> samples =new HashSet<>();

    @Relationship(type="OWNERSHIP", direction=Relationship.OUTGOING)
    private Submission owner;

    public Group() {};

    public Long getId() {
        return id;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public Submission getOwner() {
        return owner;
    }

    public void setOwner(Submission owner) {
        this.owner = owner;
    }

    public Set<Sample> getSamples() {
        return samples;
    }

}
