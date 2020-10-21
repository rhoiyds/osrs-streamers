package com.osrsstreamers.handler;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class VerifiedStreamers {

    public Map<String, String> rsnToTwitchLoginMap;


    public VerifiedStreamers() throws Exception {
        // Local testing
        // File file = new File ("filepath");
        // InputStreamReader reader = new InputStreamReader(new FileInputStream(file));

        //Remote update without plug-in version update
        URL url = new URL("https://raw.githubusercontent.com/rhoiyds/osrs-streamers/master/_resources/streamers.json");
        InputStreamReader reader = new InputStreamReader(url.openStream());
        Type collectionType = new TypeToken<Collection<Streamer>>(){}.getType();
        Collection<Streamer> streamers = new Gson().fromJson(reader, collectionType);
        rsnToTwitchLoginMap = new HashMap<>();
        streamers.forEach(streamer -> {
            streamer.characterNames.forEach(characterName -> rsnToTwitchLoginMap.put(characterName, streamer.twitchName));
        });
    }

    public String getTwitchName(String rsn) {
        return this.rsnToTwitchLoginMap.get(rsn);
    }

    public boolean isVerifiedStreamer(String name) {
        return this.rsnToTwitchLoginMap.containsKey(name);
    }

}
