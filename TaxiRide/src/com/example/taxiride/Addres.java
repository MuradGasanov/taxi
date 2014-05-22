package com.example.taxiride;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/*
 * This class is for user to enter the address. If the user decides to turn
 * on the GPS coordinate to find their current location, the user does
 * not need to change the text at the edittext "current location".
 * The addresses are save as a global varaible
 */

public class Addres extends LoggingActivity {
	public static PassengerInfo passengerInfo = new PassengerInfo();
	public static String ToAddress;
	public static String FromAddress;
	public String currentFromAddress;
	private Geocoder geoCoder;
	private double[] coordinate = new double[2];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set the content view to address.xml
		setContentView(R.layout.address);
		passengerInfo = new PassengerInfo();
		// get passenger device ID and store it in the passengerInfo class
		String deviceID = Secure.getString(getBaseContext()
				.getContentResolver(), Secure.ANDROID_ID);
		passengerInfo.setDeviceID(deviceID);
		
 		//get passenger preferences and store it to passengerInfo class
		SharedPreferences prefs = getSharedPreferences("PassengerPreference", MODE_PRIVATE);
		String fName = prefs.getString("editFullName", "");
	    passengerInfo.setfullName(fName);
	    String phoneNum = prefs.getString("editPhoneNum","");
	    passengerInfo.setPhoneNum(phoneNum);
		
		// get the button called ButtonEnter. it's a enter button after the
		// users
		// has enter the address.
		Button enter = (Button) findViewById(R.id.ButtonConfirm);
		final EditText toAddress = (EditText) findViewById(R.id.editToAddress);
		final EditText fromAddress = (EditText) findViewById(R.id.fromAddress);
		geoCoder = new Geocoder(this, Locale.getDefault());
		// if the button called enter is click direct to page FindBy.class
		enter.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {

				ToAddress = toAddress.getText().toString();
				FromAddress = fromAddress.getText().toString();

				passengerInfo.setFromAddress(FromAddress);
				convertToCoordinate(FromAddress);
				passengerInfo.setFromLat(coordinate[0]);
				passengerInfo.setFromlog(coordinate[1]);

				passengerInfo.setToAddress(ToAddress);
				convertToCoordinate(ToAddress);
				passengerInfo.setToLat(coordinate[0]);
				passengerInfo.setToLog(coordinate[1]);
				
				setDistance();

				Intent myIntent = new Intent(view.getContext(), DriverRequest.class);
				startActivityForResult(myIntent, 0);
			}

		});
	}

	/*
	 * /** You need to handle the BACK button explicitly. Pressing the BACK
	 * button finishes the Activity and results have to be set BEFORE finishing
	 * the Activity. So using lifecycle methods like onPause or onStop will not
	 * work.
	 */
	// convert addresses to coordinates
	public void convertToCoordinate(String address) {
		List<Address> addresses;
		try {
			addresses = geoCoder.getFromLocationName(address, 1);
			if (addresses.size() > 0) {
				coordinate[0] = addresses.get(0).getLatitude();
				coordinate[1] = addresses.get(0).getLongitude();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// find distance between from and to address
	public void setDistance() {
		float[] results = { 0 };
		Location distanceBetween = new Location("point a to b");
		distanceBetween.distanceBetween(passengerInfo.getFromLat(),
				passengerInfo.getFromlog(), passengerInfo.getToLat(),
				passengerInfo.getToLog(), results);
		double result = (float) (results[0]);
		result = result * 0.000621371192;
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		DecimalFormat newFormat = new DecimalFormat("#.#");
		newFormat.setDecimalFormatSymbols(symbols);
		double convertMiles = Double.valueOf(newFormat.format(result));
		passengerInfo.setDistance(convertMiles);

	}
}
