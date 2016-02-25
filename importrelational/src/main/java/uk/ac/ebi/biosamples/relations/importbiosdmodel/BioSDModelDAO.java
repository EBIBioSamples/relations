package uk.ac.ebi.biosamples.relations.importbiosdmodel;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.stereotype.Component;

import uk.ac.ebi.fg.biosd.model.organizational.BioSampleGroup;
import uk.ac.ebi.fg.biosd.model.organizational.MSI;
import uk.ac.ebi.fg.core_model.persistence.dao.hibernate.toplevel.AccessibleDAO;
import uk.ac.ebi.fg.core_model.resources.Resources;

@Component
public class BioSDModelDAO {
    

	public BioSampleGroup getGroup(String accession) {
	    EntityManagerFactory emf = Resources.getInstance().getEntityManagerFactory();
	    EntityManager em = emf.createEntityManager();

        AccessibleDAO<BioSampleGroup> dao = new AccessibleDAO<>(BioSampleGroup.class, em);
        return dao.find(accession);
	}

	public MSI getSubmission(String submissionId) {
	    EntityManagerFactory emf = Resources.getInstance().getEntityManagerFactory();
	    EntityManager em = null;
	    MSI msi = null;
	    try {
	    	em = emf.createEntityManager();
	        AccessibleDAO<MSI> dao = new AccessibleDAO<>(MSI.class, em);
	        msi = dao.find(submissionId);
	    	
	    } finally {
	    	em.close();
	    }
        return msi;
	}
}
