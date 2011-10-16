package com.theisenp.wifi;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class WiFiRadioActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        View purple = new View(this);
        purple.setBackgroundColor(0xFF582781);
        ListView list = (ListView) findViewById(R.id.radio_connection_list);
        list.setEmptyView(purple);
        
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, testList);
        list.setAdapter(listAdapter);
    }
    
    
    String[] testList = {"1", "2", "3"};
}