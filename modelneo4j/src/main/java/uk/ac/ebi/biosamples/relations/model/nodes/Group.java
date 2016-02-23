package uk.ac.ebi.biosamples.relations.model.nodes;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.fasterxml.jackson.annotation.JsonIgnore;

import uk.ac.ebi.biosamples.relations.model.edges.MemberOf;

@NodeEntity
public class Group extends SampleOrGroup {

    @Relationship(type = "MEMBER_OF", direction=Relationship.INCOMING)
    @JsonIgnore 
	private Set<MemberOf> memberOfs = new HashSet<>();

    /**
     * Dummy constructor for use by Jackson and Neo4j
     * Do not use
     */
    public Group() {
    	super();
    }
    
	public Group(String accession) {
		super(accession);
	}
	
    public void addMemberOf(MemberOf memberOf) {
    	memberOfs.add(memberOf);
    }

    public void removeMemberOf(MemberOf memberOf) {
    	memberOfs.remove(memberOf);
    }

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Group)) {
            return false;
        }

        Group other = (Group) o;

        if (id != null && other.id != null) {
        	return id.equals(other.id);
        } else {
        	return true;
        }
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
