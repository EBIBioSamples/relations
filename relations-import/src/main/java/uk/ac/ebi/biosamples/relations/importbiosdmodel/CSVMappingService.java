package uk.ac.ebi.biosamples.relations.importbiosdmodel;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
public class CSVMappingService implements Closeable {

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
	private CSVPrinter ReCuratedFromPrinter;
	private CSVPrinter DbNodePrinter;
	private CSVPrinter DbRefsPrinter;
	private CSVPrinter dbRefGroupRel;


	/*
	* Sets up csv printers for every sample/relationship by using FileWriter and BufferedWriter
	* */
	@PostConstruct
	public void doSetup() throws IOException {
		//TODO create in temp dirs, then atomically swap into file location
		outpath.mkdirs();
		samplePrinter = new CSVPrinter(new BufferedWriter(new FileWriter(new File(outpath, "sample.csv"))), CSVFormat.DEFAULT);
		groupPrinter = new CSVPrinter(new BufferedWriter(new FileWriter(new File(outpath, "group.csv"))), CSVFormat.DEFAULT);
		submissionPrinter = new CSVPrinter(new BufferedWriter(new FileWriter(new File(outpath, "submission.csv"))), CSVFormat.DEFAULT);
		ownershipSamplePrinter = new CSVPrinter(new BufferedWriter(new FileWriter(new File(outpath, "ownership_sample.csv"))), CSVFormat.DEFAULT);
		ownershipGroupPrinter = new CSVPrinter(new BufferedWriter(new FileWriter(new File(outpath, "ownership_group.csv"))), CSVFormat.DEFAULT);
		membershipPrinter = new CSVPrinter(new BufferedWriter(new FileWriter(new File(outpath, "membership.csv"))), CSVFormat.DEFAULT);
		derivationPrinter = new CSVPrinter(new BufferedWriter(new FileWriter(new File(outpath, "derivation.csv"))), CSVFormat.DEFAULT);
		sameAsPrinter = new CSVPrinter(new BufferedWriter(new FileWriter(new File(outpath, "sameAs.csv"))), CSVFormat.DEFAULT);
		childOfPrinter = new CSVPrinter(new BufferedWriter(new FileWriter(new File(outpath, "childOf.csv"))), CSVFormat.DEFAULT);
		ReCuratedFromPrinter = new CSVPrinter(new BufferedWriter(new FileWriter(new File(outpath, "curatedFrom.csv"))), CSVFormat.DEFAULT);
		DbNodePrinter = new CSVPrinter(new BufferedWriter(new FileWriter(new File(outpath, "dburls.csv"))), CSVFormat.DEFAULT);
		DbRefsPrinter = new CSVPrinter(new BufferedWriter(new FileWriter(new File(outpath, "dbRefSampleRel.csv"))), CSVFormat.DEFAULT);
		dbRefGroupRel = new CSVPrinter(new BufferedWriter(new FileWriter(new File(outpath, "dbRefGroupRel.csv"))), CSVFormat.DEFAULT);
	}

