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

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(MahoganyHomesConfig.GROUP_NAME)
public interface MahoganyHomesConfig extends Config
{
	String GROUP_NAME = "MahoganyHomes";
	String HOME_KEY = "currentHome";
	String WORLD_MAP_KEY = "worldMapIcon";
	String HINT_ARROW_KEY = "displayHintArrows";

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

	@ConfigItem(
		keyName = "highlightStairs",
		name = "Highlight Stairs",
		description = "Configures whether or not the stairs will be highlighted",
		position = 2
	)
	default boolean highlightStairs()
	{
		return true;
	}

	@ConfigItem(
		keyName = "textOverlay",
		name = "Display Text Overlay",
		description = "Configures whether or not the text overlay will be displayed for your current contract",
		position = 3
	)
	default boolean textOverlay()
	{
		return true;
	}

	@ConfigItem(
		keyName = "highlightHotspots",
		name = "Highlight Building Hotspots",
		description = "Configures whether or not the building hotspots will be highlighted",
		position = 4
	)
	default boolean highlightHotspots()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showRequiredMaterials",
		name = "Display Required Materials",
		description = "Configures whether or not to display the required materials for your current task",
		position = 5
	)
	default boolean showRequiredMaterials()
	{
		return true;
	}
}
