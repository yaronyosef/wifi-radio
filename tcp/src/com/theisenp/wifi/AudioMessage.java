package com.theisenp.wifi;

public class AudioMessage extends WiFiMessage
{

	private int songID;
	private int index;
	private int dataLength;
	
	protected AudioMessage(String messageUser, int messageSongID, int messageIndex, int messageDataLength)
	{
		super(WifiMessageType.Audio, messageUser);
		songID = messageSongID;
		index = messageIndex;
		dataLength = messageDataLength;
	}
	
	
	/**
	 * Returns the raw bytes for this message so that it can be sent over the sockets
	 * (non-Javadoc)
	 * @see com.theisenp.wifi.WiFiMessage#getMessage()
	 */
	@Override
	public byte[] getMessage()
	{
		byte[] message = new byte[37];
		
		byte[] header = getHeader();
		
		System.arraycopy(header, 0, message, 0, 25);
		System.arraycopy(intToByteArray(songID), 0, message, 25, 4);
		System.arraycopy(intToByteArray(index), 0, message, 29, 4);
		System.arraycopy(intToByteArray(dataLength), 0, message, 33, 4);
		
		return message;
	}

}
