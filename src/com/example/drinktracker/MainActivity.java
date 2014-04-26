package com.example.drinktracker;

import java.io.IOException;
import java.util.Calendar;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;


public class MainActivity extends Activity {


	private SessionSQL USER_DATA;
	//Network check function
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	private void addRow(String[] data,int SessionID){
		String name = data[0];
    	String date = data[1];
    	String drinks = data[2];
    	TableLayout itemtable = (TableLayout) findViewById(R.id.session_table_layout);
		TableRow row= new TableRow(this);
		TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);
        row.setId(SessionID);
        
        TextView DateTextView = new TextView(this);
		TextView NameTextView = new TextView(this);
		TextView DrinksTextView = new TextView(this);
		
		int padd = getResources().getDimensionPixelSize(R.dimen.entry_row_padding);
		DateTextView.setPadding(2*padd,2*padd,padd,2*padd);
		NameTextView.setPadding(padd,2*padd,padd,2*padd);
		DrinksTextView.setPadding(padd,2*padd,padd,2*padd);
		
		
		DateTextView.setText(date);
		NameTextView.setText(name);
		DrinksTextView.setText(drinks);
		row.addView(DateTextView);
		row.addView(NameTextView);
		row.addView(DrinksTextView);
		itemtable.addView(row);
		
		row.setOnClickListener( new OnClickListener() {
		    @Override
		    public void onClick( View v ) {
		        //Do Stuff
		    	Intent newTemplate = new Intent(MainActivity.this,
            			TemplateActivity.class);
		    	int idvalue = v.getId();
		    	newTemplate.putExtra("SESSION_ID", idvalue);
            	startActivity(newTemplate);
		    }
		} );
		
	}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize UI elements
        final TextView templateText = (TextView) findViewById(R.id.textView1); 
        final Button templateButton = (Button) findViewById(R.id.newtemplateButton);
        
       //Load Session Sql Or create if there isnt one
        USER_DATA = new SessionSQL(this);
       
        
        int numberOfRows = USER_DATA.sessionCount();
        Toast.makeText(getApplicationContext(), String.format("Number of items %1$d", numberOfRows),
				   Toast.LENGTH_SHORT).show();
        //Build Layout from SQL file
        String[] data = new String[4];
        
        for(int i = 1;i<=numberOfRows;i++)
        {
        	data = USER_DATA.retrieveSession(i);
        	addRow(data,Integer.parseInt(data[3]));
        	
        }
        //Check Internet connection
        
       //If connection check dropbox id
        
        //If id Differnt download new beer list
        
        //Overwrite old update number
        
        //Create New Session
        templateButton.setOnClickListener(new OnClickListener(){
        	public void onClick(View v) {
        		Intent newTemplate = new Intent(MainActivity.this,
            			TemplateActivity.class);
        		
        		Calendar c = Calendar.getInstance();
        	    int Day = c.get(Calendar.DATE);
        	    int Year = c.get(Calendar.YEAR);
        	    int Month = c.get(Calendar.MONTH)+1;
        	    String Date = String.valueOf(Month)+"/"+String.valueOf(Day)+"/"+String.valueOf(Year);
        		String[] temp = {"Template",Date,"0.00"};
        		int idvalue = USER_DATA.addSession("Template", Date, "0.00");
        		addRow(temp,idvalue);
        		
        		newTemplate.putExtra("SESSION_ID", idvalue);
            	startActivity(newTemplate);
        	}
        	
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
