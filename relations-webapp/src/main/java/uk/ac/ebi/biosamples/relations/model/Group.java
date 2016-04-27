package uk.ac.ebi.biosamples.relations.model;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by tliener on 20/04/2016.
 */
public class Group {

    @GraphId
    private Long id;

    private String accession;

    public Group(){};

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    @Relationship(type = "MEMBERSHIP", direction=Relationship.INCOMING)
    private Set<Sample> samples =new HashSet<>();


    @Relationship(type="OWNERSHIP", direction=Relationship.OUTGOING)
    private Submission owner;



    public Submission getOwner() {
        return owner;
    }

    public void setOwner(Submission owner) {
        this.owner = owner;
    }

    public Set<Sample> getSamples() {
        return samples;
    }

    public Long getId() {
        return id;
    }
}
