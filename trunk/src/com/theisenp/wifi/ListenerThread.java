package com.theisenp.wifi;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Thread that listens for new data coming in at a user defined address
 * @author Patrick Theisen
 */
public class ListenerThread extends AsyncTask<Integer, String[], Void>
{
	private static final String TAG = "ListenerThread";

	private InetAddress address;
	private DatagramSocket socket;
	List<byte[]> messageQueue;

	@Override
	protected Void doInBackground(Integer... addressEnd)
	{
		Log.d(TAG, "Running");
		
		/*
		 * Grabs the application's context and builds an IP address from the provided address
		 */
		messageQueue = new ArrayList<byte[]>();
		try
		{
			String addressString = "141.212.59.40";// + addressEnd;
			address = InetAddress.getByName(addressString);
			socket = new DatagramSocket(5000, address);
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
		Log.d(TAG, "Listener started");		
		

		//TODO: Needs a more robustly defined packet size
		byte[] data = new byte[50000];
		DatagramPacket packet = new DatagramPacket(data, data.length);

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
				socket.receive(packet);
				oTrack.write(data, 0, data.length);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		oTrack.stop();
		oTrack.release();
		socket.close();
		messageQueue.clear();
		return null;
	}
	
	@Override
	protected void onProgressUpdate(String[]... values)
	{
		super.onProgressUpdate(values);
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
