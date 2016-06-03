package uk.ac.ebi.biosamples.relations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer; // new version
//import org.springframework.boot.context.web.SpringBootServletInitializer; //old version

import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;


import uk.ac.ebi.biosamples.relations.model.Group;
import uk.ac.ebi.biosamples.relations.model.Sample;


/**
 * Created by tliener on 20/04/2016.
 */
@SpringBootApplication
public class RelationsApplication extends SpringBootServletInitializer {

    // This function adds a Link to the Sample resource
    @Bean
    public ResourceProcessor<Resource<Sample>> sampleProcessor() {
        return new ResourceProcessor<Resource<Sample>>() {
            @Override
            public Resource<Sample> process(Resource<Sample> resource) {
                //get the Sample accession through the resource object, add this to the new Link in order to produce valid link
                String accession = resource.getContent().getAccession();

                String link=resource.getLink("self").getHref();

                //If block for protects from error msg on localhost, should not be relevant on the server
                if (link.indexOf("/relations")!=-1)
                    link=link.substring(0, link.indexOf("/relations"))+"/sample/"+accession;    //On the server, this should take you to the biosamples page of the sample
                else
                    link="could not parse link correctly, maybe you are working on localhost?";

                resource.add(new Link(link, "Go here for more details on "+accession));
                resource.add(new Link(resource.getLink("self").getHref() + "/graph", "graph"));
                return resource;
            }

        };
    }
    
    //This function adds a Link to the Group resource
    @Bean
    public ResourceProcessor<Resource<Group>> groupProcessor() {
        return new ResourceProcessor<Resource<Group>>() {
            @Override
            public Resource<Group> process(Resource<Group> resource) {
                //get the GROUPs accession through the resource object, add this to the new Link in order to produce valid link
                String accession = resource.getContent().getAccession();

                String link=resource.getLink("self").getHref();

                if (link.indexOf("/relations")!=-1)
                    link=link.substring(0,link.indexOf("/relations"))+"/group/"+accession;   //On the server, this should take you to the biosamples page of the sample
                else
                    link="could not parse link correctly, maybe you are working on localhost?";

                resource.add(new Link(link, "Additional Information about the group"));
                resource.add(new Link(resource.getLink("self").getHref() + "/graph", "graph"));
                return resource;
            }
        };
    }

    public static void main(String[] args) {
    	SpringApplication.run(RelationsApplication.class, args);
    }
}
