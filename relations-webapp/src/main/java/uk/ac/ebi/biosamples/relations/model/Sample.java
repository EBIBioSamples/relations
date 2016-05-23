package uk.ac.ebi.biosamples.relations.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Relationship;

import com.google.common.collect.ImmutableSet;

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
	private Set<Sample> derivedFrom;

	@Relationship(type = "DERIVATION", direction = Relationship.OUTGOING)
	private Set<Sample> derivedTo;

	@Relationship(type = "MEMBERSHIP", direction = Relationship.OUTGOING)
	private Set<Group> groups;

	@Relationship(type = "OWNERSHIP", direction = Relationship.OUTGOING)
	private Submission owner;

	// child of is missing in the neo db at the moment
	// @Relationship(type = "CHILD OF", direction = Relationship.OUTGOING)
	// private Set<Sample> child;

	private Sample() {
	};

	public Long getId() {
		return id;
	}

	public String getAccession() {
		if (accession == null)
			throw new IllegalStateException("Accession is null");
		return accession;
	}

	public Submission getOwner() {
		if (owner == null)
			throw new IllegalStateException("Owner is null");
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
