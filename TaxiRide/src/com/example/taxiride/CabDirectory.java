package com.example.taxiride;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
/*
 *This class uses list to get the cab directory and i implement all the call 
 *buttons to call each of the cab directory
 */

public class CabDirectory extends ListActivity{
	 
	 final private static String[] Taxi = { "¿Õ∆»", "Ã¿’¿◊ ¿À¿", "œ–≈—“»∆", "œﬂ“≈– ¿", "› —œ–≈——" };
	
	public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		setContentView(R.layout.cabdirectory);
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Taxi));
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		 // TODO Auto-generated method stub
		 //super.onListItemClick(l, v, position, id);
		 String selection = l.getItemAtPosition(position).toString();
		 if (selection == "¿Õ∆»"){
			 Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri
                     .parse("tel:+79604196751"));
             startActivity(dialIntent);
		 }
		 if(selection == "Ã¿’¿◊ ¿À¿"){
			 Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri
                     .parse("tel:641400"));
             startActivity(dialIntent); 			 
		 }
		 if(selection == "œ–≈—“»∆"){
			 Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri
                     .parse("tel:+79637986060"));
             startActivity(dialIntent); 
		 }
		 if(selection == "œﬂ“≈– ¿"){
			 Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri
                     .parse("tel:+79289378505"));
             startActivity(dialIntent); 
		 }
		 if(selection == "› —œ–≈——"){
			 Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri
                     .parse("tel:+78722600000"));
             startActivity(dialIntent); 
		 } 
	}
}
