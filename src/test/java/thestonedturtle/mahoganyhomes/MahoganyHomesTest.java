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

import java.util.regex.Matcher;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MahoganyHomesTest
{
	@Test
	public void testAssignmentRegexPattern()
	{
		matchContractAssignment("Jess", "Please could you go see Jess, upstairs of the building south of the church in East Ardougne? You can get another job once you have furnished her home.");
		matchContractAssignment("Jess", "Go see Jess, upstairs of the building south of the church in East Ardougne. You can get another job once you have furnished her home.");

		matchContractAssignment("Sarah", "Please could you go see Sarah along the south wall of Varrock? You can get another job once you have furnished her home.");
		matchContractAssignment("Sarah", "Go see Sarah along the south wall of Varrock. You can get another job once you have furnished her home.");

		matchContractAssignment("Bob", "Please could you go see Bob in north-east Varrock, opposite the church? You can get another job once you have furnished his home.");
		matchContractAssignment("Bob", "Go see Bob in north-east Varrock, opposite the church. You can get another job once you have furnished his home.");
	}

	private void matchContractAssignment(final String name, final String message)
	{

		final Matcher matcher = MahoganyHomesPlugin.CONTRACT_PATTERN.matcher(message);
		assertTrue(matcher.matches());
		assertEquals(name, matcher.group(2));
	}
}
