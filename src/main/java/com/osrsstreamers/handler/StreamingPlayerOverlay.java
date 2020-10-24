package com.osrsstreamers.handler;

import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.awt.*;
import java.util.Objects;

public class StreamingPlayerOverlay extends Overlay {

    @Inject
    private Client client;

    public StreamerHandler streamerHandler;

    private static final Color TWITCH_COLOR = new Color(133, 76, 231);
    private static final Color OFFLINE_COLOR = new Color(169, 169, 169);
    private static final int PLAYER_OVERHEAD_TEXT_MARGIN =  40;

    @Inject
    private StreamingPlayerOverlay()
    {
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.MED);
    }


    @Override
    public Dimension render(Graphics2D graphics) {

        client.getPlayers().stream().filter(player -> !player.equals(client.getLocalPlayer())).forEach(player -> {
            NearbyPlayer nearbyPlayer = streamerHandler.getNearbyPlayer(player.getName());
            if (Objects.nonNull(nearbyPlayer) && !StreamStatus.NOT_STREAMER.equals(nearbyPlayer.status)) {
                Color color = OFFLINE_COLOR;
                if (StreamStatus.LIVE.equals(nearbyPlayer.getStatus())) {
                    color = TWITCH_COLOR;
                }
                final Polygon poly = player.getCanvasTilePoly();
                if (poly != null)
                {
                    OverlayUtil.renderPolygon(graphics, poly, color);
                }
                String twitchHandle = "t.tv/" + nearbyPlayer.getTwitchName();
                int zOffset = player.getLogicalHeight() + PLAYER_OVERHEAD_TEXT_MARGIN;
                Point textLocation = player.getCanvasTextLocation(graphics, Text.sanitize(Objects.requireNonNull(player.getName())), zOffset);
                OverlayUtil.renderTextLocation(graphics, textLocation, twitchHandle, color);
            }
        });

        return null;
    }
}
