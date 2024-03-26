/*
 * Copyright (c) 2020, TheStonedTurtle <https://github.com/TheStonedTurtle>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package thestonedturtle.mahoganyhomes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import net.runelite.api.Player;
import net.runelite.client.ui.overlay.OverlayLayer;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;

class MahoganyHomesOverlay extends OverlayPanel
{
	static final String RESET_SESSION_OPTION = "Reset";
	static final String CLEAR_OPTION = "Clear";
	static final String TIMEOUT_OPTION = "Timeout";

	private final MahoganyHomesPlugin plugin;
	private final MahoganyHomesConfig config;

	@Inject
	MahoganyHomesOverlay(MahoganyHomesPlugin plugin, MahoganyHomesConfig config)
	{
		super(plugin);
		setPosition(OverlayPosition.TOP_LEFT);
		setPriority(OverlayPriority.LOW);
		setLayer(OverlayLayer.UNDER_WIDGETS);
		this.plugin = plugin;
		this.config = config;

		getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Mahogany Homes Overlay"));
		getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY, TIMEOUT_OPTION, "Mahogany Homes Plugin"));
		getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY, RESET_SESSION_OPTION, "Session Data"));
		getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY, CLEAR_OPTION, "Contract"));
	}

	// Adds a line to the panel describing a material. type is the
	// un-pluralized name of the material, min and max are the range
	// required, and have is the number in the player's inventory.
	void addMaterialLine(String type, int min, int max, int have)
	{
		if (min == 0 && max == 0)
		{
			return;
		}
		LineComponent.LineComponentBuilder line = LineComponent.builder();

		if (min == max)
		{
			String plural = min > 1 ? "s" : "";
			line = line.left(String.format("%d %s%s", min, type, plural));
		}
		else
		{
			// Always pluralize by adding an "s"
			line = line.left(String.format("%d - %d %ss", min, max, type));
		}

		// If asked for, add a colored symbol on the right depicting if
		// the player has enough supplies.
		if (config.checkSupplies())
		{
			if (have >= max)
			{
				line = line.right("✓").rightColor(Color.GREEN);
			}
			else if (have >= min)
			{
				// Player has between max and min, so it's not
				// certain if they have enough.
				line = line.right("?").rightColor(Color.YELLOW);
			}
			else
			{
				line = line.right("x").rightColor(Color.RED);
			}
		}
		panelComponent.getChildren().add(line.build());
	}

	// Adds lines to the panel describing the materials required.
	void addRequiredMaterials(RequiredMaterials mats)
	{
		if (mats == null)
		{
			return;
		}

		addMaterialLine("Plank", mats.MinPlanks, mats.MaxPlanks, plugin.getNumPlanksInInventory());
		addMaterialLine("Steel Bar", mats.MinSteelBars, mats.MaxSteelBars, plugin.getNumSteelBarsInInventory());
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		final Home home = plugin.getCurrentHome();
		final Player player = plugin.getClient().getLocalPlayer();
		if (plugin.isPluginTimedOut() || !config.textOverlay() || player == null)
		{
			return null;
		}

		if (home != null)
		{
			addLine(home.getName());
			addLine(home.getHint());

			if (plugin.distanceBetween(home.getArea(), player.getWorldLocation()) > 0)
			{
				if (config.showRequiredMaterials() && plugin.getContractTier() > 0)
				{
					addLine("");

					addRequiredMaterials(home.getRequiredMaterialsByTier().getByTier(plugin.getContractTier()));
				}

				if (config.worldMapIcon())
				{
					addLine("");
					addLine("Click the house icon on your world map to see where to go");
				}
			}
			else
			{
				if (config.showRequiredMaterials() && plugin.getContractTier() > 0)
				{
					final RequiredMaterials requiredMaterials = home.getHotspotObjects().getRequiredMaterialsForVarbs(plugin.getRepairableVarbs());
					// We only want to add an empty line if there's something to be displayed
					if (requiredMaterials.MinPlanks > 0 || requiredMaterials.MinSteelBars > 0)
					{
						addLine("");
					}

					addRequiredMaterials(requiredMaterials);
				}

				addLine("");
				final int count = plugin.getCompletedCount();
				if (count > 0)
				{
					panelComponent.getChildren().add(LineComponent.builder()
						.left(count + " task(s) remaining")
						.leftColor(Color.RED)
						.build());
				}
				else
				{
					panelComponent.getChildren().add(LineComponent.builder()
						.left("All tasks completed, speak to " + home.getName())
						.leftColor(Color.GREEN)
						.build());
				}
			}
		}

		if (config.showSessionStats() && plugin.getSessionContracts() > 0)
		{
			if (home != null)
			{
				addLine("");
			}
			addLine("Contracts Done: " + plugin.getSessionContracts());
			addLine("Points Earned: " + plugin.getSessionPoints());
		}

		return super.render(graphics);
	}

	private void addLine(final String left)
	{
		panelComponent.getChildren().add(LineComponent.builder().left(left).build());
	}
}
