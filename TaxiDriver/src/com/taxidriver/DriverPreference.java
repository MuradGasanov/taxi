package com.taxidriver;

/*
 * Driver preferences class is to save driver preference to the phone and
 * the save data will be send thru http request to the server 
 * 
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class DriverPreference extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	private String deviceID;
	private String cabName;
	private String pin;
	private String phoneNum;
	private String fullName;
	private String maxPickUp;
	private String regID;
	private SharedPreferences prefs;
	private SharedPreferences.Editor ed;
	private String id;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.driver_preferences);
		if( Build.VERSION.SDK_INT >= 9){
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		    StrictMode.setThreadPolicy(policy); 
		}
		// get the device id
		deviceID = Secure.getString(getBaseContext().getContentResolver(),
				Secure.ANDROID_ID);
		// recognized that the preference have shown or not
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		sp.registerOnSharedPreferenceChangeListener(this);
		prefs = getSharedPreferences("DriverPreference", Context.MODE_PRIVATE);
		ed = prefs.edit();
		cabName = prefs.getString("editCompanyName", "");
		pin = prefs.getString("editPassword", "");
		phoneNum = prefs.getString("editPhoneNum", "");
		fullName = prefs.getString("editName", "");
		maxPickUp = prefs.getString("editMaxPickup", "");
		
		// if the preferences have been shown before save it as true
		ed.putBoolean("HaveShownPrefs", true);
		ed.commit();

	}

	@Override
	// menu to save the preferences
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, 0, "Сохранить");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		boolean isGood = false;

		SharedPreferences prefs = getSharedPreferences("DriverPreference",
				Context.MODE_PRIVATE);
		id = prefs.getString("regID", "");
		// sending the information to server. return fail it it
		// is not successfully, otherwise it would return me the regID
		// and save the regID in the driver preferences
		String result = updateHttpRequest();
		if (result.equals("fail") || result.equals("")) {
			Toast toast1 = Toast.makeText(getApplicationContext(),
					"Данные не могут быть отправлены",
					BIND_AUTO_CREATE);
			toast1.show();
			// if the result return true
		} else if (!result.equals("fail")) {
			isGood = true;

			ed.putString("regID", result);
			ed.commit();

		}
		if (isGood == true) {
			switch (item.getItemId()) {
			case 0:
				startActivity(new Intent(this, DriverWindow.class));
				return true;
			}

		}

		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	// save key into preference when user changes the preferences
	public void onSharedPreferenceChanged(SharedPreferences sp, String key) {

		if (key.equals("editName")) {
			fullName = sp.getString(key, "");
			ed.putString("editName", fullName);
			ed.commit();

		}

		if (key.equals("editCompanyName")) {
			cabName = sp.getString(key, "");
			ed.putString("editCompanyName", cabName);
			ed.commit();

		}
		if (key.equals("editPassword")) {

			pin = sp.getString(key, "");
			ed.putString("editPassword", pin);
			ed.commit();

		}
		if (key.equals("editPhoneNum")) {
			phoneNum = sp.getString(key, "");
			ed.putString("editPhoneNum", phoneNum);
			ed.commit();

		}
		if (key.equals("editMaxPickup")) {
			maxPickUp = sp.getString(key, "");
			ed.putString("editMaxPickup", maxPickUp);
			ed.commit();

		}
	}

	// send http request to the server to save driver preferences and return
	// back the regID if it's successful otherwise return false
	public String updateHttpRequest() {
		
		try {
			SharedPreferences serverSetting = getSharedPreferences("serverSettings", MODE_PRIVATE);
			String address = serverSetting.getString("address", "");
			if (address.equals("")) {
				return "fail";
			}

			String myURL = address + "settings/";
			String requestResult = "";
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(myURL);
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("regID", id));
	        nameValuePairs.add(new BasicNameValuePair("loginName", deviceID));
	        nameValuePairs.add(new BasicNameValuePair("loginPin", pin));
	        nameValuePairs.add(new BasicNameValuePair("fullName", fullName));
	        nameValuePairs.add(new BasicNameValuePair("cabName", cabName));
	        nameValuePairs.add(new BasicNameValuePair("phoneNumber", phoneNum));
	        nameValuePairs.add(new BasicNameValuePair("maxPickupDistance", maxPickUp));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        String responseString = new BasicResponseHandler().handleResponse(response);
	        return responseString;

	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		return "fail";
    }

}
