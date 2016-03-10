package uk.ac.ebi.biosamples.relations.importbiosdmodel;

import java.util.concurrent.Callable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.fg.biosd.model.organizational.MSI;
import uk.ac.ebi.fg.core_model.persistence.dao.hibernate.toplevel.AccessibleDAO;
import uk.ac.ebi.fg.core_model.resources.Resources;

@Component
//this makes sure that we have a different instance wherever it is used
@Scope("prototype")
public class RepoMSICallable implements Callable<Void> {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	private Iterable<String> accessions;

	@Autowired
	private RepoMappingService biosdToNeo4J;

	public RepoMSICallable() {
		super();
	}

	public RepoMSICallable(Iterable<String> accessions) {
		super();
		this.accessions = accessions;
	}

	public Iterable<String> getAccessions() {
		return accessions;
	}

	public void setAccessions(Iterable<String> accessions) {
		this.accessions = accessions;
	}

	@Override
	public Void call() throws Exception {
		log.info("Starting call()");

		EntityManagerFactory emf = Resources.getInstance().getEntityManagerFactory();
		EntityManager em = null;
		try {
			em = emf.createEntityManager();
			AccessibleDAO<MSI> dao = new AccessibleDAO<>(MSI.class, em);
			for (String msiAcc : accessions) {
				log.info("Trying MSI "+msiAcc);
				MSI msi = dao.find(msiAcc);
				if (!biosdToNeo4J.check(msi)) {
					continue;
				}
				log.info("Got MSI "+msiAcc);
				biosdToNeo4J.handle(msi);
			}
		} finally {
			if (em != null && em.isOpen()) {
				em.close();
			}
		}
		log.info("Finished call()");
		return null;
	}

}
