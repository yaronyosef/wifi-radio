package com.theisenp.wifi;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;

import android.util.Log;

/**
 * Class to manage the broadcast of UDP packets
 * @author Patrick Theisen
 *
 */
public class BroadcastTask extends Thread
{

	private final static String TAG = "BroadcastTask";
	private LinkedList<DatagramPacket> packetQueue;
	InetAddress address;
	
	@Override
	public void run()
	{
		super.run();
		String addressString = "141.212.59.40";
		packetQueue = new LinkedList<DatagramPacket>();
		
		DatagramSocket socket = null;
		try
		{
			/*
			 * Creates a new Datagram Socket and sends arbitrary data to the address
			 * at port 5000
			 */
			address = InetAddress.getByName(addressString);
			Log.d(TAG, address.toString());
			socket = new DatagramSocket();
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		while(!isInterrupted())
		{
			DatagramPacket packet = packetQueue.poll();
			if(packet != null)
			{
				try
				{
					Log.d(TAG, "Sending Message.  Left in queue: " + packetQueue.size());
					socket.send(packet);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				Thread.sleep(250);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		socket.close();
	}
	
	public void addToQueue(byte[] data)
	{
		DatagramPacket packet = new DatagramPacket(data, data.length, address, 5000);
		if(packetQueue == null)
		{
			Log.d(TAG, "Why is the queue null?");
			packetQueue = new LinkedList<DatagramPacket>();
		}
		packetQueue.add(packet);
		Log.d(TAG, "Packet added");
	}

}
