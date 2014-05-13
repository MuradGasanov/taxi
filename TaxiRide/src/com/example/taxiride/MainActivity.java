package com.example.taxiride;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		SharedPreferences serverSetting = getSharedPreferences("serverSettings", MODE_PRIVATE);
		String serverAddress = serverSetting.getString("address", "");
		if (serverAddress.equals("")) {
			SharedPreferences.Editor editServerSettings = serverSetting.edit();
			editServerSettings.putString("address", "http://192.168.0.24/");
			editServerSettings.commit();
		}
		
		final Intent passangerPreference = new Intent(getBaseContext(), PassengerPreference.class);
		
		Button cabDirectore = (Button) findViewById(R.id.ButtonCabDir);
		cabDirectore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent cabDirectoryView = new Intent(getBaseContext(), CabDirectory.class);
				startActivityForResult(cabDirectoryView, 0);
			}
		});
		
		Button editPreference = (Button) findViewById(R.id.editPreference);
		editPreference.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivityForResult(passangerPreference, 0);
			}
		});
		
		Button driverDir = (Button) findViewById(R.id.ButtonDriverDir);
		driverDir.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent cabDirectoryView = new Intent(getBaseContext(), Addres.class);
				startActivityForResult(cabDirectoryView, 0);
			}
		});

	}
}
