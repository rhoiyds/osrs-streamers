package com.osrsstreamers.ui;

import com.osrsstreamers.OsrsStreamersPlugin;
import net.runelite.client.plugins.info.InfoPanel;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class OsrsStreamersPluginPanel extends PluginPanel {

    OsrsStreamersPlugin plugin;

    private final JLabel title = new JLabel();

    private IconTextField searchBar;

    private JPanel streamersContainer;

    private static final ImageIcon ARROW_RIGHT_ICON;
    private static final ImageIcon GITHUB_ICON;

    public OsrsStreamersPluginPanel(OsrsStreamersPlugin osrsStreamersPlugin) {
        this.plugin = osrsStreamersPlugin;
        init();

//        setLayout(new BorderLayout());
//        setBorder(new EmptyBorder(10, 10, 10, 10));
//
//
//
//        final JPanel streamerPanel = new JPanel();
//
//        // wrapper for the main content panel to keep it from stretching
//        final JPanel contentWrapper = new JPanel(new BorderLayout());
//        contentWrapper.add(Box.createGlue(), BorderLayout.CENTER);
//        JScrollPane contentWrapperPane = new JScrollPane(contentWrapper);
//        contentWrapperPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//
//
//        contentWrapper.add(streamerPanel, BorderLayout.NORTH);
//        add(northAnchoredPanel, BorderLayout.NORTH);
//        add(contentWrapper, BorderLayout.SOUTH);

    }

    static
    {
        ARROW_RIGHT_ICON = new ImageIcon(ImageUtil.getResourceStreamFromClass(InfoPanel.class, "/util/arrow_right.png"));
        GITHUB_ICON = new ImageIcon(ImageUtil.getResourceStreamFromClass(InfoPanel.class, "github_icon.png"));
    }

    void init()
    {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        final Font smallFont = FontManager.getRunescapeSmallFont();

        streamersContainer = new JPanel();
        streamersContainer.setBorder(new EmptyBorder(10, 0, 0, 0));
        streamersContainer.setLayout(new GridLayout(0, 1, 0, 10));
        this.plugin.streamerHandler.verifiedStreamers.streamers.forEach(streamer -> {
            streamersContainer.add(buildStreamerPanel(GITHUB_ICON, streamer.twitchName, String.join(", ", streamer.characterNames), "https://twitch.tv/" + streamer.twitchName));
        });

        title.setText("OSRS Streamers");
        title.setForeground(Color.WHITE);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;

        this.searchBar = new IconTextField();
        searchBar.setIcon(IconTextField.Icon.SEARCH);
        searchBar.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 30));
        searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        searchBar.setMinimumSize(new Dimension(0, 30));
        searchBar.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                update();
            }
        });
        searchBar.addClearListener(() -> update());

        // the panel that stays at the top and doesn't scroll
        // contains the title and buttons
        final JPanel northAnchoredPanel = new JPanel();
        northAnchoredPanel.setLayout(new BoxLayout(northAnchoredPanel, BoxLayout.Y_AXIS));
        northAnchoredPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        northAnchoredPanel.add(title);
        northAnchoredPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        northAnchoredPanel.add(searchBar);

        add(northAnchoredPanel, BorderLayout.NORTH);
        add(streamersContainer, BorderLayout.CENTER);

    }

    /**
     * Builds a link panel with a given icon, text and url to redirect to.
     */
    private static JPanel buildStreamerPanel(ImageIcon icon, String topText, String bottomText, String url)
    {
        return buildStreamerPanel(icon, topText, bottomText, () -> LinkBrowser.browse(url));
    }

    /**
     * Builds a link panel with a given icon, text and callable to call.
     */
    private static JPanel buildStreamerPanel(ImageIcon icon, String topText, String bottomText, Runnable callback)
    {
        JPanel container = new JPanel();
        container.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        container.setLayout(new BorderLayout());
        container.setBorder(new EmptyBorder(10, 10, 10, 10));

        final Color hoverColor = ColorScheme.DARKER_GRAY_HOVER_COLOR;
        final Color pressedColor = ColorScheme.DARKER_GRAY_COLOR.brighter();

        JLabel iconLabel = new JLabel(icon);
        container.add(iconLabel, BorderLayout.WEST);

        JPanel textContainer = new JPanel();
        textContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        textContainer.setLayout(new GridLayout(2, 1));
        textContainer.setBorder(new EmptyBorder(5, 10, 5, 10));

        container.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                container.setBackground(pressedColor);
                textContainer.setBackground(pressedColor);
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                callback.run();
                container.setBackground(hoverColor);
                textContainer.setBackground(hoverColor);
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                container.setBackground(hoverColor);
                textContainer.setBackground(hoverColor);
                container.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                container.setBackground(ColorScheme.DARKER_GRAY_COLOR);
                textContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
                container.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        JLabel topLine = new JLabel(topText);
        topLine.setForeground(Color.WHITE);
        topLine.setFont(FontManager.getRunescapeSmallFont());

        JLabel bottomLine = new JLabel(bottomText);
        bottomLine.setForeground(Color.WHITE);
        bottomLine.setFont(FontManager.getRunescapeSmallFont());

        textContainer.add(topLine);
        textContainer.add(bottomLine);

        container.add(textContainer, BorderLayout.CENTER);

        JLabel arrowLabel = new JLabel(ARROW_RIGHT_ICON);
        container.add(arrowLabel, BorderLayout.EAST);

        return container;
    }

    public void update() {
        SwingUtilities.invokeLater(() -> updatePanel());
    }

    private void updatePanel() {
        repaint();
        revalidate();
    }
}
