package com.theisenp.wifi;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WiFiRadioActivity extends Activity {
	
	private static final String TAG = "WiFiRadioActivity";
	private static final String PREFERENCES_PREFIX = "station_";
	
	StationLongClickListener stationLongClickListener;
	StationAdapter listAdapter;
	ListView list;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        list = (ListView) findViewById(R.id.radio_connection_list);
        listAdapter = new StationAdapter(this, R.layout.radio_list_item, new ArrayList<String>());
        list.setAdapter(listAdapter);
        
        stationLongClickListener = new StationLongClickListener();
        list.setOnItemLongClickListener(stationLongClickListener);
        list.setOnItemClickListener(stationClickListener);
        registerForContextMenu(list);
        
        TextView addButton = (TextView) findViewById(R.id.header_add_item);
        addButton.setOnClickListener(addStationListener);
    }
    
    @Override
    protected void onPause()
    {
    	super.onPause();
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	SharedPreferences.Editor edit = prefs.edit();
    	int size = listAdapter.names.size();
    	edit.putInt("number_of_stations", size);
    	for(int i = 0; i < size; i++)
    	{
    		edit.putString(PREFERENCES_PREFIX + "_name_" + i, listAdapter.names.get(i));
    		edit.putString(PREFERENCES_PREFIX + "_station_" + i, listAdapter.stations.get(i));
    		edit.putString(PREFERENCES_PREFIX + "_port_" + i, listAdapter.ports.get(i));
    	}
    	edit.commit();
    	listAdapter.names.clear();
    	listAdapter.stations.clear();
    	listAdapter.ports.clear();
    	listAdapter.clear();
    }
    
    @Override
    protected void onResume()
    {
    	super.onResume();
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	String name, station, port;
    	int size = prefs.getInt("number_of_stations", 0);
    	for(int i = 0; prefs.contains(PREFERENCES_PREFIX + "_name_" + i) && i < size; i++)
    	{
    		name = prefs.getString(PREFERENCES_PREFIX + "_name_" + i, null);
    		station = prefs.getString(PREFERENCES_PREFIX + "_station_" + i, null);
    		port = prefs.getString(PREFERENCES_PREFIX + "_port_" + i, null);
    		if(name != null && station != null && port != null)
    		{
    			listAdapter.add(name, station, port);
    		}
    	}
    }
    
    class StationLongClickListener implements AdapterView.OnItemLongClickListener
    {
    	private int position;
    	
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		{
			position = (int) arg3;
			return false;
		}
    	
    }
    
    AdapterView.OnItemClickListener stationClickListener = new AdapterView.OnItemClickListener()
    {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		{
			Intent stationIntent = new Intent(WiFiRadioActivity.this, WiFiStation.class);
			stationIntent.putExtra("name", listAdapter.names.get((int) arg3));
			stationIntent.putExtra("station", listAdapter.stations.get((int) arg3));
			stationIntent.putExtra("port", listAdapter.ports.get((int) arg3));
			startActivity(stationIntent);
		}
	};
    
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
	  super.onCreateContextMenu(menu, v, menuInfo);
	  MenuInflater inflater = getMenuInflater();
	  inflater.inflate(R.menu.station_context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case (R.id.edit_station_context_menu_item):
			
			break;
		case (R.id.delete_station_context_menu_item):
			listAdapter.remove((int) stationLongClickListener.position);
			break;
		}
		
		return super.onContextItemSelected(item);
	}
	
    private class AddStationDialogBuilder extends AlertDialog.Builder
    {
    	private EditText nameEdit;
    	private EditText stationEdit;
    	private EditText portEdit;
    	
		protected AddStationDialogBuilder(Context context, final StationAdapter listItems)
		{
			super(context);
			LayoutInflater inflater = getLayoutInflater();
			setTitle("Add New Station");
			View view = inflater.inflate(R.layout.add_new_station, null);
			nameEdit = (EditText) view.findViewById(R.id.name_edit);
			stationEdit = (EditText) view.findViewById(R.id.station_edit);
			portEdit = (EditText) view.findViewById(R.id.port_edit);
			setView(view);
			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
			{
				
				public void onClick(DialogInterface dialog, int which)
				{
					Log.d(TAG, "Added New Station");
					String addName = getName();
					String addStation = getStation();
					String addPort = getPort();
					if(addName == null)
					{
						Toast.makeText(WiFiRadioActivity.this, "Please add a name", Toast.LENGTH_SHORT).show();
					}
					else if(addStation == null)
					{
						Toast.makeText(WiFiRadioActivity.this, "Please add a station", Toast.LENGTH_SHORT).show();
					}
					else if(addPort == null)
					{
						Toast.makeText(WiFiRadioActivity.this, "Please add a port", Toast.LENGTH_SHORT).show();
					}
					else
					{
						listItems.add(addName, addStation, addPort);
					}
				}
			};
			setPositiveButton("Add Station", listener);
			setNegativeButton("Cancel", null);
		}
		
		private String getName()
		{
			if(!nameEdit.getText().toString().equals(""))
			{
				return nameEdit.getText().toString();
			}
			return null;
		}
		
		private String getStation()
		{
			if(!stationEdit.getText().toString().equals(""))
			{
				return stationEdit.getText().toString();
			}
			return null;
		}
		
		private String getPort()
		{
			if(!portEdit.getText().toString().equals(""))
			{
				return portEdit.getText().toString();
			}
			return null;
		}
    }
    
    View.OnClickListener addStationListener = new View.OnClickListener()
    {
		
		public void onClick(View v)
		{
			final AddStationDialogBuilder dialog = new AddStationDialogBuilder(WiFiRadioActivity.this, listAdapter);
			dialog.show();
		}
	};
	
	private class StationAdapter extends ArrayAdapter<String>
	{
		ArrayList<String> names, stations, ports;
		
		public StationAdapter(Context context, int textViewResourceId, List<String> list)
		{
			super(context, textViewResourceId, list);
			names = new ArrayList<String>();
			stations = new ArrayList<String>();
			ports = new ArrayList<String>();
		}
		
		public void add(String name, String station, String port)
		{
			names.add(name);
			stations.add(station);
			ports.add(port);
			super.add(name);
			notifyDataSetChanged();
		}
		
		public void remove(int index)
		{
			super.remove(names.get(index));
			names.remove(index);
			stations.remove(index);
			ports.remove(index);
			notifyDataSetChanged();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			RelativeLayout layout = (RelativeLayout) convertView;
            if (layout == null)
            {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                layout = (RelativeLayout) vi.inflate(R.layout.radio_list_item, null);
            }
			
			TextView nameView = (TextView) layout.findViewById(R.id.radio_list_item_name);
			TextView stationView = (TextView) layout.findViewById(R.id.radio_list_item_station);
			TextView portView = (TextView) layout.findViewById(R.id.radio_list_item_port);
			
			nameView.setText((CharSequence) names.get(position));
			stationView.setText("Station: " + stations.get(position));
			portView.setText("Port: " + ports.get(position));
			return layout;
		}
	}
	
}