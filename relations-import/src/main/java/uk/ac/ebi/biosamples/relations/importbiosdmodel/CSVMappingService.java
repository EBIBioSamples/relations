package uk.ac.ebi.biosamples.relations.importbiosdmodel;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.io.Files;

import uk.ac.ebi.fg.biosd.model.expgraph.BioSample;
import uk.ac.ebi.fg.biosd.model.organizational.BioSampleGroup;
import uk.ac.ebi.fg.biosd.model.organizational.MSI;
import uk.ac.ebi.fg.core_model.expgraph.properties.ExperimentalPropertyType;
import uk.ac.ebi.fg.core_model.expgraph.properties.ExperimentalPropertyValue;

@Service
public class CSVMappingService implements Closeable {
	
	@Value("${neo4jIndexer.outpath:output}")
	private File outpath;

	private CSVPrinter samplePrinter;
	private CSVPrinter groupPrinter;
	private CSVPrinter submissionPrinter;
	private CSVPrinter ownershipSamplePrinter;
	private CSVPrinter ownershipGroupPrinter;
	private CSVPrinter membershipPrinter;
	private CSVPrinter derivationPrinter;

	@PostConstruct
	public void doSetup() throws IOException {
		//TODO create in temp dirs, then atomically swap into file location
		outpath.mkdirs();
		samplePrinter = new CSVPrinter(new FileWriter(new File(outpath, "sample.csv")), CSVFormat.DEFAULT);
		groupPrinter = new CSVPrinter(new FileWriter(new File(outpath, "group.csv")), CSVFormat.DEFAULT);
		submissionPrinter = new CSVPrinter(new FileWriter(new File(outpath, "submission.csv")), CSVFormat.DEFAULT);
		ownershipSamplePrinter = new CSVPrinter(new FileWriter(new File(outpath, "ownership_sample.csv")), CSVFormat.DEFAULT);
		ownershipGroupPrinter = new CSVPrinter(new FileWriter(new File(outpath, "ownership_group.csv")), CSVFormat.DEFAULT);
		membershipPrinter = new CSVPrinter(new FileWriter(new File(outpath, "membership.csv")), CSVFormat.DEFAULT);
		derivationPrinter = new CSVPrinter(new FileWriter(new File(outpath, "derivation.csv")), CSVFormat.DEFAULT);
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
	
	private synchronized void printSample(String acc) throws IOException {
		samplePrinter.print(acc);
		samplePrinter.println();
	}
	
	private synchronized void printGroup(String acc) throws IOException {
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


	public void handle(MSI msi) throws IOException {
		
		if (valid(msi)) {
			//get outside of sync block as it might call database and take a while
			printSubmission(msi.getAcc());
	
			for (BioSample sample : msi.getSamples()) {
				if (valid(sample)) {
					printSample(sample.getAcc());
					printSampleOwnership(sample.getAcc(),msi.getAcc());
					
					//this is the slow join
					for (ExperimentalPropertyValue<?> epv: sample.getPropertyValues()) {
						ExperimentalPropertyType ept = epv.getType();
						if ("derived from".equals(ept.getTermText().toLowerCase())) {
							String otherAcc = epv.getTermText();
							//TODO check that otherAcc is a valid node accession
							printDerivation(sample.getAcc(), otherAcc);
						}
					}
				}
			}
			
			for (BioSampleGroup group : msi.getSampleGroups()) {
				if (valid(group)) {
					printGroup(group.getAcc());
					printGroupOwnership(group.getAcc(),msi.getAcc());

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
