package uk.ac.ebi.biosamples.relations.service;

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


/*
* This class converts (translates) our accession numbers for samples, groups and submission into internal ids and also
* the other way around. By overwriting these two functions Spring allows us to have our accession in the URL of our API to point
* to a certain sample/group/submission instead of having kind of random internal (neo) ids.
* */
@Component
public class CustomBackendIdConverter implements BackendIdConverter {

	@Autowired
	private SampleRepository sampleRepository;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private SubmissionRepository submissionRepository;


	/*
	* This function returns the graphid, given a certain accession
	* */
	@Override
	public Serializable fromRequestId(String id, Class<?> entityType) {
		if (entityType.equals(Sample.class)) {
			Sample sample = sampleRepository.findOneByAccession(id);
			if (sample == null) { 
				return -1;
			} else {
				return sample.getId();
			}
		} else if (entityType.equals(Group.class)) {
			Group group = groupRepository.findOneByAccession(id);
			if (group == null) {
				return -1;
			} else {
				return group.getId();
			}
		} else if (entityType.equals(Submission.class)) {
			Submission submission = submissionRepository.findOneBySubmissionId(id);
			if (submission == null) {
				return -1;
			} else {
				return submission.getId();
			}
		} else {
			throw new IllegalArgumentException("Unrecognized class " + entityType);
		}
	}

	/*
	* This function returns the accession to a given graphid
	*/
	@Override
	public String toRequestId(Serializable id, Class<?> entityType) {
		if (entityType.equals(Sample.class)) {
			Sample sample = sampleRepository.findOne((Long)id);
			if (sample == null) { 
				return null;
			} else {
				return sample.getAccession();
			}
		} else if (entityType.equals(Group.class)) {
			Group group = groupRepository.findOne((Long)id);
			if (group == null) { 
				return null;
			} else {
				return group.getAccession();
			}
		} else if (entityType.equals(Submission.class)) {
			Submission submission = submissionRepository.findOne((Long)id);
			if (submission == null) { 
				return null;
			} else {
				return submission.getSubmissionId();
			}
		} else {
			throw new IllegalArgumentException("Unrecognized class " + entityType);
		}

	}

	/*
	* Has to be overwritten for the whole thing to work, the sprin docs tell us
	* */
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
