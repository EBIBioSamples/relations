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
	//private CSVPrinter nieceOrNephewPrinter;


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
		//nieceOrNephewPrinter = new CSVPrinter(new BufferedWriter(new FileWriter(new File(outpath, "nieceOrNephew.csv"))), CSVFormat.DEFAULT);
	}

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

	/*Generic print function for DBrefs from the MyEquivalent DB. Use for samples and groups*/
	private synchronized void printFromEq(String acc, Set<Entity> dbRefs, CSVPrinter printer) throws IOException{
		printer.print(acc);
		ArrayList<String> list=new ArrayList<String>();
		for (Entity ref :dbRefs){
			list.add(ref.getURI());
		}
		printer.print(list);
		printer.println();
	}

	/*Print DbRefs for sample and groups*/
	private synchronized  void printFromDBRef(String acc, Set <DatabaseRecordRef> dbRefs, CSVPrinter printer) throws IOException{
		printer.print(acc);
		ArrayList<String> list=new ArrayList<String>();
		for (DatabaseRecordRef ref : dbRefs){
			list.add(ref.getUrl());
		}
		printer.print(list);
		printer.println();

	}

	/*Print Sample without Db reference*/
	private synchronized void printSample(String acc) throws IOException{
		samplePrinter.print(acc);
		samplePrinter.println();
	}

	/* Print group without Database reference*/
	private synchronized void printGroup(String acc) throws IOException{
		groupPrinter.print(acc);
		groupPrinter.println();
	}


	private synchronized void printSampleOwnership(String acc, String subId) throws IOException {
		ownershipSamplePrinter.print(acc);
		ownershipSamplePrinter.print(subId);
		ownershipSamplePrinter.println();
	}


	private synchronized void printGroupOwnership(String acc, String subId) throws IOException {
		ownershipGroupPrinter.print(acc);
		ownershipGroupPrinter.print(subId);
		ownershipGroupPrinter.println();
	}
	
	private synchronized void printMembership(String sampleAcc, String groupAcc) throws IOException {
		membershipPrinter.print(sampleAcc);
		membershipPrinter.print(groupAcc);
		membershipPrinter.println();
	}
	
	private synchronized void printDerivation(String productAcc, String sourceAcc) throws IOException {
		derivationPrinter.print(productAcc);
		derivationPrinter.print(sourceAcc);
		derivationPrinter.println();
	}

	private synchronized void printSameAs(String acc, String otherAcc) throws IOException {
		sameAsPrinter.print(acc);
		sameAsPrinter.print(otherAcc);
		sameAsPrinter.println();
	}


	private synchronized void printChildOf(String acc, String otherAcc) throws IOException {
		childOfPrinter.print(acc);
		childOfPrinter.print(otherAcc);
		childOfPrinter.println();
	}

	private synchronized void printRecuratedFrom(String acc, String otherAcc) throws IOException{
		ReCuratedFromPrinter.print(acc);
		ReCuratedFromPrinter.print(otherAcc);
		ReCuratedFromPrinter.println();
	}

	/*
	private synchronized void printNieceOrNephew(String acc, String otherAcc) throws IOException{
		nieceOrNephewPrinter.print(acc);
		nieceOrNephewPrinter.print(otherAcc);
		nieceOrNephewPrinter.println();
	}*/


	public void handle(MSI msi) throws IOException {
		
		if (valid(msi)) {
			//get outside of sync block as it might call database and take a while
			printSubmission(msi.getAcc());
			for (BioSample sample : msi.getSamples()) {
				if (valid(sample)) {

					if (!sample.getDatabaseRecordRefs().isEmpty())
					{
						//printSampleWithDbRef(sample.getAcc(), sample.getDatabaseRecordRefs());
						printFromDBRef(sample.getAcc(), sample.getDatabaseRecordRefs(), samplePrinter);
					}

					if (!myEquivalenceManager.getSampleExternalEquivalences(sample.getAcc()).isEmpty()){
						//printSampleWithEq(sample.getAcc(), myEquivalenceManager.getSampleExternalEquivalences(sample.getAcc()));
						printFromEq(sample.getAcc(), myEquivalenceManager.getSampleExternalEquivalences(sample.getAcc()), samplePrinter);
					}

					if (sample.getDatabaseRecordRefs().isEmpty() && myEquivalenceManager.getSampleExternalEquivalences(sample.getAcc()).isEmpty()) {
						printSample(sample.getAcc());
					}

					printSampleOwnership(sample.getAcc(),msi.getAcc());

					//this is the slow join
					for (ExperimentalPropertyValue<?> epv: sample.getPropertyValues()) {
						ExperimentalPropertyType ept = epv.getType();

						if ("derived from".equals(ept.getTermText().toLowerCase())) {
							String otherAcc = epv.getTermText();
							//TODO check that otherAcc is a valid node accession
							printDerivation(sample.getAcc(), otherAcc);
						}

						if("sas".equals(ept.getTermText().toLowerCase())){
							printSameAs(sample.getAcc(), epv.getTermText());
						}

						//to convert to lower case does not seem to work. no idea why. for the sake of it I keep in the if clause just to make sure we catch all
						if ("Child Of".equals(ept.getTermText()) || "child of".equals(ept.getTermText().toLowerCase())) {
								printChildOf(sample.getAcc(),epv.getTermText());
						}

						if ("recurated from".equals(ept.getTermText().toLowerCase())){
							printRecuratedFrom(sample.getAcc(),epv.getTermText());
						}

						//If we want to save that as well
						/*if ("niece or nephew of".equals(ept.getTermText().toLowerCase())) {
							String otherAcc=epv.getTermText();
							String acc=sample.getAcc();
							printNieceOrNephew(acc, otherAcc);
						}*/
					}

				}
			}

			for (BioSampleGroup group : msi.getSampleGroups()) {
				if (valid(group)) {

					if (!group.getDatabaseRecordRefs().isEmpty()){
						printFromDBRef(group.getAcc(), group.getDatabaseRecordRefs(), groupPrinter);
					}
					if (!myEquivalenceManager.getGroupExternalEquivalences(group.getAcc()).isEmpty()){
						printFromEq(group.getAcc(), myEquivalenceManager.getSampleExternalEquivalences(group.getAcc()), groupPrinter);

					}
					if (group.getDatabaseRecordRefs().isEmpty() && myEquivalenceManager.getGroupExternalEquivalences(group.getAcc()).isEmpty()){
						printGroup(group.getAcc());
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
