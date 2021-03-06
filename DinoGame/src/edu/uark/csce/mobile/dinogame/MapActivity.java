package edu.uark.csce.mobile.dinogame;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;
import com.google.android.gms.location.LocationStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender.SendIntentException;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * MapActivity allows the user to retrieve items based off of their real-world location.
 * 
 * On activity start, retrieves locations from local database and sets Geofences based off of their data.
 * Display map with currently active Geofences and user's location. When user enters a Geofence, start
 * process of retrieving the item associated with that location from the server.
 * See RetrieveTransitionIntentService for background processing of Geofence transitions.
 * 
 * @author Jarrett Alexander, with lots of help from Android Documentation and Android Sample Projects
 *
 */
public class MapActivity extends FragmentActivity 
		implements com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks, 
		OnConnectionFailedListener, 
		OnAddGeofencesResultListener
		{
	
	private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    private static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * DateUtils.HOUR_IN_MILLIS;
    
    // Datasource of local location data
    private SimpleGeofenceStore mGeofenceStorage;
    
    // List of internal Geofence object representations, and list of Android Geofence objects
    private ArrayList<SimpleGeofence> mSimpleGeofenceList;
    private List<Geofence> mGeofenceList;
    
    // Decimal formats for latitude, longitude, and radius
    private DecimalFormat mLatLngFormat;
    private DecimalFormat mRadiusFormat;
	
    // Holds the location client
    private LocationClient mLocationClient;
    
    // Stores the PendingIntent used to request Geofence monitoring
    private PendingIntent mGeofenceRequestIntent;
    
    //Defines the allowable request types
    private enum REQUEST_TYPE {ADD}
    private REQUEST_TYPE mRequestType;
    
    // Flag that indicates if request is underway
    private boolean mInProgress;
    
    // Broadcast receiver for Geofence transitions and associated filter
    private GeofenceReceiver mBroadcastReceiver;
    private IntentFilter mIntentFilter;
    
    // Textview to display last-synced info
    private TextView syncLabel;
    
    // Google Map to display active Geofences and user location
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		mInProgress = false;
		mSimpleGeofenceList = new ArrayList<SimpleGeofence>();
		
		 // Set and localize the latitude/longitude format
        String latLngPattern = getString(R.string.lat_lng_pattern);
        mLatLngFormat = new DecimalFormat(latLngPattern);
        mLatLngFormat.applyLocalizedPattern(mLatLngFormat.toLocalizedPattern());

        // Set and localize the radius format
        String radiusPattern = getString(R.string.radius_pattern);
        mRadiusFormat = new DecimalFormat(radiusPattern);
        mRadiusFormat.applyLocalizedPattern(mRadiusFormat.toLocalizedPattern());
		
		// Instantiate a new Geofence datasource
		mGeofenceStorage = new SimpleGeofenceStore(this);
		mGeofenceStorage.open();
		
        // Create a new broadcast receiver to receive updates from the listeners and service
        mBroadcastReceiver = new GeofenceReceiver();
        mIntentFilter = new IntentFilter();
        
        // Action for broadcast Intents that report successful addition of Geofences
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_ADDED);

        // Action for broadcast Intents that report successful removal of Geofences
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_REMOVED);
        
        // Action for broadcast Intents that report Geofence transitions
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCE_TRANSITION);

        // Action for broadcast Intents containing various types of Geofencing errors
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCE_ERROR);

        // All Location Services sample apps use this category
        mIntentFilter.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);
		
		// Instantiate a new list of Geofences
		mGeofenceList = new ArrayList<Geofence>();
		
		syncLabel = (TextView) findViewById(R.id.GeofenceTestLabel);
		
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();		
		mMap.setMyLocationEnabled(true);
	}
	
	// Shows the last time the Geofences were synced with the server
	public void updateSyncLabel(){

        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		syncLabel.setText("Geofences last synced: " + format.format(new Date()));
	}
	
	// Sync locations with the server
	public void syncLocations(View v){
		
		updateSyncLabel();
		PreferencesActivity prefs = new PreferencesActivity(this);
		SendToServer t = new SendToServer(this, prefs.getId());
		t.execute("SyncGeofenceLocations");
		//activity restarted after sync
		finish();
	}
	
	public void goBack(View v){
		finish();
	}
	
	public void goToSummary(View v){
		Intent intent = new Intent(MapActivity.this, SummaryActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mGeofenceStorage.open();
		
		Log.d(GeofenceUtils.APPTAG, "in onResume");
		LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, mIntentFilter);
		
		loadCurrentGeofences();
	}
	
	@Override
    public void onPause() {
        super.onPause();
        
        mGeofenceStorage.close();
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// DialogFragment to display connection error dialog
	public static class ErrorDialogFragment extends DialogFragment {
		
		// Field to contain error dialog
		private Dialog mDialog;
		
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}
		
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}
		
		// Return a Dialog to the DialogFragment
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}
	
	// Handle results returned to the FragmentActivity by Google Play services
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
			case GeofenceUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST:
				// If the result code is Activity.RESULT_OK try to connect again
				switch (resultCode) {
					case Activity.RESULT_OK:
						break;
						
					// Any result but OK
					default:
						Log.d(GeofenceUtils.APPTAG, getString(R.string.no_resolution));
				}
			
			// If any other request code was received
			default:
				Log.d(GeofenceUtils.APPTAG, getString(R.string.unknown_activity_request_code, requestCode));
				break;
		}
	}
	
	private boolean servicesConnected() {
		
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		
		if(ConnectionResult.SUCCESS == resultCode) {
			Log.d(GeofenceUtils.APPTAG, getString(R.string.play_services_available));
			return true;
		} else {
			// Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getSupportFragmentManager(), GeofenceUtils.APPTAG);
            }
            return false;
		}
	}
	
	private PendingIntent getTransitionPendingIntent() {
        // Create an explicit Intent
        Intent intent = new Intent(this, ReceiveTransitionsIntentService.class);
        
        // Return the PendingIntent
        return PendingIntent.getService(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

	@Override
	public void onAddGeofencesResult(int statusCode, String[] geofenceRequestIds) {
		Intent broadcastIntent = new Intent();
		String msg;
		
		if(LocationStatusCodes.SUCCESS == statusCode) {
			// Create a message containing all the Geofence IDs added.
            msg = this.getString(R.string.add_geofences_result_success,
                    Arrays.toString(geofenceRequestIds));

            // In debug mode, log the result
            Log.d(GeofenceUtils.APPTAG, msg);

            // Create an Intent to broadcast to the app
            broadcastIntent.setAction(GeofenceUtils.ACTION_GEOFENCES_ADDED)
                           .addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES)
                           .putExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS, msg);
			
			// Update UI for geofence successfully added
            updateSyncLabel();
			updateMap();
		} else {
			Log.e(GeofenceUtils.APPTAG, "Geofence failed to add. FIX!");
			/*
             * Create a message containing the error code and the list
             * of geofence IDs you tried to add
             */
            msg = this.getString(
                    R.string.add_geofences_result_failure,
                    statusCode,
                    Arrays.toString(geofenceRequestIds)
            );

            // Log an error
            Log.e(GeofenceUtils.APPTAG, msg);

            // Create an Intent to broadcast to the app
            broadcastIntent.setAction(GeofenceUtils.ACTION_GEOFENCE_ERROR)
                           .addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES)
                           .putExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS, msg);
		}
		
		LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
		
		mInProgress = false;
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		mInProgress = false;
		
		// Resolve error if available, else display error dialog
		if(result.hasResolution()) {
			try {
				result.startResolutionForResult(this, GeofenceUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
			} catch (SendIntentException e) {
				e.printStackTrace();
			}
		} else {
			int errorCode = result.getErrorCode();
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, this, GeofenceUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
			
			if(errorDialog != null) {
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				errorFragment.setDialog(errorDialog);
				errorFragment.show(getSupportFragmentManager(), "Geofence Test");
			}
		}
			
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		
		switch(mRequestType) {
			case ADD: 
				Log.d(GeofenceUtils.APPTAG, "Location Client connected with request type ADD.");
				
				// Get the PendingIntent for the request
				mGeofenceRequestIntent = getTransitionPendingIntent();
				
				// Send a request to add the current Geofence
				mLocationClient.addGeofences(mGeofenceList, mGeofenceRequestIntent, this);
		}
	}

	@Override
	public void onDisconnected() {
		mInProgress = false;
		mLocationClient = null;
	}
	
	public void addGeofences() {
		mRequestType = REQUEST_TYPE.ADD;
		
		// Test for Google Play Services after setting the request type.
		if(!servicesConnected()) {
			Log.e(GeofenceUtils.APPTAG, "Google Play Services not connnected.");
			return;
		}
		
		// Create new location client. Pass current activity as listener for ConnectionCallbacks and OnConnectionFailedListener
		mLocationClient = new LocationClient(this, this, this);
		
		if(!mInProgress) {
			mInProgress = true;
			mLocationClient.connect();
		} else {
			Log.d(GeofenceUtils.APPTAG, "Request already underway.");
		}
	}	
	
	/**
     * Define a Broadcast receiver that receives updates from connection listeners and
     * the Geofence transition service.
     */
    public class GeofenceReceiver extends BroadcastReceiver {
        /*
         * Define the required method for broadcast receivers
         * This method is invoked when a broadcast Intent triggers the receiver
         */
        @Override
        public void onReceive(Context context, Intent intent) {

            // Check the action code and determine what to do
            String action = intent.getAction();
            
            Log.d(GeofenceUtils.APPTAG, "GeofenceSampleReceiver picked up something: " + action);

            // Intent contains information about errors in adding or removing Geofences
            if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_ERROR)) {

                handleGeofenceError(context, intent);

            // Intent contains information about successful addition or removal of Geofences
            } else if (
                    TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_ADDED)
                    ||
                    TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_REMOVED)) {

                handleGeofenceStatus(context, intent);

            // Intent contains information about a Geofence transition
            } else if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_TRANSITION)) {

                handleGeofenceTransition(context, intent);

            // The Intent contained an invalid action
            } else {
                Log.e(GeofenceUtils.APPTAG, getString(R.string.invalid_action_detail, action));
            }
        }

        /**
         *
         * @param context A Context for this component
         * @param intent The received broadcast Intent
         */
        private void handleGeofenceStatus(Context context, Intent intent) {
        	Log.d(GeofenceUtils.APPTAG, "Geofence status: " + intent.getAction());
        }

        /**
         * Report Geofence transitions to the UI; Currently no UI update are performed
         *
         * @param context A Context for this component
         * @param intent The Intent containing the transition
         */
        private void handleGeofenceTransition(Context context, Intent intent) {
        	Log.d(GeofenceUtils.APPTAG, "Geofence transition occured: " + intent.getAction());
        }

        /**
         * Report addition or removal errors to the log
         *
         * @param intent A broadcast Intent sent by ReceiveTransitionsIntentService
         */
        private void handleGeofenceError(Context context, Intent intent) {
            String msg = intent.getStringExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS);
            Log.e(GeofenceUtils.APPTAG, msg);
        }
    }
    
    // Retrieve current Geofences and begin process of adding them and listening for transitions
    private void loadCurrentGeofences() {
    	mGeofenceList.clear();
    	
    	// Retrieve all uncompleted Locations from local database
    	mSimpleGeofenceList = mGeofenceStorage.getAllUncompletedGeofences();
    	Log.d("count", String.valueOf(mSimpleGeofenceList.size()));
    	
    	for (SimpleGeofence fence : mSimpleGeofenceList){
    		mGeofenceList.add(fence.toGeofence());
    	}
    	
    	addGeofences();
    }
    
    // Keep map up to date with currently activated Geofences
    private void updateMap() {
    	
    	mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    	for (SimpleGeofence fence : mSimpleGeofenceList){
    		Log.d("adding geofence...", fence.toString());
    		LatLng location = new LatLng(fence.getLatitude(), fence.getLongitude());
    		
    		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location, 16);
    		mMap.animateCamera(update);
    		mMap.addMarker(new MarkerOptions().position(location).title("Geofence is here! " + fence.getId()));
    		
    		// Add circle around each Geofence
    		CircleOptions circleOptions = new CircleOptions()
    			.center(location)
    			.radius(fence.getRadius())
    			.fillColor(0x40ff0000)
    			.strokeColor(Color.TRANSPARENT)
    			.strokeWidth(2);
    		
    		Circle circle = mMap.addCircle(circleOptions);
    	}
    }
  
    //Button listeners
	public void viewSummary(View v) {
		Intent intent = new Intent(MapActivity.this, SummaryActivity.class);
		startActivity(intent);
	}
	
	public void viewPlayer(View v) {
		Intent intent = new Intent(MapActivity.this, CharacterActivity.class);
		startActivity(intent);
	}
	 
    //Method to add a Geofence for testing purposes
    private void addTestGeofence() {
    	SimpleGeofence geo = new SimpleGeofence("123", 36.0742414, -94.2218162, 500f, GEOFENCE_EXPIRATION_IN_MILLISECONDS, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT, 1234, false);  
    	mGeofenceStorage.createGeofence(geo);
    }
    
}

