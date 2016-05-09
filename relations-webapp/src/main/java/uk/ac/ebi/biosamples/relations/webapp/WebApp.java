package uk.ac.ebi.biosamples.relations.webapp;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/*  Imports needed for the ResourceProcessor  */
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import uk.ac.ebi.biosamples.relations.model.Sample;
import uk.ac.ebi.biosamples.relations.model.Group;


/**
 * Created by tliener on 20/04/2016.
 */


@SpringBootApplication
@EnableTransactionManagement
@EnableNeo4jRepositories("uk.ac.ebi.biosamples.relations")
public class WebApp  extends Neo4jConfiguration {


    @Override
    @Bean
    public SessionFactory getSessionFactory() {
        // name of package containing domain objects here
        return new SessionFactory("uk.ac.ebi.biosamples.relations.model");
    }


    @Override
    @Bean
    public Session getSession() throws Exception {
        return super.getSession();
    }


    public static void main(String[] args) throws Exception {

        SpringApplication.run(WebApp.class, args);

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
                return resource;
            }
        };
    }



}
