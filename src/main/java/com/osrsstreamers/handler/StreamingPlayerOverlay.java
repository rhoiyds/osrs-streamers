package com.osrsstreamers.handler;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;
import java.util.Objects;

public class StreamingPlayerOverlay extends Overlay {

    @Inject
    private Client client;

    public StreamerHandler streamerHandler;

    private static final Color TWITCH_COLOR = new Color(95, 58, 162);

    @Override
    public Dimension render(Graphics2D graphics) {

        client.getPlayers().forEach(player -> {
            NearbyPlayer nearbyPlayer = streamerHandler.getNearbyPlayer(player.getName());
            if (Objects.nonNull(nearbyPlayer) && StreamStatus.LIVE.equals(nearbyPlayer.getStatus())) {
                OverlayUtil.renderActorOverlay(graphics, player, "t.tv/" + player.getName(), TWITCH_COLOR);
            }
        });

        return null;
    }
}
