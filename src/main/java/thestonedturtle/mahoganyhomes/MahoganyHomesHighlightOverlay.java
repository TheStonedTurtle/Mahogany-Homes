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
import net.runelite.api.Player;
import net.runelite.api.TileObject;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

class MahoganyHomesHighlightOverlay extends Overlay
{
	public static final Color CLICKBOX_BORDER_COLOR = Color.ORANGE;
	public static final Color CLICKBOX_HOVER_BORDER_COLOR = CLICKBOX_BORDER_COLOR.darker();

	private final MahoganyHomesPlugin plugin;
	private final MahoganyHomesConfig config;

	@Inject
	MahoganyHomesHighlightOverlay(MahoganyHomesPlugin plugin, MahoganyHomesConfig config)
	{
		super(plugin);
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		final Home home = plugin.getCurrentHome();
		final Player player = plugin.getClient().getLocalPlayer();
		if (plugin.isPluginTimedOut() || home == null || player == null)
		{
			return null;
		}

		for (TileObject gameObject : plugin.getObjectsToMark())
		{
			if (gameObject.getPlane() != plugin.getClient().getPlane())
			{
				continue;
			}

			if (plugin.distanceBetween(home.getArea(), gameObject.getWorldLocation()) > 0)
			{
				// Object not inside area for this house.
				continue;
			}

			Color fillColor = config.highlightHotspotColor();
			final Hotspot spot = Hotspot.getByObjectId(gameObject.getId());
			if (spot == null)
			{
				// Ladders aren't hotspots so handle them after this check
				if (!Home.isLadder(gameObject.getId()) || !config.highlightStairs())
				{
					continue;
				}

				fillColor = config.highlightStairsColor();
			}
			else
			{
				// Do not highlight the hotspot if the config is disabled or it doesn't require any attention
				if (!config.highlightHotspots() || !plugin.doesHotspotRequireAttention(spot.getVarb()))
				{
					continue;
				}
			}

			final net.runelite.api.Point mousePosition = plugin.getClient().getMouseCanvasPosition();
			OverlayUtil.renderHoverableArea(graphics, gameObject.getClickbox(), mousePosition,
				fillColor, CLICKBOX_BORDER_COLOR, CLICKBOX_HOVER_BORDER_COLOR);
		}

		return null;
	}
}
