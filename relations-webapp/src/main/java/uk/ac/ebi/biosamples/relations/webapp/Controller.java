package uk.ac.ebi.biosamples.relations.webapp;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
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



    public Object parseSample(String accession){
        Sample tmp = sampleRepository.findOneByAccession(accession);

        //Adding accession node to nodes
        Map<String, String> tmpSource=new HashMap();
        tmpSource.put("iri",accession);
        tmpSource.put("label", accession);
        nodes.add(tmpSource);

        if (tmp.getParent()!=null) {
            Map<String, String> tmpNode=new HashMap();
            tmpNode.put("iri",tmp.getParent().getAccession());
            tmpNode.put("label",tmp.getParent().getAccession());
            nodes.add(tmpNode);

            Map<String, String> tmpList=new HashMap();
            tmpList.put("source", accession);
            tmpList.put("target", tmp.getParent().getAccession());
            tmpList.put("label","DERIVATION");
            edges.add(tmpList);
        }

        if (tmp.getOwner()!=null) {
            Map<String, String> tmpNode=new HashMap();
            tmpNode.put("iri",tmp.getOwner().getSubmissionId());
            tmpNode.put("label",tmp.getOwner().getSubmissionId());
            nodes.add(tmpNode);

            Map<String, String> tmpList=new HashMap();
            tmpList.put("source", accession);
            tmpList.put("target",tmp.getOwner().getSubmissionId());
            tmpList.put("label","OWNERSHIP");
            edges.add(tmpList);
        }

        if (tmp.getGroups()!=null) {
            Map<String, String> tmpNode=new HashMap();
            tmpNode.put("iri",tmp.getGroups().getAccession());
            tmpNode.put("label",tmp.getGroups().getAccession());
            nodes.add(tmpNode);

            Map<String, String> tmpList=new HashMap();
            tmpList.put("source", accession);
            tmpList.put("target",tmp.getGroups().getAccession());
            tmpList.put("label","MEMBERSHIP");
            edges.add(tmpList);
        }

        JSONObject json=new JSONObject();
        json.put("nodes", nodes);
        json.put("edges", edges);
        return json;
    }





    public Object parseGroup(String accession){
        Group tmp=groupRepository.findOneByAccession(accession);

        //Adding accession node to nodes
        Map<String, String> tmpSource=new HashMap();
        tmpSource.put("iri",accession);
        tmpSource.put("label", accession);
        nodes.add(tmpSource);



        /*Maybe get rid of this part, since groups belong to Submissions and therefor it cna be implied that samples also belong to submission*/
        if (tmp.getOwner()!=null)
        {
            Map<String, String> tmpNode=new HashMap();
            tmpNode.put("iri", tmp.getOwner().getSubmissionId());
            tmpNode.put("label", tmp.getOwner().getSubmissionId());
            nodes.add(tmpNode);

            Map<String, String> tmpList=new HashMap();
            tmpList.put("source", accession);
            tmpList.put("target", tmp.getOwner().getSubmissionId());
            tmpList.put("label","OWNERSHIP");
            edges.add(tmpList);
        }

        if (tmp.getSamples()!=null)     {

            for (Sample sample : tmp.getSamples()) {
                Map<String, String> tmpNode=new HashMap();
                tmpNode.put("iri",sample.getAccession());
                tmpNode.put("label",sample.getAccession());
                nodes.add(tmpNode);

                Map<String, String> tmpList=new HashMap();
                tmpList.put("target", accession);
                tmpList.put("source", sample.getAccession());
                tmpList.put("label","MEMBERSHIP");
                edges.add(tmpList);
            }



        }

        JSONObject json=new JSONObject();
        json.put("nodes", nodes);
        json.put("edges", edges);
        return json;
    }


    @RequestMapping(path="graph", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    public Object test(String accession){

        nodes = new ArrayList<Map<String, String>>();
        edges = new ArrayList<Map<String, String>>();

        int groupOrSample=accession.indexOf('G');

        //'G' in the first 3 letters of the id indicate, if it is a Group or a sample!
        //Problem with submission with this test! If we want to offer submissions this has to change!
        if (groupOrSample<=4 && groupOrSample>=0)
        {
            return parseGroup(accession);
        }
        else
        {
            return parseSample(accession);
        }

    }

}
