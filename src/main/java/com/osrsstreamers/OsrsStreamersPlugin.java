package com.osrsstreamers;

import com.google.gson.JsonParseException;
import com.google.inject.Provides;
import com.google.gson.Gson;
import javax.inject.Inject;
import javax.inject.Named;
import com.osrsstreamers.handler.*;
import com.osrsstreamers.twitch.TwitchAuthenticateResponse;
import com.osrsstreamers.ui.OsrsStreamersPluginPanel;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;
import net.runelite.http.api.RuneLiteAPI;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Slf4j
@PluginDescriptor(
	name = "OSRS Streamers"
)
public class OsrsStreamersPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private OsrsStreamersConfig config;

	@Inject
	private EventBus eventBus;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private StreamingPlayerOverlay streamingPlayerOverlay;

	@Inject
	private StreamingPlayerMinimapOverlay streamingPlayerMinimapOverlay;

	@Inject
	@Named("developerMode")
	boolean developerMode;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ChatMessageManager chatMessageManager;

	public StreamerHandler streamerHandler;

	private OsrsStreamersPluginPanel panel;

	private NavigationButton navButton;

    private boolean loggingIn;

	private static final String TWITCH_API_URL = "https://id.twitch.tv/oauth2/validate";

	private static final String TOKEN_GENERATOR_URL = "https://rhoiyds.github.io/osrs-streamers/";

	private static final String TOKEN_COMMAND = "gettoken";

	final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "/icon.png");

	private final Gson gson = new Gson();

	@Override
	protected void startUp()
	{
        loggingIn = true;
        startHandlingTwitchStreams();
	}

	@Override
	protected void shutDown()
	{
        stopHandlingTwitchStreams();
	}

	private void stopHandlingTwitchStreams() {
		if (Objects.nonNull(this.navButton)) {
			clientToolbar.removeNavigation(this.navButton);
		}
		if (Objects.nonNull(streamerHandler)) {
			eventBus.unregister(streamerHandler);
			streamerHandler = null;
			this.streamingPlayerOverlay.streamerHandler = null;
			this.streamingPlayerMinimapOverlay.streamerHandler = null;
			overlayManager.remove(streamingPlayerOverlay);
			overlayManager.remove(streamingPlayerMinimapOverlay);
		}
	}

	private void startHandlingTwitchStreams() {
		if (Objects.isNull(streamerHandler)) {
			streamerHandler = new StreamerHandler(client, config);
			checkTokenValidity();
			eventBus.register(streamerHandler);
			this.streamingPlayerOverlay.streamerHandler = streamerHandler;
			this.streamingPlayerMinimapOverlay.streamerHandler = streamerHandler;
			overlayManager.add(streamingPlayerOverlay);
			overlayManager.add(streamingPlayerMinimapOverlay);
			this.createPanel();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (!configChanged.getGroup().equals("osrsstreamers"))
		{
			return;
		}

		stopHandlingTwitchStreams();
		startHandlingTwitchStreams();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
        if (gameStateChanged.getGameState() == GameState.LOGGING_IN)
        {
            loggingIn = true;
        }
	}

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (loggingIn && !streamerHandler.validToken) {
            loggingIn = false;
            final String message = new ChatMessageBuilder()
                    .append(ChatColorType.HIGHLIGHT)
                    .append("OSRS Streamers: Invalid/expired Twitch token - type the command ::" + TOKEN_COMMAND + " in chat to generate a token.")
                    .build();
            chatMessageManager.queue(
                    QueuedMessage.builder()
                            .type(ChatMessageType.CONSOLE)
                            .runeLiteFormattedMessage(message)
                            .build());
        }
    }

	private void createPanel() {
		this.panel = new OsrsStreamersPluginPanel(this);
		this.navButton = NavigationButton.builder()
				.tooltip("OSRS Streamers")
				.icon(this.icon)
				.priority(7)
				.panel(this.panel)
				.build();
		clientToolbar.addNavigation(this.navButton);
	}

	private void checkTokenValidity() {
		this.streamerHandler.validToken = false;
		HttpUrl.Builder httpBuilder = HttpUrl.parse(TWITCH_API_URL).newBuilder();
		if (!Objects.nonNull(config.userAccessToken())) {
			log.debug("OSRS Streamers: Twitch Authentication token is not set in plugin settings.");
			if (Objects.nonNull(this.panel)) {
				this.panel.init();
			}
			return;
		}
		Request request = new Request.Builder().url(httpBuilder.build()).addHeader("Authorization", "Bearer " + config.userAccessToken()).build();

		try  {
			Response response = RuneLiteAPI.CLIENT.newCall(request).execute();
			if (!response.isSuccessful()) {
				response.body().close();
				log.debug("OSRS Streamers: There is a problem with the twitch token.");
				if (Objects.nonNull(this.panel)) {
					this.panel.init();
				}
				return;
			}
			InputStream in = response.body().byteStream();
			TwitchAuthenticateResponse twitchApiResponse = gson.fromJson(new InputStreamReader(in), TwitchAuthenticateResponse.class);
			this.streamerHandler.validToken = true;
			//ExpiresIn field is in second until expiration. Convert from seconds to days by dividing by 86400
			this.streamerHandler.daysUntilTokenExpiration = (int)(twitchApiResponse.getExpires_in() / 86400);
			log.debug("OSRS Streamers: Token is expiring in days: " + this.streamerHandler.daysUntilTokenExpiration);
			if (Objects.nonNull(this.panel)) {
				this.panel.init();
			}
			response.body().close();
		}
		catch (JsonParseException | IllegalArgumentException | IOException ex) {
			ex.printStackTrace();
		}
	}

	@Provides
	OsrsStreamersConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(OsrsStreamersConfig.class);
	}

	@Schedule(period = 2, unit = ChronoUnit.SECONDS, asynchronous = true)
	public void checkNearbyPlayers() {
		if (Objects.nonNull(this.streamerHandler)) {
			this.streamerHandler.removeOldNearbyPlayers();
			if (Objects.nonNull(config.userAccessToken()) && this.streamerHandler.validToken) {
				this.streamerHandler.fetchStreamStatusOfUndeterminedStreamers();
			}
		}
	}

	@Subscribe
	public void onCommandExecuted(CommandExecuted commandExecuted)
	{
		if (developerMode && commandExecuted.getCommand().equals("stream"))
		{
			String characterNameAndTwitchName = String.join(" ", commandExecuted.getArguments());
			streamerHandler.addStreamerFromConsole(characterNameAndTwitchName.split("/")[0], characterNameAndTwitchName.split("/")[1]);
		}

		if (commandExecuted.getCommand().equals(TOKEN_COMMAND)) {
			LinkBrowser.browse(TOKEN_GENERATOR_URL);
		}
	}

}
