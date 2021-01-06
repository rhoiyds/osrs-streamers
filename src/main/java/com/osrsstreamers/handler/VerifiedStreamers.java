package com.osrsstreamers.handler;

import java.lang.reflect.Type;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import net.runelite.http.api.RuneLiteAPI;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
public class VerifiedStreamers {

    private static final String VERIFIED_STREAMERS_LIST = "https://raw.githubusercontent.com/rhoiyds/osrs-streamers/master/resources/streamers.json";

    private static final Type COLLECTION_TYPE = new TypeToken<Collection<Streamer>>() {}.getType();

    public Map<String, String> rsnToTwitchLoginMap;

    public List<Streamer> streamers;

    public VerifiedStreamers() {
        rsnToTwitchLoginMap = new HashMap<>();
        try {
            Request request = new Request.Builder().url(HttpUrl.parse(VERIFIED_STREAMERS_LIST)).build();
            Response response = RuneLiteAPI.CLIENT.newCall(request).execute();
            if (!response.isSuccessful()) {
                response.body().close();
                log.debug("OSRS Streamers: Error while retrieving verified streamers from GitHub: {}", response.body());
                return;
            }
            String json = response.body().string();
            Collection<Streamer> streamers = new Gson().fromJson(json, COLLECTION_TYPE);
            this.streamers = new ArrayList<>(streamers);
            streamers.forEach(streamer -> {
                streamer.characterNames.forEach(characterName -> rsnToTwitchLoginMap.put(characterName, streamer.twitchName));
            });
            log.debug("OSRS Streamers: Successfully loaded verified streamers from GitHub");
        } catch (Exception e) {
            log.error("Error loading verified streamer from GitHub", e);
        }
    }

    public String getTwitchName(String rsn) {
        return this.rsnToTwitchLoginMap.get(rsn);
    }

    public boolean isVerifiedStreamer(String name) {
        return this.rsnToTwitchLoginMap.containsKey(name);
    }

}
