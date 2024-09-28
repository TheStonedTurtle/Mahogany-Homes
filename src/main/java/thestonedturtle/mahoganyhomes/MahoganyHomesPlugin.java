package thestonedturtle.mahoganyhomes;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.UsernameChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.OverlayMenuClicked;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(
	name = "Mahogany Homes"
)
public class MahoganyHomesPlugin extends Plugin
{
	@VisibleForTesting
	static final Pattern CONTRACT_PATTERN = Pattern.compile("(Please could you g|G)o see (\\w*)[ ,][\\w\\s,-]*[?.] You can get another job once you have furnished \\w* home\\.");
	@VisibleForTesting
	static final Pattern REMINDER_PATTERN = Pattern.compile("You're currently on an (\\w*) Contract\\. Go see (\\w*)[ ,][\\w\\s,-]*\\. You can get another job once you have furnished \\w* home\\.");
	private static final Pattern CONTRACT_FINISHED = Pattern.compile("You have completed [\\d,]* contracts with a total of [\\d,]* points?\\.");
	private static final Pattern CONTRACT_ASSIGNED = Pattern.compile("(\\w*) Contract: Go see [\\w\\s,-]*\\.");
	private static final Pattern REQUEST_CONTACT_TIER = Pattern.compile("Could I have an? (\\w*) contract please\\?");

	@Getter
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ConfigManager configManager;

	@Inject
	private MahoganyHomesConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private MahoganyHomesOverlay textOverlay;

	@Inject
	private MahoganyHomesHighlightOverlay highlightOverlay;

	@Inject
	private TeleportItemOverlay teleportItemOverlay;

	@Inject
	private WorldMapPointManager worldMapPointManager;

