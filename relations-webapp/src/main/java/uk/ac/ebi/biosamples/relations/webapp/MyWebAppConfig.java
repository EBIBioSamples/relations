package uk.ac.ebi.biosamples.relations.webapp;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


import org.neo4j.ogm.config.Configuration;

/*  Imports needed for the ResourceProcessor  */
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import uk.ac.ebi.biosamples.relations.model.Sample;
import uk.ac.ebi.biosamples.relations.model.Group;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;


/**
 * Created by tliener on 20/04/2016.
 */


@SpringBootApplication
@EnableTransactionManagement
@EnableNeo4jRepositories("uk.ac.ebi.biosamples.relations")
public class MyWebAppConfig extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(new Class[]{MyWebAppConfig.class, MyNeo4JConfig.class});
    }

    /*
    * This function adds a Link to the Sample resource
    * */
    @Bean
    public ResourceProcessor<Resource<Sample>> sampleProcessor(){
        return new ResourceProcessor<Resource<Sample>>(){
            @Override
            public Resource<Sample> process(Resource <Sample> resource)
            {
                //get the Sample accession through the resource object, add this to the new Link in order to produce valid link
                String accession=resource.getContent().getAccession();
                resource.add(new Link("http://whatever - for sample "+accession, "Additional Information"));
                resource.add(new Link(resource.getLink("self").getHref()+"/graph", "graph"));
                return resource;
            }

        };
    }

    /*
    * This function adds a Link to the Group resource
    * */
    @Bean
    public ResourceProcessor<Resource<Group>> groupProcessor(){
        return new ResourceProcessor<Resource<Group>> () {
            @Override
            public  Resource<Group> process(Resource <Group> resource)
            {
                //get the GROUPs accession through the resource object, add this to the new Link in order to produce valid link
                String accession=resource.getContent().getAccession();
                resource.add(new Link("http://whatever for group "+accession, "Additional Information"));
                resource.add(new Link(resource.getLink("self").getHref()+"/graph", "graph"));
                return resource;
            }
        };
    }



}
