package uk.ac.ebi.biosamples.relations.importbiosdmodel;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.stereotype.Component;

import uk.ac.ebi.fg.biosd.model.organizational.BioSampleGroup;
import uk.ac.ebi.fg.core_model.persistence.dao.hibernate.toplevel.AccessibleDAO;
import uk.ac.ebi.fg.core_model.resources.Resources;

@Component
public class BioSDModelDAO {
    

	public BioSampleGroup getGroup(String accession) {
	    EntityManagerFactory emf = Resources.getInstance().getEntityManagerFactory();
	    EntityManager em = emf.createEntityManager();

        AccessibleDAO<BioSampleGroup> biosampleDAO = new AccessibleDAO<>(BioSampleGroup.class, em);
        return biosampleDAO.find(accession);
	}
}
