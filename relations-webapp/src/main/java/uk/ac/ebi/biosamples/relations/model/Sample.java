package uk.ac.ebi.biosamples.relations.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Relationship;


/**
 * Created by tliener on 20/04/2016.
 */
@NodeEntity
public class Sample {

	@GraphId
	private Long id;

	@Property
	private String accession;

	@Relationship(type = "SAMEAS", direction = Relationship.UNDIRECTED)
	private Set<Sample> sameAs;

	@Relationship(type = "DERIVATION", direction = Relationship.INCOMING)
	private Set<Sample> derivedTo;

	@Relationship(type = "MEMBERSHIP", direction = Relationship.OUTGOING)
	private Set<Group> groups;

	@Relationship(type = "OWNERSHIP", direction = Relationship.OUTGOING)
	private Submission owner;

	@Relationship(type= "CHILDOF", direction = Relationship.OUTGOING)
	private Set<Sample> childOf;

	@Relationship(type = "DERIVATION", direction = Relationship.OUTGOING)
	private Set<Sample> derivedFrom;


	/*
	@Relationship(type= "CURATEDINTO", direction = Relationship.UNIDRECTED)
	private Set <Sample> curatedInto;
	*/



	private Sample() {};

	public Long getId() {
		return id;
	}

	public String getAccession() {
		return accession;
	}

	public Submission getOwner() {
		return owner;
	}

	public Set<Sample> getDerivedFrom() {
		return derivedFrom;
	}

	public Set<Sample> getDerivedTo() {
			return derivedTo;
	}

	public Set<Group> getGroups() {
		return groups;
	}

	public Set<Sample> getSameAs() {
		return sameAs;
	}

	public Set<Sample> getChildOf(){
		return childOf;
	}


}
