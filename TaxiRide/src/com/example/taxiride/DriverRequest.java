package com.example.taxiride;

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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/*
 * User need to type in the form to reserved a taxi. Then
 * all the information would be send to the server. User could 
 * change the information if they like or by default, it's 
 * the information that they filled out in the other class
 */
public class DriverRequest extends LoggingActivity {
	Boolean isTotalDone = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.driverrequest);
		
		if( Build.VERSION.SDK_INT >= 9){
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy); 
		}

		final EditText name = (EditText) findViewById(R.id.editName);
		name.setText(Addres.passengerInfo.getfullName());

		final EditText phoneNum = (EditText) findViewById(R.id.PhoneNumber);
		phoneNum.setText(Addres.passengerInfo.getPhoneNum());

		final EditText fromAddress = (EditText) findViewById(R.id.fromAddress);
		fromAddress.setText(Addres.passengerInfo.getFromAddress());

		final EditText toAddress = (EditText) findViewById(R.id.toAddress);
		toAddress.setText(Addres.passengerInfo.getToAddress());

		/*Addres.passengerInfo.setFromAddress(fromAddress.getText().toString());
		Addres.passengerInfo.setToAddress(toAddress.getText().toString());
		*/
		
		final EditText numOfPeople = (EditText) findViewById(R.id.EditNumOfPeople);
		numOfPeople.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				// XXX do something
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// XXX do something
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				Addres.passengerInfo.setTotalPeople(s.toString());
			}
		});

		// user press the enter button
		Button enter = (Button) findViewById(R.id.ButtonConfirm);

		enter.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				boolean isDone = false;
				// send the http request to confirmed the request, if the
				// request failed it would return fail otherwise it would
				// return the order id
				String result = updateHttpRequest();

				if (result.equals("fail")) {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Данные не могут быть отправлены", 1000);
					toast.show();
				} else {
					Addres.passengerInfo.setconfirmID(result);
					isDone = true;
				}
				// After successfully send to the server, go to the Request
				// Confirmation page
				if (isDone == true) {
					Intent myIntent = new Intent(view.getContext(), RequestConfirmation.class);
					startActivityForResult(myIntent, 0);
				}
			}

		});
	}
	
	public String updateHttpRequest() {
		
		try {
			SharedPreferences serverSetting = getSharedPreferences("serverSettings", MODE_PRIVATE);
			String address = serverSetting.getString("address", "");
			if (address.equals("")) {
				return "fail";
			}

			String myURL = address + "submit/";
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(myURL);
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("requestName", Addres.passengerInfo.getfullName()));
	        nameValuePairs.add(new BasicNameValuePair("requestPhoneNumber", Addres.passengerInfo.getPhoneNum()));
	        nameValuePairs.add(new BasicNameValuePair("requestPickupLocation", Addres.passengerInfo.getFromAddress()));
	        nameValuePairs.add(new BasicNameValuePair("requestDestination", Addres.passengerInfo.getToAddress()));
	        nameValuePairs.add(new BasicNameValuePair("totalDistance", Double.toString(Addres.passengerInfo.getDistance())));
	        nameValuePairs.add(new BasicNameValuePair("totalPeople", Addres.passengerInfo.getTotalPeople()));
	        nameValuePairs.add(new BasicNameValuePair("currentLatitude", Double.toString(Addres.passengerInfo.getFromLat())));
	        nameValuePairs.add(new BasicNameValuePair("currentLongitude", Double.toString(Addres.passengerInfo.getFromlog())));
	        nameValuePairs.add(new BasicNameValuePair("toLatitude", Double.toString(Addres.passengerInfo.getToLat())));
	        nameValuePairs.add(new BasicNameValuePair("toLongitude", Double.toString(Addres.passengerInfo.getToLog())));
	        
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
