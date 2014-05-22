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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

/*
 * This class is for the passenger side. This is the 
 * confirmation page after the passenger has 
 * reserved a taxi
 * 
 */
public class RequestConfirmation extends LoggingActivity {
	TextView driverName;
	TextView driverPhone;
	TextView arrivalTime;
	TextView cost;
	//double distance;
	double tripCost;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.requestconfirmation);
		
		if( Build.VERSION.SDK_INT >= 9){
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy); 
		}
		
		// get the driver name and their information and display on screen

		driverName = (TextView) findViewById(R.id.driverName);
		driverPhone = (TextView) findViewById(R.id.driverPhone);
		arrivalTime = (TextView) findViewById(R.id.arrivalTime);
		cost = (TextView) findViewById(R.id.cost);

		// get from and to address of passenger and display on screen
		TextView fromAddress = (TextView) findViewById(R.id.fromAddressLabel);
		fromAddress.setText("Место отправления: " + Addres.passengerInfo.getFromAddress());

		TextView toAddress = (TextView) findViewById(R.id.toAddressLabel);
		toAddress.setText("Место прибытия: " + Addres.passengerInfo.getToAddress());

		tripCost = Addres.passengerInfo.getDistance() * 18;

		cost.setText("Примерная стоимость: " + (tripCost + 50.0));

		// the refresh button. Is to get request from the server and
		// see if any driver has taken this order
		// if the taxi driver take this order, it would display
		// the driver name and information on screen otherwise
		// it's blank
		Button refresh = (Button) findViewById(R.id.buttonRefresh);

		refresh.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				// send an http request to update the information
				// if a taxi driver has not accept this request return nothing
				// else return an object with the driver name phone number and
				// etc
				TaxiRequest requestInfo = updateHttpRequest();

				if (requestInfo.getAssignedDriverName().equals("")) {
					driverName.setText("Имя водителя:");
					driverPhone.setText("Телефон водителя:");
					arrivalTime.setText("Время поездки:");
				} else {
					driverName.setText("Имя водителя: "
							+ requestInfo.getAssignedDriverName());
					driverPhone.setText("Телефон водителя: "
							+ requestInfo.getAssignedDriverPhoneNumber());
					arrivalTime.setText("Время поездки: "
							+ requestInfo.getEstimatedArrivalTime() + " мин.");
				}
				if (requestInfo.getIsRequestCompleted().equals("Y")) {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Заказ завершен",
							1000);
					toast.show();
					Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
					startActivityForResult(myIntent, 0);
				}
			}
		});

		// this is the cancel button. Is to cacnel the request if the passenger
		// wanted to
		Button cancel = (Button) findViewById(R.id.buttonCancel);
		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// send an http request to cancel the request. return failed if
				// failed otherwise it's successfully
				String result = cancelSendHttpRequest();
				if (result.equals("fail")) {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Ваш запрос не был отменен, попробуйте еще раз",
							1000);
					toast.show();
				} else {
					// after canceling the http request, the confirmation is
					// reset back to empty on every field
					Addres.passengerInfo.setconfirmID(result);
					driverName.setText("Имя водителя:");
					driverPhone.setText("Телефон водителя:");
					arrivalTime.setText("Время поездки:");
					cost.setText("Примерная стоимость:");
					Toast toast1 = Toast.makeText(getApplicationContext(),
							"Ваш запрос был отменен",
							BIND_AUTO_CREATE);
					toast1.show();
					Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
					startActivityForResult(myIntent, 0);
				}

			}

		});
	}

	// Send an http request on the refresh button to check to see if any
	// driver has accepted this request, if it has it would return a TaxiRequest
	// object which has information of the driver
	public TaxiRequest updateHttpRequest() {
		String responseString = "";
		Gson gson = new Gson();
		try {
			SharedPreferences serverSetting = getSharedPreferences("serverSettings", MODE_PRIVATE);
			String address = serverSetting.getString("address", "");

			String myURL = address + "update/";
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(myURL);
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("requestID", Addres.passengerInfo.getconfirmID()));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        responseString = new BasicResponseHandler().handleResponse(response);
	        
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		TaxiRequest taxiDriver = gson.fromJson(responseString, TaxiRequest.class);
		return taxiDriver; 
	}

	// cancel the http rquest and send it to the server

	public String cancelSendHttpRequest() {
		String responseString = "";
		Gson gson = new Gson();
		try {
			SharedPreferences serverSetting = getSharedPreferences("serverSettings", MODE_PRIVATE);
			String address = serverSetting.getString("address", "");

			String myURL = address + "remove/";
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(myURL);
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("requestID", Addres.passengerInfo.getconfirmID()));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        responseString = new BasicResponseHandler().handleResponse(response);
	        
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		return responseString;
	}

}
