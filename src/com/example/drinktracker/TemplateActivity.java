package com.example.drinktracker;

import java.util.Arrays;
import java.util.Calendar;

import com.example.drinktracker.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;


public class TemplateActivity extends Activity {
	
	public String drinkArray[][] ={ {"Name","alc","Size","Cost","$/Drink","Drinks"}}; 
	final Context context = this;
	private static final int DIALOG_ALERT = 10;
	int currentRow = 0;
	double totalCostOverall=0;
	double totalDrinksOverall=0;
	double mLtooz = 0.033814;
	double scaler = 1;
	private SessionSQL USER_DATA;
	int SESSION_ID = 0;
	final double oneDrink = 0.6;

	private boolean isEmpty(EditText etText) {
	    if (etText.getText().toString().trim().length() > 0) {
	        return false;  //not empty
	    } else {
	        return true; // empty
	    }
	};
	
	private void addRow(String name,double units,double costperDrink,double totalDrinks,int entryid)
	{
		//Add to Table
		TableLayout itemtable = (TableLayout) findViewById(R.id.entries_table_layout);
		TableRow row= new TableRow(this);
		TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);
        row.setId(entryid);
        
		TextView nameText = new TextView(this);
		TextView unitsText = new TextView(this);
		TextView costText = new TextView(this);
		TextView drinkText = new TextView(this);
		
		nameText.setTextColor(getResources().getColor(R.color.black));
		unitsText.setTextColor(getResources().getColor(R.color.black));
		costText.setTextColor(getResources().getColor(R.color.black));
		drinkText.setTextColor(getResources().getColor(R.color.black));
		
		int padd = getResources().getDimensionPixelSize(R.dimen.entry_row_padding);
		nameText.setPadding(padd,padd,padd,padd);
		unitsText.setPadding(padd,padd,padd,padd);
		costText.setPadding(padd,padd,padd,padd);
		drinkText.setPadding(padd,padd,padd+padd,padd);
//		
//		
//		TableLayout.LayoutParams lp2 = new TableLayout.LayoutParams(drinkText.getLayoutParams());
//		lp.setMargins(0, 0, padd, 0);;
//		drinkText.setLayoutParams(lp2);
//		
//		
		nameText.setGravity(Gravity.LEFT);
		unitsText.setGravity(Gravity.RIGHT);
		costText.setGravity(Gravity.RIGHT);
		drinkText.setGravity(Gravity.RIGHT);
		
		nameText.setText(name);
		unitsText.setText(String.format("%.2f",units));
		costText.setText(String.format("%.2f", costperDrink));
		drinkText.setText(String.format("%.2f", totalDrinks));
		
		row.addView(nameText);
		row.addView(unitsText);
		row.addView(costText);
		row.addView(drinkText);
		itemtable.addView(row);
		
		currentRow++;
		
		
	};
	
	private void updateCounters(double costs,double drinks)
	{
		totalCostOverall += costs;
		totalDrinksOverall += drinks;
		TextView costView = (TextView) findViewById(R.id.cost_total_stats_textView);
		costView.setText("$"+String.format("%.2f",totalCostOverall));
		
		TextView drinkView = (TextView) findViewById(R.id.drink_total_stats_textView);
		drinkView.setText(String.format("%.2f",totalDrinksOverall));
		USER_DATA.updateDrinkCount(totalDrinksOverall, SESSION_ID);
	};
	
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		SESSION_ID = intent.getIntExtra("SESSION_ID", 0);
		
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_template); 
	    USER_DATA = new SessionSQL(this);
	    
	    final Button newEntry = (Button) findViewById(R.id.entry_button);
	    final TextView templateView = (TextView) findViewById(R.id.date_textView);
	    final EditText templateName = (EditText) findViewById(R.id.template_name_textEdit);
	    //Load Date and Name
	    String[] sessionData = USER_DATA.retrieveSession(SESSION_ID);
	    String name = sessionData[0];
	    String date = sessionData[1];
	    templateView.setText(date);
	    templateName.setText(name);
	    
	    //Listeners
	    templateName.setOnEditorActionListener(
	            new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			    if (event != null) {
			        // if shift key is down, then we want to insert the '\n' char in the TextView;
			        // otherwise, the default action is to send the message.
			        if (!event.isShiftPressed()) {
			        	//event
			        	USER_DATA.updateSession(templateName.getText().toString(), templateView.getText().toString(), SESSION_ID);
			        	InputMethodManager imm = (InputMethodManager)getSystemService(
			        			Context.INPUT_METHOD_SERVICE);
			        			imm.hideSoftInputFromWindow(templateName.getWindowToken(), 0);
			        	return true;
			        }
			        return false;
			    }

			    //event
			    USER_DATA.updateSession(templateName.getText().toString(), templateView.getText().toString(), SESSION_ID);
			    InputMethodManager imm = (InputMethodManager)getSystemService(
	        			Context.INPUT_METHOD_SERVICE);
	        			imm.hideSoftInputFromWindow(templateName.getWindowToken(), 0);
			    return true;
			}
	    });
	    
	    
	    //Add Date to top
