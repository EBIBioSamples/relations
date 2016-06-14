package uk.ac.ebi.biosamples.relations.importbiosdmodel;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import uk.ac.ebi.fg.biosd.model.expgraph.BioSample;
import uk.ac.ebi.fg.biosd.model.organizational.BioSampleGroup;
import uk.ac.ebi.fg.biosd.model.organizational.MSI;
import uk.ac.ebi.fg.biosd.model.xref.DatabaseRecordRef;
import uk.ac.ebi.fg.core_model.expgraph.properties.ExperimentalPropertyType;
import uk.ac.ebi.fg.core_model.expgraph.properties.ExperimentalPropertyValue;
import uk.ac.ebi.fg.myequivalents.model.Entity;

import java.util.*;

/*
* The class is responsible for actually creating the csv files that are used in the neo importer. For every node as well as relationship
* an own csv file is created. To create each csv files, the database is queried via Hibernate, the sample and group objects are run through
* as well as their relationships and the data stored in csv files accordingly. As additional property, Database references for sample/groups are
* extracted from the object or the myEquivalent database
* */
@Service
public class CSVMappingService implements AutoCloseable {

	@Autowired
	private MyEquivalenceManager myEquivalenceManager;

	@Value("${neo4jIndexer.outpath:output}")
	private File outpath;

	private CSVPrinter samplePrinter;
	private CSVPrinter groupPrinter;
	private CSVPrinter submissionPrinter;
	private CSVPrinter ownershipSamplePrinter;
	private CSVPrinter ownershipGroupPrinter;
	private CSVPrinter membershipPrinter;
	private CSVPrinter derivationPrinter;
	private CSVPrinter sameAsPrinter;
	private CSVPrinter childOfPrinter;
	private CSVPrinter recurationPrinter;
	private CSVPrinter externalLinkPrinter;
	private CSVPrinter hasExternalLinkGroupPrinter;
	private CSVPrinter hasExternalLinkSamplePrinter;

	/*
	 * Sets up csv printers for every sample/relationship by using FileWriter
	 * and BufferedWriter
	 */
	@PostConstruct
	public void doSetup() throws IOException {
		// TODO create in temp dirs, then atomically swap into file location
		outpath.mkdirs();
		samplePrinter = new CSVPrinter(new BufferedWriter(new FileWriter(new File(outpath, "sample.csv"))),
				CSVFormat.DEFAULT);
		groupPrinter = new CSVPrinter(new BufferedWriter(new FileWriter(new File(outpath, "group.csv"))),
				CSVFormat.DEFAULT);
		submissionPrinter = new CSVPrinter(new BufferedWriter(new FileWriter(new File(outpath, "submission.csv"))),
				CSVFormat.DEFAULT);
		ownershipSamplePrinter = new CSVPrinter(
				new BufferedWriter(new FileWriter(new File(outpath, "ownership_sample.csv"))), CSVFormat.DEFAULT);
		ownershipGroupPrinter = new CSVPrinter(
				new BufferedWriter(new FileWriter(new File(outpath, "ownership_group.csv"))), CSVFormat.DEFAULT);
		membershipPrinter = new CSVPrinter(new BufferedWriter(new FileWriter(new File(outpath, "membership.csv"))),
				CSVFormat.DEFAULT);
		derivationPrinter = new CSVPrinter(new BufferedWriter(new FileWriter(new File(outpath, "derivation.csv"))),
				CSVFormat.DEFAULT);
		sameAsPrinter = new CSVPrinter(new BufferedWriter(new FileWriter(new File(outpath, "sameas.csv"))),
				CSVFormat.DEFAULT);
		childOfPrinter = new CSVPrinter(new BufferedWriter(new FileWriter(new File(outpath, "childof.csv"))),
				CSVFormat.DEFAULT);
		recurationPrinter = new CSVPrinter(new BufferedWriter(new FileWriter(new File(outpath, "recuratedfrom.csv"))),
				CSVFormat.DEFAULT);
		externalLinkPrinter = new CSVPrinter(new BufferedWriter(new FileWriter(new File(outpath, "links.csv"))),
				CSVFormat.DEFAULT);
		hasExternalLinkGroupPrinter = new CSVPrinter(
				new BufferedWriter(new FileWriter(new File(outpath, "haslink_group.csv"))), CSVFormat.DEFAULT);
		hasExternalLinkSamplePrinter = new CSVPrinter(
				new BufferedWriter(new FileWriter(new File(outpath, "haslink_sample.csv"))), CSVFormat.DEFAULT);
	}

