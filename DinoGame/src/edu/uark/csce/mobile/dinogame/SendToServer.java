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
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

	public class SendToServer extends AsyncTask<String, String, String> implements Runnable{

		private static Context context;
		//geofence array list to gather from server
		public ArrayList<SimpleGeofence> geofences;
		public SimpleGeofenceStore mGeofenceStore;
		//list of dinoitems received from server
		public ArrayList<DinoItem> dinoitems;
		//dialog on preexec
		public ProgressDialog pDialog;
		public InventoryDataSource invDataSource;
		//list of inv items
		public ArrayList<InventoryItem> invitems;
		//bitmap string rec from server
		public String bmpString;
		//function making server call
		public String function;
		private static String user_id;
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
			//Log.d("sendtoserver", "creating new sendtoserver obj");
			//context and activity set in order to update labels
			if (context == null){
				context = con;
			}
			activity = (Activity) context;
			//init arra lists
			geofences = new ArrayList<SimpleGeofence>();
			mGeofenceStore = new SimpleGeofenceStore(con);
			dinoitems = new ArrayList<DinoItem>();
			invDataSource = new InventoryDataSource(con);
			invitems = new ArrayList<InventoryItem>();
			//set userid to send to server on location visit update
			if (user_id == null){
				user_id = id;
			}
			
			//temp function for getting id from preference activity
			//getIdFromPreference();
		}
		public JSONObject URLRequest(String url, String action, List<NameValuePair> params){
			return jParser.makeHttpRequest(url, action, params);
		}

		//not needed if user id is passed as paramter
		public void getIdFromPreference(){
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			user_id = preferences.getString("android_id", "");
		}
		public String getBmp(){
			return bmpString;
		}
		// on post exec add list of inv items to local db
		public void addItemsToDB(){
			if (invitems.size() < 1){
				return;
			}
			invDataSource.open();
			for (InventoryItem i : invitems){
				try{
				invDataSource.insertInventoryItem(i);
				}catch(Exception e){
					//Log.d("adding item to local", "item " + i.getName() + " exists");
				}
			}
			invDataSource.close();
			invitems.clear();
		}
		//on post exec add list of locs to local db
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
		//locations encoded in json format
		public String GetGeofenceLocations(){
				
			// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						//user id as param so locations already visited are not sent
						params.add(new BasicNameValuePair("user", user_id));
						/////////////////////////////////////////
						//params can add in currently stored locations and send to server to filter these locations
						/////////////////////////////////////////
						// getting JSON string from URL
						
						JSONObject json = URLRequest(
								ServerUtil.URL_ALL_LOCATIONS, "POST", params);

						// Check log for locations recvd
						//Log.d("All Locations: ", json.toString());

						try {
							// Checking for SUCCESS TAG
							int success = json.getInt(TAG_SUCCESS);
							
							if (success == 1) {
								// log locs
								locations = json.getJSONArray(TAG_LOCATIONS);
								//Log.d("background", "got locations");
								//format exp date on geofence
								SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
								
								//parse each location
								for (int i = 0; i < locations.length(); i++) {
									JSONObject c = locations.getJSONObject(i);
									//Log.d("location " + i, c.toString());
									//id from remote db is identical to id in local db
									double lon = Double.valueOf(c.getString(TAG_LONG));
									double lat = Double.valueOf(c.getString(TAG_LAT));
									String lid = c.getString(TAG_ID);
									float rad = Float.valueOf(c.getString(TAG_RAD));
									Date exp_date = new Date();
									try {
										exp_date = format.parse(c.getString(TAG_EXP));
									} catch (ParseException e) {
										e.printStackTrace();
									}
									//transition not currently used
									//set in mapactivity
									int trn = Integer.valueOf(c.getString(TAG_TRAN));
									int itm = Integer.valueOf(c.getString(TAG_ITEM));
									long exp = exp_date.getTime();
									boolean cmp = false;
									//create fence
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
						//Log.d("doinback", String.valueOf(geofences.size()));
						return "success";
		}
		
		//on geofence enter update loc as visited on remote server
		public void updateLocationsVisited(String loc){
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("user", user_id));
			//loc is comma delimited
			param.add(new BasicNameValuePair("location", loc));
			//Log.d("updating location visited", loc);
			JSONObject json = URLRequest(ServerUtil.URL_UPDATE_LOC_VIS, "POST", param);
			try{
				int success = json.getInt(TAG_SUCCESS);
				//Log.d("updatelocations success", json.toString());

				if (success == 1){
					//update location complete local db
					mGeofenceStore.open();
					List<String> locs = Arrays.asList(loc.split("\\s*,\\s*"));
					for (String location : locs){
						mGeofenceStore.setLocationToCompleted(location);
						//Log.d("marking as completed", "location " + location);
					}
					mGeofenceStore.close();
				}
			}catch(JSONException e){
				e.printStackTrace();
			}
		}
		//on geofence enter send location id to remote and recv item data
		public String GetItemsByLocation(String param1){
			String result = "";
			//param1 is comma delimited list of location ids
			//allows for entering multiple locations
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
						
						
						/////////////////temp
						int color1 = Color.argb(255, 204, 204, 37);
						int color2 = Color.argb(255, 204, 37, 59);
						int color3 = Color.argb(255, 204, 204, 37);
						//////////////////
						//create inventory item from values
						//create byte array for stats
						int[] stats = {att,def,spc};
						ByteBuffer byteBuffer = ByteBuffer.allocate(stats.length * 4);        
				        IntBuffer intBuffer = byteBuffer.asIntBuffer();
				        intBuffer.put(stats);
				        byte[] array = byteBuffer.array();
				        //bitmap string recv from server and converted to byte array
				        byte[] data = Base64.decode(img_string, Base64.DEFAULT);
				        byte[] imgByteArray = img_string.getBytes(Charset.forName("UTF-8"));

						InventoryItem item = new InventoryItem(item_id, name, array, data, color1, color2, color3);
						invitems.add(item);
					}
				}
			}catch (JSONException e){
				e.printStackTrace();
			}
			return result;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//Log.d("preex", "start");
			try{
				pDialog = new ProgressDialog(context);
				pDialog.setMessage("Loading data from server. Please wait...");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(false);
				pDialog.show();
			}catch (Exception e){
				e.printStackTrace();
				//context not set
			}

			
		}

		protected String doInBackground(String... args) {
			//Log.d("getgeofencelocations", "start");
			String result = "";
			//first arg is func making server request
			function = args[0];
			
			//second arg contains location id entered/exited
			switch(args[0]) {
			case "GetGeofenceLocations":
				result = GetGeofenceLocations();
				break;
			case "GetItemsByLocation":
				result = GetItemsByLocation(args[1]);
				updateLocationsVisited(args[1]);
				break;
			case "SyncGeofenceLocations":
				result = GetGeofenceLocations();
				break;
			}
			return result;
		}

		protected void onPostExecute(String file_url) {
			//Log.d("post","start");
			try{
				pDialog.dismiss();
			}catch(Exception e){
				e.printStackTrace();
				//context not set or pdialog not init
			}
			
			switch (function){
			case "GetGeofenceLocations":
				//update local database
				addLocationsToDB();
				break;
			case "GetItemsByLocation":
				//add items to local db
				addItemsToDB();
				break;
			case "SyncGeofenceLocations":

				Intent intent = new Intent(activity, MapActivity.class);
				activity.startActivity(intent);
				break;
			}
			//Log.d("sum act", "setting tmp");
			//Log.d("post", "returning");
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			

		}

	}
