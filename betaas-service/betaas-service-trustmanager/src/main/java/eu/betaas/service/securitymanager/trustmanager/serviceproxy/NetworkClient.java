package eu.betaas.service.securitymanager.trustmanager.serviceproxy;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;

public class NetworkClient 
{
	private Logger logger= Logger.getLogger("betaas.service");
	private ArrayList<NetworkInterface> networkInterfaces;
	
	public NetworkClient ()
	{
		
	}
	
	public void getDevices ()
	{
		try
		{
			networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public ArrayList<NetworkInterface> getTopologi()
	{
		return networkInterfaces;
	}
	
	public static void main(String[] args)
	{
		NetworkClient myCli = new NetworkClient ();
		myCli.getDevices();
		ArrayList<NetworkInterface> myList = myCli.getTopologi();
		
		for (NetworkInterface active : myList) 
		{ 
			try
			{
				System.out.println ("Device Name: " + active.getDisplayName()); 
				System.out.println ("Interface Name: " + active.getName());
				System.out.println ("Physical Address: " + active.getHardwareAddress());
				if (active.getInetAddresses().hasMoreElements())
				{
					System.out.println ("IP Address: " + active.getInetAddresses().nextElement().toString());
				}				
				System.out.println ("---------------------------------------");
			}
			catch (SocketException e)
			{
				e.printStackTrace();
			}
		}
	}

}
