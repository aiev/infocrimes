package com.crimes;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.crimes.services.IOccurrenceService;
import com.crimes.services.impl.OccurrenceService;
import com.crimes.services.models.Fact;
import com.crimes.services.models.FactType;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NavUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class AddCrimeActivity extends Activity {

	private static final String TAG = "AddCrimeActivity";

	public static final String PARAM_LATITUDE = "latitude";
	public static final String PARAM_LONGITUDE = "longitude";

	private double latitude;
	private double longitude;

	private Spinner factType;
	private EditText description;
	private EditText occurrenceDate;
	private EditText occurrenceAddress;
	private ImageView staticMap;

	private boolean refreshing;
	private MenuItem refreshMenuItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_crime);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		Bundle b = getIntent().getExtras();
		latitude = b.getDouble(PARAM_LATITUDE);
		longitude = b.getDouble(PARAM_LONGITUDE);

		factType = (Spinner) findViewById(R.id.spinnerFactType);
		description = (EditText) findViewById(R.id.editDesc);
		occurrenceDate = (EditText) findViewById(R.id.editDateTime);
		occurrenceAddress = (EditText) findViewById(R.id.editAddress);
		staticMap = (ImageView) findViewById(R.id.staticMap);
	

		setRefreshing(true);
		
		OccurrenceTypeOperation task = new OccurrenceTypeOperation();
		task.execute();

		StaticMapOperation mapTask = new StaticMapOperation();
		mapTask.execute();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_crime, menu);

		refreshMenuItem = menu.findItem(R.id.action_loader);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;

		case R.id.action_save:
			if (refreshing)
				return true;

			Fact fact = new Fact();
			
			FactType currentFactType = (FactType) factType.getSelectedItem();
			//if (currentFactType != null)
			fact.setFactTypeId(currentFactType.getId());

			fact.setOccurrenceAddress(occurrenceAddress.getText().toString());
			fact.setLatitude(latitude);
			fact.setLongitude(longitude);
			fact.setOccurrenceDate("2012-05-14T01:47:00Z");
			fact.setDescription(description.getText().toString());
			fact.setName("");
			

			SaveOccurrenceOperation task = new SaveOccurrenceOperation();
			task.execute(fact);

			setRefreshing(true);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setRefreshing(boolean refreshing) {
		this.refreshing = refreshing;
		if (refreshMenuItem == null)
			return;

		if (refreshing)
			refreshMenuItem
					.setActionView(R.layout.actionbar_menuitem_progressbar);
		else
			refreshMenuItem.setActionView(null);
	}

	private class SaveOccurrenceOperation extends
			AsyncTask<Fact, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Fact... params) {
			Fact fact = params[0];
			
			IOccurrenceService srv = new OccurrenceService();

			try {
				return srv.createNew(fact);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			
			if (result) {
				Toast.makeText(AddCrimeActivity.this, "Fato salva com sucesso!", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(AddCrimeActivity.this, "Erro ao salvar fato!", Toast.LENGTH_SHORT).show();
			}

			setRefreshing(false);
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	private class OccurrenceTypeOperation extends
			AsyncTask<String, Void, Collection<FactType>> {

		@Override
		protected Collection<FactType> doInBackground(String... params) {
			Collection<FactType> foundFactTypes = null;
			IOccurrenceService srv = new OccurrenceService();

			try {
				foundFactTypes = srv.getFactTypes();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			return foundFactTypes;
		}

		@Override
		protected void onPostExecute(Collection<FactType> result) {

			if (result == null)
				return;
			
			List<FactType> resultList = new ArrayList(result);
			
			ArrayAdapter<FactType> adapter = new ArrayAdapter<FactType>(AddCrimeActivity.this, android.R.layout.simple_spinner_dropdown_item, resultList);
			factType.setAdapter(adapter);
/*

			ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
					AddCrimeActivity.this,
					android.R.layout.simple_spinner_dropdown_item,
					android.R.id.text1);
			spinnerAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			factType.setAdapter(spinnerAdapter);
			spinnerAdapter.add("Selecione o tipo do fato");

			for (FactType ft : result) {
				spinnerAdapter.add(ft.getName());
			}

			spinnerAdapter.notifyDataSetChanged();*/
			setRefreshing(false);
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}


	private class StaticMapOperation extends
			AsyncTask<String, Void, Drawable> {

		@Override
		protected Drawable doInBackground(String... params) {
			
			DisplayMetrics displayMetrics = new DisplayMetrics();
			WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
			wm.getDefaultDisplay().getMetrics(displayMetrics);
			int screenWidth = displayMetrics.widthPixels;

			String urlStaticMap = "http://maps.googleapis.com/maps/api/staticmap?center=" + Double.toString(latitude) + "," + Double.toString(longitude) + "&zoom=17&size=900x240&&markers=color:red|label:|" + Double.toString(latitude) + "," + Double.toString(longitude) + "&sensor=true";
			
			try {
				return Drawable.createFromStream((InputStream) new URL(urlStaticMap).getContent(), "src");
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Drawable result) {

			if (result != null)
				staticMap.setImageDrawable(result);

			setRefreshing(false);
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

}
