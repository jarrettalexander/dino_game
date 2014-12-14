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
import android.media.MediaPlayer;
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
	
	// Database for dinos
	private DinosDataSource datasource;
	private static String tmp;
	private static ImageView img;
	// Geofence
	public ArrayList<SimpleGeofence> geofences;
	public SimpleGeofenceStore mGeofenceStore;
	public ProgressDialog pDialog;
	public PreferencesActivity prefs;
	// Background music object
	private BackgroundSound music;
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	public TextView t;
	private static Date lastSync;
	// Server variable for sending
	private SendToServer serverCon;
	// Products JSONArray
	JSONArray locations = null;
	// Extra to pass into activities for the dino's position in list
	public final static String EXTRA_POSITION = "this.POSITION";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_summary);
		prefs = new PreferencesActivity(this);
		prefs.UpdateSecurityId();
		
		// Get source of dinos
		datasource = new DinosDataSource(this);
		datasource.open();
		
		// Populate list of dinos
		ListView listview = (ListView) findViewById(R.id.listView1);
		listview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// When dino is tapped, open that dino's information
				Intent intent = new Intent(SummaryActivity.this, CharacterActivity.class);
				intent.putExtra(EXTRA_POSITION, position);
				startActivity(intent);
			}
		});

		// Store the data in the list view
		final List<DinoItem> dinoItems = datasource.getAllDinos();
		final DinoItemAdapter dinoItemAdapter = new DinoItemAdapter(this,
				R.layout.dino_item, dinoItems);
		listview.setAdapter(dinoItemAdapter);
		
		// Create new array of geofence objects
		geofences = new ArrayList<SimpleGeofence>();
		mGeofenceStore = new SimpleGeofenceStore(this);
		t = (TextView) findViewById(R.id.textView1);
		
		if (timeToSync()){
			//load locations from background thread
			serverCon = new SendToServer(this, prefs.getId());
			serverCon.execute("GetGeofenceLocations");
		}
		
		/* testing item grab
		Log.d("executing...","getitemsbylocation");
		img = (ImageView) findViewById(R.id.imageView1);
		serverCon.execute("GetItemsByLocation", "1");
		*/
		

		try{
			//Toast.makeText(this, android_id, Toast.LENGTH_LONG).show();
			if (music == null){
				music = new BackgroundSound();
			}		
			music.execute();
		} catch(Exception e){
			// Ignore expceptions - it's just music
		}
		
	}

	public boolean timeToSync(){
		// Sync every hour
		boolean result = true;
		if (lastSync == null){
			lastSync = new Date();
			return result;
		}
		Date cur = new Date();
		Log.d("timetosync lastsync", lastSync.toString());
		Log.d("timetosync cur", cur.toString());
		long diffHours = (cur.getTime() - lastSync.getTime()) / (60 * 60 * 1000);
		if (diffHours < 1){
			result = false;
		} else {
			lastSync = new Date();
		}
		return result;
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
	
	@Override
	public void onResume() {
	    super.onResume();
	    try{
	    	music.execute();
	    }catch(Exception e){
	    	
	    }
	}
	@Override
	public void onPause() {
	    super.onPause();
	   try{
		   music.cancel(true);
	   }catch (Exception e){
		   
	   }
	}
	public class BackgroundSound extends AsyncTask<Void, Void, Void> {

	    @Override
	    protected Void doInBackground(Void... params) {
	    	try{
	    		MediaPlayer player = MediaPlayer.create(SummaryActivity.this, R.raw.theme); 
	    		player.setLooping(true); // Set looping 
	    		player.setVolume(100,100); 
	    		player.start(); 
	    	} catch (Exception e){
	    		// Ignore
	    	}
	        return null;
	    }
}
}
