package com.theisenp.wifi;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class StationBroadcastThread extends Thread
{
	private static final String ipPrefix = "230.0.0.";
	int stationAddress, stationPort;
	DatagramSocket socket;
	
	public StationBroadcastThread(int address, int port)
	{
		stationAddress = address;
		stationPort = port;
		
		try
		{
			String ipAddress = ipPrefix + String.valueOf(address);
			InetAddress inetAddress = InetAddress.getByAddress(ipAddress.getBytes());
			socket = new DatagramSocket(stationPort, inetAddress);
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void run()
	{
		super.run();
	}

}