//	    Calendar c = Calendar.getInstance();
//	    int Day = c.get(Calendar.DATE);
//	    int Year = c.get(Calendar.YEAR);
//	    int Month = c.get(Calendar.MONTH)+1;
//	    templateView.setText(String.valueOf(Month)+"/"+String.valueOf(Day)+"/"+String.valueOf(Year));
	    

	   
	    
	    //Populate list with Old data
	    String[] data = new String[7];
	    double alcohol = 0;
		double count = 0;
		double cost = 0;
		double size = 0;
		String beerName = null;
		double drinkTotal;
		double costperDrink;
		int entryID = 0;
		int sessionID;
		
		//Find Number of previous Entries and add to tabel
	    int numberOfEntries = USER_DATA.entryCount(SESSION_ID);
	    for(int i = 0; i < numberOfEntries; i++){
	    	//get Values
	    	data = USER_DATA.retrieveEntry(i, SESSION_ID);
	    	//seperate
	    	Log.d("this is my array", "arr: " + Arrays.toString(data));
	    	try {
	    	 entryID = Integer.parseInt(data[0]);
	    	 sessionID = Integer.parseInt(data[1]);
	    	 beerName = data[2];
	    	 alcohol = Double.parseDouble(data[3]);
	    	 size = Double.parseDouble(data[4]);
	    	 count = Double.parseDouble(data[5]);
	    	 cost = Double.parseDouble(data[6]);
	    	}catch(Exception e){Log.e("MYAPP", "exception", e);};
	    	
	    	//calculate new values
	    	drinkTotal = (alcohol/100)*size*count/oneDrink;
			costperDrink = cost/drinkTotal;
	    			
	    	//add row
			addRow(beerName,count,costperDrink,drinkTotal,entryID);
	    	
	    	//update Counters
			updateCounters(cost,drinkTotal);
	    }
	    newEntry.setOnClickListener(new OnClickListener(){
        	public void onClick(View v) {
        		//Open Dialog Here
        		// custom dialog
    			final Dialog dialog = new Dialog(context);
    			dialog.setContentView(R.layout.custom);
    			dialog.setTitle("New Entry");
    			
    			
    			
    			final EditText itemNameEditText = (EditText) dialog.findViewById(R.id.item_name_entry);
    			final EditText sizeEditText = (EditText) dialog.findViewById(R.id.size_entry);
    			final EditText alcoholEditText = (EditText) dialog.findViewById(R.id.alcohol_entry);
    			final EditText numberEditText = (EditText) dialog.findViewById(R.id.number_entry);
    			final EditText costEditText = (EditText) dialog.findViewById(R.id.cost_entry);
    			final Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);    			
    			final ImageButton shotButton = (ImageButton) dialog.findViewById(R.id.shots_button);
    			final ImageButton beerButton = (ImageButton) dialog.findViewById(R.id.beer_button);
    			final ImageButton wineButton = (ImageButton) dialog.findViewById(R.id.wine_button);
    			final Button unitButton = (Button) dialog.findViewById(R.id.units_button);
    			final Button dialogCloseButton = (Button) dialog.findViewById(R.id.dialogButtonCancel);
    			
    			shotButton.setOnClickListener(new OnClickListener(){
    				public void onClick(View v){
    					sizeEditText.setText("1.5");
    					unitButton.setText("oz");
    					scaler = 1;
    				}
    			});    			
    			wineButton.setOnClickListener(new OnClickListener(){
    				public void onClick(View v){
    					sizeEditText.setText("5");
    					unitButton.setText("oz");
    					scaler = 1;
    				}
    			});
    			beerButton.setOnClickListener(new OnClickListener(){
    				public void onClick(View v){
    					sizeEditText.setText("12");
    					unitButton.setText("oz");
    					scaler = 1;
    				}
    			});
    			unitButton.setOnClickListener(new OnClickListener(){
    				public void onClick(View v){
    					if(unitButton.getText().toString()=="oz"){
    						scaler=mLtooz;
    						unitButton.setText("mL");
    					}else{
    						scaler=1;
    						unitButton.setText("oz");
    					}
    				}
    			});
    			
    			// if button is clicked, close the custom dialog
    			dialogButton.setOnClickListener(new OnClickListener() {
    				@Override
    				public void onClick(View v) {
    					boolean goodtoGO = true;
    					double alcohol = 0;
    					double units = 0;
    					double cost = 0;
    					double size = 0;
    					String name = null;
    					
    					double drinkTotal;
    					double costperDrink;
    					
    					
    					if(isEmpty(itemNameEditText))
    					{
    						Toast.makeText(getApplicationContext(), "Enter item name",
    								   Toast.LENGTH_LONG).show();
    						goodtoGO = false;
    					}else{name = itemNameEditText.getText().toString().trim();}
    						
    					if(isEmpty(sizeEditText))
    					{
    						Toast.makeText(getApplicationContext(), "Enter item size",
    								   Toast.LENGTH_LONG).show();
    						goodtoGO = false;
    					}else{size = Double.parseDouble(sizeEditText.getText().toString().trim());}
    					
    					if(isEmpty(alcoholEditText))
    					{
    						Toast.makeText(getApplicationContext(), "Enter alcohol content",
    								   Toast.LENGTH_LONG).show();
    						goodtoGO = false;
    					}else{alcohol = Double.parseDouble(alcoholEditText.getText().toString().trim());}
    					    					
    					if(isEmpty(numberEditText))
    					{
    						Toast.makeText(getApplicationContext(), "Enter number of units",
    								   Toast.LENGTH_LONG).show();
    						goodtoGO = false;
    					}else{units = Double.parseDouble(numberEditText.getText().toString().trim());}
    					
    					if(isEmpty(costEditText))
    					{
    						Toast.makeText(getApplicationContext(), "Enter item cost",
    								   Toast.LENGTH_LONG).show();
    						goodtoGO = false;
    					}else{cost = Double.parseDouble(costEditText.getText().toString().trim());}
    					
    					
    					
    					if(goodtoGO)
    					{
    						//Calculate Needed Values
    						size = size*scaler;
    						drinkTotal = (alcohol/100)*size*units/oneDrink;
    						costperDrink = cost/drinkTotal;
    						int entryid =USER_DATA.addEntry(name, String.valueOf(alcohol), String.valueOf(size), String.valueOf(cost), String.valueOf(drinkTotal), SESSION_ID);
    						addRow(name,units,costperDrink,drinkTotal,entryid);
    						updateCounters(cost,drinkTotal);
    						
    						dialog.dismiss();
    						
    					}
    				}
    			});     
    			dialogCloseButton.setOnClickListener(new OnClickListener(){
    				public void onClick(View v){
    					dialog.dismiss();
    				}
    			});
    			     
    			dialog.show();
        	}        	
        });

	
	}
	
	
	
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		

	}

}

