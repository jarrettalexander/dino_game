package edu.uark.csce.mobile.dinogame;

import java.util.Arrays;
import java.util.List;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

public class ReceiveTransitionsIntentService extends IntentService {

	public SendToServer serverCon;
	public Context context;
	public ReceiveTransitionsIntentService() {
		super("ReceiveTransitionsIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// Check for errors
		if(LocationClient.hasError(intent)) {
			int errorCode = LocationClient.getErrorCode(intent);
			String errorMessage = LocationServiceErrorMessages.getErrorString(this, errorCode);
            Log.e(GeofenceUtils.APPTAG, "Location Services Error: " + errorMessage);
		} else {
			int transitionType = LocationClient.getGeofenceTransition(intent);
			
			// Test that a valid transition was reported
			if((transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) || (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT)) {
				List <Geofence> triggerList = LocationClient.getTriggeringGeofences(intent);
				String[] triggerIds = new String[triggerList.size()];
				
				for(int i = 0; i < triggerIds.length; i++) {
					triggerIds[i] = triggerList.get(i).getRequestId();
				}
				
				String ids = TextUtils.join(GeofenceUtils.GEOFENCE_ID_DELIMITER, triggerIds);
                String transitionString = getTransitionString(transitionType);
                
                // TODO: move notification send to after item is retrieved from server
                for(int i = 0; i < triggerIds.length; i++) {
                	Log.d(GeofenceUtils.APPTAG, "triggering id " + i + " = " + triggerIds[i]);
                	sendNotification(transitionString, ids, triggerIds[i]);
                }
                
                // TODO: add item to inventory in sharedPrefs and start mainActivity of game
                Log.d(GeofenceUtils.APPTAG, "Setting completed to true for Geofence: " + triggerIds[0]);
                
                // Mark triggered Geofences as completed
                SimpleGeofenceStore store = new SimpleGeofenceStore(this);
                store.open();
                for(int i = 0; i < triggerIds.length; i++) {
                	store.setLocationToCompleted(triggerIds[i]);
                } 
                store.close();
                
                // Send a broadcast intent that can be received by the MapActivity's GeofenceSampleReceiver class
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(GeofenceUtils.ACTION_GEOFENCE_TRANSITION)
                			   .addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES)
                			   // Use put extra to send additional data to MapActivity
                			   .putExtra(GeofenceUtils.EXTRA_GEOFENCE_INFO, "some message"); 
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

                // Log the transition type and a message
                Log.d(GeofenceUtils.APPTAG, getString(
                                				R.string.geofence_transition_notification_title,
                                				transitionType,
                                				ids));
                Log.d(GeofenceUtils.APPTAG, getString(R.string.geofence_transition_notification_text));

                //get items from servr by location entered
                getItemsByLocation(triggerIds);
                
            // An invalid transition was reported
            } else {
                // Always log as an error
                Log.e(GeofenceUtils.APPTAG, "Geofence Transition Error: " + Integer.toString(transitionType)); 
            }
		}
		
	}
	
    private void sendNotification(String transitionType, String ids, String triggerId) {

        // Create an explicit content Intent that starts the Inventory Activity
    	// TODO: point to the actual inventory activity
        Intent notificationIntent =
                new Intent(getApplicationContext(), InventoryActivity.class);
        
        notificationIntent.putExtra("from_notification", true);
        notificationIntent.putExtra("geofence_id", triggerId);

        // Construct a task stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the Summary Activity to the task stack as the parent
        stackBuilder.addParentStack(SummaryActivity.class);

        // Push the content Intent onto the stack
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Set the notification contents
        builder.setSmallIcon(R.drawable.ic_stat_notify_dino)
               .setContentTitle(
                       getString(R.string.geofence_transition_notification_title,
                               transitionType, ids))
               .setContentText(getString(R.string.geofence_transition_notification_text))
               .setContentIntent(notificationPendingIntent)
               .setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }
	
    private String getTransitionString(int transitionType) {
        switch (transitionType) {

            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "Entered";

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "Exited";

            default:
                return "Transition Type Unknown";
        }
    }
    
    private void getItemsByLocation(String[] location_ids){
    	
    	PreferencesActivity prefs = new PreferencesActivity(getApplicationContext());
    	if (serverCon == null){
    		serverCon = new SendToServer(getApplicationContext(), prefs.getId());
    	}
    	//create comma delimited string of ids
    	StringBuilder builder = new StringBuilder();
    	for (String id : location_ids){
    		builder.append(id);
    		//if item is not last item, append comma
    		if(Arrays.asList(location_ids).indexOf(id) < (location_ids.length - 1)){
    			builder.append(",");
    		}
    	}
    	
    	serverCon.function = "GetItemsByLocation";
		serverCon.execute("GetItemsByLocation", builder.toString());
    }
	

}
