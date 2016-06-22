package uk.ac.ebi.biosamples.relations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer; 

import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;


import uk.ac.ebi.biosamples.relations.model.Group;
import uk.ac.ebi.biosamples.relations.model.Sample;
import uk.ac.ebi.biosamples.relations.service.ApiLinkFactory;


@SpringBootApplication
public class RelationsApplication extends SpringBootServletInitializer {
    @Autowired
    private ApiLinkFactory apiLinkFactory;

    // This function adds a Link to the Sample resource
    @Bean
    public ResourceProcessor<Resource<Sample>> sampleProcessor() {
        return new ResourceProcessor<Resource<Sample>>() {
            @Override
            public Resource<Sample> process(Resource<Sample> sampleResource) {
                sampleResource.add(apiLinkFactory.createApiLinkForSample(sampleResource.getContent()));
                sampleResource.add(new Link(sampleResource.getLink("self").getHref() + "/graph", "graph"));
                return sampleResource;
            }

        };
    }
    
    //This function adds a Link to the Group resource
    @Bean
    public ResourceProcessor<Resource<Group>> groupProcessor() {
        return new ResourceProcessor<Resource<Group>>() {
            @Override
            public Resource<Group> process(Resource<Group> groupResource) {
                groupResource.add(apiLinkFactory.createApiLinkForGroup(groupResource.getContent()));
                groupResource.add(new Link(groupResource.getLink("self").getHref() + "/graph", "graph"));
                return groupResource;
            }
        };
    }

    public static void main(String[] args) {
    	SpringApplication.run(RelationsApplication.class, args);
    }
}