	/*
	* Close every printer when finished
	* */
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
		ReCuratedFromPrinter.close();
	}

	private boolean valid(BioSampleGroup group) {
		//must be owned by one msi and one msi only
		if (group.getMSIs().size() != 1) return false;
		return true;
	}

	private boolean valid(BioSample sample) {
		//must be owned by one msi and one msi only
		if (sample.getMSIs().size() != 1) return false;
		return true;
	}

	private boolean valid(MSI msi) {
		return true;
	}

	private synchronized void printSubmission(String acc) throws IOException {
		submissionPrinter.print(acc);
		submissionPrinter.println();
	}

	/*Print Sample Node with db reference
	* @param acc accession for a Sample node
	* @param dbrefstring Set<String> that defines the db urls
	* */
	private synchronized void printSample(String acc, Set<String> dbrefstring) throws IOException {
		samplePrinter.print(acc);
		samplePrinter.print(dbrefstring);
		samplePrinter.println();
	}

	/* Print group Node with database reference
	*  @param acc accession for a Group node
	* @param dbrefstring Set<String> that defines the db urls
	* */
	private synchronized void printGroup(String acc, Set<String> dbrefstring) throws IOException {
		groupPrinter.print(acc);
		groupPrinter.print(dbrefstring);
		groupPrinter.println();
	}

	/* Print Sample Ownership relationship (sample - submission)
	* @param accession accession of a node
	* @param subId submission ID
	* */
	private synchronized void printSampleOwnership(String acc, String subId) throws IOException {
		ownershipSamplePrinter.print(acc);
		ownershipSamplePrinter.print(subId);
		ownershipSamplePrinter.println();
	}

	/*Print Group Ownership relationship (group - submission)
	* @param accession accession of a group
	* @param subId submission ID
	* */
	private synchronized void printGroupOwnership(String acc, String subId) throws IOException {
		ownershipGroupPrinter.print(acc);
		ownershipGroupPrinter.print(subId);
		ownershipGroupPrinter.println();
	}

	/*Print sample to group Membership
	* @param sampleAcc accession of the sample
	* @param groupAcc accession of the group
	* */
	private synchronized void printMembership(String sampleAcc, String groupAcc) throws IOException {
		membershipPrinter.print(sampleAcc);
		membershipPrinter.print(groupAcc);
		membershipPrinter.println();
	}

	/*Prints derivation relationship
	* @param productAcc sample accession
	* @param sourceAcc sample accession
	* */
	private synchronized void printDerivation(String productAcc, String sourceAcc) throws IOException {
		derivationPrinter.print(productAcc);
		derivationPrinter.print(sourceAcc);
		derivationPrinter.println();
	}

	/*Prints as as relationship
	* @param sample accession
	* @param sample accession
	* */
	private synchronized void printSameAs(String acc, String otherAcc) throws IOException {
		sameAsPrinter.print(acc);
		sameAsPrinter.print(otherAcc);
		sameAsPrinter.println();
	}

	/*Prints childOf relationship
	* @param sample accession
	* @param sample accession
	* */
	private synchronized void printChildOf(String acc, String otherAcc) throws IOException {
		childOfPrinter.print(acc);
		childOfPrinter.print(otherAcc);
		childOfPrinter.println();
	}

	/*Prints Recurated From relationship
	* @param sample accession
	* @param sample accession
	* */
	private synchronized void printRecuratedFrom(String acc, String otherAcc) throws IOException {
		ReCuratedFromPrinter.print(acc);
		ReCuratedFromPrinter.print(otherAcc);
		ReCuratedFromPrinter.println();
	}


	/*
	* @param url url of a database link
	* */
	private synchronized void printDbNode(String url) throws IOException {
		DbNodePrinter.print(url);
		DbNodePrinter.println();
	}

	/*
	* Saves the relationship between db url and sample
	* */
	private synchronized void printRefsSampleRel(String acc, String url) throws IOException{
		dbRefGroupRel.print(acc);
		dbRefGroupRel.print(url);
		dbRefGroupRel.println();
	}

	/*
	* Saves the relationship between db url and group
	*/
	private synchronized void printRefsGroupRel(String acc, String url) throws IOException{
		DbRefsPrinter.print(acc);
		DbRefsPrinter.print(url);
		DbRefsPrinter.println();
	}

	/* Converts DbRefs coming from an Entity as stored in the myEquivalence model
	* @param dbRefs A Set<Entity> as stored in myEquivalence
	*/
	private Set<String> covertMyEquiEntity(Set<Entity> dbRefs){
		Set<String> list = new HashSet<String>();
		for (Entity ref :dbRefs){
			list.add(ref.getURI());
		}
		return list;
	}

	/*Converts DbRefs coming from the Hibernate model into Set of Strings
	* @param dbRefs A Set <DatabaseRecordRef>  as stored in the model
	*/
	private Set <String> convertDbRefs(Set <DatabaseRecordRef> dbRefs){
		Set<String> list = new HashSet<String>();
		for (DatabaseRecordRef ref : dbRefs){
			list.add(ref.getUrl());
		}
		return list;
	}



	/*
	* Main function that actually runs through the samples and groups, gets the relevant data and calles the printer functions
	* @param msi Submission msi
	* */
	public void handle(MSI msi) throws IOException {
		
		if (valid(msi)) {
			//get outside of sync block as it might call database and take a while
			printSubmission(msi.getAcc());
			for (BioSample sample : msi.getSamples()) {
				if (valid(sample)) {

					Set <String> dburls= new HashSet<String>();
					dburls.addAll(convertDbRefs(sample.getDatabaseRecordRefs()));
					dburls.addAll(covertMyEquiEntity(myEquivalenceManager.getSampleExternalEquivalences(sample.getAcc())));

					//IF we model DBRefs as own node, get rid of dburls parameter here, we don't need the urls as properties then
					printSample(sample.getAcc(), dburls);

					/*	To model the db links as own relationships: Run over dburls and create for every string an entry in DbNode as well as a
					* connection between the group and the url
					*/
					for (String url : dburls)
					{
						printRefsSampleRel(sample.getAcc(), url);
						printDbNode(url);
					}


					printSampleOwnership(sample.getAcc(),msi.getAcc());

					//this is the slow join
					for (ExperimentalPropertyValue<?> epv: sample.getPropertyValues()) {
						ExperimentalPropertyType ept = epv.getType();

						if ("derived from".equals(ept.getTermText().toLowerCase())) {
							printDerivation(sample.getAcc(), epv.getTermText());
						}

						if("same as".equals(ept.getTermText().toLowerCase())){
							printSameAs(sample.getAcc(), epv.getTermText());
						}

						//to convert to lower case does not seem to work. no idea why. for the sake of it I keep in the if clause just to make sure we catch all
						if ("Child Of".equals(ept.getTermText()) || "child of".equals(ept.getTermText().toLowerCase())) {
								printChildOf(sample.getAcc(),epv.getTermText());
						}

						if ("recurated from".equals(ept.getTermText().toLowerCase())){
							printRecuratedFrom(sample.getAcc(),epv.getTermText());
						}

					}

				}
			}

			for (BioSampleGroup group : msi.getSampleGroups()) {
				if (valid(group)) {

					Set <String> dburls= new HashSet<String>();
					dburls.addAll(convertDbRefs(group.getDatabaseRecordRefs()));
					dburls.addAll(covertMyEquiEntity(myEquivalenceManager.getSampleExternalEquivalences(group.getAcc())));

					//IF we model DBRefs as own node, get rid of dburls parameter here, we don't need the url as property
					printGroup(group.getAcc(), dburls);

					/*	To model the db links as own relationships: Run over dburls and create for every string an entry in DbNode as well as a
					* connection between the group and the url
					*/
					for (String url : dburls)
					{
						printRefsGroupRel(group.getAcc(), url);
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

		//Should never be executed
		else {			System.out.println("NOT VALID MSI"); 		}


	}




}
