package edu.uark.csce.mobile.dinogame;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class SummaryActivity extends ActionBarActivity {
	
	// Database
	private DinosDataSource datasource;
	
	public ArrayList<SimpleGeofence> geofences;
	public SimpleGeofenceStore mGeofenceStore;
	public ProgressDialog pDialog;
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_LOCATIONS = "locations";
	private static final String TAG_LONG = "longitude";
	private static final String TAG_LAT = "latitude";
	private static final String TAG_ID = "lid";
	private static final String TAG_RAD = "radius";
	private static final String TAG_EXP = "expiration";
	private static final String TAG_TRAN = "transition";
	private static final String TAG_ITEM = "item";

	public TextView t;

	// products JSONArray
		JSONArray locations = null;
	
	public final static String EXTRA_POSITION = "this.POSITION";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_summary);
		
		datasource = new DinosDataSource(this);
		datasource.open();
		
		ListView listview = (ListView) findViewById(R.id.listView1);
		listview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				Intent intent = new Intent(SummaryActivity.this, CharacterActivity.class);
				intent.putExtra(EXTRA_POSITION, position);
				startActivity(intent);
			}
		});

		// Store the data in the list view
		final List<DinoItem> dinoItems = datasource.getAllDinos();
		final ArrayAdapter<DinoItem> workoutAdapter = new ArrayAdapter<DinoItem>(this,
				android.R.layout.simple_expandable_list_item_1, dinoItems);
		listview.setAdapter(workoutAdapter);
		
		//create new array of geofence objects
		geofences = new ArrayList<SimpleGeofence>();
		mGeofenceStore = new SimpleGeofenceStore(this);
		t = (TextView) findViewById(R.id.textView1);
		//load locations from background thread
		new LoadLocations().execute();
		
		
		//updatelocal db with geofence objects
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.summary, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// Button listeners
	public void viewPlayer(View v) {
		Intent intent = new Intent(SummaryActivity.this, CharacterActivity.class);
		startActivity(intent);
	}
	
	public void viewAccount(View v) {
		Intent intent = new Intent(SummaryActivity.this, AccountActivity.class);
		startActivity(intent);
	}
	
	public void viewSettings(View v) {
		Intent intent = new Intent(SummaryActivity.this, SettingsActivity.class);
		startActivity(intent);
	}
	
	public void viewMap(View v) {
		Intent intent = new Intent(SummaryActivity.this, MapActivity.class);
		startActivity(intent);
	}
	
	public void createDino(View v) {
		Intent intent = new Intent(SummaryActivity.this, CreateDinoActivity.class);
		startActivity(intent);
	}

	
	public void addLocationsToDB(){
		//adds each geofence object to local db
		mGeofenceStore.open();
		Log.d("adding locations", String.valueOf(geofences.size()) + " locations");
		for (SimpleGeofence fence : geofences){
			mGeofenceStore.createGeofence(fence);
		}
		mGeofenceStore.close();
	}
	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadLocations extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SummaryActivity.this);
			pDialog.setMessage("Loading item locations. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			/////////////////////////////////////////
			//params can add in currently stored locations and send to server to filter these locations
			/////////////////////////////////////////
			// getting JSON string from URL
			
			JSONObject json = jParser.makeHttpRequest(
					ServerUtil.URL_ALL_LOCATIONS, "POST", params);

			// Check your log cat for JSON response
			Log.d("All Locations: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);
				
				if (success == 1) {
					// products found
					// Getting Array of Products
					locations = json.getJSONArray(TAG_LOCATIONS);
					Log.d("background", "got locations");
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					
					// looping through All Products
					Log.d("loop", "looping...");
					for (int i = 0; i < locations.length(); i++) {
						JSONObject c = locations.getJSONObject(i);
						
						// Storing each json item in variable
						double lon = Double.valueOf(c.getString(TAG_LONG));
						double lat = Double.valueOf(c.getString(TAG_LAT));
						String lid = c.getString(TAG_ID);
						float rad = Float.valueOf(c.getString(TAG_RAD));
						Date exp_date = new Date();
						try {
							exp_date = format.parse(c.getString(TAG_EXP));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						int trn = Integer.valueOf(c.getString(TAG_TRAN));
						int itm = Integer.valueOf(c.getString(TAG_ITEM));
						long exp = exp_date.getTime();
						boolean cmp = false;
						// creating new fenc object
						
						SimpleGeofence fence = new SimpleGeofence(lid, lat, lon, rad, exp, 1 | 2, itm, cmp);

						// adding fence to ArrayList
						geofences.add(fence);
					}
				} else {
					// no new locations found
					
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.d("doinback", String.valueOf(geofences.size()));
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			//update local database
			addLocationsToDB();
			//t.setText(String.valueOf(geofences.size()));
			Log.d("post", "returning");
		}

	}
}
