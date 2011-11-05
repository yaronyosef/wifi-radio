package com.theisenp.wifi;

public class WiFiMessage
{

	enum WifiMessageType
	{
		AddSong,
		Audio,
	}
	private static final int WIFI_TAG = 0xB007B007;
	private final WifiMessageType type;
	private final String user;
	
	protected WiFiMessage(WifiMessageType messageType, String messageUser)
	{
		type = messageType;
		user = messageUser;
	}
	
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
	
	public byte[] getMessage() {
		return null;
	}
	
	public static int readTag(byte[] message)
	{
		byte[] tagBytes = new byte[4];
		System.arraycopy(message, 0, tagBytes, 0, 4);
		return byteArrayToInt(tagBytes);
	}
	
	public static byte readType(byte[] message)
	{
		return message[4];
	}
	
	public static String readUser(byte[] message)
	{
		byte[] userBytes = new byte[20];
		System.arraycopy(message, 5, userBytes, 0, 20);
		return new String(userBytes);
	}
	
	public static final byte[] intToByteArray(int value)
	{
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
	}
	
	public static final int byteArrayToInt(byte [] b) {
        return (b[0] << 24)
                + ((b[1] & 0xFF) << 16)
                + ((b[2] & 0xFF) << 8)
                + (b[3] & 0xFF);
}
}
