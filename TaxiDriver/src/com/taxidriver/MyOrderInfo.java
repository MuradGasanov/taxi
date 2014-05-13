package com.taxidriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.taxidriver.TaxiRequest;

/*
 * This class show the driver the information of their order. The 
 * passenger name, phone and etc is display on the screen, if the 
 * driver could call the passenger or hit confirmed when he/she is done
 * 
 */
public class MyOrderInfo extends LoggingActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if( Build.VERSION.SDK_INT >= 9){
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy); 
		}

		setContentView(R.layout.myorderinfo);
		TextView requestName = (TextView) findViewById(R.id.myOrderRequestName);
		requestName.setText("Имя пассажира: "
				+ ListOfRequest.TAXIREQUEST.getRequestName());

		TextView requestPhone = (TextView) findViewById(R.id.myOrderRequestPhone);
		requestPhone.setText("Телефон пасcажира: "
				+ ListOfRequest.TAXIREQUEST.getRequestPhoneNumber());

		TextView requestLocation = (TextView) findViewById(R.id.myOrderFromAddress);
		requestLocation.setText("Место отправления: "
				+ ListOfRequest.TAXIREQUEST.getRequestPickupLocation());

		TextView destination = (TextView) findViewById(R.id.myOrderToAddress);
		destination.setText("Место прибытия: "
				+ ListOfRequest.TAXIREQUEST.getRequestDestination());

		TextView totalPass = (TextView) findViewById(R.id.myOrderTotalPass);
		totalPass.setText("Число пассажиров: "
						+ ListOfRequest.TAXIREQUEST.getTotalPeople());

		TextView status = (TextView) findViewById(R.id.myOrderStatus);
		status.setText("Статус: Заказ принят");

		// if the driver decide to cancel the order.
		// press on the cancel button
		Button cancel = (Button) findViewById(R.id.myOrderCancel);
		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// send an http request to the server will return success
				// if it's successfully cacnel
				// if it's not successfully, it would return failed
				String result = sendHttpRequest();
				if (result.equals("success")) {
					Toast toast1 = Toast.makeText(getApplicationContext(),
							"Заказ отменен", 100);
					toast1.show();
					Intent myIntent = new Intent(getBaseContext(), ListOfRequest.class);
					startActivityForResult(myIntent, 0);
				}
			}

		});

		// complete button
		Button complete = (Button) findViewById(R.id.myOrderComplete);
		complete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// send the request to server and delete the order from the
				// server.
				String result = completeHttpRequest();
				if (result.equals("success")) {
					Toast toast1 = Toast.makeText(getApplicationContext(),
							"Заказ завершен", 100);
					toast1.show();
					Intent myIntent = new Intent(getBaseContext(), DriverWindow.class);
					startActivityForResult(myIntent, 0);
				}

			}

		});
		// a call button that you could use to call the pasenger
		Button call = (Button) findViewById(R.id.myOrderCall);
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

	// send a http request to cancel the order
	public String sendHttpRequest() {
		String responseString = "fail";
		try {
			SharedPreferences serverSetting = getSharedPreferences(
					"serverSettings", MODE_PRIVATE);
			String address = serverSetting.getString("address", "");

			String myURL = address + "cancel/";

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(myURL);

	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("requestID", String.valueOf(ListOfRequest.TAXIREQUEST.getId())));
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

	// send a http request to let the server know the order is completed and
	// is to delete from the server
	public String completeHttpRequest() {
		String responseString = "fail";
		try {
			SharedPreferences serverSetting = getSharedPreferences(
					"serverSettings", MODE_PRIVATE);
			String address = serverSetting.getString("address", "");

			String myURL = address + "complete/";

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(myURL);

	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("requestID", String.valueOf(ListOfRequest.TAXIREQUEST.getId())));
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
