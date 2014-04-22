package com.example.drinktracker;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize UI elements
        final TextView templateText = (TextView) findViewById(R.id.textView1); 
        final Button templateButton = (Button) findViewById(R.id.newtemplateButton);
        
        templateButton.setOnClickListener(new OnClickListener(){
        	public void onClick(View v) {
        		templateText.setText("Testt");
        		Intent newTemplate = new Intent(MainActivity.this,
            			TemplateActivity.class);
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
