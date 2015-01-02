package org.bardes.mplayer.net;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.List;

public class Interface
{
	public NetworkInterface network;

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Interface)
		{
			Interface z = (Interface) obj;
			return network.equals(z.network);
		}
		return super.equals(obj);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(network.getDisplayName());
		List<InterfaceAddress> interfaceAddresses = network.getInterfaceAddresses();
		for (InterfaceAddress ia : interfaceAddresses)
		{
			InetAddress address = ia.getAddress();
			
			if (address instanceof Inet6Address)
				continue;
			
			if (address.isMulticastAddress())
				continue;
			
			sb.append(address.toString());
			sb.append(" ");
		}
		
		return sb.toString();
	}

	public String getName()
	{
		return network.getName();
	}
}
