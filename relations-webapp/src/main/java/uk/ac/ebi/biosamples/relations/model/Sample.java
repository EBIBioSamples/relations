package uk.ac.ebi.biosamples.relations.model;

/**
 * Created by tliener on 20/04/2016.
 */

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Relationship;

import com.google.common.collect.ImmutableSet;

@NodeEntity
public class Sample {

	@GraphId
	private Long id;

    @Property
	private String accession;

	@Relationship(type = "SAMEAS", direction = Relationship.UNDIRECTED)
	private Set<Sample> sameAs;

	@Relationship(type = "DERIVATION", direction = Relationship.OUTGOING)
	private Set<Sample> derivedFrom;

	@Relationship(type = "DERIVATION", direction = Relationship.INCOMING)
	private Set<Sample> derivedTo;

	@Relationship(type = "MEMBERSHIP", direction = Relationship.OUTGOING)
	private Set<Group> groups;

	@Relationship(type = "OWNERSHIP", direction = Relationship.OUTGOING)
	private Submission owner;
	
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

	public ImmutableSet<Sample> getDerivedFrom() {
		return ImmutableSet.copyOf(derivedFrom);
	}

	public ImmutableSet<Sample> getDerivedTo() {
		return ImmutableSet.copyOf(derivedTo);
	}

	public ImmutableSet<Group> getGroups() {
		return ImmutableSet.copyOf(groups);
	}

	public ImmutableSet<Sample> getSameAs() {
		return ImmutableSet.copyOf(sameAs);
	}

}