	/*
	 * Close every printer when finished
	 */
	@PreDestroy
	public void close() throws IOException {
		samplePrinter.close();
		groupPrinter.close();
		submissionPrinter.close();
		ownershipSamplePrinter.close();
		ownershipGroupPrinter.close();
		membershipPrinter.close();
		derivationPrinter.close();
		sameAsPrinter.close();
		childOfPrinter.close();
		recurationPrinter.close();
		externalLinkPrinter.close();
		hasExternalLinkGroupPrinter.close();
		hasExternalLinkSamplePrinter.close();
	}

	private boolean valid(BioSampleGroup group) {
		// must be owned by one msi and one msi only
		if (group.getMSIs().size() != 1)
			return false;
		return true;
	}

	private boolean valid(BioSample sample) {
		// must be owned by one msi and one msi only
		if (sample.getMSIs().size() != 1)
			return false;
		return true;
	}

	private boolean valid(MSI msi) {
		if (msi == null) return false;
		return true;
	}

	private void printSubmission(String acc) throws IOException {
		synchronized (submissionPrinter) {
			submissionPrinter.print(acc);
			submissionPrinter.println();
		}
	}

	/*
	 * Print Sample Node with db reference
	 * 
	 * @param acc accession for a Sample node
	 * 
	 * @param dbrefstring Set<String> that defines the db urls
	 */
	private void printSample(String acc) throws IOException {
		synchronized (samplePrinter) {
			samplePrinter.print(acc);
			samplePrinter.println();
		}
	}

	/*
	 * Print group Node with database reference
	 * 
	 * @param acc accession for a Group node
	 * 
	 * @param dbrefstring Set<String> that defines the db urls
	 */
	private void printGroup(String acc) throws IOException {
		synchronized (groupPrinter) {
			groupPrinter.print(acc);
			groupPrinter.println();
		}
	}

	/*
	 * Print Sample Ownership relationship (sample - submission)
	 * 
	 * @param accession accession of a node
	 * 
	 * @param subId submission ID
	 */
	private void printSampleOwnership(String acc, String subId) throws IOException {
		synchronized (ownershipSamplePrinter) {
			ownershipSamplePrinter.print(acc);
			ownershipSamplePrinter.print(subId);
			ownershipSamplePrinter.println();
		}
	}

	/*
	 * Print Group Ownership relationship (group - submission)
	 * 
	 * @param accession accession of a group
	 * 
	 * @param subId submission ID
	 */
	private void printGroupOwnership(String acc, String subId) throws IOException {
		synchronized (ownershipGroupPrinter) {
			ownershipGroupPrinter.print(acc);
			ownershipGroupPrinter.print(subId);
			ownershipGroupPrinter.println();
		}
	}

	/*
	 * Print sample to group Membership
	 * 
	 * @param sampleAcc accession of the sample
	 * 
	 * @param groupAcc accession of the group
	 */
	private void printMembership(String sampleAcc, String groupAcc) throws IOException {
		synchronized (membershipPrinter) {
			membershipPrinter.print(sampleAcc);
			membershipPrinter.print(groupAcc);
			membershipPrinter.println();
		}
	}

	/*
	 * Prints derivation relationship
	 * 
	 * @param productAcc sample accession
	 * 
	 * @param sourceAcc sample accession
	 */
	private void printDerivation(String productAcc, String sourceAcc) throws IOException {
		synchronized (derivationPrinter) {
			derivationPrinter.print(sourceAcc);
			derivationPrinter.print(productAcc);
			derivationPrinter.println();
		}
	}

	/*
	 * Prints Recurated From relationship
	 * 
	 * @param sample accession
	 * 
	 * @param sample accession
	 */
	private void printRecuration(String target, String original) throws IOException {
		synchronized (recurationPrinter) {
			recurationPrinter.print(original);
			recurationPrinter.print(target);
			recurationPrinter.println();
		}
	}

	/*
	 * Prints as as relationship
	 * 
	 * @param sample accession
	 * 
	 * @param sample accession
	 */
	private void printSameAs(String acc, String otherAcc) throws IOException {
		synchronized (sameAsPrinter) {
			sameAsPrinter.print(acc);
			sameAsPrinter.print(otherAcc);
			sameAsPrinter.println();
		}
	}

	/*
	 * Prints childOf relationship
	 * 
	 * @param sample accession
	 * 
	 * @param sample accession
	 */
	private void printChildOf(String child, String parent) throws IOException {
		synchronized (childOfPrinter) {
			childOfPrinter.print(child);
			childOfPrinter.print(parent);
			childOfPrinter.println();
		}
	}

	/*
	 * @param url url of a database link
	 */
	private void printDbNode(String url) throws IOException {
		synchronized (externalLinkPrinter) {
			externalLinkPrinter.print(url);
			externalLinkPrinter.println();
		}
	}

