package com.crimes.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.crimes.services.IOccurrenceService;
import com.crimes.services.models.Fact;
import com.crimes.services.models.FactContainer;
import com.crimes.services.models.FactType;
import com.crimes.services.models.FactTypeContainer;
import com.crimes.utils.MappingJackson2HttpMessageConverter;
import com.google.android.gms.maps.model.VisibleRegion;


public class OccurrenceService implements IOccurrenceService {

	private static final String BASE_URL = "http://10.1.1.8:8004";

	public Collection<Fact> getByRegion(VisibleRegion region) {
		/*
			farLeft  +----------o farRight
					 |          |
					 |          |
					 |          |
			nearLeft o----------+ nearRight
		*/
		
		String maxX = Double.toString(region.farLeft.longitude) + "," + Double.toString(region.farLeft.latitude);
		String maxY = Double.toString(region.farRight.longitude) + "," + Double.toString(region.farRight.latitude);

		String minX = Double.toString(region.nearLeft.longitude) + "," + Double.toString(region.nearLeft.latitude);
		String minY = Double.toString(region.nearRight.longitude) + "," + Double.toString(region.nearRight.latitude);
		
		final String url = BASE_URL + "/api/facts/?maxX={maxX}&maxY={maxY}&minX={minX}&minY={minY}";
		
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		
		ResponseEntity<FactContainer> responseEntity = restTemplate.exchange(url, HttpMethod.GET, 
				getRequestEntity(), FactContainer.class, maxX, maxY, minX, minY);
		return responseEntity.getBody().getFacts();
	}
	
	public Collection<Fact> getByGeoPoint(double longitude, double latitude, int dist) {
		final String url = BASE_URL + "/api/facts/?lon={longitude}&lat={latitude}&dist={dist}";
		
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		
		ResponseEntity<FactContainer> responseEntity = restTemplate.exchange(url, HttpMethod.GET, 
				getRequestEntity(), FactContainer.class, longitude, latitude, dist);
		return responseEntity.getBody().getFacts();
	}

	@Override
	public Collection<FactType> getFactTypes() {
		final String url = BASE_URL + "/api/facttypes/";
		
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		
		ResponseEntity<FactTypeContainer> responseEntity = restTemplate.exchange(url, HttpMethod.GET, 
				getRequestEntity(), FactTypeContainer.class);
		return responseEntity.getBody().getFactTypes();
	}
	
	public Fact getById(long id) {
		// The URL for making the GET request
		final String url = BASE_URL + "/api/facts/{id}/";
		// This is where we get the RestTemplate and add the message converters
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		return restTemplate.getForObject(url, Fact.class, id);
	}

	public boolean deleteById(long id) {
		final String url = BASE_URL + "/api/facts/{id}/";
		// This is where we get the RestTemplate and add the message converters
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(
				new MappingJackson2HttpMessageConverter());

		restTemplate.delete(url, Fact.class, id);
		return true;
	}

	public boolean createNew(Fact fact) {

		String url = BASE_URL + "/api/facts/";
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAccept(Collections.singletonList(new MediaType(
				"application", "json")));
		// Set the Content-Type header
		requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		// create the request body
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		//body.add("user[id]", String.valueOf(fact.getId()));
		body.add("fact_type", Integer.toString(fact.getFactTypeId()));
		body.add("occurrence_address", fact.getOccurrenceAddress());
		body.add("occurrence_date", fact.getOccurrenceDate());
		body.add("lat", Double.toString(fact.getLatitude()));
		body.add("lon", Double.toString(fact.getLongitude()));
		body.add("name", fact.getName());
		body.add("desc", fact.getDescription());
		body.add("map_point", "");
		body.add("geo_point", "");
		
		// create the request entity
		HttpEntity<?> requestEntity = new HttpEntity<Object>(body, requestHeaders);
		// Get the RestTemplate and add the message converters
		RestTemplate restTemplate = new RestTemplate();

		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		messageConverters.add(new FormHttpMessageConverter());
		messageConverters.add(new StringHttpMessageConverter());
		restTemplate.setMessageConverters(messageConverters);
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
			//Toast.makeText(, text, duration)
			Log.i("response", response.toString());
			HttpStatus status = response.getStatusCode();
			if (status == HttpStatus.CREATED) {
				return true;
			} else {
				return false;
			}
		} catch (HttpClientErrorException e) {
			Log.e("response", e.getResponseBodyAsString());
			e.printStackTrace();
			return false;
		}

	}
/*
	@Override
	public User updateUser(User updatedUser) {
		String url = BASE_URL + "updateuser?userid={id}";
		HttpHeaders requestHeaders = new HttpHeaders();
		// Set the Content-Type header
		requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		body.add("user[id]", String.valueOf(updatedUser.getId()));
		body.add("user[first_name]", updatedUser.getFirstName());
		body.add("user[last_name]", updatedUser.getLastName());

		// create the request entity
		HttpEntity<?> requestEntity = new HttpEntity<Object>(body,
				requestHeaders);
		RestTemplate restTemplate = new RestTemplate();

		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		messageConverters.add(new FormHttpMessageConverter());
		messageConverters.add(new StringHttpMessageConverter());
		restTemplate.setMessageConverters(messageConverters);
		try {
			ResponseEntity<User> response = restTemplate.exchange(url,
					HttpMethod.PUT, requestEntity, User.class,
					updatedUser.getId());
			HttpStatus status = response.getStatusCode();
			if (status == HttpStatus.CREATED) {
				return response.getBody();
			} else {
				return null;
			}
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			return null;
		}
	}*/

	private HttpEntity<?> getRequestEntity() {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAccept(Collections.singletonList(new MediaType(
				"application", "json")));
		return new HttpEntity<Object>(requestHeaders);
	}

}