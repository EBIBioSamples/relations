package uk.ac.ebi.biosamples.relations.repo;

import uk.ac.ebi.biosamples.relations.model.ExternalLink;

public interface ExternalLinkRepository extends ReadOnlyNeoRepository<ExternalLink> {
	
	public ExternalLink findOneByUrl(String Url);
	
}