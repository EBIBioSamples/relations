package uk.ac.ebi.biosamples.relations.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;
import uk.ac.ebi.biosamples.relations.model.Group;
import uk.ac.ebi.biosamples.relations.model.Sample;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 15/06/16
 */
@Component
public class ApiLinkFactory {
    @Value("${biosamples.web.server:http://www.ebi.ac.uk/biosamples/api}")
    private String biosamplesWebServerUrl;

    public Link createApiLinkForSample(Sample sample) {
        return new Link(biosamplesWebServerUrl + "/samples/" + sample.getAccession(), "details");
    }

    public Link createApiLinkForGroup(Group group) {
        return new Link(biosamplesWebServerUrl + "/groups/" + group.getAccession(), "details");
    }

}
