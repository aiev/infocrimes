package com.crimes.services.models;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FactContainer {
	 
    @JsonProperty("count")
    private int count;
    		
    @JsonProperty("next")
	private String next;
    
    @JsonProperty("previous")
	private String previous;
	
    @JsonProperty("results")
	private Collection<Fact> facts;
	 
	public Collection<Fact> getFacts() {
		return facts;
	}
	public void setFacts(Collection<Fact> facts) {
		this.facts = facts;
	}
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}