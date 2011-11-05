package com.theisenp.wifi;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WiFiStation extends Activity
{
	private static final String TAG = "WiFiStation";
	String stationName, stationIpAddress;
	ConnectionTask listener;
	BroadcastThread broadcast;
	
	private SongListAdapter playQueueAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_station);
		
		WifiManager wifi = (WifiManager)getSystemService( Context.WIFI_SERVICE );
		if(wifi != null)
		{
			MulticastLock mcLock = wifi.createMulticastLock("mylock");
			mcLock.acquire();
		}
		//in case multicast doesn't work, do something here	
		initializeStationInfo();
		
		playQueueAdapter = new SongListAdapter(this, R.layout.song_list_item, new ArrayList<String>());
		ListView playQueue = (ListView) findViewById(R.id.station_song_list);
		playQueue.setAdapter(playQueueAdapter);
		
		playQueue.setOnItemClickListener(songClickListener);
		
		
		listener = new ConnectionTask();
		listener.execute(stationIpAddress);
		broadcast = new BroadcastThread(stationIpAddress);
		broadcast.start();
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		listener.cancel(true);
	}
	
	/**
	 * Gets the station information from the intent that launched the Activity and
	 * populates the text fields in the station info bar with that information.
	 */
	private void initializeStationInfo()
	{
		Intent stationIntent = getIntent();
		stationName = stationIntent.getStringExtra("name");
		stationIpAddress = stationIntent.getStringExtra("ipAddress");
		
		TextView nameField = (TextView) findViewById(R.id.station_name);
		TextView stationField = (TextView) findViewById(R.id.ip_address);
		nameField.setText(stationName);
		stationField.setText(String.valueOf(stationIpAddress));
	}
	
	AdapterView.OnItemClickListener songClickListener = new OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
		{
			sendAudioMessage(null);
		}
	};
	
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
	
	public void addSong(String track, String artist, String user)
	{
		playQueueAdapter.add(track, artist, user);
	}
	
	public void sendAudioMessage(View v)
	{
		InputStream is = getResources().openRawResource(R.raw.partyrock);
		BufferedInputStream bis = new BufferedInputStream(is, 8000);
	    DataInputStream dis = new DataInputStream(bis);
		
		int BUFFER_SIZE = 50000;
		byte[][] buffer = new byte[32][BUFFER_SIZE];
		int i = 0;
		try
		{
			while(dis.available() > 0)
			{
				dis.read(buffer[i % 32], 0, BUFFER_SIZE);
				
				//TODO: Get rid of hardcoded user and songId values
				AudioMessage message = new AudioMessage("Theisen", 1000, i, buffer[i % 32]);
				broadcast.addToQueue(message);
				i++;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void sendAddSongMessage(View v)
	{
		AddSongMessage message = new AddSongMessage("Theisen", 123456, "Hello Seattle", "Owl City");
		broadcast.addToQueue(message);
	}
	private void queueTrack()
	{
		//player = MediaPlayer.create(WiFiStation.this, R.raw.dreams);
		
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
	
	public class ConnectionTask extends AsyncTask<String, String, Void>
	{
		private static final String TAG = "ListenerThread";

		private InetAddress address;
		private Socket socket;
		List<byte[]> messageQueue;
		private DataInputStream incoming;
		private DataOutputStream outgoing;
		private ServerSocket serverSocket;
		private Socket client;

		@Override
		protected Void doInBackground(String... addressString)
		{
			Log.d(TAG, "Running");
			
			/*
			 * Grabs the application's context and builds an IP address from the provided address
			 */
			messageQueue = new ArrayList<byte[]>();
			try
			{
				//address = InetAddress.getByName(addressString);
				ServerSocket serverSocket = new ServerSocket(5000);
				Socket client = serverSocket.accept();
				incoming = new DataInputStream(client.getInputStream());
			}
			catch (UnknownHostException e)
			{
				e.printStackTrace();
			}
			catch (SocketException e)
			{
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d(TAG, "Listener started");		
			

			//TODO: Needs a more robustly defined packet size
			byte[] data = new byte[50000];

			/*
			 * Until interrupted by the user, listens for new data at the specified address
			 * Logs and toasts the data as text whenever a packet is received
			 */
			int  intSize = android.media.AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_CONFIGURATION_STEREO,
					AudioFormat.ENCODING_PCM_16BIT);

			AudioTrack oTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
					AudioFormat.CHANNEL_CONFIGURATION_STEREO,
					AudioFormat.ENCODING_PCM_16BIT, intSize,
					AudioTrack.MODE_STREAM);

			oTrack.play();
			while(!isCancelled())
			{
				Log.d(TAG, "Waiting for packet");
				try
				{
					Log.d(TAG, "Writing data to track");
					incoming.read(data, 0, data.length);
					
					if(WiFiMessage.readTag(data) != 0xB007B007)
					{
						oTrack.write(data, 0, data.length);
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			oTrack.stop();
			oTrack.release();
			try
			{
				incoming.close();
				socket.close();
				serverSocket.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			messageQueue.clear();
			return null;
		}
		
		@Override
		protected void onProgressUpdate(String... values)
		{
			super.onProgressUpdate(values);
			addSong(values[0], values[1], values[2]);
		}
		
		private void handleMessage(byte[] messageData)
		{
			byte[] tag = new byte[4];
			System.arraycopy(messageData, 0, tag, 0, 8);
			if(WiFiMessage.byteArrayToInt(tag) != 0xB007B007)
			{
				Log.e(TAG, "Incorrect tag");
				return;
			}
			byte type = messageData[4];
			if(type == 0)
			{
				String artist = AddSongMessage.readArtist(messageData);
				String name = AddSongMessage.readName(messageData);
				String user = WiFiMessage.readUser(messageData);
				
				String values[] = {name, artist, user};
				
				publishProgress(values);
			}
			else if(type == 1)
			{
				//Audio data
			}
		}
	}

	
}
