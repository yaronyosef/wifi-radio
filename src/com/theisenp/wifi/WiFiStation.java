package com.theisenp.wifi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WiFiStation extends Activity
{
	private static final String TAG = "WiFiStation";
	String stationName;
	int stationAddress, stationPort;
	
	MediaPlayer player;
	private SongListAdapter playQueueAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_station);
		
		initializeStationInfo();
		
		playQueueAdapter = new SongListAdapter(this, R.layout.song_list_item, new ArrayList<String>());
		ListView playQueue = (ListView) findViewById(R.id.station_song_list);
		playQueue.setAdapter(playQueueAdapter);
		
		playQueue.setOnItemClickListener(songClickListener);
		player = new MediaPlayer();
		//player.setDataSource(this, URIUtils.createURI("http", "230.0.0." + stationAddress, stationPort, null, null, null));
		try
		{
			player.setDataSource("http://230.0.0.16");
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		catch (IllegalStateException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the station information from the intent that launched the Activity and
	 * populates the text fields in the station info bar with that information.
	 */
	private void initializeStationInfo()
	{
		Intent stationIntent = getIntent();
		stationName = stationIntent.getStringExtra("name");
		stationAddress = Integer.parseInt(stationIntent.getStringExtra("station"));
		stationPort = Integer.parseInt(stationIntent.getStringExtra("port"));
		
		TextView nameField = (TextView) findViewById(R.id.station_name);
		TextView stationField = (TextView) findViewById(R.id.station_number);
		TextView portField = (TextView) findViewById(R.id.station_port);
		nameField.setText(stationName);
		stationField.setText(String.valueOf(stationAddress));
		portField.setText(String.valueOf(stationPort));
	}
	
	AdapterView.OnItemClickListener songClickListener = new OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
		{
			
		}
	};
	
	public void playPause(View v)
	{
		if(player.isPlaying())
		{
			player.pause();
		}
		else
		{
			player.start();
		}
	}
	
	
	/**
	 * Opens a dialog so that the user can choose a song to add to the play queue
	 * @param v
	 * The function is called from the XML, so it needs this parameter.  It is unused.
	 */
	public void addSong(View v)
	{
		Log.d(TAG, "Adding Song...");
		/*
		 * TODO: Just adding song from assets right now.
		 * Later selecting from SD card.
		 */
		playQueueAdapter.add("Dreams Don't Turn To Dust", "Owl City", "Theisen");
	}
	
	private void queueTrack()
	{
		//player = MediaPlayer.create(WiFiStation.this, R.raw.dreams);
		playPause(null);
		
		
		//player.start();
		/*
		int minBuffer = AudioTrack.getMinBufferSize(11025, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_8BIT);
		AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 11025, AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_8BIT, minBuffer, AudioTrack.MODE_STREAM);
		
		Log.d(TAG, "Size: " + minBuffer);
		
		InputStream inputStream = getResources().openRawResource(R.raw.login);
		BufferedInputStream bufferedInputStream= new BufferedInputStream(inputStream, minBuffer);
		DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
		
		byte[][] buffer = new byte[5][minBuffer];
		int i = 0;
		try
		{
			track.play();
			while(dataInputStream.available() > 0)
			{
					dataInputStream.read(buffer[i % 5], 0, minBuffer);
					track.write(buffer[i % 5], 0, minBuffer);
					i++;
			}
			track.stop();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		Log.d(TAG, "Done Writing Data.");
		*/
	}
	
	/**
	 * An adapter to manage the list of songs in the play queue for a particular station.
	 * @author Theisen
	 */
	private class SongListAdapter extends ArrayAdapter<String>
	{
		ArrayList<String> titles, artists, users;
		
		public SongListAdapter(Context context, int textViewResourceId, List<String> list)
		{
			super(context, textViewResourceId, list);
			titles = new ArrayList<String>();
			artists = new ArrayList<String>();
			users = new ArrayList<String>();
		}
		
		public void add(String title, String artist, String user)
		{
			titles.add(title);
			artists.add(artist);
			users.add(user);
			super.add(title);
			notifyDataSetChanged();
		}
		
		public void remove(int index)
		{
			super.remove(titles.get(index));
			titles.remove(index);
			artists.remove(index);
			users.remove(index);
			notifyDataSetChanged();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			RelativeLayout layout = (RelativeLayout) convertView;
            if (layout == null)
            {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                layout = (RelativeLayout) vi.inflate(R.layout.song_list_item, null);
            }
			
			TextView nameView = (TextView) layout.findViewById(R.id.song_list_item_title);
			TextView stationView = (TextView) layout.findViewById(R.id.song_list_item_artist);
			TextView portView = (TextView) layout.findViewById(R.id.song_list_item_user);
			
			nameView.setText((CharSequence) titles.get(position));
			stationView.setText(artists.get(position));
			portView.setText("User: " + users.get(position));
			return layout;
		}
	}
}
