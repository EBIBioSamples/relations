package uk.ac.ebi.biosamples.relations;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;


/**
 * Created by tliener on 20/04/2016.
 */


@Configuration
public class MyWebAppConfig extends SpringBootServletInitializer {
/*
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
*/
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Main.class);
    }


}
