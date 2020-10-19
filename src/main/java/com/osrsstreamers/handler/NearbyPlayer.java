package com.osrsstreamers.handler;

import lombok.*;

import java.time.ZonedDateTime;

@Data
public class NearbyPlayer {

    ZonedDateTime lastSeen;
    StreamStatus status;

    public NearbyPlayer() {
        this.status = StreamStatus.UNDETERMINED;
        this.lastSeen = ZonedDateTime.now();
    }

}
