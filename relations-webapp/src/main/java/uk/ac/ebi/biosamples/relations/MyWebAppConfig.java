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
public class MyWebAppConfig extends SpringBootServletInitializer {


    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigIn() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
    //This function adds a Link to the Sample resource
    @Bean
    public ResourceProcessor<Resource<Sample>> sampleProcessor(){
        return new ResourceProcessor<Resource<Sample>>(){
            @Override
            public Resource<Sample> process(Resource <Sample> resource)
            {
                //get the Sample accession through the resource object, add this to the new Link in order to produce valid link
                String accession=resource.getContent().getAccession();
                resource.add(new Link("http://whatever - for sample "+accession, "Additional Information"));
                return resource;
            }

        };
    }

    //This function adds a Link to the Group resource
    @Bean
    public ResourceProcessor<Resource<Group>> groupProcessor(){
        return new ResourceProcessor<Resource<Group>> () {
            @Override
            public  Resource<Group> process(Resource <Group> resource)
            {
                //get the GROUPs accession through the resource object, add this to the new Link in order to produce valid link
                String accession=resource.getContent().getAccession();
                resource.add(new Link("http://whatever for group "+accession, "Additional Information"));
                return resource;
            }
        };
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        //return application.sources(MyWebAppConfig.class);
    	return application.sources(MyNeo4JConfig.class);
    }
    
    public static void main(String[] args) throws Exception {
        //SpringApplication.run(new Class[]{Main.class, MyWebAppConfig.class, MyNeo4JConfig.class}, args);
    	//SpringApplication.run(MyWebAppConfig.class, args);
    	SpringApplication.run(MyNeo4JConfig.class, args);
    }


}
