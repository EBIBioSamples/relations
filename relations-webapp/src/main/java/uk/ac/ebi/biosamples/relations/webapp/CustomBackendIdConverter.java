package uk.ac.ebi.biosamples.relations.webapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.spi.BackendIdConverter;
import org.springframework.stereotype.Component;
import uk.ac.ebi.biosamples.relations.model.Sample;
import uk.ac.ebi.biosamples.relations.model.Group;

import uk.ac.ebi.biosamples.relations.model.Submission;
import uk.ac.ebi.biosamples.relations.repo.SampleRepository;
import uk.ac.ebi.biosamples.relations.repo.GroupRepository;
import uk.ac.ebi.biosamples.relations.repo.SubmissionRepository;

import java.io.Serializable;

/**
 * Created by tliener on 22/04/2016.
 */

@Component
public class CustomBackendIdConverter implements BackendIdConverter {

	@Autowired
	SampleRepository sampleRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	SubmissionRepository submissionRepository;

	@Override // This function has to return the graphid, given a certain
				// accession
	public Serializable fromRequestId(String id, Class<?> entityType) {
		if (entityType.equals(Sample.class)) {
			return sampleRepository.findOneByAccession(id).getId();
		} else if (entityType.equals(Group.class)) {
			return groupRepository.findOneByAccession(id).getId();
		} else if (entityType.equals(Submission.class)) {
			return submissionRepository.findOneBySubmissionId(id).getId();
		} else {
			throw new IllegalArgumentException("Unrecognized class " + entityType);
		}
	}

	@Override // This function has to return the accession to a given graphid
	public String toRequestId(Serializable id, Class<?> entityType) {
		if (entityType.equals(Sample.class)) {
			return sampleRepository.findOne((Long) id).getAccession();
		} else if (entityType.equals(Group.class)) {
			return groupRepository.findOne((Long) id).getAccession();
		} else if (entityType.equals(Submission.class)) {
			return submissionRepository.findOne((Long) id).getSubmissionId();
		} else {
			throw new IllegalArgumentException("Unrecognized class " + entityType);
		}

	}

	@Override
	public boolean supports(Class<?> delimiter) {
		if (delimiter.equals(Sample.class)) {
			return true;
		} else if (delimiter.equals(Group.class)) {
			return true;
		} else if (delimiter.equals(Submission.class)) {
			return true;
		} else {
			return false;
		}
	}

}
