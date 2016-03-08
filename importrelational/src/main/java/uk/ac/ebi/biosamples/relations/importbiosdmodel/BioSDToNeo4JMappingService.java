package uk.ac.ebi.biosamples.relations.importbiosdmodel;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private Logger log = LoggerFactory.getLogger(this.getClass());

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
			toReturn = handle(dao.find(msiAcc));
		} finally {
			if (em != null && em.isOpen()) {
				em.close();
			}
		}
		return toReturn;
	}

	public void handleIterable(Iterable<String> msiAccs) {
		EntityManagerFactory emf = Resources.getInstance().getEntityManagerFactory();
		EntityManager em = null;
		try {
			em = emf.createEntityManager();
			AccessibleDAO<MSI> dao = new AccessibleDAO<>(MSI.class, em);
			for (String msiAcc : msiAccs) {
				handle(dao.find(msiAcc));
			}
		} finally {
			if (em != null && em.isOpen()) {
				em.close();
			}
		}
	}

	public boolean check(MSI msi) {
		if (msi == null) {
			log.info("MSI should not be null");
			return false;
		}
		// first check that each object in the msi is owned by only this msi
		for (BioSample sample : msi.getSamples()) {
			if (sample.getMSIs().size() != 1) {
				log.info("Sample owned by multiple MSIs "+sample.getAcc());
				return false;
			}
			for (MSI msiOther : sample.getMSIs()) {
				if (!msiOther.getAcc().equals(msi.getAcc())) {
					log.info("Sample ("+sample.getAcc()+") owned by MSI other than "+msi.getAcc()+" ("+msiOther.getAcc()+")");
					return false;
				}
			}
		}
		for (BioSampleGroup group : msi.getSampleGroups()) {
			if (group.getMSIs().size() != 1) {
				log.info("Group owned by multiple MSIs "+group.getAcc());
				return false;
			}
			for (MSI msiOther : group.getMSIs()) {
				if (!msiOther.getAcc().equals(msi.getAcc())) {
					log.info("Group ("+group.getAcc()+") owned by MSI other than "+msi.getAcc()+" ("+msiOther.getAcc()+")");
					return false;
				}
			}
		}
		//check that each sample reference by any groups is owned by one msi
		//but it doesn't have to be this one!
		for (BioSampleGroup group : msi.getSampleGroups()) {
			for (BioSample sample : group.getSamples()) {
				if (sample.getMSIs().size() != 1) {
					log.info("Group ("+group.getAcc()+") references sample ("+sample.getAcc()+") owned by multiple MSIs");
					return false;
				}				
			}
		}
		return true;
	}
	
	@Transactional
	public Submission handle(MSI msi) {

		Submission subN = new Submission();
		subN.setSubmissionId(msi.getAcc());
		//subN = subRepo.save(subN);
		for (BioSample sample : msi.getSamples()) {
			String sampleAcc = sample.getAcc();
			Sample sampleN = sampleRepo.findOneByAccession(sampleAcc);
			if (sampleN == null) {
				sampleN = new Sample();
				sampleN.setAccession(sampleAcc);
			}
			sampleN.setOwner(subN);
			//sampleN = sampleRepo.save(sampleN);
		}
		for (BioSampleGroup group : msi.getSampleGroups()) {
			Group groupN = new Group();
			groupN.setAccession(group.getAcc());
			groupN.setOwner(subN);
			//groupN = groupRepo.save(groupN);
			// add group memberships
			for (BioSample sample : group.getSamples()) {
				String sampleAcc = sample.getAcc();
				Sample sampleN = sampleRepo.findOneByAccession(sampleAcc);
				if (sampleN == null) {
					sampleN = new Sample();
					sampleN.setAccession(sampleAcc);
					//sampleN = sampleRepo.save(sampleN);
				}
				groupN.addSample(sampleN);
				//groupN = groupRepo.save(groupN);
			}
		}
		
		//save it back to repo
		//use depth 2
		//sub -- group -- sample
		subN = subRepo.save(subN, 2);
		return subN;
	}
}
