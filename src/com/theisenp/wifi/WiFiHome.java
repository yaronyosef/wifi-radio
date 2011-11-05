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

public class WiFiHome extends Activity {
	
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
    		edit.putString(PREFERENCES_PREFIX + "_ipAddress_" + i, listAdapter.ipAddresses.get(i));
    	}
    	edit.commit();
    	listAdapter.names.clear();
    	listAdapter.ipAddresses.clear();
    	listAdapter.clear();
    }
    
    @Override
    protected void onResume()
    {
    	super.onResume();
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	String name, ipAddress;
    	int size = prefs.getInt("number_of_stations", 0);
    	for(int i = 0; prefs.contains(PREFERENCES_PREFIX + "_name_" + i) && i < size; i++)
    	{
    		name = prefs.getString(PREFERENCES_PREFIX + "_name_" + i, null);
    		ipAddress = prefs.getString(PREFERENCES_PREFIX + "_ipAddress_" + i, null);
    		if(name != null && ipAddress != null)
    		{
    			listAdapter.add(name, ipAddress);
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
			Intent stationIntent = new Intent(WiFiHome.this, WiFiStation.class);
			stationIntent.putExtra("name", listAdapter.names.get((int) arg3));
			stationIntent.putExtra("ipAddress", listAdapter.ipAddresses.get((int) arg3));
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
    	private EditText ipAddressEdit;
    	
		protected AddStationDialogBuilder(Context context, final StationAdapter listItems)
		{
			super(context);
			LayoutInflater inflater = getLayoutInflater();
			setTitle("Add New Station");
			View view = inflater.inflate(R.layout.add_new_station, null);
			nameEdit = (EditText) view.findViewById(R.id.station_name_edit);
			ipAddressEdit = (EditText) view.findViewById(R.id.ip_address_edit);
			setView(view);
			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
			{
				
				public void onClick(DialogInterface dialog, int which)
				{
					Log.d(TAG, "Added New Station");
					String addName = getName();
					String addIpAddress = getIpAddress();
					if(addName == null)
					{
						Toast.makeText(WiFiHome.this, "Please add a name", Toast.LENGTH_SHORT).show();
					}
					else if(addIpAddress == null)
					{
						Toast.makeText(WiFiHome.this, "Please choose an IP Address", Toast.LENGTH_SHORT).show();
					}
					else
					{
						listItems.add(addName, addIpAddress);
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
		
		private String getIpAddress()
		{
			if(!ipAddressEdit.getText().toString().equals(""))
			{
				return ipAddressEdit.getText().toString();
			}
			return null;
		}
    }
    
    View.OnClickListener addStationListener = new View.OnClickListener()
    {
		
		public void onClick(View v)
		{
			final AddStationDialogBuilder dialog = new AddStationDialogBuilder(WiFiHome.this, listAdapter);
			dialog.show();
		}
	};
	
	private class StationAdapter extends ArrayAdapter<String>
	{
		ArrayList<String> names, ipAddresses;
		
		public StationAdapter(Context context, int textViewResourceId, List<String> list)
		{
			super(context, textViewResourceId, list);
			names = new ArrayList<String>();
			ipAddresses = new ArrayList<String>();
		}
		
		public void add(String name, String ipAddress)
		{
			names.add(name);
			ipAddresses.add(ipAddress);
			super.add(name);
			notifyDataSetChanged();
		}
		
		public void remove(int index)
		{
			super.remove(names.get(index));
			names.remove(index);
			ipAddresses.remove(index);
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
			
			TextView nameView = (TextView) layout.findViewById(R.id.station_name);
			TextView ipAddressView = (TextView) layout.findViewById(R.id.ip_address);
			
			nameView.setText((CharSequence) names.get(position));
			ipAddressView.setText("IP Address: " + ipAddresses.get(position));
			return layout;
		}
	}
	
}