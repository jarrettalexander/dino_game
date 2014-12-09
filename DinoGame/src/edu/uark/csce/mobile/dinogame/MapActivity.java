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

public class MapActivity extends FragmentActivity 
		implements com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks, 
		OnConnectionFailedListener, 
		OnAddGeofencesResultListener
		{
	
	/*TODO: create local variables for: current geofence, current itemReward
	 * structure storage of geofence data, expiration data ((expiration date from database - current date) in ms), and RewardItem data together
	 * add event handlers for entering currentGeofence that launches dialog saying "Congrats! etc" and adding item to current storage of items
	 * structure getting of current geofence from database on startup and activating it
	 */
	
	private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    private static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * DateUtils.HOUR_IN_MILLIS;
    
    private SimpleGeofence mCurrentGeofence;
    private ArrayList<SimpleGeofence> mSimpleGeofenceList;
    private List<Geofence> mGeofenceList;
    private SimpleGeofenceStore mGeofenceStorage;
    
    // decimal formats for latitude, longitude, and radius
    private DecimalFormat mLatLngFormat;
    private DecimalFormat mRadiusFormat;
	
    // Holds the location client
    private LocationClient mLocationClient;
    
    // Stores the PendingIntent used to request geofence monitoring
    private PendingIntent mGeofenceRequestIntent;
    
    //Defines the allowable request types
    private enum REQUEST_TYPE {ADD}
    private REQUEST_TYPE mRequestType;
    
    // Flag that indicates if request is underway
    private boolean mInProgress;
    
    private GeofenceSampleReceiver mBroadcastReceiver;
    private IntentFilter mIntentFilter;
    
    // Placeholder for reward
    private String reward = "new hat";
    
    // Textview to display stuff
    private TextView testLabel;
    
    // Google Map to display geofence
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
		
		// Instantiate a new geofence storage area
		mGeofenceStorage = new SimpleGeofenceStore(this);
		mGeofenceStorage.open();
		
        // Create a new broadcast receiver to receive updates from the listeners and service
        mBroadcastReceiver = new GeofenceSampleReceiver();
        mIntentFilter = new IntentFilter();
        // Action for broadcast Intents that report successful addition of geofences
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_ADDED);

        // Action for broadcast Intents that report successful removal of geofences
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_REMOVED);
        
        // Action for broadcast Intents that report geofence transitions
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCE_TRANSITION);

        // Action for broadcast Intents containing various types of geofencing errors
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCE_ERROR);

        // All Location Services sample apps use this category
        mIntentFilter.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);
		
		// Instantiate a new list of geofences
		mGeofenceList = new ArrayList<Geofence>();
		
		testLabel = (TextView) findViewById(R.id.GeofenceTestLabel);
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		mMap.setMyLocationEnabled(true);

		// Testing
		//addTestGeofence();
	}
	public void updateSyncLabel(){

        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		testLabel.setText("Geofences last synced: " + format.format(new Date()));
	}
	public void syncLocations(View v){
		
		updateSyncLabel();
		PreferencesActivity prefs = new PreferencesActivity(this);
		SendToServer t = new SendToServer(this, prefs.getId());
		t.execute("SyncGeofenceLocations");
		
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
		
		loadCurrentGeofence();
	}
	
	@Override
    public void onPause() {
        super.onPause();
        
        mGeofenceStorage.close();
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
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
        Intent intent = new Intent(this,
                ReceiveTransitionsIntentService.class);
        /*
         * Return the PendingIntent
         */
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
			// Create a message containing all the geofence IDs added.
            msg = this.getString(R.string.add_geofences_result_success,
                    Arrays.toString(geofenceRequestIds));

            // In debug mode, log the result
            Log.d(GeofenceUtils.APPTAG, msg);

            // Create an Intent to broadcast to the app
            broadcastIntent.setAction(GeofenceUtils.ACTION_GEOFENCES_ADDED)
                           .addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES)
                           .putExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS, msg);
			
			// TODO: update UI for geofence successfully added
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
		//mLocationClient.disconnect();
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
				
				// Send a request to add the current geofence
				mLocationClient.addGeofences(mGeofenceList, mGeofenceRequestIntent, this);
		}
	}

	@Override
	public void onDisconnected() {
		mInProgress = false;
		mLocationClient = null;
	}
	
	public void addGeofence() {
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
			// TODO: disconnect client, reset flag, retry request
		}
	}	
	
	/**
     * Define a Broadcast receiver that receives updates from connection listeners and
     * the geofence transition service.
     */
    public class GeofenceSampleReceiver extends BroadcastReceiver {
        /*
         * Define the required method for broadcast receivers
         * This method is invoked when a broadcast Intent triggers the receiver
         */
        @Override
        public void onReceive(Context context, Intent intent) {

            // Check the action code and determine what to do
            String action = intent.getAction();
            
            Log.d(GeofenceUtils.APPTAG, "GeofenceSampleReceiver picked up something: " + action);

            // Intent contains information about errors in adding or removing geofences
            if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_ERROR)) {

                handleGeofenceError(context, intent);

            // Intent contains information about successful addition or removal of geofences
            } else if (
                    TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_ADDED)
                    ||
                    TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_REMOVED)) {

                handleGeofenceStatus(context, intent);

            // Intent contains information about a geofence transition
            } else if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_TRANSITION)) {

                handleGeofenceTransition(context, intent);

            // The Intent contained an invalid action
            } else {
                Log.e(GeofenceUtils.APPTAG, getString(R.string.invalid_action_detail, action));
                Toast.makeText(context, R.string.invalid_action, Toast.LENGTH_LONG).show();
            }
        }

        /**
         *
         * @param context A Context for this component
         * @param intent The received broadcast Intent
         */
        private void handleGeofenceStatus(Context context, Intent intent) {
        	Log.d(GeofenceUtils.APPTAG, "Geofence status: " + intent.getAction());
        	Toast.makeText(context, "Geofence Status Changed", Toast.LENGTH_LONG).show();
        }

        /**
         * Report geofence transitions to the UI
         *
         * @param context A Context for this component
         * @param intent The Intent containing the transition
         */
        private void handleGeofenceTransition(Context context, Intent intent) {
        	//String msg = intent.getStringExtra("msg");
        	
            // TODO: UI change on transition
        	Log.d(GeofenceUtils.APPTAG, "Geofence transition occured: " + intent.getAction());
        	//Log.d(GeofenceUtils.APPTAG, "msg: " + msg);
        	Toast.makeText(context, "Geofence Transition Occured", Toast.LENGTH_LONG).show();
        	
        	//Update SharedPreferences
        	//mGeofenceStorage.incrementLastGeofenceReceived(mCurrentGeofence.getId());
        	
        	// Update Database
        	//mGeofenceStorage.setLocationToCompleted(mCurrentGeofence.getId());
        }

        /**
         * Report addition or removal errors to the UI, using a Toast
         *
         * @param intent A broadcast Intent sent by ReceiveTransitionsIntentService
         */
        private void handleGeofenceError(Context context, Intent intent) {
            String msg = intent.getStringExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS);
            Log.e(GeofenceUtils.APPTAG, msg);
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    }
    
    private void loadCurrentGeofence() {
    	mGeofenceList.clear();
    	
    	// Using SharedPreferences...
    	//mCurrentGeofence = mGeofenceStorage.getCurrentGeofence();
    	//mGeofenceList.add(mCurrentGeofence.toGeofence());
    	
    	// Using SQLite Database...
    	mSimpleGeofenceList = mGeofenceStorage.getAllUncompletedGeofences();
    	Log.d("count", String.valueOf(mSimpleGeofenceList.size()));
    	/*if(mSimpleGeofenceList.get(0) != null) {
    		mCurrentGeofence = mSimpleGeofenceList.get(0);
    		mGeofenceList.add(mCurrentGeofence.toGeofence());
    	}*/
    	for (SimpleGeofence fence : mSimpleGeofenceList){
    		mGeofenceList.add(fence.toGeofence());
    	}
    	
    	addGeofence();
    }
    
    private void updateMap() {
    	
    	/*if(mCurrentGeofence != null) {
    		LatLng location = new LatLng(mCurrentGeofence.getLatitude(), mCurrentGeofence.getLongitude());
    		mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location, 16);
    		mMap.animateCamera(update);
    		mMap.addMarker(new MarkerOptions().position(location).title("Geofence is here!"));
    		
    		// Add circle
    		CircleOptions circleOptions = new CircleOptions()
    			.center(location)
    			.radius(mCurrentGeofence.getRadius())
    			.fillColor(0x40ff0000)
    			.strokeColor(Color.TRANSPARENT)
    			.strokeWidth(2);
    		
    		Circle circle = mMap.addCircle(circleOptions);
    	}*/
    	mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    	for (SimpleGeofence fence : mSimpleGeofenceList){
    		Log.d("adding geofence...", fence.toString());
    		LatLng location = new LatLng(fence.getLatitude(), fence.getLongitude());
    		
    		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location, 16);
    		mMap.animateCamera(update);
    		mMap.addMarker(new MarkerOptions().position(location).title("Geofence is here! " + fence.getId()));
    		
    		// Add circle
    		CircleOptions circleOptions = new CircleOptions()
    			.center(location)
    			.radius(fence.getRadius())
    			.fillColor(0x40ff0000)
    			.strokeColor(Color.TRANSPARENT)
    			.strokeWidth(2);
    		
    		Circle circle = mMap.addCircle(circleOptions);
    	}
    }
    
