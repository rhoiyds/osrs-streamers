package com.osrsstreamers.handler;

import java.util.HashMap;
import java.util.Map;

public class VerifiedStreamers {

    public Map<String, String> rsnToTwitchLoginMap;

    public VerifiedStreamers() {
        rsnToTwitchLoginMap = new HashMap<>();
    }

    public String getTwitchName(String rsn) {
        return this.rsnToTwitchLoginMap.get(rsn);
    }

    public boolean isVerifiedStreamer(String name) {
        return this.rsnToTwitchLoginMap.containsKey(name);
    }

}
