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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectChanged;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.UsernameChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
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
	private static final String CONTRACT_FINISHED_MESSAGE = "Thank you so much! Would you like a cup of tea before you go?";
	private static final Duration PLUGIN_TIMEOUT_DURATION = Duration.ofMinutes(5);

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

	// Used to auto disable plugin if nothing has changed recently.
	private Instant lastChanged;
	private int lastCompletedCount = -1;

	@Override
	public void startUp()
	{
		overlayManager.add(textOverlay);
		overlayManager.add(highlightOverlay);
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			loadFromConfig();
			clientThread.invoke(this::updateVarbMap);
		}
		lastChanged = Instant.now();
		lastCompletedCount = 0;
	}

	@Override
	public void shutDown()
	{
		overlayManager.remove(textOverlay);
		overlayManager.remove(highlightOverlay);
		worldMapPointManager.removeIf(MahoganyHomesWorldPoint.class::isInstance);
		client.clearHintArrow();
		varbMap.clear();
		objectsToMark.clear();
		currentHome = null;
		mapIcon = null;
		mapArrow = null;
		lastChanged = null;
		lastCompletedCount = -1;
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
	public void onGameObjectChanged(GameObjectChanged event)
	{
		processGameObjects(event.getGameObject(), event.getPrevious());
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
			lastChanged = Instant.now().minus(PLUGIN_TIMEOUT_DURATION);
		}
	}

	@Subscribe
	public void onGameTick(GameTick t)
	{
		if (varbChange)
		{
			varbChange = false;
			updateVarbMap();

			final int completed = getCompletedCount();
			if (completed != lastCompletedCount)
			{
				lastCompletedCount = completed;
				lastChanged = Instant.now();
			}
		}

		checkForAssignmentDialog();

		// The plugin automatically disables after 5 minutes of inactivity.
		if (isPluginTimedOut() || currentHome == null)
		{
			return;
		}

		refreshHintArrow(client.getLocalPlayer().getWorldLocation());
	}

	// Check for NPC dialog assigning or reminding us of a contract
	private void checkForAssignmentDialog()
	{
		final Widget dialog = client.getWidget(WidgetInfo.DIALOG_NPC_TEXT);
		if (dialog == null)
		{
			return;
		}

		final String npcText = Text.sanitizeMultilineText(dialog.getText());
		if (npcText.equals(CONTRACT_FINISHED_MESSAGE))
		{
			setCurrentHome(null);
			updateConfig();
			return;
		}

		final Matcher startContractMatcher = CONTRACT_PATTERN.matcher(npcText);
		if (startContractMatcher.matches())
		{
			final String name = startContractMatcher.group(2);
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

		if (currentHome == null)
		{
			lastChanged = null;
			worldMapPointManager.removeIf(MahoganyHomesWorldPoint.class::isInstance);
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
		final String group = MahoganyHomesConfig.GROUP_NAME + "." + client.getUsername();
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
	}

	private void updateConfig()
	{
		final String group = MahoganyHomesConfig.GROUP_NAME + "." + client.getUsername();
		if (currentHome == null)
		{
			configManager.unsetConfiguration(group, MahoganyHomesConfig.HOME_KEY);
		}
		else
		{
			configManager.setConfiguration(group, MahoganyHomesConfig.HOME_KEY, currentHome.getName());
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
		return lastChanged != null && Duration.between(lastChanged, Instant.now()).compareTo(PLUGIN_TIMEOUT_DURATION) >= 0;
	}
}
