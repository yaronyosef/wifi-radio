package com.theisenp.wifi;

public class AddSongMessage extends WiFiMessage
{
	private int songID;
	private String name;
	private String artist;
	
	protected AddSongMessage(String messageUser, int messageSongID, String songName, String songArtist)
	{
		super(WifiMessageType.AddSong, messageUser);
		songID = messageSongID;
		name = songName;
		artist = songArtist;
	}
	
	@Override
	public byte[] getMessage()
	{
		byte[] message = new byte[69];
		
		byte[] header = getHeader();
		byte[] nameBytes = name.getBytes();
		byte[] artistBytes = artist.getBytes();
		
		System.arraycopy(header, 0, message, 0, 25);
		System.arraycopy(intToByteArray(songID), 0, message, 25, 4);
		System.arraycopy(nameBytes, 0, message, 29, Math.min(nameBytes.length, 20));
		System.arraycopy(artistBytes, 0, message, 49, Math.min(artistBytes.length, 20));
		
		return message;
	}
	
	public static String readArtist(byte[] message)
	{
		byte[] artistBytes = new byte[20];
		System.arraycopy(message, 49, artistBytes, 0, 20);
		return new String(artistBytes);
	}
	
	public static String readName(byte[] message)
	{
		byte[] nameBytes = new byte[20];
		System.arraycopy(message, 29, nameBytes, 0, 20);
		return new String(nameBytes);
	}
	
	public static int readSongID(byte[] message)
	{
		byte[] idBytes = new byte[4];
		System.arraycopy(message, 25, idBytes, 0, 4);
		return byteArrayToInt(idBytes);
	}

}