	/*
	 * Saves the relationship between db url and sample
	 */
	private void printSampleHasExternalLink(String acc, String url) throws IOException {
		synchronized (hasExternalLinkSamplePrinter) {
			hasExternalLinkSamplePrinter.print(acc);
			hasExternalLinkSamplePrinter.print(url);
			hasExternalLinkSamplePrinter.println();
		}
	}

	/*
	 * Saves the relationship between db url and group
	 */
	private void printGroupHasExternalLink(String acc, String url) throws IOException {
		synchronized (hasExternalLinkGroupPrinter) {
			hasExternalLinkGroupPrinter.print(acc);
			hasExternalLinkGroupPrinter.print(url);
			hasExternalLinkGroupPrinter.println();
		}
	}

	/*
	 * Converts DbRefs coming from an Entity as stored in the myEquivalence
	 * model
	 * 
	 * @param dbRefs A Set<Entity> as stored in myEquivalence
	 */
	private Set<String> covertMyEquiEntity(Set<URI> dbRefs) {
		Set<String> list = new HashSet<String>();
		for (URI uri : dbRefs) {
			list.add(uri.toString());
		}
		return list;
	}

	/*
	 * Converts DbRefs coming from the Hibernate model into Set of Strings
	 * 
	 * @param dbRefs A Set <DatabaseRecordRef> as stored in the model
	 */
	private Set<String> convertDbRefs(Set<DatabaseRecordRef> dbRefs) {
		Set<String> list = new HashSet<String>();
		for (DatabaseRecordRef ref : dbRefs) {
			list.add(ref.getUrl());
		}
		return list;
	}

	/*
	 * Main function that actually runs through the samples and groups, gets the
	 * relevant data and calles the printer functions
	 * 
	 * @param msi Submission msi
	 */
	public void handle(MSI msi) throws IOException {

		if (valid(msi)) {
			// get outside of sync block as it might call database and take a
			// while
			printSubmission(msi.getAcc());
			for (BioSample sample : msi.getSamples()) {
				if (valid(sample)) {

					Set<String> dburls = new HashSet<String>();
					dburls.addAll(convertDbRefs(sample.getDatabaseRecordRefs()));
					dburls.addAll(
							covertMyEquiEntity(myEquivalenceManager.getSampleExternalEquivalences(sample.getAcc())));

					printSample(sample.getAcc());

					/*
					 * To model the db links as own relationships: Run over
					 * dburls and create for every string an entry in DbNode as
					 * well as a connection between the group and the url
					 */
					for (String url : dburls) {
						printSampleHasExternalLink(sample.getAcc(), url);
						printDbNode(url);
					}

					printSampleOwnership(sample.getAcc(), msi.getAcc());

					// this is the slow join
					for (ExperimentalPropertyValue<?> epv : sample.getPropertyValues()) {
						ExperimentalPropertyType ept = epv.getType();

						if ("derived from".equals(ept.getTermText().toLowerCase())) {
							printDerivation(sample.getAcc(), epv.getTermText());
						}

						if ("same as".equals(ept.getTermText().toLowerCase())) {
							printSameAs(sample.getAcc(), epv.getTermText());
						}

						// to convert to lower case does not seem to work. no
						// idea why. for the sake of it I keep in the if clause
						// just to make sure we catch all
						if ("Child Of".equals(ept.getTermText())
								|| "child of".equals(ept.getTermText().toLowerCase())) {
							printChildOf(sample.getAcc(), epv.getTermText());
						}

						if ("recurated from".equals(ept.getTermText().toLowerCase())) {
							printRecuration(sample.getAcc(), epv.getTermText());
						}

					}

				}
			}

			for (BioSampleGroup group : msi.getSampleGroups()) {
				if (valid(group)) {

					Set<String> dburls = new HashSet<String>();
					dburls.addAll(convertDbRefs(group.getDatabaseRecordRefs()));
					dburls.addAll(
							covertMyEquiEntity(myEquivalenceManager.getSampleExternalEquivalences(group.getAcc())));

					printGroup(group.getAcc());

					/*
					 * To model the db links as own relationships: Run over
					 * dburls and create for every string an entry in DbNode as
					 * well as a connection between the group and the url
					 */
					for (String url : dburls) {
						printGroupHasExternalLink(group.getAcc(), url);
						printDbNode(url);
					}

					printGroupOwnership(group.getAcc(), msi.getAcc());

					for (BioSample sample : group.getSamples()) {
						if (valid(sample)) {
							printMembership(sample.getAcc(), group.getAcc());
						}
					}
				}
			}
		}
	}
}
