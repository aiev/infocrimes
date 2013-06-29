package com.crimes.services.models;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FactTypeContainer {
	 
    @JsonProperty("count")
    private int count;
    		
    @JsonProperty("next")
	private String next;
    
    @JsonProperty("previous")
	private String previous;
	
    @JsonProperty("results")
	private Collection<FactType> factTypes;
	 
	public Collection<FactType> getFactTypes() {
		return factTypes;
	}
	public void setFactTypes(Collection<FactType> factTypes) {
		this.factTypes = factTypes;
	}
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}