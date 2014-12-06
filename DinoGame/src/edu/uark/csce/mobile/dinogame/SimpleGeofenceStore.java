package edu.uark.csce.mobile.dinogame;

import java.util.Arrays;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SimpleGeofenceStore {
	
	// The SharedPreferences object in which geofences are stored
    private final SharedPreferences mPrefs;

    // The name of the resulting SharedPreferences
    private static final String SHARED_PREFERENCE_NAME = MapActivity.class.getSimpleName();

    // Create the SharedPreferences storage with private access only
    public SimpleGeofenceStore(Context context) {
        mPrefs = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * Returns a stored geofence by its id, or returns {@code null}
     * if it's not found.
     *
     * @param id The ID of a stored geofence
     * @return A geofence defined by its center and radius. See
     * {@link SimpleGeofence}
     */
    public SimpleGeofence getGeofence(String id) {

        /*
         * Get the latitude for the geofence identified by id, or GeofenceUtils.INVALID_VALUE
         * if it doesn't exist
         */
        double lat = mPrefs.getFloat(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_LATITUDE),
                GeofenceUtils.INVALID_FLOAT_VALUE);

        /*
         * Get the longitude for the geofence identified by id, or
         * GeofenceUtils.INVALID_VALUE if it doesn't exist
         */
        double lng = mPrefs.getFloat(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_LONGITUDE),
                GeofenceUtils.INVALID_FLOAT_VALUE);

        /*
         * Get the radius for the geofence identified by id, or GeofenceUtils.INVALID_VALUE
         * if it doesn't exist
         */
        float radius = mPrefs.getFloat(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_RADIUS),
                GeofenceUtils.INVALID_FLOAT_VALUE);

        /*
         * Get the expiration duration for the geofence identified by
         * id, or GeofenceUtils.INVALID_VALUE if it doesn't exist
         */
        long expirationDuration = mPrefs.getLong(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_EXPIRATION_DURATION),
                GeofenceUtils.INVALID_LONG_VALUE);

        /*
         * Get the transition type for the geofence identified by
         * id, or GeofenceUtils.INVALID_VALUE if it doesn't exist
         */
        int transitionType = mPrefs.getInt(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_TRANSITION_TYPE),
                GeofenceUtils.INVALID_INT_VALUE);

        // If none of the values is incorrect, return the object
        if (
            lat != GeofenceUtils.INVALID_FLOAT_VALUE &&
            lng != GeofenceUtils.INVALID_FLOAT_VALUE &&
            radius != GeofenceUtils.INVALID_FLOAT_VALUE &&
            expirationDuration != GeofenceUtils.INVALID_LONG_VALUE &&
            transitionType != GeofenceUtils.INVALID_INT_VALUE) {

            // Return a true Geofence object
            return new SimpleGeofence(id, lat, lng, radius, expirationDuration, transitionType);

        // Otherwise, return null.
        } else {
            return null;
        }
    }
    
    /**
     * Using the ID of the Geofence the user last entered, finds the next ID in order and returns the
     * Geofence associated with that ID.
     * 
     * @return A Geofence defined by its center and radius. See
     * {@link SimpleGeofence}
     */
    public SimpleGeofence getCurrentGeofence() {
    	
    	SimpleGeofence currentGeofence;
    	
    	// Get ID of last Geofence the user entered
    	String lastGeofenceReceived = mPrefs.getString(GeofenceUtils.KEY_LAST_GEOFENCE_ID, null);
    	
    	// Get the array of IDs of Geofences stored in SharedPreferences
    	int size = mPrefs.getInt(GeofenceUtils.KEY_GEOFENCE_ID_ARRAY + "_size", 0);  
        String geofenceIds[] = new String[size];  
        for(int i=0;i<size;i++)  
            geofenceIds[i] = mPrefs.getString(GeofenceUtils.KEY_GEOFENCE_ID_ARRAY + "_" + i, null);  
    	
        // Get ID just above the last ID the user entered
        Arrays.sort(geofenceIds);
        int indexOfLastGeofenceReceived = (lastGeofenceReceived != null) ? Arrays.asList(geofenceIds).indexOf(lastGeofenceReceived) : -1;
        int indexOfGeofenceToRetrieve = indexOfLastGeofenceReceived + 1;
        
        if(indexOfGeofenceToRetrieve < size) {
        	String idOfGeofenceToRetrieve = geofenceIds[indexOfGeofenceToRetrieve];
        	currentGeofence = getGeofence(idOfGeofenceToRetrieve);
        } else {
        	currentGeofence = getGeofence(lastGeofenceReceived);
        }
        
        return currentGeofence;
    	
    }
    
    /**
     * Save a geofence.

     * @param geofence The {@link SimpleGeofence} containing the
     * values you want to save in SharedPreferences
     */
    public void setGeofence(String id, SimpleGeofence geofence) {
    	
        /*
         * Get a SharedPreferences editor instance. Among other
         * things, SharedPreferences ensures that updates are atomic
         * and non-concurrent
         */
        Editor editor = mPrefs.edit();
        
        // Be sure to add the ID to the list of Geofence IDs stored
    	int size = mPrefs.getInt(GeofenceUtils.KEY_GEOFENCE_ID_ARRAY + "_size", 0);
    	editor.putInt(GeofenceUtils.KEY_GEOFENCE_ID_ARRAY + "_size", size + 1);    	
    	editor.putString(GeofenceUtils.KEY_GEOFENCE_ID_ARRAY + "_" + size, geofence.getId());

        // Write the Geofence values to SharedPreferences
        editor.putFloat(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_LATITUDE),
                (float) geofence.getLatitude());

        editor.putFloat(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_LONGITUDE),
                (float) geofence.getLongitude());

        editor.putFloat(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_RADIUS),
                geofence.getRadius());

        editor.putLong(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_EXPIRATION_DURATION),
                geofence.getExpirationDuration());

        editor.putInt(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_TRANSITION_TYPE),
                geofence.getTransitionType());

        // Commit the changes
        editor.commit();
    }
    
    public void incrementLastGeofenceReceived(String id) {
    	Editor editor = mPrefs.edit();
    	editor.putString(GeofenceUtils.KEY_LAST_GEOFENCE_ID, id);
    	editor.commit();
    }
    
    /**
     * Given a Geofence object's ID and the name of a field
     * (for example, KEY_LATITUDE), return the key name of the
     * object's values in SharedPreferences.
     *
     * @param id The ID of a Geofence object
     * @param fieldName The field represented by the key
     * @return The full key name of a value in SharedPreferences
     */
    private String getGeofenceFieldKey(String id,
            String fieldName) {
        return GeofenceUtils.KEY_PREFIX + "_" + id + "_" + fieldName;
    }
}
