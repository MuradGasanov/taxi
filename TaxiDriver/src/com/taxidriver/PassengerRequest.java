package com.taxidriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DecimalFormat;
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

import com.google.gson.Gson;
import com.taxidriver.TaxiRequest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/*
 * After the user press the open request and press the name of the 
 * requestor this page would fired up. The driver is able to see
 * the information of the passenger as well as buttons to accept 
 * the request or call the passenger 
 */
public class PassengerRequest extends LoggingActivity {

	private String driverLat;
	private String driverLog;
	private String passengerLat;
	private String passengerLog;
	private double time;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.passengerrequest);
		if( Build.VERSION.SDK_INT >= 9){
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy); 
		}
		TextView requestName = (TextView) findViewById(R.id.requestName);
		requestName.setText("Имя пассажира: "
				+ ListOfRequest.TAXIREQUEST.getRequestName());

		TextView requestPhone = (TextView) findViewById(R.id.requestPhone);
		requestPhone.setText("Телефон пасcажира: "
				+ ListOfRequest.TAXIREQUEST.getRequestPhoneNumber());

		TextView requestLocation = (TextView) findViewById(R.id.fromAddress);
		requestLocation.setText("Место отправления: "
				+ ListOfRequest.TAXIREQUEST.getRequestPickupLocation());

		TextView destination = (TextView) findViewById(R.id.toAddress);
		destination.setText("Место прибытия: "
				+ ListOfRequest.TAXIREQUEST.getRequestDestination());

		TextView totalPass = (TextView) findViewById(R.id.totalPass);
		totalPass.setText("Число пассажиров: "
				+ ListOfRequest.TAXIREQUEST.getTotalPeople());

		// get the coordinate of the passenger and the driver
		passengerLat = ListOfRequest.TAXIREQUEST.getCurrentLatitude();
		passengerLog = ListOfRequest.TAXIREQUEST.getCurrentLongitude();
		driverLat = DriverWindow.driverInfo.getCurrentLatitude();
		driverLog = DriverWindow.driverInfo.getCurrentLongitude();

		// convert it into miles
		time = 0; //(getDistance(driverLat, driverLog, passengerLat, passengerLog) * 0.6);

		// accept the request
		Button accept = (Button) findViewById(R.id.buttonAccept);

		accept.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				// send http request, if the driver has successfully
				// accepted the requet to the server than it would return
				// success
				// otherwise it would return failed

				String result = sendHttpRequest();
				if (result.equals("success")) {
					Intent myIntent = new Intent(getBaseContext(), MyOrderInfo.class);
					startActivityForResult(myIntent, 0);
				}
			}
		});

		// a call button to call the passenger
		Button call = (Button) findViewById(R.id.buttonCall);
		call.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String tel = "tel:"
						+ ListOfRequest.TAXIREQUEST.getRequestPhoneNumber();
				Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri
						.parse(tel));
				startActivity(dialIntent);
			}

		});

	}

	// get the distance between two points and return the result in double and
	// mile
	public double getDistance(String lat_A, String log_A, String lat_B,
			String log_B) {
		float[] results = { 0 };
		double latA = Double.parseDouble(lat_A);
		double logA = Double.parseDouble(log_A);
		double latB = Double.parseDouble(lat_B);
		double logB = Double.parseDouble(log_B);

		Location distanceBetween = new Location("point a to b");

		distanceBetween.distanceBetween(latA, logA, latB, logB, results);

		double result = (float) (results[0]);
		result = result * 0.000621371192;
		DecimalFormat newFormat = new DecimalFormat("#.#");
		double convertMiles = Double.valueOf(newFormat.format(result));

		return convertMiles;

	}

	// send http request to accept the request. Return success if successlly or
	// return fail
	// if it's unsuccessfully.
	public String sendHttpRequest() {
		String responseString = "fail";
		try {
			SharedPreferences serverSetting = getSharedPreferences(
					"serverSettings", MODE_PRIVATE);
			String address = serverSetting.getString("address", "");

			String myURL = address + "approve/";

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(myURL);

	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("driverLoginName", DriverWindow.driverInfo.getDeviceID()));
	        nameValuePairs.add(new BasicNameValuePair("driverLoginPin", DriverWindow.driverInfo.getPin()));
	        nameValuePairs.add(new BasicNameValuePair("driverLatitude", driverLat));
	        nameValuePairs.add(new BasicNameValuePair("driverLongitude", driverLog));
	        nameValuePairs.add(new BasicNameValuePair("estimatedArrivalTime", String.valueOf(time)));
	        nameValuePairs.add(new BasicNameValuePair("requestID", String.valueOf(ListOfRequest.requestID)));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
	        
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			responseString = new BasicResponseHandler()
					.handleResponse(response);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseString;
	}
}
