package uk.ac.ebi.biosamples.relations.model.nodes;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Relationship;

import com.fasterxml.jackson.annotation.JsonIgnore;

import uk.ac.ebi.biosamples.relations.model.edges.Ownership;

public abstract class SampleOrGroup {

	/**
	 * This is needed for Neo4J internal operations, should not be exposed
	 */
    @GraphId
    protected Long id;
    
    /** 
     * This is the accession for this sample. Uniqueness must be enforce in a Neo4j index.
     */
    protected String accession;

    /**
     * This is the owner
     */
    @Relationship(type = "OWNERSHIP", direction=Relationship.OUTGOING)
	protected Submission owner;

    /**
     * Dummy constructor for use by Jackson and Neo4j
     * Do not use
     */
    public SampleOrGroup() {
    	super();
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
		if (!owner.getOwnerships().contains(this)) {
			owner.addOwnership(this);
		}
	}
}
