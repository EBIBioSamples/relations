package uk.ac.ebi.biosamples.relations.model;

/**
 * Created by tliener on 20/04/2016.
 */

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Sample {

    @GraphId
    private Long id;

    private String accession;

    private Sample(){};

    @Relationship(type="DERIVATION", direction= Relationship.INCOMING)
    public Sample parent;

    @Relationship(type="MEMBERSHIP", direction= Relationship.OUTGOING)
    public Group groups;

    @Relationship(type="OWNERSHIP", direction=Relationship.OUTGOING)
    public Submission owner;

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

    public Sample getParent() { return parent;}

    public void setParent(Sample parent) {
        this.parent = parent;
    }

    public Group getGroups() {
        return groups;
    }

    public void setGroups(Group groups) {
        this.groups = groups;
    }

    public Long getId() {
        return id;
    }
}
