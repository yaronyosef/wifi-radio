package com.theisenp.wifi;

public abstract class WiFiMessage
{

	enum WifiMessageType
	{
		AddSong,
		Audio,
	}
	private static final int WIFI_TAG = 0xB007B007;
	private final WifiMessageType type;
	private final String user;
	
	/**
	 * Base class for all messages sent between clients
	 * @param messageType
	 * The type of message being sent (ie AudioData, AddSong, etc)
	 * @param messageUser
	 * The user who initiated this message
	 */
	protected WiFiMessage(WifiMessageType messageType, String messageUser)
	{
		type = messageType;
		user = messageUser;
	}
	
	/**
	 * @return
	 * Returns the byte data for the common header shared by all of the messages.
	 * Includes the Application tag, message type, and user name
	 */
	protected byte[] getHeader()
	{
		byte[] header = new byte[25];
		byte typeByte = 0;
		if(type == WifiMessageType.AddSong)
		{
			typeByte = 0;
		}
		else if(type == WifiMessageType.Audio)
		{
			typeByte = 1;
		}
		
		System.arraycopy(intToByteArray(WIFI_TAG), 0, header, 0, 4);
		header[4] = typeByte;
		byte[] userBytes = user.getBytes();
		System.arraycopy(userBytes, 0, header, 5, Math.min(userBytes.length, 20));
		
		return header;
	}
	
	/**
	 * This function should be overridden by all children of this base class
	 * @return
	 */
	abstract public byte[] getMessage();
	
	/**
	 * Finds the bytes in the message that correspond to the application tag and return them as an int
	 */
	public static int readTag(byte[] message)
	{
		byte[] tagBytes = new byte[4];
		System.arraycopy(message, 0, tagBytes, 0, 4);
		return byteArrayToInt(tagBytes);
	}
	
	/**
	 * Finds the byte in the message that corresponds to the message type and return it
	 */
	public static byte readType(byte[] message)
	{
		return message[4];
	}
	
	/**
	 * Finds the bytes in the message that correspond to the user name and return them as a String
	 */
	public static String readUser(byte[] message)
	{
		byte[] userBytes = new byte[20];
		System.arraycopy(message, 5, userBytes, 0, 20);
		return new String(userBytes);
	}
	
	
	/**
	 * @param value
	 * The int to be converted to bytes
	 * @return
	 * The byte representation of the integer argument
	 */
	public static final byte[] intToByteArray(int value)
	{
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
	}
	
	
	
	/**
	 * @param b
	 * The bytes to be converted to an int
	 * @return
	 * The integer representation of the byte array argument
	 */
	public static final int byteArrayToInt(byte [] b) {
        return (b[0] << 24)
                + ((b[1] & 0xFF) << 16)
                + ((b[2] & 0xFF) << 8)
                + (b[3] & 0xFF);
}
}
