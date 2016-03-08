package uk.ac.ebi.biosamples.relations.model.edges;

import java.util.Objects;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import uk.ac.ebi.biosamples.relations.model.nodes.Group;
import uk.ac.ebi.biosamples.relations.model.nodes.Sample;

@RelationshipEntity(type = "DERIVATION")
public class Derivation {

	@GraphId
	private Long id;

	@StartNode
	private Sample source;

	@EndNode
	private Sample product;

	public Derivation() {
		
	}
	
	public Derivation(Sample source, Sample product) {
		this.source = source;
		this.product = product;
	}
	
	public Sample getSource() {
		return source;
	}

	public Sample getProduct() {
		return product;
	}
	

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Derivation)) {
            return false;
        }

        Derivation other = (Derivation) o;
		return Objects.equals(source, other.source)
				&& Objects.equals(product, other.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, product);
    }
	
}
