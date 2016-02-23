package uk.ac.ebi.biosamples.relations.model.nodes;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Relationship;

import com.fasterxml.jackson.annotation.JsonIgnore;

import uk.ac.ebi.biosamples.relations.model.edges.OwnedBy;

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
     * This is the relationship to the owner (NOT the owner itself).
     */
    @Relationship(type = "OWNED_BY", direction=Relationship.OUTGOING)
    @JsonIgnore 
	private OwnedBy ownedBy;

    public SampleOrGroup() {
    	super();
	}
    
    public SampleOrGroup(String accession) {
    	super();
    	this.accession = accession;
	}

	public String getAccession() {
    	return accession;
	}

	public OwnedBy getOwnedBy() {
		return ownedBy;
	}

	public void setOwnedBy(OwnedBy ownedBy) {
		this.ownedBy = ownedBy;
	}
}
