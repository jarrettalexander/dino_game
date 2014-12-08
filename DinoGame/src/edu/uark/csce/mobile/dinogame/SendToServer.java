package edu.uark.csce.mobile.dinogame;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	public class SendToServer extends AsyncTask<String, String, String> implements Runnable{

		private static Context context;
		private SummaryActivity activity1;
		public ArrayList<SimpleGeofence> geofences;
		public SimpleGeofenceStore mGeofenceStore;
		public ArrayList<DinoItem> dinoitems;
		public ProgressDialog pDialog;
		public InventoryDataSource invDataSource;
		public ArrayList<InventoryItem> invitems;
		public String bmpString;
		public String function;
		private static String user_id;
		private TextView mapSyncLabel;
		private Activity activity;
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
		private static final String TAG_ITEMS = "items";
		private static final String TAG_ITEM_ID = "itemid";
		private static final String TAG_ITEM_NAME = "itemname";
		private static final String TAG_ITEM_DESC = "itemdesc";
		private static final String TAG_ITEM_CAP = "itemcap";
		private static final String TAG_ITEM_IMG = "itemimg";
		private static final String TAG_ITEM_COL = "color_main";
		private static final String TAG_ITEM_ACC1 = "color_acc_1";
		private static final String TAG_ITEM_ACC2 = "color_acc_2";
		private static final String TAG_ITEM_ATT = "attack";
		private static final String TAG_ITEM_DEF = "defense";
		private static final String TAG_ITEM_SPC = "special";
		
		
		


		// products JSONArray
			JSONArray locations = null;
			JSONArray items = null;
			
		public SendToServer(Context con, String id){
			Log.d("sendtoserver", "creating new sendtoserver obj");
			if (context == null){
				context = con;
			}
			activity = (Activity) context;
			//this.activity1 = act;
			geofences = new ArrayList<SimpleGeofence>();
			mGeofenceStore = new SimpleGeofenceStore(con);
			dinoitems = new ArrayList<DinoItem>();
			invDataSource = new InventoryDataSource(con);
			invitems = new ArrayList<InventoryItem>();
			if (user_id == null){
				user_id = id;
			}
			//getIdFromPreference();
		}
		public JSONObject URLRequest(String url, String action, List<NameValuePair> params){
			return jParser.makeHttpRequest(url, action, params);
		}

		public void getIdFromPreference(){
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			user_id = preferences.getString("android_id", "");
		}
		public String getBmp(){
			return bmpString;
		}
		public void addItemsToDB(){
			if (invitems.size() < 1){
				return;
			}
			invDataSource.open();
			for (InventoryItem i : invitems){
				try{
				invDataSource.insertInventoryItem(i);
				}catch(Exception e){
					Log.d("adding item to local", "item " + i.getName() + " exists");
				}
			}
			invDataSource.close();
			invitems.clear();
		}
		public void addLocationsToDB(){
			if (geofences.size() < 1){
				return;
			}
			//adds each geofence object to local db
			mGeofenceStore.open();
			Log.d("adding locations", String.valueOf(geofences.size()) + " locations");
			for (SimpleGeofence fence : geofences){
				try{
					mGeofenceStore.createGeofence(fence);
				}catch(Exception e){
					
					e.printStackTrace();
				}
			}
			mGeofenceStore.close();
			geofences.clear();
		}
		public String GetGeofenceLocations(){
				
			// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("user", user_id));
						/////////////////////////////////////////
						//params can add in currently stored locations and send to server to filter these locations
						/////////////////////////////////////////
						// getting JSON string from URL
						
						JSONObject json = URLRequest(
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
									Log.d("object " + i, c.toString());
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
								return "failed";
								
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						Log.d("doinback", String.valueOf(geofences.size()));
						return "success";
		}
		
		public void updateLocationsVisited(String loc){
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("user", user_id));
			//loc is comma delimited
			param.add(new BasicNameValuePair("location", loc));
			Log.d("updating location visited", loc);
			JSONObject json = URLRequest(ServerUtil.URL_UPDATE_LOC_VIS, "POST", param);
			try{
				int success = json.getInt(TAG_SUCCESS);
				Log.d("updatelocations success", json.toString());

				if (success == 1){
					//update location complete local db
					mGeofenceStore.open();
					List<String> locs = Arrays.asList(loc.split("\\s*,\\s*"));
					for (String location : locs){
						mGeofenceStore.setLocationToCompleted(location);
						Log.d("marking as completed", "location " + location);
					}
					mGeofenceStore.close();
				}
			}catch(JSONException e){
				e.printStackTrace();
			}
		}
		public String GetItemsByLocation(String param1){
			String result = "";
			//param1 is comma delimited list of location ids
			List<NameValuePair> param_list = new ArrayList<NameValuePair>();
			param_list.add(new BasicNameValuePair("locations", param1));
			Log.d("getting items at location", param1);
			//http get request sending location ids
			JSONObject json = URLRequest(ServerUtil.URL_ITEMS_LOCATION, "GET", param_list);
			Log.d("all items", json.toString());
			//get json object and parse, created dinoitem objects
			try{
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1){
					items = json.getJSONArray(TAG_ITEMS);
					Log.d("get items", "got " + items.length() + " items");
					for (int i = 0; i < items.length(); i++){
						
						JSONObject d = items.getJSONObject(i);
						
						int item_id = d.getInt(TAG_ITEM_ID);
						String name = d.getString(TAG_ITEM_NAME);
						String desc = d.getString(TAG_ITEM_DESC);
						String caption = d.getString(TAG_ITEM_CAP);
						String img_string = d.getString(TAG_ITEM_IMG);
						int color_main = d.getInt(TAG_ITEM_COL);
						int acc1 = d.getInt(TAG_ITEM_ACC1);
						int acc2 = d.getInt(TAG_ITEM_ACC2);
						int att = d.getInt(TAG_ITEM_ATT);
						int def = d.getInt(TAG_ITEM_DEF);
						int spc = d.getInt(TAG_ITEM_SPC);
						
						//create inventory item from values
						//create byte array for stats
						int[] stats = {att,def,spc};
						ByteBuffer byteBuffer = ByteBuffer.allocate(stats.length * 4);        
				        IntBuffer intBuffer = byteBuffer.asIntBuffer();
				        intBuffer.put(stats);
				        byte[] array = byteBuffer.array();
				        byte[] imgByteArray = img_string.getBytes(Charset.forName("UTF-8"));

						InventoryItem item = new InventoryItem(item_id, name, array, imgByteArray, color_main, acc1, acc2);
						invitems.add(item);
					}
				}
			}catch (JSONException e){
				e.printStackTrace();
			}
			//for each dinoitem object, add to local db
			return result;
		}
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.d("preex", "start");
			/*pDialog = new ProgressDialog(context);
			pDialog.setMessage("Loading item locations. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();*/
			
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			Log.d("getgeofencelocations", "start");
			String result = "";
			function = args[0];
			
			
			switch(args[0]) {
			case "GetGeofenceLocations":
				result = GetGeofenceLocations();
				break;
			case "GetItemsByLocation":
				result = GetItemsByLocation(args[1]);
				updateLocationsVisited(args[1]);
				break;
			}
			return result;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			Log.d("post","start");
			// dismiss the dialog after getting all products
			//pDialog.dismiss();
			switch (function){
			case "GetGeofenceLocations":
				//update local database
				addLocationsToDB();
				//activity.setContentView(R.layout.activity_map);
				//mapSyncLabel = (TextView)activity.findViewById(R.id.GeofenceTestLabel);
				//DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				
				//mapSyncLabel.setText("Geofences last synced: " +  format.format(new Date()));
				//activity.setContentView(R.layout.activity_summary);
				break;
			case "GetItemsByLocation":
				//add items to local db
				//this.activity1.set(bmpString);
				addItemsToDB();
				break;
			}
			Log.d("sum act", "setting tmp");
			//this.activity1.set(bmpString);
			//t.setText(String.valueOf(geofences.size()));
			Log.d("post", "returning");
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			

		}

	}
