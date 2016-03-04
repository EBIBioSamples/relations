package uk.ac.ebi.biosamples.relations.importbiosdmodel;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.biosamples.relations.model.nodes.Group;
import uk.ac.ebi.biosamples.relations.model.nodes.Sample;
import uk.ac.ebi.biosamples.relations.model.nodes.Submission;
import uk.ac.ebi.biosamples.relations.repo.GroupRepository;
import uk.ac.ebi.biosamples.relations.repo.SampleRepository;
import uk.ac.ebi.biosamples.relations.repo.SubmissionRepository;
import uk.ac.ebi.fg.biosd.model.expgraph.BioSample;
import uk.ac.ebi.fg.biosd.model.organizational.BioSampleGroup;
import uk.ac.ebi.fg.biosd.model.organizational.MSI;
import uk.ac.ebi.fg.core_model.persistence.dao.hibernate.toplevel.AccessibleDAO;
import uk.ac.ebi.fg.core_model.resources.Resources;

@Service
public class BioSDToNeo4JMappingService {
	

	@Autowired
	private SubmissionRepository subRepo;

	@Autowired
	private SampleRepository sampleRepo;

	@Autowired
	private GroupRepository groupRepo;
	
	
	public Submission handle(String msiAcc) {
		EntityManagerFactory emf = Resources.getInstance().getEntityManagerFactory();
		EntityManager em = null;
		Submission toReturn;
		try {
			em = emf.createEntityManager();
			AccessibleDAO<MSI> dao = new AccessibleDAO<>(MSI.class, em);
			MSI msi = dao.find(msiAcc);
			toReturn = handle(msi);
		} finally {
			if (em != null && em.isOpen()) {
				em.close();
			}
		}
		return toReturn;
	}
	
	//@Transactional
	public Submission handle(MSI msi) {
		Submission subN = new Submission();
		subN.setSubmissionId(msi.getAcc());
		subN = subRepo.save(subN);
		for (BioSample sample : msi.getSamples()) {
			Sample sampleN = new Sample();
			sampleN.setAccession(sample.getAcc());
			sampleN.setOwner(subN);
			sampleN = sampleRepo.save(sampleN);
		}
		for (BioSampleGroup group : msi.getSampleGroups()) {
			Group groupN = new Group();
			groupN.setAccession(group.getAcc());
			groupN.setOwner(subN);
			groupN = groupRepo.save(groupN);
			//add group memberships
			for (BioSample sample : group.getSamples()) {
				String sampleAcc = sample.getAcc();
				Sample sampleN = sampleRepo.findOneByAccession(sampleAcc);
				if (sampleN == null) {
					sampleN = new Sample();
					sampleN.setAccession(sampleAcc);
					sampleN = sampleRepo.save(sampleN);
				}
				groupN.addSample(sampleN);
				groupN = groupRepo.save(groupN);
			}
		}
		
		return subN;
	}
}
