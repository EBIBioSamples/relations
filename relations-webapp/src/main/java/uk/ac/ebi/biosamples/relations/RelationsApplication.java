package uk.ac.ebi.biosamples.relations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
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
    /*
    * This function adds a Link to the Sample resource
    * */
    @Bean
    public ResourceProcessor<Resource<Sample>> sampleProcessor(){
        return resource -> {
            //get the Sample accession through the resource object, add this to the new Link in order to produce valid link
            String accession=resource.getContent().getAccession();
            resource.add(new Link("http://whatever - for sample "+accession, "Additional Information"));
            resource.add(new Link(resource.getLink("self").getHref()+"/graph", "graph"));
            return resource;
        };
    }

    /*
    * This function adds a Link to the Group resource
    * */
    @Bean
    public ResourceProcessor<Resource<Group>> groupProcessor(){
        return resource -> {
            //get the GROUPs accession through the resource object, add this to the new Link in order to produce valid link
            String accession=resource.getContent().getAccession();
            resource.add(new Link("http://whatever for group "+accession, "Additional Information"));
            resource.add(new Link(resource.getLink("self").getHref()+"/graph", "graph"));
            return resource;
        };
    }

    public static void main(String[] args) {
    	SpringApplication.run(RelationsApplication.class, args);
    }
}