//    private void setUpMapIfNeeded() {
//        // Do a null check to confirm that we have not already instantiated the map.
//        if (mMap == null) {
//            // Try to obtain the map from the SupportMapFragment.
//            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
//                    .getMap();
//            // Check if we were successful in obtaining the map.
//            if (mMap != null) {
//                mMap.setMyLocationEnabled(true);
//                //mMap.setOnMyLocationButtonClickListener(this);
//            }
//        }
//    }
//
//    private void setUpGoogleApiClientIfNeeded() {
//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addApi(LocationServices.API)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .build();
//        }
//    }
//
//	@Override
//	public void onLocationChanged(Location location) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onConnectionSuspended(int cause) {
//		// TODO Auto-generated method stub
//		
//	}
//    
//    /**
//     * Button to get current Location. This demonstrates how to get the current Location as required
//     * without needing to register a LocationListener.
//     */
////    public void showMyLocation(View view) {
////        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
////            String msg = "Location = "
////                    + LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
////            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
////        }
////    }
    
    public void drawUserMarker(Location location) {
    	mMap.clear();
    	
    	updateMap();
    	
    	LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
    	mMap.addMarker(new MarkerOptions()
    			.position(currentPosition)
    			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
    			.title("You are here"));
    	
    }
 
  //Method for testing purposes
  private void addTestGeofence() {
	  SimpleGeofence geo = new SimpleGeofence("123", 36.0742414, -94.2218162, 500f, GEOFENCE_EXPIRATION_IN_MILLISECONDS, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT, 1234, false);  
	  mGeofenceStorage.createGeofence(geo);
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
	
	public void viewAccount(View v) {
		Intent intent = new Intent(MapActivity.this, AccountActivity.class);
		startActivity(intent);
	}
	
	public void viewSettings(View v) {
		Intent intent = new Intent(MapActivity.this, SettingsActivity.class);
		startActivity(intent);
	}
    
}

