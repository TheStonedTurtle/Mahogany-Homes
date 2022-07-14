/*
 * Copyright (c) 2022, TheStonedTurtle <https://github.com/TheStonedTurtle>
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

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RequiredMaterialsByTier
{
	// East Ardy
	JESS(new RequiredMaterials(9, 11, 0, 1),
		new RequiredMaterials(9, 11, 0, 1),
		new RequiredMaterials(12, 15, 0, 1),
		new RequiredMaterials(14, 15, 0, 1)),
	NOELLA(new RequiredMaterials(11, 12, 0, 0),
		new RequiredMaterials(11, 12, 0, 0),
		new RequiredMaterials(12, 15, 0, 0),
		new RequiredMaterials(13, 15, 0, 0)),
	ROSS(new RequiredMaterials(8, 11, 0, 1),
		new RequiredMaterials(8, 11, 0, 1),
		new RequiredMaterials(8, 11, 1, 1),
		new RequiredMaterials(10, 11, 0, 1)),

	// Falador
	LARRY(new RequiredMaterials(8, 12, 0, 1),
		new RequiredMaterials(8, 12, 0, 1),
		new RequiredMaterials(9, 12, 0, 1),
		new RequiredMaterials(12, 12, 0, 1)),
	NORMAN(new RequiredMaterials(11, 11, 0, 1),
		new RequiredMaterials(10, 11, 0, 1),
		new RequiredMaterials(10, 13, 0, 1),
		new RequiredMaterials(12, 13, 0, 1)),
	TAU(new RequiredMaterials(8, 12, 0, 1),
		new RequiredMaterials(8, 12, 0, 1),
		new RequiredMaterials(9, 13, 0, 1),
		new RequiredMaterials(12, 13, 0, 1)),

	// Hosidius
	BARBARA(new RequiredMaterials(3, 8, 0, 1),
		new RequiredMaterials(3, 8, 0, 1),
		new RequiredMaterials(9, 10, 0, 1),
		new RequiredMaterials(9, 10, 0, 1)),
	LEELA(new RequiredMaterials(8, 9, 0, 1),
		new RequiredMaterials(8, 9, 0, 1),
		new RequiredMaterials(9, 10, 0, 1),
		new RequiredMaterials(12, 13, 0, 1)),
	MARIAH(new RequiredMaterials(7, 11, 0, 1),
		new RequiredMaterials(7, 11, 0, 1),
		new RequiredMaterials(11, 14, 0, 1),
		new RequiredMaterials(13, 14, 0, 1)),

	// Varrock
	BOB(new RequiredMaterials(13, 14, 0, 0),
		new RequiredMaterials(13, 14, 0, 0),
		new RequiredMaterials(13, 17, 0, 0),
		new RequiredMaterials(16, 17, 0, 0)),
	JEFF(new RequiredMaterials(11, 13, 0, 0),
		new RequiredMaterials(11, 13, 0, 0),
		new RequiredMaterials(11, 16, 0, 0),
		new RequiredMaterials(15, 16, 0, 0)),
	SARAH(new RequiredMaterials(11, 11, 0, 1),
		new RequiredMaterials(11, 11, 0, 1),
		new RequiredMaterials(11, 11, 0, 1),
		new RequiredMaterials(11, 11, 0, 1)),
	;

	private final RequiredMaterials beginner;
	private final RequiredMaterials novice;
	private final RequiredMaterials adept;
	private final RequiredMaterials expert;

	public RequiredMaterials getByTier(final int tier)
	{
		switch (tier)
		{
			case 1:
				return beginner;
			case 2:
				return novice;
			case 3:
				return adept;
			case 4:
				return expert;
			default:
				return null;
		}
	}
}