	@Provides
	MahoganyHomesConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MahoganyHomesConfig.class);
	}

	@Getter
	private final List<GameObject> objectsToMark = new ArrayList<>();
	// Varb values: 0=default, 1=Needs repair, 2=Repaired, 3=Remove 4=Bulld 5-8=Built Tiers
	private final HashMap<Integer, Integer> varbMap = new HashMap<>();

	private BufferedImage mapIcon;
	private BufferedImage mapArrow;

	@Getter
	private Home currentHome;
	private boolean varbChange;
	private boolean wasTimedOut;
	@Getter
	private int contractTier = 0;

	@Getter
	public TeleportItem teleportItem;

	// Used to auto disable plugin if nothing has changed recently.
	private Instant lastChanged;
	private int lastCompletedCount = -1;

	@Getter
	private int sessionContracts = 0;
	@Getter
	private int sessionPoints = 0;

	private Duration pluginTimeoutDuration = Duration.ofMinutes(5);

	@Override
	public void startUp()
	{
		overlayManager.add(textOverlay);
		overlayManager.add(highlightOverlay);
		overlayManager.add(teleportItemOverlay);
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			loadFromConfig();
			clientThread.invoke(this::updateVarbMap);
		}
		lastChanged = Instant.now();
		lastCompletedCount = 0;
		pluginTimeoutDuration = Duration.ofMinutes(config.sessionTimeout());
	}

	@Override
	public void shutDown()
	{
		overlayManager.remove(textOverlay);
		overlayManager.remove(highlightOverlay);
		overlayManager.remove(teleportItemOverlay);
		worldMapPointManager.removeIf(MahoganyHomesWorldPoint.class::isInstance);
		client.clearHintArrow();
		varbMap.clear();
		objectsToMark.clear();
		currentHome = null;
		mapIcon = null;
		mapArrow = null;
		teleportItem = null;
		lastChanged = null;
		lastCompletedCount = -1;
		contractTier = 0;
		wasTimedOut = false;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged c)
	{
		if (!c.getGroup().equals(MahoganyHomesConfig.GROUP_NAME))
		{
			return;
		}

		if (c.getKey().equals(MahoganyHomesConfig.WORLD_MAP_KEY))
		{
			worldMapPointManager.removeIf(MahoganyHomesWorldPoint.class::isInstance);
			if (config.worldMapIcon() && currentHome != null)
			{
				worldMapPointManager.add(new MahoganyHomesWorldPoint(currentHome.getLocation(), this));
			}
		}
		else if (c.getKey().equals(MahoganyHomesConfig.HINT_ARROW_KEY))
		{
			client.clearHintArrow();
			if (client.getLocalPlayer() != null)
			{
				refreshHintArrow(client.getLocalPlayer().getWorldLocation());
			}
		}
		else if (c.getKey().equals(MahoganyHomesConfig.SESSION_TIMEOUT_KEY))
		{
			pluginTimeoutDuration = Duration.ofMinutes(config.sessionTimeout());
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		// Defer to game tick for better performance
		varbChange = true;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged e)
	{
		if (e.getGameState() == GameState.LOADING)
		{
			objectsToMark.clear();
		}
	}

	@Subscribe
	public void onUsernameChanged(UsernameChanged e)
	{
		loadFromConfig();
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		processGameObjects(event.getGameObject(), null);
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		processGameObjects(null, event.getGameObject());
	}

	@Subscribe
	public void onOverlayMenuClicked(OverlayMenuClicked e)
	{
		if (!e.getOverlay().equals(textOverlay))
		{
			return;
		}

		if (e.getEntry().getOption().equals(MahoganyHomesOverlay.CLEAR_OPTION))
		{
			setCurrentHome(null);
			updateConfig();
			lastChanged = null;
		}

		if (e.getEntry().getOption().equals(MahoganyHomesOverlay.TIMEOUT_OPTION))
		{
			lastChanged = Instant.now().minus(pluginTimeoutDuration);
			// Remove worldPoint and clear hint arrow when plugin times out
			worldMapPointManager.removeIf(MahoganyHomesWorldPoint.class::isInstance);
			client.clearHintArrow();
			wasTimedOut = true;
		}

		if (e.getEntry().getOption().equals(MahoganyHomesOverlay.RESET_SESSION_OPTION))
		{
			sessionContracts = 0;
			sessionPoints = 0;
		}
	}

	@Subscribe
	public void onGameTick(GameTick t)
	{
		if (contractTier == 0 || currentHome == null)
		{
			checkForContractTierDialog();
		}

		checkForAssignmentDialog();

		if (currentHome == null)
		{
			return;
		}

		if (varbChange)
		{
			varbChange = false;
			updateVarbMap();

			// If we couldn't find their contract tier recalculate it when they get close
			if (contractTier == 0)
			{
				calculateContractTier();
			}

			final int completed = getCompletedCount();
			if (completed != lastCompletedCount)
			{
				if (wasTimedOut)
				{
					// Refreshes hint arrow and world map icon if necessary
					setCurrentHome(currentHome);
					updateVarbMap();
					wasTimedOut = false;
				}

				lastCompletedCount = completed;
				lastChanged = Instant.now();
			}
		}

		// The plugin automatically disables after 5 minutes of inactivity.
		if (isPluginTimedOut())
		{
			if (!wasTimedOut)
			{
				// Remove worldPoint and clear hint arrow when plugin times out
				worldMapPointManager.removeIf(MahoganyHomesWorldPoint.class::isInstance);
				client.clearHintArrow();
			}
			wasTimedOut = true;
			return;
		}

		WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();

		refreshHintArrow(playerLocation);
		refreshTeleportItem(playerLocation);
	}

	@Subscribe
	public void onChatMessage(ChatMessage e)
	{
		if (!e.getType().equals(ChatMessageType.GAMEMESSAGE))
		{
			return;
		}

		final Matcher matcher = CONTRACT_ASSIGNED.matcher(Text.removeTags(e.getMessage()));
		if (matcher.matches())
		{
			final String type = matcher.group(1).toLowerCase();
			setContactTierFromString(type);
		}

		if (CONTRACT_FINISHED.matcher(Text.removeTags(e.getMessage())).matches())
		{
			sessionContracts++;
			sessionPoints += getPointsForCompletingTask();
			setCurrentHome(null);
			updateConfig();
		}
	}

	private void checkForContractTierDialog()
	{
		final Widget dialog = client.getWidget(ComponentID.DIALOG_PLAYER_TEXT);
		if (dialog == null)
		{
			return;
		}

		final String text = Text.sanitizeMultilineText(dialog.getText());
		final Matcher matcher = REQUEST_CONTACT_TIER.matcher(text);
		if (matcher.matches())
		{
			final String type = matcher.group(1).toLowerCase();
			setContactTierFromString(type);
		}
	}

	private void setContactTierFromString (String tier)
	{
		switch (tier)
		{
			case "beginner":
				contractTier = 1;
				break;
			case "novice":
				contractTier = 2;
				break;
			case "adept":
				contractTier = 3;
				break;
			case "expert":
				contractTier = 4;
				break;
		}
	}

	// Check for NPC dialog assigning or reminding us of a contract
	private void checkForAssignmentDialog()
	{
		final Widget dialog = client.getWidget(ComponentID.DIALOG_NPC_TEXT);
		if (dialog == null)
		{
			return;
		}

		final String npcText = Text.sanitizeMultilineText(dialog.getText());
		final Matcher startContractMatcher = CONTRACT_PATTERN.matcher(npcText);
		final Matcher reminderContract = REMINDER_PATTERN.matcher(npcText);
		String name = null;
		int tier = -1;
		if (startContractMatcher.matches())
		{
			name = startContractMatcher.group(2);
		}
		else if (reminderContract.matches())
		{
			name = reminderContract.group(2);
			tier = getTierByText(reminderContract.group(1));
		}

		if (name != null)
		{
			// They may have asked for a contract but already had one, check the configs
			if (contractTier == 0)
			{
				loadFromConfig();
				// If the config matches the assigned value then do nothing
				if (currentHome != null && currentHome.getName().equalsIgnoreCase(name))
				{
					return;
				}
			}

			// If we could parse the tier from the message (only for reminders) make sure the current tier matches it
			// update the tier and config with the parsed value
			if (tier != -1)
			{
				contractTier = tier;
			}

			for (final Home h : Home.values())
			{
				if (h.getName().equalsIgnoreCase(name) && (currentHome != h || isPluginTimedOut()))
				{
					setCurrentHome(h);
					updateConfig();
					break;
				}
			}
		}
	}

	private void setCurrentHome(final Home h)
	{
		currentHome = h;
		client.clearHintArrow();
		lastChanged = Instant.now();
		lastCompletedCount = 0;
		varbMap.clear();

		if (currentHome == null)
		{
			worldMapPointManager.removeIf(MahoganyHomesWorldPoint.class::isInstance);
			contractTier = 0;
			teleportItem = null;
			return;
		}

		if (config.worldMapIcon())
		{
			worldMapPointManager.removeIf(MahoganyHomesWorldPoint.class::isInstance);
			worldMapPointManager.add(new MahoganyHomesWorldPoint(h.getLocation(), this));
		}

		if (config.displayHintArrows() && client.getLocalPlayer() != null)
		{
			refreshHintArrow(client.getLocalPlayer().getWorldLocation());
		}

		if (config.highlightTeleports() && client.getLocalPlayer() != null)
		{
			clientThread.invoke(this::updateTeleportItem);
		}
	}

	private void updateTeleportItem()
	{
		teleportItem = currentHome.getTeleportItems().getClosestTeleportItemOnPlayer(client);
	}

	private void processGameObjects(final GameObject cur, final GameObject prev)
	{
		objectsToMark.remove(prev);

		if (cur == null || (!Hotspot.isHotspotObject(cur.getId()) && !Home.isLadder(cur.getId())))
		{
			return;
		}

		// Filter objects inside highlight overlay
		objectsToMark.add(cur);
	}

	private void updateVarbMap()
	{
		varbMap.clear();

		for (final Hotspot spot : Hotspot.values())
		{
			varbMap.put(spot.getVarb(), client.getVarbitValue(spot.getVarb()));
		}
	}

	private void loadFromConfig()
	{
		final String group = MahoganyHomesConfig.GROUP_NAME + "." + client.getAccountHash();
		final String name = configManager.getConfiguration(group, MahoganyHomesConfig.HOME_KEY);
		if (name == null)
		{
			return;
		}

		try
		{
			final Home h = Home.valueOf(name.trim().toUpperCase());
			setCurrentHome(h);
		}
		catch (IllegalArgumentException e)
		{
			log.warn("Stored unrecognized home: {}", name);
			currentHome = null;
			configManager.setConfiguration(group, MahoganyHomesConfig.HOME_KEY, null);
		}

		// Get contract tier from config if home was loaded successfully
		if (currentHome == null)
		{
			return;
		}

		final String tier = configManager.getConfiguration(group, MahoganyHomesConfig.TIER_KEY);
		if (tier == null)
		{
			return;
		}

		try
		{
			contractTier = Integer.parseInt(tier);
		}
		catch (IllegalArgumentException e)
		{
			log.warn("Stored unrecognized contract tier: {}", tier);
			contractTier = 0;
			configManager.unsetConfiguration(group, MahoganyHomesConfig.TIER_KEY);
		}
	}

	private void updateConfig()
	{
		final String group = MahoganyHomesConfig.GROUP_NAME + "." + client.getAccountHash();
		if (currentHome == null)
		{
			configManager.unsetConfiguration(group, MahoganyHomesConfig.HOME_KEY);
			configManager.unsetConfiguration(group, MahoganyHomesConfig.TIER_KEY);
		}
		else
		{
			configManager.setConfiguration(group, MahoganyHomesConfig.HOME_KEY, currentHome.getName());
			configManager.setConfiguration(group, MahoganyHomesConfig.TIER_KEY, contractTier);
		}
	}

	private void refreshHintArrow(final WorldPoint playerPos)
	{
		client.clearHintArrow();
		if (currentHome == null || !config.displayHintArrows())
		{
			return;
		}

		if (distanceBetween(currentHome.getArea(), playerPos) > 0)
		{
			client.setHintArrow(currentHome.getLocation());
		}
		else
		{
			// We are really close to house, only display a hint arrow if we are done.
			if (getCompletedCount() != 0)
			{
				return;
			}

			final Optional<NPC> npc = client.getNpcs().stream().filter(n -> n.getId() == currentHome.getNpcId()).findFirst();
			if (npc.isPresent())
			{
				client.setHintArrow(npc.get());
				return;
			}

			// Couldn't find the NPC, find the closest ladder to player
			WorldPoint location = null;
			int distance = Integer.MAX_VALUE;
			for (final GameObject obj : objectsToMark)
			{
				if (Home.isLadder(obj.getId()))
				{
					// Ensure ladder isn't in a nearby home.
					if (distanceBetween(currentHome.getArea(), obj.getWorldLocation()) > 0)
					{
						continue;
					}

					int diff = obj.getWorldLocation().distanceTo(client.getLocalPlayer().getWorldLocation());
					if (diff < distance)
					{
						distance = diff;
						location = obj.getWorldLocation();
					}
				}
			}

			if (location != null)
			{
				client.setHintArrow(location);
			}
		}
	}

	int getCompletedCount()
	{
		if (currentHome == null)
		{
			return -1;
		}

		int count = 0;
		for (final Hotspot hotspot : Hotspot.values())
		{
			final boolean requiresAttention = doesHotspotRequireAttention(hotspot.getVarb());
			if (!requiresAttention)
			{
				continue;
			}

			count++;
		}

		return count;
	}

	boolean doesHotspotRequireAttention(final int varb)
	{
		final Integer val = varbMap.get(varb);
		if (val == null)
		{
			return false;
		}

		return val == 1 || val == 3 || val == 4;
	}

	// This check assumes objects are on the same plane as the WorldArea (ignores plane differences)
	int distanceBetween(final WorldArea area, final WorldPoint point)
	{
		return area.distanceTo(new WorldPoint(point.getX(), point.getY(), area.getPlane()));
	}

	BufferedImage getMapIcon()
	{
		if (mapIcon != null)
		{
			return mapIcon;
		}

		mapIcon = ImageUtil.getResourceStreamFromClass(getClass(), "map-icon.png");
		return mapIcon;
	}

	BufferedImage getMapArrow()
	{
		if (mapArrow != null)
		{
			return mapArrow;
		}

		mapArrow = ImageUtil.getResourceStreamFromClass(getClass(), "map-arrow-icon.png");
		return mapArrow;
	}

	boolean isPluginTimedOut()
	{
		return lastChanged != null && Duration.between(lastChanged, Instant.now()).compareTo(pluginTimeoutDuration) >= 0;
	}

	int getPointsForCompletingTask()
	{
		// Contracts reward 2-5 points depending on tier
		return getContractTier() + 1;
	}

	private void calculateContractTier()
	{
		int tier = 0;
		// Values 5-8 are the tier of contract completed
		for (int val : varbMap.values())
		{
			tier = Math.max(tier, val);
		}

		// Normalizes tier from 5-8 to 1-4
		tier -= 4;
		contractTier = Math.max(tier, 0);
	}

	public Set<Integer> getRepairableVarbs()
	{
		return varbMap.keySet()
			.stream()
			.filter(this::doesHotspotRequireAttention)
			.collect(Collectors.toSet());
	}

	private void refreshTeleportItem(final WorldPoint playerPos)
	{
		if (currentHome == null || teleportItem == null || !config.highlightTeleports())
		{
			return;
		}

		if (distanceBetween(currentHome.getArea(), playerPos) - teleportItem.Distance < 10)
		{
			teleportItem = null;
		}
	}

	private int getTierByText(final String tierText)
	{
		switch (tierText)
		{
			case "Beginner":
				return 1;
			case "Novice":
				return 2;
			case "Adept":
				return 3;
			case "Expert":
				return 4;
			default:
				return -1;
		}
	}
}
