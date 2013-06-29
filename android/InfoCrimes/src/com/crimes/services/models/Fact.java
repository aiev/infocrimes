package com.crimes.services.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Fact {

	@JsonProperty("id")
	private long id;
	
	@JsonProperty("fact_type_name")
	private String factType;

	@JsonProperty("fact_type")
	private int factTypeId;
	
	@JsonProperty("occurrence_date")
	private String occurrenceDate;
	
	@JsonProperty("occurrence_address")
	private String occurrenceAddress;
	
	@JsonProperty("lon")
	private double longitude;
	
	@JsonProperty("lat")
	private double latitude;

	@JsonProperty("geo_point")
	private String geoPoint;
	@JsonProperty("map_point")
	private String mapPoint;
	
	@JsonProperty("desc")
	private String description;
	@JsonProperty("name")
	private String name;

	@JsonProperty("url")
	private String url;

	@JsonIgnore()
	private boolean isActive;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getFactType() {
		return factType;
	}
	public void setFactType(String factType) {
		this.factType = factType;
	}
	
	public int getFactTypeId() {
		return factTypeId;
	}
	public void setFactTypeId(int factTypeId) {
		this.factTypeId = factTypeId;
	}

	public String getOccurrenceDate() {
		return occurrenceDate;
	}
	public void setOccurrenceDate(String occurrenceDate) {
		this.occurrenceDate = occurrenceDate;
	}

	public String getOccurrenceAddress() {
		return occurrenceAddress;
	}
	public void setOccurrenceAddress(String occurrenceAddress) {
		this.occurrenceAddress = occurrenceAddress;
	}
	
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
