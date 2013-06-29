package com.crimes.services;

import java.util.Collection;

import com.crimes.services.models.Fact;
import com.crimes.services.models.FactType;
import com.google.android.gms.maps.model.VisibleRegion;

public interface IOccurrenceService {
	public Fact getById(long id);
	public Collection<Fact> getByGeoPoint(double longitude, double latitude, int dist);
	public Collection<Fact> getByRegion(VisibleRegion region); 
	
	public Collection<FactType> getFactTypes();
	
	public boolean deleteById(long id);
	
	public boolean createNew(Fact fact);
}
