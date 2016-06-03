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

	@CrossOrigin
	@RequestMapping(path = "groups/{accession}/graph", produces = {MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.GET)
	public JSONObject group(@PathVariable("accession") String accession) {
		Group tmp = groupRepository.findOneByAccession(accession);

		List<Map<String, String>> nodes = new ArrayList<>();
		List<Map<String, String>> edges = new ArrayList<>();

		/* Add the group start node */
		nodes.add(constructNode(accession, accession, "groups"));

		/*
		 * Submission is ignored as of now - might be deleted soon if
		 * (tmp.getOwner() != null) { Map<String, String> tmpNode = new
		 * HashMap<>(); tmpNode.put("iri", tmp.getOwner().getSubmissionId());
		 * tmpNode.put("label", tmp.getOwner().getSubmissionId());
		 * tmpNode.put("type", "submission"); nodes.add(tmpNode);
		 * 
		 * Map<String, String> tmpList = new HashMap<>(); tmpList.put("source",
		 * accession); tmpList.put("target", tmp.getOwner().getSubmissionId());
		 * tmpList.put("label", "OWNERSHIP"); edges.add(tmpList); }
		 */

		if (!tmp.getSamples().isEmpty()) {
			for (Sample sample : tmp.getSamples()) {
				nodes.add(constructNode(sample.getAccession(), sample.getAccession(), "samples"));
				edges.add(constructEdge(accession, sample.getAccession(), "MEMBERSHIP"));
			}
		}

		JSONObject json = new JSONObject();
		json.put("nodes", nodes);
		json.put("edges", edges);
		return json;
	}

	@RequestMapping(path = "samples/{accession}/graph", produces = {MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.GET)
	public JSONObject sample(@PathVariable("accession") String accession) {
		Sample tmp = sampleRepository.findOneByAccession(accession);
		List<Map<String, String>> nodes = new ArrayList<>();
		List<Map<String, String>> edges = new ArrayList<>();

		/* Add the sample start node (equals the accession) to the graph data */
		nodes.add(constructNode(accession, accession, "samples"));

		/*Get derivedFrom data*/
		if (tmp.getDerivedFrom()!=null) {
			for (Sample sample : tmp.getDerivedFrom()) {
				nodes.add(constructNode(sample.getAccession(), sample.getAccession(), "samples"));
				edges.add(constructEdge(sample.getAccession(), accession, "DERIVATION"));
			}

		}

		/*Get derivedTo data*/
		if (tmp.getDerivedTo()!=null) {
			for (Sample sample : tmp.getDerivedTo()) {
				nodes.add(constructNode(sample.getAccession(), sample.getAccession(), "samples"));
				edges.add(constructEdge(accession, sample.getAccession(), "DERIVATION"));
			}
		}

		/*Get sameAs data*/
		if (tmp.getSameAs()!=null) {
			for (Sample sample : tmp.getSameAs()) {
				nodes.add(constructNode(sample.getAccession(), sample.getAccession(), "samples"));
				edges.add(constructEdge(accession, sample.getAccession(), "SAMEAS"));
			}
		}

		/*Get childOf data*/
		if (tmp.getChildOf()!=null) {
			for (Sample sample : tmp.getChildOf()) {
				nodes.add(constructNode(sample.getAccession(), sample.getAccession(), "samples"));
				edges.add(constructEdge(accession, sample.getAccession(), "CHILDOF"));
			}
		}

		// Missing - Manually curated into
		 if (tmp.getReCuratedFrom()!=null) {
			 for (Sample sample : tmp.getReCuratedFrom()) {
				 nodes.add(constructNode(sample.getAccession(), sample.getAccession(), "samples"));
				 edges.add(constructEdge(sample.getAccession(), accession, "CURATED FROM"));
			 }
		 }

		if (tmp.getDerivedTo()!=null){
			for (Sample sample : tmp.getReCuratedInto()){
				nodes.add(constructNode(sample.getAccession(), sample.getAccession(), "samples"));
				edges.add(constructEdge(accession, sample.getAccession(), "CURATED INTO"));
			}

		}


		/*
		 * Get rid of submission - this part is about to be deleted soon if
		 * (tmp.getOwner() !=null) { Map<String, String> tmpNode = new
		 * HashMap<>(); tmpNode.put("iri", tmp.getOwner().getSubmissionId());
		 * tmpNode.put("label", tmp.getOwner().getSubmissionId());
		 * tmpNode.put("type", "submissions"); nodes.add(tmpNode);
		 * 
		 * Map<String, String> tmpList = new HashMap<>(); tmpList.put("source",
		 * accession); tmpList.put("target", tmp.getOwner().getSubmissionId());
		 * tmpList.put("label", "OWNERSHIP"); edges.add(tmpList);
		 * 
		 * nodes.add(constructNode()); edges.add(constructNode()); }
		 */

		if (tmp.getGroups()!=null) {
			for (Group group : tmp.getGroups()) {
				nodes.add(constructNode(group.getAccession(), group.getAccession(), "groups"));
				edges.add(constructEdge(accession, group.getAccession(), "MEMBERSHIP"));
			}
		}

		JSONObject json = new JSONObject();
		json.put("nodes", nodes);
		json.put("edges", edges);
		return json;
	}


	@CrossOrigin
	@RequestMapping(path= "samples/{accession}/biosamplesWeb", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
	public JSONObject biosamplesWeb(@PathVariable("accession") String accession){

		JSONObject json = new JSONObject();
		Sample tmp = sampleRepository.findOneByAccession(accession);

		ArrayList<String> list=new ArrayList<String>();

		//Adding derivedFrom to the json reply
		if (tmp.getDerivedFrom()!=null)
		{
			for (Sample tmpSample : tmp.getDerivedFrom())
			{			list.add(tmpSample.getAccession());			}
		}
		json.put("derivedFrom", list);


		//Adding derivedTo to the json reply
		if (tmp.getDerivedTo()!=null)
		{
			for (Sample tmpSample : tmp.getDerivedTo())
			{			list.add(tmpSample.getAccession());			}
		}
		json.put("derivedTo", list);

		list=new ArrayList<String>();
		//Adding childOf to the json reply
		if (tmp.getChildOf()!=null)
		{
			for (Sample tmpSample : tmp.getChildOf())
			{			list.add(tmpSample.getAccession());		}
		}
		json.put("childOf", list);


		list=new ArrayList<String>();
		//Adding derivedTo to the json reply
		if (tmp.getSameAs()!=null)
		{	for (Sample tmpSample : tmp.getSameAs())
			{			list.add(tmpSample.getAccession());			}
		}
		json.put("sameAs", list);

		list=new ArrayList<String>();
		//Adding derivedTo to the json reply
		if (tmp.getReCuratedInto()!=null)
		{	for (Sample tmpSample : tmp.getReCuratedInto())
			{			list.add(tmpSample.getAccession());			}
		}
		json.put("ReCuratedInto", list);

		list=new ArrayList<String>();
		//Adding derivedTo to the json reply
		if (tmp.getReCuratedFrom()!=null)
		{	for (Sample tmpSample : tmp.getReCuratedFrom())
			{			list.add(tmpSample.getAccession());			}
		}
		json.put("ReCuratedFrom", list);

		return json;
	}


	/*
	 * Constructing a Map<String, String> representing a Node in our model
	 */
	private Map<String, String> constructNode(String accession, String label, String type) {
		Map<String, String> tmpSource = new HashMap<>();
		tmpSource.put("iri", accession);
		tmpSource.put("label", label);
		tmpSource.put("type", type);
		return tmpSource;
	}

	/*
	 * Constructiong a Map<String, String> representing an Edge in our model.
	 */
	private Map<String, String> constructEdge(String source, String target, String edgeLabel) {
		Map<String, String> tmpList = new HashMap<>();
		tmpList.put("source", source);
		tmpList.put("target", target);
		tmpList.put("label", edgeLabel);
		return tmpList;
	}
}
