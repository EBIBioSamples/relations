package uk.ac.ebi.biosamples.relations.webapp;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.biosamples.relations.model.Group;
import uk.ac.ebi.biosamples.relations.model.Sample;
import uk.ac.ebi.biosamples.relations.repo.GroupRepository;
import uk.ac.ebi.biosamples.relations.repo.SampleRepository;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tliener on 20/04/2016.
 */

@RestController
public class Controller {

	@Autowired
	private SampleRepository sampleRepository;

	@Autowired
	private GroupRepository groupRepository;

	private List<Map<String, String>> nodes;
	private List<Map<String, String>> edges;


	public JSONObject parseSample(String accession) {
		Sample tmp = sampleRepository.findOneByAccession(accession);

		System.out.println("In parse Sample");

		// Adding accession node to nodes
		Map<String, String> tmpSource = new HashMap<>();
		tmpSource.put("iri", accession);
		tmpSource.put("label", accession);
		tmpSource.put("type", "samples");
		nodes.add(tmpSource);


		if (tmp.getDerivedFrom() != null) {
			for (Sample sample : tmp.getDerivedFrom()) {
				System.out.println(sample.getAccession());

				Map<String, String> tmpNode = new HashMap<>();
				tmpNode.put("iri", sample.getAccession());
				tmpNode.put("label", sample.getAccession());
				tmpNode.put("type", "samples");
				nodes.add(tmpNode);

				Map<String, String> tmpList = new HashMap<>();
				tmpList.put("source", sample.getAccession());
				tmpList.put("target", accession);
				tmpList.put("label", "DERIVATION");
				edges.add(tmpList);
			}

		}

		if (tmp.getDerivedTo() != null) {
			for (Sample sample : tmp.getDerivedTo()) {
				System.out.println(sample.getAccession());
				Map<String, String> tmpNode = new HashMap<>();
				tmpNode.put("iri", sample.getAccession());
				tmpNode.put("label", sample.getAccession());
				tmpNode.put("type", "samples");
				nodes.add(tmpNode);

				Map<String, String> tmpList = new HashMap<>();
				tmpList.put("source", accession);
				tmpList.put("target", sample.getAccession());
				tmpList.put("label", "DERIVATION");
				edges.add(tmpList);
			}
		}


		if (tmp.getOwner() != null) {
			Map<String, String> tmpNode = new HashMap<>();
			tmpNode.put("iri", tmp.getOwner().getSubmissionId());
			tmpNode.put("label", tmp.getOwner().getSubmissionId());
			tmpNode.put("type", "submissions");
			nodes.add(tmpNode);

			Map<String, String> tmpList = new HashMap<>();
			tmpList.put("source", accession);
			tmpList.put("target", tmp.getOwner().getSubmissionId());
			tmpList.put("label", "OWNERSHIP");
			edges.add(tmpList);
		}

		if (tmp.getGroups()!=null){
			for (Group group : tmp.getGroups()) {
				Map<String, String> tmpNode = new HashMap<>();
				tmpNode.put("iri", group.getAccession());
				tmpNode.put("label", group.getAccession());
				tmpNode.put("type", "groups");
				nodes.add(tmpNode);

				Map<String, String> tmpList = new HashMap<>();
				tmpList.put("source", accession);
				tmpList.put("target", group.getAccession());
				tmpList.put("label", "MEMBERSHIP");
				edges.add(tmpList);
			}
		}


		JSONObject json = new JSONObject();
		json.put("nodes", nodes);
		json.put("edges", edges);
		System.out.println(json);
		return json;
	}



	public JSONObject parseGroup(String accession) {
		Group tmp = groupRepository.findOneByAccession(accession);

		System.out.println("In parse Group");

		// Adding accession node to nodes
		Map<String, String> tmpSource = new HashMap<>();
		tmpSource.put("iri", accession);
		tmpSource.put("label", accession);
		tmpSource.put("type", "groups");
		nodes.add(tmpSource);

		/*
		 * Maybe get rid of this part, since groups belong to Submissions and
		 * therefor it cna be implied that samples also belong to submission
		 */
		if (tmp.getOwner() != null) {
			Map<String, String> tmpNode = new HashMap<>();
			tmpNode.put("iri", tmp.getOwner().getSubmissionId());
			tmpNode.put("label", tmp.getOwner().getSubmissionId());
			tmpNode.put("type", "submission");
			nodes.add(tmpNode);

			Map<String, String> tmpList = new HashMap<>();
			tmpList.put("source", accession);
			tmpList.put("target", tmp.getOwner().getSubmissionId());
			tmpList.put("label", "OWNERSHIP");
			edges.add(tmpList);
		}


		if (tmp.getSamples()!=null) {
			for (Sample sample : tmp.getSamples()) {
				Map<String, String> tmpNode = new HashMap<>();
				tmpNode.put("iri", sample.getAccession());
				tmpNode.put("label", sample.getAccession());
				tmpNode.put("type", "samples");
				nodes.add(tmpNode);

				Map<String, String> tmpList = new HashMap<>();
				tmpList.put("target", accession);
				tmpList.put("source", sample.getAccession());
				tmpList.put("label", "MEMBERSHIP");
				edges.add(tmpList);
			}
		}

		JSONObject json = new JSONObject();
		json.put("nodes", nodes);
		json.put("edges", edges);
		System.out.println(json);
		return json;
	}


	@RequestMapping(path = "groups/{accession}/graph", produces = { MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.GET)
	public JSONObject group(@PathVariable("accession") String accession){
		nodes = new ArrayList<Map<String, String>>();
		edges = new ArrayList<Map<String, String>>();
		return parseGroup(accession);
	}

	@RequestMapping(path = "samples/{accession}/graph", produces = { MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.GET)
	//public @ResponseBody Graph test(String accession) {
	public JSONObject sample(@PathVariable("accession") String accession){
		nodes = new ArrayList<Map<String, String>>();
		edges = new ArrayList<Map<String, String>>();
		return parseSample(accession);
	}


	/* This endpoint does not exist yet, we have to decide if we want to have it or not
	@RequestMapping(path = "submission/{submissionId}/graph")
	public void submission(@PathVariable("submissionId") String submissionId){
		System.out.println("NO GRAPH data for submission available yet - should we have that one day?");
	}*/

}
