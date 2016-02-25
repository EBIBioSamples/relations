package uk.ac.ebi.biosamples.relations.model.edges;

import java.util.Objects;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import uk.ac.ebi.biosamples.relations.model.nodes.Group;
import uk.ac.ebi.biosamples.relations.model.nodes.Sample;

@RelationshipEntity(type = "MEMBERSHIP")
public class Membership {

	@GraphId
	private Long id;

	@StartNode
	private Sample sample;

	@EndNode
	private Group group;

	public Membership() {
		
	}
	
	public Membership(Sample sample, Group group) {
		this.sample = sample;
		this.group = group;
	}
	
	public Sample getSample() {
		return sample;
	}

	public Group getGroup() {
		return group;
	}
	

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Membership)) {
            return false;
        }

        Membership other = (Membership) o;
		return Objects.equals(sample, other.sample)
				&& Objects.equals(group, other.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sample, group);
    }
	
}
