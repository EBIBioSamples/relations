package uk.ac.ebi.biosamples.relations.clientlib.rao;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.ebi.biosamples.relations.clientlib.exceptions.NoSuchSubmissionException;
import uk.ac.ebi.biosamples.relations.clientlib.exceptions.DuplicateGroupException;
import uk.ac.ebi.biosamples.relations.clientlib.exceptions.DuplicateSubmissionException;
import uk.ac.ebi.biosamples.relations.model.edges.Ownership;
import uk.ac.ebi.biosamples.relations.model.nodes.Group;
import uk.ac.ebi.biosamples.relations.model.nodes.Submission;

public class GroupRestAccessObject {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private RestTemplate rest = null;
	
	//probably want to autowire this to a config
	private final String root;
	
	private SubmissionRestAccessObject subRAO = null;
	
	//private SampleRestAccessObject sRAO;

	public GroupRestAccessObject(String root) {
		this.root = root;
	}
	
	public RestTemplate getRestTemplate() {
		///create it if not exists
		if (rest == null) {
			
			//setup handling hal+json responses
			rest = new RestTemplate();
			
			//need to create a new message converter to handle hal+json
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.registerModule(new Jackson2HalModule());
			MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
			converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
			converter.setObjectMapper(mapper);

			//add the new converters to the restTemplate
			//but make sure it is BEFORE the existing converters
			List<HttpMessageConverter<?>> converters = rest.getMessageConverters();
			converters.add(0,converter);
			rest.setMessageConverters(converters);
			
		}
		return rest;
	}
	
	public void setRestTemplate(RestTemplate rest) {
		this.rest = rest;
	}
	
	public SubmissionRestAccessObject getSubRAO() {
		return subRAO;
	}

	public void setSubRAO(SubmissionRestAccessObject subRAO) {
		this.subRAO = subRAO;
	}
	
	public Optional<Resource<Group>> getGroup(String accession) {
		// construct the appropriate URI
		URI uri;
		try {
			uri = (new URIBuilder()).setPath(root + "/groups/search/findOneByAccession")
					.addParameter("accession", accession).build();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

		ResponseEntity<Resource<Group>> responseEntity;
		try {
			responseEntity = rest.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<Resource<Group>>() {
					});
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				return Optional.empty();
			} else {
				throw e;
			}
		}
		return Optional.of(responseEntity.getBody());
	}
	
	public Optional<Resource<Group>> getGroupWithNeighbours(String accession) {
		Optional<Resource<Group>> groupResourceOptional = getGroup(accession);
		if (!groupResourceOptional.isPresent()) {
			//couldn't get a group, so pass along
			return groupResourceOptional;
		}
				
		//add the submission that owns the group
		Resource<Group> groupResource = groupResourceOptional.get();
		Group group = groupResource.getContent();
		Optional<Resource<Submission>> subResourceOptional = subRAO.getSubmissionOwningGroup(accession);
		if (subResourceOptional.isPresent()) {
			group.setOwner(subResourceOptional.get().getContent());
		}
		//add the samples members of this group
		//TODO
		
		return groupResourceOptional;
	}
	
	public Resources<Group> getGroupsOwnedBySubmission(String submissionId) {

		URI uri;
		try {
			uri = (new URIBuilder()).setPath(root + "/groups/search/findGroupsOwnedBySubmissionBySubmissionId")
					.addParameter("submissionId", submissionId).build();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		ResponseEntity<Resources<Group>> responseEntity = rest.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<Resources<Group>>() {
					});		
		return responseEntity.getBody();
	}

	public void persistNovelGroup(Group group) throws DuplicateGroupException {
		//try to get any existing one first
		Optional<Resource<Group>> existingGroup = getGroup(group.getAccession());
				
		if (existingGroup.isPresent()) {
			throw new DuplicateGroupException();
		}
		
		//does not exist, so create it via POST
		URI uri;
		try {
			uri = (new URIBuilder()).setPath(root + "/groups").build();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Group> httpEntity = new HttpEntity<>(group, headers);
		
		rest.exchange(uri, HttpMethod.POST, httpEntity,
				new ParameterizedTypeReference<Resource<Group>>() {
				});
		
		//if the group has a submission, persist that
		//TODO
		//if the group has samples, persist those
		//TODO
	}
	
	public void updateGroup(Group group) throws NoSuchSubmissionException {
		//try to get any existing one first
		Optional<Resource<Group>> existingGroup = getGroup(group.getAccession());
		
		if (!existingGroup.isPresent()) {
			//no existing submission to update
			throw new NoSuchSubmissionException();
		}
				
		URI uri;
		try {
			uri = new URI(existingGroup.get().getLink("self").getHref());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Group> httpEntity = new HttpEntity<>(group, headers);
		
		rest.exchange(uri, HttpMethod.PUT, httpEntity,
				new ParameterizedTypeReference<Resource<Group>>() {
				});
	}

}
