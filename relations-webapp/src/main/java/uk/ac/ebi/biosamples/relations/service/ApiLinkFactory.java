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
        String url = biosamplesWebServerUrl + (biosamplesWebServerUrl.endsWith("/") ? "samples/" : "/samples/") +
                sample.getAccession();
        return new Link(url, "details");
    }

    public Link createApiLinkForGroup(Group group) {
        String url = biosamplesWebServerUrl + (biosamplesWebServerUrl.endsWith("/") ? "groups/" : "/groups/") +
                group.getAccession();
        return new Link(url, "details");
    }

}
