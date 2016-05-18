package uk.ac.ebi.biosamples.relations.model;

/**
 * Created by tliener on 20/04/2016.
 */

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Sample {

	@GraphId
	private Long id;

    @Property
	private String accession;

	@Relationship(type = "SAMEAS", direction = Relationship.UNDIRECTED)
	public Set<Sample> sameAs;

	@Relationship(type = "DERIVATION", direction = Relationship.OUTGOING)
	public Set<Sample> derivedFrom;

	@Relationship(type = "MEMBERSHIP", direction = Relationship.OUTGOING)
	public Set<Group> groups;

	@Relationship(type = "OWNERSHIP", direction = Relationship.OUTGOING)
	public Submission owner;
	
	private Sample() {};

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

	public Set<Sample> getDerivedFrom() {
		return derivedFrom;
	}

	public Set<Group> getGroups() {
		return groups;
	}

	public Set<Sample> getSameAs() {
		return sameAs;
	}

}
