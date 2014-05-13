package com.taxidriver;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TwoLineListItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/*
 * 
 * This class display the list of open request. 
 * The server return a list of objects and and the
 * object is store into a list to display to the driver. 
 *
 */
public class ListOfRequest extends ListActivity {

	ArrayList<String> listItems = new ArrayList<String>();

	public static TaxiRequest TAXIREQUEST;
	private List<TaxiRequest> taxiRequestList;
	public static long requestID;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// get a list of object back from the server
		if( Build.VERSION.SDK_INT >= 9){
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy); 
		}
		String responseString = "";
		Gson gson = new Gson();
		try {
			SharedPreferences serverSetting = getSharedPreferences(
					"serverSettings", MODE_PRIVATE);
			String address = serverSetting.getString("address", "");

			String myURL = address + "list/";

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(myURL);

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			responseString = new BasicResponseHandler()
					.handleResponse(response);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Type type = new TypeToken<List<TaxiRequest>>(){}.getType();
		taxiRequestList = gson.fromJson(responseString, type);

		// get the name of request passenger and save it into the array
//		for (TaxiRequest taxiRequest : taxiRequestList) {
//
//			listItems.add(taxiRequest.getRequestName());
//
//		}

//		adapter = new ArrayAdapter<String>(this,
//				android.R.layout.simple_list_item_1, listItems);
		
		ArrayAdapter<TaxiRequest> adapter;
		adapter = new ArrayAdapter<TaxiRequest>(this,android.R.layout.simple_list_item_2, taxiRequestList){
		    @Override
		    public View getView(int position, View convertView, ViewGroup parent){
		        TwoLineListItem row;            
		        if(convertView == null){
		            LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		            row = (TwoLineListItem)inflater.inflate(android.R.layout.simple_list_item_2, null);                    
		        }else{
		            row = (TwoLineListItem)convertView;
		        }
		        TaxiRequest data = taxiRequestList.get(position);
		        row.getText1().setText(data.getRequestName());
		        row.getText2().setText(data.getRequestPickupLocation()+" : "+data.getRequestDestination());
		        row.getText1().setTextColor(Color.DKGRAY);
		        row.getText2().setTextColor(Color.DKGRAY);
		        return row;
		    }
		};

		setListAdapter(adapter);
		final ListView listView = getListView();

		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		TAXIREQUEST = taxiRequestList.get(position);
		requestID = TAXIREQUEST.getId();

		Intent myIntent = new Intent(v.getContext(), PassengerRequest.class);
		startActivityForResult(myIntent, 0);
	}
}