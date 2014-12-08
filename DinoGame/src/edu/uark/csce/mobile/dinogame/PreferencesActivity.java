package edu.uark.csce.mobile.dinogame;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;

public class PreferencesActivity extends PreferenceActivity{

	
	public String android_id;
	private Context context;
	private static final String PREF_ID = "android_id";
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
	}
	
	public PreferencesActivity(Context con){
		this.context = con;
		//set android_id
		this.android_id = Secure.getString(con.getContentResolver(),Secure.ANDROID_ID);
	}
	
	public void UpdateSecurityId(){
		//sets unique identifier
		Log.d("prefs", "getting");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String prev_id = preferences.getString(PREF_ID, "");
		Log.d("prefs", prev_id);
		if (!prev_id.equals(android_id)){
			preferences.edit().putString(PREF_ID, android_id).commit();
			Log.d("prefs", "writing " + android_id);
		}
	}
	public String getId(){
		return this.android_id;
	}
}
