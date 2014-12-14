/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.mocklocation;

/**
 *  Constants used in other classes in the app
 */
public final class LocationUtils {

    // Debugging tag for the application
    public static final String APPTAG = "Location Mock Tester";

    // Create an empty string for initializing strings
    public static final String EMPTY_STRING = new String();

    // Conversion factor for boot time
    public static final long NANOSECONDS_PER_MILLISECOND = 1000000;

    // Conversion factor for time values
    public static final long MILLISECONDS_PER_SECOND = 1000;

    // Conversion factor for time values
    public static final long NANOSECONDS_PER_SECOND =
                    NANOSECONDS_PER_MILLISECOND * MILLISECONDS_PER_SECOND;

    /*
     * Action values sent by Intent from the main activity to the service
     */
    // Request a one-time test
    public static final String ACTION_START_ONCE =
            "com.example.android.mocklocation.ACTION_START_ONCE";

    // Request continuous testing
    public static final String ACTION_START_CONTINUOUS =
                    "com.example.android.mocklocation.ACTION_START_CONTINUOUS";

    // Stop a continuous test
    public static final String ACTION_STOP_TEST =
                    "com.example.android.mocklocation.ACTION_STOP_TEST";

    /*
     * Extended data keys for the broadcast Intent sent from the service to the main activity.
     * Key1 is the base connection message.
     * Key2 is extra data or error codes.
     */
    public static final String KEY_EXTRA_CODE1 =
            "com.example.android.mocklocation.KEY_EXTRA_CODE1";

    public static final String KEY_EXTRA_CODE2 =
            "com.example.android.mocklocation.KEY_EXTRA_CODE2";

    /*
     * Codes for communicating status back to the main activity
     */

    // The location client is disconnected
    public static final int CODE_DISCONNECTED = 0;

    // The location client is connected
    public static final int CODE_CONNECTED = 1;

    // The client failed to connect to Location Services
    public static final int CODE_CONNECTION_FAILED = -1;

    // Report in the broadcast Intent that the test finished
    public static final int CODE_TEST_FINISHED = 3;

    /*
     * Report in the broadcast Intent that the activity requested the start to a test, but a
     * test is already underway
     *
     */
    public static final int CODE_IN_TEST = -2;

    // The test was interrupted by clicking "Stop testing"
    public static final int CODE_TEST_STOPPED = -3;

    // The name used for all mock locations
    public static final String LOCATION_PROVIDER = "fused";

    // An array of latitudes for constructing test data
    public static final double[] WAYPOINTS_LAT = {
    	36.090000,
	    36.087500,
	    36.085000,
	    36.082500,
	    36.080000,
	    36.077500,
	    36.075000,
	    36.074500,
	    36.0742414,
	    36.0700000,
	    36.0000000
    };

    // An array of longitudes for constructing test data
    public static final double[] WAYPOINTS_LNG = {
    	-94.2400000,
    	-94.2375000,
    	-94.2350000,
    	-94.2325000,
    	-94.2300000,
    	-94.2275000,
    	-94.2250000,
    	-94.2225000,
    	-94.2218162,
    	-94.2200000,
    	-94.2000000
    };

    // An array of accuracy values for constructing test data
    public static final float[] WAYPOINTS_ACCURACY = {
        3.0f,
        3.12f,
        3.5f,
        3.7f,
        3.12f,
        3.0f,
        3.12f,
        3.25f,
        3.30f,
        3.0f,
        3.0f
    };

    // Mark the broadcast Intent with an action
    public static final String ACTION_SERVICE_MESSAGE =
            "com.example.android.mocklocation.ACTION_SERVICE_MESSAGE";

    /*
     * Key for extended data in the Activity's outgoing Intent that records the type of test
     * requested.
     */
    public static final String EXTRA_TEST_ACTION =
            "com.example.android.mocklocation.EXTRA_TEST_ACTION";

    /*
     * Key for extended data in the Activity's outgoing Intent that records the requested pause
     * value.
     */
    public static final String EXTRA_PAUSE_VALUE =
            "com.example.android.mocklocation.EXTRA_PAUSE_VALUE";

    /*
     * Key for extended data in the Activity's outgoing Intent that records the requested interval
     * for mock locations sent to Location Services.
     */
    public static final String EXTRA_SEND_INTERVAL =
            "com.example.android.mocklocation.EXTRA_SEND_INTERVAL";
}
