package com.osrsstreamers;

import com.google.gson.Gson;
import com.osrsstreamers.handler.Streamer;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;


public class OsrsStreamersTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(OsrsStreamersPlugin.class);
		RuneLite.main(args);
	}

	@Test
	public void testStreamerNamesUnique() throws FileNotFoundException {
		BufferedReader br = new BufferedReader(new FileReader("resources/streamers.json"));
		Streamer[] streamers = new Gson().fromJson(br, Streamer[].class);
		List<String> twitchNames = Arrays.stream(streamers).map(streamer -> streamer.twitchName).collect((Collectors.toList()));
		List<String> nonDistinctStreamers = twitchNames.stream().filter(i -> Collections.frequency(twitchNames, i) > 1).collect(Collectors.toList());
		assertEquals(Collections.EMPTY_LIST, nonDistinctStreamers);
	}

	@Test
	public void testCharacterNamesUnique() throws FileNotFoundException {
		BufferedReader br = new BufferedReader(new FileReader("resources/streamers.json"));
		Streamer[] streamers = new Gson().fromJson(br, Streamer[].class);
		List<String> flatCharacterList = Arrays.stream(streamers).flatMap(streamer -> streamer.characterNames.stream()).collect(Collectors.toList());
		List<String> nonDistinctCharacterNames = flatCharacterList.stream().filter(i -> Collections.frequency(flatCharacterList, i) > 1).collect(Collectors.toList());
		assertEquals(Collections.EMPTY_LIST, nonDistinctCharacterNames);
	}
}