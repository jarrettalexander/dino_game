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
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class SummaryActivity extends ActionBarActivity {
	
	// Database
	private DinosDataSource datasource;
	private static String tmp;
	private static ImageView img;
	public ArrayList<SimpleGeofence> geofences;
	public SimpleGeofenceStore mGeofenceStore;
	public ProgressDialog pDialog;
	public PreferencesActivity prefs;
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	public TextView t;

	//server variable for sending
	private SendToServer serverCon;
	// products JSONArray
		JSONArray locations = null;
	
	public final static String EXTRA_POSITION = "this.POSITION";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_summary);
		prefs = new PreferencesActivity(this);
		prefs.UpdateSecurityId();
		
		
		
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
		serverCon = new SendToServer(this, prefs.getId());
		//serverCon.execute("GetGeofenceLocations");
		
		/* testing item grab
		Log.d("executing...","getitemsbylocation");
		img = (ImageView) findViewById(R.id.imageView1);
		serverCon.execute("GetItemsByLocation", "1");
		*/
		

		//Toast.makeText(this, android_id, Toast.LENGTH_LONG).show();
		
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


	public void set(String t){
		//method for converting string to bytearray to img
		tmp = t;
		
		/*Log.d("tmp", tmp);
		byte[] data = Base64.decode(tmp, Base64.DEFAULT);
		Bitmap bmp = BitmapFactory.decodeByteArray(data,0, data.length);
		img = (ImageView) findViewById(R.id.imageView1);
		img.setImageBitmap(bmp);*/
	}
}
