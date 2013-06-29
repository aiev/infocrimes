package com.crimes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.gesture.GesturePoint;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.crimes.TouchableWrapper.UpdateMapAfterUserInterection;
import com.crimes.services.IOccurrenceService;
import com.crimes.services.impl.OccurrenceService;
import com.crimes.services.models.Fact;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

@SuppressLint("NewApi")
public class MapCrimeActivity extends Activity implements
		OnMyLocationChangeListener, OnMapClickListener, OnCameraChangeListener {

    private static final String TAG = "MapCrimeActivity";
	
	private GoogleMap map;
	private boolean refreshing;
	private MenuItem refreshMenuItem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_crime);

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		map.setMyLocationEnabled(true);
		map.setOnMyLocationChangeListener(this);
		map.setOnCameraChangeListener(this);
		map.setOnMapClickListener(this);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
	            ActionBar.DISPLAY_SHOW_CUSTOM);
		
		getWindow().setUiOptions(ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);
/*
		ViewGroup v = (ViewGroup)LayoutInflater.from(this)
		        .inflate(R.layout.conversation_list_actionbar, null);
		
		actionBar.setCustomView(v,
	            new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
	                    ActionBar.LayoutParams.WRAP_CONTENT,
	                    Gravity.CENTER_VERTICAL | Gravity.RIGHT));*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		
		refreshMenuItem = menu.findItem(R.id.action_loader);
		
		SearchView avSearch = (SearchView) menu.findItem(R.id.action_search).getActionView();

	    avSearch.setIconifiedByDefault(true);

	    
	    avSearch.setOnQueryTextListener(new OnQueryTextListener() {

	        @Override
	        public boolean onQueryTextSubmit(String query) {
	    		MapSearchOperation task = new MapSearchOperation();
	    		task.execute(query);
	    		
	    		setRefreshing(true);

	            return false;
	        }

			@Override
			public boolean onQueryTextChange(String query) {
				return false;
			}

	    });

		   
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;

		switch (item.getItemId()) {
		case R.id.action_add_crime:
			intent = new Intent(this, AddCrimeActivity.class);
			
			LatLng target = map.getCameraPosition().target;
			
			Bundle b = new Bundle();
			b.putDouble(AddCrimeActivity.PARAM_LATITUDE, target.latitude);
			b.putDouble(AddCrimeActivity.PARAM_LONGITUDE, target.longitude);
			intent.putExtras(b);
			
			startActivity(intent);

			return true;

		case R.id.action_my_loclas:
			intent = new Intent(this, MyLocalsActivity.class);
			startActivity(intent);
			return true;

		case R.id.action_settings:
			intent = new Intent(this, PreferenceActivity.class);
			startActivity(intent);
			return true;
			
		case R.id.action_loader:
			OccurrenceOperation task = new OccurrenceOperation();
			task.execute(map.getProjection().getVisibleRegion());
			
			setRefreshing(true);
			return true;

		}

		return false;
	}
	
	private void setRefreshing(boolean refreshing) {
	    this.refreshing = refreshing;
	    if(refreshMenuItem == null) return;

	    if(refreshing)
	        refreshMenuItem.setActionView(R.layout.actionbar_menuitem_progressbar);
	    else
	        refreshMenuItem.setActionView(null);
	}

	public void onMyLocationChange(Location lastKnownLocation) {
		Toast.makeText(this,
				"onMyLocationChange" + lastKnownLocation.toString(),
				Toast.LENGTH_SHORT).show();

		map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(
				lastKnownLocation.getLatitude(), lastKnownLocation
						.getLongitude())));
		map.animateCamera(CameraUpdateFactory.zoomTo(16));

		map.setOnMyLocationChangeListener(null);
	}

	public void onMapClick(LatLng arg0) {
		//Toast.makeText(this, "onMapClick" + arg0.toString(), Toast.LENGTH_SHORT).show();
	}

	public void onCameraChange(CameraPosition arg0) {		
		OccurrenceOperation task = new OccurrenceOperation();
		task.execute(map.getProjection().getVisibleRegion());
		
		setRefreshing(true);
	}

	private class MapSearchOperation extends
			AsyncTask<String, Void, List<Address>> {

		@Override
		protected List<Address> doInBackground(String... params) {
			String query = params[0];
			
			Geocoder geocoder = new Geocoder(getApplicationContext());
			List<Address> addressesFound = null;
			
			try {
				addressesFound = geocoder.getFromLocationName(query, 5);
			} catch (IOException e) {
				
			}
			
			return addressesFound;
		}

		@Override
		protected void onPostExecute(List<Address> result) {
			setRefreshing(false);

	        if (result != null && !result.isEmpty()) {
	    		map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(result.get(0).getLatitude(), result.get(0).getLongitude())));
	    		map.animateCamera(CameraUpdateFactory.zoomTo(16));
	        }
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	private class OccurrenceOperation extends
			AsyncTask<VisibleRegion, Void, Collection<Fact>> {

		@Override
		protected Collection<Fact> doInBackground(VisibleRegion... params) {
			VisibleRegion region = params[0];

			Collection<Fact> foundFacts = null;
			IOccurrenceService srv = new OccurrenceService();

			try {
				foundFacts = srv.getByRegion(region);
			} catch (Exception ex) {
				
			}
			
			return foundFacts;
		}

		@Override
		protected void onPostExecute(Collection<Fact> result) {
			setRefreshing(false);
			
			if (result == null)
				return;

			for (Fact f : result) {
				MarkerOptions newMarker = new MarkerOptions()
					.position(new LatLng(f.getLatitude(), f.getLongitude()))
					.title(f.getFactType())
					.snippet(f.getOccurrenceDate())
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				
				
				map.addMarker(newMarker);
						
			}
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

}
