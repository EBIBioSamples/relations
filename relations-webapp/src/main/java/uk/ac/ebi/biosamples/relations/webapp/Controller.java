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

/*
* This class is the implementation of additional API endpoints - the Endpoints provided by Spring are not enough for us.
* We needed graph endpoints for the visualisation as well as a biosamplesWeb endpoint to support the biosamples webpage
* with displaying the relationship data which only lives in Neo and not in solr
* */
@RestController
public class Controller {

	@Autowired
	private SampleRepository sampleRepository;

	@Autowired
	private GroupRepository groupRepository;

	/*
	* The endpoint constructs the GraphJSON for a specific group. A Group usually has multiple samples, that are connected
	* via 'membership' with the group. Since this is the only relationship for a group, the endpoint is simpler than the
	* corresponding endpoint for samples
	* @param accession of a group
	* @return Graph JSON for a group
	* */
	@CrossOrigin
	@RequestMapping(path = "groups/{accession}/graph", produces = {MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.GET)
	public JSONObject group(@PathVariable("accession") String accession) {
		Group tmp = groupRepository.findOneByAccession(accession);

		List<Map<String, String>> nodes = new ArrayList<>();
		List<Map<String, String>> edges = new ArrayList<>();

		/* Add the group start node */
		nodes.add(constructNode(accession, accession, "groups"));

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


	/*
	* Enpoint to create the graph object for samples. The Graph JSON is constructed without spring but with simpleJSON
	* All relationship types for the sample Object are tested, and in case they are not null, processed - namely a the
	* constructNode as well as the constructEdge function are called and their return value is added to the ArrayList nodes
	 * and edges, which combined represent the returned Graph JSON Object
	* @param accession the accession of the function
	* @return Graph JSON for a sample
	* */
	@CrossOrigin
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
				edges.add(constructEdge(accession, sample.getAccession(), "OFFSPRING"));
			}
		}

		/*Get Parent data*/
		if (tmp.getParent()!=null){
			for (Sample sample : tmp.getParent()){
				nodes.add(constructNode(sample.getAccession(), sample.getAccession(), "samples"));
				edges.add(constructEdge(sample.getAccession(), accession, "OFFSPRING"));
			}

		}

		/*Get RecuratedFrom data*/
		 if (tmp.getRecuratedFrom()!=null) {
			 for (Sample sample : tmp.getRecuratedFrom()) {
				 nodes.add(constructNode(sample.getAccession(), sample.getAccession(), "samples"));
				 edges.add(constructEdge(sample.getAccession(), accession, "RECURATED"));
			 }
		 }

		/*Get RecuratedInto data*/
		if (tmp.getRecuratedInto()!=null){
			for (Sample sample : tmp.getRecuratedInto()){
				nodes.add(constructNode(sample.getAccession(), sample.getAccession(), "samples"));
				edges.add(constructEdge(accession, sample.getAccession(), "RECURATED"));
			}

		}

		/*Get the group the sample is member of*/
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


	/*
	* This endpoint is specifically for the biosamples-web project. Via jsonSimple a json file is constructed, that anwsers
	* the question which relationship the sample has - specifially to be displayed in the thymleaf template. No other information
	* is transfered, which should make this endpoint quick.
	* @param accession of a sample
	* @return json used in the biosample-web project to display the relationship between samples
	* */
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


		list=new ArrayList<String>();
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
		//Adding parentOf to json reply
		if (tmp.getParent()!=null)
		{
			for (Sample tmpSample:tmp.getParent())
			{			list.add(tmpSample.getAccession()); 	}

		}
		json.put("parentOf", list);



		list=new ArrayList<String>();
		//Adding sameAs to the json reply
		if (tmp.getSameAs()!=null)
		{	for (Sample tmpSample : tmp.getSameAs())
			{			list.add(tmpSample.getAccession());			}
		}
		json.put("sameAs", list);

		list=new ArrayList<String>();
		//Adding curatedInto the json reply
		if (tmp.getRecuratedInto()!=null)
		{	for (Sample tmpSample : tmp.getRecuratedInto())
			{			list.add(tmpSample.getAccession());			}
		}
		json.put("ReCuratedInto", list);

		list=new ArrayList<String>();
		//Adding recuratedFrom to the json reply
		if (tmp.getRecuratedFrom()!=null) {
			for (Sample tmpSample : tmp.getRecuratedFrom())
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
