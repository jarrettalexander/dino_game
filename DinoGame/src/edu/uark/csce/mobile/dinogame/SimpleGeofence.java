package edu.uark.csce.mobile.dinogame;

import com.google.android.gms.location.Geofence;

// Holds a single Geofence object
public class SimpleGeofence {

	private final String mId;
	private final double mLatitude;
	private final double mLongitude;
	private final float mRadius;
	private long mExpirationDuration;
	private int mTransitionType;
	private long mItemId;
	private boolean mCompleted;
	
	/**
     * @param geofenceId The Geofence's request ID
     * @param latitude Latitude of the Geofence's center.
     * @param longitude Longitude of the Geofence's center.
     * @param radius Radius of the geofence circle.
     * @param expiration Geofence expiration duration
     * @param transition Type of Geofence transition.
     */
	public SimpleGeofence(String geofenceId, double latitude, double longitude, float radius, long expiration, int transition, long itemId, boolean completed) {
		this.mId = geofenceId;
		this.mLatitude = latitude;
		this.mLongitude = longitude;
		this.mRadius = radius;
		this.mExpirationDuration = expiration;
		this.mTransitionType = transition;
		this.mItemId = itemId;
		this.mCompleted = completed;
	}

	public String getId() {
		return mId;
	}
	
	public double getLatitude() {
		return mLatitude;
	}
	
	public double getLongitude() {
		return mLongitude;
	}
	
	public float getRadius() {
		return mRadius;
	}	
	
	public long getExpirationDuration() {
		return mExpirationDuration;
	}

	public int getTransitionType() {
		return mTransitionType;
	}
	
	public long getItemId() {
		return mItemId;
	}
	
	public void setCompleted(boolean completed) {
		this.mCompleted = completed;
	}
	
	public boolean getCompleted() {
		return mCompleted;
	}
	
	/**
     * Creates a Location Services Geofence object from a
     * SimpleGeofence.
     *
     * @return A Geofence object
     */
	public Geofence toGeofence() {
		return new Geofence.Builder()
			.setRequestId(getId())
			.setTransitionTypes(mTransitionType)
			.setCircularRegion(getLatitude(), getLongitude(), getRadius())
			.setExpirationDuration(mExpirationDuration)
			.build();
	}
}
