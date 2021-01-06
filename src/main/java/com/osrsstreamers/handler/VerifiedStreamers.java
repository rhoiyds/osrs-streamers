package com.osrsstreamers.handler;

import java.lang.reflect.Type;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;

@Slf4j
public class VerifiedStreamers {

    private static final String VERIFIED_STREAMERS_LIST = "https://raw.githubusercontent.com/rhoiyds/osrs-streamers/master/resources/streamers.json";

    //Purposely avoiding the 'osrsstreamers' config group - because the plugin refreshes to any changes on the group
    //causing an infinite loop on start up when modifying meta data (last updated and streamers object storage)
    public static final String CONFIG_GROUP = "osrsstreamersdata";

    public static final String CONFIG_STREAMER_KEY = "streamers";

    public static final String CONFIG_LAST_UPDATED_KEY = "lastupdated";

    private static final Type COLLECTION_TYPE = new TypeToken<Collection<Streamer>>() {}.getType();

    public Map<String, String> rsnToTwitchLoginMap;

    public List<Streamer> streamers;

    private final ConfigManager configManager;

    public VerifiedStreamers(ConfigManager configManager) {
        this.configManager = configManager;
        rsnToTwitchLoginMap = new HashMap<>();
        Instant lastUpdated = configManager.getConfiguration(CONFIG_GROUP, CONFIG_LAST_UPDATED_KEY, Instant.class);

        //If it has been more than 24 hours since last update
        if (Objects.isNull(lastUpdated) || lastUpdated.plus(24, ChronoUnit.HOURS).isBefore(Instant.now())) {
            log.debug("OSRS Streamers: 24 hours passed since lastUpdated was set");
            try {
                URL url = new URL(VERIFIED_STREAMERS_LIST);
                InputStreamReader reader = new InputStreamReader(url.openStream());
                Collection<Streamer> streamers = new Gson().fromJson(reader, COLLECTION_TYPE);
                addAllCharacterNamesFromStreamers(streamers);
                configManager.setConfiguration(CONFIG_GROUP, CONFIG_STREAMER_KEY, new Gson().toJson(streamers));
                configManager.setConfiguration(CONFIG_GROUP, CONFIG_LAST_UPDATED_KEY, Instant.now().toString());
                log.debug("OSRS Streamers: Successfully loaded streamers from remote source and set lastUpdated");
            } catch (Exception e) {
                log.error("Error loading streamer list from GitHub", e);
                loadStreamersFromLocalCopy();
            }
        } else {
            loadStreamersFromLocalCopy();
        }
    }

    public String getTwitchName(String rsn) {
        return this.rsnToTwitchLoginMap.get(rsn);
    }

    public boolean isVerifiedStreamer(String name) {
        return this.rsnToTwitchLoginMap.containsKey(name);
    }

    private void loadStreamersFromLocalCopy() {
        log.debug("OSRS Streamers: Loading streamers from local source");
        String streamersJSON = configManager.getConfiguration(CONFIG_GROUP, CONFIG_STREAMER_KEY);
        Collection<Streamer> streamers = new Gson().fromJson(streamersJSON, COLLECTION_TYPE);
        addAllCharacterNamesFromStreamers(streamers);
    }

    private void addAllCharacterNamesFromStreamers(Collection<Streamer> streamers) {
        this.streamers = new ArrayList<>(streamers);
        streamers.forEach(streamer -> {
            streamer.characterNames.forEach(characterName -> rsnToTwitchLoginMap.put(characterName, streamer.twitchName));
        });
    }

}
