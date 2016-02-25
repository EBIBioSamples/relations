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
import uk.ac.ebi.biosamples.relations.clientlib.exceptions.DuplicateSubmissionException;
import uk.ac.ebi.biosamples.relations.model.nodes.Group;
import uk.ac.ebi.biosamples.relations.model.nodes.Sample;
import uk.ac.ebi.biosamples.relations.model.nodes.Submission;

public class SubmissionRestAccessObject {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private RestTemplate rest = null;
	
	//probably want to autowire this to a config
	private final String root;
	//private SampleRestAccessObject sRAO;
	private GroupRestAccessObject gRAO = null;

	public SubmissionRestAccessObject(String root) {
		this.root = root;
	}
	
	public GroupRestAccessObject getgRAO() {
		return gRAO;
	}

	public void setGroupRAO(GroupRestAccessObject gRAO) {
		this.gRAO = gRAO;
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
	
	/**
	 * Internal function for conveniently returning the submission object from a particlar URI
	 * 
	 * That URI might be the result of a query or a direct single URI
	 * 
	 * @param uri
	 * @return
	 */
	private Optional<Resource<Submission>> getSubmissionByURI(URI uri) {
		ResponseEntity<Resource<Submission>> responseEntity;
		try {
			responseEntity = getRestTemplate().exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<Resource<Submission>>() {
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

	/**
	 * Returns a submission object if the provided submissionId exists.
	 * 
	 * If the provided submissionId does not exist, then it throws NoSuchSubmissionException
	 * 
	 * In any other case (e.g. connection fail) then it throws a RestClientException
	 * 
	 * @param submissionId
	 * @return
	 * @throws RestClientException
	 * @throws NoSuchSubmissionException
	 */
	public Optional<Resource<Submission>> getSubmission(String submissionId) {
		URI uri;
		try {
			uri = (new URIBuilder()).setPath(root + "/submissions/search/findOneBySubmissionId")
					.addParameter("submissionId", submissionId).build();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		return getSubmissionByURI(uri);
	}
	
	public Optional<Resource<Submission>> getSubmissionOwningGroup(String groupAccession) {
		URI uri;
		try {
			uri = (new URIBuilder()).setPath(root + "/submissions/search/findSubmissionOwningGroupByAccession")
					.addParameter("accession", groupAccession).build();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		return getSubmissionByURI(uri);
	}
	
	public Optional<Resource<Submission>> getSubmissionOwningSample(String sampleAccession) {
		URI uri;
		try {
			uri = (new URIBuilder()).setPath(root + "/submissions/search/findSubmissionOwningSampleByAccession")
					.addParameter("accession", sampleAccession).build();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		return getSubmissionByURI(uri);
	}
	
	public Optional<Resource<Submission>> getSubmissionWithNeighbours(String submissionId) {
		Optional<Resource<Submission>> subResourceOptional = getSubmission(submissionId);
		if (!subResourceOptional.isPresent()) {
			//couldn't get a group, so pass along
			return subResourceOptional;
		}
		
		Resource<Submission> subResource = subResourceOptional.get();
		Submission sub = subResource.getContent();
		
		//add the sample owned by this submission
		//TODO
		//add the groups owned by this submission
		gRAO.getGroupsOwnedBySubmission(submissionId);
		//TODO
		
		return subResourceOptional;
	}

	public void persistNovelSubmission(Submission submission) throws DuplicateSubmissionException {
		//try to get any existing one first
		Optional<Resource<Submission>> existingSub = getSubmission(submission.getSubmissionId());
				
		if (existingSub.isPresent()) {
			throw new DuplicateSubmissionException();
		}
		
		//does not exist, so create it via POST
		URI uri;
		try {
			uri = (new URIBuilder()).setPath(root + "/submissions").build();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Submission> httpEntity = new HttpEntity<>(submission, headers);
		
		getRestTemplate().exchange(uri, HttpMethod.POST, httpEntity,
				new ParameterizedTypeReference<Resource<Submission>>() {
				});
		
		//persist any group or samples owned by this submission
	}
	
	public void updateSubmission(Submission submission) throws NoSuchSubmissionException {
		//try to get any existing one first
		Optional<Resource<Submission>> existingSub = getSubmission(submission.getSubmissionId());
		
		if (!existingSub.isPresent()) {
			//no existing submission to update
			throw new NoSuchSubmissionException();
		}
				
		URI uri;
		try {
			uri = new URI(existingSub.get().getLink("self").getHref());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Submission> httpEntity = new HttpEntity<>(submission, headers);
		
		getRestTemplate().exchange(uri, HttpMethod.PUT, httpEntity,
				new ParameterizedTypeReference<Resource<Submission>>() {
				});
	}

}
