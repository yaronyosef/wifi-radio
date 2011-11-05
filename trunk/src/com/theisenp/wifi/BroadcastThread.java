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
public class BroadcastThread extends Thread
{

	private final static String TAG = "BroadcastTask";
	private LinkedList<DatagramPacket> packetQueue;
	InetAddress address;
	
	@Override
	public void run()
	{
		super.run();
		/*
		 * TODO: Find the IP address programmatically instead of hardcoding it.
		 */
		String addressString = "141.212.59.40";
		/*
		 * Initializes the queue of messages to be sent
		 * If the messages are sent immediately it floods the network, and many of them are dropped
		 */
		packetQueue = new LinkedList<DatagramPacket>();
		
		DatagramSocket socket = null;
		try
		{
			/*
			 * Creates a new Datagram Socket and resolves the destination address
			 * for the packets.
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
		/*
		 * Loop until the user intentionally interrupts this thread
		 */
		while(!isInterrupted())
		{
			/*
			 * Get the first packet from the queue if it exists
			 */
			DatagramPacket packet = packetQueue.poll();
			/*
			 * If a packet was successfully grabbed from the queue, send it via socket
			 */
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
				/*
				 * Sleep for 250ms before sending the next message to avoid flooding the network
				 */
				Thread.sleep(250);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		socket.close();
	}
	
	/*
	 * Adds another message to the queue.
	 */
	public void addToQueue(byte[] data)
	{
		/*
		 * Builds a new DatagramPacket from the byte data
		 */
		DatagramPacket packet = new DatagramPacket(data, data.length, address, 5000);
		packetQueue.add(packet);
	}

}
