package com.theisenp.wifi;

import android.R.string;
import android.text.format.Time;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class HostsHandler {
	private NodeList localhosts;
	private NodeList remotehosts;
	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;
	private Document localdoc;
	private Document remotedoc;
	
	public HostsHandler() throws SAXException{
		Time currentTime = new Time();
		currentTime.setToNow();
		fetchLocalHosts();
		InetAddress addr = null;
		fetchRemoteHosts(addr);
	}
	/*
	 * This reads the local hosts.xml file into a DOM tree
	 */
	private void fetchLocalHosts() throws SAXException
	{
		try {
			docFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docFactory.newDocumentBuilder();
			localdoc = docBuilder.parse("/res/values/hosts.xml");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch(ParserConfigurationException e) {}
		localhosts = localdoc.getElementsByTagName("host");
	}
	/*
	 * TODO: Make this actually fetch the remote hosts.xml file
	 * and put it in a DOM tree
	 */
	private void fetchRemoteHosts(InetAddress addr) {} 
	
	/*
	 * This function reads through the remote hosts.xml file to see if there are entries
	 * which are not included in the local one or which do not match the local entries
	 * and resolves these based on the time stamp.
	 */
	public void updateLocalHosts()
	{
		for(int i = 0; i < remotehosts.getLength(); i++)
		{
			boolean isInLocalHosts = false;
			Element remoteHost = (Element)remotehosts.item(i);
			for(int j = 0; j < localhosts.getLength(); j++)
			{
				Element localHost = (Element)localhosts.item(j);
				/*
				 * This checks to see if the name of a host in the remote hosts.xml
				 * appears anywhere in the local hosts.xml
				 */
				if(remoteHost.getAttribute("name").equals(localHost.getAttribute("name")))
				{
					isInLocalHosts = true;
					Element ipAddress_R = (Element)remoteHost.getElementsByTagName("IP").item(0);
					Element ipAddress_L = (Element)localHost.getElementsByTagName("IP").item(0);
					/*
					 * This if statement checks to see if the matching host names also have matching
					 * IP Addresses
					 */
					if(!ipAddress_R.getTextContent().equalsIgnoreCase(ipAddress_L.getTextContent()))
					{
						Element time_R = (Element)remoteHost.getElementsByTagName("time").item(0);
						Element time_L = (Element)localHost.getElementsByTagName("time").item(0);
						/*
						 * This if statement checks if the date of the remote host listing
						 * is newer than the date of the date of the local host listing
						 */
						if(time_R.getTextContent().compareTo(time_L.getTextContent()) >0 )
						{
							/*
							 * TODO: put the code to change the text content of the 
							 * local host entry to the more current one found in the 
							 * remote hosts.xml file.
							 * This may be better served with attributes.
							 */
						
						}
					}
				}
				
			}
			if(!isInLocalHosts)
			{
				/*
				 * TODO: write the code to add a new child to the local hosts.xml file
				 * with the host found in the remote file
				 */
			
			}
			
		}
		
	
	}
	/*
	 * TODO:This function will update the <time> element in the entry for the 
	 * currently connected host to the current time
	 */
	public void updateHostTime(string hostName)
	{
		
	}
}
