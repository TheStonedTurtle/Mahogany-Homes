package thestonedturtle.mahoganyhomes;

import lombok.AllArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
public class TeleportItem implements Comparable<TeleportItem>
{
	public int ItemId;
	public int Distance;

	@Override
	public int compareTo(TeleportItem teleportItem)
	{
		return this.Distance - teleportItem.Distance;
	}
}
