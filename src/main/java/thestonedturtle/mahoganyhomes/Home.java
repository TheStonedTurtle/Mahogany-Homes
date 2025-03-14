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

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import org.apache.commons.text.WordUtils;

@Getter
enum Home
{
	// area is based on bounds of house not area at which stuff loads in for the homes
	// Ardy
	JESS(new WorldArea(2611, 3290, 14, 7, 0), "Upstairs of the building south of the church in East Ardougne",
		NpcID.JESS, new WorldPoint(2621, 3292, 0), TeleportItems.JESS, RequiredMaterialsByTier.JESS, HotspotObjects.JESS, 17026, 16685),
	NOELLA(new WorldArea(2652, 3317, 15, 8, 0), "North of East Ardougne market",
		NpcID.NOELLA, new WorldPoint(2659, 3322, 0), TeleportItems.NOELLA, RequiredMaterialsByTier.NOELLA, HotspotObjects.NOELLA, 17026, 16685, 15645, 15648),
	ROSS(new WorldArea(2609, 3313, 11, 9, 0), "North of the church in East Ardougne",
		NpcID.ROSS, new WorldPoint(2613, 3316, 0), TeleportItems.ROSS, RequiredMaterialsByTier.ROSS, HotspotObjects.ROSS, 16683, 16679),

	// Falador
	LARRY(new WorldArea(3033, 3360, 10, 9, 0), "North of the fountain in Falador",
		NpcID.LARRY_10418, new WorldPoint(3038, 3364, 0), TeleportItems.LARRY, RequiredMaterialsByTier.LARRY, HotspotObjects.LARRY, 24075, 24076),
	NORMAN(new WorldArea(3034, 3341, 8, 8, 0), "South of the fountain in Falador",
		NpcID.NORMAN, new WorldPoint(3038, 3344, 0), TeleportItems.NORMAN, RequiredMaterialsByTier.NORMAN, HotspotObjects.NORMAN, 24082, 24085),
	TAU(new WorldArea(3043, 3340, 10, 11, 0), "South east of the fountain in Falador",
		NpcID.TAU, new WorldPoint(3047, 3345, 0), TeleportItems.TAU, RequiredMaterialsByTier.TAU, HotspotObjects.TAU),

	// Hosidus
	BARBARA(new WorldArea(1746, 3531, 10, 11, 0), "South of Hosidius, near the mill",
		NpcID.BARBARA, new WorldPoint(1750, 3534, 0), TeleportItems.BARBARA, RequiredMaterialsByTier.BARBARA, HotspotObjects.BARBARA),
	LEELA(new WorldArea(1781, 3589, 9, 8, 0), "East of the town market in Hosidius",
		NpcID.LEELA_10423, new WorldPoint(1785, 3592, 0), TeleportItems.LEELA, RequiredMaterialsByTier.LEELA, HotspotObjects.LEELA, 11794, 11802),
	MARIAH(new WorldArea(1762, 3618, 10, 7, 0), "West of the estate agents in Hosidius",
		NpcID.MARIAH, new WorldPoint(1766, 3621, 0), TeleportItems.MARIAH, RequiredMaterialsByTier.MARIAH, HotspotObjects.MARIAH, 11794, 11802),

	// Varrock
	BOB(new WorldArea(3234, 3482, 10, 10, 0), "North-east Varrock, opposite the church",
		NpcID.BOB_10414, new WorldPoint(3238, 3486, 0), TeleportItems.BOB, RequiredMaterialsByTier.BOB, HotspotObjects.BOB, 11797, 11799),
	JEFF(new WorldArea(3235, 3445, 10, 12, 0), "Middle of Varrock, west of the museum",
		NpcID.JEFF_10415, new WorldPoint(3239, 3450, 0), TeleportItems.JEFF, RequiredMaterialsByTier.JEFF, HotspotObjects.JEFF, 11789, 11793),
	SARAH(new WorldArea(3232, 3381, 8, 7, 0), "Along the south wall of Varrock",
		NpcID.SARAH_10416, new WorldPoint(3235, 3384, 0), TeleportItems.SARAH, RequiredMaterialsByTier.SARAH, HotspotObjects.SARAH);

	private final WorldArea area;
	private final String hint;
	private final int npcId;
	private final WorldPoint location;
	private final Integer[] ladders;
	private final TeleportItems teleportItems;
	private final RequiredMaterialsByTier requiredMaterialsByTier;
	private final HotspotObjects hotspotObjects;

	Home(final WorldArea area, final String hint, final int npcId, final WorldPoint location, final TeleportItems teleportItems,
		final RequiredMaterialsByTier requiredMaterials, final HotspotObjects hotspotObjects, final Integer... ladders)
	{
		this.area = area;
		this.hint = hint;
		this.npcId = npcId;
		this.location = location;
		this.ladders = ladders;
		this.teleportItems = teleportItems;
		this.hotspotObjects = hotspotObjects;
		this.requiredMaterialsByTier = requiredMaterials;
	}

	String getName()
	{
		return WordUtils.capitalize(name().toLowerCase());
	}

	private static final ImmutableSet<Integer> LADDERS;
	static
	{
		final ImmutableSet.Builder<Integer> b = new ImmutableSet.Builder<>();
		for (final Home h : values())
		{
			b.add(h.getLadders());
		}
		LADDERS = b.build();
	}

	static boolean isLadder(final int objID)
	{
		return LADDERS.contains(objID);
	}
}
