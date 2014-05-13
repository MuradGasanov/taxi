package com.example.taxiride;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

/*
 *This class is for passenger preferences. It save the information on the phone 
 * 
 */

public class PassengerPreference extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	private SharedPreferences sp2;
	private SharedPreferences prefs2;
	private SharedPreferences.Editor ed2;

	private boolean showPrefs = false;
	private String showPref;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.passenger_preference);
		// get share preferences
		sp2 = PreferenceManager.getDefaultSharedPreferences(this);
		sp2.registerOnSharedPreferenceChangeListener(this);

		prefs2 = getSharedPreferences("PassengerPreference",
				Context.MODE_PRIVATE);

		ed2 = prefs2.edit();

		showPrefs = prefs2.getBoolean("HaveShownPrefs", false);
		showPref = new Boolean(showPrefs).toString();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, 0, "Сохранить");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				Intent myIntent = new Intent(this, MainActivity.class);
				startActivityForResult(myIntent, 0);
				return true;
			}
		return false;
	}

	@Override
	// save all the preferences information of the passenger
	public void onSharedPreferenceChanged(SharedPreferences sp, String key) {

		if (key.equals("editFullName")) {
			String value = sp.getString(key, null);
			ed2.putString("editFullName", value);
			ed2.commit();

		}
		if (key.equals("editPhoneNum")) {
			String value = sp.getString(key, null);
			ed2.putString("editPhoneNum", value);
			ed2.commit();

		}
		Preference pref = findPreference(key);
		if (pref instanceof EditTextPreference) {
			EditTextPreference etp = (EditTextPreference) pref;
			pref.setSummary(etp.getText());
		}

	}
}
