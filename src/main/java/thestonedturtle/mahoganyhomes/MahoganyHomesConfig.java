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
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup(MahoganyHomesConfig.GROUP_NAME)
public interface MahoganyHomesConfig extends Config
{
	String GROUP_NAME = "MahoganyHomes";
	String HOME_KEY = "currentHome";
	String TIER_KEY = "currentTier";
	String WORLD_MAP_KEY = "worldMapIcon";
	String HINT_ARROW_KEY = "displayHintArrows";
	String SESSION_TIMEOUT_KEY = "sessionTimeout";

	@ConfigItem(
		keyName = WORLD_MAP_KEY,
		name = "World Map Icon",
		description = "Configures whether an icon will be displayed on the world map showing where to go for your current contract",
		position = 0
	)
	default boolean worldMapIcon()
	{
		return true;
	}

	@ConfigItem(
		keyName = HINT_ARROW_KEY,
		name = "Display Hint Arrows",
		description = "Configures whether or not to display the hint arrows",
		position = 1
	)
	default boolean displayHintArrows()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "highlightHotspotColor",
		name = "Hotspot Highlight Color",
		description = "Configures the color the stairs will be highlighted",
		position = 2
	)
	default Color highlightHotspotColor()
	{
		return new Color(0, 255, 0, 50);
	}

	@ConfigItem(
		keyName = "highlightStairs",
		name = "Highlight Stairs",
		description = "Configures whether or not the stairs will be highlighted",
		position = 3
	)
	default boolean highlightStairs()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "highlightStairsColor",
		name = "Stairs Highlight Color",
		description = "Configures the color the stairs will be highlighted",
		position = 4
	)
	default Color highlightStairsColor()
	{
		return new Color(0, 255, 0, 20);
	}

	@ConfigItem(
		keyName = "textOverlay",
		name = "Display Text Overlay",
		description = "Configures whether or not the text overlay will be displayed for your current contract",
		position = 5
	)
	default boolean textOverlay()
	{
		return true;
	}

	@ConfigItem(
		keyName = "highlightHotspots",
		name = "Highlight Building Hotspots",
		description = "Configures whether or not the building hotspots will be highlighted",
		position = 6
	)
	default boolean highlightHotspots()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showRequiredMaterials",
		name = "Display Required Materials",
		description = "Configures whether or not to display the required materials for your current task",
		position = 7
	)
	default boolean showRequiredMaterials()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showSessionStats",
		name = "Display Session Stats",
		description = "Configures whether or not the amount of contracts and the points received from those contracts is displayed inside the overlay",
		position = 8
	)
	default boolean showSessionStats()
	{
		return true;
	}

	@Range(
		min = 1,
		max = 60
	)
	@ConfigItem(
		keyName = SESSION_TIMEOUT_KEY,
		name = "Session Timeout",
		description = "Configures how many minutes must pass before the session timeouts after not having any activity.<br/>" +
			"Value must be between 1 and 60 minute(s)",
		position = 9
	)
	default int sessionTimeout()
	{
		return 5;
	}

	@ConfigItem(
			keyName = "checkSupplies",
			name = "Check Supplies",
			description = "Checks if you have enough supplies in your inventory to complete your current contract.<br/>" +
				"If the Plank Sack plugin is installed, it will include planks in the plank sack.",
			position = 10
	)
	default boolean checkSupplies()
	{
		return false;
	}
}
