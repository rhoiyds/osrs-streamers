package com.osrsstreamers;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;


public class OsrsStreamersTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(OsrsStreamersPlugin.class);
		RuneLite.main(args);
	}
}