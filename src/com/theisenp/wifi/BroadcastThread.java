package com.theisenp.wifi;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;

import android.util.Log;

/**
 * Class to manage the broadcast of TCP data
 * @author Patrick Theisen
 *
 */
public class BroadcastThread extends Thread
{

	private final static String TAG = "BroadcastTask";
	private LinkedList<WiFiMessage> messageQueue;
	private InetAddress address;
	private DataInputStream incoming;
	private DataOutputStream outgoing;
	private Socket socket;
	private String addressString;
	
	public BroadcastThread(String ipAddress)
	{
		addressString = ipAddress;
	}
	
	@Override
	public void run()
	{
		super.run();
		/*
		 * Initializes the queue of messages to be sent
		 * If the messages are sent immediately it floods the network, and many of them are dropped
		 */
		messageQueue = new LinkedList<WiFiMessage>();
		
		socket = null;
		try
		{
			/*
			 * Creates a new Socket and resolves the destination address
			 * for the messages.
			 */
			socket = new Socket(addressString, 5000);
			incoming = new DataInputStream(socket.getInputStream());
			outgoing = new DataOutputStream(socket.getOutputStream());
			
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
			WiFiMessage message = messageQueue.poll();
			/*
			 * If a packet was successfully grabbed from the queue, send it via socket
			 */
			if(message != null)
			{
				try
				{
					Log.d(TAG, "Sending Message.  Left in queue: " + messageQueue.size());
					if(outgoing == null)
					{
						Log.d(TAG, "outgoing is null");
					}
					outgoing.write(message.getMessage());
					//TODO: Write null terminators??
					//outgoing.write(...)
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
		
		try
		{
			incoming.close();
			outgoing.close();
			socket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * Adds another message to the queue.
	 */
	public void addToQueue(WiFiMessage message)
	{
		/*
		 * Adds a new message to the queue to be sent via TCP
		 */
		messageQueue.add(message);
	}

}
