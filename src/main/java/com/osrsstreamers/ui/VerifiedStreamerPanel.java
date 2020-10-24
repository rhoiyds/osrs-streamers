package com.osrsstreamers.ui;

import com.osrsstreamers.OsrsStreamersPlugin;
import com.osrsstreamers.handler.Streamer;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;

public class VerifiedStreamerPanel extends JPanel {

    VerifiedStreamerPanel(OsrsStreamersPlugin plugin, Streamer streamer) {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        JPanel nameWrapper = new JPanel(new BorderLayout());
        nameWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        nameWrapper.add(new JLabel(streamer.twitchName));
        nameWrapper.add(new JLabel(String.join(", ", streamer.characterNames)));
    }

}
